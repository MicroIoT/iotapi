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

@Component
public abstract class AlarmSubscriber extends AbstractEventSubscriber{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private WebsocketClientSession websocketClientSession;
	
	public AlarmSubscriber() {
		super();
	}

	public WebsocketClientSession getWebsocketClientSession() {
		return websocketClientSession;
	}

	public void setWebsocketClientSession(WebsocketClientSession websocketClientSession) {
		this.websocketClientSession = websocketClientSession;
	}

	public abstract void onAlarm(NotifyObject notifyObject, String alarmType, Object alarmInfo, Date reportTime, Date receiveTime);

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
