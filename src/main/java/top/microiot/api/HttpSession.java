package top.microiot.api;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.util.UriComponentsBuilder;

import top.microiot.api.client.HttpClientSession;
import top.microiot.api.dto.RestGeoResults;
import top.microiot.api.dto.RestPage;
import top.microiot.domain.Device;
import top.microiot.domain.DeviceGroup;
import top.microiot.domain.IoTObject;
import top.microiot.domain.Token;
import top.microiot.domain.User;
import top.microiot.dto.DistinctInfo;
import top.microiot.dto.LoginInfo;
import top.microiot.dto.QueryInfo;
import top.microiot.dto.QueryNearPageInfo;
import top.microiot.dto.QueryPageInfo;
import top.microiot.exception.StatusException;
import top.microiot.exception.ValueException;

/**
 * 建立与物联网平台的http会话类
 *
 * @author 曹新宇
 */
@Component
public abstract class HttpSession {
	private static final String Token = "Authorization";
	private static final String WS_IOT = "/ws_iot";
	private static final String IOTP = "iotp";
	private static final String IOTPS = "iotps";
	private static final String HTTP = "http";
	private static final String HTTPS = "https";
	private static final String WS = "ws";
	private static final String WSS = "wss";
	private static final String regex = "^(" + IOTP + "|" + IOTPS
			+ ")://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

	protected Token token;
	protected boolean logined = false;

	protected HttpSessionProperties httpSessionProperties;

	@Autowired
	private RestTemplate restTemplate;

	public abstract User getCurrentUser();

	public HttpSession(HttpSessionProperties httpSessionProperties) {
		this.httpSessionProperties = httpSessionProperties;
	}

