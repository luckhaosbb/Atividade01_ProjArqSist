package missao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ranking {
    public static final int TAMANHO = 5;

    private final Path arquivo;
    private List<RegistroPartida> registros;

    public Ranking() {
        this(Paths.get("ranking.json"));
    }

    public Ranking(Path arquivo) {
        this.arquivo = arquivo;
        this.registros = carregar();
    }

    public List<RegistroPartida> getRegistros() { return registros; }
    public boolean isEmpty() { return registros.isEmpty(); }
    public Path getArquivo() { return arquivo; }

    public int maiorPontuacao() {
        return registros.isEmpty() ? 0 : registros.get(0).getPontuacao();
    }

    public boolean entraNoTop(int pontuacao) {
        if (pontuacao <= 0) return false;
        if (registros.size() < TAMANHO) return true;
        return pontuacao > registros.get(registros.size() - 1).getPontuacao();
    }

    public void registrar(RegistroPartida registro) {
        registros.add(registro);
        ordenarELimitar();
        salvar();
    }

    public void resetar() {
        registros = new ArrayList<>();
        salvar();
    }

    private void ordenarELimitar() {
        registros.sort(Comparator.comparingInt(RegistroPartida::getPontuacao).reversed());
        while (registros.size() > TAMANHO) {
            registros.remove(registros.size() - 1);
        }
    }

    private void salvar() {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < registros.size(); i++) {
            json.append("  ").append(registros.get(i).paraJson());
            if (i < registros.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("]\n");
        try {
            Files.write(arquivo, json.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Não foi possível salvar o ranking: " + e.getMessage());
        }
    }

    private List<RegistroPartida> carregar() {
        if (!Files.exists(arquivo)) {
            return new ArrayList<>();
        }
        try {
            String json = new String(Files.readAllBytes(arquivo), StandardCharsets.UTF_8);
            List<RegistroPartida> lidos = parse(json);
            lidos.sort(Comparator.comparingInt(RegistroPartida::getPontuacao).reversed());
            while (lidos.size() > TAMANHO) {
                lidos.remove(lidos.size() - 1);
            }
            return lidos;
        } catch (IOException e) {
            System.out.println("Não foi possível ler o ranking: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static List<RegistroPartida> parse(String json) {
        List<RegistroPartida> lista = new ArrayList<>();
        int index = 0;
        while (true) {
            int inicio = json.indexOf('{', index);
            if (inicio < 0) break;
            int fim = json.indexOf('}', inicio);
            if (fim < 0) break;
            Map<String, String> campos = parseObjeto(json.substring(inicio + 1, fim));
            RegistroPartida registro = montar(campos);
            if (registro != null) lista.add(registro);
            index = fim + 1;
        }
        return lista;
    }

    /** Divide por vírgula fora de aspas, para não quebrar nomes de piloto que contenham vírgula. */
    private static Map<String, String> parseObjeto(String corpo) {
        Map<String, String> campos = new HashMap<>();
        List<String> partes = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        boolean dentroDeAspas = false;
        boolean escapado = false;
        for (char c : corpo.toCharArray()) {
            if (escapado) {
                atual.append(c);
                escapado = false;
            } else if (c == '\\') {
                atual.append(c);
                escapado = true;
            } else if (c == '"') {
                dentroDeAspas = !dentroDeAspas;
                atual.append(c);
            } else if (c == ',' && !dentroDeAspas) {
                partes.add(atual.toString());
                atual.setLength(0);
            } else {
                atual.append(c);
            }
        }
        partes.add(atual.toString());

        for (String parte : partes) {
            int separador = indiceDosDoisPontos(parte);
            if (separador < 0) continue;
            String chave = limpar(parte.substring(0, separador));
            String valor = limpar(parte.substring(separador + 1));
            if (!chave.isEmpty()) campos.put(chave, valor);
        }
        return campos;
    }

    private static int indiceDosDoisPontos(String parte) {
        boolean dentroDeAspas = false;
        boolean escapado = false;
        for (int i = 0; i < parte.length(); i++) {
            char c = parte.charAt(i);
            if (escapado) { escapado = false; continue; }
            if (c == '\\') { escapado = true; continue; }
            if (c == '"') dentroDeAspas = !dentroDeAspas;
            else if (c == ':' && !dentroDeAspas) return i;
        }
        return -1;
    }

    private static String limpar(String valor) {
        String limpo = valor.trim();
        if (limpo.length() >= 2 && limpo.startsWith("\"") && limpo.endsWith("\"")) {
            limpo = limpo.substring(1, limpo.length() - 1);
        }
        return limpo.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    /** Aceita também o formato antigo do ranking.json: {"name":...,"score":...}. */
    private static RegistroPartida montar(Map<String, String> campos) {
        String piloto = campos.containsKey("piloto") ? campos.get("piloto") : campos.get("name");
        String pontuacao = campos.containsKey("pontuacao") ? campos.get("pontuacao") : campos.get("score");
        if (piloto == null || pontuacao == null) return null;

        return new RegistroPartida(
                piloto,
                inteiro(pontuacao, 0),
                campos.getOrDefault("dataHora", "-"),
                inteiro(campos.get("passageirosResgatados"), 0),
                inteiro(campos.get("totalPassageiros"), 0),
                Dificuldade.porNome(campos.get("dificuldade")),
                inteiro(campos.get("duracaoSegundos"), 0),
                inteiro(campos.get("movimentos"), 0),
                Boolean.parseBoolean(campos.getOrDefault("vitoria", "false")));
    }

    private static int inteiro(String valor, int padrao) {
        if (valor == null) return padrao;
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return padrao;
        }
    }
}
