package com.um.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.um.data.DataBaseSetting;
import com.um.data.DiagClassifyData;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.mongodb.converter.EhealthRecordConverter;

public class MedicineByDescription {
	
	
	/**
	 * Get all similar records based on the description
	 * @param eHealthRecords
	 * @param description
	 * @return
	 */
	public static List<EHealthRecord> getSimilaryEHealthRecords(List<EHealthRecord> eHealthRecordsByBatch, String diagnose,String description){
		if ("".equals(description) || eHealthRecordsByBatch.size() == 0 || eHealthRecordsByBatch == null) {
			return null;
		}
		// 1.2 split the diagnose
		String[] diagkeywords = diagnose.split(",");
		if(diagkeywords.length == 0 || diagkeywords == null){
			return null; 
		}
		// 1.3 classify the records based on diagnose info
		List<EHealthRecord> classifiedRecords = DiagMedicineProcess.getRecordsByDiagnose(diagkeywords, eHealthRecordsByBatch);
				
		// 1.4 get the similar records based on the description info
		List<EHealthRecord> similarRecords = DiagMedicineProcess.getEhealthRecordByDescription(description, classifiedRecords);
				
		return similarRecords;
	}
	
	
	/**
	 * Format the request parameters
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> parseRequestParameter(HttpServletRequest request){
		if(request == null) return null;
		Map<String, String> resultMap = new HashMap<String, String>();
		// 1. parse the request parameters
		String batch = request.getParameter("batch").trim(); // batch 
		
		// Time status
		String timeStatusString = request.getParameter("timestatus").trim();
		// 2. diagnose 
		String diagnoseString = "";
		String xuString = request.getParameter("xu").trim();
		String tanyuString = request.getParameter("tanyu").trim();
		String tanshiString = request.getParameter("tanshi").trim();
		String[] zhengxingString = request.getParameterValues("zhengxing");
		
		diagnoseString += xuString;
		diagnoseString += tanyuString.equals("yes") ? " 痰瘀" : " ";
		diagnoseString += tanshiString.equals("yes") ? " 痰湿" : " ";
		
		if(zhengxingString != null && zhengxingString.length > 0){
			for(String s : zhengxingString){
				diagnoseString += s + " ";
			}
		}
		
		// 3. description
		// 3.1 parse the description parameters
		String descriptionString = ""; // description
		String hanre = request.getParameter("hanre"); // 寒热
		String sweat = request.getParameter("sweat"); // 汗
		String xonglei = request.getParameter("xonglei"); // 胸肋痛
		String futong = request.getParameter("futong"); // 腹痛
		String[] tengtong = request.getParameterValues("tengtong"); //疼痛
		String[] bodydiscomfort = request.getParameterValues("bodydiscomfort"); //头身胸腹不适
		String defecate = request.getParameter("defecate"); //大便
		String[] constipation = request.getParameterValues("constipation"); //便秘
		String urinate = request.getParameter("urinate"); // 小便
		String tonguecolor = request.getParameter("tonguecolor"); // 舌色
		String coatedtongue = request.getParameter("coatedtongue"); // 舌苔
		String sputumamount = request.getParameter("sputumamount"); // 痰量
		String sputumcolor = request.getParameter("sputumcolor"); // 痰色
		String sleep = request.getParameter("sleep"); // 眠
		String na = request.getParameter("na"); // 纳
		String energy = request.getParameter("energy"); // 气力
		String[] pulse = request.getParameterValues("pulse"); // 脉
		String thirst = request.getParameter("thirst"); //口渴
		String taste = request.getParameter("taste"); // 口味
		String cough = request.getParameter("cough"); // 咳嗽
		
		// 3.2 format the description for return
		descriptionString = timeStatusString + "," + hanre + "," + sweat + "," + xonglei + "," + futong + ","
							+ convertArrayToString(tengtong) + convertArrayToString(bodydiscomfort)
							+ defecate + "," + convertArrayToString(constipation) + urinate + "," 
							+  tonguecolor + "," + coatedtongue + "," + sputumamount
							+ "," + sputumcolor + "," + sleep + "," + na + "," + energy + "," 
							+ convertArrayToString(pulse) + thirst
							+ "," + taste + "," + cough;
		
		resultMap.put("batch", batch); 
		resultMap.put("diagnose", diagnoseString);
		resultMap.put("description", descriptionString);
		return resultMap;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static String getDescriptionString(HttpServletRequest request){
		if (request == null) {
			return "";
		}
		String description = "";
		
		String hanre = request.getParameter("hanre"); // 寒热
		String sweat = request.getParameter("sweat"); // 汗
		String xonglei = request.getParameter("xonglei"); // 胸肋痛
		String futong = request.getParameter("futong"); // 腹痛
		String[] tengtong = request.getParameterValues("tengtong"); //疼痛
		String[] bodydiscomfort = request.getParameterValues("bodydiscomfort"); //头身胸腹不适
		String defecate = request.getParameter("defecate"); //大便
		String[] constipation = request.getParameterValues("constipation"); //便秘
		String urinate = request.getParameter("urinate"); // 小便
		String tonguecolor = request.getParameter("tonguecolor"); // 舌色
		String coatedtongue = request.getParameter("coatedtongue"); // 舌苔
		String sputumamount = request.getParameter("sputumamount"); // 痰量
		String sputumcolor = request.getParameter("sputumcolor"); // 痰色
		String sleep = request.getParameter("sleep"); // 眠
		String na = request.getParameter("na"); // 纳
		String energy = request.getParameter("energy"); // 气力
		String[] pulse = request.getParameterValues("pulse"); // 脉
		String thirst = request.getParameter("thirst"); //口渴
		String taste = request.getParameter("taste"); // 口味
		String cough = request.getParameter("cough"); // 咳嗽
		
		description = hanre + "," + sweat + "," + xonglei + "," + futong + ","
				+ convertArrayToString(tengtong) + convertArrayToString(bodydiscomfort)
				+ defecate + "," + convertArrayToString(constipation) + urinate + "," 
				+  tonguecolor + "," + coatedtongue + "," + sputumamount
				+ "," + sputumcolor + "," + sleep + "," + na + "," + energy + "," 
				+ convertArrayToString(pulse) + thirst
				+ "," + taste + "," + cough;
		
		return description;
	}
	
	
	/**
	 * 	Format the description with standard Chinese description
	 * @param desString
	 * @return
	 */
	public static String getFormatedDescirption(String desString){
		if("".equals(desString)) return "";
		
		String result = "";
		
		// 1. get the standard description of Chinese
		Map<String, String> descTableMap = convertArraysToMap(DiagClassifyData.descriptionStrings);
		Map<String, String> normalTableMap = convertArraysToMap(DiagClassifyData.normalAndBaddescription);
		
		// 2. format the description of input to return 
		String[] splits = desString.split(",");
		if( splits == null || splits.length == 0 ) return result;
		
		int length = splits.length;
		for (int i = 0; i < length; i++) {
			String s = splits[i];
			if(normalTableMap.get(s) == null) continue;
			if( !normalTableMap.get(s).equals("0") ){ 
				result += (i== length-1) ? descTableMap.get(s) : descTableMap.get(s) + ",";
			}
		}
		return result;
	}
	
