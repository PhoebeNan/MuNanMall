package com.zyn.mall.manager;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallManagerWebApplicationTests {

    @Test
    public void contextLoads() throws IOException, MyException {

        String con_filename = this.getClass().getResource("/tracker.conf").getPath();
        ClientGlobal.init(con_filename);

        TrackerClient trackerClient = new TrackerClient();

        TrackerServer trackerServer = trackerClient.getConnection();

        StorageClient storageClient = new StorageClient(trackerServer, null);

        //第三个参数代表图片元数据列表
        String[] pngs = storageClient.upload_file("C:/Users/zhaoyanan/Pictures/QQ浏览器截图/QQ浏览器截图20190627081646.png", "png", null);

        String ip = "http://192.168.157.130";
        for (String png : pngs) {
            ip+="/"+png;
            System.out.println(png);
        }
        System.out.println(ip);
        //http://192.168.157.130/group1/M00/00/00/wKidgl3LCe2AGPvvAAJHy6uK3Ps770.png
    }

}
