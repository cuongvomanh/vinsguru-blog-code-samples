package com.vinsguru.payment.repository;

import com.vinsguru.payment.entity.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalance, Integer> {
    Optional<UserBalance> findByUserId(Integer userId);
}
