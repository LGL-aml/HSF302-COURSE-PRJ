package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.Lecturer;
import com.jungle.courseshop.entity.LecturerStatus;
import com.jungle.courseshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LecturerRepo extends JpaRepository<Lecturer, Long> {
    boolean existsByUser(User user);

    List<Lecturer> findAllByStatus(LecturerStatus status);

    boolean existsByIdentityNumber(String identityNumber);

    Optional<Lecturer> findByIdentityNumber(String identityNumber);
}
