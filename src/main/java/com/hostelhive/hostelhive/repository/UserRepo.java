package com.hostelhive.hostelhive.repository;

import com.hostelhive.hostelhive.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // ✅ Fetch users by role string (e.g., "ROLE_ADMIN")
    List<User> findByRole(String role);
}
