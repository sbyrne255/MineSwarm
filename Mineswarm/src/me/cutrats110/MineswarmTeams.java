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
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class MineswarmTeams {
	public Plugin plugin;
	//Team name as string, List of players, 0 being team owner.
	private transient HashMap<String, List<UUID>> teams = new HashMap<>();
	//Player object, team they are a member of
	private transient HashMap<UUID, String> players = new HashMap<>();
	//Players name, UUID of player
	private transient HashMap<String, UUID> UUIDLookup = new HashMap<>();
	//Player name, team name (assumes all requests are to join)
	private transient HashMap<String, String> joinRequests = new HashMap<>();
	//Player, team name (assumes all requests are to tp)
	//private HashMap<Player, String> tpRequests = new HashMap<>();//Broadcast request to all team members...//Waiting on thsi because of bukkit requirements to check if they have moved, damaged ect...
	
	public MineswarmTeams(Plugin instance) {
		this.plugin = instance;
		loadTeamData();
	}

	public void addUUID(Player player) {//Gets called on join event...
		UUIDLookup.put(player.getName(), player.getUniqueId());
	}
	public Player getPlayerByUUID(UUID id) {		
		return Bukkit.getPlayer(id);
	}
	public Player getPlayerByName(String name) {		
		return Bukkit.getPlayer(UUIDLookup.get(name));
	}
	public boolean joinTeam(Player player, String team) {
		if(players.containsKey(player.getUniqueId())) {
			player.sendMessage("You are already part of a team, leave that team before joining a new one");
		}
		else {
			Player teamOwner = getPlayerByUUID(teams.get(team).get(0));
			if(teamOwner != null) {//Not a server team
				teamOwner.sendMessage("Player " + player.getName() + " is requesting to join your team. /msteam add to add the player or /msteam deny to deny.");
				joinRequests.put(player.getName(), team);
				player.sendMessage("Join request sent, waiting for approval...");
				return true;
			}
		}
		return true;
	}
	public boolean joinTeamAccept(Player sender, String playerName) {
		String teamName = joinRequests.get(playerName);
		List<UUID> membersID = teams.get(teamName);
		List<Player> members = new ArrayList<>();
		for(UUID playerID : membersID ) {
			members.add(getPlayerByUUID(playerID));
		}
		if(sender.equals(members.get(0)) || members.get(0).equals(null)) {//sender is the owner of the team being requested to join...
			Player requesty = Bukkit.getPlayer(UUIDLookup.get(playerName));
			if(requesty != null) {//Found a UUID match
				int maxSize = plugin.getConfig().getInt("max-team-size");
				if(membersID.size() <= maxSize) {
					membersID.add(requesty.getUniqueId());
					teams.put(teamName, membersID);//Update hashmap with new player added
					joinRequests.remove(playerName);
					players.put(requesty.getUniqueId(), teamName);
					requesty.setMetadata("team_members",new FixedMetadataValue(plugin, teamName));
					requesty.sendMessage("Team request has been accepted!");
				}
				else {
					if(members.get(0).equals(null)) {
						//Find next public team to join...
					}
					else {
					sender.sendMessage("Team is full, you may only have "+String.valueOf(maxSize)+" players per team.");
					}
				}
			}
			
		}
		else {
			sender.sendMessage("You don't own this team so you can't accept join requests for that player");
		}
		
		return false;
		
	}
	public boolean joinTeamDeny(Player sender, String playerName) {
		String teamName = joinRequests.get(playerName);
		List<UUID> members = teams.get(teamName);
		if(members.get(0).equals(sender.getUniqueId())) {//Sender is owner
			Player requesty = Bukkit.getPlayer(UUIDLookup.get(playerName));
			joinRequests.remove(playerName);
			requesty.sendMessage("Team join request denied.");
		}
		return false;		
	}
	public boolean leaveTeam(Player player) {
		try {
			String team ="";
			try { team = player.getMetadata("team_members").get(0).asString();}
			catch(Exception err) {player.sendMessage("It doesn't seem like you are part of a team..."); return false;}
			
			if(team ==null) {
				player.sendMessage("It doesn't seem like you are part of a team...");
				return false;
			}
			
			List<UUID> membersID = teams.get(team);
			
			if(membersID != null && membersID.contains(player.getUniqueId())) {
				if(membersID.get(0).equals(player.getUniqueId())) {//Owner is leaving team
					membersID.remove(player.getUniqueId());
					teams.put(team, membersID);//Commit changes
					setNewTeamOwner(team);//Sets new owner...
					players.put(player.getUniqueId(), null);//Set player list to be in a null team
					player.sendMessage("You have left the team");
					player.setMetadata("team_members",new FixedMetadataValue(plugin, ""));
					return true;
				}
				else {//Standard member is leaving team...
					membersID.remove(player.getUniqueId());
					teams.put(team, membersID);
					players.put(player.getUniqueId(), null);//Set player list to be in a null team
					player.sendMessage("You have left the team");
					player.setMetadata("team_members",new FixedMetadataValue(plugin, ""));
	
					return true;
				}
			}
			else {
				player.sendMessage("You are not a part of a team...");
			}
		}catch(Exception errr) {
			plugin.getLogger().info("Error leaving team: " + errr.toString());
		}
		return false;
	}
	
	
	/**
	 * Finds team owner from all active teams.
	 *
	 * @param Name of the team
	 * @return      Returns Player object (owner) if player is null the server owns the team
	 * @see         getTeamOwner
	 */
	public Player getTeamOwner(String team) {
		return Bukkit.getPlayer(teams.get(team).get(0));
	}
	/**
	 * Returns if Player (player) is part of team (TeamName).
	 *
	 * @param Player player you want to check if is in a team
	 * @param String teamName the team to check if player is part of
	 * @return      Returns true if player is a part of the team, otherwise false.
	 * @see         isTeamMember
	 */
	public boolean isTeamMember(Player player, String teamName) {
		if(teams.get(teamName).contains(player.getUniqueId())) {
			return true;
		}
		return false;
	}
	/**
	 * Gets list of Player in a team.
	 *
	 * @param String teamName the team to return members of
	 * @return      Returns List Object of Players (object) of team members for given team name.
	 * @see         getTeamMembers
	 */
	public List<Player> getTeamMembers(String teamName){
		List<Player> members = new ArrayList<>();
		for(UUID playerID : teams.get(teamName) ) {
			members.add(getPlayerByUUID(playerID));
		}
		return members;
	}
	/**
	 * Creates a team instance with a specified owner; if player owner is null that means server is owner and team is random/public team
	 *
	 * @param Name of the team (how this team will be identified)
	 * @param Owner of team, server will be a null owner.
	 * @return      Returns true if team is successfully created.
	 * @see         createTeam
	 */
	public boolean createTeam(String name, Player player) {
		try {
			//Team name is available and team owner is NOT part of another team.
			List<UUID> teamCheck = new ArrayList<>();
			teamCheck = teams.get(name);//Get all UUIDs associated with that team name...
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
					    	player.setMetadata("team_members",new FixedMetadataValue(plugin, name));
					    	return true;
					    } else {
					       //Make team...
					    	players.put(player.getUniqueId(), name);
					    	List<UUID> playerList = new ArrayList<>();
					    	playerList.add(player.getUniqueId());
					    	teams.put(name, playerList);
					    	player.setMetadata("team_members",new FixedMetadataValue(plugin, name));
					    	return true;
					    	
					    }
					}
			    }
			}	
		}catch(Exception err) {
			plugin.getLogger().info("Problem with making team: " + err.toString());
		}
		return false;
	}
	public boolean setNewTeamOwner(String teamName) {
		List<UUID> members = teams.get(teamName);
		if(members.size() <= 1) {
			teams.remove(teamName);
			return true;
		}
		else {
			Random rand = new Random();
			int  n = rand.nextInt(members.size());
			Player newOwner = getPlayerByUUID(members.get(n));
			List<UUID> newMembers = new ArrayList<>();
			newMembers.add(newOwner.getUniqueId());
			for(UUID player : members) {
				if(newOwner.equals(getPlayerByUUID(player))) {
					continue;
				}
				else {
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
		for(UUID player : members) {
			if(newOwner.getUniqueId().equals(player)) {
				continue;
			}
			else {
				members.add(player);
			}
		}
		teams.put(teamName, members);
		return true;
	}
	public boolean kickTeamMember(String kicky, Player kicker) {
		if(getTeamOwner(players.get(kicker.getUniqueId())).equals(kicker) && isTeamMember(Bukkit.getPlayer(UUIDLookup.get(kicky)), players.get(kicker.getUniqueId()))) {
			//Kicker is team owner and kicky is a member of the kicker's team.
			leaveTeam(Bukkit.getPlayer(UUIDLookup.get(kicky)));//Execute the leave function on behalf of kicked player.
			return true;			
		}
		return false;
	}
	
	//*createTeam (name, overload-player) Method
	//*joinTeam Method (name, player)
	//*leaveTeam Method (name, player)
	//*getTeamOwner(teamname) function
	//*setTeamOwner(player) method
	//*inTeam(teamname) function
	//*getTeamMembers(teamname) function
	//*kickTeamMember(player) method
	//tapRequest(teamname)
		//USE BELOW CODE to auto generate players you can TPA to, blank will request entire team.
		//Remember to add massive CD after taking damage or moving (maybe, 10 seconds?)
		//@EventHandler
		//public void onChatTab(PlayerChatTabCompleteEvent event){
		   	//Collection<String> names = event.getTabCompletions();
		   	//names.add("string");
		   	//names.add("anotherstring");
		//}

	@SuppressWarnings("unchecked")
	public boolean loadTeamData() {
		try {
	         FileInputStream fileIn = new FileInputStream(System.getProperty("user.dir") +"/plugins/Mineswarm/teams.ser");
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         this.teams = (HashMap<String, List<UUID>>) in.readObject();
	         in.close();
	         fileIn.close();
	         
	         fileIn = new FileInputStream(System.getProperty("user.dir") +"/plugins/Mineswarm/players.ser");
	         in = new ObjectInputStream(fileIn);
	         this.players = (HashMap<UUID, String>) in.readObject();
	         in.close();
	         fileIn.close();
	         
	         fileIn = new FileInputStream(System.getProperty("user.dir") +"/plugins/Mineswarm/joinRequests.ser");
	         in = new ObjectInputStream(fileIn);
	         this.joinRequests = (HashMap<String, String>) in.readObject();
	         in.close();
	         fileIn.close();
	         
	         fileIn = new FileInputStream(System.getProperty("user.dir") +"/plugins/Mineswarm/UUIDs.ser");
	         in = new ObjectInputStream(fileIn);
	         this.UUIDLookup = (HashMap<String, UUID>) in.readObject();
	         in.close();
	         fileIn.close();
	         
	         plugin.getLogger().info("LOADED TEAMS");
	      } catch (IOException | ClassNotFoundException i) {	    	  
	         plugin.getLogger().info(i.toString() + " WHILE DESER");
	         
	         return false;
	      }
		 if(teams == null) {
			 this.teams = new HashMap<>();;
		 }
		 if(players == null) {
			 this.players = new HashMap<>();;
		 }
		 if(UUIDLookup == null) {
			 this.UUIDLookup = new HashMap<>();;
		 }
		 if(joinRequests == null) {
			 this.joinRequests = new HashMap<>();;
		 }
		return true;
	}
	public boolean saveTeamData() {
		 try {
	         FileOutputStream fileOut = new FileOutputStream(System.getProperty("user.dir") +"/plugins/Mineswarm/teams.ser");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(teams);
	         out.close();
	         fileOut.close();
	         
	         fileOut = new FileOutputStream(System.getProperty("user.dir") +"/plugins/Mineswarm/players.ser");
	         out = new ObjectOutputStream(fileOut);
	         out.writeObject(players);
	         out.close();
	         fileOut.close();
	         
	         fileOut = new FileOutputStream(System.getProperty("user.dir") +"/plugins/Mineswarm/joinRequests.ser");
	         out = new ObjectOutputStream(fileOut);
	         out.writeObject(joinRequests);
	         out.close();
	         fileOut.close();
	         
	         fileOut = new FileOutputStream(System.getProperty("user.dir") +"/plugins/Mineswarm/UUIDs.ser");
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
		//tpaRequest(teamname) method

	
}