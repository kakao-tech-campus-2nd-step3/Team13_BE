package dbdr.security.service;

import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.chart.repository.ChartRepository;
import dbdr.domain.core.base.entity.BaseEntity;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.domain.institution.repository.InstitutionRepository;
import dbdr.domain.recipient.repository.RecipientRepository;
import dbdr.security.model.AuthParam;
import dbdr.security.model.DbdrAcess;
import dbdr.security.model.Role;
import dbdr.security.model.BaseUserDetails;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DbdrSeucrityService {

    private final InstitutionRepository institutionRepository;
    private final CareworkerRepository careworkerRepository;
    private final GuardianRepository guardianRepository;
    private final ChartRepository chartRepository;
    private final RecipientRepository recipientRepository;

    private final DbdrAcess dbdrAcess;

    public boolean hasAcesssPermission(Role role, @NotNull AuthParam type, String id) {

        BaseUserDetails baseUserDetails = (BaseUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

        return dbdrAcess.hasAccessPermission(baseUserDetails,
            findEntity(type, Long.parseLong(id))); //TODO : id가 long이 아닌 경우 처리

    }

    private BaseEntity findEntity(AuthParam type, long id) {
        return switch (type) {
            case INSTITUTION_ID -> institutionRepository.findById(id).orElse(null);
            case CAREWORKER_ID -> careworkerRepository.findById(id).orElse(null);
            case GUARDIAN_ID -> guardianRepository.findById(id).orElse(null);
            case CHART_ID -> chartRepository.findById(id).orElse(null);
            case RECIPIENT_ID -> recipientRepository.findById(id).orElse(null);
            default -> null;
        };
    }


}
