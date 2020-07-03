<template>
    <div class="content-bg">
        <div class="title">User-Agent信息</div>
        <div class="timer"><dateLayout class="from-wapper" busName="userAgentInfoDataBus" :defaultVal = "defaultVal"></dateLayout></div>

        <div class="item-wapper">
            <div class="item-title">业务访问用户统计-操作系统</div>
            <div class="item-content">
                <agentSystem_bar :params="param" :setIntervalObj="intervalObj" :baseConProp="{title:''}"></agentSystem_bar>
            </div>
        </div>
        <div class="item-wapper">
            <div class="item-title">业务访问用户统计-操作系统</div>
            <div class="item-content">
                <agentSystem_pie :params="param" :setIntervalObj="intervalObj" :baseConProp="{title:''}"></agentSystem_pie>
            </div>
        </div>
        <div class="item-wapper">
            <div class="item-title">业务访问用户统计-浏览器</div>
            <div class="item-content">
                <agentBrowser_bar :params="param" :setIntervalObj="intervalObj" :baseConProp="{title:''}"></agentBrowser_bar>
            </div>
        </div>
        <div class="item-wapper">
            <div class="item-title">业务访问用户统计-浏览器</div>
            <div class="item-content">
                <agentBrowser_pie :params="param" :setIntervalObj="intervalObj" :baseConProp="{title:''}"></agentBrowser_pie>
            </div>
        </div>
    </div>
    
</template>

<script>
    import dateLayout from '../../common/dateLayout'
    import bus from '../../common/bus';
    import agentBrowser_bar from '../../charts/flow/userAgentInfo/agentBrowser_bar'
    import agentBrowser_pie from '../../charts/flow/userAgentInfo/agentBrowser_pie'
    import agentSystem_bar from '../../charts/flow/userAgentInfo/agentSystem_bar'
    import agentSystem_pie from '../../charts/flow/userAgentInfo/agentSystem_pie'
    import {dateFormat,setChartParam} from '../../../../static/js/common'
    export default {
        name: "userAgentInfo_m",
        data() {
            return {
                //请求参数
                param:{
                    intervalValue:'',
                    intervalType:'',
                    starttime:'',
                    endtime:'',
                    last:'7-day'
                },
                //轮询参数
                intervalObj:{
                    state:false,
                    interval:'5000'
                },
                //时间控件参数
                defaultVal:{
                    //具体时间参数
                    lastVal:'7-day',
                    //起始时间
                    starttime:'',
                    //结束时间
                    endtime:'',
                    //具体时间 类型状态
                    dateBlock:false,
                    //是否存在轮询框
                    isIntervalBox:true,
                    //轮询状态
                    intervalState:false,
                    //轮询数值间隔
                    intervalVal:'5',
                    //轮询参数类型
                    intervalType:'second',
                    //‘快速选择’功能参数类型
                    dateUnit:'day',
                    //‘快速选择’功能参数数值
                    dateCount:'7',
                    //‘常用’ 时间值
                    commonlyVal:'',
                    //是否可以切换精确日期
                    changeState:true
                }
            }
        },
        created(){
            /*监听日期改变*/
            bus.$on('userAgentInfoDataBus',(obj)=>{
                //设置参数对应
                let arr = setChartParam(obj);
                this.param = arr[0];
                this.intervalObj = arr[1]
            })

        },
        mounted(){
        },
        methods:{
        },
        beforeDestroy(){
            bus.$off('userAgentInfoDataBus');
        },
        components:{
            dateLayout,
            agentBrowser_bar,
            agentBrowser_pie,
            agentSystem_bar,
            agentSystem_pie
        }
    }
</script>

<style scoped>
    .content-bg{
        /*height: 1000px;*/
        font-size: 1rem;
        background: url("../../../../static/img/flow-index-bg2.png");
        background-size: 100% 100%;
    }
    .title{
        font-size: 1.3rem;
        font-weight: 600;
        color: #185bff;
        padding: 10px;
        text-align: center;
        text-shadow: none;
    }
    .item-wapper{
        height: auto;
        /*background: rgb(26,36,47);*/
        background: url("../../../../static/img/bd2.png");
        background-size: 100% 100%;
        margin:0.5rem 0.2rem;
    }
    .timer{
        display: flex;
        justify-content: center;
    }
    .item-title{
        height: 2.5rem;
        line-height: 2.5rem;
        font-size: 1rem;
        text-align: left;
        padding-left: 25px;
        /*background: url("../../../static/img/title-bg.png");*/
        background-size: 100% 100%;
    }
    .item-content{
        /*height: calc(100% - 1rem);*/
        height: 230px;
        box-sizing: border-box;
        padding: 0.5rem;
        padding-top: 0;
    }
</style>
