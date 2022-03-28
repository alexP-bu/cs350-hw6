import java.util.List;
import java.util.Map;

public class Worker implements Runnable {

    private String hash;
    private Long timeout;
    private Integer result = null;
    private List<Map<String, Integer>> dictionaries;

    Worker(String hash, Long timeout, List<Map<String, Integer>> dictionaries) {
        this.hash = hash;
        this.dictionaries = dictionaries;
        this.timeout = timeout;
    }

    private Integer attemptUpdateResult() {
        //let's try some fancy lambda stuff for fun :)
        // for (Map<String,Integer> map : dictionaries) {
        // if(map.containsKey(hash)){
        // result = map.get(hash);
        // break;
        // }
        // }
        Map<String, Integer> m;
        if((m = dictionaries
                .stream()
                .filter(map -> map.containsKey(hash))
                .findFirst()
                .orElse(null)) == null){
                    return null;
        }
        return m.get(hash);
    }

    @Override
    public void run() {
        if (timeout == null) {
            while (result == null) {
                result = attemptUpdateResult();
            }
        } else {
            long endTime = System.currentTimeMillis() + timeout;
            while ((System.currentTimeMillis() < endTime) && (result == null)) {
                result = attemptUpdateResult();
            }
        }

        if (result == null) {
            Dispatcher.writeToOutput(hash);
        } else {
            Dispatcher.writeToOutput(String.valueOf(result));
        }
    }
}
