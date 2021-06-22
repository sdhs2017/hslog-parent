<template>
    <div>
        <el-dialog title="详情" :visible.sync="state" width="500px">
            <div style="max-height: 500px;overflow-y: auto;">
                <div class="row-wapper" v-for="(item,i) in columnHead">
                    <div class="row-tit">{{item.label}}</div>
                    <div class="row-val">{{rowData[item.prop]}}</div>
                </div>
            </div>
           <!-- <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveChart" :disabled="chartParams.chartName === '' ? 'disabled' : false">确 定</el-button>
            </div>-->
        </el-dialog>
    </div>
</template>

<script>
    import bus from '../common/bus';
    export default {
        name: "tableDetail",
        props:{
            dialogState:{
                type:Boolean,
            },
            columnHead:{
                type:Array
            },
            rowData:{
                type:Object
            },
            busName:{
                type:String
            }
        },
        data(){
            return{
                state:false
            }
        },
        watch:{
            'dialogState'(){
                console.log(this.dialogState)
                if(this.dialogState){
                    this.state = this.dialogState;
                }
            },
            'state'(){
                if(!this.state){
                    bus.$emit(this.busName,'false')
                }
            }
        },
        created() {
            this.state = this.dialogState;
        }
    }
</script>

<style scoped>
    .row-wapper{
        display: flex;
        align-items: start;
        color: #D6DFEB;
    }
    .row-tit{
        width: 100px;
    }
    .row-val{
        flex: 1;
    }
</style>
