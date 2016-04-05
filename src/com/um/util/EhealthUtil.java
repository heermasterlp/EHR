package com.um.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
}
