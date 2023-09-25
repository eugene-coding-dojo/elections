package org.elections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ElectionsWithDistrictStrategy extends ElectionsStrategy {
    private final Map<String, ArrayList<Integer>> votesWithDistricts = new HashMap<>();

    public ElectionsWithDistrictStrategy() {
        super();
        votesWithDistricts.put("District 1", new ArrayList<>());
        votesWithDistricts.put("District 2", new ArrayList<>());
        votesWithDistricts.put("District 3", new ArrayList<>());
    }

    @Override
    public void addCandidate(String candidate) {
        officialCandidates.add(candidate);
        candidates.add(candidate);
        votesWithDistricts.get("District 1").add(0);
        votesWithDistricts.get("District 2").add(0);
        votesWithDistricts.get("District 3").add(0);
    }

    @Override
    public void voteFor(String candidate, String electorDistrict) {
        if (votesWithDistricts.containsKey(electorDistrict)) {
            ArrayList<Integer> districtVotes = votesWithDistricts.get(electorDistrict);
            if (candidates.contains(candidate)) {
                int index = candidates.indexOf(candidate);
                districtVotes.set(index, districtVotes.get(index) + 1);
            } else {
                candidates.add(candidate);
                votesWithDistricts.forEach((district, votes) -> votes.add(0));
                districtVotes.set(candidates.size() - 1, districtVotes.get(candidates.size() - 1) + 1);
            }
        }

    }

    @Override
    public void calculateResults(Elections.VotesCountByType votes, Map<String, String> results) {
        for (Map.Entry<String, ArrayList<Integer>> entry : votesWithDistricts.entrySet()) {
            ArrayList<Integer> districtVotes = entry.getValue();
            votes.nbVotes += districtVotes.stream().reduce(0, Integer::sum);
        }

        for (int i = 0; i < officialCandidates.size(); i++) {
            int index = candidates.indexOf(officialCandidates.get(i));
            for (Map.Entry<String, ArrayList<Integer>> entry : votesWithDistricts.entrySet()) {
                ArrayList<Integer> districtVotes = entry.getValue();
                votes.nbValidVotes += districtVotes.get(index);
            }
        }

        Map<String, Integer> officialCandidatesResult = new HashMap<>();
        for (int i = 0; i < officialCandidates.size(); i++) {
            officialCandidatesResult.put(candidates.get(i), 0);
        }
        for (Map.Entry<String, ArrayList<Integer>> entry : votesWithDistricts.entrySet()) {
            ArrayList<Float> districtResult = new ArrayList<>();
            ArrayList<Integer> districtVotes = entry.getValue();
            for (int i = 0; i < districtVotes.size(); i++) {
                float candidateResult = 0;
                if (votes.nbValidVotes != 0)
                    candidateResult = ((float) districtVotes.get(i) * 100) / votes.nbValidVotes;
                String candidate = candidates.get(i);
                if (officialCandidates.contains(candidate)) {
                    districtResult.add(candidateResult);
                } else {
                    if (candidates.get(i).isEmpty()) {
                        votes.blankVotes += districtVotes.get(i);
                    } else {
                        votes.nullVotes += districtVotes.get(i);
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
            Float ratioCandidate = ((float) officialCandidatesResult.get(candidates.get(i))) / officialCandidatesResult.size() * 100;
            results.put(candidates.get(i), String.format(Locale.FRENCH, "%.2f%%", ratioCandidate));
        }
    }

}
