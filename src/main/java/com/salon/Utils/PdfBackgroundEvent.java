package com.salon.Utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Document;

public class PdfBackgroundEvent extends PdfPageEventHelper {
    private final BaseColor backgroundColor = new BaseColor(18, 18, 18); // Dark gray

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
