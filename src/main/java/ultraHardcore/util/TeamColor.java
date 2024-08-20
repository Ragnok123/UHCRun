package ultraHardcore.util;

import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TextFormat;

public enum TeamColor {
      BLUE(DyeColor.LIGHT_BLUE, TextFormat.AQUA),
      DARK_BLUE(DyeColor.BLUE, TextFormat.DARK_BLUE),
      CYAN(DyeColor.CYAN, TextFormat.DARK_AQUA),
      RED(DyeColor.RED, TextFormat.RED),
      ORANGE(DyeColor.ORANGE, TextFormat.GOLD),
      YELLOW(DyeColor.YELLOW, TextFormat.YELLOW),
      GREEN(DyeColor.GREEN, TextFormat.DARK_GREEN),
      LIGHT_GREEN(DyeColor.LIME, TextFormat.GREEN),
      MAGENTA(DyeColor.MAGENTA, TextFormat.LIGHT_PURPLE),
      PURPLE(DyeColor.PURPLE, TextFormat.DARK_PURPLE),
      GRAY(DyeColor.LIGHT_GRAY, TextFormat.GRAY),
      DARK_GRAY(DyeColor.GRAY, TextFormat.DARK_GRAY),
      BLACK(DyeColor.BLACK, TextFormat.BLACK),
      WHITE(DyeColor.WHITE, TextFormat.WHITE);

      private final DyeColor dyeColor;
      private final TextFormat chatColor;

      private TeamColor(DyeColor dyeColor, TextFormat chatColor) {
            this.dyeColor = dyeColor;
            this.chatColor = chatColor;
      }

      public String toString() {
            return "" + this.getChatColor();
      }

      public String getName() {
            return this.name().toLowerCase();
      }

      public DyeColor getDyeColor() {
            return this.dyeColor;
      }

      public TextFormat getChatColor() {
            return this.chatColor;
      }
}
