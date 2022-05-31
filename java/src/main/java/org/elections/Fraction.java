package org.elections;

public class Fraction {
    private final long numerator;
    private final long denominator;

    public static Fraction withNumeratorDenominator(long numerator, int denominator) {
        return new Fraction(numerator, denominator);
    }

    private Fraction(long numerator, long denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public float asPercent() {
        if (this.denominator == 0) {
            return 0;
        }
        return (float)this.numerator * 100 / this.denominator;
    }
}
