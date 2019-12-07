import Vue from 'vue';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';    // 默认主题
import 'font-awesome/css/font-awesome.css';
import App from './App';
import router from './router';
import store from './store/store.js' //全局中引入vuex
import axios from 'axios';

import {Message} from 'element-ui'
// import '../static/css/theme-green/index.css';       // 浅绿色主题
import '../static/css/icon.css';
import "babel-polyfill";
import qs from 'qs';
import {is_has} from '../static/js/common.js';
Vue.prototype.$is_has = is_has;
/*import * as filters from '../static//filter'
Object.keys(filters).forEach(key => {
    Vue.filter(key, filters[key])
})*/
//全局过滤方法
Vue.prototype.tableFormatter = function(val,format,obj){
    return format(val,obj)
}

Vue.prototype.$qs = qs;
Vue.use(ElementUI, { size: 'small' });

axios.defaults.withCredentials = true;
Vue.prototype.$axios = axios;
//自动切换环境 设置路径
if (process.env.NODE_ENV === 'development'){
    //axios.defaults.baseURL = '/jz';
    Vue.prototype.$baseUrl = '/jz';
} else if (process.env.NODE_ENV === 'production') {
    //axios.defaults.baseURL = '../';
    Vue.prototype.$baseUrl = '../..';
}
axios.defaults.timeout = 30000;
//axios拦截器
axios.interceptors.request.use(config => {
    return config;
}, err => {
    layer.msg('请求超时！', {icon: 5});
    //Message.error({message:'请求超时！'});
    return Promise.resolve(err);
})
axios.interceptors.response.use(data => {
    if (data.status && data.status == 200 && data.data.status == 'error'){
        layer.msg(data.data.msg, {icon: 5});
        //Message.error({message:data.data.msg});
        return;
    }
    return data;
},err => {
    if (err.response.status == 504 ){
        layer.msg('连接超时', {icon: 5});
    }else if(err.response.status == 500){
        layer.msg('服务器异常', {icon: 5});
    }else if( err.response.status == 404){
        layer.msg('请求地址出错', {icon: 5});
    } else if (err.response.status == 403) {
        layer.msg('权限不足,请联系管理员!', {icon: 5});
       // Message.error({message: '权限不足,请联系管理员!'});
    }else{
        layer.msg('未知错误', {icon: 5});
       // Message.error({message: '未知错误!'});
    }
    return Promise.resolve(err);
})


//使用钩子函数对路由进行权限跳转
router.beforeEach((to, from, next) => {
   /* const role = localStorage.getItem('ms_username');
    if(!role && to.path !== '/login'){
        next('/login');
    }else if(to.meta.permission){
        // 如果是管理员权限则可进入，这里只是简单的模拟管理员权限而已
        role === 'admin' ? next() : next('/403');
    }else{
        // 简单的判断IE10及以下不进入富文本编辑器，该组件不兼容
        if(navigator.userAgent.indexOf('MSIE') > -1 && to.path === '/editor'){
            Vue.prototype.$alert('vue-quill-editor组件不兼容IE10及以下浏览器，请使用更高版本的浏览器查看', '浏览器不兼容通知', {
                confirmButtonText: '确定'
            });
        }else{
            next();
        }
    }*/
   //判断是否是登录/注册页面的路由跳转  是-不进行拦截   不是-验证登录是否过期
   if(to.path !== '/login' && to.path !== '/resigter'){
       let loading = layer.load(1)
       axios.get(Vue.prototype.$baseUrl+'/user/checkLogin.do',{})
           .then((res)=>{
               layer.close(loading);
               if (res.data.success === "false"){ //登录过期
                   layer.open({
                       content: '您的登陆信息已经超时，请重新登陆！',
                       closeBtn: 0, //取消关闭按钮
                       yes: (index, layero)=>{
                           layer.close(index);
                           next('/login');
                       }
                   })
               }else{ //登录不过期
                   //获取权限btn
                   axios.get(Vue.prototype.$baseUrl+'/menu/selectButtonListByUser.do',{})
                       .then((res)=>{
                           let arr = [];
                           for (let i in res.data){
                               arr.push(res.data[i].buttonID);
                           }
                           sessionStorage.setItem('btnArr',JSON.stringify(arr));
                       })
                       .catch(err=>{
                           console.log(err)
                       })
                   //获取本地存储的路由
                   let newRouterVal = JSON.parse(sessionStorage.getItem(to.path));
                   //删除本地存储的路由，防止进入死循环
                   sessionStorage.removeItem(to.path);
                   //console.log(to.path)
                   //判断是否是刷新页面
                   if(newRouterVal !== null){//是
                       let newRouters = [{
                           path:'/',
                           component: resolve => require(['@/components/common/Home.vue'], resolve),
                           meta: { title: '自述文件' },
                           children:[
                               {
                                   path:'/'+ newRouterVal.path,
                                   name:newRouterVal.path,
                                   component: resolve => require(['@/components/'+ newRouterVal.component], resolve),
                                   meta: { title: newRouterVal.title }
                               }
                           ]
                       }]
                       router.addRoutes(newRouters);
                       next({...to});


                   }else {
                       next()
                   }
                    //next();

               }
           })
           .catch((err)=>{
               layer.close(loading);
           })
   }else{
       next();
   }
})
router.onReady(()=>{

})

new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app');
