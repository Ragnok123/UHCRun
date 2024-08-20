package ultraHardcore.command;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import ultraHardcore.UltraHardcore;

public abstract class BaseCommand extends Command {
      protected UltraHardcore plugin;
      protected Server server;

      public BaseCommand(String name, UltraHardcore plugin) {
            super(name);
            this.description = "GT command";
            this.usageMessage = "";
            this.plugin = plugin;
      }

      protected UltraHardcore getPlugin() {
            return this.plugin;
      }
}
