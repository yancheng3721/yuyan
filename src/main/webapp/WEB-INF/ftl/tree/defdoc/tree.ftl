<#include   "/head/head.ftl">

	<script type="text/javascript">
		var setting = {
			view: {
				addHoverDom: addHoverDom,
				removeHoverDom: removeHoverDom,
				dblClickExpand: false,
				open:true,
				fontCss: setFontCss
			},
			data:{
				key:{
					name:"docName"
				}
			},
			check: {
				enable: false
			},
			callback: {
				onRightClick: OnRightClick,onClick: zTreeOnClick
			}
		};
		var nodes = eval('${rootStr}');
		var checkedNode = {};
		var nodeStr = '${checkedNode}';
		if(nodeStr!=''){
			checkedNode = eval('('+'${checkedNode}'+')');
		}

		var userRootDirs = '${userRootDir}'.split(',');

		var newCount = 1;
		function addHoverDom(treeId, treeNode) {
			var flag = false;
			for(var i=0; i<userRootDirs.length; i++) {
				if(userRootDirs[i] == treeNode.coreRoot) {
					flag = true;
					break;
				}
			}
			if(!flag) {//如果当前用户没有该目录权限，直接返回
				return false;
			}
		
			
				
			var sObj = $("#" + treeNode.tId + "_span");
			if (treeNode.editNameFlag || $("#add_"+treeNode.tId).length>0) return;
			

			
			var addStr = "<span class='button add' id='add_" + treeNode.tId + "' title='add node'></span>";
			var editStr = "<span class='button edit' id='edit_" + treeNode.tId + "' title='edit node'></span>";
			var removeStr = "<span class='button remove' id='remove_" + treeNode.tId + "' title='remove node'></span>";
				

			sObj.after(removeStr);
			sObj.after(editStr);
			sObj.after(addStr);
			

			var btnadd = $("#add_"+treeNode.tId);
			if (btnadd) btnadd.bind("click", function(){
				// 去增加页面
				window.parent.content.src="/yuyan/tree/defdoc/toSaveDefDoc.do?PARENT_ID="+treeNode.id;
				return false;
			});
			var btnedit = $("#edit_"+treeNode.tId);
			if (btnedit) btnedit.bind("click", function(){
				// 去修改页面
				window.parent.content.src="/yuyan/tree/defdoc/toSaveDefDoc.do?ID="+treeNode.id;
				return false;
			});
			var btnremove = $("#remove_"+treeNode.tId);
			if (btnremove) btnremove.bind("click", function(){
				if(treeNode.children&&treeNode.children.length>0){
					alert('子目录不为空，不能删除');
					return ;
				}
				var pas = prompt("请输入密码");
				if(pas!='passw0rd'){
					alert('密码错误');
					return;
				}
				if(confirm('请确认是否删除')){
					
					$.ajax({
						url: "/yuyan/defdoc/deleteDefdoc.do",
						type: "post",
						dataType: "json",
						async: false,
						data: "ID="+treeNode.id,
						complete: function (response, status) {
							if (response.responseText == "success") {
							    var parentId=0;
							    if(treeNode&&treeNode.parentId){
							        parentId=treeNode.parentId;
							    }
								window.location="/yuyan/tree/defdoc/initialTree.do?checkedId="+parentId;
							}
						}
					});
					
				}
				
				return false;
			});
			
			
				
		};
		
		function removeHoverDom(treeId, treeNode) {
			$("#add_"+treeNode.tId).unbind().remove();
			$("#edit_"+treeNode.tId).unbind().remove();
			$("#remove_"+treeNode.tId).unbind().remove();
			//$("#sync_"+treeNode.tId).unbind().remove();
			
		};
	
		// 去查询页面
		function zTreeOnClick(event, treeId, treeNode){
			  //alert(treeNode.tId + ", " + treeNode.name+"isParent:"+treeNode.isParent);
			  //alert(window.parent.content.src);
			  manageDefDetail(treeNode);
			  //window.parent.content.src="/yuyan/tree/defdoc/toSaveDefDoc.do?ID="+treeNode.id;
		}

		function OnRightClick(event, treeId, treeNode) {
			if (!treeNode && event.target.tagName.toLowerCase() != "button" && $(event.target).parents("a").length == 0) {
				zTree.cancelSelectedNode();
				showRMenu("root", event.clientX, event.clientY);
			} else if (treeNode && !treeNode.noR) {
				zTree.selectNode(treeNode);
				showRMenu("node", event.clientX, event.clientY);
			}
		}

		function showRMenu(type, x, y) {
			$("#rMenu ul").show();
			if (type=="root") {
				$("#m_del").hide();
				$("#m_check").hide();
				$("#m_unCheck").hide();
			} else {
				$("#m_del").show();
				$("#m_check").show();
				$("#m_unCheck").show();
			}
			rMenu.css({"top":y+"px", "left":x+"px", "visibility":"visible"});

			$("body").bind("mousedown", onBodyMouseDown);
		}
		function hideRMenu() {
			if (rMenu) rMenu.css({"visibility": "hidden"});
			$("body").unbind("mousedown", onBodyMouseDown);
		}
		function onBodyMouseDown(event){
			if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length>0)) {
				rMenu.css({"visibility" : "hidden"});
			}
		}
		
		function addTreeRoot(){
			hideRMenu();

			window.parent.content.src="/yuyan/tree/defdoc/toSaveDefDoc.do?PARENT_ID=0";
			return false;
		}
		
		function resetTree() {
			hideRMenu();
			$.fn.zTree.init($("#treeDemo"), setting, nodes);
		}

		var zTree, rMenu;
		$(document).ready(function(){
		
			$.fn.zTree.init($("#nodes"), setting, nodes);
			zTree = $.fn.zTree.getZTreeObj("nodes");
			rMenu = $("#rMenu");

			if(checkedNode&&checkedNode.id&&checkedNode.id!=''){
				 var node = zTree.getNodesByParam("id", checkedNode.id, null)[0];
				 expandNode(node,zTree);
			}
			//zTree.expandAll(true);
		});
		
		function expandNode(node,zTree){

			if(node&&zTree){
				zTree.expandNode(node, true, false, true);
				
				var parent = node.getParentNode();

				if(node&&parent&&node.id!=parent.id){
					 expandNode(parent,zTree);
				}
			}
			
		}
		function setFontCss(treeId, treeNode) {
			var result ={color:"black"};
			
			if(treeNode.highlight==true){
				result = {color:"red"};
			}
			return result;
		}
		var searchHisNodes = new Array();
		function search(){
			disHighlight();
			var name = document.getElementById("nodeNames").value;
			if(name!=null && name!=''){
				if(name.length<2){
					alert('输入过短!');
					return;
				}
				searchHisNodes = zTree.getNodesByParamFuzzy("docName", name, null);
				if(searchHisNodes!=null && searchHisNodes.length && searchHisNodes.length>0){
					for(var n=0;n<searchHisNodes.length;n++){
						expandNode(searchHisNodes[n],zTree);
						searchHisNodes[n].highlight=true;
						zTree.updateNode(searchHisNodes[n]);
						//zTree.refresh();
					}
				}
			}else{
				alert('请输入名称');
			}
		}
		
		function disHighlight(){
			if(searchHisNodes&&searchHisNodes.length){
				for(var i=0;i<searchHisNodes.length;i++){
					searchHisNodes[i].highlight=false;
					zTree.updateNode(searchHisNodes[i]);
					//alert('for');
				}
				//alert('dishighlight');
				//zTree.updateNodes(searchHisNodes);
				searchHisNodes =  new Array();
			}
		}
		
		function manageDefDetail(treeNode) {
			window.parent.content.src="/yuyan/defdetail/manageDefdetail.do?searchbox.DOC_ID="+treeNode.id+"&searchbox.DOC_NAME="+treeNode.docName+"&searchbox.DOC_CODE="+treeNode.docCode;
		}
		
		$(document).ready(function(){
		$("#nodeNames").keydown(function(e){
		var curKey = e.which;
		if(curKey == 13){
		$("#searchBtn").click();
		return false;
		}
		});
		}); 
		function initialTree(id){
		    window.location="/yuyan/tree/defdoc/initialTree.do?checkedId="+id;
		}
		
	</script>
		<style type="text/css">
