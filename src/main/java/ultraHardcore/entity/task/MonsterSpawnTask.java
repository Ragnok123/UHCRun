package ultraHardcore.entity.task;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.scheduler.Task;
import java.util.ArrayList;
import java.util.Iterator;
import ultraHardcore.arena.Arena;

public class MonsterSpawnTask extends Task {
      private final Arena plugin;
      private final Level level;
      private final NukkitRandom random = new NukkitRandom();

      public MonsterSpawnTask(Arena plugin, Level level) {
            this.plugin = plugin;
            this.level = level;
      }

      public void onRun(int currentTick) {
            if (this.plugin.phase == 0) {
                  this.getHandler().cancel();
            }

            ArrayList entities = new ArrayList();
            boolean valid = false;
            Iterator var4 = this.level.getPlayers().values().iterator();

            while(var4.hasNext()) {
                  Player player = (Player)var4.next();
                  Entity[] var6 = this.level.getEntities();
                  int z = var6.length;

                  for(int var8 = 0; var8 < z; ++var8) {
                        Entity entity = var6[var8];
                        if (player.distance(entity) <= 25.0D) {
                              valid = true;
                              entities.add(entity);
                        }
                  }

                  if (!valid || entities.size() > 20) {
                        return;
                  }

                  int x = NukkitMath.floorDouble(player.x + (double)this.random.nextRange(-20, 20));
                  z = NukkitMath.floorDouble(player.z + (double)this.random.nextRange(-20, 20));
                  Position pos = new Position((double)x, (double)(this.level.getHighestBlockAt(x, z) + 1), (double)z, this.level);
                  byte type = 32;
                  int biome;
                  if ((biome = this.level.getBiomeId(x, z)) < 0) {
                        biome = 1;
                  }

                  int probability = this.random.nextRange(1, 100);
                  if (biome != 1 && biome != 4 && biome != 27 && biome != 3 && biome != 20) {
                        if (biome == 2) {
                              if (probability <= 10) {
                                    type = 38;
                              } else if (probability <= 25) {
                                    type = 35;
                              } else if (probability <= 55) {
                                    type = 47;
                              } else if (probability <= 80) {
                                    type = 34;
                              } else if (probability <= 95) {
                                    type = 33;
                              }
                        } else if (biome == 6) {
                              if (probability <= 10) {
                                    type = 38;
                              } else if (probability <= 25) {
                                    type = 35;
                              } else if (probability <= 30) {
                                    type = 44;
                              } else if (probability <= 50) {
                                    type = 32;
                              } else if (probability <= 70) {
                                    type = 34;
                              } else if (probability <= 85) {
                                    type = 33;
                              } else if (probability <= 95) {
                              }
                        } else if (biome != 5 && biome != 12) {
                              if (biome != 7 && biome != 0 && biome == 8) {
                                    if (probability <= 75) {
                                          type = 36;
                                    } else if (probability <= 85) {
                                          type = 41;
                                    } else if (probability <= 90) {
                                          type = 43;
                                    }
                              }
                        } else if (probability <= 10) {
                              type = 38;
                        } else if (probability <= 25) {
                              type = 35;
                        } else if (probability <= 30) {
                              type = 44;
                        } else if (probability <= 55) {
                              type = 32;
                        } else if (probability <= 80) {
                              type = 46;
                        } else if (probability <= 95) {
                              type = 33;
                        }
                  } else if (probability <= 10) {
                        type = 38;
                  } else if (probability <= 25) {
                        type = 35;
                  } else if (probability <= 30) {
                        type = 44;
                  } else if (probability <= 55) {
                        type = 32;
                  } else if (probability <= 80) {
                        type = 34;
                  } else if (probability <= 95) {
                        type = 33;
                  }

                  int time = this.level.getTime() % 24000;
                  if (player.distance(pos) > 8.0D && time >= 10900 && time <= 17800) {
                        CompoundTag nbt = (new CompoundTag()).putList((new ListTag("Pos")).add(new DoubleTag("", pos.x)).add(new DoubleTag("", pos.y)).add(new DoubleTag("", pos.z))).putList((new ListTag("Motion")).add(new DoubleTag("", 0.0D)).add(new DoubleTag("", 0.0D)).add(new DoubleTag("", 0.0D))).putList((new ListTag("Rotation")).add(new FloatTag("", 0.0F)).add(new FloatTag("", 0.0F)));
                        Entity.createEntity(type, this.level.getChunk(pos.getFloorX(), pos.getFloorZ()), nbt, new Object[0]);
                  }
            }

      }
}
