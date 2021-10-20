import java.io.FileWriter;
import java.io.IOException;

public class Grammer {
    int p;
    FileWriter writer;
    public Grammer(String destinction) throws IOException {
        p=0;
        writer = new FileWriter(destinction);
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
        writer.write("{\n");
        p++;
        Stmt();
        if(!Main.tokens.get(++p).name.equals("}"))
            System.exit(1);
        writer.write("\n}");
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
        String s = Main.tokens.get(p).name;
        if(isHexadecimal(s)){
            writer.write(String.valueOf(Integer.parseInt(s.replaceAll("^0[x|X]", ""), 16)));
        }
        else if(isOctal(s)){
            writer.write(String.valueOf(Integer.parseInt(s,8)));
        }
        else if(isDecimal(s)){
            writer.write(String.valueOf(Integer.parseInt(s)));
        }
        else
            System.exit(1);
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
}
