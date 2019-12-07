<template>
    <div class="content-bg">
        <div class="top-title">版本信息</div>
        <div class="version-describe-wapper">
            <el-button @click="editVersion" type="primary" plain>编辑</el-button>
            <el-collapse v-model="activeNames">
                <div class="edition-box" v-for="(item,index) in versionData" :key="index">
                    <div class="edition-left edit-time">{{item.time}}</div>
                    <div class="edition-right">
                        <el-collapse-item :name="(index > 0) ? index : '1'">
                            <template slot="title">
                                <div class="ball"></div>
                                <div class="edition-val">{{item.num}}</div>
                            </template>
                            <div style="text-align: end"><span @click="reviseVersion(item)" class="reviseVersion">修改</span></div>
                            <div class="edition-content" v-html="item.content"></div>
                        </el-collapse-item>
                    </div>
                </div>
            </el-collapse>
        </div>

        <div class="edit-wapper" :style="{bottom:editBottom}">
            <div class="edit-title">编辑版本信息 <span class="cnacle" @click="cancleEdit">取消</span></div>
            <div class="container">
                <p>版本号</p>
                <el-form>
                    <el-form-item>
                        <el-input v-model="versionTitle" placeholder="版本号"  class="versionTitle"></el-input>
                    </el-form-item>
                </el-form>
                <p>版本信息</p>
                <quill-editor ref="myTextEditor" v-model="content" :options="editorOption"></quill-editor>
                <el-button class="editor-btn" type="primary" @click="submit">提交</el-button>
            </div>
        </div>
        <div class="zz" v-if="editBottom === '10px'"></div>
    </div>
    
</template>

<script>
    import 'quill/dist/quill.core.css';
    import 'quill/dist/quill.snow.css';
    import 'quill/dist/quill.bubble.css';
    import { quillEditor } from 'vue-quill-editor';
    export default {
        name: 'versionDescribe',
        data: function(){
            return {
                content: '',
                editorOption: {
                    placeholder: '',
                    modules:{
                        toolbar:[
                            ['bold', 'italic', 'underline', 'strike'],        // toggled buttons
                            ['blockquote', 'code-block'],
                            [{ 'header': 1 }, { 'header': 2 }],
                            [{ 'list': 'ordered' }, { 'list': 'bullet' }],
                            [{ 'size': ['small', false, 'large', 'huge'] }]
                        ]
                    }
                },
                activeNames:['1'],//展开的版本
                versionData:[], //版本数据
                versionTitle:'',//版本号
                editBottom:'-550px'
            }
        },
        created(){
            this.getVersionData();

            //this.editor.container.style.height = `${200}px`
        },
        methods: {
            /*获取版本信息列表*/
            getVersionData(){
               this.$nextTick(()=>{
                   layer.load(1);
                   //this.$axios.post(this.$baseUrl+'',this.$qs.stringify())
                   this.$axios.get('static/filejson/versionData.json',{})
                       .then(res=>{
                           layer.closeAll('loading');
                           this.versionData = res.data;
                       })
                       .catch(err=>{
                           layer.closeAll('loading');

                       })
               })
            },
            /*编辑版本信息*/
            editVersion(){
                this.editBottom = '10px';
            },
            /*取消编辑*/
            cancleEdit(){
                this.editBottom = '-550px';
                this.versionTitle = '';
                this.content = '';
            },
            /*修改版本信息*/
            reviseVersion(item){
                this.editBottom = '10px';
                setTimeout(()=>{
                    this.versionTitle = item.num;
                    this.content = item.content;
                },200)

            },
            onEditorChange({ editor, html, text }) {
                this.content = html;
            },
            submit(){
                console.log(this.content);
                this.$message.success('提交成功！');
            }
        },
        components: {
            quillEditor
        }
    }
</script>
<style scoped>
    .content>.content-bg {
        min-height: 100%;
        background: rgb(26,36,47);
        position: relative;
        overflow: hidden;
    }
    .version-describe-wapper{
        padding: 20px;
    }

    .editor-btn{
        margin-top: 20px;
    }
    .edition-box{
        width:1000px;
        margin:0 auto;
        /* height:500px; */
        border-radius:5px;
        color: #BFF6F6;
        display: flex;
    }
    .edition-left{
        width: 140px;
    }
    .edit-time{
        font-weight:600;
        font-size:16px;
        color: #e4956d;
        text-shadow: 3px 4px 3px #4e7ba9;
        height: 50px;
        line-height: 50px;
    }
    .edition-right{
        flex: 1;
        border-left: 6px solid #1b809e;
        padding-bottom: 40px;
    }
    .ball{
        width: 30px;
        height: 30px;
        border: 10px solid #1b809e;
        border-radius: 100%;
        background:#293846;
        position: relative;
        left: -40px;
        text-align: center;
        font-size: 16px;
        line-height: 30px;
        font-weight: 600;
        box-shadow: 0px 0px 12px rgba(0,255,255,0.5);
    }
    .edition-val{
        font-size:16px;
    }
    .edit-wapper{
        width: 100%;
        background: #303e4e;
        border-radius:0;
        border: 1px solid #409eff;
        position: absolute;
        left: 0;
        z-index: 10;
        transition: all 0.2s linear;
    }
    .container{
        background: #303e4e;
        width: 1000px;
        border: 0;
        margin: 0 auto;
    }
    .container p{
        font-size: 13px;
        margin-bottom: 5px;
    }
    .versionTitle{
        margin-bottom: 10px;
    }
    .editor-btn{
        margin-top: 20px;
        width: 100%;
    }
    .edit-title{
        height: 50px;
        line-height: 50px;
        background: #386c9a;
        padding-left: 20px;
    }
    .edit-title .cnacle{
        float: right;
        margin-right: 20px;
    }
    .edit-title .cnacle:hover{
        cursor: pointer;
        text-decoration: underline;
    }
    .reviseVersion{
        margin-right: 20px;
        cursor: pointer;
    }
    .zz{
        width: 100%;
        height: 100%;
        background: rgba(0,0,0,0.5);
        position: absolute;
        top: 0;
    }
</style>
