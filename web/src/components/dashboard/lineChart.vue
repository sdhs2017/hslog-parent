<template>
    <div class="content-bg" >
        <chartBaseConfig chartType="line" :chartId="chartId" :operType="operType"></chartBaseConfig>
    </div>
    
</template>

<script>
    import chartBaseConfig from '../dashboard/chartBaseConfig'
    export default {
        name: "lineChart",
        data() {
            return {
                chartId:"",
                operType:""
            }
        },
        created(){
            //判断是否有参数  有参数说明是修改功能页面
            if(JSON.stringify(this.$route.query) !== "{}"  && this.$route.query.type === 'edit'){
                //修改此组件的name值
                this.$options.name = 'editLineChart'+ this.$route.query.id;
                this.operType = "edit"
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'editLineChart'+this.$route.query.id,
                    component:'dashboard/lineChart.vue',
                    title:'修改'
                }
                sessionStorage.setItem('/editLineChart'+this.$route.query.id,JSON.stringify(obj))
                if(this.chartId === '' || this.chartId !== this.$route.query.id){
                    this.chartId = this.$route.query.id;
                }
            }else if(this.$route.query.type === 'see'){//查看
                //修改此组件的name值
                this.$options.name = 'seeLineChart'+ this.$route.query.id;
                this.operType = "see"
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'seeLineChart'+this.$route.query.id,
                    component:'dashboard/lineChart.vue',
                    title:'查看'
                }
                sessionStorage.setItem('/seeLineChart'+this.$route.query.id,JSON.stringify(obj))
                if(this.chartId === '' || this.chartId !== this.$route.query.id){
                    this.chartId = this.$route.query.id;
                }
            }else{

            }

        },
        components:{
            chartBaseConfig
        }
    }
</script>

<style scoped>
    .top-zz{
        text-align: center;
        width: 100%;
        height: 50px;
        position: absolute;
        /*background: #;*/
        z-index: 100;
        cursor: no-drop;
        text-shadow: none;
        color: #455b75;
    }
    .from-zz{
        width: 242px;
        height:100%;
        cursor: no-drop;
        position: absolute;
        z-index: 100;
    }
    .choose-wapper{
        height: 50px;
        display: flex;
        align-items: center;
        float: left;
    }
    .saveChart{
        float: right;
        margin-right: 10px;
        margin-top: 10px;
    }
    .date-wapper{
        float: right;
        margin-right: 10px;
        margin-top: 10px;
        position: relative;
        z-index: 101;
    }
    .chart-wapper{
        height: 100%;
        overflow: hidden;
        padding-bottom: 10px;
    }
    .chart-wapper>div{
        float: left;
        background: #303e4e;
        height: calc(100vh - 237px);
    }
    .config-wapper{
        width: 300px;
        margin: 0 10px;
        position: relative;
    }
    .creatBtn{
        height: 39px;
        text-align: center;
        line-height: 39px;
        position: absolute;
        right: 0;
        top: 0;
        z-index: 1;
        /*background: #3a8ee6;*/
        font-size: 14px;
        padding: 0 8px;
        border-radius: 0;
    }
    .creatBtn:hover{
        cursor: pointer;
        background: #66b1ff;
    }
    .config-wapper /deep/ .el-tabs__content{
        height: calc(100% - 69px);
        overflow: auto;
    }
    .chart-wapper /deep/ .el-collapse-item__content{
        padding-bottom: 0;
    }
    .view-wapper{
        width: calc(100% - 330px);
        margin-right: 10px;
        position: relative;
    }
    .config-item{
        /*height:100*/
    }
    .config-wapper /deep/ .el-collapse-item__header{
        height: 35px;
        background: #455b75;
        border-top: 0;
        color: #409eff;
    }
    .config-wapper /deep/ .el-form-item__label{
        color: #FFF!important;
        font-size: 12px;
    }
    .config-wapper /deep/ .el-collapse-item__wrap{
        background: 0;
        padding: 10px;
    }
    .tablist{
        background: #3e4f63;
        margin: 10px 0;
    }
    .charts-title{
        height: 50px;
        text-align: center;
        line-height: 50px;
        color: #10d9f2;
        font-weight: 600;
        font-size: 19px;
    }
    #charts-wapper{
        height: calc(100% - 50px);
    }
    .tip-w{
        font-size: 10px;
        color: #e4956d;
    }
    .addY:hover{
        cursor: pointer;
        color: #10d9f2;
    }
    .tablist /deep/ .el-collapse-item__header{
        position: relative;
        padding-left: 30px;
    }
    .tablist /deep/ .el-collapse-item__arrow{
        position: absolute;
        left: 5px;
        top: 12px;
    }
    .tablist .removeTab{
        position: absolute;
        right: 10px;
        top: 12px;
    }
    .tablist .removeTab:hover{
        color: #e4956d;
    }
    .empty-tip{
        position: absolute;
        top: 50%;
        left: 50%;
        margin-left: -35px;
        margin-top: -10px;
        color: #6885a7;
    }
    .source-data{
        position: absolute;
        top: 10px;
        right: 10px;
    }
    .source-data button{
        background: 0;
        border: 0;
    }
    .jsonView{
        width: 100%;
        max-height: 80vh;
        overflow: auto;
    }
</style>
