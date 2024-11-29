package com.coding.exercise;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Class to hold information about a match.
 * <p>The initial score is set to 0-0 automatically.
 * Also, the start time of the match is set based on {@link System#currentTimeMillis()} method.
 */
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

    public long getStartTime() {
        return startTime;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    /**
     * Returns the score of the match: the immutable pair's 'left' value holds the home team's score and
     * the 'right' value holds the away team's score.
     */
    public @NonNull ImmutablePair<Integer, Integer> getScore() {
        return score;
    }

    /** Returns the total sum of goals scored on the match. */
    public int getSumOfScore() {
        return score.left + score.right;
    }

    /** Returns a concatenated string of the home team name and the away team name (in this order). */
    public @NonNull String getTeams() {
        return homeTeamName + awayTeamName;
    }

    /**
     * Set the current score of the match: the immutable pair's 'left' value should hold the home team's score and
     * the 'right' value should hold the away team's score.
     */
    public void setScore(final @NonNull ImmutablePair<Integer, Integer> newScore) {
        validateNewScore(this.score, newScore);
        this.score = newScore;
    }

    @Override
    public @NonNull String toString() {
        return homeTeamName + " " + score.left + " - " + awayTeamName + " " + score.right;
    }

    /**
     * Ensure the following constraints:
     * <ul>
     *     <li>the score is actually updated (no point in setting the same score);</li>
     *     <li>none of the scores are negative;</li>
     *     <li>scores do not decrease and at least one of the scores is higher than the currently registered value.</li>
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
                "Scores cannot decrease and at least one of the scores should be higher than the current one");
    }
}