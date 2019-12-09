<template>
    <div>
        <el-pagination background layout="prev, pager, next" :total="total"></el-pagination>
    </div>
</template>

<script>
    export default {
        name: "pagination",
        props:{
            total:{
                type:Number
            }
        }
    }
</script>

<style scoped>

</style>
