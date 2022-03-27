import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Solution to HW-6 as the hw states - however, this is slow. 
 * This is because each worker is generating its own brute force method until it finds a hash.
 * Can we speed this up with a hashmap so we dont have to recompute values?
 * Or maybe have different workers that generate different parts of that hashmap?
 */

public class Dispatcher{

    private Queue<String> workQueue;
    private Long timeout;
    private int totCPUs;

    public Dispatcher(int cpus, Long timeout){
        this.workQueue = new LinkedList<>();
        this.totCPUs = cpus;
        this.timeout = timeout;
    }

    /** 
     * @param path
     */
    //read lines from file and dispatch them to the queue
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

    /** 
     * @param hash
     */
    //add unit of work to work queue 
    public void dispatch(String hash){
        workQueue.add(hash);
        //if there are jobs in the queue but not available workers, keep running until there 
        //are no jobs left in the queue (workers aren't capped)
        while(!workQueue.isEmpty()){
            if(Thread.activeCount() < totCPUs){
                Thread thread = new Thread(new Worker(workQueue.poll(), timeout));
                thread.start();
            }
        }
    }

    /** 
     * @param args[0] file path
     * @param args[1] num cpus
     * @param args[2] OPTIONAL timeout
     */
    public static void main(String[] args) {
        //initialize dispatcher
        Dispatcher dispatcher;
        //the submission portal is kinda buggy with the second argument
        if(args.length < 3){    
            dispatcher = new Dispatcher(Integer.valueOf(args[1]), null);
        }else{
            dispatcher = new Dispatcher(Integer.valueOf(args[1]), Long.valueOf(args[2]));
        }
        //import hashes into dispatcher
        dispatcher.unhashFromFile(args[0]);
    }
}
