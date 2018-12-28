package com.leaniot.api.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.leaniot.api.device.stomp.GetSubscriber;
import com.leaniot.exception.ValueException;

public class MyGet extends GetSubscriber {

	@Override
	public Object getAttributeValue(String attribute) {
		if(attribute.equals("screen")) {
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
		}else {
			throw new ValueException("unknow attribute: " + attribute);
		}
		
	}

}
