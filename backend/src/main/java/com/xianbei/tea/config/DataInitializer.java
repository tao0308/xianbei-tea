package com.xianbei.tea.config;

import com.xianbei.tea.entity.SysUser;
import com.xianbei.tea.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // 如果没有店长账号，自动创建初始店长
        if (userRepository.findByUsername("admin").isEmpty()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            SysUser admin = new SysUser();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin123"));
            admin.setRealName("店长");
            admin.setPhone("13800000000");
            admin.setRole("MANAGER");
            admin.setStatus(1);
            userRepository.save(admin);
            System.out.println("[仙贝奶茶店] 初始店长账号已创建: admin / admin123");
        }
    }
}
