package me.cutrats110;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.Door;
import org.bukkit.material.Openable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;


@SuppressWarnings("deprecation")
public class Mineswarm extends JavaPlugin implements Listener{

	public final String version = "1.14.c";
	//private boolean preventDouble = false;
	private Database db = null;
	private PotionObjects potions = new PotionObjects();
	private Kits kits = new Kits(this, potions);
	private TeamBoards board = new TeamBoards(this);
	private MineswarmTeams teams = new MineswarmTeams(this, board);
	private ScheduledMobs smobs = new ScheduledMobs(this, potions);
	
	//Util & logging
	@Override
	public void onEnable(){
		getLogger().info("Mineswarm is starting...");
	    getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("Mineswarm has been enabled");	
		new EventListener(this, teams, board);		
		new ScheduledChests(this, potions);
		new ScheduledBackupDB(this, teams);
        this.saveDefaultConfig();
        db = new Database(this);
		db.connect();
		db.createTable();
		db.createMobsTable();
		db.createChestsTable();
		db.createPlayersTable();		
		db.createScoresTable();
		db.createTeamsTable();
		db.createButtonsTable();
		smobs.startMobs();
	}
	@Override
	public void onDisable(){
		teams.saveTeamData();
		//Loop through online players updating players in the DB...
		
		getLogger().info("Mineswarm has been disabled");
	}
	//Player interaction and events.
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		try{
			Player player = event.getPlayer();
			boolean opendoor = false;
		//if(preventDouble){
			try{
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType().equals(Material.IRON_DOOR)) 
				{
					Block block = event.getClickedBlock();
					
					String blockID = "X:" + String.valueOf(block.getX()) + "Z:"+String.valueOf(block.getZ()) + "W:"+String.valueOf(block.getWorld());
					int blockY = block.getY();
					String level = db.selectDoor(blockID, blockY);
					level = "[" + level + "]";
					
					BlockState state = block.getState();
					for(ItemStack item : player.getInventory()) {
						if(item != null && item.getType().equals(Material.BOOK) && item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getLore().toString().equals(level))
						{
							//https://bukkit.org/threads/open-iron-door-door-deprecated.213967/
							try{
								state = block.getRelative(BlockFace.DOWN).getState();
					            Openable door = (Openable)state.getData();
					            if(door.isOpen()){
					            	return;
					            }
					            else{
					            	opendoor = true;
					            	//Remove Key
									item.setAmount(item.getAmount() -1);
									break;
					            }
							}catch(Exception doorer){
								state = event.getClickedBlock().getState();
					            Openable door = (Openable)state.getData();
					            if(door.isOpen()){
					            	return;
					            }
					            else{
					            	opendoor = true;
					            	//Remove Key
									item.setAmount(item.getAmount() -1);	
									break;
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
				            //((Openable)door).setOpen(true);
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
					    	}, 55L);
					    event.setCancelled(true);//Probably want to cancel but shouldn't matter in adventure mode
					    return;
					}else {
						player.sendMessage("You need a " + level + " key to open this door");
					}
			}
		}
		catch(Exception err)
		{
			getLogger().info("Problem with opening door in MineSwarm. " + err.toString());
		}
		//preventDouble = false; }else{ preventDouble = true;	}	
			
			if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getType().equals(Material.JUNGLE_BUTTON)) {
				Location blockLocation = event.getClickedBlock().getLocation();
				HashMap<Location, String> buttons = db.getButtons();
				
				if(buttons.containsKey(blockLocation)) {
					try{
						if(!(player.hasMetadata("class"))) {
							kits.giveKit(player, buttons.get(blockLocation));
							return;
						}
						else
						{
							player.sendMessage("You can't use more than 1 class, die to pick a new class >:)");
							return;
						}
					}
					catch(Exception er){
						player.sendMessage("Error on class command: " + er.toString());
						return;
					}
				}
				
			}
			
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
	
