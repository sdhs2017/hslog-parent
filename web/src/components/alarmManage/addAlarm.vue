<template>
    <div  class="content-bg">
        <div class="top-title">添加告警</div>
        <div class="form-wapper">
            <alarmFrom url="/eventGroup/insert.do" :busNameObj="busName"></alarmFrom>
        </div>
    </div>
</template>

<script>
    import alarmFrom from '@/components/alarmManage/alarmFrom'
    export default {
        name: "addAlarm",
        data(){
            return{
                busName:{
                    busIndexName:'addAlarmIndexBus',
                    busFilterName:'addAlarmFilterBus',
                }
            }
        },
        components:{
            alarmFrom
        }
    }
</script>

<style scoped>
    .form-wapper{
        width: 700px;
        margin: 50px auto;
        background: #273544;
        padding: 50px;
    }
</style>
