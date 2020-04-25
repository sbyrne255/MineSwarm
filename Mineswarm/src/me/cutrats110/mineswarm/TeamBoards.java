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
	MineswarmTeams teams;
	public TeamBoards(Plugin plugin){
		this.plugin = plugin;
	}
	public void setTeamData(EventListenerHelper helper, MineswarmTeams teams) {
		this.helper = helper;
		this.teams = teams;
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
	public void createScoreBoard (String teamName) {
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
	public void updateScoreBoard(List<Player> players, String teamName, Player updatedPlayer) {
		try {
			Scoreboard teamBoard = boards.get(teamName);
			Objective teamMembersBoard = teamBoard.getObjective("teamhp");

			try{
				if(updatedPlayer.isOnline()) {
					updatedPlayer.setScoreboard(teamBoard);
					if(helper.isPlayerDown(updatedPlayer)) { teamMembersBoard.getScore(updatedPlayer.getName()).setScore((int) ((updatedPlayer.getHealth()/2) *-1)); } 
					else { teamMembersBoard.getScore(updatedPlayer.getName()).setScore((int) (updatedPlayer.getHealth()/2)); }
				}
			}
			catch(NullPointerException npe) { plugin.getLogger().warning("Null value present in setScoreboard function (1a). " +npe.toString()); }
			catch(IllegalArgumentException iae) { plugin.getLogger().warning("Illegal Argument present in setScoreboard function (1a). " +iae.toString()); }
			catch(IllegalStateException ise) { plugin.getLogger().warning("Illegal State in setScoreboard function, objective has been unregistered (1a). " +ise.toString()); }
			
			for(Player p : players){
				try{					
					if(p != null && p.isOnline()) {
						if(p == updatedPlayer) {continue;}
						p.setScoreboard(teamBoard);
						if(helper.isPlayerDown(p)) { teamMembersBoard.getScore(p.getName()).setScore((int) ((p.getHealth()/2) *-1)); } 
						else { teamMembersBoard.getScore(p.getName()).setScore((int) (p.getHealth()/2)); }
					}
				}
				catch(NullPointerException npe) { plugin.getLogger().warning("Null value present in setScoreboard function (2a). " +npe.toString()); }
				catch(IllegalArgumentException iae) { plugin.getLogger().warning("Illegal Argument present in setScoreboard function (2a). " +iae.toString()); }
				catch(IllegalStateException ise) { plugin.getLogger().warning("Illegal State in setScoreboard function, objective has been unregistered (2a). " +ise.toString()); }
			}
		}
		catch(NullPointerException npe) { plugin.getLogger().warning("Null value present in setScoreboard function (3a). " +npe.toString()); }
		catch(IllegalArgumentException iae) { plugin.getLogger().warning("Illegal Argument present in setScoreboard function (3a). " +iae.toString()); }
		catch(IllegalStateException ise) { plugin.getLogger().warning("Illegal State in setScoreboard function, objective has been unregistered (3a). " +ise.toString()); }
	}
	
	
	/**
	 * Updates scoreboard for specific team and all its members.
	 *
	 * @param List of Players on the team
	 * @param String, team name
	 * @return void
	 * @see setScoreboard(List<Player> players, String teamName, Player updatedPlayer, int health)
	 */
	public void updateScoreBoard(List<Player> players, String teamName) {
		try {
			plugin.getLogger().info("trying for team name: "+teamName);
			Scoreboard teamBoard = boards.get(teamName);
			Objective teamMembersBoard = teamBoard.getObjective("teamhp");
			for(Player p : players){
				try{
					if(p != null && p.isOnline()) {
						p.setScoreboard(teamBoard);
						if(helper.isPlayerDown(p)) { teamMembersBoard.getScore(p.getName()).setScore((int) ((p.getHealth()/2) *-1)); } 
						else { teamMembersBoard.getScore(p.getName()).setScore((int) (p.getHealth()/2)); }; 
					}
				}
				catch(NullPointerException npe) { plugin.getLogger().warning("Null value present in setScoreboard function (2d). " +npe.toString()); }
				catch(IllegalArgumentException iae) { plugin.getLogger().warning("Illegal Argument present in setScoreboard function (2d). " +iae.toString()); }
				catch(IllegalStateException ise) { plugin.getLogger().warning("Illegal State in setScoreboard function, objective has been unregistered (2d). " +ise.toString()); }
			}
		}
		catch(NullPointerException npe) { plugin.getLogger().warning("Null value present in setScoreboard function (3d). " +npe.toString()); }
		catch(IllegalArgumentException iae) { plugin.getLogger().warning("Illegal Argument present in setScoreboard function (3d). " +iae.toString()); }
		catch(IllegalStateException ise) { plugin.getLogger().warning("Illegal State in setScoreboard function, objective has been unregistered (3d). " +ise.toString()); }
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
		try {
			Scoreboard teamBoard = boards.get(teamName);
			Objective teamMembersBoard = teamBoard.getObjective("teamhp");

			try{
				if(updatedPlayer.isOnline()) {
					updatedPlayer.setScoreboard(teamBoard);
					if(helper.isPlayerDown(updatedPlayer)) { teamMembersBoard.getScore(updatedPlayer.getName()).setScore((int) ((health/2) *-1)); } 
					else { teamMembersBoard.getScore(updatedPlayer.getName()).setScore((int) (health/2)); }
				}
			}
			catch(NullPointerException npe) { plugin.getLogger().warning("Null value present in setScoreboard function (1). " +npe.toString()); }
			catch(IllegalArgumentException iae) { plugin.getLogger().warning("Illegal Argument present in setScoreboard function (1). " +iae.toString()); }
			catch(IllegalStateException ise) { plugin.getLogger().warning("Illegal State in setScoreboard function, objective has been unregistered (1). " +ise.toString()); }
			
			for(Player p : players){
				try{
					if(p != null && p.isOnline()) {
						p.setScoreboard(teamBoard);
						if(p.equals(updatedPlayer)) {continue;}
						if(helper.isPlayerDown(p)) { teamMembersBoard.getScore(p.getName()).setScore((int) ((p.getHealth()/2) *-1)); } 
						else { teamMembersBoard.getScore(p.getName()).setScore((int) (p.getHealth()/2)); }
					}
				}
				catch(NullPointerException npe) { plugin.getLogger().warning("Null value present in setScoreboard function (2). " +npe.toString()); }
				catch(IllegalArgumentException iae) { plugin.getLogger().warning("Illegal Argument present in setScoreboard function (2). " +iae.toString()); }
				catch(IllegalStateException ise) { plugin.getLogger().warning("Illegal State in setScoreboard function, objective has been unregistered (2). " +ise.toString()); }
			}
		}
		catch(NullPointerException npe) { plugin.getLogger().warning("Null value present in setScoreboard function (3). " +npe.toString()); }
		catch(IllegalArgumentException iae) { plugin.getLogger().warning("Illegal Argument present in setScoreboard function (3). " +iae.toString()); }
		catch(IllegalStateException ise) { plugin.getLogger().warning("Illegal State in setScoreboard function, objective has been unregistered (3). " +ise.toString()); }
	}
	
	/**
	 * Removes scoreboard from player when they quit the team.
	 *
	 * @param Player object to remove the board from.
	 */
	public void removeScoreboard(Player player, MSTeam team) {
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		createScoreBoard(team.getName());
		updateScoreBoard(team.getMembersPlayerObjects(), team.getName());
	}
	
}
