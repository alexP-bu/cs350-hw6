import java.util.Map;

public class Worker implements Runnable{
    
    private String hash;
    private Long timeout;
    private Integer result;
    private Map<String, Integer> dictionary;

    Worker(String hash, Long timeout, Map<String, Integer> dict){
        this.hash = hash;
        this.dictionary = dict;
        this.timeout = timeout;
    }

    @Override
    public void run(){
        if(timeout == null){
            while(result == null){
                result = dictionary.get(hash);
            }
        }else{
            long endTime = System.currentTimeMillis() + timeout;
            while((System.currentTimeMillis() < endTime) && (result == null)){
                result = dictionary.get(hash);
            }
        }

        if(result == null){
            System.out.println(hash);
        }else{
            System.out.println(result);
        }
    }
}
