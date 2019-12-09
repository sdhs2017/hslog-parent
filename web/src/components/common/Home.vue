<template>
    <div class="wrapper">
        <v-head></v-head>
        <v-sidebar></v-sidebar>
        <div class="content-box" :class="{'content-collapse':collapse}">
            <v-tags></v-tags>
            <div class="content">
                <transition name="move" mode="out-in">
                    <keep-alive :include="tagsList">
                        <router-view :key="key"></router-view>
                    </keep-alive>
                </transition>
            </div>
        </div>
        <v-bottom></v-bottom>
    </div>
</template>

<script>
    import vHead from './Header.vue';
    import vSidebar from './Sidebar.vue';
    import vBottom from './Bottom.vue';
    import vTags from './Tags.vue';
    import bus from './bus';
    export default {
        data(){
            return {
                tagsList: [],
                collapse: false
            }
        },
        components:{
            vHead,
            vSidebar,
            vTags,
            vBottom
        },
        created(){
            bus.$on('collapse', msg => {
                this.collapse = msg;
            })
            // 只有在标签页列表里的页面才使用keep-alive，即关闭标签之后就不保存到内存中了。
            bus.$on('tags', msg => {
                let arr = [];
                for(let i = 0, len = msg.length; i < len; i ++){
                    msg[i].name && arr.push(msg[i].name);
                }
                this.tagsList = arr;
            })
            //获取日志展示列文件 存放到vuex中
            this.$nextTick(()=>{
                this.$axios.get('static/filejson/logconfig.json',{})
                    .then((res)=>{
                        this.$store.commit('updateLogShowColumn',res.data)
                    })
                    .catch((err)=>{

                    })
            })

        },
        computed: {
            // 默认情况下不同路由引入同一个组件 不触发created 数据作用域是同一个，加一个可以加以区别。
            key() {
               // console.log(this.$route.name !== undefined ? this.$route.name : this.$route)
                return this.$route.name !== undefined ? this.$route.name : this.$route
            }
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {

            })

        }
    }
</script>
