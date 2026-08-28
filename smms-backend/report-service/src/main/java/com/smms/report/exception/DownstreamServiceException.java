package com.smms.report.exception;

public class DownstreamServiceException extends RuntimeException {

    private final String serviceName;
    private final int downstreamStatus;

    public DownstreamServiceException(String serviceName, int downstreamStatus) {
        super("Unable to retrieve reporting data from " + serviceName);
        this.serviceName = serviceName;
        this.downstreamStatus = downstreamStatus;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getDownstreamStatus() {
        return downstreamStatus;
    }
}
