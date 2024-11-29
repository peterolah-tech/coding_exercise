package com.coding.exercise;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ScoreBoardTest {
    private Set<Match> testBoard;
    private ScoreBoard scoreBoard;

    @BeforeEach
    void setUp() {
        testBoard = new TreeSet<>(new MatchComparator());
        scoreBoard = new ScoreBoard(testBoard);
    }

    // Tests for START
    @Test
    void testStartMatchHomeTeamAlreadyPlays() {
        scoreBoard.startMatch("Mexico", "Canada");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreBoard.startMatch("Mexico", "Hungary"));
        Assertions.assertEquals(
                "A country can only play one match at the same time", exception.getMessage());
    }

    @Test
    void testStartMatchAwayTeamAlreadyPlays() {
        scoreBoard.startMatch("Mexico", "Canada");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreBoard.startMatch("Hungary", "Canada"));
        Assertions.assertEquals(
                "A country can only play one match at the same time", exception.getMessage());
    }

    @Test
    void testStartMatchHomeTeamEmptyString() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreBoard.startMatch("", "Brazil"));
        Assertions.assertEquals(
                "The teams names cannot be blank", exception.getMessage());
    }

    @Test
    void testStartMatchHomeTeamWhiteSpace() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreBoard.startMatch(" ", "Brazil"));
        Assertions.assertEquals(
                "The teams names cannot be blank", exception.getMessage());
    }

    @Test
    void testStartMatchAwayTeamEmptyString() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreBoard.startMatch("Spain", ""));
        Assertions.assertEquals(
                "The teams names cannot be blank", exception.getMessage());
    }

    @Test
    void testStartMatchAwayTeamWhiteSpace() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreBoard.startMatch("Spain", " "));
        Assertions.assertEquals(
                "The teams names cannot be blank", exception.getMessage());
    }

    @Test
    void testStartValidMatch() {
        scoreBoard.startMatch("Mexico", "Canada");
        Assertions.assertEquals(1, testBoard.size());
        testBoard.stream()
                .findAny()
                .ifPresentOrElse(
                        match -> {
                            Assertions.assertEquals("MexicoCanada", match.getTeams());
                            ImmutablePair<Integer, Integer> score = match.getScore();
                            Assertions.assertEquals(0, score.getLeft());
                            Assertions.assertEquals(0, score.getRight());
                        },
                        () -> Assertions.fail("There were no matches on the board"));
    }

    @Test
    void testTeamNameCapitalizationAtStart() {
        scoreBoard.startMatch("sweden", "finland");
        Optional<Match> optionalMatch = testBoard.stream()
                .filter(match -> match.getTeams().equals("SwedenFinland"))
                .findAny();
        Assertions.assertTrue(optionalMatch.isPresent());
    }

    @Test
    void testTeamNameStripAtStart() {
        scoreBoard.startMatch("  Sweden", "Finland  ");
        Optional<Match> optionalMatch = testBoard.stream()
                .filter(match -> match.getTeams().equals("SwedenFinland"))
                .findAny();
        Assertions.assertTrue(optionalMatch.isPresent());
    }

    // Tests for UPDATE
    @Test
    void testUpdateScoreOnce() {
        scoreBoard.startMatch("Mexico", "Canada");
        scoreBoard.updateScore("Mexico", "Canada", new ImmutablePair<>(0, 1));
        Assertions.assertEquals(1, testBoard.size());
        testBoard.stream()
                .findAny()
                .ifPresentOrElse(
                        match -> {
                            Assertions.assertEquals("MexicoCanada", match.getTeams());
                            ImmutablePair<Integer, Integer> score = match.getScore();
                            Assertions.assertEquals(0, score.getLeft());
                            Assertions.assertEquals(1, score.getRight());
                        },
                        () -> Assertions.fail("There were no matches on the board"));
    }

    @Test
    void testUpdateScoreTwice() {
        scoreBoard.startMatch("Mexico", "Canada");
        scoreBoard.updateScore("Mexico", "Canada", new ImmutablePair<>(0, 1));
        scoreBoard.updateScore("Mexico", "Canada", new ImmutablePair<>(0, 2));
        Assertions.assertEquals(1, testBoard.size());
        testBoard.stream()
                .findAny()
                .ifPresentOrElse(
                        match -> {
                            Assertions.assertEquals("MexicoCanada", match.getTeams());
                            ImmutablePair<Integer, Integer> score = match.getScore();
                            Assertions.assertEquals(0, score.getLeft());
                            Assertions.assertEquals(2, score.getRight());
                        },
                        () -> Assertions.fail("There were no matches on the board"));
    }

    @Test
    void testUpdateScoreWrongUpdate() {
        scoreBoard.startMatch("Mexico", "Canada");
        scoreBoard.updateScore("Mexico", "Canada", new ImmutablePair<>(0, 1));
        scoreBoard.updateScore("Mexico", "Canada", new ImmutablePair<>(0, 2));
        ImmutablePair<Integer, Integer> invalidScore = new ImmutablePair<>(0, 1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreBoard.updateScore("Mexico", "Canada", invalidScore));
        Assertions.assertEquals(
                "Scores cannot decrease and at least one of the scores should be higher than the current one", exception.getMessage());
    }

    @Test
    void testUpdateNonExistentMatch() {
        ImmutablePair<Integer, Integer> newScore = new ImmutablePair<>(0, 1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreBoard.updateScore("Mexico", "Canada", newScore));
        Assertions.assertEquals(
                "These teams are not currently playing each other", exception.getMessage());
    }

    @Test
    void testTeamNameCapitalizationAtUpdate() {
        scoreBoard.startMatch("Sweden", "Finland");
        scoreBoard.updateScore("sweden", "finland", new ImmutablePair<>(1,0));
        testBoard.stream()
                .filter(match -> match.getTeams().equals("SwedenFinland"))
                .findAny()
                .ifPresentOrElse(
                        match -> {
                            Assertions.assertEquals(1, match.getScore().getLeft());
                            Assertions.assertEquals(0, match.getScore().getRight());
                        },
                        () -> Assertions.fail("The match to be updated was not registered on the board"));
    }

    @Test
    void testTeamNameStripAtUpdate() {
        scoreBoard.startMatch("Sweden", "Finland");
        scoreBoard.updateScore("  Sweden", "Finland  ", new ImmutablePair<>(1,0));
        testBoard.stream()
                .filter(match -> match.getTeams().equals("SwedenFinland"))
                .findAny()
                .ifPresentOrElse(
                        match -> {
                            Assertions.assertEquals(1, match.getScore().getLeft());
                            Assertions.assertEquals(0, match.getScore().getRight());
                        },
                        () -> Assertions.fail("The match to be updated was not registered on the board"));
    }

    // Tests for FINISH
    @Test
    void testFinishMatch() {
        scoreBoard.startMatch("Mexico", "Canada");
        scoreBoard.startMatch("Spain", "Brazil");
        scoreBoard.finishMatch("Mexico", "Canada");
        Assertions.assertEquals(1, testBoard.size());
        testBoard.stream()
                .findFirst()
                .ifPresentOrElse(
                        match -> Assertions.assertEquals("SpainBrazil", match.getTeams()),
                        () -> Assertions.fail("There were no matches on the board"));
    }

    @Test
    void testFinishNonExistentMatch() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreBoard.finishMatch("Mexico", "Canada"));
        Assertions.assertEquals(
                "These teams are not currently playing each other", exception.getMessage());
    }

    @Test
    void testTeamNameCapitalizationAtFinish() {
        scoreBoard.startMatch("Sweden", "Finland");
        Assertions.assertEquals(1, testBoard.size());
        scoreBoard.finishMatch("sweden", "finland");
        Assertions.assertTrue(testBoard.isEmpty());
    }

    @Test
    void testTeamNameStripAtFinish() {
        scoreBoard.startMatch("Sweden", "Finland");
        Assertions.assertEquals(1, testBoard.size());
        scoreBoard.finishMatch("  Sweden", "Finland  ");
        Assertions.assertTrue(testBoard.isEmpty());
    }

    // Tests for SUMMARY
    @Test
    void testGetSummaryNoOngoingMatches() {
        String expectedSummary = "There are no ongoing matches at the moment";
        String actualSummary = scoreBoard.getSummary();
        Assertions.assertEquals(expectedSummary, actualSummary);
    }

    @Test
    void testGetSummaryWithMatches() {
        scoreBoard.startMatch("Mexico", "Canada");
        scoreBoard.startMatch("Spain", "Brazil");
        scoreBoard.startMatch("Germany", "France");
        scoreBoard.updateScore("Mexico", "Canada",
                new ImmutablePair<>(0,5));
        scoreBoard.updateScore("Spain", "Brazil",
                new ImmutablePair<>(10,2));
        scoreBoard.updateScore("Germany", "France",
                new ImmutablePair<>(3,2));
        String expectedSummary = "1. Spain 10 - Brazil 2" +
                System.lineSeparator() +
                "2. Germany 3 - France 2" +
                System.lineSeparator() +
                "3. Mexico 0 - Canada 5";
        String actualSummary = scoreBoard.getSummary();
        Assertions.assertEquals(expectedSummary, actualSummary);
    }
}