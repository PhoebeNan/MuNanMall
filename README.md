# MuNanMall
木楠分布式商城

#加班时间(17：00-21：00)
11.8   11.12

# mall-user用户服务：
#8080
#mall-user-service 用户服务的service层
8070 
#mall-user-web 用户服务的web层
8080

#mall-manager-web 商品分类服务的web层
8081
#mall-service-web 商品分类服务的service层
8071

#mall-item-web 商品详细服务的web层
8082
#mall-item-service 商品详细服务的service层
此商品详细服务的service层用的是商品分类服务的service层，
因为其item前台页面使用的是spu和sku等功能已经在商品分类服务的service层写过了

#mall-search-web 商品搜索服务的web层
8083
#mall-search-service 商品搜索服务的service层
8073

#已经提交github上的不必要文件，可以用的命令：
git rm -r --cached idea

# fastfdfs分布式文件存储系统 默认端口号为 
22122

#前端木楠后台管理系统页面的启动命令
npm run dev   http://127.0.0.1:8888

#Linux虚拟机的开机账户，密码
root   123456

#并发压力测试，apache haus 命令  D:\apache-haus\Apache24\bin
先开启，输入  httpd.exe    c代表并发数  n代表请求数
abs或者ab -c 200 -n 1000 http://www.mall.com/lock

#Linux的ip地址
192.168.157.130   centos7的ip地址：192.168.1.73   ip addr
windows 192.168.94.44
00 0c:29:ba bb 43

#nginx 的启动方式Linux   
去 /usr/local/nginx/sbin写入命令
./nginx
显示如下命令表示成功
[root@hadoop1 sbin]# ps -ef|grep nginx 
root       2266      1  0 16:50 ?        00:00:00 nginx: master process ./nginx
nobody     2267   2266  0 16:50 ?        00:00:00 nginx: worker process
root       2269   1944  0 16:50 pts/0    00:00:00 grep nginx

#nginx的启动方式Windows
D:\day01Movie\leiyou_tools\nginx-1.12.2
start nginx.exe
nginx.exe -s stop  ， nginx.exe -s QUIT停止
nginx.exe -s reload    重新载入Nginx

#elasticsearch 用su es命令切换用户  cd /opt/es/elasticsearch-6.3.1/bin   http://192.168.157.131:9200/
启动用nohup ./elasticsearch  讲控制台日志输出到nohup.out文件中
ps -ef|grep elasticsearch

#kibana 相当于Navicat， 连接es的客户端
http://192.168.157.131:5601
 ps -ef|grep node
xpack.security.enabled: false 
配置es集群后，必须都启动，才能启动kibana


#关于redis  用Java程序连接时一定要使用密码
cd /usr/local/redis/bin  ./redis-cli -h 192.168.157.130 -p 6379 -a 123456  在客户端上连接redis服务端 在连接后用shutdown命令关闭redis服务端
cd /usr/lccal/redis  ./bin/redis-server ./redis.conf  加载redis配置文件并开启redis服务端
ps -ef|grep redis

#关于zookeeper
ps -ef|grep zookeeper 出现一段很长的文字表示服务已经启动
service zookeeper start   启动服务
service zookeeper stop    停止服务

#duboo服务列表网址
service dubbo-admin start  启动服务
service dubbo-admin stop   停止服务
http://192.168.157.130:8080/dubbo/governance/applications

#hosts文件路径已经用其他域名代替localhost
C:\Windows\System32\drivers\etc
127.0.0.1 localhost user.mall.com cart.mall.com manage.mall.com www.mall.com item.mall.com

#使用以下两个命令可以下载maven中的依赖
    <repositories>
        <repository>
            <id>nexus-aliyun</id>
            <name>Nexus aliyun</name>
            <url>http://maven.aliyun.com/nexus/content/repositories/central</url>
            <layout>default</layout>
            <!-- 是否开启发布版构件下载 -->
            <releases>
                <enabled>true</enabled>
            </releases>
            <!-- 是否开启快照版构件下载 -->
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>
    
    mvn clean install -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true