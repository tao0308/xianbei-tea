# 仙贝奶茶店后台管理系统 — 实施计划

> **对于执行者：** 建议使用 subagent-driven-development 或 executing-plans 技能按任务逐步实现。步骤使用复选框 (`- [ ]`) 跟踪进度。

**目标：** 构建一个前后端分离的奶茶店后台管理系统，支持员工注册/登录、店长管理员工、饮品分类和饮品管理。

**架构：** Vue 3 前端通过 REST API 与 Spring Boot 后端通信，后端连接 MySQL 数据库。JWT Token 做身份认证，后端角色拦截器控制权限。

**技术栈：** Vue 3 + Vue Router + Pinia + Element Plus / Spring Boot 3.x + Spring Security + JWT + Spring Data JPA + MySQL 8.x / Maven

**项目根目录：** `D:\HuaweiMoveData\Users\na\Desktop\ClaudeCodeProduct`

---

## 文件结构总览

```
ClaudeCodeProduct/
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/xianbei/tea/
│       ├── XianbeiTeaApplication.java
│       ├── config/
│       │   ├── SecurityConfig.java
│       │   └── JwtAuthFilter.java
│       ├── common/
│       │   ├── Result.java
│       │   └── JwtUtil.java
│       ├── entity/
│       │   ├── SysUser.java
│       │   ├── Category.java
│       │   └── Product.java
│       ├── repository/
│       │   ├── UserRepository.java
│       │   ├── CategoryRepository.java
│       │   └── ProductRepository.java
│       ├── dto/
│       │   ├── LoginRequest.java
│       │   ├── RegisterRequest.java
│       │   └── PasswordResetRequest.java
│       ├── service/
│       │   ├── AuthService.java
│       │   ├── UserService.java
│       │   ├── CategoryService.java
│       │   └── ProductService.java
│       ├── controller/
│       │   ├── AuthController.java
│       │   ├── UserController.java
│       │   ├── CategoryController.java
│       │   └── ProductController.java
│       └── exception/
│           └── GlobalExceptionHandler.java
│   └── src/main/resources/
│       └── application.yml
└── frontend/
    ├── package.json
    ├── vite.config.js
    ├── index.html
    └── src/
        ├── main.js
        ├── App.vue
        ├── api/
        │   └── index.js
        ├── router/
        │   └── index.js
        ├── store/
        │   └── auth.js
        ├── utils/
        │   └── request.js
        ├── layout/
        │   └── MainLayout.vue
        └── views/
            ├── Login.vue
            ├── Register.vue
            ├── ForgotPassword.vue
            ├── Dashboard.vue
            ├── Users.vue
            ├── Categories.vue
            └── Products.vue
```

---

### Task 1: 初始化 Spring Boot 后端项目

**文件：**
- 创建: `backend/pom.xml`
- 创建: `backend/src/main/resources/application.yml`
- 创建: `backend/src/main/java/com/xianbei/tea/XianbeiTeaApplication.java`

- [ ] **Step 1: 创建 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    <groupId>com.xianbei</groupId>
    <artifactId>tea</artifactId>
    <version>1.0.0</version>
    <name>xianbei-tea</name>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.12.3</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: 创建 application.yml**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/xianbei_tea?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

app:
  jwt:
    secret: xianbei-tea-secret-key-2026-very-long-secret-for-jwt
    expiration: 86400000
