#include<stdio.h>
#include<string.h>
#include<stdlib.h>
char token[1000],ch;
FILE *in,*out;
int p=0;
void FuncDef();
void Ident();
void Block();
void Stmt();
void Number();
int isDigit();
int hexadecimal();
int octal();
int t1();
int t2();
void process_notes();
int main(int argc,char *argv[] )
{
    in=fopen(argv[1],"r");
	out=fopen(argv[2],"w");
	//in=fopen("a.txt","r");
	//out=fopen("b.txt","w");
	ch=fgetc(in);
	while(ch==' '||ch=='\n'||ch=='\t')
		ch=fgetc(in);
	
	FuncDef();
	return 0;
	
}
int octal(){
	if(ch>='0'&&ch<='7')
	return 1;
	return 0;
}
int hexadecimal(){
	if((ch>='0'&&ch<='9')||(ch>='A'&&ch<='F'))
	return 1;
	return 0;
}
int isDigit(){
	if(ch>='0'&&ch<='9')
	return 1;
	return 0;
}
int t1()
{
	int i,sum=0,t;
	for(i=0;token[i]!='\0';i++){
		if(token[i]<='9')
			t=token[i]-'0';
		else
		t=token[i]-'A'+10;
		sum=sum*16+t;
}
	return sum;
}
int t2()
{
	int i,sum=0,t;
	for(i=0;token[i]!='\0';i++){
		t=token[i]-'0';
		sum=sum*8+t;
}
	return sum;
}
void FuncDef(){
	p=0;
	process_notes();
	while(ch==' '||ch=='\n'||ch=='\t')
		ch=fgetc(in);
	while(ch!=' '){
	token[p++]=ch;
	ch=fgetc(in);	
	}
	token[p]='\0';
	if(strcmp(token,"int")!=0)
	exit(1);
	char s[]="define dso_local i32";
	fputs(s,out);
	ch=fgetc(in);
	while(ch==' '||ch=='\n'||ch=='\t')
		ch=fgetc(in);
	Ident();
}
void process_notes(){
	if(ch=='/'){
		ch=fgetc(in);
		if(ch=='/'){
			while(ch!='\r'&&ch!='\n')
			ch=fgetc(in); 
			ch=fgetc(in);
		}
		else if(ch == '*'){
			ch=fgetc(in);
			while(ch!='*'){
				ch=fgetc(in);
				if(ch==EOF){
					exit(1);
				}
			}
			
			ch=fgetc(in);
			if(ch!='/')
			exit(0);
			ch=fgetc(in); 
		}
		else
		exit(1);
	}
}
void Ident(){
	p=0;
	process_notes();
	while(ch!=' '){
	token[p++]=ch;
	ch=fgetc(in);	
	}
	token[p]='\0';	
	if(strcmp(token,"main()")!=0)
	exit(1);
	char s[]="@main()";
	fputs(s,out);
	ch=fgetc(in);
	while(ch==' '||ch=='\n'||ch=='\t')
		ch=fgetc(in);	
	Block();
}
void Block()
{
	process_notes();
	if(ch!='{')
	exit(1);
	fputc(ch,out);
	fputc('\n',out);
	ch=fgetc(in);
	while(ch==' '||ch=='\n'||ch=='\t'||ch=='\r')
		ch=fgetc(in);
	process_notes();
	
	Stmt();
	ch=fgetc(in);
	
	while(ch==' '||ch=='\n'||ch=='\t'||ch=='\n'||ch=='\r')
		ch=fgetc(in);	
	if(ch!='}')
	exit(1);
	fputc(ch,out);
}
void Stmt(){
	p=0;
	while(ch==' '||ch=='\n'||ch=='\t'||ch=='\n'||ch=='\r')
	ch=fgetc(in);
	process_notes();
	while(ch!=' '){
	token[p++]=ch;
	ch=fgetc(in);	
	}
	
	token[p]='\0';	
	if(strcmp(token,"return")!=0)
	exit(1);
	char s[]="ret i32 ";
	fputs(s,out);
	ch=fgetc(in);
	while(ch==' '||ch=='\n'||ch=='\t')
		ch=fgetc(in);
	Number();
	while(ch==' '||ch=='\n'||ch=='\t')
		ch=fgetc(in);
	if(ch!=';')
	exit(1);
	
}
void Number(){
	p=0;
	process_notes();
	while(ch==' '||ch=='\n'||ch=='\t')
		ch=fgetc(in);
	if(ch=='0'){
		ch=fgetc(in);
		if(ch=='x'||ch=='X'){
			ch=fgetc(in);
			while(hexadecimal()){
				token[p++]=ch;
				ch=fgetc(in);
			}
			token[p]='\0';
			fprintf(out,"%d",t1());
		}
		else{
			token[p++]='0';
			while(octal()){
				token[p++]=ch;
				ch=fgetc(in);
			}
			token[p]='\0';
			fprintf(out,"%d",t2());
		}
	}
	else if(ch!='0'&&isDigit()){
		while(isDigit())
		{
			token[p++]=ch;
			ch=fgetc(in);
		}
		token[p]='\0';
		fputs(token,out);
	}
	else
	exit(1);
}
