package com.zyn.mall.util;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhaoyanan
 * @create 2019-11-13-8:21
 */
public class FastdfsUtils {


    /**
     * 上传图片到fastdfs服务器的storage
     *
     * @param multipartFile
     */
    public static String imageUpLoad(Class classType, MultipartFile multipartFile) {

        //获取资源路径
        String conf_filename = classType.getResource("/tracker.conf").getPath();
        String fdfsIp="http://192.168.157.130";
        try {
            ClientGlobal.init(conf_filename);
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageClient storageClient = new StorageClient(trackerServer, null);
            String originalFilename = multipartFile.getOriginalFilename();

            int i = originalFilename.lastIndexOf(".");
            String substring = originalFilename.substring(i + 1);

            String[] uploadFiles = storageClient.upload_file(multipartFile.getBytes(), substring, null);
            //s1=group1,s2=M00/00/00/wKidgl3LCe2AGPvvAAJHy6uK3Ps770.png
            for (String s : uploadFiles) {
                fdfsIp+="/"+s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fdfsIp;
    }
}
