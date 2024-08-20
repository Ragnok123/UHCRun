package ultraHardcore.command;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import ultraHardcore.UltraHardcore;
import ultraHardcore.arena.Arena;

public class StartCommand extends BaseCommand {
      public StartCommand(UltraHardcore plugin) {
            super("start", plugin);
      }

      public boolean execute(CommandSender sender, String s, String[] args) {
            if (sender.isOp() && sender instanceof Player) {
                  Arena arena = this.plugin.getPlayerArena((Player)sender);
                  if (arena != null && arena.isMapLoaded) {
                        arena.start();
                        return true;
                  } else {
                        return true;
                  }
            } else {
                  return true;
            }
      }
}
