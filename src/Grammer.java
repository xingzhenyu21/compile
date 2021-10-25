import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Grammer {
    int p;
    FileWriter writer;
    public Grammer(String destinction) throws IOException {
        p=0;
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
        writer.write("{");
        p++;
        Stmt();
        if(!Main.tokens.get(++p).name.equals("}"))
            System.exit(1);
        writer.write("}");
    }
    public void Stmt() throws IOException {
        if(!Main.tokens.get(p).name.equals("return"))
            System.exit(1);
        writer.write("  ret i32 ");
        p++;
        Number();
        p++;
        if(!Main.tokens.get(p).name.equals(";"))
            System.exit(1);

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
        if(all.get(all.size()-2).type==1)
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
            //System.out.println(s);
            System.exit(4);
            return 0;
        }

    }
}
