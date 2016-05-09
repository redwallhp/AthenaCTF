package io.github.redwallhp.athenactf;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;


public class AthenaCTF extends JavaPlugin {


    public static AthenaCTF instance;
    private AthenaGM athena;
    private HashMap<Match, MapConfiguration> mapConfigs;


    @Override
    public void onEnable() {
        AthenaCTF.instance = this;
        mapConfigs = new HashMap<Match, MapConfiguration>();
        if (checkAthena()) {
            new CTFListener(this);
        }
    }


    /**
     * Load the server's AthenaGM instance, returning false if AthenaGM is not installed.
     * @return true if AthenaGM is installed and active, false otherwise
     */
    private boolean checkAthena() {
        Plugin plugin = getServer().getPluginManager().getPlugin("AthenaGM");
        if (plugin == null || !(plugin instanceof AthenaGM)) {
            this.setEnabled(false);
            return false;
        } else {
            athena = (AthenaGM) plugin;
            return true;
        }
    }


    /**
     * Get the server's AthenaGM instance
     * @return AthenaGM instance
     */
    public AthenaGM getAthena() {
        return athena;
    }


    /**
     * Get the map config index
     */
    public HashMap<Match, MapConfiguration> getMapConfigs() {
        return mapConfigs;
    }


    /**
     * Get the relevant map config by Match
     * @param match The Match the config corresponds to
     * @return MapConfiguration or null
     */
    public MapConfiguration getMapConfig(Match match) {
        if (mapConfigs.containsKey(match)) {
            return mapConfigs.get(match);
        } else {
            return null;
        }
    }


    /**
     * Get the relevant map config by player
     * @param player The player to check for a MapConfiguration
     * @return MapConfiguration or null
     */
    public MapConfiguration getMapConfig(Player player) {
        Arena arena = PlayerUtil.getArenaForPlayer(getAthena().getArenaHandler(), player);
        if (arena != null) {
            return getMapConfig(arena.getMatch());
        } else {
            return null;
        }
    }


}
