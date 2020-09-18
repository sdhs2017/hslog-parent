<template>
    <div v-loading="allLoading"  element-loading-background="rgba(26,36,47, 0.2)">
        <div class="top-title" style="padding-left: 10px;"><!--{{htmlTitle}}-->
            <div class="top-zz" v-if="operType === 'see'"></div>
            <div style="float: left;display: flex;margin-right: 15px;">
                <span style="width: 42px;color: #409eff;text-shadow: none">类型:</span>
                <el-select v-model="chartsConfig.dataSourceType" placeholder="" size="mini" style="width: 90px;" @change="dataSourceChange">
                    <el-option label="ElasticSearch" value="ElasticSearch"></el-option>
                    <el-option label="MySQL" value="MySQL"></el-option>
                </el-select>
            </div>
            <div class="choose-wapper" v-if="chartsConfig.dataSourceType ==='ElasticSearch'">
                <choose-index :busName="this.busIndexName" :arr = "indexVal"></choose-index>
            </div>
            <div class="choose-wapper" v-else>
                <span style="width: 60px;color: #409eff;text-shadow: none">数据源:</span>
                <el-select v-model="chartsConfig.mysqlData" placeholder="" size="mini" style="width: 150px;" @change="mySQLTableChange">
                    <el-option
                        v-for="item in mysqlDataOpt"
                        :key="'mysql'+item.value"
                        :label="item.label"
                        :value="item.value">
                    </el-option>
                </el-select>
            </div>
            <el-button class="saveChart" type="success" plain @click="dialogFormVisible = true"  size="mini">保存</el-button>
            <div style="float: right" v-if="chartsConfig.dataSourceType ==='ElasticSearch'">
                <el-button class="update-btn" v-if="updateBtn && isCanCreate !== 'disabled'"  type="success" size="mini"  @click="updateChart" style="float: right;margin-right: 5px;margin-top: 10px;position: relative;z-index: 101;">更新</el-button>
                <el-button type="primary" v-else size="mini" plain @click="refreshChart"  style="float: right;margin-right: 5px;margin-top: 10px;position: relative;z-index: 101;">刷新</el-button>
            </div>
            <div class="date-wapper" v-if="chartsConfig.dataSourceType ==='ElasticSearch'"><date-layout :busName="busName" :defaultVal="defaultVal" :refresh="refresh"></date-layout></div>
        </div>
        <div class="filter-box" >
            <queryFilter
                v-if="chartsConfig.dataSourceType ==='ElasticSearch'"
                :busFilterName="this.busFilterName"
                :busQueryName="this.busQueryName"
                :filterArr="this.defaultFilter"
                :queryVal="this.defaultQuery"
                :useType="this.operType"
                useObject="chart"
                :templateName="this.chartsConfig.templateName"
                :preIndexName="this.chartsConfig.preIndexName"
                :suffixIndexName="this.chartsConfig.suffixIndexName"
            >
            </queryFilter>
        </div>
        <div class="chart-wapper">
            <div class="config-wapper" :style="{height:wapperHeight}">
                <el-button class="creatBtn" type="primary" @click="createBtn" :disabled="isCanCreate || operType === 'see'">生成</el-button>
                <el-tabs v-model="activeName" style="height: 100%;" type="border-card"  v-loading="leftLoading"  element-loading-background="rgba(26,36,47, 0.2)">
                    <el-tab-pane label="数据" name="first">
                        <el-collapse>
                            <el-collapse-item class="tablist" v-for="(columnItem,i) in chartsConfig.columnArr" :key="i">
                                <template slot="title" class="collapseTit">
                                    <span style="overflow: hidden;word-break: break-all;width: 200px;height: 36px;line-height: 36px;">列 {{columnItem.aliasName === '' ? columnItem.field : columnItem.aliasName}}</span><i class="header-icon el-icon-error removeTab" @click="removeYaxisTab(i,$event)" v-if="operType !== 'see'"></i>
                                </template>
                                <el-form label-position="top" style="position: relative;">
                                    <div class="from-zz" v-if="operType === 'see'"></div>
                                    <el-form-item label="列参数">
                                        <el-select v-model="columnItem.field" placeholder="请选择" filterable style="width: 100%;" size="mini" @change="columnChange($event,i)">
                                            <el-option
                                                v-for="item in chartsConfig.columnOpt"
                                                :key="'column'+item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="排序方式">
                                        <el-select v-model="columnItem.sort" placeholder="请选择" style="width: 100%;" size="mini">
                                            <el-option label="降序" value="desc"></el-option>
                                            <el-option label="升序" value="asc"></el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="名称" v-if="chartsConfig.dataSourceType === 'ElasticSearch'">
                                        <el-input v-model="columnItem.aliasName" size="mini"></el-input>
                                    </el-form-item>
                                </el-form>
                            </el-collapse-item>
                            <p style="text-align: center;font-size: 12px;margin-bottom: 10px;" v-if="operType !== 'see'"><span class="addY" @click="addColumn"> <i class="el-icon-circle-plus"></i>添加列</span></p>
                            <el-collapse-item class="tablist" v-for="(conditionItem,ci) in chartsConfig.conditionArr" :key="'conditionItem'+ci" v-if="chartsConfig.dataSourceType === 'MySQL'">
                                <template slot="title" class="collapseTit">
                                    <span style="overflow: hidden;word-break: break-all;width: 200px;height: 36px;line-height: 36px;">条件 {{ci+1}}</span><i class="header-icon el-icon-error removeTab" @click="chartsConfig.conditionArr.splice(ci,1)" v-if="operType !== 'see'"></i>
                                </template>
                                <el-form label-position="top" style="position: relative;">
                                    <div class="from-zz" v-if="operType === 'see'"></div>
                                    <el-form-item label="列参数">
                                        <el-select v-model="conditionItem.field" placeholder="请选择" @change="conditionFieldChange($event,ci)" filterable style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in chartsConfig.columnOpt"
                                                :key="'condition'+ci+item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="运算符">
                                        <el-select v-model="conditionItem.operator" placeholder="请选择" @change="conditionOperatorChange($event,ci)" filterable style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in chartsConfig.operatorOpt"
                                                :key="'operator'+ci+item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="值" v-if="conditionItem.operator === 'between'">
                                        <div style="display: flex;">
                                            <el-input v-model="conditionItem.start" size="mini"></el-input>
                                            -
                                            <el-input v-model="conditionItem.end" size="mini"></el-input>
                                        </div>
                                    </el-form-item>
                                    <el-form-item label="值" v-else-if="conditionItem.operator === 'in' || conditionItem.operator === 'not in'">
                                        <p class="values-wapper">
                                            <span v-for="(item ,n) in conditionItem.values" :key="n">{{item}}<i class="el-icon-close" title="删除" @click="conditionItem.values.splice(n,1)"></i></span>
                                        </p>
                                        <div style="display: flex;align-items: center;">
                                            <el-input v-model="addValues" size="mini"></el-input>
                                            <el-button type="primary" style="background: 0;height: 28px;" size="mini" @click="addValue(ci)">添加</el-button>
                                        </div>
                                    </el-form-item>
                                    <el-form-item label="值" v-else>
                                        <el-input v-model="conditionItem.value" size="mini"></el-input>
                                    </el-form-item>
                                </el-form>
                            </el-collapse-item>
                            <p style="text-align: center;font-size: 12px;margin-bottom: 10px;" v-if="operType !== 'see' && chartsConfig.dataSourceType !== 'ElasticSearch'"><span class="addY" @click="addCondition"> <i class="el-icon-circle-plus"></i>添加条件</span></p>
                        </el-collapse>
                    </el-tab-pane>
                    <el-tab-pane label="基本设定" name="second">
                        <el-collapse v-model="configOpened">
                            <el-collapse-item title="标题" class="tablist" name="1">
                                <el-form label-position="left" label-width="50px" style="position:relative;">
                                    <div class="from-zz" v-if="operType === 'see'"></div>
                                    <el-form-item label="标题">
                                        <el-input v-model="chartsConfig.title.text" size="mini"></el-input>
                                    </el-form-item>
                                </el-form>
                            </el-collapse-item>
                            <el-collapse-item title="显示" class="tablist" name="2">
                                <el-form label-position="left" label-width="80px" style="position:relative;">
                                    <div class="from-zz" v-if="operType === 'see'"></div>
                                    <el-form-item label="显示总数" v-if="chartsConfig.dataSourceType === 'ElasticSearch'">
                                        <el-input v-model="chartsConfig.counts" min="1" type="Number" size="mini"></el-input>
                                    </el-form-item>
                                    <el-form-item label="每页条数">
                                        <el-input v-model="chartsConfig.page.size" min="1" type="Number"  size="mini"></el-input>
                                    </el-form-item>
                                </el-form>
                            </el-collapse-item>
                        </el-collapse>
                    </el-tab-pane>


                </el-tabs>
            </div>
            <div class="view-wapper"  v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)" :style="{height:wapperHeight}">
                <div class="charts-title">{{chartsConfig.title.text}}</div>
                <div id="charts-wapper">
                    <v-basetable :tableHead="chartsConfig.tableHead" :height="tableHeight" :tableData="chartsConfig.tableData"></v-basetable>
                    <div style="display:flex;height: 50px;align-items: center;justify-content: flex-end;border-top: 1px solid #5a7494;" v-if="chartsConfig.tableData.length !== 0">
                        总条数 <b style="color: #409eff;margin: 0 10px;"> {{this.chartsConfig.trueCount}} </b>,显示条数 <b style="color: #409eff;margin: 0 10px;"> {{this.showCount}} </b>
                        <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="chartsConfig.page.cPage" :page-size="pageSize" :total="chartsConfig.page.allCounts"></el-pagination>
                    </div>
                </div>
