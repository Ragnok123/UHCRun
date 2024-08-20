package ultraHardcore.command;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import ultraHardcore.UltraHardcore;
import ultraHardcore.arena.object.PlayerData;
import ultraHardcore.language.Language;
import ultraHardcore.stats.Stats;

public class StatsCommand extends BaseCommand {
      public StatsCommand(UltraHardcore plugin) {
            super("stats", plugin);
            this.commandParameters.clear();
      }

      public boolean execute(CommandSender commandSender, String s, String[] strings) {
            if (!(commandSender instanceof Player)) {
                  return true;
            } else {
                  PlayerData data = this.getPlugin().getPlayerData((Player)commandSender);
                  Stats stats = data.stats;
                  commandSender.sendMessage(Language.translate("stats", data.getData(), String.valueOf(stats.get(Stats.Stat.KILL)), String.valueOf(stats.get(Stats.Stat.DEATH)), String.valueOf(stats.get(Stats.Stat.WIN))));
                  return true;
            }
      }
}
