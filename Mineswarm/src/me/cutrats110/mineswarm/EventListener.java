package me.cutrats110.mineswarm;

//import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SplashPotion;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
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
//import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashMap;
import java.util.UUID;


@SuppressWarnings("deprecation")
public class EventListener implements Listener {
	public Plugin plugin;
	public Database db = null;
	private boolean debugging = true;
	private PotionObjects potions = null;
	private MineswarmTeams teams = null;
	private EventListenerHelper helper = null;
	HashMap<UUID,BukkitTask> downedPlayers = new HashMap<UUID,BukkitTask>();
	HashMap<UUID,MSPlayer> players = new HashMap<UUID,MSPlayer>();
	
	public EventListener(Plugin instance, MineswarmTeams teams, TeamBoards board, PotionObjects potions) {
		plugin = instance;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		debugging = plugin.getConfig().getBoolean("debugging");
		this.db = new Database(plugin);
		this.teams = teams;
		this.potions = potions;
		helper = new EventListenerHelper(board, teams, db, players, downedPlayers, plugin);
		board.setTeamData(helper);
		
	}
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {  
        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();
        
        if(players.get(playerID) == null) {
        	players.put(playerID, new MSPlayer(player.getName(), playerID));	
        } else {
        	player.setHealth(0);
        	helper.updateTeamBoard(player);
        }	
    }
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		helper.clearPlayerFromTPQue(player);
		helper.resetPlayerFromLastStand(player);
		helper.updateTeamBoard(player);
	}
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
    	event.getDrops().clear();
        Player player = event.getEntity();
        PlayerInventory inv= player.getInventory();
        inv.clear();
        inv.setArmorContents(new ItemStack[4]);
        inv.getItemInOffHand().setAmount(0);
        inv.getItemInMainHand().setAmount(0);
        inv.setExtraContents(new ItemStack[inv.getExtraContents().length]);
        helper.updateTeamBoard(player);
    }
    //TODO
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		//Player on Player
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) { helper.playerOnPlayerDamage(e); }
		//Player on Mob
		else if(e.getDamager() instanceof Player && !(e.getEntity() instanceof Player)) { helper.playerOnEntityDamage(e); }
		//Mob on Player
		else if(!(e.getDamager() instanceof Player) && e.getEntity() instanceof Player) { helper.entityOnPlayerDamage(e); }
		
		if(e.getDamager() instanceof Trident || e.getDamager() instanceof Arrow) {
			Projectile projetile = (Projectile) e.getDamager();
			if(projetile.getShooter() instanceof Player) {
				if(downedPlayers.get(((Player) projetile.getShooter()).getUniqueId()) != null) {
					e.setCancelled(true);
					return;
				}
				else 
				{
					if(e.getEntity() instanceof Player) {
						if(downedPlayers.get(((Player)e.getEntity()).getUniqueId()) != null) {
							e.setCancelled(true);
							return;
						}
						else
						{
							Player damagee = (Player) e.getEntity();
							if(db.selectZonePVP(damagee.getLocation().getBlockX(), damagee.getLocation().getBlockY(), damagee.getLocation().getBlockZ(), damagee.getLocation().getWorld().toString()) == false) {
								e.setCancelled(true);
								return;
							}
						}
					}
				}
			}
		}
		if(!e.isCancelled()) {
			if(e.getEntity() instanceof Player) {
				Player player = (Player) e.getEntity();
				if((player.getHealth() - e.getFinalDamage()) <= .5) {
					helper.setPlayerAsDown(player);
				}
				helper.updateTeamBoard(player);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@EventHandler(ignoreCancelled = true)
	public void OnEntityExplodeEvent(EntityExplodeEvent event) {
		if(plugin.getConfig().getBoolean("disable-all-explosions")) {
			//event.setCancelled(true);
			event.blockList().clear();
			return;
		}
		EntityType ent = event.getEntityType();
		if(plugin.getConfig().getBoolean("disable-creeper-explosions") && ent.equals(EntityType.CREEPER)) {
			event.blockList().clear();
			return;
		}
		if(plugin.getConfig().getBoolean("disable-dragon-explosions") && ent.equals(EntityType.ENDER_DRAGON)) {
			event.blockList().clear();
			return;
		}
		if(plugin.getConfig().getBoolean("disable-tnt-explosions") && (ent.equals(EntityType.MINECART_TNT) ||ent.equals(EntityType.PRIMED_TNT))) {
			event.blockList().clear();
			return;
		}
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
	public void onEDeath(EntityDeathEvent event) {
		if(event.getEntity() instanceof Player){ return; }
		
		if(event.getEntity().getKiller() instanceof Player) {
			//Player player = event.getEntity().getKiller();
			//SCORE
		}
		
		Entity killer = event.getEntity().getKiller();
		//Bukkit says "splash status depends on only on the potion item." Sense I am checking the potion item as long as this effectively works I will leave it 
		if (killer != null && (killer instanceof Player || killer instanceof Arrow || killer instanceof Trident || killer instanceof SplashPotion || killer instanceof PotionEffect)) 
		{
			if(killer instanceof PotionEffect) {
				plugin.getLogger().info("POTION EFFECT WORKS!");
			}
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

	//TODO Update to remove meta data usage.
	@EventHandler(priority = EventPriority.LOWEST)
    public void PickupItem(EntityPickupItemEvent  e) {
        ItemStack pickedUp = e.getItem().getItemStack();
        if (pickedUp.getType().equals(Material.BOOK) && pickedUp.hasItemMeta() && pickedUp.getItemMeta().getDisplayName().equals("Key")){     	
        	if(e.getEntity() instanceof Player) {
        		Player player = (Player)e.getEntity();
        		Inventory invt = player.getInventory();	  
        		if(invt.firstEmpty() == -1)
        		{
        			//LOOP TO SEE IF STACK OF KEYS ALREADY EXITS.
        			for(ItemStack is : invt){
        				if(is == null){continue;}
    					//Item is same as stack...
	        			if(is.getType().equals(Material.BOOK) && is.hasItemMeta() && is.getItemMeta().getLore().get(0).equals(pickedUp.getItemMeta().getLore().get(0)))
	        			{
        					player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You picked up a " + pickedUp.getItemMeta().getLore().get(0) + " Key");
        	        		try {teams.sendTeamMessage(teams.getTeam(player).getName(), player);									
        					}catch(Exception err) {}
        	        		
        					e.setCancelled(false);
        					return;
        				}
        			}
        			player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "A key dropped by you, but you can't carry it because your inventory is full.");
            		try {teams.sendTeamMessage(player.getMetadata("team_name").get(0).asString(), player);									
    				}catch(Exception err) {}
        		}
        		else
        		{
	        		player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You picked up a " + pickedUp.getItemMeta().getLore().get(0) + " Key");
	        		try {teams.sendTeamMessage(player.getMetadata("team_name").get(0).asString(), player);									
					}catch(Exception err) {}
        		}
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
    @SuppressWarnings("incomplete-switch")
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerClick(InventoryClickEvent event) {
    	if(event.getInventory().getType().equals(InventoryType.CRAFTING) && event.getSlot() >=-1) {
    		return;    		
    	}
    	
    	if(event.getSlot() < 0) {
    		event.setCancelled(true);
    		return;
    	}
    	try {
    		if(event.getInventory().getType().equals(InventoryType.ANVIL)) {
    			InventoryHolder ih = event.getInventory().getHolder();
    				Block block = (Block) ih;
    				if(debugging) {plugin.getLogger().info("Reparing Anvil on use...");}
    				block.setType(Material.ANVIL);
    		}
   	    	if(!event.getWhoClicked().getGameMode().equals(GameMode.CREATIVE)){
    			List<String> allowedItems = plugin.getConfig().getStringList("dropping-whitelist");
    			String currentItem = event.getCurrentItem().getType().toString().replace(" ", "_").toUpperCase();
   	    		switch(event.getInventory().getType()) {
	    			case BARREL:
	    				if(allowedItems.contains(currentItem)) {break;}
	    				event.setCancelled(true);
	    				break;
	    			case BEACON:
	    				if(allowedItems.contains(currentItem)) {break;}
	    				event.setCancelled(true);
	    				break;
	    			case BLAST_FURNACE:
	    				if(allowedItems.contains(currentItem)) {break;}
	    				event.setCancelled(true);
	    				break;
	    			case BREWING:
	    				if(allowedItems.contains(currentItem)) {break;}
	    				event.setCancelled(true);
	    				break;
	    			case CARTOGRAPHY:
	    				if(allowedItems.contains(currentItem)) {break;}
	    				event.setCancelled(true);
	    				break;
	    			case DISPENSER:
	    				if(allowedItems.contains(currentItem)) {break;}
	    				event.setCancelled(true);
	    				break;
	    			case DROPPER:
	    				if(allowedItems.contains(currentItem)) {break;}
	    				event.setCancelled(true);
	    				break;
	    			case GRINDSTONE:
	    				if(allowedItems.contains(currentItem)) {break;}
	    				event.setCancelled(true);
	    				break;
	    			case HOPPER:
	    				if(allowedItems.contains(currentItem)) {break;}
	    				event.setCancelled(true);
	    				break;
	    			case LECTERN:
	    				if(allowedItems.contains(currentItem)) {break;}
	    				event.setCancelled(true);
	    				break;
	    			case LOOM:
	    				if(allowedItems.contains(currentItem)) {break;}
	    				event.setCancelled(true);
	    				break;
	    			case STONECUTTER:
	    				if(allowedItems.contains(currentItem)) {break;}
	    				event.setCancelled(true);
	    				break;
	    			case SMOKER:
	    				if(allowedItems.contains(currentItem)) {break;}
	    				event.setCancelled(true);
	    				break;
	    		}
		    	if(getClickedInventory(event.getView(), event.getRawSlot()).getType().equals(InventoryType.CHEST)){  //Top peice...
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
		        }
		    	else{
		    		if(event.isShiftClick() && !allowedItems.contains(currentItem)){//AND CURRENT ITEM IS NOT IN LIST...
		    			event.setCancelled(true);
						return;
		    		}
		    		if(event.isLeftClick() && event.isShiftClick() && !allowedItems.contains(currentItem)){//AND CURRENT ITEM IS NOT IN LIST...
		    			event.setCancelled(true);
						return;
		    		}
		    		if(event.isRightClick() && event.isShiftClick() && !allowedItems.contains(currentItem)){//AND CURRENT ITEM IS NOT IN LIST...
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
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public void PotionSplashEvent(org.bukkit.event.entity.PotionSplashEvent event){		
		try {
			if(event.getPotion().getShooter() instanceof Player) {
				boolean effectPlayer = true;
				boolean effectMob = true;
				String playerClass = "default";
				
				Player shooter = (Player) event.getPotion().getShooter();
				//TODO
				//Change to class lookup
				//if(shooter.("class")) {
					//playerClass = shooter.getMetadata("class").get(0).asString();
				//}				
				if(downedPlayers.get(shooter.getUniqueId()) != null) {
					event.setCancelled(true);
					return;
				}

				List<PotionEffectType> effectTypes = new ArrayList<>();
				for (PotionEffect effect : event.getPotion().getEffects()) {effectTypes.add(effect.getType());}
				MakePotion potion = potions.getPotionByEffects(effectTypes);
				String potionName = potion.name;
								
				for(String configPotion : plugin.getConfig().getStringList(playerClass+"-potions-disabled-players")) {
					if(configPotion.equals(potionName)) {
						effectPlayer = false;
						break;
					}
				}
				for(String configPotion : plugin.getConfig().getStringList(playerClass+"-potions-disabled-mobs")) {
					if(potionName.equals(configPotion)) {
						effectMob = false;
						break;
					}
				}
				
				for(Entity ent : event.getAffectedEntities()) {
					if(ent instanceof Player && effectPlayer == true) {
				    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() 
				    	{
					    	public void run() 
					    	{
					    		Player mob = (Player) ent;
					    		for(PotionEffectType ef : potion.effectTypes) {
					    			mob.addPotionEffect(new PotionEffect(ef, (int) potion.duration, potion.amplifier));
					    		}
					    	}
				    	}, 1L);
						
					}
					if(ent instanceof LivingEntity && effectMob && !(ent instanceof Player)) {
				    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() 
				    	{
					    	public void run() 
					    	{
					    		LivingEntity mob = (LivingEntity) ent;
					    		for(PotionEffectType ef : potion.effectTypes) {
					    			mob.addPotionEffect(new PotionEffect(ef, (int) potion.duration, potion.amplifier), true);
					    		}
					    	}
				    	}, 1L);
					}
				}
				event.setCancelled(true);				
			}
		}catch(Exception er) {plugin.getLogger().info("Error with potion blocking: " + er.toString());}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onHealthRegen(EntityRegainHealthEvent event) {
		if(event.getEntityType().equals(EntityType.PLAYER)) {
			Player player = (Player) event.getEntity();
			try {
				helper.updateTeamBoard(player);
			}catch(NullPointerException np) {} catch(Exception err) {plugin.getLogger().info("Other Error");}		
		}

	}
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e) {
		for(Entity mob : e.getChunk().getEntities()) {
			mob.remove();
		}
	}
}