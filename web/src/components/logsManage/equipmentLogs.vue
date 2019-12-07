<template>
    <div class="content-bg">
        <div class="top-title">'{{equipmentName}}' 日志</div>
        <div class="search-wapper">
            <v-search-form :formItem="formConditionsArr" :busName="busName"></v-search-form>
        </div>
        <div class="equipment-table">
            <v-basetable :tableHead="equipmentHead" :tableData="equipmentData"></v-basetable>
        </div>
        <div class="content-wapper">
            <v-logscontent :searchConditions="searchConditions" :tableHead="tableHead" :searchUrl="searchUrl" :layerObj="layerObj" ref="logContent" :moreDeleteBtn="true"></v-logscontent>
        </div>
    </div>
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vLogscontent from '@/components/logsManage/logsContent';
    import vBasetable from '../common/Basetable'
    import bus from '../common/bus';

    export default {
        name: "equipmentLogs",
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
                    id:'',
                    starttime:'',
                    endtime:'',
                    operation_level:''
                },
                searchUrl:'log/getLogListByEquipment.do',//数据地址
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
                levelVal:''//级别内容
            }
        },
        created(){
            this.getEquipmentType();
            //检索条件
            this.formConditionsArr = [
                {
                    label:'事件范围',
                    type:'datetimerange',
                    itemType:'',
                    paramName:'time',
                    model:{
                        model:[]
                    },
                    val:''
                },
                {
                    label:'日志级别',
                    paramName:'operation_level',
                    type:'select',
                    itemType:'multiple',
                    model:{
                        model:this.levelVal
                    },
                    options:this.logLevel
                }

            ]

        },
        methods:{
            /*获取资产信息*/
            getEquipmentData(){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/equipment/selectEquipment.do',this.$qs.stringify({id:this.equipmentId}))
                        .then((res)=>{
                            this.equipmentData = res.data;
                            this.logType = this.equipmentData[0].logType;
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
                let logConfig = JSON.parse(sessionStorage.getItem('logConfig'))
                //console.log(logConfig)
                let logLevelArr = logConfig[logType].level;
                for(let i in logLevelArr){
                    let obj = {
                        value:logLevelArr[i],
                        label:logLevelArr[i]
                    };
                    this.logLevel.push(obj);
                }
                //设置表头
                let headerArr = logConfig[logType].tableHead;
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
            }

        },
        watch:{
            'equipmentId'(){
                //检测搜索条件
                bus.$on(this.busName,(params)=>{
                    this.searchConditions={
                        starttime:params.starttime,
                        endtime:params.endtime,
                        operation_level:params.operation_level,
                        id:this.equipmentId
                    }
                })
            },
            'logType'(newV,oldV){

                this.setLogConfig(newV);

            }
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                vm.$options.name = 'equipmentLogs'+ to.query.id;
                //修改data参数
                vm.equipmentName = to.query.name;
                vm.busName = 'equipmentLogs'+to.query.id;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'equipmentLogs'+to.query.id,
                    component:'logsManage/equipmentLogs.vue',
                    title:'日志'
                }
                sessionStorage.setItem('/equipmentLogs'+to.query.id,JSON.stringify(obj))

                if(vm.equipmentId === '' || vm.equipmentId !== to.query.id){
                    vm.equipmentId = to.query.id;
                    vm.searchConditions.id = vm.equipmentId;
                    vm.getEquipmentData();
                }

            })

        },
        components:{
            vSearchForm,
            vLogscontent,
            vBasetable
        }
    }
</script>

<style scoped>
    .search-wapper{
        position: absolute;
        display: flex;
        justify-content: center;
        margin-bottom: 20px;
        top: 64px;
        right: 84px;
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

</style>
