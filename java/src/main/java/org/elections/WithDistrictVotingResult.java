package org.elections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WithDistrictVotingResult extends VotingResultStrategy {
    private final List<String> districts;

    public WithDistrictVotingResult(Candidates candidates, Electors electors, List<String> districts) {
        super(candidates, electors);
        this.districts = districts;
    }

    @Override
    protected Map<String, Fraction> calculateResult() {
        Map<String, Long> officialCandidatesResult = new HashMap<>();
        for (int i = 0; i < candidates.officialCandidatesCount(); i++) {
            officialCandidatesResult.put(candidates.get(i), 0L);
        }
        for (String district : districts) {
            String districtWinner = votes.districtWinner(district);
            officialCandidatesResult.put(districtWinner, officialCandidatesResult.get(districtWinner) + 1);
        }
        return officialCandidatesResult.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Fraction.withNumeratorDenominator(e.getValue(), districts.size())));
    }
}
