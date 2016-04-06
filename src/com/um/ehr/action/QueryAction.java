package com.um.ehr.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.opensymphony.xwork2.ActionSupport;
import com.um.data.DiagClassifyData;
import com.um.ehr.setting.DataBaseSetting;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.mongodb.converter.EhealthRecordConverter;
import com.um.util.DiagMedicineProcess;
import com.um.util.EhealthUtil;
import com.um.util.MedicineByDescription;

import net.sf.json.JSONObject;

/**
 *  Query action
 */
public class QueryAction extends ActionSupport implements ServletRequestAware{

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger("com.um.ehr.action.QueryAction");
	
	private HttpServletRequest request;
	
	private String result;
	
	private EHealthRecord targetRecord;
	
	
	public EHealthRecord getTargetRecord() {
		return targetRecord;
	}

	public void setTargetRecord(EHealthRecord targetRecord) {
		this.targetRecord = targetRecord;
	}
	
	/**
	 * Action : query records by input one description
	 * @return
	 */
	public String queryRecordByOneDescription(){
		logger.info("Query patient's record based one description start!");
		/*
		 * 1. get request parameters
		 */
		String batch = request.getParameter("batch");
		String descriptionStr = request.getParameter("description");
		
		/*
		 *  2. create reference table
		 */
		String description = "";
		Map<String, String[]> descKeywords =  DiagClassifyData.getDescKeywords();
		List<String> descList = new ArrayList<String>();
		for (String desc : DiagClassifyData.descriptionStrings) {
			if (desc.contains(descriptionStr)) {
				String[] splits = desc.split(":");
				String[] valueStrings = descKeywords.get(splits[0]);
				if (valueStrings != null && valueStrings.length > 0) {
					for (String v : valueStrings) {
						descList.add(v);
					}
				}
			}
		}
		
		logger.info("descrition : " + descList);
		
		/*
		 *  3. get all batch records
		 */
		List<EHealthRecord> eHealthRecordsByBatch = MedicineByDescription.getRecordsByBatch(batch); // all record with same batch
		
		List<EHealthRecord> similarRecords = new ArrayList<EHealthRecord>();
		for (EHealthRecord eHealthRecord : eHealthRecordsByBatch) {
			for (String string : descList) {
				if (eHealthRecord.getConditionsdescribed().contains(string)) {
					similarRecords.add(eHealthRecord);
					break;
				}
			}
		}
		logger.info("similar size: " + similarRecords.size());
		
		/*
		 *  4. format result
		 */
		Map<String, ArrayList<String>> formattedSimilarRecords = new HashMap<>();
		for (EHealthRecord eRecord : similarRecords) {
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
		}
		
		/*
		 *  5. return result
		 */
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("infoMap", formattedSimilarRecords);
		
		JSONObject json = JSONObject.fromObject(map);
		
		result = json.toString();
		
		logger.info("Query patient's record based one description end!");
		
		return SUCCESS;
	}

	/**
	 *  Action: query records by patient name
	 *  
	 * @return
	 */
	public String queryRecordByPatientName(){
		logger.info("Query patient's record based on name");
		
		//1. get request parameters
		String batch = request.getParameter("batch");
		String pname = request.getParameter("pname");
		
		//2. get batch records
		List<EHealthRecord> eHealthRecordsByBatch = MedicineByDescription.getRecordsByBatch(batch);
		
		//3. get records by name
		List<EHealthRecord> ehealthList = new ArrayList<EHealthRecord>();
		for (EHealthRecord e : eHealthRecordsByBatch) {
			if (e.getPatientInfo() == null) {
				continue;
			}
			if (e.getPatientInfo().getName().equals(pname.trim())) {
				ehealthList.add(e);
			}
		}
		// 4. format return result
		Map<String, Object> map = new HashMap<>();
		
		Map<String, String> infoMap = new HashMap<>();
		for (EHealthRecord eHealthRecord : ehealthList) {
			if (eHealthRecord.getPatientInfo() == null) {
				continue;
			}
			String value = eHealthRecord.getPatientInfo().getName() + "---" + eHealthRecord.getDate();
			String key = eHealthRecord.getRegistrationno();
			infoMap.put(key, value);
		}
		
		map.put("infoMap", infoMap);
		
		JSONObject json = JSONObject.fromObject(map);
		
		result = json.toString();
		
		logger.info("Query by name end!");
 
		return SUCCESS;
	}
	
