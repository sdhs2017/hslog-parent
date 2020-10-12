<template>
    <div  class="content-bg">
        <div class="top-title">修改 {{this.groupName}}</div>
        <div class="form-wapper">
            <assetFrom :groupId="groupId" :defaultFrom="groupInfo" url="/eventGroup/update.do"></assetFrom>
        </div>
    </div>
</template>

<script>
    import assetFrom from '@/components/eventGroup/eventGroupFrom'
    export default {
        name: "editEventGroup",
        data(){
            return{
                groupName:'',
                groupId:'',
                groupInfo:{}
            }
        },
        methods:{
          /*获取资产组信息*/
          getAssetGroup(){
              this.$nextTick(()=>{
                  this.loading = true;
                  this.$axios.post(this.$baseUrl+'/eventGroup/getEventGroupInfoById.do',this.$qs.stringify({event_group_id:this.groupId}))
                      .then(res=>{
                          this.loading = false;
                          let obj = res.data;
                          if(obj.success === 'true'){
                              this.groupInfo = obj.data;
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
        beforeRouteEnter(to, from, next) {
            next (vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                vm.$options.name = 'editAssetGroup'+ to.query.id;
                //修改data参数
                vm.groupName = to.query.name;
                vm.busName = 'editAssetGroup'+to.query.id;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'editAssetGroup'+to.query.id,
                    component:'assetGroup/editAssetGroup.vue',
                    title:'资产组修改'
                }
                sessionStorage.setItem('/editAssetGroup'+to.query.id,JSON.stringify(obj))
                if(vm.groupId === '' || vm.groupId !== to.query.id){
                    vm.groupId = to.query.id;
                    vm.getAssetGroup();
                }

            })

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
