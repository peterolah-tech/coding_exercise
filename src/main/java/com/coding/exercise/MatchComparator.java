package com.coding.exercise;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Comparator;

public class MatchComparator implements Comparator<Match> {

    /**
     * The comparison is done as per the below:
     * <ul>
     *     <li>the match with a lower total score is "greater";</li>
     *     <li>in case of equal total scores, the start time of the match is taken into account:
     *     the more recently started match is "greater";</li>
     *     <li>in case the matches started at the same time,
     *     the concatenated team names' lexicographic order decides.</li>
     * </ul>
     */
    @Override
    public int compare(final @NonNull Match o1, final @NonNull Match o2) {
        int scoreDifference = o2.getSumOfScore() - o1.getSumOfScore();
        if (scoreDifference != 0) {
            return scoreDifference;
        } else {
            long startTimeDifference = o1.getStartTime() - o2.getStartTime();
            if (startTimeDifference == 0) {
                return -1 * (o1.getTeams().compareTo(o2.getTeams())); // not to be inconsistent with equals
            } else if (startTimeDifference < 0) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}