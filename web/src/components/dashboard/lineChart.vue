<template>
    <div class="content-bg">
        <div class="top-title"><!--{{htmlTitle}}-->
            <div class="top-zz" v-if="this.htmlTitle.substr(0,2) == '查看'"></div>
            <div class="choose-wapper">
                <choose-index :busName="this.busIndexName" :arr = "indexVal"></choose-index>
            </div>
            <el-button class="saveChart" type="success" plain @click="dialogFormVisible = true">保存</el-button>
            <div class="date-wapper"><date-layout  :busName="busName"></date-layout></div>
        </div>
        <div class="chart-wapper">
            <div class="config-wapper">
                <el-button class="creatBtn" type="primary" @click="getData" :disabled="isCanCreate || this.htmlTitle.substr(0,2) == '查看'">生成</el-button>
                <el-tabs v-model="activeName" style="height: 100%;" type="border-card">
                    <el-tab-pane label="数据" name="first">
                        <el-collapse>
                            <el-collapse-item class="tablist" v-for="(yItem,i) in chartsConfig.yAxisArr" :key="i">
                                <template slot="title" class="collapseTit">
                                    Y轴 {{yItem.legendName}}<i class="header-icon el-icon-error removeTab" @click="removeTab(i,$event)"></i>
                                </template>
                                <el-form label-position="top" style="position: relative">
                                    <div class="from-zz" v-if="htmlTitle.substr(0,2) == '查看'"></div>
<!--                                    <el-form-item label="图例说明">-->
<!--                                        <el-input v-model="yItem.legendName" size="mini"></el-input>-->
<!--                                    </el-form-item>-->
                                    <el-form-item label="聚合类型">
                                        <el-select v-model="yItem.aggregationType" @change="yAggregationChange($event,i)" placeholder="请选择" style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in yAggregation"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="聚合参数" v-if="yItem.aggregationType !== ''  && yItem.aggregationType !== 'Count' ">
                                        <el-select v-model="yItem.aggregationParam" placeholder="请选择" filterable style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in yItem.aggregationParamArr"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="Y轴名称">
                                        <el-input v-model="yItem.yAxisName" size="mini"></el-input>
                                    </el-form-item>
                                    <el-form-item label="颜色类型">
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
                                    </el-form-item>
                                    <el-form-item label="区域填充">
                                        <el-switch
                                            v-model="yItem.areaShow"
                                            active-value="1"
                                            inactive-value="0"
                                            active-text="开启"
                                            inactive-text="关闭">
                                        </el-switch>
                                    </el-form-item>
                                </el-form>
                            </el-collapse-item>
