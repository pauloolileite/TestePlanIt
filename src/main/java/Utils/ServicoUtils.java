package Utils;

public class ServicoUtils {

    public static String formatarDuracao(int minutosTotais) {
        int horas = minutosTotais / 60;
        int minutos = minutosTotais % 60;
        return String.format("%d:%02d", horas, minutos);
    }

    public static String formatarPreco(double preco) {
        return String.format("R$ %.2f", preco);
    }

    public static int converterDuracaoParaMinutos(String duracao) throws IllegalArgumentException {
        try {
            String[] partes = duracao.split(":");
            int horas = Integer.parseInt(partes[0].trim());
            int minutos = Integer.parseInt(partes[1].trim());
            return horas * 60 + minutos;
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de duração inválido. Use hh:mm, como 1:30.");
        }
    }

    public static double converterPrecoParaDouble(String precoStr) throws IllegalArgumentException {
        try {
            precoStr = precoStr.replace("R$", "").replace(" ", "").replace(",", ".");
            return Double.parseDouble(precoStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Formato de preço inválido. Use vírgula para separar centavos, como 89,90.");
        }
    }
}
