package miraeassetmobile.backend.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="transaction_category")
@Getter
@NoArgsConstructor
public class TransactionCategory {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //JPA 사용시 필요)
    private Long id;
    @NotNull
    private String title;

    @NotNull
    private boolean isChangeable;




}
