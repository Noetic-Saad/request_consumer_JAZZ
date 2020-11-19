package com.noeticworld.sgw.requestConsumer.controllers;

import com.noeticworld.sgw.requestConsumer.service.ConfigurationDataManagerService;
import com.noeticworld.sgw.util.ZongBalanceCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetOperator {

    @Autowired
    private ConfigurationDataManagerService dataService;

    @RequestMapping(value = "/get-operator",method = RequestMethod.POST,consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Integer getOperator(long msisdn){
        return checkTelenorOrZong(msisdn);
    }

    private Integer checkTelenorOrZong(long msisdn){
        String code = null;
        ZongBalanceCheck zongBalanceCheck = new ZongBalanceCheck();
        zongBalanceCheck.logIn();
        String response = zongBalanceCheck.balanceQuery(msisdn);
        String[] arr = response.split(":RETN=");
        String[] arr2 = arr[1].split(",");
        code = arr2[0];
        if(code.equalsIgnoreCase("1002")) {
            return dataService.getTelenor();
        }else {
            return dataService.getZong();
        }
    }
}
