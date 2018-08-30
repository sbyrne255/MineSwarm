package me.cutrats110;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ScheduledMobs implements Listener {
	public Plugin plugin;
	public Database db = null;
	private boolean debugging = true;
	
	public ScheduledMobs(Plugin instance) {
		plugin = instance;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		debugging = plugin.getConfig().getBoolean("debugging");
		this.db = new Database(plugin);
		mobSpawns();
		
	}
	
	//Point XYZ from marking tool...
	//RadiusX = Math.abs(x_max * x_min);
	//RadiusY = Math.abs(y_max * y_min);
	//Radiusz = Math.abs(z_max * z_min);
	
	public void mobSpawns(){
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
		    	if(debugging){plugin.getLogger().info("I am a scheduled task, running at a scheduled time!");}	    	
		    	
		    	
		    	playerLoop://Problem, right now loop is checking ONCE per player, needs to check all players VS all spanwers... 
		    	for(Player player : Bukkit.getOnlinePlayers()){//Loop through all online players
		    		if(debugging){plugin.getLogger().info("Players are online...");}
		    		List<String> results = db.getMobSpawners(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), player.getLocation().getWorld().toString().replace("CraftWorld{name=", "").replace("}", ""));
		    		//IF PLAYER in XYZ Radius...
		    		//Loop for X in results...
		    		for(int a = 0; a < results.size(); a+=13) {
			    		if(results != null){
			    			if(debugging){plugin.getLogger().info(String.valueOf((results.size()/13)) + " Types of mobs spawning");}
			    			//Loop each entity near the spawner
			    			int mobcount = 0;
			    			for(Entity e : getEntitiesAroundPoint(results.get(a).replace("CraftWorld{name=", "").replace("}", ""), Integer.valueOf(results.get(a+1)), Integer.valueOf(results.get(a+2)), Integer.valueOf(results.get(a+3)), Integer.valueOf(results.get(a+4))))
			    			{
			    				if(e.getType().toString().equals(results.get(a+5))){
			    					mobcount++;
			    					if(mobcount > Integer.valueOf(results.get(a+6))){
			    						if(debugging){plugin.getLogger().info("To many mobs, checking next player...");}
			    						continue playerLoop;//Mobs already more than allowed...
			    					}
			    				}
			    			}
			    			//IF entities less than DB max mobs
			    			for(int i = mobcount; i < Integer.valueOf(results.get(a+6)); i++){
			    				if(debugging){plugin.getLogger().info("While we have less mobs in the area than our max spawn some mobs!.");}
			    		    	Location location = new Location (Bukkit.getWorld(results.get(a).replace("CraftWorld{name=", "").replace("}", "")), Integer.valueOf(results.get(a+1)), Integer.valueOf(results.get(a+2)), Integer.valueOf(results.get(a+3)));//Needs to be location from DB...
	
			    		    	//Check % of weapon, if 0 ignore
			    		    	if(Integer.valueOf(results.get(a+10)) != 0){
			    		    		if(debugging){plugin.getLogger().info("Probability is set, trying it");}
			    		    		//Random chance check...
			    		    		Random rand = new Random();				
									if( (rand.nextInt(Integer.valueOf(results.get(a+10))+1) == 1) || debugging)
									{
										if(debugging){plugin.getLogger().info("Either debugging or got our random number!");}
										ItemStack item = new ItemStack( Material.matchMaterial(results.get(a+11)), 1);
										item.setDurability(Short.valueOf(results.get(a+12)));
										
										LivingEntity zombie1 = (LivingEntity) Bukkit.getWorld(results.get(a)).spawnEntity(location, EntityType.valueOf(results.get(a+5)));
					    		    	zombie1.getEquipment().setItemInMainHand(item);
					    		    	if(debugging){plugin.getLogger().info("Just spawned a mob with a weapon!");}
					    		    	if(debugging){plugin.getLogger().info(zombie1.getLocation().toString());}
					    		    	
									}
									else{
										LivingEntity zombie1 = (LivingEntity) Bukkit.getWorld(results.get(a)).spawnEntity(location, EntityType.valueOf(results.get(a+5)));
						    		    if(debugging){plugin.getLogger().info("Just spawned a mob without a weapon!");}
						    		    if(debugging){plugin.getLogger().info(zombie1.getLocation().toString());}
									}
			    		    	}
			    		    	else{
									LivingEntity zombie1 = (LivingEntity) Bukkit.getWorld(results.get(a)).spawnEntity(location, EntityType.valueOf(results.get(a+5)));
					    		    if(debugging){plugin.getLogger().info("Just spawned a mob without a weapon!");}
					    		    if(debugging){plugin.getLogger().info(zombie1.getLocation().toString());}
								}
			    			}
			    		}
		    		}
		    	}
		    }
		}, (20*10), (20*10));//Delay, length allowed to run...
		//20t = 1s
		//20*10 = 10 seconds...
	}
	public Collection<Entity> getEntitiesAroundPoint(String world, int x, int y, int z, int radius) {
		Location location = new Location (Bukkit.getWorld(world), x, y, z);
	    return location.getWorld().getNearbyEntities(location, radius, radius, radius);
	}
//	public Collection<Entity> getEntitiesAroundPoint(String world, int x, int y, int z, int radiusx, int radiusy, int radiusz) {
		//Location location = new Location (Bukkit.getWorld(world), x, y, z);
	    //return location.getWorld().getNearbyEntities(location, radiusx, radiusy, radiusz);
	//}

}