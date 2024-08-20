package ultraHardcore.arena;

import GTCore.MTCore;
import GTCore.Object.BossBar;
import cn.nukkit.Player;
import cn.nukkit.AdventureSettings.Type;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockMobSpawner;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySkull;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.level.ChunkUnloadEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.inventory.Recipe;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemIngotGold;
import cn.nukkit.item.ItemIngotIron;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import ultraHardcore.UltraHardcore;
import ultraHardcore.arena.kit.Kit;
import ultraHardcore.arena.kit.KitManager;
import ultraHardcore.arena.kit.LinkageData;
import ultraHardcore.arena.manager.FormWindowManager;
import ultraHardcore.arena.object.ArenaPlayerData;
import ultraHardcore.arena.object.Border;
import ultraHardcore.arena.object.PlayerData;
import ultraHardcore.arena.object.Resource;
import ultraHardcore.arena.object.Team;
import ultraHardcore.arena.task.ArenaSchedule;
import ultraHardcore.arena.task.GenerateWorldTask;
import ultraHardcore.arena.task.PopupTask;
import ultraHardcore.entity.other.TeamEntity;
import ultraHardcore.language.Language;
import ultraHardcore.stats.Stats;
import ultraHardcore.util.ArenaData;
import ultraHardcore.util.BossBarUtil;
import ultraHardcore.util.TreeCapitator;
import ultraHardcore.util.Utils;

public class Arena extends ArenaManager implements Listener {
      public static final int LOBBY = 0;
      public static final int GAME = 1;
      public static final int ENDING = 2;
      public static final int PREPARING = 0;
      public static final int FIGHTING = 1;
      public static final int DEATHMATCH = 2;
      private String id;
      private UltraHardcore plugin;
      public Level level = null;
      public BossBar bossBar = null;
      public BossBarUtil bossBarUtil = null;
      public Border border;
      public int phase = 0;
      public int gamePhase = 0;
      public boolean starting = false;
      protected HashMap teams = new HashMap();
      public final HashMap players = new HashMap();
      public final HashMap spectators = new HashMap();
      public final HashMap playersData = new HashMap();
      private final HashMap entities = new HashMap();
      public final ArenaData data;
      public final KitManager kitManager;
      public FormWindowManager formWindowManager;
      public Team winnerTeam = null;
      public PopupTask popupTask;
      public ArenaSchedule task;
      public volatile boolean isMapLoaded = false;
      public volatile String percentage = "0%";
      private static HashMap drops = new HashMap() {
            {
                  this.put(15, new ItemIngotIron());
                  this.put(14, new ItemIngotGold());
                  this.put(16, Item.get(50, 0, 4));
            }
      };

      public Arena(UltraHardcore plugin, String id, ArenaData data) {
            super.plugin = this;
            this.id = id;
            this.plugin = plugin;
            this.data = data;
            this.bossBar = new BossBar(plugin);
            this.bossBarUtil = new BossBarUtil(this);
            this.kitManager = new KitManager();
            this.formWindowManager = new FormWindowManager(this);
            this.task = new ArenaSchedule(this);
            this.registerTeams();
            plugin.getServer().getScheduler().scheduleDelayedRepeatingTask(this.task, 20, 20);
            this.bossBarUtil.updateBar(0);
            this.updateSign();
            if (!GenerateWorldTask.running) {
                  plugin.getServer().getScheduler().scheduleAsyncTask(new GenerateWorldTask(this));
            } else {
                  GenerateWorldTask.generatorQueue.add(new GenerateWorldTask(this));
            }

      }

      public boolean start() {
            if (this.phase <= 0 && this.isMapLoaded) {
                  if (this.players.size() < 1) {
                        return false;
                  } else {
                        this.plugin.getServer().loadLevel(this.getId());
                        this.level = this.plugin.getServer().getLevelByName(this.getId());
                        if (this.level == null) {
                              return false;
                        } else {
                              this.level.setTime(0);
                              this.level.setSpawnLocation(this.level.getSpawnLocation().add(0.0D, 80.0D - this.level.getSpawnLocation().y));
                              this.border = new Border(this, this.level.getSpawnLocation());
                              this.popupTask = new PopupTask(this);
                              this.plugin.getServer().getScheduler().scheduleDelayedRepeatingTask(this.popupTask, 20, 12);
                              Position pos = this.level.getSafeSpawn();
                              this.makeTeams();
                              Iterator var2 = (new ArrayList(this.playersData.values())).iterator();

                              while(var2.hasNext()) {
                                    ArenaPlayerData data = (ArenaPlayerData)var2.next();
                                    Player p = data.getPlayerData().getPlayer();
                                    if (data.getTeam() == null) {
                                          this.onLeave(p);
                                    } else {
                                          p.removeAllEffects();
                                          p.getFoodData().setLevel(20);
                                          if (data.getKit() == Kit.GHOST) {
                                                Effect invisible = Effect.getEffect(14);
                                                invisible.setDuration(3600);
                                                invisible.setVisible(false);
                                                p.addEffect(invisible);
                                          } else if (data.getKit() == Kit.LINKAGE) {
                                                this.kitManager.linkageData.put(p.getId(), new LinkageData(p));
                                          }

                                          p.getInventory().clearAll();
                                          data.getKit().giveItems(p);
                                          p.teleport(pos);
                                    }
                              }

                              this.phase = 1;
                              this.changeGamePhase(0);
                              this.updateSign();
                              return true;
                        }
                  }
            } else {
                  return false;
            }
      }

