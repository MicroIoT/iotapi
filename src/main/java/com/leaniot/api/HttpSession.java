package com.leaniot.api;

import java.util.Map;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
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
	private static final String REMEMBER_ME = "remember-me";
	private static final String WS_IOT = "/ws_iot";
	private static final String IOTP = "iotp";
	private static final String IOTPS = "iotps";
	private static final String HTTP = "http";
	private static final String HTTPS = "https";
	private static final String WS = "ws";
	private static final String WSS = "wss";
	private static final String regex = "^(" + IOTP + "|" + IOTPS
			+ ")://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

	private static final int MAX_TEXT_MESSAGE_BUFFER_SIZE = 20 * 1024 * 1024;

	private String sessionId;
	private String uri;
	protected boolean logined;
	
	private RestTemplate restTemplate;
	
	public HttpSession() {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(new HttpClientConfig().httpClient());
		this.restTemplate = new RestTemplate(requestFactory);
		this.logined = false;
	}
	/**
	 * 建立http会话。
	 * @param username http会话用户名。
	 * @param password http会话密码。
	 * @param uri http会话uri，格式为：iotp://host:port或者iotps://host:port。
	 */
	public void start(String username, String password, String uri) {
		this.uri = uri;

		if (!this.uri.matches(regex))
			throw new ValueException(uri);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("username", username);
		map.add("password", password);
		map.add(REMEMBER_ME, "1");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(getRestUri() + "/login", request, String.class);
		String cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
		String[] session = cookie.split(";");
		this.sessionId = session[0].substring(REMEMBER_ME.length() + 1);
		this.logined = true;
	}
	/**
	 * 停止http会话。
	 */
	public void stop() {
		if (logined) {
//			stompClient.stop();
			HttpHeaders header = getSessionHeader();
			HttpEntity<String> request = new HttpEntity<String>(header);
			restTemplate.getForObject(getRestUri() + "/logout", String.class, request);
		}
	}
	/**
	 * 建立websocket底层连接。
	 * @param heartbeat web socket心跳。
	 * @return 返回websocket底层连接。
	 */
	public WebSocketStompClient getWebsocketClient(long[] heartbeat) {
		assert logined : "login first";
		
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		container.setDefaultMaxTextMessageBufferSize(MAX_TEXT_MESSAGE_BUFFER_SIZE);
		WebSocketClient client = new StandardWebSocketClient(container);
		WebSocketStompClient stompClient = new WebSocketStompClient(client);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
	    taskScheduler.afterPropertiesSet();
		stompClient.setTaskScheduler(taskScheduler);
		stompClient.setDefaultHeartbeat(heartbeat);
		
		return stompClient;
	}
	/**
	 * 获得http会话认证cookie信息。
	 * @return 返回http头。
	 */
	public HttpHeaders getSessionHeader() {
		assert logined : "login first";
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Cookie", REMEMBER_ME + "=" + this.sessionId);
		return requestHeaders;
	}

	/**
	 * 获得http会话调用REST的uri。
	 * @return 返回调用REST的uri。
	 */
	public String getRestUri() {
		if (this.uri.startsWith(IOTP))
			return this.uri.replaceFirst(IOTP, HTTP);
		else if (this.uri.startsWith(IOTPS))
			return this.uri.replaceFirst(IOTPS, HTTPS);
		else
			throw new ValueException(uri);
	}

	/**
	 * 获得http会话调用websocket的uri。
	 * @return 返回调用websocket的uri。
	 */
	public String getWSUri() {
		assert logined : "login first";
		if (this.uri.startsWith(IOTP))
			return this.uri.replaceFirst(IOTP, WS) + WS_IOT;
		else if (this.uri.startsWith(IOTPS))
			return this.uri.replaceFirst(IOTPS, WSS) + WS_IOT;
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
