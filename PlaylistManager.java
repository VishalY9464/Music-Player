import java.sql.*;
import java.util.*;

public class PlaylistManager {
    public PlaylistManager() {
        DatabaseHelper.initDB();
    }

    public void addSong(String path) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO playlist(path) VALUES(?)")) {
            stmt.setString(1, path);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllSongs() {
        List<String> songs = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT path FROM playlist")) {
            while (rs.next()) {
                songs.add(rs.getString("path"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songs;
    }
}
