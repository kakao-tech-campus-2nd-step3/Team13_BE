package dbdr.careworker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import dbdr.domain.careworker.dto.CareworkerMapper;
import dbdr.domain.careworker.dto.request.CareworkerRequest;
import dbdr.domain.careworker.dto.response.CareworkerResponse;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.service.InstitutionService;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CareworkerMapperTest {

    @InjectMocks
    private CareworkerMapper mapper = Mappers.getMapper(CareworkerMapper.class);

    @Mock
    private InstitutionService institutionService;

    private Institution institution;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        institution = Institution.builder()
                .institutionNumber(100L)
                .institutionName("Test Institution")
                .build();

        // 리플렉션을 사용하여 ID 값을 설정
        setId(institution, 1L);
    }

    @Test
    void testToResponse() throws NoSuchFieldException, IllegalAccessException {
        // Given
        Careworker careworker = Careworker.builder()
                .institution(institution)
                .name("John Doe")
                .email("johndoe@example.com")
                .phone("01012345678")
                .build();

        // 리플렉션을 사용하여 Careworker ID 설정
        setId(careworker, 1L);

        // When
        CareworkerResponse responseDTO = mapper.toResponse(careworker);

        // Then
        assertEquals(careworker.getId(), responseDTO.getId());
        assertEquals(careworker.getInstitution().getId(), responseDTO.getInstitutionId());
        assertEquals(careworker.getName(), responseDTO.getName());
        assertEquals(careworker.getEmail(), responseDTO.getEmail());
        assertEquals(careworker.getPhone(), responseDTO.getPhone());
    }

    @Test
    void testToEntity() {
        // Given
        CareworkerRequest requestDTO = new CareworkerRequest(
                1L,
                "John Doe",
                "johndoe@example.com",
                "01012345678",
                "password"
        );

        // Mock InstitutionService response
        when(institutionService.getInstitutionById(requestDTO.getInstitutionId())).thenReturn(institution);

        // When
        Careworker careworker = mapper.toEntity(requestDTO);

        // Then
        assertEquals(requestDTO.getName(), careworker.getName());
        assertEquals(requestDTO.getEmail(), careworker.getEmail());
        assertEquals(requestDTO.getPhone(), careworker.getPhone());
        assertEquals(institution, careworker.getInstitution());
    }

    // 리플렉션을 사용하여 엔티티 ID 값을 설정
    private void setId(Object entity, Long idValue) throws NoSuchFieldException, IllegalAccessException {
        Field idField = entity.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, idValue);
    }
}