package com.github.fitzoh.spring.aws.discovery;

import com.amazonaws.services.servicediscovery.AWSServiceDiscovery;
import com.amazonaws.services.servicediscovery.model.*;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;
import java.util.stream.Collectors;

public class AwsDiscoveryClient implements DiscoveryClient {

    private final AWSServiceDiscovery aws;

    public AwsDiscoveryClient(AWSServiceDiscovery aws) {
        this.aws = aws;
    }

    @Override
    public String description() {
        return "AWS Cloud Map Discovery Client";
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        ListInstancesRequest listInstancesRequest = new ListInstancesRequest()
                .withServiceId(serviceId);
        //TODO pagination
        //TODO parallel requests?
        //TODO filter on health?
        return aws.listInstances(listInstancesRequest)
                .getInstances()
                .stream()
                .map(summary -> getInstance(serviceId, summary.getId()))
                .collect(Collectors.toList());

    }

    private AwsServiceInstance getInstance(String service, String id) {
        Instance instance = aws.getInstance(new GetInstanceRequest().withInstanceId(id)).getInstance();
        return new AwsServiceInstance(service, instance);
    }

    @Override
    public List<String> getServices() {
        //TODO pagination
        return aws.listServices(new ListServicesRequest()).getServices().stream().map(ServiceSummary::getId).collect(Collectors.toList());
    }
}
