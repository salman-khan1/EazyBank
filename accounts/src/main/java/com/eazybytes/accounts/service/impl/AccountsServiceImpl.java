package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.constants.AccountConstants;
import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.dto.AccountsMsgDto;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.exception.CustomerAlreadyExistsException;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.mapper.AccountsMapper;
import com.eazybytes.accounts.mapper.CustomerMapper;
import com.eazybytes.accounts.repository.AccountRepository;
import com.eazybytes.accounts.repository.CustomerRepository;
import com.eazybytes.accounts.service.iAccountsService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements iAccountsService {

    private static final Logger log= LoggerFactory.getLogger(AccountsServiceImpl.class);
    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;
    private final StreamBridge streamBridge;



    @Override
    public void createAccount(CustomerDto customerDto) {
        Customer customer= CustomerMapper.mapToCustomer(customerDto,new Customer());
        Optional<Customer> optionalCustomer=customerRepository
                .findByMobileNumber(customerDto.getMobileNumber());
        if(optionalCustomer.isPresent()){
            throw new CustomerAlreadyExistsException("Customer already present with this mobile number"
                    +customerDto.getMobileNumber());
        }
//        customer.setCreatedAt(LocalDateTime.now());
//        customer.setCreatedBy("Anonymous");
        Customer savedCustomer=customerRepository.save(customer);
       Accounts savedAccount= accountRepository.save(createNewAccount(savedCustomer));
    sendCommunication(savedAccount,savedCustomer);
    }

    private void sendCommunication(Accounts accounts,Customer customer){
        var accountsMsgDto=new AccountsMsgDto(accounts.getAccountNumber(),customer.getName(),
                customer.getEmail(),customer.getMobileNumber());
        log.info("Send communication request for details: {}",accountsMsgDto);
        var result=streamBridge.send("s  endCommunication-out-0",accountsMsgDto);
        log.info("Is Communication request successfully processed ?:{}",result);
    }


    private Accounts createNewAccount(Customer customer){
        Accounts newAccounts=new Accounts();
        newAccounts.setCustomerId(customer.getCustomerId());
        long randomAccNum=1000000000L+ new Random().nextInt(900000000);

        newAccounts.setAccountNumber(randomAccNum);
        newAccounts.setAccountType(AccountConstants.SAVINGS);
        newAccounts.setBranchAddress(AccountConstants.ADDRESS);
//        newAccounts.setCreatedAt(LocalDateTime.now());
//        newAccounts.setCreatedBy("Anonymous");
        return newAccounts;
    }

    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
      Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
               ()-> new ResourceNotFoundException("Customer","mobileNumber",mobileNumber)
       );
      Accounts accounts =accountRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(

               ()-> new ResourceNotFoundException("Account","customerId",customer.getCustomerId().toString())
       );

     CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer,new CustomerDto());
     customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts,new AccountsDto()));
        return customerDto;
    }

    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdated=false;
        AccountsDto accountsDto=customerDto.getAccountsDto();
        if (accountsDto!=null){
            Accounts accounts = accountRepository.findById(accountsDto.getAccountNumber()).orElseThrow(
                    ()-> new ResourceNotFoundException("Account","Account Number",accountsDto.getAccountNumber().toString())
            );
            AccountsMapper.mapToAccounts(accountsDto,accounts);
            accounts=accountRepository.save(accounts);

            Long customerId=accounts.getCustomerId();
            Customer customer=customerRepository.findById(customerId).orElseThrow(
                    ()-> new ResourceNotFoundException("Customer","CustomerId", customerId.toString())

            );
            CustomerMapper.mapToCustomer(customerDto,customer);
            customerRepository.save(customer);
            isUpdated=true;
        }
        return isUpdated;
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                ()-> new ResourceNotFoundException("Customer","mobileNumber",mobileNumber)
        );
        accountRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }

    @Override
    public boolean updateCommunicationStatus(Long accountNumber) {
        boolean isUpdated=false;
        if (accountNumber!=null) {
            Accounts accounts = accountRepository.findById(accountNumber).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "AccountNumber", accountNumber.toString())
            );
            accounts.setCommunicationSw(true);
            accountRepository.save(accounts);
            isUpdated = true;
        }
            return isUpdated;
        }
    }

