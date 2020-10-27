<template>
    <div  class="content-bg">
        <div class="top-title">修改 {{this.alarmName}}</div>
        <div class="form-wapper">
            <alarmFrom :alarmId="alarmId" :defaultFrom="alarmInfo" url="/alert/update.do" :busNameObj="busName"></alarmFrom>
        </div>
    </div>
</template>

<script>
    import alarmFrom from '@/components/alarmManage/alarmFrom'
    export default {
        name: "editEventGroup",
        data(){
            return{
                alarmName:'',
                alarmId:'',
                alarmInfo:{},
                busName:{
                    busIndexName:'',
                    busFilterName:'',
                    busDateName:''
                }
            }
        },
        methods:{
            /*获取资产组信息*/
            getAlarmInfo(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/alert/getAlertInfoById.do',this.$qs.stringify({alert_id:this.alarmId}))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success === 'true'){
                                this.alarmInfo = obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.loading = false;
                        })
                })
            }
        },
        created(){
            this.busName.busIndexName = 'editAlarmIndex'+this.$route.query.id;
            this.busName.busFilterName = 'editAlarmFilter'+this.$route.query.id;
            this.busName.busDateName = 'editAlarmDate'+this.$route.query.id;
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                vm.$options.name = 'editAlarm'+ to.query.id;
                //修改data参数
                vm.alarmName = to.query.name;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'editAlarm'+to.query.id,
                    component:'alarmManage/editAlarm.vue',
                    title:'告警修改'
                }
                sessionStorage.setItem('/editAlarm'+to.query.id,JSON.stringify(obj))
                if(vm.alarmId === '' || vm.alarmId !== to.query.id){
                    vm.alarmId = to.query.id;
                    vm.getAlarmInfo();
                }

            })

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
