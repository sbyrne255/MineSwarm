package me.cutrats110;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventListener implements Listener {
	public Plugin plugin;

	public EventListener(Plugin instance) {
		plugin = instance;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
    
	@SuppressWarnings("unused")
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
        // Called when a player leaves a server
        Player player = event.getPlayer();
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
    }
    
    
    @EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		Entity damager = e.getDamager();
		Entity damageTaker = e.getEntity();
		
		if (damageTaker instanceof Player) {
		    Player taker = (Player) damageTaker;
		    if (damager instanceof Player) {
		        Player damagerPlayer = (Player) damager;
		        if(damagerPlayer.getName() != "bob" && taker.getMetadata("isdown").get(0).asBoolean()){//Check if player is medic, and or on team.
			    	taker.sendMessage("HE IS MEDIC");
		        	taker.setMetadata("isdown",new FixedMetadataValue(plugin, false));
		        	taker.setMetadata("hasdied",new FixedMetadataValue(plugin, true));
		        	for (PotionEffect effect : taker.getActivePotionEffects()){
		        		taker.removePotionEffect(effect.getType());
		        	}
		        	taker.setHealth(10);
		        	taker.setWalkSpeed((float) .2);//0 to prevent walking...
		        	e.setCancelled(true);
		        	return;
		        }
		        else
		        {
		        	damager.sendMessage("SADFSADF");
		        	e.setCancelled(true);
		        }
		    }
		}
		
		if(damager instanceof Player && !(damageTaker instanceof Player) && damager.getMetadata("isdown").get(0).asBoolean()){
			//Player on Mob violence...
			e.setCancelled(true);
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void dmg(final EntityDamageEvent event) 
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
						//if(String.valueOf(player.getHealth()).equals("1.0") || String.valueOf(player.getHealth()).equals("2.0") || String.valueOf(player.getHealth()).equals("1.5")){
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
								} else {
									player.setHealth(player.getHealth()+.5);
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

	}
	
}
