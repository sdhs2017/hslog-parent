<template>
    <div class="content-bg">
        <div class="top-title">
            异常检测
            <div style="float: right;margin-right: 10px;">
                <el-button @click="creatSocket('train')"  type="primary">训练</el-button>
                <el-button @click="creatSocket('detect')" type="primary">检测</el-button>
                <el-button @click="chartState = true" type="primary">查看异常检测结果</el-button>
            </div>
            
        </div>

        <div class="scroll-wapper">
            <div class="left-wapper">
                <div class="chart-wapper" ref="left1" v-loading="leftLoading1" element-loading-background="rgba(48, 62, 78, 0.5)"></div>
                <div class="chart-wapper" ref="left2" v-loading="leftLoading2" element-loading-background="rgba(48, 62, 78, 0.5)"></div>
            </div>
            <div class="right-wapper">
                <div class="right-tit">运行日志</div>
                <div class="p-wapper"  ref="Box">
                    <p v-for="(item,i) in this.list" :key="i">{{item}}</p>
                </div>

            </div>

        </div>
        <!--数据预览-->
        <el-dialog title="异常检测结果" :visible.sync="chartState" width="85vw" height="80vh" destroy-on-close v-loading="dataDetailLoading" element-loading-background="rgba(48, 62, 78, 0.5)" :close-on-click-modal="falseB">
            <div class="detail-wapper" ref="mybox"></div>
            <div class="text-wapper">
                <p><span style="background:#FE2D46;"></span>异常值极高，需重点查验</p>
                <p><span style="background:#FAA90E;"></span>异常值较高</p>
                <p><span style="background:#5bc0de;"></span>数据正常</p>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    const echarts = require('echarts');
    export default {
        name: "detectionAnomalies",
        data(){
            return{
                str:'system76@Galago-UltraPro:~$ juju add-relation memcached mediawiki system76',
                list:[],
                websocket:null,
                SH:'',
                moveState:true,
                chartState:false,
                dataDetailLoading:false,
                leftLoading2:false,
                leftLoading1:false,
                color:[],
                size:[],
                falseB:false,
            }
        },
        mounted(){
            this.$refs.Box.addEventListener('scroll',this.scrollHeight)
            this.getTrainChart();
            this.getDetectChart();
        },
        created(){

        },
        destroyed(){
            this.websocket.close();
        },
        watch:{
            'SH'(n,o){
               /* this.box = this.$refs.chatListWrapper
                var clientHeight = this.box.clientHeight
                var scrollTop = this.box.scrollTop
                var scrollHeight = this.box.scrollHeight
                if (scrollTop + clientHeight == scrollHeight) {*/
                if(n < o){
                    this.moveState = false
                }else if(n + this.$refs.Box.clientHeight  ==  this.$refs.Box.scrollHeight){
                    this.moveState = true
                }
            },
            'chartState'(){
                if(this.chartState){
                    this.$nextTick(()=>{
                        this.getChartData()
                    })

                }
            }
        },
        methods:{
            //训练 检测
            creatSocket(url){
                //判断当前浏览器是否支持WebSocket
                if ('WebSocket' in window) {
                    //websocket = new WebSocket("ws://172.16.0.252:8080/hslog/websocket?run_mode=train");
                    this.websocket = new WebSocket("ws://172.0.0.1:8080/hslog/websocket/"+url);
                } else {
                    alert('当前浏览器 Not support websocket')
                }
                //连接发生错误的回调方法
                this.websocket .onerror = function() {
                    console.log("WebSocket连接发生错误");
                };
                //连接成功建立的回调方法
                this.websocket .onopen = function() {
                    console.log("WebSocket连接成功");
                }
                if(url === 'train'){
                    this.appendP('训练开始');
                }else{
                    this.appendP('检测开始');
                }
                //接收到消息的回调方法
                this.websocket .onmessage = (event)=> {
                    // console.log(event.data)
                    this.appendP(event.data);
                }
            },
            scrollHeight(){
                this.SH =  this.$refs.Box.scrollTop;
                //console.log(this.SH)
            },
            appendP(str){
                this.list.push(str)
                this.$nextTick(()=>{
                    if(this.moveState){
                        $(".p-wapper").scrollTop($(".p-wapper")[0].scrollHeight);
                    }

                    //this.$refs.Box.scrollTop =
                })

            },
            /*训练图表*/
            getTrainChart(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/ml/getTrainData.do',this.$qs.stringify())
                        .then(res=>{
                            this.leftLoading1 = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                let myChart1 = echarts.init(this.$refs.left1);
                                this.leftLoading1 = false;
                                // 绘制图表
                                myChart1.setOption({
                                    title: {
                                        text: '训练数据',
                                        x:'center',
                                        textStyle:{
                                            color:'#5bc0de'
                                        }
                                    },
                                    tooltip: {
                                        trigger: 'axis',
                                        axisPointer: {
                                            type: 'cross',
                                            label: {
                                                backgroundColor: '#6a7985'
                                            }
                                        }
                                    },
                                    legend: {
                                        type:'scroll',
                                        pageTextStyle:{
                                            color:'#eee'
                                        },
                                        //data:legendData,
                                        width:'80%',
                                        right:"5%",
                                        top:"25px",
                                        textStyle:{
                                            color:'#eee'
                                        },
                                        pageIconColor:'#fff'
                                    },
                                    //dataset: [this.echartData.data],
                                    xAxis: {
                                        type: 'category',
                                        name:'时间',
                                        nameTextStyle:{
                                            color:'#5bc0de'
                                        },
                                        axisLine:{
                                            lineStyle:{
                                                color:'#5bc0de'
                                            }
                                        },
                                        boundaryGap: false,
                                        axisLabel:{
                                            borderColor:'#5bc0de',
                                            // interval:0,
                                            rotate:'10'
                                        },
                                        data:obj.data[0].timestamp
                                    },
                                    grid:{
                                        // show:true,
                                        // bottom:this.echartData.baseConfig.rotate ? '20%' : '60'
                                    },
                                    yAxis: {
                                        name:'日志数',
                                        nameTextStyle:{
                                            color:'#5bc0de'
                                        },
                                        splitLine:{
                                            lineStyle:{
                                                color:'#303e4e'
                                            }
                                        },
                                        axisLine:{
                                            lineStyle:{
                                                color:'#5bc0de'
                                            }
                                        },
                                        axisLabel: {
                                            margin: 2,
                                            formatter: function (value, index) {
                                                if (value >= 10000 && value < 10000000) {
                                                    value = value / 10000 + "万";
                                                } else if (value >= 10000000) {
                                                    value = value / 10000000 + "千万";
                                                }
                                                return value;
                                            }
                                        }
                                    },
                                    series: [{
                                        type: 'line',
                                        smooth: true,
                                        symbol:'none',
                                        lineStyle:{
                                            color:'#5bc0de'
                                        },
                                        data:obj.data[0].value
                                    }]
                                });
                                window.addEventListener("resize",()=>{
                                    myChart1.resize();
                                });
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.leftLoading1 = false;
                        })
                })
            },
            /*检测图表*/
            getDetectChart(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/ml/getTestData.do',this.$qs.stringify())
                        .then(res=>{
                            this.leftLoading2 = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                let myChart2 = echarts.init(this.$refs.left2);
                                this.leftLoading2 = false;
                                // 绘制图表
                                myChart2.setOption({
                                    title: {
                                        text: '待检测数据',
                                        x:'center',
                                        textStyle:{
                                            color:'#5bc0de'
                                        }
                                    },
                                    tooltip: {
                                        trigger: 'axis',
                                        axisPointer: {
                                            type: 'cross',
                                            label: {
                                                backgroundColor: '#6a7985'
                                            }
                                        }
                                    },
                                    legend: {
                                        type:'scroll',
                                        pageTextStyle:{
                                            color:'#eee'
                                        },
                                        //data:legendData,
                                        width:'80%',
                                        right:"5%",
                                        top:"25px",
                                        textStyle:{
                                            color:'#eee'
                                        },
                                        pageIconColor:'#fff'
                                    },
                                    //dataset: [this.echartData.data],
                                    xAxis: {
                                        type: 'category',
                                        name:'时间',
                                        nameTextStyle:{
                                            color:'#5bc0de'
                                        },
                                        axisLine:{
                                            lineStyle:{
                                                color:'#5bc0de'
                                            }
                                        },
                                        boundaryGap: false,
                                        axisLabel:{
                                            borderColor:'#5bc0de',
                                            // interval:0,
                                            rotate:'10'
                                        },
                                        data:obj.data[0].timestamp
                                    },
                                    grid:{
                                        // show:true,
                                        // bottom:this.echartData.baseConfig.rotate ? '20%' : '60'
                                    },
                                    yAxis: {
                                        name:'日志数',
                                        nameTextStyle:{
                                            color:'#5bc0de'
                                        },
                                        splitLine:{
                                            lineStyle:{
                                                color:'#303e4e'
                                            }
                                        },
                                        axisLine:{
                                            lineStyle:{
                                                color:'#5bc0de'
                                            }
                                        },
                                        axisLabel: {
                                            margin: 2,
                                            formatter: function (value, index) {
                                                if (value >= 10000 && value < 10000000) {
                                                    value = value / 10000 + "万";
                                                } else if (value >= 10000000) {
                                                    value = value / 10000000 + "千万";
                                                }
                                                return value;
                                            }
                                        }
                                    },
                                    series: [{
                                        type: 'line',
                                        smooth: true,
                                        lineStyle:{
                                            color:'#5bc0de'
                                        },
                                        data:obj.data[0].value
                                    }]
                                });
                                window.addEventListener("resize",()=>{
                                    myChart2.resize();
                                });
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.leftLoading2 = false;
                        })
                })
            },
            //弹窗图表
            getChartData(){
                this.$nextTick(()=>{
                    this.dataDetailLoading = true;
                    this.$axios.post(this.$baseUrl+'/ml/getDetectResult.do',this.$qs.stringify())
                        .then(res=>{

                            let obj = res.data;
                            if(obj.success == 'true'){
                                //console.log(obj)
                                let xAxis = obj.data[0].timestamp;
                                let yData = obj.data[0].real_value;
                                this.color = obj.data[0].color;
                                this.size = obj.data[0].size;

                                setTimeout(()=>{
                                    let myChart = echarts.init(this.$refs.mybox);
                                    this.dataDetailLoading = false;
                                    // 绘制图表
                                    myChart.setOption({
                                        title: {
                                            text: '',
                                            x:'center',
                                            textStyle:{
                                                color:'#5bc0de'
                                            }
                                        },
                                        tooltip: {
                                            trigger: 'axis',
                                            axisPointer: {
                                                type: 'cross',
                                                label: {
                                                    backgroundColor: '#6a7985'
                                                }
                                            }
                                        },
                                        legend: {
                                            type:'scroll',
                                            pageTextStyle:{
                                                color:'#eee'
                                            },
                                            //data:legendData,
                                            width:'80%',
                                            right:"5%",
                                            top:"25px",
                                            textStyle:{
                                                color:'#eee'
                                            },
                                            pageIconColor:'#fff'
                                        },
                                        //dataset: [this.echartData.data],
                                        xAxis: {
                                            type: 'category',
                                            name:'时间',
                                            nameTextStyle:{
                                                color:'#5bc0de'
                                            },
                                            axisLine:{
                                                lineStyle:{
                                                    color:'#5bc0de'
                                                }
                                            },
                                            boundaryGap: false,
                                            axisLabel:{
                                                borderColor:'#5bc0de',
                                                // interval:0,
                                                rotate:'10'
                                            },
                                            data:xAxis
                                            //data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
                                        },
                                        grid:{
                                            // show:true,
                                           // bottom:this.echartData.baseConfig.rotate ? '20%' : '60'
                                        },
                                        yAxis: {
                                            name:'日志数',
                                            nameTextStyle:{
                                                color:'#5bc0de'
                                            },
                                            splitLine:{
                                                lineStyle:{
                                                    color:'#303e4e'
                                                }
                                            },
                                            axisLine:{
                                                lineStyle:{
                                                    color:'#5bc0de'
                                                }
                                            },
                                            axisLabel: {
                                                margin: 2,
                                                formatter: function (value, index) {
                                                    if (value >= 10000 && value < 10000000) {
                                                        value = value / 10000 + "万";
                                                    } else if (value >= 10000000) {
                                                        value = value / 10000000 + "千万";
                                                    }
                                                    return value;
                                                }
                                            }
                                        },
                                        series: [{
                                            type: 'line',

                                            //data: [150, 230, 224, 218, 135, 147, 260],
                                            symbolSize:(value,params)=>{
                                                //console.log(params)
                                                let size = this.size[params.dataIndex]
                                                //console.log(size)
                                                return JSON.stringify(size)
                                               // return '10'
                                            },
                                            smooth: true,
                                            symbol:'circle',
                                            showAllSymbol: true,
                                            itemStyle:{
                                                color:(params)=>{
                                                    return this.color[params.dataIndex]
                                                }
                                            },
                                            lineStyle:{
                                                color:'#5bc0de'
                                            },
                                            data:yData
                                        }]
                                    });
                                    window.addEventListener("resize",()=>{
                                        myChart.resize();
                                    });
                                },2000)

                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.dataDetailLoading = false;
                        })
                })
            }
        }
    }
