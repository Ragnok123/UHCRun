package ultraHardcore.language;

import GTCore.Lang;
import GTCore.MTCore;
import GTCore.Object.PlayerData;
import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Language {
      private static Map data = new HashMap();

      public static void init(Map data) {
            Iterator var1 = data.entrySet().iterator();

            while(var1.hasNext()) {
                  Entry entry = (Entry)var1.next();
                  Language.data.put(entry.getKey(), ((Config)entry.getValue()).getAll());
            }

      }

      public static String translate(String message, Player p, String... args) {
            return translate(message, MTCore.getInstance().getPlayerData(p), args);
      }

      public static String translate(String message, PlayerData data, String... args) {
            return translate(message, data.getLanguage(), args);
      }

      public static String translate(String message, int language, String... args) {
            String base = ((String)((Map)data.get(language)).get(message)).replaceAll("&", "§");
            if (base == null) {
                  return message;
            } else {
                  for(int i = 0; i < args.length; ++i) {
                        base = base.replace("%" + i, args[i]);
                  }

                  return base;
            }
      }

      public static HashMap getTranslations(String msg, String... args) {
            HashMap translations = new HashMap();
            int[] var3 = Lang.getLanguages();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                  int i = var3[var5];
                  translations.put(i, translate(msg, i, args));
            }

            return translations;
      }
}
