<template>
    <div class="content-bg">
        <div class="top-title">数据库日志查询
            <div class="equipemnt-tools-btns">
<!--                <el-button type="primary" size="mini" plain ><a id="eqDownload" @click='downLoadEq'>模板下载</a></el-button>-->
<!--                <el-button type="warning" size="mini" plain @click="importState = true">日志导入</el-button>-->
            </div>
        </div>
        <div class="search-wapper">
            <v-search-form :formItem="formConditionsArr" :busName="busName"></v-search-form>
        </div>
        <div class="content-wapper">
            <v-logscontent2 :searchConditions="searchConditions" :tableHead="tableHead" :searchUrl="searchUrl"  :layerObj="layerObj" ref="logContent" :moreDeleteBtn="true"></v-logscontent2>
        </div>
    </div>
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vLogscontent2 from '@/components/logsManage/logsContent2';
    import bus from '../common/bus';
    import {dateFormat} from "../../../static/js/common";
    export default {
        name: "databaseLogSearch",
        data(){
            return{
                busName:'databaseLogSearch',
                searchConditions:{},
                searchUrl:'log/getDBLogList.do',//数据地址
                formConditionsArr:[],//查询条件
                tableHead:[],//表头列
                layerObj:{
                    detailData:{},//弹窗数据
                    layerState:false//弹窗状态
                },
                logType:[{label:'mysql(数据库)',value:'mysql'},{label:'oracle(数据库)',value:'oracle'},{label:'sqlserver(数据库)',value:'sqlserver'}]
            }
        },
        created() {
            //表头
            this.tableHead = [
                {
                    prop:'@timestamp',
                    label:'时间',
                    width:'155'
                },
                {
                    prop:'fields.db_type',
                    label:'日志类型',
                    width:'100'
                },
                {
                    prop:'fields.equipmentname',
                    label:'资产名称',
                    width:'125',
                    clickFun:(item)=>{
                        if(item.equipmentid ==='unknown'){
                            layer.msg("资产未知",{icon:5})
                        }else{
                            //跳转到资产列表页面
                            this.$router.push({name:'equipment2',params:{equipmentid: item.fields.equipmentid}})
                        }
                    }
                },
                {
                    prop:'user',
                    label:'数据库账号',
                    width:'130'
                },
                {
                    prop:'ip',
                    label:'数据库IP',
                    width:'125'
                },
                {
                    prop:'cmd',
                    label:'操作类型',
                    width:'70'
                },
                {
                    prop:'rows',
                    label:'影响行数',
                    width:'70'
                },
                {
                    prop:'status',
                    label:'执行状态码',
                    width:'80'
                },
                {
                    prop:'query',
                    label:'SQL语句',
                    width:''
                },
                {
                    prop:'tools',
                    label:'操作',
                    width:'60',
                    btns:[
                        {
                            icon:'el-icon-tickets',
                            text:'查看详情',
                            btnType: 'readDetails',
                            clickFun:(row,index)=>{
                                //console.log(this.layerObj
                                /*if(!row.agent.type){
                                    console.log('dddd')

                                }
                                console.log(row)*/
                                row.agent = {}
                                row.agent.type = row.fields.db_type
                                this.layerObj.layerState = true;
                                this.layerObj.detailData = row;
                            }
                        },
                        {
                            icon:'el-icon-circle-close',
                            text:'删除',
                            btnType: 'removeItem',
                            clickFun:(row,index)=>{
                                this.$refs.logContent.removeParams([row])
                            }
                        }
                    ]
                }
            ]
            //this.getLogType();
            //定义七天时间范围
            let endTime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
            let startTime= new Date();
            startTime.setTime(startTime.getTime() - 3600 * 1000 * 24 * this.$store.state.beforeDay);
            startTime = dateFormat('yyyy-mm-dd HH:MM:SS',startTime);
            this.searchConditions = {
                endtime: endTime,
                starttime: startTime,
                'fields.db_type':'',
                ip:'',
                cmd:'',
                'fields.equipmentname':'',

            },
                //检索条件
                this.formConditionsArr = [
                    {
                        label:'时间范围',
                        type:'datetimerange',
                        itemType:'',
                        paramName:'time',
                        model:{
                            model:[startTime,endTime]
                        },
                        val:''
                    },
                    {
                        label:'日志类型',
                        paramName:'fields.db_type',
                        type:'select',
                        itemType:'multiple',
                        model:{
                            model:''
                        },
                        options:this.logType
                    },
                    {
                        label:'操作类型',
                        paramName:'cmd',
                        type:'select',
                        itemType:'',
                        model:{
                            model:''
                        },
                        options:[{label:'select',value:'select'},{label:'update',value:'update'},{label:'insert',value:'insert'},{label:'delete',value:'delete'}]
                    },
                    {
                        label:'数据库IP',
                        paramName:'ip',
                        itemType:'',
                        model:{
                            model:''
                        },
                        type:'input'
                    },
                    {
                        label:'资产名称',
                        paramName:'fields.equipmentname',
                        model:{
                            model:''
                        },
                        itemType:'',
                        type:'input'
                    }

                ]
            //检测搜索条件
            bus.$on(this.busName,(params)=>{
                this.searchConditions = params;
            })
        },
        beforeDestroy(){
            bus.$off(this.busName);
            bus.$off('logTypeChange');
            bus.$off('exportStart');
            bus.$off('exportFail');
        },
        methods:{
            //获得日志类型
            getLogType(){
                this.$nextTick(()=>{
                    this.$axios.get('static/filejson/logTypeLevel2.json',{})
                        .then((res)=>{
                            this.logBaseJson = res.data;
                            // this.logType.push({value:'',label:'全部'});
                            for(let i=0;i<res.data.length; i++){
                                let obj = {
                                    value:res.data[i].type,
                                    label:res.data[i].type
                                };
                                this.logType.push(obj);
                            }
                            //设置日志级别  默认显示syslog日志级别
                            this.setLogLevel('syslog')
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            }
        },
        components:{
            vSearchForm,
            vLogscontent2
        }
    }
</script>

<style scoped>
    .top-title{
        font-size: 18px;
        font-weight: 600;
        padding-left: 20px;
        height: 50px;
        line-height: 50px;
        margin-bottom: 20px;
        color: #e4956d;
        text-shadow: 3px 4px 3px #4e7ba9;
        border-bottom: 1px solid #303e4e;
    }
    .equipemnt-tools-btns{
        float: right;
        margin-right: 10px;
    }
    .search-wapper{
        display: flex;
        justify-content: center;
        margin-bottom: 20px;
    }
    .input-searchword{
        width: 300px;
    }
    .content-wapper{
        height: 600px;
        padding: 0 10px 20px 10px;
        /*border:2px solid #303e4e;*/
    }
    .content-wapper>div{
        height: 100%;
    }
    .export-progress-box{
        position:absolute;
        top:35px;
        /*right:5px;*/
        display: flex;
        transition: all .2s;
        font-size: 13px;
    }
    .export-progress-wapper{
        width:50px;
        height:55px;
        border:2px solid #fff;
        border-bottom:0;
        color:#fff;
        background: #00bcd4;
        border-top:0;
        margin:0 30px;
    }
    .progress-name{
        width:46px;
        height:30px;
        text-align:center;
        /* line-height:30px; */
        line-height: 14px;
        word-break: break-all;
    }
    .progress-val{
        width:50px;
        height:50px;
        position:relative;
        left:-2px;
        text-align:center;
        line-height:50px;
        border-radius: 100%;
        border:2px solid #fff;
        font-weight:600;
        font-size:14px;
        background: #00bcd4;

    }
    .dialog-wapper p{
        color: #fff;
        line-height: 30px;
    }
    .dialog-wapper p span{
        color: #4da5ff;
    }
    .back-state{
        width: 100%;
        height: 82%;
        background: #303e4e;
        position: absolute;
        left: 0;
        top: 55px;
        z-index: 100;
    }
    .i-box{
        text-align: center;
        font-size: 50px;
        padding: 35px;
        padding-bottom: 5px;
    }
    .back-h3{
        text-align: center;
        font-size: 38px;
    }
    .back-text{
        height: 275px;
        margin: 0 20px;
        border: 1px solid #5e738c;
        margin-top: 10px;
        padding: 10px;
    }
    .back-btn{
        width: 100%;
        height: 50px;
        text-align: center;
        position: absolute;
        bottom: 0;
    }
</style>
