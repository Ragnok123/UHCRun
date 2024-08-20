package ultraHardcore.util;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemApple;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TreeCapitator {
      private static int currentLeaves = 0;
      private static Block currentBlock = null;
      private static int woodCount = 1;
      private static int leavesCount = 0;
      private static Vector3 baseBlock = null;
      private static Vector3 temporalVector = new Vector3();
      private static Set removedBlocks = new HashSet();

      public static Item[] breakTree(Block first) {
            baseBlock = first.getLocation().clone();
            baseBlock.y = 0.0D;
            woodCount = 1;
            leavesCount = 0;
            if (first.getId() == 17) {
                  currentLeaves = 18;
            } else {
                  currentLeaves = 161;
            }

            currentBlock = first;
            first.getLevel().setBlock(first, new BlockAir(), false, false);
            BlockFace[] var1 = BlockFace.values();
            int rnd = var1.length;

            int appleCount;
            for(appleCount = 0; appleCount < rnd; ++appleCount) {
                  BlockFace side = var1[appleCount];
                  Block b = first.getSide(side);
                  if (equals(b)) {
                        breakBlock(b, side.getOpposite());
                  }
            }

            ArrayList drops = new ArrayList();
            drops.add(Item.get(first.getId(), first.getDamage(), woodCount));
            rnd = (new NukkitRandom()).nextRange(1, 100);
            appleCount = rnd > 95 ? 2 : (rnd > 70 ? 1 : 0);
            if (appleCount > 0) {
                  drops.add(new ItemApple(0, appleCount));
            }

            return (Item[])drops.stream().toArray((x$0) -> {
                  return new Item[x$0];
            });
      }

      private static void breakBlock(Block b, BlockFace fromSide) {
            b.getLevel().setBlock(b, new BlockAir(), false, false);
            temporalVector.setComponents(b.x, 0.0D, b.z);
            BlockFace[] var2 = BlockFace.values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                  BlockFace side = var2[var4];
                  Block b2 = b.getSide(side);
                  if (equals(b2)) {
                        if (b2.getId() == currentLeaves) {
                              ++leavesCount;
                        } else {
                              ++woodCount;
                        }

                        breakBlock(b2, side.getOpposite());
                  }
            }

      }

      private static boolean equals(Block block) {
            int id = block.getId();
            if (block.getDamage() != currentBlock.getDamage()) {
                  return false;
            } else {
                  return currentBlock.getId() == id || id == currentLeaves;
            }
      }
}
