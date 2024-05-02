package com.eazybytes.message.Dto;

/**
 * @param accountNumber
 * @param name
 * @param email
 * @param mobileNumber
 */
public record AccountsMsgDto(Long accountNumber,String name, String email, String mobileNumber) {
}
