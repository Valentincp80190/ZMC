package com.aureskull.zmcmod.entity.custom;

import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.entity.goal.*;
import com.aureskull.zmcmod.sound.ModSounds;
import com.aureskull.zmcmod.util.PlayerData;
import com.aureskull.zmcmod.util.PlayerHelper;
import com.aureskull.zmcmod.util.StateSaverAndLoader;
import net.minecraft.entity.AnimationState;
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

    private BlockPos windowBlockPos = null;

    //NBT pas sauvegardé pour cette variable => Au redémarrage du serveur tous les zombies déjà présents voudront repasser au travers de leur porte même s'ils sont déjà passé.
    private boolean passedThroughWindow = false;

    private BlockPos mapControllerBlockPos;

    private final float RUN_THRESHOLD = 2.0f;

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


        this.goalSelector.add(3, new CrawlThroughWindowGoal(this));

        //Pour attaquer c'est juste un set sur le zombie. On pourrait donc se passer de ce goal si on fait un mix de l'attaque et du MoveToBlockGoal
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 256.0F));
    }

    private void initializeDependentTargetBlockGoals() {
        //When mapControllerBlockPos initialized
        if (!this.goalSelector.getRunningGoals().anyMatch(goal -> goal.getGoal() instanceof LookAtBlockPosBlockEntityGoal)) {
            this.goalSelector.add(3, new AttackNearestPlayerGoal(this, speed, false));
        }

        if (!this.goalSelector.getRunningGoals().anyMatch(goal -> goal.getGoal() instanceof LookAtBlockPosBlockEntityGoal)) {
            this.goalSelector.add(1, new LookAtBlockPosBlockEntityGoal(this, this.windowBlockPos));
        }

        if (!this.goalSelector.getRunningGoals().anyMatch(goal -> goal.getGoal() instanceof MoveToBlockGoal)) {
            this.goalSelector.add(3, new MoveToBlockGoal(this, this.windowBlockPos, speed));
        }

        if (!this.goalSelector.getRunningGoals().anyMatch(goal -> goal.getGoal() instanceof AttackWindowGoal)) {
            this.goalSelector.add(1, new AttackWindowGoal(this, this.windowBlockPos));
        }
    }

    public void updateSpeed(){
        if(getWorld().getBlockEntity(mapControllerBlockPos) instanceof MapControllerBlockEntity mapControllerBlockEntity){
            if(mapControllerBlockEntity.getRound() > 4){
                speed = 2.0f;
            }else speed = 1.0f;
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        if (windowBlockPos != null)
            nbt.put("standing_zombie.target_position", NbtHelper.fromBlockPos(windowBlockPos));

        if(mapControllerBlockPos != null)
            nbt.put("standing_zombie.map_controller_position", NbtHelper.fromBlockPos(mapControllerBlockPos));

        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains("standing_zombie.target_position"))
            windowBlockPos = NbtHelper.toBlockPos(nbt.getCompound("standing_zombie.target_position"));

        if(nbt.contains("standing_zombie.map_controller_position"))
            mapControllerBlockPos = NbtHelper.toBlockPos(nbt.getCompound("standing_zombie.map_controller_position"));

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
        if(speed >= RUN_THRESHOLD)
            return ModSounds.STANDING_ZOMBIE_SPRINT;
        else
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

        //Give money to the player
        if (source.getAttacker() instanceof PlayerEntity &&
                mapControllerBlockPos != null &&
                getWorld().getBlockEntity(mapControllerBlockPos) instanceof MapControllerBlockEntity mapControllerBlockEntity) {
            PlayerEntity killer = (PlayerEntity) source.getAttacker();

            if(PlayerHelper.isPlaying(killer, mapControllerBlockEntity)){
                PlayerData playerData = StateSaverAndLoader.getPlayerState(killer);
                if(playerData != null) playerData.addMoney(60);
            }
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

    public BlockPos getWindowBlockPos() {
        return windowBlockPos;
    }

    public void setWindowBlockPos(BlockPos windowBlockPos) {
        this.windowBlockPos = windowBlockPos;

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

    public BlockPos getMapControllerBlockPos() {
        return mapControllerBlockPos;
    }
}
