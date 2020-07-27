<template>
    <div v-loading.fullscreen.lock="loading" element-loading-text="获取中"  element-loading-background="rgba(0, 0, 0, 0.6)">
        <div class="title">仪表盘列表</div>
        <div class="dasboard-wapper">
            <ul>
                <li v-for="(item,i) in this.tableData" :key="i">
                    <span>{{item.title}}</span>
                    <el-button size="mini" type="primary" plain @click="goToDashboard(item)">查看</el-button>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
    export default {
        name: "dashboardList_m",
        data(){
            return{
                loading:false,
                tableData:[],
            }
        },
        created(){
            this.getData()
        },
        methods:{
            /*获取列表*/
            getData(){
               this.$nextTick(()=>{
                   this.loading = true;
                   this.$axios.post(this.$baseUrl+'/BI/getDashboards.do','')
                       .then(res=>{
                           this.loading = false;
                           let obj =res.data;
                           if (obj.success === 'true'){
                               this.tableData = obj.data;
                           } else {
                               layer.msg(res.data.message,{icon:5})
                           }
                       })
                       .catch(err=>{
                           this.loading = false;
                           layer.msg('获取数据失败',{icon:5})
                       })
               })
            },
            /*跳转页面*/
            goToDashboard(item){
                //jumpHtml('seeDashboard'+row.id,'dashboard/dashboard.vue',{name:row.title,id:row.id,type:'see'},' 查看');
                let sss = ('dashboard_m'+item.id).replace(/\//g,"&-")
                let newRouters = [{
                    path:'/mobile',
                    component: resolve => require(['../../../components/mobile/common/home'], resolve),
                    children:[
                        {
                            path:'/mobile/'+sss,
                            name:'dashboard_m'+item.id,
                            component: resolve => require(['@/components/mobile/dashboard/dashboard_m.vue'], resolve),
                            meta: { title: '统计' }
                        }
                    ]
                }]
                this.$router.addRoutes(newRouters);
                this.$router.push({path:'/mobile/'+sss,query: {name:item.title,id:item.id,type:'see'}})
            }
        }
    }
</script>

<style scoped>
    .title{
        font-size: 1.3rem;
        font-weight: 600;
        color: #185bff;
        padding: 10px;
        text-align: center;
        text-shadow: none;
    }
    .dasboard-wapper ul{
        margin: 10px;
        padding: 10px;
    }
    .dasboard-wapper ul li{
        height: 40px;
        line-height: 40px;
        font-size: 0.8rem;
        border-bottom: 1px dashed #333a7b;
        display: flex;
        align-items: center;
        justify-content: space-between;
    }
    li /deep/ .el-button--mini, .el-button--mini.is-round {
        padding: 5px 7px;
    }
</style>
