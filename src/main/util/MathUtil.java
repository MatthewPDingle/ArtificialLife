package main.util;

public class MathUtil {
	
	/**
	 * @param a number
	 * @param b base
	 * @return
	 */
	public static float log(float a, float b) {
		return (float)(Math.log(a) / Math.log(b));
	}
	
	/**
	 * Returns a number within a certain percentage of the average of v1 and v2. 
	 * For example, if v1 and v2 are 10 and 20, and percent is .2,
	 * the average is 15.  20% of 15 is 3.  The range is 7 and 23 so a random number
	 * is returned from that range
	 * 
	 * @param v1
	 * @param v2
	 * @param percent
	 * @return
	 */
	public static float getRandomNumWithinXPercentOfValues(float v1, float v2, float percent) {
		float smaller = v1;
		float larger = v2;
		if (v2 < v1) {
			smaller = v2;
			larger = v1;
		}
		float average = (smaller + larger) / 2f;
		float extra = average * percent;
		smaller = smaller - extra;
		larger = larger + extra;
		float diff = larger - smaller;
		float newValue = smaller + (float)(Math.random() * diff);
		return newValue;
	}
}
