<template>
    <div class="content-bg">
        <div class="top-title">
            <div class="choose-wapper">
                <choose-index :busName="this.busIndexName" :indexVal = "indexVal"></choose-index>
            </div>
            <el-button class="saveChart" type="success" plain @click="dialogFormVisible = true">保存</el-button>
            <div class="date-wapper"><date-layout  :busName="busName"></date-layout></div>
        </div>
        <div class="chart-wapper">
            <div class="config-wapper">
                <el-button class="creatBtn" type="primary" @click="getData" :disabled="isCanCreate">生成</el-button>
                <el-tabs v-model="activeName" style="height: 100%;" type="border-card">
                    <el-tab-pane label="数据" name="first">
                        <el-collapse>
                            <el-collapse-item :title="`衡量指标 ${yItem.legendName}`" class="tablist" v-for="(yItem,i) in chartsConfig.yAxisArr" :key="i">
                                <el-form label-position="top" >
                                    <el-form-item label="聚合类型">
                                        <el-select v-model="yItem.aggregationType" placeholder="请选择" @change="yAggregationChange($event,i)" style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in yAggregation"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="聚合参数" v-if="yItem.aggregationType !== '' && yItem.aggregationType !== 'Count' ">
                                        <el-select v-model="yItem.aggregationParam" placeholder="请选择" filterable style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in yItem.aggregationParamArr"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                   <el-form-item label="名称">
                                        <el-input v-model="yItem.yAxisName" size="mini"></el-input>
                                    </el-form-item>
                                    <!--  <el-form-item label="颜色类型">
                                         <el-select v-model="yItem.colorType" placeholder="请选择" style="width: 100%;" size="mini">
                                             <el-option
                                                 v-for="item in colorTypeArr"
                                                 :key="item.value"
                                                 :label="item.label"
                                                 :value="item.value">
                                             </el-option>
                                         </el-select>
                                     </el-form-item>
                                     <el-form-item label="颜色选择">
                                         <el-color-picker v-model="yItem.color1" show-alpha v-if="yItem.colorType === 'solidColor'"></el-color-picker>
                                         <p style="display: flex;" v-else>
                                             <span>0:</span><el-color-picker v-model="yItem.color2[0]" show-alpha></el-color-picker>
                                             <span>1:</span><el-color-picker v-model="yItem.color2[1]" show-alpha></el-color-picker>
                                         </p>
                                     </el-form-item>-->
                                </el-form>
                            </el-collapse-item>
