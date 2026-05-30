package fi.dy.masa.servux.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.allay.Allay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import fi.dy.masa.servux.dataproviders.EntitiesDataProvider;

@Mixin(Allay.class)
public abstract class MixinAllayEntity
{
	@SuppressWarnings("unchecked")
	@WrapOperation(method = "wantsToPickUp",
	               at = @At(value = "INVOKE",
	                   target = "Lnet/neoforged/neoforge/event/EventHooks;canEntityGrief(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;)Z"))
	private boolean servux$fixAllayGathering1(ServerLevel level, Entity entity, Operation<Boolean> original)
	{
		if (EntitiesDataProvider.INSTANCE.hasFixAllayGathering()) //&&
//			gameRule.gameRuleType() == GameRuleType.BOOL)        // Ensure BOOL type
		{
			return true;
		}

		return original.call(level, entity);
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
