package ru.aston.module4.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Setter
@Getter
@NoArgsConstructor
public class User {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    @Email
    @NotNull
    private String email;

    @Column(name = "age", nullable = false)
    private int age;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    public User(long id, String name, int age, String email) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof User other)) return false;
        if (!other.canEqual(this)) return false;
        if (this.id != other.getId()) return false;
        final LocalDateTime this$createdAt = this.createdAt;
        final LocalDateTime other$createdAt = other.getCreatedAt();
        return this$createdAt.equals(other$createdAt);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof User;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long $id = this.id;
        result = result * PRIME + Long.hashCode($id);
        final Object $createdAt = this.createdAt;
        result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
        return result;
    }
}
