
<#include   "/head/head.ftl">

<script type="text/javascript">
if('${_msg}'!=null&&'${_msg}'!=""){
   alert('${_msg}');
}
$(function() {

	$("#saveDialog").hide();
	
	$("#addBtn").click(function(){
    	showSave()
    });
    
    $("#cancel").click(function(){
    	$("#saveDialog").dialog('close');
    });
    $("#save").click(function(){
    	 doSave();
    });
	$("#batchDelBtn").click(function(){
    	 doBtchDel();
    });
	$("#exportBtn").click(function(){
    	 doExport();
    });
	$("#sendFileBtn").click(function(e){
			e.preventDefault();
			doBatchUpload();
		});
		
	});	
	var allFiledsStr = '[{"_CN_":"ID","_KEY_":"ID"},{"_CN_":"字典编码","_KEY_":"DOC_CODE","_NOT_NULL_":"1"},{"_CN_":"字典名称","_KEY_":"DOC_NAME","_NOT_NULL_":"1"},{"_CN_":"上级ID","_KEY_":"PARENT_ID"},{"_CN_":"备注","_KEY_":"REMARK"},{"_CN_":"更新用户","_KEY_":"UPDATE_USER"},{"_CN_":"创建时间","_KEY_":"CREATE_TIME"},{"_CN_":"更新时间","_KEY_":"UPDATE_TIME"}]';
	var allFileds = eval('('+allFiledsStr+')');
	//弹出保存框
	function showSave(obj){
		//alert(obj);
		$(".savebox").each(function(index,ele){
				if(ele.getAttribute("id")!="saveboxTYPE"){
					ele.value='';
				}
			});
		$(".showbox").each(function(index,ele){
				ele.value='';
			});
		var modify=false;
		if(obj){
			if(obj.ID){
				expand(obj);
				//alert('expandover');
				modify=true;
			}
			for(attr in obj){
			//alert(attr+":"+obj[attr]);
			
				if(document.getElementById('savebox'+attr)){
					document.getElementById('savebox'+attr).value=obj[attr];
				}else if(document.getElementById('showbox'+attr)){
					document.getElementById('showbox'+attr).value=obj[attr];
				}
			}
		}
		
		if(allFileds&&allFileds.length>0){//唯一索引字段不允许修改
			for(var i=0;i<allFileds.length;i++){
				var field = allFileds[i];
				if(field._UNIQ_&&document.getElementById('savebox'+field._KEY_)){
					document.getElementById('savebox'+field._KEY_).disabled=modify;
				}
				
			}
				
		}
		
		
		$("#saveDialog").dialog({
			resizable : false,
			width : 450,
			modal : true
		});
		
	}
    
    function expand(obj){
    	if(obj.ID){
			$.ajax({
				url: '/yuyan/defdoc/expandDefdoc.do',
				type: 'POST',
				dataType: 'json',
				timeout: 5000,
				data:obj,
				async: false,
				complete:function(response){
							var dataStr = response.responseText;
							data = eval('('+dataStr+')');
							if(data){
			    				for(attr in data){
			    					//alert(attr+":"+data[attr]);
			    					obj[attr] = data[attr]
			    				}
			    			}
					}
				});
			
    	}
    }
    
    //AJAX批量删除
	function doBtchDel(){
		
		var ids=getCheckedIds("chks");
		if(''==ids||!ids){
			alert('请至少选择一条记录！');
			return;
		}
		if(!confirm('请确认是否删除？')){
			return;
		}
		$.ajax({
				url: '/yuyan/defdoc/batchDeleteDefdoc.do',
				type: 'POST',
				dataType: 'json',
				timeout: 5000,
				data:{ids:ids},
				async: false,
				complete:function(response,status){
					
							if('success' == response.responseText){
								alert('删除成功');
								doQuery();
								return;
							}else{
								alert('删除失败，您可能无权限');
								return;
							}
						}
				});
	}
   
    function getField(prefix,allName){
    	var result = allName;
    	if(prefix){
    		var idx = allName.indexOf(prefix);
    		//alert(idx);
    		if(idx==0){
    			result = allName.substring(prefix.length);
    		}
    	}
    	return result;
    }
    
    //AJAX保存
	function doSave(){
		var params = new Object();
		var htmObjs = $(".savebox").each(function(index,ele){
				var field = getField('savebox',this.name);
				params[field]= $.trim(this.value);
			});
		
		if(!checkValid(params)){
			return;
		}
		if(!confirm('请确认是否保存？')){
			return;
		}
		//alert(222);
		$.ajax({
				url: '/yuyan/defdoc/saveDefdoc.do',
				type: 'post',
				dataType: 'json',
				timeout: 5000,
				data:params,
				async: false,
				complete:function(response,status){
							if('success' == response.responseText){
								alert('保存成功');
								doQuery();
								return;
							}else if('timeConflict' == response.responseText){
								alert('该关键词时间重叠');
								return;
							}else if('exists' == response.responseText){
								alert('保存失败。 存在相同的数据');
								return;
							}else{
							    alert('保存失败，您可能无权限!');
								return;
							}
						}
				});
	}
	
	//AJAX删除
	function doDelete(obj){
		if(!confirm('请确认是否删除？')){
			return;
		}
		$.ajax({
				url: '/yuyan/defdoc/deleteDefdoc.do',
				type: 'post',
				dataType: 'json',
				timeout: 5000,
				data:obj,
				async: false,
				complete:function(response,status){
							if('success' == response.responseText){
								alert('删除成功');
								doQuery();
								return;
							}else{
								alert('删除失败，您可能无权限');
								return;
							}
						}
				});
	}
	
	
	
	
	
	//获取选中的值
	function getCheckedIds(name){
		//alert(name);	
		var ids="";
		var checks = document.getElementsByName(name);
		//alert(checks.length);
		if(checks.length){
			for(var i=0;i<checks.length;i++){
			
				if(checks[i].checked){
					//alert(checks[i].outerHTML);	
					ids+=checks[i].value+",";
				}
			}
		}
		//alert(ids);
		return ids;
	}
	//查询
	function doQuery(){
		
		var searchForm = $("#searchForm");
		var tmp = searchForm.attr("action");
		searchForm.attr("action","/yuyan/defdoc/queryDefdoc.do");
		searchForm.submit();
	}
	
	function checkValid(obj){
		
		if(allFileds!=null&&allFileds.length){
			for(var i=0;i<allFileds.length;i++){
				var fn = allFileds[i]._KEY_;
				var fncn = allFileds[i]._CN_;
				var notNull = allFileds[i]._NOT_NULL_;
				//alert(fn);
				//alert(obj[fn]);
				if(notNull&&(!obj[fn]||''==obj[fn])){
					alert(fncn+'不能为空！');
					return false;
				}
			}
		}
		if(obj.END_TIME&&obj.START_TIME){
			var ee = moment(obj.END_TIME);
			var ss = moment(obj.START_TIME);
			var now = moment();
			if(now.isAfter(ss)){
				alert('开始时间不能小于当前时间！');
				return;
			}
			if(ss.isAfter(ee)){
				alert('开始时间不能大于结束时间！');
				return;
			}
		}
		
		return true;
	}
	
	function syncData(){
		$("#syncData").dialog({
				resizable: false,
				height:150,
				width:300,
				modal: true,
				title: "同步"
		}); 
		$.ajax({
				url: '/yuyan/defdoc/syncDefdoc.do',
				type: 'POST',
				dataType: 'json',
				success:function(result){
						var text = result.obj;
						$("#syncData").dialog("close");
						alert(text);
					  }
		});
	}
	
	//批量上传
	function showBatchUpload(){
		$("#batchupload").dialog({modal:true,minWidth:650,minHeight:250,
			close:function(){//定义窗口关闭时的函数，$("#batchupload").dialog("close")调用时，实际调用的是此处定义的方法
				doQuery();
			},
			buttons:{
				"确定":function(){
					$("#batchupload").dialog("close");
				}
			}
		});
	}
	
	function doBatchUpload() {
		var files = $("#scan").val();
		if(files==null || files==""){
			alert("请选择CSV文件");
			return false;
		}else if(files.substr(files.length-4,4).toLowerCase().match("^.csv") == null){
			alert("请选择.csv格式文件");
			return false;
		}
		$('#sguploadForm').ajaxSubmit({
                dataType:'json',
                success:function(data){
                	$("#errorMessage").html(data.obj.message);
                	alert(data.obj.message);
                	if (data.obj.message == "上传成功!") {
                	   window.location.href="/yuyan/defdoc/manageDefdoc.do";
                	}
                },
                error:function(xhr){
                    alert('上传失败，请检查文件格式，或者您可能无权限!');
                }
        });
	}
	
	function exportCsv() {
		var searchForm = $("#searchForm");
		var tmp = searchForm.attr("action");
		searchForm.attr("action","/yuyan/defdoc/exportDefdoc.do");
		searchForm.submit();
		searchForm.attr("action",tmp);
	}
	
	function unselectAll(){
		if($("#checkall").prop("checked")){
			$("[name='chks']").prop("checked",true);//全选
		}else{
			$("[name='chks']").prop("checked",false);//取消全选
		}
	}
	
	$(document).ready(function (e) {
	  e('.btn-minimize') .click(function (t) {
	    t.preventDefault();
	    var n = e(this) .parent() .parent() .next('.description-body');
	    n.is(':visible') ? e('i', e(this)) .removeClass('fa-caret-up') .addClass('fa-caret-down')  : e('i', e(this)) .removeClass('fa-caret-down') .addClass('fa-caret-up');
	    n.slideToggle('slow')
	  });
	  
	  var functionName = $("#title").text();
	  $.ajax({
				url: '/admin/description/queryDescription.do',
				type: 'POST',
				dataType: 'json',
				timeout: 5000,
				data:{FUNCTION_NAME:functionName},
				async: false,
				success:function(result){
							$("#showDescriptionSpan").html(result.obj.replace(new RegExp("\n","g"),"<br/>"));
							$("#descriptionInput").val(result.obj);
							return;
						},
				error:function(xhr){
                    return;
                }
			});
	});
	
	function edit(){
		$("#showDescription").hide();
		$("#descriptionInput").val($("#showDescriptionSpan").html().replace(new RegExp("<br>","g"),"\n"));
		$("#editDescription").show();
	}
	
	function closeEdit(){
		$("#showDescription").show();
		$("#editDescription").hide();
	}
	
	function submitEdit(){
		var description = $("#descriptionInput").val();//人工输入类型的用val
		var functionName = $("#title").text();//显示类型的用text，包含html标签的用html
		if(description.length>500){
			alert("内容大于500个字符");
			return;
		}
		$.ajax({
				url: '/admin/description/updateDescription.do',
				type: 'POST',
				dataType: 'json',
				timeout: 5000,
				data:{FUNCTION_NAME:functionName,DESCRIPTION:description},
				async: false,
				success:function(result){
							if('success' == result.obj){
								$("#showDescriptionSpan").html(description.replace(new RegExp("\n","g"),'<br/>'));
								closeEdit();
								return;
							}else{
								alert('失败');
								closeEdit();
								return;
							}
						},
				error:function(xhr){
                    alert('失败!');
                }
				});
	}
	
	function countWordNum(){
		$("#totalNum").html($("#descriptionInput").val().length+"/");
	}
	
	
	// 显示目录树，返回id和名称
	function showDirectoryTree(flag)
	{
		window.open("/admin/generateDirectoryTree.do?flag="+flag,"目录树","height=400,width=300,top=200,left=400,toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no");
	}
	
	//目录树页面调用父页面的方法，传给返回值
	function doAfterSelect(treeNode,flag){
		if(flag == 0){
			if(treeNode!=null){
				$("#saveboxKEYWORD").val(treeNode.groupId);
				$("#showboxNAME").val(treeNode.groupName);
			}
		}else{//搜索框
			document.getElementById("searchbox.KEYWORD").value = treeNode.groupId;
			document.getElementById("searchbox.NAME").value = treeNode.groupName;
		}
	}
	
	function checkNumber(){
		var number = $("#saveboxSORT").val();
		var reg1 =  /^\d+$/;
		if(number.match(reg1) == null)  {
			$("#tip").html("，必须为大于0的整数");
			$("#save").attr("disabled",true);
			$("#save").css("color","gray");
		}else{
			$("#save").removeAttr("disabled");
			$("#save").css("color","white");
			$("#tip").html("");
		}
	}
