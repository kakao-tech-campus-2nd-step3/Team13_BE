package dbdr.service;

import dbdr.domain.Careworker;
import dbdr.dto.CareworkerDTO;
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
    public List<CareworkerDTO> getAllCareworkers() {
        return careworkerRepository.findAll().stream()
                .filter(Careworker::isActive)  // 활성 상태만 필터링
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public CareworkerDTO getCareworkerById(Long id) {
        Careworker careworker = careworkerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("요양보호사를 찾을 수 없습니다."));
        return toDTO(careworker);
    }

    @Transactional
    public CareworkerDTO createCareworker(CareworkerDTO careworkerDTO) {
        Careworker careworker = new Careworker(
                careworkerDTO.getInstitutionId(),
                careworkerDTO.getName(),
                careworkerDTO.getEmail(),
                careworkerDTO.getPhone()
        );
        careworkerRepository.save(careworker);
        return toDTO(careworker);
    }


    @Transactional
    public CareworkerDTO updateCareworker(Long id, CareworkerDTO careworkerDTO) {
        Careworker careworker = careworkerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("요양보호사를 찾을 수 없습니다."));

        careworker.updateCareworker(careworkerDTO.getInstitutionId(), careworkerDTO.getName(),
                careworkerDTO.getEmail(), careworkerDTO.getPhone());

        careworkerRepository.save(careworker);
        return toDTO(careworker);
    }

    @Transactional
    public void deleteCareworker(Long id) {
        Careworker careworker = careworkerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("요양보호사를 찾을 수 없습니다."));
        careworker.deactivate();
        careworkerRepository.delete(careworker);
    }

    private CareworkerDTO toDTO(Careworker careworker) {
        return new CareworkerDTO(
                careworker.getId(),
                careworker.getInstitutionId(),
                careworker.getName(),
                careworker.getEmail(),
                careworker.getPhone(),
                careworker.getCreatedAt(),
                careworker.getUpdateAt(),
                careworker.isActive()
        );
    }
}