<!--                            <p style="text-align: center;font-size: 12px;margin-bottom: 10px;"><span class="addY" @click="addY"> <i class="el-icon-circle-plus"></i> 添加</span></p>-->
                        </el-collapse>
                        <el-collapse>
                            <el-collapse-item title="X轴" class="tablist" v-for="(xItem,i) in chartsConfig.xAxisArr" :key="i">
                                <el-form label-position="top" style="position: relative">
                                    <div class="from-zz" v-if="htmlTitle.substr(0,2) == '查看'"></div>
                                    <el-form-item label="聚合类型">
                                        <el-select v-model="xItem.aggregationType" placeholder="请选择" style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in xAggregation"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="聚合参数">
                                        <el-select v-model="xItem.aggregationParam" filterable placeholder="请选择" style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in xItem.aggregationParamArr"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="时间间隔类型">
                                        <el-select v-model="xItem.timeType"  placeholder="请选择" @blur="timeIntervalBlur" style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in timeIntervalArr"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="间隔数值">
                                        <el-input v-model="xItem.timeInterval" size="mini"></el-input>
                                    </el-form-item>
                                    <el-form-item label="X轴名称">
                                        <el-input v-model="xItem.xAxisName" size="mini"></el-input>
                                    </el-form-item>
                                </el-form>
                            </el-collapse-item>
                        </el-collapse>
                    </el-tab-pane>
                    <el-tab-pane label="基本设定" name="second">
                        <div class="config-item">
                            <el-collapse v-model="configOpened">
                                <el-collapse-item title="标题" class="tablist" name="1">
                                    <el-form label-position="left" label-width="50px" style="position: relative">
                                        <div class="from-zz" v-if="htmlTitle.substr(0,2) == '查看'"></div>
                                        <el-form-item label="标题">
                                            <el-input v-model="chartsConfig.title.text" size="mini"></el-input>
                                        </el-form-item>
                                    </el-form>
                                </el-collapse-item>
                                <el-collapse-item title="X轴" class="tablist" name="5">
                                    <el-form label-position="left" label-width="80px" style="position: relative">
                                        <div class="from-zz" v-if="htmlTitle.substr(0,2) == '查看'"></div>
                                        <el-form-item label="名称颜色">
                                            <el-color-picker v-model="chartsConfig.xNormal.nameTextStyle.color" size="mini"></el-color-picker>
                                        </el-form-item>
                                        <el-form-item label="轴线颜色">
                                            <el-color-picker v-model="chartsConfig.xNormal.axisLine.lineStyle.color" size="mini"></el-color-picker>
                                        </el-form-item>
                                        <el-form-item label="刻度间隔">
                                            <el-input v-model="chartsConfig.xNormal.axisLabel.interval" size="mini"></el-input>
                                        </el-form-item>
                                        <p class="tip-w" style="margin-bottom: 15px;">坐标轴刻度标签的显示间隔,'auto'为默认不重叠，设置成 0 强制显示所有标签,设置为 1，表示『隔一个标签显示一个标签』,依次类推。</p>
                                        <el-form-item label="刻度角度">
                                            <el-input v-model="chartsConfig.xNormal.axisLabel.rotate" size="mini"></el-input>
                                        </el-form-item>
                                        <p class="tip-w" style="margin-bottom: 15px;">旋转的角度从 -90 度到 90 度。</p>
                                        <el-form-item label="文本颜色">
                                            <el-color-picker v-model="chartsConfig.xNormal.axisLabel.color" size="mini"></el-color-picker>
                                        </el-form-item>
                                        <el-form-item label="文本大小">
                                            <el-input v-model="chartsConfig.xNormal.axisLabel.fontSize" size="mini"></el-input>
                                        </el-form-item>
                                    </el-form>
                                </el-collapse-item>
                                <el-collapse-item title="Y轴" class="tablist" name="6">
                                    <el-form label-position="left" label-width="85px" style="position: relative">
                                        <div class="from-zz" v-if="htmlTitle.substr(0,2) == '查看'"></div>
                                        <el-form-item label="名称颜色">
                                            <el-color-picker v-model="chartsConfig.yNormal.nameTextStyle.color" size="mini"></el-color-picker>
                                        </el-form-item>
                                        <el-form-item label="轴线颜色">
                                            <el-color-picker v-model="chartsConfig.yNormal.axisLine.lineStyle.color" size="mini"></el-color-picker>
                                        </el-form-item>
                                        <el-form-item label="文本颜色">
                                            <el-color-picker v-model="chartsConfig.yNormal.axisLabel.color" size="mini"></el-color-picker>
                                        </el-form-item>
                                        <el-form-item label="文本大小">
                                            <el-input v-model="chartsConfig.yNormal.axisLabel.fontSize" size="mini"></el-input>
                                        </el-form-item>
                                        <el-form-item label="分割线">
                                            <el-switch v-model="chartsConfig.yNormal.splitLine.show" size="mini"></el-switch>
                                        </el-form-item>
                                        <el-form-item label="分割线颜色" v-if="chartsConfig.yNormal.splitLine.show">
                                            <el-color-picker v-model="chartsConfig.yNormal.splitLine.lineStyle.color" size="mini"></el-color-picker>
                                        </el-form-item>
                                        <el-form-item label="分割线样式" v-if="chartsConfig.yNormal.splitLine.show">
                                            <el-select v-model="chartsConfig.yNormal.splitLine.lineStyle.type" style="width: 100%;" size="mini">
                                                <el-option label="solid" value="solid"></el-option>
                                                <el-option label="dashed" value="dashed"></el-option>
                                                <el-option label="dotted" value="dotted"></el-option>
                                            </el-select>
                                        </el-form-item>
                                        <el-form-item label="分割线透明" v-if="chartsConfig.yNormal.splitLine.show">
                                            <el-input v-model="chartsConfig.yNormal.splitLine.lineStyle.opacity" size="mini"></el-input>
                                        </el-form-item>
                                        <p class="tip-w" v-if="chartsConfig.yNormal.splitLine.show">支持从 0 到 1 的数字，为 0 时不绘制分割线。</p>
                                    </el-form>
                                </el-collapse-item>
                                <el-collapse-item title="图例" class="tablist" name="2">
                                    <el-form label-position="left" label-width="80px" style="position: relative">
                                        <div class="from-zz" v-if="htmlTitle.substr(0,2) == '查看'"></div>
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
                                    <el-form label-position="left" label-width="80px" style="position: relative">
                                        <div class="from-zz" v-if="htmlTitle.substr(0,2) == '查看'"></div>
                                        <el-form-item label="是否显示">
                                            <el-switch v-model="chartsConfig.toolbox.show"></el-switch>
                                        </el-form-item>
                                        <el-form-item label="工具选择" v-if="chartsConfig.toolbox.show">
                                            <el-checkbox-group v-model="chartsConfig.toolbox.feature">
                                                <el-checkbox label="dataView" name="type">数据视图</el-checkbox>
                                                <el-checkbox label="magicType" name="type">类型切换</el-checkbox>
