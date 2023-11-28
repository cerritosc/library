package com.focusservices.library.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.focusservices.library.commons.datatables.repository.DataTablesRepositoryFactoryBean;
import com.focusservices.library.domain.Loan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.*;
import java.time.format.*;


import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class, basePackages = {"com.focusservices.library.repository"})
@DisplayName("Entity repository test")
public class LoanRepositoryTest {

    // Constantes declaradas para la entidad Loan
    private static final Integer LOAN_ID_VALUE = 0;

    @Autowired
    private LoanRepository  loanRepository;

    @Test
    @DisplayName("Prueba de guardado. Valida que se genere el id")
    public void saveTest() {
        Loan loan = new  Loan();
        loan.setId(LOAN_ID_VALUE);

        // Se establecen valores por defecto a la prueba
        loan.setLentFrom(LocalDate.parse("11/03/1979", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        loan.setLentTo(LocalDate.parse("02/09/2014", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        loanRepository.save(loan);
        assertNotNull(loan.getId());
        assertAll(
                () -> assertEquals(LOAN_ID_VALUE, loan.getId())
        );
    }
}