```

- [ ] **Step 3: 创建入口类**

```java
package com.xianbei.tea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class XianbeiTeaApplication {
    public static void main(String[] args) {
        SpringApplication.run(XianbeiTeaApplication.class, args);
    }
}
```

---

### Task 2: 创建统一响应类和工具类

**文件：**
- 创建: `common/Result.java`
- 创建: `common/JwtUtil.java`

- [ ] **Step 1: 统一响应 Result 类**

```java
package com.xianbei.tea.common;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.message = "success";
        r.data = data;
        return r;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        return r;
    }

    public static <T> Result<T> unauthorized() {
        return error(401, "未登录或登录已过期");
    }

    public static <T> Result<T> forbidden() {
        return error(403, "权限不足");
    }
}
```

- [ ] **Step 2: JWT 工具类**

```java
package com.xianbei.tea.common;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey key;
    private final long expiration;

    public JwtUtil(@Value("${app.jwt.secret}") String secret,
                   @Value("${app.jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    public String generateToken(Long userId, String username, String role) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
```

---

### Task 3: 创建数据库实体类

**文件：**
- 创建: `entity/SysUser.java`
- 创建: `entity/Category.java`
- 创建: `entity/Product.java`

- [ ] **Step 1: SysUser 实体**

```java
package com.xianbei.tea.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sys_user")
public class SysUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "real_name", nullable = false)
    private String realName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String role = "STAFF";

    @Column(nullable = false)
    private Integer status = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 2: Category 实体**

```java
package com.xianbei.tea.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer sort = 0;

    @Column(nullable = false)
    private Integer status = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 3: Product 实体**

```java
package com.xianbei.tea.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private Integer status = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

---

### Task 4: 创建 Repository 层

**文件：**
- 创建: `repository/UserRepository.java`
- 创建: `repository/CategoryRepository.java`
- 创建: `repository/ProductRepository.java`

- [ ] **Step 1: UserRepository**

```java
package com.xianbei.tea.repository;

import com.xianbei.tea.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<SysUser, Long> {
    Optional<SysUser> findByUsername(String username);
    boolean existsByUsername(String username);
    long countByRole(String role);
}
```

- [ ] **Step 2: CategoryRepository**

```java
package com.xianbei.tea.repository;

import com.xianbei.tea.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrderBySortAsc();
}
```

- [ ] **Step 3: ProductRepository**

```java
package com.xianbei.tea.repository;

import com.xianbei.tea.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
    long countByCategoryId(Long categoryId);
}
```

---

### Task 5: DTO 请求类

**文件：**
- 创建: `dto/LoginRequest.java`
- 创建: `dto/RegisterRequest.java`
- 创建: `dto/PasswordResetRequest.java`

- [ ] **Step 1: LoginRequest**

```java
package com.xianbei.tea.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
```

- [ ] **Step 2: RegisterRequest**

```java
package com.xianbei.tea.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String realName;
    private String phone;
}
```

- [ ] **Step 3: PasswordResetRequest**

```java
package com.xianbei.tea.dto;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String username;
    private String phone;
    private String newPassword;
}
```

---

### Task 6: Service 层

**文件：**
- 创建: `service/AuthService.java`
- 创建: `service/UserService.java`
- 创建: `service/CategoryService.java`
- 创建: `service/ProductService.java`

- [ ] **Step 1: AuthService**

```java
package com.xianbei.tea.service;

import com.xianbei.tea.common.JwtUtil;
import com.xianbei.tea.common.Result;
import com.xianbei.tea.dto.LoginRequest;
import com.xianbei.tea.dto.PasswordResetRequest;
import com.xianbei.tea.dto.RegisterRequest;
import com.xianbei.tea.entity.SysUser;
import com.xianbei.tea.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public Result<Map<String, Object>> login(LoginRequest req) {
        var userOpt = userRepository.findByUsername(req.getUsername());
        if (userOpt.isEmpty()) {
            return Result.error(400, "用户名或密码错误");
        }
        SysUser user = userOpt.get();
        if (user.getStatus() == 0) {
            return Result.error(400, "账号已被禁用");
        }
        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            return Result.error(400, "用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return Result.success(Map.of(
            "token", token,
            "username", user.getUsername(),
            "realName", user.getRealName(),
            "role", user.getRole()
        ));
    }

    public Result<String> register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return Result.error(400, "用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(req.getUsername());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRealName(req.getRealName());
        user.setPhone(req.getPhone());
        user.setRole("STAFF");
        user.setStatus(1);
        userRepository.save(user);
        return Result.success("注册成功");
    }

    public Result<String> forgotPassword(PasswordResetRequest req) {
        var userOpt = userRepository.findByUsername(req.getUsername());
        if (userOpt.isEmpty()) {
            return Result.error(400, "用户名不存在");
        }
        SysUser user = userOpt.get();
        if (!user.getPhone().equals(req.getPhone())) {
            return Result.error(400, "手机号不匹配");
        }
        user.setPassword(encoder.encode(req.getNewPassword()));
        userRepository.save(user);
        return Result.success("密码重置成功");
    }
}
```

- [ ] **Step 2: UserService**

```java
package com.xianbei.tea.service;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.dto.RegisterRequest;
import com.xianbei.tea.entity.SysUser;
import com.xianbei.tea.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Result<List<SysUser>> list() {
        return Result.success(userRepository.findAll());
    }

    public Result<SysUser> getById(Long id) {
        return userRepository.findById(id)
                .map(Result::success)
                .orElse(Result.error(404, "用户不存在"));
    }

    public Result<String> create(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return Result.error(400, "用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(req.getUsername());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRealName(req.getRealName());
        user.setPhone(req.getPhone());
        user.setRole("STAFF");
        user.setStatus(1);
        userRepository.save(user);
        return Result.success("新增成功");
    }

    public Result<String> update(Long id, RegisterRequest req) {
        var opt = userRepository.findById(id);
        if (opt.isEmpty()) return Result.error(404, "用户不存在");
        SysUser user = opt.get();
        user.setRealName(req.getRealName());
        user.setPhone(req.getPhone());
        userRepository.save(user);
        return Result.success("更新成功");
    }

    public Result<String> updateStatus(Long id, Integer status) {
        var opt = userRepository.findById(id);
        if (opt.isEmpty()) return Result.error(404, "用户不存在");
        SysUser user = opt.get();
        user.setStatus(status);
        userRepository.save(user);
        return Result.success("状态更新成功");
    }

    public Result<String> resetPassword(Long id, String newPassword) {
        var opt = userRepository.findById(id);
        if (opt.isEmpty()) return Result.error(404, "用户不存在");
        SysUser user = opt.get();
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        return Result.success("密码重置成功");
    }

    public Result<String> delete(Long id) {
        if (!userRepository.existsById(id)) return Result.error(404, "用户不存在");
        userRepository.deleteById(id);
        return Result.success("删除成功");
    }
}
```

- [ ] **Step 3: CategoryService**

```java
package com.xianbei.tea.service;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Category;
import com.xianbei.tea.repository.CategoryRepository;
import com.xianbei.tea.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public Result<List<Category>> list() {
        return Result.success(categoryRepository.findAllByOrderBySortAsc());
    }

    public Result<String> create(Category category) {
        category.setId(null);
        categoryRepository.save(category);
        return Result.success("新增成功");
    }

    public Result<String> update(Long id, Category category) {
        var opt = categoryRepository.findById(id);
        if (opt.isEmpty()) return Result.error(404, "分类不存在");
        Category c = opt.get();
        c.setName(category.getName());
        c.setSort(category.getSort());
        c.setStatus(category.getStatus());
        categoryRepository.save(c);
        return Result.success("更新成功");
    }

    public Result<String> delete(Long id) {
        if (!categoryRepository.existsById(id)) return Result.error(404, "分类不存在");
        if (productRepository.countByCategoryId(id) > 0) {
            return Result.error(400, "该分类下还有饮品，无法删除");
        }
        categoryRepository.deleteById(id);
        return Result.success("删除成功");
    }
}
```

- [ ] **Step 4: ProductService**

```java
package com.xianbei.tea.service;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Product;
import com.xianbei.tea.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Result<List<Product>> list(Long categoryId) {
        if (categoryId != null) {
            return Result.success(productRepository.findByCategoryId(categoryId));
        }
        return Result.success(productRepository.findAll());
    }

    public Result<String> create(Product product) {
        product.setId(null);
        productRepository.save(product);
        return Result.success("新增成功");
    }

    public Result<String> update(Long id, Product product) {
        var opt = productRepository.findById(id);
        if (opt.isEmpty()) return Result.error(404, "饮品不存在");
        Product p = opt.get();
        p.setName(product.getName());
        p.setCategoryId(product.getCategoryId());
        p.setPrice(product.getPrice());
        p.setDescription(product.getDescription());
        p.setImageUrl(product.getImageUrl());
        productRepository.save(p);
        return Result.success("更新成功");
    }

    public Result<String> updateStatus(Long id, Integer status) {
        var opt = productRepository.findById(id);
        if (opt.isEmpty()) return Result.error(404, "饮品不存在");
        Product p = opt.get();
        p.setStatus(status);
        productRepository.save(p);
        return Result.success(status == 1 ? "已上架" : "已下架");
    }

    public Result<String> delete(Long id) {
        if (!productRepository.existsById(id)) return Result.error(404, "饮品不存在");
        productRepository.deleteById(id);
        return Result.success("删除成功");
    }
}
```

---

### Task 7: Security 配置与 JWT 过滤器

**文件：**
- 创建: `config/JwtAuthFilter.java`
- 创建: `config/SecurityConfig.java`
- 创建: `exception/GlobalExceptionHandler.java`

- [ ] **Step 1: JWT 认证过滤器**

```java
package com.xianbei.tea.config;

import com.xianbei.tea.common.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                Claims claims = jwtUtil.parseToken(header.substring(7));
                String role = claims.get("role", String.class);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(claims, null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role)));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}
```

- [ ] **Step 2: SecurityConfig**

```java
package com.xianbei.tea.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(c -> {
                CorsConfiguration config = new CorsConfiguration();
                config.addAllowedOriginPattern("*");
                config.addAllowedMethod("*");
                config.addAllowedHeader("*");
                config.setAllowCredentials(true);
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                c.configurationSource(source);
            })
            .csrf(c -> c.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/users/**").hasRole("MANAGER")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

- [ ] **Step 3: 全局异常处理器**

```java
package com.xianbei.tea.exception;

import com.xianbei.tea.common.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        return Result.error(500, "服务器内部错误: " + e.getMessage());
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDenied() {
        return Result.forbidden();
    }
}
```

---

### Task 8: Controller 层

**文件：**
- 创建: `controller/AuthController.java`
- 创建: `controller/UserController.java`
- 创建: `controller/CategoryController.java`
- 创建: `controller/ProductController.java`

- [ ] **Step 1: AuthController**

```java
package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.dto.LoginRequest;
import com.xianbei.tea.dto.PasswordResetRequest;
import com.xianbei.tea.dto.RegisterRequest;
import com.xianbei.tea.service.AuthService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/forgot-password")
    public Result<String> forgotPassword(@RequestBody PasswordResetRequest req) {
        return authService.forgotPassword(req);
    }

    @PostMapping("/reset-password")
    public Result<String> resetPassword(@RequestBody PasswordResetRequest req) {
        return authService.forgotPassword(req);
    }
}
```

- [ ] **Step 2: UserController**

```java
package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.dto.RegisterRequest;
import com.xianbei.tea.entity.SysUser;
import com.xianbei.tea.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Result<List<SysUser>> list() {
        return userService.list();
    }

    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public Result<String> create(@RequestBody RegisterRequest req) {
        return userService.create(req);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody RegisterRequest req) {
        return userService.update(id, req);
    }

    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        return userService.updateStatus(id, body.get("status"));
    }

    @PutMapping("/{id}/password")
    public Result<String> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return userService.resetPassword(id, body.get("password"));
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return userService.delete(id);
    }
}
```

- [ ] **Step 3: CategoryController**

```java
package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Category;
import com.xianbei.tea.service.CategoryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Result<List<Category>> list() {
        return categoryService.list();
    }

    @PostMapping
    public Result<String> create(@RequestBody Category category) {
        return categoryService.create(category);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Category category) {
        return categoryService.update(id, category);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return categoryService.delete(id);
    }
}
```

- [ ] **Step 4: ProductController**

```java
package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Product;
import com.xianbei.tea.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Result<List<Product>> list(@RequestParam(required = false) Long categoryId) {
        return productService.list(categoryId);
    }

    @PostMapping
    public Result<String> create(@RequestBody Product product) {
        return productService.create(product);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Product product) {
        return productService.update(id, product);
    }

    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        return productService.updateStatus(id, body.get("status"));
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return productService.delete(id);
    }
}
```

---

### Task 9: 初始化 Vue 3 前端项目

**文件：**
- 创建: `frontend/package.json`
- 创建: `frontend/vite.config.js`
- 创建: `frontend/index.html`

- [ ] **Step 1: package.json**

```json
{
  "name": "xianbei-tea-frontend",
  "private": true,
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.0",
    "vue-router": "^4.2.0",
    "pinia": "^2.1.0",
    "axios": "^1.6.0",
    "element-plus": "^2.5.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "vite": "^5.0.0"
  }
}
```

- [ ] **Step 2: vite.config.js**

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 3: index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>仙贝奶茶店</title>
</head>
<body>
  <div id="app"></div>
  <script type="module" src="/src/main.js"></script>
</body>
</html>
```

