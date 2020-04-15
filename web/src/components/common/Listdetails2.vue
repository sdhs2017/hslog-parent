<template>
    <div>
        <div id="con" ref="con">
           <!--<div class="qwe">{{this.detailsData}}</div>-->
            <ul class="details-list-wapper">
                <li v-for="(obj,index) in listArr" :key="index">
                    <el-row v-for="(item,index2) in obj" :key="index2">
                        <el-col :span="6"><div class="grid-content bg-purple details-list-name">{{item}}:</div></el-col>
                        <el-col :span="18"><div class="grid-content bg-purple-light details-list-val" v-html="dataChange(detailsData,index2)"></div></el-col>
                    </el-row>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
    import bus from '../common/bus';
    export default {
        name: "Listdetails2",
        data() {
            return{
                //显示的列数组
                listArr:[]
            }
        },
        props:{
            baseConfig:{
                type:Object,
                default:()=>{
                    return{
                        type:'1',//类型
                        title:'详情',//标题
                        areaWidth:'620px',//宽度
                        areaHeight:'520px'//高度
                    }

                }
            },
            detailsData:{
                type:Object
            }
        },
        created(){
            // //加载详情列表
            // if(this.detailsData.type === 'defaultpacket'){
            //     if(this.detailsData.application_layer_protocol === 'http'){
            //         this.listArr = this.$store.state.logshowcolumn[this.detailsData.application_layer_protocol].detailsListArr;
            //     }else{
            //         this.listArr = this.$store.state.logshowcolumn[this.detailsData.type].detailsListArr;
            //     }
            // }else if(this.detailsData.type === 'eventDetail'){
            //     this.listArr = this.$store.state.logshowcolumn[this.detailsData.type].detailsListArr;
            // }else{
            //     if (this.detailsData.agent.type){
            //         this.listArr = this.$store.state.logshowcolumn[this.detailsData.agent.type].detailsListArr;
            //     } else{
            //         this.listArr = this.$store.state.logshowcolumn[this.detailsData.type].detailsListArr;
            //     }
            //
            // }
            //获取数据详情列表
            this.$nextTick(()=>{
                this.$axios.get('static/filejson/log-event_config.json',{})
                    .then((res)=>{
                        let obj = res.data;
                        if(this.detailsData.type === 'eventDetail'){
                            this.listArr = obj.event[this.detailsData.type].detailsListArr;
                        }else{
                           this.listArr = obj.log[this.detailsData.agent.type].detailsListArr;
                        }
                        this.$nextTick(()=>{
                            this.createDetails();
                        })
                    })
                    .catch((err)=>{
                        console.log(err)
                    })
            })

        },
        mounted(){

        },
        methods:{
            //数据处理
            dataChange(obj,prop){
                let arr = prop.split('.');
                let currentData = obj;
                for (let i in arr){
                    currentData = currentData[arr[i]];
                }
                return  currentData;
            },
            /*生成弹窗*/
            createDetails(){
                layer.open({
                    type:this.baseConfig.type,
                    title:this.baseConfig.title,
                    area:[this.baseConfig.areaWidth,this.baseConfig.areaHeight],
                    content:$('#con').html()
                })
                //绑定关闭方法
                this.closeLayerFunc();
            },
            closeLayerFunc(){
                $('body').on('click','.layui-layer-close1',()=>{
                    //更改弹窗状态
                    bus.$emit('closeLayer','true');
                })
            }
        },
        beforeDestroy(){
        }
    }
</script>

<style scoped>
#con{
    display: none;
}
.details-list-wapper{
    /*width: 100%;
    height: 100%;
    overflow: auto;*/
    padding: 10px;
    font-size: 14px;
}
.details-list-wapper li{
    min-height: 40px;

}
.details-list-wapper li:nth-child(even){
    background: #303e4e;
}
.details-list-name{
    height: 30px;
    padding-top:10px;
    text-align: center;
}
.details-list-val{
    min-height: 20px;
    padding-top: 10px;
    padding-bottom: 10px;
    line-height: 20px;
    word-break: break-all;
}
</style>
