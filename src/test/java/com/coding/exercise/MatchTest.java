package com.coding.exercise;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MatchTest {
    private final Match match = new Match("Hungary", "Norway");

    @Test
    void testGetTeams() {
        Assertions.assertEquals("HungaryNorway", match.getTeams());
    }

    @Test
    void testToString() {
        match.setScore(new ImmutablePair<>(2,1));
        Assertions.assertEquals("Hungary 2 - Norway 1", match.toString());
    }
}