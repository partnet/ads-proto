package com.partnet.model;

import java.util.Map;

/**
 * @author bbarker
 */
public class Reaction {

  private final String reaction;
  private final int count;
  private final Map<String, Integer> outcomes;


  public Reaction(String reaction, int count, Map<String, Integer> outcomes) {
    this.reaction = reaction;
    this.count = count;
    this.outcomes = outcomes;
  }

  public String getReaction() {
    return reaction;
  }

  public int getCount() {
    return count;
  }

  public Map<String, Integer> getOutcomes() {
    return outcomes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Reaction reaction1 = (Reaction) o;

    if (count != reaction1.count) return false;
    if (reaction != null ? !reaction.equals(reaction1.reaction) : reaction1.reaction != null) return false;
    return !(outcomes != null ? !outcomes.equals(reaction1.outcomes) : reaction1.outcomes != null);

  }

  @Override
  public int hashCode() {
    int result = reaction != null ? reaction.hashCode() : 0;
    result = 31 * result + count;
    result = 31 * result + (outcomes != null ? outcomes.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    return sb.append("Reaction{")
        .append("reaction='").append(reaction).append('\'')
        .append(", count=").append(count)
        .append(", outcomes=").append(outcomes)
        .append('}').toString();
  }
}
