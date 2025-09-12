package View;

import Controller.ClienteController;
import Model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.sql.Connection;
import Utils.DatabaseConnection;
import java.util.List;
import java.util.Objects;

public class TelaCadastroCliente extends JFrame {
    private JTextField campoNome, campoEmail;
    private JFormattedTextField campoTelefone;
    private JButton botaoSalvar, botaoEditar, botaoExcluir;
    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;
    private ClienteController controller;
    private Cliente clienteSelecionado = null;

    public TelaCadastroCliente() {
        setTitle("Cadastro de Cliente");
        setSize(600, 600);
        setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Icons/plan_it.png"))).getImage());
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setLocationRelativeTo(null);

        Color azul1 = new Color(33, 124, 188);
        Color roxo1 = new Color(130, 0, 130);
        Color roxo2 = new Color(75, 0, 130);

        try {
            Connection conexao = DatabaseConnection.getConnection();
            controller = new ClienteController(conexao);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar: " + e.getMessage());
            return;
        }

        JPanel painelPrincipal = new JPanel(new GridBagLayout()) {
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
        painelPrincipal.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel labelTitulo = new JLabel("Cadastro de Cliente", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        labelTitulo.setForeground(azul1);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        painelPrincipal.add(labelTitulo, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

        campoNome = new JTextField();
        campoTelefone = new JFormattedTextField();
        campoEmail = new JTextField();

        JLabel labelNome = new JLabel("Nome:");
        labelNome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        painelPrincipal.add(labelNome, gbc);
        gbc.gridx = 1;
        campoNome.setPreferredSize(new Dimension(200, 30));
        painelPrincipal.add(campoNome, gbc);

        JLabel labelTelefone = new JLabel("Telefone:");
        labelTelefone.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 2;
        painelPrincipal.add(labelTelefone, gbc);
        try {
            MaskFormatter mask = new MaskFormatter("(##) #####-####");
            mask.setPlaceholderCharacter('_');
            campoTelefone = new JFormattedTextField(mask);
        } catch (Exception e) {
            campoTelefone = new JFormattedTextField();
        }
        gbc.gridx = 1;
        painelPrincipal.add(campoTelefone, gbc);

        JLabel labelEmail = new JLabel("Email:");
        labelEmail.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 3;
        painelPrincipal.add(labelEmail, gbc);
        gbc.gridx = 1;
        painelPrincipal.add(campoEmail, gbc);

        botaoSalvar = new JButton("Salvar");
        botaoSalvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botaoSalvar.setForeground(Color.WHITE);
        botaoSalvar.setBackground(azul1);
        botaoSalvar.setFocusPainted(false);
        botaoSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoSalvar.setPreferredSize(new Dimension(120, 30));
        botaoSalvar.setBorder(BorderFactory.createLineBorder(azul1, 1, true));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        painelPrincipal.add(botaoSalvar, gbc);

        botaoEditar = new JButton("Editar");
        botaoEditar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botaoEditar.setForeground(Color.WHITE);
        botaoEditar.setBackground(roxo1);
        botaoEditar.setFocusPainted(false);
        botaoEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoEditar.setPreferredSize(new Dimension(120, 30));
        botaoEditar.setBorder(BorderFactory.createLineBorder(roxo1, 1, true));

        botaoExcluir = new JButton("Excluir");
        botaoExcluir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botaoExcluir.setForeground(Color.WHITE);
        botaoExcluir.setBackground(roxo2);
        botaoExcluir.setFocusPainted(false);
        botaoExcluir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoExcluir.setPreferredSize(new Dimension(120, 30));
        botaoExcluir.setBorder(BorderFactory.createLineBorder(roxo2, 1, true));

        JPanel painelEdicao = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        painelEdicao.setBackground(Color.WHITE);
        painelEdicao.add(botaoEditar);
        painelEdicao.add(botaoExcluir);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        painelPrincipal.add(painelEdicao, gbc);

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "Telefone", "Email"}, 0);
        tabelaClientes = new JTable(modeloTabela);
        JScrollPane scroll = new JScrollPane(tabelaClientes);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        painelPrincipal.add(scroll, gbc);

        add(painelPrincipal);
        carregarClientes();

        botaoSalvar.addActionListener(e -> {
            String nome = campoNome.getText().trim();
            String telefone = campoTelefone.getText().replaceAll("[^\\d]", "");
            String email = campoEmail.getText().trim();
            try {
                controller.salvarCliente(clienteSelecionado, nome, telefone, email);
                JOptionPane.showMessageDialog(this, clienteSelecionado == null ? "Cliente cadastrado com sucesso." : "Cliente atualizado.");
                clienteSelecionado = null;
                limparCampos();
                carregarClientes();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        botaoEditar.addActionListener(e -> {
            int linha = tabelaClientes.getSelectedRow();
            if (linha != -1) {
                int id = (int) modeloTabela.getValueAt(linha, 0);
                String nome = (String) modeloTabela.getValueAt(linha, 1);
                String telefone = (String) modeloTabela.getValueAt(linha, 2);
                String email = (String) modeloTabela.getValueAt(linha, 3);
                clienteSelecionado = new Cliente(id, nome, telefone, email);
                campoNome.setText(nome);
                campoTelefone.setText(telefone);
                campoEmail.setText(email);
            }
        });

        botaoExcluir.addActionListener(e -> {
            int linha = tabelaClientes.getSelectedRow();
            if (linha != -1) {
                int id = (int) modeloTabela.getValueAt(linha, 0);
                int opcao = JOptionPane.showConfirmDialog(this, "Deseja excluir o cliente?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (opcao == JOptionPane.YES_OPTION) {
                    try {
                        controller.excluirCliente(id);
                        carregarClientes();
                        limparCampos();
                        JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage());
                    }
                }
            }
        });
    }

    private void carregarClientes() {
        try {
            modeloTabela.setRowCount(0);
            List<Cliente> lista = controller.listarClientes();
            for (Cliente c : lista) {
                modeloTabela.addRow(new Object[]{c.getId(), c.getNome(), c.getTelefone(), c.getEmail()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + e.getMessage());
        }
    }

    private void limparCampos() {
        campoNome.setText("");
        campoTelefone.setText("");
        campoEmail.setText("");
        clienteSelecionado = null;
    }
}
