<template>
    <div class="content-bg">
        <div class="title">
            <span class="goBack" @click="goBack()">返回</span>
            <b>{{equipmentName}}统计</b>
            <span></span>
        </div>
        <ul class="count-wapper">
            <li>
                <div class="count-tit">日志总数</div>
                <div class="count-val">{{logsCount}}</div>
            </li>
            <li>
                <div class="count-tit">ERROR日志</div>
                <div class="count-val err-val">{{errorLogsCount}}</div>
            </li>
            <li>
                <div class="count-tit">事件总数</div>
                <div class="count-val">{{eventCount}}</div>
            </li>
            <li>
                <div class="count-tit">高危事件</div>
                <div class="count-val err-val">{{dangerEventCount}}</div>
            </li>
        </ul>
        <div class="item-wapper">
            <div class="item-title">
                <dateLayout :busName="hourlyBusName" :defaultVal = "hourlyLogDate"></dateLayout>
            </div>
            <div class="item-content">
                <eqHourlyLogCount_line :params="hourlyLogParams" :setIntervalObj="hourlyLogIntervalObj"></eqHourlyLogCount_line>
            </div>
        </div>
        <div class="item-wapper">
            <div class="item-title">
                <dateLayout :busName="hourlyEventBusName" :defaultVal = "hourlyEventDate"></dateLayout>
            </div>
            <div class="item-content">
                <eqSyslogHourlyEventCount_moreline :params="hourlyEventParams" :setIntervalObj="hourlyEventIntervalObj"></eqSyslogHourlyEventCount_moreline>
            </div>
        </div>
        <div class="item-wapper">
            <div class="item-title">
                <dateLayout :busName="logLevelBusName" :defaultVal = "logLevelDate"></dateLayout>
            </div>
            <div class="item-content">
                <eqLogLevel_bar :params="logLevelParams" :setIntervalObj="logLevelIntervalObj"></eqLogLevel_bar>
            </div>
        </div>
        <div class="item-wapper">
            <div class="item-title">
                <dateLayout :busName="eventTypeBusName" :defaultVal = "eventTypeDate"></dateLayout>
            </div>
            <div class="item-content">
                <eqEventType_bar :params="eventTypeParams" :setIntervalObj="eventTypeIntervalObj"></eqEventType_bar>
            </div>
            <div class="item-content">
                <eqEventType_pie :params="eventTypeParams" :setIntervalObj="eventTypeIntervalObj"></eqEventType_pie>
            </div>
        </div>
    </div>

</template>

