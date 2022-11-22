package net.earthcomputer.advancedseanavigationtechniques;

import net.earthcomputer.advancedseanavigationtechniques.mixin.LivingEntityAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.registry.SculkSensorFrequencyRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class AdvancedSeaNavigationTechniques implements ModInitializer {
    public static final String MOD_ID = "advancedseanavigationtechniques";

    public static final Item SYMPATHY_POWDER = new Item(new Item.Settings().group(ItemGroup.TRANSPORTATION));

    public static final GameEvent SYMPATHY_POWDER_EVENT = new GameEvent(MOD_ID + ":sympathy_powder", 16);

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "sympathy_powder"), SYMPATHY_POWDER);

        Registry.register(Registry.GAME_EVENT, new Identifier(MOD_ID, "sympathy_powder"), SYMPATHY_POWDER_EVENT);

        SculkSensorFrequencyRegistry.register(SYMPATHY_POWDER_EVENT, 8);

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);

            if (!world.isClient && hand == Hand.MAIN_HAND && stack.getItem() instanceof ToolItem && player.getOffHandStack().getItem() == SYMPATHY_POWDER) {
                boolean result = handleSympathy(player, player.getOffHandStack(), stack, (ServerWorld) world);
                return result ? TypedActionResult.success(stack) : TypedActionResult.pass(stack);
            }



            return TypedActionResult.pass(stack);
        });
    }

    private static boolean handleSympathy(PlayerEntity player, ItemStack sympathy, ItemStack tool, ServerWorld world) {
        NbtCompound nbt = tool.getNbt();
        if (nbt == null || !nbt.containsUuid("SympathyEntity")) {
            return false;
        }
        if (!(world.getEntity(nbt.getUuid("SympathyEntity")) instanceof LivingEntity sympathyEntity)) {
            return false;
        }
        sympathyEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100));
        ((LivingEntityAccessor) sympathyEntity).callPlayHurtSound(DamageSource.player(player));
        world.emitGameEvent(sympathyEntity, SYMPATHY_POWDER_EVENT, sympathyEntity.getPos());
        if (!player.getAbilities().creativeMode) {
            sympathy.decrement(1);
        }
        return true;
    }
}
