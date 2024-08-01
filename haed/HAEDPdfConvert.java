package haed;

import common.entity.ChartInfo;
import common.entity.Flosses;
import common.entity.PaletteItem;
import common.entity.Stitch;
import common.util.OSXFileBuilder;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HAEDPdfConvert {
    private static final String BRAND_NAME = "Heaven and Earth Designs";
    private static final int NUMBER_OF_GRID_PER_PAGE_X = 80;
    private static final int NUMBER_OF_GRID_PER_PAGE_Y = 98;
    private static final int NUMBER_OF_SKIP_GRID_PER_PAGE_X = 3;
    private static final int NUMBER_OF_SKIP_GRID_PER_PAGE_Y = 3;
    private static final int PAGE_NO_OF_PALETTE_INFO_START = 3;

    private int numberOfAllPages;
    private int numberOfPalettePages;
    private int numberOfPatternPages;
    private int numberOfPatternPagesX;
    private int numberOfPatternPagesY;
    private int numberOfGridOnLastPageX;
    private int numberOfGridOnLastPageY;

    private Map<String, Integer> symbolIndexMap = new HashMap<>();

    private static String[] getString(PDDocument page) throws IOException {
        return new HAEDPDFTextStripper().getText(page).split("\n");
    }

    public void convertPdfToOsxFormatFile(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            List<PDDocument> pages = new Splitter().split(document);
            ChartInfo chartInfo = extractChartInfo(pages.get(0));
            analyzePdf(pages, chartInfo);
            List<PaletteItem> paletteItems = extractPaletteInfo(pages.subList(PAGE_NO_OF_PALETTE_INFO_START -1, PAGE_NO_OF_PALETTE_INFO_START +numberOfPalettePages-1));
            List<Stitch> fullStitches = extractFullStitches(pages.subList(numberOfAllPages - numberOfPatternPages, pages.size()));
            new OSXFileBuilder().createOSXFile(chartInfo, paletteItems, fullStitches);
//
            //PDFTextStripper stripper = new PDFTextStripper();
//            haed.HAEDPDFTextStripper stripper = new haed.HAEDPDFTextStripper();
//
//            String text = stripper.getText(document);
//            stripper.getAllFontsUsed().forEach(System.out::println);
//            System.out.println(text);
        }
    }

    private void analyzePdf(List<PDDocument> pages, ChartInfo chartInfo) {
        this.numberOfAllPages = pages.size();
        this.numberOfPatternPagesX = calculateChartPages(chartInfo.getChartWidth(), NUMBER_OF_GRID_PER_PAGE_X, NUMBER_OF_SKIP_GRID_PER_PAGE_X);
        this.numberOfPatternPagesY = calculateChartPages(chartInfo.getChartHeight(), NUMBER_OF_GRID_PER_PAGE_Y, NUMBER_OF_SKIP_GRID_PER_PAGE_Y);
        this.numberOfPatternPages = numberOfPatternPagesX * numberOfPatternPagesY;
        this.numberOfPalettePages = (numberOfAllPages - numberOfPatternPages - 2) / 2;

        if(chartInfo.getChartWidth() > NUMBER_OF_GRID_PER_PAGE_X) {
            this.numberOfGridOnLastPageX = (chartInfo.getChartWidth() - NUMBER_OF_GRID_PER_PAGE_X)
                    % (NUMBER_OF_GRID_PER_PAGE_X - NUMBER_OF_SKIP_GRID_PER_PAGE_X)
                    + NUMBER_OF_SKIP_GRID_PER_PAGE_X;
        } else {
            this.numberOfGridOnLastPageX = chartInfo.getChartWidth();
        }

        if(chartInfo.getChartHeight() > NUMBER_OF_GRID_PER_PAGE_Y) {
            this.numberOfGridOnLastPageY = (chartInfo.getChartHeight() - NUMBER_OF_GRID_PER_PAGE_Y)
                    % (NUMBER_OF_GRID_PER_PAGE_Y - NUMBER_OF_SKIP_GRID_PER_PAGE_Y)
                    + NUMBER_OF_SKIP_GRID_PER_PAGE_Y;
        } else {
            this.numberOfGridOnLastPageY = chartInfo.getChartHeight();
        }

        // Log
        System.out.println("[analyzePdf] number of all pages: " + this.numberOfAllPages);
        System.out.println("[analyzePdf] number of palette pages: " + this.numberOfPalettePages);
        System.out.println("[analyzePdf] number of pattern pages: " + this.numberOfPatternPages);
        System.out.println("[analyzePdf] number of pages(x) : " + this.numberOfPatternPagesX);
        System.out.println("[analyzePdf] number of pages(y) : " + this.numberOfPatternPagesY);
    }

    private int calculateChartPages(int allGrids, int maxGridsPerPage, int skipGrids){
        int numOfPages = 1;
        allGrids = allGrids - maxGridsPerPage;
        while(allGrids > 0){
            numOfPages++;
            allGrids = allGrids - (maxGridsPerPage - skipGrids);
        }
        return numOfPages;
    }

    private ChartInfo extractChartInfo(PDDocument page) throws IOException {
        String[] text = getString(page);

        ChartInfo chartInfo = new ChartInfo();
        chartInfo.setBrandName(text[0]);
        chartInfo.setChartName(text[1]);
        chartInfo.setChartNo(text[2].replace("Chart No:  ", ""));
        chartInfo.setDesignedBy(text[3].replace("Chart design by ", ""));

        System.out.println(text[5]);
        Pattern sizePattern = Pattern.compile("Finished Design Size\\s+(\\d+) .*by (\\d+) .*\\(((\\d+)|(\\d+-\\d+/\\d+)) W X ((\\d+)|(\\d+-\\d+/\\d+)) H inches on (\\d+ct) fabric\\)");
        Matcher sizeMatcher = sizePattern.matcher(text[5]);
        if(sizeMatcher.find()) {
            chartInfo.setChartWidth(Integer.parseInt(sizeMatcher.group(1)));
            chartInfo.setChartHeight(Integer.parseInt(sizeMatcher.group(2)));
            chartInfo.setFabricWidth(sizeMatcher.group(3));
            chartInfo.setFabricHeight(sizeMatcher.group(6));
            chartInfo.setFabricCount(sizeMatcher.group(9));
        }

        Pattern colorPattern = Pattern.compile("\\(This Chart Contains (\\d+) Colors\\) ");
        Matcher colorMatcher = colorPattern.matcher(text[6]);
        if(colorMatcher.find()) {
            chartInfo.setNumberOfColors(Integer.parseInt(colorMatcher.group(1)));
        }

        chartInfo.setCopyright(text[text.length-1].replace("Copyright ", ""));

        return chartInfo;
    }

    private List<PaletteItem> extractPaletteInfo(List<PDDocument> pages) throws IOException {
        List<PaletteItem> paletteItems = new ArrayList<>();
        Flosses flosses = new Flosses();
        for (PDDocument page: pages) {
            List<String> text = Arrays.asList(getString(page));
            int index = text.indexOf("Symbol Strands Type Number Color");
            while(++index < text.size()) {
                Pattern palettePattern = Pattern.compile("([^ ]) (\\d) ([^ ]+) ([^ ]+) (.+)");
                Matcher paletteMatcher = palettePattern.matcher(text.get(index));
                if(!paletteMatcher.find()) break;

                PaletteItem paletteItem = new PaletteItem(paletteItems.size()+1
                        , paletteMatcher.group(1)
                        , Integer.parseInt(paletteMatcher.group(2))
                        , paletteMatcher.group(3)
                        , paletteMatcher.group(4)
                        , paletteMatcher.group(5)
                        , flosses.getColorCode(paletteMatcher.group(3) + " " +paletteMatcher.group(4)));
                paletteItems.add(paletteItem);

                System.out.println("[extractPaletteInfo] " + paletteItem.toString());
                if(symbolIndexMap.containsKey(paletteItem.getSymbol())){
                    int existItemIndex = symbolIndexMap.get(paletteItem.getSymbol());
                    System.out.println("[extractPaletteInfo - override] " + paletteItems.get(existItemIndex-1).toString());
                }
                symbolIndexMap.put(paletteItem.getSymbol(), paletteItem.getIndex());

            }
        }
        return paletteItems;
    }

    private List<Stitch> extractFullStitches(List<PDDocument> pages) throws IOException {
        List<Stitch> stitches = new ArrayList<>();

        int x = 0;
        int y = 0;
        int offsetX = 0;
        int offsetY = 0;
        for(int pageY = 0; pageY < numberOfPatternPagesY; pageY++) {
            int numberOfGridCurrentPageY = NUMBER_OF_GRID_PER_PAGE_Y;
            if(pageY == numberOfPatternPagesY - 1) {
                numberOfGridCurrentPageY = numberOfGridOnLastPageY;
            }

            for(int pageX = 0; pageX < numberOfPatternPagesX; pageX++) {
                int pageNo = pageY * numberOfPatternPagesX + pageX;
                System.out.println("[extractFullStitches page-" + (pageNo + 1) +"] offsetX: " + offsetX + ", offsetY: " + offsetY);
                String[] text = getString(pages.get(pageNo));

                int startIndex = 2;
                Pattern startRowPattern = Pattern.compile("(\\(\\d+\\))+");
                if(!startRowPattern.matcher(text[1]).find()) {
                    startIndex = 1;
                }

                int numberOfNonSkippedGridCurrentPageY = numberOfGridCurrentPageY;
                int stitchCnt = 0;
                if(pageX > 0) {
                    startIndex += numberOfGridCurrentPageY;
                }
                if(pageY > 0) {
                    startIndex += NUMBER_OF_SKIP_GRID_PER_PAGE_X;
                    numberOfNonSkippedGridCurrentPageY -= NUMBER_OF_SKIP_GRID_PER_PAGE_Y;
                }

                y = offsetY;
                for(int i = 0; i < numberOfNonSkippedGridCurrentPageY; i++){
                    x = offsetX;
                    String str = text[startIndex+i].replace(" ", "");
                    for (String symbol : str.split("")) {
                        if(null == symbolIndexMap.get(symbol)){
                            String symbolCode = String.format("\\u%4s", Integer.toHexString(symbol.codePointAt(0))).replace(" ", "0");
                            System.out.println("[extractFullStitches page-" + (pageNo + 1) +"] can not find symbol index:  " + symbolCode);
                        }
                        Stitch stitch = new Stitch(x++, y, symbol, false, symbolIndexMap.get(symbol));
                        stitches.add(stitch);
                        stitchCnt++;

                    }
                    y++;
                }
                offsetX = x;
                System.out.println("[extractFullStitches] stitches: " + stitchCnt);
            }
            offsetX = 0;
            offsetY = y;
        }
        return stitches;
    }
}

