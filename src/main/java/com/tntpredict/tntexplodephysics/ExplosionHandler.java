package com.tntpredict.tntexplodephysics;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ExplosionEvent;

import java.util.List;

@EventBusSubscriber(modid = "tntexplodephysics", bus = EventBusSubscriber.Bus.GAME)
public class ExplosionHandler {

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        Level level = event.getLevel();

        // Only run logic on the server side to prevent ghost blocks
        if (level.isClientSide()) return;

        // Get the list of all blocks about to be destroyed by this explosion
        List<BlockPos> affectedBlocks = event.getAffectedBlocks();
        Vec3 explosionPos = event.getExplosion().center();

        for (BlockPos pos : affectedBlocks) {
            BlockState state = level.getBlockState(pos);

            // Skip air, water, lava, and unbreakable blocks (like bedrock)
            if (state.isAir() || state.is(Blocks.TNT) || !state.getFluidState().isEmpty() || state.getDestroySpeed(level, pos) < 0) {
                continue;
            }

            // 1. Spawn the falling block entity at the exact position
            FallingBlockEntity fallingBlock = FallingBlockEntity.fall(
                    level,
                    pos,
                    state
            );

            // 2. Calculate direction from explosion center to the block to push it away
            Vec3 blockPosVec = Vec3.atCenterOf(pos);
            Vec3 pushDirection = blockPosVec.subtract(explosionPos).normalize();

            // 3. Apply physics velocity (adjust the 0.5 multiplier to change explosion strength)
            double power = 0.5;
            fallingBlock.setDeltaMovement(
                    pushDirection.x * power,
                    pushDirection.y * power + 0.2, // Add upward lift
                    pushDirection.z * power
            );

            // 4. Force the game to sync this movement with clients immediately
            fallingBlock.hasImpulse = true;
        }

        // Optional: Clear the list so the explosion doesn't drop regular block items
        // affectedBlocks.clear();
    }
}