<!--                <div class="empty-tip" v-if="this.emptyTipState">暂无结果</div>-->
                <el-popover
                    v-if="!this.emptyTipState"
                    class="source-data"
                    placement="left"
                    title=""
                    width="400"
                    trigger="click"
                >
                    <jsonView :data="this.sourceData" theme="one-dark" :line-height="20" :deep="5" class="jsonView"></jsonView>
                    <el-button type="primary" plain size="mini" slot="reference" >源数据</el-button>
                </el-popover>
            </div>
        </div>
        <el-dialog title="保存" :visible.sync="dialogFormVisible" width="400px">
            <el-form>
                <el-form-item label="名称">
                    <el-input v-model="chartParams.chartName"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="chartParams.chartDes"></el-input>
                </el-form-item>
                <el-form-item v-if="chartId !== ''">
                    <el-switch
                        v-model="chartParams.otherSave"
                        active-text="另存为新图表">
                    </el-switch>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveChart" :disabled="chartParams.chartName === '' ? 'disabled' : false">确 定</el-button>
            </div>
        </el-dialog>
    </div>

</template>

<script>
    import dateLayout from '../common/dateLayout'
    import {setChartParam} from "../../../static/js/common";
    import bus from '../common/bus';
    import jsonView from 'vue-json-views'
    import queryFilter from '../dashboard/queryFilter'
    import chooseIndex from '../dashboard/chooseIndex'
    import vBasetable from '../common/Basetable2';
    export default {
        name: "tableBaseConfig",
        props:{
            //操作类型
            operType:{
                type:String,
                defaultVal() {
                    return 'edit'
                }
            },
            //图表类型
            chartType:{
                type:String
            },
            //图表id
            chartId:{
                type:String
            }
        },
        data() {
            return {
                refresh:0,
                allLoading:false,
                leftLoading:false,
                loading:false,
                //时间控件参数 柱状图
                defaultVal:{
                    //具体时间参数
                    lastVal:'15-min',
                    //起始时间
                    starttime:'',
                    //结束时间
                    endtime:'',
                    //具体时间 类型状态
                    dateBlock:false,
                    //是否存在轮询框
                    isIntervalBox:true,
                    //轮询状态
                    intervalState:false,
                    //轮询数值间隔
                    intervalVal:'',
                    //轮询参数类型
                    intervalType:'',
                    //‘快速选择’功能参数类型
                    dateUnit:'min',
                    //‘快速选择’功能参数数值
                    dateCount:'15',
                    //‘常用’ 时间值
                    commonlyVal:'',
                    //是否可以切换精确日期
                    changeState:true
                },
                //保存图表的弹窗状态
                dialogFormVisible:false,
                //保存图表表单参数
                chartParams:{
                    //图表名称
                    chartName:'',
                    //图表描述
                    chartDes:'',
                    //查询条件
                    searchParam:'',
                    //另存
                    otherSave:false
                },
                //echarts结构
                opt:{},
                //时间范围
                dateObj:{
                    starttime:'',//起始时间
                    endtime:'',//结束时间
                    last:'15-min',
                },
                //轮询参数
                intervalObj:{
                    state:false,
                    interval:'5000'
                },
                //轮询
                interval:'',
                //返回的源数据
                sourceData:'',
                //数据为空时提示状态
                emptyTipState:true,
                //默认配置 用于还原原始配置
                defaultConfig:'',
                //bus监听事件的名称
                busName:'createESTable',
                busIndexName:'ESTableIndexName',
                busFilterName:'',
                busQueryName:'',
                //默认数据源索引
                indexVal:[],
                //展开的选项卡
                configOpened:['1','2','3','4','5','6','7','8','9'],
                yUnit :'',
                //mysql添加多个值
                addValues:'',
                //图表基本配置
                chartsConfig:{
                    //源数据类型
                    dataSourceType:'ElasticSearch',
                    //mysql源数据
                    mysqlData:'',
                    //templateName
                    templateName:'',
                    //index前名
                    preIndexName:'',
                    //index后时间
                    suffixIndexName:'',
                    //datefield
                    datefield:'',
                    //标题
                    title:{
                        text:''
                    },
                    //列集合
                    columnArr:[
                        {
                            field:'',
                            sort:'desc',
                            aliasName: ''
                        }
                    ],
                    //条件集合
                    conditionArr:[],
                    //列顺序
                    //实际总数
                    trueCount:0,
                    //显示的总数
                    counts:100,
                    //分页条数
                    page:{
                        cPage:1,
                        size:10,
                        allCounts:0
                    },
                    //表头
                    tableHead:[],
                    //列field集合
                    columnOpt:[],
                    //operator集合
                    operatorOpt:[],
                    //表数据
                    tableData:[],
                },
                //mySQL表名集合
                mysqlDataOpt:[],
                activeName:'first',
                //保存的每一步生成的结构数据，用于返回操作
                chartsConfigArr:[],
                //过滤条件
                filters:'',
                //默认过滤条件
                defaultFilter:[],
                //更新按钮
                updateBtn:false,
                //query值
                queryVal:'',
                oldQuery:'',
                defaultQuery:'',
                //页面内容高度
                wapperHeight:1,
                //表高度
                tableHeight:1,
                //分页
                pageSize:10,
                showCount:100,
            }
        },
        created(){
            //保存配置
            this.defaultConfig = JSON.stringify(this.chartsConfig)
            //判断操作性质
            if(this.operType === 'edit'){//修改
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                //this.$options.name = 'editBarChart'+ this.$route.query.id;
                //修改data参数
                this.htmlTitle = `编辑 ${this.$route.query.name}`;
                this.busName = this.chartType + 'resiveChartSimple'+this.$route.query.id;
                this.busIndexName = this.chartType + 'IndexNameSimple' +this.$route.query.id;
                this.busFilterName = this.chartType + 'FilterNameSimple' +this.$route.query.id;
                this.busQueryName = this.chartType + 'QueryNameSimple' +this.$route.query.id;
                //时间范围监听事件
                bus.$on(this.busName,(obj)=>{
                    let arr = setChartParam(obj);
                    this.dateObj = arr[0];
                    this.intervalObj = arr[1];
                })
                //数据源监听
                bus.$on(this.busIndexName,(arr)=>{
                    //还原配置
                    this.initialize();
                    //设置数据源
                    this.chartsConfig.suffixIndexName = arr[2];
                    this.chartsConfig.preIndexName = arr[1];
                    this.chartsConfig.templateName = arr[0];
                    this.chartsConfig.datefield = arr[3];
                    //获取column数据
                    this.getColumnField()
                })
                //监听过滤条件
                bus.$on(this.busFilterName,(str)=>{
                    this.filters = str;
                    //刷新
                    //判断是否具备生成图表的条件
                    if(this.isCanCreate !== 'disabled'){
                        this.loading = true;
                        this.chartsConfig.page.cPage = 1;
                        //获取数据
                        this.getData()
                    }
                })
                //监听query
                bus.$on(this.busQueryName,(str)=>{
                    this.isUpdate(str)
                })
                if(this.chartId === '' || this.chartId !== this.$route.query.id){
                    this.chartId = this.$route.query.id;
                }
            }else if(this.operType === 'see'){//查看
                //修改此组件的name值
                //this.$options.name = 'seeBarChart'+ this.$route.query.id;
                //修改data参数
                this.htmlTitle = `查看 ${this.$route.query.name}`;
                this.busName = 'seeChartSimple'+this.$route.query.id;
                this.busIndexName = 'seeIndexNameSimple' +this.$route.query.id;
                this.busFilterName = 'seeFliterNameSimple' +this.$route.query.id;
                this.busQueryName = 'seeQueryNameSimple' +this.$route.query.id;
                //时间范围监听事件
                bus.$on(this.busName,(obj)=>{
                    let arr = setChartParam(obj);
                    this.dateObj = arr[0];
                    this.intervalObj = arr[1];
                })
                //数据源监听
                bus.$on(this.busIndexName,(arr)=>{
                    //还原配置
                    this.initialize();
                    //设置数据源
                    this.chartsConfig.suffixIndexName = arr[2];
                    this.chartsConfig.preIndexName = arr[1];
                    this.chartsConfig.templateName = arr[0];
                    this.chartsConfig.datefield = arr[3];
                    //获取column数据
                    this.getColumnField()
                })
                //监听过滤条件
                bus.$on(this.busFilterName,(str)=>{
                    this.filters = str;
                    //刷新
                    //判断是否具备生成图表的条件
                    if(this.isCanCreate !== 'disabled'){
                        this.loading = true;
                        this.chartsConfig.page.cPage = 1;
                        //获取数据
                        this.getData()
                    }
                })
                //监听query
                bus.$on(this.busQueryName,(str)=>{
                    this.isUpdate(str)
                })
                if(this.chartId === '' || this.chartId !== this.$route.query.id){
                    this.chartId = this.$route.query.id;
                }
            }else{//添加
                this.busName = this.chartType + 'addChartSimple';
                this.busIndexName = this.chartType + 'addIndexNameSimple';
                this.busFilterName = this.chartType + 'addFliterNameSimple';
                this.busQueryName = this.chartType + 'addQueryNameSimple';
                //时间范围监听事件
                bus.$on(this.busName,(obj)=>{
                    let arr = setChartParam(obj);
                    this.dateObj = arr[0];
                    this.intervalObj = arr[1];
                })
                //数据源监听
                bus.$on(this.busIndexName,(arr)=>{
                    //还原配置
                    this.initialize();
                    //设置数据源
                    this.chartsConfig.suffixIndexName = arr[2];
                    this.chartsConfig.preIndexName = arr[1];
                    this.chartsConfig.templateName = arr[0];
                    this.chartsConfig.datefield = arr[3];
                    //获取column数据
                    this.getColumnField();
                })
                //监听过滤条件
                bus.$on(this.busFilterName,(str)=>{
                    this.filters = str;
                    //刷新
                    //判断是否具备生成图表的条件
                    if(this.isCanCreate !== 'disabled'){
                        this.loading = true;
                        this.chartsConfig.page.cPage = 1;
                        //获取数据
                        this.getData()
                    }
                })
                //监听query
                bus.$on(this.busQueryName,(str)=>{
                    this.isUpdate(str)
                })
            }
            //初始化 表格高度
            this.setHeight()
        },
        mounted(){
            //监听页面高度   改变表格高度
            window.onresize =()=>{
                this.setHeight()
            }
        },
        beforeDestroy(){
            //在组件销毁前移除监听事件
            bus.$off(this.busName);
            bus.$off(this.busIndexName);
            bus.$off(this.busFilterName);
            bus.$off(this.busQueryName);
            //清楚计时器
            clearInterval(this.interval);
        },
        computed:{
            /*生成按钮状态*/
            'isCanCreate'(){
                let columnState = false;
                let sizeState = false;
                let conditionState = false;
                //判断列
                this.chartsConfig.columnArr.forEach((item)=>{
                    if (item.field === ''){
                        columnState = 'disabled';
                    }
                })
                if(this.chartsConfig.dataSourceType === 'MySQL'){
                    //判断列
                    this.chartsConfig.conditionArr.forEach((item)=>{
                        if (item.field === ''){
                            conditionState = 'disabled';
                        }
                        if (item.operator === 'in' || item.operator === 'not in'){
                            if (item.values.length === 0){
                                conditionState = 'disabled';
                            }
                        }else if(item.operator === 'between'){

                        }else{
                            if (item.value === ''){
                                conditionState = 'disabled';
                            }
                        }
                    })
                }
                //判断分页
                if((Number(this.chartsConfig.page.size) <= 0) || (Number(this.chartsConfig.counts)  <= 0) ){
                    sizeState = 'disabled';
                }
                if(columnState === false && sizeState === false && conditionState === false){
                    return false;
                }else {
                    //设置query值
                    this.oldQuery = this.queryVal;
                    this.updateBtn = false
                    return 'disabled';
                }
            }
        },
        methods:{
            //设置高度
            setHeight(){
                let windowHeight = document.body.clientHeight;
                //初始化 表格高度
                if(this.chartsConfig.dataSourceType === "MySQL"){
                    this.wapperHeight = windowHeight - 240+'px';
                    this.tableHeight = windowHeight - 340;
                }else{
                    this.wapperHeight = windowHeight - 304+'px';
                    this.tableHeight = windowHeight - 400
                }
            },
            //数据源类型改变事件
            dataSourceChange(){
                let type = this.chartsConfig.dataSourceType;
                //重置配置
                this.initialize();
                this.chartsConfig.dataSourceType = type;
                if(type === 'MySQL'){
                    this.getTableName();
                }
                this.setHeight()
            },
            //mysql表 改变事件
            mySQLTableChange(){
                let mysqlData = this.chartsConfig.mysqlData
                //重置配置
                this.initialize();
                this.chartsConfig.dataSourceType = 'MySQL';
                this.chartsConfig.mysqlData = mysqlData;
                //获取mysql字段
                this.getMySQLField()
                this.getMySQLOperator()
            },
            //列参数改变事件
            columnChange(val,i){
                if(this.chartsConfig.dataSourceType === 'MySQL'){
                    let obj = this.chartsConfig.columnOpt.find(item =>{
                        return item.value === val
                    })
                    this.chartsConfig.columnArr[i].aliasName = obj.label
                }
            },
            //条件列改变
            conditionFieldChange(value,i){
                this.chartsConfig.conditionArr[i].operator = '';
                this.chartsConfig.conditionArr[i].value = '';
                this.chartsConfig.conditionArr[i].start = '';
                this.chartsConfig.conditionArr[i].end = '';
                this.chartsConfig.conditionArr[i].values = [];
            },
            //条件operator改变
            conditionOperatorChange(value,i){
                this.chartsConfig.conditionArr[i].value = '';
                this.chartsConfig.conditionArr[i].start = '';
                this.chartsConfig.conditionArr[i].end = '';
                this.chartsConfig.conditionArr[i].values = [];
            },
            //条件添加多个值
            addValue(i){
                this.chartsConfig.conditionArr[i].values.push(this.addValues);
                this.addValues = '';
            },
            //刷新
            refreshChart(){
                this.refresh++;
            },
            //更新
            updateChart(){
                this.oldQuery = this.queryVal;
                this.refresh++;
                this.updateBtn = false;
            },
            //判断是否出现更新按钮
            isUpdate(currentVal){
                this.queryVal = currentVal;
                //判断是否可以生产报表
                if(this.isCanCreate !== 'disabled'){
                    //判断目标值是否有改变
                    if(currentVal !== this.oldQuery){
                        this.updateBtn = true
                        if($(".layui-layer-tips").length === 0){
                            layer.tips('点击更新', '.update-btn', {
                                tips: [3, '#3595CC'],
                                time: 2000
                            });
                        }

                    }else{
                        this.updateBtn = false
                    }
                }else{
                    this.oldQuery = this.queryVal;
                }
            },
            // 初始化
            initialize(){
                //还原配置
                this.chartsConfig = JSON.parse(this.defaultConfig);
                this.emptyTipState = true;
                //销毁已经创建的图表
                this.opt = {};
                this.chartParams.searchParam='';
            },
            /*添加列*/
            addColumn(){
                //this.isCanCreate = 'disabled';
                this.chartsConfig.columnArr.push({
                    field:'',
                    sort:'desc',
                    aliasName: ''
                })
            },
            /*添加条件*/
            addCondition(){
                this.chartsConfig.conditionArr.push({
                    field:'',
                    operator:'',
                    value:'',
                    start:'',
                    end:'',
                    values:[]
                })
            },
            /*生成按钮*/
            createBtn(){
                this.loading = true;
                this.chartsConfig.page.cPage = 1;
                this.getData();
            },
            //获取mysql表
            getTableName(){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/BI/showTables.do',this.$qs.stringify())
                        .then(res=>{
                            let obj = res.data;
                            if(obj.success === 'true'){
                                this.mysqlDataOpt = obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }

                        })
                        .catch(err=>{
                            console.log(err)
                        })
                })
            },
            //获取mysql 列字段
            getMySQLField(){
                this.$nextTick(()=>{
                    this.leftLoading = true;
                    this.$axios.post(this.$baseUrl+'/BI/showColumns.do',this.$qs.stringify({
                        tableName:this.chartsConfig.mysqlData
                    }))
                        .then(res=>{
                            this.leftLoading = false;
                            let obj = res.data;
                            if(obj.success === 'true'){
                                this.chartsConfig.columnOpt = obj.data;
                            }else {
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.leftLoading = false;
                        })
                })
            },
            //获取mysql operator
            getMySQLOperator(){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/BI/getSqlOperators.do',this.$qs.stringify())
                        .then(res=>{
                            let obj = res.data;
                            if(obj.success === 'true'){
                                this.chartsConfig.operatorOpt = [];
                                obj.data.forEach(item =>{
                                    this.chartsConfig.operatorOpt.push({
                                        label:item,
                                        value:item
                                    })
                                })
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }

                        })
                        .catch(err=>{
                            console.log(err)
                        })
                })
            },
            /*获取列参数集合*/
            getColumnField(){
                if(this.chartsConfig.preIndexName === '' || this.chartsConfig.preIndexName === '' ||  this.chartsConfig.preIndexName === ''){
                    return false
                }
                this.$nextTick(()=>{
                    this.leftLoading = true;
                    this.$axios.post(this.$baseUrl+'/BI/getFieldByDynamicTable.do',this.$qs.stringify({
                        pre_index_name:this.chartsConfig.preIndexName,
                        suffix_index_name:this.chartsConfig.suffixIndexName,
                        template_name:this.chartsConfig.templateName
                    }))
                        .then(res=>{
                            this.leftLoading = false;
                            this.chartsConfig.columnOpt = [];
                            res.data.forEach(item=>{
                                let obj = {
                                    value:item.fieldName,
                                    label:item.fieldName
                                }
                                this.chartsConfig.columnOpt.push(obj)

                            })
                        })
                        .catch(err=>{
                             this.leftLoading = false;
                        })
                })
            },
            /*获取数据*/
            getData(page){
                //判断请求的方法
                let url = ''
                if (this.chartsConfig.dataSourceType === 'ElasticSearch') {//es
                    url = '/BI/getDataByParams_dynamicTable.do';
                }else{
                    url = '/BI/getDataBySql.do';
                }
                let param = {}
                if(page){//分页发起的请求
                    param = JSON.parse(this.chartParams.searchParam);
                    param.page = this.chartsConfig.page.cPage
                }else{
                    if (this.chartsConfig.dataSourceType === 'ElasticSearch') {//es
                        param = {
                            starttime:this.dateObj.starttime,//起始时间
                            endtime:this.dateObj.endtime,//结束时间
                            last:this.dateObj.last,
                            unit:this.yUnit,
                            pre_index_name:this.chartsConfig.preIndexName,
                            suffix_index_name:this.chartsConfig.suffixIndexName,
                            template_name:this.chartsConfig.templateName,
                            filters_visual:this.filters,
                            queryBox:this.queryVal,
                            page:this.chartsConfig.page.cPage,//当前页码
                            page_size:this.chartsConfig.page.size,//每页条数
                            size:this.chartsConfig.counts,//显示总数
                            es_columns:JSON.stringify(this.chartsConfig.columnArr)
                        }
                    }else{ //mysql

                        let columnsArr = [];
                        this.chartsConfig.columnArr.forEach(item=>{
                            columnsArr.push({
                                column_value:item.field,
                                sort:item.sort
                            })
                        })
                        param = {
                            tableName : this.chartsConfig.mysqlData,
                            columns:JSON.stringify(columnsArr),
                            wheres:JSON.stringify(this.chartsConfig.conditionArr),
                            page:this.chartsConfig.page.cPage,//当前页码
                            page_size:this.chartsConfig.page.size,//每页条数
                        }
                    }
                }

                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+url,this.$qs.stringify(param))
                        .then(res=>{
                            this.loading = false;
                            //存储查询条件
                            this.chartParams.searchParam = JSON.stringify(param);
                            //处理数据
                            let obj = res.data;
                            this.chartsConfig.tableHead = [];
                            this.chartsConfig.tableData = [];
                            this.chartsConfig.page.allCounts = 0;
                            this.chartsConfig.trueCount = 0;
                            //拼接头部
                            this.chartsConfig.columnArr.forEach((item)=>{
                                this.chartsConfig.tableHead.push({
                                    prop:item.field,
                                    label:item.aliasName === '' ? item.field : item.aliasName,
                                    width:''
                                })
                            })
                            if (obj.success === 'true'){
                                //加载数据
                                this.chartsConfig.tableData = obj.data[0].list;
                                //处理分页
                                this.pageSize = Number(this.chartsConfig.page.size);
                                this.showCount = this.chartsConfig.counts;
                                this.chartsConfig.trueCount = obj.data[0].count;
                                if(this.chartsConfig.dataSourceType === "MySQL"){
                                    this.chartsConfig.page.allCounts = Number(obj.data[0].count);
                                    this.showCount = Number(obj.data[0].count);
                                }else{
                                    if(this.chartsConfig.trueCount >= this.chartsConfig.counts){
                                        this.chartsConfig.page.allCounts = Number(this.chartsConfig.counts);
                                    }else{
                                        this.chartsConfig.page.allCounts = Number(this.chartsConfig.trueCount);
                                    }
                                }
                            } else {
                                layer.msg(obj.message,{icon:5});
                            }

                        })
                        .catch(err=>{
                            this.loading = false;
                        })
                })
            },
            handleCurrentChange(page){
                this.loading = true;
                this.chartsConfig.page.cPage = page;
                this.getData(page);
            },
            /*保存图表*/
            saveChart(){
                let cObj = JSON.parse(JSON.stringify(this.chartsConfig))
                //清空已经获取的参数
                cObj.columnOpt = [];
                cObj.tableData = [];
                //定义参数
                let optStr = {
                    config:cObj,
                   // opt:this.opt
                }
                let params = {
                    title:this.chartParams.chartName,
                    description:this.chartParams.chartDes,
                    filters_visual:this.filters,
                    type:this.chartType,
                    pre_index_name:this.chartsConfig.preIndexName,
                    suffix_index_name:this.chartsConfig.suffixIndexName,
                    template_name:this.chartsConfig.templateName,
                    option:JSON.stringify(optStr),
                    params:this.chartParams.searchParam,
                    isSaveAs:true
                }
                //判断是否是修改图表
                if(this.chartId !== ''){
                    //判断是否是另存图表
                    if(!this.chartParams.otherSave){//不是另存（修改原先的）
                        params.id = this.chartId;
                        params.isSaveAs = false;
                    }
                }
                this.$nextTick(()=>{
                    this.allLoading = true;
                    this.$axios.post(this.$baseUrl+'/BI/saveVisualization.do',this.$qs.stringify(params))
                        .then(res=>{
                            this.allLoading = false;
                            if(res.data.success == 'true'){
                                this.dialogFormVisible = false;
                                layer.msg(res.data.message,{icon:1})
                            }else{
                                layer.msg(res.data.message,{icon:5})
                            }

                        })
                        .catch(err=>{
                            this.allLoading = false;
                            layer.msg('保存失败',{icon:5})
                        })
                })
            },
            /*删除列*/
            removeYaxisTab(i,event){
                if(this.chartsConfig.columnArr.length > 1){
                    this.chartsConfig.columnArr.splice(i,1);
                    event.stopPropagation();
                }
            }
        },
        watch:{
            //检测有无id 有则是修改
            chartId:{
                handler(newV) {
                    if(newV !== ''){
                        this.$nextTick(()=>{
                            this.allLoading = true;
                            this.$axios.post(this.$baseUrl+'/BI/getVisualizationById.do',this.$qs.stringify({
                                id:this.chartId
                            }))
                                .then(res=>{
                                    this.allLoading = false;
                                    let obj = res.data;
                                    if (obj.success == 'true'){
                                        //赋值
                                        if(JSON.parse(obj.data.params).filters){
                                            this.filters = JSON.parse(obj.data.params).filters_visual;
                                            this.defaultFilter = JSON.parse(JSON.parse(obj.data.params).filters);
                                        }
                                        let option = JSON.parse(obj.data.option);
                                        this.indexVal = [obj.data.template_name,obj.data.pre_index_name,obj.data.suffix_index_name,this.chartsConfig.datefield]
                                        this.chartsConfig = option.config;

                                        this.chartParams.chartName = obj.data.title;
                                        this.chartParams.chartDes = obj.data.description;
                                        this.chartParams.searchParam = obj.data.params;
                                        this.chartsConfig.page.cPage = 1
                                        //前台自己获取数据
                                        this.getData()
                                        //获取列参数
                                        if(this.chartsConfig.dataSourceType === 'MySQL'){//mysql
                                            this.getTableName();
                                            this.getMySQLField();
                                            this.getMySQLOperator();
                                        }else{//es
                                            this.getColumnField();
                                        }
                                        //设置高度
                                        this.setHeight();
                                    }else{
                                        layer.msg(res.data.message,{icon:5})
                                    }
                                })
                                .catch(err=>{
                                    this.allLoading = false;
                                })
                        })
                    }
                },
                immediate: true,
                deep: true
            },
            //时间范围改变
            'dateObj'(nv,ov){
                //判断是否具备生成图表的条件
                if(this.isCanCreate !== 'disabled'){
                    this.loading = true;
                    this.chartsConfig.page.cPage = 1;
                    //获取数据
                    this.getData()
                }
            },
            //轮询状态改变
            'intervalObj'(){
                //判断是否启用轮询获取数据
                if (this.intervalObj.state){
                    clearInterval(this.interval)
                    this.interval = setInterval(()=>{
                        //判断是否具备生成图表的条件
                        if(this.isCanCreate !== 'disabled'){
                            //获取数据
                            this.getData()
                        }
                    },this.intervalObj.interval)
                }else {
                    clearInterval(this.interval)
                }
            }
        },
        components:{
            dateLayout,
            chooseIndex,
            jsonView,
            queryFilter,
            vBasetable
        }
    }
