package com.eazybytes.accounts.functions;

import com.eazybytes.accounts.service.impl.AccountsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class AccountsFunction {
    private static final Logger log= LoggerFactory.getLogger(AccountsFunction.class);


    @Bean
    public Consumer<Long> updateCommunication(AccountsServiceImpl accountsService){
        return accountNumber->{
          log.info("Updating Communication Status for the account Number: "+accountNumber.toString());
          accountsService.updateCommunicationStatus(accountNumber);

        };
    }

}
