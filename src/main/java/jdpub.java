import com.netease.cloud.ClientConfiguration;
import com.netease.cloud.ClientException;
import com.netease.cloud.ServiceException;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.*;
import com.netease.cloud.services.nos.transfer.TransferManager;
import com.netease.cloud.services.nos.transfer.Upload;
import com.netease.cloud.services.nos.transfer.model.UploadResult;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class jdpub {

    // ak sk
    private final String accessKey = "8266a691389948e69843412a687b1f64";
    private final String secretKey = "cbeaedecc10542af9a708b2ce84d83bc";
    private final String endPoint = "nos-eastchina1.126.net";
    private NosClient nosClient;

    /**
     * 初始化构造
     */
    jdpub(){

        Credentials credentials = new BasicCredentials(accessKey, secretKey);
        ClientConfiguration conf = new ClientConfiguration();
        nosClient = new NosClient(credentials, conf);
        nosClient.setEndpoint(endPoint);
    }

    /**
     * 列出所有桶
     */
    public void listBuckets(){

        System.out.println("----List buckets ----");
        for (Bucket bucket : nosClient.listBuckets()) {
            CannedAccessControlList acl = nosClient.getBucketAcl(bucket.getName());
            System.out.println(" - " + bucket.getName() + " : " + acl);
        }
        System.out.println("----List buckets Complete----\n");

    }

    /**
     * 检查桶是否存在
     * @param bucketName 指定桶名
     */
    public void isBucketExists(String bucketName){

        boolean exists = nosClient.doesBucketExist(bucketName);
        System.out.println("Whether exists: " + exists + "\n");

    }

    /**
     * 删除桶
     * @param bucketName 指定桶名
     */
    public void deleteBucket(String bucketName){

        try {
            nosClient.deleteBucket(bucketName);
            System.out.println("----Delete bucket Complete----\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * 列出指定桶的读写权限
     * @param bucketName 指定桶名
     */
    public void getBucketACL(String bucketName){

        if (nosClient.doesBucketExist(bucketName)){
            CannedAccessControlList acl = nosClient.getBucketAcl(bucketName);
            System.out.println("Bucket " + bucketName + ":" + acl.toString() + "\n");
        }else{
            System.out.println("Bucket given not exsits!");
        }

    }

    /**
     * 设置指定桶的读写权限
     * @param bucketName 指定桶名
     * @param ACL 桶的安全规则：Private（私有）, PublicRead（公共读私有写）
     */
    public void setBucketACL(String bucketName,String ACL){

        if (ACL == "private"){
            nosClient.setBucketAcl(bucketName, CannedAccessControlList.Private);
        }else if (ACL == "public"){
            nosClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        }else {
            System.out.println("Invalid BucketACL!\n");
        }

    }

    // 上传文件，只能上传小于100MB的文件
    public void upload(String bucketName,String srcFilePath,String dstFilePath) {

        File uploadFile = new File(srcFilePath);

        try {
            nosClient.putObject(bucketName, dstFilePath, uploadFile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("----Upload File Complete----\n");

    }

    // 判断文件是否存在
    public void isFileExists(String bucketName,String fileName){

        boolean isExist = nosClient.doesObjectExist(bucketName, fileName,null);
        System.out.println("----Object " + fileName + " :" + isExist +"----\n");

    }

    // 获取文件元数据
    public void getFileMetadata(String bucketName,String fileName){

        try {
            ObjectMetadata fileMetadata = nosClient.getObjectMetadata(bucketName,fileName);
            String rawData = fileMetadata.getRawMetadata().toString();
            System.out.println(fileMetadata.getRawMetadata().toString().replace(",","\n") + "\n");

            System.out.println("contentType: " + fileMetadata.getContentType());
            System.out.println("contentLength: " + fileMetadata.getContentLength());
            System.out.println("getLastModified: " + fileMetadata.getLastModified());
            System.out.println("contentMD5 :" + fileMetadata.getContentMD5());
            System.out.println("tag :" + fileMetadata.getETag());
            System.out.println("x-nos-object-name: " + fileMetadata.getObjectName());


            System.out.println("ExpirationTime :" + fileMetadata.getExpirationTime());
            System.out.println("versionID :" + fileMetadata.getVersionId());
            System.out.println("callbackRet :" + fileMetadata.getCallbackRet());
            System.out.println("expirationTimeRuleId :" + fileMetadata.getExpirationTimeRuleId());
//            System.out.println(" :" + );


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    // 删除单个文件
    public void deleteFile(String bucketName,String fileName){
        try {
            nosClient.deleteObject(bucketName,fileName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // 删除指定桶内的多个文件
    public void deleteFiles(String bucketName, String... fileNames){

        DeleteObjectsResult result = null;
        try {
            DeleteObjectsRequest deleteObjectsRequest = (new DeleteObjectsRequest(bucketName)).withKeys(fileNames);
            result = nosClient.deleteObjects(deleteObjectsRequest);
            List<DeleteObjectsResult.DeletedObject> deleteObjects = result.getDeletedObjects();
            // 全部正常删除时打印结果
            for (DeleteObjectsResult.DeletedObject items: deleteObjects){
                System.out.println(items.getKey() + " -- Deleted!");
            }
            // 如果存在删除失败的对象，则只打印删除失败的结果
        } catch (MultiObjectDeleteException e) {
            List<MultiObjectDeleteException.DeleteError> deleteErrors = e.getErrors();
            for (MultiObjectDeleteException.DeleteError error : deleteErrors) {
                System.out.println(error.getKey() + " -- ignored!");
            }
        } catch (ServiceException e) {
            //捕捉nos服务器异常错误
            System.out.println(e.getMessage());
        } catch (ClientException ace) {
            //捕捉客户端错误
            System.out.println(ace.getMessage());
        }

    }

    // 大文件上传，有问题
    public void uploadBigFile (String bucketName,String srcFilePath,String dstFilePath) throws Exception {

        //初始化一个分块上传，获取分块上传ID，桶名 + 对像名 + 分块上传ID 唯一确定一个分块上传
        FileInputStream is = new FileInputStream(srcFilePath);
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, dstFilePath);
        //你还可以在初始化分块上传时，设置文件的Content-Type
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("video/x-ms-wmv");
        initRequest.setObjectMetadata(objectMetadata);
        InitiateMultipartUploadResult initResult = nosClient.initiateMultipartUpload(initRequest);
        String uploadId = initResult.getUploadId();

        int buffSize = 16000;

        //分块上传的最小单位为16K，最后一块可以小于16K，每个分块都得标识一个唯一的分块partIndex
        int readLen = 0;
        int partIndex = 1;
        byte[] buffer = {};
        while (( readLen = is.read(buffer, 0, buffSize)) != -1 ){
            InputStream partStream = new ByteArrayInputStream(buffer);
            nosClient.uploadPart(new UploadPartRequest().withBucketName("nos-zcq")
                    .withUploadId(uploadId).withInputStream(partStream)
                    .withKey(dstFilePath).withPartSize(readLen).withPartNumber(partIndex));
            partIndex++;
        }

        //这里可以检查分块是否全部上传，分块MD5是否与本地计算相符，如果不符或者缺少可以重新上传
        List<PartETag> partETags = new ArrayList<PartETag>();

        int nextMarker = 0;
        while (true) {
            ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, dstFilePath, uploadId);
            listPartsRequest.setPartNumberMarker(nextMarker);
            PartListing partList = nosClient.listParts(listPartsRequest);
            for (PartSummary ps : partList.getParts()) {
                nextMarker++;
                partETags.add(new PartETag(ps.getPartNumber(), ps.getETag()));
            }

            if (!partList.isTruncated()) {
                break;
            }
        }

        CompleteMultipartUploadRequest completeRequest =  new CompleteMultipartUploadRequest(bucketName,dstFilePath, uploadId, partETags);
        CompleteMultipartUploadResult completeResult = nosClient.completeMultipartUpload(completeRequest);

    }

    // 流式下载文件
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

    // 下载文件
    public void downloadFile(String bucketName,String srcFilePath,String localFilePath){

        try {
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, srcFilePath);
            ObjectMetadata objectMetadata = nosClient.getObject(getObjectRequest, new File(localFilePath));
            System.out.println("----Download complete----\n");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    // 根据时间下载文件
    public void downloadFileByTime(String bucketName,String srcFilePath){

        //假设需要下载的文件的最后修改时间为: Date lastModified;
        Date lastModified = new Date();
        //lastModified小于等于指定If-Modified-Since参数
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName,srcFilePath);
        Date afterTime = new Date(lastModified.getTime() + 1000);
        getObjectRequest.setModifiedSinceConstraint(afterTime);
        //此时nosObject等于null
        NOSObject nosObject = nosClient.getObject(getObjectRequest);
        Date beforeTime = new Date(lastModified.getTime() - 1000);
        getObjectRequest.setModifiedSinceConstraint(beforeTime);
        //此时nosObject不等于null，可以正常获取文件内容
        nosObject = nosClient.getObject(getObjectRequest);
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

    // 按字符范围获取文件内容
    public void downloadFileByRange(String bucketName,String srcFilePath, long startIndex,long endIndex){

        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName,srcFilePath);
        // setRange的起始范围是按字符算的，这里指的是从头到第10000个字符
        getObjectRequest.setRange(startIndex, endIndex);
        NOSObject nosObject = nosClient.getObject(getObjectRequest);
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

    // 文件copy，支持桶内copy，同用户跨桶copy
    public void copyFile(String srcBucket, String srcFile, String dstBucket, String dstFile){

        try {
            nosClient.copyObject(srcBucket, srcFile, dstBucket, dstFile);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    // 移动文件，可以实现重命名功能，只能在桶内move
    public void moveFile(String bucketName, String oldName, String newName){

        try {
            nosClient.moveObject(bucketName, oldName, bucketName, newName);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // 简单列举桶内文件，最多返回100条对象记录
    public void listFiles(String bucketName){

        ObjectListing objectListing = nosClient.listObjects(bucketName);
        List<NOSObjectSummary> sums = objectListing.getObjectSummaries();
        for (NOSObjectSummary s : sums) {
            System.out.println("\t" + s.getKey());
        }

    }

    // 分页列举桶内文件
    public void listFileByPage(String bucketName){

        List<NOSObjectSummary> listResult = new ArrayList<NOSObjectSummary>();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.setBucketName(bucketName);
        listObjectsRequest.setMaxKeys(50);
        ObjectListing listObjects = nosClient.listObjects(listObjectsRequest);
        do {
            listResult.addAll(listObjects.getObjectSummaries());
            if (!listObjects.isTruncated()) {
                ListObjectsRequest request = new ListObjectsRequest();
                request.setBucketName(listObjectsRequest.getBucketName());
                request.setMarker(listObjects.getNextMarker());
                listObjects =  nosClient.listObjects(request);
            } else {
                break;
            }
        } while (listObjects != null);

    }

    /**
     * 使用工具类上传，大小文件均可上传
     */
    public void TransferManager(String bucketName,String srcFilePath,String dstFilePath)throws Exception{

        //先实例化一个NosClient
        Credentials credentials = new BasicCredentials(accessKey, secretKey);
        NosClient nosClient = new NosClient(credentials);
        nosClient.setEndpoint(endPoint);
        //然后通过nosClient对象来初始化TransferManager
        TransferManager transferManager = new TransferManager(nosClient);

        //上传文件
        Upload upload = transferManager.upload(bucketName, dstFilePath, new File(srcFilePath));
        UploadResult result = upload.waitForUploadResult();
        transferManager.shutdownNow();

    }

}
