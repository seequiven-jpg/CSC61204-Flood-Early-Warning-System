package floodwarning.view;
import javax.swing.*;
import java.awt.*;
/** Minimal Swing view; controller wiring is a team task. */
public final class DashboardView extends JFrame {
    private final JLabel status = new JLabel("Flood data not loaded", SwingConstants.CENTER);
    public DashboardView() {
        super("Community Flood Early Warning"); setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12)); add(new JLabel("Flood Warning Dashboard", SwingConstants.CENTER), BorderLayout.NORTH);
        add(status, BorderLayout.CENTER); setSize(720, 420); setLocationRelativeTo(null);
    }
    public void showStatus(String message) { status.setText(message); }
}
