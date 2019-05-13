package com.github.senyast4745.db;

import com.github.senyast4745.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    default void checkAndSaveUser(User user) {
        if (findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicateDataException("This user is already exist in database");
        }
        this.save(user);
    }

    default void checkAndSaveUsers(Iterable<User> users) {

        users.forEach((user) -> {
                    if (findByUsername(user.getUsername()).isPresent()) {
                        throw new DuplicateDataException("This user is already exist in database");
                    }
                }
        );

        this.saveAll(users);
    }
}
