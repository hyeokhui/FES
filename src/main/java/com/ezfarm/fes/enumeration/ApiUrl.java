package com.ezfarm.fes.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ApiUrl {
	
	LOGIN("http://dev-api.aibigdata.northstar.co.kr/api/common/login.do"),
	EMBEDDING("http://dev-api.aibigdata.northstar.co.kr/api/nlp/embedding.do"),
	MRC("http://dev-api.aibigdata.northstar.co.kr/api/nlp/mrc.do");
	
	private final String url;
	ApiUrl(String url) {
		this.url = url;
	}
	
	@JsonValue
	@Override
	public String toString() {
		return url;
	}
}
