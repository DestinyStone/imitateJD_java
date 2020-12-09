package com.imitatejd.maven.imitatejd_item_controller.controller;

import bean.PmsBaseAttrInfo;
import bean.PmsBaseCatalog1;
import bean.PmsBaseCatalog2;
import bean.PmsBaseCatalog3;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import response.Message;
import response.type.ResponseCodeType;
import service.*;

import java.util.List;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/11 12:09
 * @Description:
 */
@RestController
@CrossOrigin
public class ItemController {

    @Reference
    private PmsBaseCatalog1Service pmsBaseCatalog1Service;

    @Reference
    private PmsBaseCatalog2Service pmsBaseCatalog2Service;

    @Reference
    private PmsBaseCatalog3Service pmsBaseCatalog3Service;

    @Reference
    private PmsBaseAttrInfoService pmsBaseAttrInfoService;

    @Reference
    private PmsBaseAttrValueService pmsBaseAttrValueService;

    @ApiOperation("获取一级分类")
    @GetMapping("/getPmsBaseCataLog1")
    public Message getPmsBaseCataLog1() {
        List<PmsBaseCatalog1> PmsBaseCatalog1List =  pmsBaseCatalog1Service.selectAll();
        return new Message(ResponseCodeType.SUCCESS, PmsBaseCatalog1List, true);
    }

    @ApiOperation("获取二级分类")
    @GetMapping(value = "/getPmsBaseCataLog2", params = {"catalog1Id!="})
    public Message getPmsBaseCataLog2(@RequestParam("catalog1Id") String catalog1Id) {
        List<PmsBaseCatalog2> PmsBaseCatalog2List = pmsBaseCatalog2Service.selectByCatalog1Id(catalog1Id);
        return new Message(ResponseCodeType.SUCCESS, PmsBaseCatalog2List, true);
    }

    @ApiOperation("获取三级分类")
    @GetMapping(value = "/getPmsBaseCataLog3", params = {"catalog2Id!="})
    public Message getPmsBaseCataLog3(@RequestParam("catalog2Id") String catalog2Id) {
        List<PmsBaseCatalog3> PmsBaseCatalog3List = pmsBaseCatalog3Service.selectByCatalog2Id(catalog2Id);
        return new Message(ResponseCodeType.SUCCESS, PmsBaseCatalog3List, true);
    }

    @ApiOperation("更新一级分类")
    @GetMapping(value = "/updatePmsBaseCataLog1", params = {"catalog1Id!=", "name!="})
    public Message updatePmsBaseCataLog1(@RequestParam("catalog1Id") String catalog1Id, String name) {
        pmsBaseCatalog1Service.updateById(catalog1Id, name);
        return new Message(ResponseCodeType.SUCCESS, null, true);
    }

    @ApiOperation("更新二级分类")
    @GetMapping(value = "/updatePmsBaseCataLog2", params = {"catalog2Id!=", "name!="})
    public Message updatePmsBaseCataLog2(@RequestParam("catalog2Id") String catalog2Id, @RequestParam("name") String name) {
        pmsBaseCatalog2Service.updateById(catalog2Id, name);
        return new Message(ResponseCodeType.SUCCESS, null, true);
    }

    @ApiOperation("更新三级分类")
    @GetMapping(value = "/updatePmsBaseCataLog3", params = {"catalog3Id!=", "name!="})
    public Message updatePmsBaseCataLog3(@RequestParam("catalog3Id") String catalog3Id, @RequestParam("name") String name) {
        pmsBaseCatalog3Service.updateById(catalog3Id, name);
        return new Message(ResponseCodeType.SUCCESS, null, true);
    }

    @ApiOperation("删除一级分类")
    @GetMapping(value = "/deletePmsBaseCataLog1", params = {"catalog1Id!="})
    public Message deletePmsBaseCataLog1(@RequestParam("catalog1Id") String catalog1Id){
        pmsBaseCatalog1Service.deleteById(catalog1Id);
        return new Message(ResponseCodeType.SUCCESS, null, true);
    }

    @ApiOperation("删除二级分类")
    @GetMapping(value = "/deletePmsBaseCataLog2", params = {"catalog2Id!="})
    public Message deletePmsBaseCataLog2(@RequestParam("catalog2Id") String catalog2Id){
        pmsBaseCatalog2Service.deleteById(catalog2Id);
        return new Message(ResponseCodeType.SUCCESS, null, true);
    }

