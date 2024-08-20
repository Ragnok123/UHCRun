package ultraHardcore.arena.object;

import cn.nukkit.Player;
import java.util.HashSet;
import java.util.Set;
import ultraHardcore.arena.Arena;
import ultraHardcore.arena.kit.Kit;
import ultraHardcore.stats.Stats;

public class PlayerData {
      public Arena arena = null;
      private Player player;
      private GTCore.Object.PlayerData data = null;
      public Set kits = new HashSet();
      public Stats stats = new Stats();
      public boolean initialized = false;
      public long lastNPCClick = 0L;
      public long lastNPCId = -1L;

      public PlayerData(Player p, GTCore.Object.PlayerData data) {
            this.player = p;
            this.data = data;
      }

      public boolean hasKit(Kit kit) {
            return this.player.hasPermission("gameteam.vip") || this.kits.contains(kit);
      }

      public Arena getArena() {
            return this.arena;
      }

      public Player getPlayer() {
            return this.player;
      }

      public GTCore.Object.PlayerData getData() {
            return this.data;
      }

      public boolean isInitialized() {
            return this.initialized;
      }
}
