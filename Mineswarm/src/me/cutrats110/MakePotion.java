package me.cutrats110;

import java.util.List;

import org.bukkit.potion.PotionEffectType;

public class MakePotion {
	public int id;
	public String name;
	public List<PotionEffectType> effectTypes;
	public double duration;
	public String description;
	public int amplifier;
	public List<Integer> amplifierList;
	
	
	public MakePotion(int id, String name, List<PotionEffectType> effectTypes, double duration, List<Integer> amplifier, String description) {
		this.id = id;
		this.name = name;
		this.effectTypes = effectTypes;
		this.duration = duration;
		this.amplifierList = amplifier;
		this.description = description;		
	}
	public MakePotion(int id, String name, List<PotionEffectType> effectTypes, double duration, int amplifier, String description) {
		this.id = id;
		this.name = name;
		this.effectTypes = effectTypes;
		this.duration = duration;
		this.amplifier = amplifier;
		this.description = description;		
	}
}