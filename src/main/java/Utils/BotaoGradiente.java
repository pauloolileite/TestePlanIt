package Utils;

import javax.swing.*;
import java.awt.*;

public class BotaoGradiente extends JPanel {

    private final Color cor1;
    private final Color cor2;

    public BotaoGradiente(Color cor1, Color cor2) {
        this.cor1 = cor1;
        this.cor2 = cor2;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 70, 10, 50));
        setPreferredSize(new Dimension(380, 90));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        GradientPaint gp = new GradientPaint(0, 0, cor1, getWidth(), getHeight(), cor2);
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        g2.dispose();
        super.paintComponent(g);
    }
}
