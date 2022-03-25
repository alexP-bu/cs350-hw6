import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;
/**
 * Upgrade 1; using a concurrenthashmap to not have to recompute values
 * Runtimes with one generator:
 * 12935
 * 13131
 * 12950
 * Runtimes with two generators:
 * 12974
 * 
 */

public class Dispatcher extends Thread{

    private Queue<String> workQueue;
    private Vector<Worker> workers;
    private Vector<Generator> generators;
    private int totCPUs;
    private long timeout;

    Map<String, Integer> generatedEvenHashes;
    Map<String, Integer> generatedOddHashes;
    
    public Dispatcher(int cpus, long timeout){
        this.generatedEvenHashes = new HashMap<>();
        this.generatedOddHashes = new HashMap<>();
        this.workQueue = new LinkedList<String>();
        this.workers = new Vector<>();
        this.generators = new Vector<>();
        this.totCPUs = cpus;
        this.timeout = timeout;
    }

    public void unhashFromFile(String path){
        long startTime = System.currentTimeMillis();
        try(BufferedReader br = new BufferedReader(new FileReader(new File(path)))){
            String line = br.readLine();
            //lets send a generators to start generating hashes
            //two generators - one generating odd numbers and one even
            int numGenerators = 2;
            for(int i = 0; i < numGenerators; i++){
                Generator g;
                if(i % 2 == 0){
                    g = new Generator(generatedEvenHashes, GeneratorType.EVEN);
                }else{
                    g = new Generator(generatedOddHashes, GeneratorType.ODD);
                }
                g.start();
                generators.add(g);
            }
            //read lines from file
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
        //kill any generators running
        for(Generator g : generators){
            g.finished();
        }
        //for debugging
        System.out.println("RUNTIME: " + (System.currentTimeMillis() - startTime));
    }

    public void dispatch(String hash){
        workQueue.add(hash);
        //if there are jobs in the queue but not available workers, keep running until the workers
        //result in a value or time out
        while(!workQueue.isEmpty()){
            if((Thread.activeCount() - 2 < totCPUs)){
                Worker t = new Worker(generatedEvenHashes, generatedOddHashes, workQueue.poll(), timeout);
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
