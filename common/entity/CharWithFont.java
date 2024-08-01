package common.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CharWithFont {
    private int charCode;
    private String fontName;

    private Map<Integer, Set<String>> SAME_CHARS = new HashMap<> () {{
        put(208, Set.of("HNIBGD+CrossStitch3", "HOFDGD+CrossStitch3"));
    }};

    public CharWithFont(int charCode, String fontName) {
        this.charCode = charCode;
        this.fontName = fontName;
    }

    public int getCharCode() {
        return charCode;
    }

    public String getFontName() {
        return fontName;
    }

    public char getChar() {
        return (char) charCode;
    }

    public String getUnicode() {
        return String.format("\\u%4s", Integer.toHexString(charCode)).replace(" ", "0");
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(!(obj instanceof CharWithFont)) return false;

        CharWithFont that = (CharWithFont) obj;
        if ((this.charCode == that.getCharCode()) && this.fontName.equals(that.getFontName())) return true;
        if(this.charCode == that.getCharCode()) return SAME_CHARS.get(this.charCode).containsAll(Set.of(this.fontName, that.getFontName()));
        return false;
    }

    @Override
    public int hashCode() {
        return SAME_CHARS.getOrDefault(charCode, Set.of(fontName)).stream().findFirst().get().hashCode() + charCode;
    }

    @Override
    public String toString() {
        return "common.entity.CharWithFont{" +
                "charCode=" + charCode +
                ", fontName='" + fontName + '\'' +
                '}';
    }
}
