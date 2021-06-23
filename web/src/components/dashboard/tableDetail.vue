<template>
    <div>
        <el-dialog title="详情" :visible.sync="state" width="650px" >
            <div style="max-height: 500px;overflow-y: auto;">
                <div class="row-wapper" v-for="(item,i) in columnHead">
                    <div class="row-tit">{{item.label}}：</div>
                    <div class="row-val" v-html="dataChange(rowData,item.prop)"><!--{{rowData[item.prop]}}--><!--{{dataChange(rowData,item.prop)}}--></div>
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
                if(this.dialogState){
                    console.log(this.columnHead)
                    console.log(this.rowData)
                    this.state = this.dialogState;
                }
            },
            'state'(){
                if(!this.state){
                   bus.$emit(this.busName,this.state)
                }
            }
        },
        created() {
            this.state = this.dialogState;
        },
        methods:{
            //数据处理
            dataChange(obj,prop){
                let arr = prop.split('.');
                let currentData = obj;
                for (let i in arr){
                    if( currentData[arr[i]]){
                        currentData = currentData[arr[i]];
                    }else{
                        return  ''
                    }

                }
                return  currentData;
            },
        }
    }
</script>

<style scoped>
    .row-wapper{
        display: flex;
        align-items: start;
        color: #D6DFEB;
        margin: 10px 0;
        padding: 5px 10px;
    }
    .row-wapper:nth-child(even){
        background: #3a4b5f;
    }
    .row-tit{
        width: 110px;
        font-weight: 600;
    }
    .row-val{
        flex: 1;
    }
</style>
