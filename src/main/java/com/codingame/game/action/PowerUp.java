package com.codingame.game.action;

import com.codingame.game.*;

import java.util.ArrayList;
import java.util.List;

public abstract class PowerUp implements Action {

    int damage;
    int price;
    int damageDistLimit;
    Coord origin;
    Minion powerUpUser;
    ActionDirection actionDirection;

    PowerUp(Coord origin, Minion powerUpUser, ActionDirection actionDirection) {
        this.origin = origin;
        this.powerUpUser = powerUpUser;
        this.actionDirection = actionDirection;
    }

    public abstract List<Minion> damageMinions(Game game, Maze maze);
    public abstract PowerUpType getPowerType();

    public boolean canBuy(Player player) {
        return player.getCurrentCredit() >= this.price;
    }
    public int getPrice() {
        return this.price;
    }
    public Coord getOrigin() {
        return this.origin;
    }

    public List<Coord> getAffectedCells(Maze maze) {

        ArrayList<Coord> affectedCoords = new ArrayList<>();
        affectedCoords.add(origin);

        int x = origin.getX();
        int y = origin.getY();

        int[] dx = {0 , 0, 0, 0};
        int[] dy = {0, 0, 0, 0};

        switch (this.actionDirection) {
            case UP:
                dy[3] = 1;
                break;
            case DOWN:
                dy[2] = -1;
                break;
            case RIGHT:
                dx[0] = 1;
                break;
            case LEFT:
                dx[1] = -1;
                break;
        }

        for(int k = 0 ; k < 4 ; k++) {
            if(dx[k] == 0 && dy[k] == 0)
                continue;
            for(int scale = 1; scale <= this.damageDistLimit; scale++) {
                int xx = x + dx[k] * scale;
                int yy = y + dy[k] * scale;
                if(xx < 0 || xx >= maze.getRow() || yy < 0 || yy >= maze.getCol() || maze.getGrid()[xx][yy] == 1) {
                    break;
                }
                affectedCoords.add(new Coord(xx, yy));
            }
        }
        return affectedCoords;
    }


    @Override
    public ActionType getActionType() {
        return ActionType.POWER_UP;
    }
}
