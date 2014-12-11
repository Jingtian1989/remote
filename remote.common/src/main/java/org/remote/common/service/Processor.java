package org.remote.common.service;


/**
 * Created by jingtian.zjt on 2014/12/10.
 */


public interface Processor {

    public void handleRequest(Object data, ResponseWriter writer);

}
