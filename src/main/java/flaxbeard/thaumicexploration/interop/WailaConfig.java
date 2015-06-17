package flaxbeard.thaumicexploration.interop;

import flaxbeard.thaumicexploration.block.BlockBoundJar;
import mcp.mobius.waila.api.IWailaRegistrar;

/**
 * Created by Katrina on 17/06/2015.
 */
public class WailaConfig     {


    public static void callbackRegister(IWailaRegistrar registrar)
    {
        registrar.registerBodyProvider(new BoundJarProvider(), BlockBoundJar.class);
    }
}
