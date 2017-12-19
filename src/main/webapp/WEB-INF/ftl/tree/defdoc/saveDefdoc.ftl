<#include   "/head/head.ftl">


<body>
    <div id="saveDialog" title="业务字典保存" style="display:hidden">
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
            <td><input type='text' class='savebox'  name='saveboxPARENT_ID' id='saveboxPARENT_ID' disabled="disabled">
                </input>
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
    <!--
    <input type="button" value="测试" onclick="doQuery(2)"/>
    -->
    <script type="text/javascript">
    if('${_msg}'!=null&&'${_msg}'!=""){
       alert('${_msg}');
    }

    		$(document).ready(function(){

                $("#save").click(function(){
                     doSave();
                });
    		});
        var saveObjStr = '${parent}';
        var parentNode = {};
        if(''!=saveObjStr){
            parentNode = eval('('+saveObjStr+')');
        }
    	var allFiledsStr = '[{"_CN_":"ID","_KEY_":"ID"},{"_CN_":"字典编码","_KEY_":"DOC_CODE","_NOT_NULL_":"1"},{"_CN_":"字典名称","_KEY_":"DOC_NAME","_NOT_NULL_":"1"},{"_CN_":"上级ID","_KEY_":"PARENT_ID","_UNIQ_":"1"},{"_CN_":"备注","_KEY_":"REMARK"},{"_CN_":"更新用户","_KEY_":"UPDATE_USER"},{"_CN_":"创建时间","_KEY_":"CREATE_TIME"},{"_CN_":"更新时间","_KEY_":"UPDATE_TIME"}]';
        var allFileds = eval('('+allFiledsStr+')');
        showSave(parentNode);

    	//弹出保存框
    	function showSave(obj){


    		var modify=false;
    		if(obj){
    			if(obj.ID){
    				expand(obj);
    				modify=true;
    			}
    			for(attr in obj){
    			//alert(attr+":"+obj[attr]);

    				if(document.getElementById('savebox'+attr)){
    					document.getElementById('savebox'+attr).value=obj[attr];
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
    		document.getElementById('saveboxPARENT_ID').disabled="true";
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
    								doQuery(params.PARENT_ID);
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





    	//查询
    	function doQuery(id){

    		window.parent.treeFrame.src="/yuyan/tree/defdoc/initialTree.do?checkedId="+id;
    		window.location="/yuyan/tree/defdoc/toSaveDefDoc.do?ID="+id;
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

    </script>

</body>