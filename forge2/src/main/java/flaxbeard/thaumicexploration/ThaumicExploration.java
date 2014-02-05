package flaxbeard.thaumicexploration;



import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.StepSound;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.tuple.MutablePair;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.ThaumcraftCraftingManager;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import flaxbeard.thaumicexploration.block.BlockBootsIce;
import flaxbeard.thaumicexploration.block.BlockBoundChest;
import flaxbeard.thaumicexploration.block.BlockBoundJar;
import flaxbeard.thaumicexploration.block.BlockCrucibleSouls;
import flaxbeard.thaumicexploration.block.BlockEverfullUrn;
import flaxbeard.thaumicexploration.block.BlockReplicator;
import flaxbeard.thaumicexploration.block.BlockTaintBerries;
import flaxbeard.thaumicexploration.block.BlockThinkTank;
import flaxbeard.thaumicexploration.common.CommonProxy;
import flaxbeard.thaumicexploration.enchantment.EnchantmentBinding;
import flaxbeard.thaumicexploration.enchantment.EnchantmentDisarm;
import flaxbeard.thaumicexploration.enchantment.EnchantmentNightVision;
import flaxbeard.thaumicexploration.event.TXBootsEventHandler;
import flaxbeard.thaumicexploration.event.TXEventHandler;
import flaxbeard.thaumicexploration.event.TXTickHandler;
import flaxbeard.thaumicexploration.gui.TXGuiHandler;
import flaxbeard.thaumicexploration.integration.TTIntegration;
import flaxbeard.thaumicexploration.item.ItemBlankSeal;
import flaxbeard.thaumicexploration.item.ItemBrain;
import flaxbeard.thaumicexploration.item.ItemChestSeal;
import flaxbeard.thaumicexploration.item.ItemChestSealLinked;
import flaxbeard.thaumicexploration.item.ItemFoodTalisman;
import flaxbeard.thaumicexploration.item.ItemTXArmorSpecial;
import flaxbeard.thaumicexploration.item.ItemTXArmorSpecialDiscount;
import flaxbeard.thaumicexploration.item.ItemTaintSeedFood;
import flaxbeard.thaumicexploration.item.focus.ItemFocusNecromancy;
import flaxbeard.thaumicexploration.misc.TXPotion;
import flaxbeard.thaumicexploration.misc.TXTaintPotion;
import flaxbeard.thaumicexploration.misc.WorldGenTX;
import flaxbeard.thaumicexploration.packet.TXPacketHandler;
import flaxbeard.thaumicexploration.research.ModRecipes;
import flaxbeard.thaumicexploration.research.ModResearch;
import flaxbeard.thaumicexploration.tile.TileEntityBoundChest;
import flaxbeard.thaumicexploration.tile.TileEntityBoundJar;
import flaxbeard.thaumicexploration.tile.TileEntityCrucibleSouls;
import flaxbeard.thaumicexploration.tile.TileEntityEverfullUrn;
import flaxbeard.thaumicexploration.tile.TileEntityReplicator;
import flaxbeard.thaumicexploration.tile.TileEntityThinkTank;
import flaxbeard.thaumicexploration.wand.WandRodAmberOnUpdate;
import flaxbeard.thaumicexploration.wand.WandRodTransmutationOnUpdate;


@Mod(modid = "ThaumicExploration", name = "Thaumic Exploration", version = "0.4.0", dependencies="required-after:Thaumcraft;after:ThaumicTinkerer")
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels={"tExploration"}, packetHandler = TXPacketHandler.class)
public class ThaumicExploration {
	
    @Instance("ThaumicExploration")
    public static ThaumicExploration instance;

    public static ArrayList<MutablePair<Integer, Integer>> allowedItems = new ArrayList<MutablePair<Integer, Integer>>();
	public static Item pureZombieBrain;
	public static int pureZombieBrainID;
	public static Item blankSeal;
	public static int blankSealID;
	public static Item chestSeal;
	public static int chestSealID;
	public static Item chestSealLinked;
	public static int chestSealLinkedID;
	public static Item jarSeal;
	public static int jarSealID;
	public static Item jarSealLinked;
	public static int jarSealLinkedID;
	public static Item transmutationCore;
	public static int transmutationCoreID;
	public static Item amberCore;
	public static int amberCoreID;
	
