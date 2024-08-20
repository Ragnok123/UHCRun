package ultraHardcore.arena.kit;

import cn.nukkit.Player;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockLavaStill;
import cn.nukkit.block.BlockMobSpawner;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemAxeWood;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemBootsLeather;
import cn.nukkit.item.ItemChestplateLeather;
import cn.nukkit.item.ItemFeather;
import cn.nukkit.item.ItemGunpowder;
import cn.nukkit.item.ItemHelmetLeather;
import cn.nukkit.item.ItemLeggingsLeather;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.item.ItemSteak;
import cn.nukkit.item.ItemSwordWood;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public enum Kit {
      CREEPER(new ItemGunpowder(), "kit_creeper", 0, "https://image.ibb.co/nAgcHm/image.png"),
      LINKAGE(new ItemBlock(new BlockMobSpawner()), "kit_linkage", 0, "https://image.ibb.co/eKcCiR/image.png", new HashMap() {
            {
                  this.put(0, new ItemBlock(new BlockMobSpawner(), 0, 2));
            }
      }),
      GHOST(new ItemPotion(7), "kit_ghost", 0, "https://image.ibb.co/b4BzV6/image.png"),
      NINJA(new ItemFeather(), "kit_ninja", 0, "https://image.ibb.co/ePUWcm/image.png"),
      BEGINNER(new ItemBlock(new BlockDirt()), "kit_beginner", 0, "https://thumb.ibb.co/iWZa3R/image.png"),
      FLOWER(Item.get(37), "kit_flower", 0, "https://image.ibb.co/moX6A6/image.png"),
      MISER(new ItemBlock(new BlockChest()), "kit_miser", 0, "https://image.ibb.co/cfEQ3R/image.png"),
      SALAMANDER(new ItemBlock(new BlockLavaStill()), "kit_salamander", 0, "https://image.ibb.co/nhPWcm/image.png"),
      BUTCHER(new ItemSteak(), "kit_butcher", 0, "https://image.ibb.co/b7mcHm/image.png");

      private final Item item;
      private final int cost;
      private final HashMap items;
      private final String message;
      private final String url;
      private static Item[] vipItems = new Item[]{new ItemHelmetLeather(), new ItemChestplateLeather(), new ItemLeggingsLeather(), new ItemBootsLeather(), new ItemAxeWood(), new ItemSwordWood(), new ItemSteak(0, 3)};

      private Kit(Item item, String message, int cost, String url) {
            this(item, message, cost, url, new HashMap());
      }

      private Kit(Item item, String message, int cost, String url, HashMap items) {
            this.item = item;
            this.message = message;
            this.items = items;
            this.cost = cost;
            this.url = url;
      }

      public String getName() {
            return this.name().toLowerCase();
      }

      public void giveItems(Player p) {
            PlayerInventory inv = p.getInventory();
            Iterator var3 = this.items.entrySet().iterator();

            while(var3.hasNext()) {
                  Entry entry = (Entry)var3.next();
                  inv.setItem((Integer)entry.getKey(), (Item)entry.getValue());
            }

            inv.sendContents(p);
            if (p.hasPermission("gameteam.vip")) {
                  inv.addItem(vipItems);
            }

      }

      public String getImageUrl() {
            return this.url;
      }

      public Item getItem() {
            return this.item;
      }

      public int getCost() {
            return this.cost;
      }

      public HashMap getItems() {
            return this.items;
      }

      public String getMessage() {
            return this.message;
      }
}
