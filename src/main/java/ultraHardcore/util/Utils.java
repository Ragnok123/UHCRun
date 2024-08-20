package ultraHardcore.util;

import cn.nukkit.block.Block;
import cn.nukkit.inventory.Recipe;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBook;
import cn.nukkit.item.ItemIngotGold;
import cn.nukkit.item.ItemIngotIron;
import cn.nukkit.item.ItemNetherWart;
import cn.nukkit.item.ItemPickaxeDiamond;
import cn.nukkit.item.ItemPickaxeIron;
import cn.nukkit.item.ItemPickaxeStone;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.ItemWheat;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.NukkitRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
      public static Item[] getDrops(Block block, Item item) {
            List drops = new ArrayList();
            switch(block.getId()) {
            case 14:
                  drops.add(new ItemIngotGold());
                  break;
            case 15:
                  drops.add(new ItemIngotIron());
                  break;
            case 16:
                  drops.add(Item.get(50, 0, 4));
                  break;
            case 21:
                  drops.add(new ItemBook(0, (new NukkitRandom()).nextRange(2, 4)));
                  break;
            case 31:
                  if ((new NukkitRandom()).nextBoundedInt(5) < 4) {
                        drops.add(new ItemWheat());
                  }
                  break;
            case 73:
            case 74:
                  drops.add(new ItemNetherWart(0, (new NukkitRandom()).nextRange(0, 3)));
                  break;
            default:
                  drops.addAll(Arrays.asList(block.getDrops(item)));
            }

            return (Item[])drops.stream().toArray((x$0) -> {
                  return new Item[x$0];
            });
      }

      public static Item getCraftDrops(Recipe recipe) {
            Item item = recipe.getResult();
            Enchantment eff;
            switch(((Item)item).getId()) {
            case 257:
                  item = new ItemPickaxeIron();
                  eff = Enchantment.get(15);
                  eff.setLevel(3);
                  ((Item)item).addEnchantment(new Enchantment[]{eff});
                  break;
            case 270:
            case 274:
                  item = new ItemPickaxeStone();
                  eff = Enchantment.get(15);
                  eff.setLevel(2);
                  ((Item)item).addEnchantment(new Enchantment[]{eff});
                  break;
            case 278:
                  item = new ItemPickaxeDiamond();
                  eff = Enchantment.get(15);
                  eff.setLevel(3);
                  ((Item)item).addEnchantment(new Enchantment[]{eff});
            }

            if (item instanceof ItemTool) {
                  ((Item)item).getNamedTag().putByte("Unbreakable", 1);
            }

            return (Item)item;
      }

      public static Item[] getEntityDrops(Item[] items) {
            List drops = new ArrayList();
            Item[] var2 = items;
            int var3 = items.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                  Item item = var2[var4];
                  int id = item.getId();
                  switch(id) {
                  case 319:
                        id = 320;
                        break;
                  case 363:
                        id = 364;
                        break;
                  case 365:
                        id = 366;
                        break;
                  case 411:
                        id = 412;
                        break;
                  case 423:
                        id = 424;
                  }

                  drops.add(Item.get(id, item.getDamage(), item.getCount()));
            }

            return (Item[])drops.stream().toArray((x$0) -> {
                  return new Item[x$0];
            });
      }
}
