package com.noeticworld.sgw.util;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "charging")  //, configuration = CustomFeignConfig.class
public interface BillingClient {

    @PostMapping("/charge")
    FiegnResponse charge(@RequestBody ChargeRequestProperties properties);
}
