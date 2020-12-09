package com.imitatejd.maven.imitatejd_passport_service.service.impl;

import bean.UmsMember;
import bean.UmsPermission;
import com.imitatejd.maven.imitatejd_passport_service.mapper.UmsMemberMapper;
import com.imitatejd.maven.imitatejd_passport_service.mapper.UmsPermissonMapper;
import lombok.SneakyThrows;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import response.Message;
import response.type.ResponseCodeType;
import service.UmsMemberService;
import utils.RedisKeyRule;

import java.util.Date;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/17 21:28
 * @Description:
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UmsMemberMapper umsMemberMapper;

    @Autowired
    private UmsPermissonMapper umsPermissonMapper;

    @Override
    public boolean verityCode(String email, String verityCode) {
        try {
            final String result = stringRedisTemplate.opsForValue().get(RedisKeyRule.verityKeyRule(email, null));
            if (StringUtils.isBlank(result) || !result.equals(verityCode)) {
                return false;
            }
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Message insertEmail(String email) {
        UmsMember umsMemberQuery = UmsMember.builder().email(email).build();
        UmsMember umsMember = umsMemberMapper.selectOne(umsMemberQuery);
        if (umsMember == null) {
            umsMemberQuery.setStatus(0);
            umsMemberQuery.setCreateTime(new Date());
            umsMemberQuery.setPermissionId("4");
            umsMemberMapper.insertSelective(umsMemberQuery);
            return new Message(ResponseCodeType.SUCCESS, umsMemberQuery, true);
        }

        if (umsMember.getStatus() == 1 && StringUtils.isBlank(umsMember.getUsername())) {
            umsMember.setStatus(0);
            umsMember.setCreateTime(new Date());
            umsMemberMapper.updateByPrimaryKey(umsMember);
            return new Message(ResponseCodeType.SUCCESS, umsMemberQuery, true);
        }

        if (umsMember.getStatus() == 1) {
            return new Message(ResponseCodeType.PARAM_ERROR, null, false);
        }
        return new Message(ResponseCodeType.SUCCESS, umsMember, true);
    }

    @Override
    public Message updateStautsTo1(String email) {
        try {
            UmsMember umsMemberQuery = UmsMember.builder().email(email).build();
            UmsMember umsMemberResult = umsMemberMapper.selectOne(umsMemberQuery);
            umsMemberResult.setStatus(1);
            umsMemberMapper.updateByPrimaryKey(umsMemberResult);
            return new Message(ResponseCodeType.SUCCESS, null, true);
        }catch (Exception e) {
            e.printStackTrace();
            return new Message(ResponseCodeType.UN_KNOW_ERROR, null, false);
        }

    }

    @Override
    public Message selectEmailActiveStatus(String email) {
        UmsMember umsMemberQuery = UmsMember.builder().email(email).build();
        UmsMember umsMember = umsMemberMapper.selectOne(umsMemberQuery);
        if (umsMember == null || umsMember.getStatus() == 0)
            return new Message(ResponseCodeType.SUCCESS, "未激活", true);
        if (umsMember.getStatus() == 1) {
            if (!StringUtils.isBlank(umsMember.getUsername())) {
                return new Message(ResponseCodeType.SUCCESS, "已存在用户", true);
            }
            return new Message(ResponseCodeType.SUCCESS, "激活成功", true);
        }
        return new Message(ResponseCodeType.NO_LOGIN_STATUS, "未知的错误", false);
    }

    @Override
    public int selectByUsernameReturnCount(String username) {
        UmsMember umsMemberQuery = UmsMember.builder().username(username).build();
        return umsMemberMapper.selectCount(umsMemberQuery);
    }

    @Override
    public Message addUserByEmail(String username, String password, String email) {
        // 检查是否存在用户名
        int i = this.selectByUsernameReturnCount(username);
        if (i != 0)
            return new Message(ResponseCodeType.PARAM_ERROR, "已存在的用户名", false);

        UmsMember umsMemberQuery = UmsMember.builder().email(email).build();
        UmsMember umsMemberResult = umsMemberMapper.selectOne(umsMemberQuery);

        // 检查邮箱是否已存在用户占用
        if (!StringUtils.isBlank(umsMemberResult.getUsername())) {
            return new Message(ResponseCodeType.PARAM_ERROR, "该邮箱已被绑定", false);
        }

        // 检查邮箱是否未被激活
        if (umsMemberResult.getStatus() != 1)
            return  new Message(ResponseCodeType.PARAM_ERROR, "该邮箱未激活", false);

        umsMemberResult.setUsername(username);
        umsMemberResult.setNickname(username);
        umsMemberResult.setPassword(password);
        umsMemberResult.setPermissionId("4");
        umsMemberMapper.updateByPrimaryKey(umsMemberResult);

        return new Message(ResponseCodeType.SUCCESS, "注册成功", true);
    }

    @Override
    public UmsMember selectByUsername(String username) {
        UmsMember umsMemberQuery = UmsMember.builder().username(username).build();
        UmsMember umsMemberResult = umsMemberMapper.selectOne(umsMemberQuery);
        return umsMemberResult;
    }

    @Override
    public String selectMemberPermission(String permissionId) {
        UmsPermission umsPermissionQuery = UmsPermission.builder().id(permissionId).build();
        UmsPermission umsPermissionResult = umsPermissonMapper.selectOne(umsPermissionQuery);
        return umsPermissionResult.getCode();
    }

    @Override
    public String selectByUsernameReturnId(String username) {
        UmsMember umsMemberQuery = UmsMember.builder().username(username).build();
        UmsMember umsMemberResult = umsMemberMapper.selectOne(umsMemberQuery);
        if (umsMemberResult == null) {
            return null;
        }
        return umsMemberResult.getId();
    }

    @Override
    public boolean isActiveByPhone(String phone) {
        UmsMember umsMemberQuery = UmsMember.builder().phone(phone).status(1).build();
        UmsMember umsMember = umsMemberMapper.selectOne(umsMemberQuery);
        if (umsMember != null && !StringUtils.isBlank(umsMember.getUsername()))
            return true;
        return false;
    }

    @SneakyThrows
    @Override
    public boolean isVerifySuccess(String phone, String verifyCode) {
        String code = stringRedisTemplate.opsForValue().get(RedisKeyRule.verityKeyRule(null, phone));
        return verifyCode.equals(code) ? true : false;
    }

    @Override
    public Message insertPhone(String phone) {
        UmsMember umsMemberQuery = UmsMember.builder()
                .phone(phone)
                .status(1).build();
        UmsMember umsMember = umsMemberMapper.selectOne(umsMemberQuery);
        if (umsMember == null) {
            umsMemberQuery.setCreateTime(new Date());
            umsMemberMapper.insertSelective(umsMemberQuery);
        }

        return new Message(ResponseCodeType.SUCCESS, null, true);
    }

    @Override
    public Message addUserByPhone(String username, String password, String phone) {
        // 检查是否存在用户名
        int i = this.selectByUsernameReturnCount(username);
        if (i != 0)
            return new Message(ResponseCodeType.PARAM_ERROR, "已存在的用户名", false);

        UmsMember umsMemberQuery = UmsMember.builder().phone(phone).build();
        UmsMember umsMemberResult = umsMemberMapper.selectOne(umsMemberQuery);

        // 检查邮箱是否已存在用户占用
        if (!StringUtils.isBlank(umsMemberResult.getUsername())) {
            return new Message(ResponseCodeType.PARAM_ERROR, "该邮箱已被绑定", false);
        }

        // 检查邮箱是否未被激活
        if (umsMemberResult.getStatus() != 1)
            return  new Message(ResponseCodeType.PARAM_ERROR, "该手机号未激活", false);

        umsMemberResult.setUsername(username);
        umsMemberResult.setNickname(username);
        umsMemberResult.setPassword(password);
        umsMemberResult.setPermissionId("4");
        umsMemberMapper.updateByPrimaryKey(umsMemberResult);

        return new Message(ResponseCodeType.SUCCESS, "注册成功", true);
    }
}
