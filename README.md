# V1.0 登录界面需求
1.登录分为商家，管理员 √
2.注册只有商家  √
3.存在一个系统管理员负责添加管理员（系统内实现）
4.密码显示
5.正则判断：大小写，非法字符，数字，英文字母，位数
待定：
1.验证码

# 日志
1.  修改了Vue下router.js文件中/home的指向
    name: 'home',  // 修改这里，将名称改为 'home'
以保证导航栏的首页按钮正常指向

2.  UserController.java文件下额外添加33、34行：
    response.put("userid",user.getUserId()); //返回id
    response.put("role", user.getRole());//返回用户角色（admin/merchant）
以此可以在界面右上角中显示id

3.  添加了 AdminNavbar.vue 用于管理员的导航栏
    修改了 Navbar.vue 变为了 MerchantNavbar.vue 用于商户导航栏
    修改了 MerchantNavbar.vue 下的29行：
        name: 'AppMerchantNavbar', // 组件名称

4.  修改了Home.vue代码，实现： 
    根据role不同，加载不同的导航栏