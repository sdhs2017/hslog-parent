<template>
    <div class="content-bg">
        <div class="top-title">业务流分析
            <i class="el-icon-rank el-tooltip fullScreen" title="全屏观看视图" tabindex="0" @click="fullScreen()"></i>
            <el-date-picker
                style="float: right;margin-top: 10px;margin-right: 10px;"
                v-model="timepicker"
                type="daterange"
                align="right"
                unlink-panels
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="yyyy-MM-dd"
                @change="timepickerChange"
                :picker-options="pickerOptions">
            </el-date-picker>
        </div>
        <div class="topology-wapper">
            <div id="cy"></div>
        </div>
    </div>
    
</template>

<script>
   // import * as d3 from 'd3';
    import {jumpHtml,calCircleCenter} from "../../../static/js/common";

    export default {
        name: "networkTopology",
        data() {
            return {
                timepicker:'',//日期值
                pickerOptions: { //日期选择器
                    shortcuts: [{
                        text: '最近一周',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '最近一个月',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '最近三个月',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                            picker.$emit('pick', [start, end]);
                        }
                    }]
                },
                zoomHandler:null,
            }
        },
        mounted(){

            //设置日期
            let newDate = new Date();
            let curDate = newDate.toLocaleDateString();
            let curYear = newDate.getFullYear();
            let curMonth = newDate.getMonth() + 1;
            let curDay = newDate.getDate();
            if(curMonth < 10){
                curMonth = '0'+curMonth
            }
            if(curDay < 10){
                curDay = '0' + curDay;
            }
            //let startTime = curYear+'-'+curMonth+'-01';
            let date2 = new Date(newDate);
            date2.setDate(newDate.getDate()-7);
            let startTime = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
            let endTime = curYear+'-'+curMonth+'-'+curDay;
            this.timepicker=[startTime,endTime];
            this.getNetflowTopologyData(this.timepicker)
        },
        methods:{
            /*获得数据*/
            getNetflowTopologyData(timeArr){
                layer.load(1);
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getNetworkTopological.do',this.$qs.stringify({starttime:timeArr[0],endtime:timeArr[1]}))
                        .then(res =>{
                            layer.closeAll();
                            $('#cy').html('');

                            //定义画布宽高
                            var width = $('#cy').width();
                            var height = $("#cy").height();
                            //定义画布
                            var svg = d3.select("#cy").append("svg")
                                .attr("width", width)
                                .attr("height", height)
                                .call( d3.zoom().scaleExtent([0.1,10]).on("zoom", ()=> {
                                    if(1){
                                        container.attr("transform", d3.event.transform);
                                    }
                                }))
                            console.log( d3.event)
                            var container =  svg.append("g")   //相当于缩放的容器
                                .attr("width", width)
                                .attr("height", height)
                                .attr("x","0")
                                .attr("y",'0')
                                .attr("class", "relMap_g")

                            //取得20个颜色的序列
                            var color = d3.scaleOrdinal(d3.schemeCategory20);
                            //定义力学结构
                            var simulation = d3.forceSimulation()
                                .force("link", d3.forceLink().id(function(d) {
                                    return d.id;
                                }))
                                //万有引力
                                //.force("charge", d3.forceManyBody().strength(-300))
                                // 碰撞作用力，为节点指定一个radius区域来防止节点重叠，设置碰撞力的强度，范围[0,1], 默认为0.7。设置迭代次数，默认为1，迭代次数越多最终的布局效果越好，但是计算复杂度更高
                                .force("collide", d3.forceCollide(100).strength(0.1).iterations(10))
                                .force("center", d3.forceCenter(width/2, height / 2))
                            //.alphaDecay(0.0228)
                            //d3.json("json/data2.json", function(error, graph) {
                            var maxNodeR = '';
                            var maxLinkW = '';

                            let data = res.data;
                            var graph = {};
                            var nodes = [];//节点数据
                            var links = [];//线数组
                            var nodeRArr = [];//节点半径数据
                            var linkWArr = [];//线宽度
                            data[0].data.forEach(function (item){
                                nodeRArr.push(item.count);
                                nodes.push({
                                    id:item.name,
                                    count:item.count
                                })
                            })
                            //定义最大半径
                            maxNodeR = Math.max.apply(null,nodeRArr);

                            data[0].links.forEach(function(item){
                                linkWArr.push(item.count)
                            })
                            //最大线宽
                            maxLinkW = Math.max.apply(null,linkWArr);

                            graph.nodes = nodes;
                            graph.links = data[0].links;

                            var defs = svg.append('defs');
                            //箭头
                            var marker =
                                defs.append("marker")
                                    .attr("id", "resolved")
                                    .attr("markerUnits", "strokeWidth")//设置为strokeWidth箭头会随着线的粗细发生变化
                                    //.attr("markerUnits", "userSpaceOnUse")
                                    .attr("viewBox", "0 -5 10 10")//坐标系的区域
                                    /*.attr("refX", function(d){

                                        return 22;
                                    })//箭头坐标
                                    .attr("refY", 0)
                                    /*.attr("markerWidth", 12)//标识的大小
                                    .attr("markerHeight", 12)*/
                                    .attr("orient", "auto")//绘制方向，可设定为：auto（自动确认方向）和 角度值
                                    .attr("stroke-width", 1)//箭头宽度
                                    .append("path")
                                    .attr("d", "M0,-5L10,0L0,5")//箭头的路径
                                    .attr('fill', '#65f9fd');//箭头颜色

                            //线容器
                            var edges =  container.selectAll("g.edge")
                                .data(graph.links)
                                .enter()
                                .append("g")
                                .attr("class","edge")

                            edges.append("title") //为每个线设置title（类似于html标签的title属性）
                                .text(function(d) {
                                    return d.count;
                                });
                            //线
                            var links = edges.append("path")
                                .attr("class","links")
                                .style("marker-mid", "url(#resolved)")
                                // .attr("refX",this.config.r)
                                .style("stroke","#65f9fd")
                                .style("stroke-width",function(d){
                                    var rv = (d.count * 10)/maxLinkW;
                                    d.w = rv<2 ? 2 : rv
                                    return d.w;
                                })
                                .on("mouseenter",function(d){
                                    d3.select(this).style('stroke-width', d.w+4);
                                    //当前线的index
                                    var cLIndex = [d.index];
                                    // 隐藏线
                                    svg.selectAll('.edge').filter(function (d1) {
                                        // return true;
                                        return (cLIndex.indexOf(d1.index) == -1)
                                    }).style('opacity', 0.1);
                                    //当前线的节点index
                                    var cNIndex = [d.source.index,d.target.index];
                                    //隐藏节点
                                    svg.selectAll('.node').filter(function (d1) {
                                        return (cNIndex.indexOf(d1.index) == -1);
                                    }).style('opacity', 0.1);


                                })
                                .on('mouseleave', function(d){
                                    //回复原来的状态
                                    d3.select(this).style('stroke-width', d.w);
                                    svg.selectAll('.node').filter(function (d) {
                                        return true;
                                    }).style('opacity', 1);
                                    svg.selectAll('.edge').filter(function (d) {
                                        return true;
                                    }).style('opacity', 1);
                                })
                                .on('click', (d)=>{
                                   this.edgeClick(d)
                                })
                            var linkText = edges.append('g')
                                .attr("class","linkText")
                                .append('text')
                                .attr('fill',"#fff")
                                .style("font-weight","600")
                                .text(function(d){return d.count});

                            //绘制节点
                            var node = container.selectAll(".node")
                                .data(graph.nodes)
                                .enter().append("g")
                                .attr("class", "node")
                                /*.call(d3.drag().on("start", dragstarted)//d3.drag() 创建一个拖曳行为
                                .on("drag", dragged)
                                .on("end", dragended))*/
                                .on('mouseenter', function(d){
                                    //设置样式
                                    d3.select(this).selectAll("circle").attr('stroke-width', '8');
                                    d3.select(this).selectAll("circle").attr('stroke', '#a3e5f9');
                                    //定义鼠标悬停相关的节点、线的index数组
                                    var dependsNode = [];
                                    var dependsLinkAndText = [];
                                    //当前的index
                                    var objIndex = d.index;
                                    dependsNode = dependsNode.concat([objIndex]);
                                    dependsLinkAndText = dependsLinkAndText.concat([objIndex]);
                                    //筛选出当前有关的节点、线的index
                                    graph.links.forEach(function (lkItem) {
                                        if (objIndex == lkItem['source']['index']) {
                                            dependsNode = dependsNode.concat([lkItem.target.index]);
                                        } else if (objIndex == lkItem['target']['index']) {
                                            dependsNode = dependsNode.concat([lkItem.source.index]);
                                        }
                                    });
                                    //隐藏节点
                                    svg.selectAll('.node').filter(function (d1) {
                                        return (dependsNode.indexOf(d1.index) == -1);
                                    }).style('opacity', 0.01);
                                    // 隐藏线
                                    svg.selectAll('.edge').filter(function (d1) {
                                        // return true;
                                        return ((dependsLinkAndText.indexOf(d1.source.index) == -1) && (dependsLinkAndText.indexOf(d1.target.index) == -1))
                                    }).style('opacity', 0.01);
                                })
                                .on('mouseleave', function(d){
                                    d3.select(this).selectAll("circle").attr('stroke-width','0');
                                    // d3.select(this).attr('stroke', '#c5dbf0');
                                    svg.selectAll('.node').filter(function (d) {
                                        return true;
                                    }).style('opacity', 1);
                                    svg.selectAll('.edge').filter(function (d) {
                                        return true;
                                    }).style('opacity', 1);
                                })
                                .on('click', (d)=>{
                                    this.nodeClick(d)
                                })
                            node.append("circle")
                                .attr("r", function(d) {
                                    var rv = (d.count * 100)/maxNodeR;
                                    d.w = rv<20 ? 20 : rv;
                                    return d.w;
                                })
                                /*.attr("fill", function(d,i){
                                    return color(d.count);
                                })*/
                                .attr("fill",function(d){
                                    if(d.count == maxNodeR){
                                        return "#ff7f0e";
                                    }else {
                                        return "#59c9a5";
                                    }
                                })
                                .attr('stroke',  function(d){
                                    return color(d.count);
                                })
                            node.append("text")
                                .attr("font-family", "微软雅黑")
                                .attr("text-anchor", "middle")
                                .attr("fill","#fff")
                                .style("font-weight","600")
                                .attr("dy", function() { //dy表示文字的偏移量
                                    return "0em";
                                })
                                .attr('x', function(d) {
                                    d3.select(this).append('tspan')//添加文字
                                        .text(function() {
                                            return d.id;
                                        })
                                    d3.select(this).append('tspan') //添加文字
                                        .attr("x",0)
                                        .attr("y",14)
                                        .text(function() {
                                            return "活跃度："+d.count;
                                        });
                                });
                            simulation
                                .nodes(graph.nodes)//设置力模拟的节点
                                .on("tick", ticked);

                            simulation.force("link")//添加或移除力
                                .links(graph.links);//设置连接数组
                            function dragstarted(d) {
                                if (!d3.event.active) simulation.alphaTarget(0.3).restart();//设置目标α
                                d.fx = d.x;
                                d.fy = d.y;
                            }

                            function dragged(d) {
                                d.fx = d3.event.x;
                                d.fy = d3.event.y;
                            }

                            function dragended(d) {
                                if (!d3.event.active) simulation.alphaTarget(0);
                                /*	d.fx = null;
                                    d.fy = null;*/
                            }
                            function ticked() {
                                //线
                                links
                                    .attr("d", function(d) { //设置线条的偏移以及路径
                                        var dx = d.target.x - d.source.x,
                                            dy = d.target.y - d.source.y,
                                            dr = Math.sqrt(dx * dx + dy * dy);
                                        /*下面表示位置， M（表示画笔落下的位置）, A（画椭圆）是大写的，表示绝对位置。当使用相对位置时，要小写*/
                                        return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,0 " + d.target.x + "," + d.target.y;
                                    })
                                    .attr("fill", "transparent");
                                //线描述
                                linkText.attr("transform", function (d) {
                                    var dx = d.target.x - d.source.x,
                                        dy = d.target.y - d.source.y,
                                        dr = Math.sqrt(dx * dx + dy * dy);
                                    var oa= calCircleCenter([d.source.x,d.source.y],[d.target.x,d.target.y],dr)
                                    //判断圆点位置
                                    var o = [];
                                    if(d.source.x < d.target.x && d.source.y > d.target.y){
                                        o = [oa[2],oa[3]]
                                    }else if(d.source.x > d.target.x && d.source.y > d.target.y){
                                        o = [oa[2],oa[3]]
                                    }else if(d.source.x < d.target.x && d.source.y < d.target.y){
                                        o = [oa[0],oa[1]]
                                    }else if(d.source.x > d.target.x && d.source.y < d.target.y){
                                        o = [oa[0],oa[1]]
                                    }
                                    //计算偏移量 （偏移角度为30度）
                                    var x = ( d.source.x - o[0])*Math.cos(-3.14/6) - ((d.source.y -o[1])*Math.sin(-3.14/6)) + o[0];
                                    var y = (d.source.x - o[0])*Math.sin(-3.14/6) + ((d.source.y - o[1])*Math.cos(-3.14/6)) + o[1]
                                    return "translate(" +x + "," +y + ")";
                                })
                                //调整节点位置
                                node.attr("transform", function(d) { //circle节点的偏移量
                                    if(d.count == maxNodeR){ //最大节点放在视图中心
                                        d.x = width/2;
                                        d.y = height/2
                                    }
                                    return "translate(" + d.x + "," + d.y + ")";
                                });
                                //调整视图位置

                            }


                        })
                        .catch(err =>{
                            layer.closeAll()
                            layer.msg('获取信息失败',{icon:5})
                        })
                    /*//缩放
                    var zoom = d3.zoom()
                        .scaleExtent([0.1,10])
                        .on("zoom", ()=> {
                            if(1){
                                container.attr("transform", d3.event.transform);
                            }
                        });*/
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
                obj.val = currentObj.source.id+'-'+currentObj.target.id;
                obj.ipv4_src_addr = currentObj.source.id;
                obj.ipv4_dst_addr = currentObj.target.id;
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
            },
            //日期改变事件
            timepickerChange(){
                if(this.timepicker === null){
                    this.timepicker=['','']
                }
                this.getNetflowTopologyData(this.timepicker);
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
