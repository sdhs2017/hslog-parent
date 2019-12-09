<template>
    <div class="content-bg">
        <div class="top-title">{{complete_url}} 业务流</div>
        <div class="content">
            <v-basegraph :nodeData="nodeData" :linkData="linkData" :nodeClick="nodeClick" :linkClick="linkClick"></v-basegraph>
        </div>
    </div>
    
</template>

<script>
    import vBasegraph from "@/components/common/baseGraph"
    import {savePath,jumpHtml} from "../../../static/js/common";

    export default {
        name: "funcGraph",
        data() {
            return {
                complete_url:'',
                nodeData:[],
                linkData:[]
            }
        },
        watch:{
            'complete_url'(){
                this.getUrlGraphData();
            }
        },
        methods:{
            //获取数据
            getUrlGraphData(){
                layer.load(1);
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getVisitCountOfComUrlGroupByHttpSourceIP.do',this.$qs.stringify({complete_url:this.complete_url}))
                        .then(res=>{
                            layer.closeAll();
                            let nodeArr = [];//点的集合数组
                            let linkArr = [];//线的集合数组
                            nodeArr.push({name:res.data[0].complete_url.complete_url,count: res.data[0].complete_url.count});
                            res.data[0].source.forEach(item=>{
                                let nodeObj = {};
                                nodeObj.name = item.source_ip;
                                nodeObj.count = item.count;
                                nodeArr.push(nodeObj);
                                let linkObj = {};
                                linkObj.source = item.source_ip;
                                linkObj.target = res.data[0].complete_url.complete_url;
                                linkObj.count = item.count;
                                linkArr.push(linkObj);
                            })
                            //赋值
                            this.nodeData = nodeArr;
                            this.linkData = linkArr;
                        })
                        .catch(err=>{
                            layer.closeAll();
                            layer.msg('获取数据失败',{icon:5});
                        })
                })
            },
            nodeClick(a){
                let obj={};
                obj.val = a.target._private.data.id;
                obj.clickType = "node";
                //判断值得类型
                if(obj.val.substring(0,4) === "http"){//点击的是url圆点
                    obj.type = "url";
                    obj.kname = "complete_url";
                }else{
                    obj.type = "ip";
                    obj.kname = "multifield_ip";
                }
                jumpHtml('flowLogs'+obj.val,'logsManage/flowLogs.vue',obj,'日志')
            },
            linkClick(a){
                let obj={};
                obj.clickType = "link";
                obj.type = "ip-url"
                obj.ipv4_src_addr = a.target._private.data.source;
                obj.targetVal = a.target._private.data.target;
                obj.kname = "complete_url";
                //跳转到流量日志页面
                jumpHtml('flowLogs'+obj.ipv4_src_addr+'-'+obj.targetVal,'logsManage/flowLogs.vue',obj,'日志')
            }
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                vm.complete_url = to.query.complete_url;
                vm.$options.name = 'funcGraph'+ to.query.complete_url;
                /*vm.busName = 'urlGraph'+ to.query.val;*/
                savePath(to.name,'logsManage/funcGraph.vue','业务流');
            })
        },
        components:{
            vBasegraph
        }
    }
</script>

<style scoped>
    .content{
        min-height: 810px;
        position:relative;
        padding: 10px;
    }
</style>
