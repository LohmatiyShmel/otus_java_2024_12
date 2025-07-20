package ru.petrelevich.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import ru.petrelevich.domain.Message;
import ru.petrelevich.domain.MessageDto;
import ru.petrelevich.service.DataStore;

@RestController
public class DataController {
    private static final Logger log = LoggerFactory.getLogger(DataController.class);
    private final DataStore dataStore;
    private final Scheduler workerPool;
    private final String allReadOnlyRoomId;

    public DataController(
            DataStore dataStore, Scheduler workerPool, @Value("${env.allReadOnlyRoomId}") String allReadOnlyRoomId) {
        this.dataStore = dataStore;
        this.workerPool = workerPool;
        this.allReadOnlyRoomId = allReadOnlyRoomId;
    }

    @PostMapping(value = "/msg/{roomId}")
    public Mono<Long> messageFromChat(@PathVariable("roomId") String roomId, @RequestBody MessageDto messageDto) {
        if (allReadOnlyRoomId.equals(roomId)) {
            return Mono.error(new IllegalArgumentException("Отправка сообщений в комнату 1408 запрещена"));
        }

        var messageStr = messageDto.messageStr();

        var msgId = Mono.just(new Message(null, roomId, messageStr))
                .doOnNext(msg -> log.info("messageFromChat:{}", msg))
                .flatMap(dataStore::saveMessage)
                .publishOn(workerPool)
                .doOnNext(msgSaved -> log.info("msgSaved id:{}", msgSaved.id()))
                .map(Message::id)
                .subscribeOn(workerPool);

        log.info("messageFromChat, roomId:{}, msg:{} done", roomId, messageStr);
        return msgId;
    }

    @GetMapping(value = "/msg/{roomId}", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MessageDto> getMessagesByRoomId(@PathVariable("roomId") String roomId) {
        if (allReadOnlyRoomId.equals(roomId)) {
            return dataStore
                    .loadAllMessages()
                    .map(message -> new MessageDto(message.msgText()))
                    .doOnNext(msgDto -> log.info("msgDto:{}", msgDto))
                    .subscribeOn(workerPool);
        } else {
            return Mono.just(roomId)
                    .doOnNext(room -> log.info("getMessagesByRoomId, room:{}", room))
                    .flatMapMany(dataStore::loadMessages)
                    .map(message -> new MessageDto(message.msgText()))
                    .doOnNext(msgDto -> log.info("msgDto:{}", msgDto))
                    .subscribeOn(workerPool);
        }
    }
}
