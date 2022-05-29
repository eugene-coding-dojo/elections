package org.elections;

import java.util.stream.IntStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@DisplayName("Тесты механизма голосования")
class ElectionsTest {
    private final Map<String, List<String>> electorsByDistrict = Map.of(
            "District 1", Arrays.asList("Bob", "Anna", "Jess", "July"),
            "District 2", Arrays.asList("Jerry", "Simon"),
            "District 3", Arrays.asList("Johnny", "Matt", "Carole")
    );

    @Nested
    @DisplayName("Голосование без учета округов")
    class NoDistrictsTest {

        @Nested
        @DisplayName("Список зарегистрированных кандидатов пуст")
        class NoCandidatesTest {
            @Test
            @DisplayName("Голоса за любого кандидата дают 100% испорченных бюллетеней")
            void votesForAnyCandidate_shouldShow100PercentNulls() {
                Elections elections = new Elections(electorsByDistrict, false);
                elections.voteFor("Bob", "Jerry", "District 1");
                elections.voteFor("Anna", "Jerry", "District 1");

                Map<String, String> results = elections.results();

                Map<String, String> expectedResults = Map.of(
                        "Blank", "0,00%",
                        "Null", "100,00%",
                        "Abstention", "77,78%");
                Assertions.assertThat(results).isEqualTo(expectedResults);
            }

            @Test
            @DisplayName("Голос от неизвестного избирателя учитывается и уменьшает неявку")
            void oneVoteFromUnknownElector_shouldDecreaseAbstention() {
                Elections elections = new Elections(electorsByDistrict, false);
                elections.voteFor("Eugene", "Jerry", "District 1");

                Map<String, String> results = elections.results();

                Map<String, String> expectedResults = Map.of(
                        "Blank", "0,00%",
                        "Null", "100,00%",
                        "Abstention", "88,89%");
                Assertions.assertThat(results).isEqualTo(expectedResults);
            }

            @Test
            @DisplayName("Повторное голосование доступно и уменьшает неявку")
            void multipleVotesFromSameElector_shouldDecreaseAbstention() {
                Elections elections = new Elections(electorsByDistrict, false);
                IntStream.range(1,10)
                        .forEach(i -> elections.voteFor("Eugene", "Jerry", "District 1"));

                Map<String, String> results = elections.results();

                Map<String, String> expectedResults = Map.of(
                        "Blank", "0,00%",
                        "Null", "100,00%",
                        "Abstention", "0,00%");
                Assertions.assertThat(results).isEqualTo(expectedResults);
            }

            @Test
            @DisplayName("Если голосов больше, чем избирателей, неявка отрицательна")
            void tooManyVotes_shouldMakeAbstentionNegative() {
                Elections elections = new Elections(electorsByDistrict, false);
                IntStream.range(1,11)
                        .forEach(i -> elections.voteFor("Eugene", "Jerry", "District 1"));

                Map<String, String> results = elections.results();

                Map<String, String> expectedResults = Map.of(
                        "Blank", "0,00%",
                        "Null", "100,00%",
                        "Abstention", "-11,11%");
                Assertions.assertThat(results).isEqualTo(expectedResults);

            }

        }

        @Test
        @DisplayName("Approval-Test для подсчета голосов")
        void electionWithoutDistricts() {
            Elections elections = new Elections(electorsByDistrict, false);
            elections.addOfficialCandidate("Michel");
            elections.addOfficialCandidate("Jerry");
            elections.addOfficialCandidate("Johnny");

            elections.voteFor("Bob", "Jerry", "District 1");
            elections.voteFor("Jerry", "Jerry", "District 2");
            elections.voteFor("Anna", "Johnny", "District 1");
            elections.voteFor("Johnny", "Johnny", "District 3");
            elections.voteFor("Matt", "Donald", "District 3");
            elections.voteFor("Jess", "Joe", "District 1");
            elections.voteFor("Simon", "", "District 2");
            elections.voteFor("Carole", "", "District 3");

            Map<String, String> results = elections.results();

            Map<String, String> expectedResults = Map.of(
                    "Jerry", "50,00%",
                    "Johnny", "50,00%",
                    "Michel", "0,00%",
                    "Blank", "25,00%",
                    "Null", "25,00%",
                    "Abstention", "11,11%");
            Assertions.assertThat(results).isEqualTo(expectedResults);
        }

    }



    @Test
    void givenNoCandidatesWithDistricts_oneVoteForAnyCandidate_shouldThrow() {
        Elections elections = new Elections(electorsByDistrict, true);
        elections.voteFor("Bob", "Jerry", "District 1");

        Assertions.assertThatNullPointerException().isThrownBy(elections::results);
    }


    @Test
    void electionWithDistricts() {
        Elections elections = new Elections(electorsByDistrict, true);
        elections.addOfficialCandidate("Michel");
        elections.addOfficialCandidate("Jerry");
        elections.addOfficialCandidate("Johnny");

        elections.voteFor("Bob", "Jerry", "District 1");
        elections.voteFor("Jerry", "Jerry", "District 2");
        elections.voteFor("Anna", "Johnny", "District 1");
        elections.voteFor("Johnny", "Johnny", "District 3");
        elections.voteFor("Matt", "Donald", "District 3");
        elections.voteFor("Jess", "Joe", "District 1");
        elections.voteFor("July", "Jerry", "District 1");
        elections.voteFor("Simon", "", "District 2");
        elections.voteFor("Carole", "", "District 3");

        Map<String, String> results = elections.results();

        Map<String, String> expectedResults = Map.of(
                "Jerry", "66,67%",
                "Johnny", "33,33%",
                "Michel", "0,00%",
                "Blank", "22,22%",
                "Null", "22,22%",
                "Abstention", "0,00%");
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }
}
