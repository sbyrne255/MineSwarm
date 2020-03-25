package me.cutrats110;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MSTeam {
	
	private String name;
	private ArrayList<UUID> members = new ArrayList<UUID>();
	private UUID owner;
	private boolean closed = true;
	private int score = 0;
	
	public MSTeam(String name) {
		this.name = name;
	}
	public MSTeam(String name, boolean closed) {
		this.name = name;
		this.closed = closed;
	}
	public MSTeam(String name, UUID owner, boolean closed, int score) {
		this.name = name;
		this.owner = owner;
		this.closed = closed;
		this.score = score;
	}
	
	public String getName() { return this.name; }
	
	public boolean isClosed() { return this.closed; }
	
	
	/**
	 * @return Returns UUID of the team owner.
	 * @see getOwner()
	 */
	public UUID getOwner() {return this.owner;}
	/**
	 * Selects a new team owner from its members that meet the requirements or sets the owner directly if specified.
	 *
	 * @param UUID of a team member who will become the owner.
	 * @return void
	 * @see newOwner() | newOwner(UUID player)
	 */
	public boolean newOwner() {
		ArrayList<Player> onlinePlayers = new ArrayList<Player>();
		for(UUID id : this.members) {
			Player player = Bukkit.getPlayer(id);
			if(player != null && player.isOnline() && player.getUniqueId() != this.owner) {
				onlinePlayers.add(player);
			}
		}
		if(onlinePlayers.size() >0) {
		    int rnd = new Random().nextInt(onlinePlayers.size()-1);
			this.owner = onlinePlayers.get(rnd).getUniqueId();
			return true;
		} else {return false;} 
	}
	public boolean newOwner(UUID player) {	
		if(this.members.contains(player)) {
			this.owner = player; //Old owner remains in the team as a member.
			return true;
		} else {return false;}
	}
	
	/**
	 * @return Returns ArrayList of team members' UUIDs
	 * @see getMembers()
	 */
	public ArrayList<UUID> getMembers() { return this.members; }
	public ArrayList<String> getMembersNames() { 
		ArrayList<String> playerNames = new ArrayList<String>();
		for(UUID id : this.members) {
			playerNames.add(Bukkit.getPlayer(id).getName());
		}
		return playerNames; 		
	}
	public ArrayList<Player> getMembersPlayerObjects() { 
		ArrayList<Player> playerObjects = new ArrayList<Player>();
		for(UUID id : this.members) {
			playerObjects.add(Bukkit.getPlayer(id));
		}
		return playerObjects; 		
	}
	public ArrayList<Player> getOnlineMembersPlayerObjects() { 
		ArrayList<Player> playerObjects = new ArrayList<Player>();
		for(UUID id : this.members) {
			Player player = Bukkit.getPlayer(id);
			if(player != null && player.isOnline()) {
				playerObjects.add(player);	
			}
		}
		return playerObjects; 		
	}
	/**
	 * Adds player UUID to members ArrayList assuming criteria is met
	 * New player must be a valid UUID, and online
	 *
	 * @param Player's UUID that is being added
	 * @return Returns void
	 * @see addMember(UUID playerUUID)
	 */
	public void addMember(UUID playerUUID) { this.members.add(playerUUID);}
	public boolean removeMember(UUID playerUUID) { this.members.remove(playerUUID); return true; }
	
	public void setScore(int score) {this.score=score;}
	public void addScore(int score) {this.score += score;}
	public void subtractScore(int score) {this.score -= score;}
	public int getScore() {return this.score;}
}
