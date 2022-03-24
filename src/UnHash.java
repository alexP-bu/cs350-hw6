public class UnHash {
    
    private Hash hasher;
    private int cur;
    private long timeout; //default timeout is 5s

    public UnHash(long timeout){
        this.timeout = timeout;
        this.hasher = new Hash();
    }

    //unhash any hash for any algorithm tbh
    public Integer unhash(String hash){
        long endTime = System.currentTimeMillis() + timeout;
        while(!hasher.hash(cur).equals(hash) && (System.currentTimeMillis() < endTime)){
            cur++;
        }

        if(hasher.hash(cur).equals(hash)){
            return cur;
        }else{
            return null;
        }
    }
}