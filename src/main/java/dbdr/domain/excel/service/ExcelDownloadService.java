package dbdr.domain.excel.service;

import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ExcelDownloadService {

    public byte[] generateCareworkerTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("요양보호사");
            sheet.setDefaultColumnWidth(28);

            createHeaderRow(sheet, "요양원 ID", "성명", "이메일", "휴대폰 번호");
            setCellStyleText(workbook, sheet, 3);

            String[] sampleData = {"1", "홍길동", "hong@example.com", "01012345678"};
            createSampleData(sheet, sampleData);

            return convertWorkbookToByteArray(workbook);
        } catch (IOException e) {
            throw new ApplicationException(ApplicationError.FILE_DOWNLOAD_ERROR);
        }
    }

    public byte[] generateGuardianTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("보호자");
            sheet.setDefaultColumnWidth(28);

            createHeaderRow(sheet, "성명", "휴대폰 번호", "요양원 ID");
            setCellStyleText(workbook, sheet, 1);

            String[] sampleData = {"홍길동", "01012345678", "1"};
            createSampleData(sheet, sampleData);

            return convertWorkbookToByteArray(workbook);
        } catch (IOException e) {
            throw new ApplicationException(ApplicationError.FILE_DOWNLOAD_ERROR);
        }
    }

    public byte[] generateRecipientTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("돌봄대상자");
            sheet.setDefaultColumnWidth(28);

            createHeaderRow(sheet, "성명",  "생년월일", "성별", "장기요양등급", "장기요양인정번호", "시작일", "요양원 ID", "요양보호사 ID", "보호자 ID");
            setCellStyleDate(workbook, sheet, 1);
            setCellStyleText(workbook, sheet, 4);
            setCellStyleDate(workbook, sheet, 5);

            String[] sampleData = {"홍길동", "1990-01-01","남", "3등급" ,"L0000000000-102", "1990-01-01", "1", "1", "1"};
            createSampleData(sheet, sampleData);

            return convertWorkbookToByteArray(workbook);
        } catch (IOException e) {
            throw new ApplicationException(ApplicationError.FILE_DOWNLOAD_ERROR);
        }
    }

    private void setCellStyleText(Workbook workbook, Sheet sheet, int index) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        XSSFDataFormat xssfDataFormat = (XSSFDataFormat) workbook.createDataFormat();
        style.setDataFormat(xssfDataFormat.getFormat("@"));
        sheet.setDefaultColumnStyle(index, style);
    }

    private void setCellStyleDate(Workbook workbook, Sheet sheet, int index) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        XSSFDataFormat xssfDataFormat = (XSSFDataFormat) workbook.createDataFormat();
        style.setDataFormat(xssfDataFormat.getFormat("yyyy-MM-dd"));
        sheet.setDefaultColumnStyle(index, style);
    }

    private void createHeaderRow(Sheet sheet, String... headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    private void createSampleData(Sheet sheet, String[] sampleData) {
        Row bodyRow = sheet.createRow(1);
        for (int i = 0; i < sampleData.length; i++) {
            Cell bodyCell = bodyRow.createCell(i);
            bodyCell.setCellValue(sampleData[i]);
        }
    }

    private byte[] convertWorkbookToByteArray(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}