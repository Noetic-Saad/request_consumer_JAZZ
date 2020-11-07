package com.noeticworld.sgw.requestConsumer.controllers;

import com.noeticworld.sgw.requestConsumer.entities.LoginEntity;
import com.noeticworld.sgw.requestConsumer.repository.LoginRepository;
import com.noeticworld.sgw.requestConsumer.service.ConfigurationDataManagerService;
import com.noeticworld.sgw.util.ZongBalanceCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaveFirstRequest {

    @Autowired
    private LoginRepository loginRepository;

    @RequestMapping(value = "/savelogin",method = RequestMethod.POST,consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void getOperator(@RequestBody LoginEntity loginEntity){
        System.out.println("Login USer "+loginEntity.getMsisdn());
        loginRepository.save(loginEntity);

    }


}