<!--                            <p style="text-align: center;font-size: 12px;margin-bottom: 10px;"><span class="addY" @click="addY"> <i class="el-icon-circle-plus"></i> 添加</span></p>-->
                        </el-collapse>
                        <el-collapse>
                            <el-collapse-item title="Buckets" class="tablist" v-for="(xItem,i) in chartsConfig.xAxisArr" :key="i">
                                <el-form label-position="top" >
                                    <el-form-item label="聚合类型">
                                        <el-select v-model="xItem.aggregationType" @change="xAggregationChange()" placeholder="请选择" style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in xAggregation"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="聚合参数">
                                        <el-select v-model="xItem.aggregationParam" placeholder="请选择" filterable style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in xAggregationParamArr"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="时间间隔" v-if="xItem.aggregationType === 'Date'">
                                        <el-select v-model="xItem.timeInterval" placeholder="请选择" style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in timeIntervalArr"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="排序方式" v-if="xItem.aggregationType === 'Terms'">
                                        <el-select v-model="xItem.orderType" placeholder="请选择" style="width: 100%;" size="mini">
                                            <el-option label="降序" value="desc"></el-option>
                                            <el-option label="升序" value="asc"></el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="展示数量" v-if="xItem.aggregationType === 'Terms'">
                                        <el-input v-model="xItem.topSum" size="mini"></el-input>
                                    </el-form-item>
                                    <el-form-item label="名称">
                                        <el-input v-model="xItem.xAxisName" size="mini"></el-input>
                                    </el-form-item>
                                    <!--<el-form-item label="X轴名称">
                                        <el-input v-model="xItem.xAxisName" size="mini"></el-input>
                                    </el-form-item>-->
                                </el-form>
                            </el-collapse-item>
                        </el-collapse>
                    </el-tab-pane>
                    <el-tab-pane label="基本设定" name="second">
                        <div class="config-item">
                            <el-collapse v-model="configOpened">
                                <el-collapse-item title="标题" class="tablist" name="1">
                                    <el-form label-position="left" label-width="50px">
                                        <el-form-item label="标题">
                                            <el-input v-model="chartsConfig.title.text" size="mini"></el-input>
                                        </el-form-item>
                                    </el-form>
                                </el-collapse-item>
                                <el-collapse-item title="形式" class="tablist" name="5">
                                    <el-form label-position="right" label-width="90px">
                                        <el-form-item label="环形显示">
                                            <el-switch v-model="chartsConfig.raduis.show"></el-switch>
                                        </el-form-item>
                                        <el-form-item label="内环大小" v-if="chartsConfig.raduis.show">
                                            <el-input v-model="chartsConfig.raduis.raduisArr[0]" size="mini"></el-input>
                                        </el-form-item>
                                        <el-form-item label="外环大小" v-if="chartsConfig.raduis.show">
                                            <el-input v-model="chartsConfig.raduis.raduisArr[1]" size="mini"></el-input>
                                        </el-form-item>
                                        <p class="tip-w"  style="margin-bottom: 20px;" v-if="chartsConfig.raduis.show">数值允许：'10'（px）、'10%'两种</p>
                                        <el-form-item label="南丁格尔图">
                                            <el-switch v-model="chartsConfig.roseType.show"></el-switch>
                                        </el-form-item>
                                        <el-form-item label="方式" v-if="chartsConfig.roseType.show">
                                            <el-switch
                                                v-model="chartsConfig.roseType.type"
                                                active-value="radius"
                                                inactive-value="area"
                                                active-text="radius"
                                                inactive-text="area">
                                            </el-switch>
                                        </el-form-item>
                                        <p class="tip-w"  v-if="chartsConfig.roseType.show">'radius' 扇区圆心角展现数据的百分比，半径展现数据的大小。'area' 所有扇区圆心角相同，仅通过半径展现数据大小。</p>
                                    </el-form>
                                </el-collapse-item>
                               <!-- <el-collapse-item title="颜色" class="tablist" name="6">
                                    <el-form label-position="top">
                                        <el-form-item v-for="(i,index) in this.pieData" :label="i.name" :key="index" style="margin-bottom: 0;">
                                            <el-color-picker v-model="i.itemStyle.color"></el-color-picker>
                                        </el-form-item>
                                    </el-form>
                                </el-collapse-item>-->
                                <el-collapse-item title="图例" class="tablist" name="2">
                                    <el-form label-position="left" label-width="80px">
                                        <el-form-item label="是否显示">
                                            <el-switch v-model="chartsConfig.legend.show"></el-switch>
                                        </el-form-item>
                                        <el-form-item label="左边距" v-if="chartsConfig.legend.show">
                                            <el-input v-model="chartsConfig.legend.left" size="mini"></el-input>
                                        </el-form-item>
                                        <el-form-item label="上边距" v-if="chartsConfig.legend.show">
                                            <el-input v-model="chartsConfig.legend.top" size="mini"></el-input>
                                        </el-form-item>
                                        <el-form-item label="文本颜色" v-if="chartsConfig.legend.show">
                                            <el-color-picker v-model="chartsConfig.legend.textStyle.color"></el-color-picker>
                                        </el-form-item>
                                        <p class="tip-w">边距值可选类型<br/>1:'center‘、'left‘、'top‘、'bottom‘;<br/>2:'10‘，单位为像素；<br/>3:'10%‘,百分比</p>
                                    </el-form>
                                </el-collapse-item>
                                <el-collapse-item title="工具栏" class="tablist" name="3">
                                    <el-form label-position="left" label-width="80px">
                                        <el-form-item label="是否显示">
                                            <el-switch v-model="chartsConfig.toolbox.show"></el-switch>
                                        </el-form-item>
                                        <el-form-item label="工具选择" v-if="chartsConfig.toolbox.show">
                                            <el-checkbox-group v-model="chartsConfig.toolbox.feature">
                                                <el-checkbox label="dataView" name="type">数据视图</el-checkbox>
