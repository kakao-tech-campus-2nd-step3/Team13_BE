package dbdr.domain.excel.service;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.excel.dto.FileUploadResponseDto;
import dbdr.domain.excel.dto.FileDataResponseDto;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.domain.recipient.entity.Recipient;
import dbdr.domain.recipient.repository.RecipientRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private final CareworkerRepository careworkerRepository;
    private final GuardianRepository guardianRepository;
    private final RecipientRepository recipientRepository;

    public byte[] generateCareworkerTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("요양보호사");
            sheet.setDefaultColumnWidth(28);

            createHeaderRow(sheet, "성명", "휴대폰 번호");
            setCellStyleText(workbook, sheet, 1);

            String[] sampleData = {"홍길동", "01012345678"};
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

            createHeaderRow(sheet, "휴대폰 번호", "성명");
            setCellStyleText(workbook, sheet, 0);

            String[] sampleData = {"01012345678", "홍길동"};
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

            createHeaderRow(sheet, "성명", "장기요양인정번호", "생년월일");
            setCellStyleText(workbook, sheet, 1);
            setCellStyleDate(workbook, sheet, 2);

            String[] sampleData = {"홍길동", "L0000000000-102", "1990-01-01"};
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

    public FileUploadResponseDto uploadCareworkerExcel(MultipartFile file) {
        Set<String> seenPhones = new HashSet<>();
        return processExcelFile(file, (row, successList, failedList) -> processCareworkerRow(row, successList, failedList, seenPhones));
    }

    public FileUploadResponseDto uploadGuardianExcel(MultipartFile file) {
        Set<String> seenPhones = new HashSet<>();
        return processExcelFile(file, (row, successList, failedList) -> processGuardianRow(row, successList, failedList, seenPhones));
    }

    public FileUploadResponseDto uploadRecipientExcel(MultipartFile file) {
        Set<String> seenCareNumbers = new HashSet<>();
        return processExcelFile(file, (row, successList, failedList) -> processRecipientRow(row, successList, failedList, seenCareNumbers));
    }

    private FileUploadResponseDto processExcelFile(MultipartFile file, RowProcessor rowProcessor) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<FileDataResponseDto> successList = new ArrayList<>();
            List<FileDataResponseDto> failedList = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                try {
                    rowProcessor.process(row, successList, failedList);
                } catch (ApplicationException e) {

                }
            }
            return new FileUploadResponseDto(file.getOriginalFilename(), successList, failedList);
        } catch (IOException e) {
            throw new ApplicationException(ApplicationError.FILE_UPLOAD_ERROR);
        }
    }

    private void processCareworkerRow(Row row, List<FileDataResponseDto> successList, List<FileDataResponseDto> failedList, Set<String> seenPhones) {
        String name = getCellValue(row.getCell(0));
        String phone = getCellValue(row.getCell(1));

        try {
            checkDuplicate(seenPhones, phone, ApplicationError.DUPLICATE_PHONE);

            validatePhone(phone, careworkerRepository.existsByPhone(phone));

            seenPhones.add(phone);

            Careworker careworker = new Careworker(null, name, null, phone);
            careworkerRepository.save(careworker);  // DB에 저장

            successList.add(new FileDataResponseDto(name, phone));
        } catch (ApplicationException e) {
            failedList.add(new FileDataResponseDto(name, phone));
        }
    }

    private void processGuardianRow(Row row, List<FileDataResponseDto> successList, List<FileDataResponseDto> failedList, Set<String> seenPhones) {
        String phone = getCellValue(row.getCell(0));
        String name = getCellValue(row.getCell(1));

        try {
            checkDuplicate(seenPhones, phone, ApplicationError.DUPLICATE_PHONE);

            validatePhone(phone, guardianRepository.existsByPhone(phone));

            seenPhones.add(phone);

            Guardian guardian = new Guardian(phone, name);
            guardianRepository.save(guardian);

            successList.add(new FileDataResponseDto(phone, name));
        } catch (ApplicationException e) {
            failedList.add(new FileDataResponseDto(phone, name));
        }
    }

    private void processRecipientRow(Row row, List<FileDataResponseDto> successList, List<FileDataResponseDto> failedList, Set<String> seenCareNumbers) {
        String name = getCellValue(row.getCell(0));
        String careNumber = getCellValue(row.getCell(1));
        String birth = getCellValue(row.getCell(2));

        try {
            checkDuplicate(seenCareNumbers, careNumber, ApplicationError.DUPLICATE_CARE_NUMBER);

            validateCareNumber(careNumber, recipientRepository.existsByCareNumber(careNumber));

            seenCareNumbers.add(careNumber);

            Recipient recipient = new Recipient(name, LocalDate.parse(birth), null, null, careNumber,null,null,null, null);
            recipientRepository.save(recipient);

            successList.add(new FileDataResponseDto(name, careNumber, birth));
        } catch (ApplicationException e) {
            failedList.add(new FileDataResponseDto(name, careNumber, birth));
        }
    }

    private void checkDuplicate(Set<String> seenSet, String value, ApplicationError error) {
        if (seenSet.contains(value)) {
            throw new ApplicationException(error);
        }
    }

    private void validatePhone(String phone, boolean exists) {
        if (!phone.matches("010\\d{8}")) {
            throw new ApplicationException(ApplicationError.INVALID_PHONE_NUMBER);
        }
        if (exists) {
            throw new ApplicationException(ApplicationError.DUPLICATE_PHONE);
        }
    }

    private void validateCareNumber(String careNumber, boolean exists) {
        if (exists) {
            throw new ApplicationException(ApplicationError.DUPLICATE_CARE_NUMBER);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate().toString();
                } else {
                    return String.format("%.0f", cell.getNumericCellValue());
                }
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    @FunctionalInterface
    private interface RowProcessor {
        void process(Row row, List<FileDataResponseDto> successList, List<FileDataResponseDto> failedList);
    }
}
