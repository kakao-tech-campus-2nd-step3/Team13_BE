package dbdr.domain.institution.dto.response;

public record InstitutionResponse(
    Long id,
    Long institutionNumber,
    String institutionName,
    String institutionLoginId
) {

}
