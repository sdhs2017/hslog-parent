<template>
    <div class="content-bg">
        <div class="top-title">填写版本信息</div>
        <div class="edit-wapper">
            <div class="container">
                <el-form label-width="70px">
                    <el-form-item label="版本号：">
                        <el-input v-model="versionTitle" placeholder="版本号"  class="versionTitle"></el-input>
                    </el-form-item>
                </el-form>

                <quill-editor ref="myTextEditor" v-model="content" :options="editorOption"></quill-editor>
                <el-button class="editor-btn" type="primary" @click="submit">提交</el-button>
            </div>
        </div>
    </div>
    
</template>

<script>
    import 'quill/dist/quill.core.css';
    import 'quill/dist/quill.snow.css';
    import 'quill/dist/quill.bubble.css';
    import { quillEditor } from 'vue-quill-editor';
    import { addQuillTitle } from 'static/js/quill-title.js';
    export default {
        name: 'editVersion',
        data: function(){
            return {
                versionTitle:'',
                content: '',
                editorOption: {
                    placeholder: '版本信息'
                }
            }
        },
        mounted(){
            //加载提示
            addQuillTitle();
        },
        components: {
            quillEditor
        },
        methods: {
            onEditorChange({ editor, html, text }) {
                this.content = html;
            },
            submit(){
                console.log(this.content);
                this.$message.success('提交成功！');
            }
        }
    }
</script>
<style scoped>
    .edit-wapper{
        padding: 10px 20px;
    }
    .container{
        background: #303e4e;
        border-radius:0;
        border: 1px solid #409eff;
    }
    .versionTitle{
        margin-bottom: 10px;
    }
    .editor-btn{
        margin-top: 20px;
    }
</style>
