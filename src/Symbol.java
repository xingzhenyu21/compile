import java.util.ArrayList;

public class Symbol {
    Token token;
    String register;
    String type;
    int value;
    int dimension;
    int x;
    int y;
    int[] array;
    int flag=1;
    int functiontype;
    boolean inline = false;
    int start;
    ArrayList<String> arguments;
    public void f(){
        array = new int[1000];
    }
    public void g(){arguments = new ArrayList<>();}
}
