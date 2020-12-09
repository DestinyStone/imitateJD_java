package com.imitatejd.maven.imitatejd_item_controller.controller;

import bean.PmsProductInfo;
import bean.custom.OrderEnum;
import bean.custom.SelectCheckEnum;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import response.Message;
import response.type.ResponseCodeType;
import service.PmsBaseAttrInfoService;
import service.PmsProductInfoService;
import service.PmsProductRejectService;
import service.UmsMemberService;
import utils.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/13 21:59
 * @Description:
 */
@RestController
@CrossOrigin
public class SpuController {

    @Reference
    private PmsBaseAttrInfoService pmsBaseAttrInfoService;

    @Reference
    private PmsProductInfoService pmsProductInfoService;

    @Reference
    private PmsProductRejectService pmsProductRejectService;

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Reference
    private UmsMemberService umsMemberService;

    @Value("${fdfs.storage.address}")
    private String storageAddress;

    @GetMapping(value = "/getPmsProductInfo", params = {
            "selectCheck!=",
            "sort!=", "page!=", "size!="})
    @ApiOperation("根据条件查找product")
    public Message getPmsProductInfo(@RequestParam("selectCheck") SelectCheckEnum selectCheck,
                                     @RequestParam("sort") OrderEnum sort,
                                     @RequestParam("page") int page,
                                     @RequestParam("size") int size,
                                     @RequestParam(value = "name" ,required = false) String name,
                                     @RequestParam(value = "value", required = false) String value) {
        return pmsProductInfoService.selectByMultiConditions(selectCheck.toString(), sort.toString(), page, size, name, value);
    }

    @GetMapping(value = "/updatePmsProductInfoStatus", params = {"id!=", "status!="})
    @ApiOperation("更新product状态")
    public Message updatePmsProductInfoStatus(@RequestParam("id") String id,
                                              @ApiParam("0 -> 未通过, 1 -> 通过") @RequestParam("status") Integer status) {
        return pmsProductInfoService.updateStautsById(id, status);
    }

    @GetMapping(value = "/rejectPmsProductInfo", params = {"id!=", "content!="})
    @ApiOperation("提交拒绝商品通过理由")
    public Message rejectPmsProductInfo(@RequestParam("id") String id, @RequestParam("content") String content) {
        return pmsProductInfoService.rejectPmsProductInfo(id, content);
    }

    @PostMapping(value = "/addPmsProductInfo")
    @RequiresPermissions(value = {"1000", "2000"}, logical = Logical.OR)
    @ApiOperation("添加product")
    public Message addPmsProductInfo(@RequestBody PmsProductInfo pmsProductInfo, HttpServletRequest request) {
        String token = request.getHeader("token");
        String username = JwtUtil.getUsername(token);
        String id = umsMemberService.selectByUsernameReturnId(username);
        if (StringUtils.isBlank(id)) {
            return new Message(ResponseCodeType.UN_KNOW_ERROR, "未知的错误", false);
        }
        Message message = pmsProductInfoService.insert(id, pmsProductInfo);

        return message;
    }

    @PostMapping(value = "/updatePmsProductInfo")
    @ApiOperation("更新商品")
    public Message updatePmsProductInfo(@RequestBody PmsProductInfo pmsProductInfo) {
        if (StringUtils.isBlank(pmsProductInfo.getId())) new Message(ResponseCodeType.NO_LOGIN_STATUS, null, false);

        Message message = pmsProductInfoService.updateById(pmsProductInfo);
        return message;
    }

    @GetMapping(value = "/getRejectPmsProductInfo", params = {"spuId!="})
    @ApiOperation("查找商品拒绝通过理由")
    public Message getRejectPmsProductInfo(@RequestParam("spuId") String spuId) {
        return pmsProductRejectService.selectBySpuId(spuId);
    }

    @GetMapping(value = "/getPmsProductDetail", params = {"id!="})
    @ApiOperation("获取product详情")
    public Message getPmsProductDetail(@RequestParam("id") String id) {
        return pmsProductInfoService.selectDetailById(id);
    }

    @PostMapping("/spuImageUpload")
    @ApiOperation("提交product图片")
    public Message spuImageUpload(MultipartFile file) {
        System.out.println(file.getOriginalFilename());

        try {
            final InputStream inputStream = file.getInputStream();
            String fileName =  file.getOriginalFilename();
            String fileSub = fileName.substring(fileName.lastIndexOf(".") + 1);
            StorePath storePath = fastFileStorageClient.uploadFile(inputStream, file.getSize(), fileSub, null);
            String path = "http://" + this.storageAddress + "/" + storePath.getFullPath();
            return new Message(ResponseCodeType.SUCCESS, path, true);
        } catch (IOException e) {
            e.printStackTrace();
            return new Message(ResponseCodeType.NO_LOGIN_STATUS, null, false);
        }
    }

    @GetMapping("/getUserProductInfo")
    @RequiresPermissions(value = {"1000", "2000"}, logical = Logical.OR)
    @ApiOperation("获取用户提交的product")
    public Message getUserProductInfo(HttpServletRequest request) {
        String token = request.getHeader("token");
        String username = JwtUtil.getUsername(token);

        String id = umsMemberService.selectByUsernameReturnId(username);

        if (StringUtils.isBlank(id)) return new Message(ResponseCodeType.NO_LOGIN_STATUS, null, false);
        return pmsProductInfoService.getUserProductInfoByUserId(id);
    }
}
