package com.um.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.um.data.DiagClassifyData;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.mongodb.converter.MedicineStatics;

public class DiagMedicineProcess {
	
	
	/**
	 *  根据诊断关键字，对病例进行分类，
	 * @param diagnoses
	 * @return
	 */
	public static List<EHealthRecord> getRecordsByDiagnose(String[] diagnoses,List<EHealthRecord> eRecords){
		if(diagnoses == null || diagnoses.length == 0){
			return null;
		}
		List<EHealthRecord> classifiedRecords = new ArrayList<EHealthRecord>(); //分类之后的病例list
		Set<EHealthRecord> classifiedSet = new HashSet<EHealthRecord>();
		//扫描病例list，判断病例的中医诊断类型是否全部符合诊断关键字----最长匹配
		for(EHealthRecord e : eRecords){
			String diagnose = e.getChinesediagnostics();//中医诊断
			if(isMaxMatch(diagnose, diagnoses)){
				classifiedSet.add(e);
			}
		}
		classifiedRecords.addAll(classifiedSet);
		return classifiedRecords;
	}
	
	
	/**
	 *  根据中药，得出症状关键字
	 * @param medicine
	 * @return
	 */
	public static List<String> getDescriptionByMedicine(String medicine,List<EHealthRecord> eHealthRecords){
		if(medicine == "" || eHealthRecords == null || eHealthRecords.isEmpty()){
			return null;
		}
		//1. 判断没有病例对应的关键字
		Map<EHealthRecord,ArrayList<String>> recordMap = new HashMap<EHealthRecord, ArrayList<String>>();
		ArrayList<String> keywordList = null;
		String[] keywords = DiagClassifyData.cndescriclassify;
		// 2.根据中药判断所有的病例
		for(EHealthRecord e : eHealthRecords){
			String description = e.getConditionsdescribed();
			keywordList = new ArrayList<String>();
			for(String s : keywords){
				String[] keys = s.split("\\|");
				if(description.matches(".*" + keys[0].trim() + ".*" )){
					keywordList.add(keys[0].trim());
				}
			}
			if(!keywordList.isEmpty()){
				recordMap.put(e, keywordList);
			}
		}
		// 3. 取这些关键字的并集
		Set<EHealthRecord> ehealthSet = recordMap.keySet();
		Set<String> medicineSet = new HashSet<String>();
		for(EHealthRecord e : ehealthSet){
			if(e.getChineseMedicines() != null && !e.getChineseMedicines().isEmpty()){
				for(ChineseMedicine c : e.getChineseMedicines()){
					if(c.getNameString().equals(medicine.trim())){
						medicineSet.addAll(recordMap.get(e));
					}
				}
			}
		}
		List<String> resultList = new ArrayList<String>();
		resultList.addAll(medicineSet);
		return resultList;
	}
	
	
	/**
	 * 构建关键字参照表
	 * @return
	 */
	public static Map<String, HashMap<String,ArrayList<String>>> createReference(String[] keywords){
		// 1.构造参照表
		Map<String, HashMap<String,ArrayList<String>>> keyMap = new HashMap<String, HashMap<String,ArrayList<String>>>();
		for(String key : keywords){
			String[] projects = key.split("%");// 0:部位 1:描述
			String[] descriptions = projects[1].split("#"); //不同描述
			HashMap<String, ArrayList<String>> descMap = new HashMap<String, ArrayList<String>>();
			for(String s : descriptions){
				String[] desc = s.split(":");
				String[] descKey = desc[1].split("\\|");
				ArrayList<String> descList = (ArrayList<String>) DiagMedicineProcess.arrayToList(descKey);
				descMap.put(desc[0], descList);
			}
			keyMap.put(projects[0], descMap);
		}
		return keyMap;
	}
	
	
	/**
	 * Return predict medicines based on the description
	 * @param description
	 * @param eHealthRecords
	 * @return
	 */
	public static Set<String> getMedicinesByDescription(String description,List<EHealthRecord> eHealthRecords){
		if( description.equals("") || eHealthRecords == null || eHealthRecords.size() == 0){
			return null;
		}
		// 2. Statistics the Chinese medicines
		Map<String, Integer> medicineMap = statisEhealthMedicine(eHealthRecords);
		medicineMap = DiagMedicineProcess.sortMapByValue(medicineMap); //sorted
		
		Set<String> medicineSet = medicineMap.keySet();
		return medicineSet;
	}
	
