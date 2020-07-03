<template>
    <div>
        <div class="time-wapper">{{date}} {{hour}}:{{min}}:{{sec}}</div>
        <div class="item-wapper" style="margin-top: 0;height:auto;">
            <div class="item-title">统计</div>
            <ul class="item-content" style="padding: 0.45rem 0.8rem; height: 105px;">
                <li>
                    <div class="li-tit">日志数：</div>
                    <div class="li-val">{{allLogsTotle}}</div>
                </li>
                <li>
                    <div class="li-tit">error数：</div>
                    <div class="li-val" style="color: #e4956d;">{{errorLogsTotle}}</div>
                </li>
            </ul>
        </div>
        <div class="item-wapper">
            <div class="item-title">{{date.split(' ')[0]}} 数据量统计-折线图</div>
            <div class="item-content">
                <hourlyLogCount_line :params="lineParam" :baseConProp="{title:''}"></hourlyLogCount_line>
            </div>
        </div>
        <div class="item-wapper">
            <div class="item-title">日志级别数量统计
                <el-select v-model="timeVal" placeholder="请选择" size="mini" @change="timeChange" class="select-list">
                    <el-option
                        v-for="item in timeArr"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                    </el-option>
                </el-select>
            </div>
            <div class="item-content">
                <logLevel_bar  :params="barPieParam" :baseConProp="{title:''}"></logLevel_bar>
            </div>
            <div class="item-content">
                <logLevel_pie  :params="barPieParam" :baseConProp="{title:''}"></logLevel_pie>
            </div>
        </div>
    </div>
</template>

<script>
    import {dateFormat} from "../../../../static/js/common";
    import logLevel_bar from '../../charts/log/index/logLevel_bar'
    import logLevel_pie from '../../charts/log/index/logLevel_pie'
    import hourlyLogCount_line from '../../charts/log/index/hourlyLogCount_line'
    const MONTHNAME = [ "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" ];
    const DAYNAME = ["Sun.","Mon.","Tues.","Wed.","Thur.","Fri.","Sat."];
    const HOUR = ["00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"];
    export default {
        name: "index_m",
        data() {
            return {
                loading: false,
                date: '',//日期
                hour: '',//时
                min: '',//分
                sec: '',//秒
                //日志级别参数
                barPieParam:{
                    intervalValue:'',
                    intervalType:'',
                    starttime:'',
                    endtime:'',
                    last:'7-day'
                },
                //今日每小时日志数
                lineParam:{
                    intervalValue:'',
                    intervalType:'',
                    starttime:'',
                    endtime:'',
                    last:''
                },
                allLogsTotle:0,//日志总数
                errorLogsTotle:0,//error日志数
                timeVal:'7-day',
                timeArr:[{
                    value: '7-day',
                    label: '最近7天'
                }, {
                    value: '30-day',
                    label: '最近30天'
                }, {
                    value: '90-day',
                    label: '最近90天'
                }, {
                    value: '',
                    label: '全部'
                }],
            }
        },
        created() {
            //循环创建时间 模拟时间
            setInterval(this.setDate,1000);
            //获取日志条数
            this.getLogsTotle();
            let end = new Date();
            this.lineParam.starttime= dateFormat('yyyy-mm-dd',end)+ ' 00:00:00';
            this.lineParam.endtime = dateFormat('yyyy-mm-dd',end)+ ' 23:59:59';
            //判断是否为手机端
            var ua = navigator.userAgent;
            var ipad = ua.match(/(iPad).*OS\s([\d_]+)/),
                isIphone = !ipad && ua.match(/(iPhone\sOS)\s([\d_]+)/),
                isAndroid = ua.match(/(Android)\s+([\d.]+)/),
                isMobile = isIphone || isAndroid;
            if(!isMobile) {
                this.$router.push('/index_n');
            }
        },
        methods:{
            //时间
            setDate(){
                // 创建新的日期函数
                const newDate = new Date();
                // 设置日期
                newDate.setDate(newDate.getDate());
                this.date = newDate.getFullYear() + "/" + MONTHNAME[newDate.getMonth()] + '/' +  newDate.getDate() + ' ' +DAYNAME[newDate.getDay()];
                //设置小时
                const hours = new Date().getHours();
                this.hour = ( hours < 10 ? "0" : "" ) + hours;
                // 设置分钟
                const minutes = new Date().getMinutes();
                this.min = ( minutes < 10 ? "0" : "" ) + minutes;
                // 设置秒
                const seconds = new Date().getSeconds();
                this.sec =( seconds < 10 ? "0" : "" ) + seconds;
            },
            /*获取日志条数*/
            getLogsTotle(){
                this.$axios.post(this.$baseUrl+'/ecsCommon/getIndicesCount.do',{})
                    .then(res=>{
                        this.allLogsTotle = parseInt(res.data[0].indices).toLocaleString();
                        this.errorLogsTotle = parseInt(res.data[0].indiceserror).toLocaleString();
                    })
                    .catch(err=>{
                        console.log(err)
                    })
            },
            /*日期改变*/
            timeChange(){
                //改变参数
                let obj = {
                    intervalValue:'',
                    intervalType:'',
                    starttime:'',
                    endtime:'',
                    last:this.timeVal
                }
                this.barPieParam = obj
            }
        },
        components:{
            logLevel_bar,
            logLevel_pie,
            hourlyLogCount_line
        }
    }
</script>

<style scoped>
    .time-wapper{
        height: 2rem;
        line-height: 2rem;
        text-align: center;
        font-size: 1.3rem;
        font-family: 'electronicFont';
        background-image: linear-gradient(to right, #5bc0de 40%, #75EF99 73%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
    }
    .item-wapper{
        height: auto;
        /*background: rgb(26,36,47);*/
        background: url("../../../../static/img/panel-l-t.png");
        background-size: 100% 100%;
        margin:0.5rem 0.5rem;
    }
    .item-title{
        height: 2.5rem;
        line-height: 2.5rem;
        font-size: 0.8rem;
        text-align: left;
        padding-left: 25px;
        background: url("../../../../static/img/title-bg.png");
        background-size: 100% 100%;
    }
    .item-content{
        /*height: calc(100% - 1rem);*/
        height: 230px;
        box-sizing: border-box;
        padding: 0.5rem;

    }
    ul.item-content{
        /*padding: 0;*/
        margin: 0;
    }
    .item-content li{
        display: flex;
        height: 2.5rem;
        align-items: center;
        font-size: 1rem;
        border-bottom: 1px solid #0d1c55;
    }
    .item-content li .li-tit{
        width: 30%;
        text-align: end;
        color: #a3b9ec;
        font-weight: 600;
    }
    .item-content li .li-val{
        flex: 1;
        padding-left: 20px;
        font-family: 'electronicFont';
        font-size: 1.5rem;
        color:#5bc0de;
    }
    .select-list{
        width: 100px;
        float: right;
        margin-right: 5px;
    }
    /deep/ .el-input__inner{
        -webkit-border-radius: 0!important;
        -moz-border-radius: 0!important;
        border-radius:0!important;
        position: relative;
        background: 0;
        border: 1px solid #409eff;
        color: #409eff;
    }
</style>
