package ru.nanit.mining.data;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.nanit.mining.services.DiggingService;
import ru.nanit.mining.utils.BlockHelper;
import ru.nanit.mining.utils.LocationHelper;

public class DigBlock {

    private static final double MAX_DIST = 6.0;
    private static final int ANIM_SIZE = 10;

    private Particle particle = Particle.CRIT;

    private Digger player;
    private Location particleLoc;
    private Block block;
    private BlockFace lastFace;

    private float hardness;
    private float damage = 0;
    private float lastDamage = 0;
    private long lastDamageTime;

    private float animStep;
    private int animation = -1;

    private boolean isRunning = false;
    private boolean isCrit = false;
    private boolean isBroken = false;

    private BukkitRunnable runnable;

    public DigBlock(Digger player){
        this.player = player;
        lastDamageTime = System.currentTimeMillis();
    }

    public Digger getPlayer(){
        return player;
    }

    private Particle getParticle(){
        return particle;
    }

    public Location getParticleLoc(){
        return this.particleLoc;
    }

    public Block getBlock(){
        return block;
    }

    public int getAnimation(){
        return animation;
    }

    public boolean isBroken(){
        return isBroken;
    }

    public boolean isCritEnabled(){
        return isCrit;
    }

    public DigResult damageBlock(){
        if(!BlockHelper.isUsableTool(player.getPlayer().getInventory().getItemInMainHand(), block.getType()) || hardness < 0){
            return DigResult.USELESS_TOOL;
        }

        if(isLookAt() && !isCrit){
            particle = Particle.CRIT_MAGIC;
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.BLOCK_STONE_BREAK, 1.0f, 1.3f);
            player.addTempPercents(DiggingService.CRIT_TEMP_BONUS);
            isCrit = true;
        }

        float digSpeedMult = BlockHelper.getDestroySpeed(player.getPlayer().getInventory().getItemInMainHand(), block.getType());
        float dmg = Digger.DEF_DIG_SPEED * (player.getDigSpeedPercent()/100) * digSpeedMult;

        lastDamageTime = System.currentTimeMillis();
        damage += dmg;
        lastDamage += dmg;

        if(lastDamage >= animStep){
            lastDamage = 0;
            animation++;
            playBreakAnim();
        }

        if(damage >= hardness){
            if(breakBlock()){
                return DigResult.BROKEN;
            }
            return DigResult.BREAK_CANCELLED;
        }

        return DigResult.PROCESS;
    }

    public boolean breakBlock(){
        if(BlockHelper.sendBreakBlock(player.getPlayer(), block)){
            isBroken = true;
            stop();
            return true;
        }

        stop();
        return false;
    }

    private void playBreakAnim(){
        BlockHelper.sendBreakAnimation(block, animation);
    }

    public void setBlockLoc(Block block, BlockFace face){
        if(block.equals(this.block) && lastFace.equals(face)){
            return;
        }

        this.block = block;
        this.lastFace = face;
        this.particleLoc = LocationHelper.getRandParticleLoc(block.getLocation(), face);
        this.hardness = BlockHelper.getBlockHardness(block);
        this.animStep = hardness / ANIM_SIZE;
        this.isCrit = false;
    }

    public boolean isLookAt(){
        if(!isRunning){
            return false;
        }

        double delta = Math.abs(MAX_DIST - player.getPlayer().getEyeLocation().distance(particleLoc));
        return isAround(getPlayerViewDir(), getNeededDir(), delta);
    }

    private Vector2D getPlayerViewDir(){
        return new Vector2D(wrapDegrees(player.getPlayer().getEyeLocation().getYaw()), wrapDegrees(player.getPlayer().getEyeLocation().getPitch()));
    }

    private Vector2D getNeededDir(){
        Location loc = player.getPlayer().getEyeLocation().clone();

        double dx = this.particleLoc.getX() - loc.getX();
        double dy = this.particleLoc.getY() - loc.getY();
        double dz = this.particleLoc.getZ() - loc.getZ();

        if (dx != 0) {
            loc.setYaw((dx < 0) ? (float)(1.5 * Math.PI) : (float)(0.5 * Math.PI));
            loc.setYaw(loc.getYaw() - (float) Math.atan(dz / dx));
        } else if (dz < 0) {
            loc.setYaw((float) Math.PI);
        }

        double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

        loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
        loc.setPitch(((float) -Math.atan(dy / dxz)) * 180f / (float) Math.PI);

        return new Vector2D(wrapDegrees(loc.getYaw()), wrapDegrees(loc.getPitch()));
    }

    public static float wrapDegrees(float angle) {
        angle %= 360.0F;

        if (angle >= 180.0F) {
            angle -= 360.0F;
        }

        if (angle < -180.0F) {
            angle += 360.0F;
        }

        return angle;
    }

    private boolean isAround(Vector2D v1, Vector2D v2, double delta){
        return Math.abs(v2.getX() - v1.getX()) <= delta && Math.abs(v2.getY() - v1.getY()) <= delta;
    }

    public boolean isRunning(){
        return isRunning;
    }

    public void start(Plugin plugin){
        if(!isRunning){
            runnable = new BukkitRunnable(){
                public void run(){
                    if((System.currentTimeMillis() - lastDamageTime) > 5000){
                        stop();
                    }
                    player.getPlayer().spawnParticle(getParticle(), getParticleLoc(), 0, 0, 0, 0, 0, null);
                }
            };

            runnable.runTaskTimerAsynchronously(plugin, 0, 5);
            isRunning = true;
        }
    }

    public void stop(){
        if(isRunning){
            runnable.cancel();
            isRunning = false;

            this.particleLoc = LocationHelper.getRandParticleLoc(block.getLocation(), lastFace);
            this.damage = 0;
            this.lastDamage = 0;
            this.animation = -1;

            if(!isBroken()){
                isCrit = false;
                particle = Particle.CRIT;
            }

            player.returnPrevPercents();
            BlockHelper.sendBreakAnimation(block, -1);
        }
    }

}
