package com.codingame.game;

import com.google.inject.Singleton;

@Singleton
public class PlaygroundMatrix {

    private int[][] layout;

    public void init() {
        // System.out.println("In init!!");
        layout = new int[Config.MAX_MAZE_ROW][Config.MAX_MAZE_COl];
        for(int i=0;i<Config.MAX_MAZE_ROW;i++) {
            for(int j=0;j<Config.MAX_MAZE_COl;j++) {
                layout[i][j] = 0;
            }
        }
        System.out.println(layout[3][1]);
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

        // return layout;
    }

    public void randomWalk(int row, int col) {
        int maxResourceInOneCluster = RandomUtil.randomInt(Config.MIN_RESOURCE_IN_ONE_CLUSTER,
                Config.MAX_RESOURCE_IN_ONE_CLUSTER);
        layout[row][col] = Config.VISIBLE_RESOURCE;
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

            if(layout[nextRow][nextCol] == Config.NO_RESOURCE) {
                maxResourceInOneCluster --;
                layout[nextRow][nextCol] = Config.INVISIBLE_RESOURCE;
            }
        }
    }

    //only return -1 or +1
    public int toss() {
        int tmp = RandomUtil.randomInt(0, 2) - 1;
        return tmp;
    }

    public int[][] getLayout() {
        return layout;
    }
}
