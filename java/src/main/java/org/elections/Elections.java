package org.elections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Elections {
    private final Electors electors;
    private final Candidates candidates;
    private final Votes votes;
    private boolean withDistrict;
    private final List<String> districts = new ArrayList<>();

    public Elections(Map<String, List<String>> list, boolean withDistrict) {
        electors = Electors.fromMapByDistrict(list);
        candidates = new Candidates();
        votes = new Votes();

        this.withDistrict = withDistrict;

        districts.add("District 1");
        districts.add("District 2");
        districts.add("District 3");
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
        long nbValidVotes = votes.countValid();
        long nbVotes = votes.countAllVotes();

        if (!withDistrict) {
            Map<String, Long> districtResultByCandidate = votes.resultForNoDistrict();

            for (String candidate : candidates.officialCandidates()) {
                Fraction candidateResult = Fraction.withNumeratorDenominator(districtResultByCandidate.getOrDefault(candidate,0L), nbValidVotes);
                formattedResult.addCandidateResult(candidate, candidateResult);
            }

        } else {
            Map<String, Long> officialCandidatesResult = new HashMap<>();
            for (int i = 0; i < candidates.officialCandidatesCount(); i++) {
                officialCandidatesResult.put(candidates.get(i), 0L);
            }
            for (String district : districts) {
                String districtWinner = votes.districtWinner(district);
                officialCandidatesResult.put(districtWinner, officialCandidatesResult.get(districtWinner) + 1);
            }
            for (String candidate : candidates.officialCandidates()) {
                Fraction ratio = Fraction.withNumeratorDenominator(officialCandidatesResult.getOrDefault(candidate, 0L), districts.size());
                formattedResult.addCandidateResult(candidate, ratio);
            }
        }

        formattedResult.addBlankVotes(Fraction.withNumeratorDenominator(votes.countBlanks(), nbVotes));
        formattedResult.addNullVotes(Fraction.withNumeratorDenominator(votes.countNulls(), nbVotes));
        formattedResult.addAbstention(Fraction.withNumeratorDenominator(electors.size() - nbVotes, electors.size()));

        return formattedResult.result();
    }
}
