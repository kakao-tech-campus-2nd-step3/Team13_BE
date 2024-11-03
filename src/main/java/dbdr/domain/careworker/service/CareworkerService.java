package dbdr.domain.careworker.service;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import dbdr.domain.careworker.dto.response.CareworkerResponseDTO;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.service.InstitutionService;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareworkerService {

    private final CareworkerRepository careworkerRepository;
    private final InstitutionService institutionService;

    @Transactional(readOnly = true)
    public List<CareworkerResponseDTO> getCareworkersByInstitution(Long institutionId) {
        return careworkerRepository.findByInstitutionId(institutionId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Careworker getCareworkerById(Long careworkerId) {
        return careworkerRepository.findById(careworkerId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));
    }


    @Transactional(readOnly = true)
    public CareworkerResponseDTO getCareworkerResponseById(Long careworkerId) {
        Careworker careworker = findCareworkerById(careworkerId);
        return toResponseDTO(careworker);
    }

    @Transactional
    public CareworkerResponseDTO createCareworker(CareworkerRequestDTO careworkerRequestDTO, Long institutionId) {
        ensureUniqueEmail(careworkerRequestDTO.getEmail());
        ensureUniquePhone(careworkerRequestDTO.getPhone());

        Institution institution = institutionService.getInstitutionById(institutionId);
        Careworker careworker = new Careworker(institution, careworkerRequestDTO.getName(),
                careworkerRequestDTO.getEmail(), careworkerRequestDTO.getPhone());

        careworkerRepository.save(careworker);
        return toResponseDTO(careworker);
    }

    @Transactional
    public CareworkerResponseDTO updateCareworker(Long careworkerId, CareworkerRequestDTO careworkerDTO, Long institutionId) {
        Careworker careworker = findCareworkerById(careworkerId);

        Institution institution = institutionService.getInstitutionById(institutionId);
        if (!careworker.getInstitution().equals(institution)) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }

        careworker.updateCareworker(careworkerDTO);
        return toResponseDTO(careworker);
    }

    @Transactional
    public void deleteCareworker(Long careworkerId, Long institutionId) {
        Careworker careworker = findCareworkerById(careworkerId);

        if (!careworker.getInstitution().getId().equals(institutionId)) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }

        careworker.deactivate();
        careworkerRepository.delete(careworker);
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

    private CareworkerResponseDTO toResponseDTO(Careworker careworker) {
        return new CareworkerResponseDTO(careworker.getId(), careworker.getInstitution().getId(),
                careworker.getName(), careworker.getEmail(), careworker.getPhone());
    }

    public Careworker findByLineUserId(String userId) {
        return careworkerRepository.findByLineUserId(userId).orElse(null);
    }

    public Careworker findByPhone(String phoneNumber) {
        return careworkerRepository.findByPhone(phoneNumber).orElse(null);
    }
}