package org.elections;

import java.util.Map;
import java.util.stream.Collectors;

public class NoDistrictVotingResult extends VotingResultStrategy {
    public NoDistrictVotingResult(Candidates candidates, Electors electors) {
        super(candidates, electors);
    }

    @Override
    protected Map<String, Fraction> calculateResult() {
        long validVotes = votes.countValid();
        return votes.resultForNoDistrict().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Fraction.withNumeratorDenominator(e.getValue(), validVotes)));
    }

}
