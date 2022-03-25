import java.util.Map;

public class Generator extends Thread{
    
    Hash hasher;
    Map<String, Integer> generatedHashes;
    GeneratorType generatorType;
    boolean running;

    public Generator(Map<String, Integer> map, GeneratorType t){
        this.hasher = new Hash();
        this.generatedHashes = map;
        generatorType = t;
        this.running = true;
    }

    public void finished(){
        this.running = false;
    }

    @Override
    public void run(){
        //setup generation
        int count;
        if(generatorType.equals(GeneratorType.EVEN)){
            count = 0;
        }else{
            count = 1;
        }
        //start generation
        while(running){
            generatedHashes.put(hasher.hash(count), count);
            count += 2;
        }
    }

}
