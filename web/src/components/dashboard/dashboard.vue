<template>
    <div class="content-bg">
        <div class="top-title">
            <el-button  type="primary" plain @click="drawerState = true" >添加图表</el-button>
            <el-button  type="primary" plain @click="wordsState = true; wordType = 'add'" >添加文字</el-button>
            <el-button  type="success" plain @click="saveE">保存</el-button>
            <div class="date-wapper"><date-layout  :busName="busName"></date-layout></div>
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
                    <div style="height: 100%;" v-if="item.chartType !== 'text'">
                        <div class="item-tit vue-draggable-handle">
                            <span>{{item.tit}}</span>
                            <!--                        <el-input class="tit-input" placeholder="请输入内容"></el-input>-->
                            <i class="deleteE el-icon-close" title="删除" @click="deleteE(i)"></i>
                            <i class="fullscreenE el-icon-full-screen" title="全屏" @click="fullscreenE(item)"></i>
                        </div>
                        <div class="item-con" :ref="`eb${item.i}`" :id="`${item.i}`">
                        </div>
                    </div>
                    <div v-else class="vue-draggable-handle text-box" style="height: 100%;">
                        <div class="text-wapper" v-html="item.text"></div>
                        <div class="text-i">
                            <i class="el-icon-edit" title="修改" @click="editTextBtn(i)"></i>
                            <i class="deleteE el-icon-close" title="删除" @click="deleteE(i)"></i>
                        </div>
                    </div>
                </grid-item>
            </grid-layout>
        </div>
        <!-- 图表列表  -->
        <el-drawer
            title="我是标题"
            :visible.sync="drawerState"
            size="350"
            :with-header="false">
           <div class="drawer-wapper">
               <div class="drawer-tit">
                   <i class="close-drawer el-icon-close" @click="drawerState = false"></i>
                   <span>图表列表</span>
                   <i class="refresh-list el-icon-refresh-right"></i>
               </div>
               <el-checkbox-group v-model="checkedList" class="drawer-list">
                   <ul>
                       <li v-for="(item,i) in chartsList" :key="i">
                           <el-checkbox :label="item.id">{{item.title}}</el-checkbox>
                       </li>
                   </ul>
               </el-checkbox-group>

               <el-button class="drawer-tools" type="primary" :disabled="this.checkedList.length === 0" @click="addE">确定</el-button>
           </div>
        </el-drawer>
        <!-- 全屏 -->
        <div class="zz-e" v-if="fullscreenState">
            <div class="fullscreen-wapper">
                <div class="fullscreenTit">{{fullscreenChartData.title}} <i class="close-fullscreen el-icon-close" @click="fullscreenState = false"></i></div>
                <div id="fullscreenChart" ></div>
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
    </div>
    
</template>

