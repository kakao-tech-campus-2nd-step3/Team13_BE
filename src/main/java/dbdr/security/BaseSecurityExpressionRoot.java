package dbdr.security;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class BaseSecurityExpressionRoot extends SecurityExpressionRoot implements
    MethodSecurityExpressionOperations {

    public BaseSecurityExpressionRoot(Authentication authentication){
        super(authentication);
    }

    //
    public boolean isOwner(Role role,Long targetId){
        if (getAuthentication() == null || targetId == null) {
            return false;
        }
        BaseUserDetails userDetails = (BaseUserDetails) getAuthentication().getPrincipal();

        return role == userDetails.getRole() && targetId.equals(userDetails.getId());

    }

    @Override
    public void setFilterObject(Object filterObject) {

    }

    @Override
    public Object getFilterObject() {
        return null;
    }

    @Override
    public void setReturnObject(Object returnObject) {

    }

    @Override
    public Object getReturnObject() {
        return null;
    }

    @Override
    public Object getThis() {
        return null;
    }
}
