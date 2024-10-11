package dbdr.security.model;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.chart.entity.Chart;
import dbdr.domain.core.base.entity.BaseEntity;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.recipient.entity.Recipient;
import org.springframework.stereotype.Component;

@Component
public class DbdrAcess {

    public boolean hasAccessPermission(BaseUserDetails userDetails, BaseEntity baseEntity) {

        if (userDetails.getRole().equals(Role.ADMIN)) { //ADMIN 권한이면 모든 권한을 가짐
            return true;
        }
        if (baseEntity instanceof Institution) {
            return hasAccessPermission(userDetails, (Institution) baseEntity);
        }
        if (baseEntity instanceof Careworker) {
            return hasAccessPermission(userDetails, (Careworker) baseEntity);
        }
        if (baseEntity instanceof Guardian) {
            return hasAccessPermission(userDetails, (Guardian) baseEntity);
        }
        if (baseEntity instanceof Chart) {
            return hasAccessPermission(userDetails, (Chart) baseEntity);
        }
        if (baseEntity instanceof Recipient) {
            return hasAccessPermission(userDetails, (Recipient) baseEntity);
        }
        return false;
    }

    private boolean hasAccessPermission(BaseUserDetails userDetails, Institution institution) {
        if (isInstitution(userDetails)) {
            return userDetails.getInstitutionId().equals(institution.getId());
        }
        return false; //careworker나 guardian일 경우 url에서 Institution의 ID로 접근하는 것 자체가 잘못되었다 생각
    }

    private boolean hasAccessPermission(BaseUserDetails userDetails, Careworker careworker) {
        if (isInstitution(userDetails)) {
            return userDetails.getInstitutionId().equals(careworker.getInstitutionId());
        }
        if (isCareworker(userDetails)) {
            return userDetails.getId().equals(careworker.getId());
        }
        return false;
    }


    private boolean hasAccessPermission(BaseUserDetails userDetails, Guardian guardian) {
        if (isInstitution(userDetails)) {
            return userDetails.getInstitutionId().equals(guardian.getInstitutionId());
        }
        if(isGuardian(userDetails)){
            return userDetails.getId().equals(guardian.getId());
        }
        return false;
    }

    private boolean hasAccessPermission(BaseUserDetails userDetails, Chart chart) {
        if(isInstitution(userDetails)){
            return userDetails.getInstitutionId().equals(chart.getRecipient().getInstitutionNumber());
        }
        if(isCareworker(userDetails)){
            return userDetails.getId().equals(chart.getRecipient().getCareworker().getId());
        }
        if(isGuardian(userDetails)){
            return false;
            //TODO : 환자와 보호자 간의 mapping 필요
            //return userDetails.getId().equals(chart.getRecipient().getGuardian().getId());
        }
        return false;
    }

    private boolean hasAccessPermission(BaseUserDetails userDetails, Recipient recipient) {
        //TODO : chart와 recipient사이 관계가 있으므로 리팩토링 가능
        if(isInstitution(userDetails)){
            return userDetails.getInstitutionId().equals(recipient.getInstitutionNumber());
        }
        if(isCareworker(userDetails)){
            return userDetails.getId().equals(recipient.getCareworker().getId());
        }
        if(isGuardian(userDetails)){
            return false;
            //TODO : 환자와 보호자 간의 mapping 필요
            //return userDetails.getId().equals(recipient.getGuardian().getId());
        }
        return false;
    }

    private boolean isInstitution(BaseUserDetails userDetails) {
        return userDetails.getRole().equals(Role.INSTITUTION);
    }

    private static boolean isCareworker(BaseUserDetails userDetails) {
        return userDetails.getRole().equals(Role.CAREWORKER);
    }

    private static boolean isGuardian(BaseUserDetails userDetails) {
        return userDetails.getRole().equals(Role.GUARDIAN);
    }




}
