package com.um.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.um.data.DiagClassifyData;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;

public class EhealthUtil {
	
	/**
	 *  encryption the record
	 * @param eHealthRecord
	 * @return
	 */
	public static EHealthRecord encryptionRecord(EHealthRecord eHealthRecord){
		
		// keep the information of patient privacy
		// remove the address, profession, phoneNumber, contact
		if (eHealthRecord == null || eHealthRecord.getPatientInfo() == null) {
			return eHealthRecord;
		}
		
		// remove patient name
		eHealthRecord.getPatientInfo().setName(eHealthRecord.getPatientInfo().getName().substring(0, 1) + "xx");
		
		// remove profession
		eHealthRecord.getPatientInfo().setProfession("xxxxxxx");
		
		// remove phonenumber
		eHealthRecord.getPatientInfo().setPhoneNumber("xxxxxxxx");
		
		// remove contact
		eHealthRecord.getPatientInfo().setContact("xxxxxxx");
		
		// remove address
		eHealthRecord.getPatientInfo().setAddress("xxxxxxx");
		
		return eHealthRecord;
	}
	
	/**
	 * Format statistcis result with black, blue and red color
	 * @param medicineListByStatistic
	 * @param medicineListByMachineLearnning
	 * @param medicineListByRules
	 * @return
	 */
	public static Map<String, ArrayList<String>> formatStatisticsResult(List<String> statisticsList,List<String> machineList,List<String> ruleList ){
		if (statisticsList == null || machineList == null || ruleList == null) {
			return null;
		}
		HashMap<String, ArrayList<String>> colorMap = new HashMap<String, ArrayList<String>>();
		
		/*
		 * medicines in all list : 3
		 */
		ArrayList<String> blackList = new ArrayList<String>();
		for (String sta : statisticsList) {
			if (machineList.contains(sta) && ruleList.contains(sta)) {
				// medicine in three list
				blackList.add(sta);
			}
		}
		// add black color with medicine in three list
		colorMap.put("black", blackList);
		
		/*
		 * medicine in two list : 2
		 */
		ArrayList<String> blueList = new ArrayList<String>();
		for (String sta : statisticsList) {
			if (machineList.contains(sta) && !ruleList.contains(sta) || !machineList.contains(sta) && ruleList.contains(sta)) {
				// medicine in three list
				blueList.add(sta);
			}
		}
		// add blue color with medicine in two list
		colorMap.put("blue", blueList);
		
		/*
		 * medicine in one list : 1
		 */
		ArrayList<String> redList = new ArrayList<String>();
		for (String sta : statisticsList) {
			if (!machineList.contains(sta) && !ruleList.contains(sta)) {
				// medicine in three list
				redList.add(sta);
			}
		}
		// add red color with medicine only in one list
		colorMap.put("red", redList);
		
		return colorMap;
	}
	
	/**
	 * Format machine learning result with black, blue and red
	 * @param statisticsList
	 * @param machineList
	 * @param ruleList
	 * @return
	 */
	public static Map<String, ArrayList<String>> formatMachineLearningResult(List<String> statisticsList,List<String> machineList,List<String> ruleList ){
		if (statisticsList == null || machineList == null || ruleList == null) {
			return null;
		}
		HashMap<String, ArrayList<String>> colorMap = new HashMap<String, ArrayList<String>>();
		
		/*
		 * medicines in all list : 3
		 */
		ArrayList<String> blackList = new ArrayList<String>();
		for (String sta : machineList) {
			if (statisticsList.contains(sta) && ruleList.contains(sta)) {
				// medicine in three list
				blackList.add(sta);
			}
		}
		// add black color with medicine in three list
		colorMap.put("black", blackList);
		
		/*
		 * medicine in two list : 2
		 */
		ArrayList<String> blueList = new ArrayList<String>();
		for (String sta : machineList) {
			if (statisticsList.contains(sta) && !ruleList.contains(sta) || !statisticsList.contains(sta) && ruleList.contains(sta)) {
				// medicine in three list
				blueList.add(sta);
			}
		}
		// add blue color with medicine in two list
		colorMap.put("blue", blueList);
		
		/*
		 * medicine in one list : 1
		 */
		ArrayList<String> redList = new ArrayList<String>();
		for (String sta : machineList) {
			if (!statisticsList.contains(sta) && !ruleList.contains(sta)) {
				// medicine in three list
				redList.add(sta);
			}
		}
		// add red color with medicine only in one list
		colorMap.put("red", redList);
		
		return colorMap;
	}
	
