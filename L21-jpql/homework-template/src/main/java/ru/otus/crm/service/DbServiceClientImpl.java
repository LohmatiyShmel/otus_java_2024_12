package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.MyCache;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.crm.model.Client;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;
    private final MyCache<Long, Client> cache = new MyCache<>();

    public DbServiceClientImpl(TransactionManager transactionManager, DataTemplate<Client> clientDataTemplate) {
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
    }

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(session -> {
            var clientCloned = client.clone();
            if (client.getId() == null) {
                var savedClient = clientDataTemplate.insert(session, clientCloned);
                log.info("Created client: {}", savedClient);
                cache.put(savedClient.getId(), savedClient);
                return savedClient;
            } else {
                var savedClient = clientDataTemplate.update(session, clientCloned);
                log.info("Updated client: {}", savedClient);
                cache.put(savedClient.getId(), savedClient);
                return savedClient;
            }
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        Client cachedClient = cache.get(id);
        if (cachedClient != null) {
            log.info("Retrieved client from cache: {}", cachedClient);
            return Optional.of(cachedClient);
        }
        return transactionManager.doInReadOnlyTransaction(session -> {
            Optional<Client> clientOptional = clientDataTemplate.findById(session, id);
            clientOptional.ifPresent(client -> cache.put(id, client));
            log.info("Retrieved client from DB: {}", clientOptional);
            return clientOptional;
        });
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {
            List<Client> clientList = clientDataTemplate.findAll(session);
            clientList.forEach(client -> cache.put(client.getId(), client));
            log.info("Retrieved all clients from DB: {}", clientList);
            return clientList;
        });
    }
}
