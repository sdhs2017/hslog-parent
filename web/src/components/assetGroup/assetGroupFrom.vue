<template>
    <div v-loading="loading" element-loading-background="rgba(48, 62, 78, 0.5)">
        <el-form ref="form" class="from-wapper" :model="form" label-width="100px">
            <el-form-item label="名称：">
                <span class="mustWrite">*</span>
                <el-input v-model="form.asset_group_name"></el-input>
            </el-form-item>
            <el-form-item label="区域：">
                <el-input v-model="form.asset_group_area"></el-input>
            </el-form-item>
            <el-form-item label="负责人：">
                <el-input v-model="form.asset_group_person"></el-input>
            </el-form-item>
            <el-form-item label="资产：">
                <span class="mustWrite">*</span>
                <el-transfer
                    filterable
                    :titles="['全部资产', '已选资产']"
                    :button-texts="['到左边', '到右边']"
                    v-model="form.asset_ids"
                    :data="equipmentList">
                </el-transfer>
            </el-form-item>
            <el-form-item label="说明：">
                <el-input type="textarea" v-model="form.asset_group_note"></el-input>
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
                equipmentList:[],
                form:{
                    asset_group_name:'',
                    asset_group_area:'',
                    asset_group_person:'',
                    asset_group_note:'',
                    asset_ids:[]
                },
                id:''
            }
        },
        watch:{
            'defaultFrom'(){
                this.form.asset_group_name = this.defaultFrom.asset_group_name
                this.form.asset_group_area = this.defaultFrom.asset_group_area
                this.form.asset_group_person = this.defaultFrom.asset_group_person
                this.form.asset_group_note = this.defaultFrom.asset_group_note
                this.form.asset_ids = this.defaultFrom.asset_ids
                this.form.asset_group_id = this.defaultFrom.asset_group_id
            }
        },
        created() {
            this.getEquipmentList();
        },
        methods:{
            /*获取全部资产*/
            getEquipmentList(){
               /* for (let i=1;i<20;i++){
                    this.equipmentList.push({
                        key:i,
                        label:`资产${i}`
                    })
                }*/

                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/assetGroup/getAssetList.do',this.$qs.stringify())
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success === 'true'){
                                this.equipmentList = obj.data
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
                if(this.form.asset_group_name === ''){
                    layer.msg('资产组名称不能为空',{icon:5})
                }else if(this.form.asset_ids.length === 0){
                    layer.msg('资产组中未选中任何资产',{icon:5})
                }else{
                    if(this.groupId !== ''){
                        this.form.asset_group_id = this.groupId
                    }
                    this.$nextTick(()=>{
                        this.loading = true;
                        this.$axios.post(this.$baseUrl+this.url,this.$qs.stringify(this.form))
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
                    asset_group_name:'',
                    asset_group_area:'',
                    asset_group_person:'',
                    asset_group_note:'',
                    asset_ids:[]
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
        left: -65px;
    }
</style>
