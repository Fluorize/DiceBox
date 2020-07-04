package com.gmail.supergame314.dicebox;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;


public final class DiceBox extends JavaPlugin implements Listener {

    static final String prefix = "§l[§3§lDice§2§lBox§f§l]§r ";
    static CommandSender doingD = null;
    static int maxD;
    static List<Player> xdNumbers;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("dice")){
            if(args.length == 0){
                if(doingD == sender){
                    int me = new Dice(maxD).goThrow();
                    Bukkit.broadcastMessage(prefix + "§e§l"+sender.getName()+"§3§lは§e§l" + (maxD) + "§3§l面ダイスを振って§e§l" +me+"§3§lがでた");
                    if(xdNumbers.get(me - 1) !=null)
                        Bukkit.broadcastMessage(prefix + "§e§l§n"+ xdNumbers.get(me - 1).getName()+"§5§l§nはピッタリで当てました！！ｷﾀ――(ﾟ∀ﾟ)――!!");
                    if(xdNumbers.size()!=me && xdNumbers.get(me) !=null)
                        Bukkit.broadcastMessage(prefix + "§e§l"+ xdNumbers.get(me - 1).getName()+"§2§lは1多い誤差で当てました！！");
                    if(me>=2 && xdNumbers.get(me - 2) !=null)
                        Bukkit.broadcastMessage(prefix + "§e§l"+ xdNumbers.get(me - 1).getName()+"§2§lは1少ない誤差で当てました！！");
                    doingD = null;
                    return true;
                }
                sender.sendMessage(prefix+"§c§lダイスを振ります");
                sender.sendMessage(prefix+"§c/dice 面数");
                if(sender.isOp())
                    sender.sendMessage(prefix+"§c/dice 面数D でDを始めることができます。");
                return true;
            }
            if(args[0].charAt(args[0].length()-1) == 'D' && sender.isPermissionSet("dicebox.startD")) {
                if(doingD != null){
                    sender.sendMessage(prefix+"§cただいまDは開催中です！！");
                    return true;
                }
                if(!isNum(args[0].substring(0,args[0].length()-1))) {
                    sender.sendMessage(prefix + "§c最大面数には数値を入力してください！");
                    return true;
                }
                int d = Integer.parseInt(args[0].substring(0,args[0].length()-1));
                if(d<=0){
                    sender.sendMessage(prefix + "§c最大面数には１以上の数値を入力してください！");
                    return true;
                }
                doingD = sender;
                xdNumbers = new ArrayList<>();
                while(xdNumbers.size()!=d){
                    xdNumbers.add(null);
                }
                maxD=d;
                Bukkit.broadcastMessage(prefix+"§e§l"+sender.getName()+"§d§lによって§e§l"+args[0]+"§d§lが開催されました！！");
                sender.sendMessage(prefix+"§a/udiceでダイスを振ります。");
                return true;
            }
            if(!isNum(args[0])) {
                sender.sendMessage(prefix+"§c最大面数には数値を入力してください！");
                return true;
            }
            if(Integer.parseInt(args[0])<=0){
                sender.sendMessage(prefix + "§c最大面数には１以上の数値を入力してください！");
                return true;
            }
            Bukkit.broadcastMessage(prefix + "§e§l"+sender.getName()+"§3§lは§e§l" + args[0] + "§3§l面ダイスを振って§e§l" + new Dice(Integer.parseInt(args[0])).goThrow() +"§3§lがでた");
        }
        return true;
    }

    @EventHandler
    public void dChat(AsyncPlayerChatEvent ev){
        if(doingD==null)
            return;
        if(!isNum(ev.getMessage()))
            return;
        ev.setCancelled(true);
        int num = Integer.parseInt(ev.getMessage());
        if(num<=0 || num>maxD) {
            ev.getPlayer().sendMessage(prefix+"§c1~"+maxD+"で指定してください！");
            return;
        }
        if(xdNumbers.get(num - 1) != null) {
            ev.getPlayer().sendMessage(prefix+"§cその番号は既に言われています！");
            return;
        }
        if(xdNumbers.contains(ev.getPlayer())){
            ev.getPlayer().sendMessage(prefix+"§c§lあなたは既に数字を選択しています！");
            return;
        }
        xdNumbers.set(num - 1, ev.getPlayer());
        ev.getPlayer().sendMessage(prefix+"§e§l"+num+"§a§lにしました！");
        doingD.sendMessage(prefix+"§7"+ev.getPlayer().getName()+"は"+num+"を選びました。");
    }

    @Override
    public void onEnable() {
        getCommand("dice").setExecutor(this);
        getServer().getPluginManager().registerEvents(this,this);
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    boolean isNum(String target){
        try {
            Integer.parseInt(target);
        }catch (NumberFormatException ex){
            return false;
        }
        return true;
    }

}
