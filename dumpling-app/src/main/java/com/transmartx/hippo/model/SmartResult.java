package com.transmartx.hippo.model;

import com.transmartx.hippo.constant.ResultEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "result")
public class SmartResult<T> implements Serializable{

	private static final long serialVersionUID = 6041553915547228905L;

	@ApiModelProperty(value = "状态码,200表示成功,其他表示失败")
	private int code;

	@ApiModelProperty(value = "数据")
	private transient T data;

	@ApiModelProperty(value = "操作信息")
	private transient Object msg;

	@ApiModelProperty(value = "请求状态,0表示成功,其他表示失败")
	private int status;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Object getMsg() {
		return msg;
	}

	public void setMsg(Object msg) {
		this.msg = msg;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public static SmartResult success(){
		SmartResult result = new SmartResult();
		result.setCode(ErrorCodeConstant.CODE_STATUS_SUCCESS);
		result.setMsg(ErrorCodeConstant.SUCCESS);
		return result;
	}

	public static <T> SmartResult<T> success(T data){
		SmartResult<T> result = new SmartResult<>();
		result.setCode(ErrorCodeConstant.CODE_STATUS_SUCCESS);
		result.setMsg(ErrorCodeConstant.SUCCESS);
		result.setData(data);
		return result;
	}

	public static SmartResult get(int code) {
		SmartResult result = new SmartResult();
		result.setCode(code);
		result.setMsg(ErrorCodeConstant.getErrorMsgByCode(code));
		return result;
	}

	public static SmartResult fail(){
		SmartResult result = new SmartResult();
		result.setCode(ErrorCodeConstant.CODE_STATUS_SYSTEM_ERROR);
		result.setMsg(ErrorCodeConstant.FAIL);
		return result;
	}

	public static SmartResult fail(String msg){
		SmartResult result = new SmartResult();
		result.setCode(ErrorCodeConstant.CODE_STATUS_SYSTEM_ERROR);
		result.setMsg(msg);
		return result;
	}

	public static SmartResult fail(int code) {
		SmartResult result = new SmartResult();
		result.setCode(code);
		result.setMsg(ResultEnum.findByCode(code).msg);
		return result;
	}

	public static SmartResult of(int code) {
		SmartResult result = new SmartResult();
		result.setCode(code);
		result.setMsg(ResultEnum.findByCode(code).msg);
		return result;
	}

	public static SmartResult custom(int code, String msg) {
		SmartResult result = new SmartResult();
		result.setCode(code);
		result.setMsg(msg);
		return result;
	}

	public static SmartResult fail(ResultEnum code) {
		SmartResult result = new SmartResult();
		result.setCode(code.code);
		result.setMsg(code.msg);
		return result;
	}

	public static <T> SmartResult<T> fail(T data){
		SmartResult<T> result = new SmartResult<>();
		result.setCode(ErrorCodeConstant.CODE_STATUS_SYSTEM_ERROR);
		result.setMsg(ErrorCodeConstant.FAIL);
		result.setData(data);
		return result;
	}

	@Override
	public String toString() {
		return "SmartResult{" +
				"code=" + code +
				", data=" + data +
				", msg=" + msg +
				", status=" + status +
				'}';
	}
}