	/**
	 * Format rules result with black, blue and red
	 * @param statisticsList
	 * @param machineList
	 * @param ruleList
	 * @return
	 */
	public static Map<String, ArrayList<String>> formatRulesResult(List<String> statisticsList,List<String> machineList,List<String> ruleList ){
		if (statisticsList == null || machineList == null || ruleList == null) {
			return null;
		}
		HashMap<String, ArrayList<String>> colorMap = new HashMap<String, ArrayList<String>>();
		
		/*
		 * medicines in all list : 3
		 */
		ArrayList<String> blackList = new ArrayList<String>();
		for (String sta : ruleList) {
			if (statisticsList.contains(sta) && machineList.contains(sta)) {
				// medicine in three list
				blackList.add(sta);
			}
		}
		// add black color with medicine in three list
		colorMap.put("black", blackList);
		
		/*
		 * medicine in two list : 2
		 */
		ArrayList<String> blueList = new ArrayList<String>();
		for (String sta : ruleList) {
			if (statisticsList.contains(sta) && !machineList.contains(sta) || !statisticsList.contains(sta) && machineList.contains(sta)) {
				// medicine in three list
				blueList.add(sta);
			}
		}
		// add blue color with medicine in two list
		colorMap.put("blue", blueList);
		
		/*
		 * medicine in one list : 1
		 */
		ArrayList<String> redList = new ArrayList<String>();
		for (String sta : ruleList) {
			if (!statisticsList.contains(sta) && !machineList.contains(sta)) {
				// medicine in three list
				redList.add(sta);
			}
		}
		// add red color with medicine only in one list
		colorMap.put("red", redList);
		
		return colorMap;
	}
	
	/**
	 * fix medicine list error, convert dangshen or taizishen to dangshen(taizishen) 
	 * @param list
	 * @return
	 */
	public static List<String> fixMedicineList(List<String> list){
		// fix medicinelist
		if (list.contains("党参")) {
			list.remove("党参");
			if (!list.contains("党参(太子参)")) {
				list.add("党参(太子参)");
			}
		}
		if (list.contains("太子参")) {
			list.remove("太子参");
			if (!list.contains("党参(太子参)")) {
				list.add("党参(太子参)");
			}
		}
		return list;
	}
	
	/**
	 * Get diagnose from request
	 * @param request
	 * @return
	 */
	public static String getDiagnoseFromHttpRequest(HttpServletRequest request){
		if (request == null) {
			return "";
		}
		// parse request
		String xu = request.getParameter("xu");
        String tanyu = request.getParameter("tanyu");
        String tanshi = request.getParameter("tanshi");
        String zhengxing = request.getParameter("zhengxing");
        
        // diagnose
        String diagnose = zhengxing + (tanyu.equals("yes") ? "痰瘀," : "") + (tanshi.equals("yes") ? "痰湿,":"") + xu;
        return diagnose;
	}
	
	/**
	 * Get description from request
	 * @param request
	 * @return
	 */
	public static String getDescriptionFromHttpRequest(HttpServletRequest request){
		if (request == null) {
			return "";
		}
		// parse request
		String timestatus = request.getParameter("timestatus");
        String sputumamount = request.getParameter("sputumamount");
        String sputumcolor = request.getParameter("sputumcolor");
        String cough = request.getParameter("cough");
        String pulse = request.getParameter("pulse");
        String na = request.getParameter("na");
        String defecate = request.getParameter("defecate");
        String constipation = request.getParameter("constipation");
        String urinate = request.getParameter("urinate");
        String xionglei = request.getParameter("xionglei");
        String futong = request.getParameter("futong");
        String tengtong = request.getParameter("tengtong");
        String bodydiscomfort = request.getParameter("bodydiscomfort");
        String tonguecolor = request.getParameter("tonguecolor");
        String coatedtongue = request.getParameter("coatedtongue");
        String energy = request.getParameter("energy");
        String sleep = request.getParameter("sleep");
        String hanre = request.getParameter("hanre");
        String sweat = request.getParameter("sweat");
        String thirst = request.getParameter("thirst");
        String taste = request.getParameter("taste");
        
        // description
        String description = timestatus + "," +sputumamount + "," + sputumcolor + "," + cough + "," + na + "," 
        				+ defecate + "," + urinate + "," + xionglei + ","
        				+ futong + "," + tonguecolor + "," 
        				+ coatedtongue + "," + energy + "," + sleep + "," + hanre + ","
        				+ sweat + "," + thirst + "," + taste;
        description += pulse.contains(",") ? "," + pulse : "";
        description += tengtong.contains(",") ? tengtong : "";
        description += bodydiscomfort.contains(",") ? bodydiscomfort : "";
        description += constipation == null ? "" : ",xiexie";
        //fix error
        if (description.contains(",,")) { description.replace(",,", ","); }
        
        return description;
	}
	
	/**
	 * Sort medicine list to output
	 * @param list
	 * @return
	 */
	public static List<String> sortMedicineList(List<String> list){
		if (list == null) {
			return null;
		}
		// sorted
		List<String> listSorted = new ArrayList<String>();
		for( String s : DiagClassifyData.statisticsMedicine ){
			if (list.contains(s)) {
				listSorted.add(s);
			}
		}
		return listSorted;
	}
	
