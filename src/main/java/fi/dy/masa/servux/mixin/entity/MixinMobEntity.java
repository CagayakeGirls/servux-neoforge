package fi.dy.masa.servux.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.servux.dataproviders.EntitiesDataProvider;

@Mixin(MobEntity.class)
public abstract class MixinMobEntity
{
	@Unique boolean isAllay = false;

	@Inject(method = "tickMovement", at = @At("HEAD"))
	private void servux$fixAllayGathering3(CallbackInfo ci)
	{
		if (EntitiesDataProvider.INSTANCE.hasFixAllayGathering())
		{
			Entity entity = (Entity) (Object) this;

			if (entity.getType() == EntityType.ALLAY)
			{
				this.isAllay = true;
			}
		}
	}

	@WrapOperation(method = "tickMovement",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/neoforged/neoforge/event/EventHooks;canEntityGrief(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;)Z"))
	private boolean servux$fixAllayGathering4(World level, Entity entity, Operation<Boolean> original)
	{
		if (EntitiesDataProvider.INSTANCE.hasFixAllayGathering() &&
			this.isAllay)
		{
			return true;
		}

		this.isAllay = false;
		return original.call(level, entity);
	}
}
