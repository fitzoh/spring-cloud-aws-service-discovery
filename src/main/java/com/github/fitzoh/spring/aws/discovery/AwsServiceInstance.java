package com.github.fitzoh.spring.aws.discovery;

import com.amazonaws.services.servicediscovery.model.Instance;
import org.springframework.cloud.client.ServiceInstance;

import java.net.URI;
import java.util.Map;

public class AwsServiceInstance implements ServiceInstance {

    private final String serviceId;
    private final Instance instance;

    public AwsServiceInstance(String serviceId, Instance instance) {
        this.serviceId = serviceId;
        this.instance = instance;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public String getHost() {
        //TODO alternate host attributes
        return instance.getAttributes().get("AWS_INSTANCE_IPV4");
    }

    @Override
    public int getPort() {
        //TODO are there other possible values?
        String port = instance.getAttributes().get("AWS_INSTANCE_PORT");
        //TODO error handling?
        return Integer.parseInt(port);
    }

    @Override
    public boolean isSecure() {
        return getPort() == 443;
    }

    @Override
    public URI getUri() {
        String scheme = isSecure() ? "https" : "http";
        return URI.create(String.format("%s:%s/%s", scheme, getHost(), getPort()));
    }

    @Override
    public Map<String, String> getMetadata() {
        return instance.getAttributes();
    }
}
