package ru.nanit.mining.data;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Digger {

    public static final float DEF_DIG_SPEED = 0.05f; // Per one tick

    private Player player;
    private DigBlock point;

    private int critStreak;
    private float prevPercents = -1;
    private float digSpeedPercent;

    public Digger(Player player){
        this.player = player;
    }

    public Player getPlayer(){
        return player;
    }

    public void setPlayer(Player player){
        this.player = player;
    }

    public DigBlock getCurrentBlock(){
        return point;
    }

    public void addTempPercents(float percents){
        prevPercents = digSpeedPercent;
        addDigSpeedPercent(percents);
    }

    public void returnPrevPercents(){
        if(prevPercents != -1){
            digSpeedPercent = prevPercents;
            prevPercents = -1;
        }
    }

    public void addDigSpeedPercent(float percent){
        digSpeedPercent += percent;

        if(digSpeedPercent > 100.0){
            digSpeedPercent = 100;
        }

        digSpeedPercent = round(digSpeedPercent);
    }

    public void takeDigSpeedPercent(float percent){
        digSpeedPercent -= percent;

        if(digSpeedPercent < 0){
            digSpeedPercent = 0;
        }

        digSpeedPercent = round(digSpeedPercent);
    }

    private float round(double num){
        return (float) (Math.round(num * 100.0) / 100.0);
    }

    public float getDigSpeedPercent() {
        return digSpeedPercent;
    }

    public void setDigSpeedPercent(float digSpeedPercent) {
        this.digSpeedPercent = digSpeedPercent;
        if(this.digSpeedPercent > 100){
            this.digSpeedPercent = 100;
        }
    }

    public void setDigBlock(DigBlock point){
        this.point = point;
    }

    public int getCritStreak(){
        return critStreak;
    }

    public void setCritStreak(int critStreak){
        this.critStreak = critStreak;
    }

    public void addCritStreak(){
        this.critStreak++;
    }

    public void setMiningFatique(){
        player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, -1, false, false), true);
    }
}
