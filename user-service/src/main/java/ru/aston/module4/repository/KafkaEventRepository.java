package ru.aston.module4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aston.module4.entity.KafkaEventEntity;

public interface KafkaEventRepository extends JpaRepository<KafkaEventEntity, Long> {
}

