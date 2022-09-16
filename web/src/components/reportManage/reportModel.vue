<template>
    <div class="content-bg">
        <div class="top-title">报告模板</div>
        <div class="tools-btns">
            <el-button type="success" size="mini" plain @click="addDialogFormVisible = true">添加</el-button>
            <el-button type="primary" size="mini" plain @click="reviseReportModel">修改</el-button>
            <el-button type="danger" size="mini" plain @click="removeModel">删除</el-button>
        </div>
        <div class="report-tools-form">
            <v-search-form :formItem="formConditionsArr" :busName="busName"></v-search-form>
        </div>
        <div class="table-wapper" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
            <v-basetable :selection="true" :tableHead="tableHead" :tableData="tableData" :busName="tableBusNames"></v-basetable>
        </div>
        <div class="table-page">
            <span>共检索到报告模板为 <b>{{allCounts}}</b> 个</span>
            <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="size" :total="allCounts"></el-pagination>
        </div>

        <el-dialog title="添加报告模板-基础信息" :visible.sync="addDialogFormVisible" width="650px" :close-on-click-modal = "false" top="5vh">
            <div class="dialog-content" style="height: 400px">
                <el-form :model="dialogInfo" label-width="80px" style="background: #3b4d60; padding: 10px;position: relative;">
                    <el-form-item label="报告名称">
                        <span style="color: red;position: absolute;left:-10px">*</span>
                        <el-input v-model="addFrom.reportModelName" size="mini"></el-input>
                    </el-form-item>
                    <el-form-item label="报告类型">
                        <span style="color: red;position: absolute;left:-10px">*</span>
                        <el-select v-model="addFrom.reportModelType" size="mini" placeholder="报告类型" style="width: 100%;">
                            <el-option :label="item.label" :value="item.value" v-for="(item,i) in reportModelTypeOpt" :key="i"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="页眉">
                        <el-input type="textarea" v-model="addFrom.reportModelHeader" size="mini"></el-input>
                    </el-form-item>
                    <el-form-item label="页脚">
                        <el-input type="textarea" v-model="addFrom.reportModelFooter" size="mini"></el-input>
                    </el-form-item>
                    <el-form-item label="仪表板" >
                        <span style="color: red;position: absolute;left:-10px">*</span>
                        <el-select v-model="addFrom.dashboardID" size="mini" placeholder="仪表板ID" style="width: 100%;" @change="dashboardChange()">
                            <el-option :label="item.label" :value="item.value" v-for="(item,i) in dashboardIDOpt" :key="i"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="继承模板" >
                        <el-select v-model="addFrom.extendReportModelID" size="mini" placeholder="无" style="width: 100%;" >
                            <el-option :label="item.label" :value="item.value" v-for="(item,i) in inheritModelOpt" :key="i"></el-option>
                        </el-select>
                    </el-form-item>
                    <p style="color:#ff9e25;font-size: 10px;position: relative;top: -10px;padding-left: 80px;">注：新增模板内容是否继承于已存在的模板，无表示新建自定义空模板</p>
                    <el-form-item label="说明" class="explainWapper">
                        <el-input type="textarea" v-model="addFrom.reportModelExplain" size="mini"></el-input>
                    </el-form-item>
                </el-form>
            </div>
            <div slot="footer" class="dialog-footer">
               <el-button @click="addDialogFormVisible = false">取 消</el-button>
               <el-button type="primary" @click="addModel"   :disabled="addFrom.reportModelName !== '' && addFrom.reportModelType !== '' && addFrom.dashboardID !== '' ? false : 'disabled'">提 交</el-button>
           </div>
        </el-dialog>

        <!-- 查看 / 修改 -->
        <el-dialog :title="dialogType === 'see' ? '查看报告模板' : '修改报告模板'" :visible.sync="dialogFormVisible" width="1000px" :close-on-click-modal = "false" top="5vh">
            <div class="dialog-content">
                <el-tabs v-model="activeName" type="border-card" style="height: 95%;">
                    <el-tab-pane label="基本信息" name="first"  style="height: 100%;">
                        <div class="baseInfo" style="height: 100%;display: flex;justify-content: center;" v-loading="treeLoading"  element-loading-background="rgba(48, 62, 78, 0.5)">
                            <el-form :model="dialogInfo" label-width="80px" style="background: #3b4d60; padding: 10px;height: calc(100% - 15px);overflow: auto;width: 60%;position: relative;">
                                <div style="width: 90%;height:95%;position: absolute;z-index: 10;" v-if="dialogType === 'see'"></div>
                                <el-form-item label="报告名称">
                                    <span style="color: red;position: absolute;left:-10px">*</span>
                                    <el-input v-model="dialogInfo.reportModel.reportModelName" size="mini"></el-input>
                                </el-form-item>
                                <el-form-item label="报告类型">
                                    <span style="color: red;position: absolute;left:-10px">*</span>
                                    <el-select v-model="dialogInfo.reportModel.reportModelType" size="mini" placeholder="报告类型" style="width: 100%;">
                                        <el-option :label="item.label" :value="item.value" v-for="(item,i) in reportModelTypeOpt" :key="i"></el-option>
                                    </el-select>
                                </el-form-item>
                                <el-form-item label="页眉">
                                    <el-input type="textarea" v-model="dialogInfo.reportModel.reportModelHeader" size="mini"></el-input>
                                </el-form-item>
                                <el-form-item label="页脚">
                                    <el-input type="textarea" v-model="dialogInfo.reportModel.reportModelFooter" size="mini"></el-input>
                                </el-form-item>
                                <el-form-item label="仪表板" >
                                    <span style="color: red;position: absolute;left:-10px">*</span>
                                    <el-select v-model="dialogInfo.reportModel.dashboardID" size="mini" placeholder="仪表板ID" style="width: 100%;" disabled="disabled">
                                        <el-option :label="item.label" :value="item.value" v-for="(item,i) in dashboardIDOpt" :key="i"></el-option>
                                    </el-select>
