import java.util.*;
import java.util.regex.Pattern;


public class CIDR {

	private long start_IP;
	private long end_IP;
	private String IP;
	private static Pattern p = Pattern.compile("\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}\\/\\d{1,2}");
	
	/*The string passed into the constructor should be in the format 
	 * XXX.XXX.XXX.XXX/XX, ie "192.168.100.14/24".*/
	public CIDR(String IP){
		this.IP = IP;
		if(!p.matcher(IP).matches())
			throw new IllegalArgumentException("Invalid CIDR provided");
		StringTokenizer getClasses = new StringTokenizer(IP, ".");
		start_IP = 0;
		end_IP = 0;
		for(int i = 0; i < 3; i++){
			int temp = Integer.parseInt(getClasses.nextToken());
			if(temp < 0 || temp > 255)
				throw new IllegalArgumentException("Invalid CIDR provided");
			this.start_IP +=temp;
			this.start_IP <<= 8;
		}

		this.start_IP += Integer.parseInt(getClasses.nextToken("/").replaceAll("\\D+",""));
		long mask = 0xffffffffL;
		int bits = Integer.parseInt(getClasses.nextToken());
		int change = (int)Math.pow(2, 32 - bits) - 1;
		mask -= change;
		this.start_IP &= mask;
		this.end_IP = this.start_IP + change;
	}
	
	public boolean equals(Object other){
		if(!(other instanceof CIDR))
			return false;
		else{
			CIDR otherCIDR = (CIDR) other;
			return this.start_IP == otherCIDR.getStart() && this.end_IP == otherCIDR.getEnd();
		}
	}
	
	public long getStart(){
		return this.start_IP;
	}
	
	public long getEnd(){
		return this.end_IP;
	}
	
	public String toString(){
		return IP;
	}

	public static String toIP(long n){
		StringBuilder sb = new StringBuilder();
		sb.append(n & 255);
		n >>= 8;
		for(int i = 0; i < 3; i++){
			sb.insert(0, ".");
			sb.insert(0, n & 255);
			n >>= 8;
		}
		return sb.toString();
	}
	
	public boolean contains(CIDR other){
		return this.start_IP < other.getStart() && this.end_IP > other.getEnd();
	}
	
	public boolean intersecting(CIDR other){
		return this.start_IP <= other.getEnd() && this.end_IP >= other.getStart() ||
				other.getStart() <= this.end_IP && other.getEnd() >= this.start_IP;
	}
	
	public boolean isAdjacent(CIDR other){
		return this.start_IP == other.getEnd() + 1 || this.end_IP == other.getStart() - 1;
	}
	
	
}
