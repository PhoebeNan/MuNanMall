# MuNanMall
木楠分布式商城

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

#已经提交github上的不必要文件，可以用的命令：
git rm -r --cached idea

# fastfdfs分布式文件存储系统 默认端口号为 
22122

#前端木楠后台管理系统页面的启动命令
npm run dev   http://127.0.0.1:8888

#Linux虚拟机的开机账户，密码
root   123456

#Linux的ip地址
192.168.157.130

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
