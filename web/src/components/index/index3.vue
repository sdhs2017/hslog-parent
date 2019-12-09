<template>
    <div class="wapper">
        <el-row :gutter="20" class="wapper-top">
            <el-col :span="7">
                <div class="grid-content bg-purple wapper-content">
                    <p class="content-title">统计</p>
                    <div class="content-infom">
                        <div class="current-date">
                            <div id="date">{{ date }}</div>
                            <ul>
                                <li id="hours"> {{ hour }}</li>
                                <li id="point">:</li>
                                <li id="min">{{ min }} </li>
                                <li id="point">:</li>
                                <li id="sec"> {{ sec }}</li>
                            </ul>
                        </div>
                        <div class="current-number">
                            <div class="totle-logs-all">
                                <p>已收集的日志总条数</p>
                                <p>
                                    <b> {{allLogsTotle}} </b>
                                </p>
                            </div>
                            <div class="totle-logs-error">
                                <p>ERROR日志条数</p>
                                <p>
                                    <!--<router-link :to="{path:'/equipment',query: {name: 1},meta:{ title: 'error' }}"> <b> {{errorLogsTotle}} </b></router-link>-->
                                    <b class="errorLogsCounts" title="点击查看错误日志" @click="goToSearchLogs()"> {{errorLogsTotle}} </b>
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </el-col>
            <el-col :span="10">
                <div class="grid-content bg-purple wapper-content">
                    <p class="content-title">柱状图</p>
                    <div class="content-infom">
                        <v-echarts echartType="bar" :echartData = "this.echartData.barData" @callbackFucnc="clickss"></v-echarts>
                    </div>
                </div>
            </el-col>
            <el-col :span="7">
                <div class="grid-content bg-purple wapper-content">
                    <p class="content-title">饼图</p>
                    <div class="content-infom">
                        <v-echarts echartType="pie" :echartData = "this.echartData.pieData"></v-echarts>
                    </div>
                </div>
            </el-col>
        </el-row>
        <el-row :gutter="20" class="wapper-bottom">
            <el-col :span="10">
                <div class="grid-content bg-purple wapper-content" style="height: 400px;">
                    <p class="content-title">日志检索</p>
                    <div class="content-infom" style="height: 350px;">
                        <ul class="errorLogsList" ref="errorLogsList" :style="{top}" @mouseenter="stopLogsInterval()" @mouseleave="startLogsInterval()">
                            <li class="error-logs-item" v-for="(item,index) in errorLogsData" :key="index" title="点击查看详情" @click="errorLogsClick(item)">
                                <p class="index-list-top">
                                    <span class="index-log-level">#{{item.operation_level}}#</span>
                                    <span class="index-log-ip">#{{item.ip}}#</span>
                                    <span class="index-log-eqname">#{{item.equipmentname}}#</span>
                                </p>
                                <p class="index-list-content">{{item.operation_des}}</p>
                                <p class="index-list-time">
                                    <small><i class="el-icon-time"></i> {{item.logtime}}</small>
                                </p>
                            </li>
                        </ul>
                    </div>
                </div>
            </el-col>
            <el-col :span="14">
                <div class="grid-content bg-purple wapper-content" style="height: 400px;">
                    <p class="content-title">折线图</p>
                    <div class="content-infom" style="height: 350px;">
                        <div class="content-infom" style="height: 350px;">
                            <v-echarts echartType="line" :echartData = "this.echartData.lineData"></v-echarts>
                        </div>
                    </div>
                </div>
            </el-col>
        </el-row>
        <div v-if="layerState">
            <vListdetails :baseConfig="this.baseConfig" :detailsData="this.detailData"></vListdetails>
        </div>

    </div>
</template>