	/**
	 *  Get the similar records based on the description
	 * @param description
	 * @param eHealthRecords
	 * @return
	 */
	public static List<EHealthRecord> getEhealthRecordByDescription(String description,List<EHealthRecord> eHealthRecords){
		if( description.equals("") || eHealthRecords == null || eHealthRecords.size() == 0){
			return null;
		}
		List<EHealthRecord> similarRecords = new ArrayList<EHealthRecord>(); // similar records
		
		/**
		 *  1. split the descriptions
		 */
		String[] descriptionSplits = description.split(",");
		if( descriptionSplits == null || descriptionSplits.length == 0 ) return null;
		
		/**
		 *  2. record match strategy
		 */
		
		/*
		 * 2.1 sleep
		 */
		List<String> sleepList = new ArrayList<>(); // record id with sleep description
		String[] sleepBiasArray = {"badsleep","worsesleep","worstsleep","somnolencesleep"};
		boolean hasSleepDescription = false;
		for (String string : sleepBiasArray) {
			if (description.contains(string)) {
				// description has sleep
				hasSleepDescription = true;
				break;
			}
		}
		
		// search records if it has sleep description key words
		if (hasSleepDescription) {
			// find records based on sleep description
			List<String> descriptionKeywordList = getDesctiptionKeyWordsBasedOnProject(sleepBiasArray); // description key word list
			
			// query record in all records based on sleep description
			sleepList = getRecordRegNOBasedONDescription(descriptionKeywordList, eHealthRecords);
		}
		
		/*
		 *  2.2 na
		 */
		List<String> naList = new ArrayList<>();// record id with na description
		String[] naBiasArray = {"badna","anorexiana","worsena"};
		boolean hasNaDescription = false;
		for (String string : naBiasArray) {
			if (description.contains(string)) {
				hasNaDescription = true;
				break;
			}
		}
		// search records if it has na description key words
		if (hasNaDescription) {
			// find records based on na description 
			List<String> descriptionKeywordList = getDesctiptionKeyWordsBasedOnProject(naBiasArray);
			// query record in all records based on na description
			naList = getRecordRegNOBasedONDescription(descriptionKeywordList, eHealthRecords);
		}
		
		
		/*
		 *  2.3 blood sputum
		 */
		List<String> bloodSputumList = new ArrayList<>();// record id with blood sputum description
		String[] bloodSputumBiasArray = {"redlittlesputumcolor","redmuchsputumcolor","redmoresputumcolor"};
		boolean hasBloodSputumDescription = false;
		for (String string : bloodSputumBiasArray) {
			if (description.contains(string)) {
				hasBloodSputumDescription = true;
				break;
			}
		}
		// find recods regno based on blood sputum description
		if (hasBloodSputumDescription) {
			List<String> descriptionKeywordList = getDesctiptionKeyWordsBasedOnProject(bloodSputumBiasArray);
			bloodSputumList = getRecordRegNOBasedONDescription(descriptionKeywordList, eHealthRecords);
		}
		
		/*
		 *  2.4 xiongleitong
		 */
		List<String> xiongLeiList = new ArrayList<>();// record id with xiongleitong description
		String[] xiongLeiBiasArray = {"okxionglei","badxionglei","worsexionglei"};
		boolean hasXiongLeiDescription = false;
		// if has xionglei description or not
		for (String string : xiongLeiBiasArray) {
			if (description.contains(string)) {
				hasXiongLeiDescription = true;
				break;
			}
		}
		// find record based on description
		if (hasXiongLeiDescription) {
			List<String> descriptionKeywordList = getDesctiptionKeyWordsBasedOnProject(xiongLeiBiasArray);
			xiongLeiList = getRecordRegNOBasedONDescription(descriptionKeywordList, eHealthRecords);
		}
		/*
		 *  2.5 qili
		 */
		List<String> qiliList = new ArrayList<>();// record id with qili description
		String[] qiliBiasArray = {"badenergy","worseenergy"};
		boolean hasQiliDescription = false;
		for (String string : qiliBiasArray) {
			if (description.contains(string)) {
				hasQiliDescription = true;
				break;
			}
		}
		// find records based on description
		if (hasQiliDescription) {
			List<String> descriptionKeywordList = getDesctiptionKeyWordsBasedOnProject(qiliBiasArray);
			qiliList = getRecordRegNOBasedONDescription(descriptionKeywordList, eHealthRecords);
		}
		/*
		 *  2.6 cough
		 */
		List<String> coughList = new ArrayList<>();// record id with cough description
		String[] coughBiasArray = {"badcough","worsecough","worstcough"};
		boolean hasCoughDescription = false;
		for (String string : coughBiasArray) {
			if (description.contains(string)) {
				hasCoughDescription = true;
				break;
			}
		}
		// find records based on description list
		if (hasCoughDescription) {
			List<String> descriptionKeywordList = getDesctiptionKeyWordsBasedOnProject(coughBiasArray);
			coughList = getRecordRegNOBasedONDescription(descriptionKeywordList, eHealthRecords);
		}
		/*
		 *  2.7 defecate
		 */
		List<String> defecateList = new ArrayList<>();// record id with defecate description
		String[] defecateBiasArray = {"okdefecate","baddefecate","worsedefecate","blooddefecate"};
		boolean hasDefecateDescription = false;
		for (String string : defecateBiasArray) {
			if (description.contains(string)) {
				hasDefecateDescription = true;
				break;
			}
		}
		// find record based on description
		if (hasDefecateDescription) {
			List<String> descriptionKeywordList = getDesctiptionKeyWordsBasedOnProject(defecateBiasArray);
			defecateList = getRecordRegNOBasedONDescription(descriptionKeywordList, eHealthRecords);
		}
		/*
		 *  2.8 fuxie
		 */
		List<String> fuxieList = new ArrayList<>();// record id with fuxie description
		String[] fuxieBiasArray = {"xiexie"};
		boolean hasFuXieDescription = false;
		for (String string : fuxieBiasArray) {
			if (description.contains(string)) {
				hasFuXieDescription = true;
				break;
			}
		}
		// find record based on description
		if (hasFuXieDescription) {
			List<String> descriptionKeywordList = getDesctiptionKeyWordsBasedOnProject(fuxieBiasArray);
			fuxieList = getRecordRegNOBasedONDescription(descriptionKeywordList, eHealthRecords);
		}
		
		/**
		 *  3. match methods
		 */
		List<String> recordsRegnoList = new ArrayList<>(); // all records in 8 description
		int descriptioncount = 0; // input description count
		if (sleepList != null) { recordsRegnoList.addAll(sleepList); descriptioncount++;}
		if (naList != null) { recordsRegnoList.addAll(naList); descriptioncount++;}
		if (bloodSputumList != null) { recordsRegnoList.addAll(bloodSputumList); descriptioncount++;}
		if (xiongLeiList != null) { recordsRegnoList.addAll(xiongLeiList); descriptioncount++;}
		if (qiliList != null) { recordsRegnoList.addAll(qiliList); descriptioncount++;}
		if (coughList != null) { recordsRegnoList.addAll(coughList); descriptioncount++;}
		if (defecateList != null) { recordsRegnoList.addAll(defecateList); descriptioncount++;}
		if (fuxieList != null) { recordsRegnoList.addAll(fuxieList); descriptioncount++;}
		
		// statistics records
		Map<String, Integer> regnoMap = new HashMap<>();
		Set<String> regnoSet = new HashSet<>();
		for (String reg : recordsRegnoList) {
			if (regnoSet.contains(reg)) {
				
				int value = regnoMap.get(reg);
				value++;
				regnoMap.remove(reg);
				regnoMap.put(reg, value);
				
			}else{
				regnoMap.put(reg, 1);
			}
		}
		// sort regno map
		regnoMap = DiagMedicineProcess.sortMapByValue(regnoMap);
		
		// find similar records regno not 1 
		List<String> similarRecordRegnos = new ArrayList<>();
		if (regnoMap != null) {
			Set<String> regnoMapKeyset = regnoMap.keySet();
			if (descriptioncount > 0) {
				// find similar record regno with max value
				while(descriptioncount > 1){
					for (String key : regnoMapKeyset) {
						// max match value
						if (regnoMap.get(key) == descriptioncount) {
							similarRecordRegnos.add(key);
						}
					}
					descriptioncount--;
				}
			}
		}
		
		// if some records with no same description
		if (similarRecordRegnos.size() == 0) {
			// not record with same description, choice one record from each one description list
			if (sleepList != null && sleepList.size() > 0) { similarRecordRegnos.add(sleepList.get(0));}
			if (naList != null && naList.size() > 0) { similarRecordRegnos.add(naList.get(0));}
			if (bloodSputumList != null && bloodSputumList.size() > 0) { similarRecordRegnos.add(bloodSputumList.get(0));}
			if (xiongLeiList != null && xiongLeiList.size() > 0) { similarRecordRegnos.add(xiongLeiList.get(0));}
			if (qiliList != null && qiliList.size() > 0) { similarRecordRegnos.add(qiliList.get(0));}
			if (coughList != null && coughList.size() > 0) { similarRecordRegnos.add(coughList.get(0));}
			if (defecateList != null && defecateList.size() > 0) { similarRecordRegnos.add(defecateList.get(0));}
			if (fuxieList != null && fuxieList.size() > 0) { similarRecordRegnos.add(defecateList.get(0));}
			
		}
		
		// based on regno to find records
		similarRecords = getRecordsBasedOnRegno(similarRecordRegnos, eHealthRecords);
		
		return similarRecords;
	}
	