- [ ] **Step 4: main.js**

```javascript
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'

const app = createApp(App)
app.use(ElementPlus)
app.use(router)
app.use(createPinia())
app.mount('#app')
```

- [ ] **Step 5: App.vue**

```vue
<template>
  <router-view />
</template>
```

---

### Task 10: 前端网络请求与路由配置

**文件：**
- 创建: `frontend/src/utils/request.js`
- 创建: `frontend/src/api/index.js`
- 创建: `frontend/src/store/auth.js`
- 创建: `frontend/src/router/index.js`

- [ ] **Step 1: axios 封装 (request.js)**

```javascript
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(res)
    }
    return res
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.clear()
      window.location.href = '/login'
    }
    ElMessage.error(error.response?.data?.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
```

- [ ] **Step 2: API 接口 (api/index.js)**

```javascript
import request from '../utils/request'

export const authApi = {
  login: data => request.post('/auth/login', data),
  register: data => request.post('/auth/register', data),
  forgotPassword: data => request.post('/auth/forgot-password', data),
  resetPassword: data => request.post('/auth/reset-password', data)
}

export const userApi = {
  list: () => request.get('/users'),
  create: data => request.post('/users', data),
  update: (id, data) => request.put(`/users/${id}`, data),
  updateStatus: (id, status) => request.put(`/users/${id}/status`, { status }),
  resetPassword: (id, password) => request.put(`/users/${id}/password`, { password }),
  delete: id => request.delete(`/users/${id}`)
}

export const categoryApi = {
  list: () => request.get('/categories'),
  create: data => request.post('/categories', data),
  update: (id, data) => request.put(`/categories/${id}`, data),
  delete: id => request.delete(`/categories/${id}`)
}

export const productApi = {
  list: categoryId => request.get('/products', { params: { categoryId } }),
  create: data => request.post('/products', data),
  update: (id, data) => request.put(`/products/${id}`, data),
  updateStatus: (id, status) => request.put(`/products/${id}/status`, { status }),
  delete: id => request.delete(`/products/${id}`)
}
```

