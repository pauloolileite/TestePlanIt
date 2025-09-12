package Utils;

import javax.swing.*;
import java.awt.*;

public class TabelaUtils {
    public static void aplicar(JTable tabela, Color corCabecalho) {
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabela.setRowHeight(25);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabela.getTableHeader().setBackground(corCabecalho);
        tabela.getTableHeader().setForeground(Color.WHITE);
    }
}
