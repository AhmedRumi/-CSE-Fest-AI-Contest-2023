package com.codingame.game;

import com.codingame.game.action.ActionDirection;
import com.codingame.game.grid.Grid;
import com.codingame.game.grid.TetrisBasedMapGenerator;
import com.google.inject.Singleton;

import java.util.ArrayList;

import static java.lang.Math.abs;


@Singleton
public class Maze {

    private int[][] grid;
    private int row, col;


    private boolean[][][] rowVisibility, colVisibility;

    public int[][] getGrid() {
        return grid;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    private void setRowVisibility() {
        rowVisibility = new boolean[row][col][col];
        for (int r = 0; r < row; r++) {
            for (int i = 0; i < col; i++) {
                boolean visible = true;
                for (int j = i; j >= 0; j--) {
                    visible = visible && (grid[r][j] == 0);
//                    rowVisibility[r][i][j] = visible;
                }
                visible = true;
                for (int j = i; j < col; j++) {
                    visible = visible && (grid[r][j] == 0);
//                    rowVisibility[r][i][j] = visible;
                }
            }
        }
    }

    private void setColVisibility() {
        colVisibility = new boolean[col][row][row];
        for (int c = 0; c < col; c++) {
            for (int i = 0; i < row; i++) {
                boolean visible = true;
                for (int j = i; j >= 0; j--) {
                    visible = visible && (grid[j][c] == 0);
                    colVisibility[c][i][j] = visible;
                }
                visible = true;
                for (int j = i; j < row; j++) {
                    visible = visible && (grid[j][c] == 0);
                    colVisibility[c][i][j] = visible;
                }
            }
        }
    }

    void init() {
//        row = RandomUtil.randomInt(Config.MIN_MAZE_ROW, Config.MAX_MAZE_ROW);
//        col = RandomUtil.randomOddInt(Config.MIN_MAZE_COL, Config.MAX_MAZE_COl);
        row = Config.MAX_MAZE_ROW;
        col = Config.MAX_MAZE_COl;
        grid = new int[row][col];
        Grid gridObj = new Grid(col, row);
        TetrisBasedMapGenerator generator = new TetrisBasedMapGenerator();
        generator.init();
        generator.generateWithHorizontalSymmetry(gridObj, RandomUtil.random);

        System.out.println("Rows = " + row);
        System.out.println("Cols = " + col);

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {

//                grid[i][j] = 0;
                // grid[i][j] = RandomUtil.randomInt(0, 1);
                grid[i][j] = gridObj.get(j, i).isWall() ? 1 : 0;
                if (i == 0 || j == 0 || i == row - 1 || j == col - 1) grid[i][j] = 1;
            }
        }


      
        setRowVisibility();
        setColVisibility();
    }

    public boolean isVisible(Coord pos1, Coord pos2) {
        int x1 = pos1.getX(), y1 = pos1.getY();
        int x2 = pos2.getX(), y2 = pos2.getY();
        if (x1 == x2) {
            return rowVisibility[x1][y1][y2];
        }
        if (y1 == y2) {
            return colVisibility[y1][x1][x2];
        }
        return false;
//        if(abs(pos1.getX()-pos2.getX())+abs(pos1.getY()-pos2.getY())<= Config.INITIAL_FOG_OF_WAR)
//        {
//            return true;
//        }
//        else
//        {
//            return  false;
//        }
    }

    public boolean isVisible(Coord pos1, Coord pos2, int range) {
        int x1 = pos1.getX(), y1 = pos1.getY();
        int x2 = pos2.getX(), y2 = pos2.getY();
        if (x1 == x2) {
            return rowVisibility[x1][y1][y2] && Math.abs(y1 - y2) <= range;
        }
        if (y1 == y2) {
            return colVisibility[y1][x1][x2] && Math.abs(x1 - x2) <= range;
        }
        return false;
//        if(abs(pos1.getX()-pos2.getX())+abs(pos1.getY()-pos2.getY())<=range)
//        {
//            return true;
//        }
//        else
//        {
//            return  false;
//        }

    }

//    public boolean isVisible(Coord pos1, Coord pos2, int limit, int dir) {
//        // right
//        if(dir == 1) {
//            if((pos1.getY() == pos2.getY()) && (pos2.getX() - pos1.getX() > 0) && (pos2.getX() - pos1.getX() <= limit)) {
//                return true;
//            }
//        }
//        // left
//        if(dir == 2) {
//            if((pos1.getY() == pos2.getY()) && (pos2.getX() - pos1.getX() < 0) && (pos1.getX() - pos2.getX() <= limit)) {
//                return true;
//            }
//        }
//        // top
//        if(dir == 3) {
//            if((pos1.getX() == pos2.getX()) && (pos2.getY() - pos1.getY() > 0) && (pos2.getY() - pos1.getY() <= limit)) {
//                return true;
//            }
//        }
//        // bottom
//        if(dir == 4) {
//            if((pos1.getX() == pos2.getX()) && (pos2.getY() - pos1.getY() < 0) && (pos1.getY() - pos2.getY() <= limit)) {
//                return true;
//            }
//        }
//        return false;
//    }

    public boolean isVisibleToPowerUp(Coord pos1, Coord pos2, ActionDirection dir) {
        System.out.println("Power up visibility found!");
        if(dir == ActionDirection.RIGHT) {
            return isRightAndVisible(pos1, pos2);
        }
        else if(dir == ActionDirection.LEFT) {
            return isLeftAndVisible(pos1, pos2);
        }
        else if(dir == ActionDirection.UP) {
            return isUpAndVisible(pos1, pos2);
        }
        else if (dir == ActionDirection.DOWN) {
            return isDownAndVisible(pos1, pos2);
        }
        return false;
    }

    public boolean isRightAndVisible(Coord pos1, Coord pos2) {
        return (pos1.getX() < pos2.getX()) && (pos1.getY() == pos2.getY()) && isVisible(pos1, pos2);
    }

    public boolean isLeftAndVisible(Coord pos1, Coord pos2) {
        return (pos1.getX() > pos2.getX()) && (pos1.getY() == pos2.getY()) && isVisible(pos1, pos2);
    }

    public boolean isUpAndVisible(Coord pos1, Coord pos2) {
        return (pos1.getX() == pos2.getX()) && (pos1.getY() < pos2.getY()) && isVisible(pos1, pos2);
    }

    public boolean isDownAndVisible(Coord pos1, Coord pos2) {
        return (pos1.getX() == pos2.getX()) && (pos1.getY() > pos2.getY()) && isVisible(pos1, pos2);
    }


}
