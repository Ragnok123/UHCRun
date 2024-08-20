package ultraHardcore.arena.manager;

import GTCore.Lang;
import GTCore.MTCore;
import GTCore.player.GTPlayer;
import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.TextFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import ultraHardcore.arena.Arena;
import ultraHardcore.arena.kit.Kit;
import ultraHardcore.arena.object.ArenaPlayerData;
import ultraHardcore.language.Language;

public class FormWindowManager {
      public static final int BASE_ID = -80000;
      public static final int REQUEST_ID = -80001;
      public static final int ACCEPT_ID = -80002;
      public static final int TEAM_ID = -80003;
      public static final int KIT_SELECT_ID = -80004;
      private static List translations = new ArrayList() {
            {
                  this.add(new HashMap() {
                        {
                              this.put("baseTitle", "Manage your team");
                              this.put("inviteTitle", "Invite players to your team");
                              this.put("acceptTitle", "Accept players requests");
                              this.put("requests", "Team requests");
                              this.put("request", "Request other players");
                              this.put("invitations", "Player invitations");
                              this.put("player", "Player name");
                              this.put("name", "name");
                              this.put("optional", "(optional)");
                              this.put("team", "Your team");
                              this.put("requests_help", "Click to accept request");
                              this.put("kit_title", "Kit select");
                              this.put("kit_desc", "Click on the kit you want to chose");
                        }
                  });
                  this.add(new HashMap() {
                  });
            }
      };
      private Arena plugin;

      public FormWindowManager(Arena arena) {
            this.plugin = arena;
      }

      public void addBaseWindow(Player p) {
            Map trans = (Map)translations.get(MTCore.getInstance().getPlayerData(p).getLanguage());
            FormWindowSimple window = new FormWindowSimple((String)trans.get("baseTitle"), "");
            window.addButton(new ElementButton((String)trans.get("requests"), new ElementButtonImageData("url", "https://thumb.ibb.co/hg251G/image.png")));
            window.addButton(new ElementButton((String)trans.get("request"), new ElementButtonImageData("url", "https://thumb.ibb.co/erj2gG/image.png")));
            window.addButton(new ElementButton((String)trans.get("team"), new ElementButtonImageData("url", "https://thumb.ibb.co/guSRVb/image.png")));
            ((GTPlayer)p).showFormWindow(window, -80000);
      }

      public void addRequestWindow(Player p) {
            Map trans = (Map)translations.get(MTCore.getInstance().getPlayerData(p).getLanguage());
            String player = (String)trans.get("player");
            String t = player + " " + (String)trans.get("optional");
            String placeHolder = (String)trans.get("name");
            FormWindowCustom window = new FormWindowCustom((String)trans.get("invite"));
            window.addElement(new ElementInput(player, placeHolder));
            window.addElement(new ElementInput(t, placeHolder));
            window.addElement(new ElementInput(t, placeHolder));
            window.addElement(new ElementInput(t, placeHolder));
            ((GTPlayer)p).showFormWindow(window, -80001);
      }

      public void addRequestsWindow(Player p) {
            ArenaPlayerData data = this.plugin.getPlayerData(p);
            if (data != null) {
                  Map trans = (Map)translations.get(MTCore.getInstance().getPlayerData(p).getLanguage());
                  FormWindowSimple window = new FormWindowSimple((String)trans.get("requests"), (String)trans.get("requests_help"));
                  Iterator var5 = (new ArrayList(data.requests.entrySet())).iterator();

                  while(var5.hasNext()) {
                        Entry entry = (Entry)var5.next();
                        Player target = (Player)entry.getValue();
                        if (target == null) {
                              data.requests.remove(entry.getKey());
                        } else {
                              window.addButton(new ElementButton(TextFormat.YELLOW + target.getName()));
                        }
                  }

                  ((GTPlayer)p).showFormWindow(window, -80002);
            }
      }

      public void addTeamWindow(Player p) {
            ArenaPlayerData data = this.plugin.getPlayerData(p);
            if (data != null) {
                  Map trans = (Map)translations.get(MTCore.getInstance().getPlayerData(p).getLanguage());
                  FormWindowCustom window = new FormWindowCustom((String)trans.get("team"));
                  window.addElement(new ElementLabel(""));
                  Iterator var5 = data.getTeam().getPlayers().values().iterator();

                  while(var5.hasNext()) {
                        Player team = (Player)var5.next();
                        window.addElement(new ElementLabel("" + TextFormat.YELLOW + TextFormat.BOLD + team.getName()));
                  }

                  ((GTPlayer)p).showFormWindow(window, -80003);
            }
      }

