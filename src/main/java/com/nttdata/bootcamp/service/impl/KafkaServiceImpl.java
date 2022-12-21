package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.BootCoin;
import com.nttdata.bootcamp.entity.dto.VirtualCoinKafkaDto;
import com.nttdata.bootcamp.events.BootCoinCreatedEventKafka;
import com.nttdata.bootcamp.events.EventKafka;
import com.nttdata.bootcamp.repository.BootCoinRepository;
import com.nttdata.bootcamp.service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaServiceImpl implements KafkaService {

    @Autowired
    private BootCoinRepository bootCoinRepository;

    @KafkaListener(
            topics = "${topic.bootCoin.name:topic_bootCoin}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "grupo1")
    public void consumerSave(EventKafka<?> eventKafka) {
        if (eventKafka.getClass().isAssignableFrom(BootCoinCreatedEventKafka.class)) {
            BootCoinCreatedEventKafka createdEvent = (BootCoinCreatedEventKafka) eventKafka;
            log.info("Received Data created event .... with Id={}, data={}",
                    createdEvent.getId(),
                    createdEvent.getData().toString());
            VirtualCoinKafkaDto virtualCoinKafkaDto = ((BootCoinCreatedEventKafka) eventKafka).getData();
            BootCoin bootCoin = new BootCoin();
            bootCoin.setMount(virtualCoinKafkaDto.getMount());
            bootCoin.setCellNumber(virtualCoinKafkaDto.getCellNumberReceive());

            this.bootCoinRepository.save(bootCoin).subscribe();
        }
    }

}
