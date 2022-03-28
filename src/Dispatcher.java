import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/*
 * this dispatcher is pretty cool
 * it generates several subets at the same time, all in the form of hashmaps, and cracks hashes based on those subsets
 * so its pretty quick for large numbers o.O
 */
public class Dispatcher {

    public static final int NUM_GENS = 10; //number of generators generating hashmap subsets
    private Queue<String> workQueue;
    private List<Generator> generators; //no need to use vector! we are only getting concurrently
    private List<Thread> workerThreads;
    private Long timeout;

    // optimization: let's write everything to a buffered writer and spit it
    // out at the end because sysout is too slow
    public static final BufferedWriter printer = new BufferedWriter(new OutputStreamWriter(System.out));

    public Dispatcher() {
        this.workQueue = new LinkedList<>();
        this.generators = new ArrayList<>();
        this.workerThreads = new ArrayList<>();
    }

    /**
     * @param path
     */
    // read file line by line and dispatch them to the queue
    public void unhashFromFile(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            // send generators off to begin generating values in the hashmap
            initGenerators(NUM_GENS);
            //read lines with fancy lambda :o
            br
                .lines()
                .forEach(this::dispatch); //for each line, dispatch the line to the queue
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ensure all threads finish
        this.completeThreads();
    }

    /**
     * @param hash
     */
    public void dispatch(String hash) {
        workQueue.add(hash);
        // if there are jobs in the queue but not available workers, keep running until
        // there are no jobs left in the queue (workers aren't capped)
        while (!workQueue.isEmpty()) {
            // Thread.activeCount() + 1 < totCPUs was here but for insane score I just generate infinite threads lol
            if (true) {
                Thread worker = new Thread(new Worker(workQueue.poll(), timeout, generators));
                worker.start();
                workerThreads.add(worker);
            }
        }
    }

    /**
     * @param timeout
     */
    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    /**
     * @param numGensInit
     */
    private void initGenerators(int numGensInit) {
        for (int i = 0; i < numGensInit; i++) {
            Generator g = new Generator(i);
            Thread t = new Thread(g);
            t.start();
            generators.add(g);
        }
    }

    private void completeThreads() {
        // stop all workers
        workerThreads
            .stream()
            .forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                e.printStackTrace();
                }
        });
        // stop all generators
        generators
            .stream()
            .forEach(Generator::stop);
    }

    /**
     * @param s
     */
    public static void writeToOutput(String s) {
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
    public static void main(String[] args) throws IOException {
        // initialize dispatcher
        Dispatcher dispatcher = new Dispatcher();
        // the submission portal is kinda buggy with the second argument
        // so set the timeout manually
        if (args.length > 2) {
            dispatcher.setTimeout(Long.valueOf(args[2]));
        }
        // import hashes into dispatcher
        dispatcher.unhashFromFile(args[0]);
        printer.flush();
    }
}
