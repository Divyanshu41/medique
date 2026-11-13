package com.example.medique.repository;

import com.example.medique.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id = :id")
    Optional<User> findByIdForUpdate(@Param("id") Long id);

    Page<User> findByRole(String role, Pageable pageable);
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
    Page<User> findByRoleAndIsActive(String role, Boolean isActive, Pageable pageable);
    @Query("select u from User u where lower(u.name) like lower(concat('%', :keyword, '%')) or lower(u.email) like lower(concat('%', :keyword, '%'))")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email, Pageable pageable);
    List<User> findByRole(String role);
    Long countByRole(String role);
}
