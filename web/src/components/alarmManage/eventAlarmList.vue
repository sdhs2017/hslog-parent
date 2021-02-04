<template>
    <div  class="content-bg" v-loading="loading" element-loading-background="rgba(48, 62, 78, 0.5)">
        <div class="top-title">事件告警管理
            <div class="btn-wapper">
                <el-button type="danger" size="mini" plain :disabled="delectAlarmIds.length > 0 ? false : true " @click="deleteAlarm">删除</el-button>
                <el-button type="primary" size="mini" plain @click="formState = true">添加</el-button>
                <el-button type="success" size="mini" plain  @click="refresh">刷新</el-button>
            </div>
        </div>
        <div class="search-wapper">
<!--            <v-search-form :formItem="formConditionsArr" :busName="busName"></v-search-form>-->
        </div>
        <div class="alarm-list-wapper">
            <vBasetable :selection="true" :tableHead="tableHead" :tableData="tableData" :busName="tableBusName"></vBasetable>
        </div>
        <div class="alarm-table-page">
            <span>共检索到告警数量为 <b>{{allCounts}}</b> 个</span>
            <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="size" :total="allCounts"></el-pagination>
        </div>
        <!--添加与修改弹窗-->
        <el-dialog :title="editId === '' ? '添加':'修改'" :visible.sync="formState" width="500px" v-loading="formLoading" element-loading-background="rgba(48, 62, 78, 0.5)" :close-on-click-modal="falseB">
            <el-form label-width="100px">
                <el-form-item label="事件:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-select v-model="form.event_type" @change="eventTypeChange" size="mini"  placeholder="请选择" style="width: 40%;">
                        <el-option
                            v-for="item in eventType"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                    <el-select v-model="form.event_name" size="mini" filterable placeholder="请选择" style="width: 50%;">
                        <el-option
                            v-for="item in eventArr"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="事件时间间隔:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="form.event_area" style="width: 80%;" size="mini"  type="number" min="1"  class="item"></el-input>
                    （秒）
                    <p style="font-size: 10px;color: #e4956d;">两个事件的时间间隔小于该数值时，会合并为一个时间区间。</p>
                </el-form-item>
                <el-form-item label="筛选条件:">
                    <queryFilter
                        style="line-height: 15px;"
                        :busFilterName="this.busFilterName"
                        :defaultFilterArr="this.defaultFilter"
                        :queryState="false"
                        useObject="chart"
                        :templateName="this.indexName.template_name"
                        :preIndexName="this.indexName.pre_index_name"
                        :suffixIndexName="this.indexName.suffix_index_name"
                    >
                    </queryFilter>
                </el-form-item>
                <el-form-item label="告警条件:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    事件数>=
                    <el-input v-model="form.alert_count" size="mini" style="width: 50%;"  type="number" min="1"  class="item"></el-input>
                    <p style="font-size: 10px;color: #e4956d;">两个事件的时间间隔小于该数值时，会合并为一个时间区间。</p>
                </el-form-item>
                <el-form-item label="资产组:">
                    <el-select v-model="form.alert_assetGroup_id" style="width: 74%;" filterable  placeholder="" size="mini" clearable @change="assetGroupChange">
                        <el-option
                            v-for="item in assetGroupOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="资产:">
                    <el-select v-model="form.alert_equipment_id" style="width: 74%;" filterable  placeholder="" size="mini" clearable >
                        <el-option
                            v-for="item in assetOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="editId === '' ? addAlarm() : reviseAlarm()">确 定</el-button>
                <el-button @click="formState = false">取 消</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vBasetable from '../common/Basetable2';
    import queryFilter from '../dashboard/queryFilter'
    import {jumpHtml} from "../../../static/js/common";
    import bus from '../common/bus';
    export default {
        name: "eventAlarm",
        data(){
            return{
                trueB:true,
                falseB:false,
                editId:'',//修改告警id
                loading:false,
                formLoading:false,
                formState:false,//表单弹窗状态
                defaultFilter:[],//默认筛选条件
                indexName:{
                    template_name:'winlogbeat-',
                    pre_index_name:'winlogbeat-',
                    suffix_index_name:'*',
                },
                form:{
                    event_type:'',
                    event_name:'',
                    event_area:'',
                    event_filters:'',
                    alert_count:1,
                    alert_equipment_id:'',
                    alert_assetGroup_id:''
                },
                //事件类型
                eventType:[],
                //事件
                eventArr:[],
                //资产组
                assetGroupOptions:[],
                //资产
                assetOptions:[],
                page:1,//页码
                c_page:1,//当前页码
                size:15,//每页的数量
                busName:'searchEventAlarm',
                busFilterName:'eventAlarmFilter',
                tableBusName:{
                    selectionName:'eventAlarmDelect'
                },
                allCounts:0,
                tableHead:[{
                    prop:'event_name_en_cn',
                    label:'事件名称',
                    width:''
                },{
                    prop:'event_type',
                    label:'事件类型',
                    width:''
                },{
                    prop:'event_area',
                    label:'事件时间间隔',
                    width:'',
                    formatData:(val,obj)=>{
                        return  val +' 秒'
                    }
                },{
                    prop:'alert_count',
                    label:'告警条件',
                    width:'',
                    formatData:(val,obj)=>{
                        return '事件数>= ' + val
                    }
                },{
                    prop:'event_type',
                    label:'事件类型',
                    width:''
                },{
                    prop:'alert_assetGroup_name',
                    label:'资产组',
                    width:''
                },{
                    prop:'alert_equipment_name',
                    label:'资产',
                    width:''
                },{
                    prop:'tools',
                    label:'操作',
                    width:'',
                    btns:[
                        {
                            icon:'el-icon-edit',
                            text:'修改',
                            clickFun:(row,index)=>{
                                this.formState = true;
                                this.editId = row.event_alert_id
                                this.getEventInfo();
                                this.getEvent(row.event_type);
                            }
                        },
                    ]}
                ],
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
                    event_name:''
                },
                delectAlarmIds:'',//选中删除的
            }
        },
        created() {

            this.getList(1,this.conditionFrom)

            bus.$on(this.busName,(params)=>{
                this.conditionFrom = params;
                this.getAlarmList(this.conditionFrom,1)
                this.c_page = 1;
            })
            //监听选中的资产
            bus.$on(this.tableBusName.selectionName,(params)=>{
                this.delectAlarmIds = '';
                for(let i in params){
                    this.delectAlarmIds += params[i].event_alert_id +','
                }
                //console.log(this.delectAlarmIds)
            })
            //
            //监听过滤条件
            bus.$on(this.busFilterName,(str)=>{
                this.form.event_filters = str;

            })
        },
        watch:{
            'formState'(){
                if(this.formState){
                    this.getEventType();
                    this.getAssetGroup();
                    this.getAsset('')
                }else{

                }
            }
        },
        methods:{
            /*初始化*/
            initialize(){
                this.form={
                   /* template_name:'winlogbeat-',
                    pre_index_name:'winlogbeat-',
                    suffix_index_name:'*',*/
                    event_type:'',
                    event_name:'',
                    event_area:'',
                    event_filters:'',
                    alert_count:1,
                    alert_equipment_id:'',
                    alert_assetGroup_id:''
                }
            },
            /*刷新*/
            refresh(){
                this.getList(1,this.conditionFrom)
            },
            /*获取列表*/
            getList(page,param){
                let objParam = param;
                objParam.pageIndex = page;//当前页
                objParam.pageSize = this.size;//页的条数
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/eventAlert/getEventAlertList.do',this.$qs.stringify(objParam))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.allCounts = Number(obj.data[0].count);
                                this.tableData = obj.data[0].list;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*获取资产类型*/
            getEventType(){
                this.$nextTick(()=>{
                    this.formLoading = true;
                    this.$axios.post(this.$baseUrl+'/eventAlert/getEventType.do','')
                        .then(res=>{
                            this.formLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.eventType = obj.data
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.formLoading = false;
                        })
                })
            },
            /*事件类型改变*/
            eventTypeChange(val){
                this.form.event_name = ''
                this.getEvent(val)
            },
            /*获取事件*/
            getEvent(val){
                this.$nextTick(()=>{
                    this.formLoading = true;
                    this.$axios.post(this.$baseUrl+'/eventAlert/getEventByType.do',this.$qs.stringify({
                        event_type: val
                    }))
                        .then(res=>{
                            this.formLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.eventArr = obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.formLoading = false;
                        })
                })
            },
            /*添加*/
            addAlarm(){
                if(this.checkParam()){
                    this.$nextTick(()=>{
                    this.formLoading = true;
                    this.$axios.post(this.$baseUrl+'/eventAlert/save.do',this.$qs.stringify(this.form))
                        .then(res=>{
                            this.formLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                layer.msg(obj.message,{icon:1})
                                this.formState = false
                                this.getList(1,{})
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.formLoading = false;
                        })
                })
                }

            },
            /*查询*/
            getEventInfo(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/eventAlert/getEventAlertInfo.do',this.$qs.stringify({
                        event_alert_id:this.editId
                    }))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.form = obj.data
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*修改*/
            reviseAlarm(){
                if(this.checkParam()){
                    this.$nextTick(()=>{
                        this.formLoading = true;
                        this.$axios.post(this.$baseUrl+'/eventAlert/update.do',this.$qs.stringify(this.form))
                            .then(res=>{
                                this.formLoading = false;
                                let obj = res.data;
                                if(obj.success == 'true'){
                                    layer.msg(obj.message,{icon:1})
                                    this.formState = false
                                    this.editId = ''
                                    this.getList(1,{})
                                }else{
                                    layer.msg(obj.message,{icon:5})
                                }
                            })
                            .catch(err=>{
                                this.formLoading = false;
                            })
                    })
                }

            },
            /*验证from参数合法性*/
            checkParam(){
                if(this.form.event_name === ''){
                    layer.msg("事件名称不能为空",{icon:5});
                    return false
                }else if(this.form.event_area <= 0){
                    layer.msg("事件时间间隔不合法",{icon:5});
                    return false
                } else if(this.form.alert_count <= 0){
                    layer.msg("告警条件不合法",{icon:5});
                    return false
                }else{
                    return true
                }
            },
            /*删除*/
            deleteAlarm(){
                //询问框
                layer.confirm('您确定删除么？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    this.$nextTick(()=>{
                        this.loading = true;
                        this.$axios.post(this.$baseUrl+'/eventAlert/delete.do',this.$qs.stringify({
                            event_alert_ids:this.delectAlarmIds
                        }))
                            .then(res=>{
                                this.loading = false;
                                let obj = res.data;
                                if(obj.success == 'true'){
                                    layer.msg(obj.message,{icon:1})
                                    this.getList(1,this.conditionFrom)
                                }else{
                                    layer.msg(obj.message,{icon:5})
                                }
                            })
                            .catch(err=>{
                                this.loading = false;
                            })
                    })
                }, function(){
                    layer.close();
                })

            },
            handleCurrentChange(page){
                this.getList(page, this.conditionFrom)
            },
            /*获取资产组*/
            getAssetGroup(){
                this.$nextTick(()=>{
                    this.formLoading = true;
                    this.$axios.post(this.$baseUrl+'/assetGroup/getAssetGroupList4Combobox.do',this.$qs.stringify())
                        .then(res=>{
                            this.formLoading = false;
                            let obj = res.data;
                            if(obj.success = 'true'){
                                this.assetGroupOptions = obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.formLoading = false;
                        })
                })
            },
            /*资产组改变事件*/
            assetGroupChange(val){
                this.getAsset(val);
            },
            /*获取资产*/
            getAsset(asset_group_id){
                this.$nextTick(()=>{
                    this.formLoading = true;
                    this.$axios.post(this.$baseUrl+'/assetGroup/getAssetList4Combobox.do',this.$qs.stringify({
                        asset_group_id:asset_group_id
                    }))
                        .then(res=>{
                            this.formLoading = false;
                            let obj = res.data;
                            if(obj.success = 'true'){
                                this.assetOptions = obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.formLoading = false;
                        })
                })
            },
        },
        components:{
            vBasetable,
            vSearchForm,
            queryFilter
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
    /deep/ .el-form-item__content{
        color: #3f9efe;
    }
</style>
