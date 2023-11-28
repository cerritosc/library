package com.focusservices.library.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.focusservices.library.commons.datatables.repository.DataTablesRepositoryFactoryBean;
import com.focusservices.library.domain.Book;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.*;
import java.time.format.*;


import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class, basePackages = {"com.focusservices.library.repository"})
@DisplayName("Entity repository test")
public class BookRepositoryTest {

    // Constantes declaradas para la entidad Book
    private static final Integer BOOK_ID_VALUE = 0;

    @Autowired
    private BookRepository  bookRepository;

    @Test
    @DisplayName("Prueba de guardado. Valida que se genere el id")
    public void saveTest() {
        Book book = new  Book();
        book.setId(BOOK_ID_VALUE);

        // Se establecen valores por defecto a la prueba
        book.setStock(697);
        book.setGenre("gLtzQbcpDf");
        book.setPublishedYear(LocalDate.parse("13/03/1927", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        book.setAuthor("zFrueaaMdo");
        book.setTitle("GugBXTPoHL");
        bookRepository.save(book);
        assertNotNull(book.getId());
        assertAll(
                () -> assertEquals(BOOK_ID_VALUE, book.getId())
        );
    }
}
