<template>
    <div class="select-wapper">
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
        <div>
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
    
</template>

<script>
    import bus from '../common/bus';
    export default {
        name: "flowChartsFrom",
        props:{
            refreshBus:'',
            dataBus:''
        },
        data() {
            return {
                //刷新时间间隔
                refreshTimeVal:'10000',
                refreshOpt:[
                    {
                        label:'10s',
                        value:'10000'
                    },
                    {
                        label:'30s',
                        value:'30000'
                    }
                ],
                //数据日期间隔
                dataTimeVal:'1',
                dataOpt:[
                    {
                        label:'1小时',
                        value:'1'
                    },
                    {
                        label:'2小时',
                        value:'2'
                    },
                    {
                        label:'4小时',
                        value:'4'
                    },
                    {
                        label:'8小时',
                        value:'8'
                    },
                    {
                        label:'12小时',
                        value:'12'
                    },
                    {
                        label:'24小时',
                        value:'24'
                    }
                ]
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
            }
        }
    }
</script>

<style scoped>
    .select-wapper{
        display: flex;
    }
    .select-wapper>div{
        /*margin-left: 10px;*/
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
         top: -1px;
    }
</style>
