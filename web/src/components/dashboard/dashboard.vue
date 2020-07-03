<template>
    <div class="content-bg" v-loading="allLoading"  element-loading-background="rgba(48, 62, 78, 0.5)">
        <div class="top-title" style="position: relative;">
            <div class="tit-zz" v-if="this.htmlTitle.substr(0,2) == '查看'"></div>
            <el-button  type="primary" size="mini" plain @click="drawerState = true" >添加自定义图表</el-button>
            <el-button  type="primary" size="mini" plain @click="sysDrawerState = true" >添加预设图表</el-button>
            <el-button  type="primary" size="mini" plain @click="wordsState = true; wordType = 'add'" >添加文字</el-button>
            <el-button  type="success" size="mini" plain @click="dialogFormVisible = true" style="float: right;margin-right: 10px;margin-top: 10px;">保存</el-button>
            <el-button  type="primary" size="mini" plain @click="refreshDashboard" style="float: right;margin-right: 5px;margin-top: 10px;position: relative;z-index: 101;">刷新</el-button>
            <div class="date-wapper"><!--<date-layout  :busName="busName" :refresh="refresh"></date-layout>-->
                <dateLayout :busName="busName" :defaultVal = "defaultVal" :refresh="refresh"></dateLayout>
            </div>
            <div class="tit-eq" v-if="this.equipmentId !== ''">{{this.dashboardTit}}</div>
        </div>
        <!--dashboard-->
        <div>
            <grid-layout
                :layout.sync="layout"
                :col-num="48"
                :row-height="30"
                :is-draggable="true"
                :is-resizable="true"
                :vertical-compact="true"
                :margin="[10, 10]"
                :use-css-transforms="true"
                @layout-ready="layoutReadyEvent"
            >
                <grid-item v-for="(item,i) in layout"
                           class="item-wapper"
                           :x="item.x"
                           :y="item.y"
                           :w="item.w"
                           :h="item.h"
                           :i="item.i"
                           :key="item.i"
                           drag-allow-from=".vue-draggable-handle"
                           @resized="resizedEvent"
                           @moved="movedEvent">
                    <div class="vue-draggable-handle text-box" style="height: 100%;" v-if="item.chartType == 'text'">
                        <div class="tit-zz" v-if="htmlTitle.substr(0,2) == '查看'"></div>
                        <div class="text-wapper" v-html="item.text"></div>
                        <div class="text-i">
                            <i class="el-icon-edit" title="修改" @click="editTextBtn(i)"></i>
                            <i class="deleteE el-icon-close" title="删除" @click="deleteE(i)"></i>
                        </div>
                    </div>
                    <div style="height: 100%;" v-else-if="item.chartType == 'systemChart'">
                        <div class="item-tit vue-draggable-handle">
                            <div class="tit-zz" v-if="htmlTitle.substr(0,2) == '查看'"></div>
                            <span>{{item.tit}}</span>
                            <i class="deleteE el-icon-close" title="删除" @click="deleteE(i)"></i>
                            <i class="fullscreenE el-icon-full-screen"  title="全屏" @click="fullscreenE(item)"></i>
