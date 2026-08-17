package fi.dy.masa.servux.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRuleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import fi.dy.masa.servux.dataproviders.EntitiesDataProvider;

@Mixin(Allay.class)
public abstract class MixinAllay extends PathfinderMob
{
	protected MixinAllay(EntityType<? extends PathfinderMob> type, Level level)
	{
		super(type, level);
	}

	@SuppressWarnings("unchecked")
	@WrapOperation(method = "wantsToPickUp",
	               at = @At(value = "INVOKE",
	                   target = "Lnet/neoforged/neoforge/event/EventHooks;canEntityGrief(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;)Z"))
	private <T> boolean servux$fixAllayGathering1(ServerLevel level, Entity entity, Operation<Boolean> original)
	{
		if (EntitiesDataProvider.INSTANCE.hasFixAllayGathering()) //&&
			//gameRule.gameRuleType() == GameRuleType.BOOL)        // Ensure BOOL type
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
