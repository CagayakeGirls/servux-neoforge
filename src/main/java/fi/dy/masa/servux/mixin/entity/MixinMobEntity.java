package fi.dy.masa.servux.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fi.dy.masa.servux.dataproviders.EntitiesDataProvider;

@Mixin(MobEntity.class)
public abstract class MixinMobEntity
{
	@Redirect(method = "tickMovement",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/neoforged/neoforge/event/EventHooks;canEntityGrief(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;)Z"))
	private static boolean servux$fixAllayGathering(World world, Entity entity)
	{
		if (EntitiesDataProvider.INSTANCE.hasFixAllayGathering() &&
			entity != null && entity.getType() == EntityType.ALLAY)
		{
			return true;
		}

		return net.neoforged.neoforge.event.EventHooks.canEntityGrief(world, entity);
	}
}
