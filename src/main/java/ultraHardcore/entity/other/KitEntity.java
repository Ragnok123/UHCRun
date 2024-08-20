package ultraHardcore.entity.other;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.UUID;
import ultraHardcore.arena.kit.Kit;

public class KitEntity extends EntityCreature implements InventoryHolder {
      public static final int NETWORK_ID = -1;
      public HumanInventory inventory;
      private Skin skin;
      private UUID uuid;
      private Kit kit = null;

      public KitEntity(FullChunk chunk, CompoundTag nbt) {
            super(chunk, nbt);
            this.dataProperties.putLong(0, 49152L);
      }

      public void initEntity() {
            this.dataProperties.putLong(0, 49152L);
            this.inventory = new HumanInventory(this);
            if (this.namedTag.contains("Inventory") && this.namedTag.get("Inventory") instanceof ListTag) {
                  ListTag inventoryList = this.namedTag.getList("Inventory", CompoundTag.class);
                  Iterator var2 = inventoryList.getAll().iterator();

                  label44:
                  while(true) {
                        while(true) {
                              if (!var2.hasNext()) {
                                    break label44;
                              }

                              CompoundTag item = (CompoundTag)var2.next();
                              int slot = item.getByte("Slot");
                              if (slot >= 0 && slot < 9) {
                                    this.inventory.setHotbarSlotIndex(slot, item.contains("TrueSlot") ? item.getByte("TrueSlot") : -1);
                              } else if (slot >= 100 && slot < 104) {
                                    this.inventory.setItem(this.inventory.getSize() + slot - 100, NBTIO.getItemHelper(item));
                              } else {
                                    this.inventory.setItem(slot - 9, NBTIO.getItemHelper(item));
                              }
                        }
                  }
            }

            if (this.namedTag.contains("Skin") && this.namedTag.get("Skin") instanceof CompoundTag) {
                  if (!this.namedTag.getCompound("Skin").contains("Transparent")) {
                        this.namedTag.getCompound("Skin").putBoolean("Transparent", false);
                  }

                  this.setSkin(new Skin(this.namedTag.getCompound("Skin").getByteArray("Data"), this.namedTag.getCompound("Skin").getString("ModelId")));
            }

            this.uuid = Utils.dataToUUID(new byte[][]{String.valueOf(this.getId()).getBytes(StandardCharsets.UTF_8), this.getSkin().getData(), this.getNameTag().getBytes(StandardCharsets.UTF_8)});
            super.initEntity();
            this.kit = this.getKit();
            this.setNameTag(this.getFullName());
      }

      public void saveNBT() {
            super.saveNBT();
            this.namedTag.putList(new ListTag("Inventory"));
            if (this.inventory != null) {
                  int slot;
                  Item item;
                  for(int slot = 0; slot < 9; ++slot) {
                        slot = this.inventory.getHotbarSlotIndex(slot);
                        if (slot != -1) {
                              item = this.inventory.getItem(slot);
                              if (item.getId() != 0 && item.getCount() > 0) {
                                    this.namedTag.getList("Inventory", CompoundTag.class).add(NBTIO.putItemHelper(item, slot).putByte("TrueSlot", slot));
                                    continue;
                              }
                        }

                        this.namedTag.getList("Inventory", CompoundTag.class).add((new CompoundTag()).putByte("Count", 0).putShort("Damage", 0).putByte("Slot", slot).putByte("TrueSlot", -1).putShort("id", 0));
                  }

                  int slotCount = 45;

                  for(slot = 9; slot < slotCount; ++slot) {
                        item = this.inventory.getItem(slot - 9);
                        this.namedTag.getList("Inventory", CompoundTag.class).add(NBTIO.putItemHelper(item, slot));
                  }

                  for(slot = 100; slot < 104; ++slot) {
                        item = this.inventory.getItem(this.inventory.getSize() + slot - 100);
                        if (item != null && item.getId() != 0) {
                              this.namedTag.getList("Inventory", CompoundTag.class).add(NBTIO.putItemHelper(item, slot));
                        }
                  }
            }

      }

      public void spawnTo(Player p) {
            if (!this.hasSpawned.containsKey(p.getLoaderId())) {
                  this.hasSpawned.put(p.getLoaderId(), p);
                  if (this.skin.getData().length < 8192) {
                        throw new IllegalStateException(this.getClass().getSimpleName() + " must have a valid skin set");
                  }

                  AddPlayerPacket pk = new AddPlayerPacket();
                  pk.uuid = this.getUuid();
                  pk.username = this.getName();
                  pk.entityUniqueId = this.getId();
                  pk.entityRuntimeId = this.getId();
                  pk.x = (float)this.x;
                  pk.y = (float)(this.y - 1.62D);
                  pk.z = (float)this.z;
                  pk.speedX = (float)this.motionX;
                  pk.speedY = (float)this.motionY;
                  pk.speedZ = (float)this.motionZ;
                  pk.yaw = (float)this.yaw;
                  pk.pitch = (float)this.pitch;
                  pk.item = this.getInventory().getItemInHand();
                  pk.metadata = this.dataProperties;
                  p.dataPacket(pk);
                  this.inventory.sendContents(p);
                  this.inventory.sendArmorContents(p);
                  this.inventory.sendHeldItem(p);
            }

      }

      public void despawnFrom(Player player) {
            if (this.hasSpawned.containsKey(player.getLoaderId())) {
                  RemoveEntityPacket pk = new RemoveEntityPacket();
                  pk.eid = this.getId();
                  player.dataPacket(pk);
                  this.hasSpawned.remove(player.getLoaderId());
            }

      }

      public Kit getKit() {
            if (this.kit == null) {
                  this.kit = Kit.valueOf(this.namedTag.getString("KitName").toUpperCase());
            }

            return this.kit;
      }

      private String getFullName() {
            String name = this.getKit().getName();
            int cost = this.getKit().getCost();
            return TextFormat.GRAY + "[" + (cost > 0 ? "" + TextFormat.DARK_AQUA + cost : TextFormat.GREEN + "FREE") + TextFormat.GRAY + "]  " + TextFormat.YELLOW + name.substring(0, 1).toUpperCase() + name.substring(1);
      }

      public int getNetworkId() {
            return -1;
      }

      public HumanInventory getInventory() {
            return this.inventory;
      }

      public Skin getSkin() {
            return this.skin;
      }

      public void setSkin(Skin skin) {
            this.skin = skin;
      }

      public UUID getUuid() {
            return this.uuid;
      }
}
