package Main;

import View.TelaLogin;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
           FlatLightLaf.setup();
        } catch (Exception e) {
            System.out.println("Erro ao aplicar o tema: " + e.getMessage());
        }
        SwingUtilities.invokeLater(() -> {
            TelaLogin login = new TelaLogin();
            login.setVisible(true);
        });
    }
}
