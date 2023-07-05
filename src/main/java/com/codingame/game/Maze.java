package com.codingame.game;

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
//                    visible = visible && (grid[r][j] == 0);
                    rowVisibility[r][i][j] = visible;
                }
                visible = true;
                for (int j = i; j < col; j++) {
//                    visible = visible && (grid[r][j] == 0);
                    rowVisibility[r][i][j] = visible;
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
//                    visible = visible && (grid[j][c] == 0);
                    colVisibility[c][i][j] = visible;
                }
                visible = true;
                for (int j = i; j < row; j++) {
//                    visible = visible && (grid[j][c] == 0);
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
// <<<<<<< aungon
// //        grid = new int[row][col];
// //        Grid gridObj = new Grid(col, row);
// //        TetrisBasedMapGenerator generator = new TetrisBasedMapGenerator();
// //        generator.init();
// //        generator.generateWithHorizontalSymmetry(gridObj, RandomUtil.random);

//         //ratul
//         grid = new int[Config.MAX_MAZE_ROW][Config.MAX_MAZE_COl];
//         for(int i=0;i<Config.MAX_MAZE_ROW;i++) {
//             for(int j=0;j<Config.MAX_MAZE_COl;j++) {
//                 grid[i][j] = 0;
// =======
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
// >>>>>>> rumi
            }
        }
//        System.out.println(grid[3][1]);
        // System.out.print(layout);
        // System.out.println("Out init!!");
        int offset = Config.MAX_MAZE_COl/(2*Config.MAX_PRIMARY_RESOURCE_CLUSTER);

        for(int i=0;i< Config.MAX_PRIMARY_RESOURCE_CLUSTER;i++) {
            // random 4x4 resource cluster
            int col = RandomUtil.randomInt(i*offset + Config.RANDOM_WALK_MATRIX_DIM/2,
                    (i+1)*offset-1-Config.RANDOM_WALK_MATRIX_DIM/2);
            int row = RandomUtil.randomInt(0 + Config.RANDOM_WALK_MATRIX_DIM/2,
                    Config.MAX_MAZE_ROW-1-Config.RANDOM_WALK_MATRIX_DIM/2);
            randomWalk(row, col);
        }

        //ratul

        System.out.println("Rows = " + row);
        System.out.println("Cols = " + col);

//        for (int i = 0; i < row; i++) {
//            for (int j = 0; j < col; j++) {
//
//                grid[i][j] = 0;
//                // grid[i][j] = RandomUtil.randomInt(0, 1);
//                if (i == 0 || j == 0 || i == row - 1 || j == col - 1) grid[i][j] = 1;
//            }
//        }

        setRowVisibility();
        setColVisibility();
    }

    public boolean isVisible(Coord pos1, Coord pos2) {
//        int x1 = pos1.getX(), y1 = pos1.getY();
//        int x2 = pos2.getX(), y2 = pos2.getY();
//        if (x1 == x2) {
//            return rowVisibility[x1][y1][y2];
//        }
//        if (y1 == y2) {
//            return colVisibility[y1][x1][x2];
//        }
//        return false;
// <<<<<<< aungon
        // ratul
        return true;
    }


//     public boolean isVisible(Coord pos1, Coord pos2, int limit) {
//         int x1 = pos1.getX(), y1 = pos1.getY();
//         int x2 = pos2.getX(), y2 = pos2.getY();
//         if (x1 == x2) {
//             return rowVisibility[x1][y1][y2] && Math.abs(y1 - y2) <= limit;
// =======
//         if(abs(pos1.getX()-pos2.getX())+abs(pos1.getY()-pos2.getY())<= Config.INITIAL_FOG_OF_WAR)
//         {
//             return true;
//         }
//         else
//         {
//             return  false;
//         }
//     }

    public boolean isVisible(Coord pos1, Coord pos2, int range) {
//        int x1 = pos1.getX(), y1 = pos1.getY();
//        int x2 = pos2.getX(), y2 = pos2.getY();
//        if (x1 == x2) {
//            return rowVisibility[x1][y1][y2] && Math.abs(y1 - y2) <= limit;
//        }
//        if (y1 == y2) {
//            return colVisibility[y1][x1][x2] && Math.abs(x1 - x2) <= limit;
//        }
//        return false;
        if(abs(pos1.getX()-pos2.getX())+abs(pos1.getY()-pos2.getY())<=range)
        {
            return true;
// >>>>>>> rumi
        }
        else
        {
            return  false;
        }

    }

    public boolean isVisible(Coord pos1, Coord pos2, int limit, int dir) {
        // right
        if(dir == 1) {
            if((pos1.getY() == pos2.getY()) && (pos2.getX() - pos1.getX() > 0) && (pos2.getX() - pos1.getX() <= limit)) {
                return true;
            }
        }
        // left
        if(dir == 2) {
            if((pos1.getY() == pos2.getY()) && (pos2.getX() - pos1.getX() < 0) && (pos1.getX() - pos2.getX() <= limit)) {
                return true;
            }
        }
        // top
        if(dir == 3) {
            if((pos1.getX() == pos2.getX()) && (pos2.getY() - pos1.getY() > 0) && (pos2.getY() - pos1.getY() <= limit)) {
                return true;
            }
        }
        // bottom
        if(dir == 4) {
            if((pos1.getX() == pos2.getX()) && (pos2.getY() - pos1.getY() < 0) && (pos1.getY() - pos2.getY() <= limit)) {
                return true;
            }
        }
        return false;
    }

    public void randomWalk(int row, int col) {
        int maxResourceInOneCluster = RandomUtil.randomInt(Config.MIN_RESOURCE_IN_ONE_CLUSTER,
                Config.MAX_RESOURCE_IN_ONE_CLUSTER);
        grid[row][col] = Config.VISIBLE_RESOURCE;
        int nextRow = row;
        int nextCol = col;
        while(maxResourceInOneCluster != 0) {
            int tempRow = nextRow + toss();
            int tempCol = nextCol + toss();
            if (tempRow > row+Config.RANDOM_WALK_MATRIX_DIM/2 || tempRow < row-Config.RANDOM_WALK_MATRIX_DIM/2
                    || tempCol > col+Config.RANDOM_WALK_MATRIX_DIM/2 || tempCol < col-Config.RANDOM_WALK_MATRIX_DIM/2) {
                continue;
            }
            nextRow = tempRow;
            nextCol = tempCol;

            if(grid[nextRow][nextCol] == Config.NO_RESOURCE) {
                maxResourceInOneCluster --;
                grid[nextRow][nextCol] = Config.INVISIBLE_RESOURCE;
            }
        }
    }

    public int toss() {
        int tmp = RandomUtil.randomInt(0, 2) - 1;
        return tmp;
    }
}