      public void addKitWindow(Player p) {
            ArenaPlayerData data = this.plugin.getPlayerData(p);
            Map trans = (Map)translations.get(MTCore.getInstance().getPlayerData(p).getLanguage());
            FormWindowSimple formWindowCustom = new FormWindowSimple((String)trans.get("kit_title"), (String)trans.get("kit_desc"));
            Kit[] var5 = Kit.values();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                  Kit kit = var5[var7];
                  formWindowCustom.addButton(new ElementButton((data.getPlayerData().hasKit(kit) ? TextFormat.DARK_GREEN : TextFormat.DARK_RED) + kit.getName(), new ElementButtonImageData("url", kit.getImageUrl())));
            }

            ((GTPlayer)p).showFormWindow(formWindowCustom, -80004);
      }

      public void handleResponse(Player p, int id, FormResponse formResponse) {
            FormResponseSimple responseSimple;
            if (id == -80004) {
                  if (!(formResponse instanceof FormResponseSimple)) {
                        return;
                  }

                  responseSimple = (FormResponseSimple)formResponse;

                  Kit kit;
                  try {
                        kit = Kit.values()[responseSimple.getClickedButtonId()];
                  } catch (Exception var9) {
                        MainLogger.getLogger().logException(var9);
                        p.sendMessage(var9.getMessage());
                        return;
                  }

                  this.plugin.kitManager.selectKit(p, this.plugin.getPlayerData(p), kit);
            } else if (this.plugin.phase == 0) {
                  if (id == -80000) {
                        responseSimple = (FormResponseSimple)formResponse;
                        id = responseSimple.getClickedButtonId();
                        switch(id) {
                        case 0:
                              this.addRequestsWindow(p);
                              break;
                        case 1:
                              this.addRequestWindow(p);
                              break;
                        case 2:
                              this.addTeamWindow(p);
                        }
                  } else {
                        ArenaPlayerData targetData;
                        String targetName;
                        if (id == -80001) {
                              responseSimple = (FormResponseSimple)formResponse;
                              targetName = TextFormat.clean(responseSimple.getClickedButton().getText());
                              Player target = this.plugin.getPlugin().getServer().getPlayer(targetName);
                              if (target == null) {
                                    p.sendMessage(Lang.translate(Lang.PL_NFOUND, p, new String[0]));
                                    return;
                              }

                              ArenaPlayerData targetData = this.plugin.getPlayerData(target);
                              if (targetData == null) {
                                    return;
                              }

                              if (targetData.getTeam().getOwner().getId() != target.getId()) {
                                    target = targetData.getTeam().getOwner();
                                    targetData = this.plugin.getPlayerData(target);
                                    if (targetData == null) {
                                          p.sendMessage(TextFormat.RED + "Data not found. This is probably bug");
                                          return;
                                    }
                              }

                              targetData = this.plugin.getPlayerData(p);
                              if (targetData.getTeam().getPlayers().size() > 1) {
                                    Language.translate("team_already", p);
                                    return;
                              }

                              if (targetData.getTeam().players.size() >= this.plugin.getData().teamSize) {
                                    Language.translate("team_full", p);
                                    return;
                              }

                              targetData.requests.put(p.getId(), p);
                              target.sendMessage(Language.translate("team_invite", target, p.getName()));
                        } else if (id == -80002) {
                              responseSimple = (FormResponseSimple)formResponse;
                              targetName = TextFormat.clean(responseSimple.getClickedButton().getText());
                              ArenaPlayerData data = this.plugin.getPlayerData(p);
                              if (data.getTeam().getOwner().getId() != p.getId()) {
                                    p.sendMessage(Language.translate("cannot_accept", p));
                                    return;
                              }

                              if (!p.hasPermission("gameteam.helper") && data.getTeam().getPlayers().size() >= this.plugin.getData().teamSize) {
                                    p.sendMessage(Language.translate("team_full", p));
                                    return;
                              }

                              Player target = this.plugin.getPlugin().getServer().getPlayer(targetName);
                              if (target == null) {
                                    p.sendMessage(Lang.translate(Lang.PL_NFOUND, p, new String[0]));
                                    return;
                              }

                              targetData = this.plugin.getPlayerData(target);
                              if (targetData == null) {
                                    return;
                              }

                              if (targetData.getTeam().getPlayers().size() > 1) {
                                    p.sendMessage(Language.translate("team2_already", p));
                                    return;
                              }

                              targetData.setTeam(data.getTeam());
                              data.getTeam().players.put(target.getName().toLowerCase(), target);
                              target.sendMessage(Language.translate("team_accept2", target, p.getName()));
                              p.sendMessage(Language.translate("team_accept", p, target.getName()));
                        }
                  }
            }

      }
}
