import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author sachin Bhalekar
 * 
 *         Input Format: The input file will be called input1.txt and be in the
 *         same directory as the java and class files. Line 0 will be a single
 *         integer n, the number of men (or women). Lines 1 to n will be the
 *         preferences of the n men where each line is a space seperated
 *         permutation of {1,2,3,4,,,n}. Lines n + 1 to n + n will be the
 *         preferences of the n women where each line is a space seperated
 *         permutation of {1,2,3,4,,,n}. Output: A single number which is the
 *         number of different stable matches.
 */

public class Project1 {

	public static int N, StableMatch;

	// Store the women in order of preference for each man
	public static int[][] MenPreferenceByValue = null;
	// Store the preference index of every women for each man
	public static int[][] MenPreferenceByIndex = null;

	// Store the men in order of preference for each woman
	public static int[][] WomenPreferenceByValue = null;
	// Store the preference index of every men for each woman
	public static int[][] WomenPreferenceByIndex = null;

	// Store the range of valid preference index range for each man.
	public static int[][] ValidPreferenceRangeForMen = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		/*PrintWriter out=null;
		try {
			 out = new PrintWriter(new FileWriter("src/input12.txt"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		for(int i=1;i<=1000;i++)
		{
			for(int j=1;j<=500;j++)
			{
				out.print(j+" ");
			}
			out.println("");
		}
		*/
		
		try {
			File file = new File("src/input1.txt");
			Scanner sc = new Scanner(file);

			N = sc.nextInt();
			MenPreferenceByValue = new int[N][N];
			MenPreferenceByIndex = new int[N][N];

			WomenPreferenceByValue = new int[N][N];
			WomenPreferenceByIndex = new int[N][N];

			/*
			 * Logic to store women in order of preference for each man and the
			 * preference index of women for each man
			 */
			for (int i = 0; i < N; i++) {

				for (int j = 0; j < N; j++) {
					int prefWomen = sc.nextInt() - 1;
					MenPreferenceByValue[i][j] = prefWomen;
					MenPreferenceByIndex[i][prefWomen] = j;
				}
			}
			/*
			 * Logic to store men in order of preference for each woman and the
			 * preference index of men for each woman
			 */
			for (int i = N; i < N + N; i++) {
				for (int j = 0; j < N; j++) {
					int prefMen = sc.nextInt() - 1;
					WomenPreferenceByValue[i - N][j] = prefMen;
					WomenPreferenceByIndex[i - N][prefMen] = j;
				}
			}

			// Display values of the accepted input
			// displayInput();

			// Close the input Scanner
			sc.close();

			// System.out.println("Men Proposing: ");
			/* Logic to find a stable match when men propose */
			int[] menBestMatch = GaleShapely("Men");

			// System.out.println("Women Proposing: ");
			/* Logic to find a stable match when women propose */
			int[] menWorstMatch = GaleShapely("Women");

			ValidPreferenceRangeForMen = new int[N][2];
			/*
			 * Logic to store the valid preference index range for each man.
			 * ValidPreferenceRangeForMen[i][0] is the lower bound of the
			 * preference index for man i and ValidPreferenceRangeForMen[i][1]
			 * is the upper bound of the preference index for man i. The range
			 * of lower and upper bound is inclusive.
			 */
			for (int i = 0; i < N; i++) {
				ValidPreferenceRangeForMen[i][0] = MenPreferenceByIndex[i][menBestMatch[i]];
				ValidPreferenceRangeForMen[menWorstMatch[i]][1] = MenPreferenceByIndex[menWorstMatch[i]][i];
			}

			/*
			 * Logic to display the range of preference index for each man
			 */
			// displayValidPreferenceRange(ValidPreferenceRangeForMen);

			/*
			 * Logic to create all possible combinations of match for all the
			 * men and test if the match is Stable. If the match is stable
			 * increment StableMatch counter
			 */
			ArrayList<Integer> arrleftWomen = new ArrayList<Integer>();
			for (int i = 0; i < N; i++) {
				arrleftWomen.add(i);
			}

			//Recursive method to check all possible combinations of match
			//makeMatch(arrleftWomen,0, new int[N], new int[N]);
			
			//NON-Recursive method to check all possible combinations of match
			makeMatchNonRecursive(arrleftWomen, new int[N], new int[N]);

			/* Display total number of stable match */
			// System.out.println("Total number of Stable Match: " +
			// StableMatch);
			System.out.println(StableMatch);
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}
		