<script>
    import 'quill/dist/quill.core.css';
    import 'quill/dist/quill.snow.css';
    import 'quill/dist/quill.bubble.css';
    import { Quill,quillEditor } from 'vue-quill-editor';
    import { addQuillTitle } from '../../../static/js/quill-title.js';
    import VueGridLayout from 'vue-grid-layout';
    import dateLayout from '../dashboard/dateLayout'
    import vEcharts from '../common/echarts'
    import bus from '../common/bus';
    import {dateFormat} from "../../../static/js/common";
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
                //右边栏状态
                drawerState:false,
                //选中的图例集合
                checkedList:[],
                //图例列表
                chartsList:[
                    {id:0,title:'日志级别数量统计',type:'bar'},
                    {id:1,title:'事件类型数量统计',type:'bar2'},
                    {id:2,title:'每小时日志数',type:'line'},
                    {id:3,title:'日志级别百分比',type:'pie'}
                ],
                addXLength:0,
                fullscreenState:false,
                fullscreenChartData:{
                    title:'',
                    opt:{}
                },
            }
        },
        created(){
           // console.log(dateFormat('yyyy-mm-dd HH:MM:SS',new Date()).replace(/\s*/g,""));
        },
        mounted(){
            //获取dashboard数据
            this.getDashboardData();
            //监听时间改变事件
            bus.$on(this.busName,(arr)=>{
                console.log(arr);
            })
        },
        watch:{
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
        methods:{
            /*文字弹框确定按钮*/
            wordsOkBtn(){
                console.log(JSON.stringify(this.content))
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
            /*添加图例*/
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
            /*保存dashboart*/
            saveE(){
                // console.log(JSON.stringify(this.layout))
                localStorage.setItem('dashboard',JSON.stringify(this.layout));
                layer.msg('保存成功',{icon:1})
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
                this.fullscreenChartData.i = 'fullscreenChart'
                //console.log(this.fullscreenChartData);
                setTimeout(()=>{
                    this.creatEcharts(this.fullscreenChartData)
                },500)
                //
            },
            /*获取dashboard数据*/
            getDashboardData(){
                let arr = JSON.parse(localStorage.getItem('dashboard'));
                console.log(arr)
                if(arr !== null){
                    for(let i in arr){
                        this.layout.push(arr[i]);
                        if(arr[i].chartType !== 'text'){
                            this.$nextTick(()=>{
                                setTimeout(()=>{
                                    this.creatEcharts(arr[i])
                                },1000)
                            })

                        }
                    }
                }else{
                    this.$nextTick(()=>{
                    layer.load(1);
                    //this.$axios.post(this.$baseUrl+'',this.$qs.stringify())
                    this.$axios.get('static/filejson/dashboard.json','')
                        .then(res=>{
                            layer.closeAll('loading');
                             // this.layout = res.data;
                             for (let i in res.data){
                                 this.layout.push(res.data[i]);
                                 if(res.data[i].chartType !== 'text'){
                                     /*this.getEchartsConstruction(res.data[i])
                                         .then((res)=>{
                                             return this.getEchartsData(res)
                                         })
                                         .then((res)=>{
                                             //加载图例
                                             return this.creatEcharts(res)
                                         })*/
                                     this.$nextTick(()=>{
                                         this.creatEcharts(res.data[i])
                                     })

                                 }


                             }
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
                }




                /*1.获取结构数据（位置、宽高）
                * 2.根据获取的id查询图表结构
                * 3.根据获取的id查询图表数据
                * */
                /*this.layout = [
                    {"x":0,"y":0,"w":12,"h":8,"i":"aa","tit":"日志级别统计","opt":{"title":{"text":"","x":"center","textStyle":{"color":"#5bc0de"}},"color":{"type":"linear","x":0,"y":0,"x2":0,"y2":1,"colorStops":[{"offset":0,"color":"rgba(15,219,243,1)"},{"offset":1,"color":"rgba(68,47,148,0.5)"}],"global":false},"tooltip":{},"xAxis":{"name":"级别","nameTextStyle":{"color":"#5bc0de"},"axisLine":{"lineStyle":{"color":"#5bc0de"}},"axisLabel":{"borderColor":"#5bc0de","interval":0,"rotate":"20"},"data":["info","error","message"]},"grid":{"bottom":"20%"},"yAxis":{"name":"数量/条","nameTextStyle":{"color":"#5bc0de"},"splitLine":{"lineStyle":{"color":"#303e4e"}},"axisLine":{"lineStyle":{"color":"#5bc0de"}},"axisLabel":{"margin":2}},"series":[{"name":"级别","type":"bar","data":[1121,2231,432]}]}},
                    {"x":12,"y":0,"w":12,"h":8,"i":"bb","tit":"日志级别统计","opt":{"title":{"text":"","x":"center","textStyle":{"color":"#5bc0de"}},"color":{"type":"linear","x":0,"y":0,"x2":0,"y2":1,"colorStops":[{"offset":0,"color":"rgba(15,219,243,1)"},{"offset":1,"color":"rgba(68,47,148,0.5)"}],"global":false},"tooltip":{},"xAxis":{"name":"级别","nameTextStyle":{"color":"#5bc0de"},"axisLine":{"lineStyle":{"color":"#5bc0de"}},"axisLabel":{"borderColor":"#5bc0de","interval":0,"rotate":"20"},"data":["info","error","message"]},"grid":{"bottom":"20%"},"yAxis":{"name":"数量/条","nameTextStyle":{"color":"#5bc0de"},"splitLine":{"lineStyle":{"color":"#303e4e"}},"axisLine":{"lineStyle":{"color":"#5bc0de"}},"axisLabel":{"margin":2}},"series":[{"name":"级别","type":"bar","data":[1121,2231,432]}]}}
                ]*/
            },
            /*获取echarts结构数据*/
            getEchartsConstruction(obj){
                return new Promise((resolve,rej)=>{
                    this.$nextTick(()=>{
                        layer.load(1);
                        this.$axios.get('static/filejson/baseConstruction.json','')
                            .then(res=>{
                                layer.closeAll('loading');
                                obj.opt = res.data[obj.chartType];
                                // obj.chartType = res.data.type;
                                resolve(obj);
                                /*//调用获取echart数据
                                let echartsData = this.getEchartsData(res.data.bar);
                                console.log(echartsData);*/
                            })
                            .catch(err=>{
                                layer.closeAll('loading');
                            })
                    })
                })
            },
            /*获取echarts数据*/
            getEchartsData(obj){
                return new Promise((resolve,reject)=>{
                    this.$nextTick(()=>{
                        layer.load(1);
                        this.$axios.get('static/filejson/data.json','')
                            .then(res=>{
                                layer.closeAll('loading');
                                //合并结构与数据
                                /*let eData = res.data;
                                if(obj.chartType === 'bar' || obj.chartType === 'line'){//柱状图、折线图
                                    obj.opt.xAxis.data = eData.xAxisArr;
                                    for(let i=0;i < eData.yAxisArr.length;i++){
                                        obj.opt.series[i].data = eData.yAxisArr[i];
                                    }

                                }else if(obj.chartType === 'pie'){//饼图
                                    let pieDate = obj.opt.series[0].data;
                                    for(let i=0;i < pieDate.length;i++){
                                        pieDate[i].value = eData.xAxisArr[i]
                                        pieDate[i].name = eData.yAxisArr[0][i]
                                    }
                                }*/
                                resolve(obj)
                            })
                            .catch(err=>{
                                layer.closeAll('loading');
                            })
                    })
                })
            },
            /*创建总体结构*/
            createConstruction(eObj){
                return new Promise((resolve, reject) => {
                    let x = 0;let w = 12;
                    if(this.addXLength === 0){
                        x = 0;
                    }else if(24 - this.addXLength >= w){
                        x = w
                    }
                    this.addXLength += w;
                    this.chartsCount = Number(this.chartsCount)+1;
                    let i = dateFormat('yyyy-mm-dd HH:MM:SS',new Date()).replace(/\s*/g,"");
                    let obj = {"x":x,"y":0,"w":12,"h":8,"tit":eObj.title,"i":i+this.chartsCount,chartType:eObj.type,"opt":{}};
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

            }
        },
        components:{
            vEcharts,
            dateLayout,
            quillEditor,
            GridLayout: VueGridLayout.GridLayout,
            GridItem: VueGridLayout.GridItem,

        }
    }
</script>

<style scoped>
    .date-wapper{
        float: right;
        margin-top: 10px;
        margin-right: 10px;
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
    .item-tit i:hover ,.text-box i:hover{
        cursor: pointer;
        color: #fff;
    }
    .item-con{
        width: 100%;
        position: relative;
        height: calc(100% - 50px);
        overflow: hidden;
        /*width: 613px;
        height: 260px;*/
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
        /*overflow: auto;*/
    }
    .drawer-list li{
        height: 40px;
        line-height: 40px;
        padding-left: 20%;
        color: #fff;
        border-bottom: 1px solid #4b6179;
    }
    .drawer-list /deep/ .el-checkbox__label{
        color: #fff;
        margin-left: 30px;
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
        z-index: 99;
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
