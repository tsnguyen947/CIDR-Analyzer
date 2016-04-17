import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Analyze {

	/*Main method for CIDR analyzer*/
	public static void main(String args[]){
		/*get file with CIDR data*/
		File data = new File("data.txt");
		Scanner in = null;
		try {
			in = new Scanner(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int numSets = in.nextInt();
		in.nextLine();

		/*Set up sets of CIDRs*/
		ArrayList<Set<CIDR>> sets = new ArrayList<Set<CIDR>>(numSets);
		for(int i = 0; i < numSets; i++)
			sets.add(new HashSet<CIDR>());
		while(in.hasNextLine()){
			StringTokenizer getCIDR = new StringTokenizer(in.nextLine());
			String nextCIDR = getCIDR.nextToken();
			int set = Integer.parseInt(getCIDR.nextToken());
			sets.get(set - 1).add(new CIDR(nextCIDR));
		}

		/*perform light and heavy analysis on CIDR sets*/
		int[][] result = new int[numSets][numSets];
		String[][] result2 = new String[numSets][numSets];
		analyze(sets, result);
		completeAnalyze(sets, result2);
		printMatrix(result);
		printMatrix(result2);
	}
	
	/*Performs a light analysis on the sets of CIDRs by checking the sets and seeing if any CIDRs contain,
	intersect or are adjacent to a CIDR in another set and updates the corresponding bit string. The resultant
	matrix is in the form 	[ 110 110 ] where a bit string at index (i,j) represents the interaction of set i to
							[ 010 010 ]
	set j; the most significant bit shows if a CIDR in set i contains a CIDR in set j, the middle bit shows if a 
	CIDR in set i intersects with a CIDR in set j, and the least significant bit shows if a CIDR in set i intersects
	with a CIDR in set j. */
	public static void analyze(ArrayList<Set<CIDR>> sets, int[][] result){
		int arraySize = sets.size();
		/*loop through and compare each set of CIDRs*/
		for(int i = 0; i < arraySize; i++){
			for(int j = i; j < arraySize; j++){
				Object[] a = sets.get(i).toArray();
				Object[] b= sets.get(j).toArray();
				int aSize = a.length;
				int bSize = b.length;
				/*loop through and compare CIDRs in the current two sets*/
				int tempIJ = 0;
				int tempJI = 0;
				for(int x = 0; x < aSize; x++){
					for(int y = 0; y < bSize; y++){
						boolean finished = true;
						CIDR first = (CIDR)a[x];
						CIDR second = (CIDR)b[y];
						/*check the interaction between the current CIDRs and update the result matrix
						by marking the corresponding bit there*/
						if((tempIJ & 4) == 0 || (tempJI & 4) == 0){
							finished = false;
							/*containing implies intersecting*/
							if(first.contains(second))
								tempIJ |= 6;
							if(second.contains(first))
								tempJI |= 6;
						}
						if((tempIJ & 2) == 0 || (tempJI & 2) == 0){
							finished = false;
							if(first.intersecting(second)){
								tempIJ |= 2;
								tempJI |= 2;
							}
						}
						if((tempIJ & 1) == 0 || (tempJI & 1) == 0){
							finished = false;
							if(first.isAdjacent(second)){
								tempIJ |= 1;
								tempJI |= 1;
							}
						}
						/*basically break the inner nested loop since we've found all cases for the
						current two sets i and j*/
						if(finished){
							x = aSize;
							y = bSize;
						}
					}
				}
				result[i][j] = tempIJ;
				result[j][i] = tempJI;
			}
		}
	}
	
	/*Performs a heavy analysis on the sets of CIDRs and records how many CIDRs in one set contain, intersect, or
	are adjacent to a CIDR in another set. The resultant matrix is in the form 
	[1,1: 5 (1 |5 |0 ), 1,2: 5 (2 |2 |0 )]
	[2,1: 4 (0 |1 |0 ), 2,2: 4 (0 |4 |0 )]
	The string at index (i,j) shows the interactions. For example, 1,2: 5 (2 |2 |0 ) at index (0,1), shows the
	interaction from set 1 to set 2, where set 1 has 5 CIDRs, 2 CIDRs in set 1 contain a CIDR in set 2, 2 CIDRs in set 1
	 intersect a CIDR in set 2, and 0 CIDRs in set 1 are adjacent to a CIDR in set 2*/
	public static void completeAnalyze(ArrayList<Set<CIDR>> sets, String[][] result){
		int arraySize = sets.size();
		/*loops through each pair of sets of CIDRs and analyzes the interaction between the two*/
		for(int i = 0; i < arraySize; i++){
			for(int j = i; j < arraySize; j++){
				Object[] a = sets.get(i).toArray();
				Object[] b = sets.get(j).toArray();
				int aSize = a.length;
				int bSize = b.length;
				int aConb,bCona,aIntb,bInta,aAdjb,bAdja;
				aConb = bCona = aIntb = bInta = aAdjb = bAdja = 0;
				/*If CIDR y in set j has been found to be contained, intersecting, or adjacent to CIDR x in set j
				then we'll want to keep track of it so we don't check it again since we'll be iterating through
				set j multiple times as we iterate through set i so we store the CIDR y's in a HashSet so we don't
				check them again*/
				HashSet<Integer> jConi = new HashSet<Integer>();
				HashSet<Integer> jInti = new HashSet<Integer>();
				HashSet<Integer> jAdji = new HashSet<Integer>();
				for(int x = 0; x < aSize; x++){
					/*Since we're iterating through set i in the outer loop we can keep track of the containment,
					intersection and adjacency that we've found through simple booleans*/
					boolean foundConA, foundIntA, foundAdjA;
					foundConA = foundIntA = foundAdjA = false;
					CIDR first = (CIDR)a[x];
					for(int y = 0; y < bSize; y++){
						CIDR second = (CIDR)b[y];
						if(!foundConA && first.contains(second)){
							foundIntA = true;
							foundConA = true;
							aConb++;
							aIntb++;
						}
						if(!jConi.contains(y) && second.contains(first)){
							/*contains implies intersection but we still have to check if we've found
							that the CIDR y in set j intersected a CIDR in set i by checking the HashSet*/
							jConi.add(y);
							bCona++;
							if(!jInti.contains(y)){
								bInta++;
								jInti.add(y);
							}
						}
						if(!foundIntA && first.intersecting(second)){
							foundIntA = true;
							aIntb++;
						}
						if(!jInti.contains(y) && second.intersecting(first)){
							jInti.add(y);
							bInta++;
						}
						if(!foundAdjA && first.isAdjacent(second)){
							foundAdjA = true;
							aAdjb++;
						}
						if(!jAdji.contains(y) && second.isAdjacent(first)){
							jAdji.add(y);
							bAdja++;
						}
					}
				}
				/*puts the resulting string in the matrix*/
				StringBuilder sb = new StringBuilder();
				sb.append(String.format("%d,%d: %d (%-4d|%-4d|%-4d)", i + 1, j + 1, a.length, aConb, aIntb, aAdjb));
				result[i][j] = sb.toString();
				
				sb.setLength(0);
				sb.append(String.format("%d,%d: %d (%-4d|%-4d|%-4d)", j + 1, i + 1, b.length, bCona, bInta, bAdja));
				result[j][i] = sb.toString();
			}
		}
	}
	
	public static void printMatrix(int[][] result){
		for(int[] set1: result){
			System.out.print("[");
			for(int set2: set1){
				System.out.print(" " + String.format("%3s", Integer.toBinaryString(set2)).replace(" ", "0"));
			}
			System.out.println(" ]");
		}
	}
	
	public static void printMatrix(String[][] result){
		for(String[] set1: result){
			System.out.printf("%s\n",Arrays.deepToString(set1));
		}
	}
}
