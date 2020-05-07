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
                    <div v-if="!dateBlock">
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
                                        <el-input v-model="dateCount" placeholder="数值" style="width: 80px;" size="mini"></el-input>
                                        <el-select v-model="dateUnit" style="width: 80px;margin: 0 10px;" size="mini">
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
                            <p class="item-tit">常用</p>
                            <ul class="item-con-ul">
                                <li @click="commonlyClick(item)" v-for="(item,index) in commonlyArr" :key="index">{{item.label}}</li>
                            </ul>
                        </div>
                    </div>
                    <div class="datetimeBox" v-else>
                        <div>
                            <p class="item-tit">开始日期</p>
                            <el-date-picker
                                v-model="starttime"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                type="datetime"
                                placeholder="选择日期时间">
                            </el-date-picker>
                        </div>
                        <div>
                            <p class="item-tit">结束日期</p>
                            <el-date-picker
                                v-model="endtime"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                type="datetime"
                                placeholder="选择日期时间">
                            </el-date-picker>
                        </div>
                    </div>
                    <div class="btn">
                        <el-checkbox v-model="dateBlock">固定日期</el-checkbox>
                        <el-button type="primary" size="mini" @click="confimBtn">确定</el-button>
                    </div>
                </div>
                <el-button slot="reference" class="popoverBtn" ><i class="el-icon-date"></i><i style="margin-left: 5px;" class="el-icon-arrow-down"></i></el-button>
            </el-popover>
        </div>
        <div class="date-val">{{dateViewVal}}</div>
        <div class="date-change"></div>
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
                        label:'分'
                    },{
                        value:'hour',
                        label:'时'
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
                dateBlock:false,
                starttime:'',
                endtime:'',
                dateArr:[]
            }
        },
        watch:{
            dateArr:{
                handler() {
                    bus.$emit(this.busName,this.dateArr);
                },
                deep: true
            },
            'refresh'(){
                if (JSON.stringify(this.dateObj) !== '{}'){
                    this.commonlyClick(this.dateObj)
                }
            }
        },
        created(){
            //默认15分钟
            this.commonlyClick({value:'15-min',label:'最近15分钟'})
        },
        methods:{
            /*常用快捷键  点击*/
            commonlyClick(item){
                this.dateObj = item;
                this.dateViewVal = item.label;
                this.dateArr = this.computeDate(item.value.split('-')[0],item.value.split('-')[1]);
                this.visible = false;
            },
            /*确定按钮*/
            confimBtn(){
                //判断当前参数类型
                if(this.dateBlock){ //固定日期
                    if(this.starttime === null){
                        this.starttime = ''
                    }else if(this.endtime === null){
                        this.endtime = ''
                    }
                    if(this.starttime === '' && this.endtime === ''){

                    }else{
                        this.visible = false;
                        this.dateViewVal = this.starttime +' - ' + this.endtime;
                    }
                    this.dateArr = [this.starttime,this.endtime]
                    this.dateObj={}
                }else {//非固定日期
                    this.dateArr = this.computeDate(this.dateCount,this.dateUnit);
                    let unit = '';
                    for (let i in this.dateUnitArr) {
                        let obj = this.dateUnitArr[i];
                        if(obj.value === this.dateUnit){
                            unit = obj.label
                        }
                    }
                    this.dateViewVal = '最近'+this.dateCount+unit;
                    this.dateObj = {
                        value:`${this.dateCount}-${this.dateUnit}`,
                        label:this.dateViewVal
                    }
                    this.visible = false;
                }

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
        width: 400px;
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
        width: calc(100% - 120px);
        text-shadow: none;
        line-height: 26px;
        font-size:12px;
        color: #409eff;
        padding-left: 15px;
    }
    .date-change{
        width: 60px;
    }
    .layout-wapper{
        /*padding: 10px;*/
        width: 320px;
        height: 360px;
    }
    .layout-wapper .edit,.layout-wapper .commonly{
        border-bottom: 1px solid #4a729e;
    }
    .layout-wapper .edit{
        height: 100px;
    }
    .layout-wapper .commonly{
        height: 210px;
    }
    .layout-wapper .btn{
        height: 50px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 15px;
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
    .item-con-ul li{
        width: 40%;
        height: 30px;
        line-height: 30px;
    }
    .item-con-ul li:hover{
        cursor: pointer;
        color: #409eff;
        text-decoration: underline;
    }
    .datetimeBox{
        height: 311px;
        border-bottom: 1px solid #4a729e;
    }
</style>
