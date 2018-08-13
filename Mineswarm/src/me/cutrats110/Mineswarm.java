package me.cutrats110;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Door;
import org.bukkit.material.Openable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class Mineswarm extends JavaPlugin implements Listener{
	private boolean debugging = true;
	private boolean preventDouble = true;
	@Override
	public void onEnable(){
		getLogger().info("Mineswarm is starting...");
		
	    getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("Mineswarm has been enabled");
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		try{
			Player player = event.getPlayer();
			if(preventDouble){
				try{					
					if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType().toString().toLowerCase().replace("_", "").contains("irondoor")) {
						Block block = event.getClickedBlock();
						BlockState state = block.getState();
						
						if (player.getInventory().getItemInMainHand().getItemMeta().getLore().contains("Key") || player.getInventory().getItemInOffHand().getItemMeta().getLore().contains("Key")){//getItemInOffHand
							player.sendMessage("You got yourself a mob key!");//Gotta decide how to give mob keys, either based on rooms, number of kills, ect
							
							//If done by room, setup an event for either door opening and check every 30 seconds until they enter or leave the room
							
							//Maybe make a control block proximity command that issues as the server to the nearest player/s setting their meta data to that room/region?
							
						}
						else
						{
							player.sendMessage("NOPE you don't got yourself a mob key!");
						}
						
						try{
				            state = block.getRelative(BlockFace.DOWN).getState();
				            Openable door = (Openable)state.getData();
				            door.setOpen(true);
				            state.setData((Door)door);
				            state.update();
						}catch(Exception err){
							state = event.getClickedBlock().getState();
				            Openable door = (Openable)state.getData();
				            door.setOpen(true);
				            state.setData((Door)door);
				            //state.setData((MaterialData)door);
				            state.update();
						}
						
						//Schedule Close
					    getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					    	public void run() {
					    		BlockState state = block.getState();
								try{
						            state = event.getClickedBlock().getRelative(BlockFace.DOWN).getState();
						            Openable door = (Openable)state.getData();
						            door.setOpen(false);
						            state.setData((Door)door);
		//				            state.setData((MaterialData)door);
						            state.update();
								}catch(Exception err){
									state = event.getClickedBlock().getState();
						            Openable door = (Openable)state.getData();
						            door.setOpen(false);
						            state.setData((Door)door);
						            //state.setData((MaterialData)door);
						            state.update();
								}
					    	}
					    }, 45L);
						
						event.setCancelled(true);//Probably want to cancel but shouldn't matter in adventure mode
				}
				}
				catch(Exception err)
				{
					getLogger().info("Problem with opening door in MineSwarm. " + err.toString());
				}
				preventDouble = false; }else{ preventDouble = true;	}
			
			if((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) && (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("MCMS Marking Tool"))){
				try{
					if(event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("MCMS Marking Tool")){
						player.setMetadata("pos2x",new FixedMetadataValue(this,event.getClickedBlock().getLocation().getBlockX()));
						player.setMetadata("pos2z",new FixedMetadataValue(this,event.getClickedBlock().getLocation().getBlockZ()));
						player.setMetadata("pos2y",new FixedMetadataValue(this,event.getClickedBlock().getLocation().getBlockY()));
					}
				}catch(Exception er){player.sendMessage("Error on right hand: " + er.toString());}
				try{
					if(event.getAction() == Action.LEFT_CLICK_BLOCK && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("MCMS Marking Tool")){
						player.setMetadata("pos1x",new FixedMetadataValue(this,event.getClickedBlock().getLocation().getBlockX()));
						player.setMetadata("pos1z",new FixedMetadataValue(this,event.getClickedBlock().getLocation().getBlockZ()));
						player.setMetadata("pos1y",new FixedMetadataValue(this,event.getClickedBlock().getLocation().getBlockY()));
					}
				}catch(Exception er){player.sendMessage("Error on right hand: " + er.toString());}
				
				event.setCancelled(true);//Probably want to cancel but shouldn't matter in adventure mode
			}
		}catch(Exception noncrit){
			//getLogger().info("Some uncaught error, only happens when hand is empt right clicking; probably bukkit.");
		}
	}

	@EventHandler
	public void onEDeath(EntityDeathEvent event) {
		if (event.getEntity().getKiller() != null) 
		{
			Player player = event.getEntity().getKiller();
			player.sendMessage(event.getEntity().getType().toString());
			
			if(event.getEntity().getType().toString() == "ZOMBIE"){//Zombie was killed...
				Random rand = new Random();
				if((rand.nextInt(12))+1 == 1 || debugging){
					try{
						ItemStack book_drop = new ItemStack( Material.BOOK, 1);//Drops "key" (book
						
						try{
							List<String> lore = new ArrayList<>();
							lore.add("Key HERE");
							
							ItemMeta meta = book_drop.getItemMeta();
							meta.setDisplayName("Key");
							if(meta.hasLore()){meta.getLore().add("Key Level 1");}
							else{meta.setLore(lore);}
							book_drop.setItemMeta(meta);
						}
						catch(Exception er){getLogger().info("Problem with setting custom name or lore in MOBKEYS: " + er.toString());}
						
						 
						player.getLocation().getWorld().dropItem(player.getLocation(), book_drop);
					}
					catch(Exception er){
						getLogger().info("Problem with drops: " + er.toString());
					}
					
				}				
			}
		}
	}
	
	@Override
	public void onDisable(){
		getLogger().info("Mineswarm has been disabled");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){	
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("doorlevel") && sender instanceof Player){
			Block block = player.getTargetBlock(null, 100);
			Location bl = block.getLocation();
			player.sendMessage(bl.toString());
			
			
			return true;
		}
		//MCMS Marking Tool
		if (cmd.getName().equalsIgnoreCase("markingtool") && sender instanceof Player){
			ItemStack markingTool = new ItemStack( Material.GOLDEN_HOE, 1);
			List<String> lore = new ArrayList<>();
			lore.add("Marking Tool: Left click POS1, Right click POS2");
			
			ItemMeta meta = markingTool.getItemMeta();
			meta.setDisplayName("MCMS Marking Tool");
			if(meta.hasLore()){meta.getLore().add("Marking Tool: Left click POS1, Right click POS2");}
			else{meta.setLore(lore);}
			markingTool.setItemMeta(meta);
			player.getLocation().getWorld().dropItem(player.getLocation(), markingTool);			
			
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("showzone") && sender instanceof Player){			
			try{
				//args[0];
				if (player.hasMetadata("pos1x") && player.hasMetadata("pos1z")){
					player.sendMessage("POS 1: X=" + player.getMetadata("pos1x").get(0).asString() + " Z=" + player.getMetadata("pos1z").get(0).asString());
					player.sendMessage("POS 2: X=" + player.getMetadata("pos2x").get(0).asString() + " Z=" + player.getMetadata("pos2z").get(0).asString());
					return true;
				}
				else {
					player.sendMessage("One of the positions is not set, please set both positions before displaying zone");
					return true;
				}
			}
			catch(Exception er){
				player.sendMessage("Error on show location/s: " + er.toString());
			}
		}
		return false;
	}
	
	
	
}


//When door is right clicked, it will check the player's EXP and the door's physical position; if the door is a keydoor & 
//they have enough EXP for that door (stored by position in sqlite)

//