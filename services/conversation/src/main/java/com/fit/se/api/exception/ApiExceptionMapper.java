package com.fit.se.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ApiExceptionMapper {

    public HttpStatus toStatus(Throwable throwable) {
        String name = throwable.getClass().getSimpleName();
        if (name.endsWith("NotFoundException")) return HttpStatus.NOT_FOUND;
        if (name.endsWith("AlreadyExistsException") || name.endsWith("AlreadyMemberException") || name.endsWith("ConflictException")) return HttpStatus.CONFLICT;
        if (name.endsWith("AccessDeniedException") || name.endsWith("ForbiddenException") || name.endsWith("UserBlockedFromGroupException")) return HttpStatus.FORBIDDEN;
        if (name.endsWith("ValidationException") || name.endsWith("BusinessRuleViolationException") || name.endsWith("NotAllowedException")) return HttpStatus.BAD_REQUEST;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ApiErrorCode toCode(Throwable throwable) {
        return switch (toStatus(throwable)) {
            case NOT_FOUND -> ApiErrorCode.RESOURCE_NOT_FOUND;
            case CONFLICT -> ApiErrorCode.CONFLICT;
            case FORBIDDEN -> ApiErrorCode.ACCESS_DENIED;
            case BAD_REQUEST -> ApiErrorCode.BUSINESS_RULE_VIOLATION;
            default -> ApiErrorCode.INTERNAL_SERVER_ERROR;
        };
    }
}
