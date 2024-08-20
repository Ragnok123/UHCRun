package ultraHardcore.mysql;

import GTCore.Mysql.AsyncQuery;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.MainLogger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;
import ultraHardcore.UltraHardcore;
import ultraHardcore.arena.kit.Kit;
import ultraHardcore.arena.object.PlayerData;

public class JoinQuery extends AsyncQuery {
      private HashMap data = new HashMap();

      public JoinQuery(Player p) {
            this.player = p.getName();
            this.table = "uhc";
            Server.getInstance().getScheduler().scheduleAsyncTask(this);
      }

      public void onQuery(HashMap data) {
            if (data != null && !data.isEmpty()) {
                  this.data = data;
            } else {
                  this.data = this.registerPlayer(this.player);
            }

      }

      public void onCompletion(Server server) {
            Player p = server.getPlayerExact(this.player);
            if (p != null && p.isOnline()) {
                  UltraHardcore plugin = UltraHardcore.getInstance();
                  HashMap taskData = this.data;
                  PlayerData data = plugin.getPlayerData(p);
                  data.stats.init(taskData);
                  Set kits = data.kits;
                  String kity = (String)taskData.get("kits");
                  kits.clear();
                  String[] var8 = kity.split(",");
                  int var9 = var8.length;

                  for(int var10 = 0; var10 < var9; ++var10) {
                        String k = var8[var10];

                        try {
                              Kit kit = Kit.valueOf(k.toUpperCase());
                              kits.add(kit);
                        } catch (Exception var13) {
                              MainLogger.getLogger().logException(var13);
                        }
                  }

                  data.initialized = true;
                  p.setGamemode(0);
            }
      }

      private HashMap registerPlayer(String player) {
            String name = player.toLowerCase().trim();
            HashMap data = new HashMap();
            data.put("name", name);
            data.put("kills", 0);
            data.put("deaths", 0);
            data.put("wins", 0);
            data.put("losses", 0);
            data.put("kits", "beginner");

            try {
                  PreparedStatement e = this.getMysqli().prepareStatement("INSERT INTO uhc ( name, kills, deaths, wins, kits) VALUES ('" + name + "', '" + 0 + "', ' " + 0 + "', ' " + 0 + "', 'beginner')");
                  e.executeUpdate();
            } catch (SQLException var5) {
                  var5.printStackTrace();
            }

            return data;
      }
}
