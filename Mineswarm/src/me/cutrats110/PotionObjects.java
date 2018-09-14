package me.cutrats110;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.potion.PotionEffectType;

public class PotionObjects {
	
	private List<MakePotion> splashPotions = new ArrayList<MakePotion>();
	
	public PotionObjects() {
		splashPotions.add(new MakePotion(1, "Turtle Master", 	Arrays.asList(PotionEffectType.SLOW,PotionEffectType.DAMAGE_RESISTANCE), 20, Arrays.asList(4,3), false, "Makes player move slower and grants damage resistance"));
		splashPotions.add(new MakePotion(2, "Turtle Master", 	Arrays.asList(PotionEffectType.SLOW,PotionEffectType.DAMAGE_RESISTANCE), 40, Arrays.asList(4,3), false, "Makes player move slower and grants damage resistance"));
		splashPotions.add(new MakePotion(3, "Turtle Master", 	Arrays.asList(PotionEffectType.SLOW,PotionEffectType.DAMAGE_RESISTANCE), 20, Arrays.asList(6,4), false, "Makes player move slower and grants damage resistance"));
		splashPotions.add(new MakePotion(4, "Night Vision", 	Arrays.asList(PotionEffectType.NIGHT_VISION), 		(180*10*2), 	0, false, "Grants player night vision"));
		splashPotions.add(new MakePotion(5, "Night Vision", 	Arrays.asList(PotionEffectType.NIGHT_VISION), 		(480*10*2), 	0, false, "Grants player night vision"));
		splashPotions.add(new MakePotion(6, "Invisibility", 	Arrays.asList(PotionEffectType.INVISIBILITY), 		(180*10*2), 	0, false, "Makes player invisible"));
		splashPotions.add(new MakePotion(7, "Invisibility", 	Arrays.asList(PotionEffectType.INVISIBILITY), 		(480*10*2), 	0, false, "Makes player invisible"));
		splashPotions.add(new MakePotion(8, "Leaping", 			Arrays.asList(PotionEffectType.JUMP), 				(180*10*2), 	0, false, "Makes player jump higher"));
		splashPotions.add(new MakePotion(9, "Leaping", 			Arrays.asList(PotionEffectType.JUMP), 				(480*10*2), 	0, false, "Makes player jump higher"));
		splashPotions.add(new MakePotion(10, "Leaping", 		Arrays.asList(PotionEffectType.JUMP), 				(90*10*2), 		1, false, "Makes player jump higher 2"));
		splashPotions.add(new MakePotion(11, "Fire Resistance", Arrays.asList(PotionEffectType.FIRE_RESISTANCE),	(180*10*2),		0, false, "Makes player not be harmed by fire"));
		splashPotions.add(new MakePotion(12, "Fire Resistance", Arrays.asList(PotionEffectType.FIRE_RESISTANCE),	(480*10*2), 	0, false, "Makes player not be harmed by fire"));
		splashPotions.add(new MakePotion(13, "Speed", 			Arrays.asList(PotionEffectType.SPEED), 				(180*10*2), 	0, false, "Makes player move faster"));
		splashPotions.add(new MakePotion(14, "Speed", 			Arrays.asList(PotionEffectType.SPEED), 				(480*10*2), 	0, false, "Makes player move faster"));
		splashPotions.add(new MakePotion(15, "Speed 2", 		Arrays.asList(PotionEffectType.SPEED), 				(90*10*2), 		1, false, "Makes player move faster"));
		splashPotions.add(new MakePotion(16, "Slowness", 		Arrays.asList(PotionEffectType.SLOW), 				(90*10*2), 		0, false, "Makes player move slower"));
		splashPotions.add(new MakePotion(17, "Slowness", 		Arrays.asList(PotionEffectType.SLOW), 				(240*10*2), 	0, false, "Makes player move slower"));
		splashPotions.add(new MakePotion(18, "Slowness", 		Arrays.asList(PotionEffectType.SLOW), 				(20*10*2), 		3, false, "Makes player move slower"));
		splashPotions.add(new MakePotion(19, "Water Breathing", Arrays.asList(PotionEffectType.WATER_BREATHING),	(180*10*2), 	0, false, "Makes player breath under water"));
		splashPotions.add(new MakePotion(20, "Water Breathing", Arrays.asList(PotionEffectType.WATER_BREATHING),	(480*10*2), 	0, false, "Makes player breath under water"));
		splashPotions.add(new MakePotion(21, "Healing", 		Arrays.asList(PotionEffectType.HEAL), 				0, 				0, false, "Instantly curse Damage"));
		splashPotions.add(new MakePotion(22, "Healing", 		Arrays.asList(PotionEffectType.HEAL), 				0, 				1, false, "Instantly curse Damage"));
		splashPotions.add(new MakePotion(23, "Harming", 		Arrays.asList(PotionEffectType.HARM), 				0, 				0, false, "Instantly causes Damage"));
		splashPotions.add(new MakePotion(24, "Harming", 		Arrays.asList(PotionEffectType.HARM), 				0, 				1, false, "Instantly causes Damage (2)"));
		splashPotions.add(new MakePotion(24, "Poison", 			Arrays.asList(PotionEffectType.POISON), 			(45*10*2), 		0, false, "Poison player or heals undead"));
		splashPotions.add(new MakePotion(26, "Poison", 			Arrays.asList(PotionEffectType.POISON), 			(90*10*2), 		0, false, "Poison player or heals undead"));
		splashPotions.add(new MakePotion(27, "Poison 2", 		Arrays.asList(PotionEffectType.POISON), 			(21*10*2), 		1, false, "Poison player or heals undead (2)"));
		splashPotions.add(new MakePotion(28, "Regeneration", 	Arrays.asList(PotionEffectType.REGENERATION), 		(45*10*2), 		0, false, "Regenerates someting?"));
		splashPotions.add(new MakePotion(29, "Regeneration", 	Arrays.asList(PotionEffectType.REGENERATION), 		(90*10*2), 		0, false, "Regeneration"));
		splashPotions.add(new MakePotion(30, "Regeneration 2", 	Arrays.asList(PotionEffectType.REGENERATION), 		(22*10*2), 		1, false, "Regeneration"));
		splashPotions.add(new MakePotion(31, "Strength", 		Arrays.asList(PotionEffectType.INCREASE_DAMAGE),	(180*10*2), 	0, false, "Strength players attack"));
		splashPotions.add(new MakePotion(32, "Strength", 		Arrays.asList(PotionEffectType.INCREASE_DAMAGE),	(480*10*2), 	0, false, "Strength players attack"));
		splashPotions.add(new MakePotion(33, "Strength 2", 		Arrays.asList(PotionEffectType.INCREASE_DAMAGE),	(90*10*2), 		1, false, "Strength players attack"));
		splashPotions.add(new MakePotion(34, "Weakness", 		Arrays.asList(PotionEffectType.WEAKNESS), 			(90*10*2), 		0, false, "Weakens players attack"));
		splashPotions.add(new MakePotion(35, "Weakness", 		Arrays.asList(PotionEffectType.WEAKNESS), 			(240*10*2), 	0, false, "Weakens players attack"));
		splashPotions.add(new MakePotion(36, "Luck", 			Arrays.asList(PotionEffectType.LUCK), 				(300*10*2), 	0, false, "We're up all night to get lucky"));
		splashPotions.add(new MakePotion(37, "Slow Falling", 	Arrays.asList(PotionEffectType.SLOW_FALLING), 		(90*10*2), 		0, false, "Reduces fall damage"));
		splashPotions.add(new MakePotion(38, "Slow Falling", 	Arrays.asList(PotionEffectType.SLOW_FALLING), 		(240*10*2), 	0, false, "Reduces fall damage"));
		
		splashPotions.add(new MakePotion(1901, "Turtle Master", 	Arrays.asList(PotionEffectType.SLOW,PotionEffectType.DAMAGE_RESISTANCE), 20, Arrays.asList(4,3), true, "Makes player move slower and grants damage resistance"));
		splashPotions.add(new MakePotion(1902, "Turtle Master", 	Arrays.asList(PotionEffectType.SLOW,PotionEffectType.DAMAGE_RESISTANCE), 40, Arrays.asList(4,3), true, "Makes player move slower and grants damage resistance"));
		splashPotions.add(new MakePotion(1903, "Turtle Master", 	Arrays.asList(PotionEffectType.SLOW,PotionEffectType.DAMAGE_RESISTANCE), 20, Arrays.asList(6,4), true, "Makes player move slower and grants damage resistance"));
		splashPotions.add(new MakePotion(1904, "Night Vision", 		Arrays.asList(PotionEffectType.NIGHT_VISION), 		(180*10*2), 	0, true, "Grants player night vision"));
		splashPotions.add(new MakePotion(1905, "Night Vision", 		Arrays.asList(PotionEffectType.NIGHT_VISION), 		(480*10*2), 	0, true, "Grants player night vision"));
		splashPotions.add(new MakePotion(1906, "Invisibility", 		Arrays.asList(PotionEffectType.INVISIBILITY), 		(180*10*2), 	0, true, "Makes player invisible"));
		splashPotions.add(new MakePotion(1907, "Invisibility", 		Arrays.asList(PotionEffectType.INVISIBILITY), 		(480*10*2), 	0, true, "Makes player invisible"));
		splashPotions.add(new MakePotion(1908, "Leaping", 			Arrays.asList(PotionEffectType.JUMP), 				(180*10*2), 	0, true, "Makes player jump higher"));
		splashPotions.add(new MakePotion(1909, "Leaping", 			Arrays.asList(PotionEffectType.JUMP), 				(480*10*2), 	0, true, "Makes player jump higher"));
		splashPotions.add(new MakePotion(19010, "Leaping", 			Arrays.asList(PotionEffectType.JUMP), 				(90*10*2), 		1, true, "Makes player jump higher 2"));
		splashPotions.add(new MakePotion(19011, "Fire Resistance",	Arrays.asList(PotionEffectType.FIRE_RESISTANCE),	(180*10*2),		0, true, "Makes player not be harmed by fire"));
		splashPotions.add(new MakePotion(19012, "Fire Resistance",	Arrays.asList(PotionEffectType.FIRE_RESISTANCE),	(480*10*2), 	0, true, "Makes player not be harmed by fire"));
		splashPotions.add(new MakePotion(19013, "Speed", 			Arrays.asList(PotionEffectType.SPEED), 				(180*10*2), 	0, true, "Makes player move faster"));
		splashPotions.add(new MakePotion(19014, "Speed", 			Arrays.asList(PotionEffectType.SPEED), 				(480*10*2), 	0, true, "Makes player move faster"));
		splashPotions.add(new MakePotion(19015, "Speed 2", 			Arrays.asList(PotionEffectType.SPEED), 				(90*10*2), 		1, true, "Makes player move faster"));
		splashPotions.add(new MakePotion(19016, "Slowness", 		Arrays.asList(PotionEffectType.SLOW), 				(90*10*2), 		0, true, "Makes player move slower"));
		splashPotions.add(new MakePotion(19017, "Slowness", 		Arrays.asList(PotionEffectType.SLOW), 				(240*10*2), 	0, true, "Makes player move slower"));
		splashPotions.add(new MakePotion(19018, "Slowness", 		Arrays.asList(PotionEffectType.SLOW), 				(20*10*2), 		3, true, "Makes player move slower"));
		splashPotions.add(new MakePotion(19019, "Water Breathing",	Arrays.asList(PotionEffectType.WATER_BREATHING),	(180*10*2), 	0, true, "Makes player breath under water"));
		splashPotions.add(new MakePotion(19020, "Water Breathing", 	Arrays.asList(PotionEffectType.WATER_BREATHING),	(480*10*2), 	0, true, "Makes player breath under water"));
		splashPotions.add(new MakePotion(19021, "Healing", 			Arrays.asList(PotionEffectType.HEAL), 				0, 				0, true, "Instantly curse Damage"));
		splashPotions.add(new MakePotion(19022, "Healing", 			Arrays.asList(PotionEffectType.HEAL), 				0, 				1, true, "Instantly curse Damage"));
		splashPotions.add(new MakePotion(19023, "Harming", 			Arrays.asList(PotionEffectType.HARM), 				0, 				0, true, "Instantly causes Damage"));
		splashPotions.add(new MakePotion(19024, "Harming", 			Arrays.asList(PotionEffectType.HARM), 				0, 				1, true, "Instantly causes Damage (2)"));
		splashPotions.add(new MakePotion(19024, "Poison", 			Arrays.asList(PotionEffectType.POISON), 			(45*10*2), 		0, true, "Poison player or heals undead"));
		splashPotions.add(new MakePotion(19026, "Poison", 			Arrays.asList(PotionEffectType.POISON), 			(90*10*2), 		0, true, "Poison player or heals undead"));
		splashPotions.add(new MakePotion(19027, "Poison 2", 		Arrays.asList(PotionEffectType.POISON), 			(21*10*2), 		1, true,"Poison player or heals undead (2)"));
		splashPotions.add(new MakePotion(19028, "Regeneration", 	Arrays.asList(PotionEffectType.REGENERATION), 		(45*10*2), 		0, true, "Regenerates someting?"));
		splashPotions.add(new MakePotion(19029, "Regeneration", 	Arrays.asList(PotionEffectType.REGENERATION), 		(90*10*2), 		0, true, "Regeneration"));
		splashPotions.add(new MakePotion(19030, "Regeneration 2", 	Arrays.asList(PotionEffectType.REGENERATION), 		(22*10*2), 		1, true, "Regeneration"));
		splashPotions.add(new MakePotion(19031, "Strength", 		Arrays.asList(PotionEffectType.INCREASE_DAMAGE),	(180*10*2), 	0, true, "Strength players attack"));
		splashPotions.add(new MakePotion(19032, "Strength", 		Arrays.asList(PotionEffectType.INCREASE_DAMAGE),	(480*10*2), 	0, true, "Strength players attack"));
		splashPotions.add(new MakePotion(19033, "Strength 2", 		Arrays.asList(PotionEffectType.INCREASE_DAMAGE),	(90*10*2), 		1, true, "Strength players attack"));
		splashPotions.add(new MakePotion(19034, "Weakness", 		Arrays.asList(PotionEffectType.WEAKNESS), 			(90*10*2), 		0, true, "Weakens players attack"));
		splashPotions.add(new MakePotion(19035, "Weakness", 		Arrays.asList(PotionEffectType.WEAKNESS), 			(240*10*2), 	0, true, "Weakens players attack"));
		splashPotions.add(new MakePotion(19036, "Luck", 			Arrays.asList(PotionEffectType.LUCK), 				(300*10*2), 	0, true, "We're up all night to get lucky"));
		splashPotions.add(new MakePotion(19037, "Slow Falling", 	Arrays.asList(PotionEffectType.SLOW_FALLING), 		(90*10*2), 		0, true, "Reduces fall damage"));
		splashPotions.add(new MakePotion(19038, "Slow Falling", 	Arrays.asList(PotionEffectType.SLOW_FALLING), 		(240*10*2), 	0, true, "Reduces fall damage"));
		
		
		
		
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
