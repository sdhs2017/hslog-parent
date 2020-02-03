<template>
    <div class="select-wapper">
        <div  class="form-item form-item1" :style="{top:this.item1Top}">
            <div>
                <span class="select-lable">刷新间隔：</span>
                <el-select v-model="refreshTimeVal" @change = "change1" placeholder="请选择" size="mini" class="select-list">
                    <el-option
                        v-for="item in refreshOpt"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                    </el-option>
                </el-select>
            </div>
            <div v-if="this.refreshObj.defaultVal == '' ">
                <span class="select-lable">数据时间间隔：</span>
                <el-select v-model="dataTimeVal" @change = "change2" placeholder="请选择" size="mini" class="select-list">
                    <el-option
                        v-for="item in dataOpt"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                    </el-option>
                </el-select>
            </div>
        </div>
        <div class="form-item form-item2" :style="{top:this.item2Top}">
            <div style="width: 384px;">
                <span class="select-lable">日期范围</span>
                <el-date-picker
                    v-model="dateArr"
                    type="datetimerange"
                    range-separator="至"
                    size="mini"
                    start-placeholder="开始日期"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    :default-time="['00:00:00', '23:59:59']"
                    @change="timeChange"
                    end-placeholder="结束日期">
                </el-date-picker>
            </div>
        </div>
        <div v-if="this.timeBus !== undefined" class="switch-wapper">
            轮询：
            <el-switch
                v-model="switchVal"
                @change="switchChange"
                active-color="#13ce66"
                inactive-color="#ff4949">
            </el-switch>
        </div>
    </div>
    
</template>

<script>
    import bus from '../common/bus';
    export default {
        name: "flowChartsFrom",
        props:{
            refreshBus:'',//刷新bus名称
            dataBus:'',//数据日期间隔bus名称
            switchBus:'',//switch状态改变 bus名称
            timeBus:'',//日期 bus名称
            refreshObj:{ //刷新间隔数据列表
                type:Object,
                default(){
                    return{
                        defaultVal:'',
                        opt:[]
                    }
                }
            }
        },
        data() {
            return {
                item1Top:'-6px',
                item2Top:'50px',
                dateArr:[],
                switchVal:true,
                //刷新时间间隔
                refreshTimeVal:'5000',
                refreshOpt:[
                    {
                        label:'5s',
                        value:'5000'
                    },
                    {
                        label:'10s',
                        value:'10000'
                    },
                    {
                        label:'30s',
                        value:'30000'
                    },
                    {
                        label:'60s',
                        value:'60000'
                    }
                ],
                //数据日期间隔 value-秒
                dataTimeVal:'3600',
                dataOpt:[
                    {
                        label:'1小时',
                        value:'3600'
                    },
                    {
                        label:'2小时',
                        value:'7200'
                    },
                    {
                        label:'4小时',
                        value:'14400'
                    },
                    {
                        label:'8小时',
                        value:'28800'
                    },
                    {
                        label:'12小时',
                        value:'43200'
                    },
                    {
                        label:'24小时',
                        value:'86400'
                    }
                ]
            }
        },
        created(){
            if (this.refreshObj.defaultVal !== ''){
                this.refreshTimeVal = this.refreshObj.defaultVal;
                this.refreshOpt = this.refreshObj.opt;
            }
        },
        methods:{
            /*刷新时间间隔改变事件*/
            change1(){
                bus.$emit(this.refreshBus,this.refreshTimeVal)
            },
            /*数据日期间隔改变事件*/
            change2(){
                bus.$emit(this.dataBus,this.dataTimeVal)
            },
            /*时间改变*/
            timeChange(){
                //判断数据合法性
                if(this.dateArr){
                    bus.$emit(this.timeBus,{
                        switchVal:this.switchVal,
                        dateArr:this.dateArr
                    })
                }

            },
            /*switch状态改变*/
            switchChange(){
                if(!this.switchVal){
                    this.dateArr = [];
                    this.item1Top = '-50px';
                    this.item2Top = '6px';
                }else{
                    this.item1Top = '-6px';
                    this.item2Top = '50px';
                }
                bus.$emit(this.switchBus,{
                    switchVal:this.switchVal,
                    dateArr:this.dateArr
                })
            }
        }
    }
</script>

<style scoped>
    .select-wapper{
        display: flex;
        position: relative;
        overflow: hidden;
        width: 498px;
        height: 45px;
    }
    .select-wapper>div{
        /*margin-left: 10px;*/
        display: flex;
        align-items: center;
        margin-right: 10px;
    }
    .select-wapper .form-item{
        position: absolute;
        top: -6px;
        transition: all 0.2s linear;
        right: 110px;
    }
    .select-wapper .form-item2{
        top: 50px;
    }
    .switch-wapper{
        position: absolute;
        right: 0px;
        top: -6px;
    }
    .select-wapper>div>div{
        display: flex;
        align-items: center;
    }
    .select-lable{
        font-weight: 100!important;
        /*font-size: 16px;*/
        display: inline-block;
        font-size: 12px;
        height: 28px;
        background: #409eff;
        line-height: 28px;
        padding: 0 5px;
        color: #fff;
        word-break: keep-all;
    }
    .select-list{
        width: 100px;
    }
     /deep/ .el-input__inner{
        -webkit-border-radius: 0!important;
        -moz-border-radius: 0!important;
        border-radius:0!important;
         position: relative;
         /*top: -1px;*/
    }
</style>
