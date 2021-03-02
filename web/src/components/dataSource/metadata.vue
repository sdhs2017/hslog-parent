<template>
    <div class="content-bg" style="height: auto">
        <div class="top-title">元数据管理</div>
        <div class="prop-wapper">
            <div class="left-wapper">
                <div class="left-tit"></div>
                <div class="left-list" v-loading="leftLoading"  element-loading-background="rgba(48, 62, 78, 0.5)">
                    <el-tree
                        :props="propTree"
                        :data="propData"
                        @node-click="nodeClick"
                    >
                    </el-tree>
                </div>

            </div>
            <div class="right-wapper" v-loading="rightLoading"  element-loading-background="rgba(48, 62, 78, 0.5)">
                <h3 class="table-tit">{{currentName}} </h3>
                <vBasetable :tableHead="tableHead" :height="this.tableHeight" :tableData="tableDataArr"></vBasetable>
                <div class="table-page" style="display: flex;justify-content: flex-end;align-items: center;height: 40px">
                    <span>总数为 <b>{{allCounts}} 条 </b></span>
                    <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="size" :total="allCounts"></el-pagination>
                </div>
            </div>
        </div>
        <!--配置弹窗 表-->
        <el-dialog title="修改" :visible.sync="formState" width="500px" v-loading="formLoading" element-loading-background="rgba(48, 62, 78, 0.5)" :close-on-click-modal="falseB">
            <el-form label-width="100px">
                <el-form-item label="表名:">
                    <el-input v-model="form.metadata_table" style="width: 90%;" size="mini" disabled class="item"></el-input>
                </el-form-item>
            </el-form>
            <el-form label-width="100px">
                <el-form-item label="表格分类:">
                    <el-select v-model="form.metadata_table_type" size="mini" clearable placeholder="请选择" style="width: 90%;">
                        <el-option
                            v-for="item in tableType"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <el-form label-width="100px">
                <el-form-item label="标注:">
                    <el-input v-model="form.metadata_remark" style="width: 90%;" size="mini" class="item"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="submitData">确 定</el-button>
                <el-button @click="formState = false">取 消</el-button>
            </div>
        </el-dialog>

        <!--配置弹窗 字段-->
        <el-dialog title="修改" :visible.sync="formStateField" width="500px" v-loading="formLoading" element-loading-background="rgba(48, 62, 78, 0.5)" :close-on-click-modal="falseB">
            <el-form label-width="100px">
                <el-form-item label="字段名:">
                    <el-input v-model="form.metadata_field" style="width: 90%;" size="mini" disabled class="item"></el-input>
                </el-form-item>
            </el-form>
            <el-form label-width="100px">
                <el-form-item label="字段类型:">
                    <el-input v-model="form.metadata_field_type" style="width: 90%;" size="mini" disabled class="item"></el-input>
                </el-form-item>
            </el-form>
            <el-form label-width="100px">
                <el-form-item label="敏感级别:">
                    <el-select v-model="form.metadata_sensitiveLevel" size="mini" clearable placeholder="请选择" style="width: 90%;">
                        <el-option
                            v-for="item in sensitiveLevel"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <el-form label-width="100px">
                <el-form-item label="标注:">
                    <el-input v-model="form.metadata_remark" style="width: 90%;" size="mini" class="item"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="submitData">确 定</el-button>
                <el-button @click="formStateField = false">取 消</el-button>
            </div>
        </el-dialog>
    </div>

</template>

