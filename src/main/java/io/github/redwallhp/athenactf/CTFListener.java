package io.github.redwallhp.athenactf;

import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.events.MatchCreateEvent;
import io.github.redwallhp.athenagm.events.MatchStateChangedEvent;
import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.matches.MatchState;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class CTFListener implements Listener {


    private AthenaCTF plugin;


    public CTFListener(AthenaCTF plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    /**
     * When a CTF match starts, load the map config and set flags up
     */
    @EventHandler
    public void onMatchCreate(MatchCreateEvent event) {
        if (!isCTF(event.getMatch())) return;
        try {
            MapConfiguration mapConfig = new MapConfiguration(event.getMatch());
            plugin.getMapConfigs().put(event.getMatch(), mapConfig);
        } catch (Exception ex) {
            plugin.getLogger().warning("[Map Config] " + ex.getMessage());
        }
    }


    /**
     * Clean up flags and map configuration when the match ends
     */
    @EventHandler
    public void onMatchStateChange(MatchStateChangedEvent event) {
        if (!isCTF(event.getMatch())) return;
        if (event.getCurrentState().equals(MatchState.ENDED)) {
            if (plugin.getMapConfigs().containsKey(event.getMatch())) {
                plugin.getMapConfigs().remove(event.getMatch());
            }
        }
    }


    /**
     * Check if a given Match/Map is calling for a KOTH gamemode
     * @param match The Match to check
     */
    private boolean isCTF(Match match) {
        return (match.getMap().getGameMode().equalsIgnoreCase("ctf"));
    }


    /**
     * Check if a given Player is in a Match/Map calling for a KOTH gamemode
     * @param player The Player to check
     */
    private boolean isCTF(Player player) {
        Arena arena = PlayerUtil.getArenaForPlayer(plugin.getAthena().getArenaHandler(), player);
        return (arena != null && isCTF(arena.getMatch()));
    }


}
