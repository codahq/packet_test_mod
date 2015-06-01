package com.example.mod;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.example.mod.packethandling.MessageSpawnParticles;
import com.example.mod.packethandling.MessageSpawnParticles.ParticleType;

@Mod(modid = PacketFailureMod.MODID, name = PacketFailureMod.MODID, version = PacketFailureMod.VERSION)
public class PacketFailureMod
{
	public static final String MODID = "packet_test_mod";
	public static final String VERSION = "1.0";

	public static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	public static BlockPacketTest b = new BlockPacketTest(Material.cactus);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		b.register();
		PacketFailureMod.network.registerMessage(MessageSpawnParticles.Handler.class, MessageSpawnParticles.class, 0, Side.CLIENT);
	}

	public static class BlockPacketTest extends Block
	{
		public final static String uname = "packet_test_block";

		public BlockPacketTest(Material materialIn)
		{
			super(materialIn);
			this.setCreativeTab(CreativeTabs.tabDecorations);
			this.setUnlocalizedName(uname);
		}

		public void register()
		{
			GameRegistry.registerBlock(b, uname);
		}

		@Override
		public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
		{
			/*
			 * After time, usually less than a minute, this will crash.
			 */
			if (world.isRemote)
			{
				return true;
			}
			else
			{
				System.out.println("Activated on world.isRemote: " + world.isRemote);
				double range = 75.0;
				TargetPoint target = new TargetPoint(world.provider.getDimensionId(), pos.getX(), pos.getY(), pos.getZ(), range);
				network.sendToAllAround(new MessageSpawnParticles(ParticleType.HEART, pos.getX(), pos.getY(), pos.getZ(), 1), target);
				return false;
			}

			/*
			 * Conversely, this will never crash the client even though it is the exact same code
			 */
			/*
			spawnParticles(pos.getX(), pos.getY(), pos.getZ());
			return true;
			*/
		}

		public static void spawnParticles(int startX, int startY, int startZ)
		{
			Random rand = new Random();
			int numParticles = 20000;
			int range = 64;
			for (int i = 0; i < numParticles * 4; i++)
			{
				int x = (int)(rand.nextInt(range) - (range / 2) + startX);
				int y = (int)(rand.nextInt(range) - (range / 2) + startY);
				int z = (int)(rand.nextInt(range) - (range / 2) + startZ);

				for (int pi = 0; pi < 8; ++pi)
				{
					World world = Minecraft.getMinecraft().theWorld;
					EnumParticleTypes type = rand.nextInt(2) == 1 ? EnumParticleTypes.CRIT : EnumParticleTypes.VILLAGER_HAPPY;
					world.spawnParticle(type, x, (double)y + rand.nextDouble() * 2.0D, z, rand.nextGaussian() / 4.0D, rand.nextGaussian() / 4.0D, rand.nextGaussian() / 4.0D);
				}
			}
		}
	}
}
