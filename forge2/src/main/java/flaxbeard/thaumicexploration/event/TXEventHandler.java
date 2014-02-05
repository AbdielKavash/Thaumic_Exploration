package flaxbeard.thaumicexploration.event;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITaskEntry;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.ITaintedMob;
import thaumcraft.common.lib.world.DamageSourceThaumcraft;
import thaumcraft.common.tiles.TileJarFillable;
import cpw.mods.fml.common.network.PacketDispatcher;
import flaxbeard.thaumicexploration.ThaumicExploration;
import flaxbeard.thaumicexploration.ai.EntityAICreeperDummy;
import flaxbeard.thaumicexploration.ai.EntityAINearestAttackablePureTarget;
import flaxbeard.thaumicexploration.data.TXWorldData;
import flaxbeard.thaumicexploration.entity.EntityLoveArrow;
import flaxbeard.thaumicexploration.tile.TileEntityBoundChest;
import flaxbeard.thaumicexploration.tile.TileEntityBoundJar;

public class TXEventHandler {
	public TXEventHandler() {
		//System.out.println("TEST123");
	}
	
	@ForgeSubscribe
	public void handleWorldLoad(WorldEvent.Load event) {
		TXWorldData.get(event.world);
	}
	

	@ForgeSubscribe
	public void handleTaintSpawns(EntityJoinWorldEvent event) {
		if (event.entity instanceof ITaintedMob) {
			EntityLiving mob = (EntityLiving) event.entity;
			List<EntityAITaskEntry> tasksToRemove = new ArrayList<EntityAITaskEntry>();
			for ( Object entry : mob.targetTasks.taskEntries)
			{
				EntityAITaskEntry entry2 = (EntityAITaskEntry)entry;
				if (entry2.action instanceof EntityAINearestAttackableTarget)
				{
					tasksToRemove.add((EntityAITaskEntry) entry);
				}
			}
			for (EntityAITaskEntry entry : tasksToRemove)
			{
				mob.targetTasks.removeTask(entry.action);
			}
			//System.out.println("brainwashed1");
			mob.targetTasks.addTask(1, new EntityAINearestAttackablePureTarget((EntityCreature) mob, EntityPlayer.class, 0, true)); 
		}
	}

	@ForgeSubscribe
	public void handleMobDrop(LivingDropsEvent event) {
		if (event.source == DamageSourceTX.soulCrucible) {
			event.setCanceled(true);
		}
	}
	
	@ForgeSubscribe
	public void handleTeleport(EnderTeleportEvent event) {
		if (event.entityLiving instanceof EntityEnderman || event.entityLiving instanceof EntityPlayer) {
			if (event.entityLiving.isPotionActive(ThaumicExploration.potionBinding)) {
				event.setCanceled(true);
			}
		}
	}
	
	
	@ForgeSubscribe
	public void stopCreeperExplosions(LivingUpdateEvent event) {
		if (event.entityLiving.getCurrentItemOrArmor(4) != null) {
			ItemStack heldItem = event.entityLiving.getCurrentItemOrArmor(4);
			int nightVision = EnchantmentHelper.getEnchantmentLevel(ThaumicExploration.enchantmentNightVision.effectId, heldItem);
			if(nightVision > 0 && (!event.entityLiving.isPotionActive(Potion.nightVision.id) || event.entityLiving.getActivePotionEffect(Potion.nightVision).duration < 202)) {
				event.entityLiving.addPotionEffect(new PotionEffect(Potion.nightVision.id, 202, 1));
			}
		}
		if (event.entityLiving instanceof EntityCreeper && event.entityLiving.isPotionActive(ThaumicExploration.potionBinding)) {
            EntityCreeper creeper = (EntityCreeper) event.entityLiving;
            int size = creeper.tasks.taskEntries.size();
			for(int i = 0; i<size; i++) {
				EntityAITaskEntry entry = (EntityAITaskEntry) creeper.tasks.taskEntries.get(i);
            	if(entry.action instanceof EntityAICreeperSwell) {
            		entry.action = new EntityAICreeperDummy(creeper);
            		creeper.tasks.taskEntries.set(i, entry);
            	}
            }
			//ReflectionHelper.setPrivateValue(EntityCreeper.class, (EntityCreeper) event.entityLiving, 2, LibObfuscation.TIME_SINCE_IGNITED);
		}
		else if (event.entityLiving instanceof EntityCreeper) {
			EntityCreeper creeper = (EntityCreeper) event.entityLiving;
            int size = creeper.tasks.taskEntries.size();
			for(int i = 0; i<size; i++) {
				EntityAITaskEntry entry = (EntityAITaskEntry) creeper.tasks.taskEntries.get(i);
            	if(entry.action instanceof EntityAICreeperDummy) {
            		entry.action = new EntityAICreeperSwell(creeper);
            		creeper.tasks.taskEntries.set(i, entry);
            		creeper.setCreeperState(1);
            	}
            	
            }
		}
	}
	
