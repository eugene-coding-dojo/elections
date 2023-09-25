package org.elections;

import java.text.DecimalFormat;
import java.util.*;

public class Elections {
    private final Map<String, List<String>> list;
    private final ElectionsStrategy election;

    public Elections(Map<String, List<String>> list, boolean withDistrict) {
        this.list = list;

        election = withDistrict ? new ElectionsWithDistrictStrategy() : new ElectionsWithoutDistrictStrategy();
    }

    public void addCandidate(String candidate) {
        election.addCandidate(candidate);
    }

    public void voteFor(String elector, String candidate, String electorDistrict) {
        election.voteFor(candidate, electorDistrict);
    }

    public Map<String, String> results() {
        Map<String, String> results = new HashMap<>();
        VotesCountByType votes = new VotesCountByType();

        election.calculateResults(votes, results);

        float blankResult = ((float) votes.blankVotes * 100) / votes.nbVotes;
        results.put("Blank", String.format(Locale.FRENCH, "%.2f%%", blankResult));

        float nullResult = ((float) votes.nullVotes * 100) / votes.nbVotes;
        results.put("Null", String.format(Locale.FRENCH, "%.2f%%", nullResult));

        int nbElectors = list.values().stream().map(List::size).reduce(0, Integer::sum);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        float abstentionResult = 100 - ((float) votes.nbVotes * 100 / nbElectors);
        results.put("Abstention", String.format(Locale.FRENCH, "%.2f%%", abstentionResult));

        return results;
    }

    public static class VotesCountByType {
        Integer nbVotes = 0;
        Integer nullVotes = 0;
        Integer blankVotes = 0;
        Integer nbValidVotes = 0;
    }
}