	public static EnumArmorMaterial armorMaterialCrystal;
	public static Item maskEvil;
	public static int maskEvilID;
	public static Item focusNecromancy;
	public static int focusNecromancyID;
	public static Item bootsMeteor;
	public static int bootsMeteorID;
	public static Item bootsComet;
	public static int bootsCometID;
	public static Item charmNoTaint;
	public static int charmNoTaintID;
	public static Item charmTaint;
	public static int charmTaintID;
	public static Item talismanFood;
	public static int talismanFoodID;
	
	public static Item taintBerry;
	public static int taintBerryID;
	
	public static Block boundChest;
	public static int boundChestID;
	public static Block boundJar;
	public static int boundJarID;
	public static Block thinkTankJar;
	public static int thinkTankJarID;
	public static Block everfullUrn;
	public static int everfullUrnID;
	public static Block crucibleSouls;
	
	public static int crucibleSoulsID;
	public static Block taintBerryCrop;
	public static int taintBerryCropID;
	public static Block meltyIce;
	public static int meltyIceID;
	public static Block replicator;
	public static int replicatorID;
	public static Block skullCandle;
	public static int skullCandleID;
	public static WandRod WAND_ROD_CRYSTAL;
	public static WandRod WAND_ROD_AMBER;
	
	public WorldGenTX worldGen;
	
	public static int everfullUrnRenderID;
	public static int crucibleSoulsRenderID;
	public static int replicatorRenderID;
	public static int candleSkullRenderID;
	
	public static CreativeTabs tab;
	
	public static boolean allowBoundInventories;
	public static boolean allowReplication;
	public static boolean allowMagicPlankReplication;
	public static boolean allowModWoodReplication;
	public static boolean allowModStoneReplication;
	public static boolean allowCrucSouls;
	public static boolean allowThinkTank;
	public static boolean allowFood;
	public static boolean allowUrn;
	public static boolean allowBoots;
	
	public static boolean allowOsmotic;
	
	public static boolean prefix;
	
	public static boolean brainsGolem;
	public static boolean taintBloom;
	
	public static int potionBindingID;
	public static int potionTaintWithdrawlID;
	
	public static Enchantment enchantmentBinding;
	public static Enchantment enchantmentNightVision;
	public static Enchantment enchantmentDisarm;
	
	public static int enchantmentBindingID;
	public static int enchantmentNightVisionID;
	public static int enchantmentDisarmID;
	
	
	public static Potion potionBinding;
	public static Potion potionTaintWithdrawl;
	
	@SidedProxy(clientSide = "flaxbeard.thaumicexploration.client.ClientProxy", serverSide = "flaxbeard.thaumicexploration.common.CommonProxy")
	public static CommonProxy proxy;


	private TXBootsEventHandler entityEventHandler;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
	    GameRegistry.registerWorldGenerator(this.worldGen = new WorldGenTX());
	    
	    this.worldGen.initialize();
		
		Potion[] potionTypes = null;

