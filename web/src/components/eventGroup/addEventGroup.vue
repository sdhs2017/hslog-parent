<template>
    <div  class="content-bg">
        <div class="top-title">添加事件组</div>
        <div class="form-wapper">
            <assetFrom url="/eventGroup/insert.do"></assetFrom>
        </div>
    </div>
</template>

<script>
    import assetFrom from '@/components/eventGroup/eventGroupFrom'
    export default {
        name: "addEventGroup",
        data(){
            return{

            }
        },
        components:{
            assetFrom
        }
    }
</script>

<style scoped>
    .form-wapper{
        width: 753px;
        margin: 50px auto;
        background: #303e4e;
        padding: 50px;
        padding-left: 20px;
    }
</style>