	/**
	 * Find records based on registrationno
	 * @param regnos
	 * @param eHealthRecords
	 * @return
	 */
	public static List<EHealthRecord> getRecordsBasedOnRegno(List<String> regnos, List<EHealthRecord> eHealthRecords){
		if (regnos == null || regnos.size() == 0) {
			return null;
		}
		List<EHealthRecord> result = new ArrayList<>();
		for (EHealthRecord eHealthRecord : eHealthRecords) {
			if (regnos.contains(eHealthRecord.getRegistrationno())) {
				result.add(eHealthRecord);
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param project
	 * @return
	 */
	public static List<String> getDesctiptionKeyWordsBasedOnProject(String[] project){
		if (project == null || project.length == 0) {
			return null;
		}
		List<String> descriptionKeywordList = new ArrayList<>(); // description key word list
		for (String pro : project) {
			// project : contents
			for (String descKeywords : DiagClassifyData.descKeywords) {
				if(descKeywords.contains(pro)){
					String[] splits = descKeywords.split(":");
					if (splits == null || splits.length != 2) {
						continue;
					}
					String[] contents = splits[1].split("\\|");
					for (String content : contents) {
						// add description key words to list
						descriptionKeywordList.add(content);
					}
				}
			}
		}
		return descriptionKeywordList;
	}
	
	/**
	 * get record no based on description list
	 * @param descriptions
	 * @param eHealthRecords
	 * @return
	 */
	public static List<String> getRecordRegNOBasedONDescription(List<String> descriptions, List<EHealthRecord> eHealthRecords){
		if (descriptions == null || descriptions.size() == 0) {
			return null;
		}
		// find record regno based on descriptions
		List<String> regnoList = new ArrayList<>();
		for (EHealthRecord eHealthRecord : eHealthRecords) {
			// 
			for (String desc : descriptions) {
				if (eHealthRecord.getConditionsdescribed().contains(desc)) {
					regnoList.add(eHealthRecord.getRegistrationno());
					break;
				}
			}
		}
		
		return regnoList;
	}
	
	/**
	 * Get all records based on description in range of match number
	 * @param inputMainInfo
	 * @param inputSecondInfo
	 * @param eHealthRecords
	 * @param rangeOfMatch
	 * @return
	 */
	public static List<EHealthRecord> getRecordsOnRangeOfMatchNumber(Map<String, String> inputMainInfo, Map<String, String> inputSecondInfo, List<EHealthRecord> eHealthRecords, int rangeOfMatch){
		if (inputMainInfo == null || inputSecondInfo == null || eHealthRecords == null || rangeOfMatch == 0) {
			return null;
		}
		// result
		List<EHealthRecord> result = new ArrayList<EHealthRecord>();
		
		// 2. built project reference table
		Map<String, HashMap<String, String>> projectReferenceTable = getProjectReferenceTable();
		
		List<String> mainProjectList = new ArrayList<String>();
		List<String> secondProjectList = new ArrayList<String>();
				
		for (String string : DiagClassifyData.mainProjectStrings) {
			mainProjectList.add(string);
		}
		for (String string : DiagClassifyData.secondProjectStrings) {
			secondProjectList.add(string);
		}
				
		Map<String, ArrayList<String>> descKeywordMap = getDescKeywordMap();
		
		for (EHealthRecord eHealthRecord : eHealthRecords) {
			// 4.1 built record map and initial
			Map<String, String> recordMainMap = new HashMap<String, String>();
			Map<String, String> recordSecondMap = new HashMap<String, String>();
			for (String string : DiagClassifyData.mainProjectStrings) {
				recordMainMap.put(string, "0");
			}
			for (String string : DiagClassifyData.secondProjectStrings) {
				recordSecondMap.put(string, "0");
			}
			
			// 4.2 built record description key words list
			List<String> recordDescList = new ArrayList<String>();
			Set<String> recordDescSet = new HashSet<String>();
			Set<String> descKeywordMapKeySet = descKeywordMap.keySet();
			for (String desc : descKeywordMapKeySet) {
				ArrayList<String> valueList = descKeywordMap.get(desc);
				for (String vString : valueList) {
					if (eHealthRecord.getConditionsdescribed().contains(vString)) {
						// match description
						recordDescSet.add(desc);
						break;
					}
				}
			}
			recordDescList.addAll(recordDescSet);
			// 4.3 create the main and second description map
			Set<String> projectKeySet = projectReferenceTable.keySet();
			for (String record : recordDescList) {
				// project
				for (String project : projectKeySet) {
					
					if (mainProjectList.contains(project)) {
						// main project
						HashMap<String, String> valueMap = projectReferenceTable.get(project);
						if (valueMap.get(record) != null) {
							String value = valueMap.get(record);
							recordMainMap.remove(project);
							recordMainMap.put(project, value);
						}
					}else if (secondProjectList.contains(project)) {
						// second project
						HashMap<String, String> valueMap = projectReferenceTable.get(project);
						if (valueMap.get(record) != null) {
							String value = valueMap.get(record);
							recordSecondMap.remove(project);
							recordSecondMap.put(project, value);
						}
					}
				}
			}
			// 4.4 find the match record, main description match 100% and second match 50%
			boolean isMainMatched = false, isSecondMatched = false;
			// main description match
			if (inputMainInfo.size() > 0) {
				isMainMatched = true;
				int matchNum = 0;
				Set<String> inputMainInfoKeySet = inputMainInfo.keySet();
				for (String in : inputMainInfoKeySet) {
					// match project value
					if (inputMainInfo.get(in).equals(recordMainMap.get(in)) && !inputMainInfo.get(in).equals("0")) {
						matchNum++;
					}
					// remove record description key word not in input key word
					if (!recordMainMap.get(in).equals("0") && !inputMainInfo.get(in).equals(recordMainMap.get(in))) {
						matchNum--;
					}
				}
				
				if (matchNum >= rangeOfMatch) {
					isMainMatched = true;
				}else{
					isMainMatched = false;
				}
			}else{
				isMainMatched = true;
			}
			
			// second description match
			if (inputSecondInfo.size() > 0) {
				// second description match 50%
				int matchNum = 0;
				Set<String> inputSecondInfoKeySet = inputSecondInfo.keySet();
				for (String in : inputSecondInfoKeySet) {
					// match project value
					if (inputSecondInfo.get(in).equals(recordSecondMap.get(in))) {
						matchNum++;
					}
				}
				
				if (matchNum >= 0) {
					isSecondMatched = true;
				}else{
					isSecondMatched = false;
				}
				
			}else{
				isSecondMatched = false;
			}
			// 4.5 return the result
			if (isMainMatched && isSecondMatched) {
				result.add(eHealthRecord);
			}
		}
		return result;
	}
	
	/**
	 * Create Project Reference Table
	 * @return
	 */
	public static Map<String, HashMap<String, String>> getProjectReferenceTable(){
		
		Map<String, HashMap<String, String>> projectReferenceTable = new HashMap<String, HashMap<String,String>>();
		for (String string : DiagClassifyData.machineKeywords) {
			if ("".equals(string)) {
				continue;
			}
			String[] pro_contents = string.split("%");
			if (pro_contents == null || pro_contents.length != 2) {
				continue;
			}
			String[] con_value = pro_contents[1].split("#");
			if (con_value == null || con_value.length == 0) {
				continue;
			}
					
			HashMap<String, String> key_value = new HashMap<String, String>();
			for (String ks : con_value) {
				String[] kv = ks.split(":");
				if (kv == null || kv.length != 2) {
					continue;
				}
				key_value.put(kv[1], kv[0]);
			}
			projectReferenceTable.put(pro_contents[0], key_value);
		}
		return projectReferenceTable;
	}
	
	/**
	 * getDescKeywordMap
	 * @return
	 */
	public static Map<String, ArrayList<String>> getDescKeywordMap(){
		Map<String, ArrayList<String>> descKeywordMap = new HashMap<String, ArrayList<String>>();
		for (String desc : DiagClassifyData.descKeywords) {
			String[] splits = desc.split(":");
			if (splits == null || splits.length != 2) {
				continue;
			}
			String[] contents = splits[1].split("\\|");
			ArrayList<String> list = new ArrayList<String>();
			for (String con : contents) {
				list.add(con);
			}
			descKeywordMap.put(splits[0], list);
		}
		return descKeywordMap;
	}
	
	/**
	 * Check the record match the description or not.
	 * @param e
	 * @param description <status, contents > 
	 * @param size
	 * @return
	 */
	public static boolean checkMatchBasedOnDescription(EHealthRecord e, Map<String, ArrayList<String>> description, int size){
		
		int statusmatchnum = 0;
		Set<String> statusSet = description.keySet();
		
		for (String status : statusSet) {
			int contentmatchnum = 0; // 
			ArrayList<String> desckeywordlist = description.get(status);
			for (String c : desckeywordlist) {
				if (e.getConditionsdescribed().contains(c)) {
					statusmatchnum++;
					break;
				}
				contentmatchnum++;
			}
			if (contentmatchnum == desckeywordlist.size()-1) {
				// 该状态的关键字全部都不一致，则该病例不属于相似病例
				break;
			}
		}
		// The match conditions: the number of match bigger than half of status
		if (statusmatchnum >= size) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 *  检查描述是否和某一项的关键字数组是否匹配
	 * @param description
	 * @param keywords
	 * @return
	 */
	public static boolean checkDescriptionMatch(String description,String[] keywords){
		if( description == "" || keywords == null || keywords.length == 0 ){
			return false;
		}
		for( String k : keywords ){
			if(description.contains(k)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 *  初始化描述对比表
	 *  	<部位， <状态， [k1,k2,k3.....]>>
	 * @return
	 */
	public static Map<String, HashMap<String, ArrayList<String>>> getDescriptionMap(String[] descriptionStrings){
//		String[] descriptionStrings = DiagClassifyData.cndescriptionkeywords1;
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
					for(String l : listStrings){
						list.add(l);
					}
					descHashMap.put(dStrings[0], list);
				}
			}
			descriptionMap.put(tmpStrings[0], descHashMap);
		}
		return descriptionMap;
	}
	
	/**
	 * 	统计出现概率大约 percent的中药
	 * 
	 * @param medicines----- 中药处方统计结果<名称，数量>
	 * @param length-------- 全部病历数量
	 * @param percent------- 百分比
	 * @return
	 */
	public static List<String> statisMedicineWithPercent(Map<String, Integer> medicines,int length,double percent){
		if(medicines == null || medicines.isEmpty()){
			return null;
		}
		List<String> medicineList = new ArrayList<String>(); // 中药名称
		
		Set<String> keys = medicines.keySet();
		
		for( String s : keys ){
			int count = (Integer)medicines.get(s);
			if((count * 1.0 / length) >= percent){
				// 大于percent
				medicineList.add(s);
			}
		}
		return medicineList;
	}
	
	
	/**
	 *  统计中药出现的概率
	 * 		主要功能：
	 * 				1、输入单味中药时，只算出现数量和概率
	 * 				2、输入多味中药时，计算出现概率的交集和并集
	 * @param medicines
	 * @return
	 */
	public static Map<String, String> statisMedicProbability(String medicines,List<EHealthRecord> allRecrods){
		if(medicines == ""){
			return null;
		}
		Map<String, String> resultMap = new HashMap<String, String>();
		// 1. 拆分中药名称
		
		String[] names = medicines.split(" ");
		if(names == null || names.length == 0){
			return null;
		}
		
		// 3. 计算数值
		DecimalFormat df = new DecimalFormat("#.##");
		if(names.length > 1){
			// 3.1 多味中药，需要计算交集和并集
			
			// 1、需要得出所有中药组合
			List<String> combinnationNames = null;
			if(names.length > 2){
				combinnationNames = MedicineStatics.combiantion(names); //中药组合
			}else{
				combinnationNames = new ArrayList<String>();
				String s = names[0].trim() + "|" + names[1].trim();
				combinnationNames.add(s);
			}
			
			for(String combiname : combinnationNames ){
				// 每一个组合 计算起交集和并集的情况
				String[] nStrings = combiname.split("\\|");
				if(nStrings.length == 0){
					continue;
				}
				// 2、计算这些组合的交集和并集
				List<String> medicinesList = new ArrayList<String>();
				for(String s : nStrings){
					medicinesList.add(s.trim());
				}
				//3.1.1 并集运算
				int union = EhealthRecordMath.getUnion(medicinesList, allRecrods); // 并集
				//3.1.2 交集运算
				int mix = EhealthRecordMath.getMix(nStrings, allRecrods);

				double unionPercent = 100.0 * union / allRecrods.size();
				double mixPercent   = 100.0 * mix   / allRecrods.size();
				resultMap.put(combiname, union+"|"+df.format(unionPercent)+"%"+mix +"|" +df.format(mixPercent));
			}
		}else{
			// 单味中药，只要计算出现概率就可以
			List<EHealthRecord> results = null;
			
			if(allRecrods == null || allRecrods.size() == 0){
				return null;
			}
			
			results = new ArrayList<EHealthRecord>();
			
			// 判断是否同时出现在病历中
			for(EHealthRecord e : allRecrods){
				if(hasThisMedicine(e, names)){
					//同时出现在同一病历中
					results.add(e);
				}
			}
			// 3. 整理统计结果
			if(results == null || results.size() == 0){
				return null;
			}
			
			int count = results.size();
			double percent = 100.0 * count / allRecrods.size();
			resultMap.put(names[0], count + "|" + df.format(percent));
		}
		return resultMap;
	}
	
	/**
	 * Get all record by medicine names
	 * @param names
	 * @param eHealthRecords
	 * @return
	 */
	public static List<EHealthRecord> getEhealthRecordsByCMNames(String names, List<EHealthRecord> eHealthRecords){
		if ("".equals(names) || eHealthRecords==null || eHealthRecords.size()==0) {
			return null;
		}
		// medicines
		String[] mStrings = names.split(" ");
		List<String> nameStrings = new ArrayList<String>();
		for (String string : mStrings) {
			nameStrings.add(string);
		}
		// resutl list
		List<EHealthRecord> result = new ArrayList<EHealthRecord>();
		
		for (EHealthRecord eHealthRecord : eHealthRecords) {
			if (eHealthRecord.getChineseMedicines() == null || eHealthRecord.getChineseMedicines().size() == 0) {
				continue;
			}
			//all medicine in a record
			Set<String> recordMedicinesSet = new HashSet<String>();
			for (ChineseMedicine c : eHealthRecord.getChineseMedicines()) {
				recordMedicinesSet.add(c.getNameString());
			}
			// check match
			int count = 0;
			if (nameStrings.size() == 1) {
				if (recordMedicinesSet.contains(nameStrings.get(0))) {
					result.add(eHealthRecord);
				}
			}else if (nameStrings.size() > 1){
				for (String na : nameStrings) {
					if (recordMedicinesSet.contains(na)) {
						count++;
					}else{
						break;
					}
					if (count == nameStrings.size()-1) {
						result.add(eHealthRecord);
					}
				}
			}
			
		}
		
		return result;
	}
	/**
	 * 
	 * @param eHealthRecords
	 * @return
	 */
	public static List<String> getDescriptionWithSameMedicines(List<EHealthRecord> eHealthRecords){
		if (eHealthRecords == null || eHealthRecords.size() == 0) {
			return null;
		}
		
		List<String> list = new ArrayList<String>();
		Set<String> set = new HashSet<String>();
		for (EHealthRecord eHealthRecord : eHealthRecords) {
			String formattedDescription = MedicineByDescription.formattedDescriptionByCount(eHealthRecord.getConditionsdescribed());
			String[] formatStrings = formattedDescription.split(",");
			for (String fString : formatStrings) {
				set.add(fString);
			}
		}
		
		list.addAll(set);
		return list;
	}
	
	
	/**
	 *  判断是否同时出现所有的中药
	 * @param e
	 * @param names
	 * @return
	 */
	public static boolean hasThisMedicine(EHealthRecord e,String[] names){
		if(e == null || e.getChineseMedicines() == null || 
				e.getChineseMedicines().size() == 0 || names == null || names.length == 0){
			return false;
		}
		
		List<ChineseMedicine> allMedicines = e.getChineseMedicines(); // 中药处方
		boolean hasMedicine = true;
		boolean flag = false;
		int length = allMedicines.size();
		for(String s : names){
			flag = false;
			for(int i = 0; i < length; i++){
				if(s.trim().equals(allMedicines.get(i).getNameString())){
					flag = true;// 同时出现则为true = 只要有一个不出现就为false 
				}
				if(!flag && i == length - 1){
					flag = false;
				}
			}
			hasMedicine = hasMedicine && flag;
		}
		
		return hasMedicine;
	}
	
	/**
	 *  统计病历list中的中药处方的数量
	 * @param eHealthRecords
	 * @return Map<中药名称，数量>
	 */
	public static Map<String, Integer> statisEhealthMedicine(List<EHealthRecord> eHealthRecords){
		if(eHealthRecords == null || eHealthRecords.size() == 0){
			return null;
		}
		//1、统计list中所有的中药名称
		List<String> allCnMedicines = new ArrayList<String>(); // 所有的中药名称（包含重复的项）
		for(EHealthRecord eRecord : eHealthRecords){
			if(eRecord.getChineseMedicines() != null && eRecord.getChineseMedicines().size() > 0){
				for(ChineseMedicine c : eRecord.getChineseMedicines()){
					//check the error data
					if (c.getNameString().contains("内服")||c.getNameString().contains("煎药机")) {
						continue;
					}
					allCnMedicines.add(c.getNameString());
				}
			}
		}
		
		//2、依次统计重复的名称
		Map<String, Integer> statisMedicines = MedicineStatics.staticsChineseMedicine(allCnMedicines);
		//4、返回结果
		return statisMedicines;
	}
	
	/**
     *  按值对map进行排序
     * @param orimap
     * @return
     */
    public static Map<String, Integer> sortMapByValue(Map<String, Integer> orimap){
    	if(orimap == null || orimap.isEmpty()){
    		return null;
    	}
    	
    	Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
    	
    	List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String,Integer>>(orimap.entrySet());
    	
    	Collections.sort(entryList,
    			new Comparator<Map.Entry<String,Integer>>(){

					@Override
					public int compare(Entry<String, Integer> o1,
							Entry<String, Integer> o2) {
						// TODO Auto-generated method stub
						int value1 = 0,value2 = 0;
						try {
							value1 = o1.getValue();
							value2 = o2.getValue();
						} catch (NumberFormatException e) {
							// TODO: handle exception
							value1 = 0;
							value2 = 0;
						}
						return value2 - value1;
					}
    	});
    	Iterator<Map.Entry<String, Integer>> iterator = entryList.iterator();
    	
    	Map.Entry<String, Integer> tmpEntry = null;
    	while (iterator.hasNext()) {

    		tmpEntry = iterator.next();
    		sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
		}
    	return sortedMap;
    }
    
    /**
     *  判断字符串是否全部匹配关键字
     * @param string
     * @param keywords
     * @return
     */
    public static boolean isMaxMatch(String string,String[] keywords){
    	if(string == "" || keywords == null || keywords.length == 0){
    		return false;
    	}
    	
    	for(String key : keywords){
    		if(!string.matches(".*" + key + ".*")){
    			return false;
    		}
    	}
    	return true;
    }
	
    
	
    /**
     *  convert array to list
     * @param arrays
     * @return
     */
    public static List<String> arrayToList(String[] arrays){
    	if(arrays == null || arrays.length == 0){
    		return null;
    	}
    	int length = arrays.length;
    	List<String> results = new ArrayList<String>(length);
    	
    	for( int i = 0; i < length; i++ ){
    		results.add(arrays[i].trim());
    	}
    	
    	return results;
    }
    
    /**
     *  remove some items of map that in the list
     * @param maps
     * @param list
     * @return
     */
    public static Map<String, Integer> removeMapInList(Map<String,Integer> maps,List<String> list){
    	if(maps == null || maps.isEmpty()){
    		return null;
    	}
    	// no need to remove
    	if(list == null || list.isEmpty()){
    		return maps;
    	}
    	// remove some items of map in list
    	for(String s : list){
    		maps.remove(s);
    	}
    	return maps;
    }
}
