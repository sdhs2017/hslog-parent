<template>
    <div class="content-bg">
        <div class="top-title">修改 '{{equipmentIp}}' 资产</div>
        <div class="equipment-path"></div>
        <div class="form-wapper">
            <v-equipment-form :busName="busName" :formData="formData" :routerUrl="routerUrl"></v-equipment-form>
        </div>
    </div>
</template>

<script>
    import bus from '../common/bus';
    import vEquipmentForm from '@/components/equipment/equipmentForm'
    export default {
        name: "updateEquipment",
        data(){
            return{
                busName:'',
                routerUrl:'/equipmentScan',
                formData:{},
                equipmentIp:'',
                equipmentId:''
            }
        },
        created(){

        },
        beforeDestroy(){
            bus.$off(this.busName)
        },
        methods:{
            //添加资产
            updateEquipment(params){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/assets/updateById.do',this.$qs.stringify(params))
                        .then((res)=>{
                            if(res.data.success == 'true'){
                                layer.msg("修改成功",{icon:1});
                                this.$router.push({path:'/equipmentScan'})
                            }else{
                                layer.msg("修改失败",{icon:1});
                            }
                        })
                        .catch((err)=>{

                        })
                })
            },
            /*获取资产信息*/
            getEquipmentData(){
                layer.load(1)
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/assets/selectOneAssets.do',this.$qs.stringify({id:this.equipmentId}))
                        .then((res)=>{
                            layer.closeAll();
                            this.formData = res.data;
                        })
                        .catch((err)=>{
                            layer.closeAll();
                        })
                })
            }
        },
        watch:{
            '$route'(to,form){
                //console.log(to)
            },
            'equipmentId'(newV,oldV){
                //监听保存按钮
                bus.$on(this.busName,(params)=>{
                    for (let i in params){
                        if(params[i] == null){
                            params[i] = '';
                        }
                    }
                    this.updateEquipment(params)
                })
            }
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                vm.$options.name = 'updateEquipment'+ to.query.id;
                //修改data参数
                vm.equipmentIp = to.query.ip;
                vm.busName = 'updateEquipment'+to.query.id;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'updateEquipment'+to.query.id,
                    component:'equipment/updateEquipment.vue',
                    title:'资产修改'
                }
                sessionStorage.setItem('/updateEquipment'+to.query.id,JSON.stringify(obj))
                if(vm.equipmentId === '' || vm.equipmentId !== to.query.id){
                    vm.equipmentId = to.query.id;
                    vm.getEquipmentData();
                }

            })

        },
        components:{
            vEquipmentForm
        }
    }
</script>

<style scoped>
    .form-wapper{
        padding:10px 20px;
    }
</style>
