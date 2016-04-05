package com.um.ehr.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionSupport;
import com.um.classify.CWRelationMapping;
import com.um.classify.DiagnosticsClassify;
import com.um.data.DiagClassifyData;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.mongodb.converter.MedicineStatics;
import com.um.util.DiagMedicineProcess;
import com.um.util.MedicineByDescription;

/**
 * Statistics Action
 */
public class StatisticsAction extends ActionSupport implements ServletRequestAware {
	
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger("com.um.ehr.action.StatisticsAction");
	
	private HttpServletRequest request;
	
	private String result;
	
	/**
	 *  statistics component relation of medicines
	 * @return
	 */
	public String cnmedicineProba(){
		logger.info("statistics chinese medicines proba begin");
		
		// 1. get request parameters
		String batch = request.getParameter("batch");
		String medicineStr = request.getParameter("medicines");
		String[] medicines = medicineStr.split(" ");
		
		// 2. get records based on batch
		List<EHealthRecord> eHealthRecordsByBatch = MedicineByDescription.getRecordsByBatch(batch);
		
		// 3. statistics medicines 
		Map<String, String> resMap = DiagMedicineProcess.statisMedicProbability(medicineStr,eHealthRecordsByBatch);
				
		Map<String, ArrayList<String>> resultMap = new HashMap<>();
		
		if (resMap != null && resMap.size() > 0) {
			Set<String> keySet = resMap.keySet();
			
			if(resMap.size() == 1){
				// 一组中药（单一或者两种）
				for(String s : keySet){
					String valueString = resMap.get(s);
					ArrayList<String> valueList = new ArrayList<String>();
					if(valueString.contains("%")){
						//两味中药
						String[] vStrings = valueString.split("%");
						String[] unionStrings = vStrings[0].split("\\|");
						String[] mixStrings = vStrings[1].split("\\|");
						valueList.add(unionStrings[0]); //并集
						valueList.add(unionStrings[1]);//并集百分比
						valueList.add(mixStrings[0]);//交集
						valueList.add(mixStrings[1]);//交集百分比
					}else{
						//一味中药
						String[] vs = valueString.split("\\|"); // 区分交集和并集
						valueList.add(vs[0]); // 并集
						valueList.add(vs[1]); // 并集百分比
					}
					resultMap.put(s, valueList);
				}
			}else{
				//多味中药
				for(String s : keySet){
					String valueString = resMap.get(s);
					ArrayList<String> valueList = new ArrayList<String>();
					String[] vs = valueString.split("%"); // 区分交集和并集
					valueList.add(vs[0].split("\\|")[0]); // 并集
					valueList.add(vs[0].split("\\|")[1]); // 并集百分比
					valueList.add(vs[1].split("\\|")[0]); // 交集
					valueList.add(vs[1].split("\\|")[1]); //交集百分比
							
					resultMap.put(s, valueList);
				}
			}
		}
		
		// 4.get the description statistics
		List<EHealthRecord> targetRecordList = new ArrayList<>();
		for (EHealthRecord eHealthRecord : eHealthRecordsByBatch) {
			if(DiagMedicineProcess.hasThisMedicine(eHealthRecord, medicines)){
				//同时出现在同一病历中
				targetRecordList.add(eHealthRecord);
			}
		}
		int targetRecordSize = targetRecordList.size();
		logger.info("target size: " + targetRecordSize);
		
		List<String> descriptionKeywordList = new ArrayList<>();
		
		// 描述中包含的关键字
		Map<String, String[]> descKeywordsMap = DiagClassifyData.getDescKeywords();
		Map<String, String> descriptionStringsMap = DiagClassifyData.getDescriptionStrings();
		Map<String, String> normalAndBaddescriptionMap = DiagClassifyData.getNormalAndBaddescription();
		
		for (EHealthRecord eRecord : targetRecordList) {
			Set<String> descKeywordSet = descKeywordsMap.keySet();// 全部项目
			for( String d : descKeywordSet){
				String[] values = descKeywordsMap.get(d);
				if( DiagMedicineProcess.checkDescriptionMatch(eRecord.getConditionsdescribed(), values)){
					//项目符合
					if (!normalAndBaddescriptionMap.get(d).equals("0")) {
						descriptionKeywordList.add(descriptionStringsMap.get(d));
					}
				}
			}
		}
		logger.info("st: " + descriptionKeywordList);
		// statistics description key word
		Map<String, Integer> statisticResult = new HashMap<>();
		Set<String> repeat = new HashSet<>();
		for (String string : descriptionKeywordList) {
			if (repeat.contains(string)) {
				int num = statisticResult.get(string);
				num++;
				statisticResult.remove(string);
				statisticResult.put(string, num);
			}else{
				statisticResult.put(string, 1);
				repeat.add(string);
			}
		}
		
		// sort
		statisticResult = DiagMedicineProcess.sortMapByValue(statisticResult);
		
		// 5. format result
		Map<String, Object> map = new HashMap<>();
		map.put("resultMap", resultMap);
		map.put("statisticResult", statisticResult);
		map.put("targetRecordSize", targetRecordSize);
		
		JSONObject json = JSONObject.fromObject(map);
		
		result = json.toString();
		
		logger.info("statistics chinese medicines proba end");
		
		return SUCCESS;
	}
	
