package ultraHardcore.entity.other;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import ultraHardcore.util.TeamColor;

public class TeamEntity extends Entity {
      public static final int NETWORK_ID = -94;
      private int team;
      private TeamColor teamColor;

      public TeamEntity(FullChunk chunk, CompoundTag nbt) {
            super(chunk, nbt);
      }

      public void initEntity() {
            if (this.namedTag.contains("team")) {
                  this.team = this.namedTag.getInt("team");
            }

            this.teamColor = TeamColor.values()[this.team % 14];
            this.dataProperties.putByte(3, this.teamColor.getDyeColor().getWoolData());
            this.setDataFlag(0, 16);
            this.setDataFlag(0, 14);
            this.setDataFlag(0, 15);
            this.updateNameTag(0);
      }

      public int getNetworkId() {
            return -94;
      }

      public void spawnTo(Player player) {
            if (!this.hasSpawned.containsKey(player.getLoaderId()) && player.usedChunks.containsKey(Level.chunkHash(this.chunk.getX(), this.chunk.getZ()))) {
            }

      }

      public void updateNameTag(int players) {
            String chatColor = this.getTeamColor().toString();
            this.setNameTag(chatColor + "Players: " + TextFormat.GRAY + "(" + chatColor + players + TextFormat.GRAY + "/" + chatColor + "2" + TextFormat.GRAY + ")");
      }

      public int getTeam() {
            return this.team;
      }

      public TeamColor getTeamColor() {
            return this.teamColor;
      }
}
