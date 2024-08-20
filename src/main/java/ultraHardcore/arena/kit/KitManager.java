package ultraHardcore.arena.kit;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
import java.util.HashMap;
import java.util.Iterator;
import ultraHardcore.arena.object.ArenaPlayerData;
import ultraHardcore.arena.object.PlayerData;
import ultraHardcore.language.Language;

public class KitManager {
      public HashMap linkageData = new HashMap();

      public LinkageData getLinkageData(Vector3 pos) {
            Iterator var2 = this.linkageData.values().iterator();

            LinkageData data;
            do {
                  if (!var2.hasNext()) {
                        return null;
                  }

                  data = (LinkageData)var2.next();
            } while(!data.firstPosition.equals(pos) && !data.secondPosition.equals(pos));

            return data;
      }

      public void addKitWindow(Player p) {
            p.getInventory().clearAll();

            int i;
            for(i = 0; i < p.getInventory().getHotbarSize(); ++i) {
                  p.getInventory().setHotbarSlotIndex(i, 35);
            }

            for(i = 0; i < Kit.values().length; ++i) {
                  p.getInventory().setItem(i, Kit.values()[i].getItem());
            }

            p.getInventory().sendContents(p);
            p.getInventory().setHeldItemIndex(35);
      }

      public void selectKit(Player p, ArenaPlayerData data, Kit kit) {
            if (!p.hasPermission("gameteam.vip") && !data.getPlayerData().kits.contains(kit.getName())) {
                  p.sendMessage(Language.translate("has_not_kit", data.getPlayerData().getData()));
            } else {
                  data.kit = kit;
                  p.sendMessage(Language.translate("kit_select", data.getPlayerData().getData(), kit.getName()));
            }
      }

      public void buyKit(Player p, PlayerData data, Kit kit) {
            if (data.kits.contains(kit.getName())) {
                  p.sendMessage(Language.translate("kit_already", data.getData()));
            } else if (kit.getCost() > data.getData().getMoney()) {
                  p.sendMessage(Language.translate("low_money", data.getData()));
            } else {
                  data.kits.add(kit);
            }
      }
}
