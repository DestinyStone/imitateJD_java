package com.imitatejd.maven.imitatejd_item_controller.controller;

import bean.UmsMemberReceiveAddress;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import response.Message;
import service.UmsMemberReceiveAddressService;
import service.UmsMemberService;
import utils.JwtUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/28 10:48
 * @Description:
 */
@RestController
@CrossOrigin
public class UmsController {

    @Reference
    private UmsMemberService umsMemberService;

    @Reference
    private UmsMemberReceiveAddressService umsMemberReceiveAddressService;

    @PostMapping("/addUmsMemberAddress")
    @RequiresAuthentication
    @ApiImplicitParams({ @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "token标记", required = true) })
    @ApiOperation("添加用户地址")
    public Message addUmsMemberAddress(@RequestBody @Validated UmsMemberReceiveAddress umsMemberReceiveAddress,
                                       HttpServletRequest request) {
        String token = request.getHeader("token");
        String username = JwtUtil.getUsername(token);
        String userId = umsMemberService.selectByUsernameReturnId(username);
        return umsMemberReceiveAddressService.insertAndSetUserId(umsMemberReceiveAddress, userId);
    }

    @GetMapping("/getUmsMemberAddress")
    @RequiresAuthentication
    @ApiImplicitParams({ @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "token标记", required = true) })
    @ApiOperation("获取用户地址")
    public Message getUmsMenberAddress(HttpServletRequest request) {
        String token = request.getHeader("token");
        String username = JwtUtil.getUsername(token);
        String userId = umsMemberService.selectByUsernameReturnId(username);
        return umsMemberReceiveAddressService.selectByUserId(userId);
    }

    @GetMapping(value = "/deleteUmsMemberAddressById", params = {"id!="})
    @RequiresAuthentication
    @ApiImplicitParams({ @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "token标记", required = true) })
    @ApiOperation("根据地址id删除用户地址")
    public Message deleteUmsMenberAddressById(String id, HttpServletRequest request) {
        String token = request.getHeader("token");
        String username = JwtUtil.getUsername(token);
        String userId = umsMemberService.selectByUsernameReturnId(username);
        return umsMemberReceiveAddressService.deleteByUserIdAndId(userId, id);
    }

    @GetMapping(value = "/setUmsMemberAddressDefaultById", params = {"id!="})
    @RequiresAuthentication
    @ApiImplicitParams({ @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "token标记", required = true) })
    @ApiOperation("设置用户默认地址")
    public Message setUmsMemberAddressDefaultById(String id, HttpServletRequest request) {
        String token = request.getHeader("token");
        String username = JwtUtil.getUsername(token);
        String userId = umsMemberService.selectByUsernameReturnId(username);
        return umsMemberReceiveAddressService.updateDefaultStatusByUserIdAndId(userId, id);
    }
}
