<template>
    <div class="list-Box" id="list-box">
        <div class="title">{{title}}</div>
        <div class="list-content">
            <ul>
                <li v-for="(item,index) in rankingArr" :key="index" @click="itemClick(item)" :class="index === 0?'first-item':(index === 1)?'second-item':(index === 2)?'third-item':''">
                    <div class="list-left">
                        <div class="rect"></div>
                        <div class="triangle"></div>
                        <div class="order">{{(index+1)<9?'0'+(index+1):(index+1)}}</div>
                    </div>
                    <div class="list-right">
                        <div class="empty-box"></div>
                        <div class="text" :title="item.text">{{item.text}}</div>
                        <div class="num">{{item.count > 10000 ? (Number(item.count) / 10000).toFixed(1) + ' 万':item.count }}</div>
                    </div>
                </li>
            </ul>
        </div>
    </div>
    
</template>

<script>
    import bus from '../common/bus';
    export default {
        name: "ranking",
        props:{
            //标题
            title:{
                type:String
            },
            //数据 [{text:'',count:''}]
            rankingArr:{
                type:Array
            },
            //busname
            busName:{
                type:String
            }
        },
        data() {
            return {}
        },
        created(){
        },
        methods:{
            /*列表点击事件*/
            itemClick(item,event){
                let obj = {};
                let e = event || window.event || arguments.callee.caller.arguments[0];
                //鼠标点击的坐标
                obj.menuLeft = e.clientX;
                obj.menuTop = e.clientY;
                //当前数据
                obj.text = item.text;
               bus.$emit(this.busName,obj)
            }
        }
    }
</script>

<style scoped>
    .list-Box{
        /*max-width: 530px;*/
        /* height: 600px; */
        width: 100%;
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
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
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
        position: absolute;
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