- [ ] **Step 3: auth store (store/auth.js)**

```javascript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  const realName = ref(localStorage.getItem('realName') || '')
  const role = ref(localStorage.getItem('role') || '')

  const isManager = computed(() => role.value === 'MANAGER')
  const isLoggedIn = computed(() => !!token.value)

  function setUser(data) {
    token.value = data.token
    username.value = data.username
    realName.value = data.realName
    role.value = data.role
    localStorage.setItem('token', data.token)
    localStorage.setItem('username', data.username)
    localStorage.setItem('realName', data.realName)
    localStorage.setItem('role', data.role)
  }

  function logout() {
    token.value = ''
    username.value = ''
    realName.value = ''
    role.value = ''
    localStorage.clear()
  }

  return { token, username, realName, role, isManager, isLoggedIn, setUser, logout }
})
```

- [ ] **Step 4: 路由配置 (router/index.js)**

```javascript
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue') },
  { path: '/forgot-password', name: 'ForgotPassword', component: () => import('../views/ForgotPassword.vue') },
  {
    path: '/',
    component: () => import('../layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue') },
      { path: 'users', name: 'Users', component: () => import('../views/Users.vue'), meta: { role: 'MANAGER' } },
      { path: 'categories', name: 'Categories', component: () => import('../views/Categories.vue') },
      { path: 'products', name: 'Products', component: () => import('../views/Products.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && to.path !== '/register' && to.path !== '/forgot-password' && !token) {
    return next('/login')
  }
  if (to.meta.role && localStorage.getItem('role') !== to.meta.role) {
    return next('/dashboard')
  }
  next()
})

export default router
```