.ztree li span.button.add {margin-left:2px; margin-right: -1px; background-position:-144px 0; vertical-align:top; *vertical-align:middle}
	</style>
	<style type="text/css">
div#rMenu {position:absolute; visibility:hidden; top:0; color:#008B45;text-align: left;padding: 2px;}
div#rMenu ul li{
	margin: 1px 0;
	padding: 0 5px;
	cursor: pointer;
	list-style: none outside none;
	background-color:#E8E8E8;

}

.sync {
	margin-right: 2px;
	background: url("/yuyan/assets/plugins/zTree/css/img/zTreeStandard.png") no-repeat scroll -126px -64px transparent;
	vertical-align: top;
}
	</style>
 </head>

<body>
<div style="text-align:left" class="rTitle">
			<h3>数据字典管理</h3>
</div>

	<div class="zTreeDemoBackground left" style="height:80%">
		<ul ><input id="nodeNames"/><input type="button" id=searchBtn value="查找" onclick="search()"/></ul>
		<ul class="ztree" style="height:600px;width:260px;" id="nodes"></ul>
	</div>
	
<div id="rMenu" style="width:100px;text-align:center; visibility: hidden;height:90%">
	<ul>
		<li onclick="addTreeRoot();" id="m_add">增加数据字典</li>
        <li onclick="initialTree();" id="m_refresh">刷新字典属</li>


	</ul>
</div>
</body></html>