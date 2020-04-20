package me.cutrats110.mineswarm;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ScheduledChests implements Listener {
	public Plugin plugin;
	public Database db = null;
	private PotionObjects potions = null;
	
	public ScheduledChests(Plugin instance, PotionObjects potions) {
		plugin = instance;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		this.db = new Database(plugin);
		this.potions = potions;
		chestSpawns();
	}
	
	/**
	 * Task calling database function to find all chests in db and place items in them.
	 *
	 * @return void
	 * @see Database.getChests(PotionObjects portions)
	 */
	public void chestSpawns(){
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
		    public void run() {
		    	db.getChests(potions);
		    }
		}, (20*90), (20*90));//20*90 = 1:30
		//20t = 1s
	}
}
