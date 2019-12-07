<template>
    <div class="content">
        <div id="cy"></div>
    </div>
    
</template>


<script>
    import {jumpHtml} from "../../../static/js/common";

    export default {
        name: "baseGraph",
        props:{
            nodeData:{
                type:Array
            },
            linkData:{
                type:Array
            },
            nodeClick:{
                type:Function
            },
            linkClick:{
                type:Function
            }
        },
        data() {
            return {}
        },
        mounted(){

        },
        watch:{
            nodeData:{
                handler(val, oldVal){
                    this.getNetflowTopologyData()
                },
                deep:true
            },
        },
        methods:{
            /*获得数据*/
            getNetflowTopologyData(){
                var nodesArr = [];//节点数组
                var edgesArr = [];//线数组
                var nodesCountArr = [];//节点count数组
                var linksCountArr = [];//节点count数组
                //{data:{id:'王大拿',target:true,type:'People'}},
                //{"node": 1,"name": "123.232.103.226","count": "188394"}
                //node节点
                for(let i in this.nodeData){
                    let obj = {};
                    obj.data = {};
                    obj.data.id = this.nodeData[i].name;
                    obj.data.target = true;
                    obj.data.count = this.nodeData[i].count;
                    //obj.data.type = this.nodeData[i].node;
                    nodesCountArr.push(this.nodeData[i].count)
                    nodesArr.push(obj);
                }
                //定义最大节点数
                let maxNodesCount = Math.max.apply(null,nodesCountArr);
                // {data:{source:'王大拿',target:'AAA公司',label:'董事'}},
                //{"node": 1,"count": 80761,"source": "123.232.103.226","target": "level2\n192.168.2.151"}
                //连接线
                for(let i in this.linkData){
                    let obj = {};
                    obj.data = {};
                    obj.data.source = this.linkData[i].source;
                    obj.data.target = this.linkData[i].target;
                    obj.data.count = this.linkData[i].count;
                    obj.data.label = this.linkData[i].count;
                    linksCountArr.push(this.linkData[i].count);
                    edgesArr.push(obj);
                }
                let maxLinksCount = Math.max.apply(null,linksCountArr);
                //console.log(nodesArr)
                //渲染
                var cy = cytoscape({
                    container: document.getElementById('cy'),
                    autounselectify: true,//默认情况下节点是否应该是未分类的
                    maxZoom:4,//最大缩放
                    minZoom:0.7,//最小缩放
                    wheelSensitivity:.1,//滑动滚轮一次缩放大小
                    style: cytoscape.stylesheet()
                        .selector('node')
                        .css({
                            "content": function (ele) { //文本内容
                                return ele.data('label') || ele.data('id')+'\n 活跃度:'+ele.data('count')
                            },
                            "text-valign": 'center', //文本对齐  center-居中
                            "width": function (a) { //宽度
                                //return "Company" == a.data('type') ? 60 : 145
                                var widthVal = 140;
                                return Math.pow(a.data('count')/maxNodesCount,1/3) * widthVal;
                            },
                            "height": function (a) { //高度
                                var heightVal = 140;
                                //return "Company" == a.data('type') ? 60 : 145
                                return  Math.pow(a.data('count')/maxNodesCount,1/3) * heightVal;;
                            },
                            "background-color": function (a) { //背景颜色
                                //return '#47D2AC';
                                return a.data('count') == maxNodesCount ? '#D76662' : '#59C9A5';
                            },
                            "color": '#fff', //文本颜色
                            "border-color": function (a) { //边框颜色
                                /*return !a.data('target') ? '#47D2AC' : "Company" == a.data('type') ?
                                    '#2196F4' : '#EAA829'*/
                                return '#3A4D5F';
                            },
                            "border-width":2, //边框宽度
                            "text-wrap": "wrap", //文本换行
                            "font-size": 10, //字体大小
                            "font-weight":600,
                            "font-family": "microsoft yahei",//字体
                            "overlay-color": "#fff",
                            "overlay-opacity": 0,
                            'text-outline-width': 1,
                            'text-outline-color': '#316383',//颜色设置
                            "background-opacity": 1, //透明度
                            "shape": "ellipse",//节点形状    ellipse-圆形  rectangle-矩形
                            "z-index-compare": "manual",
                            "z-index": 20,
                            "padding": function (a) {
//		                            return "Company" == a.data("type") ? 3 : 0
                                return 20;
                            }
                        })
                        .selector('edge')
                        .css({
                            "width": function(a){
                                var widthVal = 15;
                                return Math.pow(a.data('count')/maxLinksCount,1/3) * widthVal;
                            },
                            // 添加箭头
                            "line-style": function (a) {
                                return "solid"
                            },
                            "curve-style": "bezier",
                            "control-point-step-size": 20,
                            "target-arrow-shape": "triangle",
                            "target-arrow-color": function (a) {
                                // return a.data("color")
                                return '#65F9FD'
                            },
                            "arrow-scale": .8,
                            "line-color": function (a) {//线颜色
                                // return a.data("color")
                                // return '#A8D0DB'
                                return '#65F9FD'
                            },
                            "label": function (a) {
                                return a.data("label")
                            },
                            "text-opacity": 1,
                            "text-background-color": "#e4956d",

                            "text-background-opacity": 0,
                            "text-background-padding": 2,
                            "font-size":0,
                            "color":"#fff",
                            "font-weight":600,
                            "overlay-color": "#fff",
                            "overlay-opacity": 0,
                            "font-family": "microsoft yahei"
                        })
                        .selector(':selected')
                        .css({
                            "border-width": 3,
                            "border-color": '#333',
                            "background-color": 'black',
                            "line-color": 'black',
                            "target-arrow-color": 'black',
                            "source-arrow-color": 'black'
                        })
                        .selector('.nodeHover')
                        .css({
                            "shape": "ellipse",
                            "background-opacity": 1
                        })
                        .selector('.nodeActive')
                        .css({
                            "border-color": '#4EA2F0',
                            "border-width": 10,
                            "border-opacity": .5
                        })
                        .selector('.edgeShow')
                        .css({
                            /*"color": "#999",
                            "text-opacity": 1,
                            "font-weight": 400,
                            "label": function (a) {
                                return a.data("label")
                            },
                            "font-size": 10,
                            "arrow-scale": .8,
                            "width": 1.5,
                            "source-text-margin-y": 20,
                            "target-text-margin-y": 20,*/
                        })
                        .selector('.edgeActive')
                        .css({
                            "arrow-scale": .8,
                            //"width": 1.5,
                            "color": "#fff",
                            "text-opacity": 1,
                            "font-size": 12,
                            "text-background-color": "#e4956d",
                            "text-background-opacity": .8,
                            "text-background-padding": 2,
                            "source-text-margin-y": 20,
                            "target-text-margin-y": 20,
                            "z-index-compare": "manual",
                            "z-index": 1,
                            "line-color": function (a) { //直线颜色
                                return "#4EA2F0"
                            },
                            "target-arrow-color": function (a) { //箭头颜色
                                return "#4EA2F0"
                            },
                            label: function (a) {
                                return a.data("label")
                            }
                        })
                        .selector('.dull')
                        .css({
                            "z-index": 1,
                            "opacity": .2
                        }),

                    elements: {
                        nodes:nodesArr,
                        edges:edgesArr
                    },
                    layout: {
                        name: 'cose',//用哪种方式排列，可选：breadthfirst(广度优先)、cose(缝制，乱交)、preset(预设)、circle(圆形)、grid(矩形)
                        animate:true,//出来动画
                        idealEdgeLength: 60,
                        nodeOverlap: 20,
                        refresh: 20,
                        fit: true,
                        padding: 30,
                        randomize: false,
                        componentSpacing: 20,
                        nodeRepulsion: 400,
                        edgeElasticity: 10,
                        nestingFactor: 5,
                        gravity: 80,
                        numIter: 1000,
                        initialTemp: 200,
                        coolingFactor: 0.95,
                        minTemp: 1.0
                    }
                })
                cy.collection("edge").addClass("edgeShow");
                cy.on("mouseover", "node", function (a) {
                    let c = a.target;
                    cy.collection("edge").addClass('dull');
                    cy.collection("node").addClass('dull');
                    c.removeClass("dull");
                    c.neighborhood("edge").removeClass("dull");
                    c.neighborhood("edge").addClass("edgeActive");
                    c.neighborhood("edge").connectedNodes().removeClass("dull"); //当前节点的邻域边的边缘节点！
                    // c.neighborhood("node").removeClass("dull");//与上面意义相同
                })
                cy.on("mouseout", "node", function (a) {
                    let c = a.target;
                    cy.collection("edge").removeClass('dull');
                    cy.collection("node").removeClass('dull');
                    c.neighborhood("edge").removeClass("edgeActive");
                    c.neighborhood("node").removeClass("nodeActive");
                })
                //点- 点击事件
                cy.on("click", "node", (a)=> {
                    this.nodeClick(a);
                })
                //线- 点击事件
                cy.on("click", "edge", (a)=> {
                    //this.edgeClick(a.target._private.data);
                    this.linkClick(a);
                })
                //线
                cy.on("mouseover", "edge", function (a) {
                    let c = a.target;
                    cy.collection("edge").removeClass("edgeActive");
                    c.addClass("edgeActive")
                })
                cy.on("mouseout", "edge", function (a) {
                    let c = a.target;
                    c.removeClass("edgeActive")
                })

            }/*,
            /!**!/
            nodeClick(currentObj){
                /!*let obj={};
                obj.val = currentObj.id;
                obj.type = "ip";
                obj.kname = "multifield_ip";
                obj.clickType = "node";
                jumpHtml('flowLogs'+obj.val,'logsManage/flowLogs.vue',obj,'日志')*!/
            },
            /!**!/
            edgeClick(currentObj){
                let obj={};
                obj.ipv4_src_addr = currentObj.source;
                obj.ipv4_dst_addr = currentObj.target;
                obj.clickType = "link";
                obj.type = "ip"
                //跳转到流量日志页面
                jumpHtml('flowLogs'+obj.ipv4_src_addr+'-'+obj.ipv4_dst_addr,'logsManage/flowLogs.vue',obj,'日志')
            }*/
        }
    }
</script>

<style scoped>
    #cy{
        width: 100%;
        height:100%;
        position: absolute;
        left: 0;
        top:0;
        background-image: linear-gradient(to bottom, #1a242f 0%, #456479 100%);
    }
</style>

