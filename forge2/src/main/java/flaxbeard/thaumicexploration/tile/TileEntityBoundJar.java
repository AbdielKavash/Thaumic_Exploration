package flaxbeard.thaumicexploration.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.tiles.TileJarFillable;
import flaxbeard.thaumicexploration.data.BoundChestWorldData;
import flaxbeard.thaumicexploration.data.BoundJarWorldData;

public class TileEntityBoundJar extends TileJarFillable {
	public BoundJarWorldData myJarData;
    public int accessTicks = 0;
    public int id = 0;
    public int clientColor = 0;
    
    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound access = new NBTTagCompound();
        access.setInteger("accessTicks", this.accessTicks);
        access.setInteger("amount", this.amount);
        if (this.aspect != null) {
        	access.setString("aspect", this.aspect.getTag());
        }
        access.setInteger("color", this.getSealColor());
        
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, access);
    }
    
    public void setColor(int color) {
    	if (this.id > 0) {
    		myJarData = BoundJarWorldData.get(this.worldObj, "jar" + id, 0);
        }
        if (myJarData != null) {
        	myJarData.setSealColor(color);
        }
    }

    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
    {
    	NBTTagCompound access = pkt.data;
    	this.accessTicks = access.getInteger("accessTicks");
    	this.amount = access.getInteger("amount");
    	if (access.getString(("aspect")) != null) {
    		this.aspect = Aspect.getAspect(access.getString("aspect"));
    	}
    	this.setColor(access.getInteger("color"));
    	this.clientColor = access.getInteger("color");
    	
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }
	
	public int getSealColor() {
		if (myJarData == null) {
			myJarData = BoundJarWorldData.get(this.worldObj, "jar" + id, 0);
		}
		return this.myJarData.getSealColor();
	}

	@Override
	public int addToContainer(Aspect tt, int am) {
		this.updateEntity();
		if (myJarData == null) {
			myJarData = BoundJarWorldData.get(this.worldObj, "jar" + id, 0);
		}
	    if (am == 0) {
	        return am;
	    }
		if (((this.amount < this.maxAmount) && (tt == this.aspect)) || (this.amount == 0))
	    {
	      this.aspect = tt;
	      int added = Math.min(am, this.maxAmount - this.amount);
	      this.amount += added;
	      am -= added;
	      if (!this.worldObj.isRemote) {
	    	  myJarData.updateJarContents(tt, this.amount);
	      }
	    }
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		return am;
	}
	
	@Override
	public boolean takeFromContainer(Aspect tt, int am)
	{
		this.updateEntity();
		if ((this.amount >= am) && (tt == this.aspect))
		{
			this.amount -= am;
			if (this.amount <= 0)
			{
				this.aspect = null;
				this.amount = 0;
			}
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			if (!this.worldObj.isRemote) {
				myJarData.updateJarContents(tt, this.amount);
			}
			return true;
	    }
	    return false;
	}
	
    public void updateEntity()
    {
    	if (!this.worldObj.isRemote) {
	    	if (myJarData == null) {
				myJarData = BoundJarWorldData.get(this.worldObj, "jar" + id, 0);
			}
	    	
	    	//System.out.println(this.amount);
			if (this.amount != myJarData.getJarAmount()) {
				this.amount = myJarData.getJarAmount();
			}
			if (this.aspect != myJarData.getJarAspect()) {
				this.aspect = myJarData.getJarAspect();
			}
	
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    	}
    	super.updateEntity();
    }
	
	
}
