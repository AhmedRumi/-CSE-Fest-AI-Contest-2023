package com.codingame.game;

public class Config {

    public static final int MINION_TOTAL_HEALTH = 100;
    public static final int MINION_INITIAL_MINING_STRENGTH = 12;

    public static final int RIGHT_PLAYER_COLOR = 0x0000ff;
    public static final int LEFT_PLAYER_COLOR = 0xff0000;
    public static final String LEFT_PLAYER_COLOR_NAME = "red";
    public static final String RIGHT_PLAYER_COLOR_NAME = "blue";

    public static final int INITIAL_CREDIT = 15;

    public static final int[] COIN_VALUES = {1, 2, 5};
    public static final int[] COIN_WEIGHTS = {10, 3, 1};
    public static final int[] COIN_HEALTHS = {60};

    public static final int[] MINING_STRENGTHS = {10, 12, 15, 20};

    public static final int[] UPGRADE_LIMITS = {3}; //COLLECT
    public static final int MINING_UPGRADE_PRICE = 10;
    public static final int MIN_MINIONS = 3;
    public static final int MAX_MINIONS = 3;

    public static final int MIN_MAZE_ROW = 17;
    public static final int MAX_MAZE_ROW = 20;

    public static final int MIN_MAZE_COL = 33;
    public static final int MAX_MAZE_COl = 36;

    //ratul
    public static final int MAX_PRIMARY_RESOURCE_CLUSTER = 3; //3
    public static final int RANDOM_WALK_MATRIX_DIM = 5;
    public static final int MAX_RESOURCE_IN_ONE_CLUSTER = 15; //8
    public static final int MIN_RESOURCE_IN_ONE_CLUSTER = 10; //4
    public static final int VISIBLE_RESOURCE = 2;
    public static final int INVISIBLE_RESOURCE = 1;
    public static final int NO_RESOURCE = 0;

    public static final int MAZE_UPPER_OFFSET = 200;


    public static final int FIRE_DAMAGE = 20;
    public static final int FIRE_PRICE = 2;

    public static final int MINE_DAMAGE = 30;
    public static final int MINE_PRICE = 1;
    public static final int MINE_RANGE = 2;


    public static final int FREEZE_TIMEOUT = 10;
    public static final int FREEZE_PRICE = 5;

    public static final Coord[] ADJACENCY = {
            new Coord(-1, 0),
            new Coord(1, 0),
            new Coord(0, -1),
            new Coord(0, 1)
    };

    public static final int INF = 10000000;
    public static final int FRAME_DURATION = 300;
    public static final int TOTAL_UPGRADEABLE_SKILL_COUNT = 1;

    public static final int INITIAL_FOG_OF_WAR = 5;
}
