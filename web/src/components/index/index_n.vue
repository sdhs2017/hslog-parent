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
<!--                                <p class="error-totle-wapper">-->
<!--                                    <span>ERROR日志数：</span>-->
<!--                                    <b class="errorLogsCounts" title="点击查看错误日志" @click="goToSearchLogs()"> {{errorLogsTotle}} </b>-->
<!--                                </p>-->
                            </div>
                            <div class="num-right">
                                <p class="flow-totle-wapper">
                                    <span>ERROR日志数：</span>
                                    <b class="errorLogsCounts" title="点击查看错误日志" @click="goToSearchLogs()"> {{errorLogsTotle}} </b>
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
                        <dateLayout class="date-wapper" busName="changeDateBar" :defaultVal="defaultValBar" ></dateLayout>
                    </p>
                    <div class="content-infom" v-loading="barloading"  element-loading-background="rgba(48, 62, 78, 0.5)">
<!--                        <logLevel_bar  class="date-wapper"  :params="barParam" :busName1="barBusName" :setIntervalObj="intervalObjBar"></logLevel_bar>-->
                        <v-echarts echartType="bar" :echartData = "echartData.barData" :busName="barBusName" ></v-echarts>
                    </div>
                </div>
            </el-col>
            <el-col :span="7">
                <div class="grid-content bg-purple wapper-content">
                    <p class="content-title" style="display: flex;justify-content: flex-end;">
                        <dateLayout  class="date-wapper"  busName="changeDatePie" :defaultVal="defaultValPie" ></dateLayout>
                    </p>
                    <div class="content-infom" v-loading="pieloading"  element-loading-background="rgba(48, 62, 78, 0.5)">
<!--                        <logLevel_pie :params="pieParam" :setIntervalObj="intervalObjPie"></logLevel_pie>-->
                        <v-echarts echartType="pie" :echartData = "this.echartData.pieData" ></v-echarts>
                    </div>
                </div>
            </el-col>


        </el-row>
        <el-row :gutter="20" class="wapper-bottom">
            <el-col :span="10">
                <div class="grid-content bg-purple wapper-content"  style="height: 400px;">
                    <p class="content-title">异常检索
                        <dateLayout style="margin-top: 9px;"  class="date-wapper"  busName="changeDateErrorLog" :defaultVal="defaultValErrorLog"></dateLayout>
                    </p>
                    <div class="content-infom" style="height: 350px;" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
                        <p v-if="errorLogsData.length == 0" style="padding: 19px;color: #7593bc;">暂无异常</p>
                        <ul v-else class="errorLogsList" ref="errorLogsList" :style="{top}" @mouseenter="stopLogsInterval()" @mouseleave="startLogsInterval()">
                            <li class="error-logs-item" v-for="(item,index) in errorLogsData" :key="index" title="点击查看详情" @click="errorLogsClick(item)">
                                <p class="index-list-top">
                                    <span class="index-log-level">#{{item.log.level}}#</span>
                                    <span class="index-log-ip">#{{item.fields.ip}}#</span>
                                    <span class="index-log-eqname">#{{item.fields.equipmentname}}#</span>
                                </p>
                                <p class="index-list-content">{{item.message}}</p>
                                <p class="index-list-time">
                                    <small><i class="el-icon-time"></i> {{item['@timestamp']}}</small>
                                </p>
                            </li>
                        </ul>
                    </div>
                </div>
            </el-col>
            <el-col :span="14" style="height: 400px;">
                <div class="grid-content bg-purple wapper-content" style="height: 400px;">
                    <p class="content-title">数据量统计
                        <dateLayout style="margin-top: 9px;"   class="date-wapper"  busName="changeDateLine" :defaultVal="defaultValLine"></dateLayout>
                    </p>
                    <div class="content-infom"  style="height: 350px;">
                        <div class="content-infom"  style="height: 350px;">
