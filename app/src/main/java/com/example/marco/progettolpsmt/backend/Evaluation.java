package com.example.marco.progettolpsmt.backend;

/**
 * Evaluation enum class to represent an evaluation for the complexity-and-amount, used for computing the time required
 */
public enum Evaluation {
    SUPER_EASY(0.50),
    EASY(0.75),
    REGULAR(1.00),
    HARD(1.25),
    SUPER_HARD(1.50);

    private double value;

    Evaluation(double value) {
        this.value = value;
    }

    /**
     * Return the percentage of time required for the evaluation.
     * @return percentage of time required
     */
    public double getValue() {
        return value;
    }
}
