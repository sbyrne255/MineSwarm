package me.cutrats110.mineswarm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
//It is POSSIBLE a medic could TP to a downed player and save them, or a downed player could TP to a medic/team... Considering if this is a bug of feature...
//Note, I may also change how player is selected, if an arg is given to TP to a specific player, maybe give them a heads up?
//Maybe give them an option to deny or block it? (if so, just get sender.uid, lookup and cancel event.
//For random player it may be nice to allow cancel or tell the player someone will be TPing to them shortly
//On one hand this makes sure they aren't jumping off a cliff or running near lava, but also gives them a heads up to get ready
//Allowing the player to exploit the system potentially...
import java.sql.ResultSet;
import java.sql.SQLException;

//Major TODO 
// Add persistant teams with database (removed, shorta)
public class MineswarmTeams {
	public Plugin plugin;
	public HashMap<UUID, BukkitTask> tpQueue = new HashMap<>();
	
	public MineswarmTeams(Plugin instance, TeamBoards board) {
		this.plugin = instance;
		this.board = board;
		this.db = new Database(plugin);
	}
	
	/**
	 * Holds a running list of all active teams using the String team name as lookup value
	 *
	 * @return Contains a list of MSTeam objects
	 */
	private HashMap<String, MSTeam> closedTeams = new HashMap<String, MSTeam>();
	private ArrayList<MSTeam> openTeams = new ArrayList<MSTeam>();
	private HashMap<UUID, MSTeam> players = new HashMap<UUID, MSTeam>();
	private HashMap<String, MSTeam> joinRequests = new HashMap<String, MSTeam>();
	
	private TeamBoards board;
	private Database db =null;
	
	public boolean loadTeamData() 
	{
		Connection teamsConn = db.getTeamsConnection();
    	if(teamsConn == null) {
    		plugin.getLogger().info("Connection returned null.");
    		return false;
    	}
    	String sql = "SELECT name, owner, closed, score, rowid as id FROM teams";
    	try {
        	PreparedStatement pstmt = teamsConn.prepareStatement(sql);
        	HashMap<Integer, MSTeam> teams = new HashMap<Integer, MSTeam>();
        	
            ResultSet rs = pstmt.executeQuery();
            UUID id;
            while (rs.next()) {
            	try{id = UUID.fromString(rs.getString("owner"));}
            	catch (IllegalArgumentException iae) {id = UUID.fromString("053573d9-85a4-4b1a-80b3-c86d08071f24");}
            	catch (NullPointerException npr) {id = UUID.fromString("053573d9-85a4-4b1a-80b3-c86d08071f24");}
            		
            	MSTeam team = new MSTeam(rs.getString("name"),id, rs.getBoolean("closed"), rs.getInt("score"));
            	if(rs.getBoolean("closed")) {
            		closedTeams.put(team.getName(), team);
            	}
            	else {
            		openTeams.add(team);
            	}
            	teams.put(rs.getInt("id"), team);
            	plugin.getLogger().info("Adding board for team: " + team.getName());
            	board.createScoreBoard(team.getName());
            }
            
            sql = "SELECT * FROM members";
            pstmt = teamsConn.prepareStatement(sql);       	
            rs = pstmt.executeQuery();
            while (rs.next()) {
            	MSTeam cur_team = teams.get(rs.getInt("team_id"));
            	UUID cur_player = UUID.fromString(rs.getString("member"));
            	cur_team.addMember(cur_player);
            	players.put(cur_player, cur_team);
            }
            
        } catch (SQLException e) {plugin.getLogger().info("ERROR GETTING TEAMS OR MEMBERS: " + e.getMessage());}
    	finally{ try {teamsConn.close();} catch (SQLException e) {} }		
    	
		return true;
	}

