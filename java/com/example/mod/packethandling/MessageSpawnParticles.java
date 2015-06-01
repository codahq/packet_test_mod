package com.example.mod.packethandling;

import io.netty.buffer.ByteBuf;

import java.util.Random;

import com.example.mod.PacketFailureMod.BlockPacketTest;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSpawnParticles implements IMessage
{
	private ParticleType particleType;
	private double x, y, z;

	private int numParticles;

	public MessageSpawnParticles()
	{

	}

	public MessageSpawnParticles(ParticleType particleType, double x, double y, double z, int numParticles)
	{
		this.particleType = particleType;
		this.x = x;
		this.y = y;
		this.z = z;
		this.numParticles = numParticles;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		particleType = ParticleType.values()[buf.readInt()];
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		numParticles = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(particleType.getValue());
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeInt(numParticles);
	}

	public static class Handler implements IMessageHandler<MessageSpawnParticles, IMessage>
	{
		@Override
		public IMessage onMessage(MessageSpawnParticles message, MessageContext ctx)
		{
			BlockPacketTest.spawnParticles((int)message.x, (int)message.y, (int)message.z);
			return null;
		}
	}

	public enum ParticleType
	{
		HEALAREA(0), HEALSINGLE(1), HEART(2), CRIT(3), PORTAL(4), DEGENAREA(5);
		private final int value;

		private ParticleType(int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return value;
		}
	}

}
