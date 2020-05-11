<template>
    <div class="content-bg">
        <div class="top-title">修改 '{{equipmentName}}' 逻辑资产</div>
        <div class="equipment-path"></div>
        <div class="form-wapper">
            <v-equipment-form :busName="busName" :formData="formData" :routerUrl="routerUrl"></v-equipment-form>
        </div>
    </div>
</template>

<script>
    import bus from '../common/bus';
    import vEquipmentForm from '@/components/equipment/logicAssetsForm'
    export default {
        name: "reviseLogicAssets",
        data(){
            return{
                busName:'',
                routerUrl:'/equipment',
                formData:{},
                equipmentName:'',
                equipmentId:''
            }
        },
        created(){

        },
        beforeDestroy(){
            bus.$off(this.busName)
        },
        methods:{
            //修改资产
            reviseEquipment(params){
                params.id = this.equipmentId;
                 this.$nextTick(()=>{
                      this.$axios.post(this.$baseUrl+'/asset/update.do',this.$qs.stringify(params))
                          .then((res)=>{
                              if(res.data.success == 'true'){
                                  layer.msg("修改成功",{icon:1});
                                  this.$router.push({path:'/logicAssetsList'})
                              }else{
                                  layer.msg("修改失败",{icon:5});
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
                    this.$axios.post(this.$baseUrl+'/asset/selectAsset.do',this.$qs.stringify({id:this.equipmentId}))
                        .then((res)=>{
                            layer.closeAll();
                            this.formData = res.data[0];
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
                    this.reviseEquipment(params)
                })
            }
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                vm.$options.name = 'reviseLogicAssets'+ to.query.id;
                //修改data参数
                vm.equipmentName = to.query.name;
                vm.busName = 'reviseLogicAssets'+to.query.id;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'reviseLogicAssets'+to.query.id,
                    component:'equipment/reviseLogicAssets.vue',
                    title:'资产修改'
                }
                sessionStorage.setItem('/reviseLogicAssets'+to.query.id,JSON.stringify(obj))
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
