

import java.io.*;
import java.util.ArrayList;

public class Main {
    public  static  ArrayList<Token> tokens = new ArrayList<Token>();
    public static void main(String[] args) throws IOException {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            FileReader fr = new FileReader(args[0]);
            BufferedReader bf = new BufferedReader(fr);
            String str;
            // 按行读取字符串
            while ((str = bf.readLine()) != null) {
                System.out.println(str);
            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