<!--                            <i class="el-icon-edit" title="修改"  v-if="item.eId !== ''" @click="editChartBtn(i)"></i>-->
                        </div>
                        <div class="item-con" :style="{zIndex:htmlTitle.substr(0,2) == '查看' ? '100' : ''}">
                            <component :is="allComps[item.eId]" :params="sysChartParams" :baseConProp="{title:''}" :setIntervalObj="intervalObj"> </component>
                        </div>
                    </div>
                    <div style="height: 100%;" v-else >
                        <div class="item-tit vue-draggable-handle">
                            <div class="tit-zz" v-if="htmlTitle.substr(0,2) == '查看'"></div>
                            <span>{{item.tit}}</span>
                            <i class="deleteE el-icon-close" title="删除" @click="deleteE(i)"></i>
                            <i class="fullscreenE el-icon-full-screen"  title="全屏" @click="fullscreenE(item)"></i>
                            <i class="el-icon-edit" title="修改"  v-if="item.eId !== ''" @click="editChartBtn(i)"></i>
                        </div>
                        <div class="no-chart" v-if="item.eId == ''">图表已被删除</div>
                        <div v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)" class="item-con" :ref="`eb${item.i}`" :id="`${item.i}`" :style="{zIndex:htmlTitle.substr(0,2) == '查看' ? '100' : ''}"></div>
                    </div>
                </grid-item>
            </grid-layout>
        </div>
        <!-- 自定义图表列表  -->
        <el-drawer
            title="我是标题"
            :visible.sync="drawerState"
            size="350"
            :with-header="false">
           <div class="drawer-wapper">
               <div class="drawer-tit">
                   <i class="close-drawer el-icon-close" @click="drawerState = false"></i>
                   <span>图表列表</span>
                   <i class="refresh-list el-icon-refresh-right" @click="getChartsList"></i>
               </div>
               <el-checkbox-group v-model="checkedList" class="drawer-list" v-loading="chartsListLoading"  element-loading-background="rgba(48, 62, 78, 0.5)">
                   <ul>
                       <li v-for="(item,i) in chartsList" :key="i">
                           <el-checkbox :label="item.id" style="width: 260px;overflow:hidden;">{{item.title}}</el-checkbox>
                           <span>{{item.type === 'line' ? '折线图' : item.type === 'bar' ? '柱状图' : item.type === 'pie' ? '饼图' : ''}}</span>
                       </li>
                   </ul>
               </el-checkbox-group>

               <el-button class="drawer-tools" type="primary" :disabled="this.checkedList.length === 0" @click="addE">确定</el-button>
           </div>
        </el-drawer>
        <!--系统预设图表列表-->
        <el-drawer
            title="我是标题"
            :visible.sync="sysDrawerState"
            size="450"
            :with-header="false">
            <div class="drawer-wapper">
                <div class="drawer-tit">
                    <i class="close-drawer el-icon-close" @click="sysDrawerState = false"></i>
                    <span>系统预设图表</span>
                    <i class="refresh-list el-icon-refresh-right" ></i>
                </div>
                <div class="drawer-list">
                    <el-input
                        style="margin: 5px;"
                        placeholder="输入关键字进行过滤"
                        size="mini"
                        v-model="filterText">
                    </el-input>

                    <el-tree
                        class="filter-tree"
                        :data="sysChartdata"
                        show-checkbox
                        node-key="id"
                        :props="defaultProps"
                        default-expand-all
                        :filter-node-method="filterNode"
                        ref="tree">
                    </el-tree>
                </div>
                <!--:disabled="this.sysCheckedList.length === 0" -->
                <el-button class="drawer-tools" type="primary"  @click="addSE">确定</el-button>
            </div>
        </el-drawer>
        <!-- 全屏 -->
        <div class="zz-e" v-if="fullscreenState">
            <div class="fullscreen-wapper">
                <div class="fullscreenTit">{{fullscreenChartData.title}} <i class="close-fullscreen el-icon-close" @click="fullscreenState = false"></i></div>
                <div id="fullscreenChart" >
                    <component v-if="fullscreenChartData.type === 'systemChart'" :is="allComps[fullscreenChartData.eid]" :params="sysChartParams" :baseConProp="{title:''}" :setIntervalObj="{state:fullscreenChartData.intervalState}"> </component>
                </div>
            </div>
        </div>
        <!--富文本文字-->
        <el-dialog :title="wordType ==='add' ? '添加文本' : '修改文本'" :visible.sync="wordsState" :close-on-click-modal="false" width="840px">
            <quill-editor ref="myTextEditor" v-model="content" :options="editorOption">
                <!-- 自定义toolar -->
                <div id="toolbar" slot="toolbar">
                    <!-- Add a bold button -->
                    <button class="ql-bold" title="加粗">Bold</button>
                    <button class="ql-italic" title="斜体">Italic</button>
                    <button class="ql-underline" title="下划线">underline</button>
                    <button class="ql-strike" title="删除线">strike</button>
                    <button class="ql-blockquote" title="引用"></button>
                    <button class="ql-code-block" title="代码"></button>
                    <button class="ql-header" value="1" title="标题1"></button>
                    <button class="ql-header" value="2" title="标题2"></button>
                    <!--Add list -->
                    <button class="ql-list" value="ordered" title="有序列表"></button>
                    <button class="ql-list" value="bullet" title="无序列表"></button>
                    <!-- Add font size dropdown -->
                    <select class="ql-header" title="段落格式">
                        <option selected>段落</option>
                        <option value="1">标题1</option>
                        <option value="2">标题2</option>
                        <option value="3">标题3</option>
                        <option value="4">标题4</option>
                        <option value="5">标题5</option>
                        <option value="6">标题6</option>
                    </select>
                    <select class="ql-size" title="字体大小">
                        <option value="10px">10px</option>
                        <option value="12px">12px</option>
                        <option value="14px">14px</option>
                        <option value="16px" selected>16px</option>
                        <option value="18px">18px</option>
                        <option value="20px">20px</option>
                        <option value="22px">22px</option>
                        <option value="24px">24px</option>
                        <option value="26px">26px</option>
                        <option value="28px">28px</option>
                        <option value="30px">30px</option>
                    </select>
                    <select class="ql-font" title="字体">
                        <option value="SimSun">宋体</option>
                        <option value="SimHei">黑体</option>
                        <option value="Microsoft-YaHei">微软雅黑</option>
                        <option value="KaiTi">楷体</option>
                        <option value="FangSong">仿宋</option>
                        <option value="Arial">Arial</option>
                    </select>
                    <!-- Add subscript and superscript buttons -->
                    <select class="ql-color" value="color" title="字体颜色"></select>
                    <select class="ql-background" value="background" title="背景颜色"></select>
                    <select class="ql-align" value="align" title="对齐"></select>
                    <button class="ql-clean" title="还原"></button>
                    <!-- You can also add your own -->
                </div>
            </quill-editor>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="wordsOkBtn">确 定</el-button>
                <el-button @click="wordsState = false">取 消</el-button>
            </div>
        </el-dialog>
        <!--保存-->
        <el-dialog title="保存" :visible.sync="dialogFormVisible" width="400px">
            <el-form>
                <el-form-item label="名称">
                    <el-input v-model="dashboardParams.name"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="dashboardParams.des"></el-input>
                </el-form-item>
                <el-form-item v-if="dashboardId !== ''">
                    <el-switch
                        v-model="dashboardParams.otherSave"
                        active-text="另存为新的仪表盘">
                    </el-switch>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveDashBoard" :disabled="dashboardParams.name === '' ? 'disabled' : false">确 定</el-button>
            </div>
        </el-dialog>
    </div>

</template>

