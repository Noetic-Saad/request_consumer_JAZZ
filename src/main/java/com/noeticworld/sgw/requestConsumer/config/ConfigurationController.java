package com.noeticworld.sgw.requestConsumer.config;

import com.noeticworld.sgw.requestConsumer.service.ConfigurationDataManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigurationController {

    @Autowired
    private ConfigurationDataManagerService configurationDataManagerService;

    @RequestMapping(value = "/refresh",method = RequestMethod.GET)
    public void refreshConfigurations(){
        configurationDataManagerService.bootstapAndCacheConfigurationData();
    }
}