</script>
<div style="background:white;" >
<form method="post" action="" id="searchForm">			
		<div class="mRtBox">
		
			<div class="rTitle" >
				<h3><i class="title"></i><span id="title">业务字典管理</span></h3>
				<div class="description-btn">
					<a href="#" class="btn-minimize">功能说明&nbsp;<i class="fa fa-caret-down"></i></a>
				</div>
			</div>
			<div class="description-body">
				<div class="description"  id="showDescription">
					<span id="showDescriptionSpan"></span>
					<a href="javascript:;" class="on-default edit-row" onclick="edit()" style="float: right;" title="编辑"><i class="fa fa-pencil"></i></a>
				</div>
				<div class="description" style="display:none" id="editDescription">
					<textarea cols="80" rows="8" onkeyup="countWordNum()" id="descriptionInput"></textarea>
					<span id="totalNum"></span><span>最多500字符</span>
					<a href="javascript:;" class="on-default edit-row" onclick="closeEdit()" style="float: right;" title="取消"><i class="fa fa-times"></i></a>
					<a href="javascript:;" class="on-default edit-row" onclick="submitEdit()" style="float: right;padding-right: 5px;" title="保存"><i class="fa fa-save"></i></a>
				</div>
			</div>
			<div class="searchCenter">
				
				<table width="100%" class="searTabBg">
					<tr>
