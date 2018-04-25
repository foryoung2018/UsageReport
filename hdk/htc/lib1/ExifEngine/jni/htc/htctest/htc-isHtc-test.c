#include <stdio.h>
#include <string.h>
#include <htc/htcParse.h>


int main(int argc,char **argv){

    if(argc < 2){
	printf("Usage: %s image.jpt",argv[0]);
	return 1;
    }

    int htc =0 ;
    htc = isHtc(argv[1]);
    return 0;
}
