package com.imitatejd.maven.imitatejd_passport_controller.controller;

import bean.UmsMember;
import io.swagger.annotations.ApiOperation;
import mq.VerityMQName;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import response.Message;
import response.type.ResponseCodeType;
import service.UmsMemberService;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/17 19:43
 * @Description:
 */
@RestController
public class RegisterController {

    @Reference
    private UmsMemberService umsMemberService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/sendPhoneVerifyCode")
    @ApiOperation("发送手机验证码")
    public Message sendPhoneVerifyCode(String phone) {
        if (StringUtils.isBlank(phone)) return new Message(ResponseCodeType.PARAM_ERROR, null, false);

        if (umsMemberService.isActiveByPhone(phone)) {
            return  new Message(ResponseCodeType.PHONE_ALREADY_ACTIVE, "手机号已被激活", true);
        }

        rabbitTemplate.convertAndSend(VerityMQName.EXCHANGE_NAME, VerityMQName.PHONE_KEY, phone);
        return new Message(ResponseCodeType.SUCCESS, "发送成功", true);
    }

    @PostMapping("/activePhone")
    @ApiOperation("激活手机号")
    public Message activePhone(String phone, String verifyCode) {
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(verifyCode)) return new Message(ResponseCodeType.PARAM_ERROR, null, false);

        // 判断验证码是否激活成功
        boolean isVerifySuccess = umsMemberService.isVerifySuccess(phone, verifyCode);

        // 激活成功 插入手机号码
        if (!isVerifySuccess) {
            return new Message(ResponseCodeType.PARAM_ERROR, "验证码错误", true);
        }
        return umsMemberService.insertPhone(phone);
    }

    @PostMapping("/activeEmail")
    @ApiOperation("激活邮箱")
    public Message activeEmail(@Validated UmsMember umsMember) {
        if (StringUtils.isBlank(umsMember.getEmail())) return new Message(ResponseCodeType.PARAM_ERROR, null, false);

        Message message = umsMemberService.insertEmail(umsMember.getEmail());
        if (ResponseCodeType.PARAM_ERROR.equals(message.getResponseCode())) {
            return new Message(ResponseCodeType.PARAM_ERROR, "该邮箱已被注册", true);
        }

        rabbitTemplate.convertAndSend(VerityMQName.EXCHANGE_NAME, VerityMQName.EMAIL_KEY, umsMember.getEmail());
        return new Message(ResponseCodeType.SUCCESS, "发送成功", true);
    }

    @GetMapping(value = "/veriryMapping", params = {"email!=", "verityCode!="})
    @ApiOperation("验证邮箱")
    public String veriryMapping(@RequestParam("email") String email, @RequestParam("verityCode") String verityCode) {
        boolean isVerity = umsMemberService.verityCode(email, verityCode);

        if (!isVerity) {
            return "验证失败, 验证码已过期";
        }

        Message message = umsMemberService.updateStautsTo1(email);
        return "验证成功";
    }

    @PostMapping(value = "/queryActiveStatus")
    @ApiOperation("查询激活状态")
    public Message queryActiveStatus(@Validated UmsMember umsMember) {
        if (StringUtils.isBlank(umsMember.getEmail())) return new Message(ResponseCodeType.PARAM_ERROR, "参数错误", false);
        return umsMemberService.selectEmailActiveStatus(umsMember.getEmail());
    }

    @GetMapping(value = "/isRepetitionUsername", params = {"username!="})
    @ApiOperation("检查用户名是否重复")
    public Message isRepetitionUsername(@RequestParam("username") String username) {
        int i = umsMemberService.selectByUsernameReturnCount(username);
        if (i == 0) {
            return new Message(ResponseCodeType.SUCCESS, "不存在用户名", true);
        } else {
            return new Message(ResponseCodeType.SUCCESS, "已存在用户名", false);
        }
    }

    @PostMapping(value="/addUser")
    @ApiOperation("添加一个用户")
    public Message addUser(@Validated @RequestBody UmsMember umsMember) {
        if (!StringUtils.isBlank(umsMember.getEmail())) {
            return umsMemberService.addUserByEmail(umsMember.getUsername(), umsMember.getPassword(), umsMember.getEmail());
        }
        if (!StringUtils.isBlank(umsMember.getPhone())) {
            return umsMemberService.addUserByPhone(umsMember.getUsername(), umsMember.getPassword(), umsMember.getPhone());
        }
        return new Message(ResponseCodeType.PARAM_ERROR, "参数错误", false);
    }
}
