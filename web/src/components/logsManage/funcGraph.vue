<template>
    <div class="content-bg">
        <div class="top-title">{{complete_url}} 业务流</div>
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
                linkData:[],
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
            }
        },
        watch:{
            'complete_url'(){
                this.getUrlGraphData(this.timepicker);
            }
        },
        created(){
            this.timepicker=[this.$route.query.starttime,this.$route.query.endtime]
        },
        methods:{
            //获取数据
            getUrlGraphData(timeArr){
                layer.load(1);
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getVisitCountOfComUrlGroupByHttpSourceIP.do',this.$qs.stringify({
                        complete_url:this.complete_url,
                        starttime:timeArr[0],
                        endtime:timeArr[1]
                    }))
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
                obj.starttime = this.timepicker[0]
                obj.endtime = this.timepicker[1]
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
                obj.starttime = this.timepicker[0]
                obj.endtime = this.timepicker[1]
                obj.clickType = "link";
                obj.type = "ip-url"
                obj.ipv4_src_addr = a.target._private.data.source;
                obj.targetVal = a.target._private.data.target;
                obj.kname = "complete_url";
                //跳转到流量日志页面
                jumpHtml('flowLogs'+obj.ipv4_src_addr+'-'+obj.targetVal,'logsManage/flowLogs.vue',obj,'日志')
            },
            //日期改变事件
            timepickerChange(){
                if(this.timepicker === null){
                    this.timepicker=['','']
                }
                this.getUrlGraphData(this.timepicker);
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
