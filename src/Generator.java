import java.util.HashMap;
import java.util.Map;

public class Generator implements Runnable{
    
    private Map<String, Integer> dictionary = new HashMap<>();
    private final Hash hasher = new Hash();
    private boolean running = true;
    private GeneratorType type;

    public Generator(GeneratorType t){
        this.type = t;
    }

    public Map<String, Integer> getDictionary(){
        return dictionary;
    }

    public void stop(){
        this.running = false;
    }
    
    @Override
    public void run(){
        int i;
        if(type.equals(GeneratorType.EVEN)){
            i = 0;
        }else{
            i = 1;
        }
        
        while(running){
            dictionary.put(hasher.hash(i), i);
            i += 2;
        }
    }
}
