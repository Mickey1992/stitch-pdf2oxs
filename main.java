import haed.HAEDPdfConvert;

import java.io.File;
import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException {
        // Supersized The Butterfly Ball MC Chart Pack-1
        // Marvelous_Garden_Chart_Pack.pdf
        new HAEDPdfConvert().convertPdfToOsxFormatFile(new File("./pdf-charts/Supersized The Butterfly Ball MC Chart Pack-1.pdf"));

    }

}
