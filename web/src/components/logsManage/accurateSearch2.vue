<template>
    <div class="content-bg">
        <div class="top-title">精确查询</div>
        <div class="search-wapper">
            <v-search-form :formItem="formConditionsArr" :busName="busName"></v-search-form>
        </div>
        <div class="content-wapper">
            <v-logscontent2 :searchConditions="searchConditions" :tableHead="tableHead" :searchUrl="searchUrl" :exportBtn="true" :layerObj="layerObj" ref="logContent" :moreDeleteBtn="true"></v-logscontent2>
        </div>
        <div class="export-progress-box"  :style="progressRight">
            <div class="export-progress-wapper">
                <div class="progress-name" v-html="progressTitle"></div>
                <div class="progress-val">{{progressVal}}</div>
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
        name: "accurateSearch2",
        data(){
            return{
                layerObj:{
                    detailData:{},//弹窗数据
                    layerState:false//弹窗状态
                },
                busName:'accurateSearch2',
                words:'',//检索条件
                searchConditions:{},
                searchUrl:'ecsWinlog/getLogListByBlend.do',//数据地址
                formConditionsArr:[],//查询条件
                tableHead:[],//表头列
                firstSearch:true,//第一次查询标识（创建分页）
                logBaseJson:[],
                logType:[],//日志类型
                logLevel:[],//日志级别
                levelVal:'',//级别内容
                normalizationVal:'true',//完整范式化
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
                ],
                progressRight:'right:-90px;',//导出日志进度框右偏移量
                progressTitle:'导出中<br/><i class="el-icon-loading"></i>',
                progressVal:'',
                exportInternal:''
            }
        },
        created(){
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
                    prop:'agent.type',
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
                            btnType: 'readDetails',
                            clickFun:(row,index)=>{
                                //console.log(this.layerObj)
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
            this.getLogType();
            //定义七天时间范围
            let endTime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
            let startTime= new Date();
            startTime.setTime(startTime.getTime() - 3600 * 1000 * 24 * 7);
            startTime = dateFormat('yyyy-mm-dd HH:MM:SS',startTime);
            this.searchConditions = {
                normalization:'true',
                'agent.type': "",
                endtime: endTime,
                'fields.ip': "",
                'fields.equipmentname': "",
                'log.level': "",
                starttime: startTime
            },
            //检索条件
            this.formConditionsArr = [
                {
                    label:'事件范围',
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
                    paramName:'agent.type',
                    type:'select',
                    itemType:'multiple',
                    model:{
                        model:''
                    },
                    options:this.logType
                },
                {
                    label:'日志级别',
                    paramName:'log.level',
                    type:'select',
                    itemType:'multiple',
                    model:{
                        model:this.levelVal
                    },
                    options:this.logLevel
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
                }

            ]
            //判断是否是初始有条件查询 （首页跳转查询） 设置日志级别
            if (this.$route.params.logLevel) {
                let st = ''
                let et = ''
                if(this.$route.params.dateArr){
                    if(this.$route.params.dateArr[0].length){
                        st = this.$route.params.dateArr[0]+ ' 00:00:00'
                        et = this.$route.params.dateArr[1]+ ' 23:59:59'
                    }else{
                        st = dateFormat('yyyy-mm-dd',this.$route.params.dateArr[0])+ ' 00:00:00'
                        et = dateFormat('yyyy-mm-dd',this.$route.params.dateArr[1])+ ' 23:59:59'
                    }
                }
                this.formConditionsArr[0].model.model=[st,et]
                this.formConditionsArr[2].model.model=[this.$route.params.logLevel];
                this.searchConditions['log.level']=this.$route.params.logLevel;
                this.searchConditions.starttime = st;
                this.searchConditions.endtime = et;
                //清空日志类型
                this.formConditionsArr[1].model.model = ''
                this.searchConditions['agent.type'] = ''
            }
            //检测搜索条件
            bus.$on(this.busName,(params)=>{
                this.searchConditions = params;
            })
            //检测日志类型改变
            bus.$on('logTypeChange',(params)=>{
                //默认多选或者不选 显示syslog的日志级别
                let type ='syslog';
                //判断选中是否只是一个
                if(params.length === 1){
                   type = params[0]
                }
                //设置日志级别
                this.setLogLevel(type)

            })
            //获取本地是否有导出任务
            //有
            if(localStorage.getItem('exportLogs')){
                this.progressRight = 'right:5px;';
                this.progressTitle = '导出中<br/>...';
                //开启定时器 查看导出进度
                this.exportInternal = setInterval(this.updataProgressValue,1000);
            };
            //检测导出日志进度
            bus.$on('exportStart',(params)=>{
                this.progressRight = 'right:5px;';
                //开启定时器 查看导出进度
                this.exportInternal = setInterval(this.updataProgressValue,1000);
            })
            //检测导出失败
            bus.$on('exportFail',(params)=>{
                this.progressRight = 'right:-90px;'
                layer.msg('导出失败',{icon: 5});
                clearInterval(this.exportInternal);
                //删除本地保存的导出任务
                localStorage.removeItem('exportLogs');
            })
        },
        beforeDestroy(){
            bus.$off(this.busName);
            bus.$off('logTypeChange');
            bus.$off('exportStart');
            bus.$off('exportFail');
        },
        watch: {
            '$route' (to, from) { //监听路由是否变化
                if (this.$route.params.logLevel) {
                    let st = ''
                    let et = ''
                    if(this.$route.params.dateArr){
                        if(this.$route.params.dateArr[0].length){
                            st = this.$route.params.dateArr[0]+ ' 00:00:00'
                            et = this.$route.params.dateArr[1]+ ' 23:59:59'
                        }else{
                            st = dateFormat('yyyy-mm-dd',this.$route.params.dateArr[0])+ ' 00:00:00'
                            et = dateFormat('yyyy-mm-dd',this.$route.params.dateArr[1])+ ' 23:59:59'
                        }
                    }
                    this.formConditionsArr[2].model.model=[this.$route.params.logLevel];
                    this.formConditionsArr[0].model.model=[st,et]
                    this.searchConditions['log.level']=this.$route.params.logLevel;
                    this.searchConditions.starttime = st;
                    this.searchConditions.endtime = et;
                    //清空日志类型
                    this.formConditionsArr[1].model.model = ''
                    this.searchConditions['agent.type'] = ''
                }
            }
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
            },
            /*改变日志级别*/
            setLogLevel(logType){
                this.logLevel.length = 0;
                this.levelVal = '';
                for(let i =0;i<this.logBaseJson.length;i++){
                    if(this.logBaseJson[i].type == logType){
                        for(let j=0;j< this.logBaseJson[i].level.length;j++){
                            let obj = {
                                value:this.logBaseJson[i].level[j],
                                label:this.logBaseJson[i].level[j]
                            };
                            this.logLevel.push(obj);
                        }
                    }
                }
            },
            /*获取导出日志进度*/
            updataProgressValue(){
                this.$nextTick(()=>{
                    this.$axios.get(this.$baseUrl+'/log/getExportProcess.do',{})
                        .then((res)=>{
                            //导出完成
                            if(res.data[0].state === 'finished'){
                                //清楚定时器
                                clearInterval(this.exportInternal);
                                //更改显示状态
                                this.progressTitle = '导出<br/>完成';
                                this.progressVal = res.data[0].value;
                                //延时隐藏进度条
                                setTimeout(()=>{
                                    this.progressRight = 'right:-90px;'
                                },5000)
                                //延时清空进度条数据
                                setTimeout(()=>{
                                    this.progressTitle = '导出中<br/>...';
                                    this.progressVal = '';
                                },5300)
                                //导出成功弹窗
                                layer.msg('导出成功',{icon:1});
                                //删除本地保存的导出任务
                                localStorage.removeItem('exportLogs');
                            }else{ //导出未完成 更新进度
                                this.progressVal = res.data[0].value;
                            }
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
</style>
