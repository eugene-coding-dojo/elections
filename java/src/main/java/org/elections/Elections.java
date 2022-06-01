package org.elections;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Elections {
    private final Electors electors;
    private final Candidates candidates;
    private final Votes votes;
    private final VotingResultStrategy voting;

    public Elections(Map<String, List<String>> list, boolean withDistrict) {
        electors = Electors.fromMapByDistrict(list);
        candidates = new Candidates();
        votes = new Votes();

        List<String> districts = new ArrayList<>();
        districts.add("District 1");
        districts.add("District 2");
        districts.add("District 3");

        voting = withDistrict
                ? new WithDistrictVotingResult(candidates, electors, districts)
                :new NoDistrictVotingResult(candidates, electors);
    }

    public void addOfficialCandidate(String candidate) {
        candidates.addOfficial(candidate);
    }

    public void voteFor(String electorName, String candidateName, String electorDistrict) {
        if (candidates.isUnregistered(candidateName)) {
            candidates.addUnofficial(candidateName);
        }

        votes.registerVote(electorDistrict,
                electors.findByName(electorName),
                candidates.findByName(candidateName));
    }

    public Map<String, String> results() {
        FormattedResult formattedResult = new FormattedResult(Locale.FRENCH, "%.2f%%");
        voting.count(votes).printOn(formattedResult);
        return formattedResult.result();
    }
}
