package top.microiot.api.client.stomp;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import top.microiot.api.client.WebsocketClientSession;
import top.microiot.api.stomp.AbstractEventSubscriber;
import top.microiot.domain.Alarm;
import top.microiot.domain.NotifyObject;
import top.microiot.domain.attribute.DataType;
import top.microiot.exception.NotFoundException;

/**
 * 客户端告警处理，客户端收到告警通知后，将告警通知转换为用户定义的类型，供用户处理告警。
 *
 * @author 曹新宇
 */
@Component
public abstract class AlarmSubscriber extends AbstractEventSubscriber{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private WebsocketClientSession websocketClientSession;
	
	/**
	 * 客户端告警处理构造函数。
	 */
	public AlarmSubscriber() {
		super();
	}

	public WebsocketClientSession getWebsocketClientSession() {
		return websocketClientSession;
	}

	public void setWebsocketClientSession(WebsocketClientSession websocketClientSession) {
		this.websocketClientSession = websocketClientSession;
	}

	/**
	 * 不同客户端的具体告警处理的实现。
	 * @param notifyObject 上报告警的对象，可以是设备，也可以是场地。
	 * @param alarmType 告警类型名称。
	 * @param alarmInfo 告警具体信息。
	 * @param reportTime 告警上报时间。
	 * @param receiveTime 告警在平台上接收到的时间。
	 */
	public abstract void onAlarm(NotifyObject notifyObject, String alarmType, Object alarmInfo, Date reportTime, Date receiveTime);

	/**
	 * 将告警信息转变为用户的类型，调用设备的告警处理。
	 * @param event 告警通知。
	 */
	@Override
	public void onEvent(Object event) {
		Alarm alarm = (Alarm)event;
		logger.debug("alarm: " + alarm.getAlarmType());
		Object info = null;
		if(alarm.getAlarmInfo() != null) {
			DataType type = alarm.getNotifyObject().getAlarmTypes().get(alarm.getAlarmType()).getDataType();
			
			Object typeInfo = getType(alarm);
			if(typeInfo instanceof Class<?>) {
				Class<?> t = (Class<?>) typeInfo;
				info = type.getData(alarm.getAlarmInfo(), t);
			}
			else if(typeInfo instanceof ParameterizedTypeReference<?>) {
				ParameterizedTypeReference<?> t = (ParameterizedTypeReference<?>) typeInfo;
				info = type.getData(alarm.getAlarmInfo(), t);
			}
			else
				throw new NotFoundException(alarm.getAlarmType() + " converter");
		}
		
		onAlarm(alarm.getNotifyObject(), alarm.getAlarmType(), info, alarm.getReportTime(), alarm.getReceiveTime());
	}

	private Object getType(Alarm alarm) {
		return types.get(alarm.getAlarmType());
	}
}
