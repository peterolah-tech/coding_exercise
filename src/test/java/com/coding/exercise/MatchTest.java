package com.coding.exercise;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MatchTest {
    private final Match match = new Match("Hungary", "Norway");

    @Test
    void testGetSumOfScore() {
        match.setScore(new ImmutablePair<>(2,1));
        Assertions.assertEquals(3, match.getSumOfScore());
    }

    @Test
    void testGetTeams() {
        Assertions.assertEquals("HungaryNorway", match.getTeams());
    }

    @Test
    void testToString() {
        match.setScore(new ImmutablePair<>(2,1));
        Assertions.assertEquals("Hungary 2 - Norway 1", match.toString());
    }

    @Test
    void testSetScoreOnce() {
        match.setScore(new ImmutablePair<>(0, 1));
        Assertions.assertEquals(0, match.getScore().getLeft());
        Assertions.assertEquals(1, match.getScore().getRight());
    }

    @Test
    void testSetScoreTwice() {
        match.setScore(new ImmutablePair<>(0, 1));
        match.setScore(new ImmutablePair<>(0, 2));
        Assertions.assertEquals(0, match.getScore().getLeft());
        Assertions.assertEquals(2, match.getScore().getRight());
    }

    @Test
    void testSetScoreDiffHigherThanOne() {
        match.setScore(new ImmutablePair<>(0, 3));
        Assertions.assertEquals(0, match.getScore().getLeft());
        Assertions.assertEquals(3, match.getScore().getRight());
    }

    @Test
    void testSetTheSameScore() {
        match.setScore(new ImmutablePair<>(0, 1));
        ImmutablePair<Integer, Integer> invalidScore = new ImmutablePair<>(0, 1);
        IllegalArgumentException exception =
                Assertions.assertThrows(IllegalArgumentException.class, () -> match.setScore(invalidScore));
        Assertions.assertEquals(
                "This score had already been set", exception.getMessage());
    }

    @Test
    void testSetNegativeScore() {
        ImmutablePair<Integer, Integer> invalidScore = new ImmutablePair<>(0, -1);
        IllegalArgumentException exception =
                Assertions.assertThrows(IllegalArgumentException.class, () -> match.setScore(invalidScore));
        Assertions.assertEquals(
                "Scores cannot be negative", exception.getMessage());
    }

    @Test
    void testSetScoreWrongUpdate() {
        match.setScore(new ImmutablePair<>(0, 2));
        ImmutablePair<Integer, Integer> invalidScore = new ImmutablePair<>(0, 1);
        IllegalArgumentException exception =
                Assertions.assertThrows(IllegalArgumentException.class, () -> match.setScore(invalidScore));
        Assertions.assertEquals(
                "Scores cannot decrease and at least one of the scores should be higher than the current one",
                exception.getMessage());
    }
}