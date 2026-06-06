package com.tntpredict.tntexplodephysics;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = "tntexplodephysics", bus = EventBusSubscriber.Bus.GAME)
public class FireHandler {

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        // Проверяем логику только на сервере
        if (!(event.getLevel() instanceof Level level) || level.isClientSide()) return;

        // Получаем блок, который только что поставили (например, огонь)
        BlockState placedState = event.getPlacedBlock();
        BlockPos firePos = event.getPos();

        // Проверяем, является ли поставленный блок огнем
        if (placedState.is(Blocks.FIRE)) {
            // Берем координаты блока НАД которым или НА котором загорелся огонь (обычно под огнем)
            BlockPos blockUnderPos = firePos.below();
            BlockState blockUnderState = level.getBlockState(blockUnderPos);

            // Игнорируем воздух, воду, бедрок и другие горючие блоки, которые не должны падать
            if (blockUnderState.isAir() || !blockUnderState.getFluidState().isEmpty() || blockUnderState.getDestroySpeed(level, blockUnderPos) < 0) {
                return;
            }


            // Спавним падающий блок из того, что подожгли
            FallingBlockEntity fallingBlock = FallingBlockEntity.fall(level, blockUnderPos, blockUnderState);




        }
    }
}
