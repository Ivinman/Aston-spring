package ru.aston.module4.springRep;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.aston.module4.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}
