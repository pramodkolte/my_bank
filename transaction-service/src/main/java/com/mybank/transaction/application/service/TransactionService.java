package com.mybank.transaction.application.service;

import com.mybank.transaction.application.port.in.TransactionUseCase;
import com.mybank.transaction.domain.model.Transaction;
import com.mybank.transaction.domain.model.TransactionStatus;
import com.mybank.transaction.domain.model.TransactionType;
import com.mybank.transaction.domain.port.out.AccountClientPort;
import com.mybank.transaction.domain.port.out.TransactionEventPublisherPort;
import com.mybank.transaction.domain.port.out.TransactionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class TransactionService implements TransactionUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;
    private final TransactionEventPublisherPort transactionEventPublisherPort;
    private final AccountClientPort accountClientPort;

    @Override
    @Transactional
    public Transaction initiateTransfer(UUID senderId, UUID receiverId, BigDecimal amount) {

        // Run both account-active checks in parallel on virtual threads (stable API, Java 21+)
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<Boolean> senderFuture =
                    CompletableFuture.supplyAsync(() -> accountClientPort.isAccountActive(senderId), executor);
            CompletableFuture<Boolean> receiverFuture =
                    CompletableFuture.supplyAsync(() -> accountClientPort.isAccountActive(receiverId), executor);

            // Wait for both; propagates the first exception if any task fails
            CompletableFuture.allOf(senderFuture, receiverFuture).join();

            if (!senderFuture.get() || !receiverFuture.get()) {
                throw new IllegalStateException("One or both accounts are inactive or do not exist.");
            }
        } catch (ExecutionException e) {
            throw new RuntimeException("Account verification failed", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Account verification interrupted", e);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Account verification failed", e);
        }

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .senderAccountId(senderId)
                .receiverAccountId(receiverId)
                .amount(amount)
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.INITIATED)
                .build();

        Transaction saved = transactionRepositoryPort.save(transaction);
        // Saga Step 1: Emit event to be picked up by Account-Service
        transactionEventPublisherPort.publishTransactionInitiatedEvent(saved);

        return saved;
    }

    @Override
    @Transactional
    public void completeTransaction(UUID transactionId) {
        Transaction transaction = transactionRepositoryPort.findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepositoryPort.save(transaction);
        transactionEventPublisherPort.publishTransactionCompletedEvent(transaction);
    }

    @Override
    @Transactional
    public void failTransaction(UUID transactionId, String reason) {
        Transaction transaction = transactionRepositoryPort.findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setFailureReason(reason);
        transactionRepositoryPort.save(transaction);
        transactionEventPublisherPort.publishTransactionFailedEvent(transaction);
    }
}
