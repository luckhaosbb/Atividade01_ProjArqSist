package missao;

import java.util.ArrayList;
import java.util.List;

public class Nave {
    public static final int CAPACIDADE_PADRAO = 5;

    private String id;
    private int x;
    private int y;
    private int capacidade;
    private int vidas;
    private List<Passageiro> passageiros = new ArrayList<>();

    public Nave(String id, int capacidade, int vidas, int x, int y) {
        this.id = id;
        this.capacidade = capacidade;
        this.vidas = vidas;
        this.x = x;
        this.y = y;
    }

    public String getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getCapacidade() { return capacidade; }
    public int getVidas() { return vidas; }
    public List<Passageiro> getPassageiros() { return passageiros; }

    /** @return false se a borda bloqueou o movimento — nesse caso não custa combustível. */
    public boolean mover(char direcao, int minX, int maxX, int minY, int maxY) {
        int novoX = x;
        int novoY = y;
        switch (direcao) {
            case 'w': novoY = y - 1; break;
            case 's': novoY = y + 1; break;
            case 'a': novoX = x - 1; break;
            case 'd': novoX = x + 1; break;
            default: return false;
        }
        if (novoX < minX || novoX > maxX || novoY < minY || novoY > maxY) {
            return false;
        }
        this.x = novoX;
        this.y = novoY;
        return true;
    }

    public void reposicionar(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean embarcar(Passageiro p) {
        if (passageiros.size() < capacidade) {
            passageiros.add(p);
            return true;
        }
        return false;
    }

    public void perderVida() {
        this.vidas--;
    }

    public boolean estaDestruida() {
        return this.vidas <= 0;
    }

    public boolean estaNaPosicao(int x, int y) {
        return this.x == x && this.y == y;
    }
}
