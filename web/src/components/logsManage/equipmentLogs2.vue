<template>
    <div class="content-bg">
        <div class="top-title">'{{equipmentName}}' 日志</div>
        <div class="search-wapper">
            <v-search-form :formItem="formConditionsArr" :busName="busName"></v-search-form>
           <!-- <el-switch
                style="display: block;float: right;margin-left:10px;margin-top: 5px;"
                v-model="actionState"
                active-color="#13ce66"
                inactive-color="#ff4949"
                active-text="开启动作添加"
                inactive-text="">
            </el-switch>-->
        </div>
        <div class="equipment-table">
            <v-basetable2 :tableHead="equipmentHead" :tableData="equipmentData"></v-basetable2>
        </div>
        <div class="content-wapper">
            <v-logscontent2 :searchConditions="searchConditions" :tableHead="tableHead" :searchUrl="searchUrl" :layerObj="layerObj" ref="logContent" :moreDeleteBtn="true"></v-logscontent2>
        </div>
        <div class="action-wapper" ref="actionWapper">
            <h4>添加动作</h4>
            <div class="from-reg">
                <el-form label-width="50px">
                    <el-form-item label="名称">
                        <el-input v-model="action.name" size="mini" placeholder="动作名称"></el-input>
                    </el-form-item>
                    <el-form-item label="特征">
                        <div class="keywords-wapper">
                            <p :title="item" v-for="(item,i) in action.keywords" :key="i">{{item}} <i class="remove-item el-icon-close" @click="removeKeywords(i)"></i></p>
                        </div>
                    </el-form-item>
                </el-form>
            </div>
            <div class="btns-reg">
                <el-button type="primary" size="mini" @click="actionCommit">保存</el-button>
                <el-button type="info" size="mini" @click="actionState = false">取消</el-button>
            </div>
        </div>
    </div>
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vLogscontent2 from '@/components/logsManage/logsContent2';
    import vBasetable2 from '../common/Basetable2'
    import bus from '../common/bus';
    import {dateFormat} from "../../../static/js/common";

    export default {
        name: "equipmentLogs2",
        data(){
            return{
                layerObj:{
                    detailData:{},//弹窗数据
                    layerState:false//弹窗状态
                },
                busName:'',
                equipmentId:'',//资产ID
                equipmentName:'',//资产名称
                equipmentData:[],//资产信息
                searchConditions:{
                    'fields.equipmentid':'',
                    starttime:'',
                    endtime:'',
                    queryParam: '',
                    message
                },
                searchUrl:'ecsCommon/getLogListByEquipment.do',//数据地址
                formConditionsArr:[],//查询条件
                tableHead:[],//表头列
                equipmentHead:[
                    {
                        prop:'name',
                        label:'资产名称',
                        width:''
                    },
                    {
                        prop:'hostName',
                        label:'主机名',
                        width:''
                    },
                    {
                        prop:'type',
                        label:'资产类型',
                        width:'',
                        formatData:(val)=>{
                            let type = '';
                            const str = val.substring(0,2);
                            for (let n in this.typeArr){
                                let obj = this.typeArr[n];
                                if(obj.value == str){
                                    type += obj.label +'-';
                                    for(let j in obj.children){
                                        if(obj.children[j].value == val){
                                            type += obj.children[j].label;
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            return type;
                        }


                    },
                    {
                        prop:'logType',
                        label:'日志类型',
                        width:''
                    },
                    {
                        prop:'ip',
                        label:'IP地址',
                        width:'125'
                    },
                    {
                        prop:'createTime',
                        label:'进入时间',
                        width:'',
                        formatData:(val)=>{
                            if(val !== null){
                                return val.split('.')[0]
                            }
                        }
                    },
                    {
                        prop:'upDateTime',
                        label:'更新时间',
                        width:'',
                        formatData:(val)=>{
                            if(val !== null){
                                return val.split('.')[0]
                            }
                        }
                    },
                    {
                        prop:'endTime',
                        label:'截止时间',
                        width:'',
                        formatData:(val)=>{
                            if(val !== null){
                                return val.split('.')[0]
                            }
                        }
                    },
                    {
                        prop:'startUp',
                        label:'是否启用',
                        width:'',
                        formatData:(val)=>{return val == '1' ? '是' : '否'}
                    }
                ],//资产表格头
                typeArr:[],//资产类型
                firstSearch:true,//第一次查询标识（创建分页）
                logConfig:{},//日志配置信息 （表头等）
                logType:'',//日志类型
                logLevel:[],//日志级别
                levelVal:'',//级别内容
                action:{
                    name:'',
                    keywords:[]
                },
                actionState:false
            }
        },
        created(){
            this.getEquipmentType();
            //定义七天时间范围
            let endTime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
            let startTime= new Date();
            startTime.setTime(startTime.getTime() - 3600 * 1000 * 24 * this.$store.state.beforeDay);
            startTime = dateFormat('yyyy-mm-dd HH:MM:SS',startTime);
            this.searchConditions={
                'fields.equipmentid':'',
                    starttime:startTime,
                    endtime:endTime,
                    queryParam: '',
                    message:''
            }
            //检索条件
            this.formConditionsArr = [
                {
                    label:'日期范围',
                    type:'datetimerange',
                    itemType:'',
                    paramName:'time',
                    model:{
                        model:[startTime,endTime]
                    },
                    val:''
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
                },{
                    label:'日志内容',
                    paramName:'message',
                    itemType:'',
                    model:{
                        model:''
                    },
                    type:'input'
                },

            ]
            //鼠标拖选
            //this.mouseSelectText();
        },
        methods:{
            /*获取资产信息*/
            getEquipmentData(){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/equipment/selectEquipment.do',this.$qs.stringify({id:this.equipmentId}))
                        .then((res)=>{
                            this.equipmentData = res.data;
                            this.logType = this.equipmentData[0].logType;
                            if (this.logType === 'winlog'){
                                this.logType = 'winlogbeat'
                            }
                        })
                        .catch((err)=>{
                            //layer.close(loading);
                        })
                })
            },
            /*获得资产类型数据*/
            getEquipmentType(){
                this.$nextTick(()=>{
                    this.$axios.get('static/filejson/equiptype.json',{})
                        .then((res)=>{
                            this.typeArr = res.data
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            },
            /*设置日志配置*/
            setLogConfig(logType){
                //设置日志级别
                //console.log(logType);
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.get('static/filejson/log-event_config.json','')
                        .then(res=>{
                            layer.closeAll('loading');
                            let logConfig = res.data.log;
                            let logLevelArr = logConfig[logType].level;
                            for(let i in logLevelArr){
                                let obj = {
                                    value:logLevelArr[i],
                                    label:logLevelArr[i]
                                };
                                this.logLevel.push(obj);
                            }
                            //设置表头
                            let headerArr = [];
                            //判断对应日志是否存在表头
                            if(logConfig[logType]){//存在
                                headerArr = logConfig[logType].tableHead;
                            }else{//不存在 加载默认
                                headerArr = [{
                                        "prop":"@timestamp",
                                        "label":"时间",
                                        "width":"150"
                                    },
                                    {
                                        "prop":"fields.ip",
                                        "label":"IP",
                                        "width":"125"
                                    },
                                    {
                                        "prop":"message",
                                        "label":"日志内容",
                                        "width":""
                                    }]
                            }
                            headerArr.push({
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
                                            row.agent.type = 'file';
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
                            })
                            this.tableHead = headerArr;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })

            },
            /*鼠标拖选文本*/
            mouseSelectText(){
                let that = this
                var funcGetSelectText = function(){
                    var txt = '';//初始化
                    if(document.selection){//判断是否是ie浏览器
                        txt =  document.selection.createRange().text;         //ie浏览器
                    }else{
                        txt =  document.getSelection();   //其他浏览器
                    }
                    return txt.toString();
                }
                var container = container || document;
                container.onmouseup = function(){
                    var txt = funcGetSelectText();
                    if(txt)
                    {
                        that.action.keywords.push(txt)
                        window.getSelection().empty()
                    }
                }
            },
            /*删除选中的动作特征*/
            removeKeywords(i){
                this.action.keywords.splice(i,1)
            },
            /*提交动作*/
            actionCommit(){
                //验证信息
                if(this.action.name === ''){
                    layer.msg('动作名称不能为空',{icon:5})
                }else if(this.action.keywords.length === 0){
                    layer.msg('为拖选任何特征',{icon:5})
                }else{
                    let sendObj = {}
                    //获取用户ID
                    sendObj.userId= JSON.parse(localStorage.getItem('LoginUser')).id;
                    //特征
                    let feature='';

                    for(let i=0;i < this.action.keywords.length;i++){
                        feature += this.action.keywords[i]+'@#$' ;
                    }
                    sendObj.feature = feature
                    //日志类型
                    sendObj.type = this.logType;
                    //名称
                    sendObj.name = this.action.name
                    this.$nextTick(()=>{
                        layer.load(1);
                        this.$axios.post(this.$baseUrl+'/action/insert.do',this.$qs.stringify(sendObj))
                            .then(res=>{
                                layer.closeAll('loading');
                                if(res.data.success === "true"){
                                    layer.msg(res.data.message,{icon: 1});
                                    this.action = {
                                        name:'',
                                        keywords:[]
                                    }
                                }else if(res.data.success == "false"){//失败
                                    layer.msg(res.data.message,{icon: 5});
                                }
                            })
                            .catch(err=>{
                                layer.closeAll('loading');

                            })
                    })
                }
            }

        },
        watch:{
            'equipmentId'(){
                //检测搜索条件
                bus.$on(this.busName,(params)=>{
                    let queryObj = {'log.level':params['log.level'],'fields.equipmentid':this.equipmentId}
                    this.searchConditions={
                        starttime:params.starttime,
                        endtime:params.endtime,
                        message:'',
                        queryParam:JSON.stringify(queryObj),
                        'fields.equipmentid':this.equipmentId
                    }
                })
            },
            'logType'(newV,oldV){
                this.setLogConfig(newV);
            },
            'actionState'(){
                //清空数据
                this.action = {
                    name:'',
                    keywords:[]
                }
                if(this.actionState){//开启正则拖选
                    this.$refs.actionWapper.style.top = '0';
                    this.mouseSelectText()
                }else{
                    this.$refs.actionWapper.style.top = '-305px'
                    var container = container || document;
                    container.onmouseup = function(){
                    }
                }
            }
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                vm.$options.name = 'equipmentLogs2'+ to.query.id;
                //修改data参数
                vm.equipmentName = to.query.name;
                vm.busName = 'equipmentLogs2'+to.query.id;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'equipmentLogs2'+to.query.id,
                    component:'logsManage/equipmentLogs2.vue',
                    title:'日志'
                }
                sessionStorage.setItem('/equipmentLogs2'+to.query.id,JSON.stringify(obj))

                if(vm.equipmentId === '' || vm.equipmentId !== to.query.id){
                    vm.equipmentId = to.query.id;
                    vm.searchConditions['fields.equipmentid'] = vm.equipmentId;
                    vm.searchConditions.queryParam = JSON.stringify({'log.level':'','fields.equipmentid':vm.equipmentId})
                   // console.log(vm.searchConditions)
                    vm.getEquipmentData();
                }

            })

        },
        components:{
            vSearchForm,
            vLogscontent2,
            vBasetable2
        }
    }
</script>

<style scoped>
    .search-wapper{
        position: absolute;
        display: flex;
        justify-content: center;
        margin-bottom: 20px;
        top: 32px;
        right: 44px;
        z-index: 0;
    }
    .equipment-table{
        padding: 0 10px;
        height: auto;
        margin-bottom: 10px;
    }
    .content-wapper{
        height: 600px;
        padding: 0 10px 20px 10px;
        /*border:2px solid #303e4e;*/
    }
    .content-wapper>div{
        height: 100%;
    }
    .action-wapper{
        width: 300px;
        height: 300px;
        position: fixed;
        left: 0;
        top: -305px;
        background: #303e4e;
        z-index: 99891019;
        border: 1px solid #409eff;
        transition: all 0.3s linear;
    }
    .action-wapper h4{
        text-align: center;
        height: 50px;
        line-height: 50px;
        background: #285c92;
    }
    .action-wapper .from-reg{
        padding: 15px;
        height: 165px;
    }
    .btns-reg{
        height: 50px;
        display: flex;
        align-items: center;
        justify-content: space-around;
        border-top: 1px solid #285c92;
    }
    .keywords-wapper{
        height: 110px;
        /* width: 100%; */
        border: 1px solid #409eff;
        border-radius: 5px;
        overflow-y: auto;
        padding: 5px;
    }
    .keywords-wapper p {
        padding: 0 10px;
        height: 24px;
        line-height: 24px;
        font-size: 10px;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        word-break: break-all;
        border-bottom: 1px solid #3f5267;
        position: relative;
    }
    .remove-item{
        position: absolute;
        right: 5px;
        top: 6px;
        cursor: pointer;
    }
</style>
