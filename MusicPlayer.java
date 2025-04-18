// MusicPlayer.java - Main Application
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

public class MusicPlayer {
    private JFrame frame;
    private JButton playButton, pauseButton, stopButton, nextButton, addButton;
    private JList<String> playlistUI;
    private DefaultListModel<String> playlistModel;
    private AudioPlayer audioPlayer;
    private PlaylistManager playlistManager;

    public MusicPlayer() {
        frame = new JFrame("Music Streaming Player");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        playlistModel = new DefaultListModel<>();
        playlistUI = new JList<>(playlistModel);
        JScrollPane playlistScrollPane = new JScrollPane(playlistUI);
        frame.add(playlistScrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        playButton = new JButton("Play");
        pauseButton = new JButton("Pause");
        stopButton = new JButton("Stop");
        nextButton = new JButton("Next");
        addButton = new JButton("Add Song");

        controlPanel.add(playButton);
        controlPanel.add(pauseButton);
        controlPanel.add(stopButton);
        controlPanel.add(nextButton);
        controlPanel.add(addButton);
        frame.add(controlPanel, BorderLayout.SOUTH);

        audioPlayer = new AudioPlayer();
        playlistManager = new PlaylistManager();
        loadPlaylist();

        playButton.addActionListener(e -> playSelectedSong());
        pauseButton.addActionListener(e -> audioPlayer.pause());
        stopButton.addActionListener(e -> audioPlayer.stop());
        nextButton.addActionListener(e -> playNextSong());
        addButton.addActionListener(e -> addSongToPlaylist());

        frame.setVisible(true);
    }

    private void loadPlaylist() {
        List<String> songs = playlistManager.getPlaylist();
        for (String song : songs) {
            playlistModel.addElement(song);
        }
    }

    private void playSelectedSong() {
        String selectedSong = playlistUI.getSelectedValue();
        if (selectedSong != null) {
            audioPlayer.play(selectedSong);
        }
    }

    private void playNextSong() {
        int index = playlistUI.getSelectedIndex();
        if (index < playlistModel.size() - 1) {
            playlistUI.setSelectedIndex(index + 1);
            playSelectedSong();
        }
    }

    private void addSongToPlaylist() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            playlistManager.addSong(file.getAbsolutePath());
            playlistModel.addElement(file.getAbsolutePath());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MusicPlayer::new);
    }
}

// AudioPlayer.java - Audio Player Logic
import javax.sound.sampled.*;
        import java.io.File;

class AudioPlayer {
    private Clip clip;
    private boolean isPaused;
    private long pausePosition;

    public void play(String filePath) {
        try {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
            File file = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            if (isPaused) {
                clip.setMicrosecondPosition(pausePosition);
                isPaused = false;
            }

            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (clip != null && clip.isRunning()) {
            pausePosition = clip.getMicrosecondPosition();
            clip.stop();
            isPaused = true;
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}

// PlaylistManager.java - Playlist Management
import java.sql.*;
        import java.util.ArrayList;
import java.util.List;

class PlaylistManager {
    private static final String DB_URL = "jdbc:sqlite:playlist.db";

    public PlaylistManager() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS Playlist (id INTEGER PRIMARY KEY AUTOINCREMENT, song TEXT)";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getPlaylist() {
        List<String> songs = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT song FROM Playlist")) {
            while (rs.next()) {
                songs.add(rs.getString("song"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    public void addSong(String songPath) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Playlist (song) VALUES (?)")) {
            pstmt.setString(1, songPath);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// DatabaseHelper.java - For future expansion (not required in current version)
// Currently, the database logic is handled directly in PlaylistManager.java
// You may refactor later to move connection handling here for scalability.
