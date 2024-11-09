package dbdr.global;

import static org.assertj.core.api.Assertions.assertThat;

import dbdr.domain.institution.dto.request.InstitutionRequest;
import dbdr.domain.institution.entity.Institution;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
public class MapperTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void test1(){
        Institution institution = Institution.builder().institutionName("김치덮밥요양원")
            .institutionNumber(123123L).build();
    }

}
