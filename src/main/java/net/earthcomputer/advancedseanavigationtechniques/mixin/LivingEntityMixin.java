package net.earthcomputer.advancedseanavigationtechniques.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onAttacking", at = @At("HEAD"))
    public void onAttacking(Entity target, CallbackInfo ci) {
        if (world.isClient) {
            return;
        }

        LivingEntity self = (LivingEntity) (Object) this;
        ItemStack weapon = self.getMainHandStack();

        if (!(weapon.getItem() instanceof ToolItem)) { // includes swords
            return;
        }

        NbtCompound nbt = weapon.getOrCreateNbt();
        nbt.putUuid("SympathyEntity", target.getUuid());
    }
}
