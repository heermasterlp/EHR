<%@page language="java" contentType="text/html; charset=UTF-8"
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
	<title>中医诊断出现概率统计</title>
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
	
		<!-- Navigation -->
        <s:include value="navigation.html" />
	
		<!-- Main page -->
	    
		    <div id="page-wrapper">
		    	<div class="container-fluid">
		    		<!-- Page Heading -->
	                 <div class="container">
				<div>
					<div class="row">
		                    <div class="col-lg-12">
		                        <h1 class="page-header">
		                            中医诊断出现概率统计
		                        </h1>
		                    </div>
		                </div>
						<form action="CDMedicineStatis" method="get">
				    		年度：
				    		<select id="batch" name="batch">
				    			<option value="null">全部</option>
	                        	<option value="2012" selected>2012</option>
	                        	<option value="2011">2011</option>
	                        	<option value="2010">2010</option>
	                        	<option value="2009">2009</option>  
			    			</select>  
				    		<input type="button" class="btn btn-success btn-xs" value="中医诊断处方统计" />
		        		</form>
				</div>
			</div>
			<div id="contents">
			</div>
	    	</div>
	    </div>
	</div>
	
	
	 <!-- Bootstrap Core JavaScript -->
    <script src="js/bootstrap.js" type="text/javascript"></script>
    <!-- query auto complete -->
    <script src="js/query.js" type="text/javascript"></script>
	
	<!-- js function -->
	<script type="text/javascript" src="js/jquery-2.2.2.js"></script>
	<script type="text/javascript">
		
		function btn_query(){
			var $btn = $("input.btn");//获取按钮元素
			$btn.bind("click",function(){
				$("#loading").show();
				$.ajax({
	                type:"post",
	                url:"statisticsByCNDiagnose",//需要用来处理ajax请求的action,excuteAjax为处理的方法名，JsonAction为action名
	                data:{//设置数据源
	                	batch:$('#batch').val()
	                },
	                dataType:"json",//设置需要返回的数据类型
	                success:function(data){
	                	 $('#loading').hide();
	                	 /*// parse return data
	                	var jsonObject = jQuery.parseJSON(data);
	                	var infos = "<table class='table table-bordered'><thead><tr class='info'><td>No.</td><td>Info</td><td>Detail</td></tr></thead><tbody>";
	                	var index = 1;
	                	$.each(jsonObject.infoMap, function(key, value){
	                		infos += "<tr><td>" + index + "</td><td>" + value + "</td><td><a href='detailRecord?ehealthregno=" + key + "'>详细信息</a></td></tr>";
	                		index++;
	                	});
	                	infos += "</tbody></table>"; */
	                	$('#contents').html(data);
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