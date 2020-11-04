<template>
    <div class="content-bg" v-loading="loading" element-loading-background="rgba(48, 62, 78, 0.5)">
        <div class="top-title">告警
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
            </div>
        </div>
        <div class="alert-wapper">
            <el-collapse style="width: 100%;display: flex;flex-wrap: wrap" v-model="activeNames">
                <!--<el-collapse-item v-for="(litem,li) in leftAlertData" :key="li" class="alert-item" :name="li">
                    <template slot="title">
                        <div class="tit-wapper" @click="getExecuteList(litem)">
                            <div class="i-wapper">
                                <i class="el-icon-warning"></i>
                            </div>
                            <div class="txt-wapper">
                                <p class="txt-top">{{litem.alert_name}}</p>
                                <p class="txt-bottom">{{setAlertCondition(litem.alert_conditions)}}</p>
                            </div>
                            <div class="count-wapper">
                                告警次数: <b>{{litem.alert_counts}}</b>
                            </div>
                        </div>
                    </template>
                    <p style="margin: 10px 0;color: #e4956d;">执行详情</p>
                    <div class="execute-wapper">
                        <div v-for="(lexecuteItem,lindex) in litem.execute_list " :key="lindex" class="execute-item">
                            <span class="execute-time">{{lexecuteItem.time}}</span>
                            <span class="execute-msg">{{lexecuteItem.msg}}</span>
                            <span class="execute-btn" @click="showDialog(lexecuteItem)">快照详情</span>
                        </div>
                    </div>
                </el-collapse-item>-->
                <div class="left-wapper" style="width: 50%">
                    <el-collapse-item v-for="(litem,li) in leftAlertData" :key="li" class="alert-item" :name="li">
                        <template slot="title">
                            <div class="tit-wapper" @click="getExecuteList(litem)">
                                <div class="i-wapper">
                                    <i class="el-icon-warning"></i>
                                </div>
                                <div class="txt-wapper">
                                    <p class="txt-top">{{litem.alert_name}}</p>
                                    <p class="txt-bottom"> 告警条件:{{setAlertCondition(litem.alert_conditions)}}</p>
                                </div>
                                <div class="count-wapper">
                                    告警次数: <b>{{Number(litem.fire_count)}}</b>
                                </div>
                            </div>
                        </template>
                        <p style="margin: 10px 0;color: #e4956d;">执行详情（快照）</p>
                        <div class="execute-wapper">
                            <div v-for="(lexecuteItem,lindex) in litem.fire_list " :key="lindex" class="execute-item">
                                <span class="execute-time">{{lexecuteItem.run_time}}</span>
                                <span class="execute-msg">{{lexecuteItem.result}}</span>
                                <span class="execute-btn" @click="showDialog(lexecuteItem)">快照详情</span>
                            </div>
                        </div>
                    </el-collapse-item>
                </div>
                <div class="right-wapper" style="width: 50%">
                    <el-collapse-item v-for="(ritem,ri) in rightAlertData" :key="ri" class="alert-item">
                        <template slot="title">
                            <div class="tit-wapper" @click="getExecuteList(ritem)">
                                <div class="i-wapper">
                                    <i class="el-icon-warning"></i>
                                </div>
                                <div class="txt-wapper">
                                    <p class="txt-top">{{ritem.alert_name}}</p>
                                    <p class="txt-bottom">告警条件:{{setAlertCondition(ritem.alert_conditions)}}</p>
                                </div>
                                <div class="count-wapper">
                                    告警次数: <b>{{Number(ritem.fire_count)}}</b>
                                </div>
                            </div>
                        </template>
                        <p style="margin: 10px 0;color: #e4956d;">执行详情（快照）</p>
                        <div class="execute-wapper">
                            <div v-for="(rexecuteItem,rindex) in ritem.fire_list " :key="rindex" class="execute-item">
                                <span class="execute-time">{{rexecuteItem.run_time}}</span>
                                <span class="execute-msg">{{rexecuteItem.result}}</span>
                                <span class="execute-btn" @click="showDialog(rexecuteItem)">快照详情</span>
                            </div>
                        </div>
                    </el-collapse-item>
                </div>

            </el-collapse>
        </div>
        <el-dialog title="快照详情" :visible.sync="snapshotDialog" width="800px">
            <div class="snapshot-wapper">
