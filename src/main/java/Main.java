//Main class - starts the app and hooks the window up with the Controller class

import control.Controller;
import view.Window;

import javax.swing.*;

void main() {
    SwingUtilities.invokeLater(() -> {
        Window window = new Window();
        window.setVisible(true);

        Controller controller = new Controller(window.getPanel(), window);

        window.setFillColorAction(color -> controller.setFillColor(color.getRGB() & 0xFFFFFF));
        window.setBorderColorAction(color -> controller.setBorderColor(color.getRGB() & 0xFFFFFF));
    });
}
