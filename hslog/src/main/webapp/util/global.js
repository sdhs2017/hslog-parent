//年级
var grade = [
               {label: '小学',title: '小学',value: '1',selected: true},
               {label: '初中', title: '初中',value: '2',selected: true},
               {label: '高中',title: '高中',value: '3',selected: true}
    	            ];
//性别
var sex  = [
               {label: '男', title: '男',value: '1',selected: true},
               {label: '女', title: '女',value: '0',selected: true}
    	            ];
//执行状态
var missionstate=[
                 {label: '执行结果', title: '执行结果',value: '4',selected: true},
                 {label: '执行成功', title: '执行成功',value: '0'},
                 {label: '执行中', title: '执行中',value: '1'},
                 {label: '执行失败', title: '执行失败',value: '2',}
                 ];
//统计方式
var countway  = [
               {label: '年龄', title: '年龄',value: 'AGE',selected: true},
               {label: '年级', title: '年级',value: 'GRADE'}
    	            ];
//设置按钮大小 并且全选时选项全部显示
//参数id:bootstrap-multiselect多选元素的id
function adjustButton(id){
	$(document.getElementById(id)).multiselect({
		buttonWidth:'150px',
		 buttonText: function(options) {
               if (options.length === 0) {
                   return 'None selected';
               }
               else {
                   var selected = '';
                   options.each(function() {
                   	  var label = ($(this).attr('label') !== undefined) ? $(this).attr('label') : $(this).html();
                   	   selected += label + ', ';
                   	});
                   return selected.substr(0, selected.length -2);
               }
           }
	});
}
