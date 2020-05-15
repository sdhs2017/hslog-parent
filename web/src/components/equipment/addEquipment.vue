<template>
    <div class="content-bg">
        <div class="top-title">添加资产</div>
        <div class="equipment-path"></div>
        <div class="form-wapper">
            <v-equipment-form :busName="busName" :routerUrl="routerUrl"></v-equipment-form>
        </div>
    </div>
</template>

<script>
    import bus from '../common/bus';
    import vEquipmentForm from '@/components/equipment/equipmentForm'
    export default {
        name: "addEquipment",
        data(){
            return{
                routerUrl:'/equipment',
                busName:'addEquipment',
                formData:{}
            }
        },
        created(){
            //监听保存按钮
            bus.$on('addEquipment',(params)=>{
                this.formData = params;
                this.addEquipment(this.formData);
                //对参数进行处理
                //日志级别
                /*for(let i in this.formData.log_level){
                    this.formData.log_level += this.formData.log_level[i] +','
                }*/
            })
        },
        methods:{
          //添加资产
          addEquipment(params){
              this.$nextTick(()=>{
                  this.$axios.post(this.$baseUrl+'/equipment/upsert.do',this.$qs.stringify(params))
                      .then((res)=>{
                          if(res.data.success == 'true'){
                              layer.msg(res.data.message,{icon:1});
                              this.$router.push({path:'/equipment'})
                          }else{
                              layer.msg(res.data.message,{icon:1});
                          }
                      })
                      .catch((err)=>{

                      })
              })
          }
        },
        beforeDestroy(){
            bus.$off('addEquipment')
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
