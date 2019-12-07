<template>
    <div class="wapper">
        <el-row :gutter="20" class="wapper-top">
            <el-col :span="7">
                <div class="grid-content bg-purple wapper-content count-wapper" >
                    <p class="content-title"></p>
                    <div class="content-infom">
                        <div class="current-date">
                            <div id="date">{{ date }}</div>
                            <ul>
                                <li id="hours"> {{ hour }}</li>
                                <li class="point">:</li>
                                <li id="min">{{ min }} </li>
                                <li class="point">:</li>
                                <li id="sec"> {{ sec }}</li>
                            </ul>
                        </div>
                        <div class="current-number">
                            <div class="num-left">
                                <p class="all-totle-wapper">
                                    <span>日志数：</span>
                                    <b> {{allLogsTotle}} </b>
                                </p>
                                <p class="error-totle-wapper">
                                    <span>ERROR日志数：</span>
                                    <b class="errorLogsCounts" title="点击查看错误日志" @click="goToSearchLogs()"> {{errorLogsTotle}} </b>
                                </p>
                            </div>
                            <div class="num-right">
                                <p class="flow-totle-wapper">
                                    <span>流量数：</span>
                                    <b> {{flowTotle}} </b>
                                </p>
                            </div>
                            <!--<p class="all-totle-wapper">
                                <span>日志数：</span>
                                <b> {{allLogsTotle}} </b>
                            </p>
                            <p class="error-totle-wapper">
                                <span>ERROR日志数：</span>
                                <b class="errorLogsCounts" title="点击查看错误日志" @click="goToSearchLogs()"> {{errorLogsTotle}} </b>
                            </p>
                            <p class="flow-totle-wapper">
                                <span>流量数：</span>
                                <b> {{flowTotle}} </b>
                            </p>-->
                        </div>
                    </div>
                </div>
            </el-col>
            <el-col :span="10">
                <div class="grid-content bg-purple wapper-content">
                    <p class="content-title">
                        <el-date-picker
                            v-model="dateVal1"
                            type="daterange"
                            align="right"
                            size="mini"
                            value-format="yyyy-MM-dd"
                            range-separator="-"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            @change="changeDate1"
                            :picker-options="pickerOptions">
                        </el-date-picker>
                    </p>
                    <div class="content-infom">
                        <v-echarts echartType="bar" :echartData = "this.echartData.barData" @callbackFucnc="clickss"></v-echarts>
                    </div>
                </div>
            </el-col>
            <el-col :span="7">
                <div class="grid-content bg-purple wapper-content">
                    <p class="content-title">
                        <el-date-picker
                            v-model="dateVal2"
                            type="daterange"
                            align="right"
                            size="mini"
                            value-format="yyyy-MM-dd"
                            range-separator="-"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            @change="changeDate2"
                            :picker-options="pickerOptions">
                        </el-date-picker>
                    </p>
                    <div class="content-infom">
                        <v-echarts echartType="pie" :echartData = "this.echartData.pieData"></v-echarts>
                    </div>
                </div>
            </el-col>


        </el-row>
        <el-row :gutter="20" class="wapper-bottom">
            <el-col :span="10">
                <div class="grid-content bg-purple wapper-content"  style="height: 400px;">
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
            <el-col :span="14" style="height: 400px;">
                <div class="grid-content bg-purple wapper-content" style="height: 400px;">
                    <p class="content-title">{{todayDate }} 数据量统计</p>
                    <div class="content-infom"  style="height: 350px;">
                        <div class="content-infom"  style="height: 350px;">
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
                starttime:'',
                todayDate:'',
                pickerOptions: {
                    shortcuts: [{
                        text: '最近一周',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '最近一个月',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '最近三个月',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '全部',
                        onClick(picker) {
                            picker.$emit('pick', ['', '']);
                        }
                    }]
                },
                dateVal1:[],//
                dateVal2:[],
                allLogsTotle:0,//总条数
                errorLogsTotle:0,//错误日志条数
                flowTotle:0,
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
            //填充时间
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            this.dateVal1=[start,end];
            this.dateVal2=[start,end];
            //获取当前日期
            //拼接昨天日期格式 yy-mm-dd
            var mon = end.getMonth()+1;
            var da = end.getDate();
            if( mon < 10){
                mon = "0"+(end.getMonth()+1);
            }
            if(da < 10){
                da = "0"+ (end.getDate());
            }
            this.todayDate = end.getFullYear()+"-" + mon + "-" + da;
            //定义七天前时间
            var date2 = new Date(end);
            date2.setDate(end.getDate()-7);
            this.starttime = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
            //循环创建时间 模拟时间
            setInterval(this.setDate,1000);
            //获取日志条数方法
            this.getLogsTotle();
            //获取柱状图
            this.getBarPieEchartData(this.starttime,this.todayDate,true,true);
            //获取折线图数据  轮训 1min
            this.getLineEchartData();
            //setInterval(this.getLineEchartData,60000);
            //获取日志数据
            this.getErrorLogsData();
            //获取流量条数
            this.getFlowTotle();
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
            //日期改变
            changeDate1(){
                this.getBarPieEchartData(this.dateVal1[0],this.dateVal1[1],true,false);
            },
            //日期改变
            changeDate2(){
                this.getBarPieEchartData(this.dateVal2[0],this.dateVal2[1],false,true);
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
                })
            },
            //获取流量条数
            getFlowTotle(){
                this.$nextTick( ()=> {
                    this.$axios.get(this.$baseUrl+'/log/getIndicesCountByType.do',{})
                        .then((res) => {
                            this.flowTotle = parseInt(res.data[0].indices_defaultpacket).toLocaleString();
                        })
                        .catch((err) => {
                            console.log(err)
                        })
                })
            },
            //获取柱状图 饼图 数据
            getBarPieEchartData(starttime,endtime,bar,pie){
                this.$nextTick( ()=> {
                    this.$axios.get(this.$baseUrl+'/log/getCountGroupByParam.do',{
                        params:{
                            index:'estest',
                            type:'',
                            param:'operation_level',
                            starttime:starttime,
                            endtime:endtime
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
                            if (bar){
                                this.echartData.barData.xAxisArr = xVal;
                                this.echartData.barData.yAxisArr = yVal;
                            }
                            if(pie){
                                this.echartData.pieData.yAxisArr = pieVAl;
                            }

                        })
                        .catch((err) => {
                            console.log(err)
                        })
                })
            },
            //获取折线图数据
            getLineEchartData(){

                //this.echartData.lineData.baseConfig.title = this.todayDate + ' 数据量统计'

                this.$nextTick( ()=> {
                    this.$axios.get(this.$baseUrl+'/log/getCountGroupByTime.do',{
                        params:{
                            equipmentid:'',
                            index:'estest',
                            type:'',
                            param:this.todayDate
                        }
                    })
                        .then((res) => {
                            /*const obj = res.data[0];
                            const yVal = [];
                            for(let i = 0;i<24;i++){
                                if(i < 10){
                                    i = "0"+i;
                                }
                                yVal.push(obj[i]);
                            }*/
                            let yVal = [];
                            for(let i in res.data){
                                yVal.push(res.data[i].count)
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
        background: rgba(26,36,47,1);
       /* background: url("../../../static/img/border3.png");
        background-repeat: no-repeat;
        background-size: 100% 100%;
        background-color: rgba(35, 142, 222, 0.1);*/
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
    .content-title .el-input__inner{
        background: 0;
        border: 0;
        border-bottom: 1px solid #e4956d;
    }
    .content-title .el-range-editor--mini .el-range-input, .el-range-editor--small .el-range-input{
        color: #e4956d!important;
    }
    .content-infom{
        height: 280px;
        overflow: hidden;
    }
    /* 统计模块*/
    .current-date{
        height: 130px;
    }
    #date{
        /*font-family:'BebasNeueRegular', Arial, Helvetica, sans-serif;*/
        font-size:35px;
        text-align:center;
        text-shadow:0 0 5px #00c6ff;
        margin-bottom: 10px;
        font-family: 'electronicFont';
        background-image: linear-gradient(to right, #5bc0de 40%, #75EF99 73%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
    }
    .current-date ul{
        width: 100%;
        padding: 0px;
        list-style: none;
        text-align: center;
        font-family: 'electronicFont';
        background-image: linear-gradient(to right, #5bc0de 40%, #75EF99 73%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
    }
    .current-date ul li{
        display:inline;
        /* font-size:5em;  */
        text-align:center;
       /* font-family:'BebasNeueRegular', Arial, Helvetica, sans-serif;
        text-shadow:0 0 5px #00c6ff;*/
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
        .current-number p b{
            font-size: 1rem;
        }
    }
    .point {
        /*position:relative;*/
        /*-moz-animation:mymove 1s ease infinite;*/
        /*-webkit-animation:mymove 1s ease infinite;*/
        padding-left:10px;
        padding-right:10px;
        /*-webkit-animation: mymove 1s ease infinite;*/
        /*-o-animation: mymove 1s ease infinite;*/
        /*animation: mymove 1s ease infinite;*/
    }
    @-webkit-keyframes mymove
    {
        0% {opacity:1.0;}
        50% {opacity:0;}
        100% {opacity:1.0; }
    }
    @-moz-keyframes mymove
    {
        0% {opacity:1.0;}
        50% {opacity:0;}
        100% {opacity:1.0;  }
    }
    .current-number{
        /*background: #303e4e;*/
        /*display: flex;
        justify-content: space-around;
        align-items: center;
        flex-wrap: wrap;*/
        height: 150px;
        display: flex;
    }
    .current-number>div{
        width: 50%;
        height: 100%;
        /*padding: 10px 0;*/
        background: #273646;
        margin:0 10px;
    }
    .current-number p{
        /*margin-bottom: 10px;*/
        height: 50%;
        line-height: 50px;
        /*text-align: center;*/
    }
    .current-number p span{
        display: block;
        height: 25px;
        line-height: 25px;
        padding-left: 10px;
        font-size: 14px;
        color: #48cac9;
        font-weight: 600;
        /*margin-right: 15px;*/
    }
    .current-number p b{
        /*display: block;*/
        text-align: center;
        font-family: 'electronicFont';
        font-size: 1.8rem;
        height: calc(100% - 25px);
        display: flex;
        justify-content: center;
        align-items: center;

    }
    .num-right p{
        height: 100%;
    }
    .all-totle-wapper b{
        color: #5bc0de;
    }
    .error-totle-wapper b{
        color: #e4956d;
        cursor: pointer;
    }
    .error-totle-wapper b:hover{
        text-decoration: underline;
    }
    .flow-totle-wapper b{
        color: #185bff;
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
        height:36px;
        padding: 10px;
        overflow: hidden;
        line-height: 24px;
        font-size: 14px;
        color: #fafafa;
    }
    .index-list-time{
        color: #e4956d;
    }

</style>
