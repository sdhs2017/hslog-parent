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
        <div class="dashboard-con">
            <div v-for="(item,i) in layout" class="item-wapper" :key="item.i">
                <div class="vue-draggable-handle text-box" style="padding: 5px;" v-if="item.chartType == 'text'">
                    <div class="text-wapper" v-html="item.text"></div>
                </div>
                <div style="height: 250px;" v-else-if="item.chartType == 'systemChart'">
                    <div class="item-tit vue-draggable-handle">
                        <span>{{item.tit}}</span>
                    </div>
                    <div class="item-con">
                        <component :is="allComps[item.eId]" :params="sysChartParams" :baseConProp="{title:''}" :setIntervalObj="intervalObj"> </component>
                    </div>
                </div>
                <div style="height: 250px;" v-else >
                    <div class="item-tit vue-draggable-handle">
                        <div class="tit-zz"></div>
                        <span>{{item.tit}}</span>
                    </div>
                    <div class="no-chart" v-if="item.eId == ''">图表已被删除</div>
                    <div v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)" class="item-con" :ref="`eb${item.i}`" :id="`${item.i}`"></div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import dateLayout from '../../common/dateLayout'
    // import vEcharts from '../../common/echarts'
    import bus from '../../common/bus';
    import {dateFormat,jumpHtml,setChartParam} from "../../../../static/js/common";
    import allComps from '../../charts/index'
    const echarts = require('echarts');
    export default {
        name: "dashboard_m",
        data(){
            return{
                dId:'',
                interval:'',
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
                dashboardId:'',
                title:'',
                loading:true,
                layout:[],
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
                refresh:0,
                dateArr:{
                    starttime:'',//起始时间
                    endtime:'',//结束时间
                    last:'15-min',
                },
                //图表集合  存储echarts名称 用于自适应大小
                echartsArr:{},
                //时间监听事件名称
                busName:'dashboardBusName',
                color:['#1E73F0','#00D1CE','#33C3F7','#3952D3','#185BFF','#2455AD','#74EE9A','#253479'],
            }
        },
        created() {},
        mounted(){

        },
        beforeDestroy(){
            bus.$off(this.busName)
        },
        methods:{
            /*返回上一级*/
            goBack() {
                if(this.$route.query.eid){
                    this.$router.push('/mobile/equipmentList');//返回资产类别
                }else{
                    this.$router.push('/mobile/dashboardList_m');//返回仪表盘列表
                }

            },
            /*获取dashboard数据*/
            getDashboardData(){
                this.$nextTick(()=>{
                   // this.allLoading = true;
                    this.$axios.post(this.$baseUrl+'/BI/getDashboardById.do',this.$qs.stringify({
                        id:this.dashboardId
                    }))
                        .then(res=>{
                            //this.allLoading = false;
                            let obj = res.data;
                            if (obj.success == 'true'){
                               /* this.dashboardParams.name = obj.data.title;
                                this.dashboardParams.des = obj.data.description;*/
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
                           // this.allLoading = false;
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
                let url = '';
                if(resObj.obj.chartType === 'pie'){
                    url = '/BI/getDataByChartParams_pie.do'
                }else if(resObj.obj.chartType === 'bar'){
                    url = '/BI/getDataByChartParams_bar.do'
                }else if(resObj.obj.chartType === 'line'){
                    url = '/BI/getDataByChartParams_line.do'
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
                                else if(obj.chartType === 'metric'){
                                    let str = ''
                                    //循环拼接数据
                                    for(let i in obj.opt.dataset){
                                        obj.opt.dataset[i].value = parseInt(obj.opt.dataset[i].value).toLocaleString();
                                        str += `<span style="margin: 50px;"><p>${obj.opt.dataset[i].name}</p><p style="font-size: ${obj.opt.style.fontSize}px;color: ${obj.opt.style.color};font-weight: 600;">${obj.opt.dataset[i].value}</p></span>`
                                    }
                                    let box = '<div style="width: 100%;height: 100%;display: flex;justify-content: center;align-items: center">'+str+'</div>'
                                    obj.opt.series.push(box)
                                }

                                resolve(obj);
                            })
                            .catch(err=>{
                                $(document.getElementById(obj.i)).next().css("display","none")
                            })
                    })
                })
            },
            /*创建图表*/
            creatEcharts(obj){
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
                    //判断图表类型
                    if(obj.chartType !== 'metric'){//柱、折、饼
                        this.echartsArr[obj.i] = echarts.init(document.getElementById(obj.i))
                        this.echartsArr[obj.i].setOption(obj.opt);
                        window.addEventListener("resize",()=>{
                            this.echartsArr[obj.i].resize();
                        });
                    }else {//指标类型
                        $("#"+obj.i).html('')
                        $("#"+obj.i).append(obj.opt.series[0])
                    }

                    $(document.getElementById(obj.i)).next().css("display","none")
                })

            },
            /*刷新dashboard*/
            refreshDashboard(){
                this.refresh++;
            },
        },
        watch:{
            'dId'(){
                this.getDashboardData()
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
            //轮询
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
        },
        beforeRouteEnter(to, from, next) {
            next(vm => {
                vm.$options.name = 'dashboard_m'+ to.query.id;
                vm.title = to.query.name;
                vm.dashboardId = to.query.id;
                if(to.query.eid){
                    vm.equipmentId = to.query.eid;
                    vm.dId = to.query.eid+to.query.id
                    vm.busName = 'dashboard_m'+to.query.eid+to.query.id
                    let obj = {
                        path: '/mobile/equipmentDashboard' +to.query.eid,
                        component: 'mobile/dashboard/dashboard_m.vue',
                        title: ''
                    }
                    sessionStorage.setItem('/mobile/equipmentDashboard' + to.query.eid, JSON.stringify(obj))
                }else{
                    vm.dId = to.query.id;
                    vm.busName = 'dashboard_m'+to.query.id;
                    let obj = {
                        path: '/mobile/dashboard_m' +to.query.id,
                        component: 'mobile/dashboard/dashboard_m.vue',
                        title: ''
                    }
                    sessionStorage.setItem('/mobile/dashboard_m' + to.query.id, JSON.stringify(obj))
                }
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
    .item-wapper{
        margin: 10px;
        /*height: 250px;*/
        background: url("../../../../static/img/panel-l-t.png");
        background-size: 100% 100%;
    }
    .item-wapper .item-tit{
        height: 40px;
        background: url("../../../../static/img/title-bg.png");
        background-size: 100% 100%;
        display: flex;
        align-items: center;
        padding-left: 20px;
        font-size: 0.4rem;
    }
    .item-wapper .item-con{
        height: calc(100% - 40px);
    }
</style>
