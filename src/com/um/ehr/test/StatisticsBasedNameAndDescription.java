package com.um.ehr.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
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

public class StatisticsBasedNameAndDescription {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		/**
		 * 1. Statistics based on patient name with same description key words
		 */
		// 1.1 get all records
		List<EHealthRecord> eHealthRecords = MedicineByDescription.getRecordsByBatch("2012");
		if (eHealthRecords == null || eHealthRecords.size() == 0) {
			return;
		}
		// 1.2 get all patinet name
		Set<String> allNameSet = new HashSet<>();
		for (EHealthRecord eHealthRecord : eHealthRecords) {
			if (eHealthRecord.getPatientInfo() == null) {
				continue;
			}
			allNameSet.add(eHealthRecord.getPatientInfo().getName());
		}
		System.out.println("all patient name:" + allNameSet);
		
		// 1.3 statistics based on name and description key word
		Map<String, HashMap<String, ArrayList<String>>> statisticsMap = new HashMap<>();
		Map<String, Integer> patientNameStatisticsMap = new HashMap<>();
		Map<String, ArrayList<EHealthRecord>> patientRecordStatisticsMap = new HashMap<>();
		Set<String> patientNameStatisticsSet = new HashSet<>();
		
		// 1.4 statistics patient name
		for (EHealthRecord eHealthRecord : eHealthRecords) {
			String name = eHealthRecord.getPatientInfo().getName();
			if (patientNameStatisticsSet.contains(name)) {
				//
				int value = patientNameStatisticsMap.get(name);
				value++;
				patientNameStatisticsMap.remove(name);
				patientNameStatisticsMap.put(name, value);
				// add records
				patientRecordStatisticsMap.get(name).add(eHealthRecord);
			}else{
				patientNameStatisticsMap.put(name, 1);
				patientNameStatisticsSet.add(name);
				// add record
				ArrayList<EHealthRecord> list = new ArrayList<>();
				list.add(eHealthRecord);
				patientRecordStatisticsMap.put(name, list);
			}
		}
		
		// remove value = 1 
		for (String pa : patientNameStatisticsSet) {
			if (patientNameStatisticsMap.get(pa) == 1) {
				patientNameStatisticsMap.remove(pa);
			}
		}
		patientNameStatisticsMap = DiagMedicineProcess.sortMapByValue(patientNameStatisticsMap);
		System.out.println("name statistics:" + patientNameStatisticsMap);
		
		// 1.5 statistics name , description key wors and medicines
		Set<String> patientRecordStatisticsMapKeyset = patientRecordStatisticsMap.keySet();
		for (String name : patientRecordStatisticsMapKeyset) {
			HashMap<String, ArrayList<String>> descMedicineMap = new HashMap<>();
			// get records list based on the name
			List<String> content = getDescriptionAndMedicines(patientRecordStatisticsMap.get(name));
//			System.out.println(name + "  " + content);
			for (String con : content) {
				// 
				String[] descMedicineSplits = con.split("#");
				if (descMedicineMap.keySet().contains(descMedicineSplits[0])) {
					descMedicineMap.get(descMedicineSplits[0]).add(descMedicineSplits[1]);
				}else{
					ArrayList<String> medList = new ArrayList<>();
					medList.add(descMedicineSplits[1]);
					descMedicineMap.put(descMedicineSplits[0], medList);
				}
				
			}
			statisticsMap.put(name, descMedicineMap);
		}
		
		System.out.println(statisticsMap);
		
		
		// output
		File statText = new File("/Users/peterliu/Documents/file/statistNameDescriptionAndMedicine.txt");
        FileOutputStream is = new FileOutputStream(statText);
        OutputStreamWriter osw = new OutputStreamWriter(is);    
        Writer w = new BufferedWriter(osw);
		
		Set<String> nameSet = patientNameStatisticsMap.keySet();
		for (String name : nameSet) {
			int totalNum = patientNameStatisticsMap.get(name);
			int classNum = statisticsMap.get(name).size(); // class of description num
			HashMap<String, ArrayList<String>> contents = statisticsMap.get(name);
			Set<String> contentSet = contents.keySet();
			String outputStr = "name:" + name + "total num:" + totalNum + " class num :" + classNum + " class: ";
			for (String con : contentSet) {
				ArrayList<String> medicines = contents.get(con);
				ArrayList<String> medicinesSorted = new ArrayList<>();
				for (String med : medicines) {
					String[] medsplit = med.split(",");
					ArrayList<String> medsplitList = new ArrayList<>();
					for (String string : medsplit) {
						medsplitList.add(string);
					}
					for(String str : DiagClassifyData.statisticsMedicine){
						if (medsplitList.contains(str)) {
							medicinesSorted.add(str);
						}
					}
				}
				outputStr += con + "   ---  " + medicinesSorted ;
			}
			outputStr += "\n\n";
//			System.out.println(outputStr);
			w.write(outputStr);
			
		}
		w.close();
		System.out.println("finished!");
	}
	
	public static List<String> getDescriptionAndMedicines(List<EHealthRecord> list){
		if (list == null || list.size() == 0) {
			return null;
		}
		Map<String, String[]> descKeywords = DiagClassifyData.getDescKeywords();
		Set<String> descKeywordsKeySet = descKeywords.keySet();
		Map<String, String> descriptionStrings = DiagClassifyData.getDescriptionStrings();
		List<String> content = new ArrayList<>();
		
		for (EHealthRecord eHealthRecord : list) {
			//  get all description key words 
			List<String> descriptionList = new ArrayList<>();
			String descriptionStr = "";
			for (String desc : descKeywordsKeySet) {
				String[] descArray = descKeywords.get(desc);
				for (String de : descArray) {
					if (eHealthRecord.getConditionsdescribed().contains(de)) {
						descriptionList.add(desc);
						break;
					}
				}
			}
			// convert 
			for (String de : descriptionList) {
				if (descriptionStrings.get(de) != null) {
					descriptionStr += descriptionStrings.get(de) + ",";
				}
			}
			// get all medicines
			String medicineStr = "";
			if (eHealthRecord.getChineseMedicines() != null && eHealthRecord.getChineseMedicines().size() > 0) {
				for (ChineseMedicine medicine : eHealthRecord.getChineseMedicines()) {
					if (medicine.getNameString().equals("太子参") && !medicineStr.contains("党参")) {
						medicineStr += "党参,";
					}else if (medicine.getNameString().equals("党参") && !medicineStr.contains("党参")) {
						medicineStr += "党参,";
					}else {
						medicineStr += medicine.getNameString() + ",";
					}
					
				}
			}
			
			// content
			content.add(descriptionStr + "#" + medicineStr); 
			
		}
		return content;
	}

}
