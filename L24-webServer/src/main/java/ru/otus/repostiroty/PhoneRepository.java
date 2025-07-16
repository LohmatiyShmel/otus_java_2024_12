package ru.otus.repostiroty;

import java.util.Set;
import org.springframework.data.repository.CrudRepository;
import ru.otus.model.Phone;

public interface PhoneRepository extends CrudRepository<Phone, Long> {
    Set<Phone> findByClientId(Long clientId);
}
