package ru.otus.repostiroty;

import org.springframework.data.repository.CrudRepository;
import ru.otus.model.Client;

public interface ClientRepository extends CrudRepository<Client, Long> {}
