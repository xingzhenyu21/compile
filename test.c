#include<stdio.h>
#include<string.h>
char token[1000],c;
char reserve[6][30]={"if","else","while","break","continue","return"};
FILE *in;
int p=0;
int getsym();
int reserver();
int transNum(char s[]);
int isDigit();
int isLetter();
void clearToken();
int isSpace();
int isNewline();
int isTab();
int main(int argc,char *argv[]  )
{
	char path[1000];
	
	in=fopen(argv[1],"r");
	int state=1;
	
	while((c=fgetc(in))!=EOF&&state==1){
		state=getsym();
	}
	return 0;
}
void clearToken(){
	p=0;
}
int reserver(){
	int i;
	for(i=0;i<6;i++){
		if(strcmp(reserve[i],token)==0)
		return 1;
	}
	return 0;
}
int isDigit(){
	if(c>='0'&&c<='9')
	return 1;
	return 0;
}
int isLetter(){
	if((c>='A'&&c<='Z')||(c>='a'&&c<='z'))
	return 1;
	return 0;
}
int transNum(char s[]){
	int i = 0,num=0;
	for(i=0;s[i]!='\0';i++){
		num=10*num+s[i]-'0';
	}
	return num;
}
int isSpace(){
	if(c==' ')
	return 1;
	return 0;
}
int isNewline(){
	if(c=='\n')
	return 1;
	return 0;
}
int isTab(){
	if(c=='	')
	return 1;
	return 0;
}
int getsym(){
	clearToken();
	while(isSpace()||isNewline()||isTab())
		c=fgetc(in);
	if(isLetter())
	{
		while(isLetter()||isDigit()){
			token[p++]=c;
			c=fgetc(in);	
		}
		token[p]='\0';
		ungetc(c,in);
		int resultValue = reserver();
		if(resultValue==0) 
		printf("Ident(%s)\n",token);
		else{
		token[0]=token[0]-'a'+'A';
		printf("%s\n",token);
		}
	}
	else if(isDigit()){
		while(isDigit())
		{
			token[p++]=c;
			c=fgetc(in);
		}
		token[p]='\0';
		ungetc(c,in);
		printf("Number(%s)\n",token);
	}
	else if(c=='='){
		c=fgetc(in);
		if(c!='='){
			printf("Assign\n");
			ungetc(c,in);
		}
		else
		{
			printf("Eq\n");
		}
	}
	else if(c==';'){
		printf("Semicolon\n");
	}
	else if(c=='('){
		printf("LPar\n");
	}
	else if(c==')'){
		printf("RPar\n");
	}
	else if(c=='{'){
		printf("LBrace\n");
	}
	else if(c=='}'){
		printf("RBrace\n");
	}
	else if(c=='+'){
		printf("Plus\n");
	}
	else if(c=='*'){
		printf("Mult\n");
	}
	else if(c=='/'){
		printf("Div\n");
	}
	else if(c=='<'){
		printf("Lt\n");
	}
	else if(c=='>'){
		printf("Gt\n");
	}
	else{
		printf("Err\n");
		return 0;
	}
	return 1;
}
