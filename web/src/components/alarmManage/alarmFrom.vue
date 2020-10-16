<template>
    <div v-loading="loading" element-loading-background="rgba(48, 62, 78, 0.5)">
        <el-steps :active="active" simple>
            <el-step title="基本信息" icon="el-icon-edit"></el-step>
            <el-step title="选择字段" icon="el-icon-upload"></el-step>
            <el-step title="完成" icon="el-icon-picture"></el-step>
        </el-steps>
        <div class="con-wapper" v-show="active === 0">
            <div class="form-con">
                <div class="form-item">
                    <span class="mustWrite">*</span>
                    <div class="item-label">名称：</div>
                    <div class="item-con">
                        <el-input v-model="form.alert_name" size="mini" style="width: 250px;"></el-input>
                    </div>
                </div>
                <div class="form-item">
                    <span class="mustWrite" style="left: -10px;">*</span>
                    <choose-index :busName="this.busNameObj.busIndexName" :arr = "indexVal"></choose-index>
                </div>
                <div class="form-item">
                    <span class="mustWrite" >*</span>
                    <div class="item-label" style="position: absolute; top: 9px;">筛选：</div>
                    <div class="item-con" style="margin-left: 56px;">
                        <queryFilter
                            :busFilterName="this.busNameObj.busFilterName"
                            :defaultFilterArr="this.defaultFilter"
                            :queryState="false"
                            useObject="chart"
                            :templateName="this.form.templateName"
                            :preIndexName="this.form.preIndexName"
                            :suffixIndexName="this.form.suffixIndexName"
                        >
                        </queryFilter>
                    </div>
                </div>
                <div class="form-item">
                    <div class="item-label" style="position: absolute; top: 9px;">统计：</div>
                    <div class="item-con" style="margin-left: 58px;" v-loading="leftYLoading" element-loading-background="rgba(48, 62, 78, 0.5)">
                        <el-collapse>
                            <el-collapse-item class="tablist" v-for="(yItem,i) in yAxisArr" :key="i">
                                <template slot="title" class="collapseTit">
                                    <i class="header-icon el-icon-error removeTab" @click="yAxisArr = []"></i>
                                </template>
                                <el-form label-position="top" style="position: relative;">
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
                                    <el-form-item label="聚合参数"  v-if="yItem.aggregationType !== '' && yItem.aggregationType !== 'Count' ">
                                        <el-select v-model="yItem.aggregationParam" placeholder="请选择" filterable style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in yItem.aggregationParamArr"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                </el-form>
                            </el-collapse-item>
                        </el-collapse>
                        <p v-if="yAxisArr.length === 0" class="addWapper"><span @click="addMetric"><i class="el-icon-plus"></i>添加</span></p>
                    </div>
                </div>
                <div class="form-item" >
                    <div class="item-label" style="position: absolute; top: 9px;">分组：</div>
                    <div class="item-con" style="margin-left: 58px;" v-loading="leftXLoading" element-loading-background="rgba(48, 62, 78, 0.5)">
                        <el-collapse>
                            <el-collapse-item class="tablist" v-for="(xItem,i) in xAxisArr" :key="i">
                                <template slot="title" class="collapseTit">
                                    <i class="header-icon el-icon-error removeTab" @click="xAxisArr = []"></i>
                                </template>
                                <el-form label-position="top" style="position: relative">
                                    <el-form-item label="聚合类型">
                                        <el-select v-model="xItem.aggregationType" @change="xAggregationChange($event,i)" placeholder="请选择" style="width: 100%;" size="mini">
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
                                    <el-form-item label="间隔类型" v-if="xItem.aggregationType === 'Date Histogram'">
                                        <el-select v-model="xItem.timeType" placeholder="请选择" style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in timeIntervalArr"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="间隔数值" v-if="xItem.aggregationType === 'Date Histogram'">
                                        <el-input v-model="xItem.timeInterval" size="mini"></el-input>
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
                                    <el-form-item label="范围" v-if="xItem.aggregationType === 'Range'">
                                        <p class="range-p" v-for="(numRange,i) in xItem.numberRange">
                                            <el-input v-model="numRange.start" type="number" placeholder="大于等于" size="mini"></el-input>
                                            <span>-</span>
                                            <el-input v-model="numRange.end"  type="number" placeholder="小于" size="mini"></el-input>
                                            <i class="el-icon-error" @click="removeItem(xItem.numberRange,i)" disabled></i>
                                        </p>
                                        <p class="range-btn-p"><span @click="xItem.numberRange.push({start:'',end:''})"> <i class="el-icon-circle-plus"></i> 添加范围</span></p>
                                    </el-form-item>
                                    <el-form-item label="范围" v-if="xItem.aggregationType === 'Date Range'">
                                        <p class="range-p" v-for="(dateRange,i) in xItem.dateRange">
                                            <el-input v-model="dateRange.start"  size="mini"></el-input>
                                            <span>-</span>
                                            <el-input v-model="dateRange.end"  size="mini"></el-input>
                                            <i class="el-icon-error" @click="removeItem(xItem.dateRange,i)" disabled></i>
                                        </p>
                                        <p class="range-btn-p"><span @click="xItem.dateRange.push({start:'',end:''})"> <i class="el-icon-circle-plus"></i> 添加范围</span></p>
                                    </el-form-item>
                                    <el-form-item label="范围" v-if="xItem.aggregationType === 'IPv4 Range'">
                                        <p class="range-p" v-for="(ipRange,i) in xItem.ipRange">
                                            <el-input v-model="ipRange.start"  size="mini"></el-input>
                                            <span>-</span>
                                            <el-input v-model="ipRange.end"  size="mini"></el-input>
                                            <i class="el-icon-error" @click="removeItem(xItem.ipRange,i)" disabled></i>
                                        </p>
                                        <p class="range-btn-p"><span @click="xItem.ipRange.push({start:'',end:''})"> <i class="el-icon-circle-plus"></i> 添加范围</span></p>
                                    </el-form-item>
                                </el-form>
                            </el-collapse-item>
                        </el-collapse>
                        <p v-if="xAxisArr.length === 0" class="addWapper"><span @click="addBucket"><i class="el-icon-plus"></i>添加</span></p>
                    </div>
                </div>
            </div>
            <div class="btn-wapper">
                <el-button type="primary" @click="getFields()">下一步</el-button>
            </div>
        </div>
        <div class="con-wapper" v-show="active === 1">
            <div class="form-con">
                <jsonView :data="this.JSONData" theme="one-dark" :line-height="20" :deep="5" class="jsonView"></jsonView>
            </div>
            <div class="btn-wapper">
                <el-button @click="active = 0" type="primary">上一步</el-button>
            </div>
        </div>
        <div class="con-wapper" v-show="active === 2"></div>
    </div>
