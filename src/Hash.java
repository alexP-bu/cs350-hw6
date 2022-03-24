import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Hash {

    MessageDigest md;

    public Hash(){
        try{
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
        
    String hash(int hash){
        //convert digest to string, found on stackoverflow
        return String.format("%032x", new BigInteger(1, md.digest(String.valueOf(hash).getBytes(StandardCharsets.UTF_8))));
    }

    public static void main(String[] args) {
        Hash h = new Hash();
        System.out.println(h.hash(12345));
    }
    
}
