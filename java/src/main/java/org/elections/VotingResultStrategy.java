package org.elections;

import java.util.Map;

public abstract class VotingResultStrategy {
    protected final Candidates candidates;
    protected final Electors electors;
    protected Map<String, Fraction> result;
    protected Votes votes;

    public VotingResultStrategy(Candidates candidates, Electors electors) {
        this.candidates = candidates;
        this.electors = electors;
    }

    public void printOn(FormattedResult formattedResult) {
        Fraction zero = Fraction.withNumeratorDenominator(0L, 1L);
        long nbVotes = votes.countAllVotes();

        for (String candidate : candidates.officialCandidates()) {
            formattedResult.addCandidateResult(candidate, result.getOrDefault(candidate, zero));
        }

        formattedResult.addBlankVotes(Fraction.withNumeratorDenominator(votes.countBlanks(), nbVotes));
        formattedResult.addNullVotes(Fraction.withNumeratorDenominator(votes.countNulls(), nbVotes));
        formattedResult.addAbstention(Fraction.withNumeratorDenominator(electors.size() - nbVotes, electors.size()));
    }

    public VotingResultStrategy count(Votes votes) {
        this.votes = votes;
        this.result = calculateResult();
        return this;
    }

    protected abstract Map<String, Fraction> calculateResult();
}
