package ultraHardcore.stats;

import java.util.EnumMap;
import java.util.Map;

public class Stats {
      private final EnumMap stats = new EnumMap(Stats.Stat.class);
      private final EnumMap statsOriginal = new EnumMap(Stats.Stat.class);

      public void init(Map data) {
            this.statsOriginal.put(Stats.Stat.KILL, (Integer)data.get("kills"));
            this.statsOriginal.put(Stats.Stat.DEATH, (Integer)data.get("deaths"));
            this.statsOriginal.put(Stats.Stat.WIN, (Integer)data.get("wins"));
            Stats.Stat[] var2 = Stats.Stat.values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                  Stats.Stat stat = var2[var4];
                  this.stats.put(stat, 0);
            }

      }

      public void add(Stats.Stat stat) {
            this.add(stat, 1);
      }

      public void add(Stats.Stat stat, int value) {
            this.stats.put(stat, value);
      }

      public int get(Stats.Stat stat) {
            return (Integer)this.stats.get(stat) + (Integer)this.statsOriginal.get(stat);
      }

      public int getDelta(Stats.Stat stat) {
            return (Integer)this.stats.get(stat);
      }

      public static enum Stat {
            KILL(100, 5),
            DEATH(0, 0),
            WIN(1000, 50);

            private final int xp;
            private final int tokens;

            private Stat(int xp, int tokens) {
                  this.xp = xp;
                  this.tokens = tokens;
            }

            public int getXp() {
                  return this.xp;
            }

            public int getTokens() {
                  return this.tokens;
            }
      }
}
