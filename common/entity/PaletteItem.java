package common.entity;

public class PaletteItem {
    private int index;
    private String symbol;
    private int strands;
    private String flossBrand;
    private String flossNo;
    private String colorName;
    private String colorCode;

    public PaletteItem(int index, String symbol, int strands, String flossBrand, String flossNo, String colorName, String colorCode) {
        this.index = index;
        this.symbol = symbol;
        this.strands = strands;
        this.flossBrand = flossBrand;
        this.flossNo = flossNo;
        this.colorName = colorName;
        this.colorCode = colorCode;
    }

    public String toString() {
        return String.format("index: %d, symbol: %s, strands: %d, floss-brand: %s, floss-no: %s, color-name: %s, color-code: %s, unicode: \\\\u%04x"
                , index, symbol, strands, flossBrand, flossNo, colorName, colorCode, (int) symbol.charAt(0));
    }

    public int getIndex() {
        return index;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getStrands() {
        return strands;
    }

    public String getFlossBrand() {
        return flossBrand;
    }

    public String getFlossNo() {
        return flossNo;
    }

    public String getColorName() {
        return colorName;
    }

    public String getColorCode() {
        return colorCode;
    }
}
