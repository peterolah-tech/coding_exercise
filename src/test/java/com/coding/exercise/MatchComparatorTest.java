package com.coding.exercise;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
class MatchComparatorTest {
    private final MatchComparator comparator = new MatchComparator();
    private Match match1;
    private Match match2;

    @BeforeEach
    void setUp() {
        this.match1 = new Match("Hungary", "Norway", 1732549027500L);
        this.match2 = new Match("Switzerland", "Slovenia", 1732549027600L);
    }

    @Test
    void testMatch1HasMoreGoals() {
        match1.setScore(new ImmutablePair<>(3,3));
        match2.setScore(new ImmutablePair<>(1,2));
        int comparatorResult = comparator.compare(match1, match2);
        Assertions.assertTrue(comparatorResult < 0);
    }

    @Test
    void testMatch2HasMoreGoals() {
        match1.setScore(new ImmutablePair<>(1,2));
        match2.setScore(new ImmutablePair<>(3,3));
        int comparatorResult = comparator.compare(match1, match2);
        Assertions.assertTrue(comparatorResult > 0);
    }

    @Test
    void testScoreEqualityMatch1BeganEarlier() {
        int comparatorResult = comparator.compare(match1, match2);
        Assertions.assertTrue(comparatorResult < 0);
    }

    @Test
    void testScoreEqualityMatch2BeganEarlier() {
        this.match1 = new Match("England", "France", 1732549027700L);
        int comparatorResult = comparator.compare(match1, match2);
        Assertions.assertTrue(comparatorResult > 0);
    }

    @Test
    void testScoreEqualityStartedSameTime() {
        this.match1 = new Match("England", "France", 1732549027600L);
        int comparatorResult = comparator.compare(match1, match2);
        Assertions.assertTrue(comparatorResult > 0);
    }

    @Test
    void testSameMatch() {
        this.match1 = new Match("Switzerland", "Slovenia", 1732549027600L);
        int comparatorResult = comparator.compare(match1, match2);
        Assertions.assertEquals(0, comparatorResult);
    }
}