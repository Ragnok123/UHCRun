package ultraHardcore.mysql;

import GTCore.MTCore;
import GTCore.Mysql.AsyncQuery;
import cn.nukkit.Server;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ultraHardcore.arena.kit.Kit;
import ultraHardcore.arena.object.PlayerData;
import ultraHardcore.stats.Stats;

public class QuitQuery extends AsyncQuery {
      private final PlayerData[] datas;

      public QuitQuery(PlayerData... datas) {
            this.datas = datas;
            if (!MTCore.isShuttingDown) {
                  Server.getInstance().getScheduler().scheduleAsyncTask(this);
            } else {
                  this.onRun();
            }

      }

      public void onRun() {
            PlayerData[] var1 = this.datas;
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                  PlayerData data = var1[var3];
                  this.player = data.getPlayer().getName();
                  Stats stats = data.stats;
                  List kitList = new ArrayList();
                  Iterator var7 = data.kits.iterator();

                  while(var7.hasNext()) {
                        Kit kit = (Kit)var7.next();
                        kitList.add(kit.getName());
                  }

                  String kits = String.join(",", kitList);

                  try {
                        PreparedStatement statement = this.getMysqli().prepareStatement("UPDATE uhc SET kills = kills + '" + stats.getDelta(Stats.Stat.KILL) + "', deaths = deaths + '" + stats.getDelta(Stats.Stat.DEATH) + "', wins = wins + '" + stats.getDelta(Stats.Stat.WIN) + "', kits = '" + kits + "' WHERE name = '" + this.player.trim().toLowerCase() + "'");
                        statement.executeUpdate();
                  } catch (SQLException var9) {
                        var9.printStackTrace();
                  }
            }

      }
}
