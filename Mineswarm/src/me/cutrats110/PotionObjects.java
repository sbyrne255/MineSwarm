package me.cutrats110;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.potion.PotionEffectType;

public class PotionObjects {
	
	private List<MakePotion> splashPotions = new ArrayList<MakePotion>();
	
	public PotionObjects() {
		splashPotions.add(new MakePotion(0, "Turtle Master", 	Arrays.asList(PotionEffectType.SLOW,PotionEffectType.DAMAGE_RESISTANCE), 20, Arrays.asList(4,3), "Makes player move slower and grants damage resistance"));
		splashPotions.add(new MakePotion(0, "Turtle Master", 	Arrays.asList(PotionEffectType.SLOW,PotionEffectType.DAMAGE_RESISTANCE), 40, Arrays.asList(4,3), "Makes player move slower and grants damage resistance"));
		splashPotions.add(new MakePotion(0, "Turtle Master", 	Arrays.asList(PotionEffectType.SLOW,PotionEffectType.DAMAGE_RESISTANCE), 20, Arrays.asList(6,4), "Makes player move slower and grants damage resistance"));
		splashPotions.add(new MakePotion(0, "Night Vision", 	Arrays.asList(PotionEffectType.NIGHT_VISION), 	180, 	0, "Grants player night vision"));
		splashPotions.add(new MakePotion(0, "Night Vision", 	Arrays.asList(PotionEffectType.NIGHT_VISION), 	480, 	0, "Grants player night vision"));
		splashPotions.add(new MakePotion(0, "Invisibility", 	Arrays.asList(PotionEffectType.INVISIBILITY), 	180, 	0, "Makes player invisible"));
		splashPotions.add(new MakePotion(0, "Invisibility", 	Arrays.asList(PotionEffectType.INVISIBILITY), 	480, 	0, "Makes player invisible"));
		splashPotions.add(new MakePotion(0, "Leaping", 		Arrays.asList(PotionEffectType.JUMP), 			180, 	0, "Makes player jump higher"));
		splashPotions.add(new MakePotion(0, "Leaping", 		Arrays.asList(PotionEffectType.JUMP), 			480, 	0, "Makes player jump higher"));
		splashPotions.add(new MakePotion(0, "Leaping", 		Arrays.asList(PotionEffectType.JUMP), 			90, 	1, "Makes player jump higher 2"));
		splashPotions.add(new MakePotion(0, "Fire Resistance", Arrays.asList(PotionEffectType.FIRE_RESISTANCE),180,	0, "Makes player not be harmed by fire"));
		splashPotions.add(new MakePotion(0, "Fire Resistance", Arrays.asList(PotionEffectType.FIRE_RESISTANCE),480, 	0, "Makes player not be harmed by fire"));
		splashPotions.add(new MakePotion(0, "Speed", 			Arrays.asList(PotionEffectType.SPEED), 			180, 	0, "Makes player move faster"));
		splashPotions.add(new MakePotion(0, "Speed", 			Arrays.asList(PotionEffectType.SPEED), 			480, 	0, "Makes player move faster"));
		splashPotions.add(new MakePotion(0, "Speed", 			Arrays.asList(PotionEffectType.SPEED), 			90, 	1, "Makes player move faster"));
		splashPotions.add(new MakePotion(0, "Slowness", 		Arrays.asList(PotionEffectType.SLOW), 			90, 	0, "Makes player move slower"));
		splashPotions.add(new MakePotion(0, "Slowness", 		Arrays.asList(PotionEffectType.SLOW), 			240, 	0, "Makes player move slower"));
		splashPotions.add(new MakePotion(0, "Slowness", 		Arrays.asList(PotionEffectType.SLOW), 			20, 	3, "Makes player move slower"));
		splashPotions.add(new MakePotion(0, "Water Breathing", Arrays.asList(PotionEffectType.WATER_BREATHING),180, 	0, "Makes player breath under water"));
		splashPotions.add(new MakePotion(4, "Water Breathing", Arrays.asList(PotionEffectType.WATER_BREATHING),(480*10*2), 	0, "Makes player breath under water"));
		splashPotions.add(new MakePotion(0, "Healing", 		Arrays.asList(PotionEffectType.HEAL), 			0, 		0, "Instantly curse Damage"));
		splashPotions.add(new MakePotion(23, "Healing", 		Arrays.asList(PotionEffectType.HEAL), 			0, 		1, "Instantly curse Damage"));
		splashPotions.add(new MakePotion(0, "Harming", 		Arrays.asList(PotionEffectType.HARM), 			0, 		0, "Instantly causes Damage"));
		splashPotions.add(new MakePotion(0, "Harming", 		Arrays.asList(PotionEffectType.HARM), 			0, 		1, "Instantly causes Damage (2)"));
		splashPotions.add(new MakePotion(0, "Poison", 			Arrays.asList(PotionEffectType.POISON), 		45, 	0, "Poison player or heals undead"));
		splashPotions.add(new MakePotion(0, "Poison", 			Arrays.asList(PotionEffectType.POISON), 		90, 	0, "Poison player or heals undead"));
		splashPotions.add(new MakePotion(0, "Poison 2", 		Arrays.asList(PotionEffectType.POISON), 		21, 	1, "Poison player or heals undead (2)"));
		splashPotions.add(new MakePotion(0, "Regeneration", 	Arrays.asList(PotionEffectType.REGENERATION), 	45, 	0, "Regenerates someting?"));
		splashPotions.add(new MakePotion(3, "Regeneration", 	Arrays.asList(PotionEffectType.REGENERATION), 	(90*10*2), 	0, "Regeneration"));
		splashPotions.add(new MakePotion(0, "Regeneration 2", 	Arrays.asList(PotionEffectType.REGENERATION), 	22, 	1, "Regeneration"));
		splashPotions.add(new MakePotion(0, "Strength", 		Arrays.asList(PotionEffectType.INCREASE_DAMAGE),180, 	0, "Strength players attack"));
		splashPotions.add(new MakePotion(0, "Strength", 		Arrays.asList(PotionEffectType.INCREASE_DAMAGE),480, 	0, "Strength players attack"));
		splashPotions.add(new MakePotion(0, "Strength 2", 		Arrays.asList(PotionEffectType.INCREASE_DAMAGE),90, 	1, "Strength players attack"));
		splashPotions.add(new MakePotion(0, "Weakness", 		Arrays.asList(PotionEffectType.WEAKNESS), 		90, 	0, "Weakens players attack"));
		splashPotions.add(new MakePotion(0, "Weakness", 		Arrays.asList(PotionEffectType.WEAKNESS), 		240, 	0, "Weakens players attack"));
		splashPotions.add(new MakePotion(0, "Luck", 			Arrays.asList(PotionEffectType.LUCK), 			300, 	0, "We're up all night to get lucky"));
		splashPotions.add(new MakePotion(0, "Slow Falling", 	Arrays.asList(PotionEffectType.SLOW_FALLING), 	90, 	0, "Reduces fall damage"));
		splashPotions.add(new MakePotion(0, "Slow Falling", 	Arrays.asList(PotionEffectType.SLOW_FALLING), 	240, 	0, "Reduces fall damage"));
		
		
		
		
	}
	
	public String getNameByEffects(List<PotionEffectType> effectTypes) {
		for(MakePotion potion :splashPotions) {
			if(potion.effectTypes.equals(effectTypes)) {
				return potion.name;
			}
		}
		
		return null;
	}
	public int getIdByEffects(List<PotionEffectType> effectTypes) {
		for(MakePotion potion :splashPotions) {
			if(potion.effectTypes.equals(effectTypes)) {
				return potion.id;
			}
		}
		
		return -1;
	}

	public MakePotion getDrinkableDataById(int id) {
		for(MakePotion potion :splashPotions) {
			if(potion.id == id) {
				return potion;
			}
		}
		return null;
	}

}
