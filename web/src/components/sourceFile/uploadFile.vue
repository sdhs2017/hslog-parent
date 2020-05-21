<template>
    <div class="content-bg">
        <div class="top-title">本地文件上传</div>
        <div class="upload-file-wapper">
            <p>通过浏览本地文件或将文件拖到下面指定区域，上传日志文件。</p>
            <p>文件支持的类型：<span class="txt-color">txt、log、json</span></p>
            <el-form ref="form" label-width="80px">
                <el-form-item label="日志类型">
                    <span style="color:red">*</span>
                    <el-select v-model="logType" placeholder="请选择">
                        <el-option label="syslog"  value="syslog"></el-option>
                        <el-option label="winlog"  value="winlog"></el-option>
                        <el-option label="log4j"  value="log4j"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <input type="file" multiple id="ssi-upload"/>

        </div>
    </div>
    
</template>

<script>
    export default {
        name: "uploadFile",
        data() {
            return {
                logType:'syslog'
            }
        },
        created(){

        },
        mounted(){
            $('#ssi-upload').ssi_uploader({
                url:this.$baseUrl+'/upload/uploadFile.do',//地址
                data:{"logType":this.logType},//除了文件额外的数据参数
                allowed:['log','txt',"json"]//允许上传文件的类型
            })
        }
    }
</script>

<style>
    .upload-file-wapper{
        padding:20px 50px;
    }
    .upload-file-wapper p{
        margin-bottom: 30px;
    }
    .txt-color{
        color: #409eff;
    }
    .ssi-dropZonePreview {
        border-color: #4781bb;
    }
    .ssi-imgToUploadTable{
        color: #fff;
        border: 1px solid #409eff;
        box-shadow: 0 10px 6px -6px #2d4c6a;
        background: #303e4e;
    }
    .ssi-dragOver .ssi-dropZone, .ssi-previewBox .ssi-dragOver{
        color:#40b056;
        border-color:#40b056;
    }
</style>
