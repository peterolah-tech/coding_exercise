package com.coding.exercise;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
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

/**
 * The class is an implementation of a live football world cup score board which enables registering and presenting
 * information about ongoing matches.
 * <p>The implementation uses a {@link ReadWriteLock} to ensure thread-safety (as read operations are expected to be
 * considerably more frequent than write operations).
 */
public class ScoreBoard {
    private static final String NOT_PLAYING_EXCEPTION_MESSAGE =
            "These teams are not currently playing each other";
    private static final String NO_ONGOING_MATCHES_MESSAGE =
            "There are no ongoing matches at the moment";
    private final @NonNull ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final @NonNull Lock writeLock = readWriteLock.writeLock();
    private final @NonNull Lock readLock = readWriteLock.readLock();
    private final @NonNull Set<Match> board;
    private @NonNull String summary;

    public ScoreBoard() {
        this(new TreeSet<>(new MatchComparator()));
    }

    @VisibleForTesting
    ScoreBoard(final @NonNull Set<Match> board) {
        this.board = board;
        this.summary = NO_ONGOING_MATCHES_MESSAGE;
    }

    /**
     * Start a new {@link Match} and register it on the board.
     */
    public void startMatch(final @NonNull String homeTeamName, final @NonNull String awayTeamName) {
        final String formattedHomeTeam = formatTeamName(homeTeamName);
        final String formattedAwayTeam = formatTeamName(awayTeamName);
        Preconditions.checkArgument(!formattedHomeTeam.isBlank() && !formattedAwayTeam.isBlank(),
                "The teams names cannot be blank");
        writeLock.lock();
        try {
            validateTeams(formattedHomeTeam, formattedAwayTeam);
            board.add(new Match(formattedHomeTeam, formattedAwayTeam));
            updateSummary();
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Update the score of an existing {@link Match}: the immutable pair's 'left' value should hold
     * the home team's score and the 'right' value should hold the away team's score.
     */
    public void updateScore(
            final @NonNull String homeTeamName,
            final @NonNull String awayTeamName,
            final @NonNull ImmutablePair<Integer, Integer> newScore) {
        final String formattedHomeTeam = formatTeamName(homeTeamName);
        final String formattedAwayTeam = formatTeamName(awayTeamName);
        writeLock.lock();
        try {
            Optional<Match> optionalMatch = getMatch(formattedHomeTeam, formattedAwayTeam);
            ImmutablePair<Integer, Integer> oldScore;
            if (optionalMatch.isPresent()) {
                Match match = optionalMatch.get();
                oldScore = match.getScore();
                validateNewScore(oldScore, newScore);
                board.remove(match);
                match.setScore(newScore);
                board.add(match);
                updateSummary();
            } else {
                throw new IllegalArgumentException(NOT_PLAYING_EXCEPTION_MESSAGE);
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Finish an existing {@link Match} and deregister it from the score board.
     */
    public void finishMatch(final @NonNull String homeTeamName, final @NonNull String awayTeamName) {
        final String formattedHomeTeam = formatTeamName(homeTeamName);
        final String formattedAwayTeam = formatTeamName(awayTeamName);
        writeLock.lock();
        try {
            Optional<Match> optionalMatch = getMatch(formattedHomeTeam, formattedAwayTeam);
            if (optionalMatch.isPresent()) {
                Match match = optionalMatch.get();
                board.remove(match);
                updateSummary();
            } else {
                throw new IllegalArgumentException(NOT_PLAYING_EXCEPTION_MESSAGE);
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Get a summary of the currently ongoing {@link Match}es:
     * <ul>
     *     <li>the matches are ordered by their total score (higher ones on top);</li>
     *     <li>in case of equal total scores, the more recently started match is shown first;</li>
     *     <li>if even the start time is the same, then those matches are shown in a lexicographic order.</li>
     * </ul>
     */
    public @NonNull String getSummary() {
        readLock.lock();
        try {
            return summary;
        } finally {
            readLock.unlock();
        }
    }

    private @NonNull String formatTeamName(@NonNull String rawTeamName) {
        String strippedName = rawTeamName.strip();
        return StringUtils.capitalize(strippedName.toLowerCase());
    }

    /**
     * Ensure that neither the home, neither the away team is currently playing a match.
     *
     * @throws IllegalArgumentException if a team is already registered on the board.
     */
    private void validateTeams(final @NonNull String homeTeamName, final @NonNull String awayTeamName) {
        List<String> allTeams = board.stream()
                .map(match -> List.of(match.getHomeTeamName(), match.getAwayTeamName()))
                .flatMap(Collection::stream)
                .toList();
        if (allTeams.contains(homeTeamName) || allTeams.contains(awayTeamName)) {
            throw new IllegalArgumentException("A country can only play one match at the same time");
        }
    }

    /**
     * Ensure the following constraints:
     * <ul>
     *     <li>the score is actually updated (no point in setting the same score);</li>
     *     <li>none of the scores are negative;</li>
     *     <li>at least one of the scores is higher than the currently registered value.</li>
     * </ul>
     *
     * @throws IllegalArgumentException if the listed constraints are not met.
     */
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

    /**
     * Returns a {@link Match} if the requested teams are currently playing. Otherwise, returns an empty optional
     * if the two teams are not registered on the board.
     */
    private @NonNull Optional<Match> getMatch(final @NonNull String homeTeamName, final @NonNull String awayTeamName) {
        String lookedUpTeams = homeTeamName + awayTeamName;
        return board.stream()
                .filter(match -> match.getTeams().equals(lookedUpTeams))
                .findAny();
    }

    /**
     * Updates the summary based on the information stored in the board object.
     * <p>This must be called after each operation which modifies the board.
     */
    private void updateSummary() {
        if (board.isEmpty()) {
            this.summary = NO_ONGOING_MATCHES_MESSAGE;
        }
        StringBuilder stringBuilder = new StringBuilder();
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
        this.summary = stringBuilder.toString();
    }
}