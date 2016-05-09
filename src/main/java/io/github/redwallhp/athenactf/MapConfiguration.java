package io.github.redwallhp.athenactf;

import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.matches.Team;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.naming.ConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;


public class MapConfiguration {


    private Match match;
    private Integer returnTime;
    private HashMap<String, Flag> flags;


    public MapConfiguration(Match match) throws IOException, ConfigurationException {
        load(match);
        placeFlags();
    }


    /**
     * Load the map configuration from disk
     * @param match The Match
     * @throws IOException
     * @throws ConfigurationException
     */
    private void load(Match match) throws IOException, ConfigurationException {

        File file = new File(match.getMap().getPath(), "ctf.yml");
        if (!file.exists()) throw new IOException(String.format("No ctf.yml found at path %s", file.getPath()));
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        this.match = match;
        this.returnTime = yaml.getInt("return_time", 10);
        this.flags = new HashMap<String, Flag>();

        if (yaml.getConfigurationSection("flags") != null && yaml.getConfigurationSection("flags").getKeys(false).size() > 0) {
            Set<String> keys = yaml.getConfigurationSection("flags").getKeys(false);
            for (String key : keys) {
                ConfigurationSection s = yaml.getConfigurationSection(String.format("flags.%s", key));
                if (s == null) continue;
                if (!s.contains("x") || !s.contains("y") || !s.contains("z") || !s.contains("team")) {
                    throw new ConfigurationException("Flag configuration error: invalid flag definition");
                }
                Integer x = s.getInt("x");
                Integer y = s.getInt("y");
                Integer z = s.getInt("z");
                String f = s.getString("facing", null);
                String t = s.getString("team");
                Team team = null;
                if (match.getTeams().containsKey(t)) {
                    team = match.getTeams().get(t);
                } else {
                    throw new ConfigurationException("Flag configuration error: invalid team");
                }
                BlockFace facing = null;
                if (f != null) {
                    try {
                        facing = BlockFace.valueOf(f.toUpperCase());
                    } catch (IllegalArgumentException ex) {
                        facing = null;
                    }
                }
                Flag flag = new Flag(key, team, new Vector(x, y, z), facing);
                flags.put(key, flag);
            }
            if (keys.size() == 0) {
                throw new ConfigurationException("Flag configuration error: no flags defined");
            }
        }

    }


    /**
     * Place the flags in their initial home spots on match start
     */
    private void placeFlags() {
        for (Flag flag : flags.values()) {
            flag.returnHome();
        }
    }


    /**
     * Get the flag at a specified location
     * @param loc The location to check
     * @return Flag or null
     */
    public Flag getFlag(Location loc) {
        for (Flag flag : flags.values()) {
            if (flag.getLocation().equals(loc.toVector())) {
                return flag;
            }
        }
        return null;
    }


    /**
     * Get the flag a player is carrying
     * @param player The player to check
     * @return Flag or null
     */
    public Flag getFlag(Player player) {
        for (Flag flag : flags.values()) {
            if (flag.getCarrier() != null && flag.getCarrier().equals(player.getUniqueId())) {
                return flag;
            }
        }
        return null;
    }


    /**
     * Get the match this flag set is associated with
     */
    public Match getMatch() {
        return match;
    }


    /**
     * Get the flag set for this match
     */
    public HashMap<String, Flag> getFlags() {
        return flags;
    }


    /**
     * Get the time to wait before returning a dropped flag
     */
    public Integer getReturnTime() {
        return returnTime;
    }


}