<td class='tdName'>字典编码：</td>
<td style='width:140px;'>
<input name='searchbox.DOC_CODE' type='text' id='searchbox.DOC_CODE'  value='${searchbox["DOC_CODE"]}' />
</td><td class='tdName'>字典名称：</td>
<td style='width:140px;'>
<input name='searchbox.DOC_NAME' type='text' id='searchbox.DOC_NAME'  value='${searchbox["DOC_NAME"]}' />
</td></tr>

					
				</table>
				
			</div>
			<div class="searchCenter">
				<input type="hidden" name="exportFields" value="DOC_CODE,DOC_NAME,PARENT_ID,REMARK,CREATE_TIME,UPDATE_TIME,UPDATE_USER"/>
				<button id='addBtn' type="button"><span><span>新增</span></span></button>
				
				<button id='batchDelBtn' type="button"><span><span>批量删除</span></span></button>
				
				<button id='batchAddBtn' type="button" onclick="showBatchUpload()"><span><span>批量上传</span></span></button>
				
				<button id="exportCsvBtn" type="button" onclick="exportCsv()"><span><span>批量导出</span></span></button>
				
				<button id='queryBtn' type="button" class="btn-yellow" onclick="doQuery()"><span><span>查询</span></span></button>
			</div>
			<div class="searchCenter" style="position:relative;overflow:auto;">
			<table width="100%"  class="list">		
				<tr style="position:relative;top:expression(this.offsetParent.scrollTop);">
					<th style="width:60px;"><input name="checkall" id="checkall" type="checkbox" onclick="unselectAll()"/>&nbsp;全选</th>
					<th>字典编码</th>
