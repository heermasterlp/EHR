package com.um.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
		if(diagnoses == null || diagnoses.length == 0 || eRecords == null || eRecords.size() == 0){
			return null;
		}
		List<EHealthRecord> classifiedRecords = new ArrayList<EHealthRecord>(); //分类之后的病例list
		//扫描病例list，判断病例的中医诊断类型是否全部符合诊断关键字----最长匹配
		for(EHealthRecord e : eRecords){
			String diagnose = e.getChinesediagnostics();//中医诊断
			if(isMaxMatch(diagnose, diagnoses)){
				classifiedRecords.add(e);
			}
		}
		return classifiedRecords;
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
		if ("".equals(description) || eHealthRecords == null || eHealthRecords.size() == 0) {
			return null;
		}
		/*
		 * 1. Format the input description code: D1,D2,D3,D4,D5,D6,D7,D8
		 */
		List<String> inputDescriptionCodeList = new ArrayList<String>();
		String inputCodeString = "";
		// sleep
		inputCodeString = codeInputDescription(description, DiagClassifyData.getSleepBiasArray(), "D1");
		if (!"".equals(inputCodeString)) {
			inputDescriptionCodeList.add("D1");
		}
		
		// na
		inputCodeString = codeInputDescription(description, DiagClassifyData.getNaBiasArray(), "D2");
		if (!"".equals(inputCodeString)) {
			inputDescriptionCodeList.add("D2");
		}
		// blood
		inputCodeString = codeInputDescription(description, DiagClassifyData.getBloodSputumBiasArray(), "D3");
		if (!"".equals(inputCodeString)) {
			inputDescriptionCodeList.add("D3");
		}
		// xiong lei tong 
		inputCodeString = codeInputDescription(description, DiagClassifyData.getXiongLeiBiasArray(), "D4");
		if (!"".equals(inputCodeString)) {
			inputDescriptionCodeList.add("D4");
		}
		// qi li
		inputCodeString = codeInputDescription(description, DiagClassifyData.getQiliBiasArray(), "D5");
		if (!"".equals(inputCodeString)) {
			inputDescriptionCodeList.add("D5");
		}
		// cough
		inputCodeString = codeInputDescription(description, DiagClassifyData.getCoughBiasArray(), "D6");
		if (!"".equals(inputCodeString)) {
			inputDescriptionCodeList.add("D6");
		}
		//defecate
		inputCodeString = codeInputDescription(description, DiagClassifyData.getDefecateBiasArray(), "D7");
		if (!"".equals(inputCodeString)) {
			inputDescriptionCodeList.add("D7");
		}
		// fuxie
		inputCodeString = codeInputDescription(description, DiagClassifyData.getFuxieBiasArray(), "D8");
		if (!"".equals(inputCodeString)) {
			inputDescriptionCodeList.add("D8");
		}
		
		// input description key words  number
		int descriptionNum = inputDescriptionCodeList.size(); 
		/*
		 * 2.
		 */
		// map<regno,list<string> code list>>
		Map<String, ArrayList<String>> recordCodeMap = new HashMap<String, ArrayList<String>>();
		for (EHealthRecord eHealthRecord : eHealthRecords) {
			// format record description code
			ArrayList<String> recordCodeList = codeRecordDescription(eHealthRecord.getConditionsdescribed());
			recordCodeMap.put(eHealthRecord.getRegistrationno(), recordCodeList);
		}
		/*
		 * 3. find similar records 
		 */
		List<String> similarRecordRegnoList = new ArrayList<String>();
		Set<String> recordCodeMapKeySet = recordCodeMap.keySet();
		while (descriptionNum >= 1) {
			for (String recordKey : recordCodeMapKeySet) {
				// match
				if (checkListWithNumber(inputDescriptionCodeList, recordCodeMap.get(recordKey), descriptionNum)) {
					similarRecordRegnoList.add(recordKey);
				}
			}
			// has similar records
			if (similarRecordRegnoList.size() > 0) {
				break;
			}
			
			descriptionNum--;
		}
		
		List<EHealthRecord> similarRecordsList = getRecordsBasedOnRegno(similarRecordRegnoList, eHealthRecords);
		
		return similarRecordsList;
	}
	
	/**
	 * Code desription with D1,D2,D3,D4,D5,D6,D7,D8
	 * @param descirption
	 * @param list
	 * @param code
	 * @return
	 */
	public static String codeInputDescription(String descirption, List<String> list, String code){
		if ("".equals(descirption) || list == null) {
			return "";
		}
		// code
		for (String string : list) {
			if (descirption.contains(string)) {
				return code;
			}
		}
		return "";
	}
	
	/**
	 * Code record description code
	 * @param description
	 * @return
	 */
	public static ArrayList<String> codeRecordDescription(String description){
		if ("".equals(description)) {
			return null;
		}
		// 
		ArrayList<String> codeList = new ArrayList<String>();
		// code reference code map
		Map<String, ArrayList<String>> codeReferenceMap = createDescriptionCodeReference();
		
		Set<String> codeReferenceMapKeySet = codeReferenceMap.keySet();
		for (String codeString : codeReferenceMapKeySet) {
			// key words list
			ArrayList<String> keywordList = codeReferenceMap.get(codeString);
			for (String keyString : keywordList) {
				// match
				if (description.contains(keyString)) {
					codeList.add(codeString);
					break;
				}
			}
		}
		
		return codeList;
	}
	
	/**
	 * <D1,list<string> key words>
	 * @return
	 */
	public static Map<String, ArrayList<String>> createDescriptionCodeReference(){
		Map<String, ArrayList<String>> referenceMap = new HashMap<String, ArrayList<String>>();
		
		//description key words list map
		Map<String, ArrayList<String>> descKeywordsMap = new HashMap<String, ArrayList<String>>();
		for(String s : DiagClassifyData.descKeywords){
			String[] splits = s.split(":");
			if(splits == null || splits.length != 2){
				continue;
			}
			String[] values = splits[1].split("\\|");
			if(values == null || values.length == 0){
				continue;
			}
			ArrayList<String> list = new ArrayList<String>();
			for (String va : values) {
				list.add(va);
			}
			descKeywordsMap.put(splits[0], list);
		}
		
		// D1 -- sleep
		ArrayList<String> sleepKeyWordsList = new ArrayList<String>();
		for (String sl : DiagClassifyData.getSleepBiasArray()) {
			sleepKeyWordsList.addAll(descKeywordsMap.get(sl));
		}
		referenceMap.put("D1", sleepKeyWordsList);
		// D2 -- na
		ArrayList<String> naKeyWordsList = new ArrayList<String>();
		for (String na : DiagClassifyData.getNaBiasArray()) {
			naKeyWordsList.addAll(descKeywordsMap.get(na));
		}
		referenceMap.put("D2", naKeyWordsList);
		// D3 -- blood sputum
		ArrayList<String> bloodSputumKeywordList = new ArrayList<String>();
		for (String bl : DiagClassifyData.getBloodSputumBiasArray()) {
			bloodSputumKeywordList.addAll(descKeywordsMap.get(bl));
		}
		referenceMap.put("D3", bloodSputumKeywordList);
		// D4 -- xiong lei tong
		ArrayList<String> xiongleitongKeywordList = new ArrayList<String>();
		for (String xlt : DiagClassifyData.getXiongLeiBiasArray()) {
			xiongleitongKeywordList.addAll(descKeywordsMap.get(xlt));
		}
		referenceMap.put("D4", xiongleitongKeywordList);
		// D5 -- qi li
		ArrayList<String> qiliKeywordList = new ArrayList<String>();
		for (String ql : DiagClassifyData.getQiliBiasArray()) {
			qiliKeywordList.addAll(descKeywordsMap.get(ql));
		}
		referenceMap.put("D5", qiliKeywordList);
		// D6 -- cough
		ArrayList<String> coughKeywordList = new ArrayList<String>();
		for (String co : DiagClassifyData.getCoughBiasArray()) {
			coughKeywordList.addAll(descKeywordsMap.get(co));
		}
		referenceMap.put("D6", coughKeywordList);
		// D7 -- defecate
		ArrayList<String> defecateKeywordList = new ArrayList<String>();
		for (String def : DiagClassifyData.getDefecateBiasArray()) {
			defecateKeywordList.addAll(descKeywordsMap.get(def));
		}
		referenceMap.put("D7", defecateKeywordList);
		// D8 -- fu xie
		ArrayList<String> fuxieKeywordList = new ArrayList<String>();
		for (String fx : DiagClassifyData.getFuxieBiasArray()) {
			fuxieKeywordList.addAll(descKeywordsMap.get(fx));
		}
		referenceMap.put("D8", fuxieKeywordList);
		
		// return result
		return referenceMap;
	}
	
	/**
	 * Check list with number
	 * @param list1
	 * @param list2
	 * @param number
	 * @return
	 */
	public static boolean checkListWithNumber(List<String> inputList, List<String> recordList, int number){
		if (inputList == null || recordList == null) {
			return false;
		}
		int count = 0;
		// inputlist size >= record list size
		if (recordList.size() > inputList.size()) {
			return false;
		}
		
		// input list size <= record list size
		for (String re : recordList) {
			if (inputList.contains(re)) {
				count++;
			}
		}
		if (count == number && count == recordList.size()) {
			return true;
		}else{
			return false;
		}
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
    		if(!string.contains(key)){
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
