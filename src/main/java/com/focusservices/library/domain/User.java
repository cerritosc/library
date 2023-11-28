package com.focusservices.library.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import com.focusservices.library.commons.Constants;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
@Table(name = "User")
public class User implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @JsonView
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @Column(name = "first_name" )
    @Size(max = 512, message = "El campo firstName excede la longitud permitida")
    private String firstName; 

    @Column(name = "email" )
    @Size(max = 128, message = "El campo email excede la longitud permitida")
    private String email; 

    @Column(name = "last_name" )
    @Size(max = 512, message = "El campo lastName excede la longitud permitida")
    private String lastName; 

    @Column(name = "role" )
    @Size(max = 1, message = "El campo role excede la longitud permitida")
    private String role; 



    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter(onMethod = @__(@JsonIgnore))
    @ToString.Exclude
    private Set<Loan> loanes;




    // delegates de ids
}
