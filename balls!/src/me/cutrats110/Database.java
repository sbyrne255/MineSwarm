package me.cutrats110;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.plugin.Plugin;

public class Database {
	
	public Plugin plugin;
	private Connection conn = null;
	
	public Database(Plugin instance) {
		plugin = instance;
	}
	//All database stuff here...
	public void connect() {
        try {
        	// db parameters
            String url = "jdbc:sqlite:plugins/mineswarm.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            plugin.getLogger().info("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
        	plugin.getLogger().info(e.getMessage());
        }
    }
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS zones(id,min_x,min_y,min_z,max_x,max_y,max_z,level,creator,world)";
        try (
        	PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
        
        sql = "CREATE TABLE IF NOT EXISTS doors(id, level, block_y,creator)";
        try (
        	PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
    }	
    public String selectDoor(String location, int y){
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
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
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
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
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
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
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
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
    }
    public String selectZone(int x, int y, int z, String world) {
    	plugin.getLogger().info("I AM ALIVE");
        String sql = "SELECT level FROM zones WHERE min_x < ? AND min_z < ? AND min_y < ? AND max_y > ? AND max_x > ? AND max_z > ? AND world = ? LIMIT 1";
        try{
        	if(conn.isClosed()){
        		plugin.getLogger().info("CONN WAS CLOSED...");
        		connect();
        	}
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
                plugin.getLogger().info("PREPAIRED");
                
                ResultSet rs = pstmt.executeQuery();
                // loop through the result set
                while (rs.next()) {
                	plugin.getLogger().info("GOT A RESULT!");
                    return rs.getString("level");                    
                }
                plugin.getLogger().info("NO RESULT....");
            } catch (Exception e) {plugin.getLogger().info("ERROR SELECTING: " + e.getMessage());}
        plugin.getLogger().info("BAD KEY!");
        return "BAD KEY";
    }
    public String noDicks(int x, int y, int z, String world) {
        String sql = "SELECT * FROM zones WHERE creator = ?";
        try{
        	if(conn.isClosed()){
        		plugin.getLogger().info("CONN WAS CLOSED...");
        		connect();
        	}
        }
        catch(Exception er){
        	connect();
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
        try {
            	PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, "cutrats110");
                plugin.getLogger().info("PREPAIRED");
                
                ResultSet rs = pstmt.executeQuery();
                // loop through the result set
                while (rs.next()) {
                	plugin.getLogger().info("GOT A RESULT!");
                    return rs.getString("level");                    
                }
            } catch (Exception e) {plugin.getLogger().info("ERROR SELECTING: " + e.toString());}
        return "BAD KEY";
    }
	
	
}