		for (Field f : Potion.class.getDeclaredFields()) {
			f.setAccessible(true);
			try {
			if (f.getName().equals("potionTypes") || f.getName().equals("field_76425_a")) {
			Field modfield = Field.class.getDeclaredField("modifiers");
			modfield.setAccessible(true);
			modfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);
	
			potionTypes = (Potion[])f.get(null);
			final Potion[] newPotionTypes = new Potion[256];
			System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
			f.set(null, newPotionTypes);
			}
			}
			catch (Exception e) {
			}
		}
		
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		
		
		//Item IDs
		pureZombieBrainID = config.getItem("Cured Zombie Brain", 11000).getInt();
		blankSealID = config.getItem("Blank Tallow Seal", 11001).getInt();
		chestSealID = config.getItem("Chest Binding Seal", 11002).getInt();
		chestSealLinkedID = config.getItem("Linked Chest Binding Seal", 1100).getInt();
		jarSealID = config.getItem("Jar Binding Seal", 11006).getInt();
		jarSealLinkedID = config.getItem("Linked Jar Binding Seal", 11007).getInt();
		transmutationCoreID = config.getItem("Transmutation Filter Wand Core", 11004).getInt();
		amberCoreID = config.getItem("Amber Wand Core", 11005).getInt();
		maskEvilID = config.getItem("Mask of Cruelty", 11008).getInt();
		bootsMeteorID = config.getItem("Boots of the Meteor", 11010).getInt();
		bootsCometID = config.getItem("Boots of the Comet", 11011).getInt();
		charmNoTaintID = config.getItem("Wispy Dreamcatcher", 11012).getInt();
		charmTaintID = config.getItem("Tainted Band", 11014).getInt();
		taintBerryID = config.getItem("Taintberry", 11015).getInt();
		talismanFoodID = config.getItem("Talisman of Nourishment", 11013).getInt();
		focusNecromancyID = config.getItem("Focus of Necromancy", 11009).getInt();
		
		//Block IDs
		boundChestID = config.getBlock("Bound Chest", 700).getInt();
		boundJarID = config.getBlock("Bound Jar", 701).getInt();
		everfullUrnID = config.getBlock("Everfull Urn", 702).getInt();
		thinkTankJarID = config.getBlock("Think Tank", 703).getInt();
		crucibleSoulsID = config.getBlock("Crucible of Souls", 704).getInt();
		meltyIceID = config.getBlock("Fast-Melting Ice", 705).getInt();
		skullCandleID = config.getBlock("Skull Candle", 707).getInt();
		replicatorID = config.getBlock("Thaumic Replicator", 706).getInt();
		taintBerryCropID = config.getBlock("Taintberry Crop Block", 708).getInt();

		potionTaintWithdrawlID = config.get("Potion", "Taint Withdrawl", 32).getInt();
		potionBindingID = config.get("Potion", "Binding", 31).getInt();

		enchantmentBindingID = config.get("Enchantment", "Binding", 77).getInt();
		enchantmentNightVisionID = config.get("Enchantment", "Night Vision", 78).getInt();
		enchantmentDisarmID = config.get("Enchantment", "Disarming", 79).getInt();

		//allowOsmotic = config.get("Miscellaneous", "Add new enchantments to Thaumic Tinkerer's Osmotic Enchanter (Requires TT Build 72+)", true).getBoolean(true);
		prefix = config.get("Miscellaneous", "Display [TX] prefix before Thaumic Exploration research", true).getBoolean(true);
		brainsGolem = config.get("Miscellaneous", "Use Purified Brains in advanced golems", true).getBoolean(true);
		taintBloom = config.get("Miscellaneous", "Move the Etheral Bloom to the Tainturgy tab", true).getBoolean(true);
		allowBoundInventories = config.get("Miscellaneous", "Allow bound inventories", true).getBoolean(true);
		allowReplication = config.get("Miscellaneous", "Allow Thaumic Replicator", true).getBoolean(true);
		allowCrucSouls = config.get("Miscellaneous", "Allow Crucible of Souls", true).getBoolean(true);
		allowThinkTank = config.get("Miscellaneous", "Allow Think Tank", true).getBoolean(true);
		allowFood = config.get("Miscellaneous", "Allow Talisman of Nourishment", true).getBoolean(true);
		allowUrn = config.get("Miscellaneous", "Allow Everfull Urn", true).getBoolean(true);
		allowBoots = config.get("Miscellaneous", "Allow Boots of the Meteor/Comet", true).getBoolean(true);
		allowMagicPlankReplication = config.get("Replicator", "Allow replication of Greatwood/Silverwood planks", true).getBoolean(true);
		allowModWoodReplication = config.get("Replicator", "Allow replication of other mods' logs and planks", true).getBoolean(true);
		allowModStoneReplication = config.get("Replicator", "Allow replication of other mods' stone blocks", true).getBoolean(true);
		
		config.save();
	}
	
	

	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		TickRegistry.registerTickHandler(new TXTickHandler(), Side.CLIENT);
	    this.entityEventHandler = new TXBootsEventHandler();
	    
	    MinecraftForge.EVENT_BUS.register(this.entityEventHandler);
		NetworkRegistry.instance().registerGuiHandler(instance, new TXGuiHandler());
		
		everfullUrnRenderID = RenderingRegistry.getNextAvailableRenderId();
		crucibleSoulsRenderID = RenderingRegistry.getNextAvailableRenderId();
		replicatorRenderID = RenderingRegistry.getNextAvailableRenderId();
		candleSkullRenderID = RenderingRegistry.getNextAvailableRenderId();
		
		//Creative Tab
		tab = new TXTab(CreativeTabs.getNextID(), "thaumicExploration");
		
		//EventHandler
		MinecraftForge.EVENT_BUS.register(new TXEventHandler());

		//Tiles
		GameRegistry.registerTileEntity(TileEntityBoundChest.class, "tileEntityBoundChest");
		GameRegistry.registerTileEntity(TileEntityBoundJar.class, "tileEntityBoundJar");
		GameRegistry.registerTileEntity(TileEntityThinkTank.class, "tileEntityThinkTank");
		GameRegistry.registerTileEntity(TileEntityEverfullUrn.class, "tileEntityEverfullUrn");
		GameRegistry.registerTileEntity(TileEntityCrucibleSouls.class, "tileEntityCrucibleSouls");
		GameRegistry.registerTileEntity(TileEntityReplicator.class, "tileEntityReplicator");
		
		//Blocks
		thinkTankJar = new BlockThinkTank(thinkTankJarID, false).setUnlocalizedName("thaumicexploration:thinkTankJar").setCreativeTab(tab).setTextureName("thaumicExploration:blankTexture");
		everfullUrn = new BlockEverfullUrn(everfullUrnID).setHardness(2.0F).setUnlocalizedName("thaumicexploration:everfullUrn").setCreativeTab(tab).setTextureName("thaumicExploration:everfullUrn");
		crucibleSouls = new BlockCrucibleSouls(crucibleSoulsID).setHardness(2.0F).setUnlocalizedName("thaumicexploration:crucibleSouls").setCreativeTab(tab).setTextureName("thaumicExploration:crucible3");
		replicator = new BlockReplicator(replicatorID).setHardness(4.0F).setUnlocalizedName("thaumicexploration:replicator").setCreativeTab(tab).setTextureName("thaumicexploration:replicatorBottom");
		
		meltyIce = new BlockBootsIce(meltyIceID).setUnlocalizedName("thaumicexploration:meltyIce").setHardness(0.5F).setLightOpacity(3).setStepSound(Block.soundGlassFootstep).setUnlocalizedName("ice").setTextureName("ice");
		//skullCandle = new BlockSkullCandle(skullCandleID).setUnlocalizedName("thaumicexploration:skullCandle");
		
		taintBerryCrop = new BlockTaintBerries(taintBerryCropID).setUnlocalizedName("thaumicexploration:taintBerryCrop").setTextureName("thaumicExploration:berries");
	
		boundChest = new BlockBoundChest(boundChestID, 0).setHardness(2.5F).setStepSound(new StepSound("wood", 1.0F, 1.0F)).setUnlocalizedName("boundChest");
		boundJar = new BlockBoundJar(boundJarID).setUnlocalizedName("boundJar");
		
		GameRegistry.registerBlock(boundChest, "boundChest");
		GameRegistry.registerBlock(taintBerryCrop, "taintBerryCrop");
		//GameRegistry.registerBlock(skullCandle, "skullCandle");
		GameRegistry.registerBlock(meltyIce, "meltyIce");
		GameRegistry.registerBlock(boundJar, "boundJar");
		GameRegistry.registerBlock(thinkTankJar, "thinkTankJar");
		GameRegistry.registerBlock(everfullUrn, "everfullUrn");
		GameRegistry.registerBlock(crucibleSouls, "crucibleSouls");
		GameRegistry.registerBlock(replicator, "replicator");
		
		//Items
		transmutationCore = (new Item(transmutationCoreID)).setUnlocalizedName("thaumicexploration:transmutationCore").setCreativeTab(tab).setTextureName("thaumicexploration:rodTransmutation");
		talismanFood = (new ItemFoodTalisman(talismanFoodID)).setUnlocalizedName("thaumicexploration:talismanFood").setCreativeTab(tab).setTextureName("thaumicexploration:talismanFood");
		amberCore = (new Item(amberCoreID)).setUnlocalizedName("thaumicexploration:amberCore").setCreativeTab(tab).setTextureName("thaumicexploration:rodAmber");
		pureZombieBrain = (new ItemBrain(pureZombieBrainID)).setUnlocalizedName("thaumicexploration:pureZombieBrain").setCreativeTab(tab).setTextureName("thaumicexploration:pureZombieBrain");
		blankSeal = (new ItemBlankSeal(blankSealID).setCreativeTab(tab).setTextureName("thaumicexploration:sealBlank"));
		chestSeal = (new ItemChestSeal(chestSealID).setCreativeTab(tab).setTextureName("thaumicexploration:sealChest").setUnlocalizedName("thaumicexploration:chestSeal"));
		chestSealLinked = (new ItemChestSealLinked(chestSealLinkedID).setTextureName("thaumicexploration:sealChest").setUnlocalizedName("thaumicexploration:chestSeal"));
		jarSeal = (new ItemChestSeal(jarSealID).setCreativeTab(tab).setTextureName("thaumicexploration:sealJar").setUnlocalizedName("thaumicexploration:jarSeal"));
		jarSealLinked = (new ItemChestSealLinked(jarSealLinkedID).setTextureName("thaumicexploration:sealJar").setUnlocalizedName("thaumicexploration:jarSeal"));
		
		charmNoTaint = (new Item(charmNoTaintID)).setUnlocalizedName("thaumicexploration:dreamcatcher").setCreativeTab(tab).setTextureName("thaumicexploration:dreamcatcher");
		charmTaint = (new Item(charmTaintID)).setUnlocalizedName("thaumicexploration:ringTaint").setCreativeTab(tab).setTextureName("thaumicexploration:taintRing");
		maskEvil = (new ItemTXArmorSpecialDiscount(maskEvilID, ThaumcraftApi.armorMatSpecial, 2, 0)).setUnlocalizedName("thaumicexploration:maskEvil").setCreativeTab(tab).setTextureName("thaumicexploration:maskEvil");
		bootsMeteor = (new ItemTXArmorSpecial(bootsMeteorID, ThaumcraftApi.armorMatSpecial, 4, 3)).setUnlocalizedName("thaumicexploration:bootsMeteor").setCreativeTab(tab).setTextureName("thaumicexploration:bootsMeteor");
		bootsComet = (new ItemTXArmorSpecial(bootsCometID, ThaumcraftApi.armorMatSpecial, 4, 3)).setUnlocalizedName("thaumicexploration:bootsComet").setCreativeTab(tab).setTextureName("thaumicexploration:bootsComet");
		focusNecromancy = (new ItemFocusNecromancy(focusNecromancyID)).setUnlocalizedName("thaumicexploration:necromancy").setCreativeTab(tab).setTextureName("thaumicexploration:focusNecromancy");
		taintBerry = (new ItemTaintSeedFood(taintBerryID, 1, 0.3F, Block.tnt.blockID, ConfigBlocks.blockTaint.blockID)).setCreativeTab(tab).setUnlocalizedName("thaumicexploration:taintBerry").setTextureName("thaumicExploration:taintBerry");
		//Item skull = (new ItemSkullCandle(11016)).setUnlocalizedName("skull").setTextureName("skull");
		
		//Wands
		WAND_ROD_AMBER = new WandRod("amber",10,new ItemStack(ThaumicExploration.amberCore),8,new WandRodAmberOnUpdate(), new ResourceLocation("thaumicexploration:textures/models/rodAmber.png"));
		WAND_ROD_CRYSTAL = new WandRod("transmutation",100,new ItemStack(ThaumicExploration.transmutationCore),1,new WandRodTransmutationOnUpdate());
		//WandRod.rods.put("transmutation1", WAND_ROD_CRYSTAL1);
		enchantmentBinding = new EnchantmentBinding(enchantmentBindingID, 1);
		enchantmentNightVision = new EnchantmentNightVision(enchantmentNightVisionID, 1);
		enchantmentDisarm = new EnchantmentDisarm(enchantmentDisarmID, 1);
		if (Loader.isModLoaded("ThaumicTinkerer")) {
			TTIntegration.registerEnchants();
		}

		potionBinding = (new TXPotion(potionBindingID, false, 0)).setIconIndex(0, 0).setPotionName("potion.binding");
		potionTaintWithdrawl = (new TXTaintPotion(potionTaintWithdrawlID, true, 0)).setPotionName("potion.taintWithdrawl");
		
		
		proxy.registerRenderers();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		//Researches, Thaumcraft Recipes
		ModRecipes.initRecipes();
		ModResearch.initResearch();
		proxy.setUnicode();

		allowedItems.add(MutablePair.of(Block.stone.blockID,0));
		String[] ores = OreDictionary.getOreNames();
	    for (String ore : ores) {
	    	if (ore != null) {
	    		if (ore.equals("logWood")) {
	    			for (ItemStack is : OreDictionary.getOres(ore)) {
	    				AspectList ot = ThaumcraftCraftingManager.getObjectTags(is);
	    		        ot = ThaumcraftCraftingManager.getBonusTags(is, ot);
	    				if (!(is.itemID == ConfigBlocks.blockMagicalLog.blockID) && ot.getAspects().length > 0)
	    					allowedItems.add(MutablePair.of(is.itemID,is.getItemDamage()));
	    	        }
	    		}
	    		if (ore.equals("treeLeaves")) {
	    			for (ItemStack is : OreDictionary.getOres(ore)) {
	    				AspectList ot = ThaumcraftCraftingManager.getObjectTags(is);
	    		        ot = ThaumcraftCraftingManager.getBonusTags(is, ot);
	    				if (!(is.itemID == ConfigBlocks.blockMagicalLeaves.blockID) && ot.getAspects().length > 0)
	    					allowedItems.add(MutablePair.of(is.itemID,is.getItemDamage()));
	    	        }
	    		}
	    		if (allowModWoodReplication) {
		    		if (allowMagicPlankReplication) {
			    		if (ore.equals("plankWood")) {
			    			for (ItemStack is : OreDictionary.getOres(ore)) {
			    				AspectList ot = ThaumcraftCraftingManager.getObjectTags(is);
			    		        ot = ThaumcraftCraftingManager.getBonusTags(is, ot);
			    		        if (ot.getAspects().length > 0)
			    		        	allowedItems.add(MutablePair.of(is.itemID,is.getItemDamage()));
			    	        }
			    		}
		    		}
		    		else
		    		{
			    		if (ore.equals("plankWood")) {
			    			for (ItemStack is : OreDictionary.getOres(ore)) {
			    				AspectList ot = ThaumcraftCraftingManager.getObjectTags(is);
			    		        ot = ThaumcraftCraftingManager.getBonusTags(is, ot);
			    		        if (!(is.itemID == ConfigBlocks.blockWoodenDevice.blockID) && ot.getAspects().length > 0)
			    		        	allowedItems.add(MutablePair.of(is.itemID,is.getItemDamage()));
			    	        }
			    		}
		    		}
		    		
		    		if (ore.equals("slabWood")) {
		    			for (ItemStack is : OreDictionary.getOres(ore)) {
		    				AspectList ot = ThaumcraftCraftingManager.getObjectTags(is);
		    		        ot = ThaumcraftCraftingManager.getBonusTags(is, ot);
		    		        if (ot.getAspects().length > 0)
		    		        	allowedItems.add(MutablePair.of(is.itemID,is.getItemDamage()));
		    	        }
		    		}
		    		if (ore.equals("stairWood")) {
		    			for (ItemStack is : OreDictionary.getOres(ore)) {
		    				AspectList ot = ThaumcraftCraftingManager.getObjectTags(is);
		    		        ot = ThaumcraftCraftingManager.getBonusTags(is, ot);
		    		        if (ot.getAspects().length > 0)
		    		        	allowedItems.add(MutablePair.of(is.itemID,is.getItemDamage()));
		    	        }
		    		}
	    		}
	    		else
	    		{
	    			allowedItems.add(MutablePair.of(Block.woodSingleSlab.blockID,OreDictionary.WILDCARD_VALUE));
	    			allowedItems.add(MutablePair.of(Block.stairsWoodBirch.blockID,OreDictionary.WILDCARD_VALUE));
	    			allowedItems.add(MutablePair.of(Block.stairsWoodOak.blockID,OreDictionary.WILDCARD_VALUE));
	    			allowedItems.add(MutablePair.of(Block.stairsWoodJungle.blockID,OreDictionary.WILDCARD_VALUE));
	    			allowedItems.add(MutablePair.of(Block.stairsWoodSpruce.blockID,OreDictionary.WILDCARD_VALUE));
	    			allowedItems.add(MutablePair.of(Block.wood.blockID,OreDictionary.WILDCARD_VALUE));
	    			allowedItems.add(MutablePair.of(Block.planks.blockID,OreDictionary.WILDCARD_VALUE));
	    			if (allowMagicPlankReplication) {
	    				allowedItems.add(MutablePair.of(ConfigBlocks.blockWoodenDevice.blockID,6));
	    				allowedItems.add(MutablePair.of(ConfigBlocks.blockWoodenDevice.blockID,7));
	    			}	
	    		}
	    		if (allowModStoneReplication) {
		    		if (ore.equals("stone")) {
		    			for (ItemStack is : OreDictionary.getOres(ore)) {
		    				AspectList ot = ThaumcraftCraftingManager.getObjectTags(is);
		    		        ot = ThaumcraftCraftingManager.getBonusTags(is, ot);
		    		        if (ot.getAspects().length > 0)
		    		        	allowedItems.add(MutablePair.of(is.itemID,is.getItemDamage()));
		    	        }
		    		}
		    		if (ore.equals("cobblestone")) {
		    			for (ItemStack is : OreDictionary.getOres(ore)) {
		    				AspectList ot = ThaumcraftCraftingManager.getObjectTags(is);
		    		        ot = ThaumcraftCraftingManager.getBonusTags(is, ot);
		    		        if (ot.getAspects().length > 0)
		    		        	allowedItems.add(MutablePair.of(is.itemID,is.getItemDamage()));
		    	        }
		    		}
	    		}
	    		else
	    		{
	    			allowedItems.add(MutablePair.of(Block.stone.blockID, OreDictionary.WILDCARD_VALUE));
	    			allowedItems.add(MutablePair.of(Block.cobblestone.blockID, OreDictionary.WILDCARD_VALUE));
	    		}
	    		allowedItems.add(MutablePair.of(Block.cobblestoneMossy.blockID, OreDictionary.WILDCARD_VALUE));
	    		allowedItems.add(MutablePair.of(Block.stoneSingleSlab.blockID,0));
	    		allowedItems.add(MutablePair.of(Block.stoneSingleSlab.blockID,3));
	    		allowedItems.add(MutablePair.of(Block.stairsCobblestone.blockID, OreDictionary.WILDCARD_VALUE));
	    		
	    		//All sandstone, stairs, slab
	    		allowedItems.add(MutablePair.of(Block.sand.blockID,OreDictionary.WILDCARD_VALUE));
	    		allowedItems.add(MutablePair.of(Block.sandStone.blockID,OreDictionary.WILDCARD_VALUE));
	    		allowedItems.add(MutablePair.of(Block.stairsSandStone.blockID,OreDictionary.WILDCARD_VALUE));
	    		allowedItems.add(MutablePair.of(Block.stoneSingleSlab.blockID,1));
	    		
	    		//All stone bricks, stairs, slab
	    		allowedItems.add(MutablePair.of(Block.stoneBrick.blockID,OreDictionary.WILDCARD_VALUE));
	    		allowedItems.add(MutablePair.of(Block.stairsStoneBrick.blockID,OreDictionary.WILDCARD_VALUE));
	    		allowedItems.add(MutablePair.of(Block.stoneSingleSlab.blockID,5));
	    		
	    		//Bricks, stairs, slab
	    		allowedItems.add(MutablePair.of(Block.brick.blockID,OreDictionary.WILDCARD_VALUE));
	    		allowedItems.add(MutablePair.of(Block.stairsBrick.blockID,OreDictionary.WILDCARD_VALUE));
	    		allowedItems.add(MutablePair.of(Block.stoneSingleSlab.blockID,4));
	    		
	    		//All quartz, stairs, slab
	    		//allowedItems.add(MutablePair.of(Block.blockNetherQuartz.blockID,OreDictionary.WILDCARD_VALUE));
	    		//allowedItems.add(MutablePair.of(Block.stairsNetherQuartz.blockID,OreDictionary.WILDCARD_VALUE));
	    		//allowedItems.add(MutablePair.of(Block.stoneSingleSlab.blockID,7));
	    		
	    		//Netherbrick, stairs, slab
	    		allowedItems.add(MutablePair.of(Block.netherBrick.blockID,OreDictionary.WILDCARD_VALUE));
	    		allowedItems.add(MutablePair.of(Block.stairsNetherBrick.blockID,OreDictionary.WILDCARD_VALUE));
	    		allowedItems.add(MutablePair.of(Block.stoneSingleSlab.blockID,6));
	    		
	    		allowedItems.add(MutablePair.of(Block.slowSand.blockID,OreDictionary.WILDCARD_VALUE));
	    		allowedItems.add(MutablePair.of(Block.gravel.blockID,OreDictionary.WILDCARD_VALUE));
	    		allowedItems.add(MutablePair.of(Block.glass.blockID,OreDictionary.WILDCARD_VALUE));
	    		allowedItems.add(MutablePair.of(Block.grass.blockID,OreDictionary.WILDCARD_VALUE));
	    		allowedItems.add(MutablePair.of(Block.dirt.blockID,OreDictionary.WILDCARD_VALUE));
	    		allowedItems.add(MutablePair.of(Block.blockSnow.blockID,OreDictionary.WILDCARD_VALUE));
	    		allowedItems.add(MutablePair.of(Block.blockClay.blockID,OreDictionary.WILDCARD_VALUE));
	    	}
	    }
	}
	


	public void addRecipes()
	{
		
	}
	
	public void registerEntity(Class<? extends Entity> entityClass, String entityName, int bkEggColor, int fgEggColor) {
		int id = EntityRegistry.findGlobalUniqueEntityId();

		EntityRegistry.registerGlobalEntityID(entityClass, entityName, id);
		EntityList.entityEggs.put(Integer.valueOf(id), new EntityEggInfo(id, bkEggColor, fgEggColor));
	}
	
	public void addSpawn(Class<? extends EntityLiving> entityClass, int spawnProb, int min, int max, BiomeGenBase[] biomes) {
		if (spawnProb > 0) {
			EntityRegistry.addSpawn(entityClass, spawnProb, min, max, EnumCreatureType.monster, biomes);
		}
	}

	private void addAchievementName(String ach, String name)
	{
	        LanguageRegistry.instance().addStringLocalization("achievement." + ach, "en_US", name);
	}

	private void addAchievementDesc(String ach, String desc)
	{
	        LanguageRegistry.instance().addStringLocalization("achievement." + ach + ".desc", "en_US", desc);
	}


	
	@ForgeSubscribe
	public void onSound(SoundLoadEvent event) {
	// You add them the same way as you add blocks.
	//event.manager.addSound("steamcraft:wobble.ogg");
	}
	
	private class TXTab extends CreativeTabs {

		public TXTab(int par1, String par2Str) {
			super(par1, par2Str);
			// TODO Auto-generated constructor stub
		}
		
        public ItemStack getIconItemStack() {
            return new ItemStack(ThaumicExploration.thinkTankJar, 1, 0);
        }	
		
	}
}