<!--                                                <el-checkbox label="dataZoom" name="type">区域缩放</el-checkbox>-->
                                                <el-checkbox label="restore" name="type">还原</el-checkbox>
                                                <el-checkbox label="saveAsImage" name="type">保存</el-checkbox>
                                            </el-checkbox-group>
                                        </el-form-item>
                                    </el-form>
                                </el-collapse-item>
                                <el-collapse-item title="边距" class="tablist" name="4">
                                    <el-form label-position="left" label-width="80px" style="position: relative">
                                        <div class="from-zz" v-if="htmlTitle.substr(0,2) == '查看'"></div>
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
                    <jsonView :data="this.sourceData" theme="one-dark" :line-height="15" :deep="5" class="jsonView"></jsonView>
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
    import bus from '../common/bus';
    import jsonView from 'vue-json-views'
    const echarts = require('echarts');
    export default {
        name: "lineChart",
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
                //时间范围
                dateArr:[],
                sourceData:'',
                //数据为空时提示状态
                emptyTipState:true,
                defaultConfig:'',
                //图表id 用于修改图表
                chartId:'',
                defaultVal:[],
                //bus监听事件的名称
                busName:'createLineChart',
                busIndexName:'lineIndexName',
                //默认数据源索引
                indexVal:[],
                configOpened:['1','2','3','4','5','6'],
                //图表基本配置
                chartsConfig:{
                    //templateName
                    templateName:'',
                    //index前名
                    preIndexName:'',
                    //index后时间
                    suffixIndexName:'',
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
                    //工具栏
                    toolbox: {
                        show:true,
                        feature:[],
                    },
                    //边距
                    grid:{
                        left:'10%',
                        right:'10%',
                        bottom:'20%',
                        top:'60'
                    },
                    //x轴通用配置
                    xNormal:{
                        nameTextStyle:{
                            color:'#5bc0de'
                        },
                        axisLine:{
                            lineStyle:{
                                color:'#5bc0de'
                            }
                        },
                        axisLabel:{
                            interval:'auto',
                            rotate:"20",
                            color:'#5bc0de',
                            fontSize:'12'
                        }
                    },
                    yNormal:{
                        nameTextStyle:{
                            color:'#5bc0de'
                        },
                        axisLine:{
                            lineStyle:{
                                color:'#5bc0de'
                            }
                        },
                        splitLine:{
                            show:false,
                            lineStyle:{
                                color:'#417D79',
                                type:'dashed',
                                opacity:'0.5'
                            }
                        },
                        axisLabel:{
                            color:'#5bc0de',
                            fontSize:'12'
                        }
                    },
                    //x轴参数集合
                    xAxisArr:[{
                        aggregationType:'Date',
                        aggregationParam:'',//聚合字段
                        aggregationParamArr:[],//x轴聚合参数集合
                        timeType:'hourly',
                        timeInterval:'1',
                        xAxisName:'',
                    }],
                    //y轴参数集合
                    yAxisArr:[{
                        aggregationType:'',
                        aggregationParam:'',//聚合字段
                        aggregationParamArr:[],//字段集合
                        yAxisName:'',
                        legendName:'',
                        areaShow:'',
                        colorType:'solidColor',
                        color1:'#5bc0de',
                        color2:['rgba(15,219,243,1)','rgba(68,47,148,0.5)']
                    }]
                },
                htmlTitle:'创建折线图',
                activeName:'first',
                //y轴聚合类型
                yAggregation: [{
                    value: 'Count',
                    label: 'Count'
                },{
                    value: 'Sum',
                    label: 'Sum'
                },{
                    value: 'Average',
                    label: 'Average'
                },{
                    value: 'Max',
                    label: 'Max'
                },{
                    value: 'Min',
                    label: 'Min'
                }],
                //x轴聚合类型
                xAggregation: [{
                    value: 'Date',
                    label: 'Date Histogram'
                }],
                //x轴时间间隔
                timeIntervalArr:[
                    {
                        value: 'second',
                        label: '秒'
                    },{
                        value: 'minute',
                        label: '分钟'
                    },
                    {
                        value: 'hourly',
                        label: '小时'
                    },{
                        value: 'daily',
                        label: '天'
                    },{
                        value: 'weekly',
                        label: '周'
                    },{
                        value: 'monthly',
                        label: '月'
                    },{
                        value: 'yearly',
                        label: '年'
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
                opt:{}
            }
        },
        created(){
            this.defaultConfig = JSON.stringify(this.chartsConfig);
            //判断是否有参数  有参数说明是修改功能页面
            if(JSON.stringify(this.$route.query) !== "{}"  && this.$route.query.type === 'edit'){
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                this.$options.name = 'editLineChart'+ this.$route.query.id;
                //修改data参数
                this.htmlTitle = `修改 ${this.$route.query.name}`;
                this.busName = 'resiveLineChart'+this.$route.query.id;
                this.busIndexName = 'lineIndexName' +this.$route.query.id;
                //时间范围监听事件
                bus.$on(this.busName,(arr)=>{
                    this.dateArr = arr;
                })
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'editLineChart'+this.$route.query.id,
                    component:'dashboard/lineChart.vue',
                    title:'修改'
                }
                sessionStorage.setItem('/editLineChart'+this.$route.query.id,JSON.stringify(obj))
                if(this.chartId === '' || this.chartId !== this.$route.query.id){
                    this.chartId = this.$route.query.id;
                }
                //数据源监听事件
                bus.$on(this.busIndexName,(arr)=>{
                    //还原配置
                    this.initialize();
                    //设置数据源
                    this.chartsConfig.suffixIndexName = arr[2];
                    this.chartsConfig.preIndexName = arr[1];
                    this.chartsConfig.templateName = arr[0];
                    for(let i in arr){
                        if(arr[i] == ''){
                            return
                        }
                    }
                    //获取时间字段
                    this.xAggregationChange();
                })
            }else if(this.$route.query.type === 'see'){//查看
                //修改此组件的name值
                this.$options.name = 'seeLineChart'+ this.$route.query.id;
                //修改data参数
                this.htmlTitle = `查看 ${this.$route.query.name}`;
                this.busName = 'seeLineChart'+this.$route.query.id;
                this.busIndexName = 'seeLineIndexName' +this.$route.query.id;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'seeLineChart'+this.$route.query.id,
                    component:'dashboard/lineChart.vue',
                    title:'查看'
                }
                sessionStorage.setItem('/seeLineChart'+this.$route.query.id,JSON.stringify(obj))
                if(this.chartId === '' || this.chartId !== this.$route.query.id){
                    this.chartId = this.$route.query.id;
                }
                //时间范围监听事件
                bus.$on(this.busName,(arr)=>{
                    this.dateArr = arr;
                })
                //数据源监听事件
                bus.$on(this.busIndexName,(arr)=>{
                    //还原配置
                    this.initialize();
                    //设置数据源
                    //设置数据源
                    this.chartsConfig.suffixIndexName = arr[2];
                    this.chartsConfig.preIndexName = arr[1];
                    this.chartsConfig.templateName = arr[0];
                    for(let i in arr){
                        if(arr[i] == ''){
                            return
                        }
                    }
                    //获取时间字段
                    this.xAggregationChange();
                })
            }else{
                //时间范围监听事件
                bus.$on(this.busName,(arr)=>{
                    this.dateArr = arr;
                })
                //数据源监听事件
                bus.$on(this.busIndexName,(arr)=>{
                    //还原配置
                    this.initialize();
                    //设置数据源
                    this.chartsConfig.suffixIndexName = arr[2];
                    this.chartsConfig.preIndexName = arr[1];
                    this.chartsConfig.templateName = arr[0];
                    for(let i in arr){
                        if(arr[i] == ''){
                            return
                        }
                    }
                    //获取时间字段
                    this.xAggregationChange();
                })
            }

        },
        computed:{
            'isCanCreate'(){
                let yState = false;
                let xState = false;
                //判断y轴
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
                    aggregationParam:'',
                    aggregationParamArr:[],//字段集合
                    yAxisName:'',
                    legendName:'',
                    areaShow:'',
                    colorType:'solidColor',
                    color1:'#5bc0de',
                    color2:['rgba(15,219,243,1)','rgba(68,47,148,0.5)']
                })
            },
            /*时间间隔选择 失去焦点事件*/
            timeIntervalBlur(event){
                //this.chartsConfig.xAxisArr[0].timeInterval = event.target.value;
            },
            /*y轴聚合类型改变*/
            yAggregationChange($event,index){
                if (this.chartsConfig.preIndexName === '' || this.chartsConfig.suffixIndexName === '' || this.chartsConfig.templateName === ''){
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
                            pre_index_name:this.chartsConfig.preIndexName,
                            suffix_index_name:this.chartsConfig.suffixIndexName,
                            template_name:this.chartsConfig.templateName
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
                if (this.chartsConfig.preIndexName === '' || this.chartsConfig.suffixIndexName === '' || this.chartsConfig.templateName === ''){
                    layer.msg('请选择数据源',{icon:'5'});
                    return;
                }
                //清空聚合字段
                this.chartsConfig.xAxisArr[0].aggregationParam = '';
                this.chartsConfig.xAxisArr[0].aggregationParamArr = []
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/BI/getFieldByXAxisAggregation.do',this.$qs.stringify({
                        agg:'Date',
                        pre_index_name:this.chartsConfig.preIndexName,
                        suffix_index_name:this.chartsConfig.suffixIndexName,
                        template_name:this.chartsConfig.templateName
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            res.data.forEach(item=>{
                                let obj = {
                                    value:item.fieldName,
                                    label:item.fieldName
                                }
                                this.chartsConfig.xAxisArr[0].aggregationParamArr.push(obj)
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
                    startTime:this.dateArr[0],//起始时间
                    endTime:this.dateArr[1],//结束时间
                    x_agg:this.chartsConfig.xAxisArr[0].aggregationType,//x轴参数类型
                    x_field:this.chartsConfig.xAxisArr[0].aggregationParam,//x轴参数
                    y_field:this.chartsConfig.yAxisArr[0].aggregationParam,//y轴参数
                    y_agg:this.chartsConfig.yAxisArr[0].aggregationType,//y轴参数类型
                    pre_index_name:this.chartsConfig.preIndexName,
                    suffix_index_name:this.chartsConfig.suffixIndexName,
                    template_name:this.chartsConfig.templateName,
                    size:'',
                    sort:'',
                    intervalType:this.chartsConfig.xAxisArr[0].timeType,//时间间隔类型
                    intervalValue:this.chartsConfig.xAxisArr[0].timeInterval,//时间间隔数值
                };
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/BI/getDataByChartParams.do',this.$qs.stringify(param))
                        .then(res=>{
                            //存储查询条件
                            this.chartParams.searchParam = JSON.stringify(param);
                            //判断x轴label间隔类型
                            if(this.chartsConfig.xNormal.axisLabel.interval !== 'auto'){
                                this.chartsConfig.xNormal.axisLabel.interval = Number(this.chartsConfig.xNormal.axisLabel.interval)
                            }
                            layer.closeAll('loading');
                            let obj = res.data;
                            if (obj.success === 'true'){
                                let xDataArr = obj.data[0].name;
                                let yDataArr = obj.data[0].data;
                                if(xDataArr.length > 0){
                                    this.emptyTipState = false;
                                    this.createChart(xDataArr,yDataArr)
                                    this.sourceData = obj.data
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
            createChart(xDataArr,yDataArr){
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
                        trigger: 'axis',
                        axisPointer: {
                            type: 'cross'
                        }
                    },
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
                            magicType: {
                                show:this.chartsConfig.toolbox.feature.includes('magicType'),
                                type: ["line", "bar"]
                            },
                            restore: {
                                show:this.chartsConfig.toolbox.feature.includes('restore'),
                            },
                            dataZoom:{
                                show:this.chartsConfig.toolbox.feature.includes('dataZoom'),
                            },
                            saveAsImage: {
                                show:this.chartsConfig.toolbox.feature.includes('saveAsImage'),
                            }
                        },
                        iconStyle:{
                            borderColor: "#56769a"
                        }
                    },
                    grid:this.chartsConfig.grid,
                    xAxis: {
                        name:this.chartsConfig.xAxisArr[0].xAxisName,
                        boundaryGap: false,
                        nameTextStyle:{
                            color:this.chartsConfig.xNormal.nameTextStyle.color
                        },
                        axisLine:this.chartsConfig.xNormal.axisLine,
                        axisLabel:this.chartsConfig.xNormal.axisLabel,
                        data:xDataArr
                        //data: ["00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"]
                    },
                    yAxis: {
                        name:this.chartsConfig.yAxisArr[0].yAxisName,
                        nameTextStyle:{
                            color:this.chartsConfig.yNormal.nameTextStyle.color,
                        },
                        splitLine:this.chartsConfig.yNormal.splitLine,
                        axisLine:this.chartsConfig.yNormal.axisLine,
                        axisLabel: {
                            color:this.chartsConfig.yNormal.axisLabel.color,
                            fontSize:this.chartsConfig.yNormal.axisLabel.fontSize,
                            formatter: function (value, index) {
                                if (value >= 10000 && value < 10000000) {
                                    value = value / 10000 + "万";
                                } else if (value >= 10000000) {
                                    value = value / 10000000 + "千万";
                                }
                                return value;
                            }
                        }
                    },
                    series: []
                }
                for(let i in this.chartsConfig.yAxisArr){
                    if(this.chartsConfig.yAxisArr[i].colorType === 'solidColor'){
                        let obj = {
                            name: this.chartsConfig.yAxisArr[i].legendName,
                            type: 'line',
                            smooth:true,
                            itemStyle:{
                                color:this.chartsConfig.yAxisArr[i].color1
                            },
                            lineStyle:{
                                color:this.chartsConfig.yAxisArr[i].color1
                            },
                            areaStyle: {
                                color:this.chartsConfig.yAxisArr[i].color1,
                                opacity:this.chartsConfig.yAxisArr[i].areaShow
                            },
                            //data: [1121,831,432,500,444,600,700,1121,831,432,500,444,600,700,1121,831,432,500,444,600,700,1000,1111,600]
                            data:yDataArr
                        }
                        this.opt.series.push(obj)
                    }else if(this.chartsConfig.yAxisArr[i].colorType === 'tbColor'){
                        let obj = {
                            name: this.chartsConfig.yAxisArr[i].legendName,
                            type: 'line',
                            smooth:true,
                            itemStyle:{
                                color: this.chartsConfig.yAxisArr[i].color2[0]
                            },
                            lineStyle:{
                                color: this.chartsConfig.yAxisArr[i].color2[0]
                            },
                            areaStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                    offset: 0,
                                    color: this.chartsConfig.yAxisArr[i].color2[0]
                                },{
                                    offset: 1,
                                    color: this.chartsConfig.yAxisArr[i].color2[1]
                                }]),
                                opacity:this.chartsConfig.yAxisArr[i].areaShow
                            },
                            //data: [1121,831,432,500,444,600,700]
                            data:yDataArr
                        }
                        this.opt.series.push(obj)
                    }else if(this.chartsConfig.yAxisArr[i].colorType === 'lrColor'){
                        let obj = {
                            name: this.chartsConfig.yAxisArr[i].legendName,
                            type: 'line',
                            smooth:true,
                            itemStyle:{
                                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{
                                    offset: 0,
                                    color: this.chartsConfig.yAxisArr[i].color2[0]
                                },{
                                    offset: 1,
                                    color: this.chartsConfig.yAxisArr[i].color2[1]
                                }])
                            },
                            lineStyle:{
                                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{
                                    offset: 0,
                                    color: this.chartsConfig.yAxisArr[i].color2[0]
                                },{
                                    offset: 1,
                                    color: this.chartsConfig.yAxisArr[i].color2[1]
                                }])
                            },
                            areaStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{
                                    offset: 0,
                                    color: this.chartsConfig.yAxisArr[i].color2[0]
                                },{
                                    offset: 1,
                                    color: this.chartsConfig.yAxisArr[i].color2[1]
                                }]),
                                opacity:this.chartsConfig.yAxisArr[i].areaShow
                            },
                            //data: [1121,831,432,500,444,600,700]
                            data:yDataArr
                        }
                        this.opt.series.push(obj)
                    }
                }
                echarts.init(document.getElementById('charts-wapper')).dispose();//销毁前一个实例
                let myChart =  echarts.init(document.getElementById('charts-wapper'));
              //  console.log(JSON.stringify(this.opt))
                myChart.setOption(this.opt);
                window.addEventListener("resize",()=>{
                    myChart.resize();
                });
            },
            /*保存图表*/
            saveChart(){
                //清空已经获取的参数
                // this.chartsConfig.xAxisArr
                for (let i in this.chartsConfig.xAxisArr){
                    this.chartsConfig.xAxisArr[i].aggregationParamArr = [];
                    this.opt.xAxis.data = [];
                }
                for (let j in this.chartsConfig.yAxisArr){
                    this.chartsConfig.yAxisArr[j].aggregationParamArr = [];
                    this.opt.series[j].data = [];
                }
                let optStr = {
                    config:this.chartsConfig,
                    opt:this.opt
                }
                let params = {
                    title:this.chartParams.chartName,
                    description:this.chartParams.chartDes,
                    type:'line',
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
            /*删除y轴*/
            removeTab(i,event){
                console.log(i)
                this.chartsConfig.yAxisArr.splice(i,1);
                event.stopPropagation();
            }
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
                                    this.indexVal = [obj.data.template_name,obj.data.pre_index_name,obj.data.suffix_index_name]
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
                                   /* let xD = JSON.parse(obj.data.data)[0].name;
                                    let yD = JSON.parse(obj.data.data)[0].data;
                                    this.createChart(xD,yD);
                                    this.emptyTipState = false;
                                    this.sourceData = JSON.parse(obj.data.data);*/
                                    //前台自己获取数据
                                    this.getData()
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
            //时间范围改变
            'dateArr'(nv,ov){
                //判断是否具备生成图表的条件
                if(this.isCanCreate !== 'disabled'){
                    //获取数据
                    this.getData()
                }
            }
        },
     /*   beforeRouteEnter(to, from, next) {
            next (vm => {



            })

        },*/
        components:{
            dateLayout,
            chooseIndex,
            jsonView
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
