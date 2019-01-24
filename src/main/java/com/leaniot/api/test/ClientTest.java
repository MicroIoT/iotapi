package com.leaniot.api.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.leaniot.api.client.HttpClientSession;
import com.leaniot.api.client.WebsocketClientSession;

public class ClientTest {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		HttpClientSession session = new HttpClientSession();
		session.start("admin@100001", "password", "iotp://localhost:8082");
		String deviceId = "5c0510ab5b8faebb40c3f0a3";
//		Device device = session.getDevice(deviceId);
//		ActionType type = session.getActionType("update_tag");
		WebsocketClientSession wsSession = session.startWebsocket();
		
		Map<String, Class<?>> m = new HashMap<String, Class<?>>();
		m.put("ProcessFailureAlarm", ProcessFailureAlarm.class);
		wsSession.subscribe(deviceId, new MyAlarm(m));
		
		while(true) {
			String line = scanner.nextLine();
			for(int i = 0; i < 200; i ++) {
				Screen s =  (Screen) wsSession.get(deviceId, "screen", Screen.class);
				System.out.println("layout: " + s.getFix().getLayout());
				wsSession.set(deviceId, "screen", getScreen());
				wsSession.set(deviceId, "marquee", getMarquee());
				List<Tag> ts = new ArrayList<Tag>();
				ts.add(new Tag("pm25", "101"));
				ts.add(new Tag("pm10", "112"));
				Tags tags = new Tags(ScreenType.Fix, 1, ts);
				wsSession.action(deviceId, "update_tag", tags, null);
			}
			
			if(line.equals("exit"))
				scanner.close();
		}
		// session.stop();
	}
	private static Marquee getMarquee() {
		Marquee m = new Marquee();
		m.setColor("red");
		m.setFont("arial");
		m.setPosition(Position.Bottom);
		m.setSize(1);
		m.setText("hello world");
		return m;
	}
	private static Screen getScreen() {
		List<Material> materials = new ArrayList<Material>();
		Material live = new Live("live", "http://localhost");
		Material html = new HtmlTemplate("html", "http://localhost", Period.Hour, 1, null);
		Set<String> urls = new HashSet<String>();
		urls.add("http://localhost/1.html");
		urls.add("http://localhost/2.html");
		Material picture = new Picture("picture", urls);
		Material video = new Video("video", urls);
		materials.add(live);
		materials.add(html);
		materials.add(picture);
		materials.add(video);
		FixScreen fix = new FixScreen(Layout.TwoTwo, "1:1", "1:1", materials);
		List<FloatScreen> floats = new ArrayList<FloatScreen>();
		FloatScreen f1 = new FloatScreen(picture, 10, 10, 100, 100);
		FloatScreen f2 = new FloatScreen(live, 110, 110, 100, 100);
		floats.add(f1);
		floats.add(f2);
		Screen screen = new Screen("test", fix, floats);
		return screen;
	}
}
