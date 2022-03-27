public class Worker implements Runnable{
    
    private String hash;
    private UnHash unhasher;
    private Integer result;

    Worker(String hash, Long timeout){
        this.hash = hash;
        this.unhasher = new UnHash(timeout);
    }

    @Override
    public void run(){
        if((this.result = unhasher.unhash(hash)) == null){
            System.out.println(hash);
        }else{
            System.out.println(result);
        }
    }
}
