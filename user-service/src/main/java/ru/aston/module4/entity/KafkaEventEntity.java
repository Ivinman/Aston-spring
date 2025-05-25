package ru.aston.module4.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.aston.module4.dto.UserEventDto;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "kafka_event_fallback")
@Getter
@NoArgsConstructor
public class KafkaEventEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String topic;

	@Type(JsonBinaryType.class)
	@Column(columnDefinition = "jsonb")
	private UserEventDto payload;

	private LocalDateTime createdAt;

	public KafkaEventEntity(String topic, UserEventDto payload, LocalDateTime createdAt) {
		this.topic = topic;
		this.payload = payload;
		this.createdAt = createdAt;
	}
}
