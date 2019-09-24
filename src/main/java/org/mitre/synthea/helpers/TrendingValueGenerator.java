package org.mitre.synthea.helpers;

import static com.google.common.base.Preconditions.checkArgument;

import org.mitre.synthea.world.agents.Person;

/**
 * A ValueGenerator for generation of trending values. 
 * It has the following properties:
 * - values will start at a time beginTime at a starting mean beginMean
 * - values will end at a time endTime at an ending mean endMean 
 * - values will be bounded by a minimum and maximum value 
 * - values will be distributed with a given standard deviation
 */
public class TrendingValueGenerator extends ValueGenerator {

  private double standardDeviation;
  private Double minimumValue;
  private Double maximumValue;

  private double beginMean;
  private double endMean;
  private long beginTime;
  private long endTime;

  /**
   * A ValueGenerator that generates values which follow a trend.
   * 
   * @param person the person for which the values should be generated
   * @param standardDeviation the SD of the generated values
   * @param beginMean the mean at the begin time
   * @param endMean the mean at the end time
   * @param beginTime the begin time (in millis)
   * @param endTime the end time (in millis)
   * @param minimumValue a lower bound for the generated values
   * @param maximumValue an upper bound for the generated values.
   */
  public TrendingValueGenerator(Person person, double standardDeviation, 
      double beginMean, double endMean, long beginTime, long endTime, 
      Double minimumValue, Double maximumValue) {
    super(person);

    checkArgument(standardDeviation >= 0);
    checkArgument(minimumValue == null || maximumValue == null || minimumValue <= maximumValue);

    this.standardDeviation = standardDeviation;
    this.beginMean = beginMean;
    this.endMean = endMean;
    this.beginTime = beginTime;
    this.endTime = endTime;
    this.minimumValue = minimumValue;
    this.maximumValue = maximumValue;
  }

  public long getBeginTime() {
    return this.beginTime;
  }

  public long getEndTime() {
    return this.endTime;
  }

  /**
   * Return the value at the given time.
   * For times before beginTime, the beginMean is used.
   * For times after endTime, the endMean is used.
   * 
   * @param time the timestamp for which the value is requested
   * @return the value
   */
  public double getValue(long time) {
    if (time < beginTime) {
      return nextValue(beginMean);
    } else if (time > endTime) {
      return nextValue(endMean);
    } else {
      return nextValue(beginMean + (endMean - beginMean) * (time - beginTime)
          / (endTime - beginTime));
    }
  }

  /**
   * Return a next value for the given mean.
   * This will be close to the mean, but with a deviation.
   * It will be bounded by minimumValue and maximumValue.
   * 
   * @param mean the mean of the random variable
   * @return the next value generated by the random variable
   */
  private double nextValue(double mean) {

    double nextValue;

    do {
      nextValue = person.random.nextGaussian() * standardDeviation + mean;

      if ((minimumValue == null || nextValue >= minimumValue) && (maximumValue == null
          || nextValue <= maximumValue)) {
        break;
      }
    } while (true);

    return nextValue;
  }

  @Override
  public String toString() {

    final StringBuilder sb = new StringBuilder("TrendingValueGenerator {");

    sb.append("standardDeviation=").append(standardDeviation);
    sb.append(", beginMean=").append(beginMean);
    sb.append(", endMean=").append(endMean);
    sb.append(", beginTime=").append(beginTime);
    sb.append(", endTime=").append(endTime);
    sb.append(", minimumValue=").append(minimumValue);
    sb.append(", maximumValue=").append(maximumValue);
    sb.append('}');

    return sb.toString();
  }
}
