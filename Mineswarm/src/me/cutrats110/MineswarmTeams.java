package me.cutrats110;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
//It is POSSIBLE a medic could TP to a downed player and save them, or a downed player could TP to a medic/team... Considering if this is a bug of feature...
//Note, I may also change how player is selected, if an arg is given to TP to a specific player, maybe give them a heads up?
//Maybe give them an option to deny or block it? (if so, just get sender.uid, lookup and cancel event.
//For random player it may be nice to allow cancel or tell the player someone will be TPing to them shortly
//On one hand this makes sure they aren't jumping off a cliff or running near lava, but also gives them a heads up to get ready
//Allowing the player to exploit the system potentially...
// *^createTeam (name, overload-player) Method
// *^joinTeam Method (name, player)
// *^leaveTeam Method (name, player)
// *^getTeamOwner(teamname) function
// *^setTeamOwner(player) method
// *^inTeam(teamname) function
// *^getTeamMembers(teamname) function
// *^kickTeamMember(player) method
// *^tpaRequest(teamname) method

// tapRequest(teamname)
// USE BELOW CODE to auto generate players you can TPA to, blank will request
// entire team.
// Remember to add massive CD after taking damage or moving (maybe, 10 seconds?)
// @EventHandler
// public void onChatTab(PlayerChatTabCompleteEvent event){
// Collection<String> names = event.getTabCompletions();
// names.add("string");
// names.add("anotherstring");
// 



//Major TODO 
// Add back all functionality that was taken out with MSTeams being added,
// Add persistant teams with database (removed, shorta)
public class MineswarmTeams {
	public Plugin plugin;

	public HashMap<UUID, BukkitTask> tpQueue = new HashMap<>();
	
	/**
	 * Holds a running list of all active teams using the String team name as lookup value
	 *
	 * @return Contains a list of MSTeam objects
	 */
	private HashMap<String, MSTeam> closedTeams = new HashMap<String, MSTeam>();
	private ArrayList<MSTeam> openTeams = new ArrayList<MSTeam>();
	private HashMap<UUID, MSTeam> players = new HashMap<UUID, MSTeam>();
	
	
	
	// Player object, team they are a member of
//	private HashMap<UUID, String> players = new HashMap<>();
	// Players name, UUID of player
	private HashMap<String, UUID> UUIDLookup = new HashMap<>();
	// Player name, team name (assumes all requests are to join)
	private HashMap<String, MSTeam> joinRequests = new HashMap<String, MSTeam>();
	//Server based teams. 
	private List<UUID> servers = new ArrayList<>();
	
	public TeamBoards board;
	private boolean debugging = false;
	private Database db =null;
	
	public boolean loadTeamData() 
	{

		//		teams = db.getTeams();
		//TODO load team data from database.
		/*
		if(teams.size() <= 0) {
			teams = new HashMap<>();
		}
		players = db.getPlayers();
		if(players.size() <= 0) {
			players = new HashMap<>();
		}
		joinRequests = db.getJoinRequests();
		if(joinRequests.size() <= 0) {
			joinRequests = new HashMap<>();
		}
		*/
		UUIDLookup = db.getUUIDLookup();
		if(UUIDLookup.size() <= 0) {
			UUIDLookup = new HashMap<>();
		}
		servers = db.getServers();
		if(servers.size() <= 0) {
			servers = new ArrayList<>();
		}
		
		db.clearTeamsTables();
		return true;
	}

	public boolean saveTeamData() {
		//db.saveTeams(teams);
		//TODO save team data in database
//		db.saveJoinRequests(joinRequests);
//		db.savePlayers(players);
		db.saveUUIDLookup(UUIDLookup);
		db.saveServers(servers);
		return true;
	}

	public MineswarmTeams(Plugin instance, TeamBoards board) {
		this.plugin = instance;
		this.board = board;
		this.db = new Database(plugin);
		loadTeamData();
		//Select * from table X... get CSV back from DB
	}

	
	
