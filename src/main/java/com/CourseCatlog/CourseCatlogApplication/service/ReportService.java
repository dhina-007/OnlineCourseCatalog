package com.CourseCatlog.CourseCatlogApplication.service;

import com.CourseCatlog.CourseCatlogApplication.dto.CourseResponse;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final CourseService courseService;

    public byte[] generateAllCoursesPdf() {
        List<CourseResponse> all = courseService.list(null, null, 0, Integer.MAX_VALUE).getContent();
        try {
            Document doc = new Document(PageSize.A4.rotate());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(doc, baos);
            doc.open();
            doc.add(new Paragraph("All Available Courses"));
            doc.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(6);
            table.addCell("ID");
            table.addCell("Title");
            table.addCell("Category");
            table.addCell("Instructor");
            table.addCell("Language");
            table.addCell("Created At");
            for (CourseResponse c : all) {
//                table.addCell(String.valueOf(c.getId()));
//                table.addCell(c.getTitle());
//                table.addCell(c.getCategory());
//                table.addCell(c.getInstructor());
//                table.addCell(c.getLanguage());
//                table.addCell(String.valueOf(c.getCreatedAt()));
                table.addCell(c.id().toString());
                table.addCell(c.title());
                table.addCell(c.category());
                table.addCell(c.instructor());
                table.addCell(c.language());
                table.addCell(c.createdAt().toString());

            }
            doc.add(table);
            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }
}