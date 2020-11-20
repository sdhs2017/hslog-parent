<template>
    <div class="content-bg" v-loading="loading" element-loading-background="rgba(48, 62, 78, 0.5)">
        <div class="top-title">{{this.alarmName}} 执行详情
            <div class="data-box">
                日期范围:
                <el-date-picker
                    class="date-wapper"
                    v-model="dateVal"
                    type="datetimerange"
                    range-separator="至"
                    size="mini"
                    :clearable="false"
                    start-placeholder="开始日期"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    :default-time="['00:00:00', '23:59:59']"
                    @change="timeChange"
                    end-placeholder="结束日期"
                    :picker-options="pickerOptions">
                </el-date-picker>
                <el-button size="mini" type="primary" plain @click="refrshData">刷新</el-button>
            </div>
        </div>
        <div class="execute-list-wapper">
            <vBasetable :tableHead="tableHead" :tableData="tableData"></vBasetable>
        </div>
        <div class="alarm-table-page">
            <span>共检索到告警数量为 <b>{{allCounts}}</b> 个</span>
            <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="size" :total="allCounts"></el-pagination>
        </div>
        <el-dialog :title="`快照详情  ${dialogTitle}`" :visible.sync="snapshotDialog" width="800px">
            <div class="snapshot-wapper">
                <p>
                    <span class="tit">告警名称 :</span>
                    <span class="txt">{{dialogObj.alert_name}}</span>
                </p>
                <p>
                    <span class="tit">数据源 :</span>
                    <span class="txt">{{dialogObj.pre_index_name}}{{dialogObj.suffix_index_name}}</span>
                </p>
                <p>
                    <span class="tit">条件 :</span>
                    <span class="txt">{{setAlertCondition(dialogObj.alert_conditions)}}</span>
                </p>
                <p>
                    <span class="tit">执行周期 :</span>
                    <span class="txt">{{setAlertExcute(dialogObj)}}</span>
                </p>
                <p>
                    <span class="tit">时间周期 :</span>
                    <span class="txt">{{setAlertTime(dialogObj)}}</span>
                </p>
                <!--<p>
                    <span class="tit">资产组 :</span>
                    <span class="txt">{{dialogObj.alert_assetGroup_name}}</span>
                </p>
                <p>
                    <span class="tit">资产 :</span>
                    <span class="txt">{{dialogObj.alert_equipment_name}}</span>
                </p>-->
                <p>
                    <span class="tit">详情 :</span>
                    <span class="txt"><jsonView :data="dialogObj.result" theme="one-dark" :line-height="20" :deep="5" class="jsonView"></jsonView></span>
                </p>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import vBasetable from '../common/Basetable2';
    import jsonView from 'vue-json-views'
    import {dateFormat} from  '../../../static/js/common.js'
    export default {
        name: "alarmExecuteDetail",
        data(){
            return{
                alarmId:'',
                alarmName:'',
                loading:false,
                snapshotDialog:false,
                dialogTitle:'',
                allCounts:0,
                page:1,//页码
                c_page:1,//当前页码
                size:15,//每页的数量
                snapshotJson:'',
                tableHead:[
                    {
                        prop:'run_time',
                        label:'执行时间',
                        width:'200',
                        formatData:(val,obj)=>{
                            if (obj.alert_fire){
                                return `<span style="color: #F56C6C;">${val}</span>`
                            } else{
                                return val;
                            }
                        }
                    },
                    {
                        prop:'alert_conditions',
                        label:'条件',
                        width:'',
                        formatData:(val,obj)=>{
                            if (obj.alert_fire){
                                return `<span style="color: #F56C6C;">${this.setAlertCondition(val)}</span>`
                            } else{
                                return this.setAlertCondition(val);
                            }
                        }
                    },
                    {
                        prop:'match_size',
                        label:'匹配结果数',
                        width:'',
                        formatData:(val,obj)=>{
                            if (obj.alert_fire){
                                return `<span style="color: #F56C6C;">${val}</span>`
                            } else{
                                return val;
                            }
                        }
                    },
                    {
                        prop:'alert_fire',
                        label:'是否告警',
                        width:'150',
                        formatData:(val,obj)=>{
                            if (obj.alert_fire){
                                return `<span style="color: #F56C6C;">是</span>`
                            } else{
                                return '否';
                            }
                        }
                    },
                    {
                        prop:'state',
                        label:'操作',
                        width:'150',
                        clickFun:(item)=>{
                            this.snapshotDialog = true;
                            let obj = JSON.parse(JSON.stringify(item))
                            obj.result = JSON.parse(obj.result)
                            this.dialogObj = obj;
                        },
                        formatData:(val,obj)=>{
                            if (obj.alert_fire){
                                return `<span style="color: #F56C6C;">详情</span>`
                            } else{
                                return `<span style="color: #D6DFEB;">详情</span>`;
                            }
                        }
                    },
                    /*{
                        prop:'tools',
                        label:'操作',
                        width:'',
                        btns:[
                            {
                                icon:'el-icon-camera',
                                text:'查看快照',
                                clickFun:(row,index)=>{
                                    this.dialogTitle = row.time;
                                    this.snapshotDialog = true
                                    // this.detailAlarm(row.alert_id)
                                }
                            },
                        ]
                    }*/
                ],
                tableData:[],
                dateVal:[],
                param:{
                    starttime:'',
                    endtime:''
                },
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
                    }]
                },
                dialogObj:{
                    alert_name:'',
                    alert_conditions:'[{"field":"value","operator":">","value":"2000"}]',
                    pre_index_name:'winlog',
                    suffix_index_name:'*',
                    alert_exec_cycle:'',
                    alert_time:'',
                    alert_exec_type:'',
                    alert_time_type:'',
                    alert_assetGroup_name:'',
                    alert_equipment_name:'lihaaa-pc',
                    msg:'[]'
                }

            }
        },
        created(){
            //填充时间
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * this.$store.state.beforeDay);
            this.dateVal = [dateFormat('yyyy-mm-dd HH:MM:SS',start),dateFormat('yyyy-mm-dd',end)+' 23:59:59']
            this.param.endtime= this.dateVal[1];
            this.param.starttime=this.dateVal[0];
            this.getAlarmExecute(this.param,1);
        },
        methods:{
            //设置告警条件
            setAlertCondition(val){
                let str = '';
                let arr = JSON.parse(val);
                for(let i in arr){
                    if(arr[i].operator !== 'is one of' && arr[i].operator !== 'is not one of'){
                        str += `${arr[i].field} ${arr[i].operator} ${arr[i].value}; `
                    }else{
                        str += `${arr[i].field} ${arr[i].operator} ${arr[i].values}; `
                    }
                }
                return str
            },
            //设置告警执行周期
            setAlertExcute(obj){
                let str = '';
                if(obj.alert_exec_type === 'simple'){//简单
                    let type = ''
                    if(obj.alert_time_cycle_type === 'second'){
                        type = '秒'
                    }else if(obj.alert_time_cycle_type === 'minute'){
                        type = '分钟'
                    }else if(obj.alert_time_cycle_type === 'hour'){
                        type = '小时'
                    }
                    return `${obj.alert_time_cycle_num}${type}`
                }else{//复杂
                    return obj.alert_cron
                }
            },
            //设置告警时间周期
            setAlertTime(obj){
                let time = '';
                let val = obj.alert_time
                if(obj.alert_exec_type === 'simple'){//简单
                    let type = ''
                    if(obj.alert_time_cycle_type === 'second'){
                        type = '秒'
                    }else if(obj.alert_time_cycle_type === 'minute'){
                        type = '分钟'
                    }else if(obj.alert_time_cycle_type === 'hour'){
                        type = '小时'
                    }
                    return `最近${obj.alert_time_cycle_num}${type}`
                }else{//复杂
                    if(obj.alert_time_type === 'range'){//时间范围
                        let arr = val.split(',');
                        if(arr[0] === ''){
                            arr[0] = '*'
                        }
                        if(arr[1] === ''){
                            arr[1] = '*'
                        }
                        time = arr[0]+'至'+ arr[1]
                    }else{ //最近....
                        //time = val
                        if(val === '1-daying'){
                            time = '今天'
                        }else if(val === '1-weeking'){
                            time = '这周'
                        }else if(val === '1-monthing'){
                            time = '本月'
                        }else{
                            let arr = val.split('-');
                            if(arr[1] === 'min'){
                                time ='最近'+arr[0]+'分钟'
                            }else if(arr[1] === 'hour'){
                                time ='最近'+arr[0]+'小时'
                            }else if(arr[1] === 'day'){
                                time ='最近'+arr[0]+'天'
                            }else{
                                time = '时间格式未知'
                            }
                        }
                    }
                    return  time;
                }
            },
            /*刷新*/
            refrshData(){
                this.getAlarmExecute(this.param,1)
                this.c_page = 1;
            },
            /*获取执行结果*/
            getAlarmExecute(searchObj,page){

                this.loading = true;
                let objParam = searchObj;
                objParam.alert_id = this.$route.query.id;
                objParam.pageIndex = page;//当前页
                objParam.pageSize = this.size;//页的条数
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/alert/getAlertList.do',this.$qs.stringify(objParam))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success === 'true'){
                                this.allCounts = Number(obj.data.count);
                                this.tableData = obj.data.list;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*时间改变*/
            timeChange(){
                this.param.endtime= this.dateVal[1];
                this.param.starttime=this.dateVal[0];
                this.getAlarmExecute(this.param,1);
            },
            /*分页页码改变*/
            handleCurrentChange(page){
                //获取数据
                this.getAlarmExecute(this.param,page);
                //改变标识
                //this.firstGetData = false;
            },
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                vm.$options.name = 'editAlarm'+ to.query.id;
                //修改data参数
                vm.alarmName = to.query.name;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'alarmExecuteDetail'+to.query.id,
                    component:'alarmManage/alarmExecuteDetail.vue',
                    title:'执行详情'
                }
                sessionStorage.setItem('/alarmExecuteDetail'+to.query.id,JSON.stringify(obj))
                if(vm.alarmId === '' || vm.alarmId !== to.query.id){
                    vm.alarmId = to.query.id;
                   // vm.getAlarmExecute();
                }

            })

        },
        components:{
            vBasetable,
            jsonView
        }
    }
</script>

<style scoped>
    .data-box{
        float: right;
        margin-right:5px;
        color: #409eff;
        text-shadow: initial;
    }
    .btn-wapper{
        float: right;
        margin-right: 10px;
    }
    .execute-list-wapper{
        padding: 10px;
    }
    .alarm-table-page{
        border-top: 1px solid #303e4e;
        height: 40px;
        display: flex;
        justify-content: flex-end;
        align-items:center;
    }
    .alarm-table-page b{
        color: #e4956d;
    }
    .snapshot-wapper{
        color: #fff;
        height: 550px;
        overflow: auto;
    }
    .snapshot-wapper p{
        margin: 1px;
        background: #394b5f;
        padding: 10px;
        color: #afd4fb;
        display: flex;
    }
    .snapshot-wapper span{
        display: inline-block;
    }
    .snapshot-wapper .tit{
        width: 100px;
        text-align: end;
        margin-right: 20px;
        height: 100%;
        line-height: 22px;
    }
    .snapshot-wapper .txt{
        color: #4ca2fb;
        width: calc(100% - 130px);
    }
</style>
