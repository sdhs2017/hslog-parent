<template>
    <div  class="content-bg" v-loading="loading" element-loading-background="rgba(48, 62, 78, 0.5)">
        <div class="top-title">发现规则列表
            <div class="btn-wapper">
                <el-button type="primary" size="mini" plain @click="formState = true">添加</el-button>
                <el-button title="删除选中项" type="danger" size="mini" plain :disabled="delectIds.length > 0 ? false : true "  @click="remove">删除</el-button>
                <el-button type="success" size="mini" plain  @click="refresh">刷新</el-button>
            </div>
        </div>
        <div class="search-wapper">
            <v-search-form :formItem="conditionsArr" :busName="busName"></v-search-form>
        </div>
        <div class="table-wapper">
            <basetable :selection="true" :table-head="tableHead" :table-data="tableData" :busName="tableBusName"></basetable>
        </div>
        <div class="table-page">
            <span>共检索到规则 <b>{{allCounts}}</b> 个</span>
            <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="size" :total="allCounts"></el-pagination>
        </div>
        <!--添加与修改弹窗-->
        <el-dialog :title="editId === '' ? '添加':'修改'" :visible.sync="formState"  destroy-on-close width="500px" v-loading="formLoading" element-loading-background="rgba(48, 62, 78, 0.5)" :close-on-click-modal="falseB">
            <el-form label-width="100px">
                <el-form-item label="规则名称:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="form.label_name" size="mini" class="item"></el-input>
                </el-form-item>
                <el-form-item label="发现方式:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-select v-model="form.label_discover_way" size="mini" clearable @change="discoverWayChange" placeholder="请选择" style="width: 100%;">
                        <el-option
                            v-for="item in discoverWay"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="确认比例(%):" v-if="form.label_discover_way == '1'">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="form.label_discover_percent" size="mini" type="number" min="1" max="100" maxlength=3 class="item" placeholder="1-100"></el-input>
                </el-form-item>
                <el-form-item label="正则表达式:"  v-if="form.label_discover_way == '1'">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input type="textarea"  :autosize="{ minRows:4, maxRows: 10}" v-model="form.label_discover_regex"></el-input>
                </el-form-item>
                <el-form-item label="规则描述:" >
                    <el-input type="textarea"  :autosize="{ minRows:4, maxRows: 10}" v-model="form.label_remark"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="formState = false">取消</el-button>
                <el-button type="primary" @click="editId === '' ? addTab() : reviseTab()">确 定</el-button>
            </div>
        </el-dialog>

    </div>
</template>

