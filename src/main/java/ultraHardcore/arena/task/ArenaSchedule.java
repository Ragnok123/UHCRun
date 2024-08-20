package ultraHardcore.arena.task;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;
import java.util.Iterator;
import java.util.Map.Entry;
import ultraHardcore.arena.Arena;
import ultraHardcore.arena.kit.Kit;
import ultraHardcore.arena.object.ArenaPlayerData;
import ultraHardcore.language.Language;

public class ArenaSchedule implements Runnable {
      private Arena plugin;
      public int gameTime = 0;
      public int startTime = 50;
      private int endTime = 30;
      private boolean timeSwitch = true;

      public ArenaSchedule(Arena plugin) {
            this.plugin = plugin;
      }

      public void run() {
            if (this.plugin.phase == 0) {
                  this.plugin.updateSign();
                  if (this.plugin.starting) {
                        this.starting();
                  }
            } else if (this.plugin.phase == 1) {
                  this.running();
                  switch(this.plugin.gamePhase) {
                  case 0:
                  case 2:
                  default:
                        break;
                  case 1:
                        if (this.gameTime % 5 == 0) {
                              this.plugin.border.decrease();
                        }
                  }
            } else if (this.plugin.phase == 2) {
                  this.ending();
            }

      }

      private void starting() {
            this.plugin.bossBarUtil.updateBar(this.startTime);
            --this.startTime;
            if (this.startTime < 0) {
                  this.plugin.start();
            }

      }

      private void running() {
            ++this.gameTime;
            this.plugin.bossBarUtil.updateBar(this.gameTime);
            if (this.plugin.gamePhase == 0) {
                  if (this.gameTime > 720) {
                        this.plugin.changeGamePhase(1);
                  }
            } else if (this.plugin.gamePhase == 1) {
                  if (this.gameTime != 1020 && this.gameTime >= 1440) {
                        this.plugin.changeGamePhase(2);
                  }
            } else if (this.plugin.gamePhase == 2 && this.gameTime > 1800) {
                  this.plugin.winnerTeam = this.plugin.getAliveTeams()[0];
                  this.plugin.end();
            }

            this.checkNinjas();
            this.checkFlowers();
            Iterator var1 = this.plugin.playersData.values().iterator();

            while(var1.hasNext()) {
                  ArenaPlayerData data = (ArenaPlayerData)var1.next();
                  Player p = data.getPlayerData().getPlayer();
                  if (this.plugin.border.isVectorInside(p)) {
                        if (!this.plugin.checkBorder(p)) {
                              p.sendTitle(Language.translate("out_border", data.getPlayerData().getData()), "", 0, 40, 0);
                        } else {
                              double distance = this.plugin.border.distance(p);
                              if (distance <= 30.0D) {
                                    p.sendTitle(Language.translate("border_near", data.getPlayerData().getData()), "" + Math.round(distance), 0, 40, 0);
                              }
                        }
                  }
            }

      }

      private void ending() {
            --this.endTime;
            this.plugin.bossBarUtil.updateBar(this.endTime);
            String color = this.plugin.winnerTeam.getColor().toString();
            String message = TextFormat.BOLD + color + "Winners: (" + TextFormat.GRAY + String.join(color + ", " + TextFormat.GRAY, this.plugin.winnerTeam.players.keySet()) + color + ")";
            Iterator var3 = this.plugin.players.values().iterator();

            while(var3.hasNext()) {
                  Player p = (Player)var3.next();
                  p.sendTip(message);
            }

            if (this.endTime <= 0) {
                  this.plugin.stop();
            }

      }

      public void reset() {
            this.endTime = 30;
            this.startTime = 50;
            this.gameTime = 0;
            this.timeSwitch = true;
      }

      private void checkNinjas() {
            if (this.timeSwitch) {
                  if (this.plugin.level.getTime() > 0) {
                        this.timeSwitch = false;
                        this.plugin.playersData.values().forEach((data) -> {
                              if (data.getKit() == Kit.NINJA) {
                                    Player p = data.getPlayerData().getPlayer();
                                    p.removeEffect(16);
                                    Effect effect = Effect.getEffect(1);
                                    effect.setDuration(99999999);
                                    effect.setAmplifier(1);
                                    p.addEffect(effect);
                              }

                        });
                  }
            } else if (this.plugin.level.getTime() > 14000) {
                  this.timeSwitch = true;
                  Effect effect = Effect.getEffect(16);
                  effect.setDuration(99999999);
                  this.plugin.playersData.values().forEach((data) -> {
                        if (data.getKit() == Kit.NINJA) {
                              Player p = data.getPlayerData().getPlayer();
                              p.addEffect(effect);
                        }

                  });
            }

      }

      public void checkFlowers() {
            Iterator var1 = this.plugin.playersData.values().iterator();

            while(true) {
                  Player p;
                  do {
                        ArenaPlayerData data;
                        do {
                              if (!var1.hasNext()) {
                                    return;
                              }

                              data = (ArenaPlayerData)var1.next();
                        } while(data.getKit() != Kit.FLOWER);

                        p = data.getPlayerData().getPlayer();
                  } while(!p.getLevel().canBlockSeeSky(p.floor()));

                  Iterator var4 = p.getInventory().getContents().entrySet().iterator();

                  while(var4.hasNext()) {
                        Entry itemEntry = (Entry)var4.next();
                        Item item = (Item)itemEntry.getValue();
                        if (item.isTool() && item.getDamage() > 0) {
                              item.setDamage(Math.max(0, item.getDamage() - 10));
                              p.getInventory().setItem((Integer)itemEntry.getKey(), item);
                        }
                  }

                  p.getInventory().sendContents(p);
            }
      }
}
