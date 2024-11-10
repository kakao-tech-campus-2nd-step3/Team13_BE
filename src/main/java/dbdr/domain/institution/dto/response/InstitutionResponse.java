package dbdr.domain.institution.dto.response;

public record InstitutionResponse(
    Long institutionNumber,
    String institutionName,
    String institutionLoginId
) {

}