</script>

<style scoped>
    .top-zz{
        text-align: center;
        width: 100%;
        height: 50px;
        position: absolute;
        /*background: #;*/
        z-index: 100;
        cursor: no-drop;
        text-shadow: none;
        color: #455b75;
    }
    .from-zz{
        width: 242px;
        height:100%;
        cursor: no-drop;
        position: absolute;
        z-index: 100;
    }
    .choose-wapper{
        height: 50px;
        display: flex;
        align-items: center;
        float: left;
    }
    .saveChart{
        float: right;
        margin-right: 10px;
        margin-top: 10px;
    }
    .date-wapper{
        float: right;
        margin-right: 10px;
        margin-top: 10px;
        position: relative;
        z-index: 101;
    }
    .chart-wapper{
        height: 100%;
        overflow: hidden;
        padding-bottom: 10px;
    }
    .chart-wapper>div{
        float: left;
        background: #303e4e;
        /*height: calc(100vh - 304px);*/
    }
    .config-wapper{
        width: 300px;
        margin: 0 10px;
        position: relative;
    }
    .creatBtn{
        /*width: 50px;*/
        height: 39px;
        text-align: center;
        line-height: 39px;
        position: absolute;
        right: 0;
        top: 0;
        z-index: 1;
        /*background: #3a8ee6;*/
        font-size: 14px;
        padding: 0 8px;
        border-radius: 0;
    }
    .values-wapper span{
        border: 1px dashed #3c5d80;
        padding: 4px 5px;
        color: #409eff;
        font-size: 10px;
        margin: 5px;
        border-left: 2px solid #409eff;
    }
    .prevBtn{
        right: 82px;
    }
    .nextBtn{
        right: 48px;
    }
    .creatBtn:hover{
        /*cursor: pointer;*/
        /*background: #66b1ff;*/
    }
    .config-wapper /deep/ .el-tabs__content{
        height: calc(100% - 69px);
        overflow: auto;
    }
    .chart-wapper /deep/ .el-collapse-item__content{
        padding-bottom: 0;
    }
    .view-wapper{
        width: calc(100% - 330px);
        margin-right: 10px;
        position: relative;
    }
    .config-item{
        /*height:100*/
    }
    .config-wapper /deep/ .el-collapse-item__header{
        height: 35px;
        background: #455b75;
        border-top: 0;
        color: #409eff;
    }
    .config-wapper /deep/ .el-form-item__label{
        color: #FFF!important;
        font-size: 12px;
    }
    .config-wapper /deep/ .el-collapse-item__wrap{
        background: 0;
        padding: 10px;
    }
    .tablist{
        background: #3e4f63;
        margin: 10px 0;
    }
    .charts-title{
        height: 50px;
        text-align: center;
        line-height: 50px;
        color: #10d9f2;
        font-weight: 600;
        font-size: 19px;
    }
    #charts-wapper{
        height: calc(100% - 50px);
    }
    .tip-w{
        font-size: 10px;
        color: #e4956d;
    }
    .addY:hover{
        cursor: pointer;
        color: #10d9f2;
    }
    .tablist /deep/ .el-collapse-item__header{
        position: relative;
        padding-left: 30px;
    }
    .tablist /deep/ .el-collapse-item__arrow{
        position: absolute;
        left: 5px;
        top: 12px;
    }
    .tablist .removeTab{
        position: absolute;
        right: 10px;
        top: 12px;
    }
    .tablist .removeTab:hover{
        color: #e4956d;
    }
    /deep/.el-form-item--small.el-form-item{
        margin-bottom: 10px;
    }
    .empty-tip{
        position: absolute;
        top: 50%;
        left: 50%;
        margin-left: -35px;
        margin-top: -10px;
        color: #6885a7;
    }
    .source-data{
        position: absolute;
        top: 10px;
        right: 10px;
        overflow: auto;
    }
    .source-data button{
        background: 0;
        border: 0;
    }
    .jsonView{
        width: 100%;
        max-height: 80vh;
        overflow: auto;
    }
    .range-p{
        display: flex;
        align-items: center;
    }
    /*    .range-p /deep/ .el-input__inner[type=number]{
            padding-right: 2px;
        }*/
    .range-p span{
        margin: 0 5px;
    }
    .range-p i{
        margin-left: 5px;
    }
    .range-p i:hover{
        cursor: pointer;
        color: #e4956d;
    }
    .range-btn-p{
        text-align: center;
        margin-top: 5px;
    }
    .range-btn-p span:hover{
        cursor: pointer;
        color: #409eff;
    }
    .filter-box{
        padding: 0 10px;
        position: relative;
        top: -10px;
    }
    .filter-box /deep/ .el-input__inner{
        border-radius: 0;
    }
</style>
