package com.um.ehr.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.um.data.DiagClassifyData;
import com.um.model.EHealthRecord;
import com.um.util.DiagMedicineProcess;
import com.um.util.MedicineByDescription;

public class StatisticsDescriptionKeyWordInRecords {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// 1. get all batch of 2012
		List<EHealthRecord> eHealthRecordsByBatch = MedicineByDescription.getRecordsByBatch("2012");
		
		System.out.println("all size: " + eHealthRecordsByBatch.size());
		
		// 描述中包含的关键字
		Map<String, String[]> descKeywordsMap = DiagClassifyData.getDescKeywords();
		Map<String, String> descriptionStringsMap = DiagClassifyData.getDescriptionStrings();
		Map<String, String> normalAndBaddescriptionMap = DiagClassifyData.getNormalAndBaddescription();
		
		// 2. statistics description keywords
		int index = 0;
		String descriptionList = "";
		for (EHealthRecord eRecord : eHealthRecordsByBatch) {
			Set<String> descKeywordSet = descKeywordsMap.keySet();// 全部项目
			String descKeyWords = "";
			for( String d : descKeywordSet){
				String[] values = descKeywordsMap.get(d);
				if( DiagMedicineProcess.checkDescriptionMatch(eRecord.getConditionsdescribed(), values)){
					//项目符合
					if (!normalAndBaddescriptionMap.get(d).equals("0")) {
						descKeyWords += descriptionStringsMap.get(d) + ", ";
					}
				}
			}
			index++;
			System.out.println(index + ": " + descKeyWords);
			descriptionList += descKeyWords;
		}
		
		// fix error 
		descriptionList = descriptionList.substring(0, descriptionList.length() -1);
		
		// statistics description key words
		String[] descSplits = descriptionList.split(",");
		List<String> descList = new ArrayList<>();
		for (String string : descSplits) {
			descList.add(string);
		}
		
		Set<String> descSet = new HashSet<>();
		Map<String, Integer> descMap = new HashMap<>();
		for (String string : descList) {
			if (descSet.contains(string)) {
				
				int value = descMap.get(string);
				value++;
				descMap.remove(string);
				descMap.put(string, value);
				
			}else{
				descMap.put(string, 1);
				descSet.add(string);
			}
		}
		descMap = DiagMedicineProcess.sortMapByValue(descMap);
		System.out.println(descMap);
	}

}
