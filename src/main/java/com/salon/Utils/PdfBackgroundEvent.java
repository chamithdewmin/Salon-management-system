package com.salon.Utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class PdfBackgroundEvent extends PdfPageEventHelper {
    private final BaseColor backgroundColor = new BaseColor(18, 18, 18); // #121212

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        Rectangle rect = document.getPageSize();
        PdfContentByte canvas = writer.getDirectContentUnder();
        canvas.saveState();
        canvas.setColorFill(backgroundColor);
        canvas.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight());
        canvas.fill();
        canvas.restoreState();
    }
}
