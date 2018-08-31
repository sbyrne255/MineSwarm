package me.cutrats110;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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

public class MineswarmTeams {
	public Plugin plugin;

	public transient final HashMap<UUID, BukkitTask> tpQueue = new HashMap<>();

	// Team name as string, List of players, 0 being team owner.
	private transient HashMap<String, List<UUID>> teams = new HashMap<>();
	// Player object, team they are a member of
	private transient HashMap<UUID, String> players = new HashMap<>();
	// Players name, UUID of player
	private transient HashMap<String, UUID> UUIDLookup = new HashMap<>();
	// Player name, team name (assumes all requests are to join)
	private transient HashMap<String, String> joinRequests = new HashMap<>();
	// Player, team name (assumes all requests are to tp)
	// private HashMap<Player, String> tpRequests = new HashMap<>();//Broadcast
	// request to all team members...//Waiting on thsi because of bukkit
	// requirements to check if they have moved, damaged ect...
	private List<UUID> servers = new ArrayList<>(); 

	/**
	 * Creates a team instance without a specified owner, rather a random UUID saved in servers list
	 *
	 * @return Returns true if team is successfully created.
	 * @see createTeam
	 */
	public boolean createTeam() {
		try {
			String name = java.util.UUID.randomUUID().toString();
			// Team name is available and team owner is NOT part of another team.
			List<UUID> teamCheck = new ArrayList<>();
			teamCheck = teams.get(name);// Get all UUIDs associated with that team name...
			if (teamCheck != null) {
				return false;
			} else {
				if (teams.containsKey(name)) {
					return false;
				} else {
					servers.add(UUID.fromString(name));
					List<UUID> playerList = new ArrayList<>();
					playerList.add(UUID.fromString(name));
					teams.put(name, playerList);
					return true;
				}
			}
		} catch (Exception err) {
			plugin.getLogger().info("Problem with making server team: " + err.toString());
		}
		return false;
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
	public boolean createTeam(String name, Player player) {
		try {
			// Team name is available and team owner is NOT part of another team.
			List<UUID> teamCheck = new ArrayList<>();
			teamCheck = teams.get(name);// Get all UUIDs associated with that team name...
			if (teamCheck != null) {
				player.sendMessage("Team name already exists, please try something else");
				return false;
			} else {
				// Key might be present...
				if (teams.containsKey(name)) {
					player.sendMessage("Team name already exists, please try something else");
					return false;
				} else {
					// Definitely no such key
					plugin.getLogger().info("No team exists, checking player validity");
					String playerTeam = players.get(player.getUniqueId());
					if (playerTeam != null) {
						player.sendMessage("Player is in a team, you must leave before making a new team");
						return false;
					} else {
						// Key might be present...
						if (players.containsKey(player.getUniqueId())) {
							// Okay, there's a key but the value is null
							plugin.getLogger().info("Player was part of a team, left, and is now making a new team...");
							players.put(player.getUniqueId(), name);
							List<UUID> playerList = new ArrayList<>();
							playerList.add(player.getUniqueId());
							teams.put(name, playerList);
							player.setMetadata("team_members", new FixedMetadataValue(plugin, name));
							if(plugin.getConfig().getBoolean("creating-team-takes-to-spawn")) {
								player.teleport(player.getWorld().getSpawnLocation());
							}
							
							return true;
						} else {
							// Make team...
							players.put(player.getUniqueId(), name);
							List<UUID> playerList = new ArrayList<>();
							playerList.add(player.getUniqueId());
							teams.put(name, playerList);
							player.setMetadata("team_members", new FixedMetadataValue(plugin, name));
							if(plugin.getConfig().getBoolean("creating-team-takes-to-spawn")) {
								player.teleport(player.getWorld().getSpawnLocation());
							}
							return true;

						}
					}
				}
			}
		} catch (Exception err) {
			plugin.getLogger().info("Problem with making team: " + err.toString());
		}
		return false;
	}

	public void joinRandom(Player player) {
		List<UUID> membersID;
		String teamName = "";
		int loop = 0;
		//Get team members in server team, 0,1,2,3,ect if the server is full, try the next server team
		try {
			while(teams.get(servers.get(loop).toString()).size() >= plugin.getConfig().getInt("max-team-size")) {
				loop ++;
			}
		}catch(IndexOutOfBoundsException ib) {
			createTeam();
		}finally {
			teamName = servers.get(loop).toString();
		}
		membersID = teams.get(teamName);// All team members UUIDs
		Player requesty = Bukkit.getPlayer(UUIDLookup.get(player.getName()));
		if (requesty != null) {
			membersID.add(requesty.getUniqueId());
			teams.put(teamName, membersID);// Update hashmap with new player added
			players.put(requesty.getUniqueId(), teamName);
			requesty.setMetadata("team_members", new FixedMetadataValue(plugin, teamName));
			requesty.sendMessage("Team request has been accepted!");
		}
	}
	
	
	

	public MineswarmTeams(Plugin instance) {
		this.plugin = instance;
		loadTeamData();
	}

	public void addTPAQue(Player player) {
		tpQueue.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(plugin, ()->{
			List<Player> onlineTeamMates = new ArrayList<>();
			for (Player teamate : getTeamMembers(player.getMetadata("team_members").get(0).asString()) ) {
				try {
					if(teamate.isOnline()) {
						onlineTeamMates.add(teamate);
					}
				}catch(NullPointerException np) {
					continue;
				}
			}
			if(onlineTeamMates.size() < 2) {
				player.sendMessage("No teamate are online but you");
			}else {
				Random rand = new Random();
				player.teleport(onlineTeamMates.get(rand.nextInt(onlineTeamMates.size())));
			}
			tpQueue.get(player.getUniqueId()).cancel();
			tpQueue.remove(player.getUniqueId());
			
		}, 300, 200));	
	}

