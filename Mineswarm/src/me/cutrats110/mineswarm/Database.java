package me.cutrats110.mineswarm;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Database {
	
	public Plugin plugin;
	
	public Database(Plugin instance) {
		plugin = instance;
	}
	
	private Connection establishDatabaseConnection() { return establishDatabaseConnection("mineswarm"); }
	private Connection establishDatabaseConnection(String database) {
		try {
			String url = String.format("jdbc:sqlite:plugins/Mineswarm/%s.db", database);
			Connection conn = DriverManager.getConnection(url);
			
			if(conn == null) {
				plugin.getLogger().warning(String.format("Failure to establish database connection to: %s ", database));
				return null;
			} else { 
				return conn; 
			}
        } catch (SQLException e) {
        	plugin.getLogger().warning(e.getMessage());
        	return null;
        }
	}	
	private static void close(Connection conn) throws SQLException {
		if (conn != null) { conn.close(); }
	}
	private static void closeQuietly(Connection conn) {
    	try { close(conn); } 
    	catch (SQLException e) {
    		//TODO Add loging to txt file./
    	}
    }
	private static void closePreparedStatement(PreparedStatement prep) throws SQLException {
		if (prep != null) { prep.close(); }
	}
	private static void closePreparedStatementQuietly(PreparedStatement prep) {
    	try { closePreparedStatement(prep); } 
    	catch (SQLException e) {
    		//TODO Add loging to txt file./
    	}
    }
	private static void closeResultSet(ResultSet rs) throws SQLException {
		if (rs != null) { rs.close(); }
	}
	private static void closeResultSetQuietly(ResultSet rs) {
		try { closeResultSet(rs); } 
    	catch (SQLException e) {
    		//TODO Add loging to txt file./
    	}
	}
	
	public void setupDatabases() { createTable(); }	
	private void createTable() {		
        /*
         * CREATE PRIMARY DATA TABLE -- DOORS & ZONES.
         */
		Connection conn = establishDatabaseConnection("mineswarm");
        if(conn == null){ return; }
        PreparedStatement pstmt = null;        
        try {
        	
        	/*
             * CREATE ZONES TABLE.
             */
        	pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS zones(id,min_x,min_y,min_z,max_x,max_y,max_z,level,creator,world,pvp_enabled,mob_multiplier)"); 
        	pstmt.execute();
        	closePreparedStatementQuietly(pstmt);
            
        	/*
             * CREATE DOORS TABLE.
             */
            pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS doors(id, level, block_y,creator)");
            pstmt.execute();
            closePreparedStatementQuietly(pstmt);
            
            /*
             * CREATE BUTTONS TABLE.
             */
            pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS buttons(x,y,z,world,class)"); 
        	pstmt.execute();
        	closePreparedStatementQuietly(pstmt); 
        	
        	 /*
             * CREATE PLAYERS TABLE.
             */
        	pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS players(name, total_damage_taken, total_damage_delt, kit, has_died, isdown, first_joined, team_name, team_size, deaths, players_saved, downs, been_revived, start_time, end_time, mobs_killed)"); 
        	pstmt.execute();
        	closePreparedStatementQuietly(pstmt);   
        	
        	/*
             * CREATE CHESTS TABLE.
             */
        	pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS chests(x,y,z,world,creator,items)"); 
        	pstmt.execute();
        	closePreparedStatementQuietly(pstmt);
        	
        	/*
             * CREATE TEAMS TABLE.
             */
        	pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS teams (name TEXT, owner TEXT, closed INTEGER, score INTEGER)"); 
        	pstmt.execute();
        	closePreparedStatementQuietly(pstmt);      
        	
        	/*
             * CREATE MEMBERS TABLE.
             */
        	pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS members(team_id INTEGER, member TEXT)"); 
        	pstmt.execute();
        	closePreparedStatementQuietly(pstmt);     
        	
        	/*
             * CREATE SPAWNERS TABLE.
             */
        	pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS spawners(location,world,etype,max_mobs,chance,weapons,durability,enchantments, effects)"); 
        	pstmt.execute();
        	closePreparedStatementQuietly(pstmt);  
        	
        	/*
             * CREATE SOLO_SCORES TABLE.
             */
        	pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS solo_scores(name, total_damage_taken, total_damage_delt, kit, first_joined, team_name, team_size, deaths, players_saved, downs, been_revived, start_time, end_time, run_time, mobs_killed, team_members"); 
        	pstmt.execute();
        	closePreparedStatementQuietly(pstmt);
        	
        	/*
             * CREATE TEAM_SCORES TABLE.
             */
        	pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS team_scores(total_damage_taken, total_damage_delt, team_name, team_size, deaths, players_saved, downs, been_revived, start_time, end_time, run_time, mobs_killed, team_members)"); 
        	pstmt.execute();
        	closePreparedStatementQuietly(pstmt);  
            
        } catch (SQLException e) {plugin.getLogger().warning(e.getMessage());}
        finally {
        	closePreparedStatementQuietly(pstmt);
        	closeQuietly(conn);
        }
    }    
    public void emptyTeamsTable() {
    	Connection conn = establishDatabaseConnection();
    	if(conn == null){ plugin.getLogger().warning("Connection to Database Failed, returned null."); }
    	PreparedStatement pstmt = null;
    	
        try {
    		pstmt = conn.prepareStatement("DELETE FROM teams");
    		pstmt.execute();
    		closePreparedStatementQuietly(pstmt);
    		
    		pstmt = conn.prepareStatement("DELETE FROM members");
    		pstmt.execute();
    		closePreparedStatementQuietly(pstmt);
        } catch (SQLException e) {plugin.getLogger().warning(String.format("Error emptying teams table, error: ", e.getMessage())); }
    	finally {
    	        closePreparedStatementQuietly(pstmt);
    	        closeQuietly(conn);
    	}
    } 

	public HashMap<String, List<UUID>> getTeams(){
    	Connection conn = establishDatabaseConnection();
    	if(conn == null){ plugin.getLogger().warning("Connection to Database Failed, returned null."); }
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	
		HashMap<String, List<UUID>> newData = new HashMap<>();
    	try {
        	pstmt = conn.prepareStatement("SELECT * FROM teams");
            rs = pstmt.executeQuery();
            while (rs.next()) {
            	List<UUID> ids = new ArrayList<>();
            	for(String val : Arrays.asList(rs.getString("data").split("\\s*,\\s*"))) {ids.add(UUID.fromString(val));}
            	newData.put(rs.getString("key"), ids);
            }
            closeResultSetQuietly(rs);
            closePreparedStatementQuietly(pstmt);
            
        } catch (SQLException e) {plugin.getLogger().warning(String.format("Error selecting teams, error: %s", e.getMessage()) );}
    	finally {
    		closeResultSetQuietly(rs);
            closePreparedStatementQuietly(pstmt);
	        closeQuietly(conn);
    	}
    	return newData;
	}
	public HashMap<Location, String> getButtons(){
    	Connection conn = establishDatabaseConnection();
    	if(conn == null){ plugin.getLogger().warning("Connection to Database Failed, returned null."); }
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
		
		HashMap<Location, String> newData = new HashMap<>();
    	try {
        	pstmt = conn.prepareStatement("SELECT * FROM buttons");
            rs = pstmt.executeQuery();
            while (rs.next()) {
            	//Returns Location object as key, class as value.
            	newData.put((new Location(Bukkit.getWorld(rs.getString("world")), rs.getInt("x"),rs.getInt("y"),rs.getInt("z"))), rs.getString("class"));
            }
            closeResultSetQuietly(rs);
            closePreparedStatementQuietly(pstmt);
            
        } catch (SQLException e) {plugin.getLogger().warning(String.format("Error retrieving button data, error: ", e.getMessage())); }
    	finally {
    		closeResultSetQuietly(rs);
    		closePreparedStatementQuietly(pstmt);
    	    closeQuietly(conn);
    	}	
		return newData;
	}
    public void saveButton(Location location, String name) {
    	Connection conn = establishDatabaseConnection();
    	if(conn == null){ plugin.getLogger().warning("Connection to Database Failed, returned null."); }
    	PreparedStatement pstmt = null;
		
		String sql = "INSERT INTO buttons(x,y,z,world,class) VALUES(?,?,?,?,?)";
			try {
    			pstmt = conn.prepareStatement(sql);
        		pstmt.setInt(1, (int)location.getX());
        		pstmt.setInt(2, (int)location.getY());
        		pstmt.setInt(3, (int)location.getZ());
        		pstmt.setString(4, location.getWorld().getName());
        		pstmt.setString(5, name);
        		pstmt.executeUpdate();
        		closePreparedStatementQuietly(pstmt);
	        } catch (SQLException e) {plugin.getLogger().warning(String.format("Error inserting button data in table, error: ", e.getMessage())); }
	    	finally {
	    		closePreparedStatementQuietly(pstmt);
	    	    closeQuietly(conn);
	    	}	
    } 
	public boolean getScores(int topScores) {
    	Connection conn = establishDatabaseConnection();
    	if(conn == null){ plugin.getLogger().warning("Connection to Database Failed, returned null."); }
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;

    	try {
        	pstmt = conn.prepareStatement("SELECT * FROM scores ORDER BY run_time DESC LIMIT ?");
            pstmt.setInt(1, topScores);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                //Score data, do something with it...                
            }
            closeResultSetQuietly(rs);
            closePreparedStatementQuietly(pstmt);
        } catch (SQLException e) {plugin.getLogger().warning(String.format("Error retrieving score data, error: ", e.getMessage())); }
    	finally {
    		closeResultSetQuietly(rs);
            closePreparedStatementQuietly(pstmt);
    	    closeQuietly(conn);
    	}	
		return true;
	}
	
    public String getDoorByLocation(String location, int y){
    	Connection conn = establishDatabaseConnection();
    	if(conn == null){ plugin.getLogger().warning("Connection to Database Failed, returned null."); }
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	String level = null;
    	
        try {
        	pstmt = conn.prepareStatement("SELECT level, block_y FROM doors WHERE id = ?");
            pstmt.setString(1, location);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int db_y = rs.getInt("block_y");
                if((db_y-1) == y || (db_y+1) == y || db_y == y){
                	level = rs.getString("level");
                }
            }
            closeResultSetQuietly(rs);
            closePreparedStatementQuietly(pstmt);
            
        } catch (SQLException e) {plugin.getLogger().warning(String.format("Error retrieving door data from table: ", e.getMessage())); }
    	finally {
    		closeResultSetQuietly(rs);
            closePreparedStatementQuietly(pstmt);
    	    closeQuietly(conn);
    	}	
        return level;
    }
    public void makeDoor(String location, int y, String level, String creator) {
    	Connection conn = establishDatabaseConnection();
    	if(conn == null){ plugin.getLogger().warning("Connection to Database Failed, returned null."); }
    	PreparedStatement pstmt = null;
    	
        try {
        	pstmt = conn.prepareStatement("INSERT INTO doors(id,level,block_y,creator) VALUES(?,?,?,?)");
        	pstmt.setString(1, location);
            pstmt.setString(2, level);
            pstmt.setInt(3, y);
            pstmt.setString(4, creator);
            
            pstmt.executeUpdate();
            closePreparedStatementQuietly(pstmt);

        } catch (SQLException e) {plugin.getLogger().warning(String.format("Error inserting door data: ", e.getMessage())); }
    	finally {
            closePreparedStatementQuietly(pstmt);
    	    closeQuietly(conn);
    	}	
    }
    public void destroyDoor(String location, int y) {
    	Connection conn = establishDatabaseConnection();
    	if(conn == null){ plugin.getLogger().warning("Connection to Database Failed, returned null."); }
    	PreparedStatement pstmt = null;
    	
        try {
        	pstmt = conn.prepareStatement("DELETE FROM doors WHERE id = ? AND (block_y-1 = ? OR block_y+1 = ? OR block_y = ?)");
        	pstmt.setString(1, location);
            pstmt.setInt(2, y);
            pstmt.setInt(3, y);
            pstmt.setInt(4, y);
            
            pstmt.executeUpdate();
            closePreparedStatementQuietly(pstmt);

        } catch (SQLException e) {plugin.getLogger().warning(String.format("Error deleting door data: ", e.getMessage())); }
    	finally {
            closePreparedStatementQuietly(pstmt);
    	    closeQuietly(conn);
    	}	
    }
    public void deleteDoor(String location, int y, String creator) {
    	Connection conn = establishDatabaseConnection();
    	if(conn == null){ plugin.getLogger().warning("Connection to Database Failed, returned null."); }
    	PreparedStatement pstmt = null;
    	
        try {
        	pstmt = conn.prepareStatement("DELETE FROM doors WHERE id = ? AND creator = ? AND (block_y-1 = ? OR block_y+1 = ? OR block_y = ?) ");
        	pstmt.setString(1, location);
            pstmt.setString(2, creator);
            pstmt.setInt(3, y);
            pstmt.setInt(4, y);
            pstmt.setInt(5, y);
            
            pstmt.executeUpdate();
            closePreparedStatementQuietly(pstmt);
        } catch (SQLException e) {plugin.getLogger().warning(String.format("Error deleting door data: ", e.getMessage())); }
    	finally {
            closePreparedStatementQuietly(pstmt);
    	    closeQuietly(conn);
    	}	
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

    public List<String> getMobSpawners() {
        String sql = "SELECT * FROM spawners";
        try{if(conn.isClosed()){connectMobs();}}
        catch(NullPointerException np){connectMobs();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
        List<String> data = new ArrayList<String>();
        try {
            	PreparedStatement pstmt = mobConn.prepareStatement(sql);             
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                	try{
                		data.add(rs.getString("location"));//0
	                	data.add(rs.getString("world"));//1
	                	data.add(rs.getString("etype"));//2
	                	data.add(rs.getString("max_mobs"));//3 
	                	data.add(rs.getString("chance"));//4
	                	data.add(rs.getString("weapons"));//5
	                	data.add(rs.getString("durability"));//6
	                	data.add(rs.getString("enchantments"));//7
	                	data.add(rs.getString("effects"));//8
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
    public void makeSpawner(String location, String world, String mobType, int maxMobs, String weapon, int chance, int durability, List<String> enchantments, List<String> effects) {
        try{
        	if(mobConn.isClosed()){plugin.getLogger().info("OPENING CONNECTION...");connectMobs();}}
        catch(NullPointerException np){plugin.getLogger().info("NULL, OPENING CONNECTION...");connectMobs();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
    	
    	String sql = "INSERT INTO spawners(location,world,etype,max_mobs,chance,weapons,durability,enchantments,effects)"
    			+ " VALUES(?,?,?,?,?,?,?,?,?)";
        try {
        	PreparedStatement pstmt = mobConn.prepareStatement(sql);
        	if(enchantments == null || enchantments.isEmpty()) {pstmt.setString(8, "NONE");}
        	else {pstmt.setString(8, StringUtils.join(enchantments, ","));}//Convert list to CSV string
        	if(effects==null || effects.isEmpty()) {pstmt.setString(9, "NONE");}
        	else {pstmt.setString(9, StringUtils.join(effects, ","));}//Convert list to CSV String
        	pstmt.setString(1, location);
        	pstmt.setString(2, world);
        	pstmt.setString(3, mobType);
        	pstmt.setInt(4, maxMobs);        	
        	pstmt.setInt(5, chance);
        	pstmt.setString(6, weapon);
        	pstmt.setInt(7, durability);
                    
            pstmt.executeUpdate();
        } catch (SQLException e) {plugin.getLogger().info("PROBLEM ADDING MOB SPAWNER TO DB: " + e.toString());}
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

    public ResultSet getChests(PotionObjects potions) {
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
							ItemStack toAdd = null;
			    			try {
			    				int pID = Integer.valueOf(items.get(i).toString());//Should error out here if it's not an ID...
			    				MakePotion potionData = potions.getDrinkableDataById(pID);
			    				if(potionData.isSplash) {
			    					toAdd = new ItemStack(Material.SPLASH_POTION, Integer.valueOf(items.get(i+1).toString()));
			    				}else {
			    					toAdd = new ItemStack(Material.POTION, Integer.valueOf(items.get(i+1).toString()));
			    				}
				    			try {
					    				ItemMeta im = toAdd.getItemMeta();
					    				im.setDisplayName(potionData.name);
					    				PotionMeta pm = (PotionMeta) im;
					    				for(PotionEffectType effect : potionData.effectTypes) {
					    					//									Type	time in seconds probably	amplifier(1=2)
					    					pm.addCustomEffect(new PotionEffect(effect, (int)potionData.duration, potionData.amplifier), true);
					    				}
					    				pm.setColor(potionData.color);
					    				toAdd.setItemMeta(im);
				    			}
				    			catch(Exception err) {plugin.getLogger().info("ERROR : " + err.toString());}
			    				
			    				
			    				
			    			}catch(NumberFormatException  nf) {
			    				//Tipped Arrow?
			    				try {
			    				int pID = Integer.valueOf(items.get(i).toString().replaceAll("TIPPED_ARROW:", ""));//Should error out here if it's not an ID...
			    				MakePotion potionData = potions.getDrinkableDataById(pID);
								if(items.get(i).toUpperCase().contains("TIPPED_ARROW:")) {    					
			    					toAdd = new ItemStack(Material.TIPPED_ARROW, Integer.valueOf(items.get(i+1)));
			    					
			    					ItemMeta im = toAdd.getItemMeta();
			    					PotionMeta pm = (PotionMeta) im;
			    					//meta.setBasePotionData(new PotionData(PotionType.valueOf(stuff.get(0).toUpperCase().replace("TIPPED_ARROW:", ""))) );
			    					im.setDisplayName(potionData.name);
			        				for(PotionEffectType effect : potionData.effectTypes) {
			        					pm.addCustomEffect(new PotionEffect(effect, (int)potionData.duration, potionData.amplifier), true);
			        				}
			        				pm.setColor(potionData.color);
			    					toAdd.setItemMeta(im);
			    				}
			    				}catch(NumberFormatException nfe) {
									toAdd = new ItemStack(Material.getMaterial(items.get(i).toString()),Integer.valueOf(items.get(i+1)));
								}
			    			}
			    			if(toAdd != null) {
			    				contents[j] = toAdd;
			    			}							
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

    public boolean playerExists(String name) {
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
                pstmt.setString(1, name);           
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                	playerConn.close();
                	return true;
                }
        }
        catch (Exception e) {plugin.getLogger().info("ERROR SELECTING PLAYER...: " + e.toString());}
        finally{ try {playerConn.close();} catch (SQLException e) {} }
        return false;   
    }
    
     
    public void deleteChest(int x, int y, int z, String world, String creator) {
    	//Delete where position matches AND creator matches
    }
    public void destroyChest(int x, int y, int z, String world) {
    	//DELETE where position matches
    }

	public int updateTeamsTable(String name, String string, boolean closed, int score) {
		Connection conn = establishDatabaseConnection("mineswarm");
        if(conn == null){ return -1; }
        PreparedStatement pstmt = null;  
        int result = -1;
        try {
        	pstmt = conn.prepareStatement("INSERT INTO teams(name, owner, closed, score) VALUES(?,?,?,?)");
    		pstmt.setString(1, name);
    		pstmt.setString(2, string);
    		pstmt.setBoolean(3, closed);
    		pstmt.setInt(4, score);
    		
    		pstmt.executeUpdate();
    		closePreparedStatementQuietly(pstmt);
    		
    		pstmt = conn.prepareStatement("SELECT last_insert_rowid() AS LAST_ID");
    		result = Integer.parseInt(pstmt.executeQuery().getString("LAST_ID"));
    		closePreparedStatementQuietly(pstmt);
    		
        } catch (SQLException e) {plugin.getLogger().warning(String.format("Error updating teams table, error: ", e.getMessage())); }
        finally {
	        closePreparedStatementQuietly(pstmt);
	        closeQuietly(conn);
        }
        return result;
	}

	public void insertNewTeamMember(String string, int team_id) {
		Connection conn = establishDatabaseConnection("mineswarm");
        if(conn == null){ return; }
        PreparedStatement pstmt = null;
        try {
        	pstmt = conn.prepareStatement("INSERT INTO members(member, team_id) VALUES(?,?)");
        	pstmt.setString(1, string);
    		pstmt.setInt(2, team_id);
    		
    		pstmt.executeUpdate();
    		closePreparedStatementQuietly(pstmt);
        } catch (SQLException e) {plugin.getLogger().warning(String.format("Error updating new team member in table, error: ", e.getMessage())); }
        finally {
	        closePreparedStatementQuietly(pstmt);
	        closeQuietly(conn);
        }
		
	}
}