---

### Task 11: 主布局组件

**文件：**
- 创建: `frontend/src/layout/MainLayout.vue`

- [ ] **Step 1: MainLayout.vue**

```vue
<template>
  <el-container style="height: 100vh">
    <el-aside width="220px" style="background: #304156">
      <div style="height: 60px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 18px; font-weight: bold; border-bottom: 1px solid rgba(255,255,255,0.1)">
        仙贝奶茶店
      </div>
      <el-menu
        :default-active="route.path"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
        style="border-right: none"
      >
        <el-menu-item index="/dashboard">
          <el-icon><data-board /></el-icon>
          <span>工作台</span>
        </el-menu-item>
        <el-menu-item index="/users" v-if="authStore.isManager">
          <el-icon><user /></el-icon>
          <span>员工管理</span>
        </el-menu-item>
        <el-menu-item index="/categories">
          <el-icon><folder /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/products">
          <el-icon><goods /></el-icon>
          <span>饮品管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background: #fff; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; justify-content: flex-end">
        <el-dropdown @command="handleCommand">
          <span style="cursor: pointer">
            {{ authStore.realName || authStore.username }}
            <el-icon><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main style="background: #f0f2f5">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { DataBoard, User, Folder, Goods, ArrowDown } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

function handleCommand(cmd) {
  if (cmd === 'logout') {
    authStore.logout()
    router.push('/login')
  }
}
</script>
```

---

### Task 12: 登录页面

**文件：**
- 创建: `frontend/src/views/Login.vue`

- [ ] **Step 1: Login.vue**

