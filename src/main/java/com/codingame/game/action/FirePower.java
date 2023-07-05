package com.codingame.game.action;

import com.codingame.game.*;

import java.util.ArrayList;
import java.util.List;

public class FirePower extends PowerUp {

    public FirePower(Coord origin, Minion minion, ActionDirection dir) {
        super(origin, minion, dir);
        this.damage = Config.FIRE_DAMAGE;
        this.price = Config.FIRE_PRICE;
        this.damageDistLimit = Config.INF;
    }


    @Override
    public List<Minion> damageMinions(Game game, Maze maze) {
        List<Minion>damagedMinions = new ArrayList<>();
//        this.actionDirection
        System.out.println("Shooting " + this.powerUpUser.getOwner().getColor() + " " + this.powerUpUser.getID());
        for(Minion minion: game.getAliveMinions()) {
            if(minion != this.powerUpUser && maze.isVisibleToPowerUp(this.origin, minion.getPos(), this.actionDirection) ) {
                System.out.println("\tDealing Damage " + minion.getOwner().getColor() + " " + minion.getID());
                minion.dealDamage(this.damage);
                damagedMinions.add(minion);
            }
        }
        return damagedMinions;
    }

    @Override
    public PowerUpType getPowerType() {
        return PowerUpType.FIRE ;
    }
}
