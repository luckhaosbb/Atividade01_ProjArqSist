package missao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Entrada e saída do console.
 *
 * Quem enfileira as teclas até o Enter é o terminal, não a JVM, e o Java padrão
 * não expõe esse controle. Em macOS e Linux desligamos o modo canônico chamando
 * stty; em Windows, ou quando a entrada é um arquivo/pipe, o jogo continua
 * funcionando no modo linha + Enter.
 */
public final class Terminal {

    private static final InputStreamReader ENTRADA = new InputStreamReader(System.in);

    private static boolean modoDireto;
    private static boolean graficoPorQuadros;
    private static boolean sttyDisponivel;
    private static String configuracaoOriginal;

    private Terminal() { }

    public static boolean isModoDireto() { return modoDireto; }
    public static boolean isGraficoPorQuadros() { return graficoPorQuadros; }

    public static void iniciar() {
        configuracaoOriginal = stty("-g");
        sttyDisponivel = configuracaoOriginal != null && !configuracaoOriginal.isEmpty();
        graficoPorQuadros = sttyDisponivel;
        Runtime.getRuntime().addShutdownHook(new Thread(Terminal::restaurar));
    }

    /** Explica ao jogador por que o modo tecla a tecla não está disponível. */
    public static String motivoDoModoLinha() {
        return "Console sem terminal interativo: o jogo segue no modo linha + Enter.\n"
                + "Para jogar sem Enter, execute em um terminal (Terminal, iTerm, PowerShell)\n"
                + "ou, no IntelliJ, marque 'Emulate terminal in output console' na configuração\n"
                + "de execução (Run/Debug Configurations > Modify options).";
    }

    /**
     * Liga a leitura tecla a tecla durante a partida.
     * O -icanon entrega cada tecla na hora e o -echo evita que ela apareça no
     * mapa; isig continua ativo, então Ctrl+C segue encerrando o jogo.
     */
    public static void modoJogo(boolean ativo) {
        if (!sttyDisponivel) return;
        if (ativo) {
            modoDireto = stty("-icanon", "min", "1", "-echo") != null;
            if (modoDireto) esconderCursor(true);
        } else {
            esconderCursor(false);
            stty(configuracaoOriginal);
            modoDireto = false;
        }
    }

    /** Devolve o terminal ao estado original; roda também via shutdown hook. */
    public static void restaurar() {
        if (modoDireto || sttyDisponivel) {
            esconderCursor(false);
            if (configuracaoOriginal != null) stty(configuracaoOriginal);
            modoDireto = false;
        }
    }

    /** @return a tecla lida, ou '\0' quando não houver comando. */
    public static char lerTecla() {
        try {
            if (!modoDireto) {
                String linha = lerLinha();
                if (linha == null) return 'q';
                return linha.isEmpty() ? '\0' : Character.toLowerCase(linha.charAt(0));
            }
            int c = ENTRADA.read();
            if (c == -1 || c == 3 || c == 4) return 'q';
            if (c == 27) return lerSeta();
            return Character.toLowerCase((char) c);
        } catch (IOException e) {
            return 'q';
        }
    }

    /** Traduz as setas do teclado, que chegam como ESC [ A-D, para w/a/s/d. */
    private static char lerSeta() throws IOException {
        if (!ENTRADA.ready() || ENTRADA.read() != '[' || !ENTRADA.ready()) return '\0';
        switch (ENTRADA.read()) {
            case 'A': return 'w';
            case 'B': return 's';
            case 'C': return 'd';
            case 'D': return 'a';
            default:  return '\0';
        }
    }

    /** @return a linha digitada, ou null quando a entrada termina (EOF). */
    public static String lerLinha() {
        try {
            StringBuilder texto = new StringBuilder();
            int c;
            while ((c = ENTRADA.read()) != -1) {
                if (c == '\n') return texto.toString();
                if (c != '\r') texto.append((char) c);
            }
            return texto.length() == 0 ? null : texto.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public static void aguardarTecla(String mensagem) {
        System.out.println(mensagem);
        if (modoDireto) {
            lerTecla();
        } else {
            lerLinha();
        }
    }

    /** Reposiciona o cursor no topo e apaga o quadro anterior. */
    public static void limparTela() {
        if (!graficoPorQuadros) {
            System.out.println();
            return;
        }
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void esconderCursor(boolean esconder) {
        if (!graficoPorQuadros) return;
        System.out.print(esconder ? "\033[?25l" : "\033[?25h");
        System.out.flush();
    }

    /**
     * Herdar a entrada padrão faz o stty agir exatamente sobre o descritor que a
     * JVM lê. Redirecionar de /dev/tty configuraria o terminal controlador, que
     * pode ser outro dispositivo — é o caso do console de Run das IDEs, onde a
     * entrada do processo é um pipe. Assim, quando não há terminal de verdade o
     * stty falha, que é justamente como detectamos a necessidade do modo linha.
     */
    private static String stty(String... argumentos) {
        try {
            ProcessBuilder pb = new ProcessBuilder("sh", "-c",
                    "stty " + String.join(" ", argumentos));
            pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectErrorStream(true);
            Process processo = pb.start();
            String saida = new String(lerTudo(processo)).trim();
            if (!processo.waitFor(2, TimeUnit.SECONDS) || processo.exitValue() != 0) {
                return null;
            }
            return saida;
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

    private static byte[] lerTudo(Process processo) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] pedaco = new byte[256];
        int lidos;
        while ((lidos = processo.getInputStream().read(pedaco)) != -1) {
            buffer.write(pedaco, 0, lidos);
        }
        return buffer.toByteArray();
    }
}
