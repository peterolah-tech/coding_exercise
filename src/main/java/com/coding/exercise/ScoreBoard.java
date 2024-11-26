package com.coding.exercise;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ScoreBoard {
    private static final String NOT_PLAYING_EXCEPTION_MESSAGE =
            "These teams are not currently playing each other";
    private final @NonNull ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final @NonNull Lock writeLock = readWriteLock.writeLock();
    private final @NonNull Lock readLock = readWriteLock.readLock();
    private final @NonNull Set<Match> board;

    public ScoreBoard() {
        this(new TreeSet<>(new MatchComparator()));
    }

    @VisibleForTesting
    ScoreBoard(final @NonNull Set<Match> board) {
        this.board = board;
    }

    public void startMatch(final @NonNull String homeTeamName, final @NonNull String awayTeamName) {
        Preconditions.checkArgument(!homeTeamName.isBlank() && !awayTeamName.isBlank(),
                "The teams names cannot be blank");
        writeLock.lock();
        try {
            validateTeams(homeTeamName, awayTeamName);
            board.add(new Match(homeTeamName, awayTeamName));
        } finally {
            writeLock.unlock();
        }
    }

    public void updateScore(
            final @NonNull String homeTeamName,
            final @NonNull String awayTeamName,
            final @NonNull ImmutablePair<Integer, Integer> newScore) {
        writeLock.lock();
        try {
            Optional<Match> optionalMatch = getMatch(homeTeamName, awayTeamName);
            ImmutablePair<Integer, Integer> oldScore;
            if (optionalMatch.isPresent()) {
                Match match = optionalMatch.get();
                oldScore = match.getScore();
                validateNewScore(oldScore, newScore);
                board.remove(match);
                match.setScore(newScore);
                board.add(match);
            } else {
                throw new IllegalArgumentException(NOT_PLAYING_EXCEPTION_MESSAGE);
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void finishMatch(final @NonNull String homeTeamName, final @NonNull String awayTeamName) {
        writeLock.lock();
        try {
            Optional<Match> optionalMatch = getMatch(homeTeamName, awayTeamName);
            if (optionalMatch.isPresent()) {
                Match match = optionalMatch.get();
                board.remove(match);
            } else {
                throw new IllegalArgumentException(NOT_PLAYING_EXCEPTION_MESSAGE);
            }
        } finally {
            writeLock.unlock();
        }
    }

    public @NonNull String getSummary() {
        readLock.lock();
        if (board.isEmpty()) {
            return "There are no ongoing matches at the moment";
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            int counter = 1;
            for (Match match : board) {
                stringBuilder.append(counter);
                stringBuilder.append(". ");
                stringBuilder.append(match.toString());
                if (counter != board.size()) {
                    stringBuilder.append(System.lineSeparator());
                }
                counter++;
            }
            return stringBuilder.toString();
        } finally {
            readLock.unlock();
        }
    }

    private void validateTeams(final @NonNull String homeTeamName, final @NonNull String awayTeamName) {
        List<String> allTeams = board.stream()
                .map(match -> List.of(match.getHomeTeamName(), match.getAwayTeamName()))
                .flatMap(Collection::stream)
                .toList();
        if (allTeams.contains(homeTeamName) || allTeams.contains(awayTeamName)) {
            throw new IllegalArgumentException("A country can only play one match at the same time");
        }
    }

    private void validateNewScore(
            final @NonNull ImmutablePair<Integer, Integer> oldScore,
            final @NonNull ImmutablePair<Integer, Integer> newScore) {
        Preconditions.checkArgument(!oldScore.equals(newScore), "This score had already been set");
        // The last check would cover negative values, but want to send a more specific error message
        Preconditions.checkArgument(
                newScore.left >=0 && newScore.right >= 0, "Scores cannot be negative");
        Preconditions.checkArgument(newScore.left >= oldScore.left && newScore.right >= oldScore.right,
                "At least one of the scores should be higher than the current one");
    }

    private @NonNull Optional<Match> getMatch(final @NonNull String homeTeamName, final @NonNull String awayTeamName) {
        String lookedUpTeams = homeTeamName + awayTeamName;
        return board.stream()
                .filter(match -> match.getTeams().equals(lookedUpTeams))
                .findAny();
    }
}