package edu.scau.forestproject.service.impl;

import edu.scau.forestproject.dto.LoginRequest;
import edu.scau.forestproject.dto.RegisterRequest;
import edu.scau.forestproject.dto.UserBriefResponse;
import edu.scau.forestproject.exception.BusinessException;
import edu.scau.forestproject.exception.ErrorCode;
import edu.scau.forestproject.mapper.UserMapper;
import edu.scau.forestproject.pojo.LoginInfo;
import edu.scau.forestproject.pojo.User;
import edu.scau.forestproject.security.JwtTokenService;
import edu.scau.forestproject.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final ConcurrentHashMap<String, ReentrantLock> registerLocks = new ConcurrentHashMap<>();

    public UserServiceImpl(UserMapper userMapper, JwtTokenService jwtTokenService, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.jwtTokenService = jwtTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginInfo login(LoginRequest request) {
        User user = userMapper.selectByUsernameOrPhone(request.getUsernameOrPhone());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "username/phone or password is incorrect");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        String jwt = jwtTokenService.generateToken(claims);
        log.info("user login success, userId={}", user.getId());
        return new LoginInfo(user.getId(), user.getUsername(), jwt);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer id) {
        if (userMapper.selectById(id) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "user not found");
        }
        userMapper.deleteByID(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequest request) {
        String lockKey = request.getUsername() + ":" + request.getPhoneNumber();
        ReentrantLock lock = registerLocks.computeIfAbsent(lockKey, key -> new ReentrantLock());
        lock.lock();
        try {
            if (userMapper.countByUsernameOrPhone(request.getUsername(), request.getPhoneNumber()) > 0) {
                throw new BusinessException(ErrorCode.CONFLICT, "username or phone already exists");
            }
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userMapper.addUser(user);
        } finally {
            lock.unlock();
            registerLocks.remove(lockKey, lock);
        }
    }

    @Override
    @Cacheable(cacheNames = "userBrief", key = "#id")
    public UserBriefResponse getUserBrief(Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "user not found");
        }
        return new UserBriefResponse(user.getId(), user.getUsername(), user.getPhoneNumber());
    }
}
