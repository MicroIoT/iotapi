package top.microiot.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import top.microiot.api.client.HttpClientSession;
import top.microiot.api.device.HttpDeviceSession;

/**
 * http会话配置类
 *
 * @author 曹新宇
 */
@Configuration
public class HttpSessionConfig {
	@Bean(initMethod = "start")
    @Primary
    @Scope("prototype")
	public HttpClientSession httpClientSession() {
		return new HttpClientSession(httpSessionProperties());
	}
	
	@Bean(initMethod = "start")
    @Primary
    @Scope("prototype")
	public HttpDeviceSession httpDeviceSession() {
		return new HttpDeviceSession(httpSessionProperties());
	}

	@Bean
    @Primary
    @ConfigurationProperties(prefix = "microiot.connect")
    public HttpSessionProperties httpSessionProperties(){
	    return new HttpSessionProperties();
    }


}