<!--                                                <el-checkbox label="magicType" name="type">类型切换</el-checkbox>-->
<!--                                                <el-checkbox label="dataZoom" name="type">区域缩放</el-checkbox>-->
<!--                                                <el-checkbox label="restore" name="type">还原</el-checkbox>-->
                                                <el-checkbox label="saveAsImage" name="type">保存</el-checkbox>
                                            </el-checkbox-group>
                                        </el-form-item>
                                    </el-form>
                                </el-collapse-item>
                                <el-collapse-item title="边距" class="tablist" name="4">
                                    <el-form label-position="left" label-width="80px">
                                        <el-form-item label="左边距">
                                            <el-input v-model="chartsConfig.grid.left" size="mini"></el-input>
                                        </el-form-item>
                                        <el-form-item label="右边距">
                                            <el-input v-model="chartsConfig.grid.right" size="mini"></el-input>
                                        </el-form-item>
                                        <el-form-item label="上边距">
                                            <el-input v-model="chartsConfig.grid.top" size="mini"></el-input>
                                        </el-form-item>
                                        <el-form-item label="下边距">
                                            <el-input v-model="chartsConfig.grid.bottom" size="mini"></el-input>
                                        </el-form-item>
                                    </el-form>
                                    <p class="tip-w">边距值可选类型<br/>1:'center‘、'left‘、'top‘、'bottom‘;<br/>2:'10‘，单位为像素；<br/>3:'10%‘,百分比</p>
                                </el-collapse-item>
                            </el-collapse>

                        </div>
                    </el-tab-pane>
