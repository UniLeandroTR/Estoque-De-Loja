import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DemoGrupo05 {
    
    public static void main(String[] args) throws IOException {
        Path arquivoLog = Path.of("grupo05-demo.log");
        Files.deleteIfExists(arquivoLog);

        System.out.println("=== Grupo 05 - Demo de Estoque de Loja ===");
        System.out.println("Arquivo de log: " + arquivoLog.toAbsolutePath());

        primeiraSessao(arquivoLog);
        segundaSessao(arquivoLog);
        mostrarArquivoGerado(arquivoLog);
    }

    private static void primeiraSessao(Path arquivoLog) {
        System.out.println("\n--- Sessao 1: gravando dados ---");

        try (SGBD banco = new Estoque(arquivoLog)) {
            banco.put("SKU:003", new Produto("003"," Camiseta", 29.90, 10).toString());
            banco.put("SKU:001", new Produto("001"," Calça", 59.90, 5).toString());
            banco.put("SKU:002", new Produto("002"," Sapato", 99.90, 3).toString());
            banco.put("SKU:004", new Produto("004"," Bolsa", 79.90, 7).toString());

            System.out.println("SKU:001 -> " + banco.get("SKU:001"));
            System.out.println("SKU:002 -> " + banco.get("SKU:002"));
            System.out.println("Chaves em ordem: " + banco.listarSKUsEmOrdem());

            banco.put("SKU:002", new Produto("002"," Sapato", 99.90, 3).toString());
            banco.delete("SKU:003");

            System.out.println("Tamanho ao fechar a sessao 1: " + banco.tamanho());
            System.out.println("Chaves finais da sessao 1: " + banco.listarSKUsEmOrdem());
        }
    }

    private static void segundaSessao(Path arquivoLog) {
        System.out.println("\n--- Sessao 2: reabrindo e recuperando do log ---");

        try (SGBD banco = new Estoque(arquivoLog)) {
            System.out.println("Tamanho recuperado: " + banco.tamanho());
            System.out.println("SKU:001 -> " + banco.get("SKU:001"));
            System.out.println("SKU:002 -> " + banco.get("SKU:002"));
            System.out.println("SKU:003 -> " + banco.get("SKU:003"));
            System.out.println("Chaves em ordem apos reconstruir: " + banco.listarSKUsEmOrdem());
        }
    }

    private static void mostrarArquivoGerado(Path arquivoLog) throws IOException {
        System.out.println("\n--- Conteudo do log gerado ---");
        List<String> linhas = Files.readAllLines(arquivoLog, StandardCharsets.UTF_8);
        for (String linha : linhas) {
            System.out.println(linha);
        }
    }
}
