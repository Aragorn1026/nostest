import com.netease.cloud.ClientConfiguration;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.Bucket;
import java.io.File;

public class jdpri {

    public void run() {
        // JD public zcq upload file to public nos
        String accessKey = "8ec87a0f81ce4639b9b2c441edc2f647";
        String secretKey = "435d31044a3847d2b494533be9872cd8";
        String endPoint = "nos-jd-163yun.net";

        Credentials credentials = new BasicCredentials(accessKey, secretKey);
        ClientConfiguration conf = new ClientConfiguration();
        NosClient nosClient = new NosClient(credentials, conf);
        nosClient.setEndpoint(endPoint);

        for (Bucket bucket : nosClient.listBuckets()) {
            System.out.println(" - " + bucket.getName());
        }

        System.out.println("----List buckets Complete----");

        String filePath = "C:\\test.log";
        File uploadfile = new File(filePath);
        try {
            nosClient.putObject("support-nos", "test.log", uploadfile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("----Upload File Complete----");
    }
}