<script>
    import 'quill/dist/quill.core.css';
    import 'quill/dist/quill.snow.css';
    import 'quill/dist/quill.bubble.css';
    import { Quill,quillEditor } from 'vue-quill-editor';
    import { addQuillTitle } from '../../../static/js/quill-title.js';
    import VueGridLayout from 'vue-grid-layout';
    import dateLayout from '../common/dateLayout'
    import vEcharts from '../common/echarts'
    import bus from '../common/bus';
    import {dateFormat,jumpHtml,setChartParam} from "../../../static/js/common";
    import allComps from '../charts/index'
    const echarts = require('echarts');
    //引入font.css
    import '../../assets/font.css'
    // 自定义字体大小
    let Size = Quill.import('attributors/style/size')
    Size.whitelist = ['10px', '12px', '14px', '16px', '18px', '20px','22px','24px','26px','28px','30px']
    Quill.register(Size, true)
    // 自定义字体类型
    var fonts = ['SimSun', 'SimHei', 'Microsoft-YaHei', 'KaiTi', 'FangSong', 'Arial', 'Times-New-Roman', 'sans-serif', '宋体', '黑体']
    var Font = Quill.import('formats/font')
    Font.whitelist = fonts
    Quill.register(Font, true);
    export default {
        name: "dashboard",
        data() {
            return {
                interval:'',
                loading:true,
                color1 :['#00EABD','#20C1F3','#FC686F','#F9D124','#DE1AFB','#C0D7FC','#A9F4B7','#FF9E96','#75B568','#323A81'],
                //时间控件参数
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
                //整个页面遮罩
                allLoading:false,
                //自定义列表遮罩
                chartsListLoading:false,
                //所有系统预设图表模板
                allComps:allComps,
                //系统预设图表参数
                sysChartParams:{
                    intervalValue:'',
                    intervalType:'',
                    starttime:'',
                    endtime:'',
                    last:'15-min'
                },
                //轮询参数
                intervalObj:{
                    state:false,
                    interval:'5000'
                },
                //资产id
                equipmentId:'',
                //标题
                dashboardTit:'',
                htmlTitle:'新建',
                refresh:0,
                dateArr:{
                    starttime:'',//起始时间
                    endtime:'',//结束时间
                    last:'15-min',
                },
                dashboardId:'',
                //保存图表的弹窗状态
                dialogFormVisible:false,
                //保存图表表单参数
                dashboardParams:{
                    //图表名称
                    name:'',
                    //图表描述
                    des:'',
                    //查询条件
                    searchParam:'',
                    //另存
                    otherSave:false
                },
                //时间监听事件名称
                busName:'dashboardBusName',
                //文本框状态
                wordsState:false,
                //文本框参数
                editorOption: {
                    placeholder: '添加文字',
                    modules: {
                        toolbar:{
                            container: '#toolbar'
                        }
                    }
                },
                color:['#1E73F0','#00D1CE','#33C3F7','#3952D3','#185BFF','#2455AD','#74EE9A','#253479'],
                //文本框类型 添加/修改
                wordType:'add',
                //文本内容
                content:'',
                //当前修改文本的id
                currentEditIndex:'',
                //dashboard数据
                layout: [
                    //{"x":0,"y":0,"w":12,"h":8,"i":"0","tit":"日志级别统计"},
                    //{"x":12,"y":0,"w":12,"h":8,"i":"1","tit":"每小时日志数"},
                    // {"x":0,"y":0,"w":12,"h":2,"i":"cc","tit":"日志级别统计","type":"text"},
                ],
                //图表集合  存储echarts名称 用于自适应大小
                echartsArr:{},
                //当前图表计数
                chartsCount:0,
                //系统预设右边栏
                sysDrawerState:false,
                //右边栏状态
                drawerState:false,
                //选中的自定义图例集合
                checkedList:[],
                //选中的系统预设图例集合
                sysCheckedList:[],
                //图例列表
                chartsList:[],
                sysChartdata: [
                    {
                    id: '1',
                    label: '日志',
                    children: [{
                        id: 'logLevel_bar-日志级别数量统计',
                        label: '日志级别数量统计 - 柱状图',
                    },{
                        id: 'logLevel_pie-日志级别数量统计',
                        label: '日志级别数量统计 - 饼图',
                    },{
                        id: 'hourlyLogCount_line-今日日志数量统计',
                        label: '今日日志数量统计 - 折线图',
                    },
                        {
                            id: 'eqEventType_bar-事件级别数量统计(单一资产)',
                            label: '事件级别数量统计(单一资产) - 柱状图',
                        },
                        {
                            id: 'eqEventType_pie-事件级别数量统计(单一资产)',
                            label: '事件级别数量统计(单一资产) - 饼图',
                        },
                        {
                            id: 'eqHourlyLogCount_line-每小时日志数量统计(单一资产)',
                            label: '每小时日志数量统计(单一资产) - 折线图',
                        },
                        {
                            id: 'eqLogLevel_bar-日志级别数量统计(单一资产)',
                            label: '日志级别数量统计(单一资产) - 柱状图',
                        },
                        {
                            id: 'eqSyslogHourlyEventCount_moreline-每小时事件数量统计(syslog单一资产)',
                            label: '每小时事件数量统计(syslog单一资产) - 折线图',
                        },
                        {
                            id: 'eqWinlogHourlyEventCount_moreline-每小时事件数量统计(winlog单一资产)',
                            label: '每小时事件数量统计(winlog单一资产) - 折线图',
                        }]
                },{
                    id: '2',
                    label: '流量',
                    children: [{
                        id: 'eqIpPacket_bar-资产（IP）数据包个数统计',
                        label: '资产（IP）数据包个数统计 - 柱状图',
                    },{
                        id: 'eqIpPacket_pie-资产（IP）数据包个数统计',
                        label: '资产（IP）数据包个数统计 - 饼图',
                    },{
                        id: 'eqServerPacket_bar-资产（服务）数据包个数统计',
                        label: '资产（服务）数据包个数统计 - 柱状图',
                    },{
                        id: 'eqServerPacket_pie-资产（服务）数据包个数统计',
                        label: '资产（服务）数据包个数统计 - 饼图',
                    },{
                        id: 'dstIp_bar-目的Ip地址流量统计',
                        label: '目的Ip地址流量统计 - 柱状图',
                    },{
                        id: 'dstIp_pie-目的Ip地址流量统计',
                        label: '目的Ip地址流量统计 - 饼图',
                    },{
                        id: 'srcIp_bar-源Ip地址流量统计',
                        label: '源Ip地址流量统计 - 柱状图',
                    },{
                        id: 'srcIp_pie-源Ip地址流量统计',
                        label: '源Ip地址流量统计 - 饼图',
                    },{
                        id: 'mulAndBro_bar-广播包/组播包统计',
                        label: '广播包/组播包统计 - 柱状图',
                    },{
                        id: 'mulAndBro_timeline-广播包/组播包统计（实时）-实时',
                        label: '广播包/组播包统计（实时） - 折线图',
                    },{
                        id: 'packetType_bar-数据包类型统计',
                        label: '数据包类型统计 - 柱状图',
                    },{
                        id: 'packetType_timeline-数据包类型统计（实时）-实时',
                        label: '数据包类型统计（实时） - 折线图',
                    },{
                        id: 'performanceAnalysis_bar-应用平均响应时间统计',
                        label: '应用平均响应时间统计 - 柱状图',
                    },{
                        id: 'dstPortAll_bar-目的端口总流量统计',
                        label: '目的端口总流量统计 - 柱状图',
                    },{
                        id: 'dstPortAll_pie-目的端口总流量统计',
                        label: '目的端口总流量统计 - 饼图',
                    },{
                        id: 'tcpDstPort_bar-TCP目的端口总流量统计',
                        label: 'TCP目的端口总流量统计 - 柱状图',
                    },{
                        id: 'tcpDstPort_pie-TCP目的端口总流量统计',
                        label: 'TCP目的端口总流量统计 - 饼图',
                    },{
                        id: 'udpDstPort_bar-UDP目的端口总流量统计',
                        label: 'UDP目的端口总流量统计 - 柱状图',
                    },{
                        id: 'udpDstPort_pie-UDP目的端口总流量统计',
                        label: 'UDP目的端口总流量统计 - 饼图',
                    },{
                        id: 'application_bar-应用层协议长度统计',
                        label: '应用层协议长度统计 - 柱状图',
                    },{
                        id: 'application_pie-应用层协议长度统计',
                        label: '应用层协议长度统计 - 饼图',
                    },{
                        id: 'multiple_bar-协议长度统计',
                        label: '协议长度统计 - 柱状图',
                    },{
                        id: 'multiple_pie-协议长度统计',
                        label: '协议长度统计 - 饼图',
                    },{
                        id: 'transport_bar-传输层协议长度统计',
                        label: '传输层协议长度统计 - 柱状图',
                    },{
                        id: 'transport_pie-传输层协议长度统计',
                        label: '传输层协议长度统计 - 饼图',
                    },{
                        id: 'allflow_timeline-全局实时流量（实时）-实时',
                        label: '全局实时流量（实时） - 折线图',
                    },{
                        id: 'allPacketCount_timeline-全局数据包个数（实时）-实时',
                        label: '全局数据包个数 - 折线图',
                    },{
                        id: 'allUsedPer_gauge-全局实时流量（实时）-实时',
                        label: '全局实时流量（实时） - 仪表',
                    },{
                        id: 'allUsedPer_timeline-全局利用率（实时）-实时',
                        label: '全局利用率（实时） - 折线图',
                    },{
                        id: 'agentBrowser_bar-用户业务系统浏览器统计',
                        label: '用户业务系统浏览器统计 - 柱状图',
                    },{
                        id: 'agentBrowser_pie-用户业务系统浏览器统计',
                        label: '用户业务系统浏览器统计 - 饼图',
                    },{
                        id: 'agentSystem_bar-用户业务系统统计',
                        label: '用户业务系统统计 - 柱状图',
                    },{
                        id: 'agentSystem_pie-用户业务系统统计',
                        label: '用户业务系统统计 - 饼图',
                    }]
                }],
                filterText: '',
                defaultProps: {
                    children: 'children',
                    label: 'label'
                },
                addXLength:0,
                fullscreenState:false,
                fullscreenChartData:{
                    title:'',
                    opt:{}
                },
            }
        },
        created(){
            //判断是否有参数  有参数说明是修改功能页面
            if(JSON.stringify(this.$route.query) !== "{}" && this.$route.query.type === "edit"){
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                this.$options.name = 'resiveDashboard'+ this.$route.query.id;
                //修改data参数
                this.htmlTitle = `编辑 ${this.$route.query.name}`;
                this.busName = 'resiveDashboard'+this.$route.query.id;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'resiveDashboard'+this.$route.query.id,
                    component:'dashboard/dashboard.vue',
                    title:'编辑'
                }
                sessionStorage.setItem('/resiveDashboard'+this.$route.query.id,JSON.stringify(obj))
                if(this.dashboardId === '' || this.dashboardId !== this.$route.query.id){
                    this.dashboardId = this.$route.query.id;
                }
            }else if(this.$route.query.type === "see"){
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                this.$options.name = 'seeDashboard'+ this.$route.query.id;
                //修改data参数
                this.htmlTitle = `查看 ${this.$route.query.name}`;
                this.busName = 'seeDashboard'+this.$route.query.id;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'seeDashboard'+this.$route.query.id,
                    component:'dashboard/dashboard.vue',
                    title:'查看'
                }
                sessionStorage.setItem('/seeDashboard'+this.$route.query.id,JSON.stringify(obj))
                if(this.dashboardId === '' || this.dashboardId !== this.$route.query.id){
                    this.dashboardId = this.$route.query.id;
                }
            }else if(JSON.stringify(this.$route.query) !== "{}" && this.$route.query.type === "EQedit"){
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                this.$options.name = 'equipmentDashboard'+ this.$route.query.eid;
                //修改data参数
                this.htmlTitle = `编辑 ${this.$route.query.name}`;
                this.busName = 'equipmentDashboard'+this.$route.query.eid;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'equipmentDashboard'+this.$route.query.eid,
                    component:'dashboard/dashboard.vue',
                    title:'编辑'
                }
                sessionStorage.setItem('/equipmentDashboard'+this.$route.query.eid,JSON.stringify(obj))
                if(this.dashboardId === '' || this.dashboardId !== this.$route.query.id){
                    this.dashboardId = this.$route.query.id;
                    this.equipmentId = this.$route.query.eid;
                    this.dashboardTit = this.$route.query.name;
                }
            }

        },
        mounted(){
            //获取dashboard数据
            //this.getDashboardData();
            this.getChartsList()
            //监听时间改变事件
            bus.$on(this.busName,(obj)=>{
                this.dateArr = new Object();
                this.dateArr = setChartParam(obj)[0];
                this.intervalObj = setChartParam(obj)[1];
                //判断是否是单一资产的仪表盘（参数不同）
                if (this.equipmentId !== ''){//是
                    let arr = setChartParam(obj);
                    this.sysChartParams = arr[0];
                    this.sysChartParams.hsData = JSON.stringify({'fields.equipmentid':this.equipmentId})

                }else{//否
                    let arr = setChartParam(obj);
                    this.sysChartParams = arr[0];
                }
            })
        },
        beforeDestroy(){
            bus.$off(this.busName)
        },
        methods:{
            /*过滤系统预设报表*/
            filterNode(value, data) {
                if (!value) return true;
                return data.label.indexOf(value) !== -1;
            },
            /*获取图表列表*/
            getChartsList(){
                this.$nextTick(()=>{
                    this.chartsListLoading = true;
                    this.$axios.post(this.$baseUrl+'/BI/getVisualizations.do','')
                        .then(res=>{
                            this.chartsListLoading = false;
                            let obj =res.data;
                            if (obj.success == 'true'){
                                this.chartsList = obj.data;
                            } else {
                                layer.msg(res.data.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.chartsListLoading = false;
                            layer.msg('获取数据失败',{icon:5})
                        })
                })
            },
            /*文字弹框确定按钮*/
            wordsOkBtn(){
                //判断当前文本弹窗类型  add-添加   修改
                if (this.wordType === 'add'){
                    this.chartsCount = Number(this.chartsCount)+1;
                    let i = dateFormat('yyyy-mm-dd HH:MM:SS',new Date()).replace(/\s*/g,"");
                    let obj = {"x":0,"y":0,"w":24,"h":2,"tit":'',"i":i+this.chartsCount,chartType:'text',"opt":{},text:this.content};
                    this.layout.push(obj);
                } else{
                    this.layout[this.currentEditIndex].text = this.content;
                }
                this.wordsState = false;
            },
            /*修改文字*/
            editTextBtn(i){
                this.wordType = 'edit';
                this.wordsState = true;
                this.currentEditIndex = i;
                this.content = this.layout[i].text;
            },
            /*修改图表*/
            editChartBtn(i){
                switch (this.layout[i].chartType) {
                    case 'bar':
                        jumpHtml('barChart'+this.layout[i].eId,'dashboard/barChart.vue',{name:this.layout[i].tit,id:this.layout[i].eId,type:'edit'},' 修改');
                        break;
                    case 'pie':
                        jumpHtml('pieChart'+this.layout[i].eId,'dashboard/pieChart.vue',{name:this.layout[i].tit,id:this.layout[i].eId,type:'edit'},' 修改');
                        break;
                    case 'line':
                        jumpHtml('lineChart'+this.layout[i].eId,'dashboard/lineChart.vue',{name:this.layout[i].tit,id:this.layout[i].eId,type:'edit'},' 修改');
                        break;
                }
            },
            /*添加自建图例*/
            addE(){
                this.addXLength = 0;
                //this.layout.push({"x":0,"y":0,"w":12,"h":8,"i":"1","tit":"每小时日志数"})
                //this.getEchartsConstruction();
               /* this.chartsCount = Number(this.chartsCount)+1;
                this.layout.push({"x":0,"y":0,"w":12,"h":8,"i":this.chartsCount,"opt":{}})*/
               //添加选中的图例
               for (let i=0;i<this.checkedList.length;i++){
                   //找出对应的图例
                   let eId = this.checkedList[i];
                   for (let j in this.chartsList){
                       if (this.chartsList[j].id === eId){
                           //创建基本结构数据
                           this.createConstruction(this.chartsList[j])
                               .then((res)=>{
                                   //获取echarts结构数据
                                   return this.getEchartsConstruction(res)
                               })
                              .then((res)=>{
                                   //获取图例数据
                                   return this.getEchartsData(res)
                               })
                               .then((res)=>{
                                   //加载图例
                                   return this.creatEcharts(res)
                               })
                           break;
                       }
                   }
               }
               //清空选中
                this.checkedList = [];
                this.drawerState = false;
            },
            /*添加系统图例*/
            addSE(){
                // console.log();
                let selectedArr = this.$refs.tree.getCheckedKeys()
                //剔除标题id
                let overArr = [];
                for(let j in selectedArr){
                    if(selectedArr[j].length > 2){
                        overArr.push(selectedArr[j])
                    }
                }
                //添加到仪表盘中
                for (let i=0;i<overArr.length;i++){
                    let arr = overArr[i].split('-');
                    let intervals = false;
                    if (arr[2]){
                        intervals = true;
                    }
                    let obj = {
                        id:arr[0],
                        type:'systemChart',
                        tit:arr[1],
                        intervalState:intervals
                    }
                    this.createConstruction(obj)
                }
                //清空选中
                this.$refs.tree.setCheckedKeys([])
                this.sysDrawerState = false;
            },
            /*保存dashboart*/
            saveDashBoard(){
                for (let i in this.layout){
                    this.layout[i].opt = {};
                }
                let params = {
                    title:this.dashboardParams.name,
                    description:this.dashboardParams.des,
                    option:JSON.stringify(this.layout),
                    isSaveAs:true
                }
                //判断是否是修改图表
                if(this.dashboardId !== ''){
                    //判断是否是另存图表
                    if(!this.dashboardParams.otherSave){//不是另存（修改原先的）
                        params.id = this.dashboardId;
                        params.isSaveAs = false;
                    }
                }
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/BI/saveDashboard.do',this.$qs.stringify(params))
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
            /*刷新dashboard*/
            refreshDashboard(){
                this.refresh++;
            },
            /*删除图例*/
            deleteE(i){
                //删除
                this.layout.splice(i,1)
            },
            /*全屏显示*/
            fullscreenE(obj){
                //出现遮罩
                this.fullscreenState = true;
                //添加图表
                this.fullscreenChartData.title = JSON.parse(JSON.stringify(obj.tit));
                this.fullscreenChartData.opt = JSON.parse(JSON.stringify(obj.opt));
                this.fullscreenChartData.i = 'fullscreenChart';
                this.fullscreenChartData.type = JSON.parse(JSON.stringify(obj.chartType));
                if(obj.intervalState){
                    this.fullscreenChartData.intervalState = JSON.parse(JSON.stringify(obj.intervalState));
                }
                if(this.fullscreenChartData.type === 'systemChart'){
                    this.fullscreenChartData.eid = JSON.parse(JSON.stringify(obj.eId));
                }else{
                    setTimeout(()=>{
                        this.creatEcharts(this.fullscreenChartData)
                    },500)
                }
                //console.log(this.fullscreenChartData);

                //
            },
            /*获取dashboard数据*/
            getDashboardData(){
                this.$nextTick(()=>{
                   this.allLoading = true;
                    this.$axios.post(this.$baseUrl+'/BI/getDashboardById.do',this.$qs.stringify({
                        id:this.dashboardId
                    }))
                        .then(res=>{
                            this.allLoading = false;
                            let obj = res.data;
                            if (obj.success == 'true'){
                                this.dashboardParams.name = obj.data.title;
                                this.dashboardParams.des = obj.data.description;
                                let option = JSON.parse(obj.data.option);
                                this.layout = option;
                                this.$nextTick(()=>{
                                    //获取echart结构数据
                                    for(let i in this.layout){
                                        this.chartsCount = i;
                                        //判断是否是文字块、系统预设报表还是自定义报表  自定义报表需要获取图表结构
                                        if(this.layout[i].chartType !== 'text' && this.layout[i].chartType !== 'systemChart'){
                                            this.getEchartsConstruction(this.layout[i])
                                                .then((res)=>{
                                                    //获取图例数据
                                                    return this.getEchartsData(res)
                                                })
                                                .then((res)=>{
                                                    //加载图例
                                                    return this.creatEcharts(res)
                                                })
                                        }

                                    }
                                })


                            }
                        })
                        .catch(err=>{
                            this.allLoading = false;
                        })
                })
            },
            /*获取echarts结构数据*/
            getEchartsConstruction(obj){
                return new Promise((resolve,rej)=>{
                    this.$nextTick(()=>{
                        $(document.getElementById(obj.i)).next().css("display","block")
                        this.$axios.post(this.$baseUrl+'/BI/getVisualizationById.do',this.$qs.stringify({
                            id:obj.eId
                        }))
                            .then(res=>{
                               // layer.closeAll('loading');
                                let data = res.data;
                                if (data.success == 'true'){
                                    //判断图表是否适应被删除
                                    if(data.data.id === ''){
                                        obj.eId = '';
                                        resolve(obj);
                                    }
                                    let option = JSON.parse(data.data.option).opt;
                                    let param = JSON.parse(data.data.params)
                                    //console.log(option)
                                    //填充ecahrt数据
                                    option.title.show = false;
                                    obj.areaShow = JSON.parse(data.data.option).config.graph.areaShow
                                    obj.tit = data.data.title;
                                    obj.opt = option;
                                    let resObj = {
                                        obj:obj,
                                        param:param
                                    }
                                    resolve(resObj);
                                }else{
                                    layer.msg('获取数据失败',{icon:5})
                                }

                                /*//调用获取echart数据
                                let echartsData = this.getEchartsData(res.data.bar);
                                console.log(echartsData);*/
                            })
                            .catch(err=>{
                                $(document.getElementById(obj.i)).next().css("display","none")
                            })
                    })
                })
            },
            /*获取echarts数据*/
            getEchartsData(resObj){
                //判断请求的方法
                let url = '/BI/getDataByChartParams.do';
                if(resObj.obj.chartType === 'pie'){
                    url = '/BI/getDataByChartParams_pie.do'
                }

                let obj = resObj.obj;
                let param = resObj.param;
                param.starttime = this.dateArr.starttime;
                param.endtime = this.dateArr.endtime;
                param.last = this.dateArr.last;
                //判断是否是单个资产的统计
                if (this.equipmentId !== ''){
                    let eqObj = {
                        'fields.equipmentid':this.equipmentId
                    }
                    param.queryParam = JSON.stringify(eqObj);
                }
                return new Promise((resolve,reject)=>{
                    this.$nextTick(()=>{
                        this.loading = false;
                        this.$axios.post(this.$baseUrl+url,this.$qs.stringify(param))
                            .then(res=>{
                                obj.loading = false;
                                obj.opt.dataset = res.data.data;
                                obj.opt.series=[];
                                //判断图表类型
                                if(obj.chartType === 'bar'){
                                    let xL = res.data.data[0].dimensions.length - 1;//维度
                                    let colorIndex = 0;//颜色索引
                                    //处理y轴数字刻度单位
                                    obj.opt.yAxis.axisLabel.formatter= function (value, index) {
                                        if (value >= 10000 && value < 10000000) {
                                            value = value / 10000 + "万";
                                        } else if (value >= 10000000) {
                                            value = value / 10000000 + "千万";
                                        }
                                        return value;
                                    }
                                    for(let i=0;i<xL;i++){
                                        if(colorIndex === this.color1.length){
                                            colorIndex = 0;
                                        }
                                        let dObj = {
                                            name: '',
                                            type: 'bar',
                                            itemStyle: {
                                                color: this.color1[colorIndex]
                                            },
                                        }
                                        obj.opt.series.push(dObj);
                                        colorIndex++
                                    }
                                }
                                else if(obj.chartType === 'line'){
                                    let xL = res.data.data[0].dimensions.length - 1;//维度
                                    let colorIndex = 0;//颜色索引
                                    //处理y轴数字刻度单位
                                    obj.opt.yAxis.axisLabel.formatter= function (value, index) {
                                        if (value >= 10000 && value < 10000000) {
                                            value = value / 10000 + "万";
                                        } else if (value >= 10000000) {
                                            value = value / 10000000 + "千万";
                                        }
                                        return value;
                                    }
                                    for(let i=0;i<xL;i++){
                                        if(colorIndex === this.color1.length){
                                            colorIndex = 0;
                                        }
                                        let dObj = {
                                            type: 'line',
                                            smooth: true, //平滑性.
                                            showSymbol: false,
                                            hoverAnimation: false,
                                            areaStyle: {
                                                normal: {
                                                    //颜色渐变函数 前四个参数分别表示四个位置依次为左、下、右、上
                                                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                                        offset: 0,
                                                        color:  this.color1[colorIndex]
                                                    }, {
                                                        offset: 1,
                                                        color: 'rgba(116,235,213,0.1)'
                                                    }]),
                                                    opacity:obj.areaShow

                                                },

                                            },
                                            itemStyle: {
                                                normal: {
                                                    color: this.color1[colorIndex]
                                                }
                                            },
                                        }
                                        obj.opt.series.push(dObj);
                                        colorIndex++;
                                    }
                                }
                                else if(obj.chartType === 'pie'){
                                   /* obj.opt.series.push({
                                        name: '',
                                        type: 'pie',
                                        radius : '55%',
                                        center: ['50%', '60%'],
                                        itemStyle: {
                                            emphasis: {
                                                shadowBlur: 10,
                                                shadowOffsetX: 0,
                                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                                            }
                                        }
                                    })*/
                                   console.log(res.data)
                                    //饼图圆环个数
                                    let pieCount = res.data.data.length;
                                    //圆环间隔
                                    let pieSpace = 5;
                                    //最大范围
                                    let raduisMax = 70;
                                    //每个环平均的宽度
                                    let raduisVal = (raduisMax-pieSpace*(pieCount-1))/(pieCount + 1);
                                    for(let i = 0;i<pieCount;i++){
                                        if(i === 0){
                                            let objPie = {
                                                type: 'pie',
                                                radius:[0,`${raduisVal*(i+2)}%`],
                                                itemStyle: {
                                                    emphasis: {
                                                        shadowBlur: 10,
                                                        shadowOffsetX: 0,
                                                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                                                    }
                                                },
                                                label:{
                                                    normal:{
                                                        show:false
                                                    }
                                                },
                                                data:res.data.data[i]
                                            }
                                            obj.opt.series.push(objPie)
                                        }else{
                                            let objPie = {
                                                type: 'pie',
                                                radius:[`${raduisVal*(i+1)+pieSpace*i}%`,`${raduisVal*(i+2)+pieSpace*i}%`],
                                                data:res.data.data[i],
                                                itemStyle: {
                                                    emphasis: {
                                                        shadowBlur: 10,
                                                        shadowOffsetX: 0,
                                                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                                                    }
                                                },
                                                label:{
                                                    normal:{
                                                        show:false
                                                    }
                                                },

                                            }
                                            obj.opt.series.push(objPie)
                                        }
                                    }
                                }
                                console.log(obj.opt.series)
                                resolve(obj);
                            })
                            .catch(err=>{
                                $(document.getElementById(obj.i)).next().css("display","none")
                            })
                    })
                })
            },
            /*创建总体结构*/
            createConstruction(eObj){
                return new Promise((resolve, reject) => {
                    let x = 0;let w = 24;
                    if(this.addXLength === 0){
                        x = 0;
                    }else if(48 - this.addXLength >= w){
                        x = w
                    }
                    this.addXLength += w;
                    this.chartsCount = Number(this.chartsCount)+1;
                    let i = dateFormat('yyyy-mm-dd HH:MM:SS',new Date()).replace(/\s*/g,"");
                    let obj = {eId:eObj.id,"x":x,"y":0,"w":24,"h":8,intervalState:eObj.intervalState,"tit":eObj.tit ? eObj.tit : '',"i":i+this.chartsCount,chartType:eObj.type,"opt":{}};
                    this.layout.push(obj);
                    resolve(obj);
                })
            },
            /*创建图表*/
            creatEcharts(obj){
               // console.log(i)
                /*setTimeout(()=>{
                    this.echartsArr[i] = echarts.init(document.getElementById(dom))
                    this.echartsArr[i].setOption(opt);
                },1000)*/
                return new Promise((resolve, reject) => {
                    if(obj.opt.toolbox !== undefined){
                        obj.opt.toolbox.feature.dataView.optionToContent = function (opt) {
                            let axisData = opt.xAxis[0].data; //坐标数据
                            let series = opt.series; //折线图数据
                            let tdHeads = '<td  style="padding: 0 10px">#</td>'; //表头
                            let tdBodys = ''; //数据
                            series.forEach(function (item) {
                                //组装表头
                                tdHeads += `<td style="padding:10px">${item.name}</td>`;
                            });
                            let table = `<table border="1" style="width: 80%;margin-left:20px;border-collapse:collapse;border-color: #fff;font-size:14px;text-align:center"><tbody><tr>${tdHeads} </tr>`;
                            for (let i = 0, l = axisData.length; i < l; i++) {
                                for (let j = 0; j < series.length; j++) {
                                    //组装表数据
                                    tdBodys += `<td>${ series[j].data[i]}</td>`;
                                }
                                table += `<tr><td style="padding:10px">${axisData[i]}</td>${tdBodys}</tr>`;
                                tdBodys = '';
                            }
                            table += '</tbody></table>';
                            return table;
                        }
                    }
                    this.echartsArr[obj.i] = echarts.init(document.getElementById(obj.i))
                    this.echartsArr[obj.i].setOption(obj.opt);
                    window.addEventListener("resize",()=>{
                        this.echartsArr[obj.i].resize();
                    });
                    $(document.getElementById(obj.i)).next().css("display","none")
                })

            },
            /*循环创建完dashboard结构后-事件*/
            layoutReadyEvent(newLayout){
                //循环创建echarts图表
                for(let i in newLayout){
                    this.chartsCount = i;
                    this.creatEcharts(newLayout[i])
                }
                //console.log(this.chartsCount);
            },
            /*移动时的事件*/
            /*moveEvent(i, newX, newY){
                // console.log("MOVE i=" + i + ", X=" + newX + ", Y=" + newY);
            },*/
            /*移动后事件*/
            movedEvent(i, newX, newY){
                // console.log("MOVED i=" + i + ", X=" + newX + ", Y=" + newY);
                // console.log(this.layout)
            },
            /*改变宽高后*/
            resizedEvent(i, newH, newW, newHPx, newWPx){
                //this.echartBox.resize();
                //console.log("RESIZED i=" + i + ", H=" + newH + ", W=" + newW + ", H(px)=" + newHPx + ", W(px)=" + newWPx);
                if(this.echartsArr[i] != undefined) {
                    //自适应echart图表
                    this.echartsArr[i].resize();
                }

            },
        },
        watch:{
            /*系统报表过滤*/
            'filterText'(val) {
                this.$refs.tree.filter(val);
            },
            'sysDrawerState'(){
                //清空选中
                this.$nextTick(()=>{
                    this.$refs.tree.setCheckedKeys([])
                })

            },
            /*依据id查询*/
            'dashboardId'(newV) {
                if (newV !== '') {
                    this.getDashboardData()
                }
            },
            //时间范围改变
            'dateArr'(nv,ov){
                this.loading = true;
                //获取数据
                for(let i in this.layout){
                    this.chartsCount += 1;
                    //判断是否是文字块  不是则是图表类型 需要获取图表结构
                    if(this.layout[i].chartType !== 'text' && this.layout[i].chartType !== 'systemChart'){
                        this.getEchartsConstruction(this.layout[i])
                            .then((res)=>{
                                //获取图例数据
                                return this.getEchartsData(res)
                            })
                            .then((res)=>{
                                //加载图例
                                return this.creatEcharts(res)
                            })
                    }else if(this.layout[i].chartType == 'systemChart'){

                    }

                }
            },
            intervalObj:{
                handler(newV,oldV) {
                    //判断是否启用轮询获取数据
                    if (this.intervalObj.state){
                        //判断参数是否合法(是否有刷新间隔时间)
                        if(this.intervalObj.interval){//合法
                            clearInterval(this.interval)
                            this.interval = setInterval(()=>{
                                for(let i in this.layout){
                                    this.loading = false;
                                    this.chartsCount += 1;
                                    //判断是否是文字块  不是则是图表类型 需要获取图表结构
                                    if(this.layout[i].chartType !== 'text' && this.layout[i].chartType !== 'systemChart'){
                                        this.getEchartsConstruction(this.layout[i])
                                            .then((res)=>{
                                                //获取图例数据
                                                return this.getEchartsData(res)
                                            })
                                            .then((res)=>{
                                                //加载图例
                                                return this.creatEcharts(res)
                                            })
                                    }else if(this.layout[i].chartType == 'systemChart'){

                                    }

                                }
                            },this.intervalObj.interval)
                        }else{//不合法
                            this.errState = true;
                        }

                    }else {
                        clearInterval(this.interval)
                    }
                },
                immediate: true,
                deep: true
            },
            'wordsState'(){
                if(this.wordsState){
                    this.$nextTick(()=>{
                        //加载提示
                        addQuillTitle();
                    })

                }else{
                    this.content = '';
                }
            }
        },
        /*beforeRouteEnter(to, from, next) {
            next (vm => {


            })

        },*/
        components:{
            //logLevel_bar: resolve => {require(['../charts/logLevel_bar'], resolve)},//懒加载
            vEcharts,
            dateLayout,
            quillEditor,
            GridLayout: VueGridLayout.GridLayout,
            GridItem: VueGridLayout.GridItem,

        }
    }
