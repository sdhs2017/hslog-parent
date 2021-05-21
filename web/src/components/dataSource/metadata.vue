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
                <h3 class="table-tit">{{currentName}}
                    <el-button style="float: right;margin: 10px" v-if="this.currentType === 'field'" type="primary" size="mini" plain @click="dataBtn()">数据预览</el-button>
                </h3>
                <vBasetable :tableHead="tableHead" :height="this.tableHeight" :tableData="tableDataArr"></vBasetable>
                <div class="table-page" style="display: flex;justify-content: flex-end;align-items: center;height: 40px">
                    <span>总数为 <b>{{allCounts}} 条 </b></span>
                    <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="size" :total="allCounts"></el-pagination>
                </div>
            </div>
        </div>
        <!--配置弹窗 数据库-->
        <el-dialog title="修改" :visible.sync="databaseFormState" width="500px" v-loading="formLoading" element-loading-background="rgba(48, 62, 78, 0.5)" :close-on-click-modal="falseB">
            <el-form label-width="100px">
                <el-form-item label="表名:">
                    <el-input v-model="form.metadata_database_name" style="width: 90%;" size="mini" disabled class="item"></el-input>
                </el-form-item>
            </el-form>
            <el-form label-width="100px">
                <el-form-item label="是否自动发现:">
                    <el-select v-model="form.metadata_is_auto_discovery" size="mini" placeholder="请选择" style="width: 90%;">
                        <el-option
                            v-for="item in autoDiscoveryOpt"
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
                <el-button @click="databaseFormState = false">取 消</el-button>
            </div>
        </el-dialog>

        <!--配置弹窗 表-->
        <el-dialog title="修改" :visible.sync="formState" width="500px" v-loading="formLoading" element-loading-background="rgba(48, 62, 78, 0.5)" :close-on-click-modal="falseB">
            <el-form label-width="100px">
                <el-form-item label="表名:">
                    <el-input v-model="form.metadata_table_name" style="width: 90%;" size="mini" disabled class="item"></el-input>
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
                <el-form-item label="是否自动发现:">
                    <el-select v-model="form.metadata_is_auto_discovery" size="mini"  placeholder="请选择" style="width: 90%;">
                        <el-option
                            v-for="item in autoDiscoveryOpt"
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
            <el-form label-width="100px" style="height: 450px;overflow: auto">
                <el-form-item label="字段名:">
                    <el-input v-model="form.metadata_field_name" style="width: 90%;" size="mini" disabled class="item"></el-input>
                </el-form-item>
                <el-form-item label="字段类型:">
                    <el-input v-model="form.metadata_field_type" style="width: 90%;" size="mini" disabled class="item"></el-input>
                </el-form-item>
                <el-form-item label="字段长度:">
                    <el-input v-model="form.metadata_field_length" style="width: 90%;" size="mini" disabled class="item"></el-input>
                </el-form-item>
                <el-form-item label="是否为空:">
                    <el-input v-model="form.metadata_field_isnull" style="width: 90%;" size="mini" disabled class="item"></el-input>
                </el-form-item>
                <el-form-item label="字段注释:">
                    <el-input v-model="form.metadata_field_comment" style="width: 90%;" size="mini" disabled class="item"></el-input>
                </el-form-item>
                <el-form-item label="敏感级别:">
                    <el-select v-model="form.metadata_field_sensitiveLevel" size="mini" clearable placeholder="请选择" style="width: 90%;">
                        <el-option
                            v-for="item in sensitiveLevel"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="分类分级:">
                    <div class="" style="width: 95%;">
                        <div class="selectTreeItem" v-for="(selectItem,i) in selectTreeValArr">
                            <v-select-tree :options="selectTreeOptions" :value="selectItem.value" :idName="'id-'+i"></v-select-tree>
                            <!--                            {{selectItem.label}}-->
                            <i class="el-icon-close" @click="selectTreeValArr.splice(i,1)" style="cursor: pointer;color: #A84250;margin-left: 5px;"></i>
                        </div>
                        <el-button type="primary" @click="addIdentifys()" style="width: 95%;" size="mini">添加</el-button>
                    </div>
                </el-form-item>
                <el-form label-width="100px">
                    <el-form-item label="是否自动发现:">
                        <el-select v-model="form.metadata_is_auto_discovery" size="mini"  placeholder="请选择" style="width: 90%;">
                            <el-option
                                v-for="item in autoDiscoveryOpt"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-form>
                <el-form-item label="发现规则:">
                    <!--<el-select v-model="form.metadata_field_sensitiveLevel" size="mini" clearable placeholder="请选择" style="width: 90%;">
                        <el-option
                            v-for="item in sensitiveLevel"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>-->
                    <div class="" style="width: 95%;">
                        <div class="selectTreeItem" v-for="(selectItem,i) in selectedTagArr">
                            <el-select v-model="selectItem.tagVal" size="mini" placeholder="请选择" style="width: 90%;">
                                <el-option
                                    v-for="item in tagArr"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                            <!--                            {{selectItem.label}}-->
                            <i class="el-icon-close" @click="selectedTagArr.splice(i,1)" style="cursor: pointer;color: #A84250;margin-left: 5px;"></i>
                        </div>
                        <el-button type="primary" @click="addTags()" style="width: 95%;" size="mini">添加</el-button>
                    </div>
                </el-form-item>
                <el-form-item label="标注:">
                    <el-input v-model="form.metadata_remark" style="width: 90%;" size="mini" class="item"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="submitData">确 定</el-button>
                <el-button @click="formStateField = false">取 消</el-button>
            </div>
        </el-dialog>

        <!--数据预览-->
        <el-dialog title="数据预览" :visible.sync="tableDialogState" width="80vw" height="80vh" destroy-on-close v-loading="detailLoading" element-loading-background="rgba(48, 62, 78, 0.5)" :close-on-click-modal="falseB">
            <div class="detail-wapper">
                <vBasetable :tableHead="tableDialogHead" :tableData="tableDialogData" :height="tableDialogHeight"></vBasetable>
            </div>
        </el-dialog>

        <!--分类标识-->
        <el-dialog title="分类标识" :visible.sync="identifysDialogState" width="200" destroy-on-close :close-on-click-modal="falseB">
            <v-select-tree :options="selectTreeOptions" :value="selectIdentifys.value"></v-select-tree>
        </el-dialog>
    </div>

