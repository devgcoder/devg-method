package com.github.devgcoder.devgmethod.utils.async;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 执行多个SQL查询,加入线层池,提升效率
// @author devg @Date 2019/8/4 6:50
// 用法demo:
// AsyncUtil asyncUitl = AsyncUtil.newInstance();
// SockerUtil sockerUtil = new SockerUtil();
// asyncUitl.addExecutePool("key1", sockerUtil, "checkHostLogin", new Object[]{host, port}, new Class[]{String.class, Integer.class});
// asyncUitl.addExecutePool("key2", sockerUtil, "checkHostLogin", new Object[]{host, port}, new Class[]{String.class, Integer.class});
// asyncUitl.shutdown();
// Boolean access = asyncUitl.getResult(key);
// log.info("key:" + key + ",access:" + access);

public class AsyncUtil {

	private final static Logger log = LoggerFactory.getLogger(AsyncUtil.class);

	private final static Integer blockingQueueNum = 50;

	/**
	 * 线程池
	 */
	private ExecutorService executorService;

	/**
	 * 任务列表
	 */
	private Map<String, Future> futureTaskMap;


	public AsyncUtil() {
		//    this.executorService = Executors.newFixedThreadPool(10);
		this.executorService = new ThreadPoolExecutor(10, 100, 0, TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(blockingQueueNum), new ThreadPoolExecutor.DiscardPolicy());
		this.futureTaskMap = new HashMap<>();
	}

	public AsyncUtil(int threadPoolNum) {
		this.executorService = new ThreadPoolExecutor(threadPoolNum, threadPoolNum, 0, TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(blockingQueueNum), new ThreadPoolExecutor.DiscardPolicy());
		this.futureTaskMap = new HashMap<>();
	}

	public AsyncUtil(ExecutorService executorService) {
		this.executorService = executorService;
		this.futureTaskMap = new HashMap<>();
	}

	public static AsyncUtil newInstance() {
		return newInstance(4);
	}

	public static AsyncUtil newInstance(int threadPoolNum) {
		return new AsyncUtil(threadPoolNum);
	}

	/**
	 * 执行多个SQL查询,加入线层池,提升效率
	 *
	 * @param serviceObj 服务实体
	 * @param methodName 执行方法
	 * @param params 方法参数
	 * @param parameterTypes 方法参数类型
	 */
	public void addExecutePool(String key, Object serviceObj, String methodName, Object[] params, Class<?>... parameterTypes) {
		FutureTask<FutureTaskResult<Object>> ft1 = new FutureTask<>(new Callable<FutureTaskResult<Object>>() {
			@Override
			public FutureTaskResult<Object> call() throws Exception {
				Method md = serviceObj.getClass().getMethod(methodName, parameterTypes);
				Object obj = md.invoke(serviceObj, params);
				FutureTaskResult<Object> futureTaskResult = new FutureTaskResult();
				futureTaskResult.setData(obj);
				futureTaskResult.setKey(methodName);
				return futureTaskResult;
			}
		});
		futureTaskMap.put(key, ft1);
		executorService.execute(ft1);
	}

	/**
	 * @param key
	 * @param <T>
	 * @return
	 */
	public <T> T getResult(String key) {
		try {
			Future future = futureTaskMap.get(key);
			Object obj = future.get();
			FutureTaskResult<T> result = (FutureTaskResult) obj;
			return result.getData();
		} catch (InterruptedException ex) {
			log.error("async task InterruptedException", ex);
		} catch (ExecutionException ex) {
			log.error("async task ExecutionException", ex);
		}
		return null;
	}

	/**
	 * 关闭线程池
	 */
	public void shutdown() {
		executorService.shutdown();
	}

}
