package ultraHardcore;

import GTCore.MTCore;
import GTCore.Event.PlayerLoadDataEvent;
import GTCore.Task.MessageTask;
import GTCore.minigame.Minigame;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSignPost;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntitySpawnEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.level.ChunkLoadEvent;
import cn.nukkit.event.player.PlayerAchievementAwardedEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.inventory.ShapedRecipe;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemApple;
import cn.nukkit.item.ItemAppleGold;
import cn.nukkit.item.ItemIngotGold;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.ChunkRadiusUpdatedPacket;
import cn.nukkit.network.protocol.RequestChunkRadiusPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import ultraHardcore.arena.Arena;
import ultraHardcore.arena.kit.Kit;
import ultraHardcore.arena.object.PlayerData;
import ultraHardcore.arena.task.GenerateWorldTask;
import ultraHardcore.command.PointCommand;
import ultraHardcore.command.StartCommand;
import ultraHardcore.command.StatsCommand;
import ultraHardcore.entity.other.KitEntity;
import ultraHardcore.entity.other.TeamEntity;
import ultraHardcore.language.Language;
import ultraHardcore.mysql.JoinQuery;
import ultraHardcore.mysql.QuitQuery;
import ultraHardcore.util.ArenaData;

public class UltraHardcore extends PluginBase implements Listener, Minigame {
      private static UltraHardcore instance;
      private String id;
      private HashMap arenas = new HashMap();
      public HashMap players = new HashMap();
      public static boolean DEBUG = true;

      public void onLoad() {
            instance = this;
            MTCore.getInstance().minigame = this;
            this.registerEntities();
            this.registerGenerator();
            this.getServer().getScheduler().scheduleDelayedTask(new Runnable() {
                  public void run() {
                        UltraHardcore.this.enable();
                  }
            }, 1);
      }

      public void enable() {
            this.saveDefaultConfig();
            this.id = this.getConfig().getString("id");
            this.initLanguage();
            this.registerCommands();
            this.registerArenas();
            this.registerRecipes();
            this.initMessages();
            this.getServer().getPluginManager().registerEvents(this, this);
      }

      public void onDisable() {
            MTCore.isShuttingDown = true;
            GenerateWorldTask.killProcess();
            Iterator var1 = this.players.values().iterator();

            while(var1.hasNext()) {
                  PlayerData data = (PlayerData)var1.next();
                  new QuitQuery(new PlayerData[]{data});
            }

      }

      private void registerArenas() {
            this.registerArena("uhc-1", new ArenaData() {
                  {
                        this.sign = new Vector3(-54.0D, 57.0D, -690.0D);
                        this.lobby = new Vector3(-725.5D, 54.0D, -689.5D);
                        this.port = "11945";
                        this.teamSize = 2;
                  }
            });
      }

      public void registerArena(String name, ArenaData data) {
            Arena arena = new Arena(this, name, data);
            this.getServer().getPluginManager().registerEvents(arena, this);
            this.arenas.put(name, arena);
      }

      private void registerCommands() {
            this.getServer().getCommandMap().register("stats", new StatsCommand(this));
            this.getServer().getCommandMap().register("start", new StartCommand(this));
            this.getServer().getCommandMap().register("point", new PointCommand(this));
      }

      private void registerGenerator() {
      }

      private void initMessages() {
            List english = (List)MessageTask.languages.get(0);
            english.add(TextFormat.AQUA + "Use " + TextFormat.GREEN + "/point " + TextFormat.AQUA + "to aim the nearest player");
            List czech = (List)MessageTask.languages.get(1);
            czech.add(TextFormat.AQUA + "Pro zamereni nejblizsiho hrace pouzij prikaz " + TextFormat.GREEN + "/point");
      }

      private void initLanguage() {
            this.saveResource("English.yml", true);
            this.saveResource("Czech.yml", true);
            Map langs = new HashMap();
            langs.put(1, new Config(this.getDataFolder() + "/Czech.yml", 2));
            langs.put(0, new Config(this.getDataFolder() + "/English.yml", 2));
            Language.init(langs);
      }

      private void registerRecipes() {
            this.getServer().getCraftingManager().registerRecipe(new ShapedRecipe(new ItemAppleGold(), new String[]{" g ", "gag", " g "}, new HashMap() {
                  {
                        this.put('g', new ItemIngotGold());
                        this.put('a', new ItemApple());
                  }
            }, new ArrayList()));
            this.getServer().getCraftingManager().registerRecipe(new ShapedRecipe(Item.get(145), new String[]{"iii", " i ", "iii"}, new HashMap() {
                  {
                        this.put('i', Item.get(265));
                  }
            }, new ArrayList()));
            this.getServer().getCraftingManager().rebuildPacket();
      }

