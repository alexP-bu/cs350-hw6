import java.util.Map;

//The purpose of the worker is to take a hash, and see if it's in the concurrenthashmap.

public class Worker extends Thread{
    
    private String hash;
    private Integer result;
    private Map<String, Integer> generatedEvenHashes;
    private Map<String, Integer> generatedOddHashes;
    private long timeout;

    Worker(Map<String, Integer> map1, Map<String, Integer> map2, String hash, long timeout){
        this.timeout = timeout;
        this.hash = hash;
        this.generatedEvenHashes = map1;
        this.generatedOddHashes = map2;
        this.result = null;
    }

    @Override
    public void run(){
        //attempt to get hash until timout
        long endTime = System.currentTimeMillis() + timeout;
        while(System.currentTimeMillis() < endTime){
            if((result = generatedEvenHashes.get(hash)) != null){
                break;
            }else if((result = generatedOddHashes.get(hash)) != null){
                break;
            }
        }

        if(result != null){
            System.out.println(result);
        }else{
            System.out.println(hash);
        }
    }
}
