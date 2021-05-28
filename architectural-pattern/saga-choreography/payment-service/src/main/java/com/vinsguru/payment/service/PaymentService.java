package com.vinsguru.payment.service;

import com.vinsguru.dto.PaymentDto;
import com.vinsguru.dto.PurchaseOrderDto;
import com.vinsguru.events.order.OrderEvent;
import com.vinsguru.events.payment.PaymentEvent;
import com.vinsguru.events.payment.PaymentStatus;
import com.vinsguru.payment.entity.UserTransaction;
import com.vinsguru.payment.repository.UserBalanceRepository;
import com.vinsguru.payment.repository.UserTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    @Autowired
    private UserBalanceRepository balanceRepository;

    @Autowired
    private UserTransactionRepository transactionRepository;

    private Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    @Transactional
    public PaymentEvent newOrderEvent(OrderEvent orderEvent){
        PurchaseOrderDto purchaseOrder = orderEvent.getPurchaseOrder();
        PaymentDto dto = new PaymentDto(purchaseOrder.getOrderId(), purchaseOrder.getUserId(), purchaseOrder.getPrice());
        return this.balanceRepository.findById(purchaseOrder.getUserId())
                .filter(ub -> ub.getBalance() >= purchaseOrder.getPrice())
                .map(ub -> {
                    ub.setBalance(ub.getBalance() - purchaseOrder.getPrice());
                    try {
                        this.transactionRepository.save(UserTransaction.of(purchaseOrder.getOrderId(), purchaseOrder.getUserId(), purchaseOrder.getPrice()));
                    } catch (Exception exception){
                        LOGGER.error("Exception", exception);
                        return null;
                    }
                    return new PaymentEvent(dto, PaymentStatus.RESERVED);
                })
                .orElse(new PaymentEvent(dto, PaymentStatus.REJECTED));
    }

    @Transactional
    public void cancelOrderEvent(OrderEvent orderEvent){
        this.transactionRepository.findById(orderEvent.getPurchaseOrder().getOrderId())
                .ifPresent(ut -> {
                    this.transactionRepository.delete(ut);
                    this.balanceRepository.findById(ut.getUserId())
                            .ifPresent(ub -> ub.setBalance(ub.getBalance() + ut.getAmount()));
                });
    }
}
