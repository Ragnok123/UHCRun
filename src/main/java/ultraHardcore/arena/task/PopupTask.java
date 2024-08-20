package ultraHardcore.arena.task;

import cn.nukkit.Player;
import cn.nukkit.level.particle.DustParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import java.util.Iterator;
import ultraHardcore.arena.Arena;
import ultraHardcore.arena.object.ArenaPlayerData;
import ultraHardcore.arena.object.Border;
import ultraHardcore.language.Language;
import ultraHardcore.util.MutableVector2;

public class PopupTask implements Runnable {
      private final Arena plugin;

      public PopupTask(Arena plugin) {
            this.plugin = plugin;
      }

      public void run() {
            if (this.plugin.phase == 1) {
                  Iterator var1 = this.plugin.playersData.values().iterator();

                  while(var1.hasNext()) {
                        ArenaPlayerData data = (ArenaPlayerData)var1.next();
                        Player p = data.getPlayerData().getPlayer();
                        if (this.plugin.border.isVectorInside(p)) {
                              double distance = this.plugin.border.distance(p);
                              if (distance <= 30.0D) {
                                    p.sendPopup(Language.translate("border_near", data.getPlayerData().getData(), "" + Math.round(distance)));
                              }
                        }
                  }
            }

      }

      private void generateParticles(Player p) {
            Border border = this.plugin.border;
            double diffX = Math.min(Math.abs(p.x - border.minX), Math.abs(p.x - border.maxX));
            double diffZ = Math.min(Math.abs(p.z - border.minZ), Math.abs(p.z - border.maxZ));
            int minY = p.getFloorY() - 2;
            int maxY = p.getFloorY() + 5;
            MutableVector2 v1 = new MutableVector2(p.x, p.z);
            MutableVector2 v2 = new MutableVector2();
            Particle particle = new DustParticle(new Vector3(), 255, 0, 0);
            double z;
            int[] coords;
            int minX;
            int maxX;
            int y;
            double x;
            if (diffX < 10.0D) {
                  z = Math.abs(p.x - border.minX) < Math.abs(p.x - border.maxX) ? border.minX : border.maxX;
                  coords = this.getCoords(v1, v2.setComponents(z, 0.0D), 10.0D);
                  if (coords.length == 2) {
                        minX = Math.min(coords[0], coords[1]);
                        maxX = Math.max(coords[0], coords[1]);

                        for(y = minY; y <= maxY; ++y) {
                              for(x = (double)minX; x <= (double)maxX; x += 0.3D) {
                                    particle.setComponents(z, (double)y, x);
                                    this.plugin.level.addParticle(particle);
                              }
                        }
                  }
            }

            if (diffZ < 10.0D) {
                  z = Math.abs(p.z - border.minZ) < Math.abs(p.z - border.maxZ) ? border.minZ : border.maxZ;
                  coords = this.getCoords(v1, v2.setComponents(0.0D, z), 10.0D);
                  if (coords.length == 2) {
                        minX = Math.min(coords[0], coords[1]);
                        maxX = Math.max(coords[0], coords[1]);

                        for(y = minY; y <= maxY; ++y) {
                              for(x = (double)minX; x <= (double)maxX; x += 0.3D) {
                                    particle.setComponents(x, (double)y, z);
                                    this.plugin.level.addParticle(particle);
                              }
                        }
                  }
            }

      }

      public int[] getCoords(Vector2 v, Vector2 v1, double dist) {
            try {
                  double j = dist - Math.pow(v.y - v1.y, 2.0D) + Math.pow(v.x, 2.0D);
                  int x = NukkitMath.floorDouble(v.x + Math.sqrt(Math.pow(v.x, 2.0D) + j));
                  int x1 = NukkitMath.floorDouble(v.x - Math.sqrt(Math.pow(v.x, 2.0D) + j));
                  return new int[]{x, x1};
            } catch (Exception var9) {
                  var9.printStackTrace();
                  return new int[0];
            }
      }
}
