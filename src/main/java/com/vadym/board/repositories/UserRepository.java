package com.vadym.board.repositories;

import com.vadym.board.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByActivationCode(String code);
    User findByNickname(String nickname);
    List<User> findByNicknameContainingIgnoreCase(String nickname);
}
