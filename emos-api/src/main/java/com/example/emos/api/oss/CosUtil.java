package com.example.emos.api.oss;

import cn.hutool.core.util.IdUtil;
import com.example.emos.api.common.util.PageUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.AnonymousCOSCredentials;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.ciModel.job.DocHtmlRequest;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.Download;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import com.qcloud.cos.transfer.Upload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class CosUtil {
    @Value("${tencent.cloud.appId}")
    private String appId;

    @Value("${tencent.cloud.secretId}")
    private String secretId;

    @Value("${tencent.cloud.secretKey}")
    private String secretKey;

    @Value("${tencent.cloud.region}")
    private String region;

    @Value("${tencent.cloud.bucket}")
    private String bucket;

    private COSClient getCosClient() {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        COSClient cosClient = new COSClient(cred, clientConfig);
        return cosClient;
    }





    private TransferManager getTransferManager() {
        COSClient cosClient = getCosClient();

        ExecutorService threadPool = Executors.newFixedThreadPool(32);
        TransferManager transferManager = new TransferManager(cosClient, threadPool);

        // 设置高级接口的配置项
        // 分块上传阈值和分块大小分别为 5MB 和 1MB
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(5 * 1024 * 1024);
        transferManagerConfiguration.setMinimumUploadPartSize(1 * 1024 * 1024);
        transferManager.setConfiguration(transferManagerConfiguration);

        return transferManager;
    }

    public PageUtils searchFileByPage() {
        COSClient cosClient = getCosClient();

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.setBucketName(bucket);           // 设置 bucket 名称
        listObjectsRequest.setPrefix("/test/");             // 设置列出的对象名以 prefix 为前缀
        listObjectsRequest.setMaxKeys(5);                   // 设置最大列出多少个对象, 一次 listobject 最大支持1000

        ObjectListing objectListing = null;                 // 保存列出的结果
        try {
            objectListing = cosClient.listObjects(listObjectsRequest);
        } catch (CosServiceException e) {
            e.printStackTrace();
        } catch (CosClientException e) {
            e.printStackTrace();
        }

        // object summary 表示此次列出的对象列表
        List<COSObjectSummary> cosObjectSummaries = objectListing.getObjectSummaries();

        cosClient.shutdown();

        PageUtils pageUtils = new PageUtils(cosObjectSummaries, 5, 0, 5);
        return pageUtils;
    }

    public HashMap uploadfile(MultipartFile file) throws IOException {
        TransferManager transferManager = getTransferManager();

        String fileName = file.getOriginalFilename();
        String path = "/test/" + IdUtil.simpleUUID() + fileName.substring(fileName.lastIndexOf("."));

        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(file.getSize());
        meta.setContentEncoding("UTF-8");
        meta.setContentType(file.getContentType());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, path, file.getInputStream(), meta);
        putObjectRequest.setStorageClass(StorageClass.Standard);

        try {
            // 高级接口会返回一个异步结果Upload
            // 可同步地调用 waitForUploadResult 方法等待上传完成，成功返回UploadResult, 失败抛出异常
            Upload upload = transferManager.upload(putObjectRequest);
            UploadResult uploadResult = upload.waitForUploadResult();
        } catch (CosServiceException e) {
            e.printStackTrace();
        } catch (CosClientException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        transferManager.shutdownNow(true);

        HashMap map = new HashMap();
        map.put("url", "https://" + bucket + ".cos." + region + ".myqcloud.com" + path);
        map.put("path", path);

        return map;
    }

    public HashMap downloadFile(String path) {
        HashMap map = new HashMap();
        map.put("url", "https://" + bucket + ".cos." + region + ".myqcloud.com/" + path);

        return map;
//        TransferManager transferManager = getTransferManager();
//
//        File localFile = new File("/path/to/localFile");
//
//        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, path);
//        try {
//            // 返回一个异步结果 Download, 可同步的调用 waitForCompletion等待下载结束, 成功返回void, 失败抛出异常
//            Download download = transferManager.download(getObjectRequest, localFile);
//            download.waitForCompletion();
//        } catch (CosServiceException e) {
//            e.printStackTrace();
//        } catch (CosClientException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        transferManager.shutdownNow(true);
    }

    public HashMap previewFile(String path) throws URISyntaxException {
        COSClient cosClient = getCosClient();

        DocHtmlRequest request = new DocHtmlRequest();
        request.setBucketName(bucket);
        //如果需要转为图片 dstType 为 DocHtmlRequest.DocType.jpg
        request.setDstType(String.valueOf(DocHtmlRequest.DocType.html));
        request.setObjectKey(path);
        request.setPage("1");

        //调用接口，获取任务响应对象
        String url = cosClient.GenerateDocPreviewUrl(request);
        cosClient.shutdown();

        HashMap map = new HashMap();
        map.put("url", url);

        return map;
    }







    public HashMap uploadFile(MultipartFile file, TypeEnum type) throws IOException {
        String path = null;
        String fileName = file.getOriginalFilename();
        if (type == type.ARCHIVE) {
            path = "/archive/" + IdUtil.simpleUUID() + fileName.substring(fileName.lastIndexOf("."));
        } else if (type == type.MEETINGINSERT) {
            path = "/meetinginsert/" + IdUtil.simpleUUID() + fileName.substring(fileName.lastIndexOf("."));
        } else if (type == type.LEAVEINSERT) {
            path = "/leaveinsert/" + IdUtil.simpleUUID() + fileName.substring(fileName.lastIndexOf("."));
        } else if (type == type.REIMINSERT) {
            path = "/reiminsert/" + IdUtil.simpleUUID() + fileName.substring(fileName.lastIndexOf("."));
        }

        //元数据信息
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(file.getSize());
        meta.setContentEncoding("UTF-8");
        meta.setContentType(file.getContentType());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, path, file.getInputStream(), meta);
        putObjectRequest.setStorageClass(StorageClass.Standard);
        COSClient client = getCosClient();
        PutObjectResult putObjectResult = client.putObject(putObjectRequest);
        client.shutdown();

        HashMap map = new HashMap();
        map.put("url", "https://" + bucket + ".cos." + region + ".myqcloud.com" + path);
        map.put("path", path);

        return map;
    }

    public void deleteFile(String[] pathes) {
        COSClient client = getCosClient();
        for (String path : pathes) {
            client.deleteObject(bucket, path);
        }
        client.shutdown();
    }

}