      public void end() {
            this.phase = 2;
            this.updateSign();
            Iterator var1 = this.players.values().iterator();

            Player p;
            while(var1.hasNext()) {
                  p = (Player)var1.next();
                  p.getInventory().clearAll();
                  p.getInventory().sendContents(p);
                  p.setExperience(0, 0);
                  p.setHealth(20.0F);
                  p.getFoodData().setLevel(20, 20.0F);
                  p.getAdventureSettings().set(Type.ALLOW_FLIGHT, true);
                  p.getAdventureSettings().set(Type.FLYING, true);
                  p.getAdventureSettings().update();
            }

            var1 = this.winnerTeam.players.values().iterator();

            while(var1.hasNext()) {
                  p = (Player)var1.next();
                  this.getPlayerData(p).addStat(Stats.Stat.WIN);
            }

            this.messageArenaPlayers("end_game", true, new String[]{this.winnerTeam.getColor().getChatColor().toString(), this.winnerTeam.getColor().getChatColor() + "#" + this.winnerTeam.getId()});
      }

      public void stop() {
            this.task.reset();
            this.phase = 0;
            this.gamePhase = 0;
            this.starting = false;
            Iterator var1 = (new ArrayList(this.playersData.values())).iterator();

            while(var1.hasNext()) {
                  ArenaPlayerData data = (ArenaPlayerData)var1.next();
                  Player p = data.getPlayerData().getPlayer();
                  this.onLeave(p, false);
            }

            this.registerTeams();
            this.winnerTeam = null;
            var1 = this.entities.values().iterator();

            while(var1.hasNext()) {
                  TeamEntity teamEntity = (TeamEntity)var1.next();
                  teamEntity.updateNameTag(0);
            }

            this.level.unload();
            this.updateSign();
            this.isMapLoaded = false;
            this.percentage = "0%";
            if (!GenerateWorldTask.running) {
                  this.plugin.getServer().getScheduler().scheduleAsyncTask(new GenerateWorldTask(this));
            } else {
                  GenerateWorldTask.generatorQueue.add(new GenerateWorldTask(this));
            }

      }

      public void onJoin(Player p, PlayerData globalData) {
            GTCore.Object.PlayerData playerData = globalData.getData();
            if (!this.isMapLoaded) {
                  p.sendMessage(Language.translate("arena_reset", playerData));
            } else if (this.phase > 0) {
                  p.sendMessage(Language.translate("game_running", playerData));
            } else if (!p.hasPermission("gameteam.vip") && this.isArenaFull()) {
                  p.sendMessage(Language.translate("full_team", playerData));
            } else {
                  MTCore.getInstance().unsetLobby(p);
                  p.sendMessage(Language.translate("join", globalData.getData(), this.getId()));
                  p.sendMessage(Language.translate("open_inventory_kits", globalData.getData()));
                  this.messageArenaPlayers("join_others", true, new String[]{p.getName()});
                  globalData.arena = this;
                  ArenaPlayerData data = new ArenaPlayerData(this, globalData);
                  this.playersData.put(p.getName().toLowerCase(), data);
                  this.players.put(p.getName().toLowerCase(), p);
                  MTCore.getInstance().unsetLobby(p);
                  p.getInventory().setItem(3, Item.get(276).setCustomName(TextFormat.YELLOW + "Kit Select"));
                  p.getInventory().setItem(4, Item.get(358).setCustomName(TextFormat.YELLOW + "Team Manager"));
                  this.bossBar.addPlayer(p);
                  this.bossBarUtil.updateBar(this.players.size());
                  p.teleport(this.data.lobby);
                  if (this.players.size() > 16) {
                        this.starting = true;
                  }

                  this.updateSign();
            }
      }

      public void onLeave(Player p) {
            this.onLeave(p, true);
      }