<!--                    <el-tab-pane label="角色管理" name="third">角色管理</el-tab-pane>-->
                </el-tabs>
            </div>
            <div class="view-wapper" >
                <div class="charts-title">{{chartsConfig.title.text}}</div>
                <div id="charts-wapper"></div>
                <div class="empty-tip" v-if="this.emptyTipState">暂无结果</div>
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
    import dateLayout from '../dashboard/dateLayout'
    import chooseIndex from '../dashboard/chooseIndex'
    import jsonView from 'vue-json-views'
    import bus from '../common/bus';
    const echarts = require('echarts');
    export default {
        name: "barChart",
        data() {
            return {
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
                sourceData:'',
                //数据为空时提示状态
                emptyTipState:true,
                //默认配置 用于还原原始配置
                defaultConfig:'',
                //图表id 用于修改图表
                chartId:'',
                defaultVal:[],
                //bus监听事件的名称
                busName:'createPieChart',
                busIndexName:'pieIndexName',
                //默认数据源索引
                indexVal:'',
                configOpened:['1','2','3','4','5','6','7'],
                //图表基本配置
                chartsConfig:{
                    //数据源
                    indexName:'',
                    templateName:'',
                    //标题
                    title:{
                        text:''
                    },
                    //图例
                    legend:{
                        show:true,
                        left:'center',
                        top:'10',
                        textStyle: {
                            color: "#fff"
                        }
                    },
                    //南丁格尔图
                    roseType:{
                        show:false,
                        type:'radius'
                    },
                    //内外环大小
                    raduis:{
                        show:false,
                        raduisArr:['0','70%']
                    },
                    //工具栏
                    toolbox: {
                        show:false,
                        feature:[],
                    },
                    //边距
                    grid:{
                        left:'10%',
                        right:'10%',
                        bottom:'20%',
                        top:'60'
                    },
                    //x轴参数集合
                    xAxisArr:[{
                        aggregationType:'',
                        aggregationParam:'',
                        timeInterval:'',//时间间隔 聚合字段为时间时
                        xAxisName:'',//x轴名称
                        orderType:'desc',//排序方式
                        topSum:'5'//展示个数
                    }],
                    //y轴参数集合
                    yAxisArr:[{
                        aggregationType:'',
                        aggregationParam:'',
                        aggregationParamArr:[],//x轴聚合参数
                        yAxisName:'',
                        legendName:'',
                    }]
                },
                htmlTitle:'创建饼图',
                activeName:'first',
                //y轴聚合类型
                yAggregation: [{
                    value: 'Count',
                    label: 'Count'
                },{
                    value: 'Sum',
                    label: 'Sum'
                }],
                //x轴聚合类型
                xAggregation: [{
                    value: 'Terms',
                    label: 'Terms'
                }],
                //x轴聚合参数
                xAggregationParamArr:[],
                //x轴时间间隔
                timeIntervalArr:[
                    {
                        value: 'second',
                        label: '每秒'
                    },{
                        value: 'minute',
                        label: '每分钟'
                    },
                    {
                        value: 'hourly',
                        label: '每小时'
                    },{
                        value: 'daily',
                        label: '每天'
                    },{
                        value: 'weekly',
                        label: '每周'
                    },{
                        value: 'monthly',
                        label: '每月'
                    },{
                        value: 'yearly',
                        label: '每年'
                    }
                ],
                //颜色类型集合
                colorTypeArr:[
                    {
                        value: 'solidColor',
                        label: '纯色'
                    },{
                        value: 'tbColor',
                        label: '上下渐变'
                    },{
                        value: 'lrColor',
                        label: '左右渐变'
                    }
                ],
                //echarts结构
                opt:{},
                color:['#1E73F0','#00D1CE','#33C3F7','#3952D3','#185BFF','#2455AD','#74EE9A','#253479'],
                pieData:[
                    // {value:335, name:'info',itemStyle: {color:'#1E73F0'}},
                    // {value:310, name:'message',itemStyle: {color:'#00D1CE'}},
                    // {value:234, name:'alert',itemStyle: {color:'#33C3F7'}},
                    // {value:135, name:'error',itemStyle: {color:'#3952D3'}}
                ]
            }
        },
        created(){
            //原始配置
            this.defaultConfig = JSON.stringify(this.chartsConfig)
            //时间监听
            bus.$on(this.busName,(arr)=>{
                console.log(arr)
            })
            //数据源监听
            bus.$on(this.busIndexName,(arr)=>{
                //还原配置
                this.initialize()
                //设置数据源
                this.chartsConfig.indexName = arr[1];
                this.chartsConfig.templateName = arr[0];


            })
        },
        computed:{
            'isCanCreate'(){
                let yState = false;
                let xState = false;
                //
                this.chartsConfig.yAxisArr.forEach((item)=>{
                    if (item.aggregationType === ''){
                        yState = 'disabled';
                    } else if(item.aggregationParam === '' && item.aggregationType !== 'Count'){
                        yState = 'disabled';
                    }
                })
                if(this.chartsConfig.xAxisArr[0].aggregationType === '' ){
                    xState = 'disabled';
                }else if(this.chartsConfig.xAxisArr[0].aggregationParam === ''){
                    xState = 'disabled';
                }
                if(yState == false && xState == false){
                    return false
                }else {
                    return 'disabled';
                }
            }
        },
        beforeDestroy(){
            //在组件销毁前移除监听事件
            bus.$off(this.busName);
            bus.$off(this.busIndexName);
        },
        methods:{
            // 初始化
            initialize(){
                //还原配置
                this.chartsConfig = JSON.parse(this.defaultConfig);
                this.emptyTipState = true;
                //销毁已经创建的图表
                this.opt = {};
                echarts.init(document.getElementById('charts-wapper')).dispose();//销毁前一个实例
                this.chartParams.searchParam='';
            },
            /*添加y轴*/
            addY(){
                this.chartsConfig.yAxisArr.push({
                    aggregationType:'',
                    yAxisName:'',
                    legendName:'',
                    colorType:'solidColor',
                    color1:'#5bc0de',
                    color2:['rgba(15,219,243,1)','rgba(68,47,148,0.5)']
                })
            },
            /*y轴聚合类型改变*/
            yAggregationChange($event,index){
                if (this.chartsConfig.indexName === ''){
                    layer.msg('请选择数据源',{icon:'5'});
                    return;
                }
                //清空聚合字段
                this.chartsConfig.yAxisArr[index].aggregationParam = ''
                this.chartsConfig.yAxisArr[index].aggregationParamArr = [];
                //获取参数集合
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/BI/getFieldByYAxisAggregation.do',this.$qs.stringify(
                        {
                            agg:$event,
                            indexName:this.chartsConfig.indexName,
                            templateName:this.chartsConfig.templateName
                        }
                    ))
                        .then(res=>{
                            layer.closeAll('loading');
                            res.data.forEach(item=>{
                                let obj = {
                                    value:item.fieldName,
                                    label:item.fieldName
                                }
                                this.chartsConfig.yAxisArr[index].aggregationParamArr.push(obj)

                            })
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*x轴聚合类型改变*/
            xAggregationChange(){
                if (this.chartsConfig.indexName === ''){
                    layer.msg('请选择数据源',{icon:'5'});
                    return;
                }
                //清空聚合字段
                this.chartsConfig.xAxisArr[0].aggregationParam = ''
                this.xAggregationParamArr = []
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/BI/getFieldByXAxisAggregation.do',this.$qs.stringify({
                        agg:this.chartsConfig.xAxisArr[0].aggregationType,
                        indexName:this.chartsConfig.indexName,
                        templateName:this.chartsConfig.templateName
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            res.data.forEach(item=>{
                                let obj = {
                                    value:item.fieldName,
                                    label:item.fieldName
                                }
                                //console.log(this.chartsConfig.xAxisArr[0])
                                this.xAggregationParamArr.push(obj)
                                //console.log(this.chartsConfig.xAxisArr[0].aggregationParamArr)
                            })
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取数据*/
            getData(){
                let param = {
                    x_agg:this.chartsConfig.xAxisArr[0].aggregationType, //拆分参数类型
                    x_field:this.chartsConfig.xAxisArr[0].aggregationParam,//拆分参数
                    y_field:this.chartsConfig.yAxisArr[0].aggregationParam,//聚合参数
                    y_agg:this.chartsConfig.yAxisArr[0].aggregationType,//聚合参数类型
                    indexName:this.chartsConfig.indexName,//数据源
                    size:this.chartsConfig.xAxisArr[0].topSum,//展示数据的个数
                    sort:this.chartsConfig.xAxisArr[0].orderType,
                    intervalType:'',
                    intervalValue:''
                };
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/BI/getDataByChartParams.do',this.$qs.stringify(param))
                        .then(res=>{
                            layer.closeAll('loading');
                            //存储查询条件
                            this.chartParams.searchParam = JSON.stringify(param);
                            let obj = res.data;
                            if (obj.success === 'true'){
                                let xDataArr = obj.data[0].name;
                                let yDataArr = obj.data[0].data;
                                if(xDataArr.length > 0){
                                    this.sourceData = obj.data;
                                    this.emptyTipState = false;
                                    this.pieData = [];
                                    for (let i in xDataArr) {
                                        this.pieData.push({
                                            name:xDataArr[i],
                                            value:yDataArr[i],
                                            itemStyle: {color:this.color[i]}
                                        })
                                    }
                                    this.createChart(this.pieData)
                                }else{
                                    this.emptyTipState = true;
                                    echarts.init(document.getElementById('charts-wapper')).dispose();//销毁前一个实例
                                }
                            } else {
                                layer.msg(obj.message,{icon:5});
                            }
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*创建图表*/
            createChart(pieArr){
                if(this.chartsConfig.roseType.show === false){
                    this.chartsConfig.roseType.type = false;
                }
                if(!this.chartsConfig.raduis.show){
                    this.chartsConfig.raduis.raduisArr = ['0','70%']
                }
                this.opt = {
                    title: {
                        text: "",
                        x:"center",
                        textStyle:{
                            color:"#5bc0de"
                        }
                    },
                    legend: this.chartsConfig.legend,
                    tooltip: {
                        trigger: 'item',
                        formatter: '{a} <br/>{b} : {c} ({d}%)'
                    },
                    roseType:this.chartsConfig.roseType.type,
                    toolbox: {
                        show:this.chartsConfig.toolbox.show,
                        feature: {
                            dataView: {
                                show:this.chartsConfig.toolbox.feature.includes('dataView'),
                                readOnly: "false",
                                backgroundColor :"#3f5267",
                                textareaColor:'#3f5267',
                                textColor: "#fff"
                            },
                            saveAsImage: {
                                show:this.chartsConfig.toolbox.feature.includes('saveAsImage'),
                            }
                        },
                        iconStyle:{
                            borderColor: "#56769a"
                        }
                    },
                    color:['#1E73F0','#00D1CE','#33C3F7','#3952D3','#185BFF','#2455AD','#74EE9A','#253479'],
                    grid:this.chartsConfig.grid,
                    series: [
                        {
                            name: this.chartsConfig.yAxisArr[0].yAxisName,
                            type: 'pie',
                            /*radius : '55%',
                            center: ['50%', '60%'],*/
                            radius:this.chartsConfig.raduis.raduisArr,
                            data:pieArr,
                            // data:this.echartData.yAxisArr,
                            itemStyle: {
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }
                    ]
                }
                /*for(let i in this.pieData){
                    if(this.yAxisArr[i].colorType === 'solidColor'){
                        let obj = {
                            name: this.yAxisArr[i].legendName,
                            type: 'bar',
                            itemStyle: {
                                color:this.yAxisArr[i].color1
                            },
                            data: [1121,2231,432]
                        }
                        this.opt.series.push(obj)
                    }else if(this.yAxisArr[i].colorType === 'tbColor'){
                        let obj = {
                            name: this.yAxisArr[i].legendName,
                            type: 'bar',
                            itemStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                    offset: 0,
                                    color: this.yAxisArr[i].color2[0]
                                },{
                                    offset: 1,
                                    color: this.yAxisArr[i].color2[1]
                                }])
                            },
                            data: [1121,2231,432]
                        }
                        this.opt.series.push(obj)
                    }else if(this.yAxisArr[i].colorType === 'lrColor'){
                        let obj = {
                            name: this.yAxisArr[i].legendName,
                            type: 'bar',
                            itemStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{
                                    offset: 0,
                                    color: this.yAxisArr[i].color2[0]
                                },{
                                    offset: 1,
                                    color: this.yAxisArr[i].color2[1]
                                }])
                            },
                            data: [1121,2231,432]
                        }
                        this.opt.series.push(obj)
                    }
                }*/
                echarts.init(document.getElementById('charts-wapper')).dispose();//销毁前一个实例
                let myChart =  echarts.init(document.getElementById('charts-wapper'));
                //console.log(JSON.stringify(this.opt))
                myChart.setOption(this.opt);
                window.addEventListener("resize",()=>{
                    myChart.resize();
                });
            },
            /*保存图表*/
            saveChart(){
                for (let j in this.chartsConfig.yAxisArr){
                    this.chartsConfig.yAxisArr[j].aggregationParamArr = [];
                    this.opt.series = [];
                }
                let optStr = {
                    config:this.chartsConfig,
                    opt:this.opt
                }
                let params = {
                    title:this.chartParams.chartName,
                    description:this.chartParams.chartDes,
                    type:'pie',
                    indexName:this.chartsConfig.indexName,
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
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/BI/saveVisualization.do',this.$qs.stringify(params))
                        .then(res=>{
                            layer.closeAll('loading');
                            if(res.data.success == 'true'){
                                this.dialogFormVisible = false;
                                layer.msg(res.data.message,{icon:1})
                            }else{
                                layer.msg(res.data.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            layer.closeAll('loading');
                            layer.msg('保存失败',{icon:5})
                        })
                })
            },
        },
        watch:{
            //检测有无id 有则是修改
            'chartId'(newV){
                if(newV !== ''){
                    this.$nextTick(()=>{
                        layer.load(1);
                        this.$axios.post(this.$baseUrl+'/BI/getVisualizationById.do',this.$qs.stringify({
                            id:this.chartId
                        }))
                            .then(res=>{
                                layer.closeAll('loading');
                                let obj = res.data;
                                if (obj.success == 'true'){
                                    let option = JSON.parse(obj.data.option);
                                    this.chartsConfig = option.config;
                                    this.indexVal = obj.data.indexName;
                                    //x轴聚合参数
                                    let xap = this.chartsConfig.xAxisArr[0].aggregationParam;
                                    this.xAggregationChange();
                                    this.chartsConfig.xAxisArr[0].aggregationParam = xap;
                                    //y轴聚合参数
                                    for(let i=0;i < this.chartsConfig.yAxisArr.length;i++){
                                        let yap= this.chartsConfig.yAxisArr[i].aggregationParam;
                                        this.yAggregationChange(this.chartsConfig.yAxisArr[i].aggregationType,i);
                                        this.chartsConfig.yAxisArr[i].aggregationParam = yap
                                    }
                                    this.chartParams.chartName = obj.data.title;
                                    this.chartParams.chartDes = obj.data.description;
                                    this.chartParams.searchParam = obj.data.params;

                                    //后台直接返回数据
                                    let xD = JSON.parse(obj.data.data)[0].name;
                                    let yD = JSON.parse(obj.data.data)[0].data;
                                    this.pieData = [];
                                    for (let i in xD) {
                                        this.pieData.push({
                                            name:xD[i],
                                            value:yD[i],
                                            itemStyle: {color:this.color[i]}
                                        })
                                    }
                                    this.createChart(this.pieData)
                                    this.emptyTipState = false;
                                    this.sourceData = JSON.parse(obj.data.data);
                                    //前台自己获取数据
                                    //this.getData()
                                }else{
                                    layer.msg('获取数据失败',{icon:5})
                                }
                            })
                            .catch(err=>{
                                layer.closeAll('loading');

                            })
                    })
                }
            },
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                //判断是否有参数  有参数说明是修改功能页面
                if(JSON.stringify(to.query) !== "{}"){
                    // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                    //修改此组件的name值
                    vm.$options.name = 'pieChart'+ to.query.id;
                    //修改data参数
                    vm.htmlTitle = `修改 ${to.query.name}`;
                    vm.busName = 'resivePieChart'+to.query.id;
                    vm.busIndexName = 'pieIndexName' +to.query.id;
                    //时间监听
                    bus.$on(vm.busName,(arr)=>{
                        console.log(arr)
                    })
                    //数据源监听
                    bus.$on(vm.busIndexName,(arr)=>{
                        //还原配置
                        vm.initialize()
                        //设置数据源
                        vm.chartsConfig.indexName = arr[1];
                        vm.chartsConfig.templateName = arr[0];
                    })
                    //将路由存放在本地 用来刷新页面时添加路由
                    let obj = {
                        path:'pieChart'+to.query.id,
                        component:'dashboard/pieChart.vue',
                        title:'修改'
                    }
                    sessionStorage.setItem('/pieChart'+to.query.id,JSON.stringify(obj))
                    if(vm.chartId === '' || vm.chartId !== to.query.id){
                        vm.chartId = to.query.id;
                    }
                }


            })

        },
        components:{
            dateLayout,
            chooseIndex,
            jsonView
        }
    }
</script>

<style scoped>
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
    }
    .chart-wapper{
        height: 100%;
        overflow: hidden;
        padding-bottom: 10px;
    }
    .chart-wapper>div{
        float: left;
        background: #303e4e;
        height: calc(100vh - 237px);
    }
    .config-wapper{
        width: 300px;
        margin: 0 10px;
        position: relative;
    }
    .creatBtn{
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
    .creatBtn:hover{
        cursor: pointer;
        background: #66b1ff;
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
</style>
