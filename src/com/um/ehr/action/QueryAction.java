package com.um.ehr.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionSupport;
import com.um.model.EHealthRecord;
import com.um.util.MedicineByDescription;

import net.sf.json.JSONObject;

public class QueryAction extends ActionSupport implements ServletRequestAware{

	/**
	 *  Query action
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger("com.um.ehr.action.QueryAction");
	
	private HttpServletRequest request;
	
	private String result;
	
	
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
			String value = eHealthRecord.getPatientInfo().getName() + "  " + eHealthRecord.getDate();
			String key = eHealthRecord.getRegistrationno();
			infoMap.put(key, value);
		}
		
		map.put("infoMap", infoMap);
		
		JSONObject json = JSONObject.fromObject(map);
		
		result = json.toString();
		
		logger.info("return: " + json.toString());
 
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
