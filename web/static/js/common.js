/*
*将图片以PDF格式导出 方法 downloadToPDF(PDFtitle,imgBase64Arr)
* 参数imgBase64Arr:需要导出的图片集合  注：图片格式必须为Base64格式
* 参数imgTop:图片之间的间隔（上下）
* 参数imgWidth：原始图片的宽度 可以是数组 但是长度必须跟imgBase64Arr长度相同
* 参数imgHeight：原始图片的高度 可以是数组 但是长度必须跟imgBase64Arr长度相同
* 说明：	1.--此方法依赖于jsPDF.js插件 使用时应先引入此插件--
* 		2.导出的pdf纸张为A4纸大小
* 		3.图片会根据原始大小在A4纸上进行缩放
*/
import router from "../../src/router";
import axios from 'axios';

function downloadToPDF(imgBase64Arr, imgTop, imgWidth, imgHeight) {
    //存储imgtop值 用于多页的初始值
    var oldImgtop = imgTop;
    //初始化
    var pdf = new jsPDF('', 'pt', 'a4');
    //循环添加图片
    for (var i = 0;i<imgBase64Arr.length;i++){
        //判断类型
        if(imgWidth.constructor == Array){
            var contentWidth = imgWidth[i];
        }else{
            var contentWidth = imgWidth;
        }
        if(imgHeight.constructor == Array){
            var contentHeight = imgHeight[i];
        }else{
            var contentHeight = imgHeight;
        }

        //a4纸的尺寸[595.28,841.89]，html页面生成的canvas在pdf中图片的宽高
        var imgWidth = 595.28;
        var imgHeight = 592.28/contentWidth * contentHeight;
        //判断是否大于一页
        if ((imgTop+imgHeight) < 841.89) {
            //添加
            pdf.addImage(imgBase64Arr[i], 'JPEG', 0, imgTop, imgWidth, imgHeight );

        } else {
            //添加新的一页
            pdf.addPage();
            //重新定义距顶端偏移量
            imgTop = oldImgtop;
            //添加
            pdf.addImage(imgBase64Arr[i], 'JPEG', 0, imgTop, imgWidth, imgHeight)

        }
        //距顶端偏移量  += 图片高度
        imgTop =imgTop+ oldImgtop+imgHeight;
    }
    //下载
    pdf.save('content.pdf');
}
/* 密码强度检验  */
function checkStrong(sValue) {
    var modes = 0;
    //正则表达式验证符合要求的
    if (sValue.length < 1) return modes;
    if (/\d/.test(sValue)) modes++; //数字
    if (/[a-z]/.test(sValue)) modes++; //小写
    if (/[A-Z]/.test(sValue)) modes++; //大写
    if (/\W/.test(sValue)) modes++; //特殊字符

    //逻辑处理
    switch (modes) {
        case 1:
            return 1;
            break;
        case 2:
            return 2;
            break;
        case 3:
        case 4:
            return sValue.length < 12 ? 3 : 4
            break;
    }
    return modes;
}
/*
****请求前缀*****
* */
let baseUrl = '/jz'

