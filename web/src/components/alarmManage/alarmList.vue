<template>
    <div  class="content-bg" v-loading="loading" element-loading-background="rgba(48, 62, 78, 0.5)">
        <div class="top-title">告警管理
            <div class="btn-wapper">
                <el-button type="danger" size="mini" plain :disabled="delectAlarmIds.length > 0 ? false : true " @click="deleteAlarm">删除</el-button>
                <el-button type="primary" size="mini" plain @click="goToAddAlarm">添加</el-button>
                <el-button type="success" size="mini" plain  @click="refresh">刷新</el-button>
            </div>
        </div>
        <div class="search-wapper">
            <v-search-form :formItem="formConditionsArr" :busName="busName"></v-search-form>
        </div>
        <div class="alarm-list-wapper">
            <vBasetable :selection="true" :tableHead="tableHead" :tableData="tableData" :busName="tableBusName"></vBasetable>
        </div>
        <div class="alarm-table-page">
            <span>共检索到告警数量为 <b>{{allCounts}}</b> 个</span>
            <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="size" :total="allCounts"></el-pagination>
        </div>
    </div>
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vBasetable from '../common/Basetable2';
    import {jumpHtml} from "../../../static/js/common";
    import bus from '../common/bus';
    export default {
        name: "alarmList",
        data(){
            return{
                loading:false,
                page:1,//页码
                c_page:1,//当前页码
                size:15,//每页的数量
                busName:'searchAlarm',
                tableBusName:{
                    selectionName:'alarmDelect'
                },
                allCounts:0,
                tableHead:[],
                tableData:[],
                formConditionsArr:[
                    {
                        label:'名称',
                        paramName:'alert_name',
                        model:{
                            model:''
                        },
                        itemType:'',
                        type:'input'
                    }
                ],
                conditionFrom:{
                    alert_name:''
                },
                delectAlarmIds:'',//选中删除的
            }
        },
        created(){
            this.tableHead=[
                {
                    prop:'alert_name',
                    label:'名称',
                    width:''
                },
                {
                    prop:'template_name',
                    label:'数据源',
                    width:'',
                    formatData:(val,obj)=>{
                        return obj.pre_index_name+obj.suffix_index_name
                    }
                },
                {
                    prop:'alert_conditions',
                    label:'告警条件',
                    width:'',
                    formatData:(val,obj)=>{
                        let str = '';
                        let arr = JSON.parse(val);
                        for(let i in arr){
                            if(arr[i].operator !== 'is one of' && arr[i].operator !== 'is not one of'){
                                str += `${arr[i].field} ${arr[i].operator}<b> ${arr[i].value}</b>; `
                            }else{
                                str += `${arr[i].field} ${arr[i].operator}<b> ${arr[i].values}</b>; `
                            }
                        }
                        return str
                    }
                },
                {
                    prop:'alert_exec_cycle',
                    label:'执行周期',
                    width:''
                },
                {
                    prop:'alert_time',
                    label:'时间范围',
                    width:'',
                    formatData:(val,obj)=>{
                        let time = '';
                        /*//判断时间类型
                        if(obj.alert_exec_type === 'complex'){
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
                        }else{
                            time = obj.alert_time_range;
                        }*/
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
                        return time
                    }
                },
                {
                    prop:'alert_state_boolean',
                    label:'状态',
                    width:'80',
                    formatData:(val,obj)=>{
                        let str = ''
                        if(val){
                           // str = '<el-switch v-model="aa" active-color="#13ce66" inactive-color="#ff4949"> </el-switch>'
                            str='<label><input class="mui-switch" type="checkbox" checked title="执行中" style="cursor: pointer"></label>'
                        }else{
                            str='<label><input class="mui-switch mui-switch-anim" type="checkbox" title="已停止" style="cursor: pointer"></label>'
                        }
                        return str
                    },
                    clickFun:(item,i)=>{
                        this.setAlarmState(item,i)
                    }
                },
                {
                    prop:'alert_note',
                    label:'说明',
                    width:''
                },
                {
                    prop:'tools',
                    label:'操作',
                    width:'',
                    btns:[
                        {
                            icon:'el-icon-edit',
                            text:'修改',
                            clickFun:(row,index)=>{
                                this.goToReviseAlarm(row,index)
                            }
                        },
                        {
                            icon:'el-icon-tickets',
                            text:'查看执行详情',
                            clickFun:(row,index)=>{
                                this.detailAlarm(row,index)
                            }
                        },
                        {
                            icon:'el-icon-caret-right',
                            text:'立即执行一次',
                            clickFun:(row,index)=>{
                                this.startAlarm(row.alert_id)
                            }
                        },
                    ]
                }
            ]

            this.getAlarmList(this.conditionFrom,1)
            bus.$on(this.busName,(params)=>{
                this.conditionFrom = params;
                this.getAlarmList(this.conditionFrom,1)
                this.c_page = 1;
            })
            //监听选中的资产
            bus.$on(this.tableBusName.selectionName,(params)=>{
                this.delectAlarmIds = '';
                for(let i in params){
                    this.delectAlarmIds += params[i].alert_id +','
                }
                //console.log(this.delectAlarmIds)
            })
        },
        beforeDestroy(){
            bus.$off(this.busName)
        },
        methods:{
            /*添加告警*/
            goToAddAlarm(){
                this.$router.push({path:'addAlarm'});
            },
            /*刷新*/
            refresh(){
                this.c_page = 1;
                this.getAlarmList(this.conditionFrom,1);
            },
            /*获得告警列表*/
            getAlarmList(searchObj,page){
                this.loading = true;
                let objParam = searchObj;
                objParam.pageIndex = page;//当前页
                objParam.pageSize = this.size;//页的条数
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/alert/getAlertInfoByCondition.do',this.$qs.stringify(objParam))
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
            /*修改告警*/
            goToReviseAlarm(item){
                jumpHtml('editAlarm'+item.alert_id,'alarmManage/editAlarm.vue',{ name:item.alert_name,id:item.alert_id },'修改')
            },
            /*立即执行一次*/
            startAlarm(id){
                //询问框
                layer.confirm('您确定立即执行一次么？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    this.loading = true
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/alert/startOnce.do',this.$qs.stringify({alert_id:id}))
                            .then((res)=>{
                                this.loading = false
                                if(res.data.success == "true"){
                                    layer.msg(res.data.message,{icon:1});
                                }else{
                                    layer.msg(res.data.message,{icon:5});
                                }

                            })
                            .catch((err)=>{
                                this.loading = false;
                                layer.msg("删除失败",{icon:5});
                            })
                    })
                }, function(){
                    layer.close();
                })
            },
            /*删除告警*/
            deleteAlarm(){
                //询问框
                layer.confirm('您确定删除么？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    this.loading = true
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/alert/deletes.do',this.$qs.stringify({
                            alert_ids:this.delectAlarmIds
                        }))
                            .then((res)=>{
                                this.loading = false
                                if(res.data.success == "true"){
                                    layer.msg("删除成功",{icon:1});
                                    this.c_page = 1;
                                    this.getAlarmList(this.conditionFrom,1)
                                }else{
                                    layer.msg(res.data.message,{icon:5});
                                }

                            })
                            .catch((err)=>{
                                this.loading = false;
                                layer.msg("删除失败",{icon:5});
                            })
                    })
                }, function(){
                    layer.close();
                })
            },
            /*查看告警详情*/
            detailAlarm(item){
                jumpHtml('alarmExecuteDetail'+item.alert_id,'alarmManage/alarmExecuteDetail.vue',{ name:item.alert_name,id:item.alert_id },'执行详情')
            },
            /*分页页码改变*/
            handleCurrentChange(page){
               //获取数据
                this.getAlarmList(this.conditionFrom,page);
                //改变标识
                //this.firstGetData = false;
            },
            /*设置告警状态*/
            setAlarmState(item,i){
                let url = ''
                //判断此刻状态
                if(item.alert_state_boolean){//状态：启用中
                    url = '/alert/stopQuartz.do'
                }else{//状态：关闭
                    url = '/alert/startQuartz.do'
                }
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+url,this.$qs.stringify({alert_id:item.alert_id}))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success === 'true'){
                                layer.msg(obj.message,{icon:1})
                                this.tableData[i].alert_state_boolean = !item.alert_state_boolean
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
        components:{
            vBasetable,
            vSearchForm
        }
    }
