import javazoom.jl.player.Player;
import java.io.FileInputStream;
import java.io.BufferedInputStream;

public class AudioPlayer {
    private Player player;
    private Thread playerThread;
    private String currentSongPath;
    private boolean isPaused = false;
    private long pauseLocation;
    private FileInputStream fis;
    private BufferedInputStream bis;

    public void play(String songPath) {
        try {
            stop(); // Stop any currently playing song

            currentSongPath = songPath;
            fis = new FileInputStream(currentSongPath);
            bis = new BufferedInputStream(fis);
            player = new Player(bis);

            playerThread = new Thread(() -> {
                try {
                    player.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            playerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (player != null) {
                player.close();
            }
            if (playerThread != null && playerThread.isAlive()) {
                playerThread.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaying() {
        return player != null;
    }
}
