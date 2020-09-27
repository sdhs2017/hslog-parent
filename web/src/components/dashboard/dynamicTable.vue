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
                if(this.chartId === '' || this.chartId !== this.$route.query.id){
                    this.chartId = this.$route.query.id;
                }
            }else if(this.$route.query.type === 'see'){//查看
                //修改此组件的name值
                this.$options.name = 'seeDynamicTable'+ this.$route.query.id;
                //修改data参数
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
                        path:'editDynamicTable'+to.query.id,
                        component:'dashboard/dynamicTable.vue',
                        title:'修改'
                    }
                    sessionStorage.setItem('/editDynamicTable'+to.query.id,JSON.stringify(obj))
                }else if(to.query.type === 'see'){//查看
                    //将路由存放在本地 用来刷新页面时添加路由
                    let obj = {
                        path:'seeDynamicTable'+to.query.id,
                        component:'dashboard/dynamicTable.vue',
                        title:'查看'
                    }
                    sessionStorage.setItem('/seeDynamicTable'+to.query.id,JSON.stringify(obj))
                }

            })
        },
        components:{
            tableBaseConfig
        }
    }
</script>

<style scoped>

</style>
