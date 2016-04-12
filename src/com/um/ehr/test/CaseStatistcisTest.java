package com.um.ehr.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.um.data.DiagClassifyData;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.util.DiagMedicineProcess;
import com.um.util.MachineLearningPredict;
import com.um.util.MedicineByDescription;

public class CaseStatistcisTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String batch = "2012";
		List<EHealthRecord> allList = MedicineByDescription.getRecordsByBatch(batch);
		int index = 1;
		List<String> titleList = new ArrayList<String>(); // 标题
		// 2. 诊断关键字
		List<String> diagnoseKeyWords = getDiagnoseKeywords();
		titleList.addAll(diagnoseKeyWords);
		System.out.println(diagnoseKeyWords);
		// 3. 描述关键字
		Map<String, HashMap<String, ArrayList<String>>> descriptionTableMap = getDescriptionMap();
		titleList.addAll(descriptionTableMap.keySet());
		System.out.println(descriptionTableMap);
//		// 4. 中药
//		List<String> medicinelList = getMedicineList(allList);
//		titleList.addAll(medicinelList);
//		titleList.remove("太子参");
//		System.out.println(medicinelList);
		double totalpercent = 0.0;
		HashMap<String, String> dataMap = null;
		for(EHealthRecord e : allList){
			dataMap = new HashMap<String, String>();
			// 1. 判断诊断，有关键字，则1  没有则为0
			String diagnoseString = e.getChinesediagnostics(); // 中医诊断
			for(String keyword : diagnoseKeyWords){
				if(diagnoseString.contains(keyword)){
					dataMap.put(keyword, "1");
				}else{
					dataMap.put(keyword, "0");
				}
			}
			//2. 描述
					
			String descriptionString = e.getConditionsdescribed(); // 描述
			Set<String> project = descriptionTableMap.keySet();
			HashMap<String, ArrayList<String>> desHashMap = null;
			for(String descString : project){
				String valueString = "0";
				desHashMap = descriptionTableMap.get(descString);
				Set<String> desSet = desHashMap.keySet();
				for(String s:desSet){
					ArrayList<String> keywordList = desHashMap.get(s);
					for(String key: keywordList){
						if(descriptionString.matches(".*" + key + ".*")){
							// 匹配
							valueString = s;
							break;
						}
					}
				}
				dataMap.put(descString, valueString);
			}
					
			
			List<String> inputcode = new ArrayList<>();
			for (String title : titleList) {
				inputcode.add(dataMap.get(title));
			}
			
			List<String> medicineListByMachine = MachineLearningPredict.predict(inputcode); // the result of machine learning
	    	List<String> orignList = new ArrayList<>();
	    	if (e.getChineseMedicines() != null && e.getChineseMedicines().size() > 0) {
				for (ChineseMedicine cm : e.getChineseMedicines()) {
					orignList.add(cm.getNameString());
				}
			}
	    	
	    	if (orignList.contains("太子参") && !orignList.contains("党参")) {
				orignList.add("党参(太子参)");
				orignList.remove("太子参");
			}
	    	if (!orignList.contains("太子参") && orignList.contains("党参")) {
				orignList.add("党参(太子参)");
				orignList.remove("党参");
			}
	    	if (orignList.contains("太子参") && orignList.contains("党参")) {
				orignList.add("党参(太子参)");
				orignList.remove("太子参");
				orignList.remove("党参");
			}
	    	// statistics 
	    	int length = orignList.size();
	    	int count = 0;
	    	if (medicineListByMachine != null) {
	    		for (String string : medicineListByMachine) {
					if (orignList.contains(string)) {
						count++;
					}
				}
//		    	DecimalFormat df = new DecimalFormat("#.##");
		    	double percent = 100.0 * count / length;
		    	totalpercent += percent;
			}
	    	index++;
		}
		double averagepercent = totalpercent / index;
		
		System.out.println("average:" + averagepercent);
			
			index++;
		}
		

	/**
	 *  获取前60味中药
	 * @param eRecords
	 * @return
	 */
	public static List<String> getMedicineList(List<EHealthRecord> eRecords){
		if(eRecords == null || eRecords.isEmpty()){
			return null;
		}
		
		Map<String, Integer> allMedicinesMap = DiagMedicineProcess.statisEhealthMedicine(eRecords);
		System.out.println("med:" + allMedicinesMap.size());
		allMedicinesMap = DiagMedicineProcess.sortMapByValue(allMedicinesMap);
		
		Set<String> medicineNameSet = allMedicinesMap.keySet();
		List<String> medicinelList = new ArrayList<String>();
		medicinelList.addAll(medicineNameSet);
		
		
		System.out.println("medicine size:" + medicinelList.size());
		return medicinelList;
	}
	
	
	/**
	 *  初始化描述对比表
	 * @return
	 */
	public static Map<String, HashMap<String, ArrayList<String>>> getDescriptionMap(){
		String[] descriptionStrings = DiagClassifyData.descriptionKeywords1;
		Map<String, HashMap<String, ArrayList<String>>> descriptionMap = new HashMap<String, HashMap<String,ArrayList<String>>>();
		HashMap<String, ArrayList<String>> descHashMap = null;
		for(String s : descriptionStrings){
			descHashMap = new HashMap<String, ArrayList<String>>();
			String[] tmpStrings = s.split("%");  // 0:项目  1:描述
			String[] descStrings = tmpStrings[1].split("#");
			for(String ss : descStrings){
				if(ss.split(":").length == 2){
					String[] dStrings = ss.split(":");
					String[] listStrings = dStrings[1].split("\\|");
					ArrayList<String> list = new ArrayList<String>();
					for(String sss : listStrings){
						list.add(sss);
					}
					descHashMap.put(dStrings[0], list);
				}
			}
			descriptionMap.put(tmpStrings[0], descHashMap);
		}
		return descriptionMap;
	}
	
	
	/**
	 *  诊断关键字
	 * @return
	 */
	public static List<String> getDiagnoseKeywords(){
		List<String> keywordsList = new ArrayList<String>();
		// 	1. key word of all records 
		String[] keysList = DiagClassifyData.cnDiagCodeStrings;
		String[] tmpStrings = null;
		for(String s : keysList){
			tmpStrings = s.split("\\|");
			keywordsList.add(tmpStrings[0]);
		}
		
		return keywordsList;
	}
}
