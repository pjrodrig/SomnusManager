package com.yahoo.prosfis.somnusmanager.customentities;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.server.v1_8_R2.Block;
import net.minecraft.server.v1_8_R2.BlockAnvil;
import net.minecraft.server.v1_8_R2.BlockPosition;
import net.minecraft.server.v1_8_R2.Blocks;
import net.minecraft.server.v1_8_R2.CrashReportSystemDetails;
import net.minecraft.server.v1_8_R2.DamageSource;
import net.minecraft.server.v1_8_R2.Entity;
import net.minecraft.server.v1_8_R2.EntityFallingBlock;
import net.minecraft.server.v1_8_R2.IBlockData;
import net.minecraft.server.v1_8_R2.Material;
import net.minecraft.server.v1_8_R2.MathHelper;
import net.minecraft.server.v1_8_R2.MinecraftKey;
import net.minecraft.server.v1_8_R2.NBTTagCompound;
import net.minecraft.server.v1_8_R2.World;

import org.bukkit.craftbukkit.v1_8_R2.event.CraftEventFactory;

import com.google.common.collect.Lists;

// CraftBukkit

public class FakeFallingBlock extends EntityFallingBlock {

	private IBlockData block;
	public int ticksLived;
	public boolean dropItem = true;
	@SuppressWarnings("unused")
	private boolean e;
	private boolean hurtEntities;
	private int fallHurtMax = 40, maxTicks;
	private float fallHurtAmount = 2.0F;
	public NBTTagCompound tileEntityData;

	public FakeFallingBlock(World world) {
		super(world);
	}

	public FakeFallingBlock(World world, double d0, double d1, double d2, IBlockData iblockdata, int maxTicks) {
		super(world);
		this.block = iblockdata;
		this.k = true;
		this.setSize(0.98F, 0.98F);
		this.setPosition(d0, d1, d2);
		this.motX = 0.0D;
		this.motY = 0.0D;
		this.motZ = 0.0D;
		this.lastX = d0;
		this.lastY = d1;
		this.lastZ = d2;
		this.maxTicks = maxTicks;
	}

	protected boolean s_() {
		return false;
	}

	protected void h() {
	}

	public boolean ad() {
		return !this.dead;
	}

	public void t_() {
		Block block = this.block.getBlock();

		if (block.getMaterial() == Material.AIR) {
			this.die();
		} else {
			this.lastX = this.locX;
			this.lastY = this.locY;
			this.lastZ = this.locZ;
			BlockPosition blockposition;

			if (this.ticksLived++ == 0) {
				if (!this.world.isClientSide) {
					this.die();
					return;
				}
			}

			this.motY -= 0.03999999910593033D; //0.04359999910593033D;
			this.move(this.motX, this.motY, this.motZ);
			this.motX *= 0.9800000190734863D; // 0.9760999990734863D;
			this.motY *= 0.9800000190734863D; // 0.9760999990734863D;
			this.motZ *= 0.9800000190734863D; // 0.9760999990734863D;
			if (!this.world.isClientSide) {
				blockposition = new BlockPosition(this);
				if (this.onGround) {
					this.motX *= 0.699999988079071D;
					this.motZ *= 0.699999988079071D;
					this.motY *= -0.5D;
					if (this.world.getType(blockposition).getBlock() != Blocks.PISTON_EXTENSION) {
						this.die();
					}
				} else if (this.ticksLived > maxTicks && !this.world.isClientSide && (blockposition.getY() < 1 || blockposition.getY() > 256) || this.ticksLived > 600) {
					this.die();
				}
			}

		}
	}

