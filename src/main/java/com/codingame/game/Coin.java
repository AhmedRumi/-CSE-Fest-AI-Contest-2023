package com.codingame.game;

public class Coin {
    private final Coord position;
    private final int value;
    private int health;
    public Coin(Coord position, int value, int health) {
        this.position = position;
        this.value = value;
        this.health = health;
    }

    public Coord getPosition() {
        return position;
    }

    public int getValue() {
        return value;
    }
    public int getHealth() {
        return health;
    }
    public void reduceHealth(int miningStrength) {
        this.health -= miningStrength;
        if(this.health <0 ) this.health = 0;
    }
}
