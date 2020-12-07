package com.noeticworld.sgw.requestConsumer.service;

import com.noeticworld.sgw.requestConsumer.repository.OtpRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

public class OtpService {
    @Autowired
    OtpRecordRepository otpRecordRepository;
    @PostMapping("/fetchOtp")
    public Long fetchotpFromDatabase(long msisdn){
        long code;
        code=otpRecordRepository.findTopByMsisdn(msisdn);
        System.out.println("Fetch OTP Record For Msisdn : "+code);
        return code;
    }
}
