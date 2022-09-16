<template>
    <div class="content-bg">
        <div class="top-title">精确查询
            <div class="equipemnt-tools-btns">
                <!--<el-button type="primary" size="mini" plain ><a id="eqDownload" @click='downLoadEq'>模板下载</a></el-button>
                <el-button type="warning" size="mini" plain @click="importState = true">日志导入</el-button>-->
            </div>
        </div>
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
        <el-dialog title="日志导入" :visible.sync="importState" width="680px" height="550px" class="dialog-wapper">
            <div class="back-state" v-if="backState">
                <p class="i-box"><i class="el-icon-success" v-if="this.backStateObj.state == 'true'" style="color:#279e72;"></i><i class="el-icon-error" v-else style="color:#e27145;"></i></p>
                <h3 class="back-h3" v-if="this.backStateObj.state == 'true'"  style="color:#279e72;">导入成功</h3>
                <h3 class="back-h3" v-else style="color:#e27145;">导入失败</h3>
                <p class="back-text">{{this.backStateObj.text}}</p>
                <p class="back-btn"><el-button type="primary" size="mini" @click="backState = false">返回导入界面</el-button></p>
            </div>
            <p>通过浏览本地文件或将文件拖到下面指定区域，上传日志文件。</p>
            <p>文件支持的类型：<span class="txtColor">.xlsx</span> <span class="txtColor">.xls</span> <span class="txtColor">.xlsm</span></p>
            <p>文件的名称必须为：<span class="txtColor">日志</span>  (例:日志.xlsx)</p>
            <div style="padding-right: 30px;">
                <input type="file" multiple id="ssi-upload"/>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button @click="importState = false">取 消</el-button>
            </div>
        </el-dialog>
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
                backState:false,//导入结果状态框显示与否
                backStateObj:{
                    text:'',
                    state:'false'
                },//结果状态参数集合
                importState:false,
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
            //日志类型
            this.getLogType();
            //日志级别
            this.setLogLevel('');
            //定义七天时间范围
            let endTime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
            let startTime= new Date();
            startTime.setTime(startTime.getTime() - 3600 * 1000 * 24 * this.$store.state.beforeDay);
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
                        st = this.$route.params.dateArr[0]
                        et = this.$route.params.dateArr[1]
                    }
                    /*else{
                        st = dateFormat('yyyy-mm-dd',this.$route.params.dateArr[0])+ ' 00:00:00'
                        et = dateFormat('yyyy-mm-dd',this.$route.params.dateArr[1])+ ' 23:59:59'
                    }*/
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

                //设置日志级别
                let logTypeStr = ''
                logTypeStr = params.join(',')
                this.setLogLevel(logTypeStr)
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
            },
            /*导入框状态*/
            'importState'(){
                if(this.importState === true){
                    setTimeout(()=>{
                        let $that = this;
                        $('#ssi-upload').ssi_uploader({
                            url:this.$baseUrl+'/log/insertLog.do',//地址
                            maxNumberOfFiles:1,
                            allowed:['xlsx','xls','xlsm'],//允许上传文件的类型
                            ajaxOptions: {
                                success: function(res) {
                                    let obj = JSON.parse(res)
                                    $that.backState = true;
                                    $that.backStateObj ={
                                        state:obj.success,
                                        text:obj.message
                                    }
                                },
                                error:function(data){
                                    layer.msg("导入失败",{icon: 5});
                                    $(".ssi-abortUpload").click()
                                }
                            }
                        })
                    },100)

                }
            }
        },
        methods:{
            /*下载日志模板*/
            downLoadEq(){
                let ahtml = document.getElementById('eqDownload');
                ahtml.href = this.$baseUrl+'/log/logTemplateFileDownload.do';
                ahtml.click();
            },
            /*日志导入*/
            importLogs(){
               /* layer.confirm('此功能适用于实施人员，请谨慎操作。', {
                    btn: ['确定导入','取消'] //按钮
                }, (index)=> {
                    layer.close(index);

                }, function(){
                    layer.close();
                })*/
                this.importState = true
            },
            //获得日志类型
            getLogType(){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/log/getLogTypeComboxByPage.do',this.$qs.stringify({
                        pageType:'search'
                    }))
                        .then((res)=>{
                            // this.logType.push({value:'',label:'全部'});
                            for (let i in  res.data.data) {
                                this.logType.push( res.data.data[i]);
                            }
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            },
            /*改变日志级别*/
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
                                this.formConditionsArr[2].options = this.logLevel

                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.loading = false;
                        })
                })
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
                                //下载到本地
                                const link = document.createElement("a"); //自己创建的a标签
                                let url = this.$baseUrl+'/log/logFileDownload.do';
                                link.href = url;
                                document.body.appendChild(link);
                                link.click();
                                document.body.removeChild(link);
                                window.URL.revokeObjectURL(url);

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
