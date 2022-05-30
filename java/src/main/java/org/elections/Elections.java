package org.elections;

import java.util.*;

public class Elections {
    private static final String NO_DISTRICT = "No district";
    List<String> candidates = new ArrayList<>();
    List<String> officialCandidates = new ArrayList<>();
    Map<String, ArrayList<Integer>> votesWithoutDistrict;
    Map<String, ArrayList<Integer>> votesWithDistricts;
    private Map<String, List<String>> list;
    private boolean withDistrict;

    public Elections(Map<String, List<String>> list, boolean withDistrict) {
        this.list = list;
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
        Integer nullVotes = 0;
        Integer blankVotes = 0;
        int nbValidVotes = countOfficialCandidateVotes(withDistrict ? votesWithDistricts:votesWithoutDistrict);

        if (!withDistrict) {
            final ArrayList<Integer> votesForNoDistrict = votesWithoutDistrict.get(NO_DISTRICT);
            for (int i = 0; i < votesForNoDistrict.size(); i++) {
                String candidate = candidates.get(i);
                if (officialCandidates.contains(candidate)) {
                    Fraction candidateResult = Fraction.withNumeratorDenominator(votesForNoDistrict.get(i), nbValidVotes);
                    results.put(candidate, String.format(Locale.FRENCH, "%.2f%%", candidateResult.asPercent()));
                } else {
                    if (candidates.get(i).isEmpty()) {
                        blankVotes += votesForNoDistrict.get(i);
                    } else {
                        nullVotes += votesForNoDistrict.get(i);
                    }
                }
            }
        } else {
            Map<String, Integer> officialCandidatesResult = new HashMap<>();
            for (int i = 0; i < officialCandidates.size(); i++) {
                officialCandidatesResult.put(candidates.get(i), 0);
            }
            for (Map.Entry<String, ArrayList<Integer>> entry : votesWithDistricts.entrySet()) {
                ArrayList<Float> districtResult = new ArrayList<>();
                ArrayList<Integer> districtVotes = entry.getValue();
                for (int i = 0; i < districtVotes.size(); i++) {
                    Fraction candidateResult = Fraction.withNumeratorDenominator(districtVotes.get(i), nbValidVotes);
                    String candidate = candidates.get(i);
                    if (officialCandidates.contains(candidate)) {
                        districtResult.add(candidateResult.asPercent());
                    } else {
                        if (candidates.get(i).isEmpty()) {
                            blankVotes += districtVotes.get(i);
                        } else {
                            nullVotes += districtVotes.get(i);
                        }
                    }
                }
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

        int nbVotes = countTotalVotes(withDistrict ? votesWithDistricts:votesWithoutDistrict);

        Fraction blankResult = Fraction.withNumeratorDenominator(blankVotes, nbVotes);
        results.put("Blank", String.format(Locale.FRENCH, "%.2f%%", blankResult.asPercent()));

        Fraction nullResult = Fraction.withNumeratorDenominator(nullVotes, nbVotes);
        results.put("Null", String.format(Locale.FRENCH, "%.2f%%", nullResult.asPercent()));

        int nbElectors = list.values().stream().map(List::size).reduce(0, Integer::sum);
        Fraction abstentionResult = Fraction.withNumeratorDenominator(nbElectors - nbVotes, nbElectors);
        results.put("Abstention", String.format(Locale.FRENCH, "%.2f%%", abstentionResult.asPercent()));

        return results;
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
