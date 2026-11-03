//Main class - starts the app and hooks the window up with the Controller class
import control.Controller;
import view.Window;

import javax.swing.*;

void main() {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
            Window window = new Window();
            window.setVisible(true);

            new Controller(window.getPanel(), window);
        }
    });
}
