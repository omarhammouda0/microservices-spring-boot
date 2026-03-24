package com.userservice.repository;

import com.userservice.dto.UserResponseDTO;
import com.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {


    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email , Long id);

    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> getActiveUsers();

    Optional <User> findByEmailIgnoreCase(String email);

}
