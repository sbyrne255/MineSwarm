package me.cutrats110;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.Plugin;

public class Database {
	
	public Plugin plugin;
	private Connection conn = null;
	private Connection mobConn = null;
	
	public Database(Plugin instance) {
		plugin = instance;
	}
	//All database stuff here...
	public void connectMobs() {
        try {
        	// db parameters
            String url = "jdbc:sqlite:plugins/mobspawners.db";
            // create a connection to the database
            mobConn = DriverManager.getConnection(url);
        } catch (SQLException e) {
        	plugin.getLogger().info(e.getMessage());
        }
    }
	public void connect() {
        try {
        	// db parameters
            String url = "jdbc:sqlite:plugins/mineswarm.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
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
        String sql = "SELECT * FROM spawners WHERE min_x < ? AND min_z < ? AND min_y < ? AND max_y > ? AND max_x > ? AND max_z > ? AND world = ? LIMIT 1";
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
                	
                	//13-15
                	//data.add(String.valueOf(Math.abs(rs.getInt("max_x") * rs.getInt("min_x"))));
                	//data.add(String.valueOf(Math.abs(rs.getInt("max_y") * rs.getInt("min_y"))));
                	//data.add(String.valueOf(Math.abs(rs.getInt("max_z") * rs.getInt("min_z"))));
                	}
                	catch(Exception er){
                		plugin.getLogger().info("FUCK CUNTS WHORE: " + er.toString());
                	}
                	
                    return data;               
                }
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
	
}
