<template>
    <div class="content-bg" v-loading="loading"  element-loading-background="rgba(26,36,47, 0.2)">
        <div class="top-title">{{domain_url}} 功能排行</div>
        <div class="datepicker-wapper" style="padding-left: 20px;">
            <span>日期范围：</span>
            <el-date-picker
                v-model="timepicker"
                type="daterange"
                align="right"
                unlink-panels
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="yyyy-MM-dd"
                @change="timepickerChange"
                :picker-options="pickerOptions">
            </el-date-picker>
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
    import {jumpHtml,savePath,dateFormat} from "../../../static/js/common";
    export default {
        name: "funcRanking",
        data() {
            return {
                loading:false,
                domain_url:'',//参数
                rankingTitle:'功能 URL 排行',
                rankingData:[],
                busName:'',
                timepicker:'',//日期值
                pickerOptions: { //日期选择器
                    shortcuts: [{
                        text: '最近一周',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '最近一个月',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '最近三个月',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                            picker.$emit('pick', [start, end]);
                        }
                    }]
                }
            }
        },
        created(){
            //设置日期
            //设置日期
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);

            this.timepicker=[dateFormat('yyyy-mm-dd',start),dateFormat('yyyy-mm-dd',end)]
        },
        beforeDestroy () {
            //销毁绑定的bus点击事件
            bus.$off(this.busName);
        },
        watch:{
            'domain_url'(){
                this.getfuncRankingData(this.timepicker);
                //绑定点击事件
                bus.$on(this.busName,(params)=>{
                    let obj = {};
                    obj.complete_url = params.text;
                    jumpHtml('funcGraph'+obj.complete_url,'logsManage/funcGraph.vue',obj,'业务流')
                })
            }
        },
        methods:{
            //获取数据
            getfuncRankingData(timeArr){
                this.loading = true;
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getCountGroupByHttpComUrl.do',this.$qs.stringify({
                        domain_url:this.domain_url,
                        startTime:timeArr[0],
                        endTime:timeArr[1]
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
                vm.domain_url = to.query.val;
                vm.$options.name = 'funcRanking'+ to.query.val;
                vm.busName = 'funcRanking'+ to.query.val;
                savePath(to.name,'logsManage/funcRanking.vue','排行')
            })
        },
        components:{
            vRanking
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
