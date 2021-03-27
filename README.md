# MobileControl
Android端基于minicap和minitouch实现仿Webkey远程控屏软件，需Root权限。
> 此项目仅做功能展示，可能存在bug。
## 实现功能
- 支持查看手机画面、远程控制
- 支持上传文件到设备
## 项目引入框架
|    框架   |主要功能                          
|:-----------:|:-----------:
|[minicap](https://github.com/openstf/minicap)|手机截屏            
|[minitouch](https://github.com/openstf/minitouch)|手机模拟触摸          
|[nanohttpd](https://github.com/NanoHttpd/nanohttpd)|搭建HTTP服务器
|[Java-WebSocket](https://hub.fastgit.org/TooTallNate/Java-WebSocket)|搭建WebSocket服务器
## 项目运行效果
浏览器输入`http://ip:9099`，**ip**为设备ip地址，确保在同一网络。

![MobileControl](https://github.com/shenbengit/MobileControl/blob/master/screenshots/%E6%88%AA%E5%9B%BE.png)

触摸方向旋转0°

![MobileControl](https://github.com/shenbengit/MobileControl/blob/master/screenshots/%E6%89%8B%E6%9C%BA%E6%8E%A7%E5%88%B60%C2%B0.gif)

触摸方向旋转90°

![MobileControl](https://github.com/shenbengit/MobileControl/blob/master/screenshots/%E6%89%8B%E6%9C%BA%E6%8E%A7%E5%88%B690%C2%B0.gif)

触摸方向旋转180°

![MobileControl](https://github.com/shenbengit/MobileControl/blob/master/screenshots/%E6%89%8B%E6%9C%BA%E6%8E%A7%E5%88%B6180%C2%B0.gif)

触摸方向旋转270°

![MobileControl](https://github.com/shenbengit/MobileControl/blob/master/screenshots/%E6%89%8B%E6%9C%BA%E6%8E%A7%E5%88%B6270%C2%B0.gif)

查看控制台日志

![MobileControl](https://github.com/shenbengit/MobileControl/blob/master/screenshots/%E6%89%8B%E6%9C%BA%E6%8E%A7%E5%88%B6%E6%8E%A7%E5%88%B6%E5%8F%B0%E6%88%AA%E5%9B%BE.png)

## 使用流程
### minicap和minitouch
- 编译
  - 若您不想编译，则可以在[这里](https://github.com/shenbengit/MobileControl/tree/master/app/src/main/assets)直接使用已经编译好的。
  - 手动编译，具体参考[minicap](https://github.com/openstf/minicap)和[minitouch](https://github.com/openstf/minitouch)项目说明。
- 通信协议
  - adb shell 命令
  获取屏幕分辨率
  ```shell
  wm size
  ```
  获取cpu的arm架构
  ```shell
  adb shell getprop ro.product.cpu.abi
  ```
  
  - minicap
  
 
  
  - minitouch 
  
## 目前存在问题
- minicap或minitouch有时候会失效，具体问题是用`adb`初始化时无数据返回，已经添加`刷新`功能用于兼容。
- 无法上传大文件，Websocket会断，经过测试100M以内可以。
