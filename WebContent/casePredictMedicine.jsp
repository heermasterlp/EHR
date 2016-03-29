<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.Map,java.util.Set" %>
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
                    <li>
                        <a href="statisticsByCNDiagnose.jsp"><i class="fa fa-fw fa-bar-chart-o"></i> 基于中医诊断的统计</a>
                    </li>
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
                <div id="left">
            		<div class="row">
		                <div class="col-lg-12">
		                	<h1 class="page-header"> 基于案例预测处方</h1>
		                </div>
			            <form name="form1" method="post" action='predicetByCase'>
			                <div id="left_rigth">
			                    <p class="text-danger">
			                        <label>请入病例序号(1-1130) 或挂号号</label>
			                    </p>
			                    <p>
			                        <input type="text" name="count" />
			                    </p>
			                    <p class="text-danger">
			                        机器学习阈值(0~1):<br>
			                        <input type="text" name="threshold" value="0.3" />
			                    </p>
			                    <p>
			                        <input name="sub" type="submit" class="btn btn-success btn-xs" value="根据病例预测处方"  />
			                    </p>
			                </div>
			            </form>
        			</div>
    		</div>
			<div id="contents">
			</div>
	    	</div>
	    </div>
	</div>
</body>

</html>