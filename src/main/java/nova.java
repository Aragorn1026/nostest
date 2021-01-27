import com.netease.cloud.ClientConfiguration;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.*;

import java.io.*;
import java.util.Date;

public class nova {

    private String accessKey = "20af9cab29b1447fa30747fbbd705a3e";
    private String secretKey = "6edc22560947d5534a44212ceff20221";
    private String endPoint = "nos-jd.service.163.org";
    private NosClient nosClient;

    nova(){

        Credentials credentials = new BasicCredentials(accessKey, secretKey);
        ClientConfiguration conf = new ClientConfiguration();
        nosClient = new NosClient(credentials, conf);
        nosClient.setEndpoint(endPoint);
    }

    // list buckets
    public void listbuckets(){

        System.out.println("----List buckets ----");

        for (Bucket bucket : nosClient.listBuckets()) {
            System.out.println(" - " + bucket.getName());
        }

        System.out.println("----List buckets Complete----\n");

    }

    // check bucket exists or not
    public void isexists(String bucketName){

        boolean exists = nosClient.doesBucketExist(bucketName);
        System.out.println("Whether exists: " + exists + "\n");

    }

    // delete a given bucket
    public void deletebucket(String bucketName){

        try {
            nosClient.deleteBucket(bucketName);
            System.out.println("----Delete bucket Complete----\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    // list bucket's ACL
    public void getBucketACL(String bucketName){

        if (nosClient.doesBucketExist(bucketName)){
            CannedAccessControlList acl = nosClient.getBucketAcl(bucketName);
            System.out.println("Bucket " + bucketName + ":" + acl.toString() + "\n");
        }else{
            System.out.println("Bucket given not exsits!");
        }

    }

    public void setBucketACL(String bucketName,String ACL){

        if (ACL == "private"){
            nosClient.setBucketAcl(bucketName, CannedAccessControlList.Private);
        }else if (ACL == "public"){
            nosClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        }else {
            System.out.println("Invalid BucketACL!\n");
        }

    }

    // JD public zcq upload file to public nos:nos-zcq,this function can only upload files whose size less than 100MB
    public void upload(String bucketName,String srcFilePath,String dstFilePath) {

        File uploadFile = new File(srcFilePath);

        try {
            nosClient.putObject(bucketName, dstFilePath, uploadFile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("----Upload File Complete----\n");

    }

//    public void uploadBigFile (String bucketName,String srcFilePath,String dstFilePath) throws FileNotFoundException {
//
//        //初始化一个分块上传，获取分块上传ID，桶名 + 对像名 + 分块上传ID 唯一确定一个分块上传
//        FileInputStream is = new FileInputStream(srcFilePath);
//        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, dstFilePath);
//        //你还可以在初始化分块上传时，设置文件的Content-Type
//        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.setContentType("application/x-msdownload");
//        initRequest.setObjectMetadata(objectMetadata);
//        InitiateMultipartUploadResult initResult = nosClient.initiateMultipartUpload(initRequest);
//        String uploadId = initResult.getUploadId();
//
//        int buffSize = 16000;
//
//        //分块上传的最小单位为16K，最后一块可以小于16K，每个分块都得标识一个唯一的分块partIndex
//        while ((readLen = is.read(buffer, 0, buffSize)) != -1 ){
//            InputStream partStream = new ByteArrayInputStream(buffer);
//            nosClient.uploadPart(new UploadPartRequest().withBucketName("your-bucketname")
//                    .withUploadId(uploadId).withInputStream(partStream)
//                    .withKey("you-objectname").withPartSize(readLen).withPartNumber(partIndex));
//            partIndex++;
//        }
//
//    }

    public void downloadInStream(String bucketName, String sreFilePath){

        NOSObject nosObject = nosClient.getObject(bucketName,sreFilePath);
        //可以通过NOSObject对象的getObjectMetadata方法获取对象的ContentType等元数据信息
        String contentType = nosObject.getObjectMetadata().getContentType();
        //流式获取文件内容
        InputStream in = nosObject.getObjectContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while (true) {
            String line;
            try {
                line = reader.readLine();
                if (line == null) break;
                System.out.println("\n" + line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void downloadFile(String bucketName,String srcFilePath,String localFilePath){

        String destinationFile = localFilePath;
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName,srcFilePath);
        ObjectMetadata objectMetadata = nosClient.getObject(getObjectRequest, new File(destinationFile));

    }

    public void isFileExists(String bucketName,String fileName){

        boolean isExist = nosClient.doesObjectExist(bucketName, fileName,null);
        System.out.println("----" + isExist +"----");

    }

//    public void downloadFileByTime(){
//
//        //假设需要下载的文件的最后修改时间为: Date lastModified;
//        Date lastModified = new Date();
//        //lastModified小于等于指定If-Modified-Since参数
//        GetObjectRequest getObjectRequest = new GetObjectRequest("your-bucketname","your-objectname");
//        Date afterTime = new Date(lastModified.getTime() + 1000);
//        getObjectRequest.setModifiedSinceConstraint(afterTime);
//        //此时nosObject等于null
//        NOSObject nosObject = client.getObject(getObjectRequest);
//        Date beforeTime = new Date(lastModified.getTime() -1000);
//        getObjectRequest.setModifiedSinceConstraint(beforeTime);
//        //此时nosObject不等于null，可以正常获取文件内容
//        nosObject = client.getObject(getObjectRequest);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//        while (true) {
//            String line;
//            try {
//                line = reader.readLine();
//                if (line == null) break;
//                System.out.println("\n" + line);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        try {
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }


}
