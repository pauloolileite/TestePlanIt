package View;

import Controller.AgendamentoController;
import Model.Agendamento;
import Utils.DataUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import Utils.DatabaseConnection;
import java.util.List;
import java.util.Objects;

public class TelaRelatorioAgendamentos extends JFrame {

    private static final String[] COLUNAS = {
            "Data", "Hora", "Cliente", "Profissional", "Serviço", "Observações"
    };

    private static final String MSG_CSV_SUCESSO = "CSV exportado com sucesso!";
    private static final String MSG_CSV_ERRO = "Erro ao exportar: ";
    private static final String MSG_BUSCA_ERRO = "Erro ao buscar: ";
    private static final String MSG_DATA_INVALIDA = "Data inválida. Use o formato DD-MM-AAAA.";

    private JTextField campoCliente;
    private JTextField campoFuncionario;
    private JTextField campoServico;
    private JTextField campoData;
    private JButton botaoBuscar;
    private JButton botaoExportarCSV;
    private JTable tabelaResultados;
    private DefaultTableModel modeloTabela;
    private AgendamentoController controller;

    public TelaRelatorioAgendamentos() {
        setTitle("Relatório de Agendamentos");
        setSize(1000, 600);
        setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Icons/plan_it.png"))).getImage());
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        Color roxo = new Color(75, 0, 130);
        Color azul = new Color(33, 124, 188);

        try {
            Connection conexao = DatabaseConnection.getConnection();
            controller = new AgendamentoController(conexao);

            JLabel titulo = new JLabel("Relatório de Agendamentos", SwingConstants.CENTER);
            titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titulo.setForeground(roxo);
            titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
            add(titulo, BorderLayout.NORTH);

            JPanel painelFiltros = new JPanel(new GridLayout(2, 5, 10, 10));
            painelFiltros.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            painelFiltros.setBackground(Color.WHITE);

            campoCliente = new JTextField();
            campoFuncionario = new JTextField();
            campoServico = new JTextField();
            campoData = new JTextField();
            botaoBuscar = new JButton("Buscar");
            botaoExportarCSV = new JButton("Exportar CSV");

            JLabel labelCliente = new JLabel("Cliente:");
            JLabel labelFuncionario = new JLabel("Funcionário:");
            JLabel labelServico = new JLabel("Serviço:");
            JLabel labelData = new JLabel("Data (DD-MM-AAAA):");

            for (JLabel label : new JLabel[]{labelCliente, labelFuncionario, labelServico, labelData}) {
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setForeground(roxo);
            }

            painelFiltros.add(labelCliente);
            painelFiltros.add(labelFuncionario);
            painelFiltros.add(labelServico);
            painelFiltros.add(labelData);
            painelFiltros.add(new JLabel(""));

            painelFiltros.add(campoCliente);
            painelFiltros.add(campoFuncionario);
            painelFiltros.add(campoServico);
            painelFiltros.add(campoData);
            painelFiltros.add(botaoBuscar);

            add(painelFiltros, BorderLayout.PAGE_START);

            modeloTabela = new DefaultTableModel();
            modeloTabela.setColumnIdentifiers(COLUNAS);

            tabelaResultados = new JTable(modeloTabela);
            estilizarTabela(tabelaResultados, roxo);

            JScrollPane scroll = new JScrollPane(tabelaResultados);
            scroll.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            add(scroll, BorderLayout.CENTER);

            botaoBuscar.setFont(new Font("Segoe UI", Font.BOLD, 13));
            botaoBuscar.setBackground(azul);
            botaoBuscar.setForeground(Color.WHITE);
            botaoBuscar.setFocusPainted(false);

            botaoExportarCSV.setFont(new Font("Segoe UI", Font.BOLD, 13));
            botaoExportarCSV.setBackground(azul);
            botaoExportarCSV.setForeground(Color.WHITE);
            botaoExportarCSV.setFocusPainted(false);

            JPanel painelInferior = new JPanel(new BorderLayout());
            painelInferior.setBackground(Color.WHITE);
            painelInferior.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

            JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            painelBotoes.setBackground(Color.WHITE);
            painelBotoes.add(botaoExportarCSV);

            painelInferior.add(painelBotoes, BorderLayout.CENTER);
            add(painelInferior, BorderLayout.SOUTH);

            botaoBuscar.addActionListener(e -> carregarAgendamentosFiltrados());
            botaoExportarCSV.addActionListener(e -> exportarCSV());

            carregarAgendamentosFiltrados();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar tela: " + e.getMessage());
        }
    }

    private void carregarAgendamentosFiltrados() {
        try {
            String cliente = campoCliente.getText().trim();
            String funcionario = campoFuncionario.getText().trim();
            String servico = campoServico.getText().trim();
            String data = campoData.getText().trim();

            if (!data.isEmpty() && !data.matches("\\d{2}-\\d{2}-\\d{4}")) {
                JOptionPane.showMessageDialog(this, MSG_DATA_INVALIDA);
                return;
            }

            String dataFormatada = data.isEmpty() ? "" : DataUtils.formatarPadrao(data);
            List<Agendamento> resultados = controller.consultarPorFiltros(cliente, funcionario, servico, dataFormatada);
            preencherTabela(resultados);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, MSG_BUSCA_ERRO + ex.getMessage());
        }
    }

    private void preencherTabela(List<Agendamento> resultados) {
        modeloTabela.setRowCount(0);
        for (Agendamento a : resultados) {
            modeloTabela.addRow(new Object[]{
                    DataUtils.formatarData(a.getData()),
                    a.getHora(),
                    a.getCliente().getNome(),
                    a.getFuncionario().getNome(),
                    a.getServico(),
                    a.getObservacoes()
            });
        }
    }

    private void exportarCSV() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Salvar como CSV");
            int resultado = chooser.showSaveDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                FileWriter fw = new FileWriter(chooser.getSelectedFile() + ".csv");
                PrintWriter pw = new PrintWriter(fw);

                for (int i = 0; i < modeloTabela.getColumnCount(); i++) {
                    pw.print(modeloTabela.getColumnName(i));
                    if (i < modeloTabela.getColumnCount() - 1) pw.print(";");
                }
                pw.println();

                for (int i = 0; i < modeloTabela.getRowCount(); i++) {
                    for (int j = 0; j < modeloTabela.getColumnCount(); j++) {
                        Object valor = modeloTabela.getValueAt(i, j);
                        pw.print(valor);
                        if (j < modeloTabela.getColumnCount() - 1) pw.print(";");
                    }
                    pw.println();
                }

                pw.close();
                fw.close();
                JOptionPane.showMessageDialog(this, MSG_CSV_SUCESSO);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, MSG_CSV_ERRO + ex.getMessage());
        }
    }

    private void estilizarTabela(JTable tabela, Color corCabecalho) {
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(24);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabela.getTableHeader().setBackground(corCabecalho);
        tabela.getTableHeader().setForeground(Color.WHITE);
    }
}
