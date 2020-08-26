<template>
    <div class="tags" v-if="showTags">
        <span class="go-left" @click="goLeft"><i class="el-icon-d-arrow-left"></i></span>
        <div class="ul-wapper">
            <ul :style="{left:ulLeft+'px'}" @mousedown="move">
                <li class="tags-li" v-for="(item,index) in tagsList" :class="{'active': isActive(item.path)}" :key="index">
                    <router-link :to="item.path" class="tags-li-title" v-if="item.other" :title="item.other+item.title" data-ss="eee">
                        <!--{{item.other + item.title }}-->
                        <span class="tag-item-wapper"><span class="tag-span">{{item.other}}</span>{{item.title}}</span>
                    </router-link>
                    <router-link :to="item.path" class="tags-li-title" v-else>
                        {{item.title }}
                    </router-link>
                    <span class="tags-li-icon" @click="closeTags(index)"><i class="el-icon-close"></i></span>
                </li>
            </ul>
        </div>
        <span class="go-right" @click="goRight"><i class="el-icon-d-arrow-right"></i></span>
        <div class="tags-close-box">
            <el-dropdown @command="handleTags">
                <el-button size="mini" type="primary">
                    标签选项<i class="el-icon-arrow-down el-icon--right"></i>
                </el-button>
                <el-dropdown-menu size="small" slot="dropdown">
                    <el-dropdown-item command="other">关闭其他</el-dropdown-item>
                    <el-dropdown-item command="all">关闭所有</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
    </div>
</template>

<script>
    import bus from './bus';
    export default {
        data() {
            return {
                tagsList: [],
                ulLeft:0
            }
        },
        methods: {
            isActive(path) {
                return path === this.$route.fullPath;
            },
            // 关闭单个标签
            closeTags(index) {
                const delItem = this.tagsList.splice(index, 1)[0];
                const item = this.tagsList[index] ? this.tagsList[index] : this.tagsList[index - 1];
                this.$store.commit('deleteRoute',index)
                if (item) {
                    //delItem.path === this.$route.fullPath && this.$router.push(item.path);
                    if( delItem.path === this.$route.fullPath ){
                        this.$router.push(item.path)
                    }else{
                        this.setTags(this.$route);
                    }
                }else{
                    this.$router.push('/index_n');
                }
            },
            // 关闭全部标签
            closeAll(){
                this.tagsList = [];
                this.$router.push('/index_n');
            },
            // 关闭其他标签
            closeOther(){
                const curItem = this.tagsList.filter(item => {
                    return item.path === this.$route.fullPath;
                })
                this.tagsList = curItem;
            },
            // 设置标签
            setTags(route){
                //遍历数组 看新加的标签是否已经存在
                const isExist = this.tagsList.some(item => {
                    return item.path === route.fullPath;
                })
                //不存在 则添加
                if(!isExist){
                    //判断数组长度是否大于8
                    /* if(this.tagsList.length >= 8){
                         //把数组的第一个元素从其中删除，并返回第一个元素的值
                         this.tagsList.shift();
                     }*/
                    let pn = '';
                    if(route.fullPath.indexOf('=') !== -1){
                        if(route.fullPath.split('=')[1].split('&')[0] === ''){
                            pn = decodeURIComponent(route.fullPath.split('=')[1])
                        }else{
                            pn = decodeURIComponent(route.fullPath.split('=')[1].split('&')[0])
                        }
                    }
                    let obj = {
                        title: route.meta.title,
                        path: route.fullPath,
                        name: route.matched[1].components.default.name,
                        other:pn
                    }
                    //将新加的标签添加到数组中
                    this.tagsList.push(obj)
                    this.$store.commit('pushRoute',obj)
                    this.computedLeft();
                }
                bus.$emit('tags', this.tagsList);
            },
            handleTags(command){
                command === 'other' ? this.closeOther() : this.closeAll();
            },
            /*计算标签偏移的宽度*/
            computedLeft(){
                let fWidth = $('.ul-wapper').width();
                let ulWidth = $('.ul-wapper ul').width();
                let differenceWidth = fWidth - (ulWidth + 139);//差值
                if (differenceWidth >= 0){
                    this.ulLeft = 0;
                }else{
                    this.ulLeft = differenceWidth;
                }
            },
            /*向左移动*/
            goLeft(){
                if(this.ulLeft !== 0){
                    let fWidth = $('.ul-wapper').width();
                    this.ulLeft += fWidth;
                    if(this.ulLeft > 0){
                        this.ulLeft = 0;
                    }
                }
            },
            /*向右移动*/
            goRight(){
                let fWidth = $('.ul-wapper').width();
                let ulWidth = $('.ul-wapper ul').width();
                //this.ulLeft -= fWidth;
                //  -500 - 100 = -600
                // 550
                if(Math.abs(this.ulLeft - fWidth) < ulWidth){
                    this.ulLeft -= fWidth;
                }
            },
            /*拖拽移动*/
            move(e){
                /*  let xx = e.clientX;
  /!*                document.onmousemove = (e)=>{       //鼠标按下并移动的事件
                      //用鼠标的位置减去鼠标相对元素的位置，得到元素的位置
                      let left = e.clientX - disX;
                      let top = e.clientY - disY;

                      //绑定元素位置到positionX和positionY上面
                      this.positionX = top;
                      this.positionY = left;

                      //移动当前元素
                      odiv.style.left = left + 'px';
                      odiv.style.top = top + 'px';
                  };*!/
                  document.onmouseup = (e) => {
                      /!*document.onmousemove = null;
                      document.onmouseup = null;*!/
                      let x2 = e.clientX;
                      console.log(xx +'---'+x2)
                  };*/
            }
        },
        computed: {
            showTags() {
                return this.tagsList.length > 0;
            },
            tagLeft(){
                return $('.ul-wapper').width() - $('ul').width();
            }
        },
        watch:{
            $route(newValue, oldValue){
                this.setTags(newValue);
            }
        },
        created(){
            this.setTags(this.$route);
            //监听传过来的tab标签
            bus.$on('router', router => {
                this.tagsList.push(router);
                console.log('c')
            })
        }
    }

