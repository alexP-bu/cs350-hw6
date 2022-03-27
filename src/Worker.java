import java.util.Map;

public class Worker implements Runnable{
    
    private String hash;
    private Integer result;
    private Long timeout;
    private Map<String, Integer> dictionary;

    Worker(String hash, Long timeout, Map<String, Integer> dict){
        this.hash = hash;
        this.dictionary = dict;
        this.timeout = timeout;
    }

    @Override
    public void run(){
        if(timeout == null){
            //interesting style but it works
            while(dictionary.get(hash) == null){}
            System.out.println(dictionary.get(hash));
        }else{
            long endTime = System.currentTimeMillis() + timeout;
            while(System.currentTimeMillis() < endTime){
                result = dictionary.get(hash);
                if(result != null){
                    break;
                }
            }

            if(result == null){
                System.out.println(hash);
            }else{
                System.out.println(result);
            }
        }
    }
}
