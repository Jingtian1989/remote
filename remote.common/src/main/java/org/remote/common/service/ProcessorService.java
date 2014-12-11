package org.remote.common.service;

import org.remote.common.annotation.TargetType;
import org.remote.common.domain.BaseRequest;
import org.remote.common.domain.BaseResponse;
import org.remote.common.server.Connection;
import org.remote.common.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by jingtian.zjt on 2014/12/10.
 */
public class ProcessorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorService.class);


    private static int corePoolSize = 8;
    private static int maximumPoolSize = 16;
    private static int queueSize = 64;
    private static final long keepAliveTime = 300L;
    private RejectedExecutionHandler handler;
    private ThreadPoolExecutor defaultPoolExecutor;
    private ConcurrentHashMap<Class<?>, Processor> processors;

    public ProcessorService() {
        this(corePoolSize, maximumPoolSize, queueSize, "ProcessorService");
    }

    public ProcessorService(int corePoolSize, int maximumPoolSize, int queueSize, String name) {
        final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(queueSize);
        handler =  new ThreadPoolExecutor.AbortPolicy();
        defaultPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
                workQueue, new NamedThreadFactory(name), handler);
        processors = new ConcurrentHashMap<Class<?>, Processor>();
    }

    public void registerProcessor(Processor processor) throws IllegalArgumentException {
        TargetType annotation = processor.getClass().getAnnotation(TargetType.class);
        if (annotation == null) {
            throw new IllegalArgumentException("processor should be annotated with" + annotation.getClass());
        }
        Class<?> clazz = annotation.value();
        if (clazz == null) {
            throw new IllegalArgumentException("annotation value should not be null.");
        }
        Processor obj = processors.get(clazz);
        if (obj != null) {
            throw new IllegalArgumentException("processor exist annotated with " + annotation);
        }
        processors.put(clazz, processor);
    }

    public Processor getProcessor(Object data) {
        return processors.get(data.getClass());
    }

    public Executor getExecutor(BaseRequest request) {
        return defaultPoolExecutor;
    }

    public void handleRequest(BaseRequest request, Connection connection) {
        Object data = null;
        try {
            data = request.parse();
        } catch (Exception e){
            LOGGER.error("[REMOTE] parse request failed. exception:", e);
            BaseResponse response = request.error("parse request failed.");
            connection.write(response);
        }

        Processor processor = getProcessor(data);
        if (processor == null) {
            LOGGER.error("[REMOTE] unsupported message type " + data.getClass() + " from " + connection.getRemoteAddress());
            BaseResponse response = request.error("unsupported message type " + data.getClass());
            connection.write(response);
            return;
        }
        try {
            processor.handleRequest(data, new ResponseWriter(connection, request));
        } catch (Exception e) {
            LOGGER.error("[REMOTE] unexpected application exception occured. exception:", e);
            BaseResponse response = request.error("unexpected application exception");
            connection.write(response);
        }

    }



}
