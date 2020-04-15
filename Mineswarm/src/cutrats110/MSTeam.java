package cutrats110;

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
	/**
	 * Gets the given name of the team.
	 *
	 * @return Returns the team name as a String.
	 */
	public String getName() { return this.name; }
	/**
	 * Checks if team is open (anyone can join) or closed (owner must approve join request).
	 *
	 * @return True team is privately created by a player. Returns False if team is owned/created by the server.
	 */
	public boolean isClosed() { return this.closed; }
	/**
	 * Gets the current team's owner's UUID.
	 * 
	 * @return Returns UUID of the team owner.
	 */
	public UUID getOwner() {return this.owner;}
	/**
	 * Selects a new team owner from its members that meet the requirements.
	 * Team is deleted if the current owner is the only member.
	 *
	 * @return True if a new owner is selected. Returns false if no member meets the criteria. 
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
	/**
	 * Retains current owner as member and sets new UUID as owner if they are part of the team.
	 * 
	 * @param UUID of a team member who will become the owner.
	 * @return Returns True is player is now the new owner, returns false if new owner is not a part of the team.
	 * @see newOwner()
	 */
	public boolean newOwner(UUID player) {	
		if(this.members.contains(player)) {
			this.owner = player; //Old owner remains in the team as a member.
			return true;
		} else {return false;}
	}
	/**
	 * Gets an ArrayList<UUID> of all team members.
	 * 
	 * @return Returns ArrayList of team members' UUIDs
	 * @see getMembersPlayerObjects(), getMembersNames(), getOnlineMembersPlayerObjects()
	 */
	public ArrayList<UUID> getMembers() { return this.members; }
	/**
	 * Gets all team members names as Strings.
	 *
	 * @return Returns ArrayList<String> of all team members names, may skip null values.
	 * @see getMembersPlayerObjects(), getOnlineMembersPlayerObjects(), getMembers()
	 */
	public ArrayList<String> getMembersNames() { 
		ArrayList<String> playerNames = new ArrayList<String>();
		for(UUID id : this.members) {
			try {
				playerNames.add(Bukkit.getPlayer(id).getName());
			}catch(NullPointerException np) {continue;}
		}
		return playerNames; 		
	}
	/**
	 * Gets all player objects of team's members
	 *
	 * @return Returns ArrayList<Player> of all team members, may contain null objects
	 * @see getMembersPlayerObjects(), getMembersNames(), getMembers()
	 */
	public ArrayList<Player> getMembersPlayerObjects() { 
		ArrayList<Player> playerObjects = new ArrayList<Player>();
		for(UUID id : this.members) {
			try {
				playerObjects.add(Bukkit.getPlayer(id));
			}catch(NullPointerException np) {continue;}
		}
		return playerObjects; 		
	}
	/**
	 * Gets all player objects of team members that are online.
	 *
	 * @return Returns ArrayList<Player> of all online team members
	 * @see getMembersPlayerObjects(), getMembersNames(), getMembers()
	 */
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
	 * Adds player UUID to members ArrayList
	 * New player must be a valid UUID, and online but is not checked here.
	 *
	 * @param Player's UUID that is being added
	 * @return Returns void
	 * @see removeMember(UUID playerUUID)
	 */
	public void addMember(UUID playerUUID) { this.members.add(playerUUID);}
	/**
	 * Removes a member from the team by UUID of player. Does not check if they are the owner.
	 *
	 * @param Player's UUID that is being removed
	 * @return Returns true always.
	 * @see addMember(UUID playerUUID)
	 */
	public boolean removeMember(UUID playerUUID) { this.members.remove(playerUUID); return true; }
	/**
	 * This function sets the current score of the team.
	 * 
	 * @param Integer score set as team score.
	 * @return Void
	 * @see addScore(int score), subtractScore(int score), getScore()
	 */
	public void setScore(int score) {this.score=score;}
	/**
	 * Not fully implemented, this function increases the current score of the team.
	 * 
	 * @param Integer score to add (negative values will be subtractions).
	 * @return Void
	 * @see setScore(int score), subtractScore(int score), getScore()
	 */
	public void addScore(int score) {this.score += score;}
	/**
	 * Not fully implemented, this function decreases the current score of the team.
	 * 
	 * @param Integer score to subtract (negative values will be additions).
	 * @return Void
	 * @see setScore(int score), addScore(int score), getScore()
	 */
	public void subtractScore(int score) {this.score -= score;}
	/**
	 * Not fully implemented, this function returns the pre-calculated score of the team.
	 *
	 * @return Returns Integer value of team's score.
	 * @see setScore(int score), addScore(int score), subtractScore(int score)
	 */
	public int getScore() {return this.score;}
}
