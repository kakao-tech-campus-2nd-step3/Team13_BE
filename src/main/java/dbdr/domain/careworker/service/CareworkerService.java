package dbdr.domain.careworker.service;

import dbdr.domain.careworker.dto.CareworkerMapper;
import dbdr.domain.careworker.dto.request.CareworkerRequest;
import dbdr.domain.careworker.dto.request.CareworkerUpdateRequest;
import dbdr.domain.careworker.dto.response.CareworkerMyPageResponse;
import dbdr.domain.careworker.dto.response.CareworkerResponse;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.core.alarm.service.AlarmService;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.service.InstitutionService;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CareworkerService {

    private final CareworkerRepository careworkerRepository;
    private final InstitutionService institutionService;
    private final AlarmService alarmService;
    private final CareworkerMapper careworkerMapper;

    @Transactional(readOnly = true)
    public List<CareworkerResponse> getCareworkersByInstitution(Long institutionId) {
        List<Careworker> results = careworkerRepository.findAllByInstitutionId(institutionId);
        return results.stream().map(careworkerMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CareworkerResponse getCareworkerByInstitution(Long careworkerId, Long institutionId) {
        institutionService.getInstitutionById(institutionId);

        Careworker careworker = careworkerRepository.findById(careworkerId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));

        if (!careworker.getInstitution().getId().equals(institutionId)) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }

        return careworkerMapper.toResponse(careworker);
    }

    @Transactional(readOnly = true)
    public Careworker getCareworkerById(Long careworkerId) {
        return careworkerRepository.findById(careworkerId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));
    }


    @Transactional(readOnly = true)
    public CareworkerResponse getCareworkerResponseById(Long careworkerId) {
        Careworker careworker = findCareworkerById(careworkerId);
        return careworkerMapper.toResponse(careworker);
    }

    @Transactional(readOnly = true)
    public List<CareworkerResponse> getAllCareworkers() {
        List<Careworker> careworkers = careworkerRepository.findAll();
        return careworkers.stream().map(careworkerMapper::toResponse).toList();
    }

    @Transactional
    public CareworkerResponse createCareworker(CareworkerRequest careworkerRequestDTO) {
        ensureUniqueEmail(careworkerRequestDTO.getEmail());
        ensureUniquePhone(careworkerRequestDTO.getPhone());

        Careworker careworker = careworkerMapper.toEntity(careworkerRequestDTO);

        careworkerRepository.save(careworker);
        alarmService.createCareworkerAlarm(careworker);

        return careworkerMapper.toResponse(careworker);
    }

    @Transactional
    public CareworkerResponse updateCareworker(Long careworkerId, CareworkerRequest request) {
        ensureUniquePhoneButNotId(request.getPhone(), careworkerId);
        ensureUniqueEmailButNotId(request.getEmail(), careworkerId);
        Careworker careworker = findCareworkerById(careworkerId);

        /*if (!careworker.getInstitution().equals(institution)) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }*/

        careworker.updateCareworker(careworkerMapper.toEntity(request));
        return careworkerMapper.toResponse(careworker);
    }

    @Transactional
    public CareworkerResponse updateCareworkerByAdmin(Long careworkerId, CareworkerRequest request) {
        ensureUniquePhoneButNotId(request.getPhone(), careworkerId);
        ensureUniqueEmailButNotId(request.getEmail(), careworkerId);
        Careworker careworker = findCareworkerById(careworkerId);

        Institution institution = institutionService.getInstitutionById(request.getInstitutionId());
        /*if (institution == null) {
            throw new ApplicationException(ApplicationError.INSTITUTION_NOT_FOUND);
        }*/

        careworker.updateInstitution(institution);
        careworker.updateCareworker(careworkerMapper.toEntity(request));

        return careworkerMapper.toResponse(careworker);
    }

    @Transactional
    public void deleteCareworker(Long careworkerId, Long institutionId) {
        Careworker careworker = findCareworkerById(careworkerId);

        if (!careworker.getInstitution().getId().equals(institutionId)) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }

        careworkerRepository.delete(careworker);
    }

    @Transactional
    public void deleteCareworkerByAdmin(Long careworkerId) {
        Careworker careworker = findCareworkerById(careworkerId);
        careworkerRepository.delete(careworker);
    }

    @Transactional(readOnly = true)
    public CareworkerMyPageResponse getMyPageInfo(Long careworkerId) {
        Careworker careworker = findCareworkerById(careworkerId);
        return toMyPageResponseDTO(careworker);
    }

    @Transactional
    public CareworkerMyPageResponse updateWorkingDaysAndAlertTime(Long careworkerId, CareworkerUpdateRequest request) {
        Careworker careworker = careworkerRepository.findById(careworkerId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));

        careworker.updateWorkingDays(request.getWorkingDays());
        careworker.updateAlertTime(request.getAlertTime());

        return toMyPageResponseDTO(careworker);
    }


    private Careworker findCareworkerById(Long careworkerId) {
        return careworkerRepository.findById(careworkerId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));
    }

    private void ensureUniqueEmail(String email) {
        if (careworkerRepository.existsByEmail(email)) {
            throw new ApplicationException(ApplicationError.DUPLICATE_EMAIL);
        }
    }

    private void ensureUniquePhone(String phone) {
        if (careworkerRepository.findByPhone(phone).isPresent()) {
            throw new ApplicationException(ApplicationError.DUPLICATE_PHONE);
        }
    }

    /*private CareworkerResponseDTO toResponseDTO(Careworker careworker) {
        return new CareworkerResponseDTO(careworker.getId(), careworker.getInstitution().getId(),
                careworker.getName(), careworker.getEmail(), careworker.getPhone());
    }*/

    private void ensureUniquePhoneButNotId(String phone, Long id) {
        if(careworkerRepository.existsByPhoneAndIdNot(phone, id)) {
            throw new ApplicationException(ApplicationError.DUPLICATE_PHONE);
        }
    }

    private void ensureUniqueEmailButNotId(String phone, Long id) {
        if(careworkerRepository.existsByEmailAndIdNot(phone, id)) {
            throw new ApplicationException(ApplicationError.DUPLICATE_EMAIL);
        }
    }

    public List<Careworker> findByAlertTime(LocalTime currentTime) {
        return careworkerRepository.findByAlertTime(currentTime);
    }

    public Careworker findByPhone(String phoneNumber) {
        return careworkerRepository.findByPhone(phoneNumber).orElse(null);
    }

    private CareworkerMyPageResponse toMyPageResponseDTO(Careworker careworker) {
        return new CareworkerMyPageResponse(
                careworker.getName(),
                careworker.getPhone(),
                careworker.getInstitution().getInstitutionName(),
                careworker.getAlertTime(),
                careworker.getWorkingDays()
        );
    }

    @Transactional
    public void updateLineUserId(String userId, String phoneNumber) {
        Careworker careworker = findByPhone(phoneNumber);
        careworker.updateLineUserId(userId);
        careworkerRepository.save(careworker);
    }


}
