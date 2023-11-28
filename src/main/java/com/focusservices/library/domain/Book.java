package com.focusservices.library.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.*;


import com.focusservices.library.commons.Constants;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;


import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
@Table(name = "Book")
public class Book implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @JsonView
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @Column(name = "stock" )
    private Integer stock; 

    @Column(name = "genre" )
    @Size(max = 512, message = "El campo genre excede la longitud permitida")
    private String genre; 

    @Column(name = "published_year" )
    @DateTimeFormat(pattern = Constants.DATE_FORMAT)
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private LocalDate publishedYear; 

    @Column(name = "author" )
    @Size(max = 512, message = "El campo author excede la longitud permitida")
    private String author; 

    @Column(name = "title" )
    @Size(max = 512, message = "El campo title excede la longitud permitida")
    private String title; 



    @OneToMany(mappedBy = "book", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter(onMethod = @__(@JsonIgnore))
    @ToString.Exclude
    private Set<Loan> loanes;




    // delegates de ids
}