	public boolean saveTeamData() {
		db.emptyTeamsTable();
		
    	Connection teamsConn = db.getTeamsConnection();
    	if(teamsConn == null) {
    		plugin.getLogger().info("Connection returned null.");
    		return false;
    	}
		for (Map.Entry<String, MSTeam> team : closedTeams.entrySet()) {
			try {
				String sql = "INSERT INTO teams(name, owner, closed, score) VALUES(?,?,?,?)";
				PreparedStatement pstmt = teamsConn.prepareStatement(sql);
				pstmt.setString(1, team.getValue().getName());
				pstmt.setString(2, team.getValue().getOwner().toString());
				pstmt.setBoolean(3, team.getValue().isClosed());
				pstmt.setInt(4, team.getValue().getScore());
				pstmt.executeUpdate();
			}catch(Exception err) {plugin.getLogger().info("Problem inserting closed team data " + err.toString());}	
			
			int team_id = db.getLastID(teamsConn);
			for(UUID playerID : team.getValue().getMembers()) {
				try {
					String sql = "INSERT INTO members(member, team_id) VALUES(?,?)";
					PreparedStatement pstmt = teamsConn.prepareStatement(sql);
					pstmt.setString(1, playerID.toString());
					pstmt.setInt(2, team_id);
					pstmt.executeUpdate();
				}catch(Exception err) {plugin.getLogger().warning("Problem inserting closed team members " + err.toString());}
			}
		}
		for (MSTeam team : openTeams) {
			try {
				String sql = "INSERT INTO teams(name, closed, score) VALUES(?,?,?)";
				PreparedStatement pstmt = teamsConn.prepareStatement(sql);
				pstmt.setString(1, team.getName());
				pstmt.setBoolean(2, team.isClosed());
				pstmt.setInt(3, team.getScore());
				pstmt.executeUpdate();
			}catch(Exception err) {plugin.getLogger().warning("Problem inserting open team data " +err.toString());}	
			
			int team_id = db.getLastID(teamsConn);
			for(UUID playerID : team.getMembers()) {
				try {
					String sql = "INSERT INTO members(member, team_id) VALUES(?,?)";
					PreparedStatement pstmt = teamsConn.prepareStatement(sql);
					pstmt.setString(1, playerID.toString());
					pstmt.setInt(2, team_id);
					pstmt.executeUpdate();
				}catch(Exception err) {plugin.getLogger().info("Problem inserting open team members " +err.toString());}
			}
		}
		
		return true;
	}

	
	
	/**
	 * Creates a team instance without a specified owner, rather a random UUID saved
	 * in servers list
	 *
	 * @return Returns MSTeam if team is successfully created otherwise null.
	 * @see createTeam
	 */
	public MSTeam createOpenTeam(Player player) {
		try {
			String name = java.util.UUID.randomUUID().toString();
			
			MSTeam newTeam = new MSTeam(name, false);
			openTeams.add(newTeam);
			try {
			    board.createScoreBoard(name);
			    board.updateScoreBoard(newTeam.getMembersPlayerObjects(), name, player);
			}
			catch(NullPointerException npe) { plugin.getLogger().warning("Null value present in setScoreboard function (1b). " +npe.toString()); }
			catch(IllegalArgumentException iae) { plugin.getLogger().warning("Illegal Argument present in setScoreboard function (1b). " +iae.toString()); }
			catch(IllegalStateException ise) { plugin.getLogger().warning("Illegal State in setScoreboard function, objective has been unregistered (1b). " +ise.toString()); }
			catch(Exception err) {
				plugin.getLogger().info("Problem making team " + err.toString());
			}
			return newTeam;
		} catch (Exception err) {
			plugin.getLogger().info("Problem with making server team: " + err.toString());
		}
		return null;
	}

	
	public MSTeam getTeam(Player player) {
		MSTeam team = players.get(player.getUniqueId());
		return team;
	}
	/**
	 * Creates a team instance with a specified owner; if player owner is null that
	 * means server is owner and team is random/public team
	 *
	 * @param Name  of the team (how this team will be identified)
	 * @param Owner of team, server will be a null owner.
	 * @return Returns true if team is successfully created.
	 * @see createTeam
	 */
	public boolean createClosedTeam(String name, Player player) {
		try {
			//Team name is available
			if(closedTeams.get(name) == null) {
				//Player is not in a team
				UUID playerID = player.getUniqueId();
				if (players.get(playerID) == null) {
					
					MSTeam newTeam = new MSTeam(name);
					newTeam.addMember(playerID);
					newTeam.newOwner(playerID);
					
					closedTeams.put(name, newTeam);
					players.put(player.getUniqueId(), newTeam);

					if (plugin.getConfig().getBoolean("creating-team-takes-to-spawn")) {
						player.teleport(player.getWorld().getSpawnLocation());
					}
					try {
					    board.createScoreBoard(name);
					    board.updateScoreBoard(newTeam.getMembersPlayerObjects(), name, player);
					}
					catch(NullPointerException npe) { plugin.getLogger().warning("Null value present in setScoreboard function (1b). " +npe.toString()); }
					catch(IllegalArgumentException iae) { plugin.getLogger().warning("Illegal Argument present in setScoreboard function (1b). " +iae.toString()); }
					catch(IllegalStateException ise) { plugin.getLogger().warning("Illegal State in setScoreboard function, objective has been unregistered (1b). " +ise.toString()); }
					catch(Exception err) {
						plugin.getLogger().info("Problem making team " + err.toString());
					}
					return true;
				}
				else {
					player.sendMessage("You cannot join a team without first leaving the team you are in.");
					return false;
				}
				
			}else
			{
				player.sendMessage("Team name already exists, please try something else");
				return false;
			}
		} catch (Exception err) {
			plugin.getLogger().info("Problem with making team: " + err.toString());
		}
		return false;
	}
	public MSTeam getTeamByName(String name) {
		MSTeam team = closedTeams.get(name);
		if(team == null) {
			for (MSTeam oteam : openTeams) {
				if(oteam.getName() == name) {
					return oteam;
				}
			}
			return null;
		}
		else {return team;}		
	}
	public boolean joinRandom(Player player) {		
		for(MSTeam team: openTeams) {
			if(team.getMembers().size()<= plugin.getConfig().getInt("max-team-size")+1 ) {
				//Join this team
				updateTeamInfo(player, team);
				openTeams.add(team);
				player.sendMessage("You have joined a server-owned team!");
				return true;
			}
		}
		updateTeamInfo(player, createOpenTeam(player));
		player.sendMessage("You have joined a server-owned team!");
		return true;
	}
	public ArrayList<String> getMemberNames(Player player){
		MSTeam team = players.get(player.getUniqueId());
		if(team != null) {
			return team.getMembersNames();
		}
		else {return null;} 
	}
	
