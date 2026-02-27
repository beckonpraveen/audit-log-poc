package org.example.helpdesk.repository;

import org.example.helpdesk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