</script>

<style scoped>
    .scroll-wapper{
        height: calc(100vh - 270px);
        /*border: 1px solid #cccccc;*/
        margin: 10px;
        display: flex;
    }
    .left-wapper{
        width: 65%;
        height: 100%;
        padding: 0 10px;
    }
    .right-wapper{
        width: calc(35% - 10px);
        height:100%;
        background: #303e4e;
    }
    .right-tit{
        height: 30px;
        line-height: 30px;
        background: #3c6c9c;
        padding-left: 10px;
        font-size: 13px;
        font-weight: 600;
    }
    .p-wapper{
        height: calc(100% - 30px);
        overflow-y: auto;
        padding:10px;
        background: #303e4e;
    }
    .chart-wapper{
        height: calc(50% - 20px);
        width: 100%;
        background: #303e4e;
        padding-top: 20px;
        margin-bottom: 10px;
    }
    .detail-wapper{
        width: 100%;
        height: 60vh;
        overflow: auto;
    }
    .text-wapper{
        position: absolute;
        right: 10px;
        top: 63px;
        color: #2d6fb3;
        width: 185px;
        height: 100px;
    }
    .text-wapper p{
        display: flex;
        align-items: center;
    }
    .text-wapper p span{
        display: inline-block;
        width: 13px;
        height: 13px;
        margin-right: 10px;
        border-radius: 100%;
    }
</style>