      private void registerEntities() {
            Entity.registerEntity("KitEntity", KitEntity.class, true);
            Entity.registerEntity("TeamEntity", TeamEntity.class, true);
      }

      public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            CompoundTag nbt;
            Player p;
            int distance0;
            switch(command.getName().toLowerCase()) {
            case "uhcentity":
                  if (sender.isOp() && sender instanceof Player) {
                        if (args.length != 1) {
                              sender.sendMessage(TextFormat.YELLOW + "use /uhcentity <team>");
                        } else {
                              nbt = (new CompoundTag()).putList((new ListTag("Pos")).add(new DoubleTag("0", (double)((Player)sender).getFloorX() + 0.5D)).add(new DoubleTag("1", (double)((Player)sender).getFloorY())).add(new DoubleTag("2", (double)((Player)sender).getFloorZ() + 0.5D))).putList((new ListTag("Motion")).add(new DoubleTag("0", 0.0D)).add(new DoubleTag("1", 0.0D)).add(new DoubleTag("2", 0.0D))).putList((new ListTag("Rotation")).add(new FloatTag("0", (float)((Player)sender).getYaw())).add(new FloatTag("1", 0.0F))).putInt("team", Integer.valueOf(args[0]));
                              TeamEntity teamEntity = new TeamEntity(((Player)sender).chunk, nbt);
                              teamEntity.spawnToAll();
                        }
                  }
                  break;
            case "kitentity":
                  if (sender.isOp() && sender instanceof Player && args.length == 1) {
                        nbt = (new CompoundTag()).putList((new ListTag("Pos")).add(new DoubleTag("0", ((Player)sender).x)).add(new DoubleTag("1", ((Player)sender).y + (double)((Player)sender).getEyeHeight())).add(new DoubleTag("2", ((Player)sender).z))).putList((new ListTag("Motion")).add(new DoubleTag("0", 0.0D)).add(new DoubleTag("1", 0.0D)).add(new DoubleTag("2", 0.0D))).putList((new ListTag("Rotation")).add(new FloatTag("0", (float)((Player)sender).getYaw())).add(new FloatTag("1", (float)((Player)sender).getPitch()))).putBoolean("Invulnerable", true).putCompound("Skin", (new CompoundTag()).putBoolean("Transparent", false).putByteArray("Data", ((Player)sender).getSkin().getData()).putString("ModelId", ((Player)sender).getSkin().getModel())).putString("KitName", args[0]);
                        KitEntity ent = new KitEntity(((Player)sender).chunk, nbt);
                        ent.getInventory().setItemInHand(((Player)sender).getInventory().getItemInHand());
                        ent.getInventory().setArmorContents(((Player)sender).getInventory().getArmorContents());
                        ent.spawnToAll();
                  }
                  break;
            case "chunkmap":
                  p = (Player)sender;
                  FullChunk chunk = p.chunk;
                  int distance = 0;

                  try {
                        Field field = p.getClass().getDeclaredField("chunkRadius");
                        field.setAccessible(true);
                        distance = (Integer)field.get(p);
                  } catch (Exception var24) {
                        var24.printStackTrace();
                  }

                  System.out.println(distance);
                  String[][] lines = new String[distance][distance];
                  int centerX = (int)p.x >> 4;
                  int centerZ = (int)p.z >> 4;

                  int z;
                  int j;
                  for(int x = -distance; x <= distance; ++x) {
                        for(z = -distance; z <= distance; ++z) {
                              int chunkX = x + centerX;
                              j = z + centerZ;
                              distance0 = (int)Math.sqrt((double)x * (double)x + (double)z * (double)z);
                              if (distance0 <= distance) {
                                    lines[x + distance][z + distance] = p.usedChunks.containsKey(Level.chunkHash(chunkX, j)) ? " " : "X";
                              }
                        }
                  }

                  String s = "";

                  for(z = 0; z < lines.length; ++z) {
                        String[] line = lines[z];

                        for(j = 0; j < line.length; ++j) {
                              s = s + line[j];
                        }

                        s = s + "\n";
                  }

                  System.out.println(s);
                  break;
            case "reloadchunks":
                  p = (Player)sender;
                  p.usedChunks.clear();
                  ChunkRadiusUpdatedPacket updatedPacket = new ChunkRadiusUpdatedPacket();
                  System.out.println(this.getChunkRadius(p));
                  updatedPacket.radius = this.getServer().getViewDistance();
                  p.dataPacket(updatedPacket);
                  break;
            case "gendebug":
                  if (sender.isOp()) {
                        DEBUG = !DEBUG;
                  }
                  break;
            case "entities":
                  p = (Player)sender;
                  Map entities = new HashMap();
                  Entity[] var19 = p.getLevel().getEntities();
                  distance0 = var19.length;

                  for(int var21 = 0; var21 < distance0; ++var21) {
                        Entity entity = var19[var21];
                        Integer count = (Integer)entities.get(entity.getName());
                        count = count != null ? count + 1 : 1;
                        entities.put(entity.getName(), count);
                  }

                  String ss = "";

                  Entry entry;
                  for(Iterator var32 = entities.entrySet().iterator(); var32.hasNext(); ss = ss + (String)entry.getKey() + " - " + entry.getValue() + "\n") {
                        entry = (Entry)var32.next();
                  }

                  p.sendMessage(ss);
                  p.sendMessage("threads: " + Thread.activeCount());
            }

