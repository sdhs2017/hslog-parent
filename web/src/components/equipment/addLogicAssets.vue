<template>
    <div class="content-bg">
        <div class="top-title">添加逻辑资产</div>
        <div class="equipment-path"></div>
        <div class="form-wapper" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
            <v-equipment-form :busName="busName" :routerUrl="routerUrl"></v-equipment-form>
        </div>
    </div>
</template>

<script>
    import bus from '../common/bus';
    import vEquipmentForm from '@/components/equipment/logicAssetsForm'
    export default {
        name: "addLogicAssets",
        data(){
            return{
                loading:false,
                routerUrl:'/equipment',
                busName:'addLogicAssets',
                formData:{}
            }
        },
        created(){
            //监听保存按钮
            bus.$on('addLogicAssets',(params)=>{
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
              this.loading = true;
              this.$nextTick(()=>{
                  this.$axios.post(this.$baseUrl+'/asset/insert.do',this.$qs.stringify(params))
                      .then((res)=>{
                          this.loading = false;
                          if(res.data.success == 'true'){
                              layer.msg("添加成功",{icon:1});
                              this.$router.push({path:'/logicAssetsList'})
                          }else{
                              layer.msg("添加失败",{icon:5});
                          }
                      })
                      .catch((err)=>{
                          console.log(err)
                          this.loading = false;
                      })
              })
          }
        },
        beforeDestroy(){
            bus.$off('addLogicAssets')
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
