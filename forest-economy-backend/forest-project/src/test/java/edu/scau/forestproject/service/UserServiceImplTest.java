package edu.scau.forestproject.service;

import edu.scau.forestproject.dto.LoginRequest;
import edu.scau.forestproject.dto.RegisterRequest;
import edu.scau.forestproject.exception.BusinessException;
import edu.scau.forestproject.mapper.UserMapper;
import edu.scau.forestproject.pojo.LoginInfo;
import edu.scau.forestproject.pojo.User;
import edu.scau.forestproject.security.JwtTokenService;
import edu.scau.forestproject.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private final UserMapper userMapper = Mockito.mock(UserMapper.class);
    private final JwtTokenService jwtTokenService = Mockito.mock(JwtTokenService.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserServiceImpl userService = new UserServiceImpl(userMapper, jwtTokenService, passwordEncoder);

    @Test
    void shouldLoginSuccess() {
        User dbUser = new User(1, "tom", "13800138000", passwordEncoder.encode("password123"), null);
        Mockito.when(userMapper.selectByUsernameOrPhone("13800138000")).thenReturn(dbUser);
        Mockito.when(jwtTokenService.generateToken(Mockito.anyMap())).thenReturn("token123");

        LoginRequest request = new LoginRequest();
        request.setUsernameOrPhone("13800138000");
        request.setPassword("password123");

        LoginInfo loginInfo = userService.login(request);
        Assertions.assertEquals("token123", loginInfo.getToken());
        Assertions.assertEquals("tom", loginInfo.getUsername());
    }

    @Test
    void shouldRegisterConflict() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("tom");
        request.setPhoneNumber("13800138000");
        request.setPassword("password123");

        Mockito.when(userMapper.countByUsernameOrPhone("tom", "13800138000")).thenReturn(1);

        Assertions.assertThrows(BusinessException.class, () -> userService.register(request));
    }
}
