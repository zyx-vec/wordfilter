import java.util.Random;

public class RandInt {
	
	public final static String TAG = "RANDINT";
	public static Random random = null;
	
	public static int randInt(int min, int max) {
		if (random == null) {
			random = new Random();
		}
		
		int randomNum = random.nextInt((max - min) + 1) + min;
		return randomNum;
	}
}