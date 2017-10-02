<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>玉言科技</title>

<!-- CSS -->		
<link href="/admin/assets/plugins/jquery-ui/css/jquery-ui-1.8.21.custom.css" rel="stylesheet" />
<link href="/admin/assets/css/common1.css" rel="stylesheet" />
<!-- JS -->
<script src="/admin/assets/vendor/js/jquery-1.7.2.min.js"></script>
<script src="/admin/assets/plugins/jquery-ui/js/jquery-ui-1.8.21.custom.min.js" language="javascript" type="text/javascript"></script>
<script src="/admin/assets/plugins/page/page.js" language="javascript" type="text/javascript"></script>
<script src="/admin/assets/plugins/zTree3.5/jquery.ztree.core-3.5.js" type="text/javascript"></script>
<script src="/admin/assets/plugins/zTree3.5/jquery.ztree.excheck-3.5.js" type="text/javascript"></script>
<script src="/admin/assets/plugins/zTree3.5/jquery.ztree.exedit-3.5.js" type="text/javascript"></script>
<script src="/admin/assets/plugins/time/WdatePicker.js" type="text/javascript"></script>
<script src="/admin/assets/plugins/jquery-form/jquery.form.js" type="text/javascript"></script> 


<link type="text/css" href="/admin/assets/plugins/zTree/css/demo.css" rel="stylesheet">
<link type="text/css" href="/admin/assets/plugins/zTree/css/zTreeStyle.css" rel="stylesheet">


<script>

	var __receivers = new Array();
	
	//打开新的页面，选择
	function selectData(url,ary,wWidth,wHeight){
		if(ary&&ary.length){
			__receivers = ary;
			openWinCenter(url);
		}
	}
	
	function openWinCenter(openUrl,wWidth,wHeight){
		if(!wWidth){
			wWidth=800;
		}
		if(!wHeight){
			wHeight=600;
		}
		var iTop = (window.screen.availHeight-30-wHeight)/2; //获得窗口的垂直位置;
		var iLeft = (window.screen.availWidth-10-wWidth)/2; //获得窗口的水平位置;
		return window.open(openUrl,"","height="+wHeight+", width="+wWidth+", top="+iTop+", left="+iLeft); 
	}
	
</script>  
</head>