	/**
	 * Creates a team instance without a specified owner, rather a random UUID saved
	 * in servers list
	 *
	 * @return Returns MSTeam if team is successfully created otherwise null.
	 * @see createTeam
	 */
	public MSTeam createOpenTeam() {
		try {
			String name = java.util.UUID.randomUUID().toString();
			
			servers.add(UUID.fromString(name));
			MSTeam newTeam = new MSTeam(name, false);
			openTeams.add(newTeam);
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
					newTeam.newOwner(playerID);
					newTeam.addMember(playerID);
					
					closedTeams.put(name, newTeam);
					players.put(player.getUniqueId(), newTeam);

					player.setMetadata("team_name", new FixedMetadataValue(plugin, name));
					if (plugin.getConfig().getBoolean("creating-team-takes-to-spawn")) {
						player.teleport(player.getWorld().getSpawnLocation());
					}
					try {
						if(player.hasMetadata("team_name") && player.getMetadata("team_name").get(0).toString().length() >= 1) {
					    	board.makeScoreBoard(player.getMetadata("team_name").get(0).asString());
					    	board.setScoreboard(newTeam.getMembersPlayerObjects(), player.getMetadata("team_name").get(0).asString());
					    }
					    else {
					    	if(debugging){plugin.getLogger().info("team_name NOT SET");}
					    }
					}catch(NullPointerException np) {}
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
		updateTeamInfo(player, createOpenTeam());
		player.sendMessage("You have joined a server-owned team!");
		return true;
	}
	private boolean updateTeamInfo(Player player, MSTeam team) {
		UUID playerID = player.getUniqueId();
		
		players.put(playerID, team);
		team.addMember(playerID);
		players.put(player.getUniqueId(), team);
		player.setMetadata("team_name", new FixedMetadataValue(plugin, team.getName()));
		try {
			if(player.hasMetadata("team_name") && player.getMetadata("team_name").get(0).toString().length() >= 1) {
		    	board.makeScoreBoard(player.getMetadata("team_name").get(0).asString());
		    	board.setScoreboard(team.getMembersPlayerObjects(), player.getMetadata("team_name").get(0).asString());
		    }
		    else {
		    	if(debugging){plugin.getLogger().info("team_name NOT SET");}
		    }	
		}catch(NullPointerException np) {}
		catch(Exception err) {
			plugin.getLogger().info("Problem joining team" + err.toString());
			return false;
		}
		return true;
	}
/*
	public void addTPAQue(Player player) {
		tpQueue.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			List<Player> onlineTeamMates = new ArrayList<>();
			for (Player teamate : getTeamMembers(player.getMetadata("team_name").get(0).asString())) {
				try {
					if (teamate.isOnline() && !(teamate.equals(player))) {
						onlineTeamMates.add(teamate);
					}
				} catch (NullPointerException np) {
					continue;
				}
			}
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
			try {
				Player to = getPlayerByUUID(UUIDLookup.get(toPlayer));
				if (isTeamMember(to, player.getMetadata("team_name").get(0).asString())) {
					if (to.isOnline()) {
						player.teleport(to);
					}
				} else {
					player.sendMessage("That player is not part of your team.");
				}
			} catch (NullPointerException np) {
				// Player is offline...
				player.sendMessage("That player is offline now, you can only TP to online players");
			}
			tpQueue.get(player.getUniqueId()).cancel();
			tpQueue.remove(player.getUniqueId());
		}, 300, 200));// Time to wait before first call, time to wait for repeating calls (which
						// should never happen)
		return true;
	}
	*/

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
					teammate.sendMessage(message);
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
						player.sendMessage(message);
					}
				} catch (NullPointerException err) {
					continue;
				}
			}
		}
	}

	public void addUUID(Player player) {// Gets called on join event...
		UUIDLookup.put(player.getName(), player.getUniqueId());
	}

	public Player getPlayerByUUID(UUID id) {
		return Bukkit.getPlayer(id);
	}

	public Player getPlayerByName(String name) {
		return Bukkit.getPlayer(UUIDLookup.get(name));
	}

	public boolean joinRequest(Player player, String team_name) {
		//TODO what happens when owner is offline?
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
		//TODO what happens if player being added to team is offline?
		MSTeam team = joinRequests.get(playerName);
		if(team != null) {
			if(team.getOwner() == player.getUniqueId()) {
				updateTeamInfo(Bukkit.getPlayer(playerName), team);
				joinRequests.remove(playerName);
				return true;
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
		//TODO what happens if player being added to team is offline?
		MSTeam team = joinRequests.get(playerName);
		if(team != null) {
			if(team.getOwner() == player.getUniqueId()) {
				joinRequests.remove(playerName);
				Bukkit.getPlayer(playerName).sendMessage("Team Join Request was denied.");
				return true;
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
		
	private boolean updateRemoveTeamInfo(Player player, MSTeam team) {
		
		//TODO removing player from team only updates player who left's scoreboard, other players not updated.
		players.remove(player.getUniqueId());
		
		player.sendMessage("You have left the team");
		try {
			if(player.hasMetadata("team_name") && player.getMetadata("team_name").get(0).toString().length() >= 1) {
		    	board.makeScoreBoard(player.getMetadata("team_name").get(0).asString());
		    	board.setScoreboard(team.getMembersPlayerObjects(), player.getMetadata("team_name").get(0).asString());
		    }
		    else {
		    	if(debugging){plugin.getLogger().info("team_name NOT SET");}
		    }	
		}catch(NullPointerException np) {}
		catch(Exception err) {
			plugin.getLogger().info("Problem joining team" + err.toString());
		}
		
		player.setMetadata("team_name", new FixedMetadataValue(plugin, ""));
		if (plugin.getConfig().getBoolean("leaving-team-takes-to-spawn")) {
			player.teleport(player.getWorld().getSpawnLocation());
		}					
		board.removeScoreboard(player);

		return true;
		
	}
	public boolean leaveTeam(Player player) {
		MSTeam team = players.get(player.getUniqueId());
		if(team != null) {
			if(team.getOwner() == player.getUniqueId()) {//Owner is leaving
				//Transfer owner
				team.newOwner();
				updateRemoveTeamInfo(player, team);
				return true;
			}
			else {
				//Leave..
				updateRemoveTeamInfo(player, team);
				return true;
			}
		} else {
			player.sendMessage("You are not part of a team.");
			return false;
		}
	}
	/**
	 * Finds team owner from all active teams.
	 *
	 * @param Name of the team
	 * @return Returns Player object (owner) if player is null the server owns the
	 *         team
	 * @see getTeamOwner
	 */
	/*
	 * public Player getTeamOwner(String team) {
		return Bukkit.getPlayer(teams.get(team).get(0));
	}
	*/

	/**
	 * Returns if Player (player) is part of team (TeamName).
	 *
	 * @param Player player you want to check if is in a team
	 * @param String teamName the team to check if player is part of
	 * @return Returns true if player is a part of the team, otherwise false.
	 * @see isTeamMember
	 */
	/*public boolean isTeamMember(Player player, String teamName) {
		if (teams.get(teamName).contains(player.getUniqueId())) {
			return true;
		}
		return false;
	}
	*/

	/**
	 * Gets list of Player in a team.
	 *
	 * @param String teamName the team to return members of
	 * @return Returns List Object of Players (object) of team members for given
	 *         team name.
	 * @see getTeamMembers
	 */
	/*
	public List<Player> getTeamMembers(String teamName) {
		List<Player> members = new ArrayList<>();
		for (UUID playerID : teams.get(teamName)) {
			if (getPlayerByUUID(playerID) != null) {
				members.add(getPlayerByUUID(playerID));
			}
		}
		return members;
	}
	/**
	 * Gets list of Players names in a team.
	 *
	 * @param String teamName the team to return members of
	 * @return Returns List Object of names (object) of team members for given
	 *         team name.
	 * @see getTeamMembersNames
	 */
	/*
	public List<String> getTeamMembersNames(String teamName) {
		List<String> members = new ArrayList<>();
		for (UUID playerID : teams.get(teamName)) {						
			if(plugin.getServer().getOfflinePlayer(playerID).isOnline()) {
				members.add(plugin.getServer().getOfflinePlayer(playerID).getName() + ChatColor.GREEN +  " - Online");	
			}
			else {
				members.add(plugin.getServer().getOfflinePlayer(playerID).getName() + ChatColor.RED + " - Offline");
			}
		}
		return members;
	}
/*
	public boolean setNewTeamOwner(String teamName) {
		List<UUID> members = teams.get(teamName);
		if (members.size() <= 1) {//Do nothing if only 1 player in team
			if(debugging) {plugin.getLogger().info("Removing team due to lack of players.");}
			teams.remove(teamName);
			return true;
		} else {
			UUID owner = members.get(0);
			members.set(0, members.get(1));
			members.set(1, owner);
		}
			teams.remove(teamName);
			teams.put(teamName, members);
			if(debugging) {plugin.getLogger().info("Moved team so : " + plugin.getServer().getOfflinePlayer(teams.get(teamName).get(0)).getName());}
		return true;
	}

	public boolean setNewTeamOwner(String teamName, String newOwnerName) {
		Player newOwner = Bukkit.getPlayer(UUIDLookup.get(newOwnerName));
		List<UUID> members = teams.get(teamName);
		members.add(newOwner.getUniqueId());
		for (UUID player : members) {
			if (newOwner.getUniqueId().equals(player)) {
				continue;
			} else {
				members.add(player);
			}
		}
		teams.put(teamName, members);
		return true;
	}
	*/
	/*
	public boolean kickTeamMember(String kicky, Player kicker) {
		//Ask if the kicker is the team owner of the team they are on.
		if(getTeamOwner(players.get(kicker.getUniqueId())).equals(kicker)) {	
			//Get if kicky is on the same team as kicker.
			if (isTeamMember(Bukkit.getPlayer(UUIDLookup.get(kicky)), players.get(kicker.getUniqueId()))) {
				// Kicker is team owner and kicky is a member of the kicker's team.
				leaveTeam(Bukkit.getPlayer(UUIDLookup.get(kicky)));// Execute the leave function on behalf of kicked player.
				try {
					if(kicker.hasMetadata("team_name") && kicker.getMetadata("team_name").get(0).toString().length() >= 1) {
				    	board.makeScoreBoard(kicker.getMetadata("team_name").get(0).asString());
				    	board.setScoreboard(getTeamMembers(kicker.getMetadata("team_name").get(0).asString()), kicker.getMetadata("team_name").get(0).asString());
				    }
				    else {
				    	if(debugging){plugin.getLogger().info("team_name NOT SET");}
				    }	
				}catch(NullPointerException np) {}
				catch(Exception err) {
					plugin.getLogger().info("Problem joining team" + err.toString());
				}
				
				board.removeScoreboard(Bukkit.getPlayer(UUIDLookup.get(kicky)));
				return true;
			}
			else {
				kicker.sendMessage("That player is not on your team");
			}
		}else {
			kicker.sendMessage("You are not listed as the team owner");
		}
		return false;
	}
	*/
}
