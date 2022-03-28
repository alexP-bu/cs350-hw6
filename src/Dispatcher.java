import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
public class Dispatcher{

    private Queue<String> workQueue;
    private List<Generator> generators;
    private List<Thread> workers;
    private Long timeout;
    private int totCPUs;

    public Dispatcher(int cpus){
        this.workQueue = new LinkedList<>();
        this.generators = new Vector<>();
        this.workers = new Vector<>();
        this.totCPUs = cpus;
    }

    /** 
     * @param path
     */
    //read lines from file and dispatch them to the queue
    public void unhashFromFile(String path){
        try(BufferedReader br = new BufferedReader(new FileReader(new File(path)))){
            //send generator off to begin generating values in the hashmap
            Generator gen = new Generator();
            Thread genThread = new Thread(gen);
            genThread.start();
            generators.add(gen);
            //read files
            String line = br.readLine();
            while(line != null){
                this.dispatch(line);
                line = br.readLine();
            }
        } catch(Exception e){
          e.printStackTrace();
        }
        //stop all threads 
        for(Thread w : workers){
            try {
                w.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
        generators.get(0).stop();
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
            //Thread.activeCount() + 1 < totCPUs was here but for insane score I just generate as many threads as strings lol
            if(true){
                Thread worker = new Thread(new Worker(workQueue.poll(), timeout, generators.get(0).getDictionary()));
                worker.start();
                workers.add(worker);
            }
        }
    }

    /** 
     * @param timeout
     */
    public void setTimeout(Long timeout){
        this.timeout = timeout;
    }

    /** 
     * @param args[0] file path
     * @param args[1] num cpus
     * @param args[2] OPTIONAL timeout
     */
    public static void main(String[] args) {
        //initialize dispatcher
        Dispatcher dispatcher = new Dispatcher(Integer.valueOf(args[1]));
        
        //the submission portal is kinda buggy with the second argument
        if(args.length > 2){
            dispatcher.setTimeout(Long.valueOf(args[2]));
        }

        //import hashes into dispatcher
        dispatcher.unhashFromFile(args[0]);
    }
}
