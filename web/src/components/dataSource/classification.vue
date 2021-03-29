<template>
    <div  class="content-bg" v-loading="loading" element-loading-background="rgba(48, 62, 78, 0.5)">
        <div class="top-title">分类标识管理</div>
        <div class="con-wapper">
            <div class="left-wapper">
                <div class="left-tit">
                    <span>左侧</span>
                    <el-button type="success" size="mini" plain circle @click="leftDialogState = true"><i class="el-icon-plus"></i></el-button>
                </div>
                <div class="left-list" v-loading="leftLoading" element-loading-background="rgba(48, 62, 78, 0.5)">
                    <div v-for="(item,i) in leftList" class="left-item" >
                        <span class="item-name" @click="leftItemClick(item)">{{item.identify_basic_name}}</span>
                        <span class="item-btn">
                            <i class="el-icon-edit" @click="editLeftItem(item)"></i>
                            <i class="el-icon-close" @click="removeLeftItem(item)"></i>
                        </span>
                    </div>
                </div>
            </div>
            <div class="right-wapper" v-loading="rightLoading" element-loading-background="rgba(48, 62, 78, 0.5)">
                <div class="right-tit">
                    <h3 class="table-tit">{{currentLeftItem.identify_basic_name}}</h3>
                    <el-button type="primary" size="mini" plain @click="addFather"  v-if="currentLeftItem.identify_basic_id"><i class="el-icon-plus"></i></el-button>
                </div>
                <tableTree :tableHead="tableHead" :tableData="tableData" :loadObj="loadObj"></tableTree>
            </div>
        </div>

        <!--左侧弹窗-->
        <el-dialog :title="leftForm.identify_basic_id ? '修改' : '添加'" :visible.sync="leftDialogState" width="500px" v-loading="formLoading" element-loading-background="rgba(48, 62, 78, 0.5)" :close-on-click-modal="falseB" :destroy-on-close="trueB">
            <el-form label-width="60px">
                <el-form-item label="名称:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="leftForm.identify_basic_name" size="mini" class="item"></el-input>
                </el-form-item>
                <el-form-item label="描述:" >
                    <el-input type="textarea"  :autosize="{ minRows:8, maxRows: 10}" v-model="leftForm.identify_basic_remark"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="submitLeftFrom" :disabled="leftForm.identify_basic_name === '' ? 'disabled' : false">确 定</el-button>
                <el-button @click="leftDialogState = false">取 消</el-button>
            </div>
        </el-dialog>

        <!--右侧弹窗-->
        <el-dialog :title="rightForm.identify_details_id ? '修改' : '添加'" :visible.sync="rightDialogState" width="500px" v-loading="formLoading" element-loading-background="rgba(48, 62, 78, 0.5)" :close-on-click-modal="falseB" :destroy-on-close="trueB">
            <el-form label-width="75px">
                <el-form-item label="名称:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="rightForm.identify_details_name" size="mini" class="item"></el-input>
                </el-form-item>
                <el-form-item label="敏感级别:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-select v-model="rightForm.identify_details_sensitive" size="mini" clearable placeholder="请选择" style="width: 100%;">
                        <el-option
                            v-for="item in sensitiveLevel"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="描述:" >
                    <el-input type="textarea"  :autosize="{ minRows:8, maxRows: 10}" v-model="rightForm.identify_details_remark"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="submitRightFrom" :disabled="(rightForm.identify_details_name !== '' && rightForm.identify_details_sensitive !== '') ? false : 'disabled'">确 定</el-button>
                <el-button @click="rightDialogState = false">取 消</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import tableTree from '../common/BaseTableTree'
    export default {
        name: "classification",
        data(){
            return{
                loading:false,
                leftLoading:false,
                rightLoading:false,
                falseB:false,
                trueB:true,
                leftDialogState:false,
                rightDialogState:false,
                formLoading:false,
                //左侧列表
                leftList:[],
                //左侧form参数
                leftForm:{
                    identify_basic_name:'',
                    identify_basic_remark:''
                },
                //右侧表格form参数
                rightForm:{
                    identify_details_name:'',
                    identify_details_sensitive:'',
                    identify_details_remark:''
                },
                //右侧弹窗 确定按钮树形
                rightBtnProp:'',//addFather addChild edit
                //当前左侧选中项
                currentLeftItem:{},
                //敏感级别
                sensitiveLevel:[],
                //currentRightItem:{},
                tableHead:[
                    {
                        prop:'identify_details_name',
                        label:'详细分类名称',
                        width:''
                    },
                    {
                        prop:'identify_details_sensitive',
                        label:'敏感级别',
                        width:'',
                        formatData:(val,obj)=>{
                            let label = '';
                            this.sensitiveLevel.forEach(item=>{
                                if(item.value === val){
                                    label= item.label
                                }
                            })
                            return label
                        }
                    },
                    {
                        prop:'identify_details_remark',
                        label:'说明',
                        width:''
                    },
                    {
                        prop:'tools',
                        label:'操作',
                        width:'',
                        btns:[
                            {
                                icon:'el-icon-plus',
                                text:'添加子集',
                                clickFun:(row,index)=>{
                                   // this.rightForm = JSON.parse(JSON.stringify(row))
                                    this.rightBtnProp = 'addChild';
                                    this.rightDialogState = true;
                                    this.rightForm.identify_basic_id = this.currentLeftItem.identify_basic_id;
                                    this.rightForm.identify_details_superior = row.identify_details_id;
                                }
                            },
                            {
                                icon:'el-icon-edit',
                                text:'修改',
                                clickFun:(row,index)=>{
                                    this.rightForm = JSON.parse(JSON.stringify(row))
                                    this.rightDialogState = true;
                                }
                            },
                            {
                                icon:'el-icon-close',
                                text:'删除',
                                clickFun:(row,index)=>{
                                    //询问框
                                    layer.confirm('您确定删除么？', {
                                        btn: ['确定','取消'] //按钮
                                    }, (index)=>{
                                        layer.close(index);
                                        this.$nextTick(()=>{
                                            this.rightLoading = true;
                                            this.$axios.post(this.$baseUrl+'/metadataIdentify/deleteMetadataIdentify_Details.do',this.$qs.stringify({id:row.identify_details_id}))
                                                .then(res=>{
                                                    this.rightLoading = false;
                                                    let obj = res.data;
                                                    if(obj.success == 'true'){
                                                        this.loadObj.rowKey = JSON.stringify(new Date());
                                                        layer.msg(obj.message,{icon:1})
                                                        this.leftItemClick(this.currentLeftItem)
                                                    }else{
                                                        layer.msg(obj.message,{icon:5})
                                                    }
                                                })
                                                .catch(err=>{
                                                    this.rightLoading = false;
                                                })
                                        })
                                    }, function(){
                                        layer.close();
                                    })

                                }
                            }
                        ]
                    }
                ],
                tableData: [],
                tableHeight:0,
                loadObj:{
                    rowKey:'',
                    key:'identify_details_id',
                    url:'/metadataIdentify/selectBasicInfoById.do',
                    params:[{
                        key:'fid',
                        value:'',
                        rowKey:'identify_details_id'
                    }]
                }
            }
        },
        created(){
            this.loadObj.rowKey = JSON.stringify(new Date().getTime());
            //console.log( this.loadObj.rowKey )
            this.tableHeight = document.body.clientHeight - 320 ;
            window.onresize = () => {
                this.tableHeight = document.body.clientHeight - 320 ;
            };
            this.getSensitiveLevel()
            this.getLeftlist()

        },
        watch:{
            'leftDialogState'(n,o){
                if(!n){
                    this.leftForm = {
                        identify_basic_name:'',
                        identify_basic_remark:''
                    }
                }
            },
            'rightDialogState'(n,o){
                if(!n){
                    this.rightForm = {
                        identify_details_name:'',
                        identify_details_sensitive:'',
                        identify_details_remark:''
                    }
                }
            }
        },
        methods:{
            /*获取敏感级别*/
            getSensitiveLevel(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/dataSourceMetadata/getMetadataFieldSensitiveLevel.do',this.$qs.stringify())
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.sensitiveLevel = obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.loading = false;
                        })
                })
            },
            /*获取左侧列表数据*/
            getLeftlist(){
                this.$nextTick(()=>{
                    this.leftLoading = true;
                    this.$axios.post(this.$baseUrl+'/metadataIdentify/getBasicList.do',this.$qs.stringify())
                        .then(res=>{
                            this.leftLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.leftList = obj.data
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.leftLoading = false;
                        })
                })
            },
            /*左侧列表点击*/
            leftItemClick(item){
                this.currentLeftItem = item;
                this.tableData = []
                //获取左侧表格数据
                this.$nextTick(()=>{
                    this.rightLoading = true;
                    this.$axios.post(this.$baseUrl+'/metadataIdentify/selectBasicInfoById.do',this.$qs.stringify({
                        fid:item.identify_basic_id
                    }))
                        .then(res=>{
                            this.rightLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.$nextTick(()=>{
                                    this.tableData = JSON.parse(JSON.stringify(obj.data))
                                })
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.rightLoading = false;
                        })
                })
            },
            /*弹窗确定按钮*/
            submitLeftFrom(){
                let url = ''
                if(this.leftForm.identify_basic_id){//修改
                    url = '/metadataIdentify/updateMetadataIdentify_Basic.do'
                }else{//添加
                    url = '/metadataIdentify/insertMetadataIdentify_Basic.do'
                }
                this.$nextTick(()=>{
                    this.formLoading = true;
                    this.$axios.post(this.$baseUrl+url,this.$qs.stringify(this.leftForm))
                        .then(res=>{
                            this.formLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.leftDialogState = false;
                                layer.msg(obj.message,{icon:1})
                                this.getLeftlist()
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.formLoading = false;
                        })
                })
            },
            /*修改左侧item*/
            editLeftItem(item){
                this.leftDialogState = true;
                this.leftForm = JSON.parse(JSON.stringify(item));
            },
            /*删除左侧item*/
            removeLeftItem(item){
                //询问框
                layer.confirm('您确定删除么？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    this.$nextTick(()=>{
                        this.loading = true;
                        this.$axios.post(this.$baseUrl+'/metadataIdentify/deleteMetadataIdentify_Basic.do',this.$qs.stringify({
                            ids:item.identify_basic_id
                        }))
                            .then(res=>{
                                this.loading = false;
                                let obj = res.data;
                                if(obj.success == 'true'){
                                    layer.msg(obj.message,{icon:1})
                                    this.getLeftlist()
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
            /*添加父级节点*/
            addFather(){
                this.rightBtnProp = 'addFather';
                this.rightDialogState = true;
                this.rightForm.identify_basic_id = this.currentLeftItem.identify_basic_id;
                this.rightForm.identify_details_superior = this.currentLeftItem.identify_basic_id;
            },
            /*弹窗确定按钮-右侧*/
            submitRightFrom(){
                let url = ''
                if(this.rightForm.identify_details_id){//修改
                    url = '/metadataIdentify/updateMetadataIdentify_Details.do'
                }else{//添加
                    url = '/metadataIdentify/insertMetadataIdentify_Details.do'
                }
                this.$nextTick(()=>{
                    this.formLoading = true;
                    this.$axios.post(this.$baseUrl+url,this.$qs.stringify(this.rightForm))
                        .then(res=>{
                            this.formLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.rightDialogState = false;
                                layer.msg(obj.message,{icon:1})
                                this.leftItemClick(this.currentLeftItem)
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.formLoading = false;
                        })
                })
            },
            /*修改表格行*/
            editColumn(row){
                console.log(row)
            }
        },
        components:{
            tableTree
        }
    }
</script>

<style scoped>
    .con-wapper{
        display: flex;
        height: calc(100% - 100px);
        padding: 10px;
        /*justify-content: center;*/
    }
    .left-wapper{
        width: 250px;
        height: 100%;
        background: #303e4e;
        margin-right: 5px;
    }
    .left-tit{
        height: 40px;
        line-height: 40px;
        background: #3a8ee6;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 10px;
    }
    .left-tit /deep/ .el-button--mini.is-circle{
        padding: 3px;
    }
    .left-list{
        height: calc(100% - 45px);
        overflow: auto;
    }
    .left-item{
        height: 36px;
        line-height: 36px;
        display: flex;
        font-size: 13px;
        background: #3a4a5d;
        margin-bottom: 1px;
        padding:0 5px;
        position: relative;
    }
    .left-item:hover{
        background: #48607b;
        cursor: pointer;
    }
    .left-item:hover .item-btn{
        display: inline-block;
    }
    .item-name{
        display: inline-block;
        width: 100%;
    }
    .item-btn{
        flex: 1;
        position: absolute;
        width: 40px;
        right: 0;
        display: none;
    }
    .item-btn i:hover{
        color: #3a8ee6;
    }
    .right-wapper{
        width: calc(100% - 255px);
        height: 100%;
        background: #303e4e;
    }
    .right-tit{
        height: 50px;
        line-height: 50px;
        color: #3a8ee6;
        display: flex;
        justify-content: space-between;
        padding: 0 10px;
        align-items: center;
    }
</style>
