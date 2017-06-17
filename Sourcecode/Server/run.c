#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>

pid_t child;

void beforeexit(void) {	
	printf("\n");
	system("make unbind");
	printf("bluetooth off\n");
	sleep(1);
	printf("pushbtn off\n");
	sleep(1);
}

int main(void) {
	pid_t child;
	atexit(beforeexit);
	system("make bind");
	child = fork();
	if(child == -1) {
		printf("fork failed\n");
		return -1;	
	} else if (child == 0) {
		child = fork();
		if(child == -1) {
			return -1;
		} else if(child == 0) {
			printf("bluetooth connected\n");
			execl("./bluetooth",0);
		} else {
			sleep(1);
			printf("pushbtn detecting\n");
			execl("./pushBtn", 0);
		}
	} else {
		system("node app.js");
	}
}

