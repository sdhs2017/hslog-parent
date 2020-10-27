<template>
    <div class="date-layout-wapper">
        <div class="date-icon">
            <el-popover v-model="visible">
                <!--<p>这是一段内容这是一段内容确定删除吗？</p>
                <div style="text-align: right; margin: 0">
                    <el-button size="mini" type="text" @click="visible = false">取消</el-button>
                    <el-button type="primary" size="mini" @click="visible = false">确定</el-button>
                </div>-->
                <div class="layout-wapper">
                    <div v-if="!params.dateBlock">
                        <div class="edit">
                            <p class="item-tit">快速选择</p>
                            <div class="item-con">
                                <div class="item-con-left">
                                    <el-select v-model="dateType" placeholder="请选择" size="mini">
                                        <el-option
                                            label="最近"
                                            value="最近">
                                        </el-option>
                                    </el-select>
                                </div>
                                <div class="item-con-right">
                                    <p style="display: flex;justify-content: center;">
                                        <el-input v-model="params.dateCount" placeholder="数值" style="width: 80px;" size="mini"></el-input>
                                        <el-select v-model="params.dateUnit" style="width: 80px;margin: 0 10px;" size="mini">
                                            <el-option
                                                v-for="item in dateUnitArr"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </p>
                                </div>
                            </div>
                        </div>
                        <div class="commonly">
                            <p class="item-tit">常用 (优先级高于‘快速选择’)</p>
                           <!-- <el-checkbox-group
                                v-model="params.commonlyVal"
                                :min="0"
                                :max="1">
                                <el-checkbox class="commonlyItem" v-for="(item,index) in commonlyArr" :label="item.value" :key="index">{{item.label}}</el-checkbox>
                            </el-checkbox-group>-->
                            <el-radio-group v-model="params.commonlyVal">
                                <el-radio class="commonlyItem" @click.native.prevent="clickitem(item.value)"  v-for="(item,index) in commonlyArr" :label="item.value" :key="index">{{item.label}}</el-radio>
                            </el-radio-group>
                        </div>
                        <div class="interval-wapper">
<!--                            <el-checkbox v-model="params.intervalState">轮询</el-checkbox>-->
                            <span  v-if="params.isIntervalBox">
                                 轮询
                                <el-switch
                                    v-model="params.intervalState"
                                    size="mini"
                                    active-color="#13ce66"
                                    inactive-color="#ff4949">
                                </el-switch>
                            </span>

                            <el-input v-if="params.intervalState" class="intervalVal" v-model="params.intervalVal" type="number" placeholder="数值" style="width: 80px;" size="mini" min="1"></el-input>
                            <el-select v-if="params.intervalState" v-model="params.intervalType" placeholder="请选择" size="mini" style="width: 80px;" >
                                <el-option label="秒" value="second"> </el-option>
                                <el-option label="分" value="minute"> </el-option>
                                <el-option label="时" value="hourly"> </el-option>
                            </el-select>
                        </div>
                    </div>
                    <div class="datetimeBox" v-else>
                        <div>
                            <p class="item-tit">开始日期</p>
                            <el-date-picker
                                v-model="params.starttime"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                type="datetime"
                                placeholder="选择日期时间">
                            </el-date-picker>
                        </div>
                        <div>
                            <p class="item-tit">结束日期</p>
                            <el-date-picker
                                v-model="params.endtime"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                type="datetime"
                                placeholder="选择日期时间">
                            </el-date-picker>
                        </div>
                    </div>
                    <div class="btn">
<!--                        <el-checkbox v-model="dateBlock">固定日期</el-checkbox>-->
                        <span class="timeType" v-if="params.changeState" @click="params.dateBlock = !params.dateBlock">{{!this.params.dateBlock ? '精确日期' : '返回'}}</span>
                        <el-button type="primary" size="mini" @click="confimBtn">确定</el-button>
                    </div>
                </div>
                <el-button slot="reference" class="popoverBtn" ><i class="el-icon-date"></i><i style="margin-left: 5px;" class="el-icon-arrow-down"></i></el-button>
            </el-popover>
        </div>
        <div class="date-val">{{dateViewVal}} <span style="float: right;color: #e49567;margin-right: 5px">{{intervalView}}</span></div>
