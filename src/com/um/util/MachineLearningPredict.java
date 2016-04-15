package com.um.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWComplexity;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWLogicalArray;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import com.um.data.DiagClassifyData;
import com.um.model.EHealthRecord;

import newpredictum.Predictum;

/**
 *  Machine Learning predict medicines
 * @author heermaster
 *
 */
public class MachineLearningPredict {

	/**
	 *  Predict the medicine based on the input code!
	 *  
	 * @param inputcode
	 * @param threshold
	 * @return
	 */
	public static List<String> predict(List<String> inputcode){
		if( inputcode == null || inputcode.size() == 0 ){
			return null;
		}
		List<String> medicineListByMachine = new ArrayList<String>();
		// Machine learning object
		Predictum predictum = null;
		int predictConditionCount = inputcode.size(); // the number of machine learning input parameters
		MWNumericArray x = null; /* Array of x values */
		Object[] y = null;
		
		try {
			// predict bean
			predictum = new Predictum();
			
			int[] dims1 = { 1, predictConditionCount }; // the x input parameters of machine learning
			x = MWNumericArray.newInstance(dims1, MWClassID.DOUBLE,MWComplexity.REAL); // x input matrix
			// initial x input of machine learning
			for(int i = 1; i <= predictConditionCount; i++){
				x.set(i, Integer.valueOf(inputcode.get(i-1)));
			}
			// machine learning predict medicines
			y = predictum.newpredictum(1, x);
			if(y == null) return null;
			
			MWLogicalArray yy = (MWLogicalArray) y[0];
			
			if(yy == null || yy.numberOfElements() == 0) return null;
			
			int count = yy.numberOfElements(); // output variable count
			
			// sort the predict result
			String[] sortedmedicine = DiagClassifyData.machineMedicine;
			
			for( int i = 0; i < count; i++ ){
				if( (Boolean) yy.get(i + 1) ){
					medicineListByMachine.add(sortedmedicine[i]);
				}
			}
			
			
		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.gc();
			predictum = null;
		}
		return medicineListByMachine;
	}
	
	/**
	 *  Format the input parameters of machine learning
	 *  
	 * @param diagnose
	 * @param description
	 * @return
	 */
	public static List<String> parseDiagAndDesc(String diagnose, String description){
		if("".equals(diagnose) || "".equals(description)) return null;
		
		// 1. split the diagnose
		String[] diagStrings = diagnose.split(" ");
		String diagString = "";
		for(String s:diagStrings){
			diagString += s + ",";
		}
		// 2. built the x input of machine learning
		description = diagString + description;
		
		Map<String, String> descriptionCode = new HashMap<String, String>();
		// 3. the standard keyword table
		Map<String, HashMap<String, ArrayList<String>>> descriptionTableMap = DiagMedicineProcess.getDescriptionMap(DiagClassifyData.machineKeywords); //描述关键字列表
		
		Set<String> project = descriptionTableMap.keySet();
		HashMap<String, ArrayList<String>> desHashMap = null;
		for(String descString : project){
			String valueString = "0";
			desHashMap = descriptionTableMap.get(descString);
			Set<String> desSet = desHashMap.keySet();
			for(String s:desSet){
				ArrayList<String> keywordList = desHashMap.get(s);
				for(String key: keywordList){
					if(description.contains(key)){
						// match
						valueString = s;
						break;
					}
				}
			}
			descriptionCode.put(descString, valueString);
		}
		// Sort the result with a fix order
		List<String> inputcode = new ArrayList<String>(); 
		String[] sortedcode = DiagClassifyData.sortCode;
		for(String s : sortedcode){
			inputcode.add(descriptionCode.get(s));
		}
		return inputcode;
	}
	
	/**
	 * Convert diagnose and description to machine learning code 
	 * @param 
	 * @return
	 */
	public static List<String> parseDiagAndDescByEhealthRecords( EHealthRecord e){
		if( e == null ){ return null; }
		
		List<String> titleList = new ArrayList<String>(); // 标题
		// 2. 诊断关键字
		List<String> diagnoseKeyWords = getDiagnoseKeywords();
		titleList.addAll(diagnoseKeyWords);
		// 3. 描述关键字
		Map<String, HashMap<String, ArrayList<String>>> descriptionTableMap = getDescriptionMap();
		titleList.addAll(descriptionTableMap.keySet());
		
		HashMap<String, String> dataMap = new HashMap<String, String>();
		
		// diagnose 
		String diagnoseString = e.getChinesediagnostics(); // 中医诊断
		for(String keyword : diagnoseKeyWords){
			if(diagnoseString.contains(keyword)){
				dataMap.put(keyword, "1");
			}else{
				dataMap.put(keyword, "0");
			}
		}
		
		// description
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

		return inputcode;
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
