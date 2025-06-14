package ru.otus.dao;

import java.util.List;
import java.util.Optional;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DbServiceClientImpl;

public class DatabaseClientDao implements ClientDao {

    final DbServiceClientImpl dbServiceClient;

    public DatabaseClientDao(DbServiceClientImpl client) {
        this.dbServiceClient = client;
    }

    @Override
    public Optional<Client> createClient(String name, String street, List<String> numbers) {
        final Client client = new Client(name);
        final Address address = new Address();
        address.setStreet(street);
        List<Phone> phones = numbers.stream()
                .map(number -> {
                    final Phone phone = new Phone();
                    phone.setNumber(number);
                    return phone;
                })
                .toList();
        client.setAddress(address);
        client.setPhones(phones);
        return Optional.ofNullable(dbServiceClient.saveClient(client));
    }

    @Override
    public Optional<Client> findById(long id) {
        return dbServiceClient.getClient(id);
    }

    @Override
    public Optional<List<Client>> findByName(String name) {
        return dbServiceClient.getClient(name);
    }
}
