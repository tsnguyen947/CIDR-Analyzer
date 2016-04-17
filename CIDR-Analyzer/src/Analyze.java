import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Analyze {

	public static void main(String args[]){
		File data = new File("data.txt");
		Scanner in = null;
		try {
			in = new Scanner(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int numSets = in.nextInt();
		in.nextLine();
		ArrayList<Set<CIDR>> sets = new ArrayList<Set<CIDR>>(numSets);
		for(int i = 0; i < numSets; i++)
			sets.add(new HashSet<CIDR>());
		while(in.hasNextLine()){
			StringTokenizer getCIDR = new StringTokenizer(in.nextLine());
			String nextCIDR = getCIDR.nextToken();
			int set = Integer.parseInt(getCIDR.nextToken());
			sets.get(set - 1).add(new CIDR(nextCIDR));
		}
		int[][] result = new int[numSets][numSets];
		String[][] result2 = new String[numSets][numSets];
		analyze(sets, result);
		completeAnalyze(sets, result2);
		printMatrix(result);
		printMatrix(result2);
	}
	
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
							if(first.containedIn(second))
								tempIJ |= 6;
							if(second.containedIn(first))
								tempJI |= 6;
						}
						if((tempIJ & 2) == 0 || (tempJI & 2) == 0){
							finished = false;
							if(first.intersecting(second))
								tempIJ |= 2;
							if(second.intersecting(first))
								tempJI |= 2;
						}
						if((tempIJ & 1) == 0 || (tempJI & 1) == 0){
							finished = false;
							if(first.isAdjacent(second))
								tempIJ |= 1;
							if(second.isAdjacent(first))
								tempJI |= 1;
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
	
	public static void completeAnalyze(ArrayList<Set<CIDR>> sets, String[][] result){
		int arraySize = sets.size();
		for(int i = 0; i < arraySize; i++){
			for(int j = i; j < arraySize; j++){
				Object[] a = sets.get(i).toArray();
				Object[] b= sets.get(j).toArray();
				int aSize = a.length;
				int bSize = b.length;
				int aConb,bCona,aIntb,bInta,aAdjb,bAdja;
				aConb = bCona = aIntb = bInta = aAdjb = bAdja = 0;
				for(int x = 0; x < aSize; x++){
					boolean foundConA, foundIntA, foundAdjA;
					foundConA = foundIntA = foundAdjA = false;
					CIDR first = (CIDR)a[x];
					for(int y = 0; y < bSize; y++){
						boolean foundConB, foundIntB, foundAdjB;
						foundConB = foundIntB = foundAdjB = false;
						CIDR second = (CIDR)b[y];
						if(!foundConA && first.containedIn(second)){
							foundIntA = true;
							foundConA = true;
							aConb++;
							aIntb++;
						}
						if(!foundConB && second.containedIn(first)){
							foundIntB = true;
							foundConB = true;
							bCona++;
							bInta++;
						}
						if(!foundIntA && first.intersecting(second)){
							foundIntA = true;
							aIntb++;
						}
						if(!foundIntB && second.intersecting(first)){
							foundIntB = true;
							bInta++;
						}
						if(!foundAdjA && first.isAdjacent(second)){
							foundAdjA = true;
							aAdjb++;
						}
						if(!foundAdjB && second.isAdjacent(first)){
							foundAdjB = true;
							bAdja++;
						}
					}
				}
				StringBuilder sb = new StringBuilder();
				sb.append(String.format("%d,%d: %d (", i + 1, j + 1, a.length));
				sb.append(String.format("%-4d",aConb));
				sb.append("| ");
				sb.append(String.format("%-4d",aIntb));
				sb.append("| ");
				sb.append(String.format("%-4d",aAdjb));
				sb.append(")");
				result[i][j] = sb.toString();
				sb.setLength(0);
				sb.append(String.format("%d,%d: %d (", j + 1, i + 1, b.length));
				sb.append(String.format("%-4d",bCona));
				sb.append("| ");
				sb.append(String.format("%-4d",bInta));
				sb.append("| ");
				sb.append(String.format("%-4d",bAdja));
				sb.append(")");
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
