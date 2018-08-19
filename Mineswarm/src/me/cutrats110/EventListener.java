package me.cutrats110;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
//import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventListener implements Listener {
	public Plugin plugin;
	public Database db = null;
	private boolean debugging = true;
	
	public EventListener(Plugin instance) {
		plugin = instance;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		debugging = plugin.getConfig().getBoolean("debugging");
		this.db = new Database(plugin);
	}
    
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
        // Called when a player leaves a server
        //Player player = event.getPlayer();
        //plugin.getLogger().info(player.getMetadata("isdown").get(0).asString());
        //This has a captured object in memory including player MetaData, on exit back this up to SQLIte.
        //Only time MetaData won't be saved in on server crash (on disabled save meta data for all online players)...
        
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        player.setMetadata("isdown",new FixedMetadataValue(plugin, false));
        player.setMetadata("hasdied",new FixedMetadataValue(plugin, false));
        player.setWalkSpeed((float) .2);//0 to prevent walking...
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        player.setMetadata("isdown",new FixedMetadataValue(plugin, false));
        player.setMetadata("hasdied",new FixedMetadataValue(plugin, false));
        player.setWalkSpeed((float) .2);//0 to prevent walking...
        player.setGlowing(false);
    }
    public Inventory getClickedInventory(InventoryView view, int slot) {
        Inventory clickedInventory;
        if (slot < 0) {
            clickedInventory = null;
        } else if (view.getTopInventory() != null && slot < view.getTopInventory().getSize()) {
            clickedInventory = view.getTopInventory();
        } else {
            clickedInventory = view.getBottomInventory();
        }
        return clickedInventory;
    }	 
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDrag(InventoryDragEvent event) { 
    	event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerClick(InventoryClickEvent event) {     
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
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		Entity damager = e.getDamager();
		Entity damageTaker = e.getEntity();
		
		if (damageTaker instanceof Player) { Player taker = (Player) damageTaker;
		    if (damager instanceof Player) { Player damagerPlayer = (Player) damager;
		        if(damagerPlayer.getName() != "bob" && taker.getMetadata("isdown").get(0).asBoolean()){//Check if player is medic, and or on team.
			    	taker.sendMessage("HE IS MEDIC");
		        	taker.setMetadata("isdown",new FixedMetadataValue(plugin, false));
		        	taker.setMetadata("hasdied",new FixedMetadataValue(plugin, true));
		        	for (PotionEffect effect : taker.getActivePotionEffects()){
		        		taker.removePotionEffect(effect.getType());
		        	}
		        	taker.setHealth(10);
		        	taker.setWalkSpeed((float) .2);//0 to prevent walking...
		        	taker.setGlowing(false);
		        	e.setCancelled(true);
		        	return;
		        }
		        else
		        {
		        	//IF region PVP ON
		        	if(db.selectZonePVP(taker.getLocation().getBlockX(), taker.getLocation().getBlockY(), taker.getLocation().getBlockZ(), taker.getLocation().getWorld().toString()) && db.selectZonePVP(damagerPlayer.getLocation().getBlockX(), damagerPlayer.getLocation().getBlockY(), damagerPlayer.getLocation().getBlockZ(), damagerPlayer.getLocation().getWorld().toString()))
					{
		        		e.setCancelled(false);
		        	}
		        	else{
		        		e.setCancelled(true);
		        	}
		        }
		    }
		    else{
		    	if(taker.getMetadata("isdown").get(0).asBoolean()){
		    		e.setCancelled(true);
		    		return;
		    	}
		    }
		}
		
		if(damager instanceof Player && !(damageTaker instanceof Player) && damager.getMetadata("isdown").get(0).asBoolean()){
			//Player on Mob violence...
			e.setCancelled(true);
		}
		if(!(damager instanceof Player) && damageTaker instanceof Player && damageTaker.getMetadata("isdown").get(0).asBoolean()){
			//Mob on Downed Player violence...
			e.setCancelled(true);
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void dmg(EntityDamageEvent event) 
	{
		Entity e = event.getEntity();
		if(e instanceof Player) 
		{
			Player player = (Player)e;
			if(player.hasMetadata("isdown"))
			{
				if(player.getMetadata("isdown").get(0).asBoolean())
				{
					if(player.getLastDamageCause().getEntity().equals(player))
					{
						if(player.getHealth() <= 1.5){
							player.setHealth(0);
							return;
						}
						else
						{
							try{
								if(player.getHealth() <= 20)
								{
									player.setHealth(player.getHealth());
									return;
								} else {
									player.setHealth(player.getHealth()+1);
									return;
								}
							}catch(Exception errr){}
						}
						event.setCancelled(false);
					}
					else{
						event.setCancelled(true);
					}
					
				}
				else
				{
					if((player.getHealth() - player.getLastDamage())<= 1.5 && player.getMetadata("hasdied").get(0).asBoolean() == false){
						//Maybe set player to non-targetable, heal player, then use potion of damage to decrease health, then remove all potion effects when healed by medic?
						player.setMetadata("isdown",new FixedMetadataValue(plugin, true));
						player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
						player.setWalkSpeed(0);//0 to prevent walking...
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000, 1));
						player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000, 250));
						player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 1000, 1));//Start damaging player...
						player.setGlowing(true);
						player.setSneaking(true);
						event.setCancelled(true);
					}
					else
					{
						event.setCancelled(false);
					}
				}
			}
		}
		//IF it's not a player...

	}
	
	
	
	@EventHandler
	public void onEDeath(EntityDeathEvent event) {
		if (event.getEntity().getKiller() != null) 
		{
			Player player = event.getEntity().getKiller();
			if(debugging){player.sendMessage(event.getEntity().getType().toString());}
			
			List<String> mobConfig = plugin.getConfig().getStringList("key-dropping-mobs");			
			if(mobConfig.indexOf(event.getEntity().getType().toString()) != -1){
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
	
	
}
