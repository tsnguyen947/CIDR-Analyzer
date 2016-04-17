#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <time.h>

int main(int argc, char** argv){
	int numSets;
	int numPerSet;
	if(argc == 3){
		numSets = atoi(argv[1]);
		numPerSet = atoi(argv[2]);
	}
	else{
		printf("invalid arguments provided\n");
		return -1;
	}

	char* file = "../src/data.txt";

	FILE *data;
	data = fopen(file, "w");

	assert(data != NULL);

	srand(time(NULL));

	fputs(argv[1], data);
	fputs("\n", data);

	int i;
	for(i = 1; i <= numSets; i++){
		int numIPs = (rand() % numPerSet) + 1;
		int j;
		char ip[25];
		for(j = 0; j < numIPs; j++){
			memset(ip, 0, 25);
			char *current = ip;
			int seg = rand() % 256;
			sprintf(current,"%d.", seg);
			current += strlen(current);

			seg = rand() % 256;
			sprintf(current, "%d.", seg);
			current += strlen(current);

			seg = rand() % 256;
			sprintf(current, "%d.", seg);
			current += strlen(current);

			seg = rand() % 256;
			sprintf(current, "%d/",seg);
			current += strlen(current);

			seg = (rand() % 32) + 1;
			sprintf(current, "%d %d\n", seg, i);
			fputs(ip, data);
		}
	}
	fclose(data);
	return 1;
}
