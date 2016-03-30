<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.Map,java.util.Set" %>
<%@page import="com.um.util.MedicineByDescription"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
	<title>基于输入症状查询病历</title>
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
                    <!-- <li>
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
                 <div id="left">
        <div>
            <form id="myForm" name="myForm" method="post">
                <div id="left_left">
                    <!-- 症型 -->
                     <div class="row">
                        <div class="col-lg-12">
                            <h1 class="page-header">
                                基于输入预测处方
                            </h1>
                        </div>
                    </div>
                    <div>
                        <p>
                            年度：
                            <select id="batch" name="batch">  
                                <option value="null">全部</option>
                                <option value="2012" selected>2012</option>
                                <option value="2011">2011</option>
                                <option value="2010">2010</option>
                                <option value="2009">2009</option>
                            </select> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <input id="predictButton" type="button" class="btn btn-xs btn-success" value="预测处方" onclick="javascript: form.action='predictByStatisticAndMachine';" /> 
                            
                        </p>
                        <hr>
                        <p class="text-danger">
                            机器学习阈值（0～1）：<input id="threshold" type="text" name="threshold" value="0.3" />
                        </p>
                        <hr>
                        <p>时间状态</p>
                        <table class="table table-bordered">
                            <tr>
                                <td class="info"><label>时间状态:</label></td>
                                <td>
                                    <select id="timestatus" name="timestatus">
                                        <option value="cmtreat" selected>单纯中医药治疗</option>
                                        <option value="shuqian">术前</option>
                                        <option value="shuhou">术后</option>
                                        <option value="zhiliaozhong">放疗中</option>
                                        <option value="zhiliaohou">放疗后</option>
                                        <option value="hualiaozhong">化疗中</option>
                                        <option value="hualiaohou">化疗后</option>
                                        <option value="fenzi">分子靶向药物</option>
                                        <option value="mianyi">免疫治疗</option>
                                    </select>
                                </td>
                            </tr>
                        </table>
                        <hr>
                        <p>证型</p>
                        <table class="table table-bordered">
                            <tr>
                                <td class="info"><label>虚:</label></td>
                                <td>
                                    <select id="xu" name="xu">
                                        <option value="气虚">气虚</option>
                                        <option value="脾虚">脾虚</option>
                                        <option value="气阴两虚">气阴两虚</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td class="info"><label>痰瘀</label></td>
                                <td>
                                    <input type="radio" name="tanyu" value="yes">有
                                    <input type="radio" name="tanyu" value="no" checked>无
                                </td>
                            </tr>
                            <tr>
                                <td class="info"><label>痰湿</label></td>
                                <td>
                                    <input type="radio" name="tanshi" value="yes">有
                                    <input type="radio" name="tanshi" value="no" checked>无
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2">
                                    <label><input name="zhengxing" type="checkbox" value="互结" checked/>互结</label>&nbsp;&nbsp;
                                    <label><input name="zhengxing" type="checkbox" value="阻络"/>阻络</label>&nbsp;&nbsp;
                                    <label><input name="zhengxing" type="checkbox" value="热结"/>热结</label>&nbsp;&nbsp;
                                    <label><input name="zhengxing" type="checkbox" value="夹热"/>夹热</label>&nbsp;&nbsp;
                                    <label><input name="zhengxing" type="checkbox" value="瘀热"/>瘀热</label>&nbsp;&nbsp;
                                    <label><input name="zhengxing" type="checkbox" value="湿阻"/>湿阻</label>
                                </td>
                            </tr>
                        </table>
                        <br>
                        <br>
                    </div>
                    <!-- 症状 -->
                    <div id="">
                        <hr>
                        <p>症状</p>
                        <table class="table table-bordered">
                            <tr>
                                <td class="danger"><label>痰量：</label></td>
                                <td>
                                    <select id="sputumamount" name="sputumamount">
                                        <option value="oksputumamount" selected>正常</option>
                                        <option value="littlesputumamount">少</option>
                                        <option value="muchsputumamount">多</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td class="danger"><label>痰色：</label></td>
                                <td>
                                    <select id="sputumcolor" name="sputumcolor">
                                        <option value="defalutsputumcolor">正常</option>
                                        <option value="yellowsputumcolor">黄</option>
                                        <option value="whitesputumcolor">白</option>
                                        <option value="redlittlesputumcolor">红血痰（少）</option>
                                        <option value="redmuchsputumcolor">红血痰（多）</option>
                                        <option value="redmoresputumcolor">红血痰（特多）</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td class="danger"><label>咳嗽：</label></td>
                                <td>
                                    <select id="cough" name="cough">
                                        <option value="okcough" selected>正常</option>
                                        <option value="badcough">轻</option>
                                        <option value="worsecough">中</option>
                                        <option value="worstcough">重</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td class="danger"><label>脉：</label></td>
                                <td>
                                    <label><input name="pulse" type="checkbox" value="slimpulse" checked/>细</label>&nbsp;&nbsp;
                                    <label><input name="pulse" type="checkbox" value="floatpulse"/>浮</label>&nbsp;&nbsp;
                                    <label><input name="pulse" type="checkbox" value="sinkpulse"/>沉</label>&nbsp;&nbsp;
                                    <label><input name="pulse" type="checkbox" value="roughpulse"/>粗</label>&nbsp;&nbsp;
                                    <label><input name="pulse" type="checkbox" value="latepulse"/>迟</label>&nbsp;&nbsp;
                                    <label><input name="pulse" type="checkbox" value="numberpulse"/>数</label>&nbsp;&nbsp;
                                    <label><input name="pulse" type="checkbox" value="chordpulse"/>弦</label>&nbsp;&nbsp;
                                    <label><input name="pulse" type="checkbox" value="slidepulse"/>滑</label>
                                </td>
                            </tr>
                            <tr>
                                <td class="danger"><label>纳：</label></td>
                                <td>
                                <select id="na" name="na">
                                    <option value="okna" selected>正常</option>
                                    <option value="badna">纳差</option>
                                    <option value="anorexiana">厌食</option>
                                    <option value="worsena">食欲减退</option>
                                </select>
                                </td>
                            </tr>
                            
                            
                            <tr>
                                <td class="danger"><label>大便：</label></td>
                                <td>
                                    <select id="defecate" name="defecate" >
                                        <option value="defaultdefecate" selected>正常</option>
                                        <option value="okdefecate">便秘（轻）</option>
                                        <option value="baddefecate">便秘（中）</option>
                                        <option value="worsedefecate">便秘（重）</option>
                                        <option value="blooddefecate">便血</option>                   
                                    </select>
                                    &nbsp;&nbsp;&nbsp;&nbsp;<label><input name="constipation" type="checkbox" value="xiexie"/>腹泻</label>
                                </td>
                            </tr>
                            <tr>
                                <td class="danger"><label>小便：</label></td>
                                <td>
                                    <select id="urinate" name="urinate" >
                                        <option value="okurinate" selected>正常</option>
                                        <option value="badurinate">小便次多</option>
                                        <option value="worseurinate">尿频</option>
                                        <option value="bloodurinate">尿血</option>                    
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td class="danger"><label>胸肋痛：</label></td>
                                <td>
                                    <select id="xonglei" name="xonglei">
                                        <option value="noxonglei" selected>正常</option>
                                        <option value="okxonglei">胸肋痛（轻）</option>
                                        <option value="badxonglei">胸肋痛（中）</option>
                                        <option value="worsexonglei">胸肋痛（重）</option>
                                    </select>
                                </td>
                            </tr>
                            
                            <tr>
                                <td class="danger"><label>腹痛：</label></td>
                                <td>
                                    <select id="futong" name="futong">
                                        <option value="nofutong" selected>正常</option>
                                        <option value="okfutong">腹痛（轻）</option>
                                        <option value="badfutong">腹痛（中）</option>
                                        <option value="worsefutong">腹痛（重）</option>
                                    </select>
                                </td>
                            </tr>
                            
                            <tr>
                                <td class="success"><label>头身胸腹不适：</label></td>
                                <td>
                                    <label><input name="tengtong" type="checkbox" value="yaotong"/>腰痛</label>&nbsp;&nbsp;&nbsp;
                                    <label><input name="tengtong" type="checkbox" value="wantong"/>脘痛</label>&nbsp;&nbsp;&nbsp;
                                    <label><input name="tengtong" type="checkbox" value="toutong"/>头痛</label>&nbsp;&nbsp;&nbsp;
                                    <label><input name="bodydiscomfort" type="checkbox" value="touyun"/>头晕</label>
                                    <br>
                                    <label><input name="bodydiscomfort" type="checkbox" value="xinji"/>心悸</label>&nbsp;&nbsp;&nbsp;
                                    <label><input name="bodydiscomfort" type="checkbox" value="xiongmen"/>胸闷</label>&nbsp;&nbsp;&nbsp;
                                    <label><input name="bodydiscomfort" type="checkbox" value="fuzhang"/>腹胀</label>&nbsp;&nbsp;&nbsp;
                                    <label><input name="bodydiscomfort" type="checkbox" value="wanzhang"/>脘胀</label>
                                    <br>
                                    <label><input name="bodydiscomfort" type="checkbox" value="shenzhong"/>身重</label>&nbsp;&nbsp;&nbsp;
                                    <label><input name="bodydiscomfort" type="checkbox" value="erming"/>耳鸣</label>&nbsp;&nbsp;&nbsp;
                                    <label><input name="bodydiscomfort" type="checkbox" value="muxuan"/>目眩</label>&nbsp;&nbsp;&nbsp;
                                    <label><input name="bodydiscomfort" type="checkbox" value="mamu"/>麻木</label>
                                </td>
                            </tr>
                            
                            <tr>
                                <td class="success"><label>舌色：</label></td>
                                <td>
                                    <select id="tonguecolor" name="tonguecolor">
                                        <option value="oktonguecolor" selected>正常</option>
                                        <option value="whitetonguecolor">淡白</option>
                                        <option value="redtonguecolor">红色</option>
                                        <option value="jiangtonguecolor">降色</option>
                                        <option value="purpletonguecolor">紫色</option>
                                        <option value="cyantonguecolor">青色</option>
                                        <option value="bluetonguecolor">蓝色</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td class="success"><label>舌苔：</label></td>
                                <td>
                                    <select id="coatedtongue" name="coatedtongue">
                                        <option value="whitecoatedtongue" selected>正常</option>
                                        <option value="yellowcoatedtongue">黄苔</option>
                                        <option value="purplecoatedtongue">紫苔</option>
                                        <option value="blackcoatedtongue">黑（灰）苔</option>
                                        <option value="nitaicoatedtongue">腻苔</option>
                                        <option value="houtaicoatedtongue">厚苔</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td class="success"><label>气力：</label></td>
                                <td>
                                    <select id="energy" name="energy">
                                        <option value="okenergy" selected>正常</option>
                                        <option value="badenergy">差</option>
                                        <option value="worseenergy">特差</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td class="success"><label>眠：</label></td>
                                <td>
                                    <select id="sleep" name="sleep">
                                        <option value="oksleep" selected>正常</option>
                                        <option value="badsleep">失眠（轻）</option>
                                        <option value="worsesleep">失眠（中）</option>
                                        <option value="worstsleep">失眠（重）</option>
                                        <option value="somnolencesleep">嗜睡</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td class="success"><label>寒热：</label></td>
                                <td>
                                    <select id="hanre" name="hanre">
                                        <option value="hanwu" selected>正常</option>
                                        <option value="hanqing">寒（轻）</option>
                                        <option value="hanzhong">寒（重）</option>
                                        <option value="rewu">热（无）</option>
                                        <option value="reqing">热（轻）</option>
                                        <option value="rezhong">热（重）</option>
                                        <option value="hanre">寒热往来</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td class="success"><label>汗：</label></td>
                                <td>
                                    <select id="sweat" name="sweat">
                                        <option value="nosweat" selected>正常</option>
                                        <option value="sweat">有</option>
                                        <option value="zihan">自汗</option>
                                        <option value="daohan">盗汗</option>
                                        <option value="dahan">大汗</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td class="success"><label>口渴：</label></td>
                                <td>
                                    <select id="thirst" name="thirst">
                                        <option value="okthirst" selected>正常</option>
                                        <option value="badthirst">喝不多饮</option>
                                        <option value="worsethirst">口渴多饮</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td class="success"><label>口味：</label></td>
                                <td>
                                    <select id="taste" name="taste">
                                        <option value="lighttaste">正常</option>
                                        <option value="acidtaste">泛酸</option>
                                        <option value="bittertaste">口苦</option>
                                    </select>
                                </td>
                            </tr>
                        </table>
                        <br>
                    </div>
                    <hr>
                </div>
            </form>
        </div>
    </div>
    <div id="right">
    	<div id="contents">
    	</div>
        
    
    </div>
    </div>
	    	</div>
		    </div>
	
	
	<!-- Bootstrap Core JavaScript -->
    <script src="js/bootstrap.js" type="text/javascript"></script>
	<script type="text/javascript" src="js/jquery-2.2.2.js"></script>
	<script type="text/javascript">
		
		
		
		function btn(){
			var $btn = $("input.btn");//获取按钮元素
			$btn.bind("click",function(){
				$('#loading').show(); 
				var zhengxingString = "";
				var pulseString = "";
				var bodydiscomfortString = "";
				$($('input[name=zhengxing]:checked', '#myForm')).each(function(){
					zhengxingString += this.value + ",";
				});
				$($('input[name=bodydiscomfort]:checked', '#myForm')).each(function(){
					bodydiscomfortString += this.value + ",";
				});
				$($('input[name=pulse]:checked', '#myForm')).each(function(){
					pulseString += this.value + ",";
				});
				
				$.ajax({
                    type:"post",
                    url:"querybyinputdescription",//需要用来处理ajax请求的action,excuteAjax为处理的方法名，JsonAction为action名
                    data:{//设置数据源
                        batch:$('#batch').val(),
                        threshold:$('#threshold').val(),
                        timestatus:$('#timestatus').val(),
                        xu:$('#xu').val(),
                        tanyu:$('input[name=tanyu]:checked', '#myForm').val(),
                        tanshi:$('input[name=tanshi]:checked', '#myForm').val(),
                        zhengxing:zhengxingString,
                        sputumamount:$('#sputumamount').val(),
                        sputumcolor:$('#sputumcolor').val(),
                        cough:$('#cough').val(),
                        pulse:pulseString,
                        na:$('#na').val(),
                        defecate:$('#defecate').val(),
                        constipation:$('input[name=constipation]:checked', '#myForm').val(),
                        urinate:$('#urinate').val(),
                        xonglei:$('#xonglei').val(),
                        futong:$('#futong').val(),
                        tengtong:$('input[name=tengtong]').val(),
                        bodydiscomfort:bodydiscomfortString,
                        tonguecolor:$('#tonguecolor').val(),
                        coatedtongue:$('#coatedtongue').val(),
                        energy:$('#energy').val(),
                        sleep:$('#sleep').val(),
                        hanre:$('#hanre').val(),
                        sweat:$('#sweat').val(),
                        thirst:$('#thirst').val(),
                        taste:$('#taste').val()
                    },
                    dataType:"json",//设置需要返回的数据类型
                    success:function(data){
                    	$('#loading').hide(); 
                    	var jsonObject = jQuery.parseJSON(data);
	                	var infos = "<table class='table table-bordered'><thead><tr class='info'><td>No.</td><td>Info</td><td>Detail</td></tr></thead><tbody>";
	                	var index = 1;
	                	$.each(jsonObject.infoMap, function(key, value){
	                		infos += "<tr><td>" + index + "</td><td>" + value + "</td><td><a href='detailRecord?ehealthregno=" + key + "'>详细信息</a></td></tr>";
	                		index++;
                    	});
	                	infos += "</tbody></table>";
	                	$('#contents').html(infos);
                         
                    },
                    error:function(){
                        alert("系统异常，请稍后重试！");
                    }//这里不要加","
                });
			});
		}
		
		/* 页面加载完成，绑定事件 */
        $(document).ready(function(){
        	$('#loading').hide(); 
            btn();//点击提交，执行ajax
        });
	</script>
	<div id="loading" style="position: fixed; top:0; left:0; width:100%; height: 100%; center center #efefef">
		<img src="img/progress.gif" style="margin-top: 15%;margin-left: 15%;"/>
	</div>
</body>
</html>