<template>
    <div class="content-bg">
        <div class="top-title">{{ip}} 关系图</div>
        <div class="graph-wapper">
            <div id="graph-content"></div>
        </div>
    </div>
    
</template>

<script>
    const echarts = require('echarts');
    export default {
        name: "graph",
        data() {
            return {
                ip:'',
                type:'',
                count:''
            }
        },
        mounted(){

        },
        methods:{
            /*获得数据*/
            getTopologicalData(){
                // 基于准备好的容器(这里的容器是id为chart1的div)，初始化echarts实例
                var chart = echarts.init(document.getElementById("graph-content"));
                var option = {
                    backgroundColor:'#3A4D5F',
                    title:{ 	//标题
                        text:"网络关系图",
                        x:'center',
                        textStyle:{
                            color:""
                        }
                    },
                    tooltip: {  // 提示框的配置
                        formatter: function(param) {
                            if (param.dataType === 'edge') {
                                //return param.data.category + ': ' + param.data.target;
                                return param.data.count;
                            }
                            //return param.data.category + ': ' + param.data.name;
                            return param.data.name + '<br/> 次数：' + param.data.count;
                        }
                    },
                    series:[{
                        type: 'graph',          // 系列类型:关系图
                        top: '10%',             // 图表距离容器顶部的距离
                        roam: true,             // 是否开启鼠标缩放和平移漫游
                        focusNodeAdjacency: true,   // 是否在鼠标移到节点上的时候突出显示节点以及节点的边和邻接节点。
                        force:{
                            repulsion: 500,	//节点之间的斥力因子
                            edgeLength: [100, 100] //边的两个节点之间的距离，也收repulsion影响
                        },
                        layout:'force',  //图的布局  force采用力引导布局
                        symbol:'circle', //节点形状  -圆形
                        edgeSymbol:['circle','arrow'],//关系边的形状 -> 箭头
                        lineStyle:{  //线的基本样式
                            normal:{
                                opacity:0.7,  //透明度
                                /* curveness:0.5 //边的弯曲 */
                            }
                        },
                        label:{ //节点对象上的标签
                            normal:{
                                show:true,          //是否显示
                                position:'inside',  //位置  节点里面
                                textStyle:{  		//文本样式
                                    fontSize:12
                                }
                            }
                        },
                        data:[     //节点数据

                        ],
                        links:[{    //节点之间的关系数据

                        }],
                        animationEasingUpdate: "quinticInOut",//数据更细动画的欢动效果
                        animationDurationUpdate: 100          //数据更新动画的时长

                    }]
                }
                //使用此配置显示图表
                chart.setOption(option);
                var CIRCLE_SIZE = 100; //节点大小
                var LINE_SIZE = 10; //线大小
                layer.load(1);
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getTopologicalData.do',this.$qs.stringify({groupfiled:this.type,iporport:this.ip,count:this.count}))
                        .then(res =>{
                            layer.closeAll();
                            let data = res.data;
                            //关系图节点
                            var echartNodeData = data[0].data;
                            //获取中间节点的count
                            var centerCount = echartNodeData[0].count;
                            //处理节点数据
                            for(var i in echartNodeData){
                                /* var obj = {
                                    obj.name = data[i].name;
                                    draggable: true,
                                    symbolSize: 100,
                                    itemStyle: {
                                        color: '#ff0000'
                                    }
                                } */
                                echartNodeData[i].itemStyle = {};
                                //echartNodeData[i].symbolSize = [];
                                echartNodeData[i].draggable = true;//可拖拽
                                echartNodeData[i].symbolSize = Math.pow(echartNodeData[i].count/centerCount,1/3) * CIRCLE_SIZE; //大小
                                //颜色
                                if(echartNodeData[i].node === 1){
                                    echartNodeData[i].itemStyle.color = '#de9325';
                                }else if(echartNodeData[i].node === 2){
                                    echartNodeData[i].itemStyle.color = '#5f9da5';
                                }else if(echartNodeData[i].node === 3){
                                    echartNodeData[i].itemStyle.color = '#bda29a';
                                }

                            }

                            //关系图节点连线
                            var echartLinkData = data[0].links;
                            var lineArr1 = [];

                            for(var i in echartLinkData){
                                if(echartLinkData[i].node === 1){
                                    lineArr1.push(echartLinkData[i].count);
                                }
                            }
                            //获得一级线段数组中的次数最大值
                            var maxLineCount = Math.max.apply(null,lineArr1);
                            //处理线数据
                            for(var i in echartLinkData){
                                echartLinkData[i].lineStyle = {};
                                //初始线宽
                                var lineWidth = Math.pow(echartLinkData[i].count/maxLineCount,1/3) * LINE_SIZE;
                                echartLinkData[i].symbolSize = [lineWidth,2*lineWidth];
                                //echartLinkData[i].lineStyle.curveness = 0.3;
                                echartLinkData[i].lineStyle.width = lineWidth;

                                if(echartLinkData[i].node === 1){
                                    echartLinkData[i].lineStyle.color = "#EBC38C";
                                }else if(echartLinkData[i].node === 2){
                                    echartLinkData[i].lineStyle.color = "#aed1d5";
                                }
                            }

                            //设置echart
                            option.series[0].data = echartNodeData;
                            option.series[0].links = echartLinkData;
                            chart.setOption(option);
                            window.addEventListener("resize",function(){
                                chart.resize();
                            });
                        })
                        .catch(err =>{
                            layer.closeAll()
                            layer.msg('获取信息失败',{icon:5})
                        })
                })
            }
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                vm.$options.name = 'graph'+ to.query.iporport;
                //修改data参数
                if(vm.ip === '' || vm.ip !== to.query.iporport){
                    vm.ip = to.query.iporport;
                    vm.type = to.query.type;
                    vm.count = to.query.count;
                    vm.getTopologicalData();
                }

            })

        },
    }
</script>

<style scoped>
    .graph-wapper{
        padding: 10px;
    }
    #graph-content{
        height: 810px;
    }
</style>
