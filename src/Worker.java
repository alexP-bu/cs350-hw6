import java.util.Map;

public class Worker implements Runnable{
    
    private String hash;
    private Long timeout;
    private Integer result;
    private Map<String, Integer> dictionary1;
    private Map<String, Integer> dictionary2;

    Worker(String hash, Long timeout, Map<String, Integer> dict1, Map<String, Integer> dict2 ){
        this.hash = hash;
        this.dictionary1 = dict1;
        this.dictionary2 = dict2;
        this.timeout = timeout;
    }

    @Override
    public void run(){
        if(timeout == null){
            while(result == null){
                if(dictionary1.containsKey(hash) || dictionary2.containsKey(hash)){
                    result = dictionary1.containsKey(hash) ? dictionary1.get(hash) : dictionary2.get(hash);
                    break;
                }
            }
        }else{
            long endTime = System.currentTimeMillis() + timeout;
            while((System.currentTimeMillis() < endTime) && (result == null)){
                if(dictionary1.containsKey(hash) || dictionary2.containsKey(hash)){
                    result = dictionary1.containsKey(hash) ? dictionary1.get(hash) : dictionary2.get(hash);
                    break;
                }
            }
        }

        if(result == null){
            System.out.println(hash);
        }else{
            System.out.println(result);
        }
    }
}
