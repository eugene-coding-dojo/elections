package org.elections;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Elections {
    List<String> candidates = new ArrayList<>();
    List<String> officialCandidates = new ArrayList<>();
    ArrayList<Integer> votesWithoutDistricts = new ArrayList<>();
    Map<String, ArrayList<Integer>> votesWithDistricts;
    private boolean withDisctrict;

    public Elections(boolean withDistrict) {
        this.withDisctrict = withDistrict;

        votesWithDistricts = new HashMap<>();
        votesWithDistricts.put("District 1", new ArrayList<>());
        votesWithDistricts.put("District 2", new ArrayList<>());
        votesWithDistricts.put("District 3", new ArrayList<>());
    }

    public void addCandidate(String candidate) {
        officialCandidates.add(candidate);
        candidates.add(candidate);
        votesWithoutDistricts.add(0);
        votesWithDistricts.get("District 1").add(0);
        votesWithDistricts.get("District 2").add(0);
        votesWithDistricts.get("District 3").add(0);
    }

    public void voteFor(String elector, String candidate, String electorDistrict) {
        if (!withDisctrict) {
            if (candidates.contains(candidate)) {
                int index = candidates.indexOf(candidate);
                votesWithoutDistricts.set(index, votesWithoutDistricts.get(index) + 1);
            } else {
                candidates.add(candidate);
                votesWithoutDistricts.add(1);
            }
        } else {
            if (votesWithDistricts.containsKey(electorDistrict)) {
                ArrayList<Integer> districtVotes = votesWithDistricts.get(electorDistrict);
                if (candidates.contains(candidate)) {
                    int index = candidates.indexOf(candidate);
                    districtVotes.set(index, districtVotes.get(index) + 1);
                } else {
                    candidates.add(candidate);
                    votesWithDistricts.forEach((district, votes) ->{
                        votes.add(0);
                    });
                    districtVotes.set(candidates.size()-1, districtVotes.get(candidates.size()-1) + 1);
                }
            }
        }
    }

    public Map<String, String> results() {
        Map<String, String> results = new HashMap<>();
        Integer nbVotes = 0;
        Integer nullVotes = 0;
        Integer blankVotes = 0;
        int nbValidVotes = 0;

        if (!withDisctrict) {
            nbVotes = votesWithoutDistricts.stream().reduce(0, Integer::sum);
            for (int i = 0; i < officialCandidates.size(); i++) {
                int index = candidates.indexOf(officialCandidates.get(i));
                nbValidVotes += votesWithoutDistricts.get(index);
            }

            for (int i = 0; i < votesWithoutDistricts.size(); i++) {
                Integer candidatResult = (votesWithoutDistricts.get(i) * 100) / nbValidVotes;
                String candidate = candidates.get(i);
                if (officialCandidates.contains(candidate)) {
                    results.put(candidate, candidatResult.toString() + "%");
                } else {
                    if (candidates.get(i).isEmpty()) {
                        blankVotes += votesWithoutDistricts.get(i);
                    } else {
                        nullVotes += votesWithoutDistricts.get(i);
                    }
                }
            }
        } else {
            for (Map.Entry<String, ArrayList<Integer>> entry : votesWithDistricts.entrySet()) {
                ArrayList<Integer> districtVotes = entry.getValue();
                nbVotes += districtVotes.stream().reduce(0, Integer::sum);
            }

            for (int i = 0; i < officialCandidates.size(); i++) {
                int index = candidates.indexOf(officialCandidates.get(i));
                for (Map.Entry<String, ArrayList<Integer>> entry : votesWithDistricts.entrySet()) {
                    ArrayList<Integer> districtVotes = entry.getValue();
                    nbValidVotes += districtVotes.get(index);
                }
            }

            Map<String, Integer> officialCandidatesResult = new HashMap<>();
            for (int i = 0; i < officialCandidates.size(); i++) {
                officialCandidatesResult.put(candidates.get(i), 0);
            }
            for (Map.Entry<String, ArrayList<Integer>> entry : votesWithDistricts.entrySet()) {
                ArrayList<Integer> districtResult = new ArrayList<>();
                ArrayList<Integer> districtVotes = entry.getValue();
                for (int i = 0; i < districtVotes.size(); i++) {
                    Integer candidateResult = 0;
                    if (nbValidVotes != 0)
                        candidateResult = (districtVotes.get(i) * 100) / nbValidVotes;
                    String candidate = candidates.get(i);
                    if (officialCandidates.contains(candidate)) {
                        districtResult.add(candidateResult);
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
                Float ratioCandidate = ((float) officialCandidatesResult.get(candidates.get(i))) / officialCandidatesResult.size() * 100;
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                results.put(candidates.get(i), df.format(ratioCandidate) + "%");
            }
        }

        results.put("Blank", ((Integer) ((blankVotes * 100) / nbVotes)).toString() + "%");

        Integer nullResult = (nullVotes * 100) / nbVotes;
        results.put("Null", nullResult.toString() + "%");

        return results;
    }
}