<script>
    import vBasetable from '../common/Basetable2';
    export default {
        name: "metadata",
        data() {
            return {
                falseB:false,
                leftLoading:false,
                rightLoading:false,
                formLoading:false,
                formState:false,
                formStateField:false,
                allCounts:0,
                page:1,//页码
                c_page:1,//当前页码
                size:15,//每页的数量
                //修改  index
                editIndex:'',
                //修改  表单
                form:{},
                //属性树
                propTree:{
                    children: 'menus',
                    label: 'menuName'
                },
                propData: [],
                //表格参数
                conditionForm:{},
                //索引数组
                //indexArr:['hslog_packet','hslog_syslog'],
                //当前显示的索引index
                currentIndex:'0',
                currentName:'',
                tableHead:[],
                secondHead:[{
                    prop:'metadata_table',
                    label:'表名',
                    width:''
                },{
                    prop:'metadata_table_type',
                    label:'表格分类',
                    width:'',
                    formatData:(val,obj)=>{
                        let label = '';
                        this.tableType.forEach(item=>{
                            if(item.value === val){
                                //console.log(item.label)
                                label= item.label
                            }
                        })
                        return label
                    }
                },{
                    prop:'metadata_remark',
                    label:'标注',
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
                                this.form = row;
                                this.formState = true;
                                this.editIndex = index;
                            }
                        },
                    ]}
                ],
                thirdHead:[{
                    prop:'metadata_field',
                    label:'字段名',
                    width:''
                },{
                    prop:'metadata_field_type',
                    label:'字段类型',
                    width:'',
                },{
                    prop:'metadata_sensitiveLevel',
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
                },{
                    prop:'metadata_remark',
                    label:'标注',
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
                                this.form = row;
                                this.formStateField = true;
                                this.editIndex = index;
                            }
                        },
                    ]}
                ],
                //属性名称数据
                tableDataArr:[],
                tableType:[],
                //敏感级别
                sensitiveLevel:[],
                //表格高度
                tableHeight:'',
                //当前类型
                currentType:''
            }
        },
        created(){
            //获取数据
            // this.getProps('hslog_packet');
            this.getTreeData();
            this.getTableType();
            this.getSensitiveLevel();
            this.tableHeight = document.body.clientHeight - 260 ;
            window.onresize = () => {
                this.tableHeight = document.body.clientHeight - 260 ;
            };
        },
        methods:{
            /*索引列表点击*/
            /*chooseIndex(item,index){
                this.currentIndex = index;
                this.currentName = item;
                this.getProps(this.currentName)
            },*/
            //获取树节点数据
            getTreeData(){
                this.$nextTick(()=>{
                    this.leftLoading = true;
                    this.$axios.post(this.$baseUrl+'/dataSourceMetadata/getMetadataTree.do','')
                        .then(res=>{
                            // console.log(res)
                            let obj = res.data;
                            this.leftLoading = false;
                            if(obj.success == 'true'){
                                this.propData = obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }

                        })
                        .catch(err=>{
                            this.leftLoading = false;
                        })
                })
            },
            /*获取表格分类*/
            getTableType(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/dataSourceMetadata/getMetadataTableType.do',this.$qs.stringify())
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.tableType=obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.loading = false;
                        })
                })
            },
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
            /*节点点击*/
            nodeClick(node){
                this.currentName = node.menuName;
                if (node.state  == 2){
                    this.currentType = 'table'
                    this.conditionFrom = {
                        metadata_database:node.database,
                        data_source_id:node.data_source_id
                    }
                    this.tableHead=this.secondHead
                    this.getProps(1,this.conditionFrom)
                } else if (node.state == 3) {
                    console.log(node)
                     this.currentType = 'field'
                    // let url = '/metadata/getMedataByIndexDynamically.do';
                     this.conditionFrom = {
                         metadata_database:node.database,
                         data_source_id:node.data_source_id,
                         metadata_table:node.table
                     }
                    this.tableHead=this.thirdHead
                    this.getProps(1,this.conditionFrom)
                }

            },
            /*获取propsData*/
            getProps(page,params){
                let url = '';
                if(this.currentType === 'table'){
                    url = '/dataSourceMetadata/getTableInfo.do';
                }else{
                    url = '/dataSourceMetadata/getFieldInfo.do';
                }
                let objParam = params;
                objParam.pageIndex = page;//当前页
                objParam.pageSize = this.size;//页的条数
                this.$nextTick(()=>{
                    this.rightLoading = true;
                    this.$axios.post(this.$baseUrl+ url,this.$qs.stringify(objParam))
                        .then(res=>{
                            let obj = res.data;
                            this.rightLoading = false;
                            this.tableDataArr = [];
                            if(obj.success == 'true'){
                                setTimeout(()=>{
                                    this.allCounts = Number(obj.data[0].count);
                                    this.tableDataArr = obj.data[0].list;
                                },10)
                            }else{
                                this.allCounts = 0
                                layer.msg(obj.message,{icon:5})
                            }


                        })
                        .catch(err=>{
                            this.allCounts = 0
                            this.rightLoading = false;

                        })
                })
            },
            //分页
            handleCurrentChange(page){
                this.getProps(page,this.conditionFrom)
            },
            /*修改提交数据*/
            submitData(){
              /*  let url = '';
                if(this.currentType === 'table'){
                    url = '/dataSourceMetadata/update.do';
                }else{
                    url = '/dataSourceMetadata/update.do';
                }*/
                this.$nextTick(()=>{
                    this.formLoading = true;
                    this.$axios.post(this.$baseUrl+'/dataSourceMetadata/update.do',this.$qs.stringify(this.form))
                        .then(res=>{
                            this.formLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.formState = false;
                                this.formStateField = false;
                                this.tableDataArr[this.editIndex] = JSON.parse(JSON.stringify(this.form))
                                layer.msg(obj.message,{icon:1});
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.formLoading = false;
                        })
                })
            },


            /*转小写*/
            toLowcase(row, column, cellValue, index){
                if(cellValue){
                    let val = cellValue.toLowerCase();
                    return val
                }else {
                    return cellValue
                }
            },
            toEmpty(row, column, cellValue, index){
                if (cellValue == '0'){
                    return ''
                }
            },
            //转换布尔值
            changeVal(row, column, cellValue, index){
                if (cellValue == true){
                    return '是'
                } else if(cellValue == false){
                    return '否'
                }else{
                    return cellValue;
                }
            },


            /*表格懒加载*/
            load(tree, treeNode, resolve){
                let url = '';
                let param = {};
                if (this.currentType === 'template') {
                    url = '/metadata/getMedataByTemplateDynamically.do';
                    param = {
                        id:tree.id,
                        template:this.currentName
                    }
                }else{
                    url = '/metadata/getMedataByIndexDynamically.do';
                    param = {
                        id:tree.id,
                        index:this.currentName
                    }
                }
                this.$nextTick(()=>{
                    this.rightLoading = true;
                    this.$axios.post(this.$baseUrl+url,this.$qs.stringify(param))
                        .then(res=>{
                            this.rightLoading = false;
                            resolve(res.data)
                        })
                        .catch(err=>{
                            this.rightLoading = false;
                            console.log('获取失败')
                        })
                })
            }
        },
        components:{
            vBasetable
        }
    }