	@SuppressWarnings("rawtypes")
	public void e(float f, float f1) {
		Block block = this.block.getBlock();

		if (this.hurtEntities) {
			int i = MathHelper.f(f - 1.0F);

			if (i > 0) {
				ArrayList arraylist = Lists.newArrayList(this.world.getEntities(this, this.getBoundingBox()));
				boolean flag = block == Blocks.ANVIL;
				DamageSource damagesource = flag ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;
				Iterator iterator = arraylist.iterator();
				while (iterator.hasNext()) {
					Entity entity = (Entity) iterator.next();

					CraftEventFactory.entityDamage = this; // CraftBukkit
					entity.damageEntity(damagesource, (float) Math.min(MathHelper.d((float) i * this.fallHurtAmount), this.fallHurtMax));
					CraftEventFactory.entityDamage = null; // CraftBukkit
				}

				if (flag && (double) this.random.nextFloat() < 0.05000000074505806D + (double) i * 0.05D) {
					int j = ((Integer) this.block.get(BlockAnvil.DAMAGE)).intValue();

					++j;
					if (j > 2) {
						this.e = true;
					} else {
						this.block = this.block.set(BlockAnvil.DAMAGE, Integer.valueOf(j));
					}
				}
			}
		}

	}

	protected void b(NBTTagCompound nbttagcompound) {
		Block block = this.block != null ? this.block.getBlock() : Blocks.AIR;
		MinecraftKey minecraftkey = (MinecraftKey) Block.REGISTRY.c(block);

		nbttagcompound.setString("Block", minecraftkey == null ? "" : minecraftkey.toString());
		nbttagcompound.setByte("Data", (byte) block.toLegacyData(this.block));
		nbttagcompound.setByte("Time", (byte) this.ticksLived);
		nbttagcompound.setBoolean("DropItem", this.dropItem);
		nbttagcompound.setBoolean("HurtEntities", this.hurtEntities);
		nbttagcompound.setFloat("FallHurtAmount", this.fallHurtAmount);
		nbttagcompound.setInt("FallHurtMax", this.fallHurtMax);
		if (this.tileEntityData != null) {
			nbttagcompound.set("TileEntityData", this.tileEntityData);
		}

	}

	protected void a(NBTTagCompound nbttagcompound) {
		int i = nbttagcompound.getByte("Data") & 255;

		if (nbttagcompound.hasKeyOfType("Block", 8)) {
			this.block = Block.getByName(nbttagcompound.getString("Block")).fromLegacyData(i);
		} else if (nbttagcompound.hasKeyOfType("TileID", 99)) {
			this.block = Block.getById(nbttagcompound.getInt("TileID")).fromLegacyData(i);
		} else {
			this.block = Block.getById(nbttagcompound.getByte("Tile") & 255).fromLegacyData(i);
		}

		this.ticksLived = nbttagcompound.getByte("Time") & 255;
		Block block = this.block.getBlock();

		if (nbttagcompound.hasKeyOfType("HurtEntities", 99)) {
			this.hurtEntities = nbttagcompound.getBoolean("HurtEntities");
			this.fallHurtAmount = nbttagcompound.getFloat("FallHurtAmount");
			this.fallHurtMax = nbttagcompound.getInt("FallHurtMax");
		} else if (block == Blocks.ANVIL) {
			this.hurtEntities = true;
		}

		if (nbttagcompound.hasKeyOfType("DropItem", 99)) {
			this.dropItem = nbttagcompound.getBoolean("DropItem");
		}

		if (nbttagcompound.hasKeyOfType("TileEntityData", 10)) {
			this.tileEntityData = nbttagcompound.getCompound("TileEntityData");
		}

		if (block == null || block.getMaterial() == Material.AIR) {
			this.block = Blocks.SAND.getBlockData();
		}

	}

	public void a(boolean flag) {
		this.hurtEntities = flag;
	}

	public void appendEntityCrashDetails(CrashReportSystemDetails crashreportsystemdetails) {
		super.appendEntityCrashDetails(crashreportsystemdetails);
		if (this.block != null) {
			Block block = this.block.getBlock();

			crashreportsystemdetails.a("Immitating block ID", (Object) Integer.valueOf(Block.getId(block)));
			crashreportsystemdetails.a("Immitating block data", (Object) Integer.valueOf(block.toLegacyData(this.block)));
		}

	}

	public IBlockData getBlock() {
		return this.block;
	}
}
