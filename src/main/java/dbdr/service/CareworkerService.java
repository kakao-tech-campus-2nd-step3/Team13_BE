package dbdr.service;

import dbdr.domain.Careworker;
import dbdr.dto.request.CareworkerRequestDTO;
import dbdr.dto.response.CareworkerResponseDTO;
import dbdr.repository.CareworkerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CareworkerService {

    private final CareworkerRepository careworkerRepository;

    public CareworkerService(CareworkerRepository careworkerRepository) {
        this.careworkerRepository = careworkerRepository;
    }

    @Transactional(readOnly = true)
    public List<CareworkerResponseDTO> getAllCareworkers() {
        return careworkerRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CareworkerResponseDTO> getCareworkersByInstitution(Long institutionId) {
        return careworkerRepository.findByInstitutionId(institutionId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CareworkerResponseDTO getCareworkerById(Long id) {
        Careworker careworker = careworkerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("요양보호사를 찾을 수 없습니다."));
        return toResponseDTO(careworker);
    }

    @Transactional
    public CareworkerResponseDTO createCareworker(CareworkerRequestDTO careworkerRequestDTO) {
        Careworker careworker = new Careworker(
                careworkerRequestDTO.getInstitutionId(),
                careworkerRequestDTO.getName(),
                careworkerRequestDTO.getEmail(),
                careworkerRequestDTO.getPhone()
        );
        careworkerRepository.save(careworker);
        return toResponseDTO(careworker);
    }

    @Transactional
    public CareworkerResponseDTO updateCareworker(Long id, CareworkerRequestDTO careworkerRequestDTO) {
        Careworker careworker = careworkerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("요양보호사를 찾을 수 없습니다."));

        careworker.updateCareworker(careworkerRequestDTO);
        careworkerRepository.save(careworker);

        return toResponseDTO(careworker);
    }

    @Transactional
    public void deleteCareworker(Long id) {
        Careworker careworker = careworkerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("요양보호사를 찾을 수 없습니다."));
        careworker.deactivate();
        careworkerRepository.delete(careworker);
    }

    private CareworkerResponseDTO toResponseDTO(Careworker careworker) {
        return new CareworkerResponseDTO(
                careworker.getId(),
                careworker.getInstitutionId(),
                careworker.getName(),
                careworker.getEmail(),
                careworker.getPhone()
        );
    }
}
