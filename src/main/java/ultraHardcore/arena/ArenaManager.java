package ultraHardcore.arena;

import GTCore.Object.PlayerData;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockSkull;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.blockentity.BlockEntitySkull;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.TextFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import ultraHardcore.UltraHardcore;
import ultraHardcore.arena.kit.Kit;
import ultraHardcore.arena.object.ArenaPlayerData;
import ultraHardcore.arena.object.Team;
import ultraHardcore.language.Language;
import ultraHardcore.stats.Stats;
import ultraHardcore.util.TeamColor;

public abstract class ArenaManager {
      private static final Comparator teamComparator = new Comparator() {
            public int compare(Team team1, Team team2) {
                  int players1 = team1.getPlayers().size();
                  int players2 = team2.getPlayers().size();
                  return players1 > players2 ? 1 : (players1 == players2 ? 0 : -1);
            }
      };
      public Arena plugin;

      public boolean inArena(Player p) {
            return this.inArena(p.getName());
      }

      protected boolean inArena(String p) {
            return this.plugin.players.containsKey(p.toLowerCase());
      }

      protected void registerTeams() {
            this.plugin.teams.clear();
      }

      protected boolean isArenaFull() {
            return this.plugin.players.size() >= 56;
      }

      public ArenaPlayerData getPlayerData(Player p) {
            return this.getPlayerData(p.getName());
      }

      protected ArenaPlayerData getPlayerData(String name) {
            return (ArenaPlayerData)this.plugin.playersData.get(name.toLowerCase());
      }

      public boolean checkBorder(Player p) {
            if (!this.plugin.border.isVectorInside(p)) {
                  p.attack(new EntityDamageEvent(p, DamageCause.CUSTOM, 2.0F));
                  return false;
            } else {
                  return true;
            }
      }

      protected void makeTeams() {
            if (this.plugin.getData().teamSize > 1) {
                  List teams = new ArrayList();
                  List freePlayers = new ArrayList();
                  int freeSpace = 0;
                  int max = this.plugin.getData().teamSize;
                  Iterator playerIterator = this.plugin.playersData.values().iterator();

                  while(playerIterator.hasNext()) {
                        ArenaPlayerData data = (ArenaPlayerData)playerIterator.next();
                        Team team = data.getTeam();
                        if (team.getPlayers().size() == 1) {
                              freePlayers.add(data);
                        } else if (team.getPlayers().size() < max && teams.indexOf(team) < 0) {
                              freeSpace += max - team.getPlayers().size();
                              teams.add(team);
                        }
                  }

                  Collections.sort(teams, teamComparator);
                  int index;
                  ArenaPlayerData data;
                  if (freePlayers.size() > 0) {
                        Player player;
                        Team team;
                        while(!freePlayers.isEmpty() && freeSpace > 0) {
                              for(index = 0; index < teams.size(); ++index) {
                                    team = (Team)teams.get(index);
                                    if (team.getPlayers().size() < max) {
                                          if (freePlayers.size() <= 0) {
                                                break;
                                          }

                                          data = (ArenaPlayerData)freePlayers.remove(0);
                                          player = data.getPlayerData().getPlayer();
                                          team.players.put(player.getName().toLowerCase(), player);
                                          data.setTeam(team);
                                          --freeSpace;
                                    }
                              }
                        }

                        if (freePlayers.size() > 0) {
                              playerIterator = freePlayers.iterator();
                              team = null;

                              while(playerIterator.hasNext()) {
                                    data = (ArenaPlayerData)playerIterator.next();
                                    player = data.getPlayerData().getPlayer();
                                    if (team == null) {
                                          team = data.getTeam();
                                    } else {
                                          team.players.put(player.getName().toLowerCase(), player);
                                          data.setTeam(team);
                                          if (team.players.size() >= max) {
                                                team = null;
                                          }
                                    }
                              }
                        }
                  }

                  this.plugin.teams.clear();
                  index = 0;

                  for(Iterator var12 = this.plugin.playersData.values().iterator(); var12.hasNext(); ++index) {
                        data = (ArenaPlayerData)var12.next();
                        Team team = data.getTeam();
                        team.setColor(TeamColor.values()[index % TeamColor.values().length]);
                        this.plugin.teams.put(index, team);
                  }

            }
      }

