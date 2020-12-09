#环境依赖
jdk version: 1.8  
maven version: 3.5.0
mysql version: 5.6  
redis  version: 5.0  
tomcat  version: 8.5  
elasticsearch version: 7.6.0  
rabbitmq  version: 3.8.9 

#部署步骤
无


#目录结构描述
├── imitatejd_parent                   // 所有模块的父类，版本管理   
├── imitatejd_api                      // api模块  
│   ├── bean                          // 存放映射类  
│   ├── service                       // dubbo服务接口  
├── imitatejd_common                   // 工具模块 声明依赖  
├── imitatejd_common_controller        // 工具模块 声明依赖  
├── imitatejd_common_service           // 工具模块 声明依赖  
├── imitatejd_item_controller          // 商品父类，销售属性，商品属性  
├── imitatejd_item_service  
├── imitatejd_passport_controller      // 用户登录，注册，密码校验  
├── imitatejd_passport_service   
├── imitatejd_search_controller        // 商品搜索  
├── imitatejd_search_service             
├── imitatejd_cart_controller          // 购物车    
├── imitatejd_cart_service  
├── imitatejd_order_controller         // 用户订单支付  
└── imitatejd_order_service
         
     