	/**
	 *  convert the array of string to a string.
	 * @param arrays
	 * @return
	 */
	public static String convertArrayToString(String[] arrays){
		if(arrays == null || arrays.length == 0){
			return "";
		}
		String resultString = "";
		for( String s : arrays ){
			resultString += s + ",";
		}
		return resultString;
	}
	
	/**
	 *  Convert array of description to map
	 * @param arrays
	 * @return
	 */
	public static Map<String, String> convertArraysToMap(String[] arrays){
		if( arrays == null || arrays.length == 0 ) return null;

		Map<String, String> result = new HashMap<String, String>();
		for( String a : arrays ){
			String[] split = a.split(":");
			if( split == null || split.length != 2){
				continue;
			}
			result.put(split[0], split[1]);
		}
		return result;
	}
	
	/**
	 * Convert array of description keywords to map list
	 * @param arrays
	 * @return
	 */
	public static Map<String, ArrayList<String>> convertArraysToMapList(String[] arrays){
		if (arrays == null || arrays.length == 0) return null;
		
		Map<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
		
		for (String string : arrays) {
			ArrayList<String> contents = new ArrayList<String>();
			String[] splits = string.split(":");
			if (splits == null || splits.length != 2) continue;
					
			String[] list = splits[1].split("\\|");
			if (list == null || list.length == 0) continue;
			for (String lString : list) {
				contents.add(lString);
			}
			result.put(splits[0], contents);
		}
		
		return result;
	}
	
