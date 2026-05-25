package com.xianbei.tea.dto;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String username;
    private String phone;
    private String newPassword;
}
