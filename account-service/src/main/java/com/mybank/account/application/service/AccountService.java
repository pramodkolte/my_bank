package com.mybank.account.application.service;

import com.mybank.account.application.exception.AccountNotFoundException;
import com.mybank.account.application.exception.InsufficientBalanceException;
import com.mybank.account.application.port.in.AccountUseCase;
import com.mybank.account.domain.model.Account;
import com.mybank.account.domain.model.AccountStatus;
import com.mybank.account.domain.model.Currency;
import com.mybank.account.domain.port.out.AccountEventPublisherPort;
import com.mybank.account.domain.port.out.AccountRepositoryPort;
import com.mybank.account.domain.port.out.IdentityProviderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService implements AccountUseCase {

    private final AccountRepositoryPort accountRepositoryPort;
    private final IdentityProviderPort identityProviderPort;
    private final AccountEventPublisherPort eventPublisherPort;

    @Override
    public Account getAccount(UUID accountId) {
        return accountRepositoryPort.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));
    }

    @Override
    @Transactional
    public Account createAccount(UUID ownerId, Currency currency) {
        if (!identityProviderPort.verifyUserExists(ownerId)) {
            throw new IllegalArgumentException("User verification failed for ID: " + ownerId);
        }

        Account account = Account.builder()
                .id(UUID.randomUUID())
                .ownerId(ownerId)
                .balance(BigDecimal.ZERO)
                .currency(currency)
                .status(AccountStatus.ACTIVE)
                .build();

        Account saved = accountRepositoryPort.save(account);
        eventPublisherPort.publishAccountCreatedEvent(saved);
        return saved;
    }

    @Override
    @Transactional
    public Account updateBalance(UUID accountId, BigDecimal amount) {
        Account account = accountRepositoryPort.findLockedById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active.");
        }

        BigDecimal newBalance = account.getBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for account: " + accountId);
        }

        account.setBalance(newBalance);
        return accountRepositoryPort.save(account);
    }

    @Override
    @Transactional
    public void processTransfer(UUID transactionId, UUID senderId, UUID receiverId, BigDecimal amount) {
        try {
            // Lock ordering to prevent deadlocks
            UUID firstLock = senderId.compareTo(receiverId) < 0 ? senderId : receiverId;
            UUID secondLock = senderId.compareTo(receiverId) < 0 ? receiverId : senderId;

            Account first = accountRepositoryPort.findLockedById(firstLock)
                .orElseThrow(() -> new AccountNotFoundException("Account not found id: " + firstLock));
            Account second = accountRepositoryPort.findLockedById(secondLock)
                .orElseThrow(() -> new AccountNotFoundException("Account not found id: " + secondLock));
                
            Account sender = first.getId().equals(senderId) ? first : second;
            Account receiver = first.getId().equals(receiverId) ? first : second;
            
            if (sender.getStatus() != AccountStatus.ACTIVE || receiver.getStatus() != AccountStatus.ACTIVE) {
                 eventPublisherPort.publishTransactionFailedEvent(transactionId, "Accounts are not active");
                 return;
            }
            if (sender.getBalance().compareTo(amount) < 0) {
                 eventPublisherPort.publishTransactionFailedEvent(transactionId, "Insufficient funds");
                 return;
            }
            if (sender.getCurrency() != receiver.getCurrency()) {
                 eventPublisherPort.publishTransactionFailedEvent(transactionId, "Currency mismatch not supported");
                 return;
            }
            
            sender.setBalance(sender.getBalance().subtract(amount));
            receiver.setBalance(receiver.getBalance().add(amount));
            accountRepositoryPort.save(sender);
            accountRepositoryPort.save(receiver);
            
            eventPublisherPort.publishTransactionSuccessEvent(transactionId);
        } catch(Exception e) {
            eventPublisherPort.publishTransactionFailedEvent(transactionId, e.getMessage());
        }
    }
}
