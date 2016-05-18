package io.github.redwallhp.athenactf;


import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.utilities.ItemUtil;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Flag {


    private String id;
    private Team team;
    private Vector location;
    private Vector home;
    private BlockFace facing;
    private UUID carrier;
    private Long dropTime;
    private ItemStack carrierHelmet;


    public Flag(String id, Team team, Vector home, BlockFace facing) {
        this.id = id;
        this.team = team;
        this.home = home;
        this.facing = facing;
        this.location = home;
        this.carrier = null;
        this.dropTime = 0L;
        this.carrierHelmet = null;
    }


    /**
     * Place the block at a location
     * @param loc The location to set the banner at
     */
    private void setBlock(Location loc) {
        // get the block
        Block block = loc.getBlock();
        block.setType(Material.STANDING_BANNER);
        // set the banner data
        Banner banner = (Banner) block.getState();
        banner.setBaseColor(DyeColor.getByWoolData(ItemUtil.getDyeColorByte(team.getColor())));
        banner.update();
        // rotation
        if (facing != null && this.home.equals(loc.toVector())) {
            org.bukkit.material.Banner data = (org.bukkit.material.Banner) banner.getData();
            data.setFacingDirection(facing);
            banner.setData(data);
            banner.update();
        }
    }


    /**
     * Drop the flag in the specified spot, returning it home if it isn't a valid place and an alternate
     * y-value can't be found.
     * @param loc The location to drop the flag at
     */
    public void drop(Location loc) {
        Location spot = null;
        for (int y = loc.getBlockY(); y < 255; y++) {
            Block b = loc.getWorld().getBlockAt(loc.getBlockX(), y, loc.getBlockZ());
            Block u = loc.getWorld().getBlockAt(loc.getBlockX(), y - 1, loc.getBlockZ());
            if (b.getType().equals(Material.AIR) && !u.getType().equals(Material.AIR) && !u.isLiquid()) {
                spot = b.getLocation();
                break;
            }
        }
        if (spot == null) {
            returnHome();
            Messenger.autoReturn(this);
            getTeam().getMatch().playSound(Sound.ENTITY_FIREWORK_BLAST);
        } else {
            setBlock(spot);
            this.location = spot.toVector();
            this.carrier = null;
            this.dropTime = System.currentTimeMillis();
        }
    }


    /**
     * Return the flag to its home
     */
    public void returnHome() {
        if (this.carrier == null) {
            Location dropped = this.location.toLocation(team.getMatch().getWorld());
            dropped.getBlock().setType(Material.AIR);
        }
        Location loc = this.home.toLocation(team.getMatch().getWorld());
        setBlock(loc);
        this.location = this.home;
        this.carrier = null;
        this.dropTime = 0L;
    }


    /**
     * Take the flag
     * @param player The player taking the flag
     * @param block The flag block
     */
    public void take(Player player, Block block) {
        block.setType(Material.AIR);
        this.carrier = player.getUniqueId();
    }


    /**
     * Place the flag in the flag carrier's helmet slot.
     * @param player The player to distinguish
     * @param distinguish Set whether the player is distinguished
     */
    public void distinguishPlayer(Player player, boolean distinguish) {
        if (distinguish) {
            if (player.getInventory().getHelmet() != null) {
                carrierHelmet = player.getInventory().getHelmet().clone();
            } else {
                carrierHelmet = null;
            }
            ItemStack banner = new ItemStack(Material.BANNER, 1);
            BannerMeta meta = (BannerMeta)banner.getItemMeta();
            meta.setBaseColor(DyeColor.getByWoolData(ItemUtil.getDyeColorByte(team.getColor())));
            meta.setDisplayName(String.format("%s team flag", team.getColoredName()));
            banner.setItemMeta(meta);
            player.getInventory().setHelmet(banner);
        } else {
            if (carrierHelmet != null) {
                player.getInventory().setHelmet(carrierHelmet);
                carrierHelmet = null;
            } else {
                player.getInventory().setHelmet(null);
            }
        }
    }


    public String getId() {
        return id;
    }


    public Team getTeam() {
        return team;
    }


    public Vector getLocation() {
        return location;
    }


    public void setLocation(Vector location) {
        this.location = location;
    }


    public void setLocation(Location location) {
        setLocation(location.toVector());
    }


    public Vector getHome() {
        return home;
    }


    public UUID getCarrier() {
        return carrier;
    }


    public void setCarrier(UUID carrier) {
        this.carrier = carrier;
    }


    public Long getDropTime() {
        return dropTime;
    }


}
