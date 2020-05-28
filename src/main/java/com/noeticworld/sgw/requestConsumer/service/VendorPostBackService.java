package com.noeticworld.sgw.requestConsumer.service;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VendorPostBackService {

    Logger log = LoggerFactory.getLogger(VendorPostBackService.class.getName());


    @Autowired private ConfigurationDataManagerService configurationDataManagerService;


    public void sendVendorPostBack(Long vendorPlanId,String trackerId){
        System.out.println(trackerId);
        String url = configurationDataManagerService.getVendorPostBackConfig(vendorPlanId).replaceAll("=none","="+trackerId);
        System.out.println("url = " + url);
        HttpResponse<String> response = Unirest.post(url)
                .asString();
        log.info("CONSUMER SERVICE | VendorPostBackService CLASS | POSTBACK SENT ON URL | " + url);
        if(response.getStatus()==200) {
            log.info("CONSUMER SERVICE | VendorPostBackService CLASS | POSTBACK SENT FOR TRACKER-ID | " + trackerId);
        }

    }



}
