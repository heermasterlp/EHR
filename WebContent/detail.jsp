<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.um.model.EHealthRecord,java.util.List,java.util.ArrayList,com.um.model.*" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
   
    <head>
       	<meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	    <meta name="viewport" content="width=device-width, initial-scale=1">
	    <meta name="description" content="">
	    <meta name="author" content="">
		<title>Detail</title>
		<!-- Bootstrap Core CSS -->
		<link rel="stylesheet" type="text/css" href="css/bootstrap.css" />
	    <!-- style -->
	    <link rel="stylesheet" type="text/css" href="css/style.css" />
    </head>
    <body>
       
		<div class="container">
	       <h2>病历信息</h2>
	       <div>
	       		<a href="javascript:history.back()">Go Back</a>
	       </div>
	       <table class="table table-bordered">
	       	<tr class="info">
	       		<th rowspan="8"><label>个人信息</label></th>
	       		<th><label>医院</label></th>
	       		<th><label>时间</label></th>
	       		<th><label>科别</label></th>
	       	</tr>
	       	<tr>
	       		<td><s:property value="targetRecord.getHospital()"/></td>
	       		<td><s:property value="targetRecord.getDate()"/></td>
	       		<td><s:property value="targetRecord.getMedicalservice()"/></td>
	       	</tr>
	       	
	       	<tr class="info">
	       		<th><label>挂号号</label></th>
	       		<th><label>姓名</label></th>
	       		<th><label>性别</label></th>
	       	</tr>
	       	<tr>
	       		<td><s:property value="targetRecord.getRegistrationno()"/></td>
	       		<td><s:property value="targetRecord.getPatientInfo().getName()"/></td>
	       		<td><s:property value="targetRecord.getPatientInfo().getGender()"/></td>
	       	</tr>
	       	
	       	<tr class="info">
	       		<th><label>年龄</label></th>
	       		<th><label>职业</label></th>
	       		<th><label>电话</label></th>
	       	</tr>
	       	<tr>
	       		<td><s:property value="targetRecord.getPatientInfo().getAge()"/></td>
	       		<td><s:property value="targetRecord.getPatientInfo().getProfession()"/></td>
	       		<td><s:property value="targetRecord.getPatientInfo().getPhoneNumber()"/></td>
	       	</tr>
	       	
	       	<tr class="info">
	       		<th><label>联系人</label></th>
	       		<th><label>地址</label></th>
	       		<th><label></label></th>
	       	</tr>
	       	<tr>
	       		<td><s:property value="targetRecord.getPatientInfo().getContact()"/></td>
	       		<td><s:property value="targetRecord.getPatientInfo().getAddress()"/></td>
	       		<td></td>
	       	</tr>
	       	<tr>
	       		<th class="info"><label>病症描述</label></th>
	       		<td colspan="3"><s:property value="targetRecord.getConditionsdescribed()"/></td>
	       	</tr>
	       	
	       	<tr>
	       		<th class="info"><label>西医诊断</label></th>
	       		<td colspan="3"><s:property value="targetRecord.getWesterndiagnostics()"/></td>
	       	</tr>
	       	<tr>
	       		<th class="info"><label>中医诊断</label></th>
	       		<td colspan="3"><s:property value="targetRecord.getChinesediagnostics()"/></td>
	       	</tr>
	       	<tr>
	       		<th class="info"><label>处理</label></th>
	       		<td colspan="3"><s:property value="targetRecord.getProcessString()"/></td>
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
	       					<td><s:property value="targetRecord.getWesternMedicineToString()"/></td>
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
	       						<td><s:property value="targetRecord.getChineseMedicineToString()"/></td>
	       					</tr>
	       			</table>
				</td>
	       	</tr>
	       	<tr>
	       		<th class="info">医师</th>
	       		<td colspan="3"><s:property value="targetRecord.getDoctor()"/></td>
	       	</tr>
	       </table>
	       <br>
       </div>
    </body>
</html>
