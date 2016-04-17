# CIDR-Analyzer
A java program that will take in sets of CIDR blocks and compare containment, intersection, and adjacency of the 
sets and return the results in a matrix

To compile and run the code, run "make" in the src directory and that should compile the two java files. Then run 
"java Analyze" to run the analysis on the data in the data.txt file. The comments in the code should explain the 
output but I'll put them here too.

	/*Performs a light analysis on the sets of CIDRs by checking the sets and seeing if any CIDRs contain,
	intersect or are adjacent to a CIDR in another set and updates the corresponding bit string. The resultant
	matrix is in the form 	
	[ 110 110 ] 
	[ 010 010 ]
	where a bit string at index (i,j) represents the interaction of set i to set j; the most significant bit shows if 
	a CIDR in set i contains a CIDR in set j, the middle bit shows if a CIDR in set i intersects with a CIDR in set j, 
	and the least significant bit shows if a CIDR in set i intersects with a CIDR in set j. */
	
	/*Performs a heavy analysis on the sets of CIDRs and records how many CIDRs in one set contain, intersect, or
	are adjacent to a CIDR in another set. The resultant matrix is in the form 
	[1,1: 5 (1 |5 |0 ), 1,2: 5 (2 |2 |0 )]
	[2,1: 4 (0 |1 |0 ), 2,2: 4 (0 |4 |0 )]
	The string at index (i,j) shows the interactions. For example, 1,2: 5 (2 |2 |0 ) at index (0,1), shows the
	interaction from set 1 to set 2, where set 1 has 5 CIDRs, 2 CIDRs in set 1 contain a CIDR in set 2, 2 CIDRs in set 1
	intersect a CIDR in set 2, and 0 CIDRs in set 1 are adjacent to a CIDR in set 2*/
	 
The data.txt file should have the number of sets in the first line and each line after that should be a CIDR block followed
by the set it should be in, for example 10.10.0.0/16 1, so 10.10.0.0/16 will be in set 1. The CIDR_generator directory will
generate a data.txt for analysis by the java program. Just run "make" in the directory and then "./gen x y" where x is the
number of sets and y is the max number of CIDR blocks per set.

I would choose to represent the data with a horizontal bar range graph over all possible IP addresses on the x axis. The
horizontal bars will represent the range of IPs that a CIDR block can encompass and so it'll be very easy to see when a CIDR
contains another CIDR, intersects with another CIDR or is adjacent to another CIDR. The limitations of that method would
be that for very large data sets or for CIDRs with very small ranges, it becomes harder to see the interaction
between CIDRs.
