import com.netease.cloud.ClientConfiguration;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.Bucket;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws Exception {

        String bucketName = "nos-zcq";

        jdpub jdpubtest = new jdpub();
        jdpubtest.listBuckets();
        jdpubtest.isBucketExists("testzcq2");
//        jdpubtest.getBucketACL("testzcq2");
//        jdpubtest.deletebucket("noszcqtest3");
        jdpubtest.setBucketACL("testzcq2","private");
//        jdpubtest.getBucketACL("testzcq2");
    // upload(String bucketName,String srcFilePath,String dstFilePath)
//    jdpubtest.upload("nos-zcq","C:\\test.log", "tmp/nginx.log");

//    jdpubtest.downloadInStream("nos-zcq","tmp/nginx.log");
//        jdpubtest.downloadFileByRange("nos-zcq","tmp/nginx.log",0, 10000);
//        jdpubtest.downloadFile("nos-zcq","tmp/nginx.log","D:\\tmp\\test.log");
//        jdpubtest.downloadFileByTime(bucketName, "tmp/nginx.log");
//
//        Date d = new Date();
//        //d.setTime(1600047615);
//        System.out.println(d.getTime() + " -- " + d.toString());

//        jdpubtest.isFileExists("nos-zcq","tmp/nginx.log");
//        jdpubtest.downloadFile("nos-zcq","tmp/nginx.log","D:\\nginx.log");

//        nova nv = new nova();
//        nv.upload("cowork-test","C:\\report.jpg","report.jpg");
//        nv.isFileExists("cowork-test","report.jpg");
//        jdpubtest.deleteFiles("testzcq2","2020-12-30_113940.jpg", "2020-12-30_140629.jpg", "2020-12-30_112740.jpg");

//        jdpubtest.getFileMetadata(bucketName,"tmp/nginx.log");

//        jdpubtest.copyFile(bucketName,"tmp/nginx.log",bucketName,"tmp/nginx2.log");
//        jdpubtest.copyFile(bucketName,"tmp/nginx.log","nos-zcq2","nginx2.log");

//        jdpubtest.moveFile(bucketName,"tmp/nginx222.log","nginx333.log");

//        jdpubtest.listFiles(bucketName);

//        jdpubtest.listFileByPage(bucketName);


//        getUrl gu = new getUrl();
//        String dd = gu.sendGet("http://59.111.148.81:9977/getdate");
//        System.out.println(dd);

//        jdpubtest.uploadBigFile(bucketName,"C:\\Users\\zhangchangqing\\Desktop\\NSF-曾泓杰2.wmv","tmp/bigvideo.wmv");

        jdpubtest.TransferManager(bucketName,"C:\\Users\\zhangchangqing\\Desktop\\NSF-曾泓杰2.wmv","tmp/bigvideo.wmv");

    }




}

