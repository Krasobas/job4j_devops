package ru.job4j.devops.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.devops.models.User;

public interface UserRepository extends CrudRepository<User, Long> {
}
