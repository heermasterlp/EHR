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
	<title>案例预测处方</title>
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
                <div class="col-xs-6" align="left">
            		<div class="row">
		                <div class="col-lg-12">
		                	<h1 class="page-header"> 基于案例预测处方</h1>
		                </div>
			            <form id="myForm" name="myForm" action=''>
			                <div id="left_rigth">
			                    <p class="text-danger">
			                        <label>请入病例序号(1-1130) 或挂号号</label>
			                    </p>
			                    <p>
			                        <input id="count" type="text" name="count" />
			                    </p>
			                    <p class="text-danger">
			                        机器学习阈值(0~1):<br>
			                        <input id="threshold" type="text" name="threshold" value="0.3" />
			                    </p>
			                    <p>
			                        <input name="sub" type="button" class="btn btn-success btn-xs" value="根据病例预测处方"  />
			                    </p>
			                </div>
			            </form>
        			</div>
    		</div>
			<div class="col-xs-6" align="left">
				<div>
					<div><h3>原始病历中的中药</h3></div>
					<div id="orignMedicines"></div>
				</div>
				<div>
					<div><h3>机器学习结果</h3></div>
					<div id="medicineListByMachine"></div>
				</div>
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
	                url:"predictByCase",//需要用来处理ajax请求的action,excuteAjax为处理的方法名，JsonAction为action名
	                data:{//设置数据源
	                	count:$('#count').val(),
	                	threshold:$('#threshold').val()
	                },
	                dataType:"json",//设置需要返回的数据类型
	                success:function(data){
	                	$('#loading').hide();
	                	// parse return data
	                	var jsonObject = jQuery.parseJSON(data);
	                	
	                	var orignMedicinesString = "";
	                	var medicineListByMachine = "";
	                	
	                	$.each(jsonObject.orignMedicines, function(id, value){
	                		orignMedicinesString += value + ",";
	                	});
	                	$('#orignMedicines').html(orignMedicinesString);
	                	
	                	$.each(jsonObject.medicineListByMachine, function(id, value){
	                		medicineListByMachine += value + ",";
	                	});
	                	$('#medicineListByMachine').html(medicineListByMachine);
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