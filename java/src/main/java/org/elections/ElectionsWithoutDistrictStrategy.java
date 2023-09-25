package org.elections;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class ElectionsWithoutDistrictStrategy extends ElectionsStrategy {
    ArrayList<Integer> votesWithoutDistricts = new ArrayList<>();

    @Override
    public void addCandidate(String candidate) {
        this.officialCandidates.add(candidate);
        this.candidates.add(candidate);
        this.votesWithoutDistricts.add(0);
    }

    @Override
    public void voteFor(String candidate, String electorDistrict) {
        if (this.candidates.contains(candidate)) {
            int index = this.candidates.indexOf(candidate);
            this.votesWithoutDistricts.set(index, this.votesWithoutDistricts.get(index) + 1);
        } else {
            this.candidates.add(candidate);
            this.votesWithoutDistricts.add(1);
        }
    }

    @Override
    public void calculateResults(Elections.VotesCountByType votes, Map<String, String> results) {
        votes.nbVotes = this.votesWithoutDistricts.stream().reduce(0, Integer::sum);
        for (int i = 0; i < this.officialCandidates.size(); i++) {
            int index = this.candidates.indexOf(this.officialCandidates.get(i));
            votes.nbValidVotes += this.votesWithoutDistricts.get(index);
        }

        for (int i = 0; i < this.votesWithoutDistricts.size(); i++) {
            Float candidatResult = ((float) this.votesWithoutDistricts.get(i) * 100) / votes.nbValidVotes;
            String candidate = this.candidates.get(i);
            if (this.officialCandidates.contains(candidate)) {
                results.put(candidate, String.format(Locale.FRENCH, "%.2f%%", candidatResult));
            } else {
                if (this.candidates.get(i).isEmpty()) {
                    votes.blankVotes += this.votesWithoutDistricts.get(i);
                } else {
                    votes.nullVotes += this.votesWithoutDistricts.get(i);
                }
            }
        }
    }

}
