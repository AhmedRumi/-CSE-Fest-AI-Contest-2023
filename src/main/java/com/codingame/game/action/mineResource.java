package com.codingame.game.action;
import com.codingame.game.*;
public class mineResource implements Action{
    @Override
    public ActionType getActionType() {
        return ActionType.MINE_RESOURCE;
    }
}
