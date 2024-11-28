# Live Football World Cup - Coding Exercise

## Implementation notes

1. I implemented my solution using a ReadWriteLock to ensure thread safety.
I believe this is ideal for this exercise as the read operations are expected to be 
much more frequent than the write operations (especially in case of football).
2. I opted to store my data (about the matches) in an ordered collection. Namely, I used a TreeSet
with a custom comparator. Every time the board is updated, the summary string is updated as well. This way, calls
to the getSummary method simply mean the return of a string.
3. In the update method, I do not require the scores to be updated one-by-one. The reason for this decision was that
in case there is an outage upstream, the score can be updated to the correct value right away when the
system comes back online.