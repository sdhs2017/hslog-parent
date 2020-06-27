<template>
    <div class="content-bg">
        <div class="top-title">资产画像</div>
        <div class="datepicker-wapper" style="padding-left: 20px;">
            <span>日期范围：</span>
            <el-date-picker
                v-model="timepicker"
                type="datetimerange"
                align="right"
                unlink-panels
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="yyyy-MM-dd HH:mm:ss"
                @change="timepickerChange"
                :picker-options="pickerOptions">
            </el-date-picker>
        </div>
        <div class="ranking-list-wapper" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
            <div class="ip-ranking">
                <div class="start-ip-wapper">
                    <div class="list-Box">
                        <div class="title">源IP排行榜</div>
                        <div class="list-content">
                            <ul>
                                <li v-for="(item,index) in startIpArr" @click="ipClick(item,'ipv4_src_addr')" :key="index" :class="index === 0?'first-item':(index === 1)?'second-item':(index === 2)?'third-item':''">
                                    <div class="list-left">
                                        <div class="rect"></div>
                                        <div class="triangle"></div>
                                        <div class="order">{{(index+1)<9?'0'+(index+1):(index+1)}}</div>
                                    </div>
                                    <div class="list-right">
                                        <div class="empty-box"></div>
                                        <div class="text">{{item.IpOrPort}}</div>
                                        <div class="num">{{item.count > 10000 ? (Number(item.count) / 10000).toFixed(1) + ' 万':item.count }}</div>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div class="end-ip-wapper">
                    <div class="list-Box">
                        <div class="title">目的IP排行榜</div>
                        <div class="list-content">
                            <ul>
                                <li v-for="(item,index) in endIpArr" @click="ipClick(item,'ipv4_dst_addr')" :key="index" :class="index === 0?'first-item':(index === 1)?'second-item':(index === 2)?'third-item':''">
                                    <div class="list-left">
                                        <div class="rect"></div>
                                        <div class="triangle"></div>
                                        <div class="order">{{(index+1)<9?'0'+(index+1):(index+1)}}</div>
                                    </div>
                                    <div class="list-right">
                                        <div class="empty-box"></div>
                                        <div class="text">{{item.IpOrPort}}</div>
                                        <div class="num">{{item.count > 10000 ? (Number(item.count) / 10000).toFixed(1) + ' 万':item.count }}</div>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            <div class="port-ranking">
                <div class="start-port-wapper">
                    <div class="list-Box">
                        <div class="title">源端口排行榜</div>
                        <div class="list-content">
                            <ul>
                                <li v-for="(item,index) in startPortArr" @click="portClick(item,'l4_src_port')" :key="index" :class="index === 0?'first-item':(index === 1)?'second-item':(index === 2)?'third-item':''">
                                    <div class="list-left">
                                        <div class="rect"></div>
                                        <div class="triangle"></div>
                                        <div class="order">{{(index+1)<9?'0'+(index+1):(index+1)}}</div>
                                    </div>
                                    <div class="list-right">
                                        <div class="empty-box"></div>
                                        <div class="text">{{item.IpOrPort}}</div>
                                        <div class="num">{{item.count > 10000 ? (Number(item.count) / 10000).toFixed(1) + ' 万':item.count }}</div>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div class="end-port-wapper">
                    <div class="list-Box">
                        <div class="title">目的端口排行榜</div>
                        <div class="list-content">
                            <ul>
                                <li v-for="(item,index) in endPortArr" :key="index" @click="portClick(item,'l4_dst_port')" :class="index === 0?'first-item':(index === 1)?'second-item':(index === 2)?'third-item':''">
                                    <div class="list-left">
                                        <div class="rect"></div>
                                        <div class="triangle"></div>
                                        <div class="order">{{(index+1)<9?'0'+(index+1):(index+1)}}</div>
                                    </div>
                                    <div class="list-right">
                                        <div class="empty-box"></div>
                                        <div class="text">{{item.IpOrPort}}</div>
                                        <div class="num">{{item.count > 10000 ? (Number(item.count) / 10000).toFixed(1) + ' 万':item.count }}</div>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div v-if="selectedMenuState" class="zz" @click="selectedMenuState = false">
            <div class="selected-menu animated bounceIn" :style="menuLocation">
                <div class="menu-ranking" @click="rankingMenu">排行榜</div>
                <div class="men-graph" @click="graphMenu">业务流</div>
                <div class="menu-cancle" @click="cancelMenu">取消</div>
            </div>
        </div>

    </div>
    
