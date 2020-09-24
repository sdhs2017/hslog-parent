<template>
    <div  class="content-bg">
        <div class="top-title">添加资产组</div>
        <div class="form-wapper">
            <assetFrom url="/assetGroup/insert.do"></assetFrom>
        </div>
    </div>
</template>

<script>
    import assetFrom from '@/components/assetGroup/assetGroupFrom'
    export default {
        name: "addAssetGroup",
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