/*
****页面跳转方法*****
* 说明：将路由信息添加到路由文件中，实现动态路由
* 参数 path_name:跳转的路径以及组件的名称
* 参数 componentPath：引用的组件路径
* 参数 paramsObj：传递的参数集合 {}
* 参数 des:标签文字描述
* */
function jumpHtml(path_name,componentPath,paramsObj,des) {
    let sss = path_name.replace(/\//g,"&-")
    let newRouters = [{
        path:'/',
        component: resolve => require(['@/components/common/Home.vue'], resolve),
        meta: { title: '自述文件' },
        children:[
            {
                path:'/'+sss,
                name:path_name,
                component: resolve => require(['@/components/'+ componentPath], resolve),
                meta: { title: des }
            }
        ]
    }]
    router.addRoutes(newRouters);
    router.push({path:sss,query: paramsObj})
}
/*
****保存路由方法*****
* 说明：将动态添加的路由保存在本地，用于页面的刷新加载
* 参数 path_name:跳转的路径以及组件的名称
* 参数 componentPath：引用的组件路径
* 参数 des:标签文字描述
* */
function savePath(path_name,componentPath,des){
    //将路由存放在本地 用来刷新页面时添加路由
    let sss = path_name.replace(/\//g,"&-")
    let obj = {
        path:sss,
        component:componentPath,
        title:des
    }
    sessionStorage.setItem('/'+sss,JSON.stringify(obj))
}

/*
* 业务流分析页面
* 计算位置
* */
function Y_Coordinates(  x,  y,  k,  x0){
    return k * x0 - k * x + y;
}

function calCircleCenter( p1, p2, r ) {
    let k = 0.0,k_verticle = 0.0;
    let mid_x = 0.0,mid_y = 0.0;
    let a = 1.0;
    let b = 1.0;
    let c = 1.0;
    let center1 = new Array();
    let center2 = new Array();


    k = (p2[1] - p1[1]) / (p2[0] - p1[0]);

    if(k == 0)
    {
        center1[0] = (p1[0] + p2[0]) / 2.0;
        center2[0] = (p1[0] + p2[0]) / 2.0;
        center1[1] = p1[1] + Math.sqrt(r * r -(p1[0] - p2[0]) * (p1[0] - p2[0]) / 4.0);
        center2[1] = p2[1] - Math.sqrt(r * r -(p1[0] - p2[0]) * (p1[0] - p2[0]) / 4.0);
    }
    else
    {
        k_verticle = -1.0 / k;
        mid_x = (p1[0] + p2[0]) / 2.0;
        mid_y = (p1[1] + p2[1]) / 2.0;
        a = 1.0 + k_verticle * k_verticle;
        b = -2 * mid_x - k_verticle * k_verticle * (p1[0] + p2[0]);
        c = mid_x * mid_x + k_verticle * k_verticle * (p1[0] + p2[0]) * (p1[0] + p2[0]) / 4.0 -
            (r * r - ((mid_x - p1[0]) * (mid_x - p1[0]) + (mid_y - p1[1]) * (mid_y - p1[1])));

        center1[0] = (-1.0 * b + Math.sqrt(b * b -4 * a * c)) / (2 * a);
        center2[0] = (-1.0 * b - Math.sqrt(b * b -4 * a * c)) / (2 * a);
        center1[1] = Y_Coordinates(mid_x,mid_y,k_verticle,center1[0]);
        center2[1] = Y_Coordinates(mid_x,mid_y,k_verticle,center2[0]);
    }

    //return [center1[0],center1[1]]//
    return [center1[0],center1[1],center2[0],center2[1]]
    //console.log( center1[0] +  "    " + center1[1] );
    //console.log( center2[0] + "    " + center2[1] );

}

/*
* 拖拽改变宽度
*
*
* */
function gresizeW ($this) {
    var jq = $this;
    jq.wrapInner(' <div> </div>');//把内容用div括起来，在旁边加一div,用来触发Resize
    jq.children().eq(0).css({ height: '100%', overflow: 'auto' });
    jq.css({ position: 'relative', paddingRight: parseInt(jq.css('padding-right')) + 1 ,width:jq.width()});//留触发Resize的div宽度
    $(' <div style="width:2px; height:100%;cursor:e-resize;position:absolute;top:0;right:0;"> </div>')
        .on('mousedown', function (e) {
            document.onselectstart = function (e) { return false };//拖动鼠标时不显示选择效果
            var jqResize = $(this).parent();
            $(document).on('mousemove.gym', function (e) {
                jq.css("maxWidth","none");
                var w = jqResize.offset().left;
                if (e.pageX - w > 600){//最小留600px
                    jqResize.width(e.pageX - w);
                }
                e.stopPropagation();
                return false;
            })
                .on('mouseup.gym', function (e) {
                    $(document).off('.gym');
                    document.onselectstart = function (e) { return true };
                })
        })
        .on('mouseup', function (e) {
            $(document).off('.gym');
            document.onselectstart = function (e) { return true };
        }).appendTo(jq);
}
/*
* 控制按钮权限
* */
function is_has(id){
    let arr = JSON.parse(sessionStorage.getItem('btnArr'))
    if(arr.includes(id)){
        return true;
    }else{
        return false;
    }
}
/*
* 获取按钮权限
*
* */
/*function getBtn() {
    axios.get(Vue.prototype.$baseUrl+'/user/checkLogin.do',{})
        .then((res)=>{
            sessionStorage.setItem('btnArr',)
        })
        .catch(err=>{
            console.log(err)
        })
}*/
/*
* 获得当前日期 并返回固定格式 yyyy-mm-dd hh:mm:ss
* */
function getCurrentDate(fmt) {
    axios.get()
}
/*
* 时间格式化
* fmt - yyyy-mm-dd HH:MM:SS
* date - 时间
* */
function dateFormat(fmt, date) {
    let ret;
    let opt = {
        "y+": date.getFullYear().toString(),        // 年
        "m+": (date.getMonth() + 1).toString(),     // 月
        "d+": date.getDate().toString(),            // 日
        "H+": date.getHours().toString(),           // 时
        "M+": date.getMinutes().toString(),         // 分
        "S+": date.getSeconds().toString()          // 秒
        // 有其他格式化字符需求可以继续添加，必须转化成字符串
    };
    for (let k in opt) {
        ret = new RegExp("(" + k + ")").exec(fmt);
        if (ret) {
            fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
        };
    };
    return fmt;
}

export {
    downloadToPDF,
    checkStrong,
    jumpHtml,
    savePath,
    calCircleCenter,
    gresizeW,
    is_has,
    dateFormat,
    baseUrl
}
