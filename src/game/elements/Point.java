package game.elements;

public class Point {
    private  int x;
    private  int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        return (((Point)obj).getX() == x )&& (((Point)obj).getY() == y);
    }
}
