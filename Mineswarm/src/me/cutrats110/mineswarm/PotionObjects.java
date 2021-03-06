package me.cutrats110.mineswarm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.potion.PotionEffectType;

public class PotionObjects {
	//TODO Document and re-assess if this is still needed when working outside of development builds of bukkit.
	private List<MakePotion> splashPotions = new ArrayList<MakePotion>();
	
	public PotionObjects() {
		splashPotions.add(new MakePotion(0, "Water", 			Arrays.asList(PotionEffectType.LUCK), 				(0), 	0, false, Color.BLUE, "With a duration of 0 no effects should happen."));
		splashPotions.add(new MakePotion(1, "Turtle Master", 	Arrays.asList(PotionEffectType.SLOW,PotionEffectType.DAMAGE_RESISTANCE), 20, Arrays.asList(4,3), false, Color.BLUE,  "Makes player move slower and grants damage resistance"));
		splashPotions.add(new MakePotion(2, "Turtle Master", 	Arrays.asList(PotionEffectType.SLOW,PotionEffectType.DAMAGE_RESISTANCE), 40, Arrays.asList(4,3), false,  Color.BLUE, "Makes player move slower and grants damage resistance"));
		splashPotions.add(new MakePotion(3, "Turtle Master", 	Arrays.asList(PotionEffectType.SLOW,PotionEffectType.DAMAGE_RESISTANCE), 20, Arrays.asList(6,4), false,  Color.BLUE, "Makes player move slower and grants damage resistance"));
		splashPotions.add(new MakePotion(4, "Night Vision", 	Arrays.asList(PotionEffectType.NIGHT_VISION), 		(180*10*2), 	0, false,  Color.BLUE, "Grants player night vision"));
		splashPotions.add(new MakePotion(5, "Night Vision", 	Arrays.asList(PotionEffectType.NIGHT_VISION), 		(480*10*2), 	0, false,  Color.BLUE, "Grants player night vision"));
		splashPotions.add(new MakePotion(6, "Invisibility", 	Arrays.asList(PotionEffectType.INVISIBILITY), 		(180*10*2), 	0, false,  Color.GRAY, "Makes player invisible"));
		splashPotions.add(new MakePotion(7, "Invisibility", 	Arrays.asList(PotionEffectType.INVISIBILITY), 		(480*10*2), 	0, false,  Color.GRAY, "Makes player invisible"));
		splashPotions.add(new MakePotion(8, "Leaping", 			Arrays.asList(PotionEffectType.JUMP), 				(180*10*2), 	0, false,  Color.GREEN, "Makes player jump higher"));
		splashPotions.add(new MakePotion(9, "Leaping", 			Arrays.asList(PotionEffectType.JUMP), 				(480*10*2), 	0, false,  Color.GREEN, "Makes player jump higher"));
		splashPotions.add(new MakePotion(10, "Leaping", 		Arrays.asList(PotionEffectType.JUMP), 				(90*10*2), 		1, false,  Color.GREEN, "Makes player jump higher 2"));
		splashPotions.add(new MakePotion(11, "Fire Resistance", Arrays.asList(PotionEffectType.FIRE_RESISTANCE),	(180*10*2),		0, false,  Color.ORANGE, "Makes player not be harmed by fire"));
		splashPotions.add(new MakePotion(12, "Fire Resistance", Arrays.asList(PotionEffectType.FIRE_RESISTANCE),	(480*10*2), 	0, false,  Color.ORANGE, "Makes player not be harmed by fire"));
		splashPotions.add(new MakePotion(13, "Speed", 			Arrays.asList(PotionEffectType.SPEED), 				(180*10*2), 	0, false,  Color.TEAL, "Makes player move faster"));
		splashPotions.add(new MakePotion(14, "Speed", 			Arrays.asList(PotionEffectType.SPEED), 				(480*10*2), 	0, false,  Color.TEAL, "Makes player move faster"));
		splashPotions.add(new MakePotion(15, "Speed 2", 		Arrays.asList(PotionEffectType.SPEED), 				(90*10*2), 		1, false,  Color.TEAL, "Makes player move faster"));
		splashPotions.add(new MakePotion(16, "Slowness", 		Arrays.asList(PotionEffectType.SLOW), 				(90*10*2), 		0, false,  Color.RED, "Makes player move slower"));
		splashPotions.add(new MakePotion(17, "Slowness", 		Arrays.asList(PotionEffectType.SLOW), 				(240*10*2), 	0, false,  Color.BLUE, "Makes player move slower"));
		splashPotions.add(new MakePotion(18, "Slowness", 		Arrays.asList(PotionEffectType.SLOW), 				(20*10*2), 		3, false,  Color.BLUE, "Makes player move slower"));
		splashPotions.add(new MakePotion(19, "Water Breathing", Arrays.asList(PotionEffectType.WATER_BREATHING),	(180*10*2), 	0, false,  Color.BLUE, "Makes player breath under water"));
		splashPotions.add(new MakePotion(20, "Water Breathing", Arrays.asList(PotionEffectType.WATER_BREATHING),	(480*10*2), 	0, false,  Color.BLUE, "Makes player breath under water"));
		splashPotions.add(new MakePotion(21, "Healing", 		Arrays.asList(PotionEffectType.HEAL), 				2, 				0, false,  Color.RED, "Instantly curse Damage"));
		splashPotions.add(new MakePotion(22, "Healing", 		Arrays.asList(PotionEffectType.HEAL), 				2, 				1, false,  Color.RED, "Instantly curse Damage"));
		splashPotions.add(new MakePotion(23, "Harming", 		Arrays.asList(PotionEffectType.HARM), 				2, 				0, false,  Color.BLUE, "Instantly causes Damage"));
		splashPotions.add(new MakePotion(24, "Harming", 		Arrays.asList(PotionEffectType.HARM), 				2, 				1, false,  Color.BLUE, "Instantly causes Damage (2)"));
		splashPotions.add(new MakePotion(24, "Poison", 			Arrays.asList(PotionEffectType.POISON), 			(45*10*2), 		0, false,  Color.BLUE, "Poison player or heals undead"));
		splashPotions.add(new MakePotion(26, "Poison", 			Arrays.asList(PotionEffectType.POISON), 			(90*10*2), 		0, false,  Color.BLUE, "Poison player or heals undead"));
		splashPotions.add(new MakePotion(27, "Poison 2", 		Arrays.asList(PotionEffectType.POISON), 			(21*10*2), 		1, false,  Color.BLUE, "Poison player or heals undead (2)"));
		splashPotions.add(new MakePotion(28, "Regeneration", 	Arrays.asList(PotionEffectType.REGENERATION), 		(45*10*2), 		0, false,  Color.BLUE, "Regenerates someting?"));
		splashPotions.add(new MakePotion(29, "Regeneration", 	Arrays.asList(PotionEffectType.REGENERATION), 		(90*10*2), 		0, false,  Color.BLUE, "Regeneration"));
		splashPotions.add(new MakePotion(30, "Regeneration 2", 	Arrays.asList(PotionEffectType.REGENERATION), 		(22*10*2), 		1, false,  Color.BLUE, "Regeneration"));
		splashPotions.add(new MakePotion(31, "Strength", 		Arrays.asList(PotionEffectType.INCREASE_DAMAGE),	(180*10*2), 	0, false,  Color.BLUE, "Strength players attack"));
		splashPotions.add(new MakePotion(32, "Strength", 		Arrays.asList(PotionEffectType.INCREASE_DAMAGE),	(480*10*2), 	0, false,  Color.BLUE, "Strength players attack"));
		splashPotions.add(new MakePotion(33, "Strength 2", 		Arrays.asList(PotionEffectType.INCREASE_DAMAGE),	(90*10*2), 		1, false,  Color.BLUE, "Strength players attack"));
		splashPotions.add(new MakePotion(34, "Weakness", 		Arrays.asList(PotionEffectType.WEAKNESS), 			(90*10*2), 		0, false,  Color.BLUE, "Weakens players attack"));
		splashPotions.add(new MakePotion(35, "Weakness", 		Arrays.asList(PotionEffectType.WEAKNESS), 			(240*10*2), 	0, false,  Color.BLUE, "Weakens players attack"));
		splashPotions.add(new MakePotion(36, "Luck", 			Arrays.asList(PotionEffectType.LUCK), 				(300*10*2), 	0, false,  Color.BLUE, "We're up all night to get lucky"));
		splashPotions.add(new MakePotion(37, "Slow Falling", 	Arrays.asList(PotionEffectType.SLOW_FALLING), 		(90*10*2), 		0, false,  Color.BLUE, "Reduces fall damage"));
		splashPotions.add(new MakePotion(38, "Slow Falling", 	Arrays.asList(PotionEffectType.SLOW_FALLING), 		(240*10*2), 	0, false,  Color.BLUE, "Reduces fall damage"));
		splashPotions.add(new MakePotion(39, "Saturation", 		Arrays.asList(PotionEffectType.SATURATION), 		(4), 			0, false,  Color.BLUE, "Feeds player 1 food per level per tick"));
		splashPotions.add(new MakePotion(40, "Saturation", 		Arrays.asList(PotionEffectType.SATURATION), 		(4), 			1, false,  Color.BLUE, "Feeds player 1 food per level per tick"));
		splashPotions.add(new MakePotion(41, "Saturation", 		Arrays.asList(PotionEffectType.SATURATION), 		(4),		 	2, false,  Color.BLUE, "Feeds player 1 food per level per tick"));
		splashPotions.add(new MakePotion(42, "Saturation", 		Arrays.asList(PotionEffectType.SATURATION), 		(4), 			3, false,  Color.BLUE, "Feeds player 1 food per level per tick"));
		
		splashPotions.add(new MakePotion(1900, "Water", 			Arrays.asList(PotionEffectType.LUCK), 				(0), 			0, true,  Color.BLUE, "With a duration of 0 no effects should happen."));
		splashPotions.add(new MakePotion(1901, "Turtle Master", 	Arrays.asList(PotionEffectType.SLOW,PotionEffectType.DAMAGE_RESISTANCE), 20, Arrays.asList(4,3), true,  Color.BLUE, "Makes player move slower and grants damage resistance"));
		splashPotions.add(new MakePotion(1902, "Turtle Master", 	Arrays.asList(PotionEffectType.SLOW,PotionEffectType.DAMAGE_RESISTANCE), 40, Arrays.asList(4,3), true,  Color.BLUE, "Makes player move slower and grants damage resistance"));
		splashPotions.add(new MakePotion(1903, "Turtle Master", 	Arrays.asList(PotionEffectType.SLOW,PotionEffectType.DAMAGE_RESISTANCE), 20, Arrays.asList(6,4), true,  Color.BLUE, "Makes player move slower and grants damage resistance"));
		splashPotions.add(new MakePotion(1904, "Night Vision", 		Arrays.asList(PotionEffectType.NIGHT_VISION), 		(180*10*2), 	0, true,  Color.BLUE, "Grants player night vision"));
		splashPotions.add(new MakePotion(1905, "Night Vision", 		Arrays.asList(PotionEffectType.NIGHT_VISION), 		(480*10*2), 	0, true,  Color.BLUE, "Grants player night vision"));
		splashPotions.add(new MakePotion(1906, "Invisibility", 		Arrays.asList(PotionEffectType.INVISIBILITY), 		(180*10*2), 	0, true,  Color.BLUE, "Makes player invisible"));
		splashPotions.add(new MakePotion(1907, "Invisibility", 		Arrays.asList(PotionEffectType.INVISIBILITY), 		(480*10*2), 	0, true,  Color.BLUE, "Makes player invisible"));
		splashPotions.add(new MakePotion(1908, "Leaping", 			Arrays.asList(PotionEffectType.JUMP), 				(180*10*2), 	0, true,  Color.BLUE, "Makes player jump higher"));
		splashPotions.add(new MakePotion(1909, "Leaping", 			Arrays.asList(PotionEffectType.JUMP), 				(480*10*2), 	0, true,  Color.BLUE, "Makes player jump higher"));
		splashPotions.add(new MakePotion(19010, "Leaping", 			Arrays.asList(PotionEffectType.JUMP), 				(90*10*2), 		1, true,  Color.BLUE, "Makes player jump higher 2"));
		splashPotions.add(new MakePotion(19011, "Fire Resistance",	Arrays.asList(PotionEffectType.FIRE_RESISTANCE),	(180*10*2),		0, true,  Color.BLUE, "Makes player not be harmed by fire"));
		splashPotions.add(new MakePotion(19012, "Fire Resistance",	Arrays.asList(PotionEffectType.FIRE_RESISTANCE),	(480*10*2), 	0, true,  Color.BLUE, "Makes player not be harmed by fire"));
		splashPotions.add(new MakePotion(19013, "Speed", 			Arrays.asList(PotionEffectType.SPEED), 				(180*10*2), 	0, true,  Color.BLUE, "Makes player move faster"));
		splashPotions.add(new MakePotion(19014, "Speed", 			Arrays.asList(PotionEffectType.SPEED), 				(480*10*2), 	0, true,  Color.BLUE, "Makes player move faster"));
		splashPotions.add(new MakePotion(19015, "Speed 2", 			Arrays.asList(PotionEffectType.SPEED), 				(90*10*2), 		1, true,  Color.BLUE, "Makes player move faster"));
		splashPotions.add(new MakePotion(19016, "Slowness", 		Arrays.asList(PotionEffectType.SLOW), 				(90*10*2), 		0, true,  Color.BLUE, "Makes player move slower"));
		splashPotions.add(new MakePotion(19017, "Slowness", 		Arrays.asList(PotionEffectType.SLOW), 				(240*10*2), 	0, true,  Color.BLUE, "Makes player move slower"));
		splashPotions.add(new MakePotion(19018, "Slowness", 		Arrays.asList(PotionEffectType.SLOW), 				(20*10*2), 		3, true,  Color.BLUE, "Makes player move slower"));
		splashPotions.add(new MakePotion(19019, "Water Breathing",	Arrays.asList(PotionEffectType.WATER_BREATHING),	(180*10*2), 	0, true,  Color.BLUE, "Makes player breath under water"));
		splashPotions.add(new MakePotion(19020, "Water Breathing", 	Arrays.asList(PotionEffectType.WATER_BREATHING),	(480*10*2), 	0, true,  Color.BLUE, "Makes player breath under water"));
		splashPotions.add(new MakePotion(19021, "Healing", 			Arrays.asList(PotionEffectType.HEAL), 				2, 				0, true,  Color.BLUE, "Instantly cures Damage"));
		splashPotions.add(new MakePotion(19022, "Healing 2", 		Arrays.asList(PotionEffectType.HEAL), 				2, 				1, true,  Color.BLUE, "Instantly cures Damage"));
		splashPotions.add(new MakePotion(19023, "Harming", 			Arrays.asList(PotionEffectType.HARM), 				2, 				0, true,  Color.BLUE, "Instantly causes Damage"));
		splashPotions.add(new MakePotion(19024, "Harming", 			Arrays.asList(PotionEffectType.HARM), 				2, 				1, true,  Color.BLUE, "Instantly causes Damage (2)"));
		splashPotions.add(new MakePotion(19024, "Poison", 			Arrays.asList(PotionEffectType.POISON), 			(45*10*2), 		0, true,  Color.BLUE, "Poison player or heals undead"));
		splashPotions.add(new MakePotion(19026, "Poison", 			Arrays.asList(PotionEffectType.POISON), 			(90*10*2), 		0, true,  Color.BLUE, "Poison player or heals undead"));
		splashPotions.add(new MakePotion(19027, "Poison 2", 		Arrays.asList(PotionEffectType.POISON), 			(21*10*2), 		1, true,  Color.BLUE, "Poison player or heals undead (2)"));
		splashPotions.add(new MakePotion(19028, "Regeneration", 	Arrays.asList(PotionEffectType.REGENERATION), 		(45*10*2), 		0, true,  Color.BLUE, "Regenerates someting?"));
		splashPotions.add(new MakePotion(19029, "Regeneration", 	Arrays.asList(PotionEffectType.REGENERATION), 		(90*10*2), 		0, true,  Color.BLUE, "Regeneration"));
		splashPotions.add(new MakePotion(19030, "Regeneration 2", 	Arrays.asList(PotionEffectType.REGENERATION), 		(22*10*2), 		1, true,  Color.BLUE, "Regeneration"));
		splashPotions.add(new MakePotion(19031, "Strength", 		Arrays.asList(PotionEffectType.INCREASE_DAMAGE),	(180*10*2), 	0, true,  Color.BLUE, "Strength players attack"));
		splashPotions.add(new MakePotion(19032, "Strength", 		Arrays.asList(PotionEffectType.INCREASE_DAMAGE),	(480*10*2), 	0, true,  Color.BLUE, "Strength players attack"));
		splashPotions.add(new MakePotion(19033, "Strength 2", 		Arrays.asList(PotionEffectType.INCREASE_DAMAGE),	(90*10*2), 		1, true,  Color.BLUE, "Strength players attack"));
		splashPotions.add(new MakePotion(19034, "Weakness", 		Arrays.asList(PotionEffectType.WEAKNESS), 			(90*10*2), 		0, true,  Color.BLUE, "Weakens players attack"));
		splashPotions.add(new MakePotion(19035, "Weakness", 		Arrays.asList(PotionEffectType.WEAKNESS), 			(240*10*2), 	0, true,  Color.BLUE, "Weakens players attack"));
		splashPotions.add(new MakePotion(19036, "Luck", 			Arrays.asList(PotionEffectType.LUCK), 				(300*10*2), 	0, true,  Color.BLUE, "We're up all night to get lucky"));
		splashPotions.add(new MakePotion(19037, "Slow Falling", 	Arrays.asList(PotionEffectType.SLOW_FALLING), 		(90*10*2), 		0, true,  Color.BLUE, "Reduces fall damage"));
		splashPotions.add(new MakePotion(19038, "Slow Falling", 	Arrays.asList(PotionEffectType.SLOW_FALLING), 		(240*10*2), 	0, true,  Color.BLUE, "Reduces fall damage"));
		splashPotions.add(new MakePotion(19039, "Saturation", 		Arrays.asList(PotionEffectType.SATURATION), 		(4), 	0, true,  Color.BLUE, "Feeds player 1 food per level per tick"));
		splashPotions.add(new MakePotion(19040, "Saturation", 		Arrays.asList(PotionEffectType.SATURATION), 		(4), 	1, true,  Color.BLUE, "Feeds player 1 food per level per tick"));
		splashPotions.add(new MakePotion(19041, "Saturation", 		Arrays.asList(PotionEffectType.SATURATION), 		(4), 	2, true,  Color.BLUE, "Feeds player 1 food per level per tick"));
		splashPotions.add(new MakePotion(19041, "Saturation", 		Arrays.asList(PotionEffectType.SATURATION), 		(4), 	3, true,  Color.BLUE, "Feeds player 1 food per level per tick"));
		splashPotions.add(new MakePotion(19041, "Dolphin's Grace",	Arrays.asList(PotionEffectType.DOLPHINS_GRACE), 	(4), 	3, true,  Color.BLUE, "Feeds player 1 food per level per tick"));
		
		
		
		
	}
	
	public String getNameByEffects(List<PotionEffectType> effectTypes) {
		for(MakePotion potion :splashPotions) {
			if(potion.effectTypes.equals(effectTypes)) {
				return potion.name;
			}
		}
		
		return null;
	}
	public MakePotion getPotionByEffects(List<PotionEffectType> effectTypes) {
		for(MakePotion potion :splashPotions) {
			if(potion.effectTypes.equals(effectTypes)) {
				return potion;
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