<!--        <div class="date-change">{{intervalView}}</div>-->
    </div>
    
</template>

<script>
    import {dateFormat} from "../../../static/js/common";
    import bus from '../common/bus';
    export default {
        name: "dateLayout",
        props:{
            busName:{
                type:String
            },
            refresh:{
                type:Number
            },
            defaultVal:{
                type:Object
            }
        },
        data() {
            return {
                dateViewVal:'',
                intervalView:'',
                dateObj:'',
                visible:false,
                //选择的日期类型
                dateType:'最近',
                //日期单位集合
                dateUnitArr:[
                    /*{
                    value:'second',
                    label:'秒'
                    },*/{
                        value:'min',
                        label:'分钟'
                    },{
                        value:'hour',
                        label:'小时'
                    },{
                        value:'day',
                        label:'天'
                    }/*,{
                        value:'week',
                        label:'周'
                    },{
                        value:'month',
                        label:'月'
                    },{
                        value:'year',
                        label:'年'
                    }*/
                ],
                dateUnit:'min',
                dateCount:'15',
                //常用时间集合
                commonlyArr:[
                    {value:'1-daying',label:'今天'},
                    {value:'1-weeking',label:'这周'},
                    {value:'1-monthing',label:'本月'},
                    {value:'15-min',label:'最近15分钟'},
                    {value:'30-min',label:'最近半小时'},
                    {value:'1-hour',label:'最近1小时'},
                    {value:'24-hour',label:'最近24小时'},
                    {value:'7-day',label:'最近7天'},
                    {value:'30-day',label:'最近30天'},
                    {value:'90-day',label:'最近90天'},
                    // {value:'1-year',label:'最近1年'}
                ],
                //用于保存参数状态
                saveP:{},
                //参数
                params:{
                    //具体时间参数
                    lastVal:'',
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
                    intervalVal:'5',
                    //轮询参数类型
                    intervalType:'second',
                    //‘快速选择’功能参数类型
                    dateUnit:'min',
                    //‘快速选择’功能参数数值
                    dateCount:'15',
                    //‘常用’ 时间值
                    commonlyVal:'',
                    //是否可以切换精确日期
                    changeState:true
                }
            }
        },
        watch:{
            defaultVal:{
                handler() {
                    this.$nextTick( ()=> {
                        if(JSON.stringify(this.defaultVal) !== '{}'){
                            //设置默认值
                            this.params =  this.defaultVal;
                            //设置显示文字
                            this.setView()
                        }
                        this.saveP = JSON.stringify(this.params)
                    })
                },
                immediate: true,
                deep: true

            },
            //刷新
            'refresh'(){
                if (JSON.stringify(this.dateObj) !== '{}'){
                   //模拟点击时间控件 确定按钮
                    this.confimBtn()
                }
            },
            //时间控件 状态
            'visible'(){
                if(this.visible){//打开时间控件
                    //设置上一次选中的值集合
                    this.params = JSON.parse(this.saveP)
                }else{//关闭

                }
            },
            //轮询初始值
            'params.intervalState'(){
                //轮询滑块 开启
                if(this.params.intervalState){
                    //设置默认值
                    this.params.intervalVal = '5';
                    this.params.intervalType = 'second';
                }else{//关闭
                    //清空数值
                    this.params.intervalVal = '';
                    this.params.intervalType = '';
                }
            },
            //‘快速选择’ 时间数值
            'params.dateCount'(){
                //清空常用集合
                this.params.commonlyVal = ''
            },
            //‘快速选择’ 时间类型
            'params.dateUnit'(){
                //清空常用集合
                this.params.commonlyVal = ''
            }
        },
        created(){
            //默认15分钟
            //this.commonlyClick({value:'15-min',label:'最近15分钟'})
            //赋值 第一次
           /* if(JSON.stringify(this.defaultVal) !== '{}'){
                console.log(this.defaultVal)
                //设置默认值
                this.params =  this.defaultVal;
                //设置显示文字
                this.setView()
            }
            this.saveP = JSON.stringify(this.params)*/
        },
        methods:{
            /*确定按钮*/
            confimBtn(){
                //设置显示文字
                this.setView()
                //返回参数
                bus.$emit(this.busName,this.params);
                //将选中的时间信息保存
                this.saveP = JSON.stringify(this.params)
            },
            /*设置文字显示*/
            setView(){
                //判断时间参数类型
                if(this.params.dateBlock){//固定日期
                    this.params.lastVal = ''
                    //判断日期合法性
                    if(this.params.starttime === null){
                        this.params.starttime = ''
                    }else if(this.params.endtime === null){
                        this.params.endtime = ''
                    }
                    if(this.params.starttime === '' && this.params.endtime === ''){
                        layer.msg('请选择时间范围',{icon:5})
                    }else{
                        //轮询关闭
                        this.intervalView = '';
                        this.params.intervalState = false;
                        this.visible = false;
                        this.dateViewVal = this.params.starttime +' - ' + this.params.endtime;
                    }
                }else{//时间段（最近xx）
                    //清空固定日期
                    this.params.starttime = '';
                    this.params.endtime = '';
                    //判断是否开启轮询
                    if(this.params.intervalState){//开启
                        //轮询视图-文字说明
                        let type = ''
                        if(this.params.intervalType === 'second'){
                            type = '秒'
                        }else if(this.params.intervalType === 'minute'){
                            type = '分'
                        }else{
                            type = '时'
                        }
                        this.intervalView = `次/${this.params.intervalVal}${type}`
                    }else{//关闭
                        this.intervalView = '';
                    }
                    //判断时间选择的类型
                    if(this.params.commonlyVal === ''){//没有选择常用的的快捷键
                        let unit = '';
                        for (let i in this.dateUnitArr) {
                            let obj = this.dateUnitArr[i];
                            if(obj.value === this.params.dateUnit){
                                unit = obj.label
                                break;
                            }
                        }
                        this.visible = false;
                        this.dateViewVal = '最近'+this.params.dateCount+unit;
                        this.params.lastVal = this.params.dateCount+'-'+this.params.dateUnit
                    }else{ //选了常用的快捷方式
                        //遍历 取出中文释义
                        for(let i in this.commonlyArr){
                            if(this.commonlyArr[i].value === this.params.commonlyVal){
                                this.dateViewVal = this.commonlyArr[i].label;
                                this.visible = false;
                                break;
                            }
                        }
                        this.params.lastVal = this.params.commonlyVal;
                    }
                }
            },
            /*‘常用’ 时间选择点击事件*/
            clickitem (e) {
                //第一次点击选中，再次点击取消选中
                e === this.params.commonlyVal ? this.params.commonlyVal = '' : this.params.commonlyVal = e;
                //判断是否选中了值  没有则设置‘快速选择’的值为15min
                /*if(this.params.commonlyVal === ''){
                    this.params.dateCount = '15';
                    this.params.dateUnit = 'min'
                }else{
                    this.params.dateCount = '0';
                }*/
            },
            /*计算日期*/
            computeDate(count,dateUnit){
                let startTime = '',
                    endTime = '';
                //获取当前时间
                let currentDate = new Date();
                endTime = dateFormat('yyyy-mm-dd HH:MM:SS',currentDate);
                //当前月
                let currentMonth = (currentDate.getMonth()+1) < 10 ? '0'+(currentDate.getMonth()+1) : currentDate.getMonth()+1;
                //当天
                let currentDay = currentDate.getDate() < 10 ? '0'+currentDate.getDate() : currentDate.getDate();
                switch (dateUnit) {
                    //今天
                    case 'daying':
                        startTime = currentDate.getFullYear()+"-" + currentMonth + "-" + currentDay + ' 00:00:00';
                        break;
                    //这周
                    case 'weeking' :
                        var timesStamp = currentDate.getTime();
                        var cud = currentDate.getDay();
                        startTime = dateFormat('yyyy-mm-dd',new Date(timesStamp + 24 * 60 * 60 * 1000 * (0 - (cud + 6) % 7))) + ' 00:00:00';
                        break;
                    //本月
                    case 'monthing':
                        startTime = currentDate.getFullYear()+"-" + currentMonth + "-01"+ ' 00:00:00';
                        break;
                    //最近xx分钟
                    case 'min' :
                        startTime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date(currentDate.getTime() - count * 60 * 1000));
                        break;
                    //最近xx小时
                    case 'hour' :
                        startTime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date(currentDate.getTime() - count * 60 * 60 * 1000));
                        break;
                    //最近xx天
                    case 'day' :
                        startTime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date(currentDate.getTime() - count * 24 * 60 * 60 * 1000));
                        break;
                }
                return [startTime,endTime]
            }
        }
    }
