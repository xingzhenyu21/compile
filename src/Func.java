import java.io.IOException;
import java.io.PushbackReader;

public class Func {
    public static boolean sch(int ch){
        if((char)ch=='\r'||(char)ch==' '||(char)ch=='\n'||(char)ch=='\t'||(char)ch=='('|| (char)ch==')'||(char)ch=='{'||(char)ch=='}'||(char)ch=='/'||(char)ch==';')
            return true;
        return false;
    }
    public static Token getToken(int c,PushbackReader reader) throws IOException {
        int ch=c;

        while((char)ch==' '||(char)ch=='\n'||(char)ch=='\t'||(char)ch=='\r'){
            ch=reader.read();
        }
        while((char)ch=='/'){
            ch = reader.read();
            if((char)ch == '/'){
                while ((char)ch!='\n'){
                    ch = reader.read();
                }
                while((char)ch==' '||(char)ch=='\n'||(char)ch=='\t'||(char)ch=='\r'){
                    ch=reader.read();
                }
            }
            else if((char)ch == '*'){
                ch = reader.read();

                while ((char)ch!='*'){
                    ch = reader.read();
                    if(ch==-1)
                        System.exit(1);
                }
                ch = reader.read();
                if((char)ch!='/')
                    System.exit(1);
                ch = reader.read();
                while((char)ch==' '||(char)ch=='\n'||(char)ch=='\t'||(char)ch=='\r'){
                    ch=reader.read();
                }
            }
            else
                System.exit(1);
        }
        String s="";
        s+=(char)ch;
        while ((ch= reader.read())!=-1){
            if(sch(ch))
                break;
            s = s + (char)ch;

        }
        if(s.equals(""))
            return null;
        if(ch==-1) return new Token(s);
        reader.unread(ch);
        return new Token(s);
    }
}
