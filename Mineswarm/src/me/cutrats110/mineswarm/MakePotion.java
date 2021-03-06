package me.cutrats110.mineswarm;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.potion.PotionEffectType;

public class MakePotion {
	public int id;
	public String name;
	public List<PotionEffectType> effectTypes;
	public double duration;
	public String description;
	public int amplifier;
	public boolean isSplash;
	public List<Integer> amplifierList;
	public Color color;
	
	
	public MakePotion(int id, String name, List<PotionEffectType> effectTypes, double duration, List<Integer> amplifier, boolean isSplash, Color color, String description) {
		this.id = id;
		this.name = name;
		this.effectTypes = effectTypes;
		this.duration = duration;
		this.amplifierList = amplifier;
		this.description = description;		
		this.isSplash = isSplash;
		this.color = color;
	}
	public MakePotion(int id, String name, List<PotionEffectType> effectTypes, double duration, int amplifier, boolean isSplash,  Color color,String description) {
		this.id = id;
		this.name = name;
		this.effectTypes = effectTypes;
		this.duration = duration;
		this.amplifier = amplifier;
		this.description = description;		
		this.isSplash = isSplash;
		this.color = color;
	}
}

