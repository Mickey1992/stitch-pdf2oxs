package haed;

import common.entity.CharWithFont;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class HAEDPDFTextStripper extends PDFTextStripper {
    private static Map<CharWithFont, Character> charConvertMapping = new HashMap<>();
    private static Set<Integer> usedCharCode = new HashSet<>();
    private static int newCharCode = 0xffff;
    private boolean addNewCharCode;
    public HAEDPDFTextStripper() throws IOException {
        super();
    }

    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
        for (TextPosition textPosition : textPositions) {
            int charCode = textPosition.getCharacterCodes()[0];
            CharWithFont charWithFont = new CharWithFont(charCode, textPosition.getFont().getName());
            char outputChar = textPosition.getUnicode().charAt(0);
            if(charConvertMapping.containsKey(charWithFont)){
                outputChar = charConvertMapping.get(charWithFont);
            } else {
                outputChar = getNewChar(charWithFont);
                charConvertMapping.put(charWithFont, outputChar);
            }

            writeString(String.valueOf(outputChar));
        }
    }

    private char getNewChar(CharWithFont oldChar) {
        int oldCharCode = oldChar.getCharCode();
        if(!usedCharCode.contains(oldCharCode)){
            usedCharCode.add(oldCharCode);
            return oldChar.getChar();
        }

        while(usedCharCode.contains(newCharCode)) {
            newCharCode--;
        }
        usedCharCode.add(newCharCode);
        System.out.println(String.format("[convert duplicated chars]%s -> \\u%s", oldChar, Integer.toHexString(newCharCode)));

        return (char) newCharCode;
    }

    public Set<CharWithFont> getAllCharsUsed() {
        return charConvertMapping.keySet();
    }

    public List<String> getAllFontsUsed() {
        return charConvertMapping.keySet().stream().map(CharWithFont::getFontName).distinct().collect(Collectors.toList());
    }
}
