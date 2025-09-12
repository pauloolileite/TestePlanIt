package Utils;

import javax.swing.*;
import java.awt.*;

public class PainelGradiente extends JPanel {
    private final Color corInicio;
    private final Color corFim;

    public PainelGradiente(Color corInicio, Color corFim) {
        this.corInicio = corInicio;
        this.corFim = corFim;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        int largura = getWidth();
        int altura = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, corInicio, largura, altura, corFim);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, largura, altura);
        g2d.dispose();
        super.paintComponent(g);
    }
}