</script>


<style>
    .tags {
        position: relative;
        height: 30px;
        overflow: hidden;
        margin-top: 2px;
        background: rgb(25,28,40);
        box-shadow: 0 5px 10px #1A242F;
        display: flex;
    }
    .tags .el-button--primary{
        background-color: #343b56;
        border-color: #343b56;
    }
    .go-left,.go-right{
        display: inline-block;
        width: 30px;
        height: 30px;
        line-height: 30px;
        text-align: center;
        cursor: pointer;
    }
    .tags .ul-wapper {
        box-sizing: border-box;
        /*width: 100%;*/
        height: 100%;
        flex: 1;
        overflow: hidden;
        position: relative;
    }
    .ul-wapper ul{
        position: absolute;
        display: flex;
        transition: all 0.2s linear;
    }
    .tags-li {
        display: flex;
        /*margin: 3px 0px 2px 3px;*/
        /*        border-radius: 3px;*/
        font-size: 12px;
        overflow: hidden;
        cursor: pointer;
        height: 30px;
        line-height: 30px;
        margin-right: 1px;
        /*border: 1px solid #e9eaec;
        background: #fff;*/
        background: #191c28;
        border: 0;
        padding: 0 5px 0 12px;
        vertical-align: middle;
        color: #666;
        -webkit-transition: all .3s ease-in;
        -moz-transition: all .3s ease-in;
        transition: all .3s ease-in;
        -moz-user-select:none;
        -webkit-user-select:none;
        user-select:none;
    }

    .tags-li:not(.active):hover {
        background: #343b56;
    }
    .tags-li:not(.active):hover .tags-li-title{
        color: #fff;
    }
    .tags-li.active {
        color: #fff;
        background:#343b56;
        border-color: #343b56;
    }

    .tags-li-title {
        float: left;
        /*max-width: 80px;*/
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        margin-right: 5px;
        color: #aaa;
    }

    .tags-li.active .tags-li-title {
        color: #fff;
    }

    .tags-close-box {
        box-sizing: border-box;
        padding-top: 1px;
        text-align: center;
        width: 110px;
        height: 30px;
        background: rgb(25,28,40);
        box-shadow: -3px 0 15px 3px rgba(0, 0, 0, .1);
        z-index: 10;
    }
    .tag-item-wapper{
        display: flex;
    }
    .tag-span{
        display: inline-block;
        max-width: 100px;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }
    .el-icon-close:hover{
        color: red;
    }
</style>