	/**
	 * Query records based on medicines
	 * @return
	 */
	public String queryRecordByMedicines(){
		logger.info("Query records by medicines begin!");
		
		// 1. parse the request parameters
		String batch = request.getParameter("batch").trim(); // batch 
		String medicine = request.getParameter("medicines").trim(); // request medicine
		String[] medicines = medicine.split(" ");
		
		List<EHealthRecord> targetList = new ArrayList<EHealthRecord>(); // target records list
				
		// 2 get all records with same batch
		List<EHealthRecord> eHealthRecordsByBatch = MedicineByDescription.getRecordsByBatch(batch); // all record with same batch
				
		// 3. find the records by medicine name
		for (EHealthRecord eHealthRecord : eHealthRecordsByBatch) {
			if (eHealthRecord.getChineseMedicines() == null || eHealthRecord.getChineseMedicines().size() == 0) {
				continue;
			}
			int count = 0;
			for (ChineseMedicine cm : eHealthRecord.getChineseMedicines()) {
				for (String med : medicines) {
					if (cm.getNameString().equals(med)) {
						count++;
					}
				}
			}
			if (count == medicines.length) {
				// all medicine in this record
				eHealthRecord = EhealthUtil.encryptionRecord(eHealthRecord);
				targetList.add(eHealthRecord);
			}
		}
		// 4. format return result
		Map<String, Object> map = new HashMap<>();
				
		Map<String, String> infoMap = new HashMap<>();
		for (EHealthRecord eHealthRecord : targetList) {
			if (eHealthRecord.getPatientInfo() == null) {
				continue;
			}
			String value = eHealthRecord.getPatientInfo().getName() + "---" + eHealthRecord.getDate();
			String key = eHealthRecord.getRegistrationno();
			infoMap.put(key, value);
		}
				
		map.put("infoMap", infoMap);
				
		JSONObject json = JSONObject.fromObject(map);
				
		result = json.toString();
		
		logger.info("Query records by medicines end!");
		
		return SUCCESS;
	}
	
	
	/**
	 * Query records based on input description
	 * @return
	 */
	public String queryRecordByInput(){
		logger.info("Query records based on input description begin!");
		
		/**
         * 1. Parse request parameters
         */
    	// get parameters
    	String batch = request.getParameter("batch");
        String timestatus = request.getParameter("timestatus");
        String xu = request.getParameter("xu");
        String tanyu = request.getParameter("tanyu");
        String tanshi = request.getParameter("tanshi");
        String zhengxing = request.getParameter("zhengxing");
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
        
    	// format diagnose and description
        String diagnose = "";
        String description = "";
        
        // diagnose
        diagnose = zhengxing + (tanyu.equals("yes") ? "痰瘀," : "") + (tanshi.equals("yes") ? "痰湿,":"") + xu;
        logger.info("diagnose: " + diagnose);
        // description
        description = timestatus + "," +sputumamount + "," + sputumcolor + "," + cough + "," + na + "," 
        				+ defecate + "," + urinate + "," + xionglei + ","
        				+ futong + "," + tonguecolor + "," 
        				+ coatedtongue + "," + energy + "," + sleep + "," + hanre + ","
        				+ sweat + "," + thirst + "," + taste;
        description += pulse.contains(",") ? "," + pulse : "";
        description += tengtong.contains(",") ? tengtong : "";
        description += bodydiscomfort.contains(",") ? bodydiscomfort : "";
        description += constipation == null ? "" : "xiexie";
		
		/**
		 * 2. Case-base statistics to predict medicines
		 */
		
		// 2.2 get all records with same batch
		List<EHealthRecord> eHealthRecordsByBatch = MedicineByDescription.getRecordsByBatch(batch); // all record with same batch
		
		// 2.6 get similar records based on the description
		List<EHealthRecord> similaryRecords = MedicineByDescription.getSimilaryEHealthRecords(eHealthRecordsByBatch, diagnose, description);
		
		// 4. format return result
		Map<String, Object> map = new HashMap<>();
		
		Map<String, ArrayList<String>> formattedSimilarRecords = new HashMap<>();
		int count = 0;
		for (EHealthRecord eRecord : similaryRecords) {
			if (count > 20) {
				break;
			}
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
			count++;
		}
		
		// output 20
		map.put("formattedSimilarRecords", formattedSimilarRecords);
				
		JSONObject json = JSONObject.fromObject(map);
				
		result = json.toString();
		
		logger.info("Query records based on input description end!");
		
		return SUCCESS;
	}
	