	public boolean addTPAQue(Player player, String toPlayer) {
		tpQueue.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(plugin, ()->{
			try {
				Player to = getPlayerByUUID(UUIDLookup.get(toPlayer)); 
				if(isTeamMember(to, player.getMetadata("team_members").get(0).asString())) {
					if(to.isOnline()) {
						player.teleport(to);
					}	
				} else {
					player.sendMessage("That player is not part of your team.");
				}
			}catch(NullPointerException np) {
				//Player is offline...
				player.sendMessage("That player is offline now, you can only TP to online players");
			}
			tpQueue.get(player.getUniqueId()).cancel();
			tpQueue.remove(player.getUniqueId());
		}, 300, 200));//Time to wait before first call, time to wait for repeating calls (which should never happen)
		return true;
	}

	public void alertTeamOfDowns(String teamName, Player downedPlayer) {
		List<Player> teamMates = getTeamMembers(teamName);
		for(Player player : teamMates) {
			try {
				if(player.isOnline() && player != downedPlayer) {
					player.sendMessage(ChatColor.RED + downedPlayer.getName() + " IS DOWN");
				}
			}
			catch(NullPointerException err) {continue;}
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

	public boolean joinTeam(Player player, String team) {
		try {
			if (players.containsKey(player.getUniqueId()) && players.get(player.getUniqueId()) != null) {
				player.sendMessage("You are already part of a team, leave that team before joining a new one");
			} else {
				Player teamOwner = null;
				try {
					teamOwner = getPlayerByUUID(teams.get(team).get(0));
					if (!(teamOwner.equals(null))) {// Not a server team
						try {
							teamOwner.sendMessage("Player " + player.getName()
									+ " is requesting to join your team. /msteam add " + player.getName()
									+ " to add the player or /msteam deny " + player.getName() + " to deny.");
						} catch (Exception er) {
							plugin.getLogger().info("FICL OFF " + er.toString());
						}
						joinRequests.put(player.getName(), team);
						player.sendMessage("Join request sent, waiting for approval...");
						return true;
					}
				} catch (NullPointerException np) {
					player.sendMessage("We can't find that team, try a different one");
				} catch (Exception er) {
					plugin.getLogger().info("Error in else:" + er.toString());
				}
			}
		} catch (Exception err) {
			plugin.getLogger().info("Problem adding team join request " + err.toString());
		}
		return true;
	}
//Can't find player...
	public boolean joinTeamAccept(Player sender, String playerName) {
		try {
			List<UUID> membersID = null;
			String teamName = "";
			try {
				teamName = joinRequests.get(playerName);
				membersID = teams.get(teamName);// All team members UUIDs
			} catch (Exception err) {
				plugin.getLogger().info("Problem right away... " + err.toString());
			}
			if (sender.getUniqueId().equals(membersID.get(0)) || membersID.get(0).equals(null)) {// sender is the owner
				Player requesty = Bukkit.getPlayer(UUIDLookup.get(playerName));
				if (requesty != null) {// Found a UUID match
					int maxSize = plugin.getConfig().getInt("max-team-size");
					if (membersID.size() < maxSize) {
						membersID.add(requesty.getUniqueId());
						teams.put(teamName, membersID);// Update hashmap with new player added
						joinRequests.remove(playerName);
						players.put(requesty.getUniqueId(), teamName);
						requesty.setMetadata("team_members", new FixedMetadataValue(plugin, teamName));
						requesty.sendMessage("Team request has been accepted!");
					} else {
						if (membersID.get(0).equals(null)) {
							// Find next public team to join...
							plugin.getLogger().info("Find next public team...");
						} else {
							sender.sendMessage("Team is full, you may only have " + String.valueOf(maxSize) + " players per team.");
						}
					}
				} else {
					sender.sendMessage("Can't find player :/");
				}

			} else {
				sender.sendMessage("You don't own this team so you can't accept join requests for that player");
			}
		} catch (NullPointerException np) {
			sender.sendMessage("Can't seem to find the player you want to add.");
		}
		return false;

	}

	public boolean joinTeamDeny(Player sender, String playerName) {
		try {
			String teamName = joinRequests.get(playerName);
			List<UUID> members = teams.get(teamName);
			if (members.get(0).equals(sender.getUniqueId())) {// Sender is owner
				Player requesty = Bukkit.getPlayer(UUIDLookup.get(playerName));
				joinRequests.remove(playerName);
				requesty.sendMessage("Team join request denied.");
			}
		} catch (NullPointerException np) {
			sender.sendMessage("Failed to find request, did you get the name right?");
		}
		return true;
	}

	public boolean leaveTeam(Player player) {
		try {
			String team = "";
			try {
				team = player.getMetadata("team_members").get(0).asString();
			} catch (Exception err) {
				player.sendMessage("It doesn't seem like you are part of a team...");
				return false;
			}

			if (team == null) {
				player.sendMessage("It doesn't seem like you are part of a team...");
				return false;
			}

			List<UUID> membersID = teams.get(team);

			if (membersID != null && membersID.contains(player.getUniqueId())) {
				if (membersID.get(0).equals(player.getUniqueId())) {// Owner is leaving team
					membersID.remove(player.getUniqueId());
					teams.put(team, membersID);// Commit changes
					setNewTeamOwner(team);// Sets new owner...
					players.remove(player.getUniqueId());// Set player list to be in a null team
					player.sendMessage("You have left the team");
					player.setMetadata("team_members", new FixedMetadataValue(plugin, ""));
					if(plugin.getConfig().getBoolean("leaving-team-takes-to-spawn")) {
						player.teleport(player.getWorld().getSpawnLocation());
					}
					return true;
				} else {// Standard member is leaving team...
					membersID.remove(player.getUniqueId());
					teams.put(team, membersID);
					players.remove(player.getUniqueId());// Set player list to be in a null team
					player.sendMessage("You have left the team");
					player.setMetadata("team_members", new FixedMetadataValue(plugin, ""));
					if(plugin.getConfig().getBoolean("leaving-team-takes-to-spawn")) {
						player.teleport(player.getWorld().getSpawnLocation());
					}

					return true;
				}
			} else {
				player.sendMessage("You are not a part of a team...");
			}
		} catch (Exception errr) {
			plugin.getLogger().info("Error leaving team: " + errr.toString());
		}
		return false;
	}

	/**
	 * Finds team owner from all active teams.
	 *
	 * @param Name of the team
	 * @return Returns Player object (owner) if player is null the server owns the
	 *         team
	 * @see getTeamOwner
	 */
	public Player getTeamOwner(String team) {
		return Bukkit.getPlayer(teams.get(team).get(0));
	}

	/**
	 * Returns if Player (player) is part of team (TeamName).
	 *
	 * @param Player player you want to check if is in a team
	 * @param String teamName the team to check if player is part of
	 * @return Returns true if player is a part of the team, otherwise false.
	 * @see isTeamMember
	 */
	public boolean isTeamMember(Player player, String teamName) {
		if (teams.get(teamName).contains(player.getUniqueId())) {
			return true;
		}
		return false;
	}

	/**
	 * Gets list of Player in a team.
	 *
	 * @param String teamName the team to return members of
	 * @return Returns List Object of Players (object) of team members for given
	 *         team name.
	 * @see getTeamMembers
	 */
	public List<Player> getTeamMembers(String teamName) {
		List<Player> members = new ArrayList<>();
		for (UUID playerID : teams.get(teamName)) {
			members.add(getPlayerByUUID(playerID));
		}
		return members;
	}

	public boolean setNewTeamOwner(String teamName) {
		List<UUID> members = teams.get(teamName);
		if (members.size() <= 1) {
			teams.remove(teamName);
			return true;
		} else {
			Random rand = new Random();
			int n = rand.nextInt(members.size());
			Player newOwner = getPlayerByUUID(members.get(n));
			List<UUID> newMembers = new ArrayList<>();
			newMembers.add(newOwner.getUniqueId());
			for (UUID player : members) {
				if (newOwner.equals(getPlayerByUUID(player))) {
					continue;
				} else {
					newMembers.add(player);
				}
			}
			teams.put(teamName, newMembers);

		}
		return false;
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

	public boolean kickTeamMember(String kicky, Player kicker) {
		if (getTeamOwner(players.get(kicker.getUniqueId())).equals(kicker)
				&& isTeamMember(Bukkit.getPlayer(UUIDLookup.get(kicky)), players.get(kicker.getUniqueId()))) {
			// Kicker is team owner and kicky is a member of the kicker's team.
			leaveTeam(Bukkit.getPlayer(UUIDLookup.get(kicky)));// Execute the leave function on behalf of kicked player.
			return true;
		}
		return false;
	}
	@SuppressWarnings("unchecked")
	public boolean loadTeamData() {
		try {
			FileInputStream fileIn = new FileInputStream(
					System.getProperty("user.dir") + "/plugins/Mineswarm/teams.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			this.teams = (HashMap<String, List<UUID>>) in.readObject();
			in.close();
			fileIn.close();

			fileIn = new FileInputStream(System.getProperty("user.dir") + "/plugins/Mineswarm/players.ser");
			in = new ObjectInputStream(fileIn);
			this.players = (HashMap<UUID, String>) in.readObject();
			in.close();
			fileIn.close();

			fileIn = new FileInputStream(System.getProperty("user.dir") + "/plugins/Mineswarm/joinRequests.ser");
			in = new ObjectInputStream(fileIn);
			this.joinRequests = (HashMap<String, String>) in.readObject();
			in.close();
			fileIn.close();

			fileIn = new FileInputStream(System.getProperty("user.dir") + "/plugins/Mineswarm/UUIDs.ser");
			in = new ObjectInputStream(fileIn);
			this.UUIDLookup = (HashMap<String, UUID>) in.readObject();
			in.close();
			fileIn.close();

			plugin.getLogger().info("LOADED TEAMS");
		} catch (IOException | ClassNotFoundException i) {
			plugin.getLogger().info(i.toString() + " WHILE DESER");

			return false;
		}
		if (teams == null) {
			this.teams = new HashMap<>();
			;
		}
		if (players == null) {
			this.players = new HashMap<>();
			;
		}
		if (UUIDLookup == null) {
			this.UUIDLookup = new HashMap<>();
			;
		}
		if (joinRequests == null) {
			this.joinRequests = new HashMap<>();
			;
		}
		return true;
	}

	public boolean saveTeamData() {
		try {
			FileOutputStream fileOut = new FileOutputStream(
					System.getProperty("user.dir") + "/plugins/Mineswarm/teams.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(teams);
			out.close();
			fileOut.close();

			fileOut = new FileOutputStream(System.getProperty("user.dir") + "/plugins/Mineswarm/players.ser");
			out = new ObjectOutputStream(fileOut);
			out.writeObject(players);
			out.close();
			fileOut.close();

			fileOut = new FileOutputStream(System.getProperty("user.dir") + "/plugins/Mineswarm/joinRequests.ser");
			out = new ObjectOutputStream(fileOut);
			out.writeObject(joinRequests);
			out.close();
			fileOut.close();

			fileOut = new FileOutputStream(System.getProperty("user.dir") + "/plugins/Mineswarm/UUIDs.ser");
			out = new ObjectOutputStream(fileOut);
			out.writeObject(UUIDLookup);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			plugin.getLogger().info(i.toString());
			return false;
		}

		return true;
	}
}