	private boolean updateTeamInfo(Player player, MSTeam team) {
		UUID playerID = player.getUniqueId();
		
		players.put(playerID, team);
		team.addMember(playerID);
		players.put(player.getUniqueId(), team);
		board.updateScoreBoard(team.getMembersPlayerObjects(), team.getName(), player);
		return true;
	}
	public void addRandomTPAQue(Player player) {
		tpQueue.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			MSTeam team = players.get(player.getUniqueId());
			List<Player> onlineTeamMates = team.getOnlineMembersPlayerObjects();
			onlineTeamMates.remove(player);
			
			if (onlineTeamMates.size() <= 0) {
				player.sendMessage("No teamate are online but you");
			} else {
				player.teleport(onlineTeamMates.get(ThreadLocalRandom.current().nextInt(0, onlineTeamMates.size())));
			}
			
			tpQueue.get(player.getUniqueId()).cancel();
			tpQueue.remove(player.getUniqueId());

		}, 300, 200));
	}
	public boolean addTPAQue(Player player, String toPlayer) {
		tpQueue.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			Player to = Bukkit.getPlayer(toPlayer);
			if(to != null && to.isOnline()) {
				MSTeam team = players.get(player.getUniqueId());
				if(team.getMembersPlayerObjects().contains(to)) {
					player.teleport(to);
				} else {
					player.sendMessage("Player is not on your team.");
				}
			} else {player.sendMessage("Player is offline or could not be found.");}
			tpQueue.get(player.getUniqueId()).cancel();
			tpQueue.remove(player.getUniqueId());
		}, 300, 200));// Time to wait before first call, time to wait for repeating calls (which
		return true;
	}

	public void alertTeamOfDowns(String teamName, Player downedPlayer) {
		MSTeam team = players.get(downedPlayer.getUniqueId());
		if(team != null) {
			List<Player> teamMates = team.getMembersPlayerObjects();
			for (Player player : teamMates) {
				try {
					if (player.isOnline() && player != downedPlayer) {
						player.sendMessage(ChatColor.RED + downedPlayer.getName() + " IS DOWN");
					}
				} catch (NullPointerException err) {
					continue;
				}
			}
		}
	}
	
	public void sendTeamMessage(Player player, String message) {
		MSTeam team = players.get(player.getUniqueId());
		List<Player> teamMates = team.getMembersPlayerObjects();
		for (Player teammate : teamMates) {
			try {
				if (teammate.isOnline()) {
					teammate.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + message);
				}
			} catch (NullPointerException err) {
				continue;
			}
		}
	}
	public void sendTeamMessage(String message, Player exclude) {
		MSTeam team = players.get(exclude.getUniqueId());
		if(team != null) {
			for(Player player : team.getMembersPlayerObjects()) {
				try {
					if (player.isOnline() && player != exclude) {
						player.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + message);
					}
				} catch (NullPointerException err) {
					continue;
				}
			}
		}
	}

	public Player getPlayerByUUID(UUID id) {
		return Bukkit.getPlayer(id);
	}
	public boolean joinRequest(Player player, String team_name) {
		if(players.get(player.getUniqueId()) == null) {
			MSTeam team = closedTeams.get(team_name);
			if(team != null) {
				if(team.getMembers().size() < plugin.getConfig().getInt("max-team-size")) {
					try {
						Bukkit.getPlayer(team.getOwner()).sendMessage("Player " + player.getName()
								+ " is requesting to join your team. /msteam add " + player.getName()
								+ " to add the player or /msteam deny " + player.getName() + " to deny.");
					} catch (Exception er) {
						plugin.getLogger().info("FICL OFF " + er.toString());
					}
					joinRequests.put(player.getName(), team);
					player.sendMessage("Join request sent, waiting for approval...");
					return true;	
				} else {
					player.sendMessage("Team is full.");
					return false;
				}
				
			} else {
				player.sendMessage("Team does not exist");
				return false;
			}
		}
		else
		{
			player.sendMessage("You are already part of a team, leave that team before joining a new one");
			return false;
		}
	}

	public boolean joinTeamAccept(Player player, String playerName) {
		MSTeam team = joinRequests.get(playerName);
		if(team != null) {
			if(team.getOwner() == player.getUniqueId()) {
				Player joinee = Bukkit.getPlayer(playerName);
				if(joinee != null && joinee.isOnline()) {
					updateTeamInfo(Bukkit.getPlayer(playerName), team);
					joinRequests.remove(playerName);
					return true;
				}else {
					player.sendMessage("Player is offline, please try again when they are online.");
					return false;
				}
			}
			else {
				player.sendMessage("You are not the owner of this team.");
				return false;
			}
		} else {
			player.sendMessage("Team no longer exist.");
			return false;
		}
	}

	public boolean joinTeamDeny(Player player, String playerName) {
		MSTeam team = joinRequests.get(playerName);
		if(team != null) {
			if(team.getOwner() == player.getUniqueId()) {
				Player joinee = Bukkit.getPlayer(playerName);
				if(joinee != null && joinee.isOnline()) {
					joinRequests.remove(playerName);
					Bukkit.getPlayer(playerName).sendMessage("Team Join Request was denied.");
					return true;
				}else {
					player.sendMessage("Player is offline, try again when they are online.");
					return false;
				}
			}
			else {
				player.sendMessage("You are not the owner of this team.");
				return false;
			}
		} else {
			player.sendMessage("Team no longer exist.");
			return false;
		}
	}
	