<script>
    import vEcharts from '../common/echarts'
    import vListdetails from '../common/Listdetails'
    import bus from '../common/bus';

    const MONTHNAME = [ "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" ];
    const DAYNAME = ["星期日","星期一","星期二","星期三","星期四","星期五","星期六"];
    const HOUR = ["00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"];

    export default {
        name: "index",
        data() {
            return{
                date:'',//日期
                hour:'',//时
                min:'',//分
                sec:'',//秒
                allLogsTotle:0,//总条数
                errorLogsTotle:0,//错误日志条数
                echartData:{
                    barData:{//柱状图数据
                        baseConfig:{
                            title:'日志级别数量统计',
                            xAxisName:'级别',
                            yAxisName:'数量/条',
                            hoverText:'数量'
                        },
                        xAxisArr:[],
                        yAxisArr:[]
                    },
                    pieData:{//饼状图数据
                        baseConfig:{
                            title:'日志级别数量统计',
                            hoverText:'百分比'
                        },
                        xAxisArr:[],
                        yAxisArr:[]
                    },
                    lineData:{//折线图数据
                        baseConfig:{
                            title:'',
                            xAxisName:'时间/时',
                            yAxisName:'数量/条',
                            hoverText:'日志数量'
                        },
                        xAxisArr:[],
                        yAxisArr:[]
                    }
                },
                errorLogsData:{},//错误日志列表数据
                interval:'',//定时器
                activeIndex:0,//当前日志列表索引，用于日志滚动计数
                baseConfig:{
                    type:'2',
                    title:'日志详情',
                    areaWidth:'620px',//宽度
                    areaHeight:'520px'//高度
                },
                detailData:{},//弹窗数据
                layerState:false//弹窗状态
            }
        },
        created() {
            //循环创建时间 模拟时间
            setInterval(this.setDate,1000);
            //获取日志条数方法
            this.getLogsTotle();
            //获取柱状图
            this.getBarPieEchartData();
            //获取折线图数据  轮训 1min
            this.getLineEchartData();
            //setInterval(this.getLineEchartData,60000);
            //获取日志数据
            this.getErrorLogsData();
        },
        mounted(){
        },
        watch:{
            layerState(val){
                if(val){
                    //关闭日志列表滚动
                    this.stopLogsInterval();
                }else{
                    //启动日志列表滚动
                    this.startLogsInterval();

                }
            }
        },
        methods:{
            clickss(params){
                /*   //this.$router.push({ path: '/searchLogs', query: { type: params.name }})
                   let newRouters = [{
                       path:'/',
                       component: resolve => require(['../common/Home.vue'], resolve),
                       meta: { title: '自述文件' },
                       children:[
                           {
                               path: '/searchLogs/'+params.name,
                               name:'searchLogs/'+params.name,
                               component: resolve => require(['../logsManage/searchLogs.vue'], resolve),
                               meta: { title: '资产管理' }
                           }
                       ]
                   }]
                   this.$router.addRoutes(newRouters)
                   this.$router.push({ path: '/searchLogs', query: { type: params.name }})*/
                this.$router.push({name:'searchLogs',params:{words: params.name}})
            },
            //跳转页面 path:'/equipment',query: {name: 1}
            goToSearchLogs(){
                this.$router.push({name:'searchLogs',params:{words: 'error'}})
            },
            //创建时间
            setDate(){
                // 创建新的日期函数
                const newDate = new Date();
                // 设置日期
                newDate.setDate(newDate.getDate());
                this.date = newDate.getFullYear() + "/" + MONTHNAME[newDate.getMonth()] + '/' +  newDate.getDate() + ' ' +DAYNAME[newDate.getDay()];
                //设置小时
                const hours = new Date().getHours();
                this.hour = ( hours < 10 ? "0" : "" ) + hours;
                // 设置分钟
                const minutes = new Date().getMinutes();
                this.min = ( minutes < 10 ? "0" : "" ) + minutes;
                // 设置秒
                const seconds = new Date().getSeconds();
                this.sec =( seconds < 10 ? "0" : "" ) + seconds;
            },
            //获取日志条
            getLogsTotle(){
                this.$nextTick( ()=> {
                    this.$axios.get(this.$baseUrl+'/log/getIndicesCount.do',{})
                        .then((res) => {
                            this.allLogsTotle = parseInt(res.data[0].indices).toLocaleString();
                            this.errorLogsTotle = parseInt(res.data[0].indiceserror).toLocaleString();
                        })
                        .catch((err) => {
                            console.log(err)
                        })
                    /*this.postRequest('/log/getIndicesCount.do',{})
                        .then((res) => {
                            this.allLogsTotle = parseInt(res.data[0].indices).toLocaleString();
                            this.errorLogsTotle = parseInt(res.data[0].indiceserror).toLocaleString();
                        })
                        .catch((err) => {
                            console.log(err)
                        })*/
                })
            },
            //获取柱状图 饼图 数据
            getBarPieEchartData(){
                this.$nextTick( ()=> {
                    this.$axios.get(this.$baseUrl+'/log/getCountGroupByParam.do',{
                        params:{
                            index:'estest',
                            type:'',
                            param:'operation_level',
                            starttime: '2019-11-7',
                            endtime: '2019-11-13'
                        }
                    })
                        .then((res) => {
                            const obj = res.data[0];
                            const xVal = [];//x轴数据
                            const yVal = [];//y轴数据
                            const pieVAl = [];//饼图数据
                            for(const i in obj){
                                const pieObj = {};
                                xVal.push(i);
                                yVal.push(obj[i])
                                pieObj.value = obj[i];
                                pieObj.name = i;
                                pieVAl.push(pieObj)
                            }
                            //赋值
                            this.echartData.barData.xAxisArr = xVal;
                            this.echartData.barData.yAxisArr = yVal;
                            this.echartData.pieData.yAxisArr = pieVAl;
                        })
                        .catch((err) => {
                            console.log(err)
                        })
                })
            },
            //获取折线图数据
            getLineEchartData(){
                //获取当前日期
                const day1 = new Date();
                //昨天日期
                //day1.setTime(day1.getTime()-24*60*60*1000);
                //拼接昨天日期格式 yy-mm-dd
                var mon = day1.getMonth()+1;
                var da = day1.getDate();
                if( mon < 10){
                    mon = "0"+(day1.getMonth()+1);
                }
                if(da < 10){
                    da = "0"+ (day1.getDate());
                }
                var s1 = day1.getFullYear()+"-" + mon + "-" + da;
                this.echartData.lineData.baseConfig.title = s1 + ' 数据量统计'

                this.$nextTick( ()=> {
                    this.$axios.get(this.$baseUrl+'/log/getCountGroupByTime.do',{
                        params:{
                            equipmentid:'',
                            index:'estest',
                            type:'',
                            param:s1
                        }
                    })
                        .then((res) => {
                            const obj = res.data[0];
                            const yVal = [];
                            for(let i = 0;i<24;i++){
                                if(i < 10){
                                    i = "0"+i;
                                }
                                yVal.push(obj[i]);
                            }
                            //赋值
                            this.echartData.lineData.xAxisArr = HOUR;
                            this.echartData.lineData.yAxisArr = yVal;
                        })
                        .catch((err) => {
                            console.log(err)
                        })
                })

            },
            //获取错误日志数据
            getErrorLogsData(){
                //发送请求
                this.$nextTick( ()=>{
                    this.$axios.get(this.$baseUrl+'/log/getLogListByLevel.do',{
                        params:{
                            words:'error'
                        }
                    })
                        .then((res)=>{
                            //取数据的前30条
                            this.errorLogsData = res.data.slice(0,30);
                            //启动日志列表滚动
                            this.startLogsInterval();
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            },
            //错误日志列表点击事件
            errorLogsClick(item){
                //传递参数
                this.detailData = item;
                //出现弹窗
                this.layerState = true;
                //绑定关闭函数方法
                //this.closeLayer();
                //监听
                bus.$on('closeLayer',(params)=>{
                    //更改弹窗状态
                    this.layerState = false;
                })
            },
            //错误日志滚动函数
            errorLogsListScroll(){
                //判断当前显示的列表个数是否还剩3个
                if(this.activeIndex < this.errorLogsData.length-3) {
                    this.activeIndex +=1;
                } else {
                    //停止滚动
                    this.stopLogsInterval();
                    //将高度置为0；
                    this.activeIndex = 0;
                    //重新获取数据
                    this.getErrorLogsData();
                }
            },
            //停止计时器（日志列表）
            stopLogsInterval(){
                clearInterval(this.interval);
            },
            //启动计时器（日志列表）
            startLogsInterval(){
                //判断是否有详情弹窗 有则不启动计时器
                if(!this.layerState){
                    this.interval = setInterval(()=>{this.errorLogsListScroll()}, 4000);
                }

            }/*,
            //弹窗关闭回调函数
            closeLayer(){
                $('body').on('click','.layui-layer-close1',()=>{
                    //更改弹窗状态
                    this.layerState = false;
                })
            }*/
        },
        computed: {
            //计算日志列表向上偏移量
            top() {
                return -this.activeIndex * 112.66 + 'px';
            }
        },
        components:{
            vEcharts,
            vListdetails
        }
    }
</script>

<style scoped>
    .wapper{
        /*height: 100%;*/
        /*background: url("../../../static/img/bigdata3.png");*/

    }
    .wapper-top{
        margin-bottom: 20px;
    }
    .wapper .wapper-content{
        height: 38vh;
        min-height: 350px;
        /*background-image: url("../../../static/img/border.png");*/
        /*background-size: 100% 100%;*/
        /*background: rgba(26,36,47,0.5);*/
        /*background: url("../../../static/img/lt.png"),url("../../../static/img/rt.png"),url("../../../static/img/lb.png"),url("../../../static/img/rb.png");*/
        /*background-position: left top,right top,left bottom,right bottom;*/
        background: url("../../../static/img/border3.png");
        background-repeat: no-repeat;
        background-size: 100% 100%;
        background-color: rgba(35, 142, 222, 0.1);
        /*background-color: rgba(0,246,255,0.1);*/
        /*border: 1px solid #07578e;*/
        /*background-color: rgba(0,0,0,0.2);*/

    }
    .content-title{
        padding: 0 15px;
        height: 48px;
        line-height: 48px;
        font-weight: 600;
        border: 0;
        font-size: 16px;
        color: #e4956d;
        text-shadow: 3px 4px 3px #4e7ba9;
        /*text-align: center;
        line-height: 16px;*/
    }
    .content-infom{
        height: 280px;
        overflow: hidden;
    }
    /* 统计模块*/
    .current-date{
        height: 140px;
    }
    #date{
        font-family:'BebasNeueRegular', Arial, Helvetica, sans-serif;
        font-size:30px;
        text-align:center;
        text-shadow:0 0 5px #00c6ff;
        margin-bottom: 10px;
    }
    .current-date ul{
        width: 100%;
        padding: 0px;
        list-style: none;
        text-align: center;
    }
    .current-date ul li{
        display:inline;
        /* font-size:5em;  */
        text-align:center;
        font-family:'BebasNeueRegular', Arial, Helvetica, sans-serif;
        text-shadow:0 0 5px #00c6ff;
    }
    @media only screen and (min-width: 800px){
        .current-date ul li {
            font-size:3em;
        }
    }
    @media only screen and (min-width: 1500px){
        .current-date ul li {
            font-size:4em;
        }
    }
    #point {
        position:relative;
        -moz-animation:mymove 1s ease infinite;
        -webkit-animation:mymove 1s ease infinite;
        padding-left:10px; padding-right:10px;
    }
    @-webkit-keyframes mymove
    {
        0% {opacity:1.0; text-shadow:0 0 20px #00c6ff;}
        50% {opacity:0; text-shadow:none; }
        100% {opacity:1.0; text-shadow:0 0 20px #00c6ff; }
    }
    @-moz-keyframes mymove
    {
        0% {opacity:1.0; text-shadow:0 0 20px #00c6ff;}
        50% {opacity:0; text-shadow:none; }
        100% {opacity:1.0; text-shadow:0 0 20px #00c6ff; }
    }
    .current-number{
        /*background: #303e4e;*/
        display: flex;
        justify-content: space-around;
        align-items: center;
        height: 140px;
    }
    .current-number p{
        margin-bottom: 10px;
    }
    .totle-logs-all b{
        font-size: 1.5rem;
        /* color: #e4956d; */
        color: #2c76bd;
    }
    .totle-logs-error b{
        font-size: 1.5rem;
        /* color: #e4956d; */
        color: #e4956d;
        cursor: pointer;
    }
    .totle-logs-error b:hover{
        text-decoration: underline;
    }
    /*日志列表模块*/
    .errorLogsList{
        padding:0 10px;
        position: relative;
        transition: top 1s;
        -webkit-transition: top 1s;
    }
    .error-logs-item{
        height: 91.66px;
        cursor: pointer;
        padding: 10px;
        border-bottom: 1px solid #303e4e;
    }
    /*  .error-logs-item:nth-child(odd){
          background: #303e4e;
      }*/
    .error-logs-item:hover{
        background: #303e4e;
    }
    .index-list-top{
        color:#2c76bd;
        font-weight: 600;
    }
    .index-log-level{
        margin-right: 10px;
    }
    .index-log-eqname{
        float: right;
    }
    .index-list-content{
        height:40px;
        padding: 10px;
        overflow: hidden;
        line-height: 24px;
    }
    .index-list-time{
        color: #e4956d;
    }

</style>
