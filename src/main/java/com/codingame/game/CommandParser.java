package com.codingame.game;

import com.codingame.game.action.*;
import com.codingame.game.exception.GameException;
import com.codingame.game.exception.InvalidInputException;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.google.inject.Inject;

import javax.inject.Singleton;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class CommandParser {

    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private Maze maze;

    static String MOVE_COMMAND = "MOVE <id> <x> <y>";
    static String WAIT_COMMAND = "WAIT <id>";
    static String FIRE_COMMAND = "FIRE <id> <x>";
    static String FREEZE_COMMAND = "FREEZE <id>";
    static String MINE_COMMAND = "MINE <id> <x> <y>";

    static String COLLECT_COMMAND = "COLLECT <id>";

    static String UPGRADE_COMMAND = "UPGRADE <id> <SKILL>";
    String EXPECTED =
            MOVE_COMMAND +
                    " or " + WAIT_COMMAND +
                    " or " + FIRE_COMMAND +
                    " or " + FREEZE_COMMAND +
                    " or " + MINE_COMMAND +
                    " or " + COLLECT_COMMAND +
                    " or " + UPGRADE_COMMAND;

    static final Pattern PLAYER_MOVE_PATTERN = Pattern.compile(
            "^MOVE\\s+(?<id>\\d+)\\s+(?<x>-?\\d+)\\s+(?<y>-?\\d+)"
                    + "(?:\\s+(?<message>.+))?"
                    + "$",
            Pattern.CASE_INSENSITIVE
    );
    static final Pattern PLAYER_WAIT_PATTERN = Pattern.compile(
            "^WAIT\\s+(?<id>\\d+)"
                    + "(?:\\s+(?<message>.+))?"
                    + "$",
            Pattern.CASE_INSENSITIVE
    );
    static final Pattern PLAYER_FIRE_PATTERN = Pattern.compile(
            "^FIRE\\s+(?<id>\\d+)\\s+(?<x>-?\\d+)"
                    + "(?:\\s+(?<message>.+))?"
                    + "$",
            Pattern.CASE_INSENSITIVE
    );
    static final Pattern PLAYER_MINE_PATTERN = Pattern.compile(
            "^MINE\\s+(?<id>\\d+)\\s+(?<x>-?\\d+)\\s+(?<y>-?\\d+)"
                    + "(?:\\s+(?<message>.+))?"
                    + "$",
            Pattern.CASE_INSENSITIVE
    );
    static final Pattern PLAYER_FREEZE_PATTERN = Pattern.compile(
            "^FREEZE\\s+(?<id>\\d+)"
                    + "(?:\\s+(?<message>.+))?"
                    + "$",
            Pattern.CASE_INSENSITIVE
    );

    //ToDo: Dunno what this is doing tho
    static final Pattern PLAYER_COLLECT_PATTERN = Pattern.compile(
            "^COLLECT\\s+(?<id>\\d+)"
                    + "(?:\\s+(?<message>.+))?"
                    + "$",
            Pattern.CASE_INSENSITIVE
    );

    static final Pattern PLAYER_UPGRADE_PATTERN = Pattern.compile(
            "^UPGRADE\\s+(?<id>\\d+)\\s+(?<skill>-?\\d+)"
                    + "(?:\\s+(?<message>.+))?"
                    + "$",
            Pattern.CASE_INSENSITIVE
    );

    static final Pattern PLAYER_ACTION_PATTERN = Pattern.compile(
            "^(WAIT|MOVE|FIRE|FREEZE|MINE|COLLECT|UPGRADE)\\s+(?<id>\\d+).*",
            Pattern.CASE_INSENSITIVE
    );

    private void handleMineCommand(Matcher match, Minion minion) throws GameException, InvalidInputException{
        if(!match.matches()) throw new InvalidInputException(EXPECTED, "");
        int x = Integer.parseInt(match.group("x"));
        int y = Integer.parseInt(match.group("y"));
        if ( x < 0 || x >= maze.getRow() || y < 0 || y >= maze.getCol() && new Coord(x, y).manhattanTo(minion.getPos()) != 1) {
            throw new GameException(
                    String.format(
                            "Minion %d (%s) cannot place Mine at (%d, %d). Target cell has to be adjacent to minion",
                            minion.getID(),
                            minion.getOwner().getColor(),
                            x,
                            y
                    )
            );
        }
        minion.setIntendedAction(new MinePower(new Coord(x, y), minion, ActionDirection.NO_DIRECTION));
    }

    private Coord getDestination(Matcher match) throws InvalidInputException {
        if(!match.matches()) throw new InvalidInputException(EXPECTED, "");
        int x = Integer.parseInt(match.group("x"));
        int y = Integer.parseInt(match.group("y"));

        return new Coord(x, y);
    }

    private int getDirection(Matcher match, Minion minion) throws InvalidInputException {
        if(!match.matches()) throw new InvalidInputException(EXPECTED, "");
        System.out.println("firing1");
        int x = Integer.parseInt(match.group("x"));
        System.out.println("firing1.5");
        return x;
    }
    private void handleMoveCommand(Matcher match, Minion minion) throws GameException, InvalidInputException {
        Coord coord = getDestination(match);
        int x = coord.getX();
        int y = coord.getY();
        if ( x < 0 || x >= maze.getRow() || y < 0 || y >= maze.getCol()) {
            throw new GameException(
                    String.format(
                            "Minion %d (%s) cannot reach its target (%d, %d) because it is out of grid!",
                            minion.getID(),
                            minion.getOwner().getColor(),
                            x,
                            y
                    )
            );
        }
        minion.setIntendedAction(new MoveAction(coord));
    }

    Minion getMinionFromId(Player player, int minionID) throws GameException {
        try {
            Minion minion = player.getMinions()
                    .stream()
                    .filter( (value) -> (value.getID() == minionID))
                    .findFirst()
                    .get();
            if(minion.getIntendedAction() != Action.NO_ACTION) {
                throw new GameException(String.format("Minion %d cannot be commanded twice!", minionID));
            }
            return minion;
        } catch (NoSuchElementException e) {
            throw new GameException(String.format("Minion %d doesn't exist", minionID));
        }
    }

    private ActionDirection intToActionDirection(int fireDirection) {
        ActionDirection actionDirection = ActionDirection.NO_DIRECTION;
        if (fireDirection == 1)
            actionDirection = ActionDirection.RIGHT;
        else if (fireDirection == 2)
            actionDirection = ActionDirection.UP;
        else if (fireDirection == 3) {
            actionDirection = ActionDirection.LEFT;
        } else if (fireDirection == 4) {
            actionDirection = ActionDirection.DOWN;
        }

        return actionDirection;
    }
    private void handleWaitCommand(Minion minion) {
        minion.setIntendedAction(new WaitAction());
    }

    private void handleFireCommand(Matcher match, Minion minion) throws GameException, InvalidInputException {

        int dir = getDirection(match, minion);


        if ( dir < 0 || dir > 4) {
            throw new GameException(
                    String.format(
                            "Minion %d (%s) cannot set fire direction (%d) because it is out of range [0, 4]!",
                            minion.getID(),
                            minion.getOwner().getColor(),
                            dir
                    )
            );
        }

        System.out.println("firing2");
        minion.setIntendedAction(new FirePower(minion.getPos(), minion, intToActionDirection(dir)));
    }

    private void handleFreezeCommand(Matcher match, Minion minion) throws GameException, InvalidInputException {
        int dir = getDirection(match, minion);

        if ( dir < 0 || dir > 4) {
            throw new GameException(
                    String.format(
                            "Minion %d (%s) cannot set fire direction (%d) because it is out of range [0, 4]!",
                            minion.getID(),
                            minion.getOwner().getColor(),
                            dir
                    )
            );
        }

        minion.setIntendedAction(new FreezePower(minion.getPos(), minion, intToActionDirection(dir)));
    }
    private void handleCollectCommand(Minion minion) {
        minion.setIntendedAction(new mineResource());
        //ToDo: may need to pass more params
    }
    private void handleUpgradeCommand(Matcher match, Minion minion) throws InvalidInputException, GameException {

        if(!match.matches()) throw new InvalidInputException(EXPECTED, "");
        int skillNum = Integer.parseInt(match.group("skill"));
        if(skillNum>=Config.TOTAL_UPGRADEABLE_SKILL_COUNT || skillNum<0){
            throw new GameException(
                    String.format(
                            "Minion %d (%s) cannot upgrade skill (%d). Total skills :%d from 0 to (%d-1)",
                            minion.getID(),
                            minion.getOwner().getColor(),
                            skillNum,
                            Config.TOTAL_UPGRADEABLE_SKILL_COUNT,
                            Config.TOTAL_UPGRADEABLE_SKILL_COUNT
                    )
            );
        }
        else{
            if(skillNum == 0){
                minion.setIntendedAction(new UpgradeCollect(minion));
            }
        }
        //ToDo: may need to pass more params
    }

    public void parseCommands(Player player, List<String> outputs) {

        String[] commands = outputs.get(0).split("\\|");
        for(String command: commands) {
            String str = command.trim();
            if (str.isEmpty()) continue;

            try {
                Minion minion;
                System.out.println("Faltu1");
                Matcher match = PLAYER_ACTION_PATTERN.matcher(str);
                if (match.matches()) {
                    System.out.println("Faltu3");
                    int minionID = Integer.parseInt(match.group("id"));
                    minion = getMinionFromId(player, minionID);
                    if(minion.isDead()) {
                        minion.addSummary(String.format("Minion %d is dead! It cannot be commanded anymore!", minionID));
                        continue;
                    }
                    else if(minion.isFrozen()) {
                        minion.addSummary(String.format("Minion %d is Frozen! It has to wait %d more moves!", minionID, minion.getTimeOut()));
                        continue;
                    }
                }
                else {
                    System.out.println("Faltu2");
                    throw new InvalidInputException(EXPECTED, str);
                }

                if(PLAYER_MOVE_PATTERN.matcher(str).matches()) {
//                    System.out.print("Faltu debug 2");
                    handleMoveCommand(PLAYER_MOVE_PATTERN.matcher(str), minion);
                }
                else if(PLAYER_WAIT_PATTERN.matcher(str).matches()) {
                    handleWaitCommand(minion);
                }
                else if(PLAYER_FIRE_PATTERN.matcher(str).matches()) {
                    System.out.print("d kahsdja sdhahaksh dkas hfkaf kahfja hfkhassfashf jkJ");
                    handleFireCommand(PLAYER_FIRE_PATTERN.matcher(str), minion);
                }
                else if(PLAYER_FREEZE_PATTERN.matcher(str).matches()) {
                    handleFreezeCommand(PLAYER_FREEZE_PATTERN.matcher(str), minion);
                }
                else if(PLAYER_MINE_PATTERN.matcher(str).matches()) {
                    handleMineCommand(PLAYER_MINE_PATTERN.matcher(str), minion);
                }
                else if(PLAYER_COLLECT_PATTERN.matcher(str).matches()) {
                    handleCollectCommand(minion);
                }
                else if(PLAYER_UPGRADE_PATTERN.matcher(str).matches()) {
                    System.out.printf("FAltu debug");
                    System.out.printf(str);
//                    int skillNum = Integer.parseInt(match.group("skill"));

                    handleUpgradeCommand(PLAYER_UPGRADE_PATTERN.matcher(str),minion);
                }
                else {
                    throw new InvalidInputException(EXPECTED, str);
                }
            } catch (InvalidInputException | GameException e) {
                deactivatePlayer(player, e.getMessage());
                gameManager.addToGameSummary("Bad command: " + e.getMessage());
                return;
            } catch (Exception e) {
                deactivatePlayer(player, new InvalidInputException(e.toString(), EXPECTED, str).getMessage());
                gameManager.addToGameSummary("Bad command: " + e.getMessage());
                return;
            }
        }

        player.getAliveMinions()
                .filter(minion -> minion.getIntendedAction() == Action.NO_ACTION && !minion.isFrozen())
                .forEach(minion ->
                        minion.addSummary(String.format(
                                "Minion %d received no command.", minion.getID()
                        ))
                );
    }




    private void deactivatePlayer(Player player, String message) {
        player.deactivate(escapeHTMLEntities(message));
    }

    private String escapeHTMLEntities(String message) {
        return message
                .replace("&lt;", "<")
                .replace("&gt;", ">");
    }
}