</template>

<script>
    import chooseIndex from '../dashboard/chooseIndex'
    import queryFilter from '../dashboard/queryFilter'
    import jsonView from 'vue-json-views'
    import bus from '../common/bus';
    export default {
        name: "alarmFrom",
        props:{
            formType:{
                type:String,
                default(){
                    return''
                }
            },
            groupId:{
                type:String,
                default(){
                    return''
                }
            },
            url:{
                type: String,
                default(){
                    return''
                }
            },
            defaultFrom:{
                type:Object
            },
            busNameObj:{
                type:Object,
                default(){
                    return{
                        busIndexName:'',
                        busFilterName:'',
                    }
                }
            }
        },
        data() {
            return {
                JSONData:{
                    "took" : 335,
                    "timed_out" : false,
                    "_shards" : {
                        "total" : 36,
                        "successful" : 36,
                        "skipped" : 0,
                        "failed" : 0
                    },
                    "hits" : {
                        "total" : {
                            "value" : 10000,
                            "relation" : "gte"
                        },
                        "max_score" : null,
                        "hits" : [ ]
                    },
                    "aggregations" : {
                        "ip" : {
                            "doc_count_error_upper_bound" : 0,
                            "sum_other_doc_count" : 0,
                            "buckets" : [
                                {
                                    "key" : "172.16.1.2",
                                    "doc_count" : 46742735
                                },
                                {
                                    "key" : "192.168.2.13",
                                    "doc_count" : 3154194
                                },
                                {
                                    "key" : "192.168.2.182",
                                    "doc_count" : 734129
                                },
                                {
                                    "key" : "192.168.2.81",
                                    "doc_count" : 248424
                                },
                                {
                                    "key" : "192.168.2.110",
                                    "doc_count" : 179444
                                },
                                {
                                    "key" : "192.168.2.222",
                                    "doc_count" : 38360
                                },
                                {
                                    "key" : "192.168.200.15",
                                    "doc_count" : 17942
                                },
                                {
                                    "key" : "192.168.2.1",
                                    "doc_count" : 15438
                                },
                                {
                                    "key" : "192.168.200.182",
                                    "doc_count" : 10518
                                },
                                {
                                    "key" : "192.168.56.1",
                                    "doc_count" : 4260
                                }
                            ]
                        }
                    }
                },
                loading: false,
                leftYLoading:false,
                leftXLoading:false,
                active:0,
                indexVal:[],
                form:{
                    alert_name:'',
                    templateName:'',
                    preIndexName:'',
                    suffixIndexName:'',
                    datefield:'',
                    alert_filters:'',
                },
                //参数
                paramObj:{},
                defaultFilter:[],
                //bucket
                xAxisArr:[{
                    aggregationType:'',//聚合类型
                    aggregationParam:'',//聚合字段
                    aggregationParamArr:[],//字段集合
                    timeInterval:'1',//时间间隔 聚合字段为时间时
                    timeType:'hourly',
                    xAxisName:'',//x轴名称
                    orderType:'desc',//排序方式
                    topSum:'5',//展示个数
                    numberRange:[ //数值类范围 集合
                        {start:'',end:''}
                    ],
                    dateRange:[//时间类范围 集合
                        {start:'',end:''}
                    ],
                    ipRange:[
                        {start:'',end:''}
                    ]
                }],
                //metric
                yAxisArr:[{
                    aggregationType:'',//聚合类型
                    aggregationParam:'',//聚合字段
                    aggregationParamArr:[],//字段集合
                    yAxisName:'',//y轴名称
                    legendName:'',//图例说明
                }],
                //metric聚合类型
                yAggregation: [
                    {
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
                    }
                ],
                //bucket聚合类型
                xAggregation: [
                    {
                        value: 'Terms',
                        label: 'Terms'
                    },
                    {
                        value: 'Range',
                        label: 'Range'
                    },{
                        value: 'Date Histogram',
                        label: 'Date Histogram'
                    },{
                        value: 'Date Range',
                        label: 'Date Range'
                    },{
                        value: 'IPv4 Range',
                        label: 'IPv4 Range'
                    }
                ],
                //x轴时间间隔
                timeIntervalArr:[
                    /*{
                        value: 'second',
                        label: '秒'
                    },*/
                    {
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
            }
        },
        created(){
            //数据源监听
            bus.$on(this.busNameObj.busIndexName,(arr)=>{
                //还原配置
                this.initialize();
                //设置数据源
                this.form.suffixIndexName = arr[2];
                this.form.preIndexName = arr[1];
                this.form.templateName = arr[0];
                this.form.datefield = arr[3];
                //获取column数据
                //this.getColumnField()
            })
            //监听过滤条件
            bus.$on(this.busNameObj.busFilterName,(str)=>{
                this.form.alert_filters = str;

            })
        },
        beforeDestroy(){
            bus.$off(this.busNameObj.busIndexName)
            bus.$off(this.busNameObj.busFilterName)
        },
        methods:{
            /*还原配置*/
            initialize(){
                this.form.alert_filters = '';
                this.yAxisArr =[{
                    aggregationType:'',//聚合类型
                    aggregationParam:'',//聚合字段
                    aggregationParamArr:[],//字段集合
                    yAxisName:'',//y轴名称
                    legendName:'',//图例说明
                }]
                this.xAxisArr=[{
                    aggregationType:'',//聚合类型
                    aggregationParam:'',//聚合字段
                    aggregationParamArr:[],//字段集合
                    timeInterval:'1',//时间间隔 聚合字段为时间时
                    timeType:'hourly',
                    xAxisName:'',//x轴名称
                    orderType:'desc',//排序方式
                    topSum:'5',//展示个数
                    numberRange:[ //数值类范围 集合
                        {start:'',end:''}
                    ],
                    dateRange:[//时间类范围 集合
                        {start:'',end:''}
                    ],
                    ipRange:[
                        {start:'',end:''}
                    ]
                }]
            },
            /*y轴聚合类型改变*/
            yAggregationChange($event,index){
                if (this.form.preIndexName === '' || this.form.suffixIndexName === '' || this.form.templateName === ''){
                    layer.msg('请选择数据源',{icon:'5'});
                    return;
                }
                //清空聚合字段
                this.yAxisArr[index].aggregationParam = ''
                this.yAxisArr[index].aggregationParamArr = [];
                //获取参数集合
                this.$nextTick(()=>{
                    this.leftYLoading = true;
                    this.$axios.post(this.$baseUrl+'/BI/getFieldByYAxisAggregation.do',this.$qs.stringify(
                        {
                            agg:$event,
                            pre_index_name:this.form.preIndexName,
                            suffix_index_name:this.form.suffixIndexName,
                            template_name:this.form.templateName
                        }
                    ))
                        .then(res=>{
                            this.leftYLoading = false;
                            res.data.forEach(item=>{
                                let obj = {
                                    value:item.fieldName,
                                    label:item.fieldName
                                }
                                this.yAxisArr[index].aggregationParamArr.push(obj)

                            })
                        })
                        .catch(err=>{
                            this.leftYLoading = false;

                        })
                })
            },
            /*x轴聚合类型改变*/
            xAggregationChange($event,index){
                if (this.form.preIndexName === '' || this.form.suffixIndexName === '' || this.form.templateName === ''){
                    layer.msg('请选择数据源',{icon:'5'});
                    return;
                }
                //清空聚合字段
                this.xAxisArr[index].aggregationParam = '';
                this.xAxisArr[index].aggregationParamArr = [];
                this.xAxisArr[index].ipRange=[{start:'',end:''}];
                this.xAxisArr[index].numberRange=[{start:'',end:''}];
                this.xAxisArr[index].dateRange=[{start:'',end:''}];

                this.$nextTick(()=>{
                    this.leftXLoading = true;
                    this.$axios.post(this.$baseUrl+'/BI/getFieldByXAxisAggregation.do',this.$qs.stringify({
                        agg:$event,
                        pre_index_name:this.form.preIndexName,
                        suffix_index_name:this.form.suffixIndexName,
                        template_name:this.form.templateName
                    }))
                        .then(res=>{
                            this.leftXLoading = false;
                            res.data.forEach(item=>{
                                let obj = {
                                    value:item.fieldName,
                                    label:item.fieldName
                                }
                                this.xAxisArr[index].aggregationParamArr.push(obj)
                            })
                        })
                        .catch(err=>{
                            this.leftXLoading = false;

                        })
                })
            },
            /*添加metric*/
            addMetric(){
                this.yAxisArr =[{
                    aggregationType:'',//聚合类型
                    aggregationParam:'',//聚合字段
                    aggregationParamArr:[],//字段集合
                    yAxisName:'',//y轴名称
                    legendName:'',//图例说明
                }]
            },
            /*添加bucket*/
            addBucket(){
                this.xAxisArr=[{
                    aggregationType:'',//聚合类型
                    aggregationParam:'',//聚合字段
                    aggregationParamArr:[],//字段集合
                    timeInterval:'1',//时间间隔 聚合字段为时间时
                    timeType:'hourly',
                    xAxisName:'',//x轴名称
                    orderType:'desc',//排序方式
                    topSum:'5',//展示个数
                    numberRange:[ //数值类范围 集合
                        {start:'',end:''}
                    ],
                    dateRange:[//时间类范围 集合
                        {start:'',end:''}
                    ],
                    ipRange:[
                        {start:'',end:''}
                    ]
                }]
            },
            /*获取字段*/
            getFields(){
                //构建metrics（y）参数 [{aggType:"count",field:"logdate"}]
                let metricsArr = [];
                for(let i in this.yAxisArr){
                    let obj = {};
                    obj.aggType = this.yAxisArr[i].aggregationType;
                    obj.aliasName = this.yAxisArr[i].yAxisName
                    //聚合类型为count的做特殊处理
                    if(obj.aggType === 'Count'){
                        obj.field = this.form.datefield
                    }else{
                        obj.field = this.yAxisArr[i].aggregationParam;
                    }

                    metricsArr.push(obj)
                }
                //构建buckets(x)参数
                let bucketsArr = [];
                for(let i in this.xAxisArr){
                    let obj = {};
                    obj.aggType = this.xAxisArr[i].aggregationType;
                    obj.field = this.xAxisArr[i].aggregationParam;
                    obj.size=this.xAxisArr[i].topSum;
                    obj.sort=this.xAxisArr[i].orderType;
                    obj.intervalType=this.xAxisArr[i].timeType;
                    obj.intervalValue=this.xAxisArr[i].timeInterval;
                    let numberRange = [];
                    let dateRange = [];
                    let ipRange = [];
                    for(let j in this.xAxisArr[i].numberRange){
                        numberRange.push({
                            from:this.xAxisArr[i].numberRange[j].start,
                            to:this.xAxisArr[i].numberRange[j].end
                        })
                    }
                    for(let j in this.xAxisArr[i].dateRange){
                        dateRange.push({
                            from:this.xAxisArr[i].dateRange[j].start,
                            to:this.xAxisArr[i].dateRange[j].end
                        })
                    }
                    for(let j in this.xAxisArr[i].ipRange){
                        ipRange.push({
                            from:this.xAxisArr[i].ipRange[j].start,
                            to:this.xAxisArr[i].ipRange[j].end
                        })
                    }
                    if(obj.aggType === 'Range'){
                        obj.ranges = numberRange
                    }else if(obj.aggType === 'IPv4 Range'){
                        obj.ranges = ipRange
                    }else if(obj.aggType === 'Date Range'){
                        obj.ranges = dateRange
                    }else{
                        obj.ranges = [];
                    }

                    bucketsArr.push(obj)
                }
                this.paramObj = JSON.parse(JSON.stringify(this.form));
                this.paramObj.alert_metricList = JSON.stringify(metricsArr)
                this.paramObj.alert_bucketList = JSON.stringify(bucketsArr)
                console.log( this.paramObj)
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/alert/getAlertResult.do',this.$qs.stringify(this.paramObj))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data
                            if (obj.success === 'true'){
                                this.active = 1;
                                this.JSONData = obj.data
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
        },
        components:{
            chooseIndex,
            queryFilter,
            jsonView
        }
    }
</script>

<style scoped>
    .con-wapper{
        height: 500px;
        padding: 20px;
        padding-top: 40px;
    }
    .form-con{
        height: 450px;
        overflow: auto;
        border: 1px dashed #3a4a5d;
        padding: 20px;
        padding-left: 50px;
    }
    .btn-wapper{
        width: 100%;
        margin-top: 5px;
    }
    .btn-wapper button{
        width: 100%;
        margin-top: 10px;
    }
    .form-wapper>div{
        position: relative;
    }
    .form-item{
        display: flex;
        margin-bottom: 20px;
        align-items: center;
        position: relative;
    }
    .item-label{
        color: #409eff;
        font-size: 16px;
        width: 56px;
        text-align: end;

    }
    .item-con{
        width: calc(100% - 60px);
    }
    .form-item /deep/ .chooseClass .el-input__inner{
        background: #303e4e;
    }
    .mustWrite{
        color: red;
        position: absolute;
        top: 2px;
        left: -2px;
    }
    .form-item /deep/ .el-collapse-item__header{
        height: 34px;
        background: #3a4a5d;
        border-top: 1px solid #409eff;
        color: #409eff;
        border-radius: 5px;
        position: relative;

    }
    .form-item /deep/ .el-form-item__label{
        color: #FFF!important;
        font-size: 12px;
    }
    .form-item /deep/ .el-collapse-item__wrap{
        /*background: 0;*/
        padding: 10px;
        border-radius: 5px;

    }
    .tablist{
        width: 320px;
    }
    .tablist .removeTab{
        position: absolute;
        right: 10px;
        top: 12px;
    }
    .tablist .removeTab:hover{
        color: #e4956d;
    }
    .tablist /deep/ .el-collapse-item__arrow{
        position: absolute;
        left: 10px;
        top: 12px;
    }
    .addWapper{
        height: 28px;
        font-size: 13px;
        line-height: 38px;
        padding-left: 10px;
    }
    .addWapper:hover {
        cursor: pointer;
        text-decoration: underline;
        color: #409eff;
    }
    .range-p {
        display: -webkit-box;
        display: -ms-flexbox;
        display: flex;
        -webkit-box-align: center;
        -ms-flex-align: center;
        align-items: center;
    }
    .range-btn-p{
        text-align: center;
        margin-top: 5px;
    }
    .range-btn-p span:hover{
        cursor: pointer;
        color: #409eff;
    }
    /deep/ .el-form-item--mini.el-form-item, .el-form-item--small.el-form-item {
        margin-bottom: 5px;
    }
</style>
