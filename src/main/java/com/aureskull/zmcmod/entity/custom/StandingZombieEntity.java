package com.aureskull.zmcmod.entity.custom;

import com.aureskull.zmcmod.block.entity.SmallZombieWindowBlockEntity;
import com.aureskull.zmcmod.entity.goal.AttackWindowGoal;
import com.aureskull.zmcmod.entity.goal.CrawlThroughWindowGoal;
import com.aureskull.zmcmod.entity.goal.MoveToBlockGoal;
import com.aureskull.zmcmod.sound.ModSounds;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StandingZombieEntity extends HostileEntity {
    //TODO faire en sorte que l'on ne puisse pas pousser les zombies, ils doivent pouvoir nous bloquer
    public final AnimationState walkingAnimationSate = new AnimationState();
    private int walkingAnimationTimeout = 0;

    private BlockPos targetBlockPos = null;

    private boolean passedThroughWindow = false;

    public StandingZombieEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    private void setupAnimationStates(){
        if(this.walkingAnimationTimeout <= 0){
            this.walkingAnimationTimeout = this.random.nextInt(40) + 80;
            this.walkingAnimationSate.start(this.age);
        }else{
            --this.walkingAnimationTimeout;
        }
    }

    @Override
    protected void updateLimbs(float posDelta){
        float f = this.getPose() == EntityPose.STANDING ? Math.min(posDelta * 6.0f, 1.0f) : 0.0f;
        this.limbAnimator.updateLimbs(f, 0.2f);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getWorld().isClient()){
            setupAnimationStates();
        }else{
            if (this.targetBlockPos != null && this.getWorld().getBlockEntity(this.targetBlockPos) instanceof SmallZombieWindowBlockEntity window) {
                // Check if the window still has planks left
                double d0 = this.targetBlockPos.getX() + 0.5 - this.getX();
                double d2 = this.targetBlockPos.getZ() + 0.5 - this.getZ();
                this.setYaw(-((float) Math.atan2(d0, d2)) * (180F / (float) Math.PI));
                this.bodyYaw = this.getYaw();
                this.headYaw = this.bodyYaw;

                // Optionally, add a method call here to simulate the plank removal process if close enough
                // For example, check distance and reduce plank count periodically (not every tick to simulate effort)
                    /*if (this.squaredDistanceTo(Vec3d.ofCenter(this.targetBlockPos)) < 2.0 * 2.0) { // Example distance check
                        window.removePlank(); // Method in your SmallZombieWindowBlockEntity to decrease plank count
                    }*/
            }
        }
    }


    @Override
    protected void initGoals(){
        this.goalSelector.add(0, new SwimGoal(this));

        if(!passedThroughWindow)
            this.goalSelector.add(1, new CrawlThroughWindowGoal(this, 0.5D));

        if(!passedThroughWindow)
            this.goalSelector.add(2, new AttackWindowGoal(this));

        if(this.targetBlockPos != null)
            this.goalSelector.add(3, new MoveToBlockGoal(this, this.targetBlockPos, 1.0D));
        //this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        //this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class ,4f));
        //this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        if (targetBlockPos != null) {
            nbt.put("standing_zombie.target_position", NbtHelper.fromBlockPos(targetBlockPos));
        }
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("standing_zombie.target_position")) {
            this.targetBlockPos = NbtHelper.toBlockPos(nbt.getCompound("standing_zombie.target_position"));
        }
    }

    public static DefaultAttributeContainer.Builder createStandingZombieAttributes(){
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 999f);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.STANDING_ZOMBIE_AMB;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.STANDING_ZOMBIE_DEATH;
    }

    private BlockPos getWindowBlockTarget(){
        //BlockPos targetPos = window.getPos().offset(facing.getOpposite());
        return null;
    }

    public BlockPos getTargetBlockPos() {
        return targetBlockPos;
    }

    public void setTargetBlockPos(BlockPos targetBlockPos) {
        this.targetBlockPos = targetBlockPos;

        if (!this.getWorld().isClient) {
            // Remove any existing MoveToBlockGoal and re-add it to ensure it uses the updated target position
            this.goalSelector.getGoals().stream()
                    .filter(goal -> goal.getGoal() instanceof MoveToBlockGoal)
                    .findFirst()
                    .ifPresent(goal -> this.goalSelector.remove(goal.getGoal()));

            // Now add the goal with the new target
            this.goalSelector.add(3, new MoveToBlockGoal(this, this.targetBlockPos, 1.0D));
        }
    }

    public void setPassedThroughWindow(Boolean passedThroughWindow) {
        this.passedThroughWindow = passedThroughWindow;
    }

    public Boolean isPassedThroughWindow() {
        return passedThroughWindow;
    }
}
