package bourdoulous.fr.mylibrary.Utilities;

import android.os.Environment;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class PDFtemplate {
    private File file;
    private Document document;
    private Paragraph paragraph;
    private Font font_title = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private Font font_date = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC);
    private Font font_text = new Font(Font.FontFamily.TIMES_ROMAN, 11);
    private Font font_stats = new Font(Font.FontFamily.TIMES_ROMAN, 11,Font.UNDERLINE);
    private Font font_part = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);
    private Font font_header = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD,BaseColor.WHITE);


    public void openDocument(String fileName) {
        create_file(fileName);
        try {
            document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
        } catch (DocumentException | FileNotFoundException e) {
            Log.e("PDF", e.getMessage());
        }
    }

    private void create_file(String fileName) {
        File directory = new File(Environment.getExternalStorageDirectory(), "PDF");

        if (!directory.exists()) {
            directory.mkdirs();
        }
        file = new File(directory, fileName);
    }

    public void closeDocument() {
        document.close();
    }

    public void addDocumentData(String title, String subject, String author) {
        document.addTitle(title);
        document.addSubject(subject);
        document.addAuthor(author);
    }

    public void addTitles(String title, String date) {
        try {
            paragraph = new Paragraph();
            addChildParagraph(new Paragraph(title, font_title));
            addChildParagraph(new Paragraph(date, font_date));
            paragraph.setSpacingAfter(30);
            document.add(paragraph);
        } catch (DocumentException e) {
            Log.e("PDF", e.getMessage());
        }
    }

    private void addChildParagraph(Paragraph childParagraph) {
        childParagraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(childParagraph);
    }

    public void addParagraph(String text) {
        try {
            paragraph = new Paragraph(text, font_text);
            paragraph.setSpacingAfter(5);
            paragraph.setSpacingBefore(5);
            document.add(paragraph);
        } catch (DocumentException e) {
            Log.e("PDF", e.getMessage());
        }
    }

    public void setSpacingAfter(int spacingAfter) {
        try {
            paragraph = new Paragraph("", null);
            paragraph.setSpacingAfter(spacingAfter);
            document.add(paragraph);
        } catch (DocumentException e) {
            Log.e("PDF", e.getMessage());
        }
    }

    public void createTable(String[] header, ArrayList<String[]> clients) {
        try {
            //header
            paragraph = new Paragraph();
            paragraph.setFont(font_header);
            PdfPTable table = new PdfPTable(header.length);
            table.setWidthPercentage(100);
            PdfPCell cell;
            int i = 0;
            while (i < header.length) {
                cell = new PdfPCell(new Phrase(header[i]));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(new BaseColor(0, 180, 190));
                table.addCell(cell);
                i++;
            }
            paragraph.add(table);
            document.add(paragraph);
            // Content
            paragraph = new Paragraph();
            paragraph.setFont(font_text);
            table = new PdfPTable(header.length);
            table.setWidthPercentage(100);


            for (int j = 0; j < clients.size(); j++) {
                String[] row = clients.get(j);
                for (i = 0; i < row.length; i++) {
                    cell = new PdfPCell(new Phrase(row[i]));
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
            }

            paragraph.add(table);

            document.add(paragraph);
        } catch (DocumentException e) {
            Log.e("PDF", e.getMessage());
        }
    }

    public void addPart(String s) {
        try {
            paragraph = new Paragraph("   " + s, font_part);
            paragraph.setSpacingAfter(30);
            paragraph.setSpacingBefore(30);
            document.add(paragraph);
        } catch (DocumentException e) {
            Log.e("PDF", e.getMessage());
        }
    }

    public void addStats(String titleStat, String result) {
        try {
            Phrase title_phrase = new Phrase(titleStat,font_stats);
            paragraph = new Paragraph(result, font_text);

            paragraph.setSpacingAfter(50);
            document.add(title_phrase);
            document.add(paragraph);


        } catch (DocumentException e) {
            Log.e("PDF", e.getMessage());
        }
    }
}
