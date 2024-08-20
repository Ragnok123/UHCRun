package ultraHardcore.command;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import ultraHardcore.UltraHardcore;

public class TeamCommand extends BaseCommand {
      public TeamCommand(UltraHardcore plugin) {
            super("point", plugin);
            this.commandParameters.clear();
            this.commandParameters.put("invite", new CommandParameter[]{new CommandParameter("action", false, new String[]{"invite", "pozvat", "accept", "prijmout"}), new CommandParameter("player", "target", false)});
      }

      public boolean execute(CommandSender sender, String label, String[] args) {
            return true;
      }
}
