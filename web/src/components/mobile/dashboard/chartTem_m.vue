<template>
    <div>
        <div class="title">
            <span class="goBack" @click="goBack()">返回</span>
            <b>{{title}}</b>
            <el-button size="mini" type="primary" plain @click="refreshDashboard">刷新</el-button>
        </div>
        <div class="date-wapper">
            <dateLayout :busName="busName" :defaultVal = "defaultVal" :refresh="refresh"></dateLayout>
        </div>
        <div class="chart-wapper" id="chart-wapper" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">

        </div>
    </div>
</template>

<script>
    import dateLayout from '../../common/dateLayout'
    import bus from '../../common/bus';
    import {dateFormat,jumpHtml,setChartParam} from "../../../../static/js/common";
    import allComps from '../../charts/index'
    const echarts = require('echarts');
    export default {
        name: "chartTem_m",
        data(){
            return{
                loading:false,
                chartId:"",
                title:"",
                busName:"",
                chartType:'',
                refresh:0,
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
                interval:'',
                //轮询参数
                intervalObj:{
                    state:false,
                    interval:'5000'
                },
                dateArr:{
                    starttime:'',//起始时间
                    endtime:'',//结束时间
                    last:'15-min',
                },
                eOpt:{},
                param:{},
                color:['#1E73F0','#00D1CE','#33C3F7','#3952D3','#185BFF','#2455AD','#74EE9A','#253479'],
                color1 :['#00EABD','#20C1F3','#FC686F','#F9D124','#DE1AFB','#C0D7FC','#A9F4B7','#FF9E96','#75B568','#323A81'],
            }
        },
        created() {},
        beforeDestroy(){
            bus.$off(this.busName);
        },
        methods:{
            /*返回*/
            goBack(){
                this.$router.push('/mobile/chartList_m');//返回上一层
            },
            /*获取图表结构数据*/
            getEchartsConstruction(id){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/BI/getVisualizationById.do',this.$qs.stringify({
                            id: id
                        }))
                        .then(res=>{
                            this.loading = false;
                            let data = res.data;
                            if (data.success === 'true'){
                                let opt = JSON.parse(data.data.option).opt;
                                this.param = JSON.parse(data.data.params)
                                opt.title.show = false;
                                this.eOpt = {};
                                this.eOpt .areaShow = JSON.parse(data.data.option).config.graph.areaShow
                                this.eOpt .opt = opt;
                                this.getEchartData(this.param,this.eOpt)

                            }else{
                                layer.msg('获取数据失败',{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*获取数据*/
            getEchartData(param,obj){
                //判断请求的方法
                let url = '';
                if(this.chartType === 'pie'){
                    url = '/BI/getDataByChartParams_pie.do'
                }else if(this.chartType === 'bar'){
                    url = '/BI/getDataByChartParams_bar.do'
                }else if(this.chartType === 'line'){
                    url = '/BI/getDataByChartParams_line.do'
                }
                param.starttime = this.dateArr.starttime;
                param.endtime = this.dateArr.endtime;
                param.last = this.dateArr.last;
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+url,this.$qs.stringify(param))
                        .then(res=>{
                            this.loading = false;
                            obj.opt.dataset = res.data.data;
                            obj.opt.series=[];
                            //判断图表类型
                            if(this.chartType === 'bar'){
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
                            else if(this.chartType === 'line'){
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
                            else if(this.chartType === 'pie'){
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
                            else if(this.chartType === 'metric'){
                                let str = ''
                                //循环拼接数据
                                for(let i in obj.opt.dataset){
                                    obj.opt.dataset[i].value = parseInt(obj.opt.dataset[i].value).toLocaleString();
                                    str += `<span style="margin: 50px;"><p>${obj.opt.dataset[i].name}</p><p style="font-size: ${obj.opt.style.fontSize}px;color: ${obj.opt.style.color};font-weight: 600;">${obj.opt.dataset[i].value}</p></span>`
                                }
                                let box = '<div style="width: 100%;height: 100%;display: flex;justify-content: center;align-items: center">'+str+'</div>'
                                obj.opt.series.push(box)
                            }
                            //创建图表
                            //判断图表类型
                            if(this.chartType !== 'metric'){//柱、折、饼
                                let myChart = echarts.init(document.getElementById('chart-wapper'))
                                myChart.setOption(obj.opt);
                                window.addEventListener("resize",()=>{
                                    myChart.resize();
                                });
                            }else {//指标类型
                                $("#chart-wapper").html('')
                                $("#chart-wapper").append(obj.opt.series[0])
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*刷新*/
            refreshDashboard(){
                this.refresh++;
            }
        },
        watch:{
            'chartId'(){
                this.getEchartsConstruction(this.chartId);
                bus.$on(this.busName,(obj)=>{
                    this.dateArr = new Object();
                    this.dateArr = setChartParam(obj)[0];
                    this.intervalObj = setChartParam(obj)[1];
                })

            },
            'dateArr'(){
                this.loading = true;
                this.getEchartData(this.param,this.eOpt)
            },
            intervalObj:{
                handler(newV,oldV) {
                    //判断是否启用轮询获取数据
                    if (this.intervalObj.state){
                        //判断参数是否合法(是否有刷新间隔时间)
                        if(this.intervalObj.interval){//合法
                            clearInterval(this.interval)
                            this.interval = setInterval(()=>{
                                this.getEchartData(this.param,this.eOpt)
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
            }
        },
        beforeRouteEnter(to, from, next) {
            next(vm => {
                vm.$options.name = 'chartTem_m'+ to.query.id;
                vm.title = to.query.name;
                vm.busName = 'chartTem_m'+ to.query.id;
                vm.chartType = to.query.type;
                vm.chartId = to.query.id;
                let obj = {
                    path: '/mobile/chartTem_m' +to.query.id,
                    component: 'mobile/dashboard/chartTem_m.vue',
                    title: ''
                }
                sessionStorage.setItem('/mobile/chartTem_m' + to.query.id, JSON.stringify(obj))

            })

        },
        components:{
            dateLayout
        }
    }
</script>

<style scoped>
    .title{
        padding: 10px;
        display: flex;
        justify-content: space-between;
        text-shadow: none;
        align-items: center;
    }
    .title b{
        font-size: 1.2rem;
        font-weight: 600;
        color: #185bff;
    }
    .title span{
        font-size: 0.4rem;
    }
    .title /deep/ .el-button--mini, .el-button--mini.is-round {
        padding: 5px 7px;
        font-size: 0.4rem;
    }
    .date-wapper{
        display: flex;
        justify-content: center;
        align-items: center;
    }
    .chart-wapper{
        margin: 10px;
        height: 250px;
        background: url("../../../../static/img/panel-l-t.png");
        background-size: 100% 100%;
    }
</style>
