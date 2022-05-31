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
        Map<String, String> results = new HashMap<>();
        long nbValidVotes = votes.countValid();
        long nbVotes = votes.countAllVotes();

        Map<String, Long> officialCandidatesResult = new HashMap<>();
        for (int i = 0; i < candidates.officialCandidatesCount(); i++) {
            officialCandidatesResult.put(candidates.get(i), 0L);
        }
        if (!withDistrict) {
            Map<String, Long> districtResultByCandidate = votes.resultForNoDistrict();

            for (int i = 0; i < candidates.officialCandidatesCount(); i++) {
                final String candidateName = candidates.get(i);
                officialCandidatesResult.put(candidateName, districtResultByCandidate.getOrDefault(candidateName, 0L));
            }
            for (int i = 0; i < officialCandidatesResult.size(); i++) {
                Fraction candidateResult = Fraction.withNumeratorDenominator(officialCandidatesResult.get(candidates.get(i)), nbValidVotes);
                results.put(candidates.get(i), String.format(Locale.FRENCH, "%.2f%%", candidateResult.asPercent()));
            }
        } else {
            for (String district : districts) {
                String districtWinner = votes.districtWinner(district);
                officialCandidatesResult.put(districtWinner, officialCandidatesResult.get(districtWinner) + 1);
            }
            for (int i = 0; i < officialCandidatesResult.size(); i++) {
                Fraction ratio = Fraction.withNumeratorDenominator(officialCandidatesResult.get(candidates.get(i)), officialCandidatesResult.size());
                results.put(candidates.get(i), String.format(Locale.FRENCH, "%.2f%%", ratio.asPercent()));
            }
        }

        Fraction blanks = Fraction.withNumeratorDenominator(votes.countBlanks(), nbVotes);
        results.put("Blank", String.format(Locale.FRENCH, "%.2f%%", blanks.asPercent()));

        Fraction nulls = Fraction.withNumeratorDenominator(votes.countNulls(), nbVotes);
        results.put("Null", String.format(Locale.FRENCH, "%.2f%%", nulls.asPercent()));

        int nbElectors = electors.size();
        Fraction abstentionResult = Fraction.withNumeratorDenominator(nbElectors - nbVotes, nbElectors);
        results.put("Abstention", String.format(Locale.FRENCH, "%.2f%%", abstentionResult.asPercent()));

        return results;
    }
}
