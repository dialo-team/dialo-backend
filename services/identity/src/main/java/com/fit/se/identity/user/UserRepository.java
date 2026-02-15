package com.fit.se.identity.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<UserDetails> findByPhone(String phone);

    boolean existsUserByPhone(String phone);
}
