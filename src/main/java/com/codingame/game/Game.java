package com.codingame.game;

import com.codingame.game.action.*;
import com.codingame.game.view.View;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.google.inject.Inject;

import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class Game {

    int totalMinions, minionsPerPlayer;
    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private Maze maze;
    @Inject private View view;
    @Inject private PlaygroundMatrix playgroundMatrix;
    ArrayList<Minion> aliveMinions;
    private ArrayList<Coin> availableCoins;
    private ArrayList<MinePower>activeMines;

    void init() {

        this.minionsPerPlayer = RandomUtil.randomOddInt(Config.MIN_MINIONS, Config.MAX_MINIONS);
        this.totalMinions = minionsPerPlayer * gameManager.getPlayerCount();

        setPlayerSide();
        generateMinions();
        setFlagBasePosition();
        setFlagPosition();
        setMinionsPositionsRandom();
        generateCoins();

        activeMines = new ArrayList<>();
    }


//    private void generateCoins() {
//        int row = maze.getRow();
//        int col = maze.getCol();
//        availableCoins = new ArrayList<>();
//        for (int i = 0; i < row; i++) {
//            for (int j = 0; j <= col/2; j++) {
//                if (maze.getGrid()[i][j] == 1) continue;
//                boolean occupied = false;
//                for (Player player : gameManager.getPlayers()) {
//                    for (Minion minion : player.getMinions()) {
//                        if (minion.getPos().manhattanTo(i, j) == 0) {
//                            occupied = true;
//                        }
//                    }
//                    if (player.getFlagBase().getPos().manhattanTo(i, j) == 0) {
//                        occupied = true;
//                    }
//                }
//                if (!occupied) {
////                    int coinValue = Config.COIN_VALUES[RandomUtil.randomWeightedIndex(Config.COIN_WEIGHTS)];
//                    int coinValue = Config.COIN_VALUES[0];
//                    if(RandomUtil.randomInt(0, 1) == 1) {
//                        availableCoins.add(new Coin(new Coord(i, j), coinValue));
//                        if (j < col-1-j) {
//                            // mirroring
//                            availableCoins.add(new Coin(new Coord(i, col-1-j), coinValue));
//                        }
//                    }
////                    availableCoins.add(new Coin(new Coord(i, j), coinValue));
////                    if (j < col-1-j) {
////                        // mirroring
////                        availableCoins.add(new Coin(new Coord(i, col-1-j), coinValue));
////                    }
//                }
//            }
//        }
//    }

    private void generateCoins() {
        int row = Config.MAX_MAZE_ROW;
        int col = Config.MAX_MAZE_COl;

        availableCoins = new ArrayList<>();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j <= col/2; j++) {
//                System.out.print(playgroundMatrix.getLayout());
                if(playgroundMatrix.getLayout() == null) {
                    System.out.println("NULL found!!");
                }
                if (playgroundMatrix.getLayout()[i][j] == Config.NO_RESOURCE) continue;
                boolean occupied = false;
                for (Player player : gameManager.getPlayers()) {
                    for (Minion minion : player.getMinions()) {
                        if (minion.getPos().manhattanTo(i, j) == 0) {
                            occupied = true;
                        }
                    }
//                    if (player.getFlagBase().getPos().manhattanTo(i, j) == 0) {
//                        occupied = true;
//                    }
                }
                if (!occupied && (playgroundMatrix.getLayout()[i][j] == Config.INVISIBLE_RESOURCE ||
                        playgroundMatrix.getLayout()[i][j] == Config.VISIBLE_RESOURCE)) {
//                    int coinValue = Config.COIN_VALUES[RandomUtil.randomWeightedIndex(Config.COIN_WEIGHTS)];
                    int coinValue = Config.COIN_VALUES[2];
                    int health = Config.COIN_HEALTHS[0];

                    availableCoins.add(new Coin(new Coord(i, j), coinValue, health));
                    if (j < col-1-j) {
                        // mirroring
                        availableCoins.add(new Coin(new Coord(i, col-1-j), coinValue, health));
                    }
                }
            }
        }
    }

    public ArrayList<Coin> getAvailableCoins() {
        return availableCoins;
    }

    /**
     *
     *
     * Todo: Need to optimize this
     */
    ArrayList<Coin>getVisibleCoins(Player player) {
        int cnt = 0;
        ArrayList<Coin>visibleCoins = new ArrayList<>();
        for(Minion minion: player.getMinions()) {
            if(minion.isDead()) continue;
            for(Coin coin: availableCoins) {
                cnt++;
                if(maze.isVisible(coin.getPosition(), minion.getPos())) {
                    visibleCoins.add(coin);
                }
            }
        }
        System.out.println("Required operations: " +  cnt);
        return visibleCoins.stream()
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Minion> getAliveMinions() {
        return this.aliveMinions;
    }

    /**
     *
     * Todo: Need to test this. Randomly swap players' sides at the beginning
     */
    private void setPlayerSide() {
        gameManager.getPlayer(0).setLeftPlayer(true);
        gameManager.getPlayer(1).setLeftPlayer(false);
    }

    private void setMinionsPositionsRandom() {

        int midCol = maze.getCol()/2;
        List<Coord>freeSpaces = new ArrayList<>();
        for(int i = 0 ; i < maze.getRow() ; i++) {
            for(int j = 0 ; j < midCol ; j++) {
                if(maze.getGrid()[i][j] == 0) freeSpaces.add(new Coord(i,j));
            }
        }

        freeSpaces.remove(gameManager.getPlayer(0).getFlagBase().getPos());

        for(int i = 0 ; i < minionsPerPlayer ; i++) {
            int id = RandomUtil.randomInt(0, freeSpaces.size());
            Coord leftCoord = freeSpaces.get(id);
            Coord rightCoord = new Coord(leftCoord.getX(), maze.getCol() - leftCoord.getY() - 1);
            gameManager.getPlayer(0).getMinion(i).setPos(leftCoord);
            gameManager.getPlayer(1).getMinion(i).setPos(rightCoord);
            freeSpaces.remove(leftCoord);
        }

    }

    void setFlagBasePosition() {

        for(int row = maze.getRow()/ 2 ; row < maze.getRow() ; row++) {
            if(maze.getGrid()[row][1] == 0) {
                for (Player player : gameManager.getPlayers()) {
                    if (player.isLeftPlayer()) {
                        player.getFlagBase().setPos(new Coord(row, 1));
                    } else {
                        player.getFlagBase().setPos(new Coord(row, maze.getCol() - 2));
                    }
                }
                break;
            }
        }
    }

    void setFlagPosition() {
        for(Player player: gameManager.getPlayers()) {
            player.getFlag().setPos(player.getFlagBase().getPos());
        }
    }

    // sets minion's positions to the edge of the board. Currently not used
    void setMinionsPositions() {
        int leftPlayer = 0;
        int rightPlayer = (leftPlayer ^ 1);
        gameManager.getPlayer(leftPlayer).setLeftPlayer(true);
        gameManager.getPlayer(rightPlayer).setLeftPlayer(false);

        int leftColumn = 1, rightColumn = maze.getCol() - 2;
        int midPos = maze.getRow()/2;

        List<Integer>freeRows = new ArrayList<>();

        for(int row = midPos, minionLeft = minionsPerPlayer/2 + 1; minionLeft > 0 ; row++) {
            if(maze.getGrid()[row][leftColumn] == 0) {
                freeRows.add(row);
                minionLeft--;
            }
        }
        for(int row = midPos - 1 , minionLeft = minionsPerPlayer/2 ; minionLeft > 0 ; row--) {
            if(maze.getGrid()[row][leftColumn] == 0) {
                freeRows.add(row);
                minionLeft--;
            }
        }
        Collections.sort(freeRows);
        for(int i = 0 ; i < minionsPerPlayer ; i++) {
            gameManager.getPlayer(leftPlayer).getMinion(i).setPos(new Coord(freeRows.get(i), leftColumn));
            gameManager.getPlayer(rightPlayer).getMinion(i).setPos(new Coord(freeRows.get(i), rightColumn));
        }
    }

    void generateMinions() {
        aliveMinions = new ArrayList<>();
        for(Player player: gameManager.getPlayers()) {
            for(int i = 0 ; i < this.minionsPerPlayer ; i++) {
                Minion minion = new Minion(i, player);
                aliveMinions.add(minion);
                player.addMinion(minion);
            }
        }
    }

    ArrayList<String> getInitialInfo(Player player) {
        Player opponent = getOpponentOf(player);
        ArrayList<String>ret = new ArrayList<>();
        ret.add(this.maze.getRow() + " "+ this.maze.getCol());
        for(int[] row: this.maze.getGrid()) {
            StringBuilder str = new StringBuilder();
            for(int cell: row) str.append(cell == 1 ? "#": ".");
            ret.add(str.toString());
        }
        ret.add(player.getFlagBase().getPos().getX() + " " + player.getFlagBase().getPos().getY());
        ret.add(opponent.getFlagBase().getPos().getX() + " " + opponent.getFlagBase().getPos().getY());
        ret.add(PowerUpType.FIRE + " "+ Config.FIRE_PRICE + " " + Config.FIRE_DAMAGE);
        ret.add(PowerUpType.FREEZE + " "+ Config.FREEZE_PRICE + " " + Config.FREEZE_TIMEOUT);
        ret.add(PowerUpType.MINE + " "+ Config.MINE_PRICE + " " + Config.MINE_DAMAGE);

        return ret;
    }


    ArrayList<String>getGameState(Player player) {
        Player opponent = gameManager.getPlayers().get(player.getIndex() ^ 1);
        ArrayList<String>ret = new ArrayList<>();
        ret.add(player.getCurrentCredit() + " " + opponent.getCurrentCredit());
        ret.add(player.getFlag().getPos().getX() + " " + player.getFlag().getPos().getY() + " " + (player.getFlag().isCaptured() ? player.getFlag().getCarrier().getID() : -1) );
        ret.add(opponent.getFlag().getPos().getX() + " " + opponent.getFlag().getPos().getY() + " " + (opponent.getFlag().isCaptured() ? opponent.getFlag().getCarrier().getID() : -1));

        ArrayList<Minion>aliveMinions = new ArrayList<>();
        for(Minion minion: player.getMinions()) {
            if( !minion.isDead()) aliveMinions.add(minion);
        }
        ret.add(aliveMinions.size() + "");
        for(Minion minion: aliveMinions) {
            ret.add(minion.getID() + " " +minion.getPos().getX() + " " + minion.getPos().getY() + " " + minion.getHealth() + " " + minion.getTimeOut());
        }
        ArrayList<Minion>visibleOpponents = new ArrayList<>();

        for(Minion opponentMinion: opponent.getMinions()) {
            boolean visible = false;
            for(Minion minion: player.getMinions()) {
                if(!minion.isDead() && !opponentMinion.isDead() && maze.isVisible(opponentMinion.getPos(), minion.getPos())) {
                    visible = true;
                    break;
                }
            }
            if(visible) {
                visibleOpponents.add(opponentMinion);
                System.out.println(player.getColor() + " can see minion at " + opponentMinion.getPos());
            }
        }
        ret.add(visibleOpponents.size() + "");
        for(Minion minion: visibleOpponents) {
            ret.add(minion.getID() + " " + minion.getPos().getX() + " " + minion.getPos().getY() + " " + minion.getHealth() + " "  + minion.getTimeOut());
        }

        ArrayList<Coin>visibleCoins = this.getVisibleCoins(player);
        ret.add(visibleCoins.size() + "");
        for(Coin coin: visibleCoins) {
            ret.add(coin.getPosition().getX() + " " + coin.getPosition().getY());
        }
//        System.out.println("Sending to player");
//        for(String str: ret) {
//            System.out.println(str);
//        }
//        System.out.println();
        return ret;
    }

    public void resetTurnData() {
        gameManager.getPlayers().forEach(Player::turnReset);
    }

    private void updateMinionMovement() {
        for(Minion minion: aliveMinions) {
            if(minion.getIntendedAction().getActionType() == ActionType.MOVE) {
                Coord from = minion.getPos();
                Coord to = ((MoveAction)minion.getIntendedAction()).getDestination();
                List<Coord>path = this.computeShortestPath(from, to);
                minion.setPathToDestination(path);

                if(path.size() == 0) {
                    minion.addSummary(String.format("(%d, %d) is unreachable for Minion %d", to.getX(), to.getY(), minion.getID()));
                }
                else if(path.size() == 1) {
                    minion.addSummary(String.format("Minion %d is already at (%d, %d)", minion.getID(), to.getX(), to.getY()));
                }
                else  {
                    view.moveMinion(minion);
                    minion.setPos(path.get(1));
                    minion.addSummary(String.format("Minion %d moved to (%d, %d)", minion.getID(), minion.getPos().x, minion.getPos().y));
                }
            }
        }
    }

    private List<Coord> computeShortestPath(Coord from, Coord to) {
        int n = maze.getRow();
        int m = maze.getCol();
        Coord[][] parent = new Coord[n][m];
        int[][] distance = new int[n][m];

        for(int[] row: distance) {
            Arrays.fill(row, Config.INF);
        }
        Queue<Coord> queue = new LinkedList<>();
        queue.add(from);
        distance[from.getX()][from.getY()] = 0;

        while(!queue.isEmpty()) {
            Coord top = queue.remove();
            int topDistance = distance[top.getX()][top.getY()];
            for(Coord adj: Config.ADJACENCY) {
                int x = top.getX() + adj.getX();
                int y = top.getY() + adj.getY();
                if(x >=0 && x < n && y >=0 && y < m && maze.getGrid()[x][y] == 0 && distance[x][y] == Config.INF) {
                    distance[x][y] = topDistance + 1;
                    parent[x][y] = top;
                    Coord newCoord = new Coord(x, y);
                    if(newCoord.equals(to)) break;
                    queue.add(newCoord);
                }
            }
        }
        List<Coord>path = new ArrayList<>();
        if(distance[to.getX()][to.getY()] == Config.INF) return path;

        path.add(to);
        while(!to.equals(from)) {
            to = parent[to.getX()][to.getY()];
            path.add(to);
        }
        Collections.reverse(path);
        return path;
    }

    private void updateUpgradeMiningStrength() {
        for (Player player : gameManager.getPlayers()) {
            for (Minion minion : player.getMinions()) {
                if (minion.isDead()) continue;

            }
        }
    }





    private void updateCoins() {
        ArrayList<Coin> acquiredCoins = new ArrayList<>();
        for (Coin coin : availableCoins) {
            boolean acquired = false;
            for (Player player : gameManager.getPlayers()) {
                for (Minion minion : player.getMinions()) {
                    if (minion.isDead()) continue;
                    if (minion.getPos().manhattanTo(coin.getPosition()) == 0 && coin.getHealth() == 0) {
                        minion.addSummary(String.format("Minion %d, Finished COLLECT-ing at (%d, %d)", minion.getID(), minion.getPos().getX(), minion.getPos().getY()));
                        player.addCredit(coin.getValue());
                        acquired = true;
                        break;
                    }
                }
            }
            if (acquired) {
                acquiredCoins.add(coin);
            }
        }
        view.removeCoins(acquiredCoins);
        availableCoins.removeAll(acquiredCoins);
    }

    public void updateGameState() {

        updateFlagPosition();   // acquire flag for immediately unfrozen minions
        updateUpgradeSkill();
        updateMinionMovement(); // resolve movement
        updateResourceHealth();
        updateCoins();          // update coin position
        updateDamage();         // use power ups
        detonateMine();         // check and activate mines
        removeDeadMinions();    // remove damaged minions from global list
        updateFlagPosition();   // update flag position again for moving minions
        updateMinionTimeOut();  // decrease freeze time out
        printGameSummary();

        for(Player player: gameManager.getPlayers()) {
            System.out.println(player.getColor());
            for(Minion minion: player.getMinions()) {
                System.out.println(minion.getID() + " -> "  + minion.getPos() +  " = " + minion.getHealth());
            }
        }
    }

    private void detonateMine() {

        List<MinePower>detonatedMines = new ArrayList<>();

        for(MinePower mine: activeMines) {
            boolean isDetonated = false;
            for(Minion minion: aliveMinions) {
                if(mine.getOrigin().equals(minion.getPos())) {
                    isDetonated = true;
                    break;
                }
            }
            if(isDetonated) {
                gameManager.addToGameSummary( String.format("Mine detonated at (%d, %d)", mine.getOrigin().getX(), mine.getOrigin().getY()));

                detonatedMines.add(mine);

                List<Minion> damagedMinions = mine.damageMinions(this, maze);
                List<Coord> affectedCells = mine.getAffectedCells(maze);
                view.addDetonatedMine(mine);
                view.addAffectedCells(affectedCells, PowerUpType.MINE);
                view.addAffectedMinions(damagedMinions, PowerUpType.MINE);
            }
        }
        this.activeMines.removeAll(detonatedMines);
    }

    private void updateMinionTimeOut() {
        for(Minion minion: aliveMinions) {
            minion.decreaseTimeOut();
        }
    }

    private void removeDeadMinions() {
        List<Minion>deadMinions = new ArrayList<>();
        for(Minion minion: aliveMinions) {
            if(minion.isDead()) {
                deadMinions.add(minion);
                view.addDeadMinion(minion);
            }
        }
        aliveMinions.removeAll(deadMinions);
    }

    private void updateDamage() {

        for(Minion minion: aliveMinions) {
            if(minion.getIntendedAction().getActionType() == ActionType.POWER_UP) {
                PowerUp power = (PowerUp) minion.getIntendedAction();
                if(!power.canBuy(minion.getOwner())) {
                    minion.addSummary(String.format("Cannot buy power %s, not enough credit available", power.getPowerType()));
                }
                else if(power.getPowerType() == PowerUpType.MINE) {
                    MinePower mine = (MinePower) power;
                    if(!mine.placeable(maze)) {
                        minion.addSummary(String.format("Minion %d cannot place mine at (%d, %d)", minion.getID(), mine.getOrigin().getX(), mine.getOrigin().getY()));
                        continue;
                    }
                    minion.addSummary(String.format("Minion %d is placing mine at (%d, %d)", minion.getID(), mine.getOrigin().getX(), mine.getOrigin().getY()));
                    minion.getOwner().decreaseCredit(mine.getPrice());
                    activeMines.add(mine);
                    view.addMine(mine);
                }
                else {
                    minion.addSummary(String.format("Minion %d is using power: %s", minion.getID(), power.getPowerType()));
                    minion.getOwner().decreaseCredit(power.getPrice());
                    List<Minion>affectedMinions = power.damageMinions(this, maze);
                    List<Coord>affectedCells = power.getAffectedCells(maze);
                    view.addAffectedMinions(affectedMinions, power.getPowerType());
                    view.addAffectedCells(affectedCells, power.getPowerType());
                    view.addPowerUpUser(minion, power.getPowerType());
                }
            }
        }
    }

    private void updateUpgradeSkill() {
        for (Player player : gameManager.getPlayers()) {
            for (Minion minion : player.getMinions()) {
                if (minion.isDead()) continue;
                if(minion.getIntendedAction().getActionType() == ActionType.UPGRADE) {
                    UpgradeSkill upSkill = (UpgradeSkill) minion.getIntendedAction();
                    if(!upSkill.canBuy(minion.getOwner())) {
                        minion.addSummary(String.format("Cannot buy Upgrade %s, not enough credit available", upSkill.getUpgradeType()));
                    }
                    else if(!upSkill.checkSkillLimit()) {
                        minion.addSummary(String.format("Max Upgrade reached for %s, cannot upgrade More", upSkill.getUpgradeType()));
                    }
                    else {


                        minion.addSummary(String.format("Minion %d is using Upgrade: %s", minion.getID(), upSkill.getUpgradeType()));

                        minion.getOwner().decreaseCredit(upSkill.getPrice());
                        minion.increaseUpgradeStrength(upSkill.getUpgradeType().ordinal());
                    }
                }
            }
        }
    }

    private void updateResourceHealth() {
        for (Player player : gameManager.getPlayers()) {

            for (Minion minion : player.getMinions()) {
                int collect_state=0;
                for (Coin coin : availableCoins) {

                    if (minion.isDead()) continue;
                    if (minion.getIntendedAction().getActionType() == ActionType.MINE_RESOURCE){
                        if (minion.getPos().manhattanTo(coin.getPosition()) == 0 && coin.getHealth() > 0) {
                            coin.reduceHealth(Config.MINING_STRENGTHS[minion.getSkillLevel(0)]);
                            minion.addSummary(String.format("Minion %d COLLECT-ing at (%d, %d)", minion.getID(), minion.getPos().getX(), minion.getPos().getY()));
                            collect_state=1;
                            break;
                        }
                        else{
                            // ToDo: Show No resource here

                        }
                    }

                }
                if(minion.getIntendedAction().getActionType() == ActionType.MINE_RESOURCE && collect_state==0)
                {
                    minion.addSummary(String.format("Minion %d Failed to COLLECT at (%d, %d)", minion.getID(), minion.getPos().getX(), minion.getPos().getY()));

                }
            }
        }
//        for(Minion minion: aliveMinions) {
//            if(minion.getIntendedAction().getActionType() == ActionType.MINE_RESOURCE) {
//                mineResource hitResource = (mineResource) minion.getIntendedAction();
//
//            }
//        }
    }


    Player getOpponentOf(Player player) {
        return gameManager.getPlayers().get(player.getIndex() ^ 1);
    }

    private void updateFlagPosition() {
        for(Player player: gameManager.getPlayers()) {
            Flag flag = player.getFlag();

            if(flag.isCaptured()) {
                // move the flag 1st
                flag.setPos(flag.getCarrier().getPos());

                // then drop it if dead/frozen
                if(flag.getCarrier().isDead() || flag.getCarrier().isFrozen()) {
                    gameManager.addToGameSummary(String.format("%s flag is dropped", player.getColor()));
                    flag.drop();
                }
            }

            if(!flag.isCaptured()) {
                Player opponent = getOpponentOf(player);
                for(Minion minion: opponent.getMinions()) {
                    if(!minion.isDead() && !minion.isFrozen() && minion.getPos().equals(flag.getPos())) {
                        gameManager.addToGameSummary(String.format("%s's flag is captured by %s minion %d", player.getColor(), opponent.getColor(), minion.getID()));
                        flag.setCarrier(minion);
                        flag.setPos(flag.getCarrier().getPos());
                        break;
                    }
                }
            }
        }
    }

    private void printGameSummary() {
        for (Player player : gameManager.getPlayers()) {
            gameManager.addToGameSummary("Player " + player.getNicknameToken() + " credits: " + player.getCurrentCredit());
            if (player.getMinions().stream().anyMatch(minion -> !minion.getGameSummary().isEmpty())) {
                gameManager.addToGameSummary(String.format("%s:", player.getNicknameToken()));
                player.getMinions().stream()
                    .sorted(Comparator.comparing(Minion::getID))
                    .forEach(minion -> {
                        minion.getGameSummary().forEach(line -> {
                            gameManager.addToGameSummary("- " + line);
                        });
                        minion.clearSummary();
                    });
            }
        }
    }

    public boolean isGameOver() {
        boolean gameOver = false;
        for(Player player: gameManager.getPlayers()) {
//            if(player.getFlag().getPos().equals(getOpponentOf(player).getFlagBase().getPos())) {
//                getOpponentOf(player).setWinner(true);
//                gameOver = true;
//            }
            if( (int) player.getMinions().stream().filter(minion -> !minion.isDead()).count() == 0) {
                getOpponentOf(player).setWinner(true);
                gameOver = true;
            }
            if( player.isTimedOut() ) {
                getOpponentOf(player).setWinner(true);
                gameOver = true;
            }
        }
        return gameOver;
    }

    public void endGame() {
        int flagReachedCnt = 0;
        Player winner = null;
//        for(Player player: gameManager.getPlayers()) {
//            if(player.getFlag().getPos().equals(getOpponentOf(player).getFlagBase().getPos())) {
//                flagReachedCnt++;
//                gameManager.addToGameSummary(String.format("%s has captured the flag!", getOpponentOf(player).getNicknameToken()));
//                winner = getOpponentOf(player);
//            }
//        }
//        if(flagReachedCnt == 2) {
//            gameManager.addToGameSummary("Both players have captured opponent's flag at the same time\nMatch tied!");
//        }
//        else if(flagReachedCnt == 1) {
//            gameManager.addToGameSummary(String.format("%s is the winner", winner.getNicknameToken()));
//        }
//        else {
//            for (Player player : gameManager.getPlayers()) {
//                if ((int) player.getMinions().stream().filter(minion -> !minion.isDead()).count() == 0) {
//                    gameManager.addToGameSummary(String.format("%s has no minions left! %s is the winner", player.getNicknameToken(), getOpponentOf(player).getNicknameToken()));
//                }
//            }
//        }

        for (Player player : gameManager.getPlayers()) {
            if ((int) player.getMinions().stream().filter(minion -> !minion.isDead()).count() == 0) {
                gameManager.addToGameSummary(String.format("%s has no minions left! %s is the winner", player.getNicknameToken(), getOpponentOf(player).getNicknameToken()));
            }
        }
    }
}
