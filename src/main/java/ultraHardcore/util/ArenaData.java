package ultraHardcore.util;

import cn.nukkit.math.Vector3;

public class ArenaData {
      public Vector3 sign = null;
      public Vector3 lobby = null;
      public String port = null;
      public int teamSize = 1;

      public ArenaData() {
      }

      public ArenaData(Vector3 sign, Vector3 lobby) {
            this.sign = sign;
            this.lobby = lobby;
      }
}
