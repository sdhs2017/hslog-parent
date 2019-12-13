<template>
    <div class="content-bg">
        <div class="top-title">全部资产报表
            <el-dropdown class="exportEchartsMenu">
                  <span class="el-dropdown-link">
                    导出<i class="el-icon-arrow-down el-icon--right"></i>
                  </span>
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item @click.native="exportEcharts()">PDF格式</el-dropdown-item>
                    </el-dropdown-menu>
            </el-dropdown>
        </div>
        <div class="all-echarts-wapper">
            <h2 class="all-echarts-title">日志级别数量统计</h2>
            <div class="all-echarts-condition">
                <span>日志类型：</span>
                <el-select v-model="logType" placeholder="请选择" @change="logTypeChange">
                    <el-option
                        v-for="item in logTypeArr"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                    </el-option>
                </el-select>
                <span>日期范围：</span>
                <el-date-picker
                    v-model="dateVal"
                    type="daterange"
                    align="right"
                    size="mini"
                    value-format="yyyy-MM-dd"
                    range-separator="-"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    @change="changeDate"
                    :picker-options="pickerOptions">
                </el-date-picker>
            </div>
            <div class="all-echarts-content">
                <el-row>
                    <el-col :span="12">
                        <div class="grid-content bg-purple ">
                            <v-echarts echartType="bar" :echartData = "this.echartData.barData" :busName="{exportName:'allEchartbar',clickName:'allEchartsBarClick'}"></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="grid-content bg-purple-light">
                            <v-echarts echartType="pie" :echartData = "this.echartData.pieData" :busName="{exportName:'allEchartpie',clickName:'allEchartsBarClick'}"></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>
</template>

<script>
    import vEcharts from '../common/echarts';
    import bus from '../common/bus';
    import {downloadToPDF} from  '../../../static/js/common.js'
    export default {
        name: "allEcharts",
        data() {
            return {
                logType: '',
                dateVal:[],
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
                searchParams:{
                    type:'',
                    starttime:'',
                    endtime:'',
                    index:'estest',
                    param:'operation_level'
                },
                logTypeArr: [{
                    value: '',
                    label: '全部'
                }, {
                    value: 'syslog',
                    label: 'syslog'
                }, {
                    value: 'winlog',
                    label: 'winlog'
                }, {
                    value: 'log4j',
                    label: 'log4j'
                }, {
                    value: 'mysql',
                    label: 'mysql'
                }],
                echartData: {
                    barData: {//柱状图数据
                        baseConfig: {
                            title: '',
                            xAxisName: '级别',
                            yAxisName: '数量/条',
                            hoverText: '数量'
                        },
                        xAxisArr: [],
                        yAxisArr: []
                    },
                    pieData: {//饼状图数据
                        baseConfig: {
                            title: '',
                            hoverText: '百分比'
                        },
                        xAxisArr: [],
                        yAxisArr: []
                    }
                },
                exportEchartsArr:[]//导出的图表集合
            }
        },
        created(){
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            this.dateVal=[start,end];
            var mon = end.getMonth()+1;
            var da = end.getDate();
            if( mon < 10){
                mon = "0"+(end.getMonth()+1);
            }
            if(da < 10){
                da = "0"+ (end.getDate());
            }
            this.searchParams.endtime = end.getFullYear()+"-" + mon + "-" + da;
            //定义七天前时间
            var date2 = new Date(end);
            date2.setDate(end.getDate()-7);
            let sd = date2.getDate();
            if(sd < 10){
                sd = "0"+ sd;
            }
            this.searchParams.starttime = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+sd;
            this.getAllEchartsData(this.searchParams);
            //绑定导出事件
            bus.$on('allEchartbar',(params)=>{
                this.exportEchartsArr[0]= params;
            })
            bus.$on('allEchartpie',(params)=>{
                this.exportEchartsArr[1]= params;
            })
            bus.$on('allEchartsBarClick',(params)=>{
                this.$router.push({name:'searchLogs',params:{words: params.name}})
            })
        },
        methods:{
            clickss(params){
                this.$router.push({name:'searchLogs',params:{words: params.name}})
            },
            /*获取报表数据*/
            getAllEchartsData(params){
                layer.load(1)
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/log/getCountGroupByParam.do',this.$qs.stringify(params))
                        .then((res)=>{
                            layer.closeAll('loading');
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
                                pieVAl.push(pieObj);
                            }
                            //赋值
                            this.echartData.barData.xAxisArr = xVal;
                            this.echartData.barData.yAxisArr = yVal;
                            this.echartData.pieData.yAxisArr = pieVAl;
                        })
                        .catch((err)=>{
                            layer.closeAll('loading');
                        })
                })
            },
            /*日志类型改变*/
            logTypeChange(){
                this.searchParams.type = this.logType;
                this.getAllEchartsData(this.searchParams)
            },
            /*到处报表*/
            exportEcharts(){
                let imgBase64Arr = [];//用于存放图表Base64格式
                for(let i in this.exportEchartsArr){
                    //添加数据
                    imgBase64Arr.push(this.exportEchartsArr[i].getDataURL());
                }
                let canvas = $("canvas");
                let contentWidth = canvas.width();
                let contentHeight = canvas.height();
                downloadToPDF(imgBase64Arr,40,contentWidth,contentHeight)
            },
            /*时间改变*/
            changeDate(){
                this.searchParams.starttime = this.dateVal[0];
                this.searchParams.endtime = this.dateVal[1];
                this.getAllEchartsData(this.searchParams)
            }
        },
        components:{
            vEcharts
        }
    }
</script>

<style scoped>
    .exportEchartsMenu{
        float: right;
        color: #fff;
        margin-right: 20px;
        cursor: pointer;
    }
    .all-echarts-wapper{
        height: 100%;
    }
    .all-echarts-title{
        text-align: center;
        margin-bottom: 20px;
    }
    .all-echarts-condition{
        padding-left: 50px;
        margin-bottom: 20px;
    }
    .all-echarts-content{
        height: 60vh;
        padding: 10px 20px;
    }
    .all-echarts-content div{
        height: 100%;
    }
</style>
