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
	<title>Insert title here</title>
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
	    
	    <div id="page-wrapper" class="container">
	    	<div class="container-fluid">
	    		<!-- Page Heading -->
                 <div class="row">
                    <div class="col-lg-12">
                        <h1 class="page-header">
                            病历查询 
                        </h1>
                    </div>
                </div>
                <div class="col-sm-12">
			    	<form action="" method="get" role="form">
			    		<div class="form-group">
		                    <label>年度：</label>&nbsp;&nbsp;
		                    	<select id="batch" name="batch">
		                    		<option value="null">全部</option>
		                        	<option value="2012" selected>2012</option>
		                        	<option value="2011">2011</option>
		                        	<option value="2010">2010</option>
		                        	<option value="2009">2009</option>
		    					</select>  
		    			</div>
		                <div class="form-group">
		                	<label for="tags">姓名： </label>&nbsp;&nbsp;
							<input id="pname" type="text"  name="pname" />&nbsp;&nbsp;
			    			<input id="query" type="button" class="btn btn-xs btn-success" value="查询" /> 
		              	</div>
					</form>
				</div>
	    	</div>
	    	<div id="contents">
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
	                url:"querybyname",//需要用来处理ajax请求的action,excuteAjax为处理的方法名，JsonAction为action名
	                data:{//设置数据源
	                	batch:$('#batch').val(),
	                	pname:$('#pname').val()
	                },
	                dataType:"json",//设置需要返回的数据类型
	                success:function(data){
	                	$('#loading').hide();
	                	// parse return data
	                	var jsonObject = jQuery.parseJSON(data);
	                	var infos = "<table class='table table-bordered'><thead><tr class='info'><td>No.</td><td>Info</td><td>Detail</td></tr></thead><tbody>";
	                	var index = 1;
	                	$.each(jsonObject.infoMap, function(key, value){
	                		infos += "<tr><td>" + index + "</td><td>" + value + "</td><td><label onclick='a_detail(" + key + ");'>详细信息</label></td></tr>";
	                		index++;
                    	});
	                	infos += "</tbody></table>";
	                	$('#contents').html(infos);
	                },
	                error:function(){
	                    alert("系统异常，请稍后重试！");
	                }
				});
			});
			
			// detail table
			var $detailtable = $('#detail');
			$detailtable.bind("click", function(){
				$('#detail').hide();
			});
		}
		// detail function
		function a_detail(ehealthno) {
			$.ajax({
				type: "post",
				url: "detailRecord",
				data: {
					ehealthregno: ehealthno
				},
				dataType:"json",//设置需要返回的数据类型
				success: function(data) {
					var jsonObject = jQuery.parseJSON(data);
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
					
					$('#detail').show();
				}
			}); 
		}
		
		/* 页面加载完成，绑定事件 */
        $(document).ready(function(){
        	$('#loading').hide();
        	$('#detail').hide();
            btn_query();//点击提交，执行ajax
        });
	</script>
	<div id="loading" class="hidden" style="position: fixed; top:0; left:0; width:100%; height: 100%; center center #efefef">
		<img src="img/progress.gif" style="margin-top: 15%;margin-left: 15%;"/>
	</div>
	<s:include value="detail.html" />
	
	
</body>
</html>