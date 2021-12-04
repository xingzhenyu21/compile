

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;

public class Main {
    public  static  ArrayList<Token> tokens = new ArrayList<Token>();
    public  static String[] function={"AAA","BBB","CCC"};
    public static void main(String[] args) throws IOException {
        PushbackReader reader = new PushbackReader(new FileReader(args[0]),1024);
        int ch;
        Token test;
        ch=reader.read();
        while(ch!=-1) {
            test = Func.getToken(ch,reader);
            if(test == null)
                break;

            tokens.add(test);

            ch= reader.read();
        }
//////       System.out.println(tokens.size());
//        for(Token t:tokens){
//            System.out.println(t.name);
//        }
//////        if(tokens.size()!=9)
//////            System.exit(1);

        Grammer grammer = new Grammer(args[1]);

        grammer.CompUnit();
        grammer.writer.close();

    }
}
