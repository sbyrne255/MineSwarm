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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
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
	public void connectTeams() {
        try {
        	File directory = new File(System.getProperty("user.dir") +"/Mineswarm");
    		if (! directory.exists()){ directory.mkdir(); }
            String url = "jdbc:sqlite:plugins/Mineswarm/teams.db";
            // create a connection to the database
            teamsConn = DriverManager.getConnection(url);
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
	public void connectScores() {
        try {
        	// db parameters
        	File directory = new File(System.getProperty("user.dir") +"/Mineswarm");
    		if (! directory.exists()){ directory.mkdir(); }
            String url = "jdbc:sqlite:plugins/Mineswarm/scoreboard.db";
            // create a connection to the database
            scoreboardConn = DriverManager.getConnection(url);
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
    public void createTeamsTable() {
        try{
        	if(teamsConn.isClosed()){
        		
        		connectTeams();
        	}
        }
        catch(NullPointerException np){
        	connectTeams();
        }
        catch(Exception er){
        	plugin.getLogger().info("Conn check failed. " + er.toString());
        }
    	
        String sql = "CREATE TABLE IF NOT EXISTS teams(key,data)";
        try {
        	PreparedStatement pstmt = teamsConn.prepareStatement(sql);
            pstmt.execute();
            
            pstmt = teamsConn.prepareStatement("CREATE TABLE IF NOT EXISTS players(key,data)");
            pstmt.execute();
            
            pstmt = teamsConn.prepareStatement("CREATE TABLE IF NOT EXISTS uuids(key,data)");
            pstmt.execute();
            
            pstmt = teamsConn.prepareStatement("CREATE TABLE IF NOT EXISTS join_requests(key,data)");
            pstmt.execute();

            pstmt = teamsConn.prepareStatement("CREATE TABLE IF NOT EXISTS servers(data)");
            pstmt.execute();
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
        finally{ try {mobConn.close();} catch (SQLException e) {} }
    }	
    public void clearTeamsTables() {
    	File file = new File(System.getProperty("user.dir") +"/Mineswarm/teams.db");
        
        if(file.delete()) 
        { 
            System.out.println("File deleted successfully"); 
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
	public void createPlayersTable() {
        try{if(playerConn.isClosed()){ connectPlayers(); }}
        catch(NullPointerException np){connectPlayers();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
        String sql = "CREATE TABLE IF NOT EXISTS players("
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
        		+ "mobs_killed"
        		+ ");";
        try {
        	PreparedStatement pstmt = playerConn.prepareStatement(sql);
            pstmt.execute();
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
        finally{ try {playerConn.close();} catch (SQLException e) {} }
    }	
	public void createScoresTable() {
        try{if(scoreboardConn.isClosed()){ connectScores(); }}
        catch(NullPointerException np){connectScores();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
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
		try{if(teamsConn.isClosed()){connectTeams();}}
        catch(NullPointerException np){connectTeams();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
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
    public void saveTeams(HashMap<String, List<UUID>> teamData) {
		try{if(teamsConn.isClosed()){connectTeams();}}
        catch(NullPointerException np){connectTeams();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
		
		String sql = "INSERT INTO teams(key,data) VALUES(?,?)";
		
    	for (Map.Entry<String, List<UUID>> entry : teamData.entrySet()) {
    		try {
    			PreparedStatement pstmt = teamsConn.prepareStatement(sql);
        		pstmt.setString(1, entry.getKey());
        		pstmt.setString(2, StringUtils.join(entry.getValue(), ","));
        		pstmt.executeUpdate();
    		}catch(Exception err) {plugin.getLogger().info(err.toString());}
    		
    	}
    }
	
    public HashMap<String,UUID> getUUIDLookup(){
		try{if(teamsConn.isClosed()){connectTeams();}}
        catch(NullPointerException np){connectTeams();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
		HashMap<String, UUID> newData = new HashMap<>();
    	String sql = "SELECT * FROM uuids";
    	try {
        	PreparedStatement pstmt = teamsConn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
            	newData.put(rs.getString("key"), UUID.fromString(rs.getString("data")));
            }
        } catch (SQLException e) {plugin.getLogger().info("ERROR SELECTING: " + e.getMessage());}
    	finally{ try {teamsConn.close();} catch (SQLException e) {} }		
		return newData;
	}
    public void saveUUIDLookup(HashMap<String, UUID> UUIDData) {
		try{if(teamsConn.isClosed()){connectTeams();}}
        catch(NullPointerException np){connectTeams();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
		
		String sql = "INSERT INTO uuids(key,data) VALUES(?,?)";
		
    	for (Map.Entry<String, UUID> entry : UUIDData.entrySet()) {
    		try {
    			PreparedStatement pstmt = teamsConn.prepareStatement(sql);
        		pstmt.setString(1, entry.getKey());
        		pstmt.setString(2, entry.getValue().toString());
        		pstmt.executeUpdate();
    		}catch(Exception err) {plugin.getLogger().info(err.toString());}
    		
    	}
    }
	
    public HashMap<UUID,String> getPlayers(){
		try{if(teamsConn.isClosed()){connectTeams();}}
        catch(NullPointerException np){connectTeams();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
		HashMap<UUID, String> newData = new HashMap<>();
    	String sql = "SELECT * FROM players";
    	try {
        	PreparedStatement pstmt = teamsConn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
            	newData.put(UUID.fromString(rs.getString("key")), rs.getString("data"));
            }
        } catch (SQLException e) {plugin.getLogger().info("ERROR SELECTING: " + e.getMessage());}
    	finally{ try {teamsConn.close();} catch (SQLException e) {} }		
		return newData;
	}
    public void savePlayers(HashMap<UUID, String> playerData) {
		try{if(teamsConn.isClosed()){connectTeams();}}
        catch(NullPointerException np){connectTeams();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
		
		String sql = "INSERT INTO players(key,data) VALUES(?,?)";
		
    	for (Map.Entry<UUID, String> entry : playerData.entrySet()) {
    		try {
    			PreparedStatement pstmt = teamsConn.prepareStatement(sql);
        		pstmt.setString(1, entry.getKey().toString());
        		pstmt.setString(2, entry.getValue());
        		pstmt.executeUpdate();
    		}catch(Exception err) {plugin.getLogger().info(err.toString());}
    		
    	}
    }
	
	public HashMap<String, String> getJoinRequests(){
		try{if(teamsConn.isClosed()){connectTeams();}}
        catch(NullPointerException np){connectTeams();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
		HashMap<String, String> newData = new HashMap<>();
    	String sql = "SELECT * FROM join_requests";
    	try {
        	PreparedStatement pstmt = teamsConn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
            	newData.put(rs.getString("key"), rs.getString("data"));
            }
        } catch (SQLException e) {plugin.getLogger().info("ERROR SELECTING: " + e.getMessage());}
    	finally{ try {teamsConn.close();} catch (SQLException e) {} }		
		return newData;
	}
    public void saveJoinRequests(HashMap<String, String> joinData) {
		try{if(teamsConn.isClosed()){connectTeams();}}
        catch(NullPointerException np){connectTeams();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
		
		String sql = "INSERT INTO join_requests(key,data) VALUES(?,?)";
		
    	for (Map.Entry<String, String> entry : joinData.entrySet()) {
    		try {
    			PreparedStatement pstmt = teamsConn.prepareStatement(sql);
        		pstmt.setString(1, entry.getKey());
        		pstmt.setString(2, entry.getValue());
        		pstmt.executeUpdate();
    		}catch(Exception err) {plugin.getLogger().info(err.toString());}
    		
    	}
    }
	
	public List<UUID> getServers(){
		try{if(teamsConn.isClosed()){connectTeams();}}
        catch(NullPointerException np){connectTeams();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
		List<UUID> newData = new ArrayList<>();
    	String sql = "SELECT * FROM servers";
    	try {
        	PreparedStatement pstmt = teamsConn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
            	newData.add(UUID.fromString(rs.getString("data")));
            }
        } catch (SQLException e) {plugin.getLogger().info("ERROR SELECTING: " + e.getMessage());}
    	finally{ try {teamsConn.close();} catch (SQLException e) {} }		
		return newData;
	}
    public void saveServers(List<UUID> serverData) {
		try{if(teamsConn.isClosed()){connectTeams();}}
        catch(NullPointerException np){connectTeams();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
		
		String sql = "INSERT INTO servers(data) VALUES(?)";
		for(UUID value : serverData) {
			try {
    			PreparedStatement pstmt = teamsConn.prepareStatement(sql);
        		pstmt.setString(1, value.toString());
        		pstmt.executeUpdate();
    		}catch(Exception err) {plugin.getLogger().info(err.toString());}
    	}
    }
	
    
    
    
    
    
    
    
    
    
    
	public boolean getScores(int topScores) {
        try{if(scoreboardConn.isClosed()){connectScores();}}
        catch(NullPointerException np){connectScores();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
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
    public void setSoloScore(Player player) {
    	try{if(scoreboardConn.isClosed()){connectScores();}}
        catch(NullPointerException np){connectScores();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
    	
    	String sql = "INSERT INTO solo_scores(name,total_damage_taken,total_damage_delt,kit,first_joined,deaths,players_saved,downs,been_revived,start_time,end_time,run_time,mobs_killed) "
    			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (
        	PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	try { pstmt.setString(1, player.getName()); } catch(Exception iob) {pstmt.setString(1, "ERROR");}
        	try { pstmt.setInt(2, player.getMetadata("total_damage_taken").get(0).asInt()); } catch(Exception iob) {pstmt.setInt(2, 0);}
        	try { pstmt.setInt(3, player.getMetadata("total_damage_delt").get(0).asInt()); } catch(Exception iob) {pstmt.setInt(3, 0);}
        	try { pstmt.setString(4, player.getMetadata("class").get(0).asString()); } catch(Exception iob) {pstmt.setString(4, "");}
        	try { pstmt.setString(5, player.getMetadata("first_joined").get(0).asString()); } catch(Exception iob) {pstmt.setString(5, "");}
        	try { pstmt.setInt(8, player.getMetadata("deaths").get(0).asInt()); } catch(Exception iob) {pstmt.setInt(8, 0);}
        	try { pstmt.setInt(9, player.getMetadata("players_saved").get(0).asInt()); } catch(Exception iob) {pstmt.setInt(9, 0);}
        	try { pstmt.setInt(10, player.getMetadata("downs").get(0).asInt()); } catch(Exception iob) {pstmt.setInt(10, 0);}
        	try { pstmt.setInt(11, player.getMetadata("revived").get(0).asInt()); } catch(Exception iob) {pstmt.setInt(11, 0);}
        	try { pstmt.setString(12, player.getMetadata("start_time").get(0).asString()); } catch(Exception iob) {pstmt.setString(12, "UNKNOWN");}
        	try { pstmt.setString(13, player.getMetadata("end_time").get(0).asString()); } catch(Exception iob) {pstmt.setString(13, "UNKNOWN");}
        	try { 
        		pstmt.setInt(14, Integer.valueOf(player.getMetadata("end_time").get(0).asString()) - Integer.valueOf(player.getMetadata("start_time").get(0).asString())); 
        	} catch(Exception iob) {
        		pstmt.setInt(14, Integer.valueOf(new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime())));
        	}
        	try { pstmt.setString(15, player.getMetadata("mobs_killed").get(0).asString()); } catch(Exception iob) {pstmt.setString(15, "");}
        	
            pstmt.executeUpdate();
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
        finally{ try {scoreboardConn.close();} catch (SQLException e) {} }
    }
	
    public void setTeamScore(Player player, MineswarmTeams teamData) {
    	
    	try{if(scoreboardConn.isClosed()){connectScores();}}
        catch(NullPointerException np){connectScores();}
        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
    	
    	//12
    	String sql = "INSERT INTO team_scores(total_damage_taken,total_damage_delt,deaths,players_saved,downs,been_revived,start_time,end_time,run_time,mobs_killed, team_name,team_size) "
    			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
        	String teamName = player.getMetadata("team_name").get(0).asString();
        	List<Player> teamMembers = teamData.getTeamMembers(teamName);
        	int teamDmg = 0;
        	int teamTakenDmg = 0;
        	int teamDeaths = 0;
        	int teamSaves = 0;
        	int teamDowns = 0;
        	int teamBeenRevived = 0;
        	int teamSize = teamMembers.size();
        	String teamMobsKilled = "";
        	//Loop for every player in team, get their player data from SQLite (I'll need to make sure to update the DB before I call set score)
        	for(Player p : teamMembers) {
        		 String playerSQL = "SELECT * FROM players WHERE name = ?";
        	        try{if(playerConn.isClosed()){connectPlayers();}}
        	        catch(NullPointerException np){connectPlayers();}
        	        catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
        	        try {
        	            	PreparedStatement pstmt = playerConn.prepareStatement(playerSQL);
        	                pstmt.setString(1, p.getName());           
        	                ResultSet rs = pstmt.executeQuery();
        	                while (rs.next()) {
        	                	teamDmg += rs.getInt("total_damage_delt");
        	                	teamTakenDmg += rs.getInt("total_damage_taken");               
        	                	teamDeaths +=  rs.getInt("deaths");
        	                    teamSaves += rs.getInt("players_saved");
        	                    teamBeenRevived += rs.getInt("been_revived");
        	                    teamDowns += rs.getInt("downs");
        	                    teamMobsKilled += rs.getString("mobs_killed");
        	                    playerConn.close();
        	                }
        	        }
        	        catch (Exception e) {plugin.getLogger().info("ERROR SELECTING PLAYER...: " + e.toString());}
        	        finally{ try {playerConn.close();} catch (SQLException e) {} }        		
        	}
        	
        	PreparedStatement pstmt = conn.prepareStatement(sql);
        	pstmt.setInt(1, teamTakenDmg);
        	pstmt.setInt(2, teamDmg);
        	pstmt.setInt(3, teamDeaths);
        	pstmt.setInt(4, teamSaves);
        	pstmt.setInt(5, teamDowns);
        	pstmt.setInt(6, teamBeenRevived);
        	pstmt.setString(7, "START TIME");
        	pstmt.setString(8, "END TIME");
        	pstmt.setString(9, "RUN TIME");
        	pstmt.setString(10, teamMobsKilled);
        	pstmt.setString(11, teamName);
        	pstmt.setInt(12, teamSize);
            pstmt.executeUpdate();
        } catch (SQLException e) {plugin.getLogger().info(e.getMessage());}
        finally{ try {scoreboardConn.close();} catch (SQLException e) {} }
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
							try {
			    				int pID = Integer.valueOf(items.get(i).toString());//Should error out here if it's not an ID...
			    				MakePotion potionData = potions.getDrinkableDataById(pID);
			    				ItemStack toAdd = new ItemStack(Material.POTION, Integer.valueOf(items.get(i+1).toString()));    				
			    				ItemMeta im = toAdd.getItemMeta();
			    				im.setDisplayName(potionData.name);
			    				PotionMeta pm = (PotionMeta) im;
			    				for(PotionEffectType effect : potionData.effectTypes) {
			    					//									Type	time in seconds probably	amplifier(1=2)
			    					pm.addCustomEffect(new PotionEffect(effect, (int)potionData.duration, potionData.amplifier), true);
			    				}
			    				toAdd.setItemMeta(im);
			    				contents[j] = toAdd;
								
							}catch(NumberFormatException nfe) {
								contents[j] = new ItemStack(Material.getMaterial(items.get(i).toString()), Integer.valueOf(items.get(i+1)));	
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
    
    //Rebuild 3 functions, setPlayerData, newPlayerData, updatePlayerData
    public boolean setPlayerData(Player player){  	
        String sql = "SELECT * FROM players WHERE name = ? LIMIT 1";
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
                	
                	//what acutally matters? isdown won't anymore sense you die on exit...
                	//What if server still holds meta data after player levaes, then this set might be going to index 1 not 0...
                	player.setMetadata("total_damage_taken",new FixedMetadataValue(plugin, rs.getInt("total_damage_taken")));
                	player.setMetadata("total_damage_taken",new FixedMetadataValue(plugin, rs.getInt("total_damage_delt")));
                	if(rs.getString("kit") != null && rs.getString("kit").length() > 0) {
                		player.setMetadata("class",new FixedMetadataValue(plugin, rs.getString("kit")));
                	} 
                    if(rs.getString("team_name") != null && rs.getString("team_name").length() > 0) {
                    	player.setMetadata("team_name",new FixedMetadataValue(plugin, rs.getString("team_name")));
                    }     
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
    	String sql = "INSERT INTO players(name,total_damage_taken, total_damage_delt, kit, has_died, isdown, first_joined, team_name, team_size, deaths, players_saved, downs, been_revived, start_time) "
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
        	pstmt.setString(8,null);
        	pstmt.setInt(9,1);
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
    	String sql = "UPDATE players SET total_damage_taken = ?, total_damage_delt = ?, kit = ?, has_died = ?, isdown = ?, team_name = ?, team_size = ?, deaths = ?, players_saved = ?, downs = ?, been_revived = ?, start_time = ?, mobs_killed = ? WHERE name = ?";
    	
    	try{if(playerConn.isClosed()){connectPlayers();}
        }catch(NullPointerException np){connectPlayers();
        }catch(Exception er){plugin.getLogger().info("Conn check failed. " + er.toString());}
        try {
        	PreparedStatement pstmt = playerConn.prepareStatement(sql);//13 fields...
        	try { pstmt.setInt(1, player.getMetadata("total_damage_taken").get(0).asInt()); } catch(Exception iob) {pstmt.setInt(1, 0);}
        	try { pstmt.setInt(2, player.getMetadata("total_damage_delt").get(0).asInt()); } catch(Exception iob) {pstmt.setInt(2, 0);}
        	try { pstmt.setString(3, player.getMetadata("class").get(0).asString()); } catch(Exception iob) {pstmt.setString(3, "");}
        	try { pstmt.setBoolean(4, player.getMetadata("hasdied").get(0).asBoolean()); } catch(Exception iob) {pstmt.setBoolean(4, false);}
        	try { pstmt.setBoolean(5, player.getMetadata("isdown").get(0).asBoolean()); } catch(Exception iob) {pstmt.setBoolean(5, false);}
        	try { pstmt.setString(6, player.getMetadata("team_name").get(0).asString()); } catch(Exception iob) {pstmt.setString(6, "");}
        	try { pstmt.setInt(7, player.getMetadata("team_size").get(0).asInt()); } catch(Exception iob) {pstmt.setInt(7, 1);}
        	try { pstmt.setInt(8, player.getMetadata("deaths").get(0).asInt()); } catch(Exception iob) {pstmt.setInt(8, 0);}
        	try { pstmt.setInt(9, player.getMetadata("players_saved").get(0).asInt()); } catch(Exception iob) {pstmt.setInt(9, 0);}
        	try { pstmt.setInt(10, player.getMetadata("downs").get(0).asInt()); } catch(Exception iob) {pstmt.setInt(10, 0);}
        	try { pstmt.setInt(11, player.getMetadata("revived").get(0).asInt()); } catch(Exception iob) {pstmt.setInt(11, 0);}
        	try { pstmt.setString(12, player.getMetadata("start_time").get(0).asString()); } catch(Exception iob) {pstmt.setString(12, "NOT IMPLEMENTED YET");}
        	try { pstmt.setString(13, player.getMetadata("mobs_killed").get(0).asString()); } catch(Exception iob) {pstmt.setString(13, "");}
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



