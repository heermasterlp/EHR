package com.um.ehr.action;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionSupport;
import com.um.data.DiagClassifyData;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.util.BasedOnRulePredict;
import com.um.util.DiagMedicineProcess;
import com.um.util.EhealthUtil;
import com.um.util.MachineLearningPredict;
import com.um.util.MedicineByDescription;

public class PredictAciton extends ActionSupport implements ServletRequestAware{
	
	private static Logger logger = Logger.getLogger("com.um.ehr.action.PredictAciton"); 
	
	private static final long serialVersionUID = 1L;
     
	private HttpServletRequest request;
	 
	private String result;
	
	/**
     * Action :  Predict medicines with input description
     * @return SUCCESS
     */
    public String predictByInput(){
        try {
            /**
             * 1. Parse request parameters
             */
        	// 1.1 batch
        	String batch = request.getParameter("batch"); 
            
        	// 1.2 get diagnose and description from request
            String diagnose = EhealthUtil.getDiagnoseFromHttpRequest(request);
            String description = EhealthUtil.getDescriptionFromHttpRequest(request);
            
            // 1.3 formatted the description to output
    		String descconvertString = MedicineByDescription.getFormatedDescirption(description);
    		String descriptionString = diagnose + descconvertString;
            
            /**
             * 2. Predict medicines based on statistics method
             */
            List<String> medicineListByStatis = new ArrayList<String>(); // predict medicines result
            // 2.1 statistics medicines larger than 90% records
    		int OUTPUTNUMBER = 16; // the number of output medicine
    		int SIMILARRECORDSIZE = 6; // similar record number
    		
    		// 2.2 get all records with same batch
    		List<EHealthRecord> eHealthRecordsByBatch = MedicineByDescription.getRecordsByBatch(batch); // all record with same batch
    		
    		// 2.3 statistics name and number of medicines in this batch records
    		Map<String, Integer> allMedicineMap = DiagMedicineProcess.statisEhealthMedicine(eHealthRecordsByBatch);
    		
    		// 2.4  find the medicines with percent larger than 90% 
    		int allRecordsNum = eHealthRecordsByBatch.size(); // the number of this batch records
    		double percent = 0.9; // the percent 
    		
    		List<String> medicineWithInevitable = DiagMedicineProcess.statisMedicineWithPercent(allMedicineMap, allRecordsNum, percent);
    		
    		// 2.6 get similar records based on the description
    		List<EHealthRecord> similaryRecords = MedicineByDescription.getSimilaryEHealthRecords(eHealthRecordsByBatch, diagnose, description);
    		
    		if (similaryRecords != null && similaryRecords.size() > 0) {
    			// 2.7 statistic the medicines in the similar records
    			Set<String> cnmedicineSet = DiagMedicineProcess.getMedicinesByDescription(description, similaryRecords);
    			for (String med : medicineWithInevitable) {
    				if (cnmedicineSet.contains(med)) {
    					// remove the medicine from medicine list not in the cnmedicine set
    					medicineListByStatis.add(med);
    				}
    			}
    			for (String cn : cnmedicineSet) {
    				if (!medicineListByStatis.contains(cn)) {
    					// add to result list
    					medicineListByStatis.add(cn);
    				}
    			}
    		}else{
    			// similar record is zero , only return medicine with percent large than 90%
    			medicineListByStatis.addAll(medicineWithInevitable);
    		}
    		// set output number of medicines
    		if (medicineListByStatis.size() > OUTPUTNUMBER) {
    			medicineListByStatis = medicineListByStatis.subList(0, OUTPUTNUMBER);
    		}
    		
    		// 2.7 Sort the medicine with same order with machine learning result
    		List<String> medicineListByStatisticSorted = EhealthUtil.sortMedicineList(medicineListByStatis);
            
            /**
             * 3. Predict medicines based on machine learning method
             */
    		//  3.1 initial the input parameters of machine learning
    		List<String> inputcode = MachineLearningPredict.parseDiagAndDesc(diagnose, description); // format the input parameters
    		// 	3.2 predict the medicines based on the machine learning
    		List<String> medicineListByMachine = MachineLearningPredict.predict(inputcode); // the predict result of machine learning
    		
            /**
             * 4. Predict medicines based on rules
             */
    		List<String> medicineListByRules = BasedOnRulePredict.predictBasedOnRules(descriptionString);
    		
            /**
             * 5. Return result
             */
    		List<String> medicineList = new ArrayList<>(); // compensive result with statistics, machine learning and rules
    		// check medicines and count in those list
    		Map<String, String> uninomap = EhealthUtil.getUnionMapFromPredictMethod(medicineListByStatisticSorted, medicineListByMachine, medicineListByRules);
    		
    		// result not enough, add medicines in 2 list
    		Set<String> unionMapKeyset = uninomap.keySet();
    		// add medicines in 3 list
    		for (String un : unionMapKeyset) {
				if (uninomap.get(un).equals("3")) {
					medicineList.add(un);
				}
			}
    		
    		// add medicines in 2 list
    		if (medicineList.size() < OUTPUTNUMBER) {
    			for (String un : unionMapKeyset) {
    				if (uninomap.get(un).equals("2") && medicineList.size() < OUTPUTNUMBER) {
    					medicineList.add(un);
    				}
    			}
			}
    		
    		// add medicines only in 1 list
    		if (medicineList.size() < OUTPUTNUMBER) {
    			for (String un : unionMapKeyset) {
    				if (uninomap.get(un).equals("1") && medicineList.size() < OUTPUTNUMBER) {
    					medicineList.add(un);
    				}
    			}
			}
    		
    		// sorted
    		List<String> medicineListSorted = new ArrayList<String>();
    		
    		// fix medicinelist, change dangshen or taizishen to dangshen(taizishen)
    		medicineList = EhealthUtil.fixMedicineList(medicineList);
    		
    		// sorted
    		for (String med : DiagClassifyData.machineMedicine) {
				if (medicineList.contains(med)) {
					medicineListSorted.add(med);
				}
			}
    		
    		// Format similiary records result
    		Map<String, ArrayList<String>> formattedSimilarRecords = EhealthUtil.formatSimilarRecordAsReturn(similaryRecords, SIMILARRECORDSIZE);
    		
    		// fix 
    		medicineListByStatisticSorted = EhealthUtil.fixMedicineList(medicineListByStatisticSorted);
    		
    		/*
    		 * Format return records result with color
    		 */
    		// statistics result
    		Map<String, ArrayList<String>> statisticsResultMap = EhealthUtil.formatStatisticsResult(medicineListByStatisticSorted, medicineListByMachine, medicineListByRules);
    		// machine result
    		Map<String, ArrayList<String>> machineResultMap = EhealthUtil.formatMachineLearningResult(medicineListByStatisticSorted, medicineListByMachine, medicineListByRules);
    		// rules result
    		Map<String, ArrayList<String>> ruleResultMap = EhealthUtil.formatRulesResult(medicineListByStatisticSorted, medicineListByMachine, medicineListByRules);
    		
            Map<String,Object> map = new HashMap<String,Object>();
            
            map.put("descconvertString", descconvertString);
            
            map.put("medicineListByStatistics", statisticsResultMap);
            map.put("medicineListByMachine", machineResultMap);
            map.put("medicineListByRules", ruleResultMap);
            map.put("medicineList", medicineListSorted);
            
            //count
            map.put("statisticscount", medicineListByStatisticSorted.size());
            map.put("machinecount", medicineListByMachine.size());
            map.put("rulecount", medicineListByRules.size());
            map.put("listcount", medicineListSorted.size());
            
            map.put("formattedSimilarRecords", formattedSimilarRecords);
            map.put("similarSize", (similaryRecords != null ? similaryRecords.size() : 0));
            
            JSONObject json = JSONObject.fromObject(map);//将map对象转换成json类型数据
            result = json.toString();//给result赋值，传递给页面
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SUCCESS;
    }
    
    
    /**
     *  Predict by case
     * @return
     */
    public String predictByCase(){
    	
    	logger.info("predict by case start!");
    	
    	// 1. get the input parameters
    	String countString = request.getParameter("count"); // the order number of records
    			
    	// 2. find all records with batch 2012
    	List<EHealthRecord> allList = MedicineByDescription.getRecordsByBatch("2012");
    	if ("".equals(countString)) {
    		return SUCCESS;
    	}
    	// 3. find the target record based on the conditions
    	EHealthRecord targetRecord = EhealthUtil.getEHealthRecordByCountOrRegno(allList, countString);
    	if(targetRecord == null){ return SUCCESS; }
    	// record order number
    	int count = EhealthUtil.getRecordCountBasedRegno(allList, targetRecord.getRegistrationno());
    	
    	// 5. the origin medicines in target record            
    	List<String> orignMedicines = new ArrayList<String>();
    	if( targetRecord.getChineseMedicines() != null && targetRecord.getChineseMedicines().size() > 0 ){
    		for(ChineseMedicine c : targetRecord.getChineseMedicines()){
    			orignMedicines.add(c.getNameString());
    		}
    	}
    	// 6. sort the origin medicines with a fix order
    	List<String> orignMedicinesSorted = EhealthUtil.sortMedicineList(orignMedicines);
    	
    	// fix orign medicines
    	orignMedicinesSorted = EhealthUtil.fixMedicineList(orignMedicinesSorted);
    			
    	// 7. predict medicines with machine learning 
    	//  7.1 initial input parameters of machine learning
    	List<String> inputcode = MachineLearningPredict.parseDiagAndDescByEhealthRecords(targetRecord);
    			
    	//  7.2 predict medicines with machine learning
    	List<String> medicineListByMachine = MachineLearningPredict.predict(inputcode); // the result of machine learning
    			
    	// 8. calculate the accuracy
    	double statisticsPercent = 0.0; // the accuracy of case-based
    	double mechineLearningPercent = 0.0;  // the accuracy of machine learning
    			
    	statisticsPercent = 100.0 * orignMedicinesSorted.size() / orignMedicinesSorted.size(); //accuracy of case-based
    	
    	int countOfMachineLearning = EhealthUtil.getNumberOfMedicinesInOrignAndMachieResult(orignMedicinesSorted, medicineListByMachine);
    	mechineLearningPercent = 100.0 * countOfMachineLearning / orignMedicines.size(); // the accuracy of machine learning
    	
    	// 9. format result
    	Map<String, Object> map = new HashMap<String, Object>();
    	// orign medicine color map
    	Map<String, ArrayList<String>> orignColorMap = new HashMap<String, ArrayList<String>>();
    	ArrayList<String> orignblackList = new ArrayList<String>();
    	ArrayList<String> orignredList = new ArrayList<String>();
    	if (orignMedicinesSorted != null) {
			for (String so : orignMedicinesSorted) {
				if (medicineListByMachine.contains(so)) {
					orignblackList.add(so);
				}else{
					orignredList.add(so);
				}
			}
		}
    	orignColorMap.put("black", orignblackList);
    	orignColorMap.put("red", orignredList);
    	
    	// machine learning result color map
    	Map<String, ArrayList<String>> colorMap = new HashMap<String, ArrayList<String>>();
    	ArrayList<String> blackList = new ArrayList<String>();
    	ArrayList<String> redList = new ArrayList<String>();
    	
    	// format machine learning result
    	if (medicineListByMachine != null) {
    		for (String med : medicineListByMachine) {
    			if (orignMedicinesSorted.contains(med)) {
    				blackList.add(med);
    			}else{
    				redList.add(med);
    			}
    		}
		}
    	
    	colorMap.put("black", blackList);
    	colorMap.put("red", redList);
    	
    	DecimalFormat df = new DecimalFormat("#.##");
    	map.put("orignMedicines", orignColorMap);
    	map.put("medicineListByMachine", colorMap);
    	map.put("statisticsPercent", df.format(statisticsPercent));
    	map.put("mechineLearningPercent", df.format(mechineLearningPercent));
    	
    	// target record
    	map.put("targetRecord", EhealthUtil.encryptionRecord(targetRecord));
    	map.put("count", count+1);
    	
    	JSONObject json = JSONObject.fromObject(map);
    	result = json.toString();
    	
    	logger.info("predict by case end!");
    	
    	return SUCCESS;
    }
    
    
    @Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
