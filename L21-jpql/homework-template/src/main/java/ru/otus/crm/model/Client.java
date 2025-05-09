package ru.otus.crm.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client")
public class Client implements Cloneable {

    @Id
    @SequenceGenerator(name = "client_gen", sequenceName = "client_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_gen")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Phone> phones = new ArrayList<>();

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public <E> Client(Long id, String name, Address address, List<Phone> phones) {
        this.id = id;
        this.name = name;
        this.address = address;
        if (phones != null) {
            this.phones = phones;
            phones.forEach(phone -> phone.setClient(this));
        }
    }

    @Override
    @SuppressWarnings({"java:S2975", "java:S1182"})
    public Client clone() {
        Client clonedClient = new Client(this.id, this.name);

        if (this.address != null) {
            Address clonedAddress = new Address(this.address.getId(), this.address.getStreet());
            clonedClient.setAddress(clonedAddress);
        }

        if (this.phones != null) {
            List<Phone> clonedPhones = this.phones.stream()
                    .map(phone -> {
                        Phone clonedPhone = new Phone(phone.getId(), phone.getNumber());
                        clonedPhone.setClient(clonedClient);

                        return clonedPhone;
                    })
                    .toList();
            clonedClient.setPhones(clonedPhones);
        }

        return clonedClient;
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
