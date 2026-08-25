package missao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static final Random RANDOM = new Random();
    private static final Scanner SCANNER = new Scanner(System.in);

    private static final String[] NOMES_PROFESSOR = {"Dr. Silva", "Dra. Lima", "Dr. Moura"};
    private static final String[] NOMES_ENGENHEIRO = {"Eng. Rosa", "Eng. Tavares", "Eng. Duarte"};
    private static final String[] NOMES_ASTRONAUTA = {"Ast. Armstrong", "Ast. Aldrin", "Ast. Ride"};

    public static void main(String[] args) {
        Ranking ranking = new Ranking();
        exibirAnimacaoAbertura();

        boolean executando = true;
        while (executando) {
            switch (exibirMenuPrincipal()) {
                case 1: iniciarNovaMissao(ranking); break;
                case 2: exibirRanking(ranking); break;
                case 3: resetarRanking(ranking); break;
                default: executando = false; break;
            }
        }

        SCANNER.close();
        System.out.println("\nCâmbio, desligo. Fim da execução.");
    }

    private static int exibirMenuPrincipal() {
        System.out.println();
        System.out.println("================================================================");
        System.out.println("                  MISSÃO MARTE UNIFOR — MENU");
        System.out.println("================================================================");
        System.out.println(" 1 - Iniciar Nova Missão");
        System.out.println(" 2 - Visualizar Ranking Top 5");
        System.out.println(" 3 - Resetar Histórico de Ranking");
        System.out.println(" 4 - Sair do Jogo");
        return lerInteiro("Escolha uma opção: ", 1, 4);
    }

    private static void exibirRanking(Ranking ranking) {
        System.out.println();
        System.out.println("---------------- RANKING TOP 5 ----------------");
        if (ranking.isEmpty()) {
            System.out.println(" Ainda não há pontuações registradas.");
        } else {
            List<RegistroPartida> registros = ranking.getRegistros();
            for (int i = 0; i < registros.size(); i++) {
                System.out.printf(" %d. %s%n", i + 1, registros.get(i).paraTexto());
            }
        }
        System.out.println("-----------------------------------------------");
    }

    private static void resetarRanking(Ranking ranking) {
        System.out.print("Isso apaga todo o conteúdo de " + ranking.getArquivo()
                + ". Confirma? (s/n): ");
        String resposta = SCANNER.nextLine().trim().toLowerCase();
        if (resposta.startsWith("s")) {
            ranking.resetar();
            System.out.println("Ranking resetado com sucesso.");
        } else {
            System.out.println("Operação cancelada. O ranking foi mantido.");
        }
    }

    private static void iniciarNovaMissao(Ranking ranking) {
        System.out.print("\nDigite o nome do piloto: ");
        String piloto = SCANNER.nextLine().trim();
        if (piloto.isEmpty()) {
            piloto = "Piloto Anônimo";
        }

        Dificuldade dificuldade = selecionarDificuldade();
        System.out.println("Dificuldade definida para: " + dificuldade.getRotulo());

        int largura = lerInteiro("Informe a largura do mapa (5 a 30): ", 5, 30);
        int altura = lerInteiro("Informe a altura do mapa (5 a 30): ", 5, 30);

        Missao missao = criarMissao(dificuldade, largura, altura);
        exibirInstrucoes(missao);
        jogarPartida(missao, piloto, ranking);
    }

    private static Dificuldade selecionarDificuldade() {
        System.out.println("\nEscolha a Dificuldade da Missão:");
        for (Dificuldade d : Dificuldade.values()) {
            System.out.printf(" %d - %s (%d vidas, %d pontos, %d asteroides, %d passageiros, %d %s)%n",
                    d.ordinal() + 1, d.getRotulo(), d.getRecursosIniciais(), d.getPontuacaoInicial(),
                    d.getQuantidadeObstaculos(), d.getQuantidadePassageiros(),
                    d.getQuantidadeInimigos(), d.getQuantidadeInimigos() == 1 ? "inimigo" : "inimigos");
        }
        int opcao = lerInteiro("Sua escolha: ", 1, Dificuldade.values().length);
        return Dificuldade.values()[opcao - 1];
    }

    /** Sorteia a partir das casas realmente livres: em mapas pequenos, tentar
     *  posições aleatórias até achar uma vaga entraria em laço infinito. */
    private static Missao criarMissao(Dificuldade dificuldade, int largura, int altura) {
        int maxX = largura - 1;
        int maxY = altura - 1;

        Nave nave = new Nave("A-1", Nave.CAPACIDADE_PADRAO, dificuldade.getRecursosIniciais(),
                Missao.PLATAFORMA_X, Missao.PLATAFORMA_Y);
        Missao missao = new Missao(nave, dificuldade, 0, maxX, 0, maxY);

        List<int[]> livres = new ArrayList<>();
        for (int y = 0; y <= maxY; y++) {
            for (int x = 0; x <= maxX; x++) {
                if (x == Missao.PLATAFORMA_X && y == Missao.PLATAFORMA_Y) continue;
                livres.add(new int[]{x, y});
            }
        }
        Collections.shuffle(livres, RANDOM);

        int passageiros = Math.min(dificuldade.getQuantidadePassageiros(),
                Math.min(Nave.CAPACIDADE_PADRAO, livres.size()));
        for (int i = 0; i < passageiros; i++) {
            int[] pos = livres.remove(livres.size() - 1);
            missao.addPassageiro(criarPassageiro(i, pos[0], pos[1]));
        }

        // Limita inimigos e asteroides para que mapas pequenos continuem jogáveis.
        int totalCasas = largura * altura;
        int inimigos = Math.min(dificuldade.getQuantidadeInimigos(),
                Math.min(Math.max(1, totalCasas / 12), livres.size()));
        for (int i = 0; i < inimigos; i++) {
            int[] pos = livres.remove(livres.size() - 1);
            missao.addInimigo(new Inimigo("Alienígena " + (i + 1), pos[0], pos[1]));
        }

        int limiteAsteroides = Math.min(totalCasas * 30 / 100, Math.max(0, livres.size() - 2));
        int asteroides = Math.min(dificuldade.getQuantidadeObstaculos(), limiteAsteroides);
        for (int i = 0; i < asteroides; i++) {
            int[] pos = livres.remove(livres.size() - 1);
            missao.addAsteroide(new Asteroide(pos[0], pos[1]));
        }

        return missao;
    }

    private static Passageiro criarPassageiro(int indice, int x, int y) {
        int variacao = indice / 3;
        switch (indice % 3) {
            case 0:  return new Professor(NOMES_PROFESSOR[variacao % NOMES_PROFESSOR.length], x, y);
            case 1:  return new Engenheiro(NOMES_ENGENHEIRO[variacao % NOMES_ENGENHEIRO.length], x, y);
            default: return new Astronauta(NOMES_ASTRONAUTA[variacao % NOMES_ASTRONAUTA.length], x, y);
        }
    }

    private static void exibirInstrucoes(Missao missao) {
        Dificuldade d = missao.getDificuldade();
        System.out.println();
        System.out.println("================================================================");
        System.out.println("Bem-vindo à Missão Marte Unifor! Sua nave foi selecionada para uma");
        System.out.println("expedição de resgate e pesquisa na superfície marciana.");
        System.out.println();
        System.out.println("Objetivo:");
        System.out.println(" - Mover a nave pelo mapa e embarcar TODOS os passageiros");
        System.out.println(" - Evitar asteroides e o alienígena (cada colisão custa 1 vida)");
        System.out.println(" - Com todos a bordo, voltar à Plataforma de Pouso L em ("
                + Missao.PLATAFORMA_X + "," + Missao.PLATAFORMA_Y + ") para vencer");
        System.out.println(" - Manter a pontuação acima de zero");
        System.out.println();
        System.out.println("Comandos: w(cima) s(baixo) a(esquerda) d(direita) c(embarcar) q(abandonar)");
        System.out.printf("Dificuldade %s: %d vidas, %d pontos iniciais, %d passageiros.%n",
                d.getRotulo(), d.getRecursosIniciais(), d.getPontuacaoInicial(),
                missao.getTotalPassageiros());
        System.out.println("Cada movimento custa 1 ponto. Embarque: Professor +10, Engenheiro +15, Astronauta +20.");
        System.out.println("Pressione Enter para iniciar a missão...");
        SCANNER.nextLine();
    }

    private static void jogarPartida(Missao missao, String piloto, Ranking ranking) {
        Nave nave = missao.getNave();
        int pontuacao = missao.getDificuldade().getPontuacaoInicial();
        int movimentos = 0;
        long inicio = System.currentTimeMillis();
        boolean vitoria = false;
        boolean avisouPlataforma = false;
        boolean emJogo = true;

        while (emJogo) {
            desenharMapa(missao, pontuacao, piloto);
            exibirStatus(missao, pontuacao);

            if (missao.todosEmbarcados() && !avisouPlataforma) {
                System.out.println("\n✅ Todos os passageiros estão a bordo!");
                System.out.printf("🛬 Retorne à Plataforma de Pouso L em (%d,%d) para concluir a missão.%n",
                        Missao.PLATAFORMA_X, Missao.PLATAFORMA_Y);
                avisouPlataforma = true;
            }

            System.out.print("\nPara onde ir? ");
            String linha = SCANNER.nextLine().trim().toLowerCase();
            if (linha.isEmpty()) continue;
            char comando = linha.charAt(0);

            if (comando == 'q') {
                System.out.println("Missão abandonada pelo piloto.");
                break;
            }

            if (comando == 'c') {
                pontuacao += tentarEmbarcar(missao);
            } else if (comando == 'w' || comando == 'a' || comando == 's' || comando == 'd') {
                int xAnterior = nave.getX();
                int yAnterior = nave.getY();
                boolean moveu = nave.mover(comando, missao.getMinX(), missao.getMaxX(),
                        missao.getMinY(), missao.getMaxY());
                if (!moveu) {
                    System.out.println("🚧 A borda do mapa bloqueou o movimento. Nenhum ponto foi gasto.");
                    continue;
                }
                movimentos++;
                pontuacao--;

                if (missao.verificaColisao()) {
                    nave.perderVida();
                    nave.reposicionar(xAnterior, yAnterior);
                    System.out.println("💥 BOOM! Você colidiu com um asteroide e perdeu uma vida!");
                    if (nave.estaDestruida()) {
                        System.out.println("Sua nave foi totalmente destruída! Missão fracassada.");
                        break;
                    }
                    System.out.println("⚠️ A nave recuou para a posição anterior. Vidas restantes: " + nave.getVidas());
                }
            } else {
                System.out.println("Comando desconhecido. Use w, a, s, d, c ou q.");
                continue;
            }

            missao.moverInimigos(RANDOM);
            Inimigo inimigo = missao.inimigoEmColisao();
            if (inimigo != null) {
                nave.perderVida();
                System.out.println("🚨 ALERTA! " + inimigo.getNome() + " atacou a nave!");
                if (nave.estaDestruida()) {
                    System.out.println("Sua nave foi destruída pelo alienígena! Missão fracassada.");
                    break;
                }
                System.out.println("⚠️ Fuja, o alienígena está na sua cola! Vidas restantes: " + nave.getVidas());
            }

            if (pontuacao <= 0) {
                System.out.println("⛽ Combustível esgotado (pontuação zerada). Missão perdida.");
                break;
            }

            if (missao.missaoConcluida()) {
                System.out.println("\n🛬 POUSO CONFIRMADO NA PLATAFORMA (0,0)!");
                System.out.println("Missão concluída com sucesso, piloto " + piloto + "!");
                vitoria = true;
                emJogo = false;
            }
        }

        long duracao = (System.currentTimeMillis() - inicio) / 1000;
        RegistroPartida registro = new RegistroPartida(piloto, Math.max(pontuacao, 0),
                nave.getPassageiros().size(), missao.getTotalPassageiros(),
                missao.getDificuldade(), duracao, movimentos, vitoria);

        exibirEstatisticas(registro);
        registrarResultado(registro, ranking);
        exibirRanking(ranking);
    }

    private static int tentarEmbarcar(Missao missao) {
        Passageiro p = missao.passagemNaPosicao();
        if (p == null) {
            System.out.println("Nenhum passageiro nesta posição.");
            return 0;
        }
        if (!missao.embarcarPassageiroNaPosicao()) {
            System.out.println("Nave cheia, não foi possível embarcar.");
            return 0;
        }
        System.out.printf("✅ %s (%s) embarcado. +%d pontos!%n",
                p.getNome(), p.getTipo(), p.calcularPontuacao());
        return p.calcularPontuacao();
    }

    private static void exibirStatus(Missao missao, int pontuacao) {
        Nave nave = missao.getNave();
        System.out.printf("Nave em (%d,%d) | Vidas: %d | Pontos: %d | A bordo: %d/%d | Restantes no mapa: %d%n",
                nave.getX(), nave.getY(), nave.getVidas(), pontuacao,
                nave.getPassageiros().size(), nave.getCapacidade(), missao.getPassageiros().size());

        // O símbolo da nave cobre o do passageiro no mapa, então avisamos por texto.
        Passageiro aqui = missao.passagemNaPosicao();
        if (aqui != null) {
            System.out.printf("👆 %s (%s) está nesta casa. Digite 'c' para embarcar (+%d).%n",
                    aqui.getNome(), aqui.getTipo(), aqui.calcularPontuacao());
        }
    }

    private static void exibirEstatisticas(RegistroPartida registro) {
        System.out.println();
        System.out.println("------------- ESTATÍSTICAS DA PARTIDA -------------");
        System.out.printf(" Piloto................: %s%n", registro.getPiloto());
        System.out.printf(" Resultado.............: %s%n", registro.isVitoria() ? "VITÓRIA" : "DERROTA");
        System.out.printf(" Dificuldade...........: %s%n", registro.getDificuldade().getRotulo());
        System.out.printf(" Pontuação final.......: %d%n", registro.getPontuacao());
        System.out.printf(" Passageiros resgatados: %d de %d%n",
                registro.getPassageirosResgatados(), registro.getTotalPassageiros());
        System.out.printf(" Movimentos realizados.: %d%n", registro.getMovimentos());
        System.out.printf(" Duração da partida....: %d segundos%n", registro.getDuracaoSegundos());
        System.out.printf(" Data/hora.............: %s%n", registro.getDataHora());
        System.out.println("---------------------------------------------------");
    }

    private static void registrarResultado(RegistroPartida registro, Ranking ranking) {
        if (!registro.isVitoria()) {
            System.out.println("Só missões concluídas na plataforma de pouso entram no ranking.");
            return;
        }
        int recordeAnterior = ranking.maiorPontuacao();
        if (ranking.entraNoTop(registro.getPontuacao())) {
            ranking.registrar(registro);
            System.out.println("💾 Partida salva em " + ranking.getArquivo() + " — você entrou no Top 5!");
            if (registro.getPontuacao() > recordeAnterior) {
                System.out.println("🏆 NOVO RECORDE DO SERVIDOR! Parabéns, " + registro.getPiloto() + "!");
            }
        } else {
            System.out.println("Pontuação insuficiente para entrar no Top 5. Tente novamente!");
        }
    }

    private static void desenharMapa(Missao missao, int pontuacao, String piloto) {
        System.out.println();
        System.out.printf("Mapa da Missão (Pontos: %d) - Piloto: %s%n", pontuacao, piloto);
        System.out.print("    ");
        for (int x = missao.getMinX(); x <= missao.getMaxX(); x++) {
            System.out.printf(" %2d", x);
        }
        System.out.println();
        System.out.print("    ");
        for (int x = missao.getMinX(); x <= missao.getMaxX(); x++) {
            System.out.print(" __");
        }
        System.out.println();

        for (int y = missao.getMinY(); y <= missao.getMaxY(); y++) {
            System.out.printf("%3d|", y);
            for (int x = missao.getMinX(); x <= missao.getMaxX(); x++) {
                System.out.printf(" %2c", simboloDaCasa(missao, x, y));
            }
            System.out.println();
        }

        System.out.println("\nLegenda: ∆=Nave, L=Plataforma de Pouso, §=Inimigo, "
                + "P=Professor (+10), E=Engenheiro (+15), A=Astronauta (+20), #=Asteroide, .=Vazio");
        System.out.println("Comandos: w(cima)/s(baixo)/a(esquerda)/d(direita), c embarcar, q abandonar");

        if (!missao.getPassageiros().isEmpty()) {
            System.out.println("Passageiros restantes:");
            for (Passageiro p : missao.getPassageiros()) {
                System.out.printf(" - %s (%s) em (%d,%d) vale +%d%n",
                        p.getNome(), p.getTipo(), p.getX(), p.getY(), p.calcularPontuacao());
            }
        }
    }

    private static char simboloDaCasa(Missao missao, int x, int y) {
        if (missao.getNave().estaNaPosicao(x, y)) return '∆';
        for (Inimigo i : missao.getInimigos()) {
            if (i.estaNaPosicao(x, y)) return i.getSimbolo();
        }
        for (Passageiro p : missao.getPassageiros()) {
            if (p.estaNaPosicao(x, y)) return p.getSimbolo();
        }
        for (Asteroide a : missao.getAsteroides()) {
            if (a.estaNaPosicao(x, y)) return a.getSimbolo();
        }
        if (x == Missao.PLATAFORMA_X && y == Missao.PLATAFORMA_Y) return 'L';
        return '.';
    }

    private static int lerInteiro(String mensagem, int minimo, int maximo) {
        while (true) {
            System.out.print(mensagem);
            String entrada = SCANNER.nextLine().trim();
            try {
                int valor = Integer.parseInt(entrada);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
            } catch (NumberFormatException ignored) {
                // texto não numérico cai no mesmo aviso de faixa inválida
            }
            System.out.printf("Entrada inválida. Informe um número entre %d e %d.%n", minimo, maximo);
        }
    }

    private static void exibirAnimacaoAbertura() {
        String[] logo = {
            "█   █ ███  ████  ████  ███   ███     █   █  ███  ████  █████ █████",
            "██ ██  █  █     █     █   █ █   █    ██ ██ █   █ █   █   █   █    ",
            "█ █ █  █   ███   ███  █████ █   █    █ █ █ █████ ████    █   ████ ",
            "█   █  █      █     █ █   █ █   █    █   █ █   █ █  █    █   █    ",
            "█   █ ███ ████  ████  █   █  ███     █   █ █   █ █   █   █   █████"
        };

        try {
            System.out.println("\n[SISTEMA] Estabelecendo conexão com a base...\n");
            Thread.sleep(800);
            for (String linha : logo) {
                System.out.println(linha);
                Thread.sleep(150);
            }
            System.out.println("\n      >>> BEM-VINDO À UNIFOR - BASE DE MARTE <<<\n");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Erro ao carregar a abertura.");
        }
    }
}
