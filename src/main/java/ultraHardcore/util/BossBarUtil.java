package ultraHardcore.util;

import cn.nukkit.utils.TextFormat;
import ultraHardcore.arena.Arena;

public class BossBarUtil {
      public String mainLine = "";
      private Arena plugin;

      public BossBarUtil(Arena plugin) {
            this.plugin = plugin;
      }

      public void updateBar(int time) {
            if (this.plugin.phase == 0) {
                  if (this.plugin.starting) {
                        this.plugin.bossBar.setMaxHealth(50);
                        this.mainLine = TextFormat.GOLD + "Lobby" + TextFormat.GRAY + " | " + TextFormat.WHITE + " Time to start: " + time;
                        this.plugin.bossBar.setHealth(time);
                  } else {
                        this.mainLine = TextFormat.GRAY + "Welcome to UHC!  (" + TextFormat.YELLOW + time + TextFormat.GRAY + "/" + TextFormat.YELLOW + "52" + TextFormat.GRAY + ")";
                        this.plugin.bossBar.setMaxHealth(120);
                        this.plugin.bossBar.setHealth(120);
                  }
            } else if (this.plugin.phase == 1) {
                  this.plugin.bossBar.setMaxHealth(100);
                  switch(this.plugin.gamePhase) {
                  case 0:
                        this.mainLine = TextFormat.DARK_GREEN + "PREPARING  ";
                        this.plugin.bossBar.setHealth(getPercentage(time, 180));
                        break;
                  case 1:
                        this.mainLine = TextFormat.GOLD + "FIGHTING  ";
                        this.plugin.bossBar.setHealth(getPercentage(time - 180, 1920));
                        break;
                  case 2:
                        this.mainLine = TextFormat.RED + "DEATHMATCH  ";
                        this.plugin.bossBar.setHealth(getPercentage(time - 2100, 2520));
                  }

                  this.mainLine = this.mainLine + this.getTimeString(time);
            } else {
                  if (this.plugin.phase != 2) {
                        return;
                  }

                  this.plugin.bossBar.setMaxHealth(30);
                  this.mainLine = TextFormat.GOLD + "Total time: " + this.getTimeString(this.plugin.task.gameTime) + TextFormat.GRAY + " | " + TextFormat.GREEN + "Restarting in " + time;
                  this.plugin.bossBar.setHealth(time);
            }

            this.update();
      }

      private void update() {
            this.plugin.bossBar.updateText(this.mainLine);
            this.plugin.bossBar.updateInfo();
      }

      private String getTimeString(int time) {
            long hours = (long)time / 3600L;
            long minutes = ((long)time - hours * 3600L) / 60L;
            long seconds = (long)time - hours * 3600L - minutes * 60L;
            return String.format(TextFormat.WHITE + "%02d" + TextFormat.GRAY + ":" + TextFormat.WHITE + "%02d" + TextFormat.GRAY + ":" + TextFormat.WHITE + "%02d", hours, minutes, seconds).replace("-", "");
      }

      public static int getPercentage(int value, int max) {
            return value * 100 / max;
      }
}
