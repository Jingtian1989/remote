package org.remote.common.service;

import org.remote.common.annotation.TargetType;
import org.remote.common.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by jingtian.zjt on 2014/12/10.
 */
public class ProcessorRegistrar {

    private ExecutorService threadPool;
    private ConcurrentHashMap<Class<?>, Processor> processors;


    public ProcessorRegistrar() {
        threadPool = Executors.newCachedThreadPool(new NamedThreadFactory("Processor"));
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

    public Executor getExecutor() {
        return threadPool;
    }
}
