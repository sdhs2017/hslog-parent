<template>
    <div v-if="equipment.id !== ''">
        <div v-show="echartsStatus" class="echarts-content">
            <div class="echarts-time">
<!--                <el-date-picker class="equipment-echart-time" value-format="yyyy-MM-dd" v-model="timeVal" @change="dateChage" type="date" placeholder="选择日期" size="mini"></el-date-picker>-->
                <dateLayout class="from-wapper" :busName="dateChangeBusName" :defaultVal = "defaultVal"></dateLayout>
            </div>
            <div class="echarts-data" v-if="!eventTypeEchart">
                <component :is="chartName" :params="params" :busName="busName" :setIntervalObj="intervalObj"> </component>
            </div>
            <div class="echarts-data" v-else style="display: flex">
                <component is="eqEventType_bar" :params="params" :busName="busName" :setIntervalObj="intervalObj"> </component>
                <component is="eqEventType_pie" :params="params" :busName="busName" :setIntervalObj="intervalObj"> </component>
            </div>
        </div>
        <div v-show="!echartsStatus" class="echarts-content" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
            <div class="equipment-table-title">{{tableTitle}} <b @click="()=>{this.echartsStatus = true}">返回图表</b></div>
            <div class="equipment-table-content">
                <v-basetable2 :tableHead="tableHead" :tableData="tableData"></v-basetable2>
            </div>
            <div class="equipment-table-page">
                <span>总数为 <b style="color:#56b3cf;">{{allCount}}</b> 条</span>
                <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="pageSize" :total="allCount"></el-pagination>
            </div>
        </div>
    </div>
</template>

