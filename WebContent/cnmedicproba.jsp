<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
	<title>根据中药统计并、交集和症状</title>
	<!-- Bootstrap Core CSS -->
	<link rel="stylesheet" type="text/css" href="css/bootstrap.css" />
    <!-- Custom CSS -->
    <link href="css/sb-admin.css" rel="stylesheet">
    <!-- Custom Fonts -->
    <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <!-- style -->
    <link rel="stylesheet" type="text/css" href="css/style.css" />
</head>
<body>
<div id="wrapper">
	
		<s:include value="navigation.html" />
	
		<!-- Main page -->
	    
	    <div id="page-wrapper">
	    	<div class="container-fluid">
	    		<!-- Page Heading -->
                <div class="container">
					<div class="row">
				        <div class="col-lg-12">
				            <h1 class="page-header">
				           		根据中药统计并、交集和症状
				            </h1>
				        </div>
				  	</div>
					<form action="medicineProba" method="get">
						<p>
				    		年度：
				    		<select id="batch" name="batch">  
			        			<option value="null">全部</option>
			                	<option value="2012" selected>2012</option>
			                	<option value="2011">2011</option>
			                   	<option value="2010">2010</option>
			                   	<option value="2009">2009</option>
			    			</select>  
				    	</p>
				    	<br>
						请输入中药：（空格分隔）
						<br>
						<input id="medicines" type="text" name="medicines" />
						<input type="button" class="btn btn-success btn-xs" value="查询" />	
					</form>
					<hr>
				</div>
				<div id="contents">
				</div>
				<div align="left">
					<h4><label id="count"></label></h4>
				</div>
				<div id="statistics">
				</div>
	    	</div>
	    </div>
	</div>
	
	
	<!-- Bootstrap Core JavaScript -->
    <script src="js/bootstrap.js" type="text/javascript"></script>
	
	<!-- js function -->
	<script type="text/javascript" src="js/jquery-2.2.2.js"></script>
	<script type="text/javascript">
	
		function btn_query(){
			var $btn = $("input.btn");//获取按钮元素
			$btn.bind("click",function(){
				$("#loading").show();
				$.ajax({
	                type:"post",
	                url:"staticCompRelationByMedicines",//需要用来处理ajax请求的action,excuteAjax为处理的方法名，JsonAction为action名
	                data:{//设置数据源
	                	batch:$('#batch').val(),
	                	medicines:$('#medicines').val()
	                },
	                dataType:"json",//设置需要返回的数据类型
	                success:function(data){
	                	$('#loading').hide();
	                	// parse return data
	                	var jsonObject = jQuery.parseJSON(data);
	                	var infos = "";
	                	var targetRecordSize = jsonObject.targetRecordSize;
	                	/* var count = 0; // list length
	                	$.each(jsonObject.resultMap, function(id, value){
	                		count++;
	                	});
	                	
	                	if(count == 3){
	                		infos = "<table class='table table-bordered'><thead><tr class='info'><td>中药</td><td>总数</td><td>百分百</td></tr></thead><tbody><tr>";
	                		$.each(jsonObject.resultMap, function(key, value){
		                		infos += "<tr><td>" + key+"</td><td>" + value[0] +"</td></tr>";
		                	});
	                	}else if(count == 5){ */
	                		infos = "<table class='table table-bordered'><thead><tr class='info'><td>中药组合</td><td>并集数量</td><td>并集百分百</td><td>交集数量</td><td>交集百分百</td></tr></thead><tbody>";
	                		$.each(jsonObject.resultMap, function(key, value){
		                		infos += "<tr><td>" + key+"</td><td>" + value[0] +"</td><td>" + value[1] + "</td><td>" + value[2] + "</td><td>" + value[3] + "</td></tr>";
		                	});
	                		
	                		tables = "<table class='table table-bordered'><thead><tr class='info'><td>No</td><td>症状</td><td>数量</td><td>百分百</td></tr></thead><tbody>";
	                		var index = 1;
	                		$.each(jsonObject.statisticResult, function(key, value){
		                		tables += "<tr><td>" + index +"</td><td>" + key +"</td><td>" + value + "</td><td>" + (100.0 * value / Number(targetRecordSize)).toFixed(2) + "%</td></tr>";
		                		index++;
		                	});
	                		
	                	/* } */
	                	
	                	/* if(jsonObject.resultMap.length == 2){
	                		infos = "<table class='table table-bordered'><thead><tr class='info'><td></td><td></td><td></td></tr></thead><tbody><tr>";
	                		$.each(jsonObject.resultMap, function(id, value){
		                		infos += "<td>" + value + "</td>";
		                	});
	                		
	                	}else if(jsonObject.resultMap.length == 4){
	                		infos = "<table class='table table-bordered'><thead><tr class='info'><td></td><td></td><td></td><td></td><td></td></tr></thead><tbody>";
	                		$.each(jsonObject.resultMap, function(id, value){
		                		infos += "<td>" + value + "</td>";
		                	});
	                	}*/
	                	
	                	infos += "</tbody></table>"; 
	                	tables += "</tbody></table>";
	                	$('#count').html("total count:" + targetRecordSize); 
	                	$('#contents').html(infos);
	                	$('#statistics').html(tables);
	                },
	                error:function(){
	                    alert("系统异常，请稍后重试！");
	                }
				});
			});
		}
		
		/* 页面加载完成，绑定事件 */
	    $(document).ready(function(){
	    	$('#loading').hide(); 
	        btn_query();//点击提交，执行ajax
	    });
	</script>
	<div id="loading" style="position: fixed; top:0; left:0; width:100%; height: 100%; center center #efefef">
		<img src="img/progress.gif" style="margin-top: 15%;margin-left: 15%;"/>
	</div>
	
</body>
</html>