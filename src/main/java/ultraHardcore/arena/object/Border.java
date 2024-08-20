package ultraHardcore.arena.object;

import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import ultraHardcore.arena.Arena;

public class Border extends AxisAlignedBB {
      private Arena plugin;
      private Vector3 v1 = new Vector3();

      public Border(Arena plugin, Vector3 spawn) {
            super(spawn.x - 400.0D, -200.0D, spawn.z - 400.0D, spawn.x + 400.0D, 270.0D, spawn.z + 400.0D);
            this.plugin = plugin;
      }

      public void decrease() {
            this.decrease(1);
      }

      public void decrease(int amount) {
            if (this.checkSize()) {
                  this.contract((double)amount, 0.0D, (double)amount);
            }
      }

      private boolean checkSize() {
            return this.maxX - this.minX > 100.0D && this.maxZ - this.minZ > 100.0D;
      }

      public double distance(Vector3 pos) {
            double diffMinX = Math.abs(pos.x - this.minX);
            double diffMaxX = Math.abs(pos.x - this.maxX);
            double diffMinZ = Math.abs(pos.z - this.minZ);
            double diffMaxZ = Math.abs(pos.z - this.maxZ);
            return Math.min(Math.min(diffMinX, diffMaxX), Math.min(diffMinZ, diffMaxZ));
      }
}
