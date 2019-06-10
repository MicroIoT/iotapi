package top.microiot.api.client.stomp;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.messaging.simp.stomp.StompHeaders;

import top.microiot.api.dto.Response;

/**
 * 客户端请求发布处理抽象类，用于发起请求信息。
 *
 * @author 曹新宇
 */
public class RequestPublishSyncHandler extends RequestPublishHandler implements Future<Response> {
	private volatile Response result = null;
    private volatile boolean cancelled = false;
    private final CountDownLatch responsed;
    
	public RequestPublishSyncHandler(String deviceId, RequestPublisher publisher) {
		super(deviceId, publisher);
		this.responsed = new CountDownLatch(1);
	}

	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		Response response = (Response)payload;
		
		this.result = response;
		subscription.unsubscribe();
        responsed.countDown();
	}
	
	@Override
	public boolean cancel(boolean arg0) {
		if (isDone()) {
            return false;
        } else {
            responsed.countDown();
            subscription.unsubscribe();
            cancelled = true;
            return !isDone();
        }
	}

	@Override
	public Response get() throws InterruptedException, ExecutionException {
		responsed.await();
		subscription.unsubscribe();
		return result;
	}

	@Override
	public Response get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		boolean finished = responsed.await(timeout, unit);
		subscription.unsubscribe();
		if(finished)
			return result;
		else
			throw new TimeoutException("timeout");
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public boolean isDone() {
		return responsed.getCount() == 0;
	}
}
