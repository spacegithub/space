package com.mr.modules.api.service;

/**
 * Created by feng on 18-3-17
 */
public interface SiteService {
	String start(String groupIndex, String callId) throws Exception;

	Boolean isFinish(String callId) throws Exception;

	String getResultCode(String callId) throws Exception;

	String getThrowableInfo(String callId) throws Exception;

	Boolean delSiteTaskInstance(String callId) throws Exception;
}
