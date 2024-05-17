package com.QuizBowl.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<Client, Long> {
    @Query(value="select client from Client client where client.userName = ?1 and client.password = ?2")
    Client getClientLogin(String userName, String password);

    Client getClientByUserName(String userName);
}
