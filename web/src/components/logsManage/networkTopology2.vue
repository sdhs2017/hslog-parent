<template>
    <div class="content-bg">
        <div class="top-title">业务流分析 <i class="el-icon-rank el-tooltip fullScreen" title="全屏观看视图" tabindex="0" @click="fullScreen()"></i></div>
        <div class="topology-wapper">
            <div id="cy"></div>
        </div>
    </div>
    
</template>

<script>
    import {jumpHtml} from "../../../static/js/common";

    export default {
        name: "networkTopology",
        data() {
            return {}
        },
        mounted(){
            this.getNetflowTopologyData()
        },
        methods:{
            /*获得数据*/
            getNetflowTopologyData(){
                layer.load(1);
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getNetworkTopological.do',)
                        .then(res =>{
                            layer.closeAll();
                            let dataArr = res.data;
                            var nodesArr = [];//节点数组
                            var edgesArr = [];//线数组
                            var nodesCountArr = [];//节点count数组
                            var linksCountArr = [];//节点count数组
                            //{data:{id:'王大拿',target:true,type:'People'}},
                            //{"node": 1,"name": "123.232.103.226","count": "188394"}
                            //node节点
                            for(var i in dataArr[0].data){
                                var obj = {};
                                obj.data = {};
                                obj.data.id = dataArr[0].data[i].name;
                                obj.data.target = true;
                                obj.data.count = dataArr[0].data[i].count;
                                obj.data.type = dataArr[0].data[i].node;
                                nodesCountArr.push(dataArr[0].data[i].count)
                                obj.position = {};
                                obj.position.x = 100;
                                obj.position.y = 100;
                                nodesArr.push(obj);
                            }
                            var maxNodesCount = Math.max.apply(null,nodesCountArr);
                            // {data:{source:'王大拿',target:'AAA公司',label:'董事'}},
                            //{"node": 1,"count": 80761,"source": "123.232.103.226","target": "level2\n192.168.2.151"}
                            //连接线
                            for(var i in dataArr[0].links){
                                var obj = {};
                                obj.data = {};
                                obj.data.source = dataArr[0].links[i].source;
                                obj.data.target = dataArr[0].links[i].target;
                                obj.data.count = dataArr[0].links[i].count;
                                obj.data.label = dataArr[0].links[i].count;
                                linksCountArr.push(dataArr[0].links[i].count);
                                edgesArr.push(obj);
                            }
                            var maxLinksCount = Math.max.apply(null,linksCountArr);
                            //console.log(nodesArr)
                            //渲染
                            var cy = cytoscape({
                                container: document.getElementById('cy'),
                                autounselectify: true,//默认情况下节点是否应该是未分类的
                               /* maxZoom:4,//最大缩放
                                minZoom:0.7,//最小缩放*/
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
                                        "x":100,
                                        "y":100,
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
                                            return 25;
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
                                    nodeOverlap: 2000,
                                    refresh: 20,
                                    fit: true,
                                    padding: 3,
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
                                this.nodeClick(a.target._private.data);
                            })
                            //线- 点击事件
                            cy.on("click", "edge", (a)=> {
                                this.edgeClick(a.target._private.data);
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
                        })
                        .catch(err =>{
                            layer.closeAll()
                            layer.msg('获取信息失败',{icon:5})
                        })
                })
            },
            /*节点 点击事件*/
            nodeClick(currentObj){
                this.exitFullScreen();
                let obj={};
                obj.val = currentObj.id;
                obj.type = "ip";
                obj.kname = "multifield_ip";
                obj.clickType = "node";
                jumpHtml('flowLogs'+obj.val,'logsManage/flowLogs.vue',obj,'日志');
            },
            /*连接线 点击事件*/
            edgeClick(currentObj){
                this.exitFullScreen();
                let obj={};
                obj.val = currentObj.source+'-'+currentObj.target;
                obj.ipv4_src_addr = currentObj.source;
                obj.ipv4_dst_addr = currentObj.target;
                obj.clickType = "link";
                obj.type = "ip"
                //跳转到流量日志页面
                jumpHtml('flowLogs'+obj.ipv4_src_addr+'-'+obj.ipv4_dst_addr,'logsManage/flowLogs.vue',obj,'日志')
            },
            /*全屏显示*/
            fullScreen(){
                let el = document.getElementById('cy');
                let rfs = el.requestFullScreen || el.webkitRequestFullScreen || el.mozRequestFullScreen || el.msRequestFullScreen,
                    wscript;
                console.log(el)
                if(typeof rfs !== "undefined" && rfs) {
                    rfs.call(el);
                    return;
                }

                if(typeof window.ActiveXObject !== "undefined") {
                    wscript = new ActiveXObject("WScript.Shell");
                    if(wscript) {
                        wscript.SendKeys("{F11}");
                    }
                }
            },
            /*关闭全屏*/
            exitFullScreen() {
                var el= document,
                    cfs = el.cancelFullScreen || el.webkitCancelFullScreen || el.mozCancelFullScreen || el.exitFullScreen,
                    wscript;

                if (typeof cfs !== "undefined" && cfs) {
                    cfs.call(el);
                    return;
                }

                if (typeof window.ActiveXObject !== "undefined") {
                    wscript = new ActiveXObject("WScript.Shell");
                    if (wscript != null) {
                        wscript.SendKeys("{F11}");
                    }
                }
            }
        }
    }
</script>

<style scoped>
    .topology-wapper{
        min-height: 810px;
        position:relative;
        padding: 10px;
    }
    #cy{
        width: 100%;
        height:100%;
        position: absolute;
        left: 0;
        top:0;
        overflow: hidden;
        background-image: linear-gradient(to bottom, #1a242f 0%, #456479 100%);
    }
    .fullScreen{
        float: right;
        margin-right: 20px;
        cursor: pointer;
        margin-top: 16px;
    }
</style>
