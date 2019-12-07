<template>
    <div class="content-bg">

    </div>
    
</template>

<script>
    export default {
        name: "RouteLoading",
        data() {
            return {}
        },
        created(){
            layer.load(1)
        },
        beforeDestroy(){
            layer.closeAll();
        }
    }
</script>

<style scoped>

</style>
