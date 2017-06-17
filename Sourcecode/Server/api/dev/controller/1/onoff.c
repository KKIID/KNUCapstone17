#include <stdlib.h>
#include <stdio.h>

int main(int argc, char* argv[]) {
	char operation[1024];
	if(argc == 3) { 
		sprintf(operation, "echo %s > %s", argv[2], argv[1]);	
		system(operation);
		return 0;
	} else {
		return -1;
	}
}
