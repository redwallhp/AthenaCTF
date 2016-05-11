package io.github.redwallhp.athenactf;

import io.github.redwallhp.athenagm.matches.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class FlagTask extends BukkitRunnable {


    private AthenaCTF plugin;
    private HashMap<Flag, BossBar> returnBars;


    public FlagTask(AthenaCTF plugin) {
        this.plugin = plugin;
        this.returnBars = new HashMap<Flag, BossBar>();
        this.runTaskTimer(plugin, 0L, 20L);
    }


    public void run() {
        for (MapConfiguration mapConf : plugin.getMapConfigs().values()) {
            for (Flag flag : mapConf.getFlags().values()) {
                handleDroppedFlag(flag, mapConf);
            }
        }
    }


    /**
     * Handle the automatic return of any flags that are dropped
     */
    private void handleDroppedFlag(Flag flag, MapConfiguration mapConf) {
        if (flag.getCarrier() == null && !flag.getLocation().equals(flag.getHome())) {
            long timeDiff = System.currentTimeMillis() - flag.getDropTime();
            long seconds = timeDiff / 1000;
            Location loc = flag.getLocation().toLocation(flag.getTeam().getMatch().getWorld());
            if (seconds >= mapConf.getReturnTime()) {
                flag.returnHome();
                Messenger.autoReturn(flag);
                flag.getTeam().getMatch().playSound(Sound.ENTITY_FIREWORK_BLAST);
            } else if (seconds >= 5) {
                flag.getTeam().getMatch().playSound(Sound.UI_BUTTON_CLICK, 1.5f);
            }
            doReturnBar(flag, seconds, mapConf.getReturnTime());
        }
    }


    /**
     * Draw the BossBar countdown for a flag on the ground
     * @param flag The flag
     * @param seconds The number of seconds the flag has been on the ground
     * @param returnTime The duration before the flag is returned, from the config
     */
    private void doReturnBar(Flag flag, long seconds, int returnTime) {
        String msg = "%s%s flag will return in %d seconds";
        if (!returnBars.containsKey(flag)) {
            String title = String.format(msg, flag.getTeam().getColoredName(), ChatColor.RESET, returnTime);
            BossBar bar = plugin.getServer().createBossBar(title, BarColor.PURPLE, BarStyle.SOLID);
            for (Player p : flag.getTeam().getMatch().getAllPlayers()) {
                bar.addPlayer(p);
            }
            bar.setProgress(1.0);
            returnBars.put(flag, bar);
        }
        else if (returnBars.containsKey(flag) && seconds < returnTime) {
            BossBar bar = returnBars.get(flag);
            double progress;
            try {
                progress = (double)seconds / (double)returnTime;
            } catch (ArithmeticException ex) {
                progress = 1.0;
            }
            bar.setProgress(progress);
            bar.setTitle(String.format(msg, flag.getTeam().getColoredName(), ChatColor.RESET, returnTime-seconds));
        }
        else {
            BossBar bar = returnBars.get(flag);
            bar.removeAll();
            returnBars.remove(flag);
        }
    }


}
