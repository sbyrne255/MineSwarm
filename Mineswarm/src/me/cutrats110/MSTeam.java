package me.cutrats110;

import java.util.ArrayList;
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
	public void newOwner() {
		//Logic to select and set a new owner that isn't AFK and IS online.
		//
	}
		//TODO
	public void newOwner(UUID player) {	this.owner = player;}//Needs to check player is member of team before assign as owner.
	
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
	/**
	 * Adds player UUID to members ArrayList assuming criteria is met
	 * New player must be a valid UUID, online, and not AFK
	 *
	 * @param Player's UUID that is being added
	 * @return Returns true if remember is added, false if they are not.
	 * @see addMember(UUID playerUUID)
	 */
	public boolean addMember(UUID playerUUID) { 
		//Check max number of members
		//Check sending player is owner
		//TODO
		this.members.add(playerUUID); return true; 
	}
	
	public void setScore(int score) {this.score=score;}
	public void addScore(int score) {this.score += score;}
	public void subtractScore(int score) {this.score -= score;}
	public int getScore() {return this.score;}
}
