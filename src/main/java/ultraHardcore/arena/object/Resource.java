package ultraHardcore.arena.object;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBook;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemIngotGold;
import cn.nukkit.item.ItemIngotIron;
import cn.nukkit.item.ItemNetherWart;
import cn.nukkit.item.ItemPumpkinPie;
import cn.nukkit.item.ItemRabbitFoot;
import cn.nukkit.item.ItemRedstone;
import cn.nukkit.item.ItemWheat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class Resource {
      public static Map resources = new HashMap();
      private final int blockId;
      private final Consumer dropsConsumer;

      private Resource(int blockId, Consumer drops) {
            this.blockId = blockId;
            this.dropsConsumer = drops;
      }

      public static Item[] getDrops(Block block, Item item) {
            Resource resource = (Resource)resources.get(block.getId());
            if (resource == null) {
                  return block.getDrops(item);
            } else {
                  ArrayList drops = new ArrayList();
                  resource.dropsConsumer.accept(drops);
                  return (Item[])drops.stream().toArray((x$0) -> {
                        return new Item[x$0];
                  });
            }
      }

      public int getBlockId() {
            return this.blockId;
      }

      static {
            resources.put(15, new Resource(15, (items) -> {
                  items.add(new ItemIngotIron());
            }));
            resources.put(14, new Resource(14, (items) -> {
                  items.add(new ItemIngotGold());
            }));
            resources.put(16, new Resource(16, (items) -> {
                  items.add(Item.get(50, 0, 4));
            }));
            resources.put(31, new Resource(31, (items) -> {
                  if ((new Random()).nextInt(3) == 0) {
                        items.add(new ItemWheat(0, 1));
                  }

            }));
            resources.put(15, new Resource(15, (items) -> {
                  items.add(new ItemIngotIron());
            }));
            resources.put(73, new Resource(73, (items) -> {
                  items.add(new ItemRedstone(0, (new Random()).nextInt(4) + 2));
                  if ((new Random()).nextInt(2) == 0) {
                        items.add(new ItemNetherWart());
                  } else if ((new Random()).nextBoolean()) {
                        items.add(new ItemRabbitFoot());
                  }

            }));
            resources.put(21, new Resource(21, (items) -> {
                  items.add(new ItemDye(4, (new Random()).nextInt(4) + 2));
                  if ((new Random()).nextBoolean()) {
                        items.add(new ItemBook(0, (new Random()).nextInt(5) + 2));
                  }

            }));
            resources.put(86, new Resource(86, (items) -> {
                  items.add(new ItemPumpkinPie());
            }));
      }
}
