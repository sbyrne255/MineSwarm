package me.cutrats110;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Mineswarm extends JavaPlugin implements Listener{
	private boolean debugging = true;
	private boolean preventDouble = true;
	private Connection conn = null;
	//Util & logging
	@Override
	public void onEnable(){
		getLogger().info("Mineswarm is starting...");
		
	    getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("Mineswarm has been enabled");
		
		connect();
		createTable();
		
	}
	@Override
	public void onDisable(){
		getLogger().info("Mineswarm has been disabled");
	}
	//All database stuff here...
	public void connect() {
        try {
        	// db parameters
            String url = "jdbc:sqlite:plugins/mineswarm.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            getLogger().info("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
        	 getLogger().info(e.getMessage());
        }
    }
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS zones(id,min_x,min_y,min_z,max_x,max_y,max_z,level,creator,world)";
        try (
        	PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {getLogger().info(e.getMessage());}
        
        sql = "CREATE TABLE IF NOT EXISTS doors(id, level, block_y,creator)";
        try (
        	PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {getLogger().info(e.getMessage());}
    }	
    public String selectDoor(String location, int y){
        String sql = "SELECT level, block_y FROM doors WHERE id = ?";
        try{
        	if(conn.isClosed()){
        		connect();
        	}
        }
        catch(Exception er){
        	getLogger().info("Conn check failed. " + er.toString());
        }
        try {
            	PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, location);
                ResultSet rs = pstmt.executeQuery();
                // loop through the result set
                while (rs.next()) {
                    int db_y = rs.getInt("block_y");
                    if((db_y-1) == y || (db_y+1) == y || db_y == y){
                    	return rs.getString("level");
                    }
                    getLogger().info(location);
                    
                }
            } catch (SQLException e) {getLogger().info("ERROR SELECTING: " + e.getMessage());}
        return null;
    }
    public void makeDoor(String location, int y, String level, String creator) {
        String sql = "INSERT INTO doors(id,level,block_y,creator) VALUES(?,?,?,?)";
        try (
        	PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	pstmt.setString(1, location);
            pstmt.setString(2, level);
            pstmt.setInt(3, y);
            pstmt.setString(4, creator);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {getLogger().info(e.getMessage());}
    }
    public void destroyDoor(String location, int y) {
        String sql = "DELETE FROM doors WHERE id = ? AND (block_y-1 = ? OR block_y+1 = ? OR block_y = ?) ";
        try (
        	PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	pstmt.setString(1, location);
            pstmt.setInt(2, y);
            pstmt.setInt(3, y);
            pstmt.setInt(4, y);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {getLogger().info(e.getMessage());}
    }
    public void deleteDoor(String location, int y, String creator) {
        String sql = "DELETE FROM doors WHERE id = ? AND creator = ? AND (block_y-1 = ? OR block_y+1 = ? OR block_y = ?) ";
        try (
        	PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	pstmt.setString(1, location);
            pstmt.setString(2, creator);
            pstmt.setInt(3, y);
            pstmt.setInt(4, y);
            pstmt.setInt(5, y);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {getLogger().info(e.getMessage());}
    }
    public void makeZone(String id, int min_x, int min_y, int min_z, int max_x, int max_y, int max_z, String level, String creator, String world) {
        String sql = "INSERT INTO zones(id, min_x, min_y, min_z, max_x, max_y, max_z, level, creator, world) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (
        	PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	pstmt.setString(1, id);
            pstmt.setInt(2, min_x);
            pstmt.setInt(3, min_y);
            pstmt.setInt(4, min_z);
            pstmt.setInt(5, max_x);
            pstmt.setInt(6, max_y);
            pstmt.setInt(7, max_z);
            pstmt.setString(8, level);
            pstmt.setString(9, creator);
            pstmt.setString(10, world);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {getLogger().info(e.getMessage());}
    }
    
	//Player interaction and events.
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		try{
			Player player = event.getPlayer();
			boolean opendoor = false;
			if(preventDouble){
				try{
					if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType().toString().toLowerCase().replace("_", "").contains("irondoor")) 
					{
						Block block = event.getClickedBlock();
						String blockID = "X:" + String.valueOf(block.getX()) + "Z:"+String.valueOf(block.getZ()) + "W:"+String.valueOf(block.getWorld());
						int blockY = block.getY();
						String level = selectDoor(blockID, blockY);
						level = "[" + level + "]";
						
						BlockState state = block.getState();
						
						if(player.getInventory().getItemInMainHand().hasItemMeta()){
							if(player.getInventory().getItemInMainHand().getItemMeta().getLore().toString().equals(level) || player.getInventory().getItemInMainHand().getItemMeta().getLore().toString().equals("Key HERE")){
								try{
									state = block.getRelative(BlockFace.DOWN).getState();
						            Openable door = (Openable)state.getData();
						            if(door.isOpen()){
						            	return;
						            }
						            else{
						            	opendoor = true;
						            	player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount()-1);//Remove 1 from inventory
						            }
								}catch(Exception doorer){
									state = event.getClickedBlock().getState();
						            Openable door = (Openable)state.getData();
						            if(door.isOpen()){
						            	return;
						            }
						            else{
						            	opendoor = true;
						            	player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount()-1);//Remove 1 from inventory	
						            }
								}	
															
							}
						}
						else{
							if(player.getInventory().getItemInOffHand().hasItemMeta()){
								if(player.getInventory().getItemInOffHand().getItemMeta().getLore().toString().equals(level) || player.getInventory().getItemInOffHand().getItemMeta().getLore().toString().equals("Master Key")){
									//Key matches door, and it is a key...
									try{
										state = block.getRelative(BlockFace.DOWN).getState();
							            Openable door = (Openable)state.getData();
							            if(door.isOpen()){
							            	return;
							            }
							            else{
							            	opendoor = true;
							            	player.getInventory().getItemInOffHand().setAmount(player.getInventory().getItemInOffHand().getAmount()-1);//Remove 1 from inventory
							            }
									}catch(Exception doorer){
										state = event.getClickedBlock().getState();
							            Openable door = (Openable)state.getData();
							            if(door.isOpen()){
							            	return;
							            }
							            else{
							            	opendoor = true;
							            	player.getInventory().getItemInOffHand().setAmount(player.getInventory().getItemInOffHand().getAmount()-1);//Remove 1 from inventory
							            }
									}							
								}
							}
						}
						if(opendoor){
							try{
					            state = block.getRelative(BlockFace.DOWN).getState();
					            Openable door = (Openable)state.getData();
					            door.setOpen(true);
					            state.setData((Door)door);
					            state.update();
							}
							catch(Exception err)
							{
								state = event.getClickedBlock().getState();
					            Openable door = (Openable)state.getData();
					            door.setOpen(true);
					            state.setData((Door)door);
					            state.update();
							}
							//Schedule Close
						    	getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() 
						    	{
							    	public void run() 
							    	{
							    		BlockState state = block.getState();
										try{
								            state = event.getClickedBlock().getRelative(BlockFace.DOWN).getState();
								            Openable door = (Openable)state.getData();
								            door.setOpen(false);
								            state.setData((Door)door);
								            state.update();
										}catch(Exception err)
										{
											state = event.getClickedBlock().getState();
								            Openable door = (Openable)state.getData();
								            door.setOpen(false);
								            state.setData((Door)door);
								            state.update();
										}
							    	}
						    	}, 45L);
						    event.setCancelled(true);//Probably want to cancel but shouldn't matter in adventure mode
						    return;
						}
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
						player.setMetadata("world2",new FixedMetadataValue(this,event.getClickedBlock().getLocation().getWorld()));
					}
				}catch(Exception er){player.sendMessage("Error on right hand: " + er.toString());}
				try{
					if(event.getAction() == Action.LEFT_CLICK_BLOCK && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("MCMS Marking Tool")){
						player.setMetadata("pos1x",new FixedMetadataValue(this,event.getClickedBlock().getLocation().getBlockX()));
						player.setMetadata("pos1z",new FixedMetadataValue(this,event.getClickedBlock().getLocation().getBlockZ()));
						player.setMetadata("pos1y",new FixedMetadataValue(this,event.getClickedBlock().getLocation().getBlockY()));
						player.setMetadata("world1",new FixedMetadataValue(this,event.getClickedBlock().getLocation().getWorld()));
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
			if(debugging){player.sendMessage(event.getEntity().getType().toString());}
			
			if(event.getEntity().getType().toString() == "ZOMBIE"){//Zombie was killed...
				Random rand = new Random();
				if((rand.nextInt(12))+1 == 1 || debugging){
					try{
						ItemStack book_drop = new ItemStack( Material.BOOK, 1);//Drops "key" (book
						
						//SQL SELECT ZONE...
						
						try{
							List<String> lore = new ArrayList<>();
							lore.add("Key1");
							
							ItemMeta meta = book_drop.getItemMeta();
							meta.setDisplayName("Key");
							if(meta.hasLore()){meta.getLore().add("Key1");}
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
	
	//Command based functions.
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
		if (cmd.getName().equalsIgnoreCase("makezone") && sender instanceof Player){			
			try{
				if (player.hasMetadata("pos1x") && player.hasMetadata("pos1z") && player.hasMetadata("pos1y") && player.hasMetadata("pos2z") && player.hasMetadata("pos2x")&& player.hasMetadata("pos2y")&& player.hasMetadata("world1")&& player.hasMetadata("world2")){
					if(!player.getMetadata("world1").get(0).asString().equals(player.getMetadata("world2").get(0).asString())){
						player.sendMessage("You cannot set a single zone that spans multiple worlds.");
						player.sendMessage(player.getMetadata("world1").get(0).asString());
						player.sendMessage(player.getMetadata("world2").get(0).asString());
						//return true;
					}
					int min_x, min_z, min_y;
					int max_x, max_z, max_y;
					
					if(player.getMetadata("pos1x").get(0).asInt() > player.getMetadata("pos2x").get(0).asInt()){
						max_x = player.getMetadata("pos1x").get(0).asInt(); 
						min_x = player.getMetadata("pos2x").get(0).asInt();
					} else {
						max_x = player.getMetadata("pos2x").get(0).asInt();
						min_x = player.getMetadata("pos1x").get(0).asInt();
					}
					
					if(player.getMetadata("pos1z").get(0).asInt() > player.getMetadata("pos2z").get(0).asInt()){
						max_z = player.getMetadata("pos1z").get(0).asInt(); 
						min_z = player.getMetadata("pos2z").get(0).asInt();
					} else {
						max_z = player.getMetadata("pos2z").get(0).asInt();
						min_z = player.getMetadata("pos1z").get(0).asInt();
					}
					
					if(player.getMetadata("pos1y").get(0).asInt() > player.getMetadata("pos2y").get(0).asInt()){
						max_y = player.getMetadata("pos1y").get(0).asInt(); 
						min_y = player.getMetadata("pos2y").get(0).asInt();
					} else {
						max_y = player.getMetadata("pos2y").get(0).asInt();
						min_y = player.getMetadata("pos1y").get(0).asInt();
					}
					
					makeZone(args[0], min_x, min_y, min_z, max_x, max_y, max_z, args[1], player.getName(), player.getMetadata("world1").get(0).asString());

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
		if (cmd.getName().equalsIgnoreCase("makedoor") && sender instanceof Player){			
			try{
				Block block = player.getTargetBlock(null, 10);
				String blockID = "X:" + String.valueOf(block.getX()) + "Z:"+String.valueOf(block.getZ()) + "W:"+String.valueOf(block.getWorld());
				int blockY = block.getY();
				makeDoor(blockID, blockY, args[0], player.getName());
				return true;
			}
			catch(Exception er){
				player.sendMessage("Error on show location/s: " + er.toString());
			}
		}
		if (cmd.getName().equalsIgnoreCase("deletedoor") && sender instanceof Player){			
			try{
				Block block = player.getTargetBlock(null, 10);
				String blockID = "X:" + String.valueOf(block.getX()) + "Z:"+String.valueOf(block.getZ()) + "W:"+String.valueOf(block.getWorld());
				int blockY = block.getY();
				deleteDoor(blockID, blockY, player.getName());
				return true;
			}
			catch(Exception er){
				player.sendMessage("Error on show location/s: " + er.toString());
			}
		}
		if (cmd.getName().equalsIgnoreCase("destroydoor") && sender instanceof Player){			
			try{
				Block block = player.getTargetBlock(null, 10);
				String blockID = "X:" + String.valueOf(block.getX()) + "Z:"+String.valueOf(block.getZ()) + "W:"+String.valueOf(block.getWorld());
				int blockY = block.getY();
				destroyDoor(blockID, blockY);
				return true;
			}
			catch(Exception er){
				player.sendMessage("Error on show location/s: " + er.toString());
			}
		}
		
		return false;
	}
	
}
