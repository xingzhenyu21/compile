import java.io.IOException;
import java.io.PushbackReader;

public class Func {
    public static boolean sch(int ch){
        if((char)ch=='*'||(char)ch=='/'||(char)ch==','||(char)ch=='+'||(char)ch=='-'||(char)ch=='\r'||(char)ch==' '||(char)ch=='!'||(char)ch=='\n'||(char)ch=='\t'||(char)ch=='('|| (char)ch==')'||(char)ch=='{'||(char)ch=='}'||(char)ch=='/'||(char)ch==';')
            return true;
        if((char)ch=='<'||(char)ch=='>'||(char)ch=='=')
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
                    if(ch==-1)
                        return null;
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
            else{
                reader.unread(ch);
                ch='/';
                break;
            }
        }
        if(ch==-1)
            return null;
        String s="";
        s = s + (char)ch;
        if((char)ch=='{'||(char)ch=='('||(char)ch=='-'||(char)ch=='+'||(char)ch=='*'||(char)ch=='/'||(char)ch==')'||(char)ch=='}'||(char)ch=='%'||(char)ch==',')
            return new Token(s,1);
        if(((char)ch=='<'||(char)ch=='>'||(char)ch=='='||(char)ch=='!')){
            int temp=reader.read();
            if((char)temp=='='){
                s=s+(char)temp;
            }
            else{
                reader.unread(temp);
            }
            return new Token(s,1);
        }
        if((char)ch<='9'&&(char)ch>='0')
        {

            while ((ch= reader.read())!=-1){
                if(sch(ch)||!((char)ch<='9'&&(char)ch>='0'))
                    break;
                s = s + (char)ch;

            }
        }
        else{

            while ((ch= reader.read())!=-1){
                if(sch(ch))
                    break;
                s = s + (char)ch;

            }
        }

        if(ch==-1) return new Token(s);
        reader.unread(ch);
        return new Token(s);
    }
}