	//Command based functions.
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){	
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("doorlevel") && sender instanceof Player){
			Block block = player.getTargetBlock(null, 100);
			Location bl = block.getLocation();
			player.sendMessage(bl.toString());
			
			
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("msteam") && sender instanceof Player){
			switch(args[0]) {
				case "tpr":
					if(teams.tpQueue.get(player.getUniqueId()) != null) {
						//A Request already exists, cancel the first one.
						teams.tpQueue.get(player.getUniqueId()).cancel();
						teams.tpQueue.remove(player.getUniqueId());
					}
					teams.addTPAQue(player);
					player.sendMessage("You will be TPed in ~20 seconds, do not move or this will be cancelled");
					break;
				case "tpp":
					if(teams.tpQueue.get(player.getUniqueId()) != null) {
						//A Request already exists, cancel the first one.
						teams.tpQueue.get(player.getUniqueId()).cancel();
						teams.tpQueue.remove(player.getUniqueId());
					}
					teams.addTPAQue(player, args[1]);
					player.sendMessage("You will be TPed in ~20 seconds, do not move or this will be cancelled");
					break;
				case "no":
					teams.tpQueue.get(player.getUniqueId()).cancel();
					break;
				case "join": 
					try {
						teams.joinTeam(player, args[1]);
						return true;
					}catch(IndexOutOfBoundsException ib) {
						player.sendMessage("Please enter a team name you want to join");
					}
					break;
				case "joinr": 
					teams.joinRandom(player);
					return true;
				case "add": 
					try {
						teams.joinTeamAccept(player, args[1]);
						return true;
					}catch(IndexOutOfBoundsException ib) {
						player.sendMessage("Please enter the player name you want to let join");
					}
					break;
				case "deny": 
					try {
						teams.joinTeamDeny(player, args[1]);
						return true;
					}catch(IndexOutOfBoundsException ib) {
						player.sendMessage("Please enter the player name you want to deny joining");
					}
					break;
				case "leave":
					try {
						teams.leaveTeam(player);
						return true;
					}catch(Exception exc) {
						player.sendMessage("Error: " + exc.toString());
					}
					break;
				case "create":
					try {
						teams.createTeam(args[1], player);
						return true;
					}catch(IndexOutOfBoundsException ib) {
						player.sendMessage("Please enter a team name you want to create");
					}
					break;
				case "kick":
					try {
						teams.kickTeamMember(args[1], player);
						return true;
					}catch(IndexOutOfBoundsException ib) {
						player.sendMessage("Please enter the player name you want to kick.");
					}
					break;
				case "list":
					try {
						if(player.hasMetadata("team_name") && player.getMetadata("team_name").get(0).asString().length() > 0) {
							for(String name : teams.getTeamMembersNames(player.getMetadata("team_name").get(0).asString())) {
								player.sendMessage(name);
							}
							
						}
						return true;
					}catch(IndexOutOfBoundsException ib) {
						player.sendMessage("Please enter the player name you want to kick.");
					}
					break;
				case "save":
					try {
						teams.saveTeamData();
						return true;
					}catch(Exception exc) {
						player.sendMessage("Problem saving: " + exc.toString());
					}
					break;
				default:
					player.sendMessage("Invalid subcommand, type /help mineswarm for command details.");
					break;
			}
			//player.sendMessage(bl.toString());
			
			
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("PotionTypes")) {
			for(PotionType x : PotionType.values()) {
				player.sendMessage(x.toString());
			}
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
				List<String> zoneData = db.showZone(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), player.getLocation().getWorld().toString());
				for(String x : zoneData){
					player.sendMessage(x);
				}
				return true;
			}
			catch(Exception er){
				player.sendMessage("Error on show location/s: " + er.toString());
			}
		}
		if (cmd.getName().equalsIgnoreCase("gotozone") && sender instanceof Player){		
			try{
				List<String> zoneData = db.tpToZone(args[0]);
				if(zoneData.size() >= 1){
					Location location = new Location (Bukkit.getWorld(zoneData.get(0)), Integer.valueOf(zoneData.get(1)), Integer.valueOf(zoneData.get(2)), Integer.valueOf(zoneData.get(3)));
					player.teleport(location);
					return true;
				}
				else{
					return false;
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
					
					db.makeZone(args[0].replace("_", " "), min_x, min_y, min_z, max_x, max_y, max_z, args[1].replace("_", " "), player.getName(), player.getMetadata("world1").get(0).asString(), Boolean.valueOf(args[2]), Integer.valueOf(args[3]));

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
				db.makeDoor(blockID, blockY, args[0].replace("_", " "), player.getName());
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
				db.deleteDoor(blockID, blockY, player.getName());
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
				db.destroyDoor(blockID, blockY);
				return true;
			}
			catch(Exception er){
				player.sendMessage("Error on show location/s: " + er.toString());
			}
		}
		if (cmd.getName().equalsIgnoreCase("makespawner") && sender instanceof Player){			
			
			
			try {
				Block block = player.getTargetBlock(null, 10);
				String xyz = block.getX() + ","+block.getY()+ ","+block.getZ();
				String world = player.getWorld().getName();
				
				//Example: /MAKESPAWNER ZOMBIE 5 10 IRON_SWORD 0 ENCHANTMENT:SHARPNESS 3 POTION:56
				
				LivingEntity mob;
				//Spawning just a mob, no weapons, potions, of effects.
				try {
					mob = (LivingEntity) Bukkit.getWorld(world).spawnEntity(block.getLocation(), EntityType.valueOf(args[0]));
				}
				catch(Exception err) {
					player.sendMessage("Unrecognized Mob type.");
					return true;
				}
				if(args.length == 2) {
					// 			XZY, World, Type, Number, Weapon, Chance, Durability, Enchantments, Effects.
					db.makeSpawner(xyz, world, args[0].toUpperCase(), Integer.valueOf(args[1]), "NONE", 0, 0, null, null);
					return true;
				
				}
				List<String> enchantments = new ArrayList<>();
				List<String> effects = new ArrayList<>();
				String weapon = "NONE";

				for(int i = 0; i < args.length; i++) {
					if(StringUtils.countMatches(args[i].toLowerCase(), "enchantment:") >= 1) {enchantments.add(args[i].toUpperCase().replaceAll("enchantment:".toUpperCase(), "")+":"+args[i+1]);}
					if(StringUtils.countMatches(args[i].toLowerCase(), "potion:") >= 1) {effects.add(args[i].toUpperCase().replaceAll("potion:".toUpperCase(), ""));}
					if(StringUtils.countMatches(args[i].toLowerCase(), "weapon:") >= 1) {weapon = args[i].toUpperCase().replaceAll("weapon:".toUpperCase(), "");}
				}
				if(!effects.isEmpty()) {
		    		for(String pid : effects) {			
		    			MakePotion poData = potions.getDrinkableDataById(Integer.valueOf(pid));
		    			for(PotionEffectType effect : poData.effectTypes) {
	    					mob.addPotionEffect((new PotionEffect(effect, Integer.MAX_VALUE, poData.amplifier, true)));
	    				}
		    		}
		    	}
				//ZOMBIE 5 POTION:54
				if(weapon == "NONE") {
					db.makeSpawner(xyz, world, args[0].toUpperCase(), Integer.valueOf(args[1]), weapon, 0, 0, enchantments, effects);
					return true;
				}
				
				try {
					ItemStack item = new ItemStack( Material.matchMaterial(weapon), 1);
					//check here
					ItemMeta meta = item.getItemMeta();
					if (meta instanceof Damageable){
						((Damageable) meta).setDamage((short) Short.valueOf(args[4]));
					}
					//item.setDurability(Short.valueOf(args[4]));
					
					if(!enchantments.isEmpty()) {
						for(String enchantment : enchantments) {
							String[] en = enchantment.split(":");
							item.addEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(en[0].toLowerCase())), Integer.valueOf(en[1]));
						}
					}
				}catch(Exception err) {player.sendMessage("Failed to add enchantment to weapon"); return true;}

				db.makeSpawner(xyz, world, args[0].toUpperCase(), Integer.valueOf(args[1]), weapon, Integer.valueOf(args[3]), Integer.valueOf(args[4]), enchantments, effects);	
			}
			catch(Exception err) {
				getLogger().info(err.toString() + " IN COMMAND MAKESPAWNER");
			}
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("save") && sender instanceof Player){			
			try{

				return true;
			}
			catch(Exception er){
				player.sendMessage("Error ON DB BACKUP: " + er.toString());
			}
		}
		if (cmd.getName().equalsIgnoreCase("chest") && sender instanceof Player){
			
			Chest chest;			
			try {
				Block block = player.getTargetBlock(null, 10);
                chest = (Chest) block.getState();
			}
			catch(Exception err){
				player.sendMessage("That block could not be casted as a chest.");
				return true;
			}
			
			if(args.length <= 1) {
				player.sendMessage("Arguments must be ITEM QUANTITY such as: DIRT 12");
				return false;
			}
			if ( (args.length & 1) != 0 ) { 
				player.sendMessage("Missing argumenets, args should be ITEM QUANTITY");
				return false;
			}
			
			try{
				String items = "";
				for(int i = 0; i < args.length;i+=2) {
					ItemStack toAdd = null;
						try {
							int pID = Integer.valueOf(args[i]);//Should error out here if it's not an ID...
		    				MakePotion potionData = potions.getDrinkableDataById(pID);
		    				if(potionData.isSplash) {
		    					toAdd = new ItemStack(Material.SPLASH_POTION, Integer.valueOf(args[i+1].toString()));
		    				}else {
		    					toAdd = new ItemStack(Material.POTION, Integer.valueOf(args[i+1].toString()));
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
			    			catch(Exception err) {this.getLogger().info("ERROR : " + err.toString());}
							
						}catch(NumberFormatException nfe) {
							try {
								try {
				    				int pID = Integer.valueOf(args[i].toString().replaceAll("TIPPED_ARROW:", ""));//Should error out here if it's not an ID...
				    				MakePotion potionData = potions.getDrinkableDataById(pID);
									if(args[i].toUpperCase().contains("TIPPED_ARROW:")) {    					
				    					toAdd = new ItemStack(Material.TIPPED_ARROW, Integer.valueOf(args[i+1]));
				    					
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
				    				}catch(NumberFormatException nfe2) {
										toAdd = new ItemStack(Material.getMaterial(args[i].toString()),Integer.valueOf(args[i+1]));
									}
								}
							catch(Exception problem) {
								player.sendMessage("Could not convert one of more of the args to an item stack.");
								return false;
							}
							
						}
						catch(Exception err) {
							player.sendMessage("Could not convert one of more of the args to an item stack.");
							return false;
						}
					}				
				for(String item : args) {
					items += item +",";
				}items = items.replaceAll(",$", "");
				
				db.createChest(chest.getX(), chest.getY(), chest.getZ(), chest.getWorld().getName(), player.getName(), items);

				return true;
			}
			catch(Exception er){
				player.sendMessage("Error ON INSERT: " + er.toString());
			}
		}
		if (cmd.getName().equalsIgnoreCase("class")){
			try{
				if(!(player.hasMetadata("class"))) {
					kits.giveKit(player, args[0]);
					return true;
				}
				else
				{
					player.sendMessage("You can't use more than 1 class, die to pick a new class >:)");
					return true;
				}
			}
			catch(Exception er){
				player.sendMessage("Error on class command: " + er.toString());
				return false;
			}
		}
		if (cmd.getName().equalsIgnoreCase("inhand")){
			try{
				player.sendMessage(player.getInventory().getItemInMainHand().getType().toString());
			}
			catch(Exception er){
				player.sendMessage("Error on class command: " + er.toString());
				return false;
			}
		}
		//TODONE
		//USE SQLITE INSTEAD OF FILE!
		if (cmd.getName().equalsIgnoreCase("makebutton")){
			try{
				Block block = player.getTargetBlock(null, 10);
				if(block.getType().equals(Material.JUNGLE_BUTTON)) {
					
					this.getLogger().warning("JUNGLE BUTTONNNNNNN");
					try {
						db.saveButton(block.getLocation(), args[0]);
					}catch (Exception e) {
						this.getLogger().warning(e.toString());
					}
					return true;
				}
			}
			catch(Exception er){
				player.sendMessage("Error on button make command: " + er.toString());
				return false;
			}
		}
		//Overrides vanilla command for teams.
		if (cmd.getName().equalsIgnoreCase("team") && sender instanceof Player){
			player.sendMessage("Native teams are disbaled, try msteam instead.");
			return true;
		}
		return false;
	}
}
