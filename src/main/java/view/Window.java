//Creates and sizes the desktop window with standard minimize and close buttons.


package view;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class Window extends JFrame {

    private final Panel panel;
    private final JLabel statusLabel;
    private final JButton fillColorButton;
    private final JButton borderColorButton;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public Window() {
        setTitle("APGR Raster Graphics App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        fillColorButton = new JButton("Fill Color");
        fillColorButton.setFocusable(false);

        borderColorButton = new JButton("Border Color");
        borderColorButton.setFocusable(false);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(fillColorButton);
        toolbar.add(borderColorButton);

        JTextArea shortcutsLabel = createShortcutsLabel();

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        topPanel.add(shortcutsLabel);
        topPanel.add(toolbar);

        add(topPanel, BorderLayout.NORTH);


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

        updateStatus("LINE", "Flood Fill", false, false);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private static JTextArea createShortcutsLabel() {
        JTextArea shortcutsLabel = new JTextArea(
                "[L]\u00A0Line | [Shift]\u00A0Snap line | " +
                        "[P]\u00A0Polygon | [Enter]\u00A0Save current polygon | " +
                        "[F]\u00A0Fill polygon | [B]\u00A0Switch filling algorithm | " +
                        "[S]\u00A0Scan-line algorithm | [K]\u00A0Clip polygon | " +
                        "[T]\u00A0Toggle pattern | [E]\u00A0Edit lines/polygons |" +
                        "[D]\u00A0Delete vertex/polygon | [C]\u00A0Clear"
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
        return shortcutsLabel;
    }

    public void setFillColorAction(Consumer<Color> action) {
        fillColorButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(
                    this,
                    "Choose Fill Color",
                    Color.GREEN
            );

            if (selectedColor != null) {
                action.accept(selectedColor);
            }
            panel.requestFocusInWindow();
        });
    }


    public void setBorderColorAction(Consumer<Color> action) {

        borderColorButton.addActionListener(e -> {

            Color selectedColor = JColorChooser.showDialog(
                    this,
                    "Choose Border Color",
                    Color.WHITE
            );

            if (selectedColor != null) {
                action.accept(selectedColor);
            }

            // Restore keyboard focus
            panel.requestFocusInWindow();
        });
    }


    public Panel getPanel() {
        return panel;
    }

    public void updateStatus(String mode,
                             String algorithm,
                             boolean scanLineEnabled,
                             boolean patternEnabled) {

        String scanLineColor = scanLineEnabled ? "green" : "red";
        String scanLineText = scanLineEnabled ? "ON" : "OFF";

        String patternColor = patternEnabled ? "green" : "red";
        String patternText = patternEnabled ? "ON" : "OFF";

        statusLabel.setText(
                "<html>" +
                        "Mode: " + mode +
                        " &nbsp; | &nbsp; Fill: " + algorithm +
                        " &nbsp; | &nbsp; Scan-line: " +
                        "<font color='" + scanLineColor + "'><b>" +
                        scanLineText + "</b></font>" +
                        " &nbsp; | &nbsp; Pattern: " +
                        "<font color='" + patternColor + "'><b>" +
                        patternText + "</b></font>" +
                        "</html>"
        );
    }
}
