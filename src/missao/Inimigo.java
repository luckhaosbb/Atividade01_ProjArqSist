package missao;

import java.util.Random;

public class Inimigo {
    private String nome;
    private int x;
    private int y;

    public Inimigo(String nome, int x, int y) {
        this.nome = nome;
        this.x = x;
        this.y = y;
    }

    public String getNome() { return nome; }
    public int getX() { return x; }
    public int getY() { return y; }

    public char getSimbolo() { return '§'; }

    public void mover(Random random, int minX, int maxX, int minY, int maxY) {
        switch (random.nextInt(4)) {
            case 0: y = Math.max(minY, y - 1); break;
            case 1: y = Math.min(maxY, y + 1); break;
            case 2: x = Math.max(minX, x - 1); break;
            default: x = Math.min(maxX, x + 1); break;
        }
    }

    public boolean colideCom(Nave nave) {
        return nave.getX() == x && nave.getY() == y;
    }

    public boolean estaNaPosicao(int x, int y) {
        return this.x == x && this.y == y;
    }
}
