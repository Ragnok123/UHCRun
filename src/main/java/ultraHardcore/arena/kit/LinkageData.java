package ultraHardcore.arena.kit;

import cn.nukkit.Player;
import cn.nukkit.level.sound.EndermanTeleportSound;
import cn.nukkit.math.Vector3;

public class LinkageData {
      public Player owner;
      public Vector3 firstPosition = null;
      public Vector3 secondPosition = null;

      public LinkageData(Player owner) {
            this.owner = owner;
      }

      public boolean canTeleport() {
            return this.firstPosition != null && this.secondPosition != null;
      }

      public void teleport(Player p, Vector3 touched) {
            Vector3 teleportPosition = null;
            if (touched.equals(this.firstPosition)) {
                  teleportPosition = this.secondPosition;
            } else if (touched.equals(this.secondPosition)) {
                  teleportPosition = this.firstPosition;
            }

            if (teleportPosition != null) {
                  p.level.addSound(new EndermanTeleportSound(p.add(0.0D, (double)p.getEyeHeight(), 0.0D)));
                  p.teleport(teleportPosition);
                  p.level.addSound(new EndermanTeleportSound(p.add(0.0D, (double)p.getEyeHeight(), 0.0D)));
            }

      }

      public void addPos(Vector3 pos) {
            if (this.firstPosition == null) {
                  this.firstPosition = pos;
            } else if (this.secondPosition == null) {
                  this.secondPosition = pos;
            }

      }

      public void removePos(Vector3 pos) {
            if (this.firstPosition == null) {
                  this.firstPosition = null;
            } else if (this.secondPosition == null) {
                  this.secondPosition = null;
            }

      }
}
