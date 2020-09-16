<template>
    <div class="content-bg">
        <tableBaseConfig chartType="table" :chartId="chartId" :operType="operType"></tableBaseConfig>
    </div>
</template>

<script>
    import tableBaseConfig from '../dashboard/tableBaseConfig'
    export default {
        name: "dynamicTable",
        data(){
            return{
                chartId:"",
                operType:""
            }
        },
        created(){
            //判断操作性质
            if(JSON.stringify(this.$route.query) !== "{}" && this.$route.query.type === 'edit'){//修改
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                this.$options.name = 'editDynamicTable'+ this.$route.query.id;
                //修改data参数
                this.operType = "edit"
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'editDynamicTable'+this.$route.query.id,
                    component:'dashboard/dynamicTable.vue',
                    title:'编辑'
                }
                sessionStorage.setItem('/editDynamicTable'+this.$route.query.id,JSON.stringify(obj))
                if(this.chartId === '' || this.chartId !== this.$route.query.id){
                    this.chartId = this.$route.query.id;
                }
            }else if(this.$route.query.type === 'see'){//查看
                //修改此组件的name值
                this.$options.name = 'seeDynamicTable'+ this.$route.query.id;
                //修改data参数
                this.operType = "see"
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'seeDynamicTable'+this.$route.query.id,
                    component:'dashboard/dynamicTable.vue',
                    title:'查看'
                }
                sessionStorage.setItem('/seeDynamicTable'+this.$route.query.id,JSON.stringify(obj))
                if(this.chartId === '' || this.chartId !== this.$route.query.id){
                    this.chartId = this.$route.query.id;
                }
            }else{//添加
                //时间范围监听事件
                /* bus.$on(this.busName,(obj)=>{
                     let arr = setChartParam(obj);
                     this.dateObj = arr[0];
                     this.intervalObj = arr[1];
                 })
                 //数据源监听
                 bus.$on(this.busIndexName,(arr)=>{
                     //还原配置
                     this.initialize();
                     //设置数据源
                     this.chartsConfig.suffixIndexName = arr[2];
                     this.chartsConfig.preIndexName = arr[1];
                     this.chartsConfig.templateName = arr[0];
                 })*/
            }
        },
        components:{
            tableBaseConfig
        }
    }
</script>

<style scoped>

</style>
