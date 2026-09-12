package com.philosophia.exceptions;

public class PlanFoundException extends RuntimeException {
    public PlanFoundException(String message) {
        super(message);
    }
    public PlanFoundException(Long planId) {
        super("plan introuvable avec l'id : " + planId);
    }
}
