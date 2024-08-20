package ultraHardcore.task;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import java.util.HashSet;
import java.util.Set;

public class AnimalSpawnTask {
      public static HashSet creatures = new HashSet() {
            {
                  this.add(0);
            }
      };
      private final Set eligibleChunksForSpawning = new HashSet();

      private static Vector3 getRandomChunkPosition(Level worldIn, int x, int z) {
            FullChunk chunk = worldIn.getChunk(x, z);
            int i = x * 16 + worldIn.rand.nextInt(16);
            int j = z * 16 + worldIn.rand.nextInt(16);
            int k = roundUp(chunk.getHeightMap(i, j) + 1, 16);
            int l = worldIn.rand.nextInt(k > 0 ? k : chunk.getHighestBlockAt(i, j) + 16 - 1);
            return new Vector3((double)i, (double)l, (double)j);
      }

      public static int roundUp(int number, int interval) {
            if (interval == 0) {
                  return 0;
            } else if (number == 0) {
                  return interval;
            } else {
                  if (number < 0) {
                        interval *= -1;
                  }

                  int i = number % interval;
                  return i == 0 ? number : number + interval - i;
            }
      }
}
