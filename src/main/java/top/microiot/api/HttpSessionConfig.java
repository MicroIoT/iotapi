package top.microiot.api;

import java.util.concurrent.TimeUnit;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import top.microiot.api.client.HttpClientSession;
import top.microiot.api.device.HttpDeviceSession;

/**
 * http会话配置类
 *
 * @author 曹新宇
 */
@Configuration
@EnableConfigurationProperties({HttpClientProperties.class, HttpSessionProperties.class})
public class HttpSessionConfig {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private HttpClientProperties p;
	@Autowired
	private HttpSessionProperties p1;
	
	@Bean(initMethod = "start")
	@Scope("prototype")
	public HttpClientSession httpClientSession() {
		HttpClientSession httpClientSession = new HttpClientSession();
		httpClientSession.setRestTemplate(restTemplate());
		httpClientSession.setHttpSessionProperties(p1);
		
		return httpClientSession;
	}
	
	@Bean(initMethod = "start")
	@Scope("prototype")
	public HttpDeviceSession httpDeviceSession() {
		HttpDeviceSession httpDeviceSession = new HttpDeviceSession();
		httpDeviceSession.setRestTemplate(restTemplate());
		httpDeviceSession.setHttpSessionProperties(p1);
		
		return httpDeviceSession;
	}

	@Bean
	@Scope("prototype")
	public RestTemplate restTemplate() {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient());
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}
	
	@Bean
    @Scope("prototype")
    public HttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(p.getRequestTimeout())
                .setConnectTimeout(p.getConnectTimeout())
                .setSocketTimeout(p.getSocketTimeout()).build();

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingConnectionManager())
                .setKeepAliveStrategy(connectionKeepAliveStrategy())
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))  
                .build();
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
        poolingConnectionManager.setMaxTotal(p.getMaxTotalConnections());  
        poolingConnectionManager.setDefaultMaxPerRoute(p.getDefaultMaxPerRoute());  
        return poolingConnectionManager;
    }

    @Bean
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext httpContext) {
                HeaderElementIterator it = new BasicHeaderElementIterator
                        (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase("timeout")) {
                        return Long.parseLong(value) * 1000;
                    }
                }
                return p.getDefaultKeepAliveTimeMillis();
            }
        };
    }

    @Bean
    public Runnable idleConnectionMonitor(final PoolingHttpClientConnectionManager connectionManager) {
        return new Runnable() {
            @Override
            @Scheduled(fixedDelay = 10000)
            public void run() {
                try {
                    if (connectionManager != null) {
                        logger.trace("run IdleConnectionMonitor - Closing expired and idle connections...");
                        connectionManager.closeExpiredConnections();
                        connectionManager.closeIdleConnections(p.getCloseIdleConnectionWaitTimeSecs(), TimeUnit.SECONDS);
                    } else {
                        logger.trace("run IdleConnectionMonitor - Http Client Connection manager is not initialised");
                    }
                } catch (Exception e) {
                    logger.error("run IdleConnectionMonitor - Exception occurred. msg={}, e={}", e.getMessage(), e);
                }
            }
        };
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("poolScheduler");
        scheduler.setPoolSize(50);
        return scheduler;
    }
}
