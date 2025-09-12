package View;

import Controller.ServicoController;
import Utils.ServicoUtils;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import Utils.DatabaseConnection;
import java.util.Objects;

public class TelaCadastroServico extends JFrame {
    private JTextField campoNome, campoPreco;
    private JComboBox<String> comboDuracao;
    private ServicoController controller;

    public TelaCadastroServico() {
        setTitle("Cadastro de Serviço");
        setSize(450, 500);
        setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Icons/plan_it.png"))).getImage());
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setLocationRelativeTo(null);

        Color azul1 = new Color(33, 124, 188);
        Color roxo1 = new Color(75, 0, 130);

        try {
            Connection conexao = DatabaseConnection.getConnection();
            controller = new ServicoController(conexao);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar: " + e.getMessage());
            return;
        }

        JPanel painel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image fundo = new ImageIcon("src/main/resources/Imagens/PlanIt.png").getImage();
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                int x = (getWidth() - 300) / 2;
                int y = (getHeight() - 300) / 2;
                g2d.drawImage(fundo, x, y, 300, 300, this);
                g2d.dispose();
            }
        };
        painel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Cadastro de Serviço");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(azul1);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(titulo, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

        JLabel labelNome = new JLabel("Nome:");
        labelNome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(labelNome, gbc);

        campoNome = new JTextField();
        campoNome.setPreferredSize(new Dimension(250, 30));
        campoNome.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(azul1, 1, true),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        painel.add(campoNome, gbc);

        JLabel labelPreco = new JLabel("Preço (R$):");
        labelPreco.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        painel.add(labelPreco, gbc);

        campoPreco = new JTextField();
        campoPreco.setPreferredSize(new Dimension(250, 30));
        campoPreco.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(azul1, 1, true),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        painel.add(campoPreco, gbc);

        JLabel labelDuracao = new JLabel("Duração:");
        labelDuracao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        painel.add(labelDuracao, gbc);

        comboDuracao = new JComboBox<>(new String[]{
                "1:00", "1:30", "2:00", "2:30", "3:00", "3:30", "4:00", "4:30", "5:00"
        });
        comboDuracao.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        painel.add(comboDuracao, gbc);

        JButton botaoSalvar = new JButton("Salvar");
        botaoSalvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botaoSalvar.setForeground(Color.WHITE);
        botaoSalvar.setBackground(azul1);
        botaoSalvar.setFocusPainted(false);
        botaoSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoSalvar.setPreferredSize(new Dimension(120, 30));
        botaoSalvar.setBorder(BorderFactory.createLineBorder(azul1, 1, true));

        JButton botaoGerenciar = new JButton("Gerenciar");
        botaoGerenciar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botaoGerenciar.setForeground(Color.WHITE);
        botaoGerenciar.setBackground(roxo1);
        botaoGerenciar.setFocusPainted(false);
        botaoGerenciar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoGerenciar.setPreferredSize(new Dimension(120, 30));
        botaoGerenciar.setBorder(BorderFactory.createLineBorder(roxo1, 1, true));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        painelBotoes.setBackground(Color.WHITE);
        painelBotoes.add(botaoSalvar);
        painelBotoes.add(botaoGerenciar);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(painelBotoes, gbc);

        botaoSalvar.addActionListener(e -> salvarServico());
        botaoGerenciar.addActionListener(e -> new GerenciarServicos().setVisible(true));

        add(painel);
    }

    private void salvarServico() {
        String nome = campoNome.getText().trim();
        String precoStr = campoPreco.getText().trim();
        String duracaoSelecionada = (String) comboDuracao.getSelectedItem();

        try {
            double preco = ServicoUtils.converterPrecoParaDouble(precoStr);
            controller.cadastrarServico(nome, duracaoSelecionada, String.valueOf(preco));

            JOptionPane.showMessageDialog(this, "Serviço cadastrado com sucesso!");
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

}
