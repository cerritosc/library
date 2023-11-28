package com.focusservices.library.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.focusservices.library.commons.datatables.repository.DataTablesRepositoryFactoryBean;
import com.focusservices.library.domain.User;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class, basePackages = {"com.focusservices.library.repository"})
@DisplayName("Entity repository test")
public class UserRepositoryTest {

    // Constantes declaradas para la entidad User
    private static final Integer USER_ID_VALUE = 0;

    @Autowired
    private UserRepository  userRepository;

    @Test
    @DisplayName("Prueba de guardado. Valida que se genere el id")
    public void saveTest() {
        User user = new  User();
        user.setId(USER_ID_VALUE);

        // Se establecen valores por defecto a la prueba
        user.setFirstName("NTFDJRJusP");
        user.setEmail("uvFJFIKKwp");
        user.setLastName("QPvYIlUwmm");
        user.setRole("HpfBfbpSIq");
        userRepository.save(user);
        assertNotNull(user.getId());
        assertAll(
                () -> assertEquals(USER_ID_VALUE, user.getId())
        );
    }
}