	/**
	 * Query record detail information
	 * @return
	 */
	public String detailRecord(){
		logger.info("query record detail start");
		// 1. get request parameters
		String ehealthregno = request.getParameter("ehealthregno");
		logger.info("requestno: " + ehealthregno);
		// 2. query record by condition
		BasicDBObject condition = new BasicDBObject("ehealthrecord.registrationno",ehealthregno);
		
		MongoClient client = new MongoClient(DataBaseSetting.host,DataBaseSetting.port);
		MongoDatabase db = client.getDatabase(DataBaseSetting.database);
		MongoCollection<Document> ehealthRecordCollection = db.getCollection(DataBaseSetting.ehealthcollection);
		
		FindIterable<Document> iterable = ehealthRecordCollection.find(condition);
		
		Map<String, Object> map = new HashMap<>();
		
		
		if (iterable == null || iterable.first() == null) {
			
			client.close();
			return SUCCESS;
		}
		
		// 3. format result
		Document targetRecordDoc = iterable.first();
		
		targetRecord = EhealthUtil.encryptionRecord(EhealthRecordConverter.toEHealthRecord(targetRecordDoc));
		
		map.put("targetRecord", targetRecord);
		
		JSONObject json = JSONObject.fromObject(map);
		
		result = json.toString();
		
		client.close();
		logger.info("query record detail end");
		return SUCCESS;
	}
	
	/**
	 * Query records by condition
	 * @return
	 */
	public String queryRecordsByCondition(){
		logger.info("query records by condition start!");
		
		List<EHealthRecord> targetRecordsList = new ArrayList<>();
		
		// 1. get request parameters
		String batch = request.getParameter("batch");
		String pname = request.getParameter("pname");
		String process = request.getParameter("process");
		String medicine = request.getParameter("medicines");
		String[] medicines = null;
		if (medicine != null && !"".equals(medicine)) {
			medicines = medicine.split(" ");
		}
		
		// 2.2 get all records with same batch
		List<EHealthRecord> eHealthRecordsByBatch = MedicineByDescription.getRecordsByBatch(batch); // all record with same batch
		
		for (EHealthRecord eHealthRecord : eHealthRecordsByBatch) {
			boolean isMatch = false;
			// patient name
			if (pname != null && !"".equals(pname)) {
				if (!eHealthRecord.getPatientInfo().getName().equals(pname)) {
					isMatch = false;
				}else{
					isMatch = true;
				}
			}else{
				isMatch = true;
			}
			
			// record process
			if (process != null && !"".equals(process)) {
				if (!eHealthRecord.getProcessString().equals(process)) {
					isMatch = isMatch && false;
				}else{
					isMatch = isMatch && true;
				}
			}else{
				isMatch = isMatch && true;
			}
			// medicines
			if (medicines != null && medicines.length > 0) {
				if (eHealthRecord.getChineseMedicines() == null || eHealthRecord.getChineseMedicines().size() == 0) {
					continue;
				}
				int count = 0;
				for (ChineseMedicine cm : eHealthRecord.getChineseMedicines()) {
					for (String med : medicines) {
						if (cm.getNameString().equals(med)) {
							count++;
						}
					}
				}
				if (count == medicines.length) {
					// all medicine in this record
					isMatch = isMatch && true;
				}else{
					isMatch = isMatch && false;
				}
			}else{
				isMatch = isMatch && true;
			}
			// match add to list
			if (isMatch) {
				targetRecordsList.add(eHealthRecord);
			}
		}
		
		// 3. format result
		Map<String, Object> map = new HashMap<>();
		
		Map<String, ArrayList<String>> formattedSimilarRecords = new HashMap<>();
		for (EHealthRecord eRecord : targetRecordsList) {
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
		}
		
		map.put("infoMap", formattedSimilarRecords);
		
		JSONObject json = JSONObject.fromObject(map);
		
		result = json.toString();
		
		logger.info("query records by condition end!");
		return SUCCESS;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
