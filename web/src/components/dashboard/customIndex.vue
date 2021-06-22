<template>
    <div style="display:flex;align-items: center;">
        <div class="tit">数据源：</div>
        <el-input v-model="value" size="mini" @focus="inputClick"></el-input>
<!--        <el-button type="success" plain @click="sendVal"  style="margin-left: 5px;" size="mini">确定</el-button>-->
        <el-dialog title="自定义数据源索引" :visible.sync="dialogValue" width="440px">
            <div>
                <el-input v-model="dialogVal" ref="dialogInput" size="mini"></el-input>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogValue = false">取 消</el-button>
                <el-button type="primary" @click="sendVal" :disabled="dialogVal === '' ? 'disabled' : false">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import bus from '../common/bus';
    export default {
        name: "customIndex",
        props:{
            defaultVal:{
                type:String
            },
            busName:{
                type:String
            }
        },
        directives: {
            focus: {
                inserted: function (el) {
                    el.querySelector('input').focus()
                }
            }
        },
        data(){
            return{
                value:'',
                dialogVal:'',
                dialogValue:false,
            }
        },
        created(){
            if(this.defaultVal){
                this.value = this.defaultVal
            }
        },
        watch:{
            'dialogValue'(){
                if(this.dialogValue){
                    // el.querySelector('input').focus()
                    this.$nextTick(()=>{
                        this.$refs.dialogInput.focus()
                    })
                }
            }
        },
        methods:{
            inputClick(){
                this.dialogVal = this.value;
                this.dialogValue = true
            },
            sendVal(){
                this.value = this.dialogVal;
                this.dialogValue = false;
                bus.$emit(this.busName,this.value)
            }
        }
    }
</script>

<style scoped>
    .tit{
        width: 117px;
        color: #409eff;
        text-shadow: none;
    }
</style>
