package com.coding.exercise;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Match {
    private final long startTime;
    private final @NonNull String homeTeamName;
    private final @NonNull String awayTeamName;
    private @NonNull ImmutablePair<Integer, Integer> score;
    public Match(final @NonNull String homeTeamName, final @NonNull String awayTeamName) {
        this(homeTeamName, awayTeamName, System.currentTimeMillis());
    }
    @VisibleForTesting
    Match(final @NonNull String homeTeamName, final @NonNull String awayTeamName, final long startTime) {
        this.startTime = startTime;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.score = new ImmutablePair<>(0, 0);
    }

    public @NonNull ImmutablePair<Integer, Integer> getScore() {
        return score;
    }

    public @NonNull String getTeams() {
        return homeTeamName + awayTeamName;
    }
    public void setScore(final @NonNull ImmutablePair<Integer, Integer> newScore) {
        this.score = newScore;
    }

    @Override
    public @NonNull String toString() {
        return homeTeamName + " " + score.left + " - " + awayTeamName + " " + score.right;
    }
}