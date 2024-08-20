package ultraHardcore.entity.other;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockAir;
import cn.nukkit.event.entity.EntityArmorChangeEvent;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.network.protocol.InventoryContentPacket;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.MobArmorEquipmentPacket;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import java.util.Arrays;
import java.util.Collection;

public class HumanInventory extends BaseInventory {
      protected int itemInHandIndex = 0;
      protected int[] hotbar = new int[this.getHotbarSize()];

      public HumanInventory(KitEntity player) {
            super(player, InventoryType.PLAYER);
            Arrays.fill(this.hotbar, -1);
      }

      public int getSize() {
            return super.getSize() - 4 - 9;
      }

      public void setSize(int size) {
            super.setSize(size + 4 + 9);
            this.sendContents((Collection)this.getViewers());
      }

      public int getHotbarSlotIndex(int index) {
            return index >= 0 && index < this.getHotbarSize() ? this.hotbar[index] : -1;
      }

      public void setHotbarSlotIndex(int index, int slot) {
            if (index >= 0 && index < this.getHotbarSize() && slot >= -1 && slot < this.getSize()) {
                  this.hotbar[index] = slot;
            }

      }

      public int getHeldItemIndex() {
            return this.itemInHandIndex;
      }

      public void setHeldItemIndex(int index) {
            if (index >= 0 && index < this.getHotbarSize()) {
                  this.itemInHandIndex = index;
                  this.sendHeldItem(this.getHolder().getViewers().values());
            }

      }

      public Item getItemInHand() {
            Item item = this.getItem(0);
            return (Item)(item != null ? item : new ItemBlock(new BlockAir(), 0, 0));
      }

      public boolean setItemInHand(Item item) {
            return this.setItem(0, item);
      }

      public int getHeldItemSlot() {
            return this.getHotbarSlotIndex(this.itemInHandIndex);
      }

      public void setHeldItemSlot(int slot) {
            if (slot >= -1 && slot < this.getSize()) {
                  this.getItem(slot);
                  int itemIndex = this.getHeldItemIndex();
                  this.setHotbarSlotIndex(itemIndex, slot);
            }

      }

      public void sendHeldItem(Player player) {
            Item item = this.getItemInHand();
            MobEquipmentPacket pk = new MobEquipmentPacket();
            pk.eid = player.equals(this.getHolder()) ? 0L : this.getHolder().getId();
            pk.item = item;
            pk.hotbarSlot = (byte)this.getHeldItemIndex();
            pk.inventorySlot = (byte)this.getHeldItemIndex();
            player.dataPacket(pk);
      }

