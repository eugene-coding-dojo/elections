package org.elections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class ElectionsTest {

    @Test
    void first() {
        Elections elections = new Elections();
        elections.addCandidate("Michel");
        elections.addCandidate("Jerry");
        elections.addCandidate("Johnny");

        elections.voteFor("Bob", "Jerry", "Bob's District");
        elections.voteFor("Anna", "Johnny", "Anna's District");
        elections.voteFor("Matt", "Donald", "Matt's District");
        elections.voteFor("Jess", "Joe", "Jess's District");

        Map<String, String> results = elections.results();

        Map<String, String> expectedResults = Map.of(
                "Jerry", "25%",
                "Johnny", "25%",
                "Michel", "0%",
                "Null", "50%");
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }
}