```vue
<template>
  <div style="display: flex; justify-content: center; align-items: center; height: 100vh; background: #f0f2f5">
    <el-card style="width: 420px; padding: 20px">
      <h2 style="text-align: center; margin-bottom: 30px">仙贝奶茶店 · 登录</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleLogin" :loading="loading">登录</el-button>
        </el-form-item>
      </el-form>
      <div style="text-align: center; font-size: 14px">
        <router-link to="/register" style="margin-right: 20px">注册账号</router-link>
        <router-link to="/forgot-password">忘记密码</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { authApi } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await authApi.login(form)
    authStore.setUser(res.data)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>
```

---

### Task 13: 注册页面

**文件：**
- 创建: `frontend/src/views/Register.vue`

- [ ] **Step 1: Register.vue**

```vue
<template>
  <div style="display: flex; justify-content: center; align-items: center; min-height: 100vh; background: #f0f2f5">
    <el-card style="width: 420px; padding: 20px">
      <h2 style="text-align: center; margin-bottom: 30px">注册员工账号</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item prop="realName">
          <el-input v-model="form.realName" placeholder="真实姓名" />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleRegister" :loading="loading">注册</el-button>
        </el-form-item>
      </el-form>
      <div style="text-align: center; font-size: 14px">
        已有账号？<router-link to="/login">返回登录</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: '', realName: '', phone: '', password: '', confirmPassword: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (rule, val, cb) => val === form.password ? cb() : cb(new Error('两次密码不一致')), trigger: 'blur' }
  ]
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await authApi.register(form)
    ElMessage.success('注册成功')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>
```

---

### Task 14: 忘记密码页面

**文件：**
- 创建: `frontend/src/views/ForgotPassword.vue`

- [ ] **Step 1: ForgotPassword.vue**

```vue
<template>
  <div style="display: flex; justify-content: center; align-items: center; height: 100vh; background: #f0f2f5">
    <el-card style="width: 420px; padding: 20px">
      <h2 style="text-align: center; margin-bottom: 30px">忘记密码</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="注册时绑定的手机号" />
        </el-form-item>
        <el-form-item prop="newPassword">
          <el-input v-model="form.newPassword" type="password" placeholder="新密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleReset" :loading="loading">重置密码</el-button>
        </el-form-item>
      </el-form>
      <div style="text-align: center; font-size: 14px">
        想起密码了？<router-link to="/login">返回登录</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: '', phone: '', newPassword: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }]
}

async function handleReset() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await authApi.forgotPassword(form)
    ElMessage.success('密码重置成功')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>
```

---

### Task 15: 工作台页面

**文件：**
- 创建: `frontend/src/views/Dashboard.vue`

- [ ] **Step 1: Dashboard.vue**

```vue
<template>
  <div>
    <el-card style="margin-bottom: 20px">
      <h3>欢迎回来，{{ authStore.realName || authStore.username }}</h3>
      <p style="color: #909399; margin-top: 8px">{{ authStore.isManager ? '店长' : '员工' }} · 仙贝奶茶店后台管理系统</p>
    </el-card>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <div style="text-align: center; padding: 20px">
            <div style="font-size: 36px; font-weight: bold; color: #409EFF">{{ stats.staffCount }}</div>
            <div style="color: #909399; margin-top: 8px">员工总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div style="text-align: center; padding: 20px">
            <div style="font-size: 36px; font-weight: bold; color: #67C23A">{{ stats.categoryCount }}</div>
            <div style="color: #909399; margin-top: 8px">分类数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div style="text-align: center; padding: 20px">
            <div style="font-size: 36px; font-weight: bold; color: #E6A23C">{{ stats.productCount }}</div>
            <div style="color: #909399; margin-top: 8px">饮品数量</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../store/auth'
import { userApi, categoryApi, productApi } from '../api'

const authStore = useAuthStore()
const stats = ref({ staffCount: 0, categoryCount: 0, productCount: 0 })

onMounted(async () => {
  try {
    const [userRes, cateRes, prodRes] = await Promise.all([
      userApi.list(), categoryApi.list(), productApi.list()
    ])
    stats.value = {
      staffCount: userRes.data?.length || 0,
      categoryCount: cateRes.data?.length || 0,
      productCount: prodRes.data?.length || 0
    }
  } catch (e) {
    // 员工无权限查看 userApi.list，单独请求分类和饮品
    const [cateRes, prodRes] = await Promise.all([
      categoryApi.list(), productApi.list()
    ])
    stats.value = {
      staffCount: '-',
      categoryCount: cateRes.data?.length || 0,
      productCount: prodRes.data?.length || 0
    }
  }
})
</script>
```

---

### Task 16: 员工管理页面

**文件：**
- 创建: `frontend/src/views/Users.vue`

- [ ] **Step 1: Users.vue**

