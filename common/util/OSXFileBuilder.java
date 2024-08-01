package common.util;

import common.entity.ChartInfo;
import common.entity.PaletteItem;
import common.entity.Stitch;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

public class OSXFileBuilder {
    Document document;

    public OSXFileBuilder() {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        document = documentBuilder.newDocument();
    }
    public void createOSXFile(ChartInfo chartInfo, List<PaletteItem> paletteItems, List<Stitch> fullStitches){
        Element chartElement = document.createElement("chart");
        appendPropertiesElement(chartElement, chartInfo);
        appendPaletteElement(chartElement, paletteItems);
        appendFullStitchElement(chartElement, fullStitches);
        document.appendChild(chartElement);

        writeXml("test.oxs");
    }

    private void appendPropertiesElement(Element rootElement, ChartInfo chartInfo) {
        Element propertiesElement = document.createElement("properties");
        propertiesElement.setAttribute("chatTitle", chartInfo.getChartName());
        propertiesElement.setAttribute("author", chartInfo.getDesignedBy());
        propertiesElement.setAttribute("chartwidth", String.valueOf(chartInfo.getChartWidth()));
        propertiesElement.setAttribute("chartheight", String.valueOf(chartInfo.getChartHeight()));
        propertiesElement.setAttribute("copyright", chartInfo.getCopyright());
        propertiesElement.setAttribute("palettecount", String.valueOf(chartInfo.getNumberOfColors()));
        rootElement.appendChild(propertiesElement);
    }
    private void appendPaletteElement(Element rootElement, List<PaletteItem> paletteItems) {
        Element paletteElement = document.createElement("palette");
        paletteItems.stream().forEach(item -> {
            Element paletteItemElement = document.createElement("palette_item");
            paletteItemElement.setAttribute("index", String.valueOf(item.getIndex()));
            paletteItemElement.setAttribute("number", item.getFlossBrand() + " " + item.getFlossNo());
            paletteItemElement.setAttribute("name", item.getColorName());
            paletteItemElement.setAttribute("color", item.getColorCode());
            paletteItemElement.setAttribute("strands", String.valueOf(item.getStrands()));
            paletteElement.appendChild(paletteItemElement);
        });
        rootElement.appendChild(paletteElement);
    }

    private void appendClothInfo(Element paletteElement) {
        Element paletteItemElement = document.createElement("palette_item");
        paletteItemElement.setAttribute("index", "0");
        paletteItemElement.setAttribute("number", "cloth");
        paletteItemElement.setAttribute("name", "cloth");
        paletteItemElement.setAttribute("color", "FFFFFF");
        paletteItemElement.setAttribute("strands", "2");
        paletteElement.appendChild(paletteItemElement);
    }

    private void appendFullStitchElement(Element rootElement, List<Stitch> stitches) {
        Element fullStitchesElement = document.createElement("fullstitches");
        stitches.stream().forEach(stitch -> {
            Element stitchElement = document.createElement("stitch");
            stitchElement.setAttribute("x", String.valueOf(stitch.getX()));
            stitchElement.setAttribute("y", String.valueOf(stitch.getY()));
            stitchElement.setAttribute("palindex", String.valueOf(stitch.getPaletteIndex()));
            stitchElement.setAttribute("marked", String.valueOf(stitch.isMarked()));
            fullStitchesElement.appendChild(stitchElement);
        });
        rootElement.appendChild(fullStitchesElement);
    }

    private void writeXml(String fileName) {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();

            // pretty print
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new FileOutputStream(fileName));

            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }


    }
}
