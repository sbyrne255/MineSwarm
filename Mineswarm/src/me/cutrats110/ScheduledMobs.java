package me.cutrats110;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class ScheduledMobs implements Listener {
	public Plugin plugin;
	public Database db = null;
	private boolean debugging = true;
	private PotionObjects po = null;
	public transient HashMap<Location, List<LivingEntity>> spawners = new HashMap<>();
	
	
	//Can't serialize entityes, but can I get Entity ID and serialize that, then deserialize and set spanwers to Location (from XYZ) and loop IDs getting entity
	
	public ScheduledMobs(Plugin instance, PotionObjects po) {
		plugin = instance;
		debugging = plugin.getConfig().getBoolean("debugging");
		this.db = new Database(plugin);
		this.po = po;
	}
	public void startMobs() {
		for(World world : plugin.getServer().getWorlds()) {
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "killall mobs "+world.getName());
		}
		mobSpawns();		
	}
	
	public void mobSpawns(){
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
		    	if(debugging){plugin.getLogger().info("I am a scheduled task, running at a scheduled time!");}	
		    	
		    	//IF LIST SIZE IS SMALLER THAN MAX MOBS (ADJUST LATER) LOOP THROUGH DIFFERENCES SPAWNING MORE UNTIL IT IS THE SAME...
		    	//IF LIST SIZE IS BIGGER THAN MAX MOBS, REMOVE ENTRIES UNTIL IT FITS.
		    	
		    	//					KEY/SPAWN  % CHANCE			FOR WEAPONS		 FOR MOB 	MOBTYPE 	Max number
		    	//DB should return, Location, weapon Chance, Weapon Durability, Weapon, Entity Type, Max Entities
		    	
		    	//Instead of advanced for loop, just normal loop, then set the value of this position instead of deleting it.
		    	List<String> data = db.getMobSpawners();
		    	if(data == null) {
		    		plugin.getLogger().info("No data found");
		    		return;
		    	}
		    	//Loop each DB entrie...
		    	for(int d = 0; d < data.size()-8; d+=9) {
		    		//Prep all global variables...
		    		List<String> loc = null;
		    		String world = "";
		    		Location location = null;
		    		String entityType = "";
		    		int maxEntities = 0;
		    		int chance = 0;
		    		String weapon = "";
		    		short dura = 0;
		    		List<String> enchantments = new ArrayList<>();
		    		List<Integer> effects = new ArrayList<>();
		    		List<LivingEntity> entities = new ArrayList<>();
		    		//Set variables here...
		    		try {
			    		loc = Arrays.asList(data.get(d).split("\\s*,\\s*"));
			    		world = data.get(d+1);
			    		location = new Location(Bukkit.getWorld(world), Integer.valueOf(loc.get(0)), Integer.valueOf(loc.get(1)), Integer.valueOf(loc.get(2)));
			    		entityType = data.get(d+2);
			    		maxEntities = Integer.valueOf(data.get(d+3));
			    		chance = Integer.valueOf(data.get(d+4));
			    		weapon = data.get(d+5);
			    		dura = Short.valueOf(data.get(d+6));
			    		if(data.get(d+7) != "NONE" && data.get(d+7) != null) {
			    			enchantments = Arrays.asList(data.get(d+7).split("\\s*,\\s*"));	
			    		}
			    		if(data.get(d+8) != "NONE" && data.get(d+8) != null) {
			    			try {
			    				for (String field : data.get(d+8).split("\\s*,\\s*")) {if(field != "NONE") {effects.add(Integer.parseInt(field));}}
			    			}catch(NumberFormatException nfe) {}
			    		}
			    		
			    		
		    		}catch(NullPointerException np) {
		    			plugin.getLogger().info("NP IN  DB INFO FOR MOB SPAWNERS " + np.toString());
		    		}
		    		try {
		    			entities = spawners.get(location);
		    			if(entities == null) {
		    				entities = new ArrayList<>();
		    			}
		    		}catch(NullPointerException np) {}
		    		
		    		//If there are more entities we are ready to spawn than our DB's max size (normally from altering the DB directly), remove the last ones...
		    		while(entities.size() > maxEntities) {
		    			entities.remove(entities.size()-1);		
		    			if(debugging) {plugin.getLogger().info("Entities are too many, removing...");}
		    		}
		    		
		    		//Loop through all known entities and check if we need to spawn more...
		    		for(int i = 0; i< entities.size(); i++) {
		    			LivingEntity entity = entities.get(i);
		    			if(entity.isDead()) {//Spawn new guy
		    				if(chance != 0){
		    		    		Random rand = new Random();				
								if( (rand.nextInt(chance)+1 == 1) || debugging)
								{
									ItemStack item = new ItemStack( Material.matchMaterial(weapon), 1);
									item.setDurability(dura);
									
									if(!enchantments.isEmpty()) {
										//Enchantments are not empty...
										for(String enchantment : enchantments) {
											String[] en = enchantment.split(":");
											item.addEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(en[0].toLowerCase())), Integer.valueOf(en[1]));
										}
									}
									
									LivingEntity mob = (LivingEntity) Bukkit.getWorld(world).spawnEntity(location, EntityType.valueOf(entityType));
				    		    	mob.getEquipment().setItemInMainHand(item);
				    		    	if(!effects.isEmpty()) {
				    		    		for(int pid : effects) {			
				    		    			MakePotion poData = po.getDrinkableDataById(pid);
				    		    			for(PotionEffectType effect : poData.effectTypes) {
				    		    				//mob.setCustomName("PuffyCloud");
				    		    		        //mob.setCustomNameVisible(true);
				    	    					mob.addPotionEffect((new PotionEffect(effect, Integer.MAX_VALUE, poData.amplifier, true)));
				    	    				}
				    		    		}
				    		    	}
				    		    	entities.set(i, mob);
								}
								else{
									LivingEntity mob = (LivingEntity) Bukkit.getWorld(world).spawnEntity(location, EntityType.valueOf(entityType));
									if(!effects.isEmpty()) {
				    		    		for(int pid : effects) {			
				    		    			MakePotion poData = po.getDrinkableDataById(pid);
				    		    			for(PotionEffectType effect : poData.effectTypes) {
				    		    				//mob.setCustomName("PuffyCloud");
				    		    		        //mob.setCustomNameVisible(true);
				    	    					mob.addPotionEffect((new PotionEffect(effect, Integer.MAX_VALUE, poData.amplifier, true)));
				    	    				}
				    		    		}
				    		    	}
									entities.set(i, mob);
								}
		    		    	}
		    		    	else{
								LivingEntity mob = (LivingEntity) Bukkit.getWorld(world).spawnEntity(location, EntityType.valueOf(entityType));
								if(!effects.isEmpty()) {
			    		    		for(int pid : effects) {			
			    		    			MakePotion poData = po.getDrinkableDataById(pid);
			    		    			for(PotionEffectType effect : poData.effectTypes) {
			    		    				//mob.setCustomName("PuffyCloud");
			    		    		        //mob.setCustomNameVisible(true);
			    	    					mob.addPotionEffect((new PotionEffect(effect, Integer.MAX_VALUE, poData.amplifier, true)));
			    	    				}
			    		    		}
			    		    	}
								entities.set(i, mob);
							}
		    			}
		    		}
		    		
		    		//If we spanwed all the entities, but we're short (normally if DB is altered directly) spawn more until list matches max size.
		    		while(entities.size() < maxEntities) {
		    			if(debugging) {plugin.getLogger().info(String.valueOf(entities.size()) + "Not enough entities, looping to add more...");}
		    			LivingEntity mob = null;
		    			if(chance != 0){
	    		    		Random rand = new Random();		
							if( (rand.nextInt(chance+1) == 1) || debugging)
							{
								ItemStack item = new ItemStack( Material.matchMaterial(weapon), 1);
								item.setDurability(dura);
								
								mob = (LivingEntity) Bukkit.getWorld(world).spawnEntity(location, EntityType.valueOf(entityType));
			    		    	mob.getEquipment().setItemInMainHand(item);
			    		    	entities.add(mob);
							}
							else{
								mob = (LivingEntity) Bukkit.getWorld(world).spawnEntity(location, EntityType.valueOf(entityType));
								entities.add(mob);
							}
	    		    	}
	    		    	else{
							mob = (LivingEntity) Bukkit.getWorld(world).spawnEntity(location, EntityType.valueOf(entityType));
							entities.add(mob);
						}
		    		}
		    		
		    		//Update Spanwers data.
		    		spawners.put(location, entities);
		    	}
		    	
		    	//Get everything from DB (Locations all I need...)
		    	//Loop throuw locations (key) selecting all the List<Entities>
		    	//Loop through each entity
		    	//IF ENTITY.IsDead is true, response a new one at LOCATION, else continue...
		    }
		}, 20, (20*20));//Delay from first start, repeats every X
		//20t = 1s
		//20*10 = 10 seconds...
	}
	
	
	
	
	
	
	
}