	/**
	 * 建立http会话。
	 */
	public void start() {
		if (!logined) {
			if (!getUri().matches(regex))
				throw new ValueException(httpSessionProperties.getUri());

			try {
				HttpEntity<?> requestEntity = getRequest(getLoginInfo());
				String url = getRestUri() + "/login";

				ResponseEntity<Token> rssResponse = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Token.class);
				this.token = rssResponse.getBody();
			} catch (ResourceAccessException e) {
				throw new StatusException(e.getMessage());
			} catch (HttpClientErrorException | HttpServerErrorException | UnknownHttpStatusCodeException e) {
				throw new StatusException(e.getResponseBodyAsString());
			}

			this.logined = true;
		}
	}

	/**
	 * 停止http会话。
	 */
	public void stop() {
		if (logined) {
			this.logined = false;
			this.token = null;
		}
	}

	public void refreshToken() {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(Token, "Bearer " + token.getRefreshToken());

		HttpEntity<HttpHeaders> requestEntity = new HttpEntity<HttpHeaders>(null, requestHeaders);
		UriComponentsBuilder builder = getUrl("/token", null);
		URI uri = builder.build().encode().toUri();

		ResponseEntity<Token> rssResponse = null;

		try {
			rssResponse = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Token.class);
			token = rssResponse.getBody();
		} catch (ResourceAccessException e) {
			throw new StatusException(e.getMessage());
		} catch (HttpClientErrorException | HttpServerErrorException | UnknownHttpStatusCodeException e) {
			throw new StatusException(e.getResponseBodyAsString());
		}
	}

	protected LoginInfo getLoginInfo() {
		LoginInfo info = new LoginInfo();
		info.setUsername(httpSessionProperties.getUsername());
		info.setPassword(httpSessionProperties.getPassword());
		return info;
	}

	/**
	 * 获得认证token信息。
	 * 
	 * @return 返回http头。
	 */
	public HttpHeaders getHttpAuth() {
		assert logined : "login first";
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(Token, "Bearer " + token.getToken());
		return requestHeaders;
	}

	public StompHeaders getStompAuth() {
		assert logined : "login first";
		StompHeaders header = new StompHeaders();
		header.set(Token, "Bearer " + token.getToken());
		return header;
	}

	/**
	 * 获得http会话调用REST的uri。
	 * 
	 * @return 返回调用REST的uri。
	 */
	public String getRestUri() {
		if (getUri().startsWith(IOTP))
			return getUri().replaceFirst(IOTP, HTTP);
		else if (getUri().startsWith(IOTPS))
			return getUri().replaceFirst(IOTPS, HTTPS);
		else
			throw new ValueException(getUri());
	}

	/**
	 * 获得http会话调用websocket的uri。
	 * 
	 * @return 返回调用websocket的uri。
	 */
	public String getWSUri() {
		assert logined : "login first";
		if (getUri().startsWith(IOTP))
			return getUri().replaceFirst(IOTP, WS) + WS_IOT;
		else if (getUri().startsWith(IOTPS))
			return getUri().replaceFirst(IOTPS, WSS) + WS_IOT;
		else
			throw new ValueException(getUri());
	}

	private String getUri() {
		return httpSessionProperties.getUri();
	}

	/**
	 * 获取指定设备的信息。
	 * 
	 * @param id 设备标识符。
	 * @return 返回指定设备。
	 */
	public Device getDevice(String id) {
		if (id != null && !id.isEmpty()) {
			return getEntity(HttpClientSession.deviceUrl + "/" + id, null, HttpClientSession.deviceType);
		} else
			throw new ValueException("id can't be empty");
	}

	/**
	 * 获取指定设备组的信息。
	 * 
	 * @param id 指定设备组的标识符
	 * @return 返回指定设备组信息。
	 */
	public DeviceGroup getDeviceGroup(String id) {
		if (id != null && !id.isEmpty()) {
			return getEntity(HttpClientSession.deviceGroupUrl + "/" + id, null, HttpClientSession.deviceGroupType);
		} else
			throw new ValueException("device group id can't be empty");
	}

	@SuppressWarnings("unchecked")
	public <T> T getEntityById(Class<? extends IoTObject> object, String id) {
		assert logined : "login first";
		String url = "/" + getIoTObjectName(object) + "/query/id/" + id;
		return (T) getEntity(url, null, object);
	}

	@SuppressWarnings("unchecked")
	public <T> T getOneEntity(Class<? extends IoTObject> object, QueryInfo info) {
		String url = "/" + getIoTObjectName(object) + "/query/one";
		Map<String, String> queryParams = buildQueryParams(info);

		return (T) getEntity(url, queryParams, object);
	}

	public <T> List<T> getEntityList(Class<? extends IoTObject> object, QueryInfo info,
			ParameterizedTypeReference<List<T>> responseType) {
		assert logined : "login first";
		String url = "/" + getIoTObjectName(object) + "/query/list";
		Map<String, String> queryParams = buildQueryParams(info);

		return getEntity(url, queryParams, responseType);
	}

	public <T> Page<T> getEntityPage(Class<? extends IoTObject> object, QueryPageInfo info,
			ParameterizedTypeReference<RestPage<T>> responseType) {
		assert logined : "login first";
		String url = "/" + getIoTObjectName(object) + "/query/page";
		Map<String, String> queryParams = buildQueryPageParams(info);

		return getEntity(url, queryParams, responseType);
	}

	public <T> RestGeoResults<T> getEntityGeo(Class<? extends IoTObject> object, QueryNearPageInfo info,
			ParameterizedTypeReference<RestGeoResults<T>> responseType) {
		assert logined : "login first";
		String url = "/" + getIoTObjectName(object) + "/query/geo";
		Map<String, String> queryParams = buildQueryNearParams(info);

		return getEntity(url, queryParams, responseType);
	}

	public <T> List<T> getEntityAggregate(Class<? extends IoTObject> object, QueryInfo info,
			ParameterizedTypeReference<List<T>> responseType) {
		assert logined : "login first";
		String url = "/" + getIoTObjectName(object) + "/query/aggregate";
		Map<String, String> queryParams = buildQueryParams(info);

		return getEntity(url, queryParams, responseType);
	}

	public <T> List<T> getEntityDistinct(Class<? extends IoTObject> object, DistinctInfo info,
			ParameterizedTypeReference<List<T>> responseType) {
		assert logined : "login first";
		String url = "/" + getIoTObjectName(object) + "/query/distinct";
		Map<String, String> queryParams = buildQueryDistinctParams(info);

		return getEntity(url, queryParams, responseType);
	}

	public int count(Class<? extends IoTObject> object, QueryInfo info) {
		assert logined : "login first";
		String url = "/" + getIoTObjectName(object) + "/query/count";
		Map<String, String> queryParams = buildQueryParams(info);

		return getEntity(url, queryParams, Integer.class);
	}

	public boolean exist(Class<? extends IoTObject> object, QueryInfo info) {
		assert logined : "login first";
		String url = "/" + getIoTObjectName(object) + "/query/exist";
		Map<String, String> queryParams = buildQueryParams(info);

		return getEntity(url, queryParams, Boolean.class);
	}

	protected static String getIoTObjectName(Class<? extends IoTObject> object) {
		return object.getSimpleName().toLowerCase() + "s";
	}

	protected <T> T getEntity(String getUri, Map<String, String> queryParams, Class<T> responseType) {
		assert logined : "login first";
		int count = 0;
		ResponseEntity<T> rssResponse = null;
		while (true) {
			HttpHeaders header = getHttpAuth();

			HttpEntity<HttpHeaders> requestEntity = new HttpEntity<HttpHeaders>(null, header);
			UriComponentsBuilder builder = getUrl(getUri, queryParams);
			URI uri = builder.build().encode().toUri();

			try {
				rssResponse = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, responseType);
				return rssResponse.getBody();
			} catch (ResourceAccessException e) {
				throw new StatusException(e.getMessage());
			} catch (UnknownHttpStatusCodeException e) {
				throw new StatusException(e.getResponseBodyAsString());
			} catch (HttpClientErrorException | HttpServerErrorException e) {
				if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
					if (count++ == 1)
						throw new StatusException(e.getResponseBodyAsString());
					else
						refreshToken();
				} else
					throw new StatusException(e.getResponseBodyAsString());
			}
		}
	}

	protected <T> T getEntity(String getUri, Map<String, String> queryParams, ParameterizedTypeReference<T> responseType) {
		assert logined : "login first";

		int count = 0;
		ResponseEntity<T> rssResponse = null;
		while (true) {
			HttpHeaders header = getHttpAuth();

			HttpEntity<HttpHeaders> requestEntity = new HttpEntity<HttpHeaders>(null, header);
			UriComponentsBuilder builder = getUrl(getUri, queryParams);
			URI uri = builder.build().encode().toUri();

			try {
				rssResponse = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, responseType);
				return rssResponse.getBody();
			} catch (ResourceAccessException e) {
				throw new StatusException(e.getMessage());
			} catch (UnknownHttpStatusCodeException e) {
				throw new StatusException(e.getResponseBodyAsString());
			} catch (HttpClientErrorException | HttpServerErrorException e) {
				if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
					if (count++ == 1)
						throw new StatusException(e.getResponseBodyAsString());
					else
						refreshToken();
				} else
					throw new StatusException(e.getResponseBodyAsString());
			}
		}

	}

	protected <T> T postEntity(String postUri, Object request, Class<T> responseType) {
		assert logined : "login first";
		HttpMethod method = HttpMethod.POST;
		return manageEntity(postUri, method, request, responseType);
	}

	protected <T> T patchEntity(String patchUri, Object request, Class<T> responseType) {
		assert logined : "login first";
		HttpMethod method = HttpMethod.PATCH;
		return manageEntity(patchUri, method, request, responseType);
	}

	protected <T> T deleteEntity(String deleteUri, Object request, Class<T> responseType) {
		assert logined : "login first";
		HttpMethod method = HttpMethod.DELETE;
		return manageEntity(deleteUri, method, request, responseType);
	}

	private <T> T manageEntity(String uri, HttpMethod method, Object request, Class<T> responseType) {
		int count = 0;
		ResponseEntity<T> rssResponse = null;
		while (true) {
			try {
				HttpEntity<?> requestEntity = getRequestWithAuth(request);
				String url = getRestUri() + uri;

				rssResponse = restTemplate.exchange(url, method, requestEntity, responseType);
				return rssResponse.getBody();
			} catch (ResourceAccessException e) {
				throw new StatusException(e.getMessage());
			} catch (UnknownHttpStatusCodeException e) {
				throw new StatusException(e.getResponseBodyAsString());
			} catch (HttpClientErrorException | HttpServerErrorException e) {
				if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
					if (count++ == 1)
						throw new StatusException(e.getResponseBodyAsString());
					else
						refreshToken();
				} else
					throw new StatusException(e.getResponseBodyAsString());
			}
		}
	}

	private HttpEntity<?> getRequestWithAuth(Object request) {
		HttpHeaders header = getHttpAuth();
		header.setContentType(MediaType.APPLICATION_JSON_UTF8);

		HttpEntity<?> requestEntity = new HttpEntity<>(request, header);
		return requestEntity;
	}

	private HttpEntity<?> getRequest(Object request) {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<?> requestEntity = new HttpEntity<>(request);
		return requestEntity;
	}

	private Map<String, String> buildQueryParams(QueryInfo info) {
		Map<String, String> queryParams = new HashMap<String, String>();
		if (info != null) {
			if (info.getFilter() != null)
				queryParams.put("filter", info.getFilter());
			if (info.getSort() != null)
				queryParams.put("sort", info.getSort());
			if (info.getCollation() != null)
				queryParams.put("collation", info.getCollation());
		}
		return queryParams;
	}

	private Map<String, String> buildQueryPageParams(QueryPageInfo info) {
		Map<String, String> queryParams = buildQueryParams(info);
		queryParams.put("pageNumber", String.valueOf(info.getPageNumber()));
		queryParams.put("pageSize", String.valueOf(info.getPageSize()));

		return queryParams;
	}

	private Map<String, String> buildQueryNearParams(QueryNearPageInfo info) {
		Map<String, String> queryParams = buildQueryPageParams(info);
		queryParams.put("x", String.valueOf(info.getX()));
		queryParams.put("y", String.valueOf(info.getY()));
		queryParams.put("maxDistance", String.valueOf(info.getMaxDistance()));
		queryParams.put("metrics", info.getMetrics().toString());

		return queryParams;
	}

	private Map<String, String> buildQueryDistinctParams(DistinctInfo info) {
		Map<String, String> queryParams = buildQueryPageParams(info);
		queryParams.put("field", info.getField());
		queryParams.put("returnClass", info.getReturnClass().toString());

		return queryParams;
	}

	private UriComponentsBuilder getUrl(String getUri, Map<String, String> queryParams) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRestUri() + getUri);
		if (queryParams != null) {
			for (Map.Entry<String, String> queryParam : queryParams.entrySet()) {
				String key = queryParam.getKey();
				String value = queryParam.getValue();

				builder.queryParam(key, value);
			}
		}
		return builder;
	}
}
