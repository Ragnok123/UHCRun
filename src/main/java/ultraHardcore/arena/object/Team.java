package ultraHardcore.arena.object;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import ultraHardcore.util.TeamColor;

public class Team {
      public final HashMap players = new HashMap();
      private int id;
      private TeamColor color;
      private Player owner;

      public Team(Player owner) {
            this.owner = owner;
            this.players.put(owner.getName().toLowerCase(), owner);
      }

      public void message(String message, Player player, ArenaPlayerData data) {
            if (player == null) {
                  Iterator var8 = this.getPlayers().entrySet().iterator();

                  while(var8.hasNext()) {
                        Entry entry = (Entry)var8.next();
                        ((Player)entry.getValue()).sendMessage(message);
                  }

            } else {
                  String msg = TextFormat.GRAY + "[" + TextFormat.DARK_PURPLE + "Lobby" + TextFormat.GRAY + "] " + player.getDisplayName() + TextFormat.DARK_AQUA + " > " + data.getPlayerData().getData().getChatColor() + message;
                  Iterator var5 = this.getPlayers().values().iterator();

                  while(var5.hasNext()) {
                        Player p = (Player)var5.next();
                        p.sendMessage(msg);
                  }

            }
      }

      public HashMap getPlayers() {
            return this.players;
      }

      public int getId() {
            return this.id;
      }

      public void setId(int id) {
            this.id = id;
      }

      public TeamColor getColor() {
            return this.color;
      }

      public void setColor(TeamColor color) {
            this.color = color;
      }

      public Player getOwner() {
            return this.owner;
      }

      public void setOwner(Player owner) {
            this.owner = owner;
      }
}
