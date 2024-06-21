package com.transmartx.hippo.utils.sign;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class RestHttpclient {

	private static final String HTTPS_URL = StringUtil.getProperty(
			"config.properties", "HTTPS_URL");
	
	private static final String sign_private_key = StringUtil.getProperty(
			"config.properties", "SIGN_PRIVATE_KEY");


	public static HttpClient getsslhttpClient() throws Exception {
		
		  SSLContext ctx = SSLContext.getInstance("TLS");  
	        X509TrustManager tm = new X509TrustManager() {
	        	
				@Override
				public void checkClientTrusted(X509Certificate[] arg0,
						String arg1)
						throws java.security.cert.CertificateException {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void checkServerTrusted(X509Certificate[] arg0,
						String arg1)
						throws java.security.cert.CertificateException {
					// TODO Auto-generated method stub
					
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return null;
				}  
	          
	        };  
	        
	        final X509HostnameVerifier DO_NOT_VERIFY = new X509HostnameVerifier() {
	   		 @Override
	   		  public boolean verify(String hostname, SSLSession session) {
	   	            return true;
	   	        }

	   			@Override
	   			public void verify(String arg0, SSLSocket arg1) throws IOException {
	   				// TODO Auto-generated method stub
	   				
	   			}

	   			@Override
	   			public void verify(String arg0, X509Certificate arg1)
	   					throws SSLException {
	   				// TODO Auto-generated method stub
	   				
	   			}

	   			@Override
	   			public void verify(String arg0, String[] arg1, String[] arg2)
	   					throws SSLException {
	   				// TODO Auto-generated method stub
	   				
	   			}
	   	    };
	        
	        ctx.init(null, new TrustManager[] { tm }, null);  
	        SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(ctx,DO_NOT_VERIFY);  
	        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(ssf).build();
		return httpclient;
	}
	
	  public static String post(AsiainfoHeader header, String body) throws Exception {
		    HttpClient httpclient=getsslhttpClient();
	        try {  
	        	AsiainfoHashMap head = AsiainfoHashMap.toAsiainfoHashMap(header);
	            HttpPost postMethod = new HttpPost(HTTPS_URL);	            
	            String content = RSASignature.getSignContent(RSASignature.getSortedMap(head))+body;
	            
	            String signstr = RSASignature.sign(content,sign_private_key);
	            
	            Set keys = head.keySet();
	            Iterator it = keys.iterator();
	            while (it.hasNext()) {
	            	String a= (String)it.next();
	            	postMethod.setHeader(a, head.get(a));
	            }
	            postMethod.setHeader("sign",signstr);
	            
	            StringEntity entity = new StringEntity(body, "application/json", "UTF-8");
			 	postMethod.setEntity(entity);
	        	 
			 	HttpResponse response = httpclient.execute(postMethod);
	        	 
			 	StatusLine respHttpStatus = response.getStatusLine();
			 	int staus= respHttpStatus.getStatusCode();
			 	if(staus==200){
				 	HttpEntity responseBody = response.getEntity();
				 	return EntityUtils.toString(responseBody,"UTF-8");
			 	} else {
				 	return "����ʧ��:"+staus;
			 	}
	        }catch(Exception e){
	        	e.printStackTrace();
	        } finally {  
	        	httpclient.getConnectionManager().shutdown();  
	        }  
	        return null;
	    }  
	
	

}
