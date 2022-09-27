<template>
    <div class="content-bg">
        <div class="top-title">DNS精确查询
        </div>

        <!---查询框 -->
        <div class="search-wapper">
            <v-search-form :formItem="formConditionsArr" :busName="dnsSearchBusName"></v-search-form>
        </div>
        <div class="content-wapper">
            <v-logscontent2 :searchConditions="searchConditions" :tableHead="tableHead" :searchUrl="searchUrl" :layerObj="layerObj" ref="logContent" :moreDeleteBtn="true"></v-logscontent2>
        </div>
        <div v-if="this.layerObj.formDetailsState">
            <div>
                <div id="con" ref="con">
                    <!--<div class="qwe">{{this.de tailsData}}</div>-->
                    <ul class="details-list-wapper">
                        <li v-for="(obj,index) in formDetailColumns" :key="index">
                            <el-row>
                                <el-col :span="6"><div class="grid-content bg-purple details-list-name">{{obj.label}}:</div></el-col>
                                <el-col :span="18"><div class="grid-content bg-purple-light details-list-val" v-html="dataChange(layerObj.formDetailsData,obj.prop)"></div></el-col>
                            </el-row>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vLogscontent2 from '@/components/logsManage/logsContent2';
    import bus from '../common/bus';
    import {dateFormat} from "../../../static/js/common";
    export default {
        name: "dnsSearch",
        data(){
            return{
                formConditionsArr:[],//查询条件
                logType:[],//日志类型
                logLevel:[],//日志级别
                levelVal:'',//级别内容
                dnsSearchBusName:'dnsSearchButton',//查询按钮命名，用于事件绑定
                searchConditions:{},
                tableHead:[],//表头列
                searchUrl:'ecsWinlog/getLogListByBlend.do',//查询数据的地址
                layerObj:{//table的查看详情表单
                    formDetailsData:{},//弹窗数据
                    formDetailsState:false//弹窗状态
                },
                formDetailColumns:[],

                //是否完整范式化
                normalizationVal:'true',//默认完整范式化
                normalizationOpt:[
                    {
                        label:'全部',
                        value:''
                    },{
                        label:'是',
                        value:'true'
                    },{
                        label:'否',
                        value:'false'
                    }
                ]
            }
        },
        created(){

            //定义七天时间范围
            let endTime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
            let startTime= new Date();
            startTime.setTime(startTime.getTime() - 3600 * 1000 * 24 * this.$store.state.beforeDay);
            startTime = dateFormat('yyyy-mm-dd HH:MM:SS',startTime);
            //日志级别
            this.setLogLevel('syslog');
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
                    label:'IP地址',
                    paramName:'fields.ip',
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
                },
                {
                    label:'日志级别',
                    paramName:'log.level',
                    type:'select',
                    itemType:'multiple',
                    model:{
                        model:''
                    },
                    options:this.logLevel
                },
                {
                    label:'日志源',
                    paramName:'syslog.facility_label',
                    width:'70',
                    model:{
                        model:''
                    },
                    itemType:'',
                    type:'input'
                },
                {
                    label:'进程',
                    paramName:'process.program',
                    width:'70',
                    model:{
                        model:''
                    },
                    itemType:'',
                    type:'input'
                },
                {
                    label:'是否完整范式化',
                    paramName:'normalization',
                    width:'70',
                    type:'select',
                    itemType:'',
                    model:{
                        model:this.normalizationVal
                    },
                    options:this.normalizationOpt
                }
            ]
            //检测搜索条件
            bus.$on(this.dnsSearchBusName,(params)=>{
                this.searchConditions = params;
            })
            //查看详情需要显示的字段
            this.formDetailColumns=[{
                label:'时间',
                prop:'@timestamp'
            },{
                label:'日志级别',
                prop:'log.level'
            },{
                label:'资产名称',
                prop:'fields.equipmentname'
            },{
                label:'IP',
                prop:'fields.ip'
            },{
                label:'message',
                prop:'log.level'
            }]
            //表头
            this.tableHead = [
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
                    prop:'syslog.facility_label',
                    label:'日志源',
                    width:'100'
                },
                {
                    prop:'process.program',
                    label:'进程',
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
                    prop:'fields.ip',
                    label:'IP',
                    width:'125'
                },
                {
                    prop:'message',
                    label:'日志内容',
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
                            //btnType: 'readDetails',
                            clickFun:(row,index)=>{
                                //console.log(this.layerObj)
                                //this.layerObj.layerState = true;
                                //this.layerObj.detailData = row;
                                this.layerObj.formDetailsState = true;
                                this.layerObj.formDetailsData = row;
                                //等待页面元素刷新完毕再执行里面的操作
                                this.$nextTick(()=>{

                                    layer.open({
                                        type:'1',
                                        title:'日志详情',
                                        area:['620px','520px'],
                                        content:$('#con').html()
                                    })
                                })

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


        },
        methods:{
            /*改变日志级别,通过请求获取日志级别，并赋值给下拉框的options*/
            setLogLevel(logType){
                this.$nextTick(()=>{
                    // this.loading = true;
                    this.$axios.post(this.$baseUrl+'/log/getLogLevelByLogType.do',this.$qs.stringify({
                        logType:logType
                    }))
                        .then(res=>{
                            //this.loading = false;
                            let obj = res.data;

                            if(obj.success == 'true'){
                                this.logLevel = []
                                for (let i=0;i < obj.data.length;i++){
                                    this.$set(this.logLevel,i,obj.data[i])
                                    //this.logLevel.push(obj.data[i])
                                }
                                this.formConditionsArr[3].options = this.logLevel

                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.loading = false;
                        })
                })
            },
            //数据处理
            dataChange(formDetailData,prop){
                console.info(formDetailData);
                console.info(prop);
                let arr = prop.split('.');
                let currentData = formDetailData;
                for (let i in arr){
                    currentData = currentData[arr[i]];
                }
                console.info(currentData);
                return  currentData;
            },
        },
        components:{
            vSearchForm,
            vLogscontent2
        },
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
    #con{
        display: none;
    }
    .details-list-wapper{
        /*width: 100%;
        height: 100%;
        overflow: auto;*/
        padding: 10px;
        font-size: 14px;
    }
    .details-list-wapper li{
        min-height: 40px;

    }
    .details-list-wapper li:nth-child(even){
        background: #303e4e;
    }
    .details-list-name{
        height: 30px;
        padding-top:10px;
        text-align: center;
    }
    .details-list-val{
        min-height: 20px;
        padding-top: 10px;
        padding-bottom: 10px;
        line-height: 20px;
        word-break: break-all;
    }
    .layui-layer .layui-layer-content{
        background: rgb(26,36,47)!important;
        overflow: auto!important;
        border: 1px solid #303e4e!important;
    }
</style>
