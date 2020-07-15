<template>
    <div class="content-bg" v-loading="loading"  element-loading-background="rgba(26,36,47, 0.1)">
        <div class="top-title">{{title}}
            <div class="datepicker-wapper" style="padding-left: 20px;float: right;margin-right: 10px;">
                <baseDate type="datetimerange" :busName="this.busName" :defaultVal="this.timepicker"></baseDate>
            </div>
        </div>
        <div class="detail-content" >
            <div class="list-wapper" v-if="startIpArr">
                <div class="detail-list-title">源IP排行榜</div>
                <ul class="detail-list-ul">
                    <li v-for="(item,index) in startIpArr" :key="index" @click="startIpClick(item)" :class="index === 0?'first-item':(index === 1)?'second-item':(index === 2)?'third-item':''">
                        <span class="detail-list-order">{{index+1}}</span>
                        <span class="detail-list-value">{{item.IpOrPort}}</span>
                        <span class="detail-list-count">{{item.count > 10000 ? (Number(item.count) / 10000).toFixed(1) + ' 万':item.count }}</span>
                    </li>
                </ul>
            </div>
            <div class="list-wapper" v-if="endIpArr">
                <div class="detail-list-title">目的IP排行榜</div>
                <ul class="detail-list-ul">
                    <li v-for="(item,index) in endIpArr" :key="index" @click="endIpClick(item)" :class="index === 0?'first-item':(index === 1)?'second-item':(index === 2)?'third-item':''">
                        <span class="detail-list-order">{{index+1}}</span>
                        <span class="detail-list-value">{{item.IpOrPort}}</span>
                        <span class="detail-list-count">{{item.count > 10000 ? (Number(item.count) / 10000).toFixed(1) + ' 万':item.count }}</span>
                    </li>
                </ul>
            </div>
            <div class="list-wapper" v-if="startPortArr">
                <div class="detail-list-title">源端口排行榜</div>
                <ul class="detail-list-ul">
                    <li v-for="(item,index) in startPortArr" :key="index" @click="startPortClick(item)" :class="index === 0?'first-item':(index === 1)?'second-item':(index === 2)?'third-item':''">
                        <span class="detail-list-order">{{index+1}}</span>
                        <span class="detail-list-value">{{item.IpOrPort}}</span>
                        <span class="detail-list-count">{{item.count > 10000 ? (Number(item.count) / 10000).toFixed(1) + ' 万':item.count }}</span>
                    </li>
                </ul>
            </div>
            <div class="list-wapper" v-if="endPortArr">
                <div class="detail-list-title">目的端口排行榜</div>
                <ul class="detail-list-ul">
                    <li v-for="(item,index) in endPortArr" :key="index" @click="endPortClick(item)" :class="index === 0?'first-item':(index === 1)?'second-item':(index === 2)?'third-item':''">
                        <span class="detail-list-order">{{index+1}}</span>
                        <span class="detail-list-value">{{item.IpOrPort}}</span>
                        <span class="detail-list-count">{{item.count > 10000 ? (Number(item.count) / 10000).toFixed(1) + ' 万':item.count }}</span>
                    </li>
                </ul>
            </div>
        </div>
        <div class="conditionBox">
            <div class="boxLeft">
                <div class="cond1" v-if="startIpArr">
                    <span class="cond1_title">源IP</span>
                    <div class="cond1_text">
                         <div v-if="startIpVal !== ''">
                            <span class="val">{{startIpVal}}</span>
                            <i class=" el-icon-circle-close" @click="startIpVal = ''"></i>
                        </div>
                    </div>
                </div>
                <div class="cond2" v-if="endIpArr">
                    <span class="cond2_title">目的IP</span>
                    <div class="cond2_text">
                        <div v-if="endIpVal !== ''">
                            <span class="val">{{endIpVal}}</span>
                            <i class=" el-icon-circle-close" @click="endIpVal = ''"></i>
                        </div>
                    </div>
                </div>
                <div class="cond3" v-if="startPortArr">
                    <span class="cond3_title">源端口</span>
                    <div class="cond3_text">
                        <div v-if="startPortVal !== ''">
                            <span class="val">{{startPortVal}}</span>
                            <i class="el-icon-circle-close" @click="startPortVal = ''"></i>
                        </div>
                    </div>
                </div>
                <div class="cond4" v-if="endPortArr">
                    <span class="cond4_title">目的端口</span>
                    <div class="cond4_text">
                        <div v-if="endPortVal !== ''">
                            <span class="val">{{endPortVal}}</span>
                            <i class=" el-icon-circle-close" @click="endPortVal = ''"></i>
                        </div>
                    </div>
                </div>
            </div>
            <div class="btnBox" @click="readNetflowLogs">查看日志<i class="el-icon-lx-right"></i></div>
        </div>
    </div>
    
</template>

