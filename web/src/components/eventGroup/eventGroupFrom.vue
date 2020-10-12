<template>
    <div v-loading="loading" element-loading-background="rgba(48, 62, 78, 0.5)">
        <el-form ref="form" class="from-wapper" :model="form" label-width="100px">
            <el-form-item label="名称：">
                <span class="mustWrite">*</span>
                <el-input v-model="form.event_group_name"></el-input>
            </el-form-item>
            <el-form-item label="linux：">
                <el-transfer
                    filterable
                    :titles="['全部事件', '已选事件']"
                    :button-texts="['到左边', '到右边']"
                    v-model="form.syslog_event_ids"
                    :data="syslogEventList">
                </el-transfer>
            </el-form-item>
            <el-form-item label="windows：">
                <el-transfer
                    filterable
                    :titles="['全部事件', '已选事件']"
                    :button-texts="['到左边', '到右边']"
                    v-model="form.winlog_event_ids"
                    :data="winlogEventList">
                </el-transfer>
            </el-form-item>
            <el-form-item label="说明：">
                <el-input type="textarea" v-model="form.event_group_note"></el-input>
            </el-form-item>
            <el-form-item style="display:flex;justify-content: space-evenly;">
                <el-button type="primary" @click="submitFrom">确定</el-button>
                <el-button @click="clearFrom">清空</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
    export default {
        name: "assetGroupFrom",
        props:{
            formType:{
                type:String,
                default(){
                    return''
                }
            },
            groupId:{
                type:String,
                default(){
                    return''
                }
            },
            url:{
                type: String,
                default(){
                    return''
                }
            },
            defaultFrom:{
                type:Object
            }
        },
        data(){
            return{
                loading:false,
                winlogEventList:[],
                syslogEventList:[],
                form:{
                    event_group_name:'',
                    event_group_note:'',
                    syslog_event_ids:[],
                    winlog_event_ids:[],
                },
                id:''
            }
        },
        watch:{
            'defaultFrom'(){
                this.form.event_group_name = this.defaultFrom.event_group_name
                this.form.event_group_note = this.defaultFrom.event_group_note
                this.form.syslog_event_ids = this.defaultFrom.syslog_event_ids
                this.form.winlog_event_ids = this.defaultFrom.winlog_event_ids
                this.form.event_group_id = this.defaultFrom.event_group_id
            }
        },
        created() {
            this.getEventList('winlogbeat');
            this.getEventList('syslog');
        },
        methods:{
            /*获取事件*/
            getEventList(type){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/eventGroup/getEventListByType.do',this.$qs.stringify({
                        log_type:type
                    }))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success === 'true'){
                                if(type === 'syslog'){
                                   this.syslogEventList = obj.data
                                }else if(type === 'winlogbeat'){
                                    this.winlogEventList = obj.data
                                }
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*提交数据*/
            submitFrom(){
                //判断参数合法性
                if(this.form.event_group_name === ''){
                    layer.msg('事件组名称不能为空',{icon:5})
                }else if(this.form.syslog_event_ids.length === 0 && this.form.winlog_event_ids.length === 0){
                    layer.msg('事件组中未选中任何事件',{icon:5})
                }else{
                    if(this.groupId !== ''){
                        this.form.event_group_id = this.groupId
                    }
                    this.$nextTick(()=>{
                        let obj = JSON.parse(JSON.stringify(this.form))
                        if(obj.winlog_event_ids.length === 0){
                            obj.winlog_event_ids = ''
                        }
                        if(obj.syslog_event_ids.length === 0){
                            obj.syslog_event_ids = ''
                        }
                        this.loading = true;
                        this.$axios.post(this.$baseUrl+this.url,this.$qs.stringify(obj))
                            .then(res=>{
                                this.loading = false;
                                let obj = res.data;
                                if(obj.success === 'true'){
                                    layer.msg(obj.message,{icon:1})
                                    //添加操作 成功 清除数据
                                    if(this.groupId === ''){
                                        this.clearFrom()
                                    }
                                }else{
                                    layer.msg(obj.message,{icon:5})
                                }
                            })
                            .catch(err=>{
                                 this.loading = false;
                            })
                    })
                }
            },
            /*清除数据*/
            clearFrom(){
                this.form={
                    event_group_name:'',
                    event_group_note:'',
                    syslog_event_ids:[],
                    winlog_event_ids:[],
                }
            }
        }
    }
</script>

<style scoped>
    .from-wapper>div{
        position: relative;
    }
    .mustWrite{
        color: red;
        position: absolute;
        top: 2px;
        left: -17px;
    }
</style>
