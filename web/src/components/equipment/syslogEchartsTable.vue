<template>
    <div v-if="equipment.id !== ''">
        <div v-show="echartsStatus" class="echarts-content">
            <div class="echarts-time">
                <el-date-picker class="equipment-echart-time" value-format="yyyy-MM-dd" v-model="timeVal" @change="dateChage" type="date" placeholder="选择日期" size="mini"></el-date-picker>
            </div>
            <div class="echarts-data" v-if="!eventTypeEchart">
                <component :is="chartName" :params="params" :busName="busName"> </component>
            </div>
            <div class="echarts-data" v-else style="display: flex">
                <component is="eqEventType_bar" :params="params" :busName="busName"> </component>
                <component is="eqEventType_pie" :params="params" :busName="busName"> </component>
            </div>
        </div>
        <div v-show="!echartsStatus" class="echarts-content"  v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
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
    import vEcharts from '../common/echarts'
    import vBasetable2 from '../common/Basetable2';
    import bus from '../common/bus';
    import eqHourlyLogCount_line from '../charts/log/equipment/eqHourlyLogCount_line'
    import eqLogLevel_bar from '../charts/log/equipment/eqLogLevel_bar'
    import eqEventType_bar from '../charts/log/equipment/eqEventType_bar'
    import eqEventType_pie from '../charts/log/equipment/eqEventType_pie'
    import eqSyslogHourlyEventCount_moreline from '../charts/log/equipment/eqSyslogHourlyEventCount_moreline'
    export default {
        // name: "echartsTable",
        props:{
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
            chartName:{
                type:String
            },
            echartType:{
                type:String
            },
            urls:{
                type:Object,
                default(){
                    return{
                        tableUrl:''
                    }
                }
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
                loading:false,
                echartsStatus:true,
                timeVal:'',//时间
                timeValArr:[],
                params:{},
                echartDataType:'',//用于区分图表数据
                tableCondition:{},//表格数据参数
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
                exportEchartdata:false
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
        },
        beforeDestroy(){
            bus.$off(this.busName.clickName)
        },
        watch:{
            // /!*检测资产id的变动*!/
            'equipment.id'(){
                this.$nextTick(()=>{
                    this.params = {
                        starttime:this.timeVal+ ' 00:00:00',
                        endtime:this.timeVal+ ' 23:59:59',
                        hsData:JSON.stringify({'fields.equipmentid':this.equipment.id})
                    };
                    //日志级别参数、事件类型
                    /*if(this.chartName === 'eqLogLevel_bar'){
                        this.params.groupField='log.level';
                    }else if(this.chartName === 'eqEventType_bar'){
                        this.params.groupField='event.action';
                    }*/
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
                        this.tableCondition = {
                            starttime : this.timeVal+' '+ params.name+':00:00',
                            endtime : this.timeVal+' '+ params.name+':59:59',
                            'fields.equipmentid':this.equipment.id,
                        }
                        //隐藏图表
                        this.echartsStatus = false;
                        //判断当前的图表类型
                        if(this.echartType === 'line'){//日志总数
                            this.tableTitle = this.timeVal+' '+this.clickXVal+'时 日志列表'
                            this.tableHead = this.logTableHead;
                        }else if(this.echartType === 'moreline'){//事件每小时
                            this.tableTitle = this.timeVal+' '+this.clickXVal+' '+params.seriesName+'事件列表'
                            this.clickXVal = params.name;
                            this.tableCondition = {
                                starttime : this.timeVal+' '+ params.name+':00:00',
                                endtime : this.timeVal+' '+ params.name+':59:59',
                                'fields.equipmentid':this.equipment.id
                            }
                            this.tableCondition['log.levels'] = params.seriesName;
                            this.tableHead = this.eventTableHead;
                        }else if(this.echartType === 'bar'){//日志类型
                            this.tableTitle = this.timeVal+' '+this.clickXVal+' 日志列表'
                            this.tableCondition = {
                                starttime : this.timeVal+' 00:00:00',
                                endtime : this.timeVal+' 23:59:59',
                                'fields.equipmentid':this.equipment.id,
                            }
                            this.tableCondition['log.level'] = params.name;
                            this.tableHead = this.logTableHead;
                        }else{//事件类型
                            this.tableTitle = this.timeVal+' '+this.clickXVal+' 事件列表';
                            this.tableCondition = {
                                starttime : this.timeVal+' 00:00:00',
                                endtime : this.timeVal+' 23:59:59',
                                'fields.equipmentid':this.equipment.id,
                            }
                            this.tableCondition['event.action'] = params.name;
                            this.tableHead = this.eventTableHead;
                        }
                        this.getLogsListData(1)
                    })
                })
            },
            'exportEchartData'(){
                bus.$emit('dd',this.echartdata)
            }
        },
        methods:{
            /*获取日志列表数据*/
            getLogsListData(page){
                this.loading = true;
                this.tableCondition.page = page;
                this.tableCondition.size = this.pageSize;
                let hsObj = {};
                hsObj.hsData = this.tableCondition;
                this.$nextTick(()=>{
                    this.$axios.get(this.$baseUrl+'/'+this.urls.tableUrl,{params:hsObj})
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
                this.params = {
                    starttime:this.timeVal+ ' 00:00:00',
                    endtime:this.timeVal+ ' 23:59:59',
                    hsData:JSON.stringify({'fields.equipmentid':this.equipment.id})
                };
              /*  //日志级别参数、事件类型
                if(this.chartName === 'eqLogLevel_bar'){
                    this.params.groupField='log.level';
                }else if(this.chartName === 'eqEventType_bar'){
                    this.params.groupField='event.action';
                }*/
            },
            /*页码改变*/
            handleCurrentChange(page){
                this.getLogsListData(page)
            }
        },
        components:{
            vEcharts,
            vBasetable2,
            eqHourlyLogCount_line,
            eqLogLevel_bar,
            eqEventType_bar,
            eqEventType_pie,
            eqSyslogHourlyEventCount_moreline
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
        height: 40px;
        line-height: 40px;
        padding-left: 20px;
    }
    .equipment-echart-time{
        width: 126px;
    }
    .echarts-data{
        height: calc(100% - 40px);
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