    @ApiOperation("删除三级分类")
    @GetMapping(value = "/deletePmsBaseCataLog3", params = {"catalog3Id!="})
    public Message deletePmsBaseCataLog3(@RequestParam("catalog3Id") String catalog3Id){
        pmsBaseCatalog3Service.deleteById(catalog3Id);
        return new Message(ResponseCodeType.SUCCESS, null, true);
    }

    @ApiOperation("添加一级分类")
    @GetMapping(value = "/addPmsBaseCataLog1", params = {"name!="})
    public Message addPmsBaseCataLog1(@RequestParam("name") String name) {
        Message message = pmsBaseCatalog1Service.insertByNotNameRepetition(name);
        return message;
    }

    @ApiOperation("添加二级分类")
    @GetMapping(value = "/addPmsBaseCataLog2", params = {"name!=", "catalog1Id!="})
    public Message addPmsBaseCataLog2(@RequestParam("name") String name, @RequestParam("catalog1Id") String catalog1Id) {
        Message message = pmsBaseCatalog2Service.insertByNotNameRepetition(name, catalog1Id);
        return message;
    }

    @ApiOperation("添加三级分类")
    @GetMapping(value = "/addPmsBaseCataLog3", params = {"name!=", "catalog2Id!="})
    public Message addPmsBaseCataLog3(@RequestParam("name") String name, @RequestParam("catalog2Id") String catalog2Id) {
        Message message = pmsBaseCatalog3Service.insertByNotNameRepetition(name, catalog2Id);
        return message;
    }

    @ApiOperation("获取平台属性级分类")
    @GetMapping(value = "/getPmsBaseAttrInfo", params = {"catalog3Id!="})
    public Message getPmsBaseAttrInfo(@RequestParam("catalog3Id") String catalog3Id) {
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList =  pmsBaseAttrInfoService.getPmsBaseAttrInfoByCatalog3Id(catalog3Id);
        return new Message(ResponseCodeType.SUCCESS, pmsBaseAttrInfoList, true);
    }

    @ApiOperation("添加平台属性")
    @GetMapping(value = "/addPmsBaseAttrInfo", params = {"catalog3Id!=", "name!="})
    public Message addPmsBaseAttrInfo(@RequestParam("catalog3Id") String catalog3Id, @RequestParam("name") String name) {
        return pmsBaseAttrInfoService.addPmsBaseAttrInfoByCatalog3Id(name, catalog3Id);
    }

    @ApiOperation("更新平台属性")
    @GetMapping(value = "/updatePmsBaseAttrInfo", params = {"id!=", "name!="})
    public Message updatePmsBaseAttrInfo(@RequestParam("id") String id, @RequestParam("name") String name) {
        return pmsBaseAttrInfoService.updatePmsBaseAttrInfo(id, name);
    }

    @ApiOperation("删除平台属性")
    @GetMapping(value = "/deletePmsBaseAttrInfo", params = {"id!="})
    public Message deletePmsBaseAttrInfo(@RequestParam("id") String id) {
        return pmsBaseAttrInfoService.deletePmsBaseAttrInfo(id);
    }

    @ApiOperation("获取平台属性值")
    @GetMapping(value = "/getPmsBaseAttrValue", params = {"attrId!="})
    public Message getPmsBaseAttrInfoValue(@RequestParam("attrId") String attrId) {
        return pmsBaseAttrValueService.selectByAttrId(attrId);
    }

    @ApiOperation("添加平台属性值")
    @GetMapping(value = "/insertPmsBaseAttrValue", params = {"attrId!=", "valueName!="})
    public Message insertPmsBaseAttrInfoValue(@RequestParam("attrId") String attrId, @RequestParam("valueName") String valueName) {
        return pmsBaseAttrValueService.insertByNotNameRepetition(attrId, valueName);
    }

    @ApiOperation("更新平台属性值")
    @GetMapping(value = "/updatePmsBaseAttrValue", params = {"id!=", "valueName!="})
    public Message updatePmsBaseAttrValue(@RequestParam("id") String id, @RequestParam("valueName") String valueName) {
        return pmsBaseAttrValueService.updateByNotNameRepetition(id, valueName);
    }

    @ApiOperation("删除平台属性值")
    @GetMapping(value = "/deletePmsBaseAttrValue", params = {"id!="})
    public Message deletePmsBaseAttrInfoValue(@RequestParam("id") String id) {
        return pmsBaseAttrValueService.deleteById(id);
    }

}
