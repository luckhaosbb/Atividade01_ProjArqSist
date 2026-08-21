package missao;
import java.util.Random;

public class Inimigo {
    private int x;
    private int y;
    private Random random = new Random();

    public Inimigo(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }

    public void mover(int limiteX, int limiteY) {
        int direcao = random.nextInt(4);
        
        switch (direcao) {
            case 0 -> y = Math.max(0, y - 1);
            case 1 -> y = Math.min(limiteY, y + 1);
            case 2 -> x = Math.max(0, x - 1);
            case 3 -> x = Math.min(limiteX, x + 1);
        }
    }

    public boolean verificarColisao(int getX, int getY) {
        return this.x == getX && this.y == getY;
    }

    
}