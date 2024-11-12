package dbdr.domain.guardian.dto.response;

public record GuardianResponse(
    Long id,
    String phone,
    String name,
    Long institutionId,
    boolean isActive) {
}
