package com.noeticworld.sgw.util;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "billing-service")  //, configuration = CustomFeignConfig.class
public interface BillingClient {

    @PostMapping("/charge")
    boolean charge(@RequestBody ChargeRequestProperties properties);
}