      protected void processDeath(Player p, ArenaPlayerData data, EntityDamageEvent event) {
            event.setCancelled();
            FullChunk chunk = p.chunk;
            CompoundTag nbt = (new CompoundTag()).putList((new ListTag("Pos")).add(new DoubleTag("", p.getX())).add(new DoubleTag("", p.getY())).add(new DoubleTag("", p.getZ()))).putList((new ListTag("Motion")).add(new DoubleTag("", 0.0D)).add(new DoubleTag("", 0.0D)).add(new DoubleTag("", 0.0D))).putList((new ListTag("Rotation")).add(new FloatTag("", 0.0F)).add(new FloatTag("", 0.0F)));
            EntityLightning lightning = new EntityLightning(chunk, nbt);
            lightning.setEffect(false);
            lightning.spawnToAll();
            CompoundTag nbt3 = (new CompoundTag()).putString("id", "Skull").putByte("SkullType", 3).putInt("x", p.getFloorX()).putInt("y", p.getFloorY() + 1).putInt("z", p.getFloorZ()).putByte("Rot", (int)p.yaw);
            data.addStat(Stats.Stat.KILL);
            if (data.getKit() != Kit.MISER) {
                  CompoundTag nbt2 = (new CompoundTag("")).putList(new ListTag("Items")).putString("id", "Chest").putInt("x", (int)p.x).putInt("y", (int)p.y).putInt("z", (int)p.z);
                  BlockEntityChest blockEntityChest = new BlockEntityChest(p.chunk, nbt2);
                  ArrayList drops = new ArrayList();
                  drops.addAll(p.getInventory().getContents().values());
                  drops.addAll(Arrays.asList(p.getInventory().getArmorContents()));
                  if (data.getKit() == Kit.CREEPER) {
                        Explosion explosion = new Explosion(p, 0.8D, p);
                        explosion.explodeA();
                        explosion.explodeB();
                  } else if (data.getKit() == Kit.LINKAGE) {
                        ArrayList drops2 = new ArrayList();
                        Iterator var12 = drops.iterator();

                        while(var12.hasNext()) {
                              Item item = (Item)var12.next();
                              if (item.getId() != 52) {
                                    drops2.add(item);
                              }
                        }

                        drops = drops2;
                  }

                  Iterator var16 = drops.iterator();

                  while(var16.hasNext()) {
                        Item item = (Item)var16.next();
                        blockEntityChest.getInventory().addItem(new Item[]{item});
                  }

                  blockEntityChest.saveNBT();
                  nbt3.putList(blockEntityChest.namedTag.getList("Items"));
            }

            new BlockEntitySkull(p.chunk, nbt3);
            this.plugin.level.setBlock(p, new BlockSkull(), true, false);
            if (event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent)event).getDamager() instanceof Player) {
                  Player damager = (Player)((EntityDamageByEntityEvent)event).getDamager();
                  this.messageArenaPlayers("contact", true, damager.getDisplayName(), p.getDisplayName(), "" + this.plugin.players.size());
            }

            p.getInventory().clearAll();
            p.getInventory().sendContents(p);
            p.setHealth(20.0F);
            p.getFoodData().setLevel(20, 20.0F);
            p.setExperience(0, 0);
            p.setMovementSpeed(0.1F);
            p.setLastDamageCause(event);
            this.plugin.onLeave(p, false);
      }

      protected int getMaxPlayers() {
            return 52;
      }

      public void messageArenaPlayers(String message, boolean translate, String... parameters) {
            if (!translate) {
                  this.plugin.players.values().forEach((p) -> {
                        p.sendMessage(message);
                  });
            } else {
                  HashMap translations = Language.getTranslations(message, parameters);
                  Iterator var5 = this.plugin.playersData.values().iterator();

                  while(var5.hasNext()) {
                        ArenaPlayerData data = (ArenaPlayerData)var5.next();
                        data.getPlayerData().getPlayer().sendMessage((String)translations.get(data.getPlayerData().getData().getLanguage()));
                  }

            }
      }

      public void messageAllPlayers(String message) {
            this.messageAllPlayers(message, false);
      }

      public void messageAllPlayers(String message, String... args) {
            this.messageAllPlayers(message, (Player)null, (ArenaPlayerData)null, false, args);
      }

      public void messageAllPlayers(String message, boolean addPrefix, String... args) {
            this.messageAllPlayers(message, (Player)null, (ArenaPlayerData)null, addPrefix, args);
      }

      public void messageAllPlayers(String message, Player player, ArenaPlayerData data) {
            this.messageAllPlayers(message, player, data, false);
      }

      public void messageAllPlayers(String message, Player player, ArenaPlayerData data, boolean addPrefix, String... args) {
            Player p;
            if (player == null) {
                  HashMap translations = Language.getTranslations(message, args);
                  Iterator var11 = this.plugin.playersData.values().iterator();

                  while(var11.hasNext()) {
                        ArenaPlayerData pData = (ArenaPlayerData)var11.next();
                        p = pData.getPlayerData().getData().getPlayer();
                        if (p.isOnline()) {
                              p.sendMessage(addPrefix ? UltraHardcore.getPrefix() : "" + (String)translations.get(pData.getPlayerData().getData().getLanguage()));
                        }
                  }

            } else {
                  PlayerData baseData = data.getPlayerData().getData();
                  String msg;
                  if (this.plugin.phase > 0) {
                        msg = TextFormat.GRAY + "[" + data.getTeam().getColor() + "All" + TextFormat.GRAY + "]   " + TextFormat.RED + "[" + TextFormat.GREEN + baseData.getLevel() + TextFormat.RED + "] " + baseData.getPrefix() + data.getTeam().getColor() + player.getName() + baseData.getSuffix() + baseData.getChatColor() + ": " + message;
                  } else {
                        msg = TextFormat.RED + "[" + TextFormat.GREEN + baseData.getLevel() + TextFormat.RED + "] " + baseData.getPrefix() + player.getName() + baseData.getSuffix() + baseData.getChatColor() + ": " + message;
                  }

                  Iterator var8 = (new ArrayList(this.plugin.players.values())).iterator();

                  while(var8.hasNext()) {
                        p = (Player)var8.next();
                        p.sendMessage(msg);
                  }

            }
      }

      public Team getTeam(int id) {
            return (Team)this.plugin.teams.get(id);
      }

      protected void checkAlive() {
            if (this.plugin.phase == 1 || this.plugin.phase == 2) {
                  Team[] teams = this.getAliveTeams();
                  if (teams.length <= 0) {
                        this.plugin.stop();
                        return;
                  }

                  if (teams.length == 1) {
                        this.plugin.winnerTeam = teams[0];
                        this.plugin.end();
                  }
            }

      }

      public Team[] getAliveTeams() {
            ArrayList teams = new ArrayList();
            Iterator var2 = this.plugin.teams.values().iterator();

            while(var2.hasNext()) {
                  Team team = (Team)var2.next();
                  if (team.getPlayers().size() > 0) {
                        teams.add(team);
                  }
            }

            return (Team[])teams.stream().toArray((x$0) -> {
                  return new Team[x$0];
            });
      }

      private String getDisplayPhase(int phase, int gamePhase) {
            switch(phase) {
            case 0:
                  if (this.plugin.starting) {
                        return TextFormat.GOLD + "Starting " + TextFormat.GRAY + "(" + this.plugin.task.startTime + ")";
                  }

                  return TextFormat.GREEN + "Lobby";
            case 1:
                  switch(gamePhase) {
                  case 0:
                        return "" + TextFormat.BOLD + TextFormat.DARK_GREEN + "PREPARING";
                  case 1:
                        return "" + TextFormat.BOLD + TextFormat.GOLD + "FIGHTING";
                  case 2:
                        return "" + TextFormat.BOLD + TextFormat.RED + "DEATHMATCH";
                  }
            default:
                  return "";
            case 2:
                  return TextFormat.BLACK + "Restarting...";
            }
      }

      public void updateSign() {
            Vector3 sign = this.plugin.data.sign;
            Level level = Server.getInstance().getDefaultLevel();
            BlockEntity entity = level.getBlockEntity(sign);
            String line1 = TextFormat.DARK_RED + "> " + this.plugin.getId() + " <";
            String line2 = TextFormat.DARK_GRAY + "--------------";
            String line3 = "" + TextFormat.GRAY + this.plugin.players.size() + "/52";
            String line4;
            if (this.plugin.phase == 0 && this.plugin.percentage != null) {
                  line4 = TextFormat.BLACK + "Resetting... (" + this.plugin.percentage + ")";
            } else {
                  line4 = this.getDisplayPhase(this.plugin.phase, this.plugin.gamePhase);
            }

            if (!(entity instanceof BlockEntitySign)) {
                  CompoundTag nbt = (new CompoundTag()).putString("id", "Sign").putString("Text1", line1).putString("Text2", line2).putString("Text3", line3).putString("Text4", line4).putInt("x", sign.getFloorX()).putInt("y", sign.getFloorY()).putInt("z", sign.getFloorZ());
                  (new BlockEntitySign(level.getChunk((int)sign.x >> 4, (int)sign.z >> 4), nbt)).spawnToAll();
            } else {
                  BlockEntitySign blockEntitySign = (BlockEntitySign)entity;
                  blockEntitySign.setText(new String[]{line1, line2, line3, line4});
            }

      }
}
