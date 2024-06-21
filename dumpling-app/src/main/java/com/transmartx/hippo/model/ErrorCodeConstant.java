package com.transmartx.hippo.model;


public class ErrorCodeConstant {
	
	public static final int STATUS_SUCCESS = 1;
	public static final int STATUS_FAIL= 0;
	
	public static final int CODE_STATUS_SUCCESS = 200;
	public static final int CODE_STATUS_SYSTEM_ERROR = -1;
	public static final int CODE_STATUS_PARAM_ERROR = -2;

	public static final int CODE_CAMPAIGN_NAME_ERROR = -10010;
	public static final int CODE_CAMPAIGN_NAME_EXIST = -10011;

	public static final int CODE_RULE_ERROR = -20010;
	public static final int CODE_RULE_EXIST_ERROR = -20011;
	
	public static final String SUCCESS = "success";
	public static final String FAIL = "fail";
	public static final String PARAMERROR = "parameters error";
	public static final String USER_NO_EXIST = "user no exist";
	public static final String SYSTEMERROR = "system error";

	private ErrorCodeConstant(){
	}
	
	public static String getErrorMsgByCode(int code){
		String msg = null;
		switch (code) {
		case CODE_STATUS_SUCCESS:
			msg = "success";
			break;
		case CODE_STATUS_SYSTEM_ERROR:
			msg = "system error";
			break;
		case CODE_STATUS_PARAM_ERROR:
			msg = "param error";
			break;
		case CODE_CAMPAIGN_NAME_ERROR:
			msg = "活动名称不存在";
			break;
		case CODE_CAMPAIGN_NAME_EXIST:
			msg = "活动名称已存在";
			break;
		default:
			break;
		}
		return msg;
	}
	

}
