package me.cutrats110;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ScheduledChests implements Listener {
	public Plugin plugin;
	public Database db = null;
	private boolean debugging = true;
	private PotionObjects potions = null;
	
	public ScheduledChests(Plugin instance, PotionObjects potions) {
		plugin = instance;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		debugging = plugin.getConfig().getBoolean("debugging");
		this.db = new Database(plugin);
		this.potions = potions;
		chestSpawns();
		
	}
	
	
	public void chestSpawns(){
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
		    public void run() {
		    	if(debugging){plugin.getLogger().info("I am a scheduled task, running at a scheduled time!");}
		    	db.getChests(potions);
		    }
		}, (20*90), (20*90));//20*90 = 1:30
		//20t = 1s
	}
}
