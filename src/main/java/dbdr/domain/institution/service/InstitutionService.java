package dbdr.domain.institution.service;

import dbdr.domain.institution.dto.request.InstitutionRequest;
import dbdr.domain.institution.dto.response.InstitutionResponse;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.repository.InstitutionRepository;
import java.util.List;

import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    public InstitutionResponse getInstitutionById(Long id) {
        Institution institution = getInstitution(id);
        return new InstitutionResponse(institution.getInstitutionNumber(),
            institution.getInstitutionName());
    }

    public InstitutionResponse updateInstitution(Long id, InstitutionRequest institutionRequest) {
        institutionIdExists(institutionRequest.institutionNumber());

        Institution institution = getInstitution(id);
        institution.updateInstitution(institutionRequest.institutionNumber(), institutionRequest.institutionName());
        institutionRepository.save(institution);
        return new InstitutionResponse(institutionRequest.institutionNumber(),
            institutionRequest.institutionName());
    }

    public List<InstitutionResponse> getAllInstitution() {
        List<Institution> institutionList = institutionRepository.findAll();
        return institutionList.stream().map(
            institution -> new InstitutionResponse(institution.getInstitutionNumber(),
                institution.getInstitutionName())).toList();
    }

    public InstitutionResponse addInstitution(InstitutionRequest institutionRequest) {
        institutionIdExists(institutionRequest.institutionNumber());
        Institution institution = new Institution(institutionRequest.institutionNumber(),
            institutionRequest.institutionName());
        institution = institutionRepository.save(institution);
        return new InstitutionResponse(institution.getInstitutionNumber(),
            institution.getInstitutionName());
    }

    public void deleteInstitutionById(Long id) {
        Institution institution = getInstitution(id);
        institution.deactivate();
        institutionRepository.delete(institution);
    }

    private void institutionIdExists(Long institutionNumber) {
        if (institutionRepository.existsByInstitutionNumber(institutionNumber)) {
            throw new ApplicationException(ApplicationError.DUPLICATE_INSTITUTION_NUMBER);
        }
    }

    private Institution getInstitution(Long id) {
        return institutionRepository.findById(id)
            .orElseThrow(() -> new ApplicationException(ApplicationError.INSTITUTION_NOT_FOUND));
    }
}
