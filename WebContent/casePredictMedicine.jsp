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
	<title>根据案例验证机器学习结果</title>
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
		                	<h1 class="page-header"> 根据案例验证机器学习结果</h1>
		                </div>
			            <form id="myForm" name="myForm" action=''>
			                <div id="left_rigth">
			                    <p class="text-danger">
			                        <label>请入病例序号(1-1130) 或挂号号</label>
			                    </p>
			                    <p>
			                        <input id="count" type="text" name="count" />
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
					<div><h3>原始病历中的中药<label id="originPercent"></label></h3></div>
					<div id="orignMedicines"></div>
				</div>
				<hr>
				<div>
					<div><h3>机器学习结果<label id="machinePercnet"></label></h3></div>
					<div id="medicineListByMachine"></div>
				</div>
				<hr>
				<div>
					<div><h3>病历</h3></div>
					<div><h5><label id="detailcount"></label></h5></div>
					<table class="table table-bordered" >
				       	<tr class="info">
				       		<th rowspan="8"><label>个人信息</label></th>
				       		<th><label>医院</label></th>
				       		<th><label>时间</label></th>
				       		<th><label>科别</label></th>
				       	</tr>
				       	<tr>
				       		<td><label id="hospital"></label></td>
				       		<td><label id="date"></label></td>
				       		<td><label id="medicalservice"></label></td>
				       	</tr>
				       	
				       	<tr class="info">
				       		<th><label>挂号号</label></th>
				       		<th><label>姓名</label></th>
				       		<th><label>性别</label></th>
				       	</tr>
				       	<tr>
				       		<td><label id="registrationno"></label></td>
				       		<td><label id="patientname"></label></td>
				       		<td><label id="patientgender"></label></td>
				       	</tr>
				       	
				       	<tr class="info">
				       		<th><label>年龄</label></th>
				       		<th><label>职业</label></th>
				       		<th><label>电话</label></th>
				       	</tr>
				       	<tr>
				       		<td><label id="patientage"></label></td>
				       		<td><label id="patientprofession"></label></td>
				       		<td><label id="patientphone"></label></td>
				       	</tr>
				       	
				       	<tr class="info">
				       		<th><label>联系人</label></th>
				       		<th><label>地址</label></th>
				       		<th><label></label></th>
				       	</tr>
				       	<tr>
				       		<td><label id="patientcontact"></label></td>
				       		<td><label id="patientaddress"></label></td>
				       		<td><label id=""></label></td>
				       	</tr>
				       	<tr>
				       		<th class="info"><label>病症描述</label></th>
				       		<td colspan="3"><label id="description"></label></td>
				       	</tr>
				       	
				       	<tr>
				       		<th class="info"><label>西医诊断</label></th>
				       		<td colspan="3"><label id="westerndiagnose"></label></td>
				       	</tr>
				       	<tr>
				       		<th class="info"><label>中医诊断</label></th>
				       		<td colspan="3"><label id="chinesediagnose"></label></td>
				       	</tr>
				       	<tr>
				       		<th class="info"><label>处理</label></th>
				       		<td colspan="3"><label id="process"></label></td>
				       	</tr>
				       	<tr>
				       		<th class="info"><label>西药处方</label></th>
				       		<td colspan="3">
				       			<table>
				       				<tr>
				       					<th></th>
				       					<th>名称</th>
				       				</tr>
				       				<tr>
				       					<td></td>
				       					<td><label id="westernmedicines"></label></td>
				       				</tr>
				       			</table>
							</td>
				       	</tr>
				       	<tr>
				       		<th class="info"><label>中药处方</label></th>
				       		<!-- 中药处方 -->
				       		<td colspan="3">
				       			<table >
				       				<tr>
				       					<th></th>
				       					<th>名称</th>
					  				</tr>
				       					<tr>
				       						<td></td>
				       						<td><label id="chinesemedicines"></label></td>
				       					</tr>
				       			</table>
							</td>
				       	</tr>
				       	<tr>
				       		<th class="info">医师</th>
				       		<td colspan="3"><label id="doctor"></label></td>
				       	</tr>
			       </table>
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
	                	count:$('#count').val()
	                },
	                dataType:"json",//设置需要返回的数据类型
	                success:function(data){
	                	$('#loading').hide();
	                	// parse return data
	                	var jsonObject = jQuery.parseJSON(data);
	                	
	                	// origin medicine
	                	var originPercent = jsonObject.statisticsPercent;
	                	$('#originPercent').html(" ("+ originPercent +"%)");
	                	var orignMedicinesString = "";
	                	var orignMedicinesBlack = "";
	                	var orignMedicinesRed = "";
	                	
	                	$.each(jsonObject.orignMedicines.black, function(id, value){
	                		orignMedicinesBlack += value + ",";
	                	});
	                	$.each(jsonObject.orignMedicines.red, function(id, value){
	                		orignMedicinesRed += value + ",";
	                	});
	                	$('#orignMedicines').html(orignMedicinesBlack + "<font color='red'>" + orignMedicinesRed + "</font>");
	                	
	                	// machine learning medicines
	                	var machinePercnet = jsonObject.mechineLearningPercent;
	                	$('#machinePercnet').html(" ("+ machinePercnet +"%)");
	                	
	                	// black
	                	var medicineListByMachineBlack = "";
	                	$.each(jsonObject.medicineListByMachine.black, function(id, value){
	                		medicineListByMachineBlack += value + ",";
	                	});
	                	// red
	                	var medicineListByMachineRed = "";
	                	$.each(jsonObject.medicineListByMachine.red, function(id, value){
	                		medicineListByMachineRed += value + ",";
	                	});
	                	
	                	$('#medicineListByMachine').html(medicineListByMachineBlack + "<font color='red'>" + medicineListByMachineRed + "</font>");
	                	
	                	// target record
	                	$('#detailcount').html("编号：" + jsonObject.count);
	                	$('#hospital').html(jsonObject.targetRecord.hospital);
						$('#date').html(jsonObject.targetRecord.date);
						$('#medicalservice').html(jsonObject.targetRecord.medicalservice);
						$('#registrationno').html(jsonObject.targetRecord.registrationno);
						$('#patientname').html(jsonObject.targetRecord.patientInfo.name);
						$('#patientgender').html(jsonObject.targetRecord.patientInfo.gender);
						$('#patientage').html(jsonObject.targetRecord.patientInfo.age);
						$('#patientprofession').html(jsonObject.targetRecord.patientInfo.profession);
						$('#patientphone').html(jsonObject.targetRecord.patientInfo.phoneNumber);
						$('#patientcontact').html(jsonObject.targetRecord.patientInfo.contact);
						$('#patientaddress').html(jsonObject.targetRecord.patientInfo.address);
						$('#description').html(jsonObject.targetRecord.conditionsdescribed);
						$('#westerndiagnose').html(jsonObject.targetRecord.westerndiagnostics);
						$('#chinesediagnose').html(jsonObject.targetRecord.chinesediagnostics);
						$('#process').html(jsonObject.targetRecord.processString);
						$('#westernmedicines').html(jsonObject.targetRecord.westernMedicineToString);
						$('#chinesemedicines').html(jsonObject.targetRecord.chineseMedicineToString);
						$('#doctor').html(jsonObject.targetRecord.doctor);
	                	
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