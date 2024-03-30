package com.aureskull.zmcmod.entity.custom;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.block.entity.SmallZombieWindowBlockEntity;
import com.aureskull.zmcmod.entity.goal.*;
import com.aureskull.zmcmod.entity.goal.ZombieAttackGoal;
import com.aureskull.zmcmod.sound.ModSounds;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
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

    //NBT pas sauvegardé pour cette variable => Au redémarrage du serveur tous les zombies déjà présents voudront repasser au travers de leur porte même s'ils sont déjà passé.
    private boolean passedThroughWindow = false;

    private BlockPos mapControllerBlockPos;

    public boolean asJoinedPlayer = false;

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
        }
    }


    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));

        this.goalSelector.add(1, new MoveToBlockGoal(this, this.targetBlockPos, 1.0D));
        this.goalSelector.add(1, new AttackWindowGoal(this));
        //this.goalSelector.add(1, new LookAtBlockPosBlockEntityGoal(this, this.targetBlockPos));
        this.goalSelector.add(3, new CrawlThroughWindowGoal(this, 0.5D));

        //Faire en sorte que le zombie se déplace vers les coordonnées du joueur le plus proche tant que le joueur n'est pas en vu.
        this.goalSelector.add(3, new MoveToNearestPlayerGoal(this, 2.0D));
        this.goalSelector.add(3, new ZombieAttackGoal(this, 1.7, false)); // Consider adjusting the speed as per your requirement
        //this.targetSelector.add(4, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, false, false, (entity) -> true));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 256.0F));
    }

    private void initializeDependentTargetBlockGoals() {
        if (!this.goalSelector.getRunningGoals().anyMatch(goal -> goal.getGoal() instanceof LookAtBlockPosBlockEntityGoal)) {
            this.goalSelector.add(1, new LookAtBlockPosBlockEntityGoal(this, this.targetBlockPos));
        }

        if (!this.goalSelector.getRunningGoals().anyMatch(goal -> goal.getGoal() instanceof MoveToBlockGoal)) {
            this.goalSelector.add(3, new MoveToBlockGoal(this, this.targetBlockPos, 1.0D));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        if (targetBlockPos != null)
            nbt.put("standing_zombie.target_position", NbtHelper.fromBlockPos(targetBlockPos));

        if(mapControllerBlockPos != null)
            nbt.put("standing_zombie.map_controller_position", NbtHelper.fromBlockPos(targetBlockPos));



        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains("standing_zombie.target_position"))
            this.targetBlockPos = NbtHelper.toBlockPos(nbt.getCompound("standing_zombie.target_position"));

        if(nbt.contains("standing_zombie.map_controller_position"))
            this.mapControllerBlockPos = NbtHelper.toBlockPos(nbt.getCompound("standing_zombie.map_controller_position"));
    }

    public static DefaultAttributeContainer.Builder createStandingZombieAttributes(){
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 256.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 999f);
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
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

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);

        if (!this.getWorld().isClient) {
            sendZombieHasBeenKilledToMapController();
        }
    }

    private void sendZombieHasBeenKilledToMapController(){
        if(mapControllerBlockPos != null
                && getWorld().getBlockEntity(this.mapControllerBlockPos) instanceof MapControllerBlockEntity mapControllerBE
                && mapControllerBE.isStarted()){
            mapControllerBE.setKilledZombiesInRound(mapControllerBE.getKilledZombiesInRound() + 1);
        }
    }


    @Override
    public int getXpToDrop() {
        return 0;
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

            this.goalSelector.getGoals().stream()
                    .filter(goal -> goal.getGoal() instanceof LookAtBlockPosBlockEntityGoal)
                    .findFirst()
                    .ifPresent(goal -> this.goalSelector.remove(goal.getGoal()));

            // Now add the goals
            initializeDependentTargetBlockGoals();
        }
    }

    public void setPassedThroughWindow(Boolean passedThroughWindow) {
        this.passedThroughWindow = passedThroughWindow;
    }

    public Boolean isPassedThroughWindow() {
        return passedThroughWindow;
    }

    public void setMapControllerBlockPos(BlockPos mapControllerBlockPos) {
        this.mapControllerBlockPos = mapControllerBlockPos;
    }
}
