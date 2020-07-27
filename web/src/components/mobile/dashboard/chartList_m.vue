<template>
    <div v-loading.fullscreen.lock="loading" element-loading-text="获取中"  element-loading-background="rgba(0, 0, 0, 0.6)">
        <div class="title">图表列表</div>
        <ul class="chart-list">
            <li v-for="(item,i) in this.chartList" :key="i" class="chart-item">
                <p class="item-icon">
                    <i class="el-icon-s-data" v-if="item.type ==='bar'"></i>
                    <i class="el-icon-share" v-else-if="item.type ==='line'"></i>
                    <i class="el-icon-pie-chart" v-else-if="item.type ==='pie'"></i>
                    <i v-else style="font-style: italic;font-size: 27px;font-family: 'electronicFont';">123</i>
                </p>
                <p class="item-tit">{{item.title}}</p>
                <p class="item-btn"><el-button size="mini" plain type="primary" @click="goToChart(item)">查看</el-button></p>
            </li>
        </ul>
    </div>
</template>

<script>
    export default {
        name: "chartList_m",
        data(){
            return{
                loading:false,
                chartList:[]
            }
        },
        created() {
            this.getChartList();
        },
        methods:{
            /*获取图表列表*/
            getChartList(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/BI/getVisualizations.do','')
                        .then(res=>{
                            this.loading = false;
                            let obj =res.data;
                            if (obj.success === 'true'){
                                this.chartList = obj.data;
                            } else {
                                layer.msg(res.data.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*跳转页面*/
            goToChart(item){
                let sss = ('chartTem_m'+item.id).replace(/\//g,"&-")
                let newRouters = [{
                    path:'/mobile',
                    component: resolve => require(['../../../components/mobile/common/home'], resolve),
                    children:[
                        {
                            path:'/mobile/'+sss,
                            name:'chartTem_m'+item.id,
                            component: resolve => require(['@/components/mobile/dashboard/chartTem_m.vue'], resolve),
                            meta: { title: '统计' }
                        }
                    ]
                }]
                this.$router.addRoutes(newRouters);
                this.$router.push({path:'/mobile/'+sss,query: {name:item.title,id:item.id,type:item.type}})
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
    .chart-list{
        display: flex;
        justify-content: space-around;
        flex-wrap: wrap;
    }
    .chart-item{
        width: 30%;
        max-width: 120px;
        height: 150px;
        font-size: 0.3rem;
        margin-top: 5px;
        margin-bottom: 5px;
        position: relative;
        background: url("../../../../static/img/panel-l-t.png");
        background-size: 100% 100%;
        background-color: #0c1138;
    }
    .chart-item /deep/ .el-button--mini, .el-button--mini.is-round {
        padding: 5px 7px;
        background: none;
        border-color: #0f246b;
    }
    .chart-item p{
        text-align: center;
        padding:2px 5px;
    }
    .item-icon{
        font-size: 40px;
        height: 50px;
        line-height: 44px;
    }
    .item-icon i{
        border-radius: 100%;
        border: 1px solid #113395;
        padding: 8px;
        font-size: 30px;
        background: #0b0f34;
    }
    .item-tit{
        height: 50px;
        display: flex;
        justify-content: center;
        align-items: center;
        font-weight: 600;
        color: #6cc6f3;
    }
    .item-btn{
        height: 36px;
        line-height: 36px;
    }
</style>
