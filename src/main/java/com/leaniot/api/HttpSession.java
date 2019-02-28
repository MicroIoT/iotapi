package com.leaniot.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.util.UriComponentsBuilder;

import com.leaniot.exception.StatusException;
import com.leaniot.exception.ValueException;

/**
 * 建立与物联网平台的http会话
 *
 * @author 曹新宇
 */
@Component
public abstract class HttpSession {
	private static final String WS_IOT = "/ws_iot";
	private static final String IOTP = "iotp";
	private static final String IOTPS = "iotps";
	private static final String HTTP = "http";
	private static final String HTTPS = "https";
	private static final String WS = "ws";
	private static final String WSS = "wss";
	private static final String regex = "^(" + IOTP + "|" + IOTPS
			+ ")://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

	private String sessionId;
	protected boolean logined = false;
	@Value("${leaniot.iotp.username}")
	private String username;
	@Value("${leaniot.iotp.password}")
	private String password;
	@Value("${leaniot.iotp.uri}")
	private String uri;
	
	private RestTemplate restTemplate;
	
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public HttpSession() {
	}
	/**
	 * 建立http会话。
	 * @param username http会话用户名。
	 * @param password http会话密码。
	 * @param uri http会话uri，格式为：iotp://host:port或者iotps://host:port。
	 */
	public void start() {
		if(!logined) {
			if (!uri.matches(regex))
				throw new ValueException(uri);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add("username", username);
			map.add("password", password);

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

			ResponseEntity<String> response = restTemplate.postForEntity(getRestUri() + "/login", request, String.class);
			String cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
			String[] session = cookie.split(";");
			this.sessionId = session[0].trim();
			this.logined = true;
		}
	}
	/**
	 * 停止http会话。
	 */
	public void stop() {
		if (logined) {
			HttpHeaders header = getSessionHeader();
			HttpEntity<String> request = new HttpEntity<String>(header);
			restTemplate.getForObject(getRestUri() + "/logout", String.class, request);
			this.logined = false;
		}
	}
	
	/**
	 * 获得http会话认证cookie信息。
	 * @return 返回http头。
	 */
	public HttpHeaders getSessionHeader() {
		assert logined : "login first";
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(HttpHeaders.COOKIE, this.sessionId);
		return requestHeaders;
	}

	/**
	 * 获得http会话调用REST的uri。
	 * @return 返回调用REST的uri。
	 */
	public String getRestUri() {
		if (uri.startsWith(IOTP))
			return uri.replaceFirst(IOTP, HTTP);
		else if (uri.startsWith(IOTPS))
			return uri.replaceFirst(IOTPS, HTTPS);
		else
			throw new ValueException(uri);
	}

	/**
	 * 获得http会话调用websocket的uri。
	 * @return 返回调用websocket的uri。
	 */
	public String getWSUri() {
		assert logined : "login first";
		if (uri.startsWith(IOTP))
			return uri.replaceFirst(IOTP, WS) + WS_IOT;
		else if (uri.startsWith(IOTPS))
			return uri.replaceFirst(IOTPS, WSS) + WS_IOT;
		else
			throw new ValueException(uri);
	}

	/**
	 * 调用REST get。
	 * @param getUri 调用get的uri。
	 * @param responseType 调用get返回的类型。
	 */
	protected <T> T getEntity(String getUri, Class<T> responseType) {
		assert logined : "login first";
		HttpHeaders header = getSessionHeader();
		HttpEntity<HttpHeaders> requestEntity = new HttpEntity<HttpHeaders>(null, header);
		try {
			ResponseEntity<T> rssResponse = restTemplate.exchange(getRestUri() + getUri, HttpMethod.GET, requestEntity,
					responseType);
			return rssResponse.getBody();
		} catch (ResourceAccessException e) {
			throw new StatusException(e.getMessage());
		} catch (HttpClientErrorException | HttpServerErrorException | UnknownHttpStatusCodeException e) {
			throw new StatusException(e.getResponseBodyAsString());
		}

	}

	/**
	 * 调用REST get，返回复杂类型。
	 * @param getUri 调用get的uri。
	 * @param queryParams 调用get的查询参数。
	 * @param responseType 调用get返回的类型。
	 */
	protected <T> T getEntity(String getUri, Map<String, String> queryParams, ParameterizedTypeReference<T> responseType) {
		assert logined : "login first";
		HttpHeaders header = getSessionHeader();
		HttpEntity<HttpHeaders> requestEntity = new HttpEntity<HttpHeaders>(null, header);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRestUri() + getUri);
		for(Map.Entry<String, String> queryParam : queryParams.entrySet()) {
		    String key = queryParam.getKey();
		    String value = queryParam.getValue();

		    builder.queryParam(key, value);
		}
		try {
			ResponseEntity<T> rssResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, requestEntity,
					responseType);
			return rssResponse.getBody();
		} catch (ResourceAccessException e) {
			throw new StatusException(e.getMessage());
		} catch (HttpClientErrorException | HttpServerErrorException | UnknownHttpStatusCodeException e) {
			throw new StatusException(e.getResponseBodyAsString());
		}

	}

	/**
	 * 调用REST post。
	 * @param postUri 调用post的uri。
	 * @param request 调用post输入的请求参数。
	 * @param responseType 调用post返回的类型。
	 */
	protected <T> T postEntity(String postUri, Object request, Class<T> responseType) {
		assert logined : "login first";
		HttpHeaders header = getSessionHeader();
		HttpEntity<?> requestEntity = new HttpEntity<>(request, header);
		try {
			ResponseEntity<T> rssResponse = restTemplate.exchange(getRestUri() + postUri, HttpMethod.POST,
					requestEntity, responseType);
			return rssResponse.getBody();
		} catch (ResourceAccessException e) {
			throw new StatusException(e.getMessage());
		} catch (HttpClientErrorException | HttpServerErrorException | UnknownHttpStatusCodeException e) {
			throw new StatusException(e.getResponseBodyAsString());
		}

	}
}