<script>
    import dateLayout from '../../common/dateLayout';
    import bus from '../../common/bus';
    import {setChartParam} from "../../../../static/js/common";
    import eqHourlyLogCount_line from '../../charts/log/equipment/eqHourlyLogCount_line'
    import eqLogLevel_bar from '../../charts/log/equipment/eqLogLevel_bar'
    import eqEventType_bar from '../../charts/log/equipment/eqEventType_bar'
    import eqEventType_pie from '../../charts/log/equipment/eqEventType_pie'
    import eqSyslogHourlyEventCount_moreline from '../../charts/log/equipment/eqSyslogHourlyEventCount_moreline'
    export default {
        name: "equipmentCharts",
        data() {
            return {
                timeVal:'',
                //每小时日志
                hourlyLogDate:{
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
                },
                hourlyLogIntervalObj:{
                    state:false,
                    interval:'5000'
                },
                hourlyLogParams:{},
                hourlyBusName:'',
                //每小时事件
                hourlyEventDate:{
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
                },
                hourlyEventIntervalObj:{
                    state:false,
                    interval:'5000'
                },
                hourlyEventParams:{},
                hourlyEventBusName:'',
                //日志级别
                logLevelDate:{
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
                },
                logLevelIntervalObj:{
                    state:false,
                    interval:'5000'
                },
                logLevelParams:{},
                logLevelBusName:'',
                //事件类型
                eventTypeDate:{
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
            },
                eventTypeIntervalObj:{
                    state:false,
                    interval:'5000'
                },
                eventTypeParams:{},
                eventTypeBusName:'',
                //资产id
                equipmentId: '',
                //资产名
                equipmentName: '',
                //日志条数
                logsCount: '',
                //error日志条数
                errorLogsCount: '',
                //事件条数
                eventCount: '',
                //高危事件条数
                dangerEventCount: '',

            }
        },
        created() {
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
            this.hourlyLogDate.endtime= this.timeVal+ ' 23:59:59'
            this.hourlyLogDate.starttime = this.timeVal+ ' 00:00:00'
            this.hourlyEventDate.endtime= this.timeVal+ ' 23:59:59'
            this.hourlyEventDate.starttime = this.timeVal+ ' 00:00:00'
            this.logLevelDate.endtime= this.timeVal+ ' 23:59:59'
            this.logLevelDate.starttime = this.timeVal+ ' 00:00:00'
            this.eventTypeDate.endtime= this.timeVal+ ' 23:59:59'
            this.eventTypeDate.starttime = this.timeVal+ ' 00:00:00'
        },
        methods: {
            /*返回上一级*/
            goBack() {
                this.$router.push('/mobile/equipmentList');//返回上一层
            },
            /*获取日志总数于错误日志数*/
            getLogCount(id) {
                this.$nextTick(() => {
                    this.$axios.post(this.$baseUrl + '/ecsCommon/getIndicesCount.do', this.$qs.stringify({
                        hsData: JSON.stringify({'fields.equipmentid': id})
                    }))
                        .then((res) => {
                            this.logsCount = res.data[0].indices;
                            this.errorLogsCount = res.data[0].indiceserror;
                        })
                        .catch((err) => {
                            console.log(err)
                        })
                })
            },
            /*获取事件总数于高危事件数*/
            getEventCount(id) {
                this.$nextTick(() => {
                    this.$axios.post(this.$baseUrl + '/ecsSyslog/getEventsCount.do', this.$qs.stringify({
                        hsData: JSON.stringify({'fields.equipmentid': id})
                    }))
                        .then((res) => {
                            this.eventCount = res.data[0].events;
                            this.dangerEventCount = res.data[0].eventserror;
                        })
                        .catch((err) => {
                            console.log(err)
                        })
                })
            },
        },
        watch: {
            equipmentId: {
                handler: function (o, n) {
                    this.getLogCount(this.equipmentId);
                    this.getEventCount(this.equipmentId);
                    //每小时日志数报表
                    this.hourlyBusName = 'hourlyBusName' + this.equipmentId;
                    this.hourlyLogParams = {
                        intervalValue:'',
                        intervalType:'',
                        starttime:this.timeVal+ ' 00:00:00',
                        endtime:this.timeVal+ ' 23:59:59',
                        last:'',
                        queryParam:JSON.stringify({'fields.equipmentid':this.equipmentId})
                    };
                    bus.$on(this.hourlyBusName,(obj)=>{
                        //设置参数对应
                        let arr = setChartParam(obj);
                        this.hourlyLogParams = arr[0];
                        this.hourlyLogParams.queryParam=JSON.stringify({'fields.equipmentid':this.equipmentId});
                        this.hourlyLogIntervalObj = arr[1]
                    })
                    //每小时事件数报表
                    this.hourlyEventBusName = 'hourlyEventBusName' + this.equipmentId;
                    this.hourlyEventParams = {
                        intervalValue:'',
                        intervalType:'',
                        starttime:this.timeVal+ ' 00:00:00',
                        endtime:this.timeVal+ ' 23:59:59',
                        last:'',
                        queryParam:JSON.stringify({'fields.equipmentid':this.equipmentId})
                    };
                    bus.$on(this.hourlyEventBusName,(obj)=>{
                        //设置参数对应
                        let arr = setChartParam(obj);
                        this.hourlyEventParams = arr[0];
                        this.hourlyEventParams.queryParam=JSON.stringify({'fields.equipmentid':this.equipmentId});
                        this.hourlyEventIntervalObj = arr[1]
                    })
                    //日志级别报表
                    this.logLevelBusName = 'logLevelBusName' + this.equipmentId;
                    this.logLevelParams = {
                        intervalValue:'',
                        intervalType:'',
                        starttime:this.timeVal+ ' 00:00:00',
                        endtime:this.timeVal+ ' 23:59:59',
                        last:'',
                        queryParam:JSON.stringify({'fields.equipmentid':this.equipmentId})
                    };
                    bus.$on(this.logLevelBusName,(obj)=>{
                        //设置参数对应
                        let arr = setChartParam(obj);
                        this.logLevelParams = arr[0];
                        this.logLevelParams.queryParam=JSON.stringify({'fields.equipmentid':this.equipmentId});
                        this.logLevelIntervalObj = arr[1]
                    })
                    //事件类型
                    this.eventTypeBusName = 'eventTypeBusName' + this.equipmentId;
                    this.eventTypeParams = {
                        intervalValue:'',
                        intervalType:'',
                        starttime:this.timeVal+ ' 00:00:00',
                        endtime:this.timeVal+ ' 23:59:59',
                        last:'',
                        queryParam:JSON.stringify({'fields.equipmentid':this.equipmentId})
                    };
                    bus.$on(this.eventTypeBusName,(obj)=>{
                        //设置参数对应
                        let arr = setChartParam(obj);
                        this.eventTypeParams = arr[0];
                        this.eventTypeParams.queryParam=JSON.stringify({'fields.equipmentid':this.equipmentId});
                        this.eventTypeIntervalObj = arr[1]
                    })
                },
                //immediate: true
            }
        },
        beforeRouteEnter(to, from, next) {
            next(vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                vm.$options.name = 'equipmentCharts' + to.query.id;
                vm.equipmentId = to.query.id;
                //修改data参数
                vm.equipmentName = to.query.name;
                //vm.busName = 'equipmentEcharts'+to.query.id;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path: '/mobile/equipmentCharts' + to.query.id,
                    component: 'mobile/equipment/equipmentCharts.vue',
                    title: '统计'
                }
                sessionStorage.setItem('/mobile/equipmentCharts' + to.query.id, JSON.stringify(obj))
                /*if(vm.equipmentId === '' || vm.equipmentId !== to.query.id){
                  vm.equipmentId = to.query.id;
                  vm.logType = to.query.logType;
                }*/

            })

        },
        components: {
            dateLayout,
            eqHourlyLogCount_line,
            eqLogLevel_bar,
            eqEventType_bar,
            eqEventType_pie,
            eqSyslogHourlyEventCount_moreline
        },
    }
