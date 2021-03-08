<template>
    <div  class="content-bg" v-loading="loading" element-loading-background="rgba(48, 62, 78, 0.5)">
        <div class="top-title">数据源管理
            <div class="btn-wapper">
                <el-button type="primary" size="mini" plain @click="formState = true">添加</el-button>
                <el-button title="初始化选中项" type="warning" size="mini" plain :disabled="delectIds.length > 0 ? false : true "  @click="initialize">初始化</el-button>
                <el-button title="删除选中项" type="danger" size="mini" plain :disabled="delectIds.length > 0 ? false : true "  @click="remove">删除</el-button>
                <el-button type="success" size="mini" plain  @click="refresh">刷新</el-button>
            </div>
        </div>
        <div class="table-wapper">
            <basetable :selection="true" :table-head="tableHead" :table-data="tableData" :busName="tableBusName"></basetable>
        </div>
        <div class="table-page">
            <span>共检索到数据源 <b>{{allCounts}}</b> 个</span>
            <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="size" :total="allCounts"></el-pagination>
        </div>
        <!--添加与修改弹窗-->
        <el-dialog :title="editId === '' ? '添加':'修改'" :visible.sync="formState" width="500px" v-loading="formLoading" element-loading-background="rgba(48, 62, 78, 0.5)" :close-on-click-modal="falseB">
            <el-form label-width="100px">
                <el-form-item label="数据源名称:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="form.data_source_name" style="width: 90%;" size="mini" placeholder="" class="item"></el-input>
                </el-form-item>
                <!--<el-form-item label="数据源分类:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                     <el-select v-model="form.type" @change="" size="mini"  placeholder="请选择" style="width: 90%;">
                         <el-option
                             v-for="item in dataSourceType"
                             :key="item.value"
                             :label="item.label"
                             :value="item.value">
                         </el-option>
                     </el-select>
                </el-form-item>-->
                <el-form-item label="数据源类型:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-select v-model="form.data_source_item_type" size="mini"  placeholder="请选择" style="width: 90%;">
                        <el-option
                            v-for="item in childrenType"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="IP地址:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="form.data_source_ip" style="width: 90%;" size="mini" placeholder="" class="item"></el-input>
                </el-form-item>
                <el-form-item label="端口:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="form.data_source_port" style="width: 90%;" size="mini" placeholder="" class="item"></el-input>
                </el-form-item>
                <el-form-item label="用户名:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="form.data_source_username" style="width: 90%;" size="mini" placeholder="" class="item"></el-input>
                </el-form-item>
                <el-form-item label="密码:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input type="password" show-password v-model="form.data_source_password" style="width: 90%;" size="mini" placeholder="" class="item"></el-input>
                </el-form-item>
                <el-form-item label="数据库/实例:">
                    <span style="color:red;position: absolute;left: -10px;" v-if="form.data_source_item_type !== 'MySQL'">*</span>
                    <el-input v-model="form.data_source_dbname" style="width: 90%;" size="mini" placeholder="" class="item"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <div style="float: left;">
                    <el-button type="primary" plain  @click="testLink">测试连接</el-button>
                    <span v-if="linkObj.state" style="font-size: 14px;color: #1ab394;">{{linkObj.text}}</span>
                    <span v-else style="font-size: 14px;color: #e4956d;">{{linkObj.text}}</span>
                </div>
                <el-button @click="formState = false">取消</el-button>
                <el-button type="primary" @click="editId === '' ? addDataSource() : reviseDataSource()">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="元数据详情" :visible.sync="detailState" width="80vw" height="80vh" destroy-on-close v-loading="detailLoading" element-loading-background="rgba(48, 62, 78, 0.5)" :close-on-click-modal="falseB">
            <div class="detail-wapper">
                <div class="left-wapper">
                    <el-menu class="sidebar-el-menu" default-active="1" background-color="#405267" @open="titleClick"
                             text-color="#bfcbd9" active-text-color="#20a0ff">
                        <template v-for="(item,i) in leftList">
                            <el-submenu :index="item.id" :key="i" v-if="item.isExpand === 'true'">
                                <template slot="title" >
                                   <span slot="title">{{ item.label }}</span>
                                </template>
                                <template v-for="subItem in item.child">
                                    <el-menu-item :index="subItem.id" :key="subItem.id" @click="childClick(subItem)" :title="subItem.label" style="font-size: 10px;">
                                        {{ subItem.label }}
                                    </el-menu-item>
                                </template>
                            </el-submenu>
                            <el-menu-item :index="item.id" :key="item.id"  v-else @click="childClick(item)" :title="item.label">
                                <span slot="title">{{ item.label }}</span>
                            </el-menu-item>
                        </template>
                    </el-menu>

                </div>
                <div class="right-wapper">
                    <basetable :height="this.tableHeight" :table-head="fieldTableHead" :table-data="fieldTableData"></basetable>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import basetable from '../common/Basetable2'
    import bus from '../common/bus';
    export default {
        name: "index",
        data(){
            return{
                falseB:false,
                loading:false,
                //查询条件
                conditionFrom:{},
                page:1,//页码
                c_page:1,//当前页码
                size:15,//每页的数量
                allCounts:0,
                checkList:[],
                tableBusName:{
                    selectionName:'dataSourceDelect'
                },
                //删除id 集合
                delectIds:'',
                tableHead:[
                    {prop:'data_source_name',label:'数据源名称'},
                    {prop:'data_source_item_type',label:'数据库类型', width:'100',},
                    {prop:'description',label:'说明'},
                    {
                        prop:'data_source_is_initialized',
                        label:'是否初始化',
                        width:'100',
                        formatData:(val,obj)=>{
                            if(val == '1'){
                                return '是'
                            }else{
                                return '否'
                            }

                        }
                    },
                    {prop:'data_source_init_time',label:'初始化时间'},
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
                                    this.editId = row.data_source_id
                                    this.getDataSourceInfo();
                                }
                            },
                            {
                                icon:'el-icon-tickets',
                                text:'查看',
                                clickFun:(row,index)=>{
                                    this.detailState = true
                                    this.editId = row.data_source_id
                                    this.getLeftList()
                                }
                            },
                        ]}
                ],
                tableData:[
                    // {data_source_name:'123',data_source_item_type:'MySQL'}
                ],
                //修改id
                editId:'',
                formState:false,
                formLoading:false,
                //添加 表单
                form:{
                    data_source_name:'',//数据源名称
                   // type:'',//数据源分类
                    data_source_item_type:'',//数据源子类型
                    data_source_ip:'',//ip地址
                    data_source_port:'',//端口
                    data_source_username:'',//用户名
                    data_source_password:'',//密码
                    data_source_dbname:'',//数据库/实例
                },
                //数据源分类集合
                dataSourceType:[
                    {label:'关系型数据库',value:'1'},
                    {label:'大数据平台',value:'2'}
                ],
                //数据源子类型集合
                childrenType:[
                   /* {label:'MySQL',value:'MySQL'},
                    {label:'SQL sever',value:'SQL sever'},
                    {label:'Oracle',value:'Oracle'}*/
                ],
                //连接状态
                linkObj:{
                    state:true,
                    text:'连接成功'
                },
                //详情 弹窗状态
                detailState:false,
                detailLoading:false,
                leftList:[],
                fieldTableHead:[
                    {prop:'COLUMN_NAME',label:'字段名称'},
                    {prop:'DATA_TYPE',label:'数据类型'},
                    {prop:'LENGTH',label:'数据类型长度'},
                    {
                        prop:'IS_NULLABLE',
                        label:'是否为空',
                        formatData:(val,obj)=>{
                            if(val === 'YES'){
                                return "是"
                            }else{
                                return '否'
                            }
                        }
                    },
                    {prop:'COLUMN_COMMENT',label:'注释'},
                ],
                fieldTableData:[],
                tableHeight:0
            }
        },
        created(){
            this.getDataList(1,{})
            //监听选中的资产
            bus.$on(this.tableBusName.selectionName,(params)=>{
                this.delectIds = '';
                for(let i in params){
                    this.delectIds += params[i].data_source_id +','
                }
                //console.log(this.delectAlarmIds)
            })
            this.tableHeight = document.body.clientHeight - 370 ;
            window.onresize = () => {
                this.tableHeight = document.body.clientHeight - 370 ;
            };
        },
        watch:{
            'formState'(){
                if(this.formState){
                   this.getDBType()
                    this.linkObj={
                        state:true,
                        text:''
                    }
                }else{
                    this.editId = '';
                    this.clearForm();
                    /* this.getAsset('');
                    this.getEvent('');
                    this.form.event_alert_name = '';*/
                }
            },
            'detailState'(){
                if(!this.detailState){
                    this.editId = '';
                    this.leftList = []
                    this.fieldTableData = []
                }
            }
        },
        methods:{
            /*清空表单*/
            clearForm(){
                this.form={
                    data_source_name:'',//数据源名称
                    // type:'',//数据源分类
                    data_source_item_type:'',//数据源子类型
                    data_source_ip:'',//ip地址
                    data_source_port:'',//端口
                    data_source_username:'',//用户名
                    data_source_password:'',//密码
                    data_source_dbname:'',//数据库/实例
                }
            },
            /*初始化*/
            initialize(){
                //询问框
                layer.confirm('您确定初始化么？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    this.$nextTick(()=>{
                        this.loading = true;
                        this.$axios.post(this.$baseUrl+'/dataSource/dataSourceInit.do',this.$qs.stringify({
                            data_source_ids:this.delectIds
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
            /*刷新*/
            refresh(){
                this.getDataList(1,this.conditionFrom)
            },
            /*获取列表*/
            getDataList(page,param){
                let objParam = param;
                objParam.pageIndex = page;//当前页
                objParam.pageSize = this.size;//页的条数
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/dataSource/getDataSourceList.do',this.$qs.stringify(objParam))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.allCounts = Number(obj.data[0].count);
                                this.tableData = obj.data[0].list[0];
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
            getDataSourceInfo(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/dataSource/getDataSourceInfo.do',this.$qs.stringify({
                        data_source_id:this.editId
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
            /*获取数据库类型*/
            getDBType(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/dataSource/getDataSourceItemType.do',this.$qs.stringify(''))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                console.log(obj)
                                this.childrenType = obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*添加数据源*/
            addDataSource(){
                if(this.checkParam()){
                    this.$nextTick(()=>{
                        this.formLoading = true;
                        this.$axios.post(this.$baseUrl+'/dataSource/save.do',this.$qs.stringify(this.form))
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
            /*修改数据源*/
            reviseDataSource(){
                if(this.checkParam()){
                    this.$nextTick(()=>{
                        this.formLoading = true;
                        this.$axios.post(this.$baseUrl+'/dataSource/update.do',this.$qs.stringify(this.form))
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
                        this.$axios.post(this.$baseUrl+'/dataSource/delete.do',this.$qs.stringify({
                            data_source_ids:this.delectIds
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
                                    this.linkObj = {
                                        state:false,
                                        text:obj.message
                                    }
                                    //layer.msg(obj.message,{icon:5})
                                }
                            })
                            .catch(err=>{
                                this.formLoading = false;
                            })
                    })
                }
            },
            /*获取弹窗左侧列表*/
            getLeftList(){
                this.leftList = []
                this.$nextTick(()=>{
                    this.detailLoading = true;
                    this.$axios.post(this.$baseUrl+'/dataSource/getDataBaseOrTable.do',this.$qs.stringify({
                        data_source_id:this.editId
                    }))
                        .then(res=>{
                            this.detailLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.leftList = obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.detailLoading = false;
                        })
                })
            },
            /*详情左侧标题点击*/
            titleClick(key, path){
                console.log(key)
                let index = ''
                for(let i=0;i< this.leftList.length;i++){
                    if(key === this.leftList[i].id){
                        index = i;
                        break;
                    }
                }
                //获取子列表
                this.getChildren(index);
            },
            /*获取子菜单*/
            getChildren(index){
                this.$nextTick(()=>{
                    this.detailLoading = true;
                    this.$axios.post(this.$baseUrl+'/dataSource/getTablesByDatabase.do',this.$qs.stringify({
                        data_source_id:this.editId,
                        database:this.leftList[index].id
                    }))
                        .then(res=>{
                            this.detailLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.leftList[index].child = obj.data
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.detailLoading = false;
                        })
                })
            },
            /*获取字段数据*/
            childClick(item){
               this.$nextTick(()=>{
                   this.detailLoading = true;
                   this.$axios.post(this.$baseUrl+'/dataSource/getTableFields.do',this.$qs.stringify({
                       data_source_id:this.editId,
                       database:item.database,
                       table:item.id
                   }))
                       .then(res=>{
                           this.detailLoading = false;
                           let obj = res.data;
                           if(obj.success == 'true'){
                               this.fieldTableData = obj.data;
                           }else{
                               layer.msg(obj.message,{icon:5})
                           }
                       })
                       .catch(err=>{
                            this.detailLoading = false;
                       })
               })
            },
            /*验证from参数合法性*/
            checkParam(){
                if(this.form.data_source_name === ''){
                    layer.msg("数据源名称不能为空",{icon:5});
                    return false
                }else if(this.form.data_source_item_type === ''){
                    layer.msg("数据源类型不能为空",{icon:5});
                    return false
                }else if(this.form.data_source_ip === ''){
                    layer.msg("IP不能为空",{icon:5});
                    return false
                }else if(this.form.data_source_port === ''){
                    layer.msg("端口不能为空",{icon:5});
                    return false
                } else if(this.form.data_source_username  === ''){
                    layer.msg("用户名不能为空",{icon:5});
                    return false
                } else if(this.form.data_source_password  === ''){
                    layer.msg("密码不能为空",{icon:5});
                    return false
                } else if(this.form.data_source_item_type  !== 'MySQL' && this.form.data_source_dbname === ''){
                    layer.msg("数据库类型为"+this.form.data_source_item_type+"时，数据库/实例不能为空",{icon:5});
                    return false
                }else{
                    return true
                }
            },
        },
        components:{
            basetable
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
    .detail-wapper{
        display: flex;
        width: 100%;
        height: 64vh;
    }
    .left-wapper{
        width: 210px;
        overflow-x: hidden;
    }
    .left-wapper /deep/ .el-submenu{
        border-bottom: 1px solid #4d6077;
    }
    .left-wapper /deep/ .el-submenu__title{
        height: 34px;
        line-height: 34px;
    }
    .left-wapper /deep/ .el-submenu .el-menu-item{
        height: 34px;
        line-height: 34px;
        overflow: hidden;
        min-width: 100px;
    }
    .left-wapper /deep/ .el-menu-item{
        height: 34px;
        line-height: 34px;
    }
    .right-wapper{
        flex:1;
        height: 100%;
        overflow: auto;
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
