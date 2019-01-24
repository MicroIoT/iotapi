package com.leaniot.api.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.leaniot.api.device.HttpDeviceSession;
import com.leaniot.api.device.WebsocketDeviceSession;

public class DeviceTest {

	public static void main(String[] args) {
//		ClientSession session = new ClientSession();
//		session.start("admin@100001", "password", "iotp://localhost:8082");
//		Device device = session.getDevice("5c0510ab5b8faebb40c3f0a3");
//		ActionType type = session.getActionType("update_tag");
//		session.stop();
		HttpDeviceSession cSession = new HttpDeviceSession();
		cSession.start("f2c1e66a-dc69-482d-af4f-f229dab72227", "18559728", "iotp://localhost:8082");
		WebsocketDeviceSession cws = cSession.startWebsocket();
		cws.subscribe(new ToiletGet());
		
		HttpDeviceSession aSession = new HttpDeviceSession();
		aSession.start("0969ca82-5c48-41cc-86fa-caea4149e15a", "60459872", "iotp://localhost:8082");
		
		WebsocketDeviceSession ws = aSession.startWebsocket();
		ws.subscribe(new MyGet());
		Map<String, Class<?>> attType = new HashMap<String, Class<?>>();
		attType.put("screen", Screen.class);
		attType.put("marquee", Marquee.class);
		ws.subscribe(new MySet(attType));
		Map<String, Class<?>> actionType = new HashMap<String, Class<?>>();
		actionType.put("update_tag", Tags.class);
		ws.subscribe(new MyAction(actionType));
//		events.put("energy", new Double(12.34));
//		events.put("water", new Double(34.56));
//		s.reportEvents(events);
		Scanner scanner = new Scanner(System.in);
		while(true) {
			String line = scanner.nextLine();
			if(line.equals("exit"))
				scanner.close();
			else {
				Map<String, Object> info= new HashMap<String, Object>();
				info.put("url", "http://localhost/1.jpg");
				info.put("reason", "download fail");
				aSession.reportAlarm("ProcessFailureAlarm", info);
			}
		}
//		s.stop();
	}

}
