import java.util.HashMap;

public class Generator implements Runnable{
    
    private HashMap<String, Integer> dictionary = new HashMap<>(5_000);
    private final Hash hasher = new Hash();
    private boolean running = true;
    private int id;

    public Generator(int id){
        this.id = id;
    }

    public HashMap<String, Integer> getDictionary(){
        return dictionary;
    }

    public void stop(){
        this.running = false;
    }
    
    @Override
    public void run(){
        int i;
        i = this.id;
        
        while(running){
            dictionary.put(hasher.hash(i), i);
            i += Dispatcher.NUM_GENS; //5 generators
        }
    }
}