```vue
<template>
  <div>
    <el-card>
      <div style="margin-bottom: 16px">
        <el-button type="primary" @click="openCreate">新增员工</el-button>
      </div>
      <el-table :data="userList" border stripe v-loading="loading">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="role" label="角色">
          <template #default="{ row }">
            <el-tag :type="row.role === 'MANAGER' ? 'danger' : 'info'">
              {{ row.role === 'MANAGER' ? '店长' : '员工' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="handleStatusChange(row)" :disabled="row.role === 'MANAGER'" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" @click="openResetPwd(row)" v-if="row.role !== 'MANAGER'">重置密码</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" v-if="row.role !== 'MANAGER'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑员工' : '新增员工'" width="500px">
      <el-form :model="dialog.form" :rules="dialog.rules" ref="dialogFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="dialog.form.username" :disabled="dialog.isEdit" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="dialog.form.realName" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="dialog.form.phone" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!dialog.isEdit">
          <el-input v-model="dialog.form.password" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveUser" :loading="dialog.saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码弹窗 -->
    <el-dialog v-model="pwdDialog.visible" title="重置密码" width="400px">
      <el-form :model="pwdDialog.form" ref="pwdFormRef">
        <el-form-item label="新密码" :rules="[{ required: true, message: '请输入新密码' }]">
          <el-input v-model="pwdDialog.form.password" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="savePassword" :loading="pwdDialog.saving">确认重置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { userApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const userList = ref([])
const dialogFormRef = ref(null)
const pwdFormRef = ref(null)

const dialog = reactive({
  visible: false, isEdit: false, saving: false,
  form: { username: '', realName: '', phone: '', password: '' },
  rules: {
    username: [{ required: true, message: '请输入用户名' }],
    realName: [{ required: true, message: '请输入姓名' }],
    phone: [{ required: true, message: '请输入手机号' }]
  }
})

const pwdDialog = reactive({
  visible: false, saving: false, currentId: null,
  form: { password: '' }
})

async function fetchList() {
  loading.value = true
  try {
    const res = await userApi.list()
    userList.value = res.data
  } finally { loading.value = false }
}

function openCreate() {
  dialog.isEdit = false
  dialog.form = { username: '', realName: '', phone: '', password: '' }
  dialog.visible = true
}

function openEdit(row) {
  dialog.isEdit = true
  dialog.form = { username: row.username, realName: row.realName, phone: row.phone, password: '' }
  dialog._editId = row.id
  dialog.visible = true
}

async function saveUser() {
  const valid = await dialogFormRef.value.validate().catch(() => false)
  if (!valid) return
  dialog.saving = true
  try {
    if (dialog.isEdit) {
      await userApi.update(dialog._editId, dialog.form)
      ElMessage.success('更新成功')
    } else {
      await userApi.create(dialog.form)
      ElMessage.success('新增成功')
    }
    dialog.visible = false
    fetchList()
  } finally { dialog.saving = false }
}

async function handleStatusChange(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await userApi.updateStatus(row.id, newStatus)
  row.status = newStatus
  ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
}

function openResetPwd(row) {
  pwdDialog.currentId = row.id
  pwdDialog.form.password = ''
  pwdDialog.visible = true
}

async function savePassword() {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return
  pwdDialog.saving = true
  try {
    await userApi.resetPassword(pwdDialog.currentId, pwdDialog.form.password)
    ElMessage.success('密码已重置')
    pwdDialog.visible = false
  } finally { pwdDialog.saving = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除员工「${row.realName}」吗？`, '提示')
  await userApi.delete(row.id)
  ElMessage.success('已删除')
  fetchList()
}

onMounted(fetchList)
</script>
```

---

### Task 17: 分类管理页面

**文件：**
- 创建: `frontend/src/views/Categories.vue`

- [ ] **Step 1: Categories.vue**

```vue
<template>
  <div>
    <el-card>
      <div style="margin-bottom: 16px">
        <el-button type="primary" @click="openCreate">新增分类</el-button>
      </div>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '显示' : '隐藏' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑分类' : '新增分类'" width="400px">
      <el-form :model="dialog.form" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name" :rules="[{ required: true, message: '请输入分类名称' }]">
          <el-input v-model="dialog.form.name" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="dialog.form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="dialog.form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="dialog.saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { categoryApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref([])
const formRef = ref(null)
const dialog = reactive({
  visible: false, isEdit: false, saving: false,
  form: { name: '', sort: 0, status: 1 },
  _editId: null
})