<th>字典名称</th>
<th>上级ID</th>
<th>备注</th>
<th>创建时间</th>
<th>更新时间</th>
<th>更新用户</th>

					<th style="width:80px;">操作</th>
				</tr>
				<#list objs as obj>
				<tr >
					<td align="left">
						<input type="checkbox"  name="chks"  value="${obj.ID?c}"/>
						${page.length*(page.currentPage-1)+obj_index+1}
					</td>
					<td>${obj.DOC_CODE}</td>
<td>${obj.DOC_NAME}</td>
<td>${obj.PARENT_ID}</td>
<td>${obj.REMARK}</td>
<td>${obj.CREATE_TIME}</td>
<td>${obj.UPDATE_TIME}</td>
<td>${obj.UPDATE_USER}</td>


					<td>
                		<a  href="javascript:void(0)" onclick="doDelete({ID:'${obj.ID?c}'})">删除</a>&nbsp;| 
                    	<a  href="javascript:void(0)" onclick="showSave({ID:'${obj.ID?c}'})">修改</a>
					</td>
				</tr>
				</#list>
			
			</table>
			</div>
			
			<div class="searchCenter pagenation" style="text-align:right">
				<script type="text/javascript">
					window.__page=new SnPage(document.getElementById("searchForm"),'/yuyan/defdoc/queryDefdoc.do','${page.totalPage?c}','${page.currentPage?c}');
				</script>
				每页&nbsp;<input type="text"  name="length" style="width:40px;" value="${page.length?c}"/>&nbsp;条记录&nbsp;|
				总共<font color="red">${page.totalPage?c}</font>页，<font color="red">${page.total?c}</font>条记录&nbsp;|&nbsp;第<font color="red">${page.currentPage?c}</font>页
				<a href="#" onclick="__page.goToPage('1');" >首页</a>
				<a href="#" onclick="__page.goToPage('${page.beforePage?c}');" >上一页</a>
				<a href="#" onclick="__page.goToPage('${page.nextPage?c}');" >下一页</a>
				跳转至&nbsp;<input type="text" id="currentPage" value="${page.currentPage?c}" name="currentPage" style="width:40px;text-align: center;"/>&nbsp;页 
				<a href="#" onclick="__page.goToPage(document.getElementById('currentPage').value);">跳转</a>
				<a href="#" onclick="__page.goToPage('${page.totalPage?c}');" >尾页</a>
			</div>
			</form>
		</div>

