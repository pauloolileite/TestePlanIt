package View;

import Controller.ServicoController;
import Model.Servico;
import Utils.ServicoUtils;
import Utils.TabelaUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import Utils.DatabaseConnection;
import java.util.List;
import java.util.Objects;

public class GerenciarServicos extends JFrame {
    private static final String MSG_ERRO_CONEXAO = "Erro ao conectar: ";
    private static final String MSG_ERRO_CARREGAR = "Erro ao carregar serviços: ";
    private static final String MSG_ERRO_EDITAR = "Erro ao editar: ";
    private static final String MSG_ERRO_EXCLUIR = "Erro ao excluir: ";
    private static final String MSG_SELECIONE_SERVICO = "Selecione um serviço.";

    private JTable tabelaServicos;
    private DefaultTableModel modeloTabela;
    private ServicoController controller;

    public GerenciarServicos() {
        setTitle("Gerenciar Serviços");
        setSize(700, 450);
        setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Icons/plan_it.png"))).getImage());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Color roxo = new Color(75, 0, 130);

        try {
            Connection conexao = DatabaseConnection.getConnection();
            controller = new ServicoController(conexao);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, MSG_ERRO_CONEXAO + e.getMessage());
            return;
        }

        JLabel titulo = new JLabel("Gerenciar Serviços", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(roxo);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(titulo, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel(new Object[]{"Nome", "Duração", "Preço (R$)"}, 0);
        tabelaServicos = new JTable(modeloTabela);
        TabelaUtils.aplicar(tabelaServicos, roxo);

        JScrollPane scroll = new JScrollPane(tabelaServicos);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scroll, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        painelBotoes.setBackground(Color.WHITE);

        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar");

        for (JButton btn : new JButton[]{btnEditar, btnExcluir, btnAtualizar}) {
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBackground(roxo);
            btn.setForeground(Color.WHITE);
            btn.setPreferredSize(new Dimension(120, 35));
            painelBotoes.add(btn);
        }

        add(painelBotoes, BorderLayout.SOUTH);

        btnEditar.addActionListener(e -> editarServico());
        btnExcluir.addActionListener(e -> excluirServico());
        btnAtualizar.addActionListener(e -> carregarServicos());

        carregarServicos();
    }

    private void carregarServicos() {
        try {
            List<Servico> lista = controller.listarServicos();
            preencherTabela(lista);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, MSG_ERRO_CARREGAR + e.getMessage());
        }
    }

    private void preencherTabela(List<Servico> lista) {
        modeloTabela.setRowCount(0);
        for (Servico s : lista) {
            modeloTabela.addRow(new Object[]{
                    s.getNome(),
                    ServicoUtils.formatarDuracao(s.getDuracao()),
                    ServicoUtils.formatarPreco(s.getPreco())
            });
        }
    }

    private void editarServico() {
        int linha = tabelaServicos.getSelectedRow();
        if (linha != -1) {
            String nomeAtual = (String) modeloTabela.getValueAt(linha, 0);
            String duracaoAtual = (String) modeloTabela.getValueAt(linha, 1);
            String precoAtual = ((String) modeloTabela.getValueAt(linha, 2)).replace("R$", "").trim();

            String nome = JOptionPane.showInputDialog(this, "Novo nome:", nomeAtual);
            String duracao = JOptionPane.showInputDialog(this, "Nova duração (hh:mm):", duracaoAtual);
            String precoStr = JOptionPane.showInputDialog(this, "Novo preço (ex: 89,90):", precoAtual);

            if (nome != null && duracao != null && precoStr != null) {
                try {
                    int duracaoMin = ServicoUtils.converterDuracaoParaMinutos(duracao);
                    double preco = ServicoUtils.converterPrecoParaDouble(precoStr);
                    int id = controller.buscarIdPorNome(nomeAtual);

                    controller.atualizarServico(new Servico(id, nome.trim(), duracaoMin, preco));
                    carregarServicos();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, MSG_ERRO_EDITAR + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, MSG_SELECIONE_SERVICO);
        }
    }

    private void excluirServico() {
        int linha = tabelaServicos.getSelectedRow();
        if (linha != -1) {
            String nome = (String) modeloTabela.getValueAt(linha, 0);
            int opcao = JOptionPane.showConfirmDialog(this, "Deseja excluir o serviço '" + nome + "'?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (opcao == JOptionPane.YES_OPTION) {
                try {
                    int id = controller.buscarIdPorNome(nome);
                    controller.excluirServico(id);
                    carregarServicos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, MSG_ERRO_EXCLUIR + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, MSG_SELECIONE_SERVICO);
        }
    }
}