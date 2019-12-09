	var sendObj = {};//传参对象
	sendObj.pageSize = 10;
	sendObj.departmentId = "";
	var screenHeight = $(window).height();//获得浏览器当前窗口可视区域高度
	$(".table-responsive").height(screenHeight - 300);//设置高度
	$(".user_tree").height(screenHeight - 140);//设置高度
	//初始化加载组织机构
	 getOrgTree($(".treeBox"),{level:1})
	//请求数据  组织+用户  $this-添加树形菜单的位置  depObj--参数
	 function getOrgTree($this,depObj){
		 $this.children("span").next(".loadingBox").append('<img class="treeLoading" src="img/loading.gif" />')
		//发送请求
		$.ajax({
			type:"post",
			url:"../../department/selectAll.do",
			data:depObj,
			async:true,
			success:function(data){
				$(".treeLoading").remove();
				//判断数据是否为空
				/*if(data.department.length == 0 && data.user[0].length == 0){
					layer.msg('暂无子目录',{icon: 5});
				}else{
					//加载组织机构列表
					orgTree($this,data.department);
					//判断是否有用户数据
					if(data.user != undefined){
						userTree($this,data.user[0]);
						//加载用户列表
						var userListHtmls = splitUserList(data.user[0]);
						 //添加到页面中
						$('#user_list>tbody').html(userListHtmls);
					}
					
				}*/
				//加载组织机构列表
				orgTree($this,data.department);
				//判断是否有用户数据
				if(data.user != undefined){
					userTree($this,data.user[0]);
					//加载用户列表
					var userListHtmls = splitUserList(data.user[0]);
					 //添加到页面中
					$('#user_list>tbody').html(userListHtmls);
				}
			},
			error:function(err){
				
			}
		})
	}
	
	//机构树 拼串 $this-存放的地方  data-返回的数据
	function orgTree($this,data){
		 
		  var  treeHtml = '';
		  for (var i = 0; i < data.length; i++) {
			  //去除数据中的null 替换为''
			  data[i] = replaceNull(data[i]);
			  //判断是不是最外层元素  是则添加到rootUL盒子里
	            if (data[i].superiorId == '') {
	            	treeHtml += "<li data-id='" + data[i].id + "' data-level='" + data[i].level + "' data-name='" + data[i].name + "' data-superiorId='" + data[i].superiorId + "' data-subordinate='" + data[i].subordinate + "' data-orderId='" + data[i].orderId + "' data-comment='" + data[i].comment + "'><span  onclick='spanClick()'><i class='glyphicon glyphicon-home'></i> " + data[i].name + "</span><b class='loadingBox'></b></li>";
	            }else{
	            	/* // 判断是否有子集元素
	                if(data[i].isSubordinate == 1){
	                	icon = "glyphicon-triangle-right";
	                }else{
	                	icon = "glyphicon-user";
	                }*/
	            	icon = "glyphicon-triangle-right";
	                //拼接子目录               
	                treeHtml += "<li data-id='" + data[i].id + "' data-level='" + data[i].level + "' data-name='" + data[i].name + "' data-superiorId='" + data[i].superiorId + "' data-subordinate='" + data[i].subordinate + "' data-orderId='" + data[i].orderId + "' data-comment='" + data[i].comment + "'><span  onclick='spanClick()'><i class='glyphicon " + icon + "'></i> " + data[i].name + "</span><b class='loadingBox'></b></li>"	            	
	            	
	            }
	      }
		 //判断 当前是够有子目录 即ul
		  if($this.children("ul").length == 0){
			  $this.append("<ul></ul>")
		  }else{
			 
		  }
		  $this.children("ul").html(treeHtml);
		 
	}
	
	//拼接机构数 用户  $this-存放的地方  data-返回的数据
	function userTree($this,data){
		var  treeHtml = '';
		icon = "glyphicon-user";
		for (var i = 0; i < data.length; i++) {
			 treeHtml += "<li data-id='" + data[i].id + "' data-departmentId='" + data[i].departmentId + "'><span  onclick='spanClick()'><i class='glyphicon " + icon + "'></i> " + data[i].name + "</span></li>"
		}
		 //判断 当前是够有子目录 即ul
		  if($this.children("ul").length == 0){
			  $this.append("<ul></ul>")
		  }else{
			 
		  }
		$this.children("ul").append(treeHtml);
	}
	var depBtnType = '';
	
	//添加组合机构按钮点击事件
	$(".create").click(function(){	
		depBtnType = "addBtn";
		//获得当前选中的节点
		var $currentDom = $(".treeBox .onClickDom");
		var depObj = {};
		var level = 1;
		var subordinate = 0;
		var $p = $(".treeBox");
		//判断是否已经存在根节点
		 if($currentDom.length == 0){
			 
		 }else{
			 
			 $p = $currentDom.parent();
			 //获得其父节点的的level值
			 var level = $currentDom.parent().attr("data-level");
			 //获得其父节点的id值
			 var pId = $currentDom.parent().attr("data-id");
			 depObj.id = pId;
			 
		 }
		 depObj.level = level;
		 depObj.subordinate = subordinate;
		 departmentHtml(depBtnType,depObj)

	})
	
	//删除组织机构按钮事件
	$(".remove").click(function(){
		var depObj = {};
		//获得当前选中的节点
		var $currentDom = $(".treeBox .onClickDom");
		if($currentDom.length == 0){
			 layer.msg('未选中任何组织或用户',{icon:5})
		 }else{
			 //获得其父节点的id值
			 var pId = $currentDom.parent().attr("data-id");
//			 depObj.id = pId;
			//询问框
			layer.confirm('您确定删除么？', {
			  btn: ['确定','取消'] //按钮
			}, function(index){
				layer.close(index);
				 //发送数据到后台 进行删除
				 $.ajax({
					type:"post",
					url:"../../department/delete.do",
					data:{"id":pId},
					async:true,
					success:function(data){
						//判断是否删除成功
						if(data){
							layer.msg('删除成功',{icon: 1});
							//重新加载组织结构树
							getOrgTree($(".treeBox"),{level:1})
						}else{
							layer.msg('删除失败，该目录存在子集',{icon:5})
						}
						
					},
					error:function(err){
						
					}
				})
				//关闭弹窗
				layer.close();
			}, function(){
			  layer.close();
			});
			
			 
		 }

	})
	
	///修改组织机构按钮事件
	$(".edit").click(function(){
		depBtnType = "editBtn";
		//获得当前选中的节点
		var $currentDom = $(".treeBox .onClickDom");
		var depObj = {};
		if($currentDom.length == 0){
			 layer.msg('未选中任何组织或用户',{icon:5})
		 }else{
			 //获的数据
			 depObj.name = $currentDom.parent("li").attr("data-name");
			 depObj.id = $currentDom.parent("li").attr("data-id");
			 depObj.level = $currentDom.parent("li").attr("data-level");
			 
			 if($currentDom.parent("li").attr("data-superiorId") !=null  && $currentDom.parent("li").attr("data-superiorId")!='null'){
				 depObj.superiorId = $currentDom.parent("li").attr("data-superiorId")
			 }
			 
			 depObj.subordinate = $currentDom.parent("li").attr("data-subordinate");
			 if($currentDom.parent("li").attr("data-orderId")!=null && $currentDom.parent("li").attr("data-orderId") !='null'){
				 depObj.orderId = $currentDom.parent("li").attr("data-orderId");
			 }
			 
			 depObj.comment = $currentDom.parent("li").attr("data-comment");
			 departmentHtml(depBtnType,depObj)
		 }
	})
	
	//部门 弹窗html
	function departmentHtml(btnType,depObj){
		var layerTitle = "添加";
		if(btnType == "editBtn"){
			layerTitle = "修改"
		}
		 var layerHtml = '<form class="form-horizontal">'
					+		'<div class="form-group">'
			 		+			'<label for="inputEmail3" class="col-sm-2 control-label">名称：</label>'
			 		+			'<div class="col-sm-10">'
			 		+				'<input type="text" class="form-control departmentName">'
			 		+			'</div>'
					+		'</div>'
					+		'<div class="form-group">'
			 		+			'<label for="inputEmail3" class="col-sm-2 control-label">描述：</label>'
			 		+			'<div class="col-sm-10">'
			 		+				'<textarea class="form-control departmentComment" rows="8"></textarea>'
			 		+			'</div>'
					+		'</div>'		
					+	 '</form>'
		 layer.open({				
		 		type: 1,
		 		title:[layerTitle,''],//标题
				area: ['450px', '400px'], //宽高
				btn: ['确定','取消'], //按钮
				btn1:function(index){
					//获得输入框中的值
					var name = $(".departmentName").val();
					var comment = $(".departmentComment").val();
					depObj.name = name;
					depObj.comment = comment;
					if(name == ""){
						layer.msg("部门名称不能为空",{icon:5});
					}else{
						//判断不同的按钮类型
						if(btnType == "addBtn"){
							
							//发送参数 获取数据
			  				createOrgTree('',depObj)
						}else if(depBtnType == "editBtn"){
							$.ajax({
								type:"post",
								url:"../../department/updateById.do",
								data:depObj,
								async:true,
								success:function(data){
									layer.msg('修改成功',{icon: 1});
									//重新加载组织结构树
									getOrgTree($(".treeBox"),{level:1})
								},
								error:function(err){
									
								}
							})
						}
						//关闭弹窗
				  		layer.close(index);
					}
				},
		 		content: layerHtml
			});
		 //判断按钮类型
		 if(depBtnType == "editBtn"){
			 //添加数据
			 $(".departmentName").val(depObj.name);
			 $(".departmentComment").val(depObj.comment);
		 }
	}
	
	//新建组织机构树  depObj-传的参数集合 $this-需要加载数据的父级标签
	function createOrgTree($this,depObj){
		//发送请求
		$.ajax({
			
			type:"post",
			url:"../../department/insert.do",
			data:depObj,
			async:true,
			success:function(data){
				layer.msg('添加成功',{icon: 1});
				//重新加载组织结构树
				getOrgTree($(".treeBox"),{level:1})
			},
			error:function(err){
				
			}
		})
	}
	
	$(".user_tree").click(function(ev){
		 $(".treeBox span").removeClass("onClickDom");
		/* if(ev.target.nodeName == "span"){
			 spanClick();
		 }*/
	})
	 
	//机构数span点击事件
	function spanClick(){
		//储存当前点击的对象；
		var ev = arguments.callee.caller.arguments[0] || window.event;  
	    var $this =$(ev.target);
	    //判断当前点击的标签  如果是i标签或者是用户 则
	    if($this[0].nodeName == "I" ){	    	
	    	
	    }else if($this.children("i").hasClass("glyphicon-user")){ //点击的是用户
	    	//获取部门id
	    	depObjId = $this.parent("li").attr("data-id");
	    	sendObj.departmentId = $this.parent("li").attr("data-departmentId");
	    	//发送请求 获取用户数据
	    	$.ajax({
				type:"post",
				url:"../../users/selectUser.do",
				data:{"id":depObjId},
				async:true,
				success:function(data){
					//加载用户列表
					var userListHtmls = splitUserList(data[0]);
					 //添加到页面中
					$('#user_list>tbody').html(userListHtmls);
					//移除请求过得状态
					$(".treeBox span").removeClass("overget");
				},
				error:function(data){
					
				}
			});
	    }else{
	    	//判断点击的是部门名称 还是 公司名称
	    	if($this.children("i").hasClass("glyphicon-triangle-right") || $this.children("i").hasClass("glyphicon-triangle-bottom")) {//部门
	    		//获得公司名称
	    		var treeComName = $this.parents('li[data-level = "1"]').attr("data-name");
	    		var userComName = $(".companyName").html();
	    		/*if(treeComName == userComName){
	    			
	    		}else{
	    			$(".companyName").html(treeComName)
	    		}*/
	    		$(".companyName").html($this.text())
	    		
	    	}else{//公司
	    		//添加部门名称
	    		$(".companyName").html($this.text())
	    	}
	    	//新建对象 用于存放参数
			var depObj = {};		
		    //删除其他span中的class
		    $(".treeBox span").removeClass("onClickDom");
		    //给当前点击的span 添加class名
		    
		    if($this.hasClass("onClickDom")){
		    	$this.removeClass("onClickDom");
		    }else{
		    	$this.addClass("onClickDom");
		    }
		    //获取其父级标签
		    var pLi = $this.parent();
		   //获取当前父级元素的id
		    var pId = pLi.attr("data-id");
		    depObj.id = pId;
		    sendObj.departmentId = pId;
		    //判断是否已经请求加载过数据
		    if($this.hasClass("overget")){
		    	
		    }else{
		    	 //获取数据并显示
			    getOrgTree(pLi,depObj);	  
		    }
		    $(".treeBox span").removeClass("overget");
		  //给当前点击的span标记为已经点击  （添加overget类）
	    	$this.addClass("overget");
		    
		   // $('.treeBox li:has(ul)').addClass('parent_li').find(' > span').attr('title', 'Collapse this branch');
		    //获取要展开或收缩的里标签
			var children = $this.parent('li').find(' > ul ');
			//判断li状态  是visible 则隐藏  否则展示
			if (children.is(":visible")) {
				children.hide('fast');
				if($this.children('i').attr('class') !== 'glyphicon glyphicon-home'){
					//切换i标签
					$this.attr('title', 'Expand this branch').find(' > i').addClass('glyphicon-triangle-right').removeClass('glyphicon-triangle-bottom');
				}					
			} else {
				children.show('fast');
				if($this.children('i').attr('class') !== 'glyphicon glyphicon-home'){
					//切换i标签
					$this.attr('title', 'Collapse this branch').find(' > i').addClass('glyphicon-triangle-bottom').removeClass('glyphicon-triangle-right');
				}
			}	
			//阻止冒泡事件
			ev.stopPropagation();
			
	    }
		
	}
	
	
	
	
	
	var num;
