package ru.otus.dao;

import java.util.List;
import java.util.Optional;
import ru.otus.crm.model.Client;

public interface ClientDao {

    Optional<Client> createClient(String name, String street, List<String> number);

    Optional<Client> findById(long id);

    Optional<List<Client>> findByName(String name);
}
