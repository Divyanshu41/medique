package com.example.medique.repository;

import com.example.medique.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // for login

    // Pagination support
    Page<User> findByRole(String role, Pageable pageable);

    Page<User> findByIsActive(Boolean isActive, Pageable pageable);

    Page<User> findByRoleAndIsActive(String role, Boolean isActive, Pageable pageable);

    // Search functionality
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String email, Pageable pageable);

    List<User> findByRole(String role);

    Long countByRole(String role);

    Boolean existsByEmail(String email);
}
