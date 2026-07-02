package com.leavemanagement.leave.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(int requested, int available) {
        super("Solde insuffisant: " + requested + " jours demandés, " + available + " jours disponibles");
    }
}
