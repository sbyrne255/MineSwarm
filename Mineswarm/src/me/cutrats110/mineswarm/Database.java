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
	private Connection conn = null;
	private Connection mobConn = null;
	private Connection chestConn = null;
	private Connection playerConn = null;
	private Connection scoreboardConn = null;
	private Connection teamsConn = null;
	private Connection buttonsConn = null;
	
	public Database(Plugin instance) {
		plugin = instance;
	}
	
	public Connection establishDatabaseConnection(String database) {
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
	
	/*
	//All database stuff here...
	public void connectChests() {
        try {
            String url = "jdbc:sqlite:plugins/Mineswarm/mineswarmChests.db";
            // create a connection to the database
            chestConn = DriverManager.getConnection(url);
        } catch (SQLException e) {
        	plugin.getLogger().info(e.getMessage());
        }
    }
	public void connectButtons() {
        try {
            String url = "jdbc:sqlite:plugins/Mineswarm/buttons.db";
            buttonsConn = DriverManager.getConnection(url);
        } catch (SQLException e) {
        	plugin.getLogger().info(e.getMessage());
        }
    }
	public Connection getTeamsConnection() {
        try {
            String url = "jdbc:sqlite:plugins/Mineswarm/teams.db";
            // create a connection to the database
            this.teamsConn = DriverManager.getConnection(url);
        } catch (SQLException e) {
        	plugin.getLogger().info(e.getMessage());
        	return null;
        }
        return this.teamsConn;
    }
	public void connectMobs() {
        try {
            String url = "jdbc:sqlite:plugins/Mineswarm/mobspawners.db";
            // create a connection to the database
            mobConn = DriverManager.getConnection(url);
        } catch (SQLException e) {
        	plugin.getLogger().info(e.getMessage());
        }
    }
	public void connect() {
        try {
            String url = "jdbc:sqlite:plugins/Mineswarm/mineswarm.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
        	plugin.getLogger().info(e.getMessage());
        }
    }
	public void connectPlayers() {
        try {
            String url = "jdbc:sqlite:plugins/Mineswarm/playerdata.db";
            // create a connection to the database
            playerConn = DriverManager.getConnection(url);
        } catch (SQLException e) {
        	plugin.getLogger().info(e.getMessage());
        }
    }

*/
	
	//TODO combine all tables into one database, makes way more sense.
	public void setupDatabases() { createTable(); }
	
	private void createTable() {
        /*
         * CREATE PRIMARY DATA TABLE -- DOORS & ZONES.
         */
		Connection conn = establishDatabaseConnection("mineswarm");
        if(conn == null){ return; }
        PreparedStatement pstmt = null;        
        try {
        	pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS zones(id,min_x,min_y,min_z,max_x,max_y,max_z,level,creator,world,pvp_enabled,mob_multiplier)"); 
        	pstmt.execute();
        	closePreparedStatementQuietly(pstmt);
            
            pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS doors(id, level, block_y,creator)");
            pstmt.execute();
            closePreparedStatementQuietly(pstmt);
            
        } catch (SQLException e) {plugin.getLogger().warning(e.getMessage());}
        finally {
        	closePreparedStatementQuietly(pstmt);
        	closeQuietly(conn);
        }
        
        /*
         * CREATE BUTTONS TABLE.
         */
		conn = establishDatabaseConnection("buttons");
        if(conn == null){ return; }
        pstmt = null;        
        try {
        	pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS buttons(x,y,z,world,class)"); 
        	pstmt.execute();
        	closePreparedStatementQuietly(pstmt);            
        } catch (SQLException e) {plugin.getLogger().warning(e.getMessage());}
        finally {
        	closePreparedStatementQuietly(pstmt);
        	closeQuietly(conn);
        }
        
        /*
         * CREATE PLAYER DATA TABLE.
         */
		conn = establishDatabaseConnection("playerdata");
        if(conn == null){ return; }
        pstmt = null;        
        try {
        	pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS players("
            		+ "name,"
            		+ "total_damage_taken,"
            		+ "total_damage_delt,"
            		+ "kit,"
            		+ "has_died,"
            		+ "isdown,"
            		+ "first_joined,"
            		+ "team_name,"
            		+ "team_size,"
            		+ "deaths,"
            		+ "players_saved,"
            		+ "downs,"
            		+ "been_revived,"
            		+ "start_time,"
            		+ "end_time,"
            		+ "mobs_killed)"); 
        	pstmt.execute();
        	closePreparedStatementQuietly(pstmt);            
        } catch (SQLException e) {plugin.getLogger().warning(e.getMessage());}
        finally {
        	closePreparedStatementQuietly(pstmt);
        	closeQuietly(conn);
        }
        
        /*
         * CREATE BUTTONS TABLE.
         */
		conn = establishDatabaseConnection("chests");
        if(conn == null){ return; }
        pstmt = null;        
        try {
        	pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS chests(x,y,z,world,creator,items)"); 
        	pstmt.execute();
        	closePreparedStatementQuietly(pstmt);            
        } catch (SQLException e) {plugin.getLogger().warning(e.getMessage());}
        finally {
        	closePreparedStatementQuietly(pstmt);
        	closeQuietly(conn);
        }
        
    }
	
		
    public void createTeamsTable() {
    	this.teamsConn = getTeamsConnection();
    	if(this.teamsConn == null) {
    		plugin.getLogger().info("Connection returned null.");
    		return;
    	}
    	
        String sql = "CREATE TABLE IF NOT EXISTS teams (name TEXT, owner TEXT, closed INTEGER, score INTEGER)";
        try {
        	PreparedStatement pstmt = teamsConn.prepareStatement(sql);
            pstmt.execute();
            
            pstmt = teamsConn.prepareStatement("CREATE TABLE IF NOT EXISTS members(team_id INTEGER, member TEXT)");
            pstmt.execute();
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
        finally{ try {teamsConn.close();} catch (SQLException e) {} }
    }	
    public void deleteTeamsTables() {
    	File file = new File(System.getProperty("user.dir") +"/Mineswarm/teams.db");
        
        if(file.delete()) 
        { 
            System.out.println("File deleted successfully"); 
        }
    }	
    public void emptyTeamsTable() {
    	this.teamsConn = getTeamsConnection();
    	if(this.teamsConn == null) {
    		plugin.getLogger().info("Connection returned null.");
    		return;
    	}
    	try {
    		String sql = "DELETE FROM teams";
    		PreparedStatement pstmt = teamsConn.prepareStatement(sql);
    		pstmt.execute();
    		
    		
    		sql = "DELETE FROM members";
    		pstmt = teamsConn.prepareStatement(sql);
    		pstmt.execute();
		} catch (SQLException e) {
    		plugin.getLogger().info("Error emptying Teams Tables " + e.toString());
		}
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
    	
        String sql = "CREATE TABLE IF NOT EXISTS spawners(location,world,etype,max_mobs,chance,weapons,durability,enchantments, effects)";
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
	
	public void createScoresTable() {
		Connection conn = establishDatabaseConnection("scoreboard");
        if(conn == null){ return; }
    	String scoresSQL =
    			"CREATE TABLE IF NOT EXISTS solo_scores("
        		+ "name,"
        		+ "total_damage_taken,"
        		+ "total_damage_delt,"
        		+ "kit,"
        		+ "first_joined,"
        		+ "team_name,"
        		+ "team_size,"
        		+ "deaths,"
        		+ "players_saved,"
        		+ "downs,"
        		+ "been_revived,"
        		+ "start_time,"
        		+ "end_time,"
        		+ "run_time,"
        		+ "mobs_killed,"
        		+ "team_members"
        		+ ");";//May need to adjust so it can be NAME or TEAM NAME if the team wins...
    	String scoresSQLTeam =
    			"CREATE TABLE IF NOT EXISTS team_scores("
        		+ "total_damage_taken,"//Loop through team mates to get damage?
        		+ "total_damage_delt,"
        		+ "team_name,"
        		+ "team_size,"
        		+ "deaths,"
        		+ "players_saved,"
        		+ "downs,"
        		+ "been_revived,"
        		+ "start_time,"//Owner start time?
        		+ "end_time,"//now
        		+ "run_time,"//Difference...
        		+ "mobs_killed,"
        		+ "team_members"
        		+ ");";//May need to adjust so it can be NAME or TEAM NAME if the team wins...
        try {
        	PreparedStatement pstmt = scoreboardConn.prepareStatement(scoresSQL);
            pstmt.execute();
            pstmt = scoreboardConn.prepareStatement(scoresSQLTeam);
            pstmt.execute();
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
        finally{ try {scoreboardConn.close();} catch (SQLException e) {} }
    }	

	//PROBABLY NEED TO MAKE 2 SCORE BOARD TABELS, ONE FOR TEAMS AND ONE FOR SOLO
	//TWO DIFFERENT FUNCTIONS, ONE INSERTS SOLO, ONE INSERTS TEAMS
	//IF PLAYER IS PART OF A TEAM THAT IS GREATER THAN 1 insert into teams, otherwise solo.
	
	
	
	
	public HashMap<String, List<UUID>> getTeams(){
    	this.teamsConn = getTeamsConnection();
    	if(this.teamsConn == null) {
    		plugin.getLogger().info("Connection returned null.");
    		return null;
    	}
		HashMap<String, List<UUID>> newData = new HashMap<>();
    	String sql = "SELECT * FROM teams";
    	try {
        	PreparedStatement pstmt = teamsConn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
            	List<UUID> ids = new ArrayList<>();
            	for(String val : Arrays.asList(rs.getString("data").split("\\s*,\\s*"))) {ids.add(UUID.fromString(val));}
            	newData.put(rs.getString("key"), ids);
            }
        } catch (SQLException e) {plugin.getLogger().info("ERROR SELECTING: " + e.getMessage());}
    	finally{ try {teamsConn.close();} catch (SQLException e) {} }		
		return newData;
	}
	public int getLastID(Connection conn) {
		    try {
		    	String results = conn.prepareStatement("SELECT last_insert_rowid() AS LAST_ID;").executeQuery().getString("LAST_ID");
				return Integer.parseInt(results);
			} catch (SQLException e) {
				plugin.getLogger().info(e.toString());
				return -1;
			}
	}
	
    public HashMap<Location, String> getButtons(){
		try{if(buttonsConn.isClosed()){connectButtons();}}
        catch(NullPointerException np){connectButtons();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
		HashMap<Location, String> newData = new HashMap<>();
    	String sql = "SELECT * FROM buttons";//CONSIDER SWITCHING THIS TO A SELECT * WHERE LOCATION = LOCATION, LESS SCALEABLE, BUT REASONABLE
    	try {
        	PreparedStatement pstmt = buttonsConn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
            	//Returns Location object as key, class as value.
            	newData.put((new Location(Bukkit.getWorld(rs.getString("world")), rs.getInt("x"),rs.getInt("y"),rs.getInt("z"))), rs.getString("class"));
            }
        } catch (SQLException e) {plugin.getLogger().info("ERROR SELECTING: " + e.getMessage());}
    	finally{ try {buttonsConn.close();} catch (SQLException e) {} }		
		return newData;
	}
    public void saveButton(Location location, String name) {
		try{if(buttonsConn.isClosed()){connectButtons();}}
        catch(NullPointerException np){connectButtons();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
		
		String sql = "INSERT INTO buttons(x,y,z,world,class) VALUES(?,?,?,?,?)";
			try {
    			PreparedStatement pstmt = buttonsConn.prepareStatement(sql);
        		pstmt.setInt(1, (int)location.getX());
        		pstmt.setInt(2, (int)location.getY());
        		pstmt.setInt(3, (int)location.getZ());
        		pstmt.setString(4, location.getWorld().getName());
        		pstmt.setString(5, name);
        		pstmt.executeUpdate();
    		}catch(Exception err) {plugin.getLogger().info(err.toString());}
	    	finally{ try {buttonsConn.close();} catch (SQLException e) {} }	
			
    }
    
    
    
    
    
    
    
    
	public boolean getScores(int topScores) {
		Connection conn = establishDatabaseConnection("scoreboard");
        if(conn == null){ return false; }
        
    	String sql = "SELECT * FROM scores ORDER BY run_time DESC LIMIT ?";
    	try {
        	PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, topScores);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                //Score data, do something with it...                
            }
        } catch (SQLException e) {plugin.getLogger().info("ERROR SELECTING: " + e.getMessage());}
    	finally{ try {scoreboardConn.close();} catch (SQLException e) {} }
        
		return true;
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
}



