
<head>

    <meta http-equiv="Content-Type"content="text/html;charset=utf-8">
    <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %> 
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    

    <title>登录-宸析日志分析系统</title>
	<!-- <title>登录-中网云安日志审计系统</title> -->
    <link rel="shortcut icon" href="favicon.ico"> 
    <link rel="icon" type="image/x-icon" href="img/cx.ico" />
    <!-- <link rel="icon" type="image/x-icon" href="img/zwya_icon.ico" /> -->
    <link href="hplus/css/bootstrap.min.css?v=3.3.5" rel="stylesheet">
    <link href="hplus/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">
    <link href="hplus/css/animate.min.css" rel="stylesheet">
    <link href="hplus/css/style.min.css?v=4.0.0" rel="stylesheet">
    <style>
    	html{
    		height: 100%;
    	}
    	body{
    		height:100%;
    		width: 100%;
    		overflow:hidden;
    		background: url('img/download.jpg');
    		background-size:100% 100%;
    		filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src='img/download.jpg',sizingMethod='scale');
			-ms-filter: "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='img/download.jpg',sizingMethod='scale')";   		
    	}
    	.gray-bg .middle-box{
    		width:440px;
    		/* margin:0 auto; */
    		max-width:450px;
		    height: auto;
		    z-index:1000;
		    overflow: hidden;
		   /*  margin: 10% auto 0 auto; */
		   	box-shadow: 0 5px 30px #2c3d4e;
		    border-radius: 5px;
		    position:absolute;
		    top:25%;
		    left:50%;
		    /* background: rgba(255, 255, 255, 0.65); */
		    margin-left:-220px;
    	}
    	.middle-box img{
    		width: 370px;
    		margin-bottom: 20px;
    	}
    	#loginForm{
    		width:270px;
    		margin:20px auto;
    	}
    	.middle-box {
    		padding:30px 0 30px 0;
    	
    	}
    	#buttonLogin{
    		/* background:#009966; */
    		background:#2c76bd;
    		border:0;
    	}
    	.register,.certificate{
    		color:#ccc;
    	}
    	.register:hover,.certificate:hover{
    		color:#eee;
    		text-decoration:underline;
    	}
    	.aBox{
    		display:flex;
    		justify-content: space-between;
    	}
    	.certificate{
    		position: absolute;
		    right: 30px;
		    color: #2c76bd;
    	}
    	.layui-layer-iframe{
			background:0!important;
		}
		.layui-layer-iframe .layui-layer-title{
			background:#293846;
			color:#e4956d!important;
			border-bottom:1px solid #4781bb;
		}
		.layui-layer-setwin .layui-layer-close1 {
		    background-position: -187px -38px!important;
		}
    	/* .layui-layer-msg{
    		box-shadow: 0 5px 30px #2c3d4e!important;
		    border-radius: 5px;
		    border:0!important;
		    color:#fff!important;
    	}
    	.layui-layer{
    		background:#2c76bd!important;
    		
    		
    	} */
    </style>
    
    <!--[if lt IE 8]>
    <meta http-equiv="refresh" content="0;ie.html" />
    <![endif]-->
    <script src="hplus/js/jquery.min.js?v=2.1.4"></script>
    <script src="hplus/js/jquery.form.js"></script>
    <script src="hplus/js/bootstrap.min.js?v=3.3.5"></script>
<!--     <script type="text/javascript" src="http://tajs.qq.com/stats?sId=9051096" charset="UTF-8"></script> -->
    <script src="hplus/js/plugins/layer/layer.min.js"></script>
    <script src="js/ajax.js"></script>
</head>
<script>
$(function() {  
    //提交按钮点击事件
    $("#buttonLogin").click(function(){
    	var obj = {};
    	//获取登录名
    	obj.phone = $(".name").val();
    	//获取密码
    	obj.password = $(".password").val();
		layer.msg('登陆中,请稍等', {
		  icon: 16,
		  shade: 0.1,
		  time:0,
		  offset: '50px'
		});
		$.ajax({
			type:"post",
			url:"user/login.do",
			data:obj,
			timeout:30000,
			async:true,
			success:function(data){
				sessionStorage.setItem("htmlNum",0);
				console.log(data)
				//关闭进度条
				layer.closeAll('loading');
				if(data.success=="true"){
					//保存在本地
					localStorage.setItem("LoginUser", JSON.stringify(data.user));
	            	window.location.href="view/index.jsp";
	            }else if(data.success=="false"){
	            	layer.msg(data.message,{
						icon: 5,
						shade: 0.1,
						offset: '50px'
					});
	            }
			},
			error:function(data){
				//关闭进度条
				layer.closeAll('loading');
				if(data.status=0){
					layer.msg("登陆超时，请重试！",{
						icon: 5,
						shade: 0.1,
						offset: '50px'
					});
				}else{
					layer.msg('登录失败',{
						icon: 5,
						shade: 0.1,
						offset: '50px'
					});
				}
				
			}
		})   
    }) 
    //证书跟新
    $(".certificate").click(function(){
    	layer.open({
   		  type: 2,
   		  title: '证书更新',
   		 /*  shadeClose: true, */
   		  shade: 0.5,
   		  area: ['680px', '550px'],
   		  content: 'uploadCertificate.html' //iframe的url
   		});
    })
    
}); 
</script>
<body class="gray-bg">
    <div class="middle-box text-center loginscreen animated fadeInDown">
       	<img src="img/login_cx.png">
       	<!-- <img  src="img/zwya_logo.png"> -->
        <div>
           <!--  <h3>欢迎使用</h3> -->

            <form id="loginForm" class="m-t" role="form" onsubmit="return false">
                <div class="form-group">
                    <input class="form-control name" placeholder="用户名"  name="phone"  >
                </div>
                <div class="form-group">
                    <input type="password" class="form-control password" placeholder="密码"  name="password" >
                </div>
                <button id="buttonLogin" type="submit" class="btn btn-primary block full-width m-b">登 录</button>
                <a class="register" href="register.html">没有账号？点击注册</a>&nbsp;
            </form>
        </div>				
		<a class="certificate" >许可证更新</a>
    </div>
    <!-- 粒子效果 -->
    <script type="text/javascript" color="255,255,255" opacity='1' zIndex="2" count="50" src="js/canvas-nest.js"></script>
</body>

</html>