//	$("#rootUL").on("click","")
	//定义每页显示的条数
	var pageSize = 3;
	//加载用户列表函数  
	function getUserList(pageIndex,obj){
		searchStutus = false;
		obj.pageIndex = pageIndex;
		$.ajax({
			type:"post",
			url:"../../users/selectAlls.do",
			data:obj,
			async:true,
			success:function(data){
				//用户数据
				var userData = data.user[0];
				
				//判断是否是第一次请求  第一次创建分页
				if(pageIndex == 1){
					//定义第一次请求
					num = false;
					//总条数
					var count = data.count[0].count;

					//总页数
					var pageCount = Math.ceil(count/pageSize);
					//创建分页
					creatPage(pageCount);
					//调用拼接字符串函数
					var userListHtmls = splitUserList(userData);
					 //添加到页面中
					$('#user_list>tbody').html(userListHtmls);
					num = true;
				}else{
					//调用拼接字符串函数
					var userListHtmls = splitUserList(userData);
					 //添加到页面中
					$('#user_list>tbody').html(userListHtmls);
				}
				
								
			},
			error:function(err){
				console.log(err);
			}
		})
	}
	
	//拼接用户列表字符串函数
	function splitUserList(data){
		var userHtml = '';
		for(var i in data){
			var userObj = data[i];
			//循环替换值为null的  替换成'';
			for(var j in userObj){
				if(userObj[j] == null){
					userObj[j] = ' -';
				}
			}
			var img = '1';//人员头像
			var userSex = "男";
			//转换 
			if(userObj.sex == 1){
				
			}else{ 					
				userSex = "女";
				img = "2";
			}
			userHtml += '<tr>'   
					+		 '<td> <input type="checkbox" name="'+userObj.id+'"></td>'
					+		 '<td><img alt="image" src="img/'+img+'.jpg" /></td>'
                    +        '<td class="user_name">'
                    +        	'<span id="'+userObj.id+'">'+userObj.name+'</a>'
                    +        '</td>'
                    +        '<td class="user_sex">'+userSex+'</td>'
                    +        '<td class="user_age">'+userObj.age+'</td>'
                    +        '<td class="user_jobPosition" departmentId="'+userObj.departmentId+'">'+userObj.departmentName+'</td>'                                                          							
                    +        '<td class="user_email"><i class="glyphicon glyphicon-envelope"> </i> <span>'+userObj.email+'</span></td>'
                    +       	'<td class="user_phone"><i class="glyphicon glyphicon-earphone"> </i> <span>'+userObj.phone+'</span></td>'
                    +       	'<td class="td_tools">'
                    +       		'<i class="glyphicon glyphicon-edit user_revise" onclick="btnRevise()" title="修改"> </i>'
                    +       		'<i class="glyphicon glyphicon-remove" onclick="removeUser()" title="删除"> </i>'
                    +       		'<i class="glyphicon glyphicon-list-alt" title="查看详情"> </i>'
                    +       	'</td>'
                    +    '</tr>'
                                      
		}
		
		return userHtml;
		
	}
	
	//创建分页函数   pageCount-总页数
	function creatPage(pageCount){
		$(".tools_page").pagination1(pageCount, {
				num_edge_entries: 1, //边缘页数
				num_display_entries: 4, //主体页数
				callback: pageselectCallback,
				items_per_page: 1, //每页显示1项
				prev_text: "&laquo;",
				next_text: "&raquo;"
			});
	}
	//分页回调函数
	function pageselectCallback(page_index){
		$("#theadCheck").removeAttr("checked"); 
		if(num == true){
			//判断是否是搜索产生的分页  false-不是
			if(searchStutus = false){
				getUserList(pageIndex+1,obj);
			}else{
				//调用函数显示数据
				searchUsers(page_index+1);
			}
			
		}
		
	}
	
	
	//定义一个object 用于存放弹窗中的form信息
	var userObj = {};
	
	//点击添加按钮 执行函数
	$(".user_add").click(function(){
		//调用函数
		layerHtmls();
		
	})
	//点击删除按钮 执行函数
	$(".user_removeMore").click(function(){
		//获取被选择的复选框
		var checkboxs = $("#user_list>tbody input[type=checkbox]:checked");
		var userId = '';
		//判断是否又被选中的  没有则提示 有则继续执行删除操作
		if(checkboxs.length == 0){
			layer.msg('未选中任何用户',{icon: 5});
		}else{
			//循环拼接id值
			for(var i = 0; i< checkboxs.length;i++){
				userId += checkboxs[i].getAttribute("name")+',';
			}	
			//调用删除函数
			removeUser(userId)
		}
		
	})
	//表头中的复选框 点击全选或取消全选
	$('#theadCheck').click(function(){	
		//如果checkbox_Status == 0 则未选中
		if($('#theadCheck')[0].checked == true){
			//更改状态
			$("#user_list>tbody input[type=checkbox]").prop("checked", "checked");
		}else if($('#theadCheck')[0].checked == false){
			//更改状态
			$("#user_list>tbody input[type=checkbox]").removeAttr("checked"); 
		}
	})
	

	//用户修改按钮执行 函数
	function btnRevise(){
		//设置不可修改的选项
		var btnType = "revise";
		//定义一个对象
		var obj = {};
		//储存当前点击的对象；
		var ev = event || window.event || arguments.callee.caller.arguments[0];  
	    var $this =$(ev.target);  
		/*//获得本行的name 值
		obj.userName = $this.parent().siblings(".user_name").children("span").html();
		//获得本行的sex 值
		obj.userSex = $this.parent().siblings(".user_sex").html();
		//获得本行的电话 值
		obj.userPhone =$this.parent().siblings(".user_phone").children("span").html();
		//获得本行的email 值
		obj.userEmail =$this.parent().siblings(".user_email").children("span").html();*/
		//获得本行id 值
		var id = $this.parent().siblings(".user_name").children("span").attr("id");
		/*//获得本行age 值
		obj.userAge = $this.parent().siblings(".user_age").html();
		//获得部门的id
		obj.departmentId =  $this.parent().siblings(".user_jobPosition").attr("departmentId");
		//存放数据
*///		obj.userName = userName;
		//发送请求获得用户数数据
		$.ajax({
			type:"post",
			url:"../../user/selectById.do",
			data:{id:id},
			async:true,
			success:function(data){
				var obj = JSON.parse(data);
				//调用弹窗  函数
				layerHtmls(btnType,obj,$this);
			},
			error:function(data){
				layer.msg('信息获取失败',{icon: 5});
			}
		});
		
		
		
	}
	//删除用户
	function removeUser(id){
		//判断是批量删除 还是单个删除  （批量删除会传参）
		if(id == undefined){
			var ev = event || window.event || arguments.callee.caller.arguments[0];
		    var $this =$(ev.target); 
			var id = $this.parent().siblings(".user_name").children("span").attr("id");
		}
		//询问框
		layer.confirm('您确定删除此用户么？', {
		  btn: ['确定','取消'] //按钮
		}, function(index){
			layer.close(index);
			$.ajax({
				type:"post",
				url:"../../users/deletes.do",
				data:{"ids":id},
				async:false,
				success:function(data){
					layer.msg('删除成功',{icon: 1});
					//更新用户列表
//					getUserList();
					getUserList(1,sendObj);
				},
				error:function(err){
					layer.msg('删除失败',{icon: 5});
					console.log(err)
				}
			})
			//关闭弹窗
			layer.close();
		}, function(){
		  layer.close();
		});
	}
		
	//弹窗 函数（用户）    btnType-按钮类型  obj-数据对象（用于修改按钮点击时 添加数据到弹窗） $this-当前点击按钮对象
	function layerHtmls(btnType,obj,$this){
		userObj = {};
		var layerTitle = "添加用户";
		//弹窗 修改 、添加  form表单 html代码
		var layHtml = 	'<form class="form-horizontal">'					
	  				+		'<div class="form-group">'
	   		 		+			'<label for="inputEmail3" class="col-sm-3 control-label">电话(账号)<span style="color:red">*</span>：</label>'
	    			+			'<div class="col-sm-8">'
	      			+				'<input type="tel" class="form-control form_userPhone" placeholder="电话(账号)11位有效数字">'
	    			+			'</div>'
	  				+		'</div>'
	  				+		'<div class="form-group passw">'
	   		 		+			'<label for="inputEmail3" class="col-sm-3 control-label">密码<span style="color:red">*</span>：</label>'
	    			+			'<div class="col-sm-8">'
	      			+				'<input type="password" class="form-control form_password1" placeholder="输入密码">'
	    			+			'</div>'
	  				+		'</div>'
	  				+		'<div class="form-group passw">'
	   		 		+			'<label for="inputEmail3" class="col-sm-3 control-label">确认密码<span style="color:red">*</span>：</label>'
	    			+			'<div class="col-sm-8">'
	      			+				'<input type="password" class="form-control form_password2" placeholder="确认密码">'
	    			+			'</div>'
	  				+		'</div>'
	  				+		'<div class="form-group">'
	   		 		+			'<label for="inputEmail3" class="col-sm-3 control-label">姓名<span style="color:red">*</span>：</label>'
	    			+			'<div class="col-sm-8">'
	      			+				'<input type="text" class="form-control form_username" placeholder="姓名">'
	    			+			'</div>'
	  				+		'</div>'
	  				+		'<div class="form-group">'
	   		 		+			'<label for="inputEmail3" class="col-sm-3 control-label">部门<span style="color:red">*</span>：</label>'
	    			+			'<div class="col-sm-8">'
	      			+				'<input class="easyui-combotree combotree_input" data-options="" style="width:100%">'
	    			+			'</div>'
	  				+		'</div>'
	  				+		'<div class="form-group">'
	   		 		+			'<label for="inputEmail3" class="col-sm-3 control-label">性别：</label>'
	    			+			'<div class="col-sm-8 sex_box">'
	      			+				'<input type="radio" name="sex" value="1" checked class="nan">男'
	      			+				'<input type="radio" name="sex" value="0" class="nv">女'
	    			+			'</div>'
	  				+		'</div>'
	  				+		'<div class="form-group">'
	   		 		+			'<label for="inputEmail3" class="col-sm-3 control-label">年龄：</label>'
	    			+			'<div class="col-sm-8">'
	      			+				'<input type="text" class="form-control form_userage" placeholder="年龄">'
	    			+			'</div>'
	  				+		'</div>'
	  				
	  				+		'<div class="form-group">'
	   		 		+			'<label for="inputEmail3" class="col-sm-3 control-label">邮箱：</label>'
	    			+			'<div class="col-sm-8">'
	      			+				'<input type="email" class="form-control form_userEmail" placeholder="邮箱">'
	    			+			'</div>'
	  				+		'</div>'
	  				+	'</form>'
	  				
		if(btnType == "revise"){
			layerTitle = "修改用户信息";
			
		}
		//弹窗
		layer.open({ 
	 		type: 1,
	 		title:[layerTitle,'font-size:18px;border-bottom:1px solid #ccc;height:60px'],//标题
  			area: ['520px', '550px'], //宽高
  			btn: ['确定','取消'], //按钮
  			btn1:function(index){
  				//获得用户姓名
  				var user_name = $(".form_username").val();
  				//获得性别
  				var user_sex = $("input[name='sex']:checked").val();
  				//获得用户电话
  				var user_phone = $(".form_userPhone").val();
  				//获得用户年龄
  				var user_age = $(".form_userage").val();
  				//获得用户密码
  				var user_pw = $(".form_password1").val();
  				var user_pw2 = $(".form_password2").val();
  				//获得用户邮箱
  				var user_email = $(".form_userEmail").val();
  				//获取用户部门id
  				var user_departmentId = $(".combotree_input").val();
  				//判断age 并转换 
  			/*	if(user_age==null||user_age==''){
  					user_age=999;
  				}*/
  				//存储
  				userObj.name = user_name;
  				userObj.sex = user_sex;
  				userObj.phone = user_phone;
  				userObj.age = user_age;
  				userObj.password = user_pw;
  				userObj.Email = user_email;
  				userObj.departmentId = user_departmentId;
  				
  				//判断是哪个按钮的点击事件  如果是btnType == "revise" 则是修改按钮触发的 否则是添加按钮触发的
  				if(btnType == "revise"){
  					//删除 对象中的密码属性
  					delete userObj.password;
  					//添加id属性
  					userObj.id = obj.userId; 					
					//执行修改函数
					reviseUser(userObj,$this);
					//关闭弹窗
			  		layer.close(index);
				}else{	

					if(user_phone == '' || user_phone.length != 11 || !$.isNumeric(user_phone)){
						layer.msg('电话（账号）不能为空或格式不正确',{icon: 5});
					}else if(user_pw != user_pw2 || user_pw ==''){
						layer.msg('两次密码输入不一致或密码为空',{icon: 5});
					}else if(user_departmentId == ''){
						layer.msg('未选中任何部门',{icon: 5});
					}else if(user_name == ''){					
						layer.msg('姓名不能为空',{icon: 5});
					}else{
						//执行添加函数
						addUser(userObj);	
						//关闭弹窗
				  		layer.close(index);
					}
									
				}
  				
  			},
 	 		content: layHtml
		});
		//判断是哪个按钮的点击事件  如果是btnType == "revise" 则是修改按钮触发的 否则是添加按钮触发的
		if(btnType == "revise"){
			//删除密码框
			$(".passw").remove();
			
			if(obj.userSex == "男"){
				
			}else{
				$(".nan").removeAttr("checked");
				$(".nv").attr("checked","checked");
			}
			//添加信息
			$(".form_username").attr("value",obj.name);
			$(".form_userPhone").attr("value",obj.phone);
			$(".form_userEmail").attr("value",obj.email);
			$(".form_userage").attr("value",obj.age);
			$(".combotree_input").attr("value",obj.departmentId);
			
			
		}else{			
			$(".form_username").removeAttr("disabled");
			
		}
		//重新渲染
//		$.parser.parse();
		//下拉树形菜单
		$(".combotree_input").attr("data-options","url:'../../department/selectAllDepartment.do',method:'post'");
		$.parser.parse();
	/*	
		$('.panel-body-noheader > .tree').tree({
			url: 'tree6_data.json',
			loadFilter: function(rows){

				return convert(rows);
			}
		});*/
				
	}
	//转换成树形结构 函数
	/*function convert(rows){
		function exists(rows, parentId){
			for(var i=0; i<rows.length; i++){
				if (rows[i].id == parentId) return true;
			}
			return false;
		}
		
		var nodes = [];
		// get the top level nodes
		for(var i=0; i<rows.length; i++){
			var row = rows[i];
			if (!exists(rows, row.parentId)){
				nodes.push({
					id:row.id,
					text:row.name
				});
			}
		}
		
		var toDo = [];
		for(var i=0; i<nodes.length; i++){
			toDo.push(nodes[i]);
		}
		while(toDo.length){
			var node = toDo.shift();	// the parent node
			// get the children nodes
			for(var i=0; i<rows.length; i++){
				var row = rows[i];
				if (row.parentId == node.id){
					var child = {id:row.id,text:row.name};
					if (node.children){
						node.children.push(child);
					} else {
						node.children = [child];
					}
					toDo.push(child);
				}
			}
		}
		return nodes;
	}
	*/
	//添加用户 函数
	function addUser(userObj){	
		$.ajax({
			type:"post",
			url:"../../users/inserts.do",
			data:userObj,
			async:true,
			success:function(data){
				layer.msg('添加成功',{icon: 1});
				//getUserList();
				getUserList(1,sendObj);
			},
			error:function(data){
				layer.msg('添加失败',{icon: 5});
			}
		});
	}
	
	//修改用户 函数
	function reviseUser(userObj,$this){
		$.ajax({
			type:"post",
			url:"../../users/inserts.do",
			data:userObj,
			async:true,
			success:function(data){
				layer.msg('修改成功',{icon: 1});
				//刷新用户列表
				//getUserList();	
				getUserList(1,sendObj);
			},
			error:function(data){
				layer.msg('修改失败',{icon: 5});
			}
		});
	}
	var searchStutus = false;
	//点击搜索按钮 查询用户
	$(".search_user").click(function(){		
		searchUsers(1);
		searchStutus = true;
	})
	//搜索用户函数  pageIndex-页数
	function searchUsers(pageIndex){
		var searchObj = {};
		//获取输入的name值 并存放
		searchObj.name = $(".search_name").val();
		//获取输入的phone值 并存放
		searchObj.phone = $(".search_phone").val();
		searchObj.pageSize = 3;
//		searchObj.pageIndex = pageIndex;
		//调用加载列表函数
		getUserList(pageIndex,searchObj);
		
	}
	
	//table表格tr 点击选中复选框
	$("#user_list>tbody").on("click","tr",function(e){
		var ev = event || window.event || arguments.callee.caller.arguments[0];
	    var $this =$(ev.target); 
	   if($this[0].nodeName == "I" || $this[0].nodeName == "INPUT"){
		   
	   }else{
		  
		   var inputCheckbox = $(this).children().children("input");
		   if(inputCheckbox[0].checked == false){
				//更改状态
			   inputCheckbox.prop("checked", "checked");
			}else if(inputCheckbox[0].checked == true){
				//更改状态
				inputCheckbox.removeAttr("checked"); 
			}
	   }
	   e.stopPropagation();
	})
	
	//对象替换null值为'';
	function replaceNull(obj){
		for(var i in obj){
			if(obj[i] == null || obj[i] == "null"){
				obj[i] = '';
			}
		}
		return obj;
	}
	
	 $(function(){$(".table-responsive").slimScroll({height:screenHeight - 300})});
	


















