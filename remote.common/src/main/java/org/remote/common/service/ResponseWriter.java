package org.remote.common.service;

import org.remote.common.domain.BaseRequest;
import org.remote.common.domain.BaseResponse;
import org.remote.common.server.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public class ResponseWriter {

    public static final Logger LOGGER = LoggerFactory.getLogger(ResponseWriter.class);

    private Connection connection;
    private BaseRequest request;
    public ResponseWriter(Connection connection, BaseRequest request) {
        this.connection = connection;
        this.request = request;
    }

    public Connection getConnection() {
        return connection;
    }

    public BaseRequest getRequest() {
        return request;
    }

    public void write(Object data) {
        try {
            BaseResponse response = request.response(data);
            connection.write(response);
        } catch (Exception e) {
            LOGGER.error("[REMOTE] build response failed. exception:", e);
            BaseResponse response = request.error("build response failed.");
            connection.write(response);
        }
    }
}