		long endTime = System.nanoTime();

		long duration = (endTime - startTime); 
		System.out.println(duration);
	}

	/**
	 * @param arrMatchResultForMen
	 * @param arrMatchResultForWomen
	 * @return true if stable match else false. This method is used to check if
	 *         the match provided as input parameter is Stable. Input parameter
	 *         arrMatchResultForMen[i] contains match for man i and
	 *         arrMatchResultForWomen[i] contains match for woman i. The method
	 *         returns true if match is stable else returns false.
	 */
	public static boolean checkStableMatch(int[] arrMatchResultForMen, int[] arrMatchResultForWomen) {
		boolean blResult = true;

		for (int i = 0; i < N; i++) {
			// get all the women having more preference for man i than his
			// current matched women arrMatchResultForMen[i]
			// get index prefIndexOfCurrentMatchedWomen of the current matched
			// women form man i using
			// MenPreferenceByIndex[i][arrMatchResultForMen[i]]
			int prefIndexOfCurrentMatchedWomen = MenPreferenceByIndex[i][arrMatchResultForMen[i]];

			/*
			 * run the loop until index prefIndexOfCurrentMatchedWomen which is
			 * also the number of women having more preference for man i than
			 * his current match arrMatchResultForMen[i] and fetch all the women
			 * having more preference for man i from MenPreferenceByValue[i][j]
			 * where 0 < j < prefIndexOfCurrentMatchedWomen
			 */
			for (int j = 0; j < prefIndexOfCurrentMatchedWomen; j++) {
				// get woman w having higher preference for man i than his
				// current match arrMatchResultForMen[i]
				int w = MenPreferenceByValue[i][j];
				// below if will always be true
				// if (MenPreferenceByIndex[i][w] <
				// MenPreferenceByIndex[i][arrMatchResultForMen[i]]) {
				if (WomenPreferenceByIndex[w][i] < WomenPreferenceByIndex[w][arrMatchResultForWomen[w]]) {
					blResult = false;
					break;
				}
				// }
			}
		}

		return blResult;

	}

	/**
	 * @param leftWomen
	 * @param manIndex
	 * @param arrMatchResultForMen
	 * @param arrMatchResultForWomen
	 *            This recursive method includes logic to create all possible
	 *            combinations of match. The method also include logic to ignore
	 *            match which includes pairs outside the validPreferenceRange.
	 *            Every valid match is checked for stability and the StableMatch
	 *            counter is incremented for every stable match found.
	 */
	public static void makeMatch(ArrayList<Integer> leftWomen, int manIndex, int[] arrMatchResultForMen,
			int[] arrMatchResultForWomen) {
		if (manIndex == N) {
			if (checkStableMatch(arrMatchResultForMen, arrMatchResultForWomen)) {
				//System.out.println("Stable Match");
				StableMatch++;
			} /*
				 * else { System.out.println("NOT a Stable Match"); }
				 */
			//System.out.println("Match Combination below MEN: ");
			//displayAllPossibleMatch(arrMatchResultForMen);

			// System.out.println("Match Combination below WOMEN: ");
			// displayAllPossibleMatch(arrMatchResultForWomen);

			return;
		}

		for (int i = 0; i < leftWomen.size(); i++) {
			/*
			 * Logic to ignore the match if the preference index of the women i
			 * for man manIndex is outside the valid preference Index for man
			 * manIndex.
			 */
			int w = leftWomen.get(0);
			if (MenPreferenceByIndex[manIndex][w] >= ValidPreferenceRangeForMen[manIndex][0]
					&& MenPreferenceByIndex[manIndex][w] <= ValidPreferenceRangeForMen[manIndex][1]) {
				arrMatchResultForMen[manIndex] = w;
				arrMatchResultForWomen[w] = manIndex;

				leftWomen.remove(0);
				makeMatch(leftWomen, manIndex + 1, arrMatchResultForMen, arrMatchResultForWomen);
				leftWomen.add(w);

			} else {
				leftWomen.remove(0);
				leftWomen.add(w);

			}

		}

	}

	/**
	 * @param leftWomen
	 * @param arrMatchResultForMen
	 * @param arrMatchResultForWomen
	 *            This Iterative method includes logic to create all possible
	 *            combinations of match. The method also include logic to ignore
	 *            match which includes pairs outside the validPreferenceRange.
	 *            Every valid match is checked for stability and the StableMatch
	 *            counter is incremented for every stable match found.
	 */

	public static void makeMatchNonRecursive(ArrayList<Integer> leftWomen, int[] arrMatchResultForMen,
			int[] arrMatchResultForWomen) {

		int stackIndex = 0;
		int i = 0;
		int manIndex = 0;
		boolean blStackLoad = false;
		ArrayList<ArrayList<Integer>> stackLeftWomen = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> arrIndexI = new ArrayList<Integer>();
		ArrayList<Integer> arrManIndex = new ArrayList<Integer>();
		while (true) {

			if (manIndex == N) {
				if (checkStableMatch(arrMatchResultForMen, arrMatchResultForWomen)) {
					//System.out.println("Stable Match");
					StableMatch++;
				}
				/*
				 * else { System.out.println("NOT a Stable Match"); }
				 */
				//System.out.println("Match Combination below MEN: ");
				//displayAllPossibleMatch(arrMatchResultForMen);

				// System.out.println("Match Combination below WOMEN: ");
				// displayAllPossibleMatch(arrMatchResultForWomen);

			}

			for (; i < leftWomen.size(); i++) {
				/*
				 * Logic to ignore the match if the preference index of the
				 * women i for man manIndex is outside the valid preference
				 * Index for man manIndex.
				 */
				int w = leftWomen.get(0);
				if (MenPreferenceByIndex[manIndex][w] >= ValidPreferenceRangeForMen[manIndex][0]
						&& MenPreferenceByIndex[manIndex][w] <= ValidPreferenceRangeForMen[manIndex][1]) {

					arrMatchResultForMen[manIndex] = w;
					arrMatchResultForWomen[w] = manIndex;

					// LOAD STACK
					ArrayList<Integer> arrTemp = new ArrayList<Integer>(leftWomen);
					arrTemp.remove(0);
					arrTemp.add(w);
					stackLeftWomen.add(arrTemp);
					arrIndexI.add(i);
					arrManIndex.add(manIndex);

					/*
					 * remove the matched women and pass remaining women to
					 * match against next men. Break from this loop and
					 * reinitialize the variables to imitate a recursive.
					 */
					leftWomen.remove(0);
					manIndex++;
					blStackLoad = true;
					break;

				} else {
					/*
					 * Since women w was outside the preference index of man
					 * manIndex, women w need to be removed from ArrayList
					 * leftwomen as it should not be matched with man manIndex
					 * again. However women w may be paired with other men as it
					 * was not matched with current man and will be matched with
					 * some other man hence add it to the end of ArrayLisy
					 * leftwomen. Only the women in ArrayList leftwomen are
					 * considered for match with men
					 */
					leftWomen.remove(0);
					leftWomen.add(w);

				}

			}

			if (blStackLoad) {
				// reinitializing the variables
				i = 0;
				blStackLoad = false;
				stackIndex++;

				continue;
			}

			// UNLOAD STACK and initialize the value of variables from the stack
			// to imitate return from recursive method.
			if (stackLeftWomen.size() == 0)
				break;
			leftWomen = stackLeftWomen.get(stackIndex - 1);
			i = arrIndexI.get(stackIndex - 1) + 1;
			manIndex = arrManIndex.get(stackIndex - 1);
			stackLeftWomen.remove(stackIndex - 1);
			arrIndexI.remove(stackIndex - 1);
			arrManIndex.remove(stackIndex - 1);
			stackIndex--;

		}

	}

	/**
	 * @param strProposingGroup
	 * @return int[] containing stable match as per the proposing group. GALE
	 *         SHAPELY ALGORITHM: This method returns a stable match if men
	 *         propose or women propose based upon the value passed in input
	 *         parameter strProposingGroup. Valid value for strProposingGroup is
	 *         "Men" or "Women"
	 */
	public static int[] GaleShapely(String strProposingGroup) {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		int[] Group1 = new int[N];
		int[] Group2 = new int[N];

		/*
		 * initialize an Arraylist of size N with values of men. Initialize
		 * Group1[i]=-1 and Group2[i]=-1 indicating an unengaged member i
		 */
		for (int i = 0; i < N; i++) {
			arr.add(i);
			Group1[i] = -1;
			Group2[i] = -1;
		}
		int[][] preferenceOfProposingGroupByValue = null;
		int[][] preferenceOfReceivingGroupByIndex = null;
		if ("Men".equals(strProposingGroup)) {
			preferenceOfProposingGroupByValue = MenPreferenceByValue;
			preferenceOfReceivingGroupByIndex = WomenPreferenceByIndex;
		} else {
			preferenceOfProposingGroupByValue = WomenPreferenceByValue;
			preferenceOfReceivingGroupByIndex = MenPreferenceByIndex;
		}
		/* While there is atleast one unengaged member in Arraylist arr */
		while (!arr.isEmpty()) {
			int member1 = arr.get(0);
			for (int i = 0; i < N; i++) {
				/* member2 is the most preferred match for member1 */
				int member2 = preferenceOfProposingGroupByValue[member1][i];
				/* if member2 is not engaged. engage member2 with member1 */
				if (Group2[member2] == -1) {
					Group1[member1] = member2;
					Group2[member2] = member1;
					break;
				} else {
					/*
					 * if member2 is engaged with member# and member2 prefers
					 * member1 over member# then unengage member2 with member#,
					 * engage member2 with member1, and add unengaged member# to
					 * ArrayList arr.
					 */
					if (preferenceOfReceivingGroupByIndex[member2][member1] < preferenceOfReceivingGroupByIndex[member2][Group2[member2]]) {
						Group1[Group2[member2]] = -1;
						arr.add(Group2[member2]);

						Group1[member1] = member2;
						Group2[member2] = member1;
						break;
					}
				}
			}
			/*
			 * Since member1 is engaged by this point, remove member1 from
			 * ArrayList arr. The index of member1 in Arraylist is 0
			 */
			arr.remove(0);
		}

		/* Logic to display stable match with respect to the calling group */
		// displayAllPossibleMatch(Group1);

		/* Group1 contains the stable match with respect to the calling group */
		return Group1;
	}

	/*
	 * BELOW METHODS CONTAIN DISPLAY LOGIC FOR DEBUGGING PURPOSE
	 */

	/**
	 * @param displayArray
	 *            VALID PREFERENCE RANGE FOR MEN DISPLAY METHOD
	 */
	public static void displayValidPreferenceRange(int[][] displayArray) {
		for (int i = 0; i < displayArray.length; i++) {
			System.out.println("M" + i + ": " + displayArray[i][0] + "->" + displayArray[i][1]);
		}
	}

	/**
	 * @param displayArray
	 *            DISPLAY MATCH METHOD
	 */
	public static void displayAllPossibleMatch(int[] displayArray) {
		for (int i = 0; i < displayArray.length; i++) {
			System.out.println(i + "<->" + displayArray[i]);
		}
	}

	/**
	 * DISPLAY INPUT METHOD
	 */
	public static void displayInput() {
		System.out.println("Men Pref by Value");

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print(MenPreferenceByValue[i][j] + " ");
			}
			System.out.println("");
		}

		System.out.println("Women Pref by Value");
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print(WomenPreferenceByValue[i][j] + " ");
			}
			System.out.println("");
		}

		System.out.println("Men Pref by Index");

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print(MenPreferenceByIndex[i][j] + " ");
			}
			System.out.println("");
		}

		System.out.println("Women Pref by Index");
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print(WomenPreferenceByIndex[i][j] + " ");
			}
			System.out.println("");
		}

	}

}