<!--                                    <el-input type="textarea" v-model="dialogInfo.reportModel.dashboardID" size="mini"></el-input>-->
                                </el-form-item>
                                <el-form-item label="说明" class="explainWapper">
                                    <el-input type="textarea" v-model="dialogInfo.reportModel.reportModelExplain" size="mini"></el-input>
                                </el-form-item>
                                <p style="text-align: center;margin-top: 10px;" v-if="dialogType === 'resive'">
                                    <el-button type="primary" @click="sendBaseInfo"  :disabled="dialogInfo.reportModel.reportModelName !== '' ? false : 'disabled'">提交修改</el-button>
                                </p>

                            </el-form>

                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="模板信息" name="second" style="height: 95%;">
                        <div class="detailWapper" >
                            <div class="treeWapper"  v-loading="leftLoading"  element-loading-background="rgba(48, 62, 78, 0.5)">
                                <div class="treeCon">
                                    <el-tree :data="dialogInfo.reportModelInfo" :props="defaultProps" @node-click="nodeClick" :expand-on-click-node="false" @node-drop="handleDrop" :draggable="dialogType === 'see' ? false : true">
                                     <span class="custom-tree-node" slot-scope="{ node, data }">
                                        <span class="item-con">{{ node.label}}</span>
                                         <span class="item-btn" v-if="dialogType !== 'see'">
                                             <i class="el-icon-plus" title="添加子节点" @click.stop="appendNode(data)"></i>
                                             <i class="el-icon-close" title="删除" @click.stop="deleteNode(data)"></i>
                                        </span>
                                     </span>
                                    </el-tree>
                                </div>
                                <p class="appendNode"  @click="appendNode(dialogInfo.reportModelInfo,true)" v-if="dialogType !== 'see'">添加节点</p>
                            </div>
                            <div class="formWapper" v-loading="treeLoading"  element-loading-background="rgba(48, 62, 78, 0.5)" style="position: relative;">
                                <div style="position:relative;padding: 0 10px;border-bottom: 1px solid #50667e;margin-bottom: 5px;height: 50px;">
                                    <div style="font-size: 16px;color: #2d8cf0;font-weight: 600;margin-right: 10px;width: calc(100% - 100px);position: absolute;top: 10px;" class="ovd">{{nodeName}}</div>
                                    <el-button style="position: absolute;right: 0;top: 5px;" type="primary" :disabled="btnDisabled ? 'disabled' : false" @click="putResive" v-if="dialogType === 'resive' && nodeForm.reportModelInfoID">提交修改</el-button>
                                </div>
                                <el-form label-width="80px" style="height: calc(100% - 52px);overflow: auto;overflow-x: hidden;position: relative;">
                                    <div style="width: 100%;height:100%;position: absolute;z-index: 10;" v-if="dialogType === 'see'"></div>

                                    <el-form-item label="内容类型">
                                        <el-select v-model="nodeForm.contentType" size="mini" placeholder="内容类型" @change="contentTypeChange">
                                            <el-option :label="item.label" :value="item.value" v-for="(item,i) in contentTypeOpt" :key="i"></el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="图表名称" v-if="nodeForm.contentType === 'image'">
                                        <span style="color: red;position: absolute;left:-10px">*</span>
                                        <el-input v-model="nodeForm.imageName" size="mini"></el-input>
                                    </el-form-item>
                                    <el-form-item label="表格名称" v-if="nodeForm.contentType === 'table'">
                                        <span style="color: red;position: absolute;left:-10px">*</span>
                                        <el-input v-model="nodeForm.tableName" size="mini"></el-input>
                                    </el-form-item>
                                    <el-form-item label="内容">
                                        <el-select v-model="nodeForm.content" size="mini" style="width: 100%;" v-if="nodeForm.contentType === 'table'">
                                            <el-option :label="item.label" :value="item.value" v-for="(item,i) in tableIdOpt" :key="i"></el-option>
                                        </el-select>
                                        <el-select v-model="nodeForm.content" size="mini" style="width: 100%;" v-else-if="nodeForm.contentType === 'image'">
                                            <el-option :label="item.label" :value="item.value" v-for="(item,i) in imgIdOpt" :key="i"></el-option>
                                        </el-select>

                                        <div v-else-if="nodeForm.contentType === 'text'">
                                            <p style="color:#ff9e25;font-size: 10px;">文本类型的内容填写规则：文本+指标数据统计（下拉框 可选择‘空’）</p>
                                            <div>
                                                <div v-for="(item,i) in textContentArr" :key="i" style="display: flex;align-items: center;">
                                                    <el-input v-model="item.text" size="mini" style="width: 65%;margin-right: 10px;" @change="textConChange"></el-input>
                                                    <el-select v-model="item.metricID" size="mini" style="width: 30%;" @change="textConChange">
                                                        <el-option :label="o.label" :value="o.value" v-for="(o,i) in metricOpt" :key="i"></el-option>
                                                    </el-select>
                                                    <i class="el-icon-close" @click="textContentArr.splice(i,1)" style="cursor: pointer;margin-left: 7px;"></i>
                                                </div>
                                            </div>
                                            <div class="addText" v-if="dialogType !== 'see'" style="height: 26px;line-height: 26px;margin-top: 5px;" @click="addText">添加</div>
                                        </div>
                                        <el-input type="textarea" v-else v-model="nodeForm.content" size="mini"></el-input>
                                    </el-form-item>

                                    <el-form-item label="是否加粗" v-if="nodeForm.contentType === 'text'">
                                        <el-radio-group v-model="nodeForm.bold">
                                            <el-radio :label="1">是</el-radio>
                                            <el-radio :label="0">否</el-radio>
                                        </el-radio-group>
                                    </el-form-item>
                                    <el-form-item label="位置" v-if="nodeForm.contentType === 'text'">
                                        <el-select v-model="nodeForm.alignment" size="mini" placeholder="位置" style="width: 100%;">
                                            <el-option :label="item.label" :value="item.value" v-for="(item,i) in alignmentOpt" :key="i"></el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="字号" v-if="nodeForm.contentType === 'text'">
                                        <el-input-number size="mini" :min='1' v-model="nodeForm.fontSize"></el-input-number>
                                    </el-form-item>
                                    <el-form-item label="段前（磅）" v-if="nodeForm.contentType === 'text'">
                                        <el-input-number size="mini" :min='0' v-model="nodeForm.spacingBefore"></el-input-number>
                                    </el-form-item>

                                    <el-form-item label="备注" class="explainWapper">
                                        <el-input type="textarea" v-model="nodeForm.remark" size="mini"></el-input>
                                    </el-form-item>
                                </el-form>