//TODO DOCUMENT FROM HERE UP.
	private boolean updateRemoveTeamInfo(Player player, MSTeam team, Boolean kicked) {
		players.remove(player.getUniqueId());
		if(kicked) {
			player.sendMessage("You were kicked from the team");
		}else {
			player.sendMessage("You have left the team");
		}
		team.removeMember(player.getUniqueId());
		board.updateScoreBoard(team.getMembersPlayerObjects(), team.getName(), player);
		
		if (plugin.getConfig().getBoolean("leaving-team-takes-to-spawn")) {
			player.teleport(player.getWorld().getSpawnLocation());
		}					
		board.removeScoreboard(player, team);
		board.updateScoreBoard(team.getMembersPlayerObjects(), team.getName());

		return true;
		
	}
	/**
	 * Removes player from team assuming they are part of the team. 
	 * Sets new owner if owner is leaving team.
	 * 
	 * @return True if player leaves, returns false if not all criteria is met.
	 */
	public boolean leaveTeam(Player player, Boolean kicked) {
		MSTeam team = players.get(player.getUniqueId());
		if(team != null) {
			if(team.getOwner() == player.getUniqueId()) {//Owner is leaving
				//Transfer owner
				team.newOwner();
				updateRemoveTeamInfo(player, team, kicked);
				//Check team size, if no members exist close team.
				if(team.getMembers().size() <= 0) {closedTeams.remove(team.getName());}
				return true;
			}
			else {
				//Leave..
				updateRemoveTeamInfo(player, team, kicked);
				if(team.getMembers().size() <= 0) {closedTeams.remove(team.getName());}
				return true;
			}
		} else {
			player.sendMessage("You are not part of a team.");
			return false;
		}
	}
	/**
	 * Kicks the specified player if sender is owner and player being kicked is part of the team and not the owner
	 * Player must be online, a member of your team, and sender being the owner.
	 * 
	 * @return True if player is kicked, returns false if not all criteria is met.
	 * @see leaveTeam(Player player, Boolean kicked)
	 */
	public boolean kickTeamMember(String kicky, Player kicker) {
		MSTeam team = players.get(kicker.getUniqueId());
		if(team.getOwner() == kicker.getUniqueId()) {
			if(team.getMembersNames().contains(kicky)) {
				Player kickyPlayer = Bukkit.getPlayer(kicky);
				if(kickyPlayer != null && kickyPlayer.isOnline()) {
					if(kickyPlayer.getUniqueId() != team.getOwner()) {
						leaveTeam(kickyPlayer, true);
						return true;
					}else {
						kicker.sendMessage("yOu CaNnOt KiCk YoUrSeLf...");
						return false;
					}
				}else {
					kicker.sendMessage("Player is offline. Try again when they are online.");
					return false;
				}
			}else {
				kicker.sendMessage("Player is not a member of your team.");
				return false;
			}
		} else {
			kicker.sendMessage("You are not the team owner.");
			return false;
		}
	}
}