	/**
	 *  Get the e-health records by the batch
	 *  
	 * @param batch
	 * @return
	 */
	public static List<EHealthRecord> getRecordsByBatch(String batch){
		
		if("".equals(batch)) return null;
		List<EHealthRecord> eHealthRecordsByBatch = new ArrayList<EHealthRecord>();
		// built the conditions structure of batch
		List<EHealthRecord> allList = getAllRecords();
		if (batch.equals("null")) {
			eHealthRecordsByBatch = allList;
		}else{
			for (EHealthRecord eRecord : allList) {
				if (eRecord.getBatchString().equals(batch.substring(0, 4))) {
					eHealthRecordsByBatch.add(eRecord);
				}
			}
		}
		return eHealthRecordsByBatch;
	}
	
	/**
	 * Get all records 
	 * @return
	 */
	public static List<EHealthRecord> getAllRecords(){
		
		final List<EHealthRecord> eHealthRecords = new ArrayList<EHealthRecord>();
		
		MongoClient client = new MongoClient(DataBaseSetting.host,DataBaseSetting.port);
		MongoDatabase db = client.getDatabase(DataBaseSetting.database);
		MongoCollection<Document> ehealthRecordCollection = db.getCollection(DataBaseSetting.ehealthcollection);
		
		// List of ehealth record
		FindIterable<Document> iterable = ehealthRecordCollection.find();
		
		iterable.forEach(new Block<Document>() {

			@Override
			public void apply(Document document) {
				// TODO Auto-generated method stub
				EHealthRecord eHealthRecord = EhealthRecordConverter.toEHealthRecord(document);
	        	
	        	if(eHealthRecord != null){
	        		eHealthRecords.add(eHealthRecord);
	        	}
			}
		});
		
		client.close();
		return eHealthRecords;
	}
	
	
	/**
	 *  格式化病症描述
	 *  	：将描述文本转化成输入格式的描述方式
	 * @param description
	 * @return
	 */
	public static String formattedDescriptionByCount(String description){
		
		if( description.equals("") ) return null;
		String formattedDescriptionString = "";
		
		// keyword list
		
		Map<String, ArrayList<String>> keywordList = MedicineByDescription.convertArraysToMapList(DiagClassifyData.descKeywords);
		// the description code
		Set<String> descriptionFormatted = new HashSet<String>();
		Set<String> keywordListSet = keywordList.keySet();
		for (String k : keywordListSet) {
			ArrayList<String> contents = keywordList.get(k);
			if (contents == null || contents.size() == 0 ) { continue; }
			
			for (String c : contents) {
				if (description.contains(c)) {
					// match 
					descriptionFormatted.add(k);
					break;
				}
			}
		}
		
		if (descriptionFormatted.size() == 0) {
			return formattedDescriptionString;
		}
		// key word reference
		Map<String, String> descTableMap = MedicineByDescription.convertArraysToMap(DiagClassifyData.descriptionStrings);
		Map<String, String> normalTableMap = MedicineByDescription.convertArraysToMap(DiagClassifyData.normalAndBaddescription);
		
		
		for (String d : descriptionFormatted) {
			if (normalTableMap.get(d) == null || normalTableMap.get(d).equals("0") || descTableMap.get(d) == null) {
				continue;
			}
			formattedDescriptionString += descTableMap.get(d) + ",";
		}
		
		return formattedDescriptionString;
	}
	
	/**
	 * Get all medicines in records with repeat
	 * @param eHealthRecords
	 * @return
	 */
	public static List<String> getAllMedicinesNameWithRepeat(List<EHealthRecord> eHealthRecords){
		if (eHealthRecords == null || eHealthRecords.size() == 0) {
			return null;
		}
		// statistics result
		List<String> medicinesList = new ArrayList<String>();
		
		// get all medicines in this records list
		for(EHealthRecord e : eHealthRecords){
			if(e.getChineseMedicines() != null && e.getChineseMedicines().size() > 0){
				for(ChineseMedicine c : e.getChineseMedicines()){
					medicinesList.add(c.getNameString());
				}
			}
		}
		
		return medicinesList;
	}
	
}