	/**
	 * Statistics medicines based on batch
	 * @return
	 */
	public String statisticsMedicinesByBatch(){
		logger.info("statistics chinese medicines by batch start");
		
		// 1. get request parameters
		String batch = request.getParameter("batch");
		
		// 2. get records of batch
		List<EHealthRecord> eHealthRecordsByBatch = MedicineByDescription.getRecordsByBatch(batch);
		
		int length = eHealthRecordsByBatch.size(); //record number
		// all medicines in records with repeat
		List<String> medicineNamesList = MedicineByDescription.getAllMedicinesNameWithRepeat(eHealthRecordsByBatch);
		// statistics result
		HashMap<String, Integer> resultMap = MedicineStatics.staticsChineseMedicine(medicineNamesList);
		// format result 
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultMap", resultMap);
		map.put("recordSize", length);
		
		JSONObject json = JSONObject.fromObject(map);
		
		result = json.toString();
		
		logger.info("statistics chinese medicines by batch end");
		
		return SUCCESS;
	}
	
	/**
	 * statistics by chinese diagnose
	 * @return
	 * @throws IOException 
	 */
	public String statisticsByCNDiagnose() throws IOException{
		logger.info("statistics by chinese diagnose start");
		
		String batch = request.getParameter("batch");
		
		/**
		 * 1. 读取病历信息
		 */
		List<EHealthRecord> eHealthRecordsByBatch = MedicineByDescription.getRecordsByBatch(batch); // 符合某一批次的全部病历
		
		int ehealthCount = eHealthRecordsByBatch.size();
		/**
		 * 2.诊断分类
		 */
		List<DiagnosticsClassify> chineseDiagnostics = CWRelationMapping.createDiagnostics(DiagClassifyData.cnDiagClassify);
		
		/**
		 * 3. 对病历进行分类处理
		 * 		3.1 中医诊断分类
		 */
		Map<String, Integer> cnClassifyNumber = new HashMap<String, Integer>();
		CWRelationMapping.chineseDiagnosticsClassify(eHealthRecordsByBatch,chineseDiagnostics);//中医诊断分类
		
		int numOfChineseDiag = chineseDiagnostics.size();
		Map<String, List<String>> cnMedicineOfcnDiag = new HashMap<String, List<String>>();
		for(int i = 0; i < numOfChineseDiag; i++ ){
			
			DiagnosticsClassify cndiag = chineseDiagnostics.get(i);
			List<String> cnMedicines = new ArrayList<String>();
			
			// 每一类中病例的数量
			cnClassifyNumber.put(cndiag.getDiagString(), cndiag.geteHealthRecords().size());
			
			if(cndiag != null && cndiag.geteHealthRecords() != null && cndiag.geteHealthRecords().size() > 0){
				for(EHealthRecord eRecord:cndiag.geteHealthRecords()){
					if(eRecord != null && eRecord.getChineseMedicines() != null && eRecord.getChineseMedicines().size() > 0){
						for(ChineseMedicine cm:eRecord.getChineseMedicines()){
							if(cm == null){
								break;
							}
							cnMedicines.add(cm.getNameString());
						}
					}
				}
				cnMedicineOfcnDiag.put(cndiag.getDiagString(), cnMedicines);
			}
			
		}
		Set<String> cnKeySets = cnMedicineOfcnDiag.keySet();
		List<String> cnlist = null;
		//对中医诊断---中药处方进行统计
		Map<String, HashMap<String, Integer>> cnClassifyStatistics = new HashMap<String, HashMap<String,Integer>>();
		for(String key:cnKeySets){
			cnlist = CWRelationMapping.copyList(cnMedicineOfcnDiag.get(key));
			if(cnlist != null && cnlist.size() > 0){
				// 对中药处方进行统计
				HashMap<String, Integer> cnStatistic = CWRelationMapping.cnMedicineStatistics(cnlist);
				cnStatistic = (HashMap<String, Integer>) DiagMedicineProcess.sortMapByValue(cnStatistic);
				cnClassifyStatistics.put(key, cnStatistic);
			}
		}
		
		// 4. format result
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultMap", cnClassifyStatistics);
		map.put("numMap", cnClassifyNumber);
		map.put("ehealthCount", ehealthCount);
		
		JSONObject json = JSONObject.fromObject(map);
		result = json.toString();
		
		
		logger.info("statistics by chinese diagnose end");
		
		return SUCCESS;
	}
	