	@ForgeSubscribe
	public void handleArrow(ArrowLooseEvent event) {
		event.setCanceled(true);
		ItemStack par1ItemStack = event.bow;
		World par2World = event.entityPlayer.worldObj;
		EntityPlayer par3EntityPlayer = event.entityPlayer;
		Random itemRand = new Random();
		int j = event.charge;

        boolean flag = par3EntityPlayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, par1ItemStack) > 0;

        if (flag || par3EntityPlayer.inventory.hasItem(Item.arrow.itemID))
        {
            float f = (float)j / 20.0F;
            f = (f * f + f * 2.0F) / 3.0F;

            if ((double)f < 0.1D)
            {
                return;
            }

            if (f > 1.0F)
            {
                f = 1.0F;
            }

            EntityLoveArrow entityarrow = new EntityLoveArrow(par2World, par3EntityPlayer, f * 2.0F);

            if (f == 1.0F)
            {
                entityarrow.setIsCritical(true);
            }

            int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, par1ItemStack);

            if (k > 0)
            {
                entityarrow.setDamage(entityarrow.getDamage() + (double)k * 0.5D + 0.5D);
            }

            int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, par1ItemStack);

            if (l > 0)
            {
                entityarrow.setKnockbackStrength(l);
            }

            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, par1ItemStack) > 0)
            {
                entityarrow.setFire(100);
            }

            par1ItemStack.damageItem(1, par3EntityPlayer);
            par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

            if (flag)
            {
                entityarrow.canBePickedUp = 2;
            }
            else
            {
                par3EntityPlayer.inventory.consumeInventoryItem(Item.arrow.itemID);
            }

            if (!par2World.isRemote)
            {
                par2World.spawnEntityInWorld(entityarrow);
            }
        }
	}
	