	/**
	 * Get statistics result of medicine from case-base , machine learning and rule methods
	 * @param list1-----case-base result
	 * @param list2-----machine learning result
	 * @param list3-----rule result
	 * @return
	 */
	public static Map<String, String> getUnionMapFromPredictMethod(List<String> list1, List<String> list2, List<String> list3){
		Map<String, String> uninomap = new HashMap<>();
		if (list1 == null || list2 == null || list3 == null) {
			return uninomap;
		}
		Set<String> union = new HashSet<>();
		union.addAll(list1); union.addAll(list2); union.addAll(list3);
		// check medicines and count in those list
		for (String un : union) {
			if (list1.contains(un) && list2.contains(un) && list3.contains(un)) {
				// all in three list
				uninomap.put(un, "3");
			}
			if (!list1.contains(un) && list2.contains(un) && list3.contains(un) ||
					list1.contains(un) && !list2.contains(un) && list3.contains(un) ||
					list1.contains(un) && list2.contains(un) && !list3.contains(un)) {
				// in 2 list
				uninomap.put(un, "2");
			}
			if (!list1.contains(un) && !list2.contains(un) && list3.contains(un) ||
					!list1.contains(un) && list2.contains(un) && !list3.contains(un) ||
					list1.contains(un) && !list2.contains(un) && !list3.contains(un)) {
				// only in 1 list
				uninomap.put(un, "1");
			}
		}
		return uninomap;
	}
	
	/**
	 * Format similar records as result to return
	 * @param SIMILARRECORDSIZE
	 * @return
	 */
	public static Map<String, ArrayList<String>> formatSimilarRecordAsReturn(List<EHealthRecord> similaryRecords, int SIMILARRECORDSIZE){
		// Format similiary records result
		Map<String, ArrayList<String>> formattedSimilarRecords = new HashMap<>();
		int index = 0;
		if (similaryRecords != null) {
			for (EHealthRecord eRecord : similaryRecords) {
				String regno = eRecord.getRegistrationno();
				String recordDescription = eRecord.getConditionsdescribed();
				// format description
				String formattedDescription = MedicineByDescription.formattedDescriptionByCount(recordDescription);
				String formattedMedicines = "";
				if (eRecord.getChineseMedicines() == null || eRecord.getChineseMedicines().size() == 0) {
					continue;
				}
				for (ChineseMedicine cMedicine : eRecord.getChineseMedicines()) {
					formattedMedicines += cMedicine.getNameString() + ",";
				}
				// result
				ArrayList<String> descAndMedicines = new ArrayList<>();
				descAndMedicines.add(formattedDescription);
				descAndMedicines.add(formattedMedicines);
				formattedSimilarRecords.put(regno, descAndMedicines);
				if (index > SIMILARRECORDSIZE) {
					break;
				}
				index++;
			}
		}
		return formattedSimilarRecords;
	}
	
	/**
	 * Get one ehealth record based on the count and regno
	 * @param eHealthRecords
	 * @param string
	 * @return
	 */
	public static EHealthRecord getEHealthRecordByCountOrRegno(List<EHealthRecord> eHealthRecords, String string){
		if (eHealthRecords == null || "".equals(string)) {
			return null;
		}
		EHealthRecord targetRecord = null;
		if( string.length() > 4 ){
    		// the input info is the register number of record
    		for( EHealthRecord e : eHealthRecords ){
    			if( e.getRegistrationno().equals(string) ){
    				targetRecord = e;
    				break;
    			}
    		}
    	}else{
    		// the input info is the order number of all records
    		int count = Integer.valueOf(string); // order number
    		count--;
    		if (count < eHealthRecords.size() && count >= 0) {
    			targetRecord = eHealthRecords.get( count );
			}
    	}
		return targetRecord;
	}
	
	/**
	 * Get record count in all records based on regno
	 * @param eHealthRecords
	 * @param regno
	 * @return
	 */
	public static int getRecordCountBasedRegno(List<EHealthRecord> eHealthRecords, String regno){
		if (eHealthRecords == null || "".equals(regno)) {
			return 0;
		}
		int count = 0;
		for(EHealthRecord e : eHealthRecords){
			if (e.getRegistrationno().equals(regno)) {
				break;
			}
			count++;
		}
		return count;
	}
	
	/**
	 * Get number of medicine in bath orign medicine list and machine learning medicine 
	 * @param orignMedicineList
	 * @param machineLearningList
	 * @return
	 */
	public static int getNumberOfMedicinesInOrignAndMachieResult(List<String> orignMedicineList, List<String> machineLearningList){
		if (orignMedicineList == null || machineLearningList == null) {
			return 0;
		}
		int index = 0;
    	if (machineLearningList != null) {
    		for( String s : machineLearningList ){
        		if( orignMedicineList.contains(s) ){
        			index++;
        		}
        	}
		}
    	return index;
	}
	
}