      public void onLeave(Player p, boolean message) {
            Player player = (Player)this.players.remove(p.getName().toLowerCase());
            if (player != null) {
                  this.bossBar.removePlayer(p);
                  p.teleport(MTCore.getInstance().lobby);
                  p.setSpawn(MTCore.getInstance().lobby);
                  if (this.phase == 0) {
                        this.bossBarUtil.updateBar(this.players.size());
                  }

                  ArenaPlayerData data = (ArenaPlayerData)this.playersData.remove(p.getName().toLowerCase());
                  this.players.remove(p.getName().toLowerCase());
                  if (data != null) {
                        Team team = data.getTeam();
                        if (team != null) {
                              team.players.remove(p.getName().toLowerCase());
                              data.setTeam((Team)null);
                              if (team.players.size() > 0) {
                                    Player newOwner = (Player)team.getPlayers().values().iterator().next();
                                    newOwner.sendMessage(Language.translate("become_owner", newOwner));
                                    team.setOwner(newOwner);
                              }
                        }

                        data.getPlayerData().arena = null;
                  }

                  if (message) {
                        this.messageArenaPlayers("leave", true, new String[]{p.getDisplayName(), "" + this.players.size(), "" + this.getMaxPlayers()});
                  }

                  if (this.phase == 1) {
                        this.plugin.getServer().getScheduler().scheduleDelayedTask(new Runnable() {
                              public void run() {
                                    Arena.this.checkAlive();
                              }
                        }, 1);
                  }

                  this.updateSign();
            }
      }

      public void joinSpectator(Player p) {
      }

      @EventHandler(
            ignoreCancelled = true
      )
      public void onBlockBreak(BlockBreakEvent e) {
            Player p = e.getPlayer();
            Block b = e.getBlock();
            ArenaPlayerData data = this.getPlayerData(p);
            if (data != null) {
                  if (b.getId() == 144) {
                        BlockEntity blockEntity = b.getLevel().getBlockEntity(b);
                        if (blockEntity instanceof BlockEntitySkull) {
                              List drops = new ArrayList();
                              ListTag items = blockEntity.namedTag.getList("Items", CompoundTag.class);
                              if (items != null && items.size() > 0) {
                                    for(int i = 0; i < items.size(); ++i) {
                                          CompoundTag tag = (CompoundTag)items.get(i);
                                          if (tag != null) {
                                                Item item = NBTIO.getItemHelper(tag);
                                                if (item.getId() != 0) {
                                                      drops.add(item);
                                                }
                                          }
                                    }
                              }

                              if (!drops.isEmpty()) {
                                    drops.forEach((ix) -> {
                                          b.getLevel().dropItem(b, ix);
                                    });
                              }

                              p.addEffect(Effect.getEffect(10).setDuration(80).setAmplifier(1).setVisible(false));
                        }
                  } else if (b.getId() == 52) {
                        e.setDrops(new Item[0]);
                        LinkageData linkageData = this.kitManager.getLinkageData(b);
                        if (linkageData == null) {
                              return;
                        }

                        linkageData.removePos(b);
                        Item item = new ItemBlock(new BlockMobSpawner());
                        if (!linkageData.owner.getInventory().canAddItem(item)) {
                              linkageData.owner.getInventory().setItem(35, item);
                        } else {
                              linkageData.owner.getInventory().addItem(new Item[]{item});
                        }
                  } else if (b.getId() != 17 && b.getId() != 162) {
                        e.setDrops(Resource.getDrops(b, e.getItem()));
                  } else {
                        e.setDrops(TreeCapitator.breakTree(b));
                  }

            }
      }

      @EventHandler(
            ignoreCancelled = true
      )
      public void onBlockPlace(BlockPlaceEvent e) {
            Player p = e.getPlayer();
            Block b = e.getBlock();
            ArenaPlayerData data = this.getPlayerData(p);
            if (data != null) {
                  if (data.getKit() == Kit.LINKAGE) {
                        LinkageData linkageData = (LinkageData)this.kitManager.linkageData.get(p.getId());
                        if (linkageData != null) {
                              linkageData.addPos(b);
                        }
                  }
            }
      }

