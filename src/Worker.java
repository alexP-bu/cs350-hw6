import java.util.HashMap;
import java.util.List;

public class Worker implements Runnable{
    
    private String hash;
    private Long timeout;
    private Integer result = null;
    private List<HashMap<String, Integer>> dictionaries;

    Worker(String hash, Long timeout, List<HashMap<String,Integer>> dictionaries){
        this.hash = hash;
        this.dictionaries = dictionaries;
        this.timeout = timeout;
    }

    private void attemptUpdateResult(){
        for (HashMap<String,Integer> map : dictionaries) {
            if(map.containsKey(hash)){
                result = map.get(hash);
                break;
            }
        }
    }

    @Override
    public void run(){
        if(timeout == null){
            while(result == null){
                this.attemptUpdateResult();
            }
        }else{
            long endTime = System.currentTimeMillis() + timeout;
            while((System.currentTimeMillis() < endTime) && (result == null)){
                this.attemptUpdateResult();
            }
        }

        if(result == null){
            Dispatcher.writeToOutput(hash);
        }else{
            Dispatcher.writeToOutput(String.valueOf(result));
        }
    }
}
