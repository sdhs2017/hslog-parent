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
        components:{
            tableBaseConfig
        }
    }
</script>

<style scoped>

</style>
