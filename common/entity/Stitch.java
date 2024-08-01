package common.entity;

public class Stitch {
    private int x;
    private int y;
    private String symbol;
    private int paletteIndex;
    private boolean marked;

    public Stitch(int x, int y, String symbol, boolean marked, int paletteIndex) {
        this.x = x;
        this.y = y;
        this.symbol = symbol;
        this.marked = marked;
        this.paletteIndex = paletteIndex;
    }

    public String toString() {
        return String.format("x: %d, y: %d, symbol: %s, marked: %s", x, y, symbol, marked);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getPaletteIndex() {
        return paletteIndex;
    }

    public void setPaletteIndex(int paletteIndex) {
        this.paletteIndex = paletteIndex;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }
}