<!--                            <v-echarts echartType="line" :echartData = "this.echartData.lineData"></v-echarts>-->
                            <hourlyLogCount_line :params="lineParam" :setIntervalObj="intervalObjLine"></hourlyLogCount_line>
                        </div>
                    </div>
                </div>
            </el-col>
        </el-row>
        <div v-if="layerState">
            <vListdetails2 :baseConfig="this.baseConfig" :detailsData="this.detailData"></vListdetails2>
        </div>

    </div>
</template>

<script>
    import vEcharts from '../common/echarts_n'
    import dateLayout from '../common/dateLayout'
    import vListdetails2 from '../common/Listdetails2'
    import logLevel_bar from '../charts/log/index/logLevel_bar'
    import logLevel_pie from '../charts/log/index/logLevel_pie'
    import hourlyLogCount_line from '../charts/log/index/hourlyLogCount_line'
    import bus from '../common/bus';
    import {dateFormat,setChartParam } from "../../../static/js/common";

    const MONTHNAME = [ "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" ];
    const DAYNAME = ["星期日","星期一","星期二","星期三","星期四","星期五","星期六"];
    const HOUR = ["00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"];

    export default {
        name: "index_n",
        data() {
            return{
                barloading:false,
                pieloading:false,
                //时间控件参数 柱状图
                defaultValBar:{
                    //具体时间参数
                    lastVal:'',
                    //起始时间
                    starttime:'',
                    //结束时间
                    endtime:'',
                    //具体时间 类型状态
                    dateBlock:true,
                    //是否存在轮询框
                    isIntervalBox:false,
                    //轮询状态
                    intervalState:false,
                    //轮询数值间隔
                    intervalVal:'',
                    //轮询参数类型
                    intervalType:'',
                    //‘快速选择’功能参数类型
                    dateUnit:'hour',
                    //‘快速选择’功能参数数值
                    dateCount:'1',
                    //‘常用’ 时间值
                    commonlyVal:'',
                    //是否可以切换精确日期
                    changeState:true
                },
                //饼图
                defaultValPie:{
                    //具体时间参数
                    lastVal:'',
                    //起始时间
                    starttime:'',
                    //结束时间
                    endtime:'',
                    //具体时间 类型状态
                    dateBlock:true,
                    //是否存在轮询框
                    isIntervalBox:false,
                    //轮询状态
                    intervalState:false,
                    //轮询数值间隔
                    intervalVal:'',
                    //轮询参数类型
                    intervalType:'',
                    //‘快速选择’功能参数类型
                    dateUnit:'hour',
                    //‘快速选择’功能参数数值
                    dateCount:'1',
                    //‘常用’ 时间值
                    commonlyVal:'',
                    //是否可以切换精确日期
                    changeState:true
                },
                //折线图
                defaultValLine:{
                    //具体时间参数
                    lastVal:'',
                    //起始时间
                    starttime:'',
                    //结束时间
                    endtime:'',
                    //具体时间 类型状态
                    dateBlock:true,
                    //是否存在轮询框
                    isIntervalBox:false,
                    //轮询状态
                    intervalState:false,
                    //轮询数值间隔
                    intervalVal:'',
                    //轮询参数类型
                    intervalType:'',
                    //‘快速选择’功能参数类型
                    dateUnit:'hour',
                    //‘快速选择’功能参数数值
                    dateCount:'1',
                    //‘常用’ 时间值
                    commonlyVal:'',
                    //是否可以切换精确日期
                    changeState:true
                },
                //error log
                defaultValErrorLog:{
                    //具体时间参数
                    lastVal:'',
                    //起始时间
                    starttime:'',
                    //结束时间
                    endtime:'',
                    //具体时间 类型状态
                    dateBlock:true,
                    //是否存在轮询框
                    isIntervalBox:false,
                    //轮询状态
                    intervalState:false,
                    //轮询数值间隔
                    intervalVal:'',
                    //轮询参数类型
                    intervalType:'',
                    //‘快速选择’功能参数类型
                    dateUnit:'hour',
                    //‘快速选择’功能参数数值
                    dateCount:'1',
                    //‘常用’ 时间值
                    commonlyVal:'',
                    //是否可以切换精确日期
                    changeState:false
                },
                //日志级别柱状图参数
                barParam:{
                    intervalValue:'',
                    intervalType:'',
                    starttime:'',
                    endtime:'',
                    last:''
                },
                //轮询参数
                intervalObjBar:{
                    state:false,
                    interval:'5000'
                },
                //轮询参数
                intervalObjPie:{
                    state:false,
                    interval:'5000'
                },
                //轮询参数
                intervalObjLine:{
                    state:false,
                    interval:'5000'
                },
                //日志级别柱状图 点击name
                barBusName:{
                    clickName:'indexLogLevelClick',
                    exportName:''
                },
                //日志级别饼图图参数
                pieParam:{
                    intervalValue:'',
                    intervalType:'',
                    starttime:'',
                    endtime:'',
                    last:''
                },
                //今日每小时日志数
                lineParam:{
                    intervalValue:'',
                    intervalType:'',
                    starttime:'',
                    endtime:'',
                    last:''
                },
                //error日志时间参数
                errorLogParam:{
                    intervalValue:'',
                    intervalType:'',
                    starttime:'',
                    endtime:'',
                    last:''
                },
                echartData:{
                    barData: {//柱状图数据
                        baseConfig:{
                            title:'日志级别数量统计',
                            xAxisName:'级别',
                            yAxisName:'数量/条',
                            hoverText:'数量',
                            itemColor:[['rgba(68,47,148,0.5)','rgba(15,219,243,1)']]
                        },
                        data:{
                            dimensions:[],
                            source:[]
                        }
                    },
                    pieData: {//饼状图数据
                        baseConfig:{
                            title:'日志级别数量统计',
                            xAxisName:'级别',
                            yAxisName:'数量/条',
                            hoverText:'数量',
                            itemColor:[['rgba(68,47,148,0.5)','rgba(15,219,243,1)']]
                        },
                        data:{
                            dimensions:[],
                            source:[]
                        }
                    },
                },
                //日常检索遮罩
                loading:false,
                date:'',//日期
                hour:'',//时
                min:'',//分
                sec:'',//秒
                starttime:'',
                todayDate:'',
                dateVal1:[],//
                dateVal2:[],
                allLogsTotle:0,//总条数
                errorLogsTotle:0,//错误日志条数
                flowTotle:0,
                errorLogsData:[],//错误日志列表数据
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
            //判断是否为手机端
            var ua = navigator.userAgent;
            var ipad = ua.match(/(iPad).*OS\s([\d_]+)/),
                isIphone = !ipad && ua.match(/(iPhone\sOS)\s([\d_]+)/),
                isAndroid = ua.match(/(Android)\s+([\d.]+)/),
                isMobile = isIphone || isAndroid;
            if(isMobile) {
                //window.location="mobile/index.html"
                this.$router.push('/mobile/index_m');
            }
            //填充时间
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * this.$store.state.beforeDay);
            this.dateVal1=[start,end];
            this.dateVal2=[start,end];

            this.todayDate = dateFormat('yyyy-mm-dd',end)
            this.starttime = dateFormat('yyyy-mm-dd',start)
            //初始时间
            this.defaultValBar.endtime= dateFormat('yyyy-mm-dd HH:MM:SS',end);
            this.defaultValBar.starttime = dateFormat('yyyy-mm-dd',start) + ' 00:00:00';
            this.defaultValPie.endtime= dateFormat('yyyy-mm-dd HH:MM:SS',end);
            this.defaultValPie.starttime = dateFormat('yyyy-mm-dd',start) + ' 00:00:00';
            this.defaultValLine.starttime= dateFormat('yyyy-mm-dd',end)+ ' 00:00:00';
            this.defaultValLine.endtime = dateFormat('yyyy-mm-dd HH:MM:SS',end);
            this.defaultValErrorLog.endtime= dateFormat('yyyy-mm-dd HH:MM:SS',end);
            this.defaultValErrorLog.starttime = dateFormat('yyyy-mm-dd',start) + ' 00:00:00';

            this.barParam.endtime= dateFormat('yyyy-mm-dd HH:MM:SS',end);
            this.barParam.starttime = dateFormat('yyyy-mm-dd',start) + ' 00:00:00';
            this.pieParam.endtime= dateFormat('yyyy-mm-dd HH:MM:SS',end);
            this.pieParam.starttime = dateFormat('yyyy-mm-dd',start) + ' 00:00:00';
            this.lineParam.starttime= dateFormat('yyyy-mm-dd',end)+ ' 00:00:00';
            this.lineParam.endtime = dateFormat('yyyy-mm-dd HH:MM:SS',end);
            this.errorLogParam.endtime= dateFormat('yyyy-mm-dd HH:MM:SS',end);
            this.errorLogParam.starttime = dateFormat('yyyy-mm-dd',start) + ' 00:00:00';
            /*监听日期改变*/
            bus.$on('changeDateBar',(obj)=>{
                //设置参数对应
                let arr = setChartParam(obj);
                this.barParam = arr[0];
                this.intervalObjBar = arr[1];
                this.getBarPieEchartData(this.barParam,true,false,'manual');
            })
            /*监听日期改变*/
            bus.$on('changeDatePie',(obj)=>{
                //设置参数对应
                let arr = setChartParam(obj);
                this.pieParam = arr[0];
                this.intervalObjPie = arr[1];
                this.getBarPieEchartData(this.barParam,false,true,'manual');
            })
            /*监听日期改变*/
            bus.$on('changeDateLine',(obj)=>{
                //设置参数对应
                let arr = setChartParam(obj);
                this.lineParam = arr[0];
                this.intervalObjLine = arr[1];
            })
            /*监听日期改变 */
            bus.$on('changeDateErrorLog',(obj)=>{
                //设置参数对应
                let arr = setChartParam(obj);
                this.errorLogParam = arr[0];
                this.getErrorLogsData()
            })
            //循环创建时间 模拟时间
            setInterval(this.setDate,1000);
            // //获取日志条数方法
             this.getLogsTotle();
             this.getErrorLogTotle();
            // //获取柱状图,饼图
            this.getBarPieEchartData(this.barParam,true,true,'auto');
            // //获取折线图数据  轮训 1min
            //this.getLineEchartData();
            //setInterval(this.getLineEchartData,60000);
            // //获取日志数据
            this.getErrorLogsData();
            // //获取流量条数
            // this.getFlowTotle();
            bus.$on(this.barBusName.clickName,(params)=>{
                this.$router.push({name:'accurateSearch2',params:{logLevel: params.name,dateArr:[this.barParam.starttime,this.barParam.endtime]}})
            })
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
                this.$router.push({name:'accurateSearch2',params:{logLevel: params.name,dateArr:this.dateVal1}})
            },
            //跳转页面 path:'/equipment',query: {name: 1}
            goToSearchLogs(){
                if(this.errorLogsTotle != 0){
                    this.$router.push({name:'accurateSearch2',params:{logLevel: 'error'}})
                }

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
                    this.$axios.get(this.$baseUrl+'/ecsCommon/getIndicesCount.do',{})
                        .then((res) => {
                            this.allLogsTotle = parseInt(res.data[0].indices).toLocaleString();
                        })
                        .catch((err) => {
                            console.log(err)
                        })
                })
            },
            //获取error日志数量
            getErrorLogTotle(){
                this.$nextTick( ()=> {
                    this.$axios.get(this.$baseUrl+'/ecsCommon/getIndicesCountByLevel.do',{})
                        .then((res) => {
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
                    this.$axios.get(this.$baseUrl+'/ecsWinlog/getIndicesCountByType.do',{})
                        .then((res) => {
                            this.flowTotle = parseInt(res.data[0].indices_defaultpacket).toLocaleString();
                        })
                        .catch((err) => {
                            console.log(err)
                        })
                })
            },
            //获取错误日志数据
            getErrorLogsData(){
                //发送请求
                this.loading = true;
                this.$nextTick( ()=>{
                    this.$axios.post(this.$baseUrl+'/ecsWinlog/getLogListByBlend.do',this.$qs.stringify({
                        hsData:JSON.stringify({
                            starttime:this.errorLogParam.starttime,
                            endtime:this.errorLogParam.endtime,
                            'log.level':'error',
                            page:1,
                            size:20
                        })
                    }))
                        .then((res)=>{
                            this.loading = false
                            //取数据的前30条
                            this.errorLogsData = res.data[0].list.slice(0,30);
                            if (this.errorLogsData.length < 3){
                                setTimeout(()=>{
                                    //启动日志列表滚动
                                    this.startLogsInterval();
                                },5000)
                            } else{
                                //启动日志列表滚动
                                this.startLogsInterval();
                            }

                        })
                        .catch((err)=>{
                            this.loading = false
                            console.log(err)
                        })
                })
            },
            //获取柱状图 饼图 数据
            getBarPieEchartData(params,bar,pie,refreshVal){
                if(bar){
                    this.barloading = true
                }
                if(pie){
                    this.pieloading = true
                }
                params.refresh = refreshVal
                this.$nextTick( ()=> {
                    this.$axios.post(this.$baseUrl+'/log/getCountGroupByLogLevel_barAndPie.do',this.$qs.stringify(params))
                        .then((res) => {
                            let obj = res.data;
                            if(obj.success === 'true'){
                                //赋值
                                if (bar){
                                    this.barloading = false
                                    this.echartData.barData.data = obj.data[0]
                                }
                                if(pie){
                                    this.pieloading = false
                                    this.echartData.pieData.data = obj.data[0];
                                }
                            }else{
                                //赋值
                                if (bar){
                                    this.barloading = false
                                }
                                if(pie){
                                    this.pieloading = false
                                }
                            }


                        })
                        .catch((err) => {
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
                    clearInterval(this.interval);
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
        beforeDestroy(){
            bus.$off(this.barBusName.clickName);
            bus.$off('changeDateBar');
            bus.$off('changeDatePie');
            bus.$off('changeDateLine');
        },
        components:{
            vEcharts,
            dateLayout,
            vListdetails2,
            logLevel_bar,
            logLevel_pie,
            hourlyLogCount_line
        }
    }
</script>

<style scoped>
    .wapper{
        /*height: 100%;*/
        /*background: url("../../../static/img/bigdata3.png");*/

    }
    .date-wapper{
        float: right;
        margin: 5px;
        margin-right: 0;
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
        height: 150px;
        display: flex;
    }
    .current-number>div{
        width: 50%;
        height: 100%;
        background: #273646;
        margin:0 10px;
    }
    .current-number p{
        height: 100%;
        line-height: 50px;
    }
    .current-number p span{
        display: block;
        height: 50px;
        line-height: 50px;
        padding-left: 10px;
        font-size: 14px;
        color: #48cac9;
        font-weight: 600;
    }
    .current-number p b{
        /*display: block;*/
        text-align: center;
        font-family: 'electronicFont';
        font-size: 1.8rem;
        height: calc(100% - 50px);
        display: flex;
        justify-content: center;
        align-items: center;

    }
    .current-number p .errorLogsCounts{
        color: #e4956d;
    }
    .errorLogsCounts:hover{
        cursor: pointer;
        text-decoration: underline;
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