<script>
    import baseDate from '../common/baseDate'
    import bus from '../common/bus';
    import {savePath,jumpHtml} from "../../../static/js/common";

    export default {
        name: "rankingListDetail",
        data() {
            return {
                timepicker:[],
                loading:false,
                iporport:'',
                busName:'',
                type:'',
                startIpArr:[],//源ip
                endIpArr:[],//目的ip
                startPortArr:[],//源端口
                endPortArr:[],//目的端口
                startIpVal:'',
                endIpVal:'',
                startPortVal:'',
                endPortVal:'',

            }
        },
        computed:{
            title(){
                let t = '';
                if(this.type === 'ipv4_src_addr'){
                    t = '源IP：'+ this.iporport;
                    this.startIpVal = this.iporport;
                }else if(this.type === 'ipv4_dst_addr'){
                    t = '目的IP：'+ this.iporport;
                    this.endIpVal = this.iporport;
                }else if(this.type === 'l4_src_port'){
                    t = '源端口：'+ this.iporport;
                    this.startPortVal = this.iporport;
                }else if(this.type === 'l4_dst_port'){
                    t = '目的端口：'+ this.iporport;
                    this.endPortVal = this.iporport;
                }
                return t;
            }
        },
        created(){
            this.iporport = this.$route.query.iporport;
            this.type = this.$route.query.type;
            this.busName = 'rankingListDetail' + this.$route.query.iporport
            this.timepicker = [this.$route.query.starttime,this.$route.query.endtime]
            bus.$on(this.busName,(arr)=>{
                this.timepicker = arr;
                this.getData();
            })
        },
        beforeDestroy(){
            bus.$off(this.busName)
        },
        methods:{
            /*获得数据*/
            getData(){
                this.loading = true;
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getIPAndPortTop.do',this.$qs.stringify({
                        groupfiled:this.type,
                        iporport:this.iporport,
                        starttime:this.timepicker[0],
                        endtime:this.timepicker[1]
                    }))
                        .then(res =>{
                            this.loading = false;
                            this.startIpArr = res.data[0].ipv4_src_addr;
                            this.endIpArr = res.data[0].ipv4_dst_addr;
                            this.startPortArr = res.data[0].l4_src_port;
                            this.endPortArr = res.data[0].l4_dst_port;
                        })
                        .catch(err =>{
                            this.loading = false;
                            layer.msg('获取信息失败',{icon:5})
                        })
                })
            },
            /*源ip点击*/
            startIpClick(item){
                this.startIpVal = item.IpOrPort;
            },
            /*目的IP点击*/
            endIpClick(item){
                this.endIpVal = item.IpOrPort;
            },
            /*源端口点击*/
            startPortClick(item){
                this.startPortVal = item.IpOrPort;
            },
            /*目的端口点击*/
            endPortClick(item){
                this.endPortVal = item.IpOrPort;
            },
            /*创建动画盒子*/

            /*查看日志*/
            readNetflowLogs(){
                jumpHtml('netflowLogs'+this.iporport,'logsManage/netflowLogs.vue',{iporport:this.iporport,ipv4_src_addr:this.startIpVal,ipv4_dst_addr:this.endIpVal,l4_src_port:this.startPortVal,l4_dst_port:this.endPortVal,starttime:this.timepicker[0],endtime:this.timepicker[1]},"日志");
             }
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                vm.$options.name = 'rankingListDetail'+ to.query.iporport;
                if (to.query.type === 'ipv4_src_addr') {
                    vm.startIpArr = undefined
                }else if(to.query.type === 'ipv4_dst_addr'){
                    vm.endIpArr = undefined
                }else if(to.query.type === 'l4_src_port'){
                    vm.startPortArr = undefined
                }else if(to.query.type === 'l4_dst_port'){
                    vm.endPortArr = undefined
                }
                //修改data参数
                if(vm.iporport === '' || vm.iporport !== to.query.iporport){

                   // vm.getData();
                }
                savePath(to.name,'logsManage/rankingListDetail.vue','排行');
            })

        },
        components:{
            baseDate
        }
    }
</script>

<style scoped>
    .detail-content{
        display: flex;
        justify-content: space-around;
        padding-bottom: 100px;
    }
    .detail-list-title{
        width: 270px;
        height: 56px;
        line-height: 56px;
        background: #3b5773;
        color: #fff;
        text-indent: 10px;
    }
    .detail-list-ul li {
        border-top: 1px dashed rgba(255,255,255,0.3);
        line-height: 35px;
        font-size: 11px;
        height: 35px;
        margin: 0;
        width: 270px;
        background: #3b5773;

     }
    .detail-list-ul li:hover{
        cursor: pointer;
        background: #44678a;
    }
    .detail-list-ul li span{
        display: inline-block;
    }
    .detail-list-order{
        width: 40px;
        text-align: center;
    }
    .detail-list-value{
        width: calc(100% - 130px);
        text-align: center;
    }
    .detail-list-count{
        width: 80px;
        text-align: center;
    }
    .detail-list-ul .first-item{
        color: #f75656;
        font-weight: 600;
    }
    .detail-list-ul .second-item{
        color: #ff8547;
        font-weight: 600;
    }
    .detail-list-ul .third-item{
        color: #ffac38;
        font-weight: 600;
    }
    .conditionBox{
        display:flex;
        height:80px;
        width:1160px;
        position:fixed;
        left:50%;
        margin-left:-510px;
        bottom:36px;
        background:#2f455c;
        overflow: hidden;
    }
    .boxLeft{
        display:flex;
        flex:1;
        overflow: hidden;
    }
    .boxLeft>div{
        width:316px;
        padding:10px;
        height:60px;
        display:flex;
    }
    .boxLeft>div>span{
        font-weight:600;
    }
    .boxLeft>div>div{
        flex:1;
        margin-left:10px;
        border:2px solid #608ab5;
        display:flex;
        justify-content: center;
        align-items: center;
        font-size: 16px;
        font-weight: 600;
    }
    .boxLeft>div>div i{
        position:relative;
        display:none;
        top:-15px;
        right:-50px;
        cursor:pointer;
    }
    .boxLeft>div:hover i{
        display: inline-block;
    }
    .btnBox{
        width:150px;
        height:80px;
        line-height:80px;
        text-align:center;
        font-weight:600;
        font-size:16px;
        cursor:pointer;
        background:#2c76bd;
        color:#fff;
        display: flex;
        justify-content: center;
        align-items: center;
    }
    .btnBox i{
        margin-left: 10px;
        font-size: 12px;
        border-radius: 100%;
        padding: 5px;
        border: 2px solid #fff;
    }
</style>
