package dbdr.domain.excel.service;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.excel.dto.*;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.repository.InstitutionRepository;
import dbdr.domain.recipient.entity.Recipient;
import dbdr.domain.recipient.repository.RecipientRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExcelUploadService {

    private final CareworkerRepository careworkerRepository;
    private final GuardianRepository guardianRepository;
    private final RecipientRepository recipientRepository;
    private final InstitutionRepository institutionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CareworkerFileUploadResponse uploadCareworkerExcel(MultipartFile file, Long institutionId) {
        Set<String> seenPhones = new HashSet<>();
        List<ExcelCareworkerResponse> uploaded = new ArrayList<>();
        List<ExcelCareworkerResponse> failed = new ArrayList<>();

        processExcelFile(file, (row) -> processCareworkerRow(row, uploaded, failed, seenPhones, institutionId));

        return new CareworkerFileUploadResponse(file.getOriginalFilename(), uploaded, failed);
    }

    @Transactional
    public GuardianFileUploadResponse uploadGuardianExcel(MultipartFile file, Long institutionId) {
        Set<String> seenPhones = new HashSet<>();
        List<ExcelGuardianResponse> uploaded = new ArrayList<>();
        List<ExcelGuardianResponse> failed = new ArrayList<>();

        processExcelFile(file, (row) -> processGuardianRow(row, uploaded, failed, seenPhones, institutionId));

        return new GuardianFileUploadResponse(file.getOriginalFilename(), uploaded, failed);
    }

    @Transactional
    public RecipientFileUploadResponse uploadRecipientExcel(MultipartFile file, Long institutionId) {
        Set<String> seenCareNumbers = new HashSet<>();
        List<ExcelRecipientResponse> uploaded = new ArrayList<>();
        List<ExcelRecipientResponse> failed = new ArrayList<>();

        processExcelFile(file, (row) -> processRecipientRow(row, uploaded, failed, seenCareNumbers, institutionId));

        return new RecipientFileUploadResponse(file.getOriginalFilename(), uploaded, failed);
    }

    private void processExcelFile(MultipartFile file, RowProcessor rowProcessor) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                rowProcessor.process(row);
            }
        } catch (IOException e) {
            throw new ApplicationException(ApplicationError.FILE_UPLOAD_ERROR);
        }
    }

    private void processCareworkerRow(Row row, List<ExcelCareworkerResponse> successList,
                                      List<ExcelCareworkerResponse> failedList, Set<String> seenPhones, Long institutionId) {
        String name = getCellValue(row.getCell(0));
        String email = getCellValue(row.getCell(1));
        String phone = getCellValue(row.getCell(2));
        String loginPassword = getCellValue(row.getCell(3));

        String encryptedPassword = passwordEncoder.encode(loginPassword);

        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.INSTITUTION_NOT_FOUND));

        try {
            checkDuplicate(seenPhones, phone, ApplicationError.DUPLICATE_PHONE);
            validatePhone(phone, careworkerRepository.existsByPhone(phone));
            validateEmail(email,careworkerRepository.existsByEmail(email));
            seenPhones.add(phone);

            Careworker careworker = Careworker.builder()
                    .institution(institution)
                    .name(name)
                    .email(email)
                    .phone(phone)
                    .loginPassword(encryptedPassword)
                    .build();
            careworkerRepository.save(careworker);

            successList.add(new ExcelCareworkerResponse(careworker.getId(), institution.getId(), name, email, phone));
        } catch (ApplicationException e) {
            failedList.add(new ExcelCareworkerResponse(null, institution.getId(), name, email, phone));
        }
    }

    private void processGuardianRow(Row row, List<ExcelGuardianResponse> successList,
                                    List<ExcelGuardianResponse> failedList, Set<String> seenPhones, Long institutionId) {
        String name = getCellValue(row.getCell(0));
        String phone = getCellValue(row.getCell(1));
        String loginPassword = getCellValue(row.getCell(2));

        String encryptedPassword = passwordEncoder.encode(loginPassword);

        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.INSTITUTION_NOT_FOUND));

        try {
            checkDuplicate(seenPhones, phone, ApplicationError.DUPLICATE_PHONE);
            validatePhone(phone, guardianRepository.existsByPhone(phone));
            seenPhones.add(phone);

            Guardian guardian = Guardian.builder()
                    .name(name)
                    .phone(phone)
                    .institution(institution)
                    .loginPassword(encryptedPassword)
                    .build();
            guardianRepository.save(guardian);

            successList.add(new ExcelGuardianResponse(guardian.getId(), name, phone, institution.getId()));
        } catch (ApplicationException e) {
            failedList.add(new ExcelGuardianResponse(null,  name, phone, institution.getId()));
        }
    }

    private void processRecipientRow(Row row, List<ExcelRecipientResponse> successList,
                                     List<ExcelRecipientResponse> failedList, Set<String> seenCareNumbers, Long institutionId) {
        String name = getCellValue(row.getCell(0));
        String birth = getCellValue(row.getCell(1));
        String gender = getCellValue(row.getCell(2));
        String careLevel = getCellValue(row.getCell(3));
        String careNumber = getCellValue(row.getCell(4));
        String startDate = getCellValue(row.getCell(5));
        Long careworkerId = Long.valueOf(getCellValue(row.getCell(6)));
        Long guardianId = Long.valueOf(getCellValue(row.getCell(7)));


        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.INSTITUTION_NOT_FOUND));

        Careworker careworker = careworkerRepository.findById(careworkerId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));

        Guardian guardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.GUARDIAN_NOT_FOUND));

        try {
            checkDuplicate(seenCareNumbers, careNumber, ApplicationError.DUPLICATE_CARE_NUMBER);
            validateCareNumber(careNumber, recipientRepository.existsByCareNumber(careNumber));
            seenCareNumbers.add(careNumber);

            Recipient recipient = Recipient.builder()
                    .name(name)
                    .careNumber(careNumber)
                    .birth(LocalDate.parse(birth))
                    .gender(gender)
                    .careLevel(careLevel)
                    .startDate(LocalDate.parse(startDate))
                    .institution(institution)
                    .careworker(careworker)
                    .guardian(guardian)
                    .build();
            recipientRepository.save(recipient);

            successList.add(new ExcelRecipientResponse(
                    recipient.getId(), name, LocalDate.parse(birth), gender, careLevel, careNumber, LocalDate.parse(startDate), institution.getId(), careworker.getId(), guardian.getId()));
        } catch (ApplicationException e) {
            failedList.add(new ExcelRecipientResponse(
                    null, name, LocalDate.parse(birth), gender, careLevel, careNumber, LocalDate.parse(startDate), institution.getId(), careworker.getId(), guardian.getId()));
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

    private void validateEmail(String email, boolean exists) {
        if (exists) {
            throw new ApplicationException(ApplicationError.DUPLICATE_EMAIL);
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
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate().toString();
                } else {
                    return String.format("%.0f", cell.getNumericCellValue()).trim();
                }
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue()).trim();
            case FORMULA:
                return cell.getCellFormula().trim();
            default:
                return "";
        }
    }

    @FunctionalInterface
    private interface RowProcessor {
        void process(Row row);
    }
}
