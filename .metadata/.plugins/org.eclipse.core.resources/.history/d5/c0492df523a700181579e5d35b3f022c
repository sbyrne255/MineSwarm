package me.cutrats110;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ScheduledChests implements Listener {
	public Plugin plugin;
	public Database db = null;
	private boolean debugging = true;
	
	public ScheduledChests(Plugin instance) {
		plugin = instance;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		debugging = plugin.getConfig().getBoolean("debugging");
		this.db = new Database(plugin);
		chestSpawns();
		
	}
	
	
	public void chestSpawns(){
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
		    public void run() {
		    	if(debugging){plugin.getLogger().info("I am a scheduled task, running at a scheduled time!");}
		    	db.getChests();
		    }
		}, (20*20), (20*20));//Delay, length allowed to run...
		//20t = 1s
		//20*10 = 10 seconds...
	}
}
