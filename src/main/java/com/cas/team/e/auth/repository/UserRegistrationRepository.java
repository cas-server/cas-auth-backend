package com.cas.team.e.auth.repository;

import com.cas.team.e.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface UserRegistrationRepository extends JpaRepository<User, Integer> {
    // User findUserByUsernameAndPassword(String username, String password);
    User findUserByUsername(String username);
}
