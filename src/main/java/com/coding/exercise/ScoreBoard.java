package com.coding.exercise;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;
import java.util.TreeSet;

public class ScoreBoard {
    private final @NonNull Set<Match> board;

    public ScoreBoard() {
        this(new TreeSet<>(new MatchComparator()));
    }

    @VisibleForTesting
    ScoreBoard(final @NonNull Set<Match> board) {
        this.board = board;
    }
    public void startMatch(final @NonNull String homeTeamName, final @NonNull String awayTeamName) {
    }

    public void updateScore(
            final @NonNull String homeTeamName,
            final @NonNull String awayTeamName,
            final @NonNull ImmutablePair<Integer, Integer> newScore) {
    }

    public void finishMatch(final @NonNull String homeTeamName, final @NonNull String awayTeamName) {
    }

    public @NonNull String getSummary() {
        return "";
    }
}