package ru.otus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("phone")
@Getter
@Setter
@NoArgsConstructor
public class Phone {
    @Id
    private Long id;

    private String number;

    @Column("client_id")
    private Long clientId;

    public Phone(String number, Long clientId) {
        this.number = number;
        this.clientId = clientId;
    }
}