      public void sendHeldItem(Player[] players) {
            Item item = this.getItemInHand();
            MobEquipmentPacket pk = new MobEquipmentPacket();
            pk.item = item;
            pk.hotbarSlot = (byte)this.getHeldItemSlot();
            pk.inventorySlot = (byte)this.getHeldItemIndex();
            Player[] var4 = players;
            int var5 = players.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                  Player player = var4[var6];
                  pk.eid = this.getHolder().getId();
                  player.dataPacket(pk);
            }

      }

      public void sendHeldItem(Collection players) {
            this.sendHeldItem((Player[])players.stream().toArray((x$0) -> {
                  return new Player[x$0];
            }));
      }

      public void onSlotChange(int index, Item before, boolean send) {
            KitEntity holder = this.getHolder();
            super.onSlotChange(index, before, send);
            if (index >= this.getSize()) {
                  this.sendArmorSlot(index, (Collection)this.getViewers());
                  this.sendArmorSlot(index, this.getHolder().getViewers().values());
            }

      }

      public int getHotbarSize() {
            return 9;
      }

      public Item getArmorItem(int index) {
            return this.getItem(this.getSize() + index);
      }

      public boolean setArmorItem(int index, Item item) {
            return this.setItem(this.getSize() + index, item);
      }

      public Item getHelmet() {
            return this.getItem(this.getSize());
      }

      public Item getChestplate() {
            return this.getItem(this.getSize() + 1);
      }

      public Item getLeggings() {
            return this.getItem(this.getSize() + 2);
      }

      public Item getBoots() {
            return this.getItem(this.getSize() + 3);
      }

      public boolean setHelmet(Item helmet) {
            return this.setItem(this.getSize(), helmet);
      }

      public boolean setChestplate(Item chestplate) {
            return this.setItem(this.getSize() + 1, chestplate);
      }

      public boolean setLeggings(Item leggings) {
            return this.setItem(this.getSize() + 2, leggings);
      }

      public boolean setBoots(Item boots) {
            return this.setItem(this.getSize() + 3, boots);
      }

      public boolean setItem(int index, Item item, boolean send) {
            if (index >= 0 && index < this.size) {
                  if (item.getId() != 0 && item.getCount() > 0) {
                        if (index >= this.getSize()) {
                              EntityArmorChangeEvent ev = new EntityArmorChangeEvent(this.getHolder(), this.getItem(index), item, index);
                              Server.getInstance().getPluginManager().callEvent(ev);
                              if (ev.isCancelled() && this.getHolder() != null) {
                                    this.sendArmorSlot(index, (Collection)this.getViewers());
                                    return false;
                              }

                              item = ev.getNewItem();
                        } else {
                              EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent(this.getHolder(), this.getItem(index), item, index);
                              Server.getInstance().getPluginManager().callEvent(ev);
                              if (ev.isCancelled()) {
                                    this.sendSlot(index, (Collection)this.getViewers());
                                    return false;
                              }

                              item = ev.getNewItem();
                        }

                        Item old = this.getItem(index);
                        this.slots.put(index, item.clone());
                        this.onSlotChange(index, old, send);
                        return true;
                  } else {
                        return this.clear(index);
                  }
            } else {
                  return false;
            }
      }

      public boolean clear(int index, boolean send) {
            if (this.slots.containsKey(index)) {
                  Item item = new ItemBlock(new BlockAir(), (Integer)null, 0);
                  Item old = (Item)this.slots.get(index);
                  Item item;
                  if (index >= this.getSize() && index < this.size) {
                        EntityArmorChangeEvent ev = new EntityArmorChangeEvent(this.getHolder(), old, item, index);
                        Server.getInstance().getPluginManager().callEvent(ev);
                        if (ev.isCancelled()) {
                              if (index >= this.size) {
                                    this.sendArmorSlot(index, (Collection)this.getViewers());
                              } else {
                                    this.sendSlot(index, (Collection)this.getViewers());
                              }

                              return false;
                        }

                        item = ev.getNewItem();
                  } else {
                        EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent(this.getHolder(), old, item, index);
                        Server.getInstance().getPluginManager().callEvent(ev);
                        if (ev.isCancelled()) {
                              if (index >= this.size) {
                                    this.sendArmorSlot(index, (Collection)this.getViewers());
                              } else {
                                    this.sendSlot(index, (Collection)this.getViewers());
                              }

                              return false;
                        }

                        item = ev.getNewItem();
                  }

                  if (item.getId() != 0) {
                        this.slots.put(index, item.clone());
                  } else {
                        this.slots.remove(index);
                  }

                  this.onSlotChange(index, old, send);
            }

            return true;
      }

      public Item[] getArmorContents() {
            Item[] armor = new Item[4];

            for(int i = 0; i < 4; ++i) {
                  armor[i] = this.getItem(this.getSize() + i);
            }

            return armor;
      }

      public void clearAll() {
            int limit = this.getSize() + 4;

            for(int index = 0; index < limit; ++index) {
                  this.clear(index);
            }

      }

      public void sendArmorContents(Player player) {
            this.sendArmorContents(new Player[]{player});
      }

      public void sendArmorContents(Player[] players) {
            Item[] armor = this.getArmorContents();
            MobArmorEquipmentPacket pk = new MobArmorEquipmentPacket();
            pk.eid = this.getHolder().getId();
            pk.slots = armor;
            pk.encode();
            pk.isEncoded = true;
            Player[] var4 = players;
            int var5 = players.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                  Player player = var4[var6];
                  player.dataPacket(pk);
            }

      }

      public void setArmorContents(Item[] items) {
            if (items.length < 4) {
                  Item[] newItems = new Item[4];
                  System.arraycopy(items, 0, newItems, 0, items.length);
                  items = newItems;
            }

            for(int i = 0; i < 4; ++i) {
                  if (items[i] == null) {
                        items[i] = new ItemBlock(new BlockAir(), (Integer)null, 0);
                  }

                  if (items[i].getId() == 0) {
                        this.clear(this.getSize() + i);
                  } else {
                        this.setItem(this.getSize() + 1, items[i]);
                  }
            }

      }

      public void sendArmorContents(Collection players) {
            this.sendArmorContents((Player[])players.stream().toArray((x$0) -> {
                  return new Player[x$0];
            }));
      }

      public void sendArmorSlot(int index, Player player) {
            this.sendArmorSlot(index, new Player[]{player});
      }

      public void sendArmorSlot(int index, Player[] players) {
            Item[] armor = this.getArmorContents();
            MobArmorEquipmentPacket pk = new MobArmorEquipmentPacket();
            pk.eid = this.getHolder().getId();
            pk.slots = armor;
            pk.encode();
            pk.isEncoded = true;
            Player[] var5 = players;
            int var6 = players.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                  Player player = var5[var7];
                  player.dataPacket(pk);
            }

      }

      public void sendArmorSlot(int index, Collection players) {
            this.sendArmorSlot(index, (Player[])players.stream().toArray((x$0) -> {
                  return new Player[x$0];
            }));
      }

      public void sendContents(Player player) {
            this.sendContents(new Player[]{player});
      }

      public void sendContents(Collection players) {
            this.sendContents((Player[])players.stream().toArray((x$0) -> {
                  return new Player[x$0];
            }));
      }

      public void sendContents(Player[] players) {
            InventoryContentPacket pk = new InventoryContentPacket();
            pk.slots = new Item[this.getSize()];

            for(int i = 0; i < this.getSize(); ++i) {
                  pk.slots[i] = this.getItem(i);
            }

            Player[] var8 = players;
            int var4 = players.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                  Player player = var8[var5];
                  int id = player.getWindowId(this);
                  if (id != -1 && player.spawned) {
                        pk.inventoryId = (byte)id;
                        player.dataPacket(pk.clone());
                  } else {
                        this.close(player);
                  }
            }

      }

      public void sendSlot(int index, Player player) {
            this.sendSlot(index, new Player[]{player});
      }

      public void sendSlot(int index, Collection players) {
            this.sendSlot(index, (Player[])players.stream().toArray((x$0) -> {
                  return new Player[x$0];
            }));
      }

      public void sendSlot(int index, Player[] players) {
            InventorySlotPacket pk = new InventorySlotPacket();
            pk.slot = index;
            pk.item = this.getItem(index).clone();
            Player[] var4 = players;
            int var5 = players.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                  Player player = var4[var6];
                  int id = player.getWindowId(this);
                  if (id == -1) {
                        this.close(player);
                  } else {
                        pk.inventoryId = (byte)id;
                        player.dataPacket(pk.clone());
                  }
            }

      }

      public KitEntity getHolder() {
            return (KitEntity)super.getHolder();
      }
}
