package ru.otus.model;

import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("client")
@Getter
@Setter
@NoArgsConstructor
public class Client {

    @Id
    private Long id;

    private String name;

    @Column("address_id")
    private Long addressId;

    private transient Address address;
    private transient Set<Phone> phones;

    public Client(String name) {
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