</script>

<style scoped>
    .prop-wapper{
        display: flex;
        height: 100%;
        width: calc(100% - 20px);
        padding: 10px;
    }
    .prop-wapper>div{
        /*margin: 10px;*/
        background: #303e4e;
    }
    .left-wapper{
        width: 250px;
        /*margin: 0 10px;*/
        height: calc(100vh - 150px);
        border: 1px solid #5a7494;

    }
    .right-wapper{
        width: calc(100% - 280px);
        /*flex: 1;*/
        padding: 10px;
        /*height: calc(100vh - 250px);*/
    }
    .left-tit{
        height: 2px;
        line-height: 40px;
        padding-left: 20px;
        /* background: #5a7494; */
        background: #3a8ee6;
        font-size: 15px;
        font-weight: 600;
    }
    .left-list{
        height: calc(100% - 15px);
        overflow-y: auto;
        padding: 5px;
    }
    .left-list li{
        height: 36px;
        line-height: 36px;
        padding:0 10px;
        font-size: 13px;
        border-bottom: 1px solid #40556d;
        text-align: center;
    }
    .left-list li:hover{
        background: #40556d;
        cursor: pointer;
    }
    .left-list .current-li{
        background: #4a678a;
    }
    .table-tit{
        height: 50px;
        line-height: 50px;
        border-bottom: 1px solid #5a7494;
        padding-left: 20px;
        color: #3a8ee6;
    }
    .table-box{
        /*height: calc(100vh - 300px);*/
    }
    .right-wapper /deep/ .el-table thead.is-group th {
        background: #485c73;
    }
    .right-wapper /deep/ .el-table--border, .el-table--group{
        border:0
    }
    .right-wapper /deep/ .el-table--border th, .el-table__fixed-right-patch {
        border-bottom: 1px solid #5a728e;
    }
    .right-wapper /deep/ .el-table--border th{
        border-right: 1px solid #5a728e;
    }
    .left-list /deep/ .el-tree-node__content{
        height: 30px;
        line-height: 30px;
        color: #D6DFEB;
    }
    .left-list /deep/ .el-tree-node__label{
        border-bottom: 1px solid #405165;
        color: #D6DFEB;
    }
    .right-wapper /deep/ .el-icon-arrow-right{
        color: #fff;
    }
    .left-list /deep/ .el-tree {
        width: 100%;
        height: 100%;
        /*overflow: scroll;*/
    }

    .left-list /deep/ .el-tree>.el-tree-node {
        display: inline-block;
        min-width: 100%;
    }
</style>
