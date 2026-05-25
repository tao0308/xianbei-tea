package com.xianbei.tea.dto;

import lombok.Data;

@Data
public class CustomerRegisterRequest {
    private String username;
    private String password;
    private String realName;
    private String phone;
}