</script>

<style scoped>
    .btn-wapper{
        float: right;
        margin-right: 10px;
    }
    .alarm-list-wapper{
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
    /deep/ .mui-switch {
        width: 42px;
        height: 21px;
        position: relative;
        border: 1px solid #ff4949;
        background-color: #ff4949;
        box-shadow: #dfdfdf 0 0 0 0 inset;
        border-radius: 20px;
        border-top-left-radius: 20px;
        border-top-right-radius: 20px;
        border-bottom-left-radius: 20px;
        border-bottom-right-radius: 20px;
        background-clip: content-box;
        display: inline-block;
        -webkit-appearance: none;
        user-select: none;
        outline: none; }
    /deep/ .mui-switch:before {
        content: '';
        width: 20px;
        height: 20px;
        position: absolute;
        top: 0px;
        left: 0;
        border-radius: 20px;
        border-top-left-radius: 20px;
        border-top-right-radius: 20px;
        border-bottom-left-radius: 20px;
        border-bottom-right-radius: 20px;
        background-color: #fff;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.4); }
    /deep/ .mui-switch:checked {
        border-color: #64bd63;
        box-shadow: #64bd63 0 0 0 16px inset;
        background-color: #64bd63; }
    /deep/ .mui-switch:checked:before {
        left: 21px; }
    /deep/ .mui-switch.mui-switch-animbg {
        transition: background-color ease 0.4s; }
    /deep/ .mui-switch.mui-switch-animbg:before {
        transition: left 0.3s; }
    /deep/ .mui-switch.mui-switch-animbg:checked {
        box-shadow: #dfdfdf 0 0 0 0 inset;
        background-color: #64bd63;
        transition: border-color 0.4s, background-color ease 0.4s; }
    /deep/ .mui-switch.mui-switch-animbg:checked:before {
        transition: left 0.3s;
    }
    /deep/ .mui-switch.mui-switch-anim {
        transition: border cubic-bezier(0, 0, 0, 1) 0.4s, box-shadow cubic-bezier(0, 0, 0, 1) 0.4s;
    }
    /deep/ .mui-switch.mui-switch-anim:before {
        transition: left 0.3s;
    }
    /deep/ .mui-switch.mui-switch-anim:checked {
        box-shadow: #64bd63 0 0 0 16px inset;
        background-color: #64bd63;
        transition: border ease 0.4s, box-shadow ease 0.4s, background-color ease 1.2s;
    }
    /deep/ .mui-switch.mui-switch-anim:checked:before {
        transition: left 0.3s;
    }

</style>
