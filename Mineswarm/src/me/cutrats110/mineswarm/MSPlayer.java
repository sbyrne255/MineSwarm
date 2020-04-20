package me.cutrats110.mineswarm;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.EntityType;

public class MSPlayer {
	private UUID id;
	private String name;
	private boolean partOfTeam = false;
	private String itemClass = null;
	private int totalDamageTaken = 0;
	private int totalDamageDealt = 0;
	private boolean isInLastStand = false;
	private int deaths = 0;
	private int timesDowned = 0;
	private int timesDownedSinceLastDeath = 0;
	private int TotalMobsKilled = 0;
	private int numberOfPlayersRevived = 0;
	private String firstJoined = Instant.now().toString();
	private HashMap<EntityType, Integer> mobsKilled = new HashMap<EntityType, Integer>();
	
	public MSPlayer(String name, UUID id) {
		this.name = name;
		this.id = id;
	}
	
	
	//Setters
	public void setIsPartOfTeam(Boolean x) { partOfTeam = x; }
	public void setItemClass(String itemClass) { this.itemClass = itemClass; }
	public void addToTotalDamageTaken(int addtionalDamage) { this.totalDamageTaken += addtionalDamage;}	
	public void addToTotalDamageDealt(int addtionalDamage) { this.totalDamageDealt += addtionalDamage;}
	public void setIsInLastStand(boolean x) {this.isInLastStand = x;}
	public void addDeath() {this.deaths += 1;}
	public void addTimesDowned() {this.timesDowned += 1; this.timesDownedSinceLastDeath +=1;}
	public void resetTimesDownedSinceLastDeath() {this.timesDownedSinceLastDeath = 0;}
	public void addTotalMobsKilled() {this.TotalMobsKilled += 1;}
	public void setTotalMobsKilled(int mobs) {this.TotalMobsKilled = mobs;}
	public void incrementTotalMobsKilled(int mobs) {this.TotalMobsKilled += mobs;}
	public void addTotalNumberOfPlayersRevived() {this.numberOfPlayersRevived += 1;}
	public void addToMobsKilled(EntityType entType) {
		addTotalMobsKilled();
		Integer mobsKilledOfType = mobsKilled.get(entType);
		if(mobsKilledOfType != null) {
			mobsKilled.put(entType, (mobsKilledOfType+1));
		} else {
			mobsKilled.put(entType, 1);
		}
	}
	
	
	public String getItemClass() { return this.itemClass; }
	public String getName() {return this.name;}
	public UUID getID() {return this.id;}
	public boolean isPartOfTeam() {return this.partOfTeam;}
	public int getTotalDamageTaken() {return this.totalDamageTaken;}
	public int getTotalDamageDealt() {return this.totalDamageDealt;}
	public boolean isInLastStand() {return this.isInLastStand;}
	public int getTotalDeaths() {return this.deaths;}
	public int getTotalTimesDowned() {return this.timesDowned;}
	public int getTimesDownedSinceLastDeath() {return this.timesDownedSinceLastDeath;}
	public int getTotalMobsKilled() {return this.TotalMobsKilled;}
	public int getTotalPlayersRevived() {return this.numberOfPlayersRevived;}
	public String getJoinedDate() {return this.firstJoined;}
	
	

	
	
}
