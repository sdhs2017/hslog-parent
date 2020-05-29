<template>
    <div class="content-bg">
        <div class="top-title">服务列表</div>
        <div class="search-wapper">
            <v-search-form :formItem="formConditionsArr" :busName="busNames.searchBusName"></v-search-form>
        </div>
        <div class="tools-wapper">
            <el-button type="danger" size="mini" plain  @click="removeService">停用</el-button>
        </div>
        <div class="table-wapper" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
            <v-basetable :selection="true" :tableHead="tableHead" :tableData="tableData" :busName="busNames"></v-basetable>
        </div>
        <div class="table-page">
            <span>共检索到资产数量为 <b>{{allCounts}}</b> 台</span>
            <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="size" :total="allCounts"></el-pagination>
        </div>
        <!--修改服务列表-->
        <el-dialog title="修改服务列表" :visible.sync="reviseWapper" width="440px">
            <el-form label-width="80px">
                <el-form-item label="路径:" >
                    <el-input :title="reviseCondition.url" v-model="reviseCondition.url" disabled></el-input>
                </el-form-item>
                <el-form-item label="名称:" >
                    <el-input v-model="reviseCondition.name" ></el-input>
                </el-form-item>
                <el-form-item label="是否启用:" >
                    <el-radio-group v-model="reviseCondition.state">
                        <el-radio :label="1">是</el-radio>
                        <el-radio :label="0">否</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="描述:" >
                    <el-input type="textarea"  :autosize="{ minRows:4, maxRows: 6}" v-model="reviseCondition.describe"></el-input>
                </el-form-item>
                <el-form-item label="是否补全:" >
                    <el-radio-group v-model="reviseCondition.complementState">
                        <el-radio :label="1">已补全</el-radio>
                        <el-radio :label="0">未补全</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="updateService">确 定</el-button>
                <el-button @click="reviseWapper = false">取 消</el-button>
            </div>
        </el-dialog>
    </div>
    
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vBasetable from '../common/Basetable';
    import bus from '../common/bus';
    export default {
        name: "serviceList",
        data() {
            return {
                loading:false,
                busNames:{
                    searchBusName:'serviceListSearch',
                    selectionName:'serviceListSelection',
                    reviseServiceListName:'serviceListRevise'
                },
                searchConditions:{ //查询条件
                    name:'',//名称
                    ip:'',//ip
                    port:'',//端口
                    protocol:'',//协议
                    url:'',//路径
                    relativeUrl:'',//相对路径
                    state:'',//是否启用
                    complementState:'',//补全状态
                },
                delectEquipmentIds:'',//需要删除的列表id集合
                complementStateArr:[
                    {
                        value:'',
                        label:'',
                    },
                    {
                        value:'0',
                        label:'未补全',
                    },
                    {
                        value:'1',
                        label:'已补全',
                    }
                ],
                stateArr:[
                    {
                        value:'',
                        label:'',
                    },
                    {
                        value:'0',
                        label:'否',
                    },
                    {
                        value:'1',
                        label:'是',
                    }
                ],
                tableHead:[
                    {
                        prop:'name',
                        label:'名称',
                        width:'80'
                    },
                    {
                        prop:'ip',
                        label:'IP',
                        width:'120'
                    }
                    ,
                    {
                        prop:'port',
                        label:'端口',
                        width:'65'
                    }
                    ,
                    {
                        prop:'protocol',
                        label:'协议',
                        width:'65'
                    }
                    ,
                    {
                        prop:'url',
                        label:'路径',
                        width:''
                    }
                    ,
                    {
                        prop:'relativeUrl',
                        label:'相对路径',
                        width:''
                    }
                    ,
                    {
                        prop:'complementState',
                        label:'补全状态',
                        width:'75',
                        formatData:(val)=>{return val == '1' ? '已补全' : '<span style="color:#e6a23c;">未补全</span>'}
                    }
                    ,
                    {
                        prop:'state',
                        label:'是否启用',
                        width:'75',
                        formatData:(val)=>{return val == '1' ? '是' : '否'}
                    }
                    ,
                    {
                        prop:'describe',
                        label:'描述',
                        width:''
                    }
                    ,
                    {
                        prop:'createTime',
                        label:'创建时间',
                        width:''
                    }
                    ,
                    {
                        prop:'updateTime',
                        label:'修改时间',
                        width:''
                    }
                    ,
                    {
                        prop:'stopTime',
                        label:'停用时间',
                        width:''
                    },
                    {
                        prop:'tools',
                        label:'操作',
                        width:'50',
                        btns:[
                            {
                                icon:'el-icon-edit',
                                text:'修改',
                                btnType: 'reviseService',
                                clickFun:(params)=>{
                                    this.getServiceById(params.id);
                                }
                            }
                        ]
                    }
                ],//表列
                tableData:[],//表数据
                formConditionsArr:[],//查询条件集合
                allCounts:0,
                page:1,//页码
                c_page:1,//当前页码
                size:15,//每页的数量
                reviseWapper:false,//修改弹窗状态
                reviseCondition:{
                    id:'',
                    url:'',
                    name:'',
                    state:'',
                    describe:'',
                    complementState:''
                }
            }
        },
        created(){
            //查询条件
            this.formConditionsArr=[
                {
                    label:'名称',
                    paramName:'name',
                    model:{
                        model:''
                    },
                    itemType:'',
                    type:'input'
                },
                {
                    label:'IP',
                    paramName:'ip',
                    itemType:'',
                    model:{
                        model:''
                    },
                    type:'input'
                },
                {
                    label:'端口',
                    paramName:'port',
                    model:{
                        model:''
                    },
                    itemType:'',
                    type:'input'
                },
                {
                    label:'协议',
                    paramName:'protocol',
                    model:{
                        model:''
                    },
                    itemType:'',
                    type:'input'
                },
                {
                    label:'路径',
                    paramName:'url',
                    model:{
                        model:''
                    },
                    itemType:'',
                    type:'input'
                },
                {
                    label:'相对路径',
                    paramName:'relativeUrl',
                    model:{
                        model:''
                    },
                    itemType:'',
                    type:'input'
                },
                {
                    label:'状态',
                    paramName:'complementState',
                    type:'select',
                    itemType:'',
                    model:{
                        model:''
                    },
                    options:this.complementStateArr
                },
                {
                    label:'启用',
                    paramName:'state',
                    type:'select',
                    itemType:'',
                    model:{
                        model:''
                    },
                    options:this.stateArr
                }
            ];
            this.getServiceListData(this.searchConditions,1)
            //监听查询条件组件传过来的条件
            bus.$on(this.busNames.searchBusName,(params)=>{
                this.searchConditions = params;
                this.getServiceListData(this.searchConditions,1)
                this.c_page = 1;
            })
           //监听选中的资产
            bus.$on(this.busNames.selectionName,(params)=>{
                this.delectEquipmentIds = '';
                for(let i in params){
                    this.delectEquipmentIds += params[i].id +','
                }
            })

        },
        beforeDestroy(){
            bus.$off(this.busNames.searchBusName);
            bus.$off(this.busNames.selectionName);
        },
        methods:{
            /*获取服务列表数据*/
            getServiceListData(searchObj,page){
                this.loading = true;
                let obj = searchObj;
                obj.pageIndex = page;//当前页
                obj.pageSize = this.size;//页的条数
                //请求
                this.$nextTick( ()=> {
                    this.$axios.post(this.$baseUrl+'/serviceInfo/selectPage.do',this.$qs.stringify(obj))
                        .then((res) => {
                            this.loading = false;
                            //填充表格数据
                            this.tableData = res.data[0].serviceInfo;
                            this.allCounts = JSON.parse(res.data[0].count.count);
                            /*//将查询条件保存，用于分页
                            this.saveCondition = this.searchConditions;*/

                        })
                        .catch((err) => {
                            this.loading = false;
                            console.log(err);
                        })
                })
            },
            /*分页页码改变*/
            handleCurrentChange(page){
                //获取数据
                this.getServiceListData(this.searchConditions,page);
            },
            /*删除*/
            removeService(){
                if(this.delectEquipmentIds === ''){
                    layer.msg('未选中任何服务',{icon: 5});
                }else{
                    //询问框
                    layer.confirm('您确定停用么？', {
                        btn: ['确定','取消'] //按钮
                    }, (index)=>{
                        layer.close(index);
                        this.$nextTick(()=>{
                            this.$axios.post(this.$baseUrl+'/serviceInfo/delete.do',this.$qs.stringify({id:this.delectEquipmentIds}))
                                .then((res)=>{
                                    if(res.data.success =='true'){
                                        layer.msg("停用成功",{icon:1});
                                        this.getServiceListData(this.searchConditions,1)
                                        this.c_page = 1;
                                    }else{
                                        layer.msg("停用失败",{icon:5});
                                    }

                                })
                                .catch((err)=>{
                                    layer.msg("停用失败",{icon:5});
                                })
                        })
                    }, function(){
                        layer.close();
                    })
                }
            },
            /*获取修改的服务信息*/
            getServiceById(id){
                this.$nextTick( ()=> {
                    layer.load(1)
                    this.$axios.post(this.$baseUrl+'/serviceInfo/selectServiceById.do',this.$qs.stringify({id:id}))
                        .then((res) => {
                            layer.closeAll('loading');
                            //填充信息
                            this.reviseCondition.id = res.data.id;
                            this.reviseCondition.url = res.data.url;
                            this.reviseCondition.name = res.data.name;
                            this.reviseCondition.state = res.data.state;
                            this.reviseCondition.describe = res.data.describe;
                            this.reviseCondition.complementState = res.data.complementState;
                            //弹窗
                            this.reviseWapper = true;
                        })
                        .catch((err) => {
                            layer.closeAll('loading');
                            console.log(err);
                        })
                })
            },
            /*修改*/
            updateService(){
                layer.load(1)
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/serviceInfo/updateById.do',this.$qs.stringify(this.reviseCondition))
                        .then((res)=>{
                            if(res.data.success =='true'){
                                layer.msg("修改成功",{icon:1});
                                this.getServiceListData(this.searchConditions,1)
                                this.c_page = 1;
                                this.reviseWapper = false;
                            }else{
                                layer.msg("修改失败",{icon:5});
                            }

                        })
                        .catch((err)=>{
                            layer.msg("修改失败",{icon:5});
                        })
                })
            }
        },
        components:{
            vSearchForm,
            vBasetable
        }
    }
</script>

<style scoped>
.search-wapper{
    max-width: 1400px;
    margin: 20px auto;
    padding: 0 10px;
}
.tools-wapper{
    padding-left: 10px;
    margin-bottom: 10px;
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
