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
import java.util.Collection;
import java.util.List;
import java.util.Random;


public final class DiceBox extends JavaPlugin implements Listener {

    static final String prefix = "§l[§3§lDice§2§lBox§f§l]§r ";
    static CommandSender doingD = null;
    static int maxD;
    static List<Player> xdNumbers;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (command.getName().equalsIgnoreCase("dice")) {
                if (args.length == 0) {


                    if (doingD == sender) {
                        //D throw
                        int me = throwDice(maxD);
                        Bukkit.broadcastMessage(prefix + "§e§l" + sender.getName() + "§3§lは§e§l" + (maxD) + "§3§l面ダイスを振って§e§l" + me + "§3§lがでた");
                        boolean b = false;
                        if (xdNumbers.get(me - 1) != null) {
                            Bukkit.broadcastMessage(prefix + "§e§l§n" + xdNumbers.get(me - 1).getName() + "§5§l§nはピッタリで当てました！！ｷﾀ――(ﾟ∀ﾟ)――!!");
                            b = true;
                        }
                        if (xdNumbers.size() != me && xdNumbers.get(me) != null) {
                            Bukkit.broadcastMessage(prefix + "§e§l" + xdNumbers.get(me).getName() + "§2§lは1多い誤差で当てました！！");
                            b = true;
                        }
                        if (me >= 2 && xdNumbers.get(me - 2) != null) {
                            Bukkit.broadcastMessage(prefix + "§e§l" + xdNumbers.get(me - 2).getName() + "§2§lは1少ない誤差で当てました！！");
                            b = true;
                        }
                        if (!b) sender.sendMessage(prefix + "§7§l当選者はいませんでした。");
                        doingD = null;
                        return true;
                    }


                    //
                    sender.sendMessage(prefix + "§c§lダイスを振ります");
                    sender.sendMessage(prefix + "§c/dice <面数> [個数]");
                    sender.sendMessage(prefix + "§7個数が10個以上だと公のチャットに公開されません");
                    if (sender.isOp()) {
                        sender.sendMessage(prefix + "§c/dice <面数>D でDを始めることができます。");
                        sender.sendMessage(prefix + "§c/dice dlist でDの番号使用状況が見れます。 ");
                    }
                    return true;
                }

                if (args[0].charAt(args[0].length() - 1) == 'D' && sender.isPermissionSet("dicebox.startD")) {
                    //start D
                    if (doingD != null) {
                        sender.sendMessage(prefix + "§cただいまDは開催中です！！");
                        return true;
                    }
                    int d = isLargerNumberThan0(args[0].substring(0, args[0].length() - 1));
                    doingD = sender;
                    xdNumbers = new ArrayList<>();
                    while (xdNumbers.size() != d) {
                        xdNumbers.add(null);
                    }
                    maxD = d;
                    Bukkit.broadcastMessage(prefix + "§e§l" + sender.getName() + "§d§lによって§e§l" + args[0] + "§d§lが開催されました！！");
                    sender.sendMessage(prefix + "§a/diceでダイスを振ります。");
                    return true;
                }


                if (args[0].equalsIgnoreCase("dlist") && sender.isPermissionSet("dicebox.startD")) {
                    if (doingD == null) {
                        sender.sendMessage(prefix + "§c§lDはただいま開催していません！");
                        return true;
                    }
                    if (doingD != sender) {
                        sender.sendMessage(prefix + "§c§lこのコマンドはD開催者専用です！");
                        return true;
                    }
                    int ii = 0;
                    for (Player xdNumber : xdNumbers) {
                        if (xdNumber != null) ii++;
                    }
                    sender.sendMessage(prefix + "§a§l数字利用率：§e§l" + ((int) (((double) ii) / maxD * 10000)) / 100.0 + "% §7(" + ii + "/" + maxD + ")");
                    ii = 0;
                    Collection<? extends Player> ps = getServer().getOnlinePlayers();
                    for (Player player : ps) {
                        if (xdNumbers.contains(player)) ii++;
                    }
                    sender.sendMessage(prefix + "§a§l鯖民参加率：§e§l" + ((int) (((double) ii) / ps.size() * 10000)) / 100.0 + "% §7(" + ii + "/" + ps.size() + ")");
                    return true;
                }
                //throw Dice
                int d = isLargerNumberThan0(args[0]);
                String s = "";
                int t=(args.length ==1?1:isLargerNumberThan0(args[1]));
                int tt=0;
                for (int i=0;i<t;i++) {
                    int ii=throwDice(d);
                    s += ii+",";
                    tt+=ii;
                }
                s=s.substring(0,s.length()-1);
                if(t<10)Bukkit.broadcastMessage(prefix + "§e§l" + sender.getName() + "§3§lは§e§l" + args[0] + "§3§l面ダイスを§e§l"+(t==1?"§3§l":t+"§3§l個")+"振って§e§l" +s+(t!=1?"§6§l(TOTAL:"+tt+")":"")+ "§3§lがでた");
                else sender.sendMessage(prefix + "§e§l" + sender.getName() + "§3§lは§e§l" + args[0] + "§3§l面ダイスを§e§l" + (t + "§3§l個") + "振って§e§l" + s+"§6§l(TOTAL:"+tt+")" + "§3§lがでた (PRIVATE)");
            }
        } catch (NumberException e) {
            sender.sendMessage(e.message);
            return true;
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


    int isLargerNumberThan0(String s) throws NumberException{
        if(!isNum(s)){
            throw new NumberException(prefix + "§c数値を入力してください！");
        }
        int i = Integer.parseInt(s);
        if(i<1){
            throw new NumberException(prefix + "§c１以上の数値を選択してください！");
        }
        return i;
    }

    //1~max
    int throwDice(int max){
        return new Random().nextInt(max)+1;
    }
}

class NumberException extends Exception{
    String message;

    public NumberException(String message){
        this.message = message;
    }

}