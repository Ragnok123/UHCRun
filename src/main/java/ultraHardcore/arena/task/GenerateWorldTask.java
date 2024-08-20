package ultraHardcore.arena.task;

import GTCore.reflect.Reflect;
import cn.nukkit.InterruptibleThread;
import cn.nukkit.Server;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.MainLogger;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import ultraHardcore.UltraHardcore;
import ultraHardcore.arena.Arena;

public class GenerateWorldTask extends AsyncTask {
      public static ArrayDeque generatorQueue = new ArrayDeque();
      private Arena arena;
      private String map;
      private int attemps = 0;
      private static GenerateWorldTask.ConsoleReader reader;
      private static final String ENTER = "\n";
      public static boolean running = false;
      public static Process process = null;
      private boolean memoryStop = false;
      private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\(\\d+ total, ~(\\d+(?:[,.]\\d+)?)%\\)");

      public GenerateWorldTask(Arena arena) {
            this.arena = arena;
            this.map = arena.getId().toLowerCase();
            running = true;
      }

      public void onRun() {
            running = true;
            deleteWorld(this.map);
            boolean failed = false;

            try {
                  if (!this.memoryStop) {
                  }

                  Thread.sleep(2000L);
                  String path = Server.getInstance().getDataPath() + "plugins/UHC/minecraft/minecraft.jar";
                  ProcessBuilder builder = new ProcessBuilder(new String[]{"java", "-Xmx2048M", "-jar", path, "--port", this.arena.data.port});
                  builder.directory((new File(Server.getInstance().getDataPath() + "plugins/UHC/minecraft")).getAbsoluteFile());
                  process = builder.start();
                  if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
                        int pid = (Integer)Reflect.on(process).get("pid");
                        System.out.println("pid: " + pid);
                        Runtime.getRuntime().exec("renice -19 -p " + pid);
                  }

                  InputStream in = process.getInputStream();
                  OutputStream out = process.getOutputStream();
                  BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                  GenerateWorldTask.reader = new GenerateWorldTask.ConsoleReader(reader);
                  GenerateWorldTask.reader.start();
                  long lastLine = System.currentTimeMillis();
                  long stop = Long.MAX_VALUE;

                  label91:
                  while(Server.getInstance().isRunning() && GenerateWorldTask.reader.isAlive()) {
                        while(true) {
                              String line;
                              do {
                                    if (GenerateWorldTask.reader.lines.isEmpty()) {
                                          if (System.currentTimeMillis() - lastLine > 20000L || System.currentTimeMillis() - stop > 20000L) {
                                                killProcess();
                                                failed = true;
                                                System.out.println("something wrong");
                                                this.memoryStop = false;
                                                break label91;
                                          }

                                          Thread.sleep(500L);
                                          continue label91;
                                    }

                                    line = (String)GenerateWorldTask.reader.lines.remove();
                              } while(line == null);

                              if (UltraHardcore.DEBUG) {
                                    MainLogger.getLogger().info(line);
                              }

                              if (!this.memoryStop && line.toLowerCase().contains("for help, type \"help\" or \"?\"")) {
                                    sendCommand("mv delete " + this.map, out);
                                    sendCommand("mv create " + this.map + " normal -g TerrainControl", out);
                              } else if ((!this.memoryStop || !line.toLowerCase().contains("for help, type \"help\" or \"?\"")) && !line.toLowerCase().contains("console: complete!")) {
                                    if (line.toLowerCase().contains("task successfully completed for world \"" + this.map + "\"!")) {
                                          sendCommand("stop", out);
                                          stop = System.currentTimeMillis();
                                    } else if (line.toLowerCase().contains("more chunks processed")) {
                                          this.arena.percentage = getPercentage(line.toLowerCase());
                                    } else if (line.toLowerCase().contains("available memory is very low")) {
                                          sendCommand("stop", out);
                                          stop = System.currentTimeMillis();
                                          failed = true;
                                          if (this.attemps < 3) {
                                                this.memoryStop = true;
                                          }
                                    }
                              } else {
                                    this.memoryStop = false;
                                    sendCommand("wb " + this.map + " set 500 500 spawn", out);
                                    sendCommand("wb " + this.map + " fill 20", out);
                                    sendCommand("wb fill confirm", out);
                              }

                              lastLine = System.currentTimeMillis();
                        }
                  }

                  if (!failed) {
                        FileUtils.copyDirectory(new File(UltraHardcore.getInstance().getDataFolder() + "/minecraft/" + this.map), new File(Server.getInstance().getDataPath() + "worlds/" + this.map));
                        FileUtils.deleteDirectory(new File(UltraHardcore.getInstance().getDataFolder() + "/minecraft/" + this.map));
                  }
            } catch (InterruptedException | IOException var13) {
                  var13.printStackTrace();
            }

            killProcess();
            if (failed) {
                  if (this.attemps >= 3) {
                        MainLogger.getLogger().error("Could not generate world " + this.map);
                  }

                  ++this.attemps;
                  this.onRun();
            }

      }

      public void onCompletion(Server server) {
            this.arena.isMapLoaded = true;
            this.arena.percentage = null;
            if (!generatorQueue.isEmpty()) {
                  server.getScheduler().scheduleAsyncTask((AsyncTask)generatorQueue.removeFirst());
            } else {
                  running = false;
                  process = null;
                  reader = null;
            }

      }

      public static void deleteWorld(String name) {
            try {
                  File directory = new File(Server.getInstance().getDataPath() + "worlds/" + name);
                  FileUtils.deleteDirectory(directory);
            } catch (IOException var2) {
                  var2.printStackTrace();
            }

      }

      private static void sendCommand(String cmd, OutputStream out) throws IOException {
            out.write((cmd + "\n").getBytes());
            out.flush();
      }

      private static String getPercentage(String message) {
            Matcher m = VARIABLE_PATTERN.matcher(message);
            if (m.find()) {
                  String var = m.group();
                  if (var != null && !var.isEmpty()) {
                        var = var.replace(",", ".");
                        int index = var.indexOf("~");
                        int index2 = var.indexOf("%", index);
                        return var.substring(index + 1, index2) + "%";
                  }
            }

            return null;
      }

      public static void killProcess() {
            if (reader != null && !reader.isInterrupted()) {
                  reader.interrupt();
            }

            if (process != null && process.isAlive()) {
                  process.destroyForcibly();
            }

      }

      private class ConsoleReader extends Thread implements InterruptibleThread {
            public Queue lines = new ConcurrentLinkedDeque();
            private BufferedReader reader;

            public ConsoleReader(BufferedReader reader) {
                  this.reader = reader;
            }

            public void run() {
                  while(true) {
                        try {
                              String line;
                              if ((line = this.reader.readLine()) != null) {
                                    this.lines.add(line);
                                    continue;
                              }
                        } catch (IOException var2) {
                              var2.printStackTrace();
                        }

                        return;
                  }
            }
      }
}
