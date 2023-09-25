package org.elections;

import java.util.*;

public class Elections {
    private static final String NO_DISTRICT = "No district";
    List<String> candidates = new ArrayList<>();
    List<String> officialCandidates = new ArrayList<>();
    Map<String, ArrayList<Integer>> votesWithoutDistrict;
    Map<String, ArrayList<Integer>> votesWithDistricts;
    private final Electors electors;
    private boolean withDistrict;

    public Elections(Map<String, List<String>> list, boolean withDistrict) {
        electors = Electors.fromMapByDistrict(list);
        this.withDistrict = withDistrict;

        votesWithoutDistrict = new HashMap<>();
        votesWithoutDistrict.put(NO_DISTRICT, new ArrayList<>());

        votesWithDistricts = new HashMap<>();
        votesWithDistricts.put("District 1", new ArrayList<>());
        votesWithDistricts.put("District 2", new ArrayList<>());
        votesWithDistricts.put("District 3", new ArrayList<>());
    }

    public void addOfficialCandidate(String candidate) {
        officialCandidates.add(candidate);
        candidates.add(candidate);

        votesWithoutDistrict.get(NO_DISTRICT).add(0);

        votesWithDistricts.get("District 1").add(0);
        votesWithDistricts.get("District 2").add(0);
        votesWithDistricts.get("District 3").add(0);
    }

    public void voteFor(String elector, String candidate, String electorDistrict) {
        if (!withDistrict) {
            countVote(candidate, votesWithoutDistrict, NO_DISTRICT);
        } else if (votesWithDistricts.containsKey(electorDistrict)) {
            countVote(candidate, votesWithDistricts, electorDistrict);
        }
    }

    private void countVote(String candidate, Map<String, ArrayList<Integer>> votesMap, String districtName) {
        if (!candidates.contains(candidate)) {
            addUnofficialCandidate(candidate, votesMap);
        }
        incrementCandidateVotesCounter(candidate, candidates, votesMap.get(districtName));
    }

    private void addUnofficialCandidate(String candidate, Map<String, ArrayList<Integer>> votesMap) {
        candidates.add(candidate);
        votesMap.forEach((district, votes) -> votes.add(0));
    }

    private void incrementCandidateVotesCounter(String candidate, List<String> candidates, ArrayList<Integer> votesByCandidate) {
        int index = candidates.indexOf(candidate);
        votesByCandidate.set(index, votesByCandidate.get(index) + 1);
    }

    public Map<String, String> results() {
        Map<String, String> results = new HashMap<>();
        int nbValidVotes = countOfficialCandidateVotes(withDistrict ? votesWithDistricts:votesWithoutDistrict);
        int nbVotes = countTotalVotes(withDistrict ? votesWithDistricts:votesWithoutDistrict);
        Fraction nullVotes = Fraction.withDenominator(nbVotes);
        Fraction blankVotes = Fraction.withDenominator(nbVotes);

        Map<String, Integer> officialCandidatesResult = new HashMap<>();
        for (int i = 0; i < officialCandidates.size(); i++) {
            officialCandidatesResult.put(candidates.get(i), 0);
        }
        if (!withDistrict) {
            for (Map.Entry<String, ArrayList<Integer>> entry : votesWithoutDistrict.entrySet()) {
                ArrayList<Integer> districtResult = fillVoteBuckets(nullVotes, blankVotes, entry);

                for (int i = 0; i < officialCandidates.size(); i++) {
                    officialCandidatesResult.put(officialCandidates.get(i), districtResult.get(i));
                }
            }
            for (int i = 0; i < officialCandidatesResult.size(); i++) {
                Fraction candidateResult = Fraction.withNumeratorDenominator(officialCandidatesResult.get(candidates.get(i)), nbValidVotes);
                results.put(candidates.get(i), String.format(Locale.FRENCH, "%.2f%%", candidateResult.asPercent()));
            }
        } else {
            for (Map.Entry<String, ArrayList<Integer>> entry : votesWithDistricts.entrySet()) {
                ArrayList<Integer> districtResult = fillVoteBuckets(nullVotes, blankVotes, entry);

                int districtWinnerIndex = 0;
                for (int i = 1; i < districtResult.size(); i++) {
                    if (districtResult.get(districtWinnerIndex) < districtResult.get(i))
                        districtWinnerIndex = i;
                }
                officialCandidatesResult.put(candidates.get(districtWinnerIndex), officialCandidatesResult.get(candidates.get(districtWinnerIndex)) + 1);
            }
            for (int i = 0; i < officialCandidatesResult.size(); i++) {
                Fraction ratio = Fraction.withNumeratorDenominator(officialCandidatesResult.get(candidates.get(i)), officialCandidatesResult.size());
                results.put(candidates.get(i), String.format(Locale.FRENCH, "%.2f%%", ratio.asPercent()));
            }
        }

        results.put("Blank", String.format(Locale.FRENCH, "%.2f%%", blankVotes.asPercent()));
        results.put("Null", String.format(Locale.FRENCH, "%.2f%%", nullVotes.asPercent()));

        int nbElectors = electors.size();
        Fraction abstentionResult = Fraction.withNumeratorDenominator(nbElectors - nbVotes, nbElectors);
        results.put("Abstention", String.format(Locale.FRENCH, "%.2f%%", abstentionResult.asPercent()));

        return results;
    }

    private ArrayList<Integer> fillVoteBuckets(Fraction nullVotes, Fraction blankVotes, Map.Entry<String, ArrayList<Integer>> entry) {
        ArrayList<Integer> districtResult = new ArrayList<>();
        ArrayList<Integer> districtVotes = entry.getValue();
        for (int i = 0; i < districtVotes.size(); i++) {
            String candidate = candidates.get(i);
            if (officialCandidates.contains(candidate)) {
                districtResult.add(districtVotes.get(i));
            } else {
                if (candidates.get(i).isEmpty()) {
                    blankVotes.addToNumerator(districtVotes.get(i));
                } else {
                    nullVotes.addToNumerator(districtVotes.get(i));
                }
            }
        }
        return districtResult;
    }

    private int countOfficialCandidateVotes(Map<String, ArrayList<Integer>> entries) {
        int nbValidVotes = 0;
        for (int i = 0; i < officialCandidates.size(); i++) {
            for (Map.Entry<String, ArrayList<Integer>> entry : entries.entrySet()) {
                ArrayList<Integer> districtVotes = entry.getValue();
                nbValidVotes += districtVotes.get(i);
            }
        }
        return nbValidVotes;
    }

    private Integer countTotalVotes(Map<String, ArrayList<Integer>> entries) {
        int totalVotes = 0;
        for (Map.Entry<String, ArrayList<Integer>> entry : entries.entrySet()) {
            totalVotes += entry.getValue().stream().reduce(0, Integer::sum);
        }
        return totalVotes;
    }
}
