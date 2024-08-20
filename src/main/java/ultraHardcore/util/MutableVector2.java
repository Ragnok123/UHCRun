package ultraHardcore.util;

import cn.nukkit.math.Vector2;

public class MutableVector2 extends Vector2 {
      public double x;
      public double y;

      public MutableVector2() {
            super(0.0D, 0.0D);
      }

      public MutableVector2(double x, double z) {
            super(x, z);
      }

      public MutableVector2 setComponents(double x, double z) {
            this.x = x;
            this.y = z;
            return this;
      }
}
