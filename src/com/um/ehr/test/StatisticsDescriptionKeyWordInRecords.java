package com.um.ehr.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.um.data.DiagClassifyData;
import com.um.model.ChineseMedicine;
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
		
		//statistics description 
		Map<String, ArrayList<EHealthRecord>> descRecordMap = new HashMap<>();
		
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
//			System.out.println(index + ": " + descKeyWords);
			//
			if (descRecordMap.keySet().contains(descKeyWords)) {
				descRecordMap.get(descKeyWords).add(eRecord);
			}else{
				ArrayList<EHealthRecord> list = new ArrayList<>();
				list.add(eRecord);
				descRecordMap.put(descKeyWords, list);
			}
			descriptionList += descKeyWords;
		}
		
		Set<String> descRecordMapKeySet = descRecordMap.keySet();
		Map<String, Integer> descRecordMapSort = new HashMap<>();
		Map<String, ArrayList<String>> descRecordStaMap = new HashMap<>();
		for (String string : descRecordMapKeySet) {
			if (descRecordMap.get(string).size() > 1) {
				descRecordMapSort.put(string, descRecordMap.get(string).size());
				ArrayList<EHealthRecord> records = descRecordMap.get(string);
				ArrayList<String> medicineList = new ArrayList<>();
				for (EHealthRecord eRecord : records) {
					String medStr = "";
					if (eRecord.getChineseMedicines() == null || eRecord.getChineseMedicines().size() == 0) {
						continue;
					}
					Set<String> medSet = new HashSet<>();
					for (ChineseMedicine  cm : eRecord.getChineseMedicines()) {
						medSet.add(cm.getNameString());
					}
					for (String medd : DiagClassifyData.statisticsMedicine) {
						if (medSet.contains(medd)) {
							medStr += medd + ",";
						}
					}
					medicineList.add(medStr);
				}
//				System.out.println(string + " : " + descRecordMap.get(string).size() + "---" + medicineList);
				descRecordStaMap.put(string, medicineList);
			}
		}
		//sorted
		descRecordMapSort = DiagMedicineProcess.sortMapByValue(descRecordMapSort);
		
		for (String string : descRecordMapSort.keySet()) {
			System.out.println(string + " : " + descRecordStaMap.get(string).size() + "----" + descRecordStaMap.get(string));
		}
		
//		// fix error 
//		descriptionList = descriptionList.substring(0, descriptionList.length() -1);
//		
//		// statistics description key words
//		String[] descSplits = descriptionList.split(",");
//		List<String> descList = new ArrayList<>();
//		for (String string : descSplits) {
//			descList.add(string);
//		}
//		
//		Set<String> descSet = new HashSet<>();
//		Map<String, Integer> descMap = new HashMap<>();
//		for (String string : descList) {
//			if (descSet.contains(string)) {
//				
//				int value = descMap.get(string);
//				value++;
//				descMap.remove(string);
//				descMap.put(string, value);
//				
//			}else{
//				descMap.put(string, 1);
//				descSet.add(string);
//			}
//		}
//		descMap = DiagMedicineProcess.sortMapByValue(descMap);
//		System.out.println(descMap);
	}

}
