//Creates and sizes the desktop window with standard minimize and close buttons.


package view;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private final Panel panel;
    private final JLabel statusLabel;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public Window() {
        setTitle("APGR Raster Graphics App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // TOP: Keyboard shortcuts


        JTextArea shortcutsLabel = new JTextArea(
                "[L]\u00A0Line | [Shift]\u00A0Snap line | " +
                        "[P]\u00A0Polygon | [Enter]\u00A0Save current polygon | " +
                        "[F]\u00A0Fill polygon | [B]\u00A0Switch filling algorithm " +
                        "[S]\u00A0Scan-line algorithm | [K]\u00A0Clip polygon | [C]\u00A0Clear"
        );

        shortcutsLabel.setLineWrap(true);
        shortcutsLabel.setWrapStyleWord(true);

        shortcutsLabel.setEditable(false);
        shortcutsLabel.setFocusable(false);
        shortcutsLabel.setBackground(Color.WHITE);

// Keep the shortcuts component within the canvas width
        shortcutsLabel.setPreferredSize(new Dimension(800, 45));

        shortcutsLabel.setBorder(
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        );

        add(shortcutsLabel, BorderLayout.NORTH);


        // CENTER: Drawing canvas
        panel = new Panel(WIDTH, HEIGHT);
        add(panel, BorderLayout.CENTER);

        // BOTTOM: Current application state
        statusLabel = new JLabel();
        statusLabel.setOpaque(true);
        statusLabel.setBackground(Color.WHITE);
        statusLabel.setBorder(
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        );

        add(statusLabel, BorderLayout.SOUTH);

        updateStatus("LINE", "Flood Fill", false);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public Panel getPanel() {
        return panel;
    }

    public void updateStatus(String mode,
                             String algorithm,
                             boolean scanLineEnabled) {

        String scanLineColor = scanLineEnabled ? "green" : "red";
        String scanLineText = scanLineEnabled ? "ON" : "OFF";

        statusLabel.setText(
                "<html>" +
                        "Mode: " + mode +
                        " &nbsp; | &nbsp; Fill: " + algorithm +
                        " &nbsp; | &nbsp; Scan-line: " +
                        "<font color='" + scanLineColor + "'><b>" +
                        scanLineText +
                        "</b></font>" +
                        "</html>"
        );
    }
}
