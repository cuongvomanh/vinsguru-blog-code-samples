package com.vinsguru.payment.service;

import com.vinsguru.dto.InventoryDto;
import com.vinsguru.dto.PaymentDto;
import com.vinsguru.payment.entity.UserBalance;
import com.vinsguru.payment.repository.UserBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentQueryService {

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    public PaymentDto findByUserId(Integer userId){
        Optional<UserBalance> userBalance = userBalanceRepository.findByUserId(userId);
        userBalance.orElseThrow(BadRequestCustomException::new);
        return userBalance.map(e -> PaymentDto.of(null, e.getUserId(), null, e.getBalance())).orElseThrow(RuntimeException::new);
    }
}