//	@ForgeSubscribe
//	public void handleTaintSeeds(BlockEvent.HarvestDropsEvent event) {
//		if (event.drops.size() > 0 && !event.drops.contains(Item.itemsList[Block.tallGrass.blockID]) && event.block.blockID == Block.tallGrass.blockID && event.world.getBiomeGenForCoords(event.x, event.z) == ThaumcraftWorldGenerator.biomeTaint) {
//			event.drops.clear();
//			event.drops.add(new ItemStack(Item.arrow));
//		}
//	}
	
	@ForgeSubscribe
	public void handleEnchantmentAttack(LivingAttackEvent event) {
		if ((event.entityLiving instanceof EntityEnderman || event.entityLiving instanceof EntityCreeper || event.entityLiving instanceof EntityPlayer)&& event.source.getSourceOfDamage() instanceof EntityLivingBase) {
			EntityLivingBase attacker = (EntityLivingBase) event.source.getSourceOfDamage();
			ItemStack heldItem = attacker.getHeldItem();
			if(heldItem == null)
				return;

			int binding = EnchantmentHelper.getEnchantmentLevel(ThaumicExploration.enchantmentBinding.effectId, heldItem);
			if(binding > 1) {
				event.entityLiving.addPotionEffect(new PotionEffect(ThaumicExploration.potionBinding.id, 50, 1));
			}
		}
		if (event.source.getSourceOfDamage() instanceof EntityLivingBase) {
			EntityLivingBase attacker = (EntityLivingBase) event.source.getSourceOfDamage();
			ItemStack heldItem = attacker.getHeldItem();
			if(heldItem == null)
				return;

			int binding = EnchantmentHelper.getEnchantmentLevel(ThaumicExploration.enchantmentBinding.effectId, heldItem);
			if (binding > 0) {
				event.entityLiving.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 50, 1));
			}
		}
		if (event.source.getSourceOfDamage() instanceof EntityLivingBase) {
			EntityLivingBase attacker = (EntityLivingBase) event.source.getSourceOfDamage();
			ItemStack heldItem = attacker.getHeldItem();
			if(heldItem == null)
				return;

			int disarm = EnchantmentHelper.getEnchantmentLevel(ThaumicExploration.enchantmentDisarm.effectId, heldItem);
			if (disarm > 0 && !(event.entityLiving instanceof EntityPlayer)) {
				if (event.entityLiving.getHeldItem() != null && !event.entityLiving.worldObj.isRemote && event.entityLiving.worldObj.rand.nextInt(10-(2*disarm)) == 0) {
					ItemStack itemstack = event.entityLiving.getHeldItem();
					event.entityLiving.setCurrentItemOrArmor(0, null);
					World world = event.entityLiving.worldObj;
					double x = event.entityLiving.posX;
					double y = event.entityLiving.posY;
					double z = event.entityLiving.posZ;
					float f = world.rand.nextFloat() * 0.8F + 0.1F;
	                float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
	                float f2 = world.rand.nextFloat();
	                EntityItem entityitem;
			    	int k1 = world.rand.nextInt(21) + 10;
		
		            k1 = itemstack.stackSize;
		
		            entityitem = new EntityItem(world, (double)((float)x + f), (double)((float)y + f1), (double)((float)z + f2), new ItemStack(itemstack.itemID, k1, itemstack.getItemDamage()));
		            float f3 = 0.05F;
		            entityitem.motionX = (double)((float)world.rand.nextGaussian() * f3);
		            entityitem.motionY = (double)((float)world.rand.nextGaussian() * f3 + 0.2F);
		            entityitem.motionZ = (double)((float)world.rand.nextGaussian() * f3);
		
		            if (itemstack.hasTagCompound())
		            {
		                entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
		            }
		            world.spawnEntityInWorld(entityitem);
				}
			}
		}
	}
	
	@ForgeSubscribe
	public void handleTaint(LivingHurtEvent event) {
		if (event.entityLiving.worldObj.rand.nextInt(4) < 3) {
		if (event.source.damageType == "mob") {
			if (event.source.getSourceOfDamage() instanceof ITaintedMob) {
				if (event.entityLiving instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) event.entityLiving;
					
//					List<String> completed = Thaumcraft.proxy.getResearchManager().getResearchForPlayerSafe(player.username);
//				    if ((completed != null) && (completed.contains("CRUCIBLE")) && (completed.contains("NITOR")))
//				    {
//				    	
//				      completed.remove("CRUCIBLE");
//				      completed.remove("NITOR");
//				      completed = new ArrayList();
//				      completed.add("TAINTURGE");
//				      completed.add("TAINTURGE2");
//				      Thaumcraft.proxy.getCompletedResearch().put(player.username, completed);
//				    }
//				    //Thaumcraft.proxy.getResearchManager().updateResearchNBT(player);
//				    
//				    ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
//			        DataOutputStream outputStream = new DataOutputStream(bos);
//			        try
//			        {
//			            outputStream.writeByte(6);
//			            outputStream.writeInt(player.worldObj.provider.dimensionId);
//			            outputStream.writeInt(player.entityId);
//			           
//			        }
//			        catch (Exception ex)
//			        {
//			            ex.printStackTrace();
//			        }
//			
//			        Packet250CustomPayload packet = new Packet250CustomPayload();
//			        packet.channel = "tExploration";
//			        packet.data = bos.toByteArray();
//			        packet.length = bos.size();
//			        PacketDispatcher.sendPacketToPlayer(packet, (Player) player);
					
					for (int i = 0; i<10; i++) {
						//if (player.inventory.getStackInSlot(i) != null)
							
						if (player.inventory.getStackInSlot(i) != null && player.inventory.getStackInSlot(i).itemID == ThaumicExploration.charmNoTaint.itemID) {
							event.setCanceled(true);
							break;
						}
					}
				}
			}
		}
		if (event.source == DamageSourceThaumcraft.taint  || event.source == DamageSourceThaumcraft.tentacle  || event.source == DamageSourceThaumcraft.swarm ) {
			if (event.entityLiving instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) event.entityLiving;
				for (int i = 0; i<10; i++) {
					if (player.inventory.getStackInSlot(i) != null && player.inventory.getStackInSlot(i).itemID == ThaumicExploration.charmNoTaint.itemID) {
						event.setCanceled(true);
						break;
					}
				}
					
			}
		}
	}
	}
	
	@ForgeSubscribe
	public void handleItemUse(PlayerInteractEvent event) {
		byte type = 0;
		
		if (event.entityPlayer.worldObj.blockExists(event.x, event.y, event.z)) {
			//System.out.println(event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z) + " " + ThaumicExploration.boundChest.blockID);
			if (event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z) == Block.chest.blockID) {

				if (event.entityPlayer.inventory.getCurrentItem() != null){ 
					if (event.entityPlayer.inventory.getCurrentItem().itemID == ThaumicExploration.chestSeal.itemID) {
						type = 1;
					}
					else if (event.entityPlayer.inventory.getCurrentItem().itemID == ThaumicExploration.chestSealLinked.itemID) {
						type = 2;
					}
				}
			}
			else if (event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z) == ThaumicExploration.boundChest.blockID) {
				World world = event.entityPlayer.worldObj;
				//System.out.println(event.entityPlayer.worldObj.isRemote + ItemBlankSeal.itemNames[((TileEntityBoundChest) world.getBlockTileEntity(event.x, event.y, event.z)).getSealColor()]);
				if (event.entityPlayer.inventory.getCurrentItem() != null){ 
					if (event.entityPlayer.inventory.getCurrentItem().itemID == ThaumicExploration.chestSeal.itemID ) {
						int color = ((TileEntityBoundChest) world.getBlockTileEntity(event.x, event.y, event.z)).clientColor;		
						type = 3;
						if (15-(event.entityPlayer.inventory.getCurrentItem().getItemDamage()) == color) {
							int nextID = ((TileEntityBoundChest) world.getBlockTileEntity(event.x, event.y, event.z)).id;
							ItemStack linkedSeal = new ItemStack(ThaumicExploration.chestSealLinked.itemID, 1, event.entityPlayer.inventory.getCurrentItem().getItemDamage());
							NBTTagCompound tag = new NBTTagCompound();
							tag.setInteger("ID", nextID);
							tag.setInteger("x", event.x);
							tag.setInteger("y", event.y);
							tag.setInteger("z", event.z);
							tag.setInteger("dim", world.provider.dimensionId);
							linkedSeal.setTagCompound(tag);

							event.entityPlayer.inventory.addItemStackToInventory(linkedSeal);
							if (!event.entityPlayer.capabilities.isCreativeMode)
								event.entityPlayer.inventory.decrStackSize(event.entityPlayer.inventory.currentItem, 1);
						}
						event.setCanceled(true);
					}
				}
			}
		
		}


		if (event.entityPlayer.worldObj.blockExists(event.x, event.y, event.z)) {
			if (event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z) == ConfigBlocks.blockJar.blockID && event.entityPlayer.worldObj.getBlockMetadata(event.x, event.y, event.z) == 0) {
				if (event.entityPlayer.inventory.getCurrentItem() != null && ((TileJarFillable)event.entityPlayer.worldObj.getBlockTileEntity(event.x, event.y, event.z)).aspectFilter == null && ((TileJarFillable)event.entityPlayer.worldObj.getBlockTileEntity(event.x, event.y, event.z)).amount == 0){ 
					if (event.entityPlayer.inventory.getCurrentItem().itemID == ThaumicExploration.jarSeal.itemID) {
						type = 4;
					}
					else if (event.entityPlayer.inventory.getCurrentItem().itemID == ThaumicExploration.jarSealLinked.itemID) {
						type = 5;
					}
				}
			}
			else if (event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z) == ThaumicExploration.boundJar.blockID) {
				World world = event.entityPlayer.worldObj;
				if (event.entityPlayer.inventory.getCurrentItem() != null){ 
					if (event.entityPlayer.inventory.getCurrentItem().itemID == ThaumicExploration.jarSeal.itemID ) {
						int color = ((TileEntityBoundJar) world.getBlockTileEntity(event.x, event.y, event.z)).getSealColor();		
						type = 6;
						if (15-(event.entityPlayer.inventory.getCurrentItem().getItemDamage()) == color) {
							int nextID = ((TileEntityBoundJar) world.getBlockTileEntity(event.x, event.y, event.z)).id;
							ItemStack linkedSeal = new ItemStack(ThaumicExploration.jarSealLinked.itemID, 1, event.entityPlayer.inventory.getCurrentItem().getItemDamage());
							NBTTagCompound tag = new NBTTagCompound();
							tag.setInteger("ID", nextID);
							tag.setInteger("x", event.x);
							tag.setInteger("y", event.y);
							tag.setInteger("z", event.z);
							tag.setInteger("dim", world.provider.dimensionId);
							linkedSeal.setTagCompound(tag);

							event.entityPlayer.inventory.addItemStackToInventory(linkedSeal);
							if (!event.entityPlayer.capabilities.isCreativeMode)
								event.entityPlayer.inventory.decrStackSize(event.entityPlayer.inventory.currentItem, 1);
						}
						event.setCanceled(true);
					}
				}
			}
		}
		
		
		
		if (event.entityPlayer.worldObj.isRemote && type > 0) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
	        DataOutputStream outputStream = new DataOutputStream(bos);
	
	        try
	        {
	            outputStream.writeByte(1);
	            outputStream.writeInt(event.entityPlayer.worldObj.provider.dimensionId);
	            outputStream.writeInt(event.x);
	            outputStream.writeInt(event.y);
	            outputStream.writeInt(event.z);
	            outputStream.writeByte(type);
	            outputStream.writeInt( event.entityPlayer.entityId);
	           
	        }
	        catch (Exception ex)
	        {
	            ex.printStackTrace();
	        }
	
	        Packet250CustomPayload packet = new Packet250CustomPayload();
	        packet.channel = "tExploration";
	        packet.data = bos.toByteArray();
	        packet.length = bos.size();
	        //PacketDispatcher.sendPacketToServer(packet);
	        PacketDispatcher.sendPacketToServer(packet);
	        //System.out.println("sent");
		}

		
	}
	
}
