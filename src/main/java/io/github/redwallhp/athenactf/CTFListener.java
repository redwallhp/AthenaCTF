package io.github.redwallhp.athenactf;

import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.events.MatchCreateEvent;
import io.github.redwallhp.athenagm.events.MatchStateChangedEvent;
import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.matches.MatchState;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;


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


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (!isCTF(event.getPlayer())) return;
        if (event.getAction().equals(Action.PHYSICAL)) return;
        if (event.getClickedBlock() == null || !event.getClickedBlock().getType().equals(Material.STANDING_BANNER)) return;
        if (plugin.getMapConfig(event.getPlayer()) == null) return;

        MapConfiguration mapConf = plugin.getMapConfig(event.getPlayer());
        Flag flag = mapConf.getFlag(event.getClickedBlock().getLocation());
        Team team = PlayerUtil.getTeamForPlayer(plugin.getAthena().getArenaHandler(), event.getPlayer());
        if (flag != null && team != null) {

            // Take the enemy flag
            if (!flag.getTeam().equals(team)) {
                flag.take(event.getPlayer(), event.getClickedBlock());
                if (flag.getLocation().equals(flag.getHome())) {
                    Messenger.stealFlag(event.getPlayer(), team, flag);
                } else {
                    Messenger.takeFlag(event.getPlayer(), team, flag);
                }
            }

            // Return own flag
            else if (flag.getTeam().equals(team) && !flag.getLocation().equals(flag.getHome())) {
                flag.returnHome();
                Messenger.returnFlag(event.getPlayer(), flag);
            }

            // Score
            else if (flag.getTeam().equals(team) && flag.getLocation().equals(flag.getHome())) {
                Flag carried = mapConf.getFlag(event.getPlayer());
                if (carried != null) {
                    // handle score
                    carried.returnHome();
                    Messenger.score(event.getPlayer(), flag.getTeam());
                }
            }

        }

        event.setCancelled(true);

    }


    /**
     * Drop the flag on player death
     * @param event
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        if (!isCTF(event.getEntity())) return;
        if (plugin.getMapConfig(event.getEntity()) == null) return;

        MapConfiguration mapConf = plugin.getMapConfig(event.getEntity());
        Flag flag = mapConf.getFlag(event.getEntity());

        if (flag != null) {
            flag.drop(event.getEntity().getLocation());
            Messenger.dropFlag(event.getEntity(), flag);
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


    /**
     * Get the Flag a player clicked
     * @param blockClicked the block clicked
     * @return Flag or null
     */
    private Flag getFlagClicked(Player player, Block blockClicked) {
        MapConfiguration mapConf = plugin.getMapConfig(player);
        if (mapConf == null) return null;
        Flag flag = mapConf.getFlag(blockClicked.getLocation());
        if (flag != null) {
            return flag;
        } else {
            return null;
        }
    }


}
