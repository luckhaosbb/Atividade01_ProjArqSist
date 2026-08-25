package missao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Missao {
    public static final int PLATAFORMA_X = 0;
    public static final int PLATAFORMA_Y = 0;

    private final Nave nave;
    private final Dificuldade dificuldade;
    private final int minX;
    private final int maxX;
    private final int minY;
    private final int maxY;

    private final List<Passageiro> passageiros = new ArrayList<>();
    private final List<Asteroide> asteroides = new ArrayList<>();
    private final List<Inimigo> inimigos = new ArrayList<>();

    private int totalPassageiros;

    public Missao(Nave nave, Dificuldade dificuldade, int minX, int maxX, int minY, int maxY) {
        this.nave = nave;
        this.dificuldade = dificuldade;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public Nave getNave() { return nave; }
    public Dificuldade getDificuldade() { return dificuldade; }
    public List<Passageiro> getPassageiros() { return passageiros; }
    public List<Asteroide> getAsteroides() { return asteroides; }
    public List<Inimigo> getInimigos() { return inimigos; }
    public int getMinX() { return minX; }
    public int getMaxX() { return maxX; }
    public int getMinY() { return minY; }
    public int getMaxY() { return maxY; }
    public int getTotalPassageiros() { return totalPassageiros; }

    public void addPassageiro(Passageiro p) {
        passageiros.add(p);
        totalPassageiros++;
    }

    public void addAsteroide(Asteroide a) { asteroides.add(a); }
    public void addInimigo(Inimigo i) { inimigos.add(i); }

    public boolean verificaColisao() {
        for (Asteroide a : asteroides) {
            if (a.colideCom(nave)) return true;
        }
        return false;
    }

    public void moverInimigos(Random random) {
        for (Inimigo i : inimigos) {
            i.mover(random, minX, maxX, minY, maxY);
        }
    }

    public Inimigo inimigoEmColisao() {
        for (Inimigo i : inimigos) {
            if (i.colideCom(nave)) return i;
        }
        return null;
    }

    public Passageiro passagemNaPosicao() {
        for (Passageiro p : passageiros) {
            if (p.estaNaPosicao(nave.getX(), nave.getY())) return p;
        }
        return null;
    }

    public boolean embarcarPassageiroNaPosicao() {
        Iterator<Passageiro> it = passageiros.iterator();
        while (it.hasNext()) {
            Passageiro p = it.next();
            if (p.estaNaPosicao(nave.getX(), nave.getY())) {
                boolean ok = nave.embarcar(p);
                if (ok) it.remove();
                return ok;
            }
        }
        return false;
    }

    public boolean todosEmbarcados() { return passageiros.isEmpty(); }

    public boolean naveNaPlataforma() {
        return nave.estaNaPosicao(PLATAFORMA_X, PLATAFORMA_Y);
    }

    public boolean missaoConcluida() {
        return todosEmbarcados() && naveNaPlataforma();
    }
}
