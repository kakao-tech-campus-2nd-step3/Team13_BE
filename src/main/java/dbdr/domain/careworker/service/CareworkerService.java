package dbdr.domain.careworker.service;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import dbdr.domain.careworker.dto.response.CareworkerResponseDTO;
import dbdr.domain.careworker.repository.CareworkerRepository;
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

    @Transactional(readOnly = true)
    public List<CareworkerResponseDTO> getAllCareworkers() {
        return careworkerRepository.findAll().stream().map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CareworkerResponseDTO> getCareworkersByInstitution(Long institutionId) {
        return careworkerRepository.findByInstitutionId(institutionId).stream()
            .map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CareworkerResponseDTO getCareworkerById(Long id) {
        Careworker careworker = findCareworkerById(id);
        return toResponseDTO(careworker);
    }

    @Transactional
    public CareworkerResponseDTO createCareworker(CareworkerRequestDTO careworkerRequestDTO) {
        emailExists(careworkerRequestDTO.getEmail());

        Careworker careworker = new Careworker(careworkerRequestDTO.getInstitutionId(),
            careworkerRequestDTO.getName(), careworkerRequestDTO.getEmail(),
            careworkerRequestDTO.getPhone());
        careworkerRepository.save(careworker);
        return toResponseDTO(careworker);
    }

    @Transactional
    public CareworkerResponseDTO updateCareworker(Long id,
        CareworkerRequestDTO careworkerRequestDTO) {
        emailExists(careworkerRequestDTO.getEmail());

        Careworker careworker = findCareworkerById(id);

        careworker.updateCareworker(careworkerRequestDTO);
        careworkerRepository.save(careworker);

        return toResponseDTO(careworker);
    }

    @Transactional
    public void deleteCareworker(Long id) {
        Careworker careworker = findCareworkerById(id);
        careworker.deactivate();
        careworkerRepository.delete(careworker);
    }

    private Careworker findCareworkerById(Long id) {
        return careworkerRepository.findById(id)
            .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));

    }

    private void emailExists(String email) {
        if (careworkerRepository.existsByEmail(email)) {
            throw new ApplicationException(ApplicationError.DUPLICATE_EMAIL);
        }
    }

    private CareworkerResponseDTO toResponseDTO(Careworker careworker) {
        return new CareworkerResponseDTO(careworker.getId(), careworker.getInstitutionId(),
            careworker.getName(), careworker.getEmail(), careworker.getPhone());
    }

    public Careworker findByLineUserId(String userId) {
        if (careworkerRepository.findByLineUserId(userId) == null) {
            throw new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND);
        }
        return careworkerRepository.findByLineUserId(userId);
    }

    public Careworker findByPhone(String phoneNumber) {
        if (careworkerRepository.findByPhone(phoneNumber) == null) {
            throw new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND);
        }
        return careworkerRepository.findByPhone(phoneNumber);
    }
}
