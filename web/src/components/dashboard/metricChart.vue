<template>
    <div class="content-bg" >
        <chartBaseConfig chartType="metric" :chartId="chartId" :operType="operType"></chartBaseConfig>
    </div>

</template>

<script>
    import chartBaseConfig from '../dashboard/chartBaseConfig'
    export default {
        name: "metricChart",
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
                this.$options.name = 'editMetricChart'+ this.$route.query.id;
                this.operType = "edit"
                if(this.chartId === '' || this.chartId !== this.$route.query.id){
                    this.chartId = this.$route.query.id;
                }
            }else if(this.$route.query.type === 'see'){//查看
                //修改此组件的name值
                this.$options.name = 'seeMetricChart'+ this.$route.query.id;
                this.operType = "see"
                if(this.chartId === '' || this.chartId !== this.$route.query.id){
                    this.chartId = this.$route.query.id;
                }
            }else{

            }

        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                //判断是否有参数  有参数说明是修改功能页面
                if(JSON.stringify(to.query) !== "{}"  && to.query.type === 'edit'){
                    //将路由存放在本地 用来刷新页面时添加路由
                    let obj = {
                        path:'editMetricChart'+to.query.id,
                        component:'dashboard/metricChart.vue',
                        title:'修改'
                    }
                    sessionStorage.setItem('/editMetricChart'+to.query.id,JSON.stringify(obj))
                }else if(to.query.type === 'see'){//查看
                    //将路由存放在本地 用来刷新页面时添加路由
                    let obj = {
                        path:'seeMetricChart'+to.query.id,
                        component:'dashboard/metricChart.vue',
                        title:'查看'
                    }
                    sessionStorage.setItem('/seeMetricChart'+to.query.id,JSON.stringify(obj))
                }

            })
        },
        components:{
            chartBaseConfig
        }
    }
</script>

<style scoped>
    .chart-wapper>div{
        float: left;
        background: #303e4e;
        height: calc(100vh - 237px);
    }
    .config-wapper /deep/ .el-tabs__content{
        height: calc(100% - 69px);
        overflow: auto;
    }
    .chart-wapper /deep/ .el-collapse-item__content{
        padding-bottom: 0;
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

</style>
