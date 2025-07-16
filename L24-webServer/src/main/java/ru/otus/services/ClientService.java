package ru.otus.services;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.model.dto.ClientCreateDto;
import ru.otus.repostiroty.AddressRepository;
import ru.otus.repostiroty.ClientRepository;
import ru.otus.repostiroty.PhoneRepository;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepo;
    private final AddressRepository addressRepo;
    private final PhoneRepository phoneRepo;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void createClient(ClientCreateDto dto) {
        Address address = null;
        if (dto.getStreet() != null && !dto.getStreet().isEmpty()) {
            address = new Address();
            address.setStreet(dto.getStreet());
            address = addressRepo.save(address);
        }

        Client client = new Client();
        client.setName(dto.getName());
        if (address != null) {
            client.setAddressId(address.getId());
        }
        Client savedClient = clientRepo.save(client);

        if (dto.getPhones() != null && !dto.getPhones().trim().isEmpty()) {
            Set<Phone> phones = Arrays.stream(dto.getPhones().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(number -> {
                        Phone phone = new Phone();
                        phone.setNumber(number);
                        phone.setClientId(savedClient.getId());
                        return phone;
                    })
                    .collect(Collectors.toSet());

            phoneRepo.saveAll(phones);
        }
    }

    public List<Client> getAllClientsWithRelations() {
        String sql =
                """
                    SELECT
                        c.id AS client_id, c.name AS client_name,
                        a.id AS address_id, a.street AS address_street,
                        p.id AS phone_id, p.number AS phone_number
                    FROM client c
                    LEFT JOIN address a ON c.address_id = a.id
                    LEFT JOIN phone p ON c.id = p.client_id
                """;

        Map<Long, Client> clientMap = new HashMap<>();

        jdbcTemplate.query(sql, rs -> {
            Long clientId = rs.getLong("client_id");

            Client client = clientMap.computeIfAbsent(clientId, id -> {
                Client c = new Client();
                c.setId(clientId);
                try {
                    c.setName(rs.getString("client_name"));
                    c.setAddressId(rs.getObject("address_id", Long.class));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                c.setAddress(null);
                c.setPhones(new HashSet<>());
                return c;
            });

            Long addressId = rs.getObject("address_id", Long.class);
            if (addressId != null && client.getAddress() == null) {
                Address address = new Address();
                address.setId(addressId);
                address.setStreet(rs.getString("address_street"));
                client.setAddress(address);
            }

            Long phoneId = rs.getObject("phone_id", Long.class);
            if (phoneId != null) {
                Phone phone = new Phone();
                phone.setId(phoneId);
                phone.setNumber(rs.getString("phone_number"));
                phone.setClientId(clientId);
                client.getPhones().add(phone);
            }
        });

        return new ArrayList<>(clientMap.values());
    }
}
