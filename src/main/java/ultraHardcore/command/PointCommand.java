package ultraHardcore.command;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.network.protocol.SetSpawnPositionPacket;
import cn.nukkit.utils.TextFormat;
import java.util.Iterator;
import ultraHardcore.UltraHardcore;
import ultraHardcore.arena.Arena;
import ultraHardcore.arena.object.ArenaPlayerData;
import ultraHardcore.arena.object.Team;

public class PointCommand extends BaseCommand {
      public PointCommand(UltraHardcore plugin) {
            super("point", plugin);
            this.commandParameters.clear();
      }

      public boolean execute(CommandSender sender, String s, String[] args) {
            if (!(sender instanceof Player)) {
                  return true;
            } else {
                  Player player = (Player)sender;
                  Arena arena = this.plugin.getPlayerArena(player);
                  if (arena == null) {
                        return true;
                  } else {
                        if (arena.phase == 1) {
                              Player p = null;
                              ArenaPlayerData data0 = arena.getPlayerData(player);
                              Team team = data0.getTeam();
                              double distance = 2.147483647E9D;
                              Iterator var11 = arena.playersData.values().iterator();

                              while(var11.hasNext()) {
                                    ArenaPlayerData data = (ArenaPlayerData)var11.next();
                                    Team t = data.getTeam();
                                    Player pl = data.getPlayerData().getPlayer();
                                    if (pl.distance(player) < distance && t != null && t.getPlayers().size() > 0 && t.getId() != team.getId()) {
                                          p = pl;
                                          break;
                                    }
                              }

                              if (p != null) {
                                    SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
                                    pk.x = p.getFloorX();
                                    pk.y = p.getFloorY();
                                    pk.z = p.getFloorZ();
                                    pk.spawnType = 1;
                                    player.dataPacket(pk);
                                    player.sendActionBar(TextFormat.YELLOW + p.getName(), 5, 40, 5);
                              }
                        }

                        return true;
                  }
            }
      }
}