<script>
    import basetable from '../common/Basetable2'
    import vSearchForm from '../common/BaseSearchForm';
    import bus from '../common/bus';
    export default {
        name: "tabManage",
        data(){
            return{
                falseB:false,
                loading:false,
                conditionsArr:[
                    {
                        label:'规则名称',
                        paramName:'label_name',
                        model:{
                            model:''
                        },
                        itemType:'',
                        type:'input'
                    }
                ],
                busName:'tabManageSearch',
                //查询条件
                conditionFrom:{label_name:''},
                page:1,//页码
                c_page:1,//当前页码
                size:15,//每页的数量
                allCounts:0,
                tableBusName:{
                    selectionName:'tabManageDelect'
                },
                //删除id 集合
                delectIds:'',
                tableData:[],
                tableHead:[
                    {
                        prop:'label_name',
                        label:'规则名称',
                        width:''
                    },
                    {
                        prop:'label_discover_way',
                        label:'发现方式',
                        width:'',
                        formatData:(val,obj)=>{
                            let label = '';
                            this.discoverWay.forEach(item=>{
                                if(item.value == val){
                                    label= item.label
                                }
                            })
                            return label
                        }
                    },
                    {
                        prop:'label_discover_percent',
                        label:'确认比例(%)',
                        width:''
                    },
                    {
                        prop:'label_discover_regex',
                        label:'正则表达式',
                        width:''
                    },
                    {
                        prop:'label_remark',
                        label:'规则描述',
                        width:''
                    },
                    {
                        prop:'tools',
                        label:'操作',
                        width:'60',
                        btns:[
                            {
                                icon:'el-icon-edit',
                                text:'修改',
                                clickFun:(row,index)=>{
                                    this.formState = true;
                                    this.editId = row.label_id
                                    this.getTabInfo();
                                }
                            },
                        ]}
                ],
                //修改id
                editId:'',
                formState:false,
                formLoading:false,
                //添加 表单
                form:{
                    label_name:'',
                    label_discover_way:1,
                    label_discover_percent:'',
                    label_discover_regex:'',
                    label_remark:'',
                },
                //发现方式
                discoverWay:[
                    {value:'1',label:'按正则发现'},
                    { value:'0',label:'无规则' }
                ],
            }
        },
        watch:{
            'formState'(n,o){
                if(!n){
                    this.form = {
                        label_name:'',
                        label_discover_way:1,
                        label_discover_percent:'',
                        label_discover_regex:'',
                        label_remark:'',
                    }
                    //清除选中
                    this.editId = ''
                }
            }
        },
        created() {
            this.getDataList(1,this.conditionFrom)
            this.getDiscoverWay()
            //检测搜索条件
            bus.$on(this.busName,(params)=>{
                this.conditionFrom = params;
                this.getDataList(1,this.conditionFrom)
            })
            //监听选中
            bus.$on(this.tableBusName.selectionName,(params)=>{
                this.delectIds = '';
                for(let i in params){
                    this.delectIds += params[i].label_id +','
                }
                //console.log(this.delectAlarmIds)
            })
            this.tableHeight = document.body.clientHeight - 402 ;
            window.onresize = () => {
                let h1 = document.body.clientHeight - 402 ;
                this.tableHeight =  h1 < 320 ? 320 : h1;
                let h2 = document.body.clientHeight - 370 ;
                this.tableDialogHeight =  h2 < 320 ? 320 : h2;
            };
        },
        methods:{
            /*获取发现方式*/
            getDiscoverWay(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/DSGLabel/getLabelDiscoverWay.do',this.$qs.stringify())
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.discoverWay = obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.loading = false;
                        })
                })
            },
            //发现方式改变
            discoverWayChange(){
                this.form.label_discover_percent = ''
                this.form.label_discover_regex = ''
            },
            /*刷新*/
            refresh(){
                this.getDataList(1,this.conditionFrom)
            },
            /*获取列表*/
            getDataList(page,param){
                this.c_page = page;
                let objParam = param;
                objParam.pageIndex = page;//当前页
                objParam.pageSize = this.size;//页的条数
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/DSGLabel/searchLabel.do',this.$qs.stringify(objParam))
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
            /*分页*/
            handleCurrentChange(page){
                this.getDataList(page, this.conditionFrom)
            },
            /*查询单条信息*/
            getTabInfo(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/DSGLabel/getLabelInfoById.do',this.$qs.stringify({
                        label_id:this.editId
                    }))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.form = obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.loading = false;
                        })
                })
            },
            /*添加标签*/
            addTab(){
                if(this.checkParam()){
                    this.$nextTick(()=>{
                        this.formLoading = true;
                        this.$axios.post(this.$baseUrl+'/DSGLabel/insertLabel.do',this.$qs.stringify(this.form))
                            .then(res=>{
                                this.formLoading = false;
                                let obj = res.data;
                                if(obj.success == 'true'){
                                    layer.msg(obj.message,{icon:1})
                                    this.formState = false
                                    this.getDataList(1,{})
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
            /*修改标签*/
            reviseTab(){
                if(this.checkParam()){
                    this.$nextTick(()=>{
                        this.formLoading = true;
                        this.$axios.post(this.$baseUrl+'/DSGLabel/updateLabel.do',this.$qs.stringify(this.form))
                            .then(res=>{
                                this.formLoading = false;
                                let obj = res.data;
                                if(obj.success == 'true'){
                                    layer.msg(obj.message,{icon:1})
                                    this.formState = false
                                    this.editId = ''
                                    this.getDataList(1,{})
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
            /*删除数据源*/
            remove(){
                //询问框
                layer.confirm('您确定删除么？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    this.$nextTick(()=>{
                        this.loading = true;
                        this.$axios.post(this.$baseUrl+'/DSGLabel/deleteLabel.do',this.$qs.stringify({
                            ids:this.delectIds
                        }))
                            .then(res=>{
                                this.loading = false;
                                let obj = res.data;
                                if(obj.success == 'true'){
                                    layer.msg(obj.message,{icon:1})
                                    this.getDataList(1,this.conditionFrom)
                                    this.delectIds = '';
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
            /*测试链接*/
            testLink(){
                if(this.checkParam()){
                    this.$nextTick(()=>{
                        this.formLoading = true;
                        this.$axios.post(this.$baseUrl+'/dataSource/testConnection.do',this.$qs.stringify(this.form))
                            .then(res=>{
                                this.formLoading = false;
                                let obj = res.data;
                                if(obj.success == 'true'){
                                    this.linkObj = {
                                        state:true,
                                        text:obj.message
                                    }
                                    //layer.msg(obj.message,{icon:1})
                                }else{
                                    //                   console.log(typeof obj)
                                    this.linkObj = {
                                        state:false,
                                        text:obj.message,
                                        alertInfo:obj.alertInfo
                                    }
                                    this.errorLinkState = true;
                                    //layer.msg(obj.message,{icon:5})
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
                if(this.form.label_name === ''){
                    layer.msg('名称不能为空',{icon:5})
                    return false
                }else if(this.form.label_discover_way === 1 && this.form.label_discover_regex === ''){
                    layer.msg('发现方式为正则时，正则表达式不能为空',{icon:5})
                    return false
                }else if(this.form.label_discover_way === 1 && this.form.label_discover_percent > 100){
                    layer.msg('确认比例值为1-100',{icon:5})
                    return false
                }else if(this.form.label_discover_way === 1 && this.form.label_discover_percent < 1){
                    layer.msg('确认比例值为1-100',{icon:5})
                    return false
                }else{
                    return true
                }
            }
        },
        components:{
            basetable,
            vSearchForm
        }
    }
</script>

<style scoped>
    .btn-wapper{
        float: right;
        margin-right: 10px;
    }
    .table-wapper{
        padding: 10px;
        width: calc(100% - 20px);
    }
    .table-page{
        border-top: 1px solid #303e4e;
        height: 40px;
        display: flex;
        justify-content: flex-end;
        align-items:center;
    }
    .table-page b{
        color: #e4956d;
    }
</style>