      @EventHandler(
            ignoreCancelled = true
      )
      public void onInteract(PlayerInteractEvent e) {
            Player p = e.getPlayer();
            Block b = e.getBlock();
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                  if (b.getId() == 52) {
                        ArenaPlayerData data = this.getPlayerData(p);
                        if (data == null) {
                              return;
                        }

                        LinkageData linkageData = this.kitManager.getLinkageData(b);
                        if (linkageData == null) {
                              return;
                        }

                        linkageData.teleport(p, b);
                  }
            } else if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                  Item item = e.getItem();
                  if (this.phase == 0 && item.hasCustomName()) {
                        switch(item.getId()) {
                        case 276:
                              this.formWindowManager.addKitWindow(p);
                              break;
                        case 358:
                              this.formWindowManager.addBaseWindow(p);
                        }
                  }
            }

      }

      @EventHandler(
            ignoreCancelled = true
      )
      public void onHit(EntityDamageEvent e) {
            Entity entity = e.getEntity();
      }

      @EventHandler
      public void onHealthRegain(EntityRegainHealthEvent e) {
            if (e.getEntity() instanceof Player && e.getRegainReason() == 1) {
                  e.setCancelled();
            }

      }

      @EventHandler(
            ignoreCancelled = true
      )
      public void onChunkUnload(ChunkUnloadEvent e) {
            FullChunk chunk = e.getChunk();
            Iterator var3 = chunk.getEntities().values().iterator();

            Entity entity;
            do {
                  if (!var3.hasNext()) {
                        return;
                  }

                  entity = (Entity)var3.next();
            } while(!(entity instanceof TeamEntity));

            e.setCancelled();
      }

      @EventHandler
      public void onQuit(PlayerQuitEvent e) {
            this.onLeave(e.getPlayer());
      }

      public void changeGamePhase(int phase) {
            if (this.gamePhase != phase) {
                  switch(phase) {
                  case 0:
                        this.messageArenaPlayers("phase_preparing", true, new String[0]);
                        break;
                  case 1:
                        this.messageArenaPlayers("phase_fighting", true, new String[0]);
                        break;
                  case 2:
                        this.messageArenaPlayers("phase_deathmatch", true, new String[0]);
                        NukkitRandom rnd = new NukkitRandom();
                        AxisAlignedBB area = new AxisAlignedBB(this.border.minX + 10.0D, 0.0D, this.border.minZ + 10.0D, this.border.maxX - 10.0D, 0.0D, this.border.maxZ - 10.0D);
                        Iterator var4 = this.players.values().iterator();

                        while(var4.hasNext()) {
                              Player p = (Player)var4.next();
                              p.teleport(p.temporalVector.setComponents((double)rnd.nextRange((int)area.minX, (int)area.maxX), 128.0D, (double)rnd.nextRange((int)area.minZ, (int)area.maxZ)));
                        }
                  }

                  this.gamePhase = phase;
                  this.updateSign();
            }
      }

      @EventHandler(
            ignoreCancelled = true
      )
      public void onChat(PlayerChatEvent e) {
            if (!e.isCancelled()) {
                  e.setRecipients(new HashSet());
                  Player p = e.getPlayer();
                  ArenaPlayerData data = this.getPlayerData(p);
                  if (data != null) {
                        e.setCancelled();
                        if (this.phase != 0 && data.getTeam() != null && (!e.getMessage().startsWith("!") || e.getMessage().length() <= 1)) {
                              data.getTeam().message(e.getMessage(), p, data);
                        } else {
                              if (e.getMessage().startsWith("!")) {
                                    e.setMessage(e.getMessage().substring(1));
                              }

                              this.messageAllPlayers(e.getMessage(), p, data);
                        }
                  }
            }
      }

      @EventHandler(
            ignoreCancelled = true
      )
      public void onHunger(PlayerFoodLevelChangeEvent e) {
            Player p = e.getPlayer();
            if (this.inArena(p)) {
                  if (this.phase == 0 || this.gamePhase == 0) {
                        e.setCancelled();
                  }

            }
      }

      @EventHandler
      public void onItemConsume(PlayerItemConsumeEvent e) {
            Player p = e.getPlayer();
            if (this.inArena(p) && this.phase == 0) {
                  e.setCancelled();
            }
      }

      @EventHandler(
            ignoreCancelled = true
      )
      public void onCraft(CraftItemEvent e) {
            Player p = e.getPlayer();
            Recipe recipe = e.getRecipe();
            Item result = Utils.getCraftDrops(recipe);
            if (!result.equals(recipe.getResult())) {
                  e.setCancelled();
                  p.getInventory().removeItem(e.getInput());
                  p.getInventory().addItem(new Item[]{result});
                  p.getInventory().sendContents(p);
            }
      }

      @EventHandler(
            ignoreCancelled = true
      )
      public void onEntityDeath(EntityDeathEvent e) {
            Entity entity = e.getEntity();
            if (entity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                  e.setDrops(Utils.getEntityDrops(e.getDrops()));
            }

      }

      @EventHandler
      public void onFormWindowResponded(PlayerFormRespondedEvent e) {
            Player p = e.getPlayer();
            this.formWindowManager.handleResponse(p, e.getFormID(), e.getResponse());
      }

      public String getId() {
            return this.id;
      }

      public UltraHardcore getPlugin() {
            return this.plugin;
      }

      public ArenaData getData() {
            return this.data;
      }
}