	/**
	 * statistics by chinese classify
	 * @return
	 * @throws IOException 
	 */
	public String statisticsByCWClassify() throws IOException{
		
		logger.info("statistics by chinese classify start");
		String batch = request.getParameter("batch");
		
		/**
		 * 1. 读取病历信息
		 */
		List<EHealthRecord> eHealthRecordsByBatch = MedicineByDescription.getRecordsByBatch(batch); // 符合某一批次的全部病历
		
		int recordSize = eHealthRecordsByBatch.size();
		
		/**
		 * 2.诊断分类
		 */
		List<DiagnosticsClassify> chineseDiagnostics = CWRelationMapping.createDiagnostics(DiagClassifyData.cnDiagClassify);
		List<DiagnosticsClassify> westernDiagnostics = CWRelationMapping.createDiagnostics(DiagClassifyData.wnDiagClassify);
		
		/**
		 * 3. 对病历进行分类处理
		 * 		3.1 中医诊断分类
		 *      3.2 西医诊断分类
		 */
		
		CWRelationMapping.chineseDiagnosticsClassify(eHealthRecordsByBatch,chineseDiagnostics);//中医诊断分类		
		CWRelationMapping.westernDiagnosticsClassify(eHealthRecordsByBatch, westernDiagnostics);//西医诊断分类
		
		/**
		 *  4. 对中西医诊断mapping统计
		 */
		
		List<String> classStatisList = new ArrayList<String>();
		
		int cnLen = chineseDiagnostics.size(); //中医诊断类型数量
		
		for(int i = 0; i < cnLen; i++){
			DiagnosticsClassify cndiag = chineseDiagnostics.get(i);
			if(cndiag == null || cndiag.geteHealthRecords() == null || cndiag.geteHealthRecords().size() == 0){
				continue;
			}
			int lenRecord = cndiag.geteHealthRecords().size(); // 每一个中医诊断类型的病例数
			for(int j = 0; j < lenRecord; j++){
				EHealthRecord eRecord = cndiag.geteHealthRecords().get(j); 
				int wnLen = westernDiagnostics.size(); // 西医诊断类型的数量
				for(int k = 0; k < wnLen; k++){
					DiagnosticsClassify wndiag = westernDiagnostics.get(k);
					if(wndiag == null || wndiag.geteHealthRecords() == null || wndiag.geteHealthRecords().size() == 0){
						continue;
					}
					int lenWnR = wndiag.geteHealthRecords().size(); // 西医诊断类型的病历数
					for(int m = 0; m < lenWnR; m++){
						if(eRecord.getRegistrationno() == wndiag.geteHealthRecords().get(m).getRegistrationno()){
							classStatisList.add("C" + (i+1) + "|" + "W" + (k+1)); 
						}
					}
				}
			}
		}
		
		Map<String, Integer> classMap = new HashMap<String, Integer>(); //分类统计
		
		List<String> copyOfclassStatisList = CWRelationMapping.copyList(classStatisList);
		
		classMap.put(copyOfclassStatisList.get(0).trim(), 1);
		
		for(int i = 0; i < copyOfclassStatisList.size(); i++){
			int count = 1;
			for(int j=i+1; j<copyOfclassStatisList.size();j++){
				if(copyOfclassStatisList.get(j).equals(copyOfclassStatisList.get(i))){
					//重复
					count++;
					copyOfclassStatisList.remove(j);
					j--; // 去掉之后，从当前位置继续
				}
			}
			classMap.put(copyOfclassStatisList.get(i), count);
		}
		
		Map<String, Integer> chineseDiagMap = new HashMap<String, Integer>();
//		Map<String, Integer> westernDiagMap = new HashMap<String, Integer>();
		
		for(DiagnosticsClassify d : chineseDiagnostics ){
			if( d.geteHealthRecords().size() > 0 ){
				chineseDiagMap.put(d.getDiagString(), d.geteHealthRecords().size());
			}
		}
		
//		for( DiagnosticsClassify d: westernDiagnostics){
//			if( d.geteHealthRecords().size() > 0 ){
//				westernDiagMap.put(d.getDiagString(), d.geteHealthRecords().size());
//			}
//		}
		
		// 排序
		chineseDiagMap = DiagMedicineProcess.sortMapByValue(chineseDiagMap);
//		westernDiagMap = DiagMedicineProcess.sortMapByValue(westernDiagMap);
		
		// format result
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultMap", chineseDiagMap);
		map.put("recordSize", recordSize);
		
		JSONObject json = JSONObject.fromObject(map);
		result = json.toString();
		
		
		logger.info("statistics by chinese classify end");
		return SUCCESS;
	}
	

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
