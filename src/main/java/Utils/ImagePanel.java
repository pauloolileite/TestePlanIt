package Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Objects;

public class ImagePanel extends JPanel {
    private final Image imagem;
    private final int larguraMaxima;
    private final int alturaMaxima;
    private final int alinhamentoHorizontal;
    private float opacidade = 1.0f;

    private Runnable onClick;

    public ImagePanel(String caminhoImagem, int larguraMaxima, int alturaMaxima) {
        this(new ImageIcon(caminhoImagem).getImage(), larguraMaxima, alturaMaxima, SwingConstants.CENTER);
    }

    public ImagePanel(String caminhoImagem, int larguraMaxima, int alturaMaxima, int alinhamentoHorizontal) {
        this(new ImageIcon(caminhoImagem).getImage(), larguraMaxima, alturaMaxima, alinhamentoHorizontal);
    }

    public ImagePanel(URL imagemURL, int larguraMaxima, int alturaMaxima, int alinhamentoHorizontal) {
        this(new ImageIcon(Objects.requireNonNull(imagemURL)).getImage(), larguraMaxima, alturaMaxima, alinhamentoHorizontal);
    }

    private ImagePanel(Image imagem, int larguraMaxima, int alturaMaxima, int alinhamentoHorizontal) {
        this.imagem = imagem;
        this.larguraMaxima = larguraMaxima;
        this.alturaMaxima = alturaMaxima;
        this.alinhamentoHorizontal = alinhamentoHorizontal;

        setOpaque(false);
        setPreferredSize(new Dimension(larguraMaxima, alturaMaxima));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) onClick.run();
            }
        });
    }

    public void setOnClick(Runnable action) {
        this.onClick = action;
    }

    public void setOpacidade(float opacidade) {
        this.opacidade = Math.max(0f, Math.min(1f, opacidade));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int larguraOriginal = imagem.getWidth(null);
        int alturaOriginal = imagem.getHeight(null);

        if (larguraOriginal <= 0 || alturaOriginal <= 0) return;

        double proporcao = Math.min((double) larguraMaxima / larguraOriginal, (double) alturaMaxima / alturaOriginal);
        int novaLargura = (int) (larguraOriginal * proporcao);
        int novaAltura = (int) (alturaOriginal * proporcao);

        int x;
        switch (alinhamentoHorizontal) {
            case SwingConstants.LEFT -> x = 0;
            case SwingConstants.RIGHT -> x = getWidth() - novaLargura;
            default -> x = (getWidth() - novaLargura) / 2;
        }
        int y = (getHeight() - novaAltura) / 2;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidade));
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(imagem, x, y, novaLargura, novaAltura, this);
        g2d.dispose();
    }
}
