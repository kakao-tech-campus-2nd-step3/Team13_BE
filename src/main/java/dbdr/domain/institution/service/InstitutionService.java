package dbdr.domain.institution.service;

import dbdr.domain.institution.dto.request.InstitutionRequest;
import dbdr.domain.institution.dto.response.InstitutionResponse;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.repository.InstitutionRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Institution getInstitutionById(Long id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ApplicationError.INSTITUTION_NOT_FOUND));
    }

    public InstitutionResponse getInstitutionResponseById(Long id) {
        Institution institution = getInstitutionById(id);
        return new InstitutionResponse(institution.getInstitutionNumber(),
                institution.getInstitutionName(), institution.getLoginId());
    }

    public InstitutionResponse updateInstitution(Long institutionId,
                                                 InstitutionRequest institutionRequest) {
        ensureUniqueInstitutionNumber(institutionRequest.institutionNumber());

        Institution institution = getInstitutionById(institutionId);
        String password = passwordEncoder.encode(institutionRequest.institutionLoginPassword());
        institution.updateInstitution(institutionRequest.institutionLoginId(),
                password, institutionRequest.institutionNumber(),
                institutionRequest.institutionName());
        institutionRepository.save(institution);
        return new InstitutionResponse(institutionRequest.institutionNumber(),
                institutionRequest.institutionName(), institutionRequest.institutionLoginId());
    }

    public List<InstitutionResponse> getAllInstitution() {
        List<Institution> institutionList = institutionRepository.findAll();
        return institutionList.stream().map(
                institution -> new InstitutionResponse(institution.getInstitutionNumber(),
                        institution.getInstitutionName(), institution.getLoginId())).toList();
    }

    public InstitutionResponse addInstitution(InstitutionRequest institutionRequest) {
        String password = passwordEncoder.encode(institutionRequest.institutionLoginPassword());
        ensureUniqueInstitutionNumber(institutionRequest.institutionNumber());
        Institution institution = Institution.builder()
                .loginId(institutionRequest.institutionLoginId())
                .loginPassword(password)
                .institutionNumber(institutionRequest.institutionNumber())
                .institutionName(institutionRequest.institutionName()).build();
        institution = institutionRepository.save(institution);
        return new InstitutionResponse(institution.getInstitutionNumber(),
                institution.getInstitutionName(), institution.getLoginId());
    }

    public void deleteInstitutionById(Long institutionId) {
        Institution institution = getInstitutionById(institutionId);
        institution.deactivate();
        institutionRepository.delete(institution);
    }

    private void ensureUniqueInstitutionNumber(Long institutionNumber) {
        if (institutionRepository.existsByInstitutionNumber(institutionNumber)) {
            throw new ApplicationException(ApplicationError.DUPLICATE_INSTITUTION_NUMBER);
        }
    }
}
