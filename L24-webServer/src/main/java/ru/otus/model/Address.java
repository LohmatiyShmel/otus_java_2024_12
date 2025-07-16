package ru.otus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("address")
@Getter
@Setter
@NoArgsConstructor
public class Address {
    @Id
    private Long id;

    private String street;

    public Address(String street) {
        this.street = street;
    }
}