</template>

<script>
    import vBasetable from '../common/Basetable2';
    import vSelectTree from '../common/selectTree_n';
    import bus from '../common/bus';
    export default {
        name: "metadata",
        data() {
            return {
                falseB:false,
                leftLoading:false,
                rightLoading:false,
                formLoading:false,
                formState:false,
                databaseFormState:false,
                formStateField:false,
                identifysDialogState:false,
                allCounts:0,
                page:1,//页码
                c_page:1,//当前页码
                size:15,//每页的数量
                //修改  index
                editIndex:'',
                //修改  表单
                form:{},
                //用于存放下拉树 选中的值 c72d8368-f723-4555-a051-8c31662c338f,ccfe3107-7a04-4cee-9121-6049b1dc7b1a,a3de7ece-c4db-4a88-ac10-f66836ef47e7,
                selectTreeValArr:[],
                //是否自动发现
                autoDiscoveryOpt:[],
                //标签
                tagArr:[],
                //选中的标签
                selectedTagArr:[],
                //分类标识树  数据
                selectTreeOptions:[],
                //修改选中的分类标识
                selectIdentifys:{value:'',label:''},
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
                //数据库
                firstHead:[{
                    prop:'metadata_database_name',
                    label:'数据库名称',
                    width:''
                },{
                    prop:'metadata_is_auto_discovery',
                    label:'是否自动发现',
                    width:'',
                    formatData:(val,obj)=>{
                        let label = '';
                        this.autoDiscoveryOpt.forEach(item=>{
                            if(item.value == val){
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
                                //this.form = JSON.parse(JSON.stringify(row));
                                this.databaseFormState = true;
                                this.editIndex = index;
                                this.$nextTick(()=>{
                                    this.loading = true;
                                    this.$axios.post(this.$baseUrl+'/dataSourceMetadata/getMetadataDatabaseInfo.do',this.$qs.stringify({
                                        metadata_database_id:row.metadata_database_id
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
                            }
                        },
                    ]}
                ],
                //表
                secondHead:[{
                    prop:'metadata_table_name',
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
                    prop:'metadata_is_auto_discovery',
                    label:'是否自动发现',
                    width:'',
                    formatData:(val,obj)=>{
                        let label = '';
                        this.autoDiscoveryOpt.forEach(item=>{
                            if(item.value == val){
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
                               // this.form = JSON.parse(JSON.stringify(row));
                                this.formState = true;
                                this.editIndex = index;
                                this.$nextTick(()=>{
                                    this.loading = true;
                                    this.$axios.post(this.$baseUrl+'/dataSourceMetadata/getMetadataTableInfo.do',this.$qs.stringify({
                                        metadata_table_id:row.metadata_table_id
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
                            }
                        },
                    ]}
                ],
                //字段
                thirdHead:[{
                    prop:'metadata_field_name',
                    label:'字段名',
                    width:''
                },{
                    prop:'metadata_field_type',
                    label:'字段类型',
                    width:'',
                },{
                    prop:'metadata_field_length',
                    label:'字段长度',
                    width:'',
                },{
                    prop:'metadata_field_isnull',
                    label:'是否为空',
                    width:'',
                },{
                    prop:'metadata_field_comment',
                    label:'字段注释',
                    width:'',
                },{
                    prop:'metadata_field_sensitiveLevel',
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
                    prop:'metadata_identify_names',
                    label:'分类分级',
                    width:'',
                },{
                    prop:'metadata_label_names',
                    label:'发现规则',
                    width:'',
                },{
                    prop:'metadata_is_auto_discovery',
                    label:'是否自动发现',
                    width:'',
                    formatData:(val,obj)=>{
                        let label = '';
                        this.autoDiscoveryOpt.forEach(item=>{
                            if(item.value == val){
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
                                this.getTagArr()
                                this.selectedTagArr = [];
                                let nameArr = []
                                if(row.metadata_identify_names){
                                    nameArr = row.metadata_identify_names.split(';')
                                }
                                this.$nextTick(()=>{
                                    this.rightLoading = true;
                                    this.$axios.post(this.$baseUrl+'/dataSourceMetadata/getMetadataFieldInfo.do',this.$qs.stringify({
                                        metadata_field_id : row.metadata_field_id
                                    }))
                                        .then(res=>{
                                            this.rightLoading = false;
                                            this.selectTreeValArr=[];
                                            let obj = res.data;
                                            if(obj.success == 'true'){
                                                this.form = obj.data;
                                                if(obj.data.metadata_identify_ids){
                                                    let idArr = obj.data.metadata_identify_ids.split(',');
                                                    for (let i in idArr){
                                                        if(idArr[i] !== ''){
                                                            this.selectTreeValArr.push({
                                                                value:idArr[i],
                                                                label:nameArr[i]
                                                            })
                                                        }
                                                    }
                                                }
                                                if(obj.data.metadata_label_ids){
                                                    let idArr = obj.data.metadata_label_ids.split(',');
                                                    for (let i in idArr){
                                                        if(idArr[i] !== ''){
                                                            this.selectedTagArr.push({
                                                               tagVal:idArr[i]
                                                            })
                                                        }
                                                    }
                                                }
                                                this.formStateField = true;
                                                this.editIndex = index;
                                                //let nameArr = obj.data.metadata_identify_names.split(',')

                                            }else{
                                                layer.msg(obj.message,{icon:5})
                                            }
                                        })
                                        .catch(err=>{
                                             this.rightLoading = false;
                                        })
                                })


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
                currentType:'',
                //数据预览表格弹窗状态
                tableDialogState:false,
                detailLoading:false,
                tableDialogHeight:0,
                tableDialogHead:[],
                tableDialogData:[],

            }
        },
        created(){
            //获取数据
            // this.getProps('hslog_packet');
            this.getSelectTree()
            this.getTreeData();
            this.getTableType();
            this.getTagArr()
            this.getSensitiveLevel();
            this.getAutoDiscovery();
            this.tableHeight = document.body.clientHeight - 260 ;
            window.onresize = () => {
                /*this.tableHeight = document.body.clientHeight - 260 ;
                this.tableDialogHeight = document.body.clientHeight - 370 ;*/
                let h1 = document.body.clientHeight - 260 ;
                this.tableHeight =  h1 < 320 ? 320 : h1;
                let h2 = document.body.clientHeight - 370 ;
                this.tableDialogHeight =  h2 < 320 ? 320 : h2;
            };
            bus.$on('getValue',obj=>{
                //console.log(obj)
                let k = Object.keys(obj)[0];
                let index = k.split('-')[1];
                this.selectTreeValArr[index] = obj[k]
                //console.log(this.selectTreeValArr)
            })
        },
        watch:{
            'tableDialogState'(nv,ov){
                /*if(nv){
                    this.tableDialogHead=[]
                    this.tableDialogData=[]
                    this.getFieldData()
                }else{

                }*/
            }
        },
        methods:{
            /*索引列表点击*/
            /*chooseIndex(item,index){
                this.currentIndex = index;
                this.currentName = item;
                this.getProps(this.currentName)
            },*/
            /*获取自动发现 值*/
            getAutoDiscovery(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/dataSourceMetadata/getIsAutoDiscoveryCombobox.do',this.$qs.stringify())
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.autoDiscoveryOpt = obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.loading = false;
                        })
                })
            },
            /*获取标签*/
            getTagArr(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/DSGLabel/getLabelAll4Combobox.do',this.$qs.stringify())
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.tagArr = obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            //添加分类标识
            addIdentifys(){
                this.selectTreeValArr.push({value:'',label:''})
            },
            /*添加标签*/
            addTags(){
                //let propName = 'tag_' + this.selectedTagArr.length;
                this.selectedTagArr.push({tagVal:''})
            },
            /*获取分类标识下来树数据*/
            getSelectTree(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/metadataIdentify/getMetadataIdentifyTree.do',this.$qs.stringify())
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.selectTreeOptions = obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
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

                if(node.state  == 1){
                    this.currentName = node.menuName;
                    this.currentType = 'database';
                    this.conditionFrom = {
                        // metadata_database:node.database,
                        data_source_id:node.data_source_id
                    }
                    this.tableHead=this.firstHead
                    this.getProps(1,this.conditionFrom)

                }else if (node.state  == 2){
                    this.currentName = node.menuName;
                    this.currentType = 'table'
                    this.conditionFrom = {
                        metadata_database:node.database,
                        data_source_id:node.data_source_id
                    }
                    this.tableHead=this.secondHead
                    this.getProps(1,this.conditionFrom)
                } else if (node.state == 3) {
                    this.currentName = node.menuName;
                    // console.log(node)
                     this.currentType = 'field'
                    // let url = '/metadata/getMedataByIndexDynamically.do';
                     this.conditionFrom = {
                         metadata_database:node.database,
                         data_source_id:node.data_source_id,
                         metadata_table:node.table
                     }
                    this.tableHead=this.thirdHead
                    this.getProps(1,this.conditionFrom)
                }else{
                   // this.currentType = ''
                }

            },
            /*获取propsData*/
            getProps(page,params){
                this.c_page = page;
                let url = '';
                if(this.currentType === 'table'){
                    url = '/dataSourceMetadata/getTableInfo.do';
                }else if(this.currentType === 'field'){
                    url = '/dataSourceMetadata/getFieldInfo.do';
                }else if(this.currentType === 'database'){
                    url = '/dataSourceMetadata/getDatabaseInfo.do';
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
                let url = '';
                if(this.currentType === 'database'){
                    url = '/dataSourceMetadata/updateMetadataDatabaseInfo.do';
                }else if(this.currentType === 'table'){
                    url = '/dataSourceMetadata/updateMetadataTableInfo.do';
                }else if(this.currentType === 'field'){
                    url = '/dataSourceMetadata/updateMetadataFieldInfo.do';
                    //标识
                    let ids = '';
                    let names = '';
                    for(let i=0;i<this.selectTreeValArr.length;i++){
                        if(this.selectTreeValArr[i].value !== ''){
                            ids += this.selectTreeValArr[i].value + ',';
                            names += this.selectTreeValArr[i].label + ',';
                        }else{
                            layer.msg('分类标识有未选择项',{icon:5});
                            return false
                        }
                    }
                    this.form.metadata_identify_ids = ids;
                    this.form.metadata_identify_names = names;
                    //标签
                    let tagIds = '';
                    let tagNames = '';
                    for(let i=0;i<this.selectedTagArr.length;i++){
                        let obj = this.selectedTagArr[i];
                        if(obj.tagVal !== ''){
                            tagIds += obj.tagVal +','
                            for(let j in this.tagArr){
                                if(obj.tagVal === this.tagArr[j].value){
                                    tagNames += this.tagArr[j].label+','
                                    break
                                }

                            }
                        }else{
                            layer.msg('标签有未选择项',{icon:5});
                            return false
                        }
                    }
                    this.form.metadata_label_ids = tagIds;
                    this.form.metadata_label_names = tagNames;
                }
                this.$nextTick(()=>{
                    this.formLoading = true;
                    this.$axios.post(this.$baseUrl+url,this.$qs.stringify(this.form))
                        .then(res=>{
                            this.formLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.formState = false;
                                this.formStateField = false;
                                this.databaseFormState = false;
                                //this.tableDataArr[this.editIndex] = JSON.parse(JSON.stringify(this.form))
                                this.getProps(this.c_page,this.conditionFrom)
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

            /*预览按钮*/
            dataBtn(){
                this.tableDialogState = true;
                this.getFieldData()
            },
            /*数据预览*/
            getFieldData(){
                this.tableDialogHead=[]
                this.tableDialogData=[]
                this.$nextTick(()=>{
                    this.tableDialogHeight = document.body.clientHeight - 370 ;
                    this.detailLoading = true;
                    this.$axios.post(this.$baseUrl+'/dataSourceMetadata/getDataPreview.do',this.$qs.stringify({
                        database:this.conditionFrom.metadata_database,
                        table:this.conditionFrom.metadata_table,
                        data_source_id:this.conditionFrom.data_source_id
                    }))
                        .then(res=>{
                            this.detailLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.tableDialogHead = obj.data[0].fields
                                this.tableDialogData = obj.data[0].data
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.detailLoading = false;
                        })
                })
            }

        },
        components:{
            vBasetable,
            vSelectTree
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
    .detail-wapper{
        width: 100%;
        height: 64vh;
        overflow: auto;
    }
    /deep/ .el-input.is-disabled .el-input__inner {
        background-color: #303e4e;
        border-color: #5a7494;
    }
    .selectTreeItem{
       /* height: 30px;
        border-radius: 5px;
        border: 1px solid #ccc;
        display:flex;
        align-items: center;
        margin-bottom: 5px;*/
        display: flex;
        align-items: center;
        margin-bottom: 5px;
    }
</style>
