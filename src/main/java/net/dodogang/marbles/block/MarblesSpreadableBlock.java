package net.dodogang.marbles.block;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;

import java.util.Random;
import java.util.function.Supplier;

public abstract class MarblesSpreadableBlock extends SnowyBlock {
    protected final Supplier<Block> hostBlockSupplier;
    protected Block hostBlock = null;

    protected MarblesSpreadableBlock(Supplier<Block> hostBlock, AbstractBlock.Settings settings) {
        super(settings);
        this.hostBlockSupplier = hostBlock;
    }

    private static boolean canSurvive(BlockState state, WorldView world, BlockPos pos) {
        BlockPos up = pos.up();
        BlockState upState = world.getBlockState(up);
        if (upState.isOf(Blocks.SNOW) && upState.get(SnowBlock.LAYERS) == 1) {
            return true;
        } else if (upState.getFluidState().getLevel() == 8) {
            return false;
        } else {
            int opacity = ChunkLightProvider.getRealisticOpacity(world, state, pos, upState, up, Direction.UP, upState.getOpacity(world, up));
            return opacity < world.getMaxLightLevel();
        }
    }

    private static boolean canSpread(BlockState state, WorldView world, BlockPos pos) {
        return canSurvive(state, world, pos) && !world.getFluidState(pos.up()).isIn(FluidTags.WATER);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        Block host = this.getHostBlock();
        if (!canSurvive(state, world, pos)) {
            world.setBlockState(pos, host.getDefaultState());
        } else {
            if (world.getLightLevel(pos.up()) >= 9) {
                BlockState defaultState = getDefaultState();

                for (int i = 0; i < 4; ++i) {
                    BlockPos near = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                    if (world.getBlockState(near).isOf(host) && canSpread(defaultState, world, near)) {
                        world.setBlockState(near, defaultState.with(SNOWY, world.getBlockState(near.up()).isOf(Blocks.SNOW)));
                    }
                }
            }
        }
    }

    protected Block getHostBlock() {
        return hostBlock == null ? hostBlockSupplier.get() : hostBlock;
    }
}
