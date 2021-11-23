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
    public Grammer(String destinction) throws IOException {
        p=0;
        r=0;
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
        if(!Main.tokens.get(p).name.equals("int"))
            System.exit(1);
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
        Block();
    }
    public void Block() throws IOException {
        if(!Main.tokens.get(p).name.equals("{"))
            System.exit(1);
        writer.write("{\n");
        p++;
        BlockItem();
        if(!Main.tokens.get(++p).name.equals("}"))
            System.exit(1);
        writer.write("}");
    }
    public void BlockItem() throws IOException {

        if(Main.tokens.get(p).name.equals("const")){
            p++;
            ConstDecl();
        }
        else if(Main.tokens.get(p).name.equals("int")){
            p++;
            VarDecl(1);
        }
        else {
            Stmt();
        }
    }
    public void VarDecl(int type) throws IOException {
        Token x=Main.tokens.get(p);

        if(isIdent(x.name)){

            for(Symbol t:symbols)
            {
                if(t.token.name.equals(x.name))
                    System.exit(19);
            }
            Symbol symbol = new Symbol();
            symbol.token=x;
            if(type==0)
            symbol.type="const int";
            else if(type==1)
                symbol.type="int";
            writer.write("%"+r+" = alloca i32\n");
            r++;
            symbol.register="%"+(r-1);
            symbols.add(symbol);
            p++;
            if(Main.tokens.get(p).name.equals("=")){
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
                    writer.write("%"+r+" = call i32 @"+s+"()"+'\n');
                    r++;
                    writer.write("store i32 %"+(r-1)+", i32* "+symbol.register+'\n');
                    p++;
                    VarDecl(type);
                }
                else{String cv=Exp();
                writer.write("store i32 "+cv+", i32* "+symbol.register+'\n');
                p++;
                //System.out.println(Main.tokens.get(p).name);
                VarDecl(type);}
            }
            else if(Main.tokens.get(p).name.equals(","))
            {
                p++;
                VarDecl(type);
            }
            else if(Main.tokens.get(p).name.equals(";"))
            {
                p++;
                BlockItem();
            }
            else
            {
                System.out.println(Main.tokens.get(p).name);
                System.exit(13);
            }
        }
        else if(Main.tokens.get(p).name.equals(","))
        {
            p++;
            VarDecl(type);
        }
        else if(Main.tokens.get(p).name.equals(";"))
        {
            p++;
            BlockItem();
        }
        else
            System.exit(19);
    }
    public void ConstDecl() throws IOException {
        Token x=Main.tokens.get(p);

        if(x.name.equals("int")){
            p++;
            x=Main.tokens.get(p);
            if(isIdent(x.name)){

                for(Symbol t:symbols)
                {
                    if(t.token.name.equals(x.name))
                        System.exit(19);
                }
                Symbol symbol = new Symbol();
                symbol.token=x;
                symbol.type="const int";
                writer.write("%"+r+" = alloca i32\n");
                r++;
                symbol.register="%"+(r-1);
                symbols.add(symbol);
                p++;
                if(Main.tokens.get(p).name.equals("=")){
                    p++;
                    String cv=Exp();
                    writer.write("store i32 "+cv+", i32* "+symbol.register+'\n');
                    p++;
                    //System.out.println(Main.tokens.get(p).name);
                    VarDecl(0);
                }
                else if(Main.tokens.get(p).name.equals(","))
                {
                    p++;
                    VarDecl(0);
                }
                else if(Main.tokens.get(p).name.equals(";"))
                {
                    p++;
                    BlockItem();
                }
                else
                    System.exit(13);
            }
            else
                System.exit(19);
        }
        else
            System.exit(11);
    }
    public void Stmt() throws IOException {

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
            p++;
            BlockItem();
        }
        else if(isIdent(Main.tokens.get(p).name)){
            Symbol x=null;
            //System.out.println(Main.tokens.get(p).name);
            for(Symbol s:symbols)
            {

                if(s.token.name.equals(Main.tokens.get(p).name)){
                    x=s;

                    if(s.type.equals("const int"))
                        System.exit(1);
                    break;
                }
            }
            if(x==null)
                System.exit(9);
            p++;
            if(!Main.tokens.get(p).name.equals("="))
                System.exit(1);
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
                writer.write("%"+r+" = call i32 @"+s+"()"+'\n');
                r++;
                writer.write("store i32 %"+(r-1)+", i32* "+x.register+'\n');
                p++;

            }
            else{
            String cv=Exp();
            writer.write("store i32 "+cv+", i32* "+x.register+'\n');
            p++;}
            if(!Main.tokens.get(p).name.equals(";"))
                System.exit(56);
            p++;
            BlockItem();
        }
        else{
            System.out.println(Main.tokens.get(p).name);
            System.exit(12);
        }

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
        while (!Main.tokens.get(p).name.equals(";")&&!Main.tokens.get(p).name.equals(",")) {
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
            // System.out.println(Main.tokens.get(p-1).name +String.valueOf(Main.tokens.get(p-1).type));
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

                            all.add(y-1,new Token("0"));
                            //all.set(y-1,new Token(String.valueOf(compute(all.get(y-1))*(-1))));
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
                                    for(Symbol s:symbols){
                                        if(s.token.name.equals(d.name)){
                                                er=s;
                                                break;
                                        }
                                    }
                                    if(er==null)
                                        System.exit(3);
                                    writer.write("%"+r+" = load i32, i32* "+er.register+'\n');
                                    r++;
                                    writer.write(" %"+r+" = add i32 0, %"+(r-1)+'\n');
                                    r=r+1;
                                    opnd.add(new Token("%"+(r-1),true));
                                }
                                else{
                                    writer.write(" %"+r+" = add i32 0, "+d.name+'\n');
                                    r++;
                                    opnd.add(new Token("%"+(r-1),true));
                                }
                        }
                        else if(q.name.equals("-")){
                            if(isIdent(d.name)){
                                for(Symbol s:symbols){
                                    if(s.token.name.equals(d.name)){
                                        er=s;
                                        break;
                                    }
                                }
                                if(er==null)
                                    System.exit(3);
                                writer.write("%"+r+" = load i32, i32* "+er.register+'\n');
                                r++;
                                writer.write(" %"+r+" = sub i32 0, %"+(r-1)+'\n');
                                r=r+1;
                                opnd.add(new Token("%"+(r-1),true));
                            }
                            else{
                                writer.write(" %"+r+" = sub i32 0, "+d.name+'\n');
                                r++;
                                opnd.add(new Token("%"+(r-1),true));
                            }


                        }
                        else
                            System.exit(5);
                        continue;
                    }
                    f = opnd.remove(opnd.size()-1);

                    Symbol w1 = null,w2=null;
                    k--;
                    if(q.name.equals("+")){
                        if(isIdent(d.name)&&isIdent(f.name)){
                            for(Symbol s:symbols){
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(s.token.name.equals(d.name))
                                    w2=s;

                                if(w1!=null&&w2!=null)
                                    break;
                            }
                            if(w1==null||w2==null)
                                System.exit(3);
                            writer.write("%"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write("%"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %"+r+" = add i32 %"+(r-2)+", %"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%"+(r-1),true));
                        }
                        else if(isIdent(d.name)&&!isIdent(f.name)){
                            for(Symbol s:symbols){
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                }
                                if(w2!=null)
                                    break;
                            }
                            if(w2==null)
                                System.exit(3);
                            writer.write("%"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %"+r+" = add i32 "+ f.name+", %"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%"+(r-1),true));
                        }
                        else if(!isIdent(d.name)&&isIdent(f.name)){
                            for(Symbol s:symbols){
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(w1!=null)
                                    break;
                            }
                            if(w1==null)
                                System.exit(3);
                            writer.write("%"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write(" %"+r+" = add i32 %"+(r-1)+", "+ d.name +'\n');
                            r=r+1;
                            opnd.add(new Token("%"+(r-1),true));
                        }
                        else{
                            writer.write(" %"+r+" = add i32 "+f.name+", "+d.name+'\n');
                            r++;
                            opnd.add(new Token("%"+(r-1),true));
                        }

                    }
                    else if(q.name.equals("-")){
                        if(isIdent(d.name)&&isIdent(f.name)){
                            for(Symbol s:symbols){
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(s.token.name.equals(d.name))
                                    w2=s;

                                if(w1!=null&&w2!=null)
                                    break;
                            }
                            if(w1==null||w2==null)
                                System.exit(3);
                            writer.write("%"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write("%"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %"+r+" = sub i32 %"+(r-2)+", %"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%"+(r-1),true));
                        }
                        else if(isIdent(d.name)&&!isIdent(f.name)){
                            for(Symbol s:symbols){
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                }
                                if(w2!=null)
                                    break;
                            }
                            if(w2==null)
                                System.exit(3);
                            writer.write("%"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %"+r+" = sub i32 "+f.name+", %"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%"+(r-1),true));
                        }
                        else if(!isIdent(d.name)&&isIdent(f.name)){
                            for(Symbol s:symbols){
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(w1!=null)
                                    break;
                            }
                            if(w1==null)
                                System.exit(3);
                            writer.write("%"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write(" %"+r+" = sub i32 %"+(r-1)+", "+d.name+'\n');
                            r=r+1;
                            opnd.add(new Token("%"+(r-1),true));
                        }
                        else{
                            writer.write(" %"+r+" = sub i32 "+f.name+", "+d.name+'\n');
                            //System.out.println(d.name);
                            r++;
                            opnd.add(new Token("%"+(r-1),true));
                        }
                    }
                    else if(q.name.equals("*")){
                        if(isIdent(d.name)&&isIdent(f.name)){
                            for(Symbol s:symbols){
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(s.token.name.equals(d.name))
                                    w2=s;

                                if(w1!=null&&w2!=null)
                                    break;
                            }
                            if(w1==null||w2==null)
                                System.exit(3);
                            writer.write("%"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write("%"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %"+r+" = mul i32 %"+(r-2)+", %"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%"+(r-1),true));
                        }
                        else if(isIdent(d.name)&&!isIdent(f.name)){
                            for(Symbol s:symbols){
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                }
                                if(w2!=null)
                                    break;
                            }
                            if(w2==null)
                                System.exit(3);
                            writer.write("%"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %"+r+" = mul i32 "+f.name+", %"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%"+(r-1),true));
                        }
                        else if(!isIdent(d.name)&&isIdent(f.name)){
                            for(Symbol s:symbols){
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(w1!=null)
                                    break;
                            }
                            if(w1==null)
                                System.exit(3);
                            writer.write("%"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write(" %"+r+" = mul i32 %"+(r-1)+", "+d.name+'\n');
                            r=r+1;
                            opnd.add(new Token("%"+(r-1),true));
                        }
                        else{
                            writer.write(" %"+r+" = mul i32 "+f.name+", "+d.name+'\n');
                            r++;
                            opnd.add(new Token("%"+(r-1),true));
                        }
                    }
                    else if(q.name.equals("/")){
                        if(isIdent(d.name)&&isIdent(f.name)){
                            for(Symbol s:symbols){
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(s.token.name.equals(d.name))
                                    w2=s;

                                if(w1!=null&&w2!=null)
                                    break;
                            }
                            if(w1==null||w2==null)
                                System.exit(3);
                            writer.write("%"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write("%"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %"+r+" = sdiv i32 %"+(r-2)+", %"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%"+(r-1),true));
                        }
                        else if(isIdent(d.name)&&!isIdent(f.name)){
                            for(Symbol s:symbols){
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                }
                                if(w2!=null)
                                    break;
                            }
                            if(w2==null)
                                System.exit(3);
                            writer.write("%"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %"+r+" = sdiv i32 "+f.name+", %"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%"+(r-1),true));
                        }
                        else if(!isIdent(d.name)&&isIdent(f.name)){
                            for(Symbol s:symbols){
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(w1!=null)
                                    break;
                            }
                            if(w1==null)
                                System.exit(3);
                            writer.write("%"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write(" %"+r+" = sdiv i32 %"+(r-1)+", "+d.name+'\n');
                            r=r+1;
                            opnd.add(new Token("%"+(r-1),true));
                        }
                        else{
                            writer.write(" %"+r+" = sdiv i32 "+f.name+", "+d.name+'\n');
                            r++;
                            opnd.add(new Token("%"+(r-1),true));
                        }
                    }
                    else if(q.name.equals("%")){
                        if(isIdent(d.name)&&isIdent(f.name)){
                            for(Symbol s:symbols){
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(s.token.name.equals(d.name))
                                    w2=s;

                                if(w1!=null&&w2!=null)
                                    break;
                            }
                            if(w1==null||w2==null)
                                System.exit(3);
                            writer.write("%"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write("%"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %"+r+" = srem i32 %"+(r-2)+", %"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%"+(r-1),true));
                        }
                        else if(isIdent(d.name)&&!isIdent(f.name)){
                            for(Symbol s:symbols){
                                if(s.token.name.equals(d.name)){
                                    w2=s;
                                }
                                if(w2!=null)
                                    break;
                            }
                            if(w2==null)
                                System.exit(3);
                            writer.write("%"+r+" = load i32, i32* "+w2.register+'\n');
                            r++;
                            writer.write(" %"+r+" = srem i32 "+f.name+", %"+(r-1)+'\n');
                            r=r+1;
                            opnd.add(new Token("%"+(r-1),true));
                        }
                        else if(!isIdent(d.name)&&isIdent(f.name)){
                            for(Symbol s:symbols){
                                if(s.token.name.equals(f.name)){
                                    w1=s;
                                }
                                if(w1!=null)
                                    break;
                            }
                            if(w1==null)
                                System.exit(3);
                            writer.write("%"+r+" = load i32, i32* "+w1.register+'\n');
                            r++;
                            writer.write(" %"+r+" = srem i32 %"+(r-1)+", "+d.name+'\n');
                            r=r+1;
                            opnd.add(new Token("%"+(r-1),true));
                        }
                        else{
                            writer.write(" %"+r+" = srem i32 "+f.name+", "+d.name+'\n');
                            r++;
                            opnd.add(new Token("%"+(r-1),true));
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
            for(Symbol temp:symbols){
                if(temp.token.name.equals(all.get(0).name)){
                    writer.write("%"+r+" = load i32, i32* "+temp.register+'\n');
                    r++;
                    return "%"+(r-1);
                }
            }
            if(isDigit(all.get(0).name))
                return String.valueOf(Integer.parseInt(all.get(0).name));
            System.exit(10);
        }
        return opnd.get(opnd.size()-1).name;
    }
    public void Number() throws IOException {
        int x=0;
        Token t;
//        String s = Main.tokens.get(p).name;
////        if(isHexadecimal(s)){
////            writer.write(String.valueOf(Integer.parseInt(s.replaceAll("^0[x|X]", ""), 16)));
////        }
////        else if(isOctal(s)){
////            writer.write(String.valueOf(Integer.parseInt(s,8)));
////        }
////        else if(isDecimal(s)){
////            writer.write(String.valueOf(Integer.parseInt(s)));
////        }
////        else
////            System.exit(1);
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

                // System.out.println(Main.tokens.get(p-1).name +String.valueOf(Main.tokens.get(p-1).type));
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