<!--                                <el-button type="primary" @click="" >提交修改</el-button>-->
                            </div>
                        </div>
                    </el-tab-pane>
                </el-tabs>

            </div>
            <!--<div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveReportModel" >提 交</el-button>
            </div>-->
        </el-dialog>

    </div>
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vBasetable from '../common/Basetable2';
    import {jumpHtml} from "../../../static/js/common";
    import bus from '../common/bus';
    export default {
        name: "reportModel",
        components:{
            vSearchForm,
            vBasetable
        },
        data(){
            return{
                loading:false,
                treeLoading:false,
                leftLoading:false,
                //添加  弹窗
                addDialogFormVisible:false,
                //添加 from表单
                addFrom:{
                    reportModelName:"",
                    reportModelType:"",
                    reportModelHeader:"",
                    reportModelfooter:"",
                    dashboardID:"",
                    reportModelExplain:"",
                    extendReportModelID:"",
                },
                //报告类型
                reportModelTypeOpt:[
                    {label:'日报',value:'day'},
                    {label:'周报',value:'week'},
                    {label:'月报',value:'month'},
                ],
                formConditionsArr:[],
                searchConditions:{//查询条件
                    reportModelName:'',
                    reportModelType:'',
                },
                saveCondition:'',//用于分页
                //查询绑定 busname
                busName:'reportModelSearch',
                //表格 监听事件名称
                tableBusNames:{
                    selectionName:'tableSelectedBusName'
                },
                //表格多选存放数组
                selectReportModelArr:[],
                allCounts:0,//总数
                page:1,//页码
                c_page:1,//当前页码
                size:15,//每页的数量
                //表头
                tableHead:[
                    {
                        prop:'reportModelName',
                        label:'报告名称',
                        width:''
                    },
                    {
                        prop:'reportModelType_CN',
                        label:'报告类型',
                        width:'',
                    },
                    {
                        prop:'reportModelExplain',
                        label:'报告说明',
                        width:''
                    },
                    {
                        prop:'tools',
                        label:'操作',
                        width:'',
                        btns:[
                            {
                                icon:'el-icon-view',
                                text:'修改',
                                clickFun:(row,index)=>{
                                    this.selectReportModelArr = [];
                                    this.dialogType = 'see'
                                    this.selectReportModelArr.push(row)
                                    this.getReportInfo(row)
                                }
                            }
                        ]
                    }
                ],
                //表 数据
                tableData:[],
                //弹窗
                dialogFormVisible:false,
                dialogTitle:'修改报告模板',
                //弹窗 类型  resive/see
                dialogType:'see',
                activeName:'first',
                //弹窗 数据
                dialogInfo:{
                    reportModel:{},//基本信息
                    reportModelInfo:[]//树结构信息
                },
                //树结构 配置
                defaultProps: {
                    children: 'children',
                    label: 'name'
                },
                //点击的树节点名称
                nodeName:"",
                //当前修改的node节点的level
                nodeLevel:'',
                btnDisabled:true,//修改确定按钮状态
                //树节点 form信息
                nodeForm:{},//用于编辑
                nodeOldForm:{},//用于比较 是否存在修改
                contentTypeOpt:[
                    {label:'标题',value:'title'},
                    {label:'正文文本',value:'text'},
                    {label:'表格',value:'table'},
                    {label:'图表',value:'image'},
                ],
                alignmentOpt:[
                    {label:'居左',value:0},
                    {label:'居中',value:1},
                    {label:'居右',value:2},

                ],
                //仪表盘ID下拉框集合
                dashboardIDOpt:[],
                //继承模板下拉框集合
                inheritModelOpt:[],
                //表格下拉框集合
                tableIdOpt:[],
                //图表下拉框集合
                imgIdOpt:[],
                //文本集合
                textContentArr:[
                    {text:'',metricID:""}
                ],
                metricOpt:[]
            }
        },
        mounted(){

        },
        created(){
            //获取模板列表
            this.getReportModelList(this.searchConditions,1)
            //获取仪表板列表
            this.getDashboardIdList();
            //查询条件
            this.formConditionsArr =[
                {
                    label:'报告名称',
                    paramName:'reportModelName',
                    model:{
                        model:''
                    },
                    itemType:'',
                    type:'input'
                },
                {
                    label:'报告类型',
                    paramName:'reportModelType',
                    type:'select',
                    itemType:'',
                    model:{
                        model:''
                    },
                    options:this.reportModelTypeOpt
                },
            ]

            //监听查询条件组件传过来的条件
            bus.$on(this.busName,(params)=>{
                this.searchConditions = params;
                this.getReportModelList(this.searchConditions,1)
                this.c_page = 1;
                //清除选中
                this.selectReportModelArr = []
            })
            //监听选中的资产
            bus.$on(this.tableBusNames.selectionName,(params)=>{
                this.selectReportModelArr = params;
                //console.log(this.selectIdArr )
                /*for(let i in params){
                    this.delectEquipmentIds += params[i].id +','
                }*/
                //console.log(this.delectEquipmentIds)
            })
        },
        watch:{
            //监听 弹窗状态  关闭时 将表单清空
            'dialogFormVisible'(){
                if(!this.dialogFormVisible){
                    this.nodeForm = {};
                    this.activeName = 'first';
                    this.nodeName = '';
                    this.btnDisabled = true;
                    //获取模板列表
                    this.getReportModelList(this.searchConditions,this.c_page)
                    //清空选中资产
                    this.selectReportModelArr = []
                }else{
                    this.nodeOldForm = JSON.stringify(this.nodeForm);

                }
            },

            //监听 值变化
            nodeForm:{
                handler(new_value,old_value){
                    /*console.log(this.dialogFormVisible)
                    console.log(old_value)
                    console.log(new_value)*/

                    if(JSON.stringify(new_value) == this.nodeOldForm){
                        this.btnDisabled = true;
                    }else{
                        this.btnDisabled = false;
                    }
                },
                deep: true
            }
        },
        methods:{
            //添加模板
            addModel(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/reportModel/insertReportModel.do',this.$qs.stringify(this.addFrom))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                layer.msg(obj.message,{icon:1})
                                this.getReportModelList(this.searchConditions,1);
                                this.c_page = 1;
                                this.addDialogFormVisible = false;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            //删除模板
            removeModel(){
                if(this.selectReportModelArr.length === 0){
                    layer.msg('未选中表格中的报告模板',{icon:5})
                }else{
                    let ids = '';
                    this.selectReportModelArr.forEach((item)=>{
                        ids += item.reportModelID + ','
                    })
                    layer.confirm('您确定删除么？', {
                        btn: ['确定','取消'] //按钮
                    }, (index)=>{
                        this.loading = true;
                        this.$nextTick(()=>{
                            this.$axios.post(this.$baseUrl+'/reportModel/deleteReportModel.do',this.$qs.stringify({
                                reportModelIDs:ids
                            }))
                                .then(res =>{
                                    this.loading = false;
                                    let obj = res.data;
                                    if(obj.success == 'true'){
                                        layer.msg(obj.message,{icon:1})
                                        this.getReportModelList(this.searchConditions,1);
                                        this.c_page = 1;
                                        this.addDialogFormVisible = false;
                                        this.selectReportModelArr = [];
                                    }else{
                                        layer.msg(obj.message,{icon:5})
                                    }
                                })
                                .catch(err =>{
                                    this.loading = false;
                                })

                        })
                    })

                }
            },
            //查询列表
            getReportModelList(condition,page){
                this.loading = true;
                let obj = condition;
                obj.pageIndex = page;//当前页
                obj.pageSize = this.size;//页的条数
                this.c_page = page;
                //请求
                this.$nextTick( ()=> {
                    this.$axios.post(this.$baseUrl+'/reportModel/getReportModelListByPage.do',this.$qs.stringify(obj))
                        .then((res) => {
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.tableData = obj.data.reportModelList;
                                this.allCounts = JSON.parse(obj.data.count);
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                            //this.tableData = res.data
                            //this.allCounts = JSON.parse(res.data[0].count.count);
                            //将查询条件保存，用于分页
                            //this.saveCondition = this.searchConditions;

                        })
                        .catch((err) => {
                            this.loading = false;
                            console.log(err);
                        })
                })
            },
            //获取单个报告信息
            getReportInfo(obj){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/reportModel/getReportModelInfoAll.do',this.$qs.stringify({
                        reportModelID : obj.reportModelID
                    }))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.dialogInfo = obj.data;
                                this.dialogFormVisible = true;

                                //获取表格列表
                                this.getTableList()
                                //获取image列表
                                this.getImgList();
                                //获取metric
                                this.getMetricList();
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            //修改按钮
            reviseReportModel(){
                if(this.selectReportModelArr.length === 0){
                    layer.msg('未选中表格中的报告模板',{icon:5})
                }else if(this.selectReportModelArr.length > 1){
                    layer.msg('只可以选中一个报告模板修改',{icon:5})
                }else{
                    this.dialogType = 'resive'
                    this.getReportInfo(this.selectReportModelArr[0]);
                }
            },
            //获取仪表板列表
            getDashboardIdList(){
                this.$nextTick(()=>{
                    //this.loading = true;
                    this.$axios.post(this.$baseUrl+'/reportModel/getAllDashboardList.do',this.$qs.stringify())
                        .then(res=>{
                            // this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.dashboardIDOpt = obj.data
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             // this.loading = false;
                        })
                })
            },
            //仪表板 改变
            dashboardChange(){
                this.getInheritModelList();
                this.addFrom.extendReportModelID = '';
            },
            //获取继承模板列表
            getInheritModelList(){
                this.$nextTick(()=>{
                    //this.loading = true;
                    this.$axios.post(this.$baseUrl+'/reportModel/getReportModelListByDashboardID_ReportModelAdd.do',this.$qs.stringify({
                        dashboardID:this.addFrom.dashboardID
                    }))
                        .then(res=>{
                            // this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.inheritModelOpt = obj.data
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            // this.loading = false;
                        })
                })
            },
            //获取表格列表
            getTableList(){
                this.$nextTick(()=>{
                    this.treeLoading = true;
                    this.$axios.post(this.$baseUrl+'/reportModel/getTablesByDashboardID.do',this.$qs.stringify({
                        dashboardID:this.dialogInfo.reportModel.dashboardID
                    }))
                        .then(res=>{
                            this.treeLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.tableIdOpt = obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.treeLoading = false;
                        })
                })
            },
            //获取图表列表
            getImgList(){
                this.$nextTick(()=>{
                    this.treeLoading = true;
                    this.$axios.post(this.$baseUrl+'/reportModel/getImagesByDashboardID.do',this.$qs.stringify({
                        dashboardID:this.dialogInfo.reportModel.dashboardID
                    }))
                        .then(res=>{
                            this.treeLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.imgIdOpt = obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.treeLoading = false;
                        })
                })
            },
            //获取metric 数据列表
            getMetricList(){
                this.$nextTick(()=>{
                    this.treeLoading = true;
                    this.$axios.post(this.$baseUrl+'/reportModel/getMetricsByDashboardID.do',this.$qs.stringify({
                        dashboardID:this.dialogInfo.reportModel.dashboardID
                    }))
                        .then(res=>{
                            this.treeLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.metricOpt = obj.data;
                                this.metricOpt.unshift({label:'无',value:""})
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.treeLoading = false;
                        })
                })
            },
            /*分页页码改变*/
            handleCurrentChange(page){
                //获取数据
                this.getReportModelList(this.searchConditions,page);
                this.c_page = page
            },
            /*树节点 点击*/
            nodeClick(data,node,tree){
                // console.log(data)
                /*console.log(node)
                console.log(tree)*/
                //console.log(node)
                this.nodeLevel = node.level;
                this.nodeName = data.name

                //获取节点信息
                this.$nextTick(()=>{
                    this.treeLoading = true;
                    this.$axios.post(this.$baseUrl+'/reportModel/getReportModelInfoByReportModelInfoID.do',this.$qs.stringify({
                        reportModelInfoID:data.id
                    }))
                        .then(res=>{
                            this.treeLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.nodeForm = obj.data;
                                //判断节点的类型 为text需要格式化内容
                                this.textContentArr = [];
                                if(this.nodeForm.contentType === "text"){
                                   this.textContentArr = JSON.parse(this.nodeForm.content)
                                }
                                this.nodeOldForm = JSON.stringify(this.nodeForm)
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.treeLoading = false;
                        })
                })
            },
            //内容类型 改变事件
            contentTypeChange(){
                this.nodeForm.imageName = '';
                this.nodeForm.tableName ='';
                this.nodeForm.content = ''
                if(this.nodeForm.contentType === 'table'){
                    this.getTableList()
                    //this.nodeForm.content = ''
                }else if(this.nodeForm.contentType === 'image'){
                    this.getImgList();
                    //this.nodeForm.content = ''
                }else if(this.nodeForm.contentType === 'text'){
                    this.getMetricList();
                    //this.nodeForm.content = ''
                }
            },
            /*提交基本信息*/
            sendBaseInfo(){
                this.$nextTick(()=>{
                    this.treeLoading = true;
                    this.$axios.post(this.$baseUrl+'/reportModel/updateReportModelByReportModelID.do',this.$qs.stringify(this.dialogInfo.reportModel))
                        .then(res=>{
                            this.treeLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                layer.msg(obj.message,{icon:1})
                                this.getReportModelList(this.searchConditions,this.c_page)
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.treeLoading = false;
                        })
                })
            },
            /*确定修改 按钮*/
            putResive(){
                this.nodeForm.titleLevel = this.nodeLevel
                if(this.nodeForm.contentType === "text"){
                    // this.textContentArr = JSON.parse(this.nodeForm.content)
                    this.nodeForm.content = JSON.stringify(this.textContentArr)
                }
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/reportModel/updateReportModelInfoByReportModelInfoID.do',this.$qs.stringify(this.nodeForm))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                layer.msg(obj.message,{icon:1});
                                //清空表单
                                this.nodeForm = {};
                                this.nodeName = ''
                                this.nodeOldForm = JSON.stringify(this.nodeForm);
                                this.btnDisabled = true;
                                //重新获取数据
                                this.getReportInfo(this.selectReportModelArr[0])
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },

            /*拖拽 实现排序*/
            handleDrop(draggingNode, dropNode, dropType, ev) {
                let paramData = [];
                // 当拖拽类型不为inner,说明只是同级或者跨级排序，只需要寻找目标节点的父ID，获取其对象以及所有的子节点，并为子节点设置当前对象的ID为父ID即可
                // 当拖拽类型为inner，说明拖拽节点成为了目标节点的子节点，只需要获取目标节点对象即可
                let data = dropType != "inner" ? dropNode.parent.data : dropNode.data;
                let nodeData = dropNode.level == 1 && dropType != "inner" ? data : data.children;
                // 设置父ID
                nodeData.forEach(element => {
                    element.pid =  data.id ? data.id : ''; //
                });
                nodeData.forEach((element, i) => {
                    var dept = {
                        deptId: element.id,
                        parentDeptId: element.pid,
                        order: i
                    };
                    paramData.push(dept);
                });

                this.$nextTick(()=>{
                    this.leftLoading = true;
                    this.$axios.post(this.$baseUrl+'/reportModel/UpdateTreeOrder.do',this.$qs.stringify({
                        nodeList:JSON.stringify(paramData)
                    }))
                        .then(res=>{
                            this.leftLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){

                            }else{
                                layer.msg(obj.message,{icon:5})
                                this.getReportInfo(this.selectReportModelArr[0])
                            }
                        })
                        .catch(err=>{
                             this.leftLoading = false;
                        })
                })
                //this.loading = true;
            },
            //添加节点  outType值为 true 说明是添加最外层节点
            appendNode(nodeData,outType){
                let paramObj = {};
                //添加最外层节点
                if(outType){ //不是
                    //参数
                    paramObj = {
                        reportModelID:this.selectReportModelArr[0].reportModelID,
                        parentID:'',
                        orderCode:nodeData.length
                    }
                }else{//是
                    if (!nodeData.children) {
                        this.$set(nodeData, 'children', []);
                    }
                    //参数
                    paramObj = {
                        reportModelID:this.selectReportModelArr[0].reportModelID,
                        parentID:nodeData.id,
                        orderCode:nodeData.children.length
                    }
                }
                //data.children.push(newChild);
                this.$nextTick(()=>{
                    this.leftLoading = true;
                    this.$axios.post(this.$baseUrl+'/reportModel/insertReportModelInfo.do',this.$qs.stringify(paramObj))
                        .then(res=>{
                            this.leftLoading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                let nodeObj = {}
                                nodeObj.id = obj.data.reportModelInfoID;
                                nodeObj.name=obj.data.content;
                                if(outType){
                                    nodeData.push(nodeObj);
                                }else{
                                    nodeData.children.push(nodeObj);
                                }

                            }else{
                                layer.msg(obj.message,{icon:5})
                                this.getReportInfo(this.selectReportModelArr[0])
                            }
                        })
                        .catch(err=>{
                             this.leftLoading = false;
                        })
                })
            },

            //删除节点
            deleteNode(data){
                if(data.children && data.children.length !== 0){
                    layer.msg('该节点存在子集，无法删除',{icon:5})
                }else{
                    layer.confirm('您确定删除么？', {
                        btn: ['确定','取消'] //按钮
                    }, (index)=>{
                        this.leftLoading = true;
                        this.$nextTick(()=>{
                            this.$axios.post(this.$baseUrl+'/reportModel/deleteReportModelInfo.do',this.$qs.stringify({
                                reportModelInfoID:data.id
                            }))
                                .then(res =>{
                                    this.leftLoading = false;
                                    let obj = res.data;
                                    if(obj.success == 'true'){
                                        this.nodeForm = {};
                                        this.nodeName = '';
                                        layer.msg(obj.message,{icon:1})
                                        this.getReportInfo(this.selectReportModelArr[0])
                                    }else{
                                        layer.msg(obj.message,{icon:5})
                                        this.getReportInfo(this.selectReportModelArr[0])
                                    }
                                })
                                .catch(err =>{
                                    this.leftLoading = false;
                                })

                        })
                    })
                }

            },
            //添加文本类型数据
            addText(){
                this.textContentArr.push({text:"",metricID:""});
                this.btnDisabled = false;
            },
            textConChange(){
                this.btnDisabled = false;
            }
        }
    }
