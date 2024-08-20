package ultraHardcore.arena.object;

import java.util.HashMap;
import java.util.Map;
import ultraHardcore.arena.Arena;
import ultraHardcore.arena.kit.Kit;
import ultraHardcore.stats.Stats;

public class ArenaPlayerData {
      private Team team = null;
      public Kit kit;
      private final PlayerData playerData;
      private final Arena arena;
      public Map requests;
      public int currentCompassIndex;

      public ArenaPlayerData(Arena arena, PlayerData playerData) {
            this.kit = Kit.BEGINNER;
            this.requests = new HashMap();
            this.currentCompassIndex = 0;
            this.arena = arena;
            this.playerData = playerData;
            this.team = new Team(playerData.getPlayer());
      }

      public void addStat(Stats.Stat stat) {
            this.playerData.stats.add(stat);
            this.playerData.getData().addMoney(stat.getTokens());
            this.playerData.getData().addExp(stat.getXp());
      }

      public Team getTeam() {
            return this.team;
      }

      public void setTeam(Team team) {
            this.team = team;
      }

      public Kit getKit() {
            return this.kit;
      }

      public PlayerData getPlayerData() {
            return this.playerData;
      }

      public Arena getArena() {
            return this.arena;
      }
}
