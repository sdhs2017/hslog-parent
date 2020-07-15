<template>
    <div class="content-bg" v-loading="loading"  element-loading-background="rgba(26,36,47, 0.2)">
        <div class="top-title">应用资产画像
            <div class="datepicker-wapper" style="padding-left: 20px;float: right;margin-right: 10px;">
                <baseDate type="datetimerange" busName="urlIpRanking"></baseDate>
            </div>
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
    import baseDate from '../common/baseDate'
    import {jumpHtml,gresizeW,dateFormat} from "../../../static/js/common";
    export default {
        name: "urlIpRanking",
        data() {
            return {
                loading:false,
                rankingTitle:'应用资产 Top10 排行',
                rankingData:[],
                busName:'urlRanking',
                timepicker:'',//日期值
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
            //绑定点击事件
            bus.$on(this.busName,(params)=>{
                this.selectedMenuState = true;
                this.menuLeft = params.menuLeft;
                this.menuTop = params.menuTop;
                this.currentItemVal = params.text;
            })
           bus.$on('urlIpRanking',(arr)=>{
               this.timepicker = arr;
               this.getRankingData(this.timepicker)
           })
        },
        mounted(){
            gresizeW($('.ranking-wapper'))
        },
        beforeDestroy () {
            //销毁绑定的bus点击事件
            bus.$off(this.busName);
            bus.$off('urlIpRanking');
        },
        methods:{
            //获取数据
            getRankingData(timeArr){
                this.loading = true;
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getTopGroupByIPOrPort.do',this.$qs.stringify({
                        groupfiled : "ipv4_dst_addr",
                        application_layer_protocol : "http,https",
                        starttime:timeArr[0],
                        endtime:timeArr[1]
                    }))
                        .then(res=>{
                            this.loading = false;
                            let arr = [];
                            res.data[0].ipv4_dst_addr.forEach((item)=>{
                                let obj = {};
                                obj.text=item.IpOrPort;
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
            //菜单-排行榜 点击事件
            rankingMenu(){
                let obj = {};
                obj.val = this.currentItemVal;
                obj.starttime = this.timepicker[0]
                obj.endtime = this.timepicker[1]
                jumpHtml('urlRanking'+this.currentItemVal,'logsManage/urlRanking.vue',obj,'应用画像');
            },
            //菜单-业务流 点击事件
            graphMenu(){
                let obj = {};
                obj.val = this.currentItemVal;
                obj.type = 'ip'
                obj.starttime = this.timepicker[0]
                obj.endtime = this.timepicker[1]
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
