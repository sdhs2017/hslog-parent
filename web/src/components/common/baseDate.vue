<template>
    <div>
        <el-date-picker
            v-if="type === 'daterange'"
            v-model="dateVal"
            type="daterange"
            align="right"
            size="mini"
            value-format="yyyy-MM-dd"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="changeDate"
            :picker-options="pickerOptions">
        </el-date-picker>
        <span v-if="type === 'datetimerange'">日期范围：</span>
        <el-date-picker
            v-if="type === 'datetimerange'"
            v-model="dateVal2"
            type="datetimerange"
            range-separator="至"
            size="mini"
            start-placeholder="开始日期"
            value-format="yyyy-MM-dd HH:mm:ss"
            :default-time="['00:00:00', '23:59:59']"
            @change="timeChange"
            end-placeholder="结束日期"
            :picker-options="pickerOptions">
        </el-date-picker>
    </div>
    
</template>

<script>
    import bus from '../common/bus';
    export default {
        name: "baseDate",
        props:{
            type:{
              type:String
            },
            busName:{
                type:String
            },
            defaultVal:{
                type:Array
            },
        },
        data() {
            return {
                dateVal:[],
                dateVal2:[],
                pickerOptions: {
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
                    }, {
                        text: '全部',
                        onClick(picker) {
                            picker.$emit('pick', ['', '']);
                        }
                    }]
                }
            }
        },
        mounted(){
            //this.dateVal = this.defaultVal;
            let newDate = new Date();
            let curDate = newDate.toLocaleDateString();
            let curYear = newDate.getFullYear();
            let curMonth = newDate.getMonth() + 1;
            let curDay = newDate.getDate();
            let curHour = newDate.getHours();
            let curMin = newDate.getMinutes();
            let curSec = newDate.getSeconds();
            if(curMonth < 10){
                curMonth = '0'+curMonth
            }
            if(curDay < 10){
                curDay = '0' + curDay;
            }
            if(curHour < 10){
                curHour = '0' + curHour;
            }
            if(curMin < 10){
                curMin = '0' + curMin;
            }
            if(curSec < 10){
                curSec = '0' + curSec;
            }
            let startTime = curYear+'-'+curMonth+'-01';
            let endTime = curYear+'-'+curMonth+'-'+curDay+' '+curHour+':'+curMin+':'+curSec;
            if(this.type ==='daterange'){
                this.dateVal = [startTime,endTime];
            }else if(this.type === 'datetimerange'){
                this.dateVal2 = [startTime+' 00:00:00',endTime];
            }

        },
        watch:{
            'dateVal'(){
                bus.$emit(this.busName,this.dateVal)
            },
            'dateVal2'(){
                bus.$emit(this.busName,this.dateVal2)
            }
        },
        methods:{
            changeDate(){},
            timeChange(){},
        }
    }
</script>

<style scoped>
    span{
        color: #409eff;
        text-shadow: 0;
        text-shadow: initial;
    }
</style>
