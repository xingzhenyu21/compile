public class Token {
    String name;
    int type;
    public Token(String name){
        type = 0;
        this.name = name;
    }
    public Token(String name,int type){
        this.type = type;
        this.name = name;
    }

}
