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
		    	//SELECT * FROM DB...   	
		    	//Remember to check the BLOCK id for close entities, NOT the player!!!
		    	playerLoop:
		    	for(Player player : Bukkit.getOnlinePlayers()){
		    		if(debugging){plugin.getLogger().info("Players are online...");}
		    		List<String> results = db.getMobSpawners(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), player.getLocation().getWorld().toString().replace("CraftWorld{name=", "").replace("}", ""));
		    		//IF PLAYER in XYZ Radius...
		    		int mobcount = 0;
		    		if(results != null){			
		    			if(debugging){plugin.getLogger().info("Results are NOT null, trying more stuff...");}
		    			//Known working for a squared area, IE: 30*30*30 room
		    			for(Entity e : getEntitiesAroundPoint(results.get(0).replace("CraftWorld{name=", "").replace("}", ""), Integer.valueOf(results.get(1)), Integer.valueOf(results.get(2)), Integer.valueOf(results.get(3)), Integer.valueOf(results.get(4)))){
		    			
		    			//Demo for new Marking method, in which the difference between X-min and X-max is used for 1 wall...
		    			//for(Entity e : getEntitiesAroundPoint(results.get(0).replace("CraftWorld{name=", "").replace("}", ""), Integer.valueOf(results.get(1)), Integer.valueOf(results.get(2)), Integer.valueOf(results.get(3)), Integer.valueOf(results.get(13)), Integer.valueOf(results.get(14)), Integer.valueOf(results.get(15)))){
		    				//For each entity near the block...
		    				if(debugging){plugin.getLogger().info("Checking mobs around area...");}
		    				if(e.getType().toString().equals(results.get(5))){
		    					if(debugging){plugin.getLogger().info("Found Mob with same type...");}
		    					mobcount++;
		    					if(mobcount > Integer.valueOf(results.get(6))){
		    						if(debugging){plugin.getLogger().info("To many mobs, checking next player...");}
		    						continue playerLoop;//Mobs already more than allowed...
		    					}
		    				}
		    			}
		    			//IF entities less than DB max mobs
		    			for(int i = mobcount; i < Integer.valueOf(results.get(6)); i++){
		    				if(debugging){plugin.getLogger().info("While we have less mobs in the area than our max spawn some mobs!.");}
		    		    	Location location = new Location (Bukkit.getWorld(results.get(0).replace("CraftWorld{name=", "").replace("}", "")), Integer.valueOf(results.get(1)), Integer.valueOf(results.get(2)), Integer.valueOf(results.get(3)));//Needs to be location from DB...
		    		    	if(debugging){plugin.getLogger().info(String.valueOf(location.getBlockX()));}
		    		    	if(debugging){plugin.getLogger().info(String.valueOf(location.getBlockY()));}
		    		    	if(debugging){plugin.getLogger().info(String.valueOf(location.getBlockZ()));}
		    		    	//Check % of weapon, if 0 ignore
		    		    	if(Integer.valueOf(results.get(10)) != 0){
		    		    		if(debugging){plugin.getLogger().info("Probability is set, trying it");}
		    		    		//Random chance check...
		    		    		Random rand = new Random();				
								if( (rand.nextInt(Integer.valueOf(results.get(10))+1) == 1) || debugging){
									if(debugging){plugin.getLogger().info("Either debugging or got our random number!");}
									ItemStack item = new ItemStack( Material.matchMaterial(results.get(11)), 1);
									item.setDurability(Short.valueOf(results.get(12)));
									if(location.getBlock().equals(Material.AIR)){
										LivingEntity zombie1 = (LivingEntity) Bukkit.getWorld(results.get(0)).spawnEntity(location, EntityType.valueOf(results.get(5)));
					    		    	zombie1.getEquipment().setItemInMainHand(item);
					    		    	if(debugging){plugin.getLogger().info("Just spawned a zombie with a weapon!");}
					    		    	if(debugging){plugin.getLogger().info(zombie1.getLocation().toString());}
									}
									else{
										location.setY(location.getY()+2);
										if(location.getBlock().equals(Material.AIR)){
											LivingEntity zombie1 = (LivingEntity) Bukkit.getWorld(results.get(0)).spawnEntity(location, EntityType.valueOf(results.get(5)));
						    		    	zombie1.getEquipment().setItemInMainHand(item);
						    		    	if(debugging){plugin.getLogger().info("Just spawned a zombie with a weapon!");}
						    		    	if(debugging){plugin.getLogger().info(zombie1.getLocation().toString());}
										}
										else
										{
											location.setY(location.getY()-4);
											if(location.getBlock().equals(Material.AIR)){
												LivingEntity zombie1 = (LivingEntity) Bukkit.getWorld(results.get(0)).spawnEntity(location, EntityType.valueOf(results.get(5)));
							    		    	zombie1.getEquipment().setItemInMainHand(item);
							    		    	if(debugging){plugin.getLogger().info("Just spawned a zombie with a weapon!");}
							    		    	if(debugging){plugin.getLogger().info(zombie1.getLocation().toString());}
											}else
											{
												location.setY(location.getY()+2);
												LivingEntity zombie1 = (LivingEntity) Bukkit.getWorld(results.get(0)).spawnEntity(location, EntityType.valueOf(results.get(5)));
							    		    	zombie1.getEquipment().setItemInMainHand(item);
							    		    	if(debugging){plugin.getLogger().info("Just spawned a zombie with a weapon!");}
							    		    	if(debugging){plugin.getLogger().info(zombie1.getLocation().toString());}
											}
										}
									}
				    		    	
								}
								else{
									if(location.getBlock().equals(Material.AIR)){
										LivingEntity zombie1 = (LivingEntity) Bukkit.getWorld(results.get(0)).spawnEntity(location, EntityType.valueOf(results.get(5)));
					    		    	if(debugging){plugin.getLogger().info("Just spawned a zombie without a weapon!");}
					    		    	if(debugging){plugin.getLogger().info(zombie1.getLocation().toString());}
									}
									else{
										location.setY(location.getY()+2);
										if(location.getBlock().equals(Material.AIR)){
											LivingEntity zombie1 = (LivingEntity) Bukkit.getWorld(results.get(0)).spawnEntity(location, EntityType.valueOf(results.get(5)));
						    		    	if(debugging){plugin.getLogger().info("Just spawned a zombie without a weapon!");}
						    		    	if(debugging){plugin.getLogger().info(zombie1.getLocation().toString());}
										}
										else
										{
											location.setY(location.getY()-4);
											if(location.getBlock().equals(Material.AIR)){
												LivingEntity zombie1 = (LivingEntity) Bukkit.getWorld(results.get(0)).spawnEntity(location, EntityType.valueOf(results.get(5)));
							    		    	if(debugging){plugin.getLogger().info("Just spawned a zombie without a weapon!");}
							    		    	if(debugging){plugin.getLogger().info(zombie1.getLocation().toString());}
											}else
											{
												location.setY(location.getY()+2);
												LivingEntity zombie1 = (LivingEntity) Bukkit.getWorld(results.get(0)).spawnEntity(location, EntityType.valueOf(results.get(5)));
							    		    	if(debugging){plugin.getLogger().info("Just spawned a zombie without a weapon!");}
							    		    	if(debugging){plugin.getLogger().info(zombie1.getLocation().toString());}
											}
										}
									}
								}
		    		    	}
		    			}
		    		}
		    		else{
		    			if(debugging){plugin.getLogger().info("No data found...");}
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
