package com.transmartx.hippo.utils.sign;

public class Test {

	public void GetKey(String str) {
		String filepath=".";
    	if (str.length() > 0) {
            RSAEncrypt.genKeyPair(str);  
    	} else {
			RSAEncrypt.genKeyPair(filepath);
    	}
	}

	public void restReq() throws Exception{
		AsiainfoHeader head = new AsiainfoHeader();
		head.setAppId("100502515");
		head.setBusiSerial("8A5CDF7970BDDCFDEBAE97BFA426AC73");
		head.setNonce("1546856439HHQWLWZHFSAMUMFSFJ1045");
		head.setTimestamp("20190122100239839");
		head.setRoute_type("1");
		head.setRoute_value("19874299394");
		String body = "{\"params\":{\"sourceCode\":\"1085\",\"crmpfPubInfo\":{\"orgId\":\"\",\"rowsPerPage\":\"\",\"staffId\":\"\",\"cityCode\":\"\",\"countryCode\":\"\",\"paging\":\"\",\"pageNum\":\"\"},\"reqInfo\":{\"picNameR\":\"\",\"channelId\":\"ECOP\",\"billId\":\"0306104017693\",\"custName\":\"74bd1f9434ead49d5b3a0394bbf76efc\",\"custCertNo\":\"122219cf9363904d451099fb3139e2ca72e13e5e7963b45f\",\"transactionID\":\"20020190305164320150077\",\"picNameRPath\":\"BOSS200108520190306104018082190_R.jpg\"},\"targetCode\":\"200990\",\"busiCode\":\"RETURN_IDENTITY_INFO\",\"version\":\"1.0\"}}";

		System.out.println(RestHttpclient.post(head, body));
	}
	
    public static void main(String[] args) throws Exception {  
       new Test().restReq();
    }
    
}