</template>

<script>
    import {dateFormat} from "../../../static/js/common";
    export default {
        name: "rankingList",
        data() {
            return {
                loading:false,
                timepicker:[],//日期值
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
                startIpArr:[],//源ip
                endIpArr:[],//目的ip
                startPortArr:[],//源端口
                endPortArr:[],//目的端口
                selectedMenuState : false, //菜单是否显示
                selectedItem:'',
                selectedItemType:'',
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
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);

            this.timepicker=[dateFormat('yyyy-mm-dd',start),dateFormat('yyyy-mm-dd',end)]
            this.getRankingListData(this.timepicker);
        },
        methods:{
            /*获取日志信息*/
            getRankingListData(timeArr){
                this.loading = true;
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getTopGroupByIPOrPort.do',this.$qs.stringify({
                        startTime:timeArr[0],
                        endTime:timeArr[1]
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
            /*ip列表点击事件*/
            ipClick(item,type,event){
                this.selectedMenuState = true;
                let e = event || window.event || arguments.callee.caller.arguments[0];
                this.menuLeft = e.clientX;
                this.menuTop = e.clientY;
                this.selectedItem = item;
                this.selectedItemType = type;
            },
            /*端口列表点击事件*/
            portClick(item,type){
                this.selectedItem = item;
                this.selectedItemType = type;
                let newRouters = [{
                    path:'/',
                    component: resolve => require(['../common/Home.vue'], resolve),
                    meta: { title: '自述文件' },
                    children:[
                        {
                            path: '/rankingListDetail'+this.selectedItem.IpOrPort,
                            name:'rankingListDetail'+this.selectedItem.IpOrPort,
                            component: resolve => require(['../logsManage/rankingListDetail.vue'], resolve),
                            meta: { title: '排行' }
                        }
                    ]
                }]
                this.$router.addRoutes(newRouters);
                this.$router.push({path:'/rankingListDetail'+this.selectedItem.IpOrPort,query: { iporport:this.selectedItem.IpOrPort,type:this.selectedItemType }})
            },
            /*跳转到排行详情*/
            rankingMenu(){
                let newRouters = [{
                    path:'/',
                    component: resolve => require(['../common/Home.vue'], resolve),
                    meta: { title: '自述文件' },
                    children:[
                        {
                            path: '/rankingListDetail'+this.selectedItem.IpOrPort,
                            name:'rankingListDetail'+this.selectedItem.IpOrPort,
                            component: resolve => require(['../logsManage/rankingListDetail.vue'], resolve),
                            meta: { title: '排行' }
                        }
                    ]
                }]
                this.$router.addRoutes(newRouters);
                this.$router.push({path:'/rankingListDetail'+this.selectedItem.IpOrPort,query: { iporport:this.selectedItem.IpOrPort,type:this.selectedItemType }})
                //关闭菜单
                this.cancelMenu();
            },
            /*关系图*/
            graphMenu(){
                let newRouters = [{
                    path:'/',
                    component: resolve => require(['../common/Home.vue'], resolve),
                    meta: { title: '自述文件' },
                    children:[
                        {
                            path: '/graph'+this.selectedItem.IpOrPort,
                            name:'graph'+this.selectedItem.IpOrPort,
                            component: resolve => require(['../logsManage/graph.vue'], resolve),
                            meta: { title: '关系图' }
                        }
                    ]
                }]
                this.$router.addRoutes(newRouters);
                this.$router.push({path:'/graph'+this.selectedItem.IpOrPort,query: { iporport:this.selectedItem.IpOrPort,type:this.selectedItemType,count:this.selectedItem.count }})
                //关闭菜单
                this.cancelMenu();
            },
            /*关闭菜单*/
            cancelMenu(){
                this.selectedMenuState = false;
            },
            /*日期改变事件*/
            timepickerChange(){
                if(this.timepicker === null){
                    this.timepicker=['','']
                }
                this.getRankingListData(this.timepicker);
            }
        }
    }
</script>

<style scoped>
    .ranking-list-wapper{
        padding: 10px;
        height: calc(100% - 100px);
        margin: 10px 0;
    }
    .ip-ranking {
        display: flex;
    }
    .port-ranking {
        display: flex;
    }
    .start-ip-wapper, .end-ip-wapper, .start-port-wapper, .end-port-wapper {
        width: 50%;
        padding: 40px;
        padding-top: 10px;
    }
    .start-ip-wapper, .start-port-wapper {
        border-right: 1px solid #4b759e;
    }

    .start-ip-wapper, .end-ip-wapper {
        border-bottom: 1px solid #4b759e;
    }
    .list-Box{
        max-width: 530px;
        /* height: 600px; */
        background: #3b5773;
        margin: 10px auto;
    }
    .title{
        height: 60px;
        line-height: 50px;
        border-bottom: 1px dashed #6598c7;
        text-align: center;
        font-size: 18px;
        font-weight: 600;
        color: #fff;
    }
    .list-content{
        padding: 10px;
    }
    .list-content li{
        height: 46px;
        margin-bottom: 1px;
        overflow: hidden;
        color: #fff;
        font-weight: 600;
        font-size: 13px;
        /* display: flex; */
    }
    .list-content li:hover{
        cursor: pointer;
        box-shadow: 6px 6px 15px #6592bb;
    }
    .list-left {
        height: 100%;
        width: 66px;
        display: flex;
        position: relative;
        float: left;
    }
    .list-left .rect {
        width: 20px;
        height: 100%;
        background: #245d92;
        border-radius: 5px 0 0 5px;
    }
    .list-left .triangle {
        width: 0;
        height: 0;
        border-bottom: 46px solid #245d92;
        border-right: 46px solid transparent;
    }
    .list-left .order {
        position: absolute;
        left: 9px;
        top: 12px;
        font-size: 14px;
        font-weight: 600;
        height: 24px;
        line-height: 24px;
        width: 26px;
        text-align: center;
        color: #fff;
        border-bottom: 1px solid #fff;
    }
    .list-right {
        margin: 3px 0;
        height: 40px;
        width: 100%;
        background: #4b759e;
        border-radius: 0 5px 5px 0;
    }
    .list-right .empty-box {
        width: 66px;
    }

    .list-right>div {
        float: left;
        line-height: 40px;
    }
    .list-right .text {
        width: calc(100% - 166px);
        height: 100%;
        text-align: center;
    }
    .list-right .num {
        width: 100px;
        height: 100%;
        text-align: center;
    }
    .list-content .first-item{
        color: #f75656;
        font-weight: 600;
    }
    .first-item .list-left .rect{
        background: #f75656;
    }
    .first-item .list-left .triangle{
        border-bottom: 46px solid #f75656;
    }
    .list-content .second-item{
        color: #ff8547;
        font-weight: 600;
    }
    .second-item .list-left .rect{
        background: #ff8547;
    }
    .second-item .list-left .triangle{
        border-bottom: 46px solid #ff8547;
    }
    .list-content .third-item{
        color: #ffac38;
        font-weight: 600;
    }
    .third-item .list-left .rect{
        background: #ffac38;
    }
    .third-item .list-left .triangle{
        border-bottom: 46px solid #ffac38;
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