</div>
<div id="saveDialog" title="业务字典保存" style="display:none" >
	<input type="hidden" class="savebox" name="saveboxID" id="saveboxID" >
	<div class="box-content">
	<table style="margin-top:10px;font-size:12px;">
		
		<tr style='height:35px;'>
<td width='140px' align='right'>字典编码：</td>
<td> <input type='text' class='savebox'  name='saveboxDOC_CODE' id='saveboxDOC_CODE' ></td>
</tr>
<tr style='height:35px;'>
<td width='140px' align='right'>字典名称：</td>
<td> <input type='text' class='savebox'  name='saveboxDOC_NAME' id='saveboxDOC_NAME' ></td>
</tr>
<tr style='height:35px;'> 
<td width='140px' align='right'>上级ID：</td>
<td><select style='width:100%;' class='savebox'  name='saveboxPARENT_ID' id='saveboxPARENT_ID'>
	<option value=''>选择上级ID</option>
<#if PARENT_IDSelect??>
<#list PARENT_IDSelect?keys as key>
	<option value='${key}'>${PARENT_IDSelect[key]}</option>
</#list>
</#if>
	</select>
</td>
</tr>
<tr style='height:35px;'>
<td width='140px' align='right'>备注：</td>
<td> <input type='text' class='savebox'  name='saveboxREMARK' id='saveboxREMARK' ></td>
</tr>

		
	</table>
	</div>
	<div class="box-foot" style="text-align: right">
		<button name="save" id="save"  type="button" ><span><span>保存</span></span></button>
		<button name="cancel" class="btn-white" id="cancel"  type="button" ><span><span>取消</span></span></button>
	</div>
</div>

<#--同步信息-->
<div id="syncData" style="display:none;text-align:center;">
	<span style="font-size:12pt;">
		正在同步，请稍等...，<b>请勿关闭该窗口</b>
	</span>
</div>
<#--批量上传-->
<div class="showBox" id="batchupload" style="display:none;" title="业务字典批量上传">
	<form action="/yuyan/defdoc/uploadDefdoc.do" method="POST" id="sguploadForm" name="sguploadForm" enctype="multipart/form-data" onsubmit="return false;">
		<div style="text-align: center;margin: 10px 0;">
			<span>路径：</span>
			<input type="hidden" name="importFields" value="DOC_CODE,DOC_NAME,PARENT_ID,REMARK"/>
			<input type="file" name="scan" id="scan"  style="height:26px;width:220px"></input>
			<button name="send" id="sendFileBtn" type="button" ><span><span >上传</span></span></button>
		</div>
	</form>
	<div id="errorMessage" style="color:red;text-align:center;"></div>
	<div align="center">
		<div class="add" >
			<div class="rTitle" align="left">
				<h3>记录字段顺序</h3>
			</div>
			<table width="100%" class="up_table" border="1">
				<th>字典编码</th><th>字典名称</th><th>上级ID</th><th>备注</th><th>创建时间</th><th>更新时间</th><th>更新用户</th>
			</table>
			<div class="rTitle" align="left">
				<h3>上传注意事项</h3>
			</div>
			<ol style="list-style-type:decimal;text-align:left;margin-left:20px;">
				<li>1 请保证上传数据列数正确</li><li>2 每次上传限制1000条</li><li>3 上传文件为CSV格式，GBK编码</li>
				<li><span style="color:red">上传文件必须为csv文件</span></li>
				<li><span style="color:red">文件从第一行读起，字段分隔符用英文逗号（,）</span></li>
			</ol>
		</div>
		
	</div>
</div>