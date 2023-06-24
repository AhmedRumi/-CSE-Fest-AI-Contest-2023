import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class SkeletonMain {
    public static void main(String[] args) {


        /* Multiplayer Game */
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        // Adds as many player as you need to test your game
        gameRunner.addAgent(Mover.class, "red");
        gameRunner.addAgent(Flamer.class, "blue");
        gameRunner.setSeed(247L);

        // Another way to add a player
        // gameRunner.addAgent("python3 /home/user/player.py");
        

        gameRunner.start();
    }
}
