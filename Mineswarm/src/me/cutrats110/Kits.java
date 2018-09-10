package me.cutrats110;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Kits {
	public Plugin plugin;
	public Database db = null;
	private PotionObjects potions = null;
	public Kits(Plugin instance, PotionObjects potions) {
		plugin = instance;
		this.db = new Database(plugin);
		this.potions = potions;
		
	}

	void giveKit(Player player, String kit) {
		//Read standard items (all players get).
		//Check kit against config, if more items needed:
		
		ItemStack guideBook = new ItemStack(Material.WRITTEN_BOOK, 1);
		BookMeta bookMeta = (BookMeta) guideBook.getItemMeta();
		bookMeta.setTitle(plugin.getConfig().getString("guide-title"));
		bookMeta.setAuthor(plugin.getConfig().getString("guide-author"));
		List<String> pages = new ArrayList<String>();
		//256 MAX characters per page.
		for(String page : plugin.getConfig().getStringList("guide-pages")) {
			if(page.length() > 256) {
				plugin.getLogger().info("Warnning, page in guide exceeds maximum number of characters (256) and will be shortened. Please split this page into two pages");
			}
			pages.add(page);
		}
		bookMeta.setPages(pages);
		guideBook.setItemMeta(bookMeta);
		
		player.getInventory().addItem(guideBook);
		
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
		List<String> items;
		
    	items = plugin.getConfig().getStringList(kit.toLowerCase()+"-class");
    	for(String item : items) {
    		try {
    			List<String> stuff = Arrays.asList(item.split("\\s*,\\s*"));
    			ItemStack toAdd = null;
    			
    			
    			try {
    				int pID = Integer.valueOf(stuff.get(0).toString());//Should error out here if it's not an ID...
    				MakePotion potionData = potions.getDrinkableDataById(pID);
    				toAdd = new ItemStack(Material.POTION, Integer.valueOf(stuff.get(1).toString()));    				
    				ItemMeta im = toAdd.getItemMeta();
    				im.setDisplayName(potionData.name);
    				PotionMeta pm = (PotionMeta) im;
    				for(PotionEffectType effect : potionData.effectTypes) {
    					//									Type	time in seconds probably	amplifier(1=2)
    					pm.addCustomEffect(new PotionEffect(effect, (int)potionData.duration, potionData.amplifier), true);
    				}
    				toAdd.setItemMeta(im);
    			}catch(NumberFormatException  nf) {
    				toAdd = new ItemStack(Material.getMaterial(stuff.get(0).toString()),Integer.valueOf(stuff.get(1)));
    			}
    			
    			if(stuff.size() > 2) {//Item has Type, Quantitiy, and some other CS fields...
    				for(int i = 2; i < stuff.size(); i+=2) {
    					try {
    						toAdd.addEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(stuff.get(i).toLowerCase())), Integer.valueOf(stuff.get(i+1)));
    					}
    					catch(IndexOutOfBoundsException bounds) {
    						plugin.getLogger().info("Config misconfiguration at: " + stuff.get(0) + " Index is out of bounds for enchantments.");
    					}
    					catch(Exception err) {
    						plugin.getLogger().info("Unhandled Exception trying to make item enchantments: " + err.toString());
    					}
    				}
    			}
    			
    			player.getInventory().addItem(toAdd);
    		}catch(Exception err) {
    			plugin.getLogger().info("Failed to convert item: " + item + " to item stack.");
    		}
    	}
    	if(items.size() > 0) {
    		player.setMetadata("class",new FixedMetadataValue(plugin, kit.toLowerCase()));
    		player.setMetadata("start_time",new FixedMetadataValue(plugin, timeStamp));
    	}
		
		
		
		
		
	}

}
