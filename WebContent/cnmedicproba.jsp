<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
	<title>chinese medicines probably</title>
	<!-- Bootstrap Core CSS -->
	<link rel="stylesheet" type="text/css" href="css/bootstrap.css" />
    <!-- Custom CSS -->
    <link href="css/modern-business.css" rel="stylesheet">
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
        <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <a class="navbar-brand" href="welcome.jsp">吴万垠教授肺癌中医专家系统</a>
            </div>
            <!-- Top Menu Items -->
            <ul class="nav navbar-right top-nav">
            	<li >
	        		<a href="logout">logout</a>
	            </li>
	        </ul>
	        
            <!-- Sidebar Menu Items - These collapse to the responsive navigation menu on small screens -->
            <div class="collapse navbar-collapse navbar-ex1-collapse" align="left">
                <ul class="nav navbar-nav side-nav">
                    <li class="active">
                        <a href="welcome.jsp"><i class="fa fa-fw fa-table"></i> 基于病人姓名查询病历</a>
                    </li>
                    <li>
                        <a href="findRecordByCMName.jsp"><i class="fa fa-fw fa-table"></i> 基于中药名称查询病历</a>
                    </li>
                    <li>
                        <a href="findRecordByInputDescription.jsp"><i class="fa fa-fw fa-table"></i> 基于输入症状查询病历</a>
                    </li>
                    <li>
                        <a href="predictMedicine.jsp"><i class="fa fa-fw fa-edit"></i> 输入预测处方</a>
                    </li>
                    <li>
                        <a href="casePredictMedicine.jsp"><i class="fa fa-fw fa-edit"></i> 案例预测处方</a>
                    </li>
                    <li>
                        <a href="cnmedicproba.jsp"><i class="fa fa-fw fa-bar-chart-o"></i> 中药关系统计</a>
                    </li>
                    <li>
                        <a href="statisticsByCM.jsp"><i class="fa fa-fw fa-bar-chart-o"></i> 统计中药处方</a>
                    </li>
                   <!--  <li>
                        <a href="statisticsByCNDiagnose.jsp"><i class="fa fa-fw fa-bar-chart-o"></i> 基于中医诊断的统计</a>
                    </li> -->
                    <li>
                        <a href="statisticsByCWClassify.jsp"><i class="fa fa-fw fa-bar-chart-o"></i> 中医诊断统计</a>
                    </li>
                </ul>
            </div>
            <!-- /.navbar-collapse -->
        </nav>
	
		<!-- Main page -->
	    
	    <div id="page-wrapper">
	    	<div class="container-fluid">
	    		<!-- Page Heading -->
                <div class="container">
					<div class="row">
				        <div class="col-lg-12">
				            <h1 class="page-header">
				           		中药关系统计
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
	                	
	                	var count = 0; // list length
	                	$.each(jsonObject.resultMap, function(id, value){
	                		count++;
	                	});
	                	
	                	if(count == 3){
	                		infos = "<table class='table table-bordered'><thead><tr class='info'><td>中药</td><td>总数</td><td>百分百</td></tr></thead><tbody><tr>";
	                		$.each(jsonObject.resultMap, function(id, value){
		                		infos += "<td>" + value + "</td>";
		                	});
	                	}else if(count == 5){
	                		infos = "<table class='table table-bordered'><thead><tr class='info'><td>中药组合</td><td>并集数量</td><td>并集百分百</td><td>交集数量</td><td>交集百分百</td></tr></thead><tbody>";
	                		$.each(jsonObject.resultMap, function(id, value){
		                		infos += "<td>" + value + "</td>";
		                	});
	                	}
	                	
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
	                	}
	                	
	                	infos += "</tr></tbody></table>"; */
	                	$('#contents').html(infos);
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