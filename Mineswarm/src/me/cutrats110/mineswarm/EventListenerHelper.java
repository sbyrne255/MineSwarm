package me.cutrats110.mineswarm;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class EventListenerHelper {
	private TeamBoards board = null;
	private MineswarmTeams teams = null;
	public Database db = null;
	HashMap<UUID,BukkitTask> downedPlayers;
	HashMap<UUID,MSPlayer> players;
	Plugin plugin;

	public EventListenerHelper(TeamBoards boards, MineswarmTeams teams, Database db, HashMap<UUID, MSPlayer> players, HashMap<UUID,BukkitTask> downedPlayers) {
		this.board = boards;
		this.teams = teams;
		this.db = db;
		this.downedPlayers = downedPlayers;
		this.players = players;
	}
	public EventListenerHelper(TeamBoards boards, MineswarmTeams teams, Database db, HashMap<UUID, MSPlayer> players, HashMap<UUID,BukkitTask> downedPlayers, Plugin plugin) {
		this.board = boards;
		this.teams = teams;
		this.db = db;
		this.downedPlayers = downedPlayers;
		this.players = players;
		this.plugin = plugin;
	}
	
	public void resetPlayerFromLastStand(Player player) {
        player.setWalkSpeed((float) .2);
        player.setGlowing(false);
        MSTeam team = teams.getTeam(player);
        if(team != null) {
        	board.setScoreboard(team.getMembersPlayerObjects(), team.getName(), player, 20);
        }
        
        BukkitTask downedTask = downedPlayers.get(player.getUniqueId());
		if(downedTask != null) {
			downedTask.cancel();
			downedPlayers.remove(player.getUniqueId());
		}
	}
	public void clearPlayerFromTPQue(Player player) {
		BukkitTask PlayerTPTask = teams.tpQueue.get(player.getUniqueId());
		if(PlayerTPTask != null) {
			PlayerTPTask.cancel();
			teams.tpQueue.remove(player.getUniqueId());
		}	
	}
	public void updateTeamBoard(Player player) {
		MSTeam team = teams.getTeam(player);
    	if(team != null) {
    		board.makeScoreBoard(team.getName());
        	board.setScoreboard(team.getMembersPlayerObjects(), team.getName());
    	}
	}
	public void updateTeamBoard(Player player, int health) {
		MSTeam team = teams.getTeam(player);
    	if(team != null) {
        	board.setScoreboard(team.getMembersPlayerObjects(), team.getName(), player, health);
    	}
	}
	//Returns true if down, false if not down.
	public boolean isPlayerDown(Player player) {
		if(downedPlayers.get(player.getUniqueId()) != null) {
			return true;
		} else {
			return false;
		}
	}
	public boolean isPlayerOnTeam(Player player) {
		if(teams.getTeam(player) != null) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	
	public void playerOnPlayerDamage(EntityDamageByEntityEvent e) {
		Player damagee = (Player) e.getEntity();
		Player damager = (Player) e.getDamager();
		
		BukkitTask downedDamager = downedPlayers.get(damager.getUniqueId());
		if(downedDamager != null) {
			e.setCancelled(true);
	    	return;
		}
		
		BukkitTask downedDamagee = downedPlayers.get(damagee.getUniqueId());
		if(downedDamagee != null) {
			if(damager.getInventory().getItemInMainHand().getType().equals(Material.PLAYER_HEAD) || damager.getInventory().getItemInOffHand().getType().equals(Material.PLAYER_HEAD) || damager.getInventory().getItemInMainHand().getType().equals(Material.WITHER_SKELETON_SKULL) || damager.getInventory().getItemInOffHand().getType().equals(Material.WITHER_SKELETON_SKULL)) {
			  	damagee.sendMessage(damager.getName() + " has revived you!");
			  	damager.sendMessage("You have revived " + damagee.getName());
			  	
	        	for (PotionEffect effect : damagee.getActivePotionEffects()){damagee.removePotionEffect(effect.getType());}
	        	damagee.setHealth(10);//Reset health to half.
	        	damagee.setWalkSpeed((float) .2);
	        	damagee.setGlowing(false);
	        	
	        	downedDamagee.cancel();
				downedPlayers.remove(damagee.getUniqueId());
	        	
				//Meta data score changes.

				
	        	
	          	if(damager.getInventory().getItemInMainHand().getType().equals(Material.PLAYER_HEAD)) {
		    		ItemStack heads = new ItemStack(Material.PLAYER_HEAD, damager.getInventory().getItemInMainHand().getAmount() -1);
		    		damager.getInventory().setItemInMainHand(heads);
		    	}
	        	if(damager.getInventory().getItemInMainHand().getType().equals(Material.WITHER_SKELETON_SKULL)){
		    		ItemStack heads = new ItemStack(Material.WITHER_SKELETON_SKULL, damager.getInventory().getItemInMainHand().getAmount() -1);
		    		damager.getInventory().setItemInMainHand(heads);
		    	}
		    	if(damager.getInventory().getItemInOffHand().getType().equals(Material.PLAYER_HEAD)) {
		    		ItemStack heads = new ItemStack(Material.PLAYER_HEAD, damager.getInventory().getItemInOffHand().getAmount() -1);
		    		damager.getInventory().setItemInOffHand(heads);
		    	}
		    	if(damager.getInventory().getItemInOffHand().getType().equals(Material.WITHER_SKELETON_SKULL)) {
		    		ItemStack heads = new ItemStack(Material.WITHER_SKELETON_SKULL, damager.getInventory().getItemInOffHand().getAmount() -1);
		    		damager.getInventory().setItemInOffHand(heads);
		    	}
		    	e.setCancelled(true);
		    	return;
			}
			
		}
		if(db.selectZonePVP(damagee.getLocation().getBlockX(), damagee.getLocation().getBlockY(), damagee.getLocation().getBlockZ(), damagee.getLocation().getWorld().toString()) == false) {
			//PVP is off where the damagee is standing
			e.setCancelled(true);
			return;
		}	
	}
	public void playerOnEntityDamage(EntityDamageByEntityEvent e) {
		try {
			Player damager = (Player) e.getDamager();
			//LivingEntity damagee = (LivingEntity) e.getEntity();
			
			BukkitTask downedDamager = downedPlayers.get(damager.getUniqueId());
			if(downedDamager != null) {
				e.setCancelled(true);
		    	return;
			}
		}catch(Exception er) {
			plugin.getLogger().info("BAD TOUCH IN PLAYER_ON_ENTITY_DAMAGE");
		}
	}
	public void entityOnPlayerDamage(EntityDamageByEntityEvent e) {
		Player damagee = (Player) e.getEntity();
		if(isPlayerDown(damagee)) {
			e.setCancelled(true);
			return;
		}
		
		if(isPlayerOnTeam(damagee)) {
			updateTeamBoard(damagee);
		}
		//TODO update damage score.
	}
	public void setPlayerAsDown(Player player) {
		//TODO update scores
		player.setHealth(20);
		player.setWalkSpeed(0);
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10000, 1));
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000, 250));
		player.setGlowing(true);
		player.setSneaking(true);
		downedPlayerScheduler(player);
				
		if(isPlayerOnTeam(player)) {
			MSTeam team = teams.getTeam(player);
			board.setScoreboard(team.getMembersPlayerObjects(), teams.getTeam(player).getName(), player, (int) ((player.getHealth())*-1));
			teams.alertTeamOfDowns(team.getName(), player);	
		}
	}
	private void downedPlayerScheduler(Player player) {
		downedPlayers.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			try {
				if((player.getHealth() - 1) <= 0)
				{
					downedPlayers.get(player.getUniqueId()).cancel();
					downedPlayers.remove(player.getUniqueId());
					player.setHealth(0);
					return;
				}
				else{
					player.setHealth(player.getHealth() - 1);
				}
				this.updateTeamBoard(player);
			}catch(NullPointerException np) {
				plugin.getLogger().info("Damagee or other element is null, likely due to disconnecting. " + np.toString());
				downedPlayers.remove(player.getUniqueId());
				player.setHealth(0);
			}catch(Exception err) { plugin.getLogger().info("Error on slowly killing person. " + err.toString()); }
		}, 10, 35));
		
	}
}
