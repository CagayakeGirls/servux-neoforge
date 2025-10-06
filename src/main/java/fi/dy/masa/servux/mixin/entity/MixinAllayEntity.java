package fi.dy.masa.servux.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fi.dy.masa.servux.dataproviders.EntitiesDataProvider;

@Mixin(AllayEntity.class)
public abstract class MixinAllayEntity
{
//	@Shadow public abstract Brain<AllayEntity> getBrain();

	@WrapOperation(method = "canGather",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/neoforged/neoforge/event/EventHooks;canEntityGrief(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;)Z"))
	private boolean servux$fixAllayGathering1(ServerWorld serverWorld, Entity entity, Operation<Boolean> original)
	{
		if (EntitiesDataProvider.INSTANCE.hasFixAllayGathering())
		{
			return true;
		}

		return original.call(serverWorld, entity);
	}

//	@Inject(method = "isItemPickupCoolingDown",
//	        at = @At("RETURN"), cancellable = true)
//	private void servux$fixAllayGathering2(CallbackInfoReturnable<Boolean> cir)
//	{
//		if (EntitiesDataProvider.INSTANCE.hasFixAllayGathering() &&
//			cir.getReturnValue())
//		{
//			cir.setReturnValue(false);
//		}
//	}
}
