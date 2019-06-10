package top.microiot.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
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

import top.microiot.api.dto.RestGeoResults;
import top.microiot.api.dto.RestPage;
import top.microiot.domain.IoTObject;
import top.microiot.dto.DistinctInfo;
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

	private String sessionId;
	protected boolean logined = false;
	
    private HttpSessionProperties p;
	
	public void setHttpSessionProperties(HttpSessionProperties p) {
		this.p = p;
	}

	private RestTemplate restTemplate;
	
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public HttpSession() {
	}
	
	/**
	 * 建立http会话。
	 */
	public void start() {
		if(!logined) {
			if (!getUri().matches(regex))
				throw new ValueException(p.getUri());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add("username", p.getUsername());
			map.add("password", p.getPassword());
			map.add(REMEMBER_ME, "1");
	
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
	
			ResponseEntity<String> response = restTemplate.postForEntity(getRestUri() + "/login", request, String.class);
			List<String> cookie = response.getHeaders().get(HttpHeaders.SET_COOKIE);
			for(String c : cookie) {
				if(c.startsWith(REMEMBER_ME)) {
					this.sessionId = c.split(";")[0];
					break;
				}
			}
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
			restTemplate.postForEntity(getRestUri() + "/logout", request, String.class);
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
		if (getUri().startsWith(IOTP))
			return getUri().replaceFirst(IOTP, HTTP);
		else if (getUri().startsWith(IOTPS))
			return getUri().replaceFirst(IOTPS, HTTPS);
		else
			throw new ValueException(getUri());
	}

	/**
	 * 获得http会话调用websocket的uri。
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
		return p.getUri();
	}
	@SuppressWarnings("unchecked")
	public <T> T getEntityById(IoTObject object, String id) {
		assert logined : "login first";
		String url = "/" + object.getName() + "s/query/id/" + id;
		return (T) getEntity(url, null, object.getIoT());
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getOneEntity(IoTObject object, QueryInfo info) {
		String url = "/" + object.getName() + "s/query/one";
		Map<String, String> queryParams = buildQueryParams(info);
		
		return (T) getEntity(url, queryParams, object.getIoT());
	}
	
	public <T> List<T> getEntityList(IoTObject object, QueryInfo info, ParameterizedTypeReference<List<T>> responseType) {
		assert logined : "login first";
		String url = "/" + object.getName() + "s/query/list";
		Map<String, String> queryParams = buildQueryParams(info);
	
		return getEntity(url, queryParams, responseType);
	}
	
	public <T> Page<T> getEntityPage(IoTObject object, QueryPageInfo info, ParameterizedTypeReference<RestPage<T>> responseType) {
		assert logined : "login first";
		String url = "/" + object.getName() + "s/query/page";
		Map<String, String> queryParams = buildQueryPageParams(info);
	
		return getEntity(url, queryParams, responseType);
	}
	
	public <T> RestGeoResults<T> getEntityGeo(IoTObject object, QueryNearPageInfo info, ParameterizedTypeReference<RestGeoResults<T>> responseType) {
		assert logined : "login first";
		String url = "/" + object.getName() + "s/query/geo";
		Map<String, String> queryParams = buildQueryNearParams(info);
	
		return getEntity(url, queryParams, responseType);
	}
	
	public <T> List<T> getEntityAggregate(IoTObject object, QueryInfo info, ParameterizedTypeReference<List<T>> responseType) {
		assert logined : "login first";
		String url = "/" + object.getName() + "s/query/aggregate";
		Map<String, String> queryParams = buildQueryParams(info);
	
		return getEntity(url, queryParams, responseType);
	}
	
	public <T> List<T> getEntityDistinct(IoTObject object, DistinctInfo info, ParameterizedTypeReference<List<T>> responseType) {
		assert logined : "login first";
		String url = "/" + object.getName() + "s/query/distinct";
		Map<String, String> queryParams = buildQueryDistinctParams(info);
	
		return getEntity(url, queryParams, responseType);
	}
	
	public int count(IoTObject object, QueryInfo info) {
		assert logined : "login first";
		String url = "/" + object.getName() + "s/query/count";
		Map<String, String> queryParams = buildQueryParams(info);
	
		return getEntity(url, queryParams, Integer.class);
	}
	
	public boolean exist(IoTObject object, QueryInfo info) {
		assert logined : "login first";
		String url = "/" + object.getName() + "s/query/exist";
		Map<String, String> queryParams = buildQueryParams(info);
	
		return getEntity(url, queryParams, Boolean.class);
	}
	
	protected <T> T getEntity(String getUri, Map<String, String> queryParams, Class<T> responseType) {
		assert logined : "login first";
		HttpHeaders header = getSessionHeader();
		
		HttpEntity<HttpHeaders> requestEntity = new HttpEntity<HttpHeaders>(null, header);
		UriComponentsBuilder builder = getUrl(getUri, queryParams);
		
		try {
			ResponseEntity<T> rssResponse = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity,
					responseType);
			return rssResponse.getBody();
		} catch (ResourceAccessException e) {
			throw new StatusException(e.getMessage());
		} catch (HttpClientErrorException | HttpServerErrorException | UnknownHttpStatusCodeException e) {
			throw new StatusException(e.getResponseBodyAsString());
		}

	}
	
	private Map<String, String> buildQueryParams(QueryInfo info) {
		Map<String, String> queryParams = new HashMap<String, String>();
		if(info != null) {
			if(info.getFilter() != null)
				queryParams.put("filter", info.getFilter());
			if(info.getSort() != null)
				queryParams.put("sort", info.getSort());
			if(info.getCollation() != null)
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
	
	protected <T> T getEntity(String getUri, Map<String, String> queryParams, ParameterizedTypeReference<T> responseType) {
		assert logined : "login first";
		HttpHeaders header = getSessionHeader();
		
		HttpEntity<HttpHeaders> requestEntity = new HttpEntity<HttpHeaders>(null, header);
		UriComponentsBuilder builder = getUrl(getUri, queryParams);
		
		try {
			ResponseEntity<T> rssResponse = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity,
					responseType);
			return rssResponse.getBody();
		} catch (ResourceAccessException e) {
			throw new StatusException(e.getMessage());
		} catch (HttpClientErrorException | HttpServerErrorException | UnknownHttpStatusCodeException e) {
			throw new StatusException(e.getResponseBodyAsString());
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
		HttpHeaders header = getSessionHeader();
		header.setContentType(MediaType.APPLICATION_JSON_UTF8);

		HttpEntity<?> requestEntity = new HttpEntity<>(request, header);
		try {
			ResponseEntity<T> rssResponse = restTemplate.exchange(getRestUri() + uri, method,
					requestEntity, responseType);
			return rssResponse.getBody();
		} catch (ResourceAccessException e) {
			throw new StatusException(e.getMessage());
		} catch (HttpClientErrorException | HttpServerErrorException | UnknownHttpStatusCodeException e) {
			throw new StatusException(e.getResponseBodyAsString());
		}
	}
	
	private UriComponentsBuilder getUrl(String getUri, Map<String, String> queryParams) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRestUri() + getUri);
		if(queryParams != null) {
			for(Map.Entry<String, String> queryParam : queryParams.entrySet()) {
			    String key = queryParam.getKey();
			    String value = queryParam.getValue();

			    builder.queryParam(key, value);
			}
		}
		return builder;
	}
}
