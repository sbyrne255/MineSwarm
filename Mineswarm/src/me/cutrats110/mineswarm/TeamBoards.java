package me.cutrats110.mineswarm;

import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class TeamBoards {
	public Plugin plugin = null;
	EventListenerHelper helper;
	public TeamBoards(Plugin plugin){
		this.plugin = plugin;
	}
	public void setTeamData(EventListenerHelper helper) {
		this.helper = helper;
	}
	
	/**
	 * Holds list of all boards (each team has a board)
	 *
	 * @return HashMap<String, ScoreBoard>
	 */
	private HashMap<String, Scoreboard> boards = new HashMap<>();
	/**
	 * Creates a new board for team. Called when team is created.
	 *
	 * @param Team name as a string
	 * @return void
	 */
	public void makeScoreBoard (String teamName) {
		try {
			Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();	
			Objective teamMembersBoard = board.registerNewObjective("teamhp", "dummy","hp");	
			teamMembersBoard.setDisplaySlot(DisplaySlot.SIDEBAR);
			teamMembersBoard.setDisplayName("  Team Health  ");	
			boards.put(teamName, board);
		}catch(Exception er) {
			plugin.getLogger().info("Error in making board.  " + er.toString());
		}
	}
	
	/**
	 * Updates scoreboard for specific team and all its members.
	 *
	 * @param List of Players on the team
	 * @param String, team name
	 * @return void
	 * @see setScoreboard(List<Player> players, String teamName, Player updatedPlayer, int health)
	 */
	public void setScoreboard(List<Player> players, String teamName) {
		try {
			Scoreboard teamBoard = boards.get(teamName);
			Objective teamMembersBoard = teamBoard.getObjective("teamhp");
			if(players != null) {
				for(Player p : players){
					try {
						if(p.isOnline()) {
							if(helper.isPlayerDown(p)) {
								teamMembersBoard.getScore(p.getName()).setScore((int) ((p.getHealth()/2) * -1));	
							} else {
								teamMembersBoard.getScore(p.getName()).setScore((int) (p.getHealth()/2));
							}
						}
					}catch(NullPointerException np) {continue;}
				}
				for(Player p : players){
					if(p.isOnline()) {
						p.setScoreboard(teamBoard);
					}
				}
			}
		}catch(NullPointerException np) {plugin.getLogger().info("Unexpected null value while updating scoreboard. ");}
		catch(Exception er) {plugin.getLogger().info("Unexpected error while setting scoreboard " + er.toString());}
	}
	
	/**
	 * Updates scoreboard for specific player then updates other members to reflect new health change.
	 * Is used when a player takes damage and needs to be updated on the board before the board is refreshed for other players.
	 *
	 * @param List of Players on the team.
	 * @param String, team name.
	 * @param Player object of player with the changing health value.
	 * @param The new health value as an Int.
	 * @return void
	 * @see setScoreboard(List<Player> players, String teamName)
	 */
	public void setScoreboard(List<Player> players, String teamName, Player updatedPlayer, int health) {
		Scoreboard teamBoard = boards.get(teamName);
		Objective teamMembersBoard = teamBoard.getObjective("teamhp");
		for(Player p : players){
			try {
				if(p.isOnline()) {
					if(helper.isPlayerDown(p)) {
						if(p != updatedPlayer) {teamMembersBoard.getScore(p.getName()).setScore((int) ((p.getHealth()/2) *-1));
						}else {teamMembersBoard.getScore(updatedPlayer.getName()).setScore(health*-1);}
					} else {
						if(p != updatedPlayer) {teamMembersBoard.getScore(p.getName()).setScore((int) (p.getHealth()/2));
						}else {teamMembersBoard.getScore(updatedPlayer.getName()).setScore(health/2);}
					}
				}
			}catch(NullPointerException np) {continue;}
		}
		for(Player p : players){
			try {
				if(p.isOnline()) {
					p.setScoreboard(teamBoard);	
				}
			}catch(NullPointerException np) {continue;}
		}
	}
	
	/**
	 * Removes scoreboard from player when they quit the team.
	 *
	 * @param Player object to remove the board from.
	 */
	public void removeScoreboard(Player player) {
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
	}
	
}
