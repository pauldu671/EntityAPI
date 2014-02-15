package io.snw.entityapi.api.mind.behaviour.goals;

import io.snw.entityapi.api.ControllableEntity;
import io.snw.entityapi.api.mind.behaviour.BehaviourType;
import io.snw.entityapi.entity.selector.EntitySelectorNearestAttackableTarget;
import net.minecraft.server.v1_7_R1.DistanceComparator;
import net.minecraft.server.v1_7_R1.EntityLiving;
import net.minecraft.server.v1_7_R1.IEntitySelector;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;

import java.util.Collections;
import java.util.List;

/**
 * Moves the entity towards the nearest attackable target
 */

public class BehaviourMoveTowardsNearestAttackableTarget extends BehaviourTarget {

    private final Class classToTarget;
    private final int chance;
    private final DistanceComparator distComparator;
    private final IEntitySelector selector;
    private EntityLiving target;

    public BehaviourMoveTowardsNearestAttackableTarget(ControllableEntity controllableEntity, Class classToTarget, int chance, boolean checkSenses) {
        this(controllableEntity, classToTarget, chance, checkSenses, false);
    }

    public BehaviourMoveTowardsNearestAttackableTarget(ControllableEntity controllableEntity, Class classToTarget, int chance, boolean checkSenses, boolean useMelee) {
        this(controllableEntity, classToTarget, chance, checkSenses, useMelee, null);
    }

    public BehaviourMoveTowardsNearestAttackableTarget(ControllableEntity controllableEntity, Class classToTarget, int chance, boolean checkSenses, boolean useMelee, IEntitySelector selector) {
        super(controllableEntity, checkSenses, useMelee);
        this.classToTarget = classToTarget;
        this.chance = chance;
        this.distComparator = new DistanceComparator(this.handle);
        this.selector = new EntitySelectorNearestAttackableTarget(this, selector);
    }

    @Override
    public BehaviourType getType() {
        return BehaviourType.ONE;
    }

    @Override
    public String getDefaultKey() {
        return "Move Nearest Target";
    }

    @Override
    public boolean shouldStart() {
        if (this.chance > 0 && this.handle.aI().nextInt(this.chance) != 0) {
            return false;
        } else {
            double range = this.controllableEntity.getPathfindingRange();
            List list = this.handle.world.a(this.classToTarget, this.handle.boundingBox.grow(range, 4.0D, range), this.selector);

            Collections.sort(list, this.distComparator);
            if (list.isEmpty()) {
                return false;
            } else {
                this.target = (EntityLiving) list.get(0);
                return true;
            }
        }
    }

    @Override
    public void start() {
        this.controllableEntity.setTarget((CraftLivingEntity) this.target.getBukkitEntity());
        super.start();
    }

    @Override
    public void tick() {

    }
}