</script>

<style scoped>
    .tools-btns{
        position: absolute;
        right: 50px;
        top: 30px;
    }
    .report-tools-form{
        margin-bottom: 20px;
    }
    .table-wapper{
        min-height: 600px;
        padding:0 10px;
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

    .dialog-content{
        font-size: 12px;
        color: #fff;
        height: 70vh;
        overflow-y: auto;
    }
    .dialog-content /deep/ .el-input__inner{
        background: none;
    }
    .dialog-content /deep/ .el-form-item__label{
        font-size: 12px;
    }
    .dialog-content /deep/ .el-form-item{
        /*width: 46%;*/
        margin-right: 10px;
        margin-bottom: 10px;
    }
    .dialog-content /deep/ .el-form-item__content{
        width: calc(100% - 80px);
    }
    .explainWapper /deep/ .el-form-item__content{
        /*width: calc(100% - 124px);*/
    }

    .detailWapper{
        display: flex;
        background: #3b4d60;
        padding: 10px;
        /*height:calc(100% - 250px)*/
        height: 100%;
    }
    .detailWapper .treeWapper{
        width: 30%;
        border: 1px solid #2c76bd;
        margin-right: 5px;
        padding: 10px;
        padding-bottom: 2px;
        /*height:calc(100% - 250px)*/
    }
    .detailWapper .treeWapper .treeCon{
        overflow: auto;
        height: calc(100% - 30px);
    }
    .appendNode {
        text-align: center;
        height: 30px;
        color: #fff;
        background:#63799f;
        line-height: 30px;
        cursor: pointer;
    }
    .addText{
        text-align: center;
        height: 30px;
        color: #fff;
        background:#63799f;
        line-height: 30px;
        cursor: pointer;
    }
    .detailWapper .treeWapper>p:hover ,.addText:hover{
        background: #5f7292;
    }
    .detailWapper .formWapper{
        flex:1
    }
    .detailWapper /deep/ .el-tree-node__content{
        height: auto;
    }
    .detailWapper /deep/ .custom-tree-node{
        width: calc(100% - 36px);
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
    .detailWapper /deep/ .item-con{
        border-bottom: 1px solid #47678d;
        display: inline-block;
        max-width: 80%;
        /*word-break: break-all;
        white-space: normal;*/
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        margin-bottom: 5px;
        margin-top: 5px;
    }
    .dialog-content /deep/ .el-tabs__content{
        height: calc(100% - 69px);
    }
    .ovd{
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }
    .el-icon-plus:hover{
        color: #00ff62;
    }
</style>
