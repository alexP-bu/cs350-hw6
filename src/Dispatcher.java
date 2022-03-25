import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

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

public class Dispatcher extends Thread{

    private Queue<String> workQueue = new LinkedList<String>();
    private Vector<Worker> workers = new Vector<>();
    private int totCPUs;
    private long timeout;
    
    public Dispatcher(int cpus, long timeout){
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

        //wait for workers to finish at end of file, just for safety
        for(Worker w : workers){
            try{
                w.join();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void dispatch(String hash){
        workQueue.add(hash);
        //if there are jobs in the queue but not available workers, keep running until the workers
        //result in a value or time out
        while(!workQueue.isEmpty()){
            if((Thread.activeCount() < totCPUs)){
                Worker t = new Worker(workQueue.remove(), timeout);
                t.start();
                workers.add(t);
            }
        }
    }

    public static void main(String[] args) {
        //initialize dispatcher
        Dispatcher d = new Dispatcher(Integer.valueOf(args[1]), Long.valueOf(args[2]));
        //import hashes into dispatcher
        d.unhashFromFile(args[0]);
    }
}
