package me.cutrats110;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class TeamBoards {
	public void makeScoreBoard () {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();	
				
		Objective teamMembersBoard = board.registerNewObjective("teamhp", "health","hp");	
		teamMembersBoard.setDisplaySlot(DisplaySlot.SIDEBAR);
		teamMembersBoard.setDisplayName("  Team Health  ");

		for(Player player : teamMembers()) {
			teamMembersBoard.getScore(ChatColor.GREEN + player.getName()+" - HP").setScore((int) player.getHealth());
		}
		
		Objective teamNameBoard = board.registerNewObjective("teamName", "dummy","name");		
		teamNameBoard.setDisplaySlot(DisplaySlot.BELOW_NAME);
		teamNameBoard.setDisplayName(getTeamName("Futurama2!"));
		teamNameBoard.getScore("teamname").setScore(teamSize("Team name"));
		
		for(Player player : teamMembers()){//Team members only...
			player.setScoreboard(board);
		}
		
		
		//Team class tut: https://bukkit.org/threads/team-systems.411790/

	}
	private List<Player> teamMembers() {
		List<Player> players = new ArrayList<Player>();
		for(Player online : Bukkit.getOnlinePlayers()){//Team members only...
			  players.add(online);
		}
		return players;
		
	}
	private int teamSize(String teamName) {
		return 5;
	}
	private String getTeamName(String playerUUID) {
		return playerUUID;
	}
}