<!--                <jsonView :data="this.tableHead" theme="one-dark" :line-height="20" :deep="5" class="jsonView"></jsonView>-->
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
    import jsonView from 'vue-json-views'
    import {dateFormat} from  '../../../static/js/common.js'
    export default {
        name: "alert",
        data(){
            return{
                activeNames:[],
                loading:false,
                snapshotDialog:false,
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
                    }, {
                        text: '全部',
                        onClick(picker) {
                            picker.$emit('pick', ['', '']);
                        }
                    }]
                },
                leftAlertData:[],
                rightAlertData:[ ],
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
            this.dateVal = [dateFormat('yyyy-mm-dd HH:MM:SS',start),dateFormat('yyyy-mm-dd HH:MM:SS',end)]
            this.param.endtime= this.dateVal[1];
            this.param.starttime=this.dateVal[0];
            this.getAlertData(this.param);
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
            /*时间改变*/
            timeChange(){
                this.activeNames=[]
                this.param.endtime= this.dateVal[1];
                this.param.starttime=this.dateVal[0];
                this.getAlertData(this.param);
            },

            /*获取告警*/
            getAlertData(param){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/alert/getAlertFireInfo.do',this.$qs.stringify(param))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success === "true"){
                                this.leftAlertData = [];
                                this.rightAlertData = [];
                                obj.data.forEach((item,i) =>{
                                    if(i % 2 === 0){
                                        this.leftAlertData.push(item)
                                    }else{
                                        this.rightAlertData.push(item)
                                    }
                                })
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*获取快照*/
            getExecuteList(item){
                if(item.fire_list.length === 0){//第一次加载 请求数据
                    this.$nextTick(()=>{
                        this.loading = true;
                        this.$axios.post(this.$baseUrl+'/alert/getAlertFireList.do',this.$qs.stringify({
                            alert_id:item.alert_id,
                            starttime:this.dateVal[0],
                            endtime:this.dateVal[1]
                        }))
                            .then(res=>{
                                this.loading = false;
                                let obj = res.data;
                                if(obj.success === "true"){
                                    item.fire_list = obj.data;
                                }else{
                                    layer.msg(obj.message,{icon:5})
                                }
                            })
                            .catch(err=>{
                                this.loading = false;
                            })
                    })
                }
            },
            /*快照弹窗*/
            showDialog(item){
                this.snapshotDialog = true
                item.result = JSON.parse(item.result)
                this.dialogObj = item;
            }
        },
        components:{
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
    .alert-wapper{
        padding: 20px;
        /*display: flex;*/
        /*flex-wrap: wrap;*/
    }
    .alert-item{
        /*width: calc(50% - 20px);*/
        margin: 20px 10px;
        /*float: left;*/
        border-radius: 5px;
        overflow: hidden;
    }
    .alert-wapper /deep/ .el-collapse-item__header {
       /* background: #ffd4d4;
        color: #f56c6c;
        border-top: 1px solid #f56c6c;
        height: 80px;*/
        background: #A84250;
        border-top: 10px solid #3e536e;
        border-bottom: 10px solid #3e536e;
        height: 80px;
        color: #FEEAE9;
    }
    .alert-wapper /deep/ .el-collapse-item__wrap{
       /*border-bottom: 1px solid #f56c6c;*/
        background: #3E536E;
   }
    .tit-wapper{
        display: flex;
        width: 100%;
        position: relative;
    }
    .tit-wapper p{
        height: 40px;
        line-height: 40px;
    }
    .tit-wapper .i-wapper{
        width: 60px;
        font-size: 40px;
        text-align: center;
    }
    .tit-wapper .txt-top{
        font-size: 19px;
    }
    .tit-wapper .txt-bottom{
        height: 32px;
        line-height: 32px;
        color: #fb9593;
    }
    .count-wapper{
        position: absolute;
        right: 20px;
        color: #fb9593;
    }
    .count-wapper b{
        font-size: 20px;
        color: #fff;
    }
    .alert-wapper .execute-wapper{
        max-height: 200px;
        overflow: auto;
        /*margin-top: 20px;*/
        margin-right: 20px;
        background: #496180;
        padding: 10px;
    }
    .execute-item{
        border-bottom:1px dashed #3e536e;
        padding: 3px ;
        color: #ff8394;
        display: flex;
    }
    .execute-item:hover{
        background:#5e7998;
    }
    .execute-item>span{
        display: inline-block;
    }
    .execute-time{
        width: 150px;
    }
    .execute-msg{
        width: calc(100% - 220px);
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
        word-break: break-all;
    }
    .execute-btn{
        width: 56px;
        color: #409eff;
    }
    .execute-btn:hover{
        cursor: pointer;
        color: #fb9593;
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
