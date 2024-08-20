package ultraHardcore.arena.object;

import cn.nukkit.Player;

public class TeamRequest {
      public final Player from;
      public final Player to;

      public TeamRequest(Player from, Player to) {
            this.from = from;
            this.to = to;
      }
}
