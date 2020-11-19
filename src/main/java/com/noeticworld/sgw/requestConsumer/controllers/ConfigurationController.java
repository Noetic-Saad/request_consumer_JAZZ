package com.noeticworld.sgw.requestConsumer.controllers;

import com.noeticworld.sgw.requestConsumer.service.ConfigurationDataManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigurationController {

    Logger log = LoggerFactory.getLogger(ConfigurationController.class.getName());

    @Autowired
    private ConfigurationDataManagerService configurationDataManagerService;

    @RequestMapping(value = "/refresh",method = RequestMethod.GET)
    public void refreshConfigurations(){
        configurationDataManagerService.bootstapAndCacheConfigurationData();
        log.info("SUBSCRIBER SERVICE |  CONFIGURATIONCONTROLLER CLASS | CONFIGURATIONS REFRESHED");
    }
}
