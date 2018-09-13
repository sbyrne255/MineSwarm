package me.cutrats110;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashMap;
import java.util.UUID;

public class EventListener implements Listener {
	public Plugin plugin;
	public Database db = null;
	private boolean debugging = true;
	private TeamBoards board;
	private MineswarmTeams teams = null;
	
	public EventListener(Plugin instance, MineswarmTeams teams) {
		plugin = instance;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		debugging = plugin.getConfig().getBoolean("debugging");
		this.db = new Database(plugin);
		this.teams = teams;
		this.board = new TeamBoards(plugin);
	}
	public EventListener(Plugin instance, MineswarmTeams teams, TeamBoards board) {
		plugin = instance;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		debugging = plugin.getConfig().getBoolean("debugging");
		this.db = new Database(plugin);
		this.teams = teams;
		this.board = board;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		//I need to save any meta data right now.
		
		
		Player player = event.getPlayer();
		if(player.hasMetadata("isdown") && player.getMetadata("isdown").get(0).asBoolean()) {
			player.setHealth(0);
		}
		if(downedPlayers.containsKey(player.getUniqueId())) {
			plugin.getLogger().info("HAS EVENT I NDOWNS" );
			downedPlayers.get(player.getUniqueId()).cancel();
			plugin.getLogger().info("CANNED" );
			downedPlayers.remove(player.getUniqueId());
			plugin.getLogger().info("REMOVED");
		}
		
		try {
			if(teams.tpQueue.containsKey(player.getUniqueId())) {
				teams.tpQueue.get(player.getUniqueId()).cancel();
				teams.tpQueue.remove(player.getUniqueId());
			}				
		}catch(Exception np) {}
		try {
			//Remove from owner position..
			if(teams.getTeamOwner(player.getMetadata("team_name").get(0).asString()).equals(player)) {
				if(debugging) {plugin.getLogger().info("Exiting player is team owner");}
				//Player is owner of team...
				teams.setNewTeamOwner(player.getMetadata("team_name").get(0).asString());
			}
		}catch(Exception err) {}
		try {db.updatePlayerData(event.getPlayer());}
		catch(Exception err) {plugin.getLogger().info("ERROR WITH DB... " + err.toString());}
		try {
			if(player.hasMetadata("team_name") && player.getMetadata("team_name").get(0).toString().length() >= 1) {
		    	board.makeScoreBoard(player.getMetadata("team_name").get(0).asString());
		    	board.setScoreboard(teams.getTeamMembers(player.getMetadata("team_name").get(0).asString()), player.getMetadata("team_name").get(0).asString());
		    }
		    else {
		    	if(debugging){plugin.getLogger().info("team_name NOT SET");}
		    }	
		}catch(NullPointerException np) {}
		catch(IllegalStateException ic) {}//Thrown due to player being disconnected, can't set a DCed players scoreboard, can update teams though.
		catch(Exception err) {
			plugin.getLogger().info("Problem leaving game" + err.toString());
		}
		
		
		
		
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {  
        Player player = event.getPlayer();
        teams.addUUID(player);
        //narrowed it down so there is no doubt, something in this DB code prevents players from going into last stand after leaving and rejoining....
        if(db.setPlayerData(player) == false) {
        	//Player did not exist, create them...
        	plugin.getLogger().info("MAKING NEW PLAYER....");
        	db.newPlayer(player);
        }
        
        try {
	        if(player.hasMetadata("team_name") && player.getMetadata("team_name").get(0).toString().length() >= 1) {//If player is part of a team...
	        	board.makeScoreBoard(player.getMetadata("team_name").get(0).asString());
	        	board.setScoreboard(teams.getTeamMembers(player.getMetadata("team_name").get(0).asString()), player.getMetadata("team_name").get(0).asString());
	        }
	        else {
	        	if(debugging){plugin.getLogger().info("team_name NOT SET");}
	        }
        }catch(NullPointerException np) {}
        catch(Exception err) {plugin.getLogger().info("Error on player join with Meta data: " + err.toString());}
    }
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		try {
	        try {
	        	downedPlayers.get(player.getUniqueId()).cancel();
				downedPlayers.remove(player.getUniqueId());
	        }catch(Exception err) {}
	        
	        player.setMetadata("isdown",new FixedMetadataValue(plugin, false));
	        player.setMetadata("hasdied",new FixedMetadataValue(plugin, false));
	        player.removeMetadata("class", plugin);
	        player.setWalkSpeed((float) .2);//0 to prevent walking...
	        player.setGlowing(false);
	        
			if(player.hasMetadata("team_name") && player.getMetadata("team_name").get(0).toString().length() >= 1) {//If player is part of a team...
	        	board.makeScoreBoard(player.getMetadata("team_name").get(0).asString());
	        	board.setScoreboard(teams.getTeamMembers(player.getMetadata("team_name").get(0).asString()), player.getMetadata("team_name").get(0).asString(), player, 20);
	        }
	        else {
	        	if(debugging){plugin.getLogger().info("team_name NOT SET");}
	        }
		}catch(NullPointerException np) {} catch(Exception err) {plugin.getLogger().info("Other Error");}			
	}
    
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getTo().getBlockX() == e.getFrom().getBlockX() && e.getTo().getBlockY() == e.getFrom().getBlockY() && e.getTo().getBlockZ() == e.getFrom().getBlockZ()) return; //The player hasn't moved
        if (teams.tpQueue.containsKey(e.getPlayer().getUniqueId())) {
        	teams.tpQueue.get(e.getPlayer().getUniqueId()).cancel();
        	teams.tpQueue.remove(e.getPlayer().getUniqueId());
            e.getPlayer().sendMessage("TP has been cancelled due to movement");
            return;
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
    	if (teams.tpQueue.containsKey(event.getEntity().getPlayer().getUniqueId())) {
        	teams.tpQueue.get(event.getEntity().getPlayer().getUniqueId()).cancel();
        	teams.tpQueue.remove(event.getEntity().getPlayer().getUniqueId());
            return;
        }
    	
    	event.getDrops().clear();
        Player player = event.getEntity();
        try {
        	downedPlayers.get(player.getUniqueId()).cancel();
			downedPlayers.remove(player.getUniqueId());
        }catch(Exception err) {}
        
        player.setMetadata("isdown",new FixedMetadataValue(plugin, false));
        player.setMetadata("hasdied",new FixedMetadataValue(plugin, false));
        player.removeMetadata("class", plugin);
        player.setWalkSpeed((float) .2);//0 to prevent walking...
        player.setGlowing(false);
        
        if(player.hasMetadata("deaths")) {
			player.setMetadata("deaths",new FixedMetadataValue(plugin, player.getMetadata("deaths").get(0).asInt() +1));
		}
		else {
			player.setMetadata("deaths",new FixedMetadataValue(plugin, 1));
		}
        
        try {
	    	//Update scoreboard now that player has been revived.		    	
	    	if(player.hasMetadata("team_name") && player.getMetadata("team_name").get(0).toString().length() >= 1) {//If player is part of a team...
	        	board.makeScoreBoard(player.getMetadata("team_name").get(0).asString());
	        	board.setScoreboard(teams.getTeamMembers(player.getMetadata("team_name").get(0).asString()), player.getMetadata("team_name").get(0).asString(), player, 0);
	        }
        }catch(Exception err) {}
        
    }
	@EventHandler
	public void onEDeath(EntityDeathEvent event) {
		
		if(event.getEntity().getKiller() instanceof Player) {
			Player player = event.getEntity().getKiller();
			try {
				if(player.hasMetadata("mobs_killed")) {
	    			player.setMetadata("mobs_killed",new FixedMetadataValue(plugin, player.getMetadata("mobs_killed").get(0).asString() + "\n" + event.getEntity().getType().toString()));
	    		}
	    		else {
	    			player.setMetadata("mobs_killed",new FixedMetadataValue(plugin, event.getEntity().getType().toString()));
	    		}
			}catch(NullPointerException np) {}
			catch(IndexOutOfBoundsException ibe) {}
		}
		
		
		if (event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player) 
		{
			Player player = event.getEntity().getKiller();
			if(debugging){player.sendMessage(event.getEntity().getType().toString());}
			
			List<String> mobConfig = plugin.getConfig().getStringList("key-dropping-mobs");			
			if(mobConfig.indexOf(event.getEntity().getType().toString()) != -1)
			{
				Random rand = new Random();
				if((rand.nextInt(Integer.valueOf(mobConfig.get(mobConfig.indexOf(event.getEntity().getType().toString())+1))))+1 == 1 || debugging){
					try{
						ItemStack book_drop = new ItemStack( Material.BOOK, 1);//Drops "key" (book
						//SQL SELECT ZONE...
						
						try{
							int x = event.getEntity().getLocation().getBlockX();
							int y = event.getEntity().getLocation().getBlockY();
							int z = event.getEntity().getLocation().getBlockZ();
							String world = event.getEntity().getLocation().getWorld().toString();
							String keyinfo = "";
							try{
								keyinfo = db.selectZoneLevel(x,y,z,world);
							}catch(Exception ers){
								plugin.getLogger().info(ers.toString());
							}
							List<String> lore = new ArrayList<>();
							lore.add(keyinfo);
							
							ItemMeta meta = book_drop.getItemMeta();
							meta.setDisplayName("Key");
							if(meta.hasLore()){meta.getLore().add(keyinfo);}//This lore should be the key level....
							else{meta.setLore(lore);}
							book_drop.setItemMeta(meta);
						}
						catch(Exception er){plugin.getLogger().info("Problem with setting custom name or lore in MOBKEYS: " + er.toString());}
						
						if(plugin.getConfig().getString("keys-drop-to") == "player"){
							//Drop on Player
							player.getLocation().getWorld().dropItem(player.getLocation(), book_drop);
						}
						else{
							//Drop on Zombie...
							event.getEntity().getLocation().getWorld().dropItem(player.getLocation(), book_drop);
						}
					}
					catch(Exception er){
						plugin.getLogger().info("Problem with drops: " + er.toString());
					}
					
				}				
			}
			try{
				if(plugin.getConfig().getStringList("item-dropping-mobs").contains(event.getEntity().getType().toString().toUpperCase())){
					List<String> configElement = plugin.getConfig().getStringList("mob-drops");
					
					for(int i = 0; i < configElement.size(); i+=3) {
						//i = 2
						//i 0
						//7-2 = 5
						Material item = Material.matchMaterial(configElement.get(i));						
						Random rand = new Random();				
						
						if((rand.nextInt(Integer.valueOf(configElement.get(i+1))))+1 == 1 || debugging){
							ItemStack item_drop = new ItemStack(item, Integer.valueOf(configElement.get(i+2)));//Drops "key" (book
							if(plugin.getConfig().getString("items-drop-to") == "player"){
								//Drop on Player
								player.getLocation().getWorld().dropItem(player.getLocation(), item_drop);
								break;
							}
							else{
								//Drop on Zombie...
								event.getEntity().getLocation().getWorld().dropItem(player.getLocation(), item_drop);
								break;
							}
						}
					}
				}
			}catch(Exception er){
				plugin.getLogger().info("Problem with random item drop: " + er.toString());
			}
		}
	}

  
	@EventHandler(priority = EventPriority.LOWEST)
    public void PickupItem(EntityPickupItemEvent  e) {
        ItemStack pickedUp = e.getItem().getItemStack();
        if (pickedUp.getType().equals(Material.BOOK) && pickedUp.hasItemMeta() && pickedUp.getItemMeta().getDisplayName().equals("Key")){
        	if(e.getEntity() instanceof Player) {
        		Player player = (Player)e.getEntity();
        		player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You picked up a " + pickedUp.getItemMeta().getLore().get(0) + " Key");
        		try {teams.sendTeamMessage(player.getMetadata("team_name").get(0).asString(), ChatColor.GOLD + "" + ChatColor.BOLD + player.getName() + " picked up a " + pickedUp.getItemMeta().getLore().get(0) + " Key", player);									
				}catch(Exception err) {}
        	}
        } 
    }
    public Inventory getClickedInventory(InventoryView view, int slot) {
    	try {
	        Inventory clickedInventory;
	        if (slot < 0) {
	            clickedInventory = null;
	        } else if (view.getTopInventory() != null && slot < view.getTopInventory().getSize()) {
	            clickedInventory = view.getTopInventory();
	        } else {
	            clickedInventory = view.getBottomInventory();
	        }
	        return clickedInventory;
    	}catch(Exception nope) {}
    	return null;
    }	 
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDrag(InventoryDragEvent event) { 
    	event.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerClick(InventoryClickEvent event) {   
    	try {
	    	//Got some null pointers here...
	    	if(!event.getWhoClicked().getGameMode().equals(GameMode.CREATIVE)){
		    	if(getClickedInventory(event.getView(), event.getRawSlot()).getType().equals(InventoryType.CHEST)){  //Top peice...
		    		//plugin.getLogger().info(getClickedInventory(event.getView(), event.getRawSlot()).getType().toString());
		    		if(!event.isShiftClick()){
		    			if(event.isLeftClick()){
		    				event.setCancelled(true);
		    				return;
		    			}
		    			if(event.isRightClick()){
		    				event.setCancelled(true);
		    				return;
		    			}
		    		}    		
		        }//https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/InventoryInteractEvent.html
		    	else{
		    		//plugin.getLogger().info(getClickedInventory(event.getView(), event.getRawSlot()).getType().toString());
		    		if(event.isShiftClick()){//AND CURRENT ITEM IS NOT IN LIST...
		    			event.setCancelled(true);
						return;
		    		}
		    		if(event.isLeftClick() && event.isShiftClick()){//AND CURRENT ITEM IS NOT IN LIST...
		    			event.setCancelled(true);
						return;
		    		}
		    		if(event.isRightClick() && event.isShiftClick()){//AND CURRENT ITEM IS NOT IN LIST...
		    			event.setCancelled(true);
						return;
		    		}
		    	}
	    	}
	    	
	    	try{
		    	if(event.getClick().isRightClick()){
		    		if(event.getAction().equals(InventoryAction.DROP_ONE_CURSOR) || event.getAction().equals(InventoryAction.DROP_ALL_CURSOR) ){
		    			event.getWhoClicked().getInventory().remove(event.getCurrentItem());
		    			//IF Item in acceptable drop list...
		    			if(plugin.getConfig().getBoolean("debugging")){plugin.getLogger().info(event.getCurrentItem().getType().toString().replace(" ", "_").toUpperCase());}
		    			if(plugin.getConfig().getStringList("dropping-whitelist").contains(event.getCurrentItem().getType().toString().replace(" ", "_").toUpperCase())){
		    				event.setCancelled(false);
		    				return;
		    			}
		    			else{
		    				event.setCancelled(true);
		    			}
		    		}
		    	}    
	    	}catch(Exception err){
	    		if(plugin.getConfig().getBoolean("debugging")){
	    			plugin.getLogger().info("Minor bump in inventory watching (RC) " + err.toString());
	    		}
	    	}
    	}catch(Exception nope) {}
    }
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDrop(PlayerDropItemEvent event) {
		if(plugin.getConfig().getBoolean("debugging")){plugin.getLogger().info(event.getItemDrop().getName().toString().replace(" ", "_").toUpperCase());}
		//IF item in accepted drop list...
		if(plugin.getConfig().getStringList("dropping-whitelist").contains(event.getItemDrop().getName().toString().replace(" ", "_").toUpperCase())){
			event.setCancelled(false);
			return;
		}
		else{
			event.setCancelled(true);
		} 
    }
	
	
	private PotionObjects potions = new PotionObjects();
	@EventHandler(priority = EventPriority.HIGH)
	public void PotionSplashEvent(org.bukkit.event.entity.PotionSplashEvent event){		
		//Shooter is player.
		try {
			if(event.getPotion().getShooter() instanceof Player) {
				Player shooter = (Player) event.getPotion().getShooter();
				if(shooter.hasMetadata("isdown") && shooter.getMetadata("isdown").get(0).asBoolean()) {
					event.setCancelled(true);
					return;
				}
				
				List<PotionEffectType> effectTypes = new ArrayList<>();
				for (PotionEffect effect : event.getPotion().getEffects()) {effectTypes.add(effect.getType());}
				String potionName = potions.getNameByEffects(effectTypes);
				if(!(plugin.getConfig().getList("globally-allowed-potions").contains(potionName))) {
				
					//Loop through all effect entities.
					for(Entity ent : event.getAffectedEntities()) {
						//If entity is a player get the effects and find out if the potion should effect the player or not (based on config)
						if(ent instanceof Player) {
							if(ent.hasMetadata("class") && plugin.getConfig().getList(ent.getMetadata("class").get(0).asString()+"-allowed-potions").contains(potionName)){
								//Item was in the allowed list for this class!
								plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() 
						    	{
							    	public void run() 
							    	{
										((Player) ent).addPotionEffects(event.getPotion().getEffects());
							    	}
						    	}, 1L);
							}
							else {
								//Potion is blocked from effecting players so continue with loop (event will be cancelled, mobs will be hurt in 1 tick)
								continue;						
							}
						}else {
							//ALTERNATIVE, CANCEL THE EVENT BUT SCHEDULE TO ADD THE EFFECT TO EVERY MOB 1TICK AFTER...
							//THIS WORKS, IT CANCELS ALL POTIONS ON PLAYERS SENT BY PLAYERS...
							//PROBABLY NEED TO ADD SOME EXECEPTIONS SUCH AS MEDICS BEING ALLOWED TO USE HEALING POTIONS?
							if(ent instanceof LivingEntity) {
						    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() 
						    	{
							    	public void run() 
							    	{
							    		LivingEntity mob = (LivingEntity) ent;
										mob.addPotionEffects(event.getPotion().getEffects());
							    	}
						    	}, 1L);
						    }
						}
					}
					event.setCancelled(true);
				}
				
			}
		}catch(Exception er) {}
	}
	
	//This section handles:
	//player revives...
	//Prevents arrows/tridents from hurting players
	//Prevents PVP from player to player in none PVP zones.
	//Prevents downed players from harming mobs
	//Scores damage done to mobs
	
	
	
	
	
	
	
	
	HashMap<UUID,BukkitTask> downedPlayers = new HashMap<>();
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		try {
			  if(e.getDamager() instanceof Trident) {
				  Projectile projetile = (Projectile) e.getDamager();
				  if(projetile.getShooter() instanceof Player) {
					  Player shooter = (Player) projetile.getShooter();
					  if(shooter.hasMetadata("isdown") && shooter.getMetadata("isdown").get(0).asBoolean()) {
						  e.setCancelled(true);
						  return;
					  }
				  }	  
			  }
			  if(e.getDamager() instanceof Arrow) {
				  Projectile projetile = (Projectile) e.getDamager();
				  if(projetile.getShooter() instanceof Player) {
					  Player shooter = (Player) projetile.getShooter();
					  if(shooter.hasMetadata("isdown") && shooter.getMetadata("isdown").get(0).asBoolean()) {
						  e.setCancelled(true);
						  return;
					  }
				  }	  
			  }
			
			//Player hit player...
			if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
				Player damagee = (Player) e.getEntity();
				Player damager = (Player) e.getDamager();
				
				//If damagee is down, check damager's hand and return after
				if(damagee.hasMetadata("isdown") && damagee.getMetadata("isdown").get(0).asBoolean() == true) {
					plugin.getLogger().info("Player is down...");
					//Player is down...
					if(damager.getInventory().getItemInMainHand().getType().equals(Material.PLAYER_HEAD) || damager.getInventory().getItemInOffHand().getType().equals(Material.PLAYER_HEAD) || damager.getInventory().getItemInMainHand().getType().equals(Material.WITHER_SKELETON_SKULL) || damager.getInventory().getItemInOffHand().getType().equals(Material.WITHER_SKELETON_SKULL)) {
						plugin.getLogger().info("Hitter has head in hand...");
						//Player has item in hand (is trying to revive)
					  	damagee.sendMessage("A medic has revived you!");
					  	damagee.setMetadata("isdown",new FixedMetadataValue(plugin, false));
					  	damagee.setMetadata("hasdied",new FixedMetadataValue(plugin, true));
					  	
					  	//Remove all debuffs we placed on downed player...
			        	for (PotionEffect effect : damagee.getActivePotionEffects()){damagee.removePotionEffect(effect.getType());}
			        	damagee.setHealth(10);//Reset health to half.
			        	damagee.setWalkSpeed((float) .2);//Set walkspeed to default
			        	damagee.setGlowing(false);//Remove glow.
			        	
			        	//CANCEL SCHEDULED DEATH!
			        	downedPlayers.get(damagee.getUniqueId()).cancel();
						downedPlayers.remove(damagee.getUniqueId());
			        	
			        	
			        	//Increment times revived.
			        	if(damagee.hasMetadata("revived")) {damagee.setMetadata("revived",new FixedMetadataValue(plugin, damagee.getMetadata("revived").get(0).asInt() + 1));}
			    		else {damagee.setMetadata("revived",new FixedMetadataValue(plugin, 1));}
			        	
			        	//Increment times player saved another player
			        	if(damager.hasMetadata("players_saved")) {damager.setMetadata("players_saved",new FixedMetadataValue(plugin, damager.getMetadata("players_saved").get(0).asInt() + 1));}
			    		else {damager.setMetadata("players_saved",new FixedMetadataValue(plugin, 1));}
						
			        				    
			        	//Remove head from player. [check what happens when they only have 1 head.]
				    	if(damager.getInventory().getItemInMainHand().getType().equals(Material.PLAYER_HEAD)) {
				    		ItemStack heads = new ItemStack(Material.PLAYER_HEAD, damager.getInventory().getItemInMainHand().getAmount() -1);
				    		damager.getInventory().setItemInMainHand(heads);
				    	}
				    	if(damager.getInventory().getItemInOffHand().getType().equals(Material.PLAYER_HEAD)) {
				    		ItemStack heads = new ItemStack(Material.PLAYER_HEAD, damager.getInventory().getItemInOffHand().getAmount() -1);
				    		damager.getInventory().setItemInOffHand(heads);
				    	}
				    	e.setCancelled(true);
				    	return;
					}
					
				}
				if(db.selectZonePVP(damagee.getLocation().getBlockX(), damagee.getLocation().getBlockY(), damagee.getLocation().getBlockZ(), damagee.getLocation().getWorld().toString()) == false) {
					//PVP is off where the damagee is standing
					e.setCancelled(true);
					return;
				}				
			}
			//If we reach here, at least one entity was not a player.
			//If one entity is Arrow and other is player, prevent damage to player in none PVP zones
			if(e.getDamager() instanceof Arrow && e.getEntity() instanceof Player) {
				Player damagee = (Player) e.getEntity();
				Arrow arrow = null;				
				arrow = (Arrow) e.getDamager();
				if(arrow.getShooter() instanceof Player && damagee instanceof Player && db.selectZonePVP(damagee.getLocation().getBlockX(), damagee.getLocation().getBlockY(), damagee.getLocation().getBlockZ(), damagee.getLocation().getWorld().toString()) == false) {
					e.setCancelled(true);
					return;
				}
			}
			//If one entity is Trident and other is player, prevent damage to player in none PVP zones
			if(e.getDamager() instanceof Trident && e.getEntity() instanceof Player) {
				Player damagee = (Player) e.getEntity();
				Trident trident = null;				
				trident = (Trident) e.getDamager();					
				if(trident.getShooter() instanceof Player && damagee instanceof Player && db.selectZonePVP(damagee.getLocation().getBlockX(), damagee.getLocation().getBlockY(), damagee.getLocation().getBlockZ(), damagee.getLocation().getWorld().toString()) == false) {
					e.setCancelled(true);
					return;
				}
			}
			
			//Attacker is player, if he is down cancel, if he is not calculate damage done and score it.
			if(e.getDamager() instanceof Player && !(e.getEntity() instanceof Player)){
				//Damager is a player, damagee is not.
				Player damager = (Player)e.getDamager();
				LivingEntity damagee = (LivingEntity) e.getEntity();
				
				//Damager is NOT down [increments damage delt
				if (damager.hasMetadata("isdown") && damager.getMetadata("isdown").get(0).asBoolean() == false) {
					//Increment damager's score.
					if(damager.hasMetadata("total_damage_delt")) {damager.setMetadata("total_damage_delt",new FixedMetadataValue(plugin, damager.getMetadata("total_damage_delt").get(0).asInt() + damagee.getLastDamageCause().getDamage()));}
			    	else {damager.setMetadata("total_damage_delt",new FixedMetadataValue(plugin, damagee.getLastDamageCause().getDamage()));}
				}
				else {
					e.setCancelled(true);
					return;
				}				
			}
			//Mob on Player violence, prevents downed player from taking damage
			if(!(e.getDamager() instanceof Player) && e.getEntity() instanceof Player){
				//Damager is NOT a player, but damagee is
				Player damagee = (Player) e.getEntity();
				
				if(damagee.hasMetadata("isdown") && damagee.getMetadata("isdown").get(0).asBoolean()) {
					e.setCancelled(true);
					return;
				}else {
			    	//Update scoreboard now that player has been revived.		    	
			    	if(damagee.hasMetadata("team_name") && damagee.getMetadata("team_name").get(0).toString().length() >= 1) {//If player is part of a team...
			        	board.makeScoreBoard(damagee.getMetadata("team_name").get(0).asString());
			        	board.setScoreboard(teams.getTeamMembers(damagee.getMetadata("team_name").get(0).asString()), damagee.getMetadata("team_name").get(0).asString(), damagee, (int) ((damagee.getHealth()-e.getFinalDamage())));
			        }
				}
			}
			
		}
		catch(Exception err) {
			if(debugging) {plugin.getLogger().info("Not sure why, but we got an error: " + err.toString());}
		}
	}
	@EventHandler
	public void onEDamage(EntityDamageEvent e) {
		//Player is being damaged...
		if(e.getEntity() instanceof Player) {
			Player damagee = (Player) e.getEntity();
			
			//Update total damage taken...
			if(damagee.hasMetadata("total_damage_taken")) {damagee.setMetadata("total_damage_taken",new FixedMetadataValue(plugin, damagee.getMetadata("total_damage_taken").get(0).asInt() + damagee.getLastDamage()));}
    		else {damagee.setMetadata("total_damage_taken",new FixedMetadataValue(plugin, damagee.getLastDamage()));}
			
		if(e.getEntity() instanceof Player) {
			if(damagee.hasMetadata("isdown") && damagee.getMetadata("isdown").get(0).asBoolean()) {
				//Champ is down; continue to apply damage
				e.setCancelled(true);
				//Update scoreboard.
				if(damagee.hasMetadata("team_name") && damagee.getMetadata("team_name").get(0).toString().length() >= 1) {//If player is part of a team...
		        	board.makeScoreBoard(damagee.getMetadata("team_name").get(0).asString());
		        	board.setScoreboard(teams.getTeamMembers(damagee.getMetadata("team_name").get(0).asString()), damagee.getMetadata("team_name").get(0).asString(), damagee, (int) ((damagee.getHealth()-e.getFinalDamage())*-1));
		        }
				
			}
			else {
				if((damagee.getHealth() - e.getFinalDamage()) <= .5) {
					try {teams.alertTeamOfDowns(damagee.getMetadata("team_name").get(0).asString(), damagee);									
					}catch(Exception err) {}
					
					//Reset health, set meta data, set walk speed, add potions, set glow, set sneaking,
					damagee.setHealth(20);//Full health for last stand...
					damagee.setMetadata("hasdied", new FixedMetadataValue(plugin, true));//Set has died before applying damage...
					damagee.setMetadata("isdown",new FixedMetadataValue(plugin, true));
					damagee.setWalkSpeed(0);//0 to prevent walking...
					damagee.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10000, 1));
					damagee.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000, 250));
					damagee.setGlowing(true);
					damagee.setSneaking(true);
					
					//Increment downs score.
					if(damagee.hasMetadata("downs")) {damagee.setMetadata("downs",new FixedMetadataValue(plugin, damagee.getMetadata("downs").get(0).asInt() + 1));}
		    		else {damagee.setMetadata("downs",new FixedMetadataValue(plugin, 1));}
					if(damagee.hasMetadata("team_name") && damagee.getMetadata("team_name").get(0).toString().length() >= 1) {//If player is part of a team...
			        	board.makeScoreBoard(damagee.getMetadata("team_name").get(0).asString());
			        	board.setScoreboard(teams.getTeamMembers(damagee.getMetadata("team_name").get(0).asString()), damagee.getMetadata("team_name").get(0).asString(), damagee, (int) ((damagee.getHealth()-e.getFinalDamage())*-1));
			        }
					
					downedPlayers.put(damagee.getUniqueId(), Bukkit.getScheduler().runTaskTimer(plugin, () -> {
						try {
						damagee.setHealth(damagee.getHealth() - 1);
						if(damagee.hasMetadata("team_name") && damagee.getMetadata("team_name").get(0).toString().length() >= 1) {//If player is part of a team...
				        	board.makeScoreBoard(damagee.getMetadata("team_name").get(0).asString());
				        	board.setScoreboard(teams.getTeamMembers(damagee.getMetadata("team_name").get(0).asString()), damagee.getMetadata("team_name").get(0).asString(), damagee, (int) ((damagee.getHealth()-1)*-1));
				        }
						}catch(Exception err) {
							damagee.setHealth(0);
						}
					}, 10, 35));//delay before first run, sequential runs after...
					
					
					e.setCancelled(true);
					return;
				}
			}
		}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onHealthRegen(EntityRegainHealthEvent event) {
		if(event.getEntityType().equals(EntityType.PLAYER)) {
			Player player = (Player) event.getEntity();
			try {
				if(player.hasMetadata("team_name") && player.getMetadata("team_name").get(0).toString().length() >= 1) {//If player is part of a team...
		        	board.makeScoreBoard(player.getMetadata("team_name").get(0).asString());
		        	board.setScoreboard(teams.getTeamMembers(player.getMetadata("team_name").get(0).asString()), player.getMetadata("team_name").get(0).asString(), player, (int) (player.getHealth() + event.getAmount()));
		        }
			}catch(NullPointerException np) {} catch(Exception err) {plugin.getLogger().info("Other Error");}		
		}

	}
}