<script>
    import dateLayout from '../common/dateLayout'
    import vBasetable2 from '../common/Basetable2';
    import bus from '../common/bus';
    import {setChartParam} from "../../../static/js/common";
    import eqHourlyLogCount_line from '../charts/log/equipment/eqHourlyLogCount_line'
    import eqLogLevel_bar from '../charts/log/equipment/eqLogLevel_bar'
    import eqEventType_bar from '../charts/log/equipment/eqEventType_bar'
    import eqEventType_pie from '../charts/log/equipment/eqEventType_pie'
    import eqWinlogHourlyEventCount_moreline from '../charts/log/equipment/eqWinlogHourlyEventCount_moreline'
    export default {
        // name: "echartsTable",
        props:{
            echartsConfig:{
                type: Object,
            },
            eventTypeEchart:{
                type: Boolean,
                default(){
                    return false
                }
            },
            equipment:{
                type: Object,
                default(){
                    return{
                        id:'',
                        type:''
                    }
                }
            },
            urls:{
                type:Object,
                default(){
                    return{
                        echartUrl:'',
                        tableUrl:''
                    }
                }
            },
            chartName:{
                type:String
            },
            echartType:{
                type:String
            },
            exportName:{
                type:String
            },
            busName:{
                type:Object,
                default(){
                    return{
                        clickName:'',
                        exportName:''
                    }
                }
            }
        },
        data(){
            return{
                dateChangeBusName:'',
                loading:false,
                echartsStatus:true,
                timeVal:'',//时间
                echartDataType:'',//用于区分图表数据
                params:{},
                echartData:{
                    barData:{//柱状图数据
                        baseConfig:{
                        },
                        xAxisArr:[],
                        yAxisArr:[]
                    },
                    pieData:{//饼状图数据
                        baseConfig:{
                        },
                        xAxisArr:[],
                        yAxisArr:[]
                    },
                    lineData:{//折线图数据
                        baseConfig:{
                        },
                        xAxisArr:["00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"],
                        yAxisArr:[]
                    }
                },
                echartsConditions:{
                    equipmentid:'',
                    type:'',
                    param:'',
                    index:'estest'
                },//图表参数
                tableCondition:{},//表格数据参数
                /*busName:{//监听名称
                    clickName:'',
                    exportName:''
                },*/
                tableHead:[],
                logTableHead:[//表头
                    {
                        prop:'@timestamp',
                        label:'时间',
                        width:'150'
                    },
                    {
                        prop:'log.level',
                        label:'级别',
                        width:'100'
                    },
                    {
                        prop:'agent.type',
                        label:'日志类型',
                        width:'100'
                    },
                    {
                        prop:'message',
                        label:'日志内容',
                        width:''
                    },
                ],
                eventTableHead:[
                    {
                        prop:'@timestamp',
                        label:'时间',
                        width:'150'
                    },
                    {
                        prop:'event.action',
                        label:'事件名称',
                        width:'150'
                    },
                    {
                        prop:'agent.type',
                        label:'日志类型',
                        width:'100'
                    },
                    {
                        prop:'winlog',
                        label:'事件原因',
                        width:'100'
                    },
                    {
                        prop:'message',
                        label:'日志内容',
                        width:''
                    }
                ],
                tableData:[],//表格数据
                pageSize:10,//每页条数
                c_page:1,//当前页
                allCount:0,//总条数
                clickXVal:'',//点击图表的x轴数据
                tableTitle:'',//列表头文字
                echartdata:{},
                exportEchartdata:false,
                //轮询参数
                intervalObj:{
                    state:false,
                    interval:'5000'
                },
                //时间控件参数
                defaultVal:{
                    //具体时间参数
                    lastVal:'',
                    //起始时间
                    starttime:'',
                    //结束时间
                    endtime:'',
                    //具体时间 类型状态
                    dateBlock:true,
                    //是否存在轮询框
                    isIntervalBox:true,
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
                }
            }
        },
        created(){
            //获取当前日期
            let day1 = new Date();
            let mon = day1.getMonth()+1;
            let da = day1.getDate();
            if( mon < 10){
                mon = "0"+(day1.getMonth()+1);
            }
            if(da < 10){
                da = "0"+ (day1.getDate());
            }
            this.timeVal = day1.getFullYear()+"-" + mon + "-" + da;
            this.defaultVal.endtime= this.timeVal+ ' 23:59:59'
            this.defaultVal.starttime = this.timeVal+ ' 00:00:00'

        },
        beforeDestroy(){
            bus.$off(this.busName.clickName)
            bus.$off(this.busName.dateChangeBusName)
        },
        watch:{
            /*检测资产id的变动*/
            'equipment.id'(){
               this.$nextTick(()=>{
                   //请求参数
                   this.dateChangeBusName  = 'dateChangeBusName'+this.busName.clickName;
                   this.params = {
                       intervalValue:'',
                       intervalType:'',
                       starttime:this.timeVal+ ' 00:00:00',
                       endtime:this.timeVal+ ' 23:59:59',
                       last:'',
                       queryParam:JSON.stringify({'fields.equipmentid':this.equipment.id})
                   };
                   //监听时间改变
                   bus.$on(this.dateChangeBusName,(obj)=>{
                       //设置参数对应
                       let arr = setChartParam(obj);
                       this.params = arr[0];
                       this.params.queryParam=JSON.stringify({'fields.equipmentid':this.equipment.id});
                       this.intervalObj = arr[1]
                   })
                   //监听导出事件
                   bus.$on(this.busName.exportName,(params)=>{
                       this.exportEchartdata = true;
                       //向父组件传递参数
                       this.echartdata = params;
                       bus.$emit(this.exportName,params)
                   })
                   //监听点击事件
                   bus.$on(this.busName.clickName,(params)=>{
                       this.c_page = 1;
                       this.clickXVal = params.name;
                       if(params.name.length < 2){
                           params.name = '0'+params.name
                       }
                       //隐藏图表
                       this.echartsStatus = false;
                       //判断当前的图表类型
                       if(this.echartType === 'line'){//日志总数
                            this.tableTitle = this.clickXVal+' 日志列表'
                            this.tableHead = this.logTableHead;
                           //处理时间
                           let reg=/00:00$/gi;
                           this.tableCondition = {
                               starttime : this.clickXVal,
                               endtime : this.clickXVal.replace(reg,"59:59"),
                               queryParam:JSON.stringify({'fields.equipmentid':this.equipment.id})
                           }
                       }else if(this.echartType === 'moreline'){//事件每小时
                           this.tableTitle = this.clickXVal+' '+params.seriesName+'事件列表'
                           //处理时间
                           let reg=/00:00$/gi;
                           this.tableCondition = {
                               starttime : this.clickXVal,
                               endtime : this.clickXVal.replace(reg,"59:59"),
                               queryParam:JSON.stringify({'fields.equipmentid':this.equipment.id,'event.action':params.seriesName})
                           }
                           this.tableHead = this.logTableHead;
                       }else if(this.echartType === 'bar'){//日志类型
                           this.tableTitle = this.clickXVal+' 日志列表'
                           this.tableCondition = this.params;
                           this.tableCondition.queryParam=JSON.stringify({'fields.equipmentid':this.equipment.id,'log.level':params.name});
                           this.tableHead = this.logTableHead;
                       }else{//事件类型
                           this.tableTitle = this.clickXVal+' 事件列表';
                           this.tableCondition = this.params;
                           this.tableCondition.queryParam=JSON.stringify({'fields.equipmentid':this.equipment.id,'event.action':params.name});
                           this.tableHead = this.eventTableHead;
                       }
                       this.getLogsListData(1)
                   })
               })
            },
            'exportEchartData'(){
                console.log('eeeee')
                bus.$emit('dd',this.echartdata)
            }
        },
        methods:{
            /*获取日志列表数据*/
            getLogsListData(page){
                this.loading = true;
                this.tableCondition.page = page;
                this.tableCondition.size = this.pageSize;
              /*  let hsObj = {};
                hsObj.hsData = this.tableCondition;*/
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/'+this.urls.tableUrl,this.$qs.stringify(this.tableCondition))
                        .then((res)=>{
                            this.loading = false;
                            //console.log(res.data)
                            this.allCount = res.data[0].count;
                            this.tableData = res.data[0].list;
                        })
                        .catch((err)=>{
                            this.loading = false;
                        })
                })
            },
            /*日期改变*/
            dateChage(){
                if(this.timeVal){
                    this.params = {
                        starttime:this.timeVal+ ' 00:00:00',
                        endtime:this.timeVal+ ' 23:59:59',
                        hsData:JSON.stringify({'fields.equipmentid':this.equipment.id})
                    }
                }else{
                    this.params = {
                        starttime:'',
                        endtime:'',
                        hsData:JSON.stringify({'fields.equipmentid':this.equipment.id})
                    }
                }
            },
            /*页码改变*/
            handleCurrentChange(page){
                this.getLogsListData(page)
            }
        },
        components:{
            dateLayout,
            vBasetable2,
            eqHourlyLogCount_line,
            eqLogLevel_bar,
            eqEventType_bar,
            eqEventType_pie,
            eqWinlogHourlyEventCount_moreline
        }
    }
</script>

<style scoped>
    .echarts-content{
        height: 38vh;
        min-height: 280px;
        background: #303e4e;
    }
    .echarts-time{
        height: 60px;
        line-height: 60px;
        padding-left: 20px;
        display: flex;
        justify-content: flex-end;
        align-items: center;
        margin-right: 10px;
    }
    .equipment-echart-time{
        width: 126px;
    }
    .echarts-data{
        height: calc(100% - 70px);
    }
    .equipment-table-title{
        height: 40px;
        line-height: 40px;
        text-align: center;
        color: #d3946f;
        font-weight: 600;
        border-bottom: 1px solid #5a7494;
    }
    .equipment-table-title b{
        float: right;
        font-size: 13px;
        margin-right: 10px;
        cursor: pointer;
        color: #56b3cf;
    }
    .equipment-table-content{
        height: calc(100% - 70px);
        overflow: auto;
    }
    .equipment-table-page{
        border-top: 1px solid #5a7494;
        height: 30px;
        font-size: 13px;
        display: flex;
        justify-content: flex-end;
        align-items: center;
    }
</style>