            return true;
      }

      public PlayerData getPlayerData(Player p) {
            return (PlayerData)this.players.get(p.getId());
      }

      @EventHandler
      public void onDataLoad(PlayerLoadDataEvent e) {
            Player p = e.getPlayer();
            new JoinQuery(p);
      }

      @EventHandler
      public void onInteract(PlayerInteractEvent e) {
            Player p = e.getPlayer();
            Block b = e.getBlock();
            Action action = e.getAction();
            int chunkX = b.getFloorX() >> 4;
            int chunkZ = b.getFloorZ() >> 4;
            if (b.getLevel().getId() == this.getServer().getDefaultLevel().getId() && action == Action.RIGHT_CLICK_BLOCK && b instanceof BlockSignPost) {
                  e.setCancelled();
                  PlayerData data = this.getPlayerData(p);
                  if (data.getArena() == null) {
                        Arena arena = this.getArenaBySign(b);
                        if (arena != null) {
                              arena.onJoin(p, this.getPlayerData(p));
                        }

                  }
            }
      }

      private Arena getArenaBySign(Vector3 pos) {
            Iterator var2 = this.arenas.values().iterator();

            Arena arena;
            do {
                  if (!var2.hasNext()) {
                        return null;
                  }

                  arena = (Arena)var2.next();
            } while(!arena.data.sign.equals(pos));

            return arena;
      }

      public Arena getPlayerArena(Player p) {
            return this.getPlayerData(p).getArena();
      }

      @EventHandler
      public void onQuit(PlayerQuitEvent e) {
            Player p = e.getPlayer();
            PlayerData data = (PlayerData)this.players.remove(p.getId());
            if (data != null) {
                  new QuitQuery(new PlayerData[]{data});
            }

      }

      public static String getPrefix() {
            return TextFormat.GOLD + "[UHC] " + TextFormat.WHITE;
      }

      @EventHandler
      public void onInteract(PlayerInteractEntityEvent e) {
            Entity entity = e.getEntity();
            Player p = e.getPlayer();
            PlayerData data = this.getPlayerData(p);
            if (!data.isInitialized()) {
                  e.setCancelled();
            } else {
                  if (entity instanceof KitEntity) {
                        e.setCancelled();
                        Kit kit = ((KitEntity)entity).getKit();
                        String firstLine = TextFormat.GRAY + "========[ " + TextFormat.BLUE + kit.getName() + TextFormat.GRAY + " ]========\n";
                        p.sendMessage(firstLine + TextFormat.YELLOW + ">>  " + TextFormat.GRAY + Language.translate(kit.getMessage(), data.getData()));
                  }

            }
      }

      @EventHandler
      public void onEntityClick(EntityDamageEvent ev) {
            if (!(ev instanceof EntityDamageByEntityEvent)) {
                  Entity entity = ev.getEntity();
                  if (entity.getLevel().getId() == MTCore.getInstance().level.getId() && ev.getCause() == DamageCause.VOID && entity instanceof Player) {
                        entity.teleport(((Player)entity).getSpawn());
                  }

            } else {
                  EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)ev;
                  Entity entity = e.getEntity();
                  Entity damager = e.getDamager();
                  if (entity instanceof TeamEntity) {
                        e.setCancelled();
                        if (damager instanceof Player) {
                              Player p = (Player)damager;
                              if (p.isOp() && p.getInventory().getItemInHand().getId() == 334) {
                                    entity.close();
                                    return;
                              }
                        }

                  } else {
                        if (entity instanceof KitEntity) {
                              e.setCancelled();
                              KitEntity kitEntity = (KitEntity)entity;
                              if (damager instanceof Player) {
                                    Player p = (Player)damager;
                                    if (p.isOp() && p.isCreative()) {
                                          Item item = p.getInventory().getItemInHand();
                                          if (item.getId() == 334) {
                                                kitEntity.getInventory().clearAll();
                                                kitEntity.getInventory().sendContents(kitEntity.getViewers().values());
                                                kitEntity.getInventory().sendArmorContents(kitEntity.getViewers().values());
                                                return;
                                          }

                                          if (!item.isArmor() && item.getId() != 397) {
                                                kitEntity.getInventory().setItemInHand(item);
                                                kitEntity.getInventory().sendHeldItem(kitEntity.getViewers().values());
                                          } else {
                                                if (item.isChestplate()) {
                                                      kitEntity.getInventory().setChestplate(item);
                                                } else if (!item.isHelmet() && item.getId() != 397) {
                                                      if (item.isLeggings()) {
                                                            kitEntity.getInventory().setLeggings(item);
                                                      } else if (item.isBoots()) {
                                                            kitEntity.getInventory().setBoots(item);
                                                      }
                                                } else {
                                                      kitEntity.getInventory().setHelmet(item);
                                                }

                                                kitEntity.getInventory().sendArmorContents(kitEntity.getViewers().values());
                                          }

                                          return;
                                    }

                                    PlayerData data = this.getPlayerData(p);
                                    if (!data.isInitialized()) {
                                          e.setCancelled();
                                          return;
                                    }

                                    Kit kit = kitEntity.getKit();
                                    long time = System.currentTimeMillis();
                                    boolean hasKit = data.kits.contains(kit);
                                    if (!hasKit) {
                                          if (time - data.lastNPCClick < 300L && data.lastNPCId == kitEntity.getId()) {
                                                data.lastNPCClick = 0L;
                                                if (data.getData().getMoney() < kit.getCost()) {
                                                      p.sendMessage(getPrefix() + Language.translate("low_money", data.getData()));
                                                      return;
                                                }

                                                data.kits.add(kit);
                                                p.sendMessage(getPrefix() + Language.translate("buy_kit", data.getData(), kit.getName()));
                                          } else {
                                                p.sendMessage(getPrefix() + Language.translate("buy_kit_help", data.getData()));
                                                data.lastNPCClick = time;
                                                data.lastNPCId = kitEntity.getId();
                                          }
                                    } else {
                                          p.sendMessage(getPrefix() + Language.translate("kit_already", data.getData()));
                                    }
                              }
                        }

                  }
            }
      }

      @EventHandler
      public void onJoin(PlayerJoinEvent e) {
            Player p = e.getPlayer();
            this.players.put(p.getId(), new PlayerData(p, MTCore.getInstance().getPlayerData(p)));
      }

      @EventHandler
      public void onEntitySpawn(EntitySpawnEvent e) {
            Entity entity = e.getEntity();
      }

      public void onChunkLoad(ChunkLoadEvent e) {
            FullChunk chunk = e.getChunk();
            System.out.println("X: " + chunk.getX() + "   Z: " + chunk.getZ());
      }

      private int getChunkRadius(Player p) {
            try {
                  Field field = p.getClass().getSuperclass().getDeclaredField("chunkRadius");
                  field.setAccessible(true);
                  System.out.println(field);
                  return (Integer)field.get(p);
            } catch (Exception var3) {
                  var3.printStackTrace();
                  return -1;
            }
      }

      public void onDataPacketReceive(DataPacketReceiveEvent e) {
            if (e.getPacket().pid() == 69) {
                  RequestChunkRadiusPacket pk = (RequestChunkRadiusPacket)e.getPacket();
                  System.out.println("chunk radius packet: " + pk.radius);
            }

      }

      public String getInfoAbout() {
            return null;
      }

      public String getMinigameName() {
            return "uhc";
      }

      @EventHandler
      public void onAchievement(PlayerAchievementAwardedEvent e) {
            e.setCancelled();
      }

      public String getShortName() {
            return "uhc";
      }

      public static UltraHardcore getInstance() {
            return instance;
      }

      public String getId() {
            return this.id;
      }

      public HashMap getArenas() {
            return this.arenas;
      }
}
