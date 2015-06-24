package com.partnet.util;

/**
 * Range defined by upper and lower float values.
 *
 * @author jwhite
 */
public class Range {

  private float low;
  private float high;

  public Range(float low, float high) {
    this.low = low;
    this.high = high;
  }

  public float getLow() {
    return low;
  }

  public float getHigh() {
    return high;
  }

  public boolean contains(float value) {
    return value >= low && value <= high;
  }

}
