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
            Sheet sheet = workbook.createSheet("Careworkers");
            createHeaderRow(sheet, "Name", "Phone");

            applyTextStyle(sheet, 1);

            return convertWorkbookToByteArray(workbook);
        } catch (IOException e) {
            throw new ApplicationException(ApplicationError.FILE_DOWNLOAD_ERROR);
        }
    }


    public byte[] generateGuardianTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Guardians");
            createHeaderRow(sheet, "Phone", "Name");

            applyTextStyle(sheet, 0);

            return convertWorkbookToByteArray(workbook);
        } catch (IOException e) {
            throw new ApplicationException(ApplicationError.FILE_DOWNLOAD_ERROR);
        }
    }


    public byte[] generateRecipientTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Recipients");
            createHeaderRow(sheet, "Name", "Care Number", "Birth");

            applyTextStyle(sheet, 1);
            applyDateStyle(sheet, 2);

            return convertWorkbookToByteArray(workbook);
        } catch (IOException e) {
            throw new ApplicationException(ApplicationError.FILE_DOWNLOAD_ERROR);
        }
    }


    private void applyTextStyle(Sheet sheet, int columnIndex) {
        Workbook workbook = sheet.getWorkbook();
        CellStyle textStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        textStyle.setDataFormat(format.getFormat("@"));  // 텍스트 형식

        for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) row = sheet.createRow(rowIndex);
            Cell cell = row.getCell(columnIndex);
            if (cell == null) cell = row.createCell(columnIndex);
            cell.setCellStyle(textStyle);
        }
    }

    private void applyDateStyle(Sheet sheet, int columnIndex) {
        Workbook workbook = sheet.getWorkbook();
        CellStyle dateStyle = workbook.createCellStyle();
        DataFormat dateFormat = workbook.createDataFormat();
        dateStyle.setDataFormat(dateFormat.getFormat("yyyy-MM-dd"));  // 날짜 형식

        for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) row = sheet.createRow(rowIndex);
            Cell cell = row.getCell(columnIndex);
            if (cell == null) cell = row.createCell(columnIndex);
            cell.setCellStyle(dateStyle);
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


    private byte[] convertWorkbookToByteArray(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }


    private void createHeaderRow(Sheet sheet, String... headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    @FunctionalInterface
    private interface RowProcessor {
        void process(Row row, List<FileDataResponseDto> successList, List<FileDataResponseDto> failedList);
    }
}