<template>
    <div class="content-bg" v-loading="loading"  element-loading-background="rgba(26,36,47, 0.2)">
        <div class="top-title">{{domain_url}} 功能排行
            <div class="datepicker-wapper" style="padding-left: 20px;float: right;margin-right: 10px;">
                <baseDate type="datetimerange" :defaultVal="this.timepicker" :busName="dateBusName"></baseDate>
            </div>
        </div>
        <div class="content">
            <div class="ranking-wapper">
                <v-ranking :title="rankingTitle" :rankingArr="rankingData" :busName="busName"></v-ranking>
            </div>
        </div>
    </div>
    
</template>

<script>
    import vRanking from '../common/ranking';
    import bus from '../common/bus';
    import baseDate from '../common/baseDate'
    import {jumpHtml,savePath,dateFormat} from "../../../static/js/common";
    export default {
        name: "funcRanking",
        data() {
            return {
                loading:false,
                domain_url:'',//参数
                rankingTitle:'功能 URL 排行',
                rankingData:[],
                dateBusName:'',
                busName:'',
                timepicker:[],//日期值
            }
        },
        created(){
            this.timepicker=[this.$route.query.starttime,this.$route.query.endtime]
            this.domain_url = this.$route.query.val;
            this.busName = 'funcRanking'+ this.$route.query.val;
            this.dateBusName = 'funcRankingDateBusName' + this.$route.query.val
            bus.$on(this.dateBusName,(val)=>{
                this.timepicker=val;
                this.getfuncRankingData(this.timepicker)
            })
            bus.$on(this.busName,(params)=>{
                let obj = {};
                obj.complete_url = params.text;
                obj.starttime = this.timepicker[0]
                obj.endtime = this.timepicker[1]
                jumpHtml('funcGraph'+obj.complete_url,'logsManage/funcGraph.vue',obj,'业务流')
            })
        },
        beforeDestroy () {
            //销毁绑定的bus点击事件
            bus.$off(this.busName);
            bus.$off(this.dateBusName);
        },
        watch:{
        },
        methods:{
            //获取数据
            getfuncRankingData(timeArr){
                this.loading = true;
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getCountGroupByHttpComUrl.do',this.$qs.stringify({
                        domain_url:this.domain_url,
                        starttime:timeArr[0],
                        endtime:timeArr[1]
                    }))
                        .then(res=>{
                            this.loading = false;
                            let arr = [];
                            res.data.forEach((item)=>{
                                let obj = {};
                                obj.text=item.complete_url;
                                obj.count = item.count;
                                arr.push(obj);
                            })
                            this.rankingData = arr;
                        })
                        .catch(err=>{
                            this.loading = false;
                            layer.msg('获取数据失败',{icon:5})
                        })
                })
            },
            //日期改变事件
            timepickerChange(){
                if(this.timepicker === null){
                    this.timepicker=['','']
                }
                this.getfuncRankingData(this.timepicker);
            }
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                vm.$options.name = 'funcRanking'+ to.query.val;
                savePath(to.name,'logsManage/funcRanking.vue','排行')
            })
        },
        components:{
            vRanking,
            baseDate
        }
    }
</script>

<style scoped>
    .ranking-wapper{
        max-width: 630px;
        /* height: 600px; */
        background: #3b5773;
        margin: 10px auto;
    }
</style>
