package com.noeticworld.sgw.util;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("mtgateway")
public interface MtClient {

    @PostMapping("/mt")
    void sendMt(@RequestBody MtProperties mtProperties);

}
