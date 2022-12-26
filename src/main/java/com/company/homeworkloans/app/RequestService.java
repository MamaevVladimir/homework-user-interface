package com.company.homeworkloans.app;

import com.company.homeworkloans.entity.Client;
import com.company.homeworkloans.entity.Loan;
import com.company.homeworkloans.entity.LoanStatus;
import io.jmix.core.DataManager;
import io.jmix.core.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class RequestService {
    @Autowired
    DataManager dataManager;

    public boolean isValidClient(Client client) {
        return client != null;
    }

    public boolean isValidAmount(BigDecimal amount) {
        return amount != null && (amount.compareTo(BigDecimal.ZERO) > 0);
    }

    public Loan createLoan(Client client, BigDecimal amount) {
        Loan loan = dataManager.create(Loan.class);
        loan.setClient(client);
        loan.setAmount(amount);
        loan.setRequestDate(LocalDate.now());
        loan.setStatus(LoanStatus.REQUESTED);

        return dataManager.save(loan);
    }

    public List<Client> loadClients() {
        return dataManager.load(Client.class).all().list();
    }

    public void setApprovedStatus(UUID loanId) {
        Loan approved = dataManager.load(Loan.class)
                .id(loanId)
                .one();

        approved.setStatus(LoanStatus.APPROVED);
        dataManager.save(approved);
    }

    public void setRejectedStatus(UUID loanId) {
        Loan rejected = dataManager.load(Loan.class)
                .id(loanId)
                .one();

        rejected.setStatus(LoanStatus.REJECTED);
        dataManager.save(rejected);
    }

    public void deleteLoan(UUID loanId) {
        dataManager.remove(Id.of(loanId, Loan.class));
    }
}