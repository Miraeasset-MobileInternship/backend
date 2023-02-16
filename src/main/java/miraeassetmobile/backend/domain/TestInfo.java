package miraeassetmobile.backend.domain;


import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "test_table")
@Getter
public class TestInfo {

    @Id
    private int id;

    private String title;




}
