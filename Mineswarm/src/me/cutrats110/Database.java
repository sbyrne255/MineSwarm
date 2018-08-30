package me.cutrats110;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class Database {
	
	public Plugin plugin;
	private Connection conn = null;
	private Connection mobConn = null;
	private Connection chestConn = null;
	private Connection playerConn = null;
	
	public Database(Plugin instance) {
		plugin = instance;
	}
	//All database stuff here...
	public void connectChests() {
        try {
        	File directory = new File(System.getProperty("user.dir") +"/Mineswarm");
    		if (! directory.exists()){ directory.mkdir(); }
            String url = "jdbc:sqlite:plugins/Mineswarm/mineswarmChests.db";
            // create a connection to the database
            chestConn = DriverManager.getConnection(url);
        } catch (SQLException e) {
        	plugin.getLogger().info(e.getMessage());
        }
    }
	public void connectMobs() {
        try {
        	File directory = new File(System.getProperty("user.dir") +"/Mineswarm");
    		if (! directory.exists()){ directory.mkdir(); }
            String url = "jdbc:sqlite:plugins/Mineswarm/mobspawners.db";
            // create a connection to the database
            mobConn = DriverManager.getConnection(url);
        } catch (SQLException e) {
        	plugin.getLogger().info(e.getMessage());
        }
    }
	public void connect() {
        try {
        	// db parameters
        	File directory = new File(System.getProperty("user.dir") +"/Mineswarm");
    		if (! directory.exists()){ directory.mkdir(); }
            String url = "jdbc:sqlite:plugins/Mineswarm/mineswarm.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
        	plugin.getLogger().info(e.getMessage());
        }
    }
	public void connectPlayers() {
        try {
        	// db parameters
        	File directory = new File(System.getProperty("user.dir") +"/Mineswarm");
    		if (! directory.exists()){ directory.mkdir(); }
            String url = "jdbc:sqlite:plugins/Mineswarm/playerdata.db";
            // create a connection to the database
            playerConn = DriverManager.getConnection(url);
        } catch (SQLException e) {
        	plugin.getLogger().info(e.getMessage());
        }
    }
   
	public void createTable() {
        try{
        	if(conn.isClosed()){
        		
        		connect();
        	}
        }
        catch(NullPointerException np){
        	connect();
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
    	
    	String sql = "CREATE TABLE IF NOT EXISTS zones(id,min_x,min_y,min_z,max_x,max_y,max_z,level,creator,world,pvp_enabled,mob_multiplier)";
        try (
        	PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
        
        sql = "CREATE TABLE IF NOT EXISTS doors(id, level, block_y,creator)";
        try (
        	PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
        finally{ try {conn.close();} catch (SQLException e) {} }
    }	
    public void createMobsTable() {
        try{
        	if(mobConn.isClosed()){
        		
        		connectMobs();
        	}
        }
        catch(NullPointerException np){
        	connectMobs();
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
    	
        String sql = "CREATE TABLE IF NOT EXISTS spawners(min_x,min_y,min_z,max_x,max_y,max_z,world,radius,etype,max_mobs,x_offset,y_offset,z_offset,chance,weapons,durability)";
        try (
        	PreparedStatement pstmt = mobConn.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
        finally{ try {mobConn.close();} catch (SQLException e) {} }
    }	
    public void createChestsTable() {
        try{
        	if(mobConn.isClosed()){
        		
        		connectChests();
        	}
        }
        catch(NullPointerException np){
        	connectChests();
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
    	
        String sql = "CREATE TABLE IF NOT EXISTS chests(x,y,z,world,creator,items)";
        try (
        	PreparedStatement pstmt = chestConn.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
        finally{ try {chestConn.close();} catch (SQLException e) {} }
    }	
	public void createPlayersTable() {
        try{if(playerConn.isClosed()){ connectPlayers(); }}
        catch(NullPointerException np){connectPlayers();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
    	String scoresSQL =
    			"CREATE TABLE IF NOT EXISTS scores("
        		+ "name,"
        		+ "total_damage_taken,"
        		+ "total_damage_delt,"
        		+ "kit,"
        		+ "first_joined,"
        		+ "team_members,"
        		+ "team_size,"
        		+ "deaths,"
        		+ "players_saved,"
        		+ "downs,"
        		+ "been_revived,"
        		+ "start_time,"
        		+ "end_time,"
        		+ "mobs_killed"
        		+ ");";
    			
    			
        String sql = "CREATE TABLE IF NOT EXISTS players("
        		+ "name,"
        		+ "total_damage_taken,"
        		+ "total_damage_delt,"
        		+ "kit,"
        		+ "has_died,"
        		+ "isdown,"
        		+ "first_joined,"
        		+ "team_members,"
        		+ "team_size,"
        		+ "deaths,"
        		+ "players_saved,"
        		+ "downs,"
        		+ "been_revived,"
        		+ "start_time,"
        		+ "end_time,"
        		+ "mobs_killed"
        		+ ");";
        try {
        	PreparedStatement pstmt = playerConn.prepareStatement(sql);
            pstmt.execute();
            pstmt = playerConn.prepareStatement(scoresSQL);
            pstmt.execute();
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
        finally{ try {playerConn.close();} catch (SQLException e) {} }
    }	
    
    public String selectDoor(String location, int y){
        try{
        	if(conn.isClosed()){
        		
        		connect();
        	}
        }
        catch(NullPointerException np){
        	connect();
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
    	
    	String sql = "SELECT level, block_y FROM doors WHERE id = ?";
        try{
        	if(conn.isClosed()){
        		connect();
        	}
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
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
                    plugin.getLogger().info(location);
                    
                }
            } catch (SQLException e) {plugin.getLogger().info("ERROR SELECTING: " + e.getMessage());}
        	finally{ try {conn.close();} catch (SQLException e) {} }
        return null;
    }
    public void makeDoor(String location, int y, String level, String creator) {
        try{
        	if(conn.isClosed()){
        		
        		connect();
        	}
        }
        catch(NullPointerException np){
        	connect();
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
    	
    	String sql = "INSERT INTO doors(id,level,block_y,creator) VALUES(?,?,?,?)";
        try (
        	PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	pstmt.setString(1, location);
            pstmt.setString(2, level);
            pstmt.setInt(3, y);
            pstmt.setString(4, creator);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
        finally{ try {conn.close();} catch (SQLException e) {} }
    }
    public void destroyDoor(String location, int y) {
        try{
        	if(conn.isClosed()){
        		
        		connect();
        	}
        }
        catch(NullPointerException np){
        	connect();
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
    	
    	
    	String sql = "DELETE FROM doors WHERE id = ? AND (block_y-1 = ? OR block_y+1 = ? OR block_y = ?) ";
        try (
        	PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	pstmt.setString(1, location);
            pstmt.setInt(2, y);
            pstmt.setInt(3, y);
            pstmt.setInt(4, y);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
        finally{ try {conn.close();} catch (SQLException e) {} }
    }
    public void deleteDoor(String location, int y, String creator) {
        try{
        	if(conn.isClosed()){
        		
        		connect();
        	}
        }
        catch(NullPointerException np){
        	connect();
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
    	
    	
    	String sql = "DELETE FROM doors WHERE id = ? AND creator = ? AND (block_y-1 = ? OR block_y+1 = ? OR block_y = ?) ";
        try (
        	PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	pstmt.setString(1, location);
            pstmt.setString(2, creator);
            pstmt.setInt(3, y);
            pstmt.setInt(4, y);
            pstmt.setInt(5, y);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
        finally{ try {conn.close();} catch (SQLException e) {} }
    }
    public void makeZone(String id, int min_x, int min_y, int min_z, int max_x, int max_y, int max_z, String level, String creator, String world, boolean pvp_enabled, int mob_multiplier) {
        try{
        	if(conn.isClosed()){
        		
        		connect();
        	}
        }
        catch(NullPointerException np){
        	connect();
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
    	
    	
    	String sql = "INSERT INTO zones(id, min_x, min_y, min_z, max_x, max_y, max_z, level, creator, world, pvp_enabled, mob_multiplier) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
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
            pstmt.setBoolean(11, pvp_enabled);
            pstmt.setInt(12, mob_multiplier);
            
            
            pstmt.executeUpdate();
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
        finally{ try {conn.close();} catch (SQLException e) {} }
    }
    public String selectZoneLevel(int x, int y, int z, String world) {
        String sql = "SELECT level FROM zones WHERE min_x < ? AND min_z < ? AND min_y < ? AND max_y > ? AND max_x > ? AND max_z > ? AND world = ? LIMIT 1";
        try{
        	if(conn.isClosed()){
        		connect();
        	}
        }
        catch(NullPointerException np){
        	connect();
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
        try {
            	PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, x);
                pstmt.setInt(2, z);
                pstmt.setInt(3, y);
                pstmt.setInt(4, y);
                pstmt.setInt(5, x);
                pstmt.setInt(6, z);
                pstmt.setString(7, world);                
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    return rs.getString("level");                    
                }
        }
        catch (Exception e) {plugin.getLogger().info("ERROR SELECTING: " + e.getMessage());}
        finally{ try {conn.close();} catch (SQLException e) {} }
        return "Generic";
    }
    public boolean selectZonePVP(int x, int y, int z, String world) {
        String sql = "SELECT pvp_enabled FROM zones WHERE min_x < ? AND min_z < ? AND min_y < ? AND max_y > ? AND max_x > ? AND max_z > ? AND world = ? LIMIT 1";
        try{
        	if(conn.isClosed()){
        		connect();
        	}
        }
        catch(NullPointerException np){
        	connect();
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
        try {
            	PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, x);
                pstmt.setInt(2, z);
                pstmt.setInt(3, y);
                pstmt.setInt(4, y);
                pstmt.setInt(5, x);
                pstmt.setInt(6, z);
                pstmt.setString(7, world);                
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    return rs.getBoolean("pvp_enabled");
                }
        }
        catch (Exception e) {plugin.getLogger().info("ERROR SELECTING: " + e.getMessage());}
        finally{ try {conn.close();} catch (SQLException e) {} }
        return plugin.getConfig().getBoolean("default-pvp-on");
    }
    public List<String> showZone(int x, int y, int z, String world) {
        String sql = "SELECT * FROM zones WHERE min_x < ? AND min_z < ? AND min_y < ? AND max_y > ? AND max_x > ? AND max_z > ? AND world = ? LIMIT 1";
        try{
        	if(conn.isClosed()){
        		connect();
        	}
        }
        catch(NullPointerException np){
        	connect();
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
        List<String> data = new ArrayList<String>();
        try {
            	PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, x);
                pstmt.setInt(2, z);
                pstmt.setInt(3, y);
                pstmt.setInt(4, y);
                pstmt.setInt(5, x);
                pstmt.setInt(6, z);
                pstmt.setString(7, world);                
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                	data.add("Starting points: X: " + rs.getString("min_x") + " Y: " + rs.getString("min_y") + " Z: " + rs.getString("min_z"));
                	data.add("Ending points: X: " + rs.getString("max_x") + " Y: " + rs.getString("max_y") + " Z: " + rs.getString("max_z"));
                	data.add("World: " + rs.getString("world"));
                	data.add("PVP Enabled: " + rs.getString("pvp_enabled"));
                	data.add("Mob health multiplier: " + rs.getString("mob_multiplier"));
                	data.add("Created by: " + rs.getString("creator"));
                	data.add("Level: " + rs.getString("level"));
                	data.add("Name/ID: " + rs.getString("id"));
                	conn.close();
                	return data;                
                }
        }
        catch (Exception e) {plugin.getLogger().info("ERROR SELECTING: " + e.getMessage());}
        finally{ try {conn.close();} catch (SQLException e) {} }
        data.add("No zone found.");
        return data;
    } 
    public List<String> tpToZone(String name) {
        String sql = "SELECT * FROM zones WHERE id = ? OR level = ? LIMIT 1";
        try{
        	if(conn.isClosed()){
        		connect();
        	}
        }
        catch(NullPointerException np){
        	connect();
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
        List<String> data = new ArrayList<String>();
        try {
            	PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, name);  
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                	int x =  ((Integer.valueOf(rs.getString("min_x"))) + (Integer.valueOf(rs.getString("max_x"))))/2;//Middle of X
                	int y =  ((Integer.valueOf(rs.getString("min_y"))) + (Integer.valueOf(rs.getString("max_y"))))/2;//Middle of Y
                	int z =  ((Integer.valueOf(rs.getString("min_z"))) + (Integer.valueOf(rs.getString("max_z"))))/2;//Middle of Z
                	String world = rs.getString("world").replace("CraftWorld{name=", "").replace("}", "");
                	
                	data.add(world);
                	data.add(String.valueOf(x));
                	data.add(String.valueOf(y));
                	data.add(String.valueOf(z));
                	conn.close();
                	return data;                
                }
        }
        catch (Exception e) {plugin.getLogger().info("ERROR SELECTING: " + e.getMessage());}
        finally{ try {conn.close();} catch (SQLException e) {} }
        return null;
    }
    public List<String> getMobSpawners(int x, int y, int z, String world) {
        String sql = "SELECT * FROM spawners WHERE min_x < ? AND min_z < ? AND min_y < ? AND max_y > ? AND max_x > ? AND max_z > ? AND world = ?";
        try{
        	if(conn.isClosed()){
        		connectMobs();
        	}
        }
        catch(NullPointerException np){
        	connectMobs();
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
        
        List<String> data = new ArrayList<String>();
        try {
            	PreparedStatement pstmt = mobConn.prepareStatement(sql);
                pstmt.setInt(1, x);
                pstmt.setInt(2, z);
                pstmt.setInt(3, y);
                pstmt.setInt(4, y);
                pstmt.setInt(5, x);
                pstmt.setInt(6, z);
                pstmt.setString(7, world);                
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                	try{
                	//Player is near a spanwer...
                	//Add radius to min location to get block's true location (t...)
	                	int tx = rs.getInt("min_x") + rs.getInt("radius");
	                	int ty = rs.getInt("min_y") + rs.getInt("radius");
	                	int tz = rs.getInt("min_z") + rs.getInt("radius");
	                	//Location of mobspawner block.
	                	//0-4
	                	data.add(rs.getString("world"));
	                	data.add(String.valueOf(tx));
	                	data.add(String.valueOf(ty));
	                	data.add(String.valueOf(tz));
	                	data.add(rs.getString("radius"));
	                	
	                	//Entity type
	                	//5-6
	                	data.add(rs.getString("etype"));
	                	data.add(rs.getString("max_mobs")); 
	                	//Spawn location...
	                	//7-9
	                	data.add(String.valueOf(tx+rs.getInt("x_offset")));
	                	data.add(String.valueOf(ty+rs.getInt("y_offset")));
	                	data.add(String.valueOf(tz+rs.getInt("z_offset")));
	                	
	                	//Weapon related.
	                	//10-12
	                	data.add(rs.getString("chance"));
	                	data.add(rs.getString("weapons"));
	                	data.add(rs.getString("durability"));
                	}
                	catch(Exception er){
                		plugin.getLogger().info("Error " + er.toString());
                	}
                	          
                }
                return data;
        }
        catch (Exception e) {plugin.getLogger().info("ERROR SELECTING MOB SPAWNERS...: " + e.toString());}
        finally{ try {mobConn.close();} catch (SQLException e) {} }
        return null;
    }
    public void makeSpawner(int min_x, int min_y, int min_z, int max_x, int max_y, int max_z, String world, int radius, String etype, int max_mobs, int xoff, int yoff, int zoff, int chance, String weapon, int durability) {
        try{
        	if(mobConn.isClosed()){
        		
        		plugin.getLogger().info("OPENING CONNECTION...");
        		connectMobs();
        	}
        }
        catch(NullPointerException np){
        	plugin.getLogger().info("NULL, OPENING CONNECTION...");
        	connectMobs();
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
    	
    	String sql = "INSERT INTO spawners(min_x,min_y,min_z,max_x,max_y,max_z,world,radius,etype,max_mobs,x_offset,y_offset,z_offset,chance,weapons,durability)"
    			+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
        	PreparedStatement pstmt = mobConn.prepareStatement(sql);
        	pstmt.setInt(1, min_x);
        	pstmt.setInt(2, min_y);
        	pstmt.setInt(3, min_z);
        	
        	pstmt.setInt(4, max_x);
        	pstmt.setInt(5, max_y);
        	pstmt.setInt(6, max_z);
        	
        	pstmt.setString(7, world);
        	pstmt.setInt(8, radius);
        	pstmt.setString(9, etype);
        	
        	pstmt.setInt(10, max_mobs);
        	pstmt.setInt(11, xoff);
        	pstmt.setInt(12, yoff);
        	pstmt.setInt(13, zoff);
        	
        	pstmt.setInt(14, chance);
        	pstmt.setString(15, weapon);
        	pstmt.setInt(16, durability);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {plugin.getLogger().info("FUCK, PROBLEM HERE: " + e.toString());}
        catch(Exception err){
        	plugin.getLogger().info("BAD IN INSERT, NOT SQL PROBLEM: " + err.toString());
        }
        finally{ try {mobConn.close();} catch (Exception e) {} }
    }
    public void createChest(int x, int y, int z, String world, String creator, String items) {
        try{
        	if(chestConn.isClosed()){
        		
        		plugin.getLogger().info("OPENING CONNECTION...");
        		connectChests();
        	}
        }
        catch(NullPointerException np){
        	plugin.getLogger().info("NULL, OPENING CONNECTION...");
        	connectChests();
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
    	
    	String sql = "INSERT INTO chests(x,y,z,world,creator,items)"
    			+ " VALUES(?,?,?,?,?,?)";
        try {
        	PreparedStatement pstmt = chestConn.prepareStatement(sql);
        	pstmt.setInt(1, x);
        	pstmt.setInt(2, y);
        	pstmt.setInt(3, z);
        	pstmt.setString(4, world);
        	pstmt.setString(5, creator);
        	pstmt.setString(6, items);

            
            pstmt.executeUpdate();
        } catch (SQLException e) {plugin.getLogger().info("Problem inserting new chest data: " + e.toString());}
        catch(Exception err){
        	plugin.getLogger().info("SQL error see error: " + err.toString());
        }
        finally{ try {chestConn.close();} catch (Exception e) {} }
    }
    public ResultSet getChests() {
        String sql = "SELECT * FROM chests";
        try{
        	if(conn.isClosed()){
        		connectChests();
        	}
        }
        catch(NullPointerException np){
        	connectChests();
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
        
        try {
        	PreparedStatement pstmt = chestConn.prepareStatement(sql);    
        	
	    	ResultSet rs = pstmt.executeQuery();
	    	try {
	    		Chest chest;
				while (rs.next()) {
					try {
						chest = (Chest) new Location(Bukkit.getWorld(rs.getString("world")), rs.getInt("x"),rs.getInt("y"),rs.getInt("z")).getBlock().getState();
						List<String> items = Arrays.asList(rs.getString("items").split("\\s*,\\s*"));
						ItemStack[] contents = new ItemStack[(items.size()/2)];
						int j = 0;
						for(int i = 0; i < items.size(); i+=2) {//This builds my itemStack array...
							contents[j] = new ItemStack(Material.getMaterial(items.get(i).toString()), Integer.valueOf(items.get(i+1)));
							j++;
						}
						chest.getBlockInventory().setContents(contents);
					}catch(Exception err) {
						plugin.getLogger().info("Failed to cast block: " + err.toString());
					}
					
				}
			} catch (Exception e) {
				plugin.getLogger().info(e.toString());
			}
        	
        	
        	
            return null;                
        }
        catch (Exception e) {plugin.getLogger().info("General Error...: " + e.toString());}
        finally{ try {chestConn.close();} catch (SQLException e) {} }
        return null;
    }

    public boolean setPlayerData(Player player){
        String sql = "SELECT * FROM players WHERE name = ?";
        try{
        	if(playerConn.isClosed()){
        		connectPlayers();
        	}
        }
        catch(NullPointerException np){
        	connectPlayers();
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
        try {
            	PreparedStatement pstmt = playerConn.prepareStatement(sql);
                pstmt.setString(1, player.getName());           
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                	player.setMetadata("total_damage_taken",new FixedMetadataValue(plugin, rs.getInt("total_damage_taken")));
                    player.setMetadata("total_damage_delt",new FixedMetadataValue(plugin, rs.getInt("total_damage_delt")));
                    if(rs.getString("kit") != null && rs.getString("kit").length() > 0) {
                    	plugin.getLogger().info("Setting class");
                    	player.setMetadata("class",new FixedMetadataValue(plugin, rs.getString("kit")));
                    }                    
                    player.setMetadata("hasdied",new FixedMetadataValue(plugin, rs.getBoolean("has_died")));
                	player.setMetadata("isdown",new FixedMetadataValue(plugin, rs.getBoolean("isdown")));
                    player.setMetadata("team_members",new FixedMetadataValue(plugin, rs.getString("team_members")));
                    player.setMetadata("team_size",new FixedMetadataValue(plugin, rs.getInt("team_size")));
                    player.setMetadata("deaths",new FixedMetadataValue(plugin, rs.getInt("deaths")));
                    player.setMetadata("players_saved",new FixedMetadataValue(plugin, rs.getInt("players_saved")));
                    player.setMetadata("revived",new FixedMetadataValue(plugin, rs.getInt("been_revived")));
                    player.setMetadata("downs",new FixedMetadataValue(plugin, rs.getInt("downs")));
                    player.setMetadata("start_time",new FixedMetadataValue(plugin, rs.getString("start_time")));
                    player.setMetadata("mobs_killed",new FixedMetadataValue(plugin, rs.getString("mobs_killed")));
                    playerConn.close();
                	return true;
                }
        }
        catch (Exception e) {plugin.getLogger().info("ERROR SELECTING PLAYER...: " + e.toString());}
        finally{ try {playerConn.close();} catch (SQLException e) {} }
        return false;    
    }
    public void newPlayer(Player player) {
    	String sql = "INSERT INTO players(name,total_damage_taken, total_damage_delt, kit, has_died, isdown, first_joined, team_members, team_size, deaths, players_saved, downs, been_revived, start_time) "
    								+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    	
    	try{if(playerConn.isClosed()){connectPlayers();}
        }catch(NullPointerException np){connectPlayers();
        }catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
        try {
        	String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        	
        	PreparedStatement pstmt = playerConn.prepareStatement(sql);
        	pstmt.setString(1, player.getName());
        	pstmt.setInt(2, 0);
        	pstmt.setInt(3, 0);
        	pstmt.setString(4, null);
        	pstmt.setBoolean(5, false);
        	pstmt.setBoolean(6, false);
        	pstmt.setString(7, timeStamp);
        	pstmt.setString(8, player.getName());
        	pstmt.setInt(9, 1);
        	pstmt.setInt(10, 0);
        	pstmt.setInt(11, 0);
        	pstmt.setInt(12, 0);
        	pstmt.setInt(13, 0);
        	pstmt.setString(14, null);
        	
            
            pstmt.executeUpdate();
            
        	player.setMetadata("isdown",new FixedMetadataValue(plugin, false));
            player.setMetadata("hasdied",new FixedMetadataValue(plugin, false));
            player.setMetadata("total_damage_taken",new FixedMetadataValue(plugin, 0));
            player.setMetadata("total_damage_delt",new FixedMetadataValue(plugin, 0));
            player.setMetadata("deaths",new FixedMetadataValue(plugin, 0));
            player.setMetadata("players_saved",new FixedMetadataValue(plugin, 0));
            player.setMetadata("downs",new FixedMetadataValue(plugin, 0));
            player.setMetadata("been_revived",new FixedMetadataValue(plugin, 0));
            
            
        } catch (SQLException e) {plugin.getLogger().info("PROBLEM INSERTING NEW PLAYER: " + e.toString());}
        catch(Exception err){
        	plugin.getLogger().info("PROBLEM IN INSERT, NOT SQL PROBLEM: " + err.toString());
        }
        finally{ try {playerConn.close();} catch (Exception e) {} }
    }
    public void updatePlayerData(Player player) {
    	String sql = "UPDATE players SET total_damage_taken = ?, total_damage_delt = ?, kit = ?, has_died = ?, isdown = ?, team_members = ?, team_size = ?, deaths = ?, players_saved = ?, downs = ?, been_revived = ?, start_time = ?, mobs_killed = ? WHERE name = ?";
    	
    	try{if(playerConn.isClosed()){connectPlayers();}
        }catch(NullPointerException np){connectPlayers();
        }catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
        try {
        	PreparedStatement pstmt = playerConn.prepareStatement(sql);//13 fields...
        	try { pstmt.setInt(1, player.getMetadata("total_damage_taken").get(0).asInt()); } catch(IndexOutOfBoundsException iob) {pstmt.setInt(1, 0);}
        	try { pstmt.setInt(2, player.getMetadata("total_damage_delt").get(0).asInt()); } catch(IndexOutOfBoundsException iob) {pstmt.setInt(2, 0);}
        	try { pstmt.setString(3, player.getMetadata("class").get(0).asString()); } catch(Exception iob) {pstmt.setString(3, "");}
        	try { pstmt.setBoolean(4, player.getMetadata("hasdied").get(0).asBoolean()); } catch(IndexOutOfBoundsException iob) {pstmt.setBoolean(4, false);}
        	try { pstmt.setBoolean(5, player.getMetadata("isdown").get(0).asBoolean()); } catch(IndexOutOfBoundsException iob) {pstmt.setBoolean(5, false);}
        	try { pstmt.setString(6, player.getMetadata("team_members").get(0).asString()); } catch(IndexOutOfBoundsException iob) {pstmt.setString(6, "NOT IMPLEMENTED YET");}
        	try { pstmt.setInt(7, player.getMetadata("team_size").get(0).asInt()); } catch(IndexOutOfBoundsException iob) {pstmt.setInt(7, 1);}
        	try { pstmt.setInt(8, player.getMetadata("deaths").get(0).asInt()); } catch(IndexOutOfBoundsException iob) {pstmt.setInt(8, 0);}
        	try { pstmt.setInt(9, player.getMetadata("players_saved").get(0).asInt()); } catch(IndexOutOfBoundsException iob) {pstmt.setInt(9, 0);}
        	try { pstmt.setInt(10, player.getMetadata("downs").get(0).asInt()); } catch(IndexOutOfBoundsException iob) {pstmt.setInt(10, 0);}
        	try { pstmt.setInt(11, player.getMetadata("revived").get(0).asInt()); } catch(IndexOutOfBoundsException iob) {pstmt.setInt(11, 0);}
        	try { pstmt.setString(12, player.getMetadata("start_time").get(0).asString()); } catch(IndexOutOfBoundsException iob) {pstmt.setString(12, "NOT IMPLEMENTED YET");}
        	try { pstmt.setString(13, player.getMetadata("mobs_killed").get(0).asString()); } catch(IndexOutOfBoundsException iob) {pstmt.setString(12, "");}
        	pstmt.setString(14, player.getName());
        	
            pstmt.executeUpdate();          
            
        } catch (SQLException e) {plugin.getLogger().info("PROBLEM UPDATING EXISTING PLAYER: " + e.toString());}
        catch(Exception err){
        	plugin.getLogger().info("PROBLEM IN UPDATE, NOT SQL PROBLEM: " + err.toString());
        }
        finally{ try {playerConn.close();} catch (Exception e) {} }
    }
     
    public void deleteChest(int x, int y, int z, String world, String creator) {
    	//Delete where position matches AND creator matches
    }
    public void destroyChest(int x, int y, int z, String world) {
    	//DELETE where position matches
    }
}

