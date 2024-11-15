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
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CareworkerMapperTest {

    @InjectMocks
    private CareworkerMapper mapper = Mappers.getMapper(CareworkerMapper.class);

    @Mock
    private InstitutionService institutionService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Institution institution;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {

        MockitoAnnotations.openMocks(this);

        Field passwordEncoderField = CareworkerMapper.class.getDeclaredField("passwordEncoder");
        passwordEncoderField.setAccessible(true);
        passwordEncoderField.set(mapper, passwordEncoder);

        institution = Institution.builder()
            .institutionNumber(100L)
            .institutionName("Test Institution")
            .build();

        setId(institution, 1L);
    }

    @Test
    void testToResponse() throws NoSuchFieldException, IllegalAccessException {

        Careworker careworker = Careworker.builder()
            .institution(institution)
            .name("John Doe")
            .email("johndoe@example.com")
            .phone("01012345678")
            .build();

        setId(careworker, 1L);

        CareworkerResponse responseDTO = mapper.toResponse(careworker);

        assertEquals(careworker.getId(), responseDTO.getId());
        assertEquals(careworker.getInstitution().getId(), responseDTO.getInstitutionId());
        assertEquals(careworker.getName(), responseDTO.getName());
        assertEquals(careworker.getEmail(), responseDTO.getEmail());
        assertEquals(careworker.getPhone(), responseDTO.getPhone());
    }

    @Test
    void testToEntity() {

        CareworkerRequest requestDTO = new CareworkerRequest(
            1L,
            "John Doe",
            "johndoe@example.com",
            "01012345678",
            "password"
        );

        when(institutionService.getInstitutionById(requestDTO.getInstitutionId())).thenReturn(institution);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        Careworker careworker = mapper.toEntity(requestDTO);

        assertEquals(requestDTO.getName(), careworker.getName());
        assertEquals(requestDTO.getEmail(), careworker.getEmail());
        assertEquals(requestDTO.getPhone(), careworker.getPhone());
        assertEquals("encodedPassword", careworker.getLoginPassword());
        assertEquals(institution, careworker.getInstitution());
    }

    private void setId(Object entity, Long idValue) throws NoSuchFieldException, IllegalAccessException {
        Field idField = entity.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, idValue);
    }
}