</script>

<style scoped>
    .popoverBtn{
        background: #23568a;
        border: 0;
        color: #fff;
        position: relative;
        top: -2px;
        border-radius: 0;
        height: 26px;
    }
    .popoverBtn:hover{
        background: #3c6c9c;
    }
    .date-layout-wapper{
        height: 26px;
        width: 347px;
        display: flex;
        justify-content: center;
        border: 1px solid #409eff;
        align-items: center;
        text-shadow: none;
        font-size: 16px;
    }
    .date-layout-wapper>div{
        height: 26px;
    }
    .date-icon{
        width: 60px;
        line-height: 30px;
        text-align: center;
    }
    .date-val{
        width: calc(100% - 62px);
        text-shadow: none;
        line-height: 26px;
        font-size:12px;
        color: #409eff;
        padding-left: 15px;
    }
    .date-change{
        width: 60px;
        font-size: 10px;
        line-height: 26px;
    }
    .layout-wapper{
        /*padding: 10px;*/
        width: 320px;
        height: 360px;
    }
    .layout-wapper .commonly{
        border-bottom: 1px dashed #35526f;
    }
    .layout-wapper .edit{
        height: 90px;
        border-bottom: 1px dashed #35526f;
    }
    .layout-wapper .interval-wapper{
        height: 46px;
        line-height: 46px;
        border-bottom: 1px solid #4a729e;
        display: flex;
        justify-content: space-between;
        padding: 0 15px;
    }
    .layout-wapper .commonly{
        height: 180px;
    }
    .layout-wapper .btn{
        height: 50px;
        /*display: flex;
        justify-content: space-between;
        align-items: center;*/
        padding: 0 15px;
    }
    .layout-wapper .btn /deep/ .el-button{
        float: right;
        margin-top: 10px;
    }
    .layout-wapper .btn .timeType{
        margin-top: 14px;
        float: left;
    }
    .layout-wapper .item-tit{
        height: 40px;
        line-height: 40px;
        font-size: 12px;
        font-weight: 600;
        color: #e4956d;
    }
    .layout-wapper .item-con{
        padding: 0 10px;
        display: flex;
        justify-content: space-between;
    }
    .layout-wapper .edit .item-con-left{
        width: 70px;
        height: 30px;
    }
    .layout-wapper .edit .item-con-right{
        width: calc(100% - 80px);
    }
    .item-con-ul{
        display: flex;
        justify-content: space-around;
        flex-wrap: wrap;
        color: #F5F5F5;
    }
    .commonlyItem{
        width: 34%;
        height: 26px;
        line-height: 26px;
        margin-left: 16px;
    }
    .commonlyItem:hover{
        cursor: pointer;
        color: #409eff;
        text-decoration: underline;
    }
    .item-con-ul .currentItem{
        color: #409eff;
        text-decoration: underline;
    }
    .datetimeBox{
        height: 311px;
        border-bottom: 1px solid #4a729e;
    }
    .timeType:hover{
        cursor: pointer;
        text-decoration: underline;
    }
    .intervalVal /deep/ .el-input__inner{
        padding-right: 2px;
    }
    .layout-wapper /deep/ .el-date-picker__time-header{
        border-bottom: 1px solid #409eff;
    }
</style>
