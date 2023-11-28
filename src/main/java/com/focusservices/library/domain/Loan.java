package com.focusservices.library.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
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
@Table(name = "loan")
public class Loan implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @JsonView
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @Column(name = "lent_from" )
    @DateTimeFormat(pattern = Constants.DATE_FORMAT)
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private LocalDate lentFrom; 

    @Column(name = "lent_to" )
    @DateTimeFormat(pattern = Constants.DATE_FORMAT)
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private LocalDate lentTo; 

    @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "id_book", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Book book; 

    @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User user; 

    // delegates de ids
    public Integer getBookIdDelegate() {
        if(book != null) {
            return book.getId();
        }
        else return null;
    } 

    public void setBookIdDelegate(Integer id) {
        if(id != null) {
            this.book = new Book();
            this.book.setId(id);
        }
        else {
            this.book = null;
        }
    }
    
    public String getBookStDescripcionDelegate() {
    	StringBuilder sb = new StringBuilder();
        if(book != null) {
        	sb.append(book.getTitle());
        	sb.append(" - ");
        	sb.append(book.getPublishedYear());
        }
        return sb.toString();
    }

    public String getBookSelect2Delegate() {
        return String.valueOf(getBookIdDelegate())
            + "|"
            + getBookStDescripcionDelegate();
    }

    public Integer getUserIdDelegate() {
        if(user != null) {
            return user.getId();
        }
        else return null;
    } 
    
    public String getUserStDescripcionDelegate() {
    	StringBuilder sb = new StringBuilder();
    	
        if(user != null) {
        	sb.append(user.getFirstName());
        	sb.append(" ");
        	sb.append(user.getLastName());
        }
        return sb.toString();
    }

    public String getUserSelect2Delegate() {
        return String.valueOf(getUserIdDelegate())
            + "|"
            + getUserStDescripcionDelegate();
    }

}
