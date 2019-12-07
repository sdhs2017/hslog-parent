<template>
    <div class="content-bg">
        <div class="top-title">{{ipv4_dst_addr}} 应用画像</div>
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
        <div v-if="selectedMenuState" class="zz" @click="selectedMenuState = false">
            <div class="selected-menu animated bounceIn" :style="menuLocation">
                <div class="menu-ranking" @click="rankingMenu">排行榜</div>
                <div class="men-graph" @click="graphMenu">业务流</div>
                <div class="menu-cancle" @click="selectedMenuState = false">取消</div>
            </div>
        </div>
    </div>
    
</template>

<script>
    import vRanking from '../common/ranking';
    import bus from '../common/bus';
    import {jumpHtml,savePath,gresizeW} from "../../../static/js/common";
    export default {
        name: "urlRanking",
        data() {
            return {
                rankingTitle:'URL Top10 排行',
                rankingData:[],
                busName:'urlRanking',
                application_layer_protocol:'',
                ipv4_dst_addr:'',
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
                },
                currentItemVal:'',//当前点击的列表的值
                selectedMenuState : false, //菜单是否显示
                menuLeft:'', //菜单x位置
                menuTop:''   //菜单Y位置
            }
        },
        computed:{
            menuLocation(){
                return {
                    top:this.menuTop+'px',
                    left:this.menuLeft+'px'
                }
            }
        },
        created(){
            //设置日期
            let newDate = new Date();
            let curDate = newDate.toLocaleDateString();
            let curYear = newDate.getFullYear();
            let curMonth = newDate.getMonth() + 1;
            let curDay = newDate.getDate();
            if(curMonth < 10){
                curMonth = '0'+curMonth
            }
            if(curDay < 10){
                curDay = '0' + curDay;
            }
            let startTime = curYear+'-'+curMonth+'-01';
            let endTime = curYear+'-'+curMonth+'-'+curDay;
            this.timepicker=[startTime,endTime]
            //获取排行榜数据
            this.getRankingData(this.timepicker);

        },
        mounted(){
            //console.log('fff')
            gresizeW($('.ranking-wapper'))
        },
        beforeDestroy () {
            //销毁绑定的bus点击事件
            bus.$off(this.busName);
        },
        methods:{
            //获取数据
            getRankingData(timeArr){
                layer.load(1);
                this.$nextTick(()=>{
                    if(this.$route.query.val){
                        this.application_layer_protocol = 'http';
                        this.ipv4_dst_addr = this.$route.query.val;
                        this.rankingTitle = this.$route.query.val + ' URL 排行';
                        this.$options.name = 'urlRanking'+ this.$route.query.val;
                        this.busName = 'urlRanking'+ this.$route.query.val;

                    }
                    this.$axios.post(this.$baseUrl+'/flow/getCountGroupByUrl.do',this.$qs.stringify({
                        application_layer_protocol:this.application_layer_protocol,
                        ipv4_dst_addr:this.ipv4_dst_addr,
                        startTime:timeArr[0],
                        endTime:timeArr[1]
                    }))
                        .then(res=>{
                            layer.closeAll();
                            let arr = [];
                            res.data.forEach((item)=>{
                                let obj = {};
                                obj.text=item.domain_url;
                                obj.count = item.count;
                                arr.push(obj);
                            })
                            this.rankingData = arr;
                        })
                        .catch(err=>{
                            layer.closeAll()
                            layer.msg('获取数据失败',{icon:5})
                        })
                })
            },
            //菜单-排行榜 点击事件
            rankingMenu(){
                let obj = {};
                obj.val = this.currentItemVal;
                jumpHtml('funcRanking'+this.currentItemVal,'logsManage/funcRanking.vue',obj,'排行');
            },
            //菜单-业务流 点击事件
            graphMenu(){
                let obj = {};
                obj.val = this.currentItemVal;
                jumpHtml('urlGraph'+this.currentItemVal,'logsManage/urlGraph.vue',obj,'业务流');
            },
            //日期改变事件
            timepickerChange(){
                if(this.timepicker === null){
                    this.timepicker=['','']
                }
                this.getRankingData(this.timepicker);
            }
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                //绑定点击事件
                if(to.query.val){
                    vm.busName = 'urlRanking'+to.query.val;
                }
                bus.$on(vm.busName,(params)=>{
                    vm.selectedMenuState = true;
                    vm.menuLeft = params.menuLeft;
                    vm.menuTop = params.menuTop;
                    vm.currentItemVal = params.text;
                })
                savePath(to.name,'logsManage/urlRanking.vue','排行')
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
    .zz{
        width: 100vw;
        height: 100vh;
        background: rgba(0,0,0,0.2);
        position: fixed;
        top: 0;
        left: 0;
    }
    .selected-menu{
        position: fixed;
        width: 80px;
        color:#fff;
        box-shadow: 0 3px 15px #4781bb;
        font-size: 12px;
        background: rgb(52, 114, 174);
        z-index: 99999999900;
    }
    .menu-ranking{
        width:100%;
        height: 30px;
        text-align: center;
        cursor: pointer;
        line-height: 30px;
        border-bottom: 1px solid rgb(92, 144, 192);
    }
    .men-graph{
        width:100%;
        height: 30px;
        text-align: center;
        cursor: pointer;
        line-height: 30px;
        border-bottom: 1px solid rgb(92, 144, 192);
    }
    .menu-cancle{
        width:100%;
        height: 30px;
        text-align: center;
        cursor: pointer;
        line-height: 30px;
    }
    .selected-menu div:hover{
        background: rgb(29,80,128);
    }
</style>
