package QA;

import java.io.Serializable;

/**
 * Класс ответа команды
 */
public class Response implements Serializable {

    private final Object[] response;

    public Response(Object... response) {
        this.response = response;
    }

    public Object[] getResponse(){
        return this.response;
    }

    @Override
    public String toString(){
        String s = "";
        for(Object i: response){
            s += i.toString() + "\n";
        }
        return s;
    }
}
