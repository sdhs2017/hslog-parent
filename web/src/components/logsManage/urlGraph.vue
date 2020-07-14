<template>
    <div class="content-bg">
        <div class="top-title">{{domain_url}} 业务流</div>
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
        <div class="content" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
            <v-basegraph :nodeData="nodeData" :linkData="linkData" :nodeClick="nodeClick" :linkClick="linkClick"></v-basegraph>
        </div>
    </div>
    
</template>

<script>
    import vBasegraph from "@/components/common/baseGraph"
    import {savePath,jumpHtml} from "../../../static/js/common";

    export default {
        name: "urlGraph",
        data() {
            return {
                loading:false,
                domain_url:'',
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
                nodeData:[],
                linkData:[]
            }
        },
        created(){
            this.timepicker=[this.$route.query.starttime,this.$route.query.endtime]
        },
        watch:{
            'domain_url'(){
                this.getUrlGraphData(this.timepicker);
            }
        },
        methods:{
            //获取数据
            getUrlGraphData(timeArr){
                let url = '/flow/getVisitCountGroupByHttpSourceIP.do';
                let obj = {};
                if(this.$route.query.type === 'ip'){
                    url = '/flow/getDstIPCountGroupByHTTPSrcIP.do';
                    obj.ipv4_dst_addr = this.$route.query.val;
                }else{
                    obj.domain_url = this.domain_url;
                }
                obj.starttime=timeArr[0],
                obj.endtime=timeArr[1]
                this.loading = true;
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+url,this.$qs.stringify(obj))
                        .then(res=>{
                            this.loading = false;
                            let nodeArr = [];//点的集合数组
                            let linkArr = [];//线的集合数组

                            if(this.$route.query.type === 'ip'){
                                nodeArr.push({name:res.data[0].ipv4_dst_addr.ipv4_dst_addr,count: res.data[0].ipv4_dst_addr.count});
                            }else{
                                nodeArr.push({name:res.data[0].domain.domain_url,count: res.data[0].domain.count});
                            }
                            res.data[0].source.forEach(item=>{
                                let nodeObj = {};
                                nodeObj.name = item.source_ip;
                                nodeObj.count = item.count;
                                nodeArr.push(nodeObj);
                                let linkObj = {};
                                linkObj.source = item.source_ip;
                                if(this.$route.query.type === 'ip'){
                                    linkObj.target = res.data[0].ipv4_dst_addr.ipv4_dst_addr;
                                }else {
                                    linkObj.target = res.data[0].domain.domain_url;
                                }

                                linkObj.count = item.count;
                                linkArr.push(linkObj);
                            })
                            //赋值
                            this.nodeData = nodeArr;
                            this.linkData = linkArr;
                        })
                        .catch(err=>{
                            this.loading = false;
                            layer.msg('获取数据失败3',{icon:5});
                        })
                })
            },
            nodeClick(a){
                let obj={};
                obj.val = a.target._private.data.id;
                obj.clickType = "node";
                obj.starttime = this.timepicker[0]
                obj.endtime = this.timepicker[1]
                //判断值得类型
                if(obj.val.substring(0,4) === "http"){//点击的是url圆点
                    obj.type = "url";
                    obj.kname = "domain_url";
                }else{
                    obj.type = "ip";
                    obj.kname = "multifield_ip";
                }
                jumpHtml('flowLogs'+obj.val,'logsManage/flowLogs.vue',obj,'日志')
            },
            linkClick(a){
                let obj={};
                obj.val=a.target._private.data.source+'-'+a.target._private.data.target;
                obj.clickType = "link";
                obj.ipv4_src_addr = a.target._private.data.source;
                obj.starttime = this.timepicker[0]
                obj.endtime = this.timepicker[1]
                if(this.$route.query.type === 'ip'){
                    obj.type = "ip"
                    obj.ipv4_dst_addr = a.target._private.data.target;
                }else {
                    obj.type = "ip-url"
                    obj.targetVal = a.target._private.data.target;
                    obj.kname = "domain_url";
                }



                //跳转到流量日志页面
                jumpHtml('flowLogs'+obj.ipv4_src_addr+obj.targetVal,'logsManage/flowLogs.vue',obj,'日志');

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
                vm.domain_url = to.query.val;
                vm.$options.name = 'urlGraph'+ to.query.val;
                /*vm.busName = 'urlGraph'+ to.query.val;*/
                savePath(to.name,'logsManage/urlGraph.vue','业务流');
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
