package org.elections;

public class Fraction {
    private int numerator;
    private int denominator;

    public static Fraction withDenominator_(int denominator) {
        return new Fraction().setNumerator_denominator_(0, denominator);
    }

    public static Fraction withNumeratorDenominator(int numerator, int denominator) {
        return new Fraction().setNumerator_denominator_(numerator, denominator);
    }

    private Fraction() {}

    private Fraction setNumerator_denominator_(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
        return this;
    }

    public float asPercent() {
        if (this.denominator == 0) {
            return 0;
        }
        return (float)this.numerator * 100 / this.denominator;
    }
}
