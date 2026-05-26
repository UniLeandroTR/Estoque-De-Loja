import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Implementacao didatica de um banco chave-valor com:
 * - mapa principal em memoria e ordenado por chave;
 * - persistencia em append-only log;
 * - reconstrucao do estado na inicializacao.
 */
public class Estoque implements SGBD {
    private final Map<String, String> memoria = new TreeMap<>();
    private final LogAppendOnly log;

    public Estoque(Path arquivoLog) {
        this.log = new LogAppendOnly(arquivoLog);
        reconstruirEstado();
    }

    @Override
    public synchronized void put(String chave, String valor) {
        validarChave(chave);
        Objects.requireNonNull(valor, "valor nao pode ser nulo");

        RegistroLog registro = new RegistroLog(TipoOperacao.PUT, chave, valor);
        log.registrar(registro);

        memoria.put(chave, valor);
    }

    @Override
    public synchronized String get(String chave) {
        validarChave(chave);
        return memoria.get(chave);
    }

    @Override
    public synchronized void delete(String chave) {
        validarChave(chave);

        RegistroLog registro = new RegistroLog(TipoOperacao.DEL, chave, "");
        log.registrar(registro);

        memoria.remove(chave);
    }

    @Override
    public synchronized List<String> listarSKUsEmOrdem() {
        ArrayList<String> chavesOrdenadas = new ArrayList<>(memoria.keySet());
        chavesOrdenadas.sort(String::compareTo);
        return chavesOrdenadas;
    }

    @Override
    public synchronized int tamanho() {
        return memoria.size();
    }

    @Override
    public synchronized void fechar() {
        log.close();
    }

    private void reconstruirEstado() {
        for (RegistroLog registro : log.lerTodos()) {
            aplicarEmMemoria(registro);
        }
    }

    private void aplicarEmMemoria(RegistroLog registro) {
        if (registro.getOperacao() == TipoOperacao.PUT) {
            memoria.put(registro.getChave(), registro.getValor());
            return;
        }
        memoria.remove(registro.getChave());
    }

    private void validarChave(String chave) {
        Objects.requireNonNull(chave, "chave nao pode ser nula");
        if (chave.isBlank()) {
            throw new IllegalArgumentException("chave nao pode ser vazia");
        }
    }
}
