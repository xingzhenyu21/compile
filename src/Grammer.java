import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Stack;

public class Grammer {
    int p;
    FileWriter writer;
    Stack<Symbol> symbols=new Stack<Symbol>();
    int r;
    int current_block;
    int []index = new int[1000];
    public Grammer(String destinction) throws IOException {
        p=0;
        r=1;
        current_block=0;
        writer = new FileWriter(destinction);
    }

    int f(Token t){
        if(t.type==0){
            return 5;
        }
        if(t.name.equals("+"))
            return 2;
        else if(t.name.equals("-"))
            return 2;
        else if(t.name.equals("*"))
            return 4;
        else if(t.name.equals("/"))
            return 4;
        else if(t.name.equals("%"))
            return 4;
        else if(t.name.equals("("))
            return 0;
        else if(t.name.equals(")"))
            return 5;
        else if(t.name.equals("#"))
            return 0;
        else if(t.name.equals("!"))
            return 5;
        else {
            System.exit(1);
            return 0;
        }

    }
    int g(Token t){
        if(t.type==0){
            return 6;
        }
        if(t.name.equals("+"))
            return 1;
        else if(t.name.equals("-"))
            return 1;
        else if(t.name.equals("*"))
            return 3;
        else if(t.name.equals("/"))
            return 3;
        else if(t.name.equals("%"))
            return 3;
        else if(t.name.equals("("))
            return 6;
        else if(t.name.equals(")"))
            return 0;
        else if(t.name.equals("#"))
            return 0;
        else if(t.name.equals("!"))
            return 4;
        else {
            System.exit(1);
            return 0;
        }
    }
    public void CompUnit() throws IOException {
        writer.write("declare i32 @getint()\n");
        writer.write("declare void @putint(i32)\n");
        writer.write("declare i32 @getch()\n");
        writer.write("declare void @putch(i32)\n");
        while(true){
            if(Main.tokens.get(p).name.equals("const")){
                p++;
                ConstDecl2();
                p++;
            }
            else if(Main.tokens.get(p).name.equals("int")){
                if(Main.tokens.get(p+1).name.equals("main"))
                    break;
                p++;
                VarDecl2();
                p++;
            }
            else
                break;
        }

        if(!Main.tokens.get(p).name.equals("int"))
            System.exit(19373367);
        writer.write("define dso_local i32");
        if(!Main.tokens.get(++p).name.equals("main"))
            System.exit(1);
        writer.write(" @main");
        if(!Main.tokens.get(++p).name.equals("("))
            System.exit(1);
        writer.write("(");
        if(!Main.tokens.get(++p).name.equals(")"))
            System.exit(1);
        writer.write(")");
        p++;
        Block(0,0,0);
    }
    public void Block(int z,int labelx,int labely) throws IOException {
        index[++current_block]=symbols.size();
        if(!Main.tokens.get(p).name.equals("{")){
            writer.close();

            System.exit(132);}
        if(z==0)
        writer.write("{\n");
        p++;

        while (p<Main.tokens.size()&&!Main.tokens.get(p).name.equals("}")) {
            BlockItem(labelx,labely);
            p++;

        }

        if(z==0)
        writer.write("}");
        int num = symbols.size()-index[current_block];
        while(num>0){
            symbols.pop();
            num--;
        }
        current_block--;
    }
    public void BlockItem(int labelx,int labely) throws IOException {

        if(Main.tokens.get(p).name.equals("const")){
            p++;
            ConstDecl();

        }
        else if(Main.tokens.get(p).name.equals("int")){
            p++;
            VarDecl();

        }
        else {
            Stmt(labelx,labely);
        }
    }
    public void VarDef2() throws IOException {
        Token x=Main.tokens.get(p);
        if(isIdent(x.name)) {
            if (isDefined(x.name))
                System.exit(127);
            Symbol symbol = new Symbol();
            symbol.token = x;

            symbol.type = "int";

            symbol.register = "@" + x.name;
            symbols.add(symbol);
            p++;
            if (Main.tokens.get(p).name.equals("=")) {
                p++;
                int cv = Exp2();
                symbol.value=cv;
                writer.write(symbol.register+" = dso_local global i32 "+cv+'\n');
            }
            else if(Main.tokens.get(p).name.equals(",")){
                p--;
                symbol.value=0;
                writer.write(symbol.register+" = dso_local global i32 0"+'\n');
            }
            else if(Main.tokens.get(p).name.equals(";")){
                p--;
                symbol.value=0;
                writer.write(symbol.register+" = dso_local global i32 0"+'\n');
            }
            else{
                System.out.println(Main.tokens.get(p).name);
                System.exit(67);
            }
        }
        else
            System.exit(435);
    }
    public void VarDef() throws IOException {
        Token x=Main.tokens.get(p);
        if(isIdent(x.name)) {
            if (isDefined(x.name))
                System.exit(127);
            Symbol symbol = new Symbol();
            symbol.token = x;
            symbol.type = "int";
            writer.write("%x" + r + " = alloca i32\n");
            r++;
            symbol.register = "%x" + (r - 1);
            symbols.add(symbol);
            p++;

            if (Main.tokens.get(p).name.equals("=")) {
                p++;
                if (Main.tokens.get(p).name.equals("getint") || Main.tokens.get(p).name.equals("getch")) {
                    String s;
                    s = Main.tokens.get(p).name;
                    p++;
                    if (!Main.tokens.get(p).name.equals("("))
                        System.exit(34);
                    p++;
                    if (!Main.tokens.get(p).name.equals(")"))
                        System.exit(34);
                    writer.write("%x" + r + " = call i32 @" + s + "()" + '\n');
                    r++;
                    writer.write("store i32 %x" + (r - 1) + ", i32* " + symbol.register + '\n');

                }
                else {
                    String cv = Exp();
                    writer.write("store i32 " + cv + ", i32* " + symbol.register + '\n');
                }
            }
            else if(Main.tokens.get(p).name.equals(",")){
                p--;
            }
            else if(Main.tokens.get(p).name.equals(";")){
                p--;
            }
            else{
                System.exit(67);
            }
        }
        else
            System.exit(435);
    }
    public void VarDecl() throws IOException {
        Token x=Main.tokens.get(p);

        if(isIdent(x.name)){

            while(true){
                VarDef();
                p++;
                if(!Main.tokens.get(p).name.equals(","))
                    break;
                p++;
            }
            if(!Main.tokens.get(p).name.equals(";"))
                System.exit(12);
        }
        else
            System.exit(19);
    }
    public void VarDecl2() throws IOException {
        Token x=Main.tokens.get(p);

        if(isIdent(x.name)){

            while(true){
                VarDef2();
                p++;
                if(!Main.tokens.get(p).name.equals(","))
                    break;
                p++;
            }
            if(!Main.tokens.get(p).name.equals(";"))
                System.exit(12);
        }
        else
            System.exit(19);
    }
    public String LOrExp() throws IOException {
        String qw=null;
        String t;
        while(true){
            t=LAndExp();
            if(qw!=null){
            writer.write("%x"+r+" = or i32 "+qw+", "+t+'\n');
            r++;
            qw="%x"+(r-1);
            }
            else
                qw=t;
            p++;

            if(!Main.tokens.get(p).name.equals("||")) {
                p--;
                break;
            }
            p++;
        }
        return qw;
    }
    public String LAndExp() throws IOException {
        String qw=null;
        String t;
        while(true){
            t=EqExp();
            if(qw!=null){
                writer.write("%x"+r+" = and i32 "+qw+", "+t+'\n');
                r++;
                qw="%x"+(r-1);
            }
            else
                qw=t;
            p++;
            if(!Main.tokens.get(p).name.equals("&&")) {
                p--;
                break;
            }
            p++;
        }
        return qw;
    }
    public String EqExp() throws IOException {
        String qw=null;
        String t;
        int x=0;
        while(true){
            t=RelExp();
            if(qw!=null){
                if(x==1){
                    writer.write("%x"+r+"= icmp eq i32 "+qw+", "+t+'\n');
                    r++;
                    writer.write("%x"+r+"= zext i1 %x"+(r-1)+" to i32\n");
                    r++;
                }
                else {
                    writer.write("%x"+r+"= icmp ne i32 "+qw+", "+t+'\n');
                    r++;
                    writer.write("%x"+r+"= zext i1 %x"+(r-1)+" to i32\n");
                    r++;
                }
                qw="%x"+(r-1);
            }
            else
                qw=t;
            p++;
            if(Main.tokens.get(p).name.equals("==")) {
                x=1;
            }
            else if(Main.tokens.get(p).name.equals("!=")) {
                x=2;
            }
            else{
                p--;
                break;
            }
            p++;
        }
        return qw;
    }
    public String RelExp() throws IOException {
        String qw=null;
        String t;
        int x=0;
        while(true){
            t=AddExp();
            if(qw!=null){
                if(x==1){
                    writer.write("%x"+r+"= icmp slt i32 "+qw+", "+t+'\n');
                    r++;
                    writer.write("%x"+r+"= zext i1 %x"+(r-1)+" to i32\n");
                    r++;
                }
                else if(x==2) {
                    writer.write("%x"+r+"= icmp sgt i32 "+qw+", "+t+'\n');
                    r++;
                    writer.write("%x"+r+"= zext i1 %x"+(r-1)+" to i32");
                    r++;
                }
                else if(x==3) {
                    writer.write("%x"+r+"= icmp sle i32 "+qw+", "+t+'\n');
                    r++;
                    writer.write("%x"+r+"= zext i1 %x"+(r-1)+" to i32\n");
                    r++;
                }
                else {
                    writer.write("%x"+r+"= icmp sge i32 "+qw+", "+t+'\n');
                    r++;
                    writer.write("%x"+r+"= zext i1 %x"+(r-1)+" to i32\n");
                    r++;
                }
                qw="%x"+(r-1);
            }
            else
                qw=t;
            p++;
            if(Main.tokens.get(p).name.equals("<")) {
                x=1;
            }
            else if(Main.tokens.get(p).name.equals(">")) {
                x=2;
            }
            else if(Main.tokens.get(p).name.equals("<=")) {
                x=3;
            }
            else if(Main.tokens.get(p).name.equals(">=")) {
                x=4;
            }
            else{
                p--;
                break;
            }
            p++;
        }
        return qw;
    }
    public String AddExp() throws IOException {
        return Exp();
    }
    public String cond() throws IOException {

        return LOrExp();
    }
    public boolean isDefined(String s){
        for(int i=symbols.size()-1;i>=index[current_block];i--){
            if(symbols.elementAt(i).token.name.equals(s)){
                return true;
            }
        }
        return false;
    }
    public void ConstDecl() throws IOException {
        Token x=Main.tokens.get(p);

        if(x.name.equals("int")) {
            p++;
            while (true) {
                ConstDef();
                p++;
                if (!Main.tokens.get(p).name.equals(","))
                    break;
                p++;
            }
            if(!Main.tokens.get(p).name.equals(";"))
                System.exit(21324);
        }
        else
            System.exit(11);
    }
    public void ConstDecl2() throws IOException {
        Token x=Main.tokens.get(p);

        if(x.name.equals("int")) {
            p++;
            while (true) {
                ConstDef2();
                p++;
                if (!Main.tokens.get(p).name.equals(","))
                    break;
                p++;
            }
            if(!Main.tokens.get(p).name.equals(";"))
                System.exit(21324);
        }
        else
            System.exit(11);
    }
    public void ConstDef() throws IOException {
        Token x=Main.tokens.get(p);
        if(isIdent(x.name)){
            if(isDefined(x.name))
                System.exit(128);
            Symbol symbol = new Symbol();
            symbol.token=x;
            symbol.type="const int";
            writer.write("%x"+r+" = alloca i32\n");
            r++;
            symbol.register="%x"+(r-1);
            symbols.add(symbol);
            p++;
            if(Main.tokens.get(p).name.equals("=")){
                p++;
                String cv=Exp();
                writer.write("store i32 "+cv+", i32* "+symbol.register+'\n');
            }
            else if(Main.tokens.get(p).name.equals(","))
            {
                p--;
            }
            else if(Main.tokens.get(p).name.equals(";"))
            {
                p--;
            }
            else
                System.exit(13);
        }
        else
            System.exit(19);
    }
    public void ConstDef2() throws IOException {
        Token x=Main.tokens.get(p);
        if(isIdent(x.name)){
            if(isDefined(x.name))
                System.exit(128);
            Symbol symbol = new Symbol();
            symbol.token=x;
            symbol.type="const int";

            symbol.register="@"+x.name;
            symbols.add(symbol);
            p++;
            if(Main.tokens.get(p).name.equals("=")){
                p++;
                int cv=Exp2();
                symbol.value=cv;
                writer.write(symbol.register+" = dso_local global i32 "+cv+'\n');
            }
            else if(Main.tokens.get(p).name.equals(","))
            {
                p--;
                symbol.value=0;
                writer.write(symbol.register+" = dso_local global i32 0\n");

            }
            else if(Main.tokens.get(p).name.equals(";"))
            {
                p--;
                symbol.value=0;
                writer.write(symbol.register+" = dso_local global i32 0");
            }
            else
                System.exit(13);
        }
        else
            System.exit(19);
    }
    public void Stmt(int labelx,int labely) throws IOException {

        if(Main.tokens.get(p).name.equals("return")){
            p++;
            String zx=Exp();
            writer.write("  ret i32 ");
            writer.write(zx+'\n');
            p++;
            if(!Main.tokens.get(p).name.equals(";"))
                System.exit(56);


        }
        else if(Main.tokens.get(p).name.equals("putint")||Main.tokens.get(p).name.equals("putch")){
            p++;

            String name=Main.tokens.get(p-1).name;
            if(!Main.tokens.get(p).name.equals("("))
                System.exit(17);
            p++;
            String csv=Exp();

            writer.write("call void @"+name+"(i32 "+csv+")\n");
            p++;
            if(!Main.tokens.get(p).name.equals(")"))
                System.exit(57);
            p++;
            if(!Main.tokens.get(p).name.equals(";"))
                System.exit(57);

        }
        else if(Main.tokens.get(p).name.equals("continue")){
            p++;
            if(!Main.tokens.get(p).name.equals(";"))
                System.exit(34);
            writer.write("br label %x"+labelx+'\n');
        }
        else if(Main.tokens.get(p).name.equals("break")){
            p++;
            if(!Main.tokens.get(p).name.equals(";"))
                System.exit(34);
            writer.write("br label %x"+labely+'\n');
        }
        else if(Main.tokens.get(p).name.equals("while")){
            int label=r;
            r++;
            writer.write("br label %x"+label+'\n');
            writer.write("x"+label+":\n");
            p++;
            if(!Main.tokens.get(p).name.equals("(")){
                System.exit(124);
            }
            p++;
            String cond = cond();
            writer.write("   %x"+r+" = icmp ne i32 "+cond+", 0"+'\n');
            r++;
            int label1 = r;
            int label2 = 1+r;
            writer.write("   br i1 %x"+(r-1)+",label %x"+r+", label %x"+(r+1)+'\n');
            r=r+2;
            p++;
            if(!Main.tokens.get(p).name.equals(")")){

                System.exit(124);
            }
            p++;
            writer.write("x"+label1+":\n");
            Stmt(label,label2);
            writer.write("  br label %x"+label+'\n');
            writer.write("x"+label2+":\n");
        }
        else if(Main.tokens.get(p).name.equals("if")){
            p++;
            if(!Main.tokens.get(p).name.equals("(")){

                System.exit(124);
            }
            p++;
            String cond = cond();
            writer.write("   %x"+r+" = icmp ne i32 "+cond+", 0"+'\n');
            r++;
            int label1 = r;
            int label2 = 1+r;
            writer.write("   br i1 %x"+(r-1)+",label %x"+r+", label %x"+(r+1)+'\n');
            r=r+2;
            p++;

            if(!Main.tokens.get(p).name.equals(")")){

                System.exit(124);
            }
            p++;

            if(Main.tokens.get(p).name.equals("{")) {
                writer.write("x"+label1+":\n");

                Block(1,labelx,labely);
                p++;

                int label3=r++;
                if(Main.tokens.get(p).name.equals("else")){
                    p++;
                    writer.write("br label %x"+label3+'\n');
                    writer.write("x"+label2+":\n");
                    if(Main.tokens.get(p).name.equals("{")){
                        Block(1,labelx,labely);
                        writer.write("br label %x"+label3+'\n');
                        writer.write("x"+label3+":\n");

                    }
                    else{
                        BlockItem(labelx,labely);
                        writer.write("br label %x"+label3+'\n');
                        writer.write("x"+label3+":\n");
                    }
                }
                else{
                    p--;
                    writer.write("br label %x"+label2+'\n');
                    writer.write("x"+label2+":\n");
                }
            }
            else{
                writer.write("x"+label1+":\n");
                int label3=r++;
                BlockItem(labelx,labely);
                p++;

                if(Main.tokens.get(p).name.equals("else")){
                    p++;
                    writer.write("br label %x"+label3+'\n');
                    writer.write("x"+label2+":\n");

                    if(Main.tokens.get(p).name.equals("{")){
                        Block(1,labelx,labely);
                        writer.write("br label %x"+label3+'\n');
                        writer.write("x"+label3+":\n");
                        //p++;

                    }
                    else{

                        BlockItem(labelx,labely);

                        writer.write("br label %x"+label3+'\n');
                        writer.write("x"+label3+":\n");

                    }

                }
                else{
                    p--;
                    writer.write("br label %x"+label2+'\n');
                    writer.write("x"+label2+":\n");
                }
            }

        }

        else if(isIdent(Main.tokens.get(p).name)){
            Symbol x=null;
            for(int i = symbols.size()-1;i>=0;i--)
            {
                Symbol s=symbols.get(i);
                if(s.token.name.equals(Main.tokens.get(p).name)){
                    x=s;

                    if(s.type.equals("const int"))
                        System.exit(1);
                    break;
                }
            }
            if(x==null){

                System.exit(9);}
            p++;
            if(!Main.tokens.get(p).name.equals("=")){

                while(!Main.tokens.get(p).name.equals(";"))
                {
                    p++;
                }
                return;
            }
            p++;
            if(Main.tokens.get(p).name.equals("getint")||Main.tokens.get(p).name.equals("getch")){
                String s;
                s=Main.tokens.get(p).name;
                p++;
                if(!Main.tokens.get(p).name.equals("("))
                    System.exit(34);
                p++;
                if(!Main.tokens.get(p).name.equals(")"))
                    System.exit(34);
                writer.write("%x"+r+" = call i32 @"+s+"()"+'\n');
                r++;
                writer.write("store i32 %x"+(r-1)+", i32* "+x.register+'\n');
                p++;

            }
            else{
                String cv=Exp();
                writer.write("store i32 "+cv+", i32* "+x.register+'\n');
                p++;
            }
            if(!Main.tokens.get(p).name.equals(";"))
                System.exit(56);
        }
        else if(Main.tokens.get(p).name.equals("{")){
            Block(1,labelx,labely);
        }
        else{
            writer.close();
            System.out.println(Main.tokens.get(p).name);
            System.exit(12132);
        }

    }
    public boolean isExp(){
        String t=Main.tokens.get(p).name;
        if(isDigit(t)||isIdent(t)||t.equals("!")||t.equals("+")||t.equals("-")||t.equals("*")||t.equals("/")||t.equals("%")||t.equals("(")||t.equals(")"))
            return true;
        return false;
    }
    public String Exp() throws IOException {
        int x=0;
        Token t;
        ArrayList<Token> optr = new ArrayList<Token>();
        ArrayList<Token> opnd = new ArrayList<Token>();
        ArrayList<Token> all = new ArrayList<Token>();
        optr.add(new Token("#",1));
        int left=0;
        int right=0;

        while (isExp()) {

            if (!all.isEmpty() && (all.get(all.size() - 1).name.equals("+") || all.get(all.size() - 1).name.equals("-")) && (Main.tokens.get(p).name.equals("-") || Main.tokens.get(p).name.equals("+"))) {
                if (all.get(all.size() - 1).name.equals("+") && Main.tokens.get(p).name.equals("+"))
                    p++;
                else if (all.get(all.size() - 1).name.equals("-") && Main.tokens.get(p).name.equals("-")) {

                    all.set(all.size() - 1, new Token("+",1));
                    p++;
                }
                else {

                    all.set(all.size() - 1, new Token("-",1));
                    p++;
                }
            }
            else if(!all.isEmpty()&&all.get(all.size()-1).name.equals("!")&&Main.tokens.get(p).name.equals("!")){
                all.remove(all.size()-1);
                p++;
            }
            else {
                if(Main.tokens.get(p).name.equals("("))
                    left++;
                if(Main.tokens.get(p).name.equals(")")){
                    if(right>=left)
                        break;
                    right++;
                }
                all.add(Main.tokens.get(p++));
            }
        }
        p--;
        System.out.print('\n');
        all.add(new Token("#",1));
        int k=0;

        for(int y=0;y<all.size();y++){
            if(all.get(y).type == 0){
                if(y>0&&(all.get(y-1).name.equals("+")||all.get(y-1).name.equals("-"))){
                    if(y==1||all.get(y-2).name.equals("(")){
                        if(all.get(y-1).name.equals("+")){
                            all.remove(y-1);
                        }
                        else{

                            all.add(y-1,new Token("0"));

                        }
                    }
                }
            }
        }

        if(all.get(all.size()-2).type==1&&!all.get(all.size()-2).name.equals(")"))
            System.exit(7);
        for(int i = 0;i<all.size()-1;i++){
            if(all.get(i).name.equals("*")&&all.get(i+1).name.equals("*"))
                System.exit(8);
        }

        while (k<all.size()){
            t = all.get(k++);

            if(t.type==1){
                if(f(optr.get(optr.size()-1))<g(t)){
                    optr.add(t);
                }
                else if(f(optr.get(optr.size()-1))==g(t)){
                    if(t.name.equals("#")){
                        break;
                    }
                    optr.remove(optr.size()-1);
                }
                else{
                    Token q = optr.remove(optr.size()-1);
                    Token d = opnd.remove(opnd.size()-1);
                    Token f;
                    if(opnd.isEmpty()&&(q.name.equals("+")||q.name.equals("-"))){
                        k--;
                        Symbol er=null;
                        if(q.name.equals("+")){
                                if(isIdent(d.name)){
                                    for(int i=symbols.size()-1;i>=0;i--){
                                        Symbol s=symbols.get(i);
                                        if(s.token.name.equals(d.name)){
                                                er=s;
                                                break;
                                        }
                                    }
                                    if(er==null)
                                        System.exit(3);
                                    writer.write("%x"+r+" = load i32, i32* "+er.register+'\n');
                                    r++;
                                    writer.write(" %x"+r+" = add i32 0, %x"+(r-1)+'\n');
                                    r=r+1;
                                    opnd.add(new Token("%x"+(r-1),true));
                                }
                                else{
                                    writer.write(" %x"+r+" = add i32 0, "+d.name+'\n');
                                    r++;
                                    opnd.add(new Token("%x"+(r-1),true));
                                }
                        }
                        else if(q.name.equals("-")){
                            if(isIdent(d.name)){
                                for(int i=symbols.size()-1;i>=0;i--){
                                    Symbol s=symbols.get(i);
                                    if(s.token.name.equals(d.name)){
                                        er=s;
                                        break;
                                    }
                                }
                                if(er==null)
                                    System.exit(3);
                                writer.write("%x"+r+" = load i32, i32* "+er.register+'\n');
                                r++;
                                writer.write(" %x"+r+" = sub i32 0, %x"+(r-1)+'\n');
                                r=r+1;
                                opnd.add(new Token("%x"+(r-1),true));
                            }
                            else{
                                writer.write(" %x"+r+" = sub i32 0, "+d.name+'\n');
                                r++;
                                opnd.add(new Token("%x"+(r-1),true));
                            }


                        }

                        else
                            System.exit(5);
                        continue;
                    }
                    if(q.name.equals("!")){
                        k--;
                        Symbol er=null;
                        if(isIdent(d.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    er=s;
                                    break;
                                }
                            }
                            if(er==null)
                                System.exit(3);
                            writer.write("%x"+r+"= load i32, i32* "+er.register+'\n');
                            r++;
                            writer.write("%x"+r+"= icmp ne i32 "+(r-1)+", 0"+'\n');
                            r++;
                            writer.write("%x"+r+" = xor i1  %x"+(r-1)+", true"+'\n');
                            r++;
                            writer.write("%x"+r+"= zext i1 %x"+(r-1)+" to i32\n");
                            r++;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                        else{
                            writer.write("%x"+r+"= icmp ne i32 "+d.name+", 0"+'\n');
                            r++;
                            writer.write("%x"+r+" = xor i1  %x"+(r-1)+", true"+'\n');
                            r++;
                            writer.write("%x"+r+"= zext i1 %x"+(r-1)+" to i32\n");
                            r++;
                            opnd.add(new Token("%x"+(r-1),true));
                        }

                        continue;
                    }
                    f = opnd.remove(opnd.size()-1);

                    Symbol w1 = null,w2=null;
                    k--;
                    if(q.name.equals("+")){
                        if(isIdent(d.name)&&isIdent(f.name)){
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                    break;
                                }

                            }
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                    break;
                                }
                            }
                            if(w1==null||w2==null)
                                System.exit(3);
                            if(f.name.equals("sum")&&d.name.equals("a")){
                                for(Symbol sd:symbols)
                                    System.out.println(sd.token.name+" "+sd.register);
                                System.out.println(w1.token.name+" "+w1.register);
                                System.out.println(w2.token.name+" "+w2.register);
                            }
                            writer.write("%x"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write("%x"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %x"+r+" = add i32 %x"+(r-2)+", %x"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                        else if(isIdent(d.name)&&!isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                }
                                if(w2!=null)
                                    break;
                            }
                            if(w2==null)
                                System.exit(3);
                            writer.write("%x"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %x"+r+" = add i32 "+ f.name+", %x"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                        else if(!isIdent(d.name)&&isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(w1!=null)
                                    break;
                            }
                            if(w1==null)
                                System.exit(3);
                            writer.write("%x"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write(" %x"+r+" = add i32 %x"+(r-1)+", "+ d.name +'\n');
                            r=r+1;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                        else{
                            writer.write(" %x"+r+" = add i32 "+f.name+", "+d.name+'\n');
                            r++;
                            opnd.add(new Token("%x"+(r-1),true));
                        }

                    }
                    else if(q.name.equals("-")){
                        if(isIdent(d.name)&&isIdent(f.name)){
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                    break;
                                }

                            }
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                    break;
                                }
                            }
                            if(w1==null||w2==null)
                                System.exit(3);
                            writer.write("%x"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write("%x"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %x"+r+" = sub i32 %x"+(r-2)+", %x"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                        else if(isIdent(d.name)&&!isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                }
                                if(w2!=null)
                                    break;
                            }
                            if(w2==null)
                                System.exit(3);
                            writer.write("%x"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %x"+r+" = sub i32 "+f.name+", %x"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                        else if(!isIdent(d.name)&&isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(w1!=null)
                                    break;
                            }
                            if(w1==null)
                                System.exit(3);
                            writer.write("%x"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write(" %x"+r+" = sub i32 %x"+(r-1)+", "+d.name+'\n');
                            r=r+1;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                        else{
                            writer.write(" %x"+r+" = sub i32 "+f.name+", "+d.name+'\n');

                            r++;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                    }
                    else if(q.name.equals("*")){
                        if(isIdent(d.name)&&isIdent(f.name)){
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                    break;
                                }

                            }
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                    break;
                                }
                            }
                            if(w1==null||w2==null)
                                System.exit(3);
                            writer.write("%x"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write("%x"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %x"+r+" = mul i32 %x"+(r-2)+", %x"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                        else if(isIdent(d.name)&&!isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                }
                                if(w2!=null)
                                    break;
                            }
                            if(w2==null)
                                System.exit(3);
                            writer.write("%x"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %x"+r+" = mul i32 "+f.name+", %x"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                        else if(!isIdent(d.name)&&isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(w1!=null)
                                    break;
                            }
                            if(w1==null)
                                System.exit(3);
                            writer.write("%x"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write(" %x"+r+" = mul i32 %x"+(r-1)+", "+d.name+'\n');
                            r=r+1;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                        else{
                            writer.write(" %x"+r+" = mul i32 "+f.name+", "+d.name+'\n');
                            r++;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                    }
                    else if(q.name.equals("/")){
                        if(isIdent(d.name)&&isIdent(f.name)){
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                    break;
                                }

                            }
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                    break;
                                }
                            }
                            if(w1==null||w2==null)
                                System.exit(3);
                            writer.write("%x"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write("%x"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %x"+r+" = sdiv i32 %x"+(r-2)+", %x"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                        else if(isIdent(d.name)&&!isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                }
                                if(w2!=null)
                                    break;
                            }
                            if(w2==null)
                                System.exit(3);
                            writer.write("%x"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %x"+r+" = sdiv i32 "+f.name+", %x"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                        else if(!isIdent(d.name)&&isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(w1!=null)
                                    break;
                            }
                            if(w1==null)
                                System.exit(3);
                            writer.write("%x"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write(" %x"+r+" = sdiv i32 %x"+(r-1)+", "+d.name+'\n');
                            r=r+1;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                        else{
                            writer.write(" %x"+r+" = sdiv i32 "+f.name+", "+d.name+'\n');
                            r++;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                    }
                    else if(q.name.equals("%")){
                        if(isIdent(d.name)&&isIdent(f.name)){
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                    break;
                                }

                            }
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                    break;
                                }
                            }
                            if(w1==null||w2==null)
                                System.exit(3);
                            writer.write("%x"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write("%x"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %x"+r+" = srem i32 %x"+(r-2)+", %x"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                        else if(isIdent(d.name)&&!isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                }
                                if(w2!=null)
                                    break;
                            }
                            if(w2==null)
                                System.exit(3);
                            writer.write("%x"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %x"+r+" = srem i32 "+f.name+", %x"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                        else if(!isIdent(d.name)&&isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(w1!=null)
                                    break;
                            }
                            if(w1==null)
                                System.exit(3);
                            writer.write("%x"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write(" %x"+r+" = srem i32 %x"+(r-1)+", "+d.name+'\n');
                            r=r+1;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                        else{
                            writer.write(" %x"+r+" = srem i32 "+f.name+", "+d.name+'\n');
                            r++;
                            opnd.add(new Token("%x"+(r-1),true));
                        }
                    }
                    else {
                        System.exit(2);
                    }
                }
            }
            else{
                opnd.add(t);
            }
        }
        if(optr.size()!=1)
            System.exit(3);
        //writer.write(String.valueOf(compute(opnd.get(opnd.size()-1))));

        if(all.size()==2){
            for(int i=symbols.size()-1;i>=0;i--){
                Symbol temp=symbols.get(i);
                if(temp.token.name.equals(all.get(0).name)){
                    writer.write("%x"+r+" = load i32, i32* "+temp.register+'\n');
                    r++;
                    return "%x"+(r-1);
                }
            }
            if(isDigit(all.get(0).name))
                return String.valueOf(Integer.parseInt(all.get(0).name));
            System.exit(10);
        }
        if(isIdent(opnd.get(opnd.size()-1).name)){
            for(int i=symbols.size()-1;i>=0;i--){
                Symbol temp=symbols.get(i);
                if(temp.token.name.equals(opnd.get(opnd.size()-1).name)){
                    writer.write("%x"+r+" = load i32, i32* "+temp.register+'\n');
                    r++;
                    return "%x"+(r-1);
                }
            }

        }
        return opnd.get(opnd.size()-1).name;
    }
    public int Exp2() throws IOException {
        int x=0;
        Token t;
        ArrayList<Token> optr = new ArrayList<Token>();
        ArrayList<Token> opnd = new ArrayList<Token>();
        ArrayList<Token> all = new ArrayList<Token>();
        optr.add(new Token("#",1));
        int left=0;
        int right=0;

        while (isExp()) {
            if (!all.isEmpty() && (all.get(all.size() - 1).name.equals("+") || all.get(all.size() - 1).name.equals("-")) && (Main.tokens.get(p).name.equals("-") || Main.tokens.get(p).name.equals("+"))) {
                if (all.get(all.size() - 1).name.equals("+") && Main.tokens.get(p).name.equals("+"))
                    p++;
                else if (all.get(all.size() - 1).name.equals("-") && Main.tokens.get(p).name.equals("-")) {

                    all.set(all.size() - 1, new Token("+",1));
                    p++;
                }
                else {
                    all.set(all.size() - 1, new Token("-",1));
                    p++;
                }
            }
            else if(!all.isEmpty()&&all.get(all.size()-1).name.equals("!")&&Main.tokens.get(p).name.equals("!")){
                all.remove(all.size()-1);
                p++;
            }
            else {
                if(Main.tokens.get(p).name.equals("("))
                    left++;
                if(Main.tokens.get(p).name.equals(")")){
                    if(right>=left)
                        break;
                    right++;
                }
                all.add(Main.tokens.get(p++));
            }
        }
        p--;
        System.out.print('\n');
        all.add(new Token("#",1));
        int k=0;

        for(int y=0;y<all.size();y++){
            if(all.get(y).type == 0){
                if(y>0&&(all.get(y-1).name.equals("+")||all.get(y-1).name.equals("-"))){
                    if(y==1||all.get(y-2).name.equals("(")){
                        if(all.get(y-1).name.equals("+")){
                            all.remove(y-1);
                        }
                        else{

                            all.add(y-1,new Token("0"));

                        }
                    }
                }
            }
        }

        if(all.get(all.size()-2).type==1&&!all.get(all.size()-2).name.equals(")"))
            System.exit(7);
        for(int i = 0;i<all.size()-1;i++){
            if(all.get(i).name.equals("*")&&all.get(i+1).name.equals("*"))
                System.exit(8);
        }

        while (k<all.size()){
            t = all.get(k++);
            if(t.type==1){
                if(f(optr.get(optr.size()-1))<g(t)){
                    optr.add(t);
                }
                else if(f(optr.get(optr.size()-1))==g(t)){
                    if(t.name.equals("#")){
                        break;
                    }
                    optr.remove(optr.size()-1);
                }
                else{
                    Token q = optr.remove(optr.size()-1);
                    Token d = opnd.remove(opnd.size()-1);
                    Token f;
                    if(opnd.isEmpty()&&(q.name.equals("+")||q.name.equals("-"))){
                        k--;
                        Symbol er=null;
                        if(q.name.equals("+")){
                            if(isIdent(d.name)){
                                for(int i=symbols.size()-1;i>=0;i--){
                                    Symbol s=symbols.get(i);
                                    if(s.token.name.equals(d.name)){
                                        er=s;
                                        break;
                                    }
                                }
                                if(er==null|| !er.type.equals("const int"))
                                    System.exit(3);
                                opnd.add(new Token(String.valueOf(er.value),true));
                            }
                            else{

                                opnd.add(new Token(d.name,true));
                            }
                        }
                        else {
                            if(isIdent(d.name)){
                                for(int i=symbols.size()-1;i>=0;i--){
                                    Symbol s=symbols.get(i);
                                    if(s.token.name.equals(d.name)){
                                        er=s;
                                        break;
                                    }
                                }
                                if(er==null|| !er.type.equals("const int"))
                                    System.exit(3);
                                opnd.add(new Token(String.valueOf(-1*er.value),true));
                            }
                            else{

                                opnd.add(new Token(String.valueOf(-1*Integer.parseInt(d.name)),true));
                            }

                        }
                        continue;
                    }
                    f = opnd.remove(opnd.size()-1);
                    Symbol w1 = null,w2=null;
                    k--;
                    if(q.name.equals("+")){
                        if(isIdent(d.name)&&isIdent(f.name)){
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                    break;
                                }

                            }
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                    break;
                                }
                            }

                            if(w1==null||w2==null||!w1.type.equals("const int")||!w2.type.equals("const int"))
                                System.exit(3);

                            opnd.add(new Token(String.valueOf(w1.value+w2.value),true));
                        }
                        else if(isIdent(d.name)&&!isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                }
                                if(w2!=null)
                                    break;
                            }
                            if(w2==null||!w2.type.equals("const int"))
                                System.exit(3);

                            opnd.add(new Token(String.valueOf(Integer.parseInt(f.name)+w2.value),true));
                        }
                        else if(!isIdent(d.name)&&isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(w1!=null)
                                    break;
                            }
                            if(w1==null||!w1.type.equals("const int"))
                                System.exit(3);

                            opnd.add(new Token(String.valueOf(Integer.parseInt(d.name)+w1.value),true));
                        }
                        else{

                            opnd.add(new Token(String.valueOf(Integer.parseInt(f.name)+Integer.parseInt(d.name)),true));
                        }

                    }
                    else if(q.name.equals("-")){
                        if(isIdent(d.name)&&isIdent(f.name)){
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                    break;
                                }

                            }
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                    break;
                                }
                            }

                            if(w1==null||w2==null||!w1.type.equals("const int")||!w2.type.equals("const int"))
                                System.exit(3);

                            opnd.add(new Token(String.valueOf(w1.value-w2.value),true));
                        }
                        else if(isIdent(d.name)&&!isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                }
                                if(w2!=null)
                                    break;
                            }
                            if(w2==null||!w2.type.equals("const int"))
                                System.exit(3);

                            opnd.add(new Token(String.valueOf(Integer.parseInt(f.name)-w2.value),true));
                        }
                        else if(!isIdent(d.name)&&isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(w1!=null)
                                    break;
                            }
                            if(w1==null||!w1.type.equals("const int"))
                                System.exit(3);

                            opnd.add(new Token(String.valueOf(w1.value-Integer.parseInt(d.name)),true));
                        }
                        else{

                            opnd.add(new Token(String.valueOf(Integer.parseInt(f.name)-Integer.parseInt(d.name)),true));
                        }

                    }
                    else if(q.name.equals("*")){
                        if(isIdent(d.name)&&isIdent(f.name)){
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                    break;
                                }

                            }
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                    break;
                                }
                            }

                            if(w1==null||w2==null||!w1.type.equals("const int")||!w2.type.equals("const int"))
                                System.exit(3);

                            opnd.add(new Token(String.valueOf(w1.value*w2.value),true));
                        }
                        else if(isIdent(d.name)&&!isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                }
                                if(w2!=null)
                                    break;
                            }
                            if(w2==null||!w2.type.equals("const int"))
                                System.exit(3);

                            opnd.add(new Token(String.valueOf(Integer.parseInt(f.name)*w2.value),true));
                        }
                        else if(!isIdent(d.name)&&isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(w1!=null)
                                    break;
                            }
                            if(w1==null||!w1.type.equals("const int"))
                                System.exit(3);

                            opnd.add(new Token(String.valueOf(w1.value*Integer.parseInt(d.name)),true));
                        }
                        else{

                            opnd.add(new Token(String.valueOf(Integer.parseInt(f.name)*Integer.parseInt(d.name)),true));
                        }

                    }
                    else if(q.name.equals("/")){
                        if(isIdent(d.name)&&isIdent(f.name)){
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                    break;
                                }

                            }
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                    break;
                                }
                            }
                            if(w1==null||w2==null||!w1.type.equals("const int")||!w2.type.equals("const int"))
                                System.exit(3);

                            opnd.add(new Token(String.valueOf(w1.value/w2.value),true));
                        }
                        else if(isIdent(d.name)&&!isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                }
                                if(w2!=null)
                                    break;
                            }
                            if(w2==null||!w2.type.equals("const int"))
                                System.exit(3);

                            opnd.add(new Token(String.valueOf(Integer.parseInt(f.name)/w2.value),true));
                        }
                        else if(!isIdent(d.name)&&isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(w1!=null)
                                    break;
                            }
                            if(w1==null||!w1.type.equals("const int"))
                                System.exit(3);

                            opnd.add(new Token(String.valueOf(w1.value/Integer.parseInt(d.name)),true));
                        }
                        else{

                            opnd.add(new Token(String.valueOf(Integer.parseInt(f.name)/Integer.parseInt(d.name)),true));
                        }

                    }
                    else if(q.name.equals("%")){
                        if(isIdent(d.name)&&isIdent(f.name)){
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                    break;
                                }

                            }
                            for(int i = symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                    break;
                                }
                            }

                            if(w1==null||w2==null||!w1.type.equals("const int")||!w2.type.equals("const int"))
                                System.exit(3);

                            opnd.add(new Token(String.valueOf(w1.value%w2.value),true));
                        }
                        else if(isIdent(d.name)&&!isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                }
                                if(w2!=null)
                                    break;
                            }
                            if(w2==null||!w2.type.equals("const int"))
                                System.exit(3);

                            opnd.add(new Token(String.valueOf(Integer.parseInt(f.name)%w2.value),true));
                        }
                        else if(!isIdent(d.name)&&isIdent(f.name)){
                            for(int i=symbols.size()-1;i>=0;i--){
                                Symbol s=symbols.get(i);
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(w1!=null)
                                    break;
                            }
                            if(w1==null||!w1.type.equals("const int"))
                                System.exit(3);

                            opnd.add(new Token(String.valueOf(w1.value%Integer.parseInt(d.name)),true));
                        }
                        else{
                            opnd.add(new Token(String.valueOf(Integer.parseInt(f.name)%Integer.parseInt(d.name)),true));
                        }

                    }
                    else {
                        System.exit(2);
                    }
                }
            }
            else{

                opnd.add(t);
            }
        }
        if(optr.size()!=1)
            System.exit(3);
        if(all.size()==2){
            for(int i=symbols.size()-1;i>=0;i--){
                Symbol temp=symbols.get(i);
                if(temp.token.name.equals(all.get(0).name)&&temp.type.equals("const int")){

                    return temp.value;
                }
            }
            if(isDigit(all.get(0).name))
                return Integer.parseInt(all.get(0).name);
            System.exit(10);
        }
        if(isIdent(opnd.get(opnd.size()-1).name)){
            for(int i=symbols.size()-1;i>=0;i--){
                Symbol temp=symbols.get(i);
                if(temp.token.name.equals(opnd.get(opnd.size()-1).name)&&temp.type.equals("const int")){

                    return temp.value;
                }
            }
            System.exit(1024);
        }
        return Integer.parseInt(opnd.get(opnd.size()-1).name);
    }
    public void Number() throws IOException {
        int x=0;
        Token t;
        ArrayList<Token> optr = new ArrayList<Token>();
        ArrayList<Token> opnd = new ArrayList<Token>();
        ArrayList<Token> all = new ArrayList<Token>();
        optr.add(new Token("#",1));

        while (!Main.tokens.get(p).name.equals(";")) {
            if (!all.isEmpty() && (all.get(all.size() - 1).name.equals("+") || all.get(all.size() - 1).name.equals("-")) && (Main.tokens.get(p).name.equals("-") || Main.tokens.get(p).name.equals("+"))) {
                if (all.get(all.size() - 1).name.equals("+") && Main.tokens.get(p).name.equals("+"))
                    p++;
                else if (all.get(all.size() - 1).name.equals("-") && Main.tokens.get(p).name.equals("-")) {

                    all.set(all.size() - 1, new Token("+",1));
                    p++;
                }
                else {

                    all.set(all.size() - 1, new Token("-",1));
                    p++;
                }
            }
            else
                all.add(Main.tokens.get(p++));


        }
        p--;
        all.add(new Token("#",1));
        int k=0;

        for(int y=0;y<all.size();y++){
            if(all.get(y).type == 0){
                if(y>0&&(all.get(y-1).name.equals("+")||all.get(y-1).name.equals("-"))){
                    if(y==1||all.get(y-2).name.equals("(")){
                        if(all.get(y-1).name.equals("+")){
                            all.remove(y-1);
                        }
                        else{
                            all.remove(y-1);
                            all.set(y-1,new Token(String.valueOf(compute(all.get(y-1))*(-1))));
                        }
                    }
                }
            }
        }
//        for(Token c:all)
//            System.out.println(c.name);
        if(all.get(all.size()-2).type==1&&!all.get(all.size()-2).name.equals(")"))
            System.exit(7);
        for(int i = 0;i<all.size()-1;i++){
            if(all.get(i).name.equals("*")&&all.get(i+1).name.equals("*"))
                System.exit(8);
        }
        for(Token g:all)
            System.out.println(g.name);
        while (k<all.size()){
            t = all.get(k++);
            if(t.type==1){
                    if(f(optr.get(optr.size()-1))<g(t)){
                        optr.add(t);
                    }
                    else if(f(optr.get(optr.size()-1))==g(t)){
                        if(t.name.equals("#")){
                            break;
                        }
                        optr.remove(optr.size()-1);
                    }
                    else{
                        Token q = optr.remove(optr.size()-1);
                        Token d = opnd.remove(opnd.size()-1);
                        int b = compute(d);
                        if(opnd.isEmpty()&&(q.name.equals("+")||q.name.equals("-"))){
                            k--;

                            if(q.name.equals("+")){
                                opnd.add(new Token(String.valueOf(b)));
                            }
                            else
                                opnd.add(new Token(String.valueOf(-1*b)));
                            continue;
                        }
                        d = opnd.remove(opnd.size()-1);
                        int a = compute(d);

                        k--;
                        if(q.name.equals("+"))
                            opnd.add(new Token(String.valueOf(a+b)));
                        else if(q.name.equals("-"))
                            opnd.add(new Token(String.valueOf(a-b)));
                        else if(q.name.equals("*"))
                            opnd.add(new Token(String.valueOf(a*b)));
                        else if(q.name.equals("/"))
                            opnd.add(new Token(String.valueOf(a/b)));
                        else if(q.name.equals("%"))
                            opnd.add(new Token(String.valueOf(a%b)));
                        else {

                            System.exit(2);
                        }
                    }
            }
            else{
                opnd.add(t);
            }
        }
        if(optr.size()!=1)
            System.exit(3);
        writer.write(String.valueOf(compute(opnd.get(opnd.size()-1))));
    }
    public boolean isDigit(String str)
    {
        try {
            int num=Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean  isHexadecimal(String s){

        String rex = "^0[x|X][A-Fa-f0-9]+";
        if(s.matches(rex))
            return true;
        return false;
    }
    public boolean isOctal(String s){
        String rex = "^0[0-7]*";
        if(s.matches(rex))
            return true;
        return false;
    }
    public boolean isIdent(String s){
        String rex="^[A-Za-z_][A-Za-z0-9_]*$";
        if(s.matches(rex))
            return true;
        return false;
    }
    public boolean isDecimal(String s){
        String rex = "[1-9][0-9]*";
        if(s.matches(rex))
            return true;
        return false;
    }
    public int compute(Token t) throws IOException {
        String s = t.name;
        if(t.name.charAt(0)=='-'){
            if(isDecimal(s.substring(1))){
                return -1*Integer.parseInt(s.substring(1));
            }
            else if(isHexadecimal(s.substring(1))){
                return -1*Integer.parseInt(s.substring(1).replaceAll("^0[x|X]", ""), 16);
            }
            else if(isOctal(s.substring(1))){
                return  -1*Integer.parseInt(s.substring(1),8);
            }

            System.exit(5);
            return 0;
        }
        else{
            if(isDecimal(s)){
                return Integer.parseInt(s);
            }
            else if(isHexadecimal(s)){
                return Integer.parseInt(s.replaceAll("^0[x|X]", ""), 16);
            }
            else if(isOctal(s)){
                return  Integer.parseInt(s,8);
            }
            System.out.println(s);
            System.exit(4);
            return 0;
        }

    }
}