</script>

<style scoped>
    .top-zz{
        text-align: center;
        width: 100%;
        height: 100%;
        position: absolute;
        /*background: #;*/
        z-index: 100;
        cursor: no-drop;
        text-shadow: none;
        color: #455b75;
    }
    .tit-zz{
        width: 100%;
        height: 100%;
        position: absolute;
        z-index: 1;
        cursor: no-drop;
        text-shadow: none;
    }
    .tit-eq{
        position: absolute;
        text-shadow: none;
        top: 0;
        left: 41%;
        text-align: center;
    }
    .date-wapper{
        float: right;
        margin-top: 10px;
        margin-right: 10px;
        position: relative;
        z-index: 101;
    }
    .item-wapper{
        background: #303e4e;
    }
    .item-wapper i{
        display: none;
    }
    .item-wapper:hover i{
        display: inline-block!important;
    }
    .text-wapper{
        display: flex;
        align-items: center;
        font-weight: 600;
        padding-left: 20px;
        height: 100%;
    }
    .text-box .text-i{
        position: absolute;
        right: 10px;
        top: 15px;
        color: #10d9f2;
    }
    .item-tit{
        height: 49px;
        border-bottom: 1px solid #56769a;
        line-height: 50px;
        padding-left: 20px;
        color: #10d9f2;
        font-weight: 600;
        position: relative;
    }
    .item-tit .tit-input{
        width: 70%;
        display: inline-block;
    }
    .item-tit i{
        float: right;
        margin-right: 10px;
        margin-top: 15px;
    }
    .fullscreenE{
        position: relative;
        z-index: 2;
    }
    .item-tit i:hover ,.text-box i:hover{
        cursor: pointer;
        color: #fff;
    }
    .item-con{
        width: 100%;
        position: relative;
        height: calc(100% - 50px);
        overflow: hidden;
        /*z-index: 101;*/
        /*width: 613px;
        height: 260px;*/
    }
    .no-chart{
        width: 100%;
        position: absolute;
        top: 50px;
        height: calc(100% - 50px);
        overflow: hidden;
        display: flex;
        justify-content: center;
        align-items: center;
        color: #56769a;
    }
    .charts-list{
        box-sizing: border-box;
        width: 300px;
        height: 100%;
        position: absolute;
        right: 10px;
        background: #303e4e;
        border: 1px solid #56769a;
        transition: all 0.3s linear;
        top: 0;
    }
    /deep/ .el-drawer__body{
        height: 100%;
        overflow: auto;
    }
    .drawer-wapper{
        /*padding: 10px 20px;*/
        height: 100%;
        width: 350px;
    }
    .drawer-tit{
        display: flex;
        height: 50px;
        justify-content: space-between;
        align-items: center;
        background: #476b94;
        padding: 0 20px;
    }
    .drawer-tit i:hover{
        cursor: pointer;
        color: #10d9f2;
    }
    .drawer-list{
        height: calc(100% - 100px);
        padding: 0 10px;
        overflow: auto;
    }
    .drawer-list li{
        height: 40px;
        line-height: 40px;
        color: #fff;
        border-bottom: 1px solid #4b6179;
        display: flex;
        justify-content: space-between;
        font-size: 13px;
        padding-right:5px;
    }
    .drawer-list /deep/ .el-checkbox__label{
        color: #fff;
        margin-left: 30px;
        font-size: 13px;
    }
    .drawer-tools{
        width: 100%;
        display: flex;
        height: 50px;
        background: #476b94;
        justify-content: center;
        align-items: center;
        border: 0;
    }
    .zz-e{
        position: fixed;
        width: 100vw;
        height: 100vh;
        top: 0;
        left: 0;
        z-index: 101;
        background:rgba(0,0,0,0.7);
        display: flex;
        justify-content: center;
        align-items: center;
    }
    .fullscreen-wapper{
        width: 80%;
        height: 80%;
        background: #303e4e;
    }
    .fullscreenTit{
        height: 49px;
        padding-left: 20px;
        color: #10d9f2;
        font-weight: 600;
        line-height: 49px;
        border-bottom: 1px solid #4b6179;
    }
    #fullscreenChart{
        width: 100%;
        height: calc(100% - 50px);
    }
    .close-fullscreen{
        float: right;
        margin-right: 10px;
        margin-top: 15px;
    }
    .ql-editor{
        color: #fff;
    }
    /deep/ #ql-picker-options-5{
        background: #4b6179;
    }
</style>