async function fetchList() {
  loading.value = true
  try {
    const res = await categoryApi.list()
    list.value = res.data
  } finally { loading.value = false }
}

function openCreate() {
  dialog.isEdit = false
  dialog.form = { name: '', sort: 0, status: 1 }
  dialog.visible = true
}

function openEdit(row) {
  dialog.isEdit = true
  dialog._editId = row.id
  dialog.form = { name: row.name, sort: row.sort, status: row.status }
  dialog.visible = true
}

async function save() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  dialog.saving = true
  try {
    if (dialog.isEdit) {
      await categoryApi.update(dialog._editId, dialog.form)
    } else {
      await categoryApi.create(dialog.form)
    }
    ElMessage.success('保存成功')
    dialog.visible = false
    fetchList()
  } finally { dialog.saving = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除分类「${row.name}」吗？`, '提示')
  await categoryApi.delete(row.id)
  ElMessage.success('已删除')
  fetchList()
}

onMounted(fetchList)
</script>
```

---

### Task 18: 饮品管理页面

**文件：**
- 创建: `frontend/src/views/Products.vue`

- [ ] **Step 1: Products.vue**

```vue
<template>
  <div>
    <el-card>
      <div style="margin-bottom: 16px; display: flex; gap: 12px">
        <el-select v-model="filterCategoryId" placeholder="按分类筛选" clearable @change="fetchList" style="width: 200px">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-button type="primary" @click="openCreate">新增饮品</el-button>
      </div>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="name" label="饮品名称" />
        <el-table-column label="分类" width="120">
          <template #default="{ row }">{{ getCategoryName(row.categoryId) }}</template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑饮品' : '新增饮品'" width="500px">
      <el-form :model="dialog.form" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name" :rules="[{ required: true, message: '请输入饮品名称' }]">
          <el-input v-model="dialog.form.name" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId" :rules="[{ required: true, message: '请选择分类' }]">
          <el-select v-model="dialog.form.categoryId" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price" :rules="[{ required: true, message: '请输入价格' }]">
          <el-input-number v-model="dialog.form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="dialog.form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="图片" prop="imageUrl">
          <el-input v-model="dialog.form.imageUrl" placeholder="图片URL地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="dialog.saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { productApi, categoryApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref([])
const categories = ref([])
const filterCategoryId = ref(null)
const formRef = ref(null)
const dialog = reactive({
  visible: false, isEdit: false, saving: false,
  form: { name: '', categoryId: null, price: 0, description: '', imageUrl: '' },
  _editId: null
})

function getCategoryName(id) {
  return categories.value.find(c => c.id === id)?.name || '-'
}

async function fetchList() {
  loading.value = true
  try {
    const res = await productApi.list(filterCategoryId.value)
    list.value = res.data
  } finally { loading.value = false }
}

async function fetchCategories() {
  const res = await categoryApi.list()
  categories.value = res.data
}

function openCreate() {
  dialog.isEdit = false
  dialog.form = { name: '', categoryId: null, price: 0, description: '', imageUrl: '' }
  dialog.visible = true
}

function openEdit(row) {
  dialog.isEdit = true
  dialog._editId = row.id
  dialog.form = { name: row.name, categoryId: row.categoryId, price: row.price, description: row.description || '', imageUrl: row.imageUrl || '' }
  dialog.visible = true
}

async function save() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  dialog.saving = true
  try {
    if (dialog.isEdit) {
      await productApi.update(dialog._editId, dialog.form)
    } else {
      await productApi.create(dialog.form)
    }
    ElMessage.success('保存成功')
    dialog.visible = false
    fetchList()
  } finally { dialog.saving = false }
}

async function handleStatusChange(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await productApi.updateStatus(row.id, newStatus)
  row.status = newStatus
  ElMessage.success(newStatus === 1 ? '已上架' : '已下架')
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除饮品「${row.name}」吗？`, '提示')
  await productApi.delete(row.id)
  ElMessage.success('已删除')
  fetchList()
}

onMounted(() => { fetchCategories(); fetchList() })
</script>
```

---

## 计划自检

- ✅ **Spec 覆盖** — 所有 API 接口、前端页面、路由、权限控制均已覆盖
- ✅ **无占位符** — 每段代码都是完整的可执行代码
- ✅ **类型一致** — 所有接口路径、字段名、方法签名在前后端之间保持一致
- ✅ **数据库表结构** — 三张表（sys_user, category, product）完全匹配设计文档
- ✅ **权限控制** — 后端 SecurityConfig 控制接口权限，前端路由守卫控制页面访问
