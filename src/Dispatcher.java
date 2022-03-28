import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/*
 * this dispatcher is pretty cool
 * it generates several subets at the same time, all in the form of hashmaps, and cracks hashes based on those subsets
 * so its pretty quick for large numbers o.O
 */
public class Dispatcher{

    public static final int NUM_GENS = 10;
    private Queue<String> workQueue;
    private List<Generator> generators;
    private List<Thread> workers;
    private List<HashMap<String, Integer>> dictionaries;
    private Long timeout;

    //final optimization: let's write everything to a buffered writer and spit it out at the end
    public static BufferedWriter printer = new BufferedWriter(new OutputStreamWriter(System.out));

    public Dispatcher(){
        this.workQueue = new LinkedList<>();
        this.generators = new ArrayList<>();
        this.workers = new ArrayList<>();
        this.dictionaries = new ArrayList<HashMap<String, Integer>>();
    }

    /** 
     * @param path
     */
    //read lines from file and dispatch them to the queue
    public void unhashFromFile(String path){
        try(BufferedReader br = new BufferedReader(new FileReader(new File(path)))){
            //send 5 generators off to begin generating values in the hashmap
            initGenerators(NUM_GENS);
            //read files
            String line = br.readLine();
            while(line != null){
                this.dispatch(line);
                line = br.readLine();
            }
        } catch(Exception e){
          e.printStackTrace();
        }
        //finish all threads
        this.completeThreads();
        try{
            printer.flush();
        }catch (Exception e){
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
            //Thread.activeCount() + 1 < totCPUs was here but for insane score I just generate infinite threads lol
            if(true){
                Thread worker = new Thread(new Worker(workQueue.poll(), timeout, dictionaries));
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

    private void initGenerators(int numGensInit){
        for(int i = 0; i < numGensInit; i++){
            Generator g = new Generator(i);
            Thread t = new Thread(g);
            t.start();
            generators.add(g);
            dictionaries.add(g.getDictionary());
        }
    }

    private void completeThreads(){
        //stop all threads 
        workers.forEach(arg0 -> {
            try {
                arg0.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        });
        //stop all generators
        generators.forEach(Generator::stop);
    }

    public static void writeToOutput(String s){
        try {
            printer.write(s + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 
     * @param args[0] file path
     * @param args[1] num cpus
     * @param args[2] OPTIONAL timeout
     */
    public static void main(String[] args) {
        //initialize dispatcher
        Dispatcher dispatcher = new Dispatcher();
        
        //the submission portal is kinda buggy with the second argument
        if(args.length > 2){
            dispatcher.setTimeout(Long.valueOf(args[2]));
        }

        //import hashes into dispatcher
        dispatcher.unhashFromFile(args[0]);
    }
}