</script>

<style scoped>
    .title {
        font-size: 1.3rem;
        color: #185bff;
        padding: 10px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        text-shadow: none;
    }

    .goBack {
        font-size: 0.7rem;
    }

    .item-wapper {
        height: auto;
        /*background: rgb(26,36,47);*/
        background: url("../../../../static/img/panel-l-t.png");
        background-size: 100% 100%;
        margin: 0.5rem 0.2rem;
    }

    .item-title {
        height: 2.5rem;
        line-height: 1rem;
        font-size: 0.38rem;
        text-align: left;
        padding-left: 10px;
        background: url("../../../../static/img/title-bg.png");
        background-size: 100% 100%;
        display: flex;
        align-items: center
    }

    .time-wapper {
        float: right;
        width: 127px;
        margin-right: 5px;
    }

    .time-wapper /deep/ .el-input__inner {
        width: 100% !important;
    }

    .item-content {
        /*height: calc(100% - 1rem);*/
        height: 230px;
        box-sizing: border-box;
        padding: 0.5rem;

    }

    .count-wapper {
        display: flex;
        height: 90px;
        justify-content: space-around;
        font-size: 0.3rem;
    }

    .count-wapper li {
        width: 24%;
        height: 90px;
        background: url("../../../../static/img/panel-l-t.png");
        background-size: 100% 100%;
        font-weight: 600;
    }

    .count-wapper .count-tit {
        height: 40px;
        line-height: 40px;
        background: #0e1d59;
        color: #39c0bd;
        text-align: center;
    }

    .count-wapper .count-val {
        height: 50px;
        line-height: 50px;
        font-family: 'electronicFont';
        font-size: 0.9rem;
        text-align: center;
    }

    .count-wapper .err-val {
        color: #e4956d;
    }

    /deep/ .el-input__inner {
        -webkit-border-radius: 0 !important;
        -moz-border-radius: 0 !important;
        border-radius: 0 !important;
        position: relative;
        background: 0;
        border: 1px solid #409eff;
        color: #409eff;
    }
</style>
