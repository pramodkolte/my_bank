package com.mybank.transaction.application.service;

import com.mybank.transaction.application.port.in.TransactionUseCase;
import com.mybank.transaction.domain.model.Transaction;
import com.mybank.transaction.domain.model.TransactionStatus;
import com.mybank.transaction.domain.model.TransactionType;
import com.mybank.transaction.domain.port.out.AccountClientPort;
import com.mybank.transaction.domain.port.out.TransactionEventPublisherPort;
import com.mybank.transaction.domain.port.out.TransactionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mybank.transaction.infrastructure.adapter.out.client.AccountDto;

import java.util.Optional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
    public Transaction initiateTransfer(UUID authenticatedUserId, UUID senderId, UUID receiverId, BigDecimal amount) {

        SecurityContext context = SecurityContextHolder.getContext();

        // Run both account fetches in parallel on virtual threads
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<Optional<AccountDto>> senderFuture =
                    CompletableFuture.supplyAsync(() -> {
                        SecurityContextHolder.setContext(context);
                        try {
                            return accountClientPort.getAccount(senderId);
                        } finally {
                            SecurityContextHolder.clearContext();
                        }
                    }, executor);

            CompletableFuture<Optional<AccountDto>> receiverFuture =
                    CompletableFuture.supplyAsync(() -> {
                        SecurityContextHolder.setContext(context);
                        try {
                            return accountClientPort.getAccountStatus(receiverId);
                        } finally {
                            SecurityContextHolder.clearContext();
                        }
                    }, executor);

            CompletableFuture.allOf(senderFuture, receiverFuture).join();

            AccountDto senderAccount = senderFuture.get()
                    .orElseThrow(() -> new IllegalStateException("Sender account not found."));
            AccountDto receiverAccount = receiverFuture.get()
                    .orElseThrow(() -> new IllegalStateException("Receiver account not found."));

            // Ownership check
            if (!senderAccount.getOwnerId().equals(authenticatedUserId)) {
                throw new AccessDeniedException("Access Denied: You do not own the sender account.");
            }

            // Status check
            if (!"ACTIVE".equalsIgnoreCase(senderAccount.getStatus()) || !"ACTIVE".equalsIgnoreCase(receiverAccount.getStatus())) {
                throw new IllegalStateException("One or both accounts are inactive.");
            }

        } catch (ExecutionException e) {
            throw new RuntimeException("Account verification failed", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Account verification interrupted", e);
        } catch (AccessDeniedException | IllegalStateException e) {
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

    @Override
    public List<Transaction> getTransactionHistory(UUID authenticatedUserId, UUID accountId, BigDecimal minAmount, BigDecimal maxAmount, TransactionType type, LocalDateTime startDate, LocalDateTime endDate) {
        // Verify ownership
        AccountDto account = accountClientPort.getAccount(accountId)
                .orElseThrow(() -> new IllegalStateException("Account not found."));
        
        if (!account.getOwnerId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("Access Denied: You do not own this account.");
        }

        return transactionRepositoryPort.findTransactions(accountId, minAmount, maxAmount, type, null, startDate, endDate);
    }

    @Override
    public List<Transaction> getAllTransactions(UUID accountId, BigDecimal minAmount, BigDecimal maxAmount, TransactionType type, TransactionStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        // Global search - no ownership check
        return transactionRepositoryPort.findTransactions(accountId, minAmount, maxAmount, type, status, startDate, endDate);
    }
}
