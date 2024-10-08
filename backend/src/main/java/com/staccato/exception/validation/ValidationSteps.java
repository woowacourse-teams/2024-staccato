package com.staccato.exception.validation;

import jakarta.validation.GroupSequence;

public class ValidationSteps {
    public interface FirstStep {
    }

    public interface SecondStep {
    }

    @GroupSequence({FirstStep.class, SecondStep.class})
    public interface ValidationSequence {
    }
}
