package io.github.haykam821.anvildrop.game.map;

import java.util.Iterator;

import io.github.haykam821.anvildrop.game.AnvilDropConfig;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import net.minecraft.world.level.chunk.ChunkGenerator;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.plasmid.api.game.world.generator.TemplateChunkGenerator;

public class AnvilDropMap {
	private static final BlockState CLEAR_STATE = Blocks.AIR.defaultBlockState();
	private static final BlockState ANVIL_STATE = Blocks.ANVIL.defaultBlockState();
	private static final BlockState ALTERNATE_ANVIL_STATE = Blocks.ANVIL.defaultBlockState().setValue(AnvilBlock.FACING, Direction.EAST);

	private final MapTemplate template;
	private final AnvilDropConfig config;
	private final BlockBounds platformBounds;
	private final AABB box;
	private final BlockBounds clearBounds;
	private final BlockBounds dropBounds;

	public AnvilDropMap(MapTemplate template, AnvilDropConfig config, BlockBounds platformBounds, BlockBounds clearBounds, BlockBounds dropBounds) {
		this.template = template;
		this.config = config;

		this.platformBounds = platformBounds;
		this.box = this.platformBounds.asBox().inflate(-1, -0.5, -1);

		this.clearBounds = clearBounds;
		this.dropBounds = dropBounds;
	}

	public BlockBounds getPlatformBounds() {
		return this.platformBounds;
	}

	public AABB getBox() {
		return this.box;
	}

	public void clearAnvils(ServerLevel world) {
        for (BlockPos pos : this.clearBounds) {
            if (this.config.isBreaking() && !world.isEmptyBlock(pos)) {
                world.destroyBlock(pos.atY(0), false);
            }
            world.setBlockAndUpdate(pos, CLEAR_STATE);
        }
	}

	public void dropAnvils(ServerLevel world) {
        for (BlockPos pos : this.dropBounds) {
            if (world.getRandom().nextDouble() < this.config.getChance()) {
                BlockState state = world.getRandom().nextBoolean() ? ANVIL_STATE : ALTERNATE_ANVIL_STATE;
                world.setBlock(pos, state, 0);
            }
        }
	}

	public ChunkGenerator createGenerator(MinecraftServer server) {
		return new TemplateChunkGenerator(server, this.template);
	}
}