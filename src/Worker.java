public class Worker extends Thread{
    
    private String hash;
    private UnHash unhasher;
    private Integer result;

    Worker(String hash, long timeout){
        this.hash = hash;
        this.unhasher = new UnHash(timeout);
    }

    @Override
    public void run(){
        if((result = unhasher.unhash(hash)) == null){
            System.out.println(hash);
        }else{
            System.out.println(result);
        }
    }
}
