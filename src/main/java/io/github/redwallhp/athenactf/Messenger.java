package io.github.redwallhp.athenactf;


import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Messenger {


    public static void stealFlag(Player stealer, Team team, Flag flag) {
        String tMsg = String.format("%s%s%s stole %sthe enemy flag", team.getChatColor(), stealer.getName(), ChatColor.RESET, flag.getTeam().getChatColor());
        String eMsg = String.format("%s%s%s stole %syour flag", team.getChatColor(), stealer.getName(), ChatColor.RESET, flag.getTeam().getChatColor());
        team.broadcast(tMsg);
        flag.getTeam().broadcast(eMsg);
    }


    public static void takeFlag(Player stealer, Team team, Flag flag) {
        String tMsg = String.format("%s%s%s picked up %sthe enemy flag", team.getChatColor(), stealer.getName(), ChatColor.RESET, flag.getTeam().getChatColor());
        String eMsg = String.format("%s%s%s picked up %syour flag", team.getChatColor(), stealer.getName(), ChatColor.RESET, flag.getTeam().getChatColor());
        team.broadcast(tMsg);
        flag.getTeam().broadcast(eMsg);
    }


    public static void returnFlag(Player returner, Flag flag) {
        String tMsg = String.format("%s%s%s returned %syour flag", flag.getTeam().getChatColor(), returner.getName(), ChatColor.RESET, flag.getTeam().getChatColor());
        String eMsg = String.format("%s%s%s returned %sthe enemy flag", flag.getTeam().getChatColor(), returner.getName(), ChatColor.RESET, flag.getTeam().getChatColor());
        flag.getTeam().broadcast(tMsg);
        broadcastOtherteams(eMsg, flag.getTeam());
    }


    public static void dropFlag(Player dropper, Flag flag) {
        Team team = PlayerUtil.getTeamForPlayer(flag.getTeam().getMatch(), dropper);
        String tMsg = String.format("%s%s%s dropped %sthe enemy flag", team.getChatColor(), dropper.getName(), ChatColor.RESET, flag.getTeam().getChatColor());
        String eMsg = String.format("%s%s%s dropped %syour flag", team.getChatColor(), dropper.getName(), ChatColor.RESET, flag.getTeam().getChatColor());
        flag.getTeam().broadcast(eMsg);
        broadcastOtherteams(tMsg, flag.getTeam());
    }


    public static void score(Player scorer, Team team) {
        String tMsg = String.format("%s%s%s scored for %syour team", team.getChatColor(), scorer.getName(), ChatColor.RESET, team.getChatColor());
        String eMsg = String.format("%s%s%s scored for %sthe enemy team", team.getChatColor(), scorer.getName(), ChatColor.RESET, team.getChatColor());
        team.broadcast(tMsg);
        broadcastOtherteams(eMsg, team);
    }


    private static void broadcastOtherteams(String msg, Team skipTeam) {
        for (Team team : skipTeam.getMatch().getTeams().values()) {
            if (!team.equals(skipTeam)) {
                team.broadcast(msg);
            }
        }
    }


}
