import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Solution to HW-6 as the hw states - however, this is slow. 
 * This is because each worker is generating its own brute force method until it finds a hash.
 * We can speed this up by having some workers generating the dictionary, and another few workers
 * selecting jobs from the queue
 * 
 * runtimes for this version (ms):
 * 11662
 * 11714
 * 11647
 */

public class Dispatcher{

    private Queue<String> workQueue;
    private Long timeout;
    private int totCPUs;

    
    public Dispatcher(int cpus){
        this.workQueue = new LinkedList<>();
        this.totCPUs = cpus;
    }

    public Dispatcher(int cpus, Long timeout){
        this.workQueue = new LinkedList<>();
        this.totCPUs = cpus;
        this.timeout = timeout;
    }


    public void unhashFromFile(String path){
        try(BufferedReader br = new BufferedReader(new FileReader(new File(path)))){
            String line = br.readLine();
            while(line != null){
                this.dispatch(line);
                line = br.readLine();
            }
        } catch(Exception e){
          e.printStackTrace();
        }
    }

    public void dispatch(String hash){
        workQueue.add(hash);
        //if there are jobs in the queue but not available workers, keep running until the workers
        //result in a value or time out
        while(!workQueue.isEmpty()){
            if(Thread.activeCount() < totCPUs){
                Worker t = new Worker(workQueue.poll(), timeout);
                t.start();
            }
        }
    }

    public static void main(String[] args) {
        //initialize dispatcher
        Dispatcher d;
        if(args.length < 3){
            d = new Dispatcher(Integer.valueOf(args[1]));
        }else{
            d = new Dispatcher(Integer.valueOf(args[1]), Long.valueOf(args[2]));
        }
        //import hashes into dispatcher
        d.unhashFromFile(args[0]);
    }
}
