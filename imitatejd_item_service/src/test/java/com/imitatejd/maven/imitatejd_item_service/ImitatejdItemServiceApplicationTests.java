package com.imitatejd.maven.imitatejd_item_service;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ImitatejdItemServiceApplicationTests {
//
//    @Autowired
//    private PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;
//
//    @Autowired
//    private PmsSkuInfoMapper pmsSkuInfoMapper;
//
//    @Autowired
//    private PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;
//
//    @Autowired
//    private PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;
//
//    @Autowired
//    private PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;
//
//    @Test
//    public void test001() throws IOException {
//        List<PmsSkuInfo> pmsSkuInfoList = pmsSkuInfoMapper.selectAllByDefaultImage();
//        FileOutputStream fileOutputStream = new FileOutputStream("D://1.txt");
//        fileOutputStream.write(JSONObject.toJSONString(pmsSkuInfoList).getBytes());
//        fileOutputStream.close();
//    }
//
//    @Test
//    public void test() {
////        long beforeTime = System.currentTimeMillis();
//        PmsSkuInfo pmsSkuInfoIdQuery = PmsSkuInfo.builder().id("33821").build();
//        PmsSkuInfo pmsSkuInfoResult = pmsSkuInfoMapper.selectOne(pmsSkuInfoIdQuery);
//        PmsSkuInfo pmsSkuInfoSpuIdQuery = PmsSkuInfo.builder().spuId(pmsSkuInfoResult.getSpuId()).build();
//        long beforeTime = System.currentTimeMillis();
//        List<PmsSkuInfo> pmsSkuInfoList = pmsSkuInfoMapper.select(pmsSkuInfoSpuIdQuery);
//        long afterTime = System.currentTimeMillis();
//        String[] ids = pmsSkuInfoList.stream().map(PmsSkuInfo::getId).toArray(String[]::new);
//        List<PmsBaseAttrInfo> pmsBaseAttrInfoList = pmsBaseAttrInfoMapper.selectBySkuIds(ids, "33821");
//        System.out.println(afterTime - beforeTime);
//    }
//
//    @Test
//    public void genCatalogFile() throws IOException {
//        this.getCatalog1();
//        this.getCatalog2();
//        this.getCatalog3();
//    }
//
//    public void getCatalog1() throws IOException {
//        List<PmsBaseCatalog1> pmsBaseCatalog1List = pmsBaseCatalog1Mapper.selectAll();
//        FileOutputStream fileOutputStream = new FileOutputStream("C://Users//Public//Desktop//catalog1.json");
//        String s = JSONObject.toJSONString(pmsBaseCatalog1List);
//        fileOutputStream.write(s.getBytes());
//        fileOutputStream.close();
//    }
//
//    public void getCatalog2() throws IOException {
//        List<PmsBaseCatalog2> pmsBaseCatalog2List = pmsBaseCatalog2Mapper.selectAll();
//        FileOutputStream fileOutputStream = new FileOutputStream("C://Users//Public//Desktop//catalog2.json");
//        String s = JSONObject.toJSONString(pmsBaseCatalog2List);
//        fileOutputStream.write(s.getBytes());
//        fileOutputStream.close();
//    }
//
//    public void getCatalog3() throws IOException {
//        List<PmsBaseCatalog3> pmsBaseCatalog3List = pmsBaseCatalog3Mapper.selectAll();
//        FileOutputStream fileOutputStream = new FileOutputStream("C://Users//Public//Desktop//catalog3.json");
//        String s = JSONObject.toJSONString(pmsBaseCatalog3List);
//        fileOutputStream.write(s.getBytes());
//        fileOutputStream.close();
//    }
//
//    public void setUnrealData() {
//
//    }
}
