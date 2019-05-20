package com.neusoft.mangerservice.serviceimpl;



import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.neusoft.bean.po.*;
import com.neusoft.interfaces.MangerService;
import com.neusoft.mangerservice.dao.*;
import com.neusoft.serviceutils.util.RedisUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class MangerServiceImpl implements MangerService {
    @Autowired
    BaseCatalog1Mapper baseCatalog1Dao;
    @Autowired
    BaseCatalog2Mapper baseCatalog2Dao;
    @Autowired
    BaseCatalog3Mapper baseCatalog3Dao;
    @Autowired
    BaseAttrInfoMapper baseAttrInfoDao;
    @Autowired
    BaseAttrValueMapper baseAttrValueDao;
    @Autowired
    SpuInfoMapper spuInfoDao;
    @Autowired
    BaseSaleAttrMapper baseSaleAttrDao;

    @Autowired
    SpuSaleAttrMapper spuSaleAttrDao;
    @Autowired
    SpuSaleAttrValueMapper spuSaleAttrValueDao;
    @Autowired
    SpuImageMapper spuImageDao;
    @Autowired
    SkuInfoMapper skuInfoDao;
    @Autowired
    SkuImageMapper skuImageDao;
    @Autowired
    SkuAttrValueMapper skuAttrValueDao;
    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueDao;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<BaseCatalog1> selectAllBase1() {
        return baseCatalog1Dao.selectAll();
    }

    @Override
    public List<BaseCatalog2> selectAllBase2(int base1Id) {
        BaseCatalog2 baseCatalog2=new BaseCatalog2();
        baseCatalog2.setCatalog1Id((long)base1Id);
        return baseCatalog2Dao.select(baseCatalog2);
    }

    @Override
    public List<BaseCatalog3> selectAllBase3(int base2Id) {
        BaseCatalog3 baseCatalog3=new BaseCatalog3();
        baseCatalog3.setCatalog2Id((long)base2Id);
        return baseCatalog3Dao.select(baseCatalog3);
    }

    @Override
    public List<BaseAttrInfo> selectAll(int base3Id) {
        BaseAttrInfo baseInfo=new BaseAttrInfo();
        baseInfo.setCatalog3Id((long)base3Id);
        return baseAttrInfoDao.select(baseInfo);
    }


    @Override
    public int addBaseAttrValue(BaseAttrInfo baseAttrInfo) {
        int i = baseAttrInfoDao.insertSelective(baseAttrInfo);
        List<BaseAttrValue> baseAttrValueList=baseAttrInfo.getAttrValueList();
        for (BaseAttrValue bv:baseAttrValueList) {
            bv.setAttrId(baseAttrInfo.getId());
            baseAttrValueDao.insert(bv);
        }
        return 1;
    }

    @Override
    public int deleteBaseInfo(int baseInfoId) {
        BaseAttrValue bv=new BaseAttrValue();
        bv.setAttrId((long)baseInfoId);
        baseAttrValueDao.delete(bv);
        BaseAttrInfo bi=new BaseAttrInfo();
        bi.setId((long)baseInfoId);
        int i = baseAttrInfoDao.delete(bi);
        return i;
    }

    @Override
    public List<BaseAttrValue> selectAllValue(int infoId) {
        BaseAttrValue baseAttrValue=new BaseAttrValue();
        baseAttrValue.setAttrId((long)infoId);
        return baseAttrValueDao.select(baseAttrValue);
    }

    @Override
    public List<SpuInfo> selectAllSpuInfo(int base3Id) {
        SpuInfo spuInfo=new SpuInfo();
        spuInfo.setCatalog3Id((long)base3Id);
        return spuInfoDao.select(spuInfo);
    }

    @Override
    public List<BaseSaleAttr> selectAllBaseSaleAttr() {
        return baseSaleAttrDao.selectAll();
    }

    @Override
    public int addSpuInfos(SpuInfo spuInfo) {
        //添加Spu
        spuInfoDao.insert(spuInfo);
        //获取Spu的主键
        Long spuId=spuInfo.getId();
        //保存上传的图片
        //获取SpuImage集合
        List<SpuImage> spuImageList=spuInfo.getSpuImageList();
        for (SpuImage spuImage:spuImageList){
            //设置Spu主键Id
            spuImage.setSpuId(spuId);
            spuImageDao.insert(spuImage);
        }
        //保存Spu的销售属性
        //获取SpuSaleAttr的集合
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr spuSaleAttr:spuSaleAttrList){
            //设置Spu主键Id
            spuSaleAttr.setSpuId(spuId);
            //添加SpuSaleAttr销售属性
           spuSaleAttrDao.insert(spuSaleAttr);

           //保持Spu的销售属性值
            //获取SpuSaleAttr里的销售值集合
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            for(SpuSaleAttrValue spuSaleAttrValue:spuSaleAttrValueList){
                //设置Spu的主键Id
                spuSaleAttrValue.setSpuId(spuId);
                //添加spuSaleAttrValue销售属性值
                spuSaleAttrValueDao.insert(spuSaleAttrValue);
            }
        }
        return 1;
    }

    @Override
    public int deleteSpuInfos(Long spuId) {
        SpuInfo spuInfo = spuInfoDao.selectByPrimaryKey(spuId);
        System.out.println(spuInfo.toString());
        //删除图片
        //获取SpuImage集合
        List<SpuImage> spuImageList=spuInfo.getSpuImageList();
        if (spuImageList==null||spuImageList.size()<0){

        }else {
            for (SpuImage spuImage:spuImageList){

                spuImageDao.delete(spuImage);
            }
        }
        List<SpuSaleAttr> spuSaleAttrList=spuInfo.getSpuSaleAttrList();
        if (spuSaleAttrList==null||spuSaleAttrList.size()<0){

        }else {
            for (SpuSaleAttr spuSaleAttr:spuSaleAttrList){

                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                for(SpuSaleAttrValue spuSaleAttrValue:spuSaleAttrValueList){

                    //删除spuSaleAttrValue销售属性值
                    spuSaleAttrValueDao.delete(spuSaleAttrValue);
                }
                //删除spuSaleAttr销售属性
                spuSaleAttrDao.delete(spuSaleAttr);
            }
        }

        spuInfoDao.delete(spuInfo);
        return 1;
    }

    @Override
    public int updateSpuInfos(SpuInfo spuInfo) {
        return spuInfoDao.updateByPrimaryKey(spuInfo);
    }

    @Override
    public SpuInfo selectSpus(Long spuId) {
        return spuInfoDao.selectByPrimaryKey(spuId);
    }

    @Override
    public List<SkuInfo> selectAllSkuInfo(Long spuId) {
        SkuInfo skuInfo=new SkuInfo();
        skuInfo.setSpuId(spuId);
        return skuInfoDao.select(skuInfo);
    }

    @Override
    public List<SpuImage> selcetAllSpuImage(Long spuId) {
        SpuImage spuImage=new SpuImage();
        spuImage.setSpuId(spuId);
        return spuImageDao.select(spuImage);
    }

    @Override
    public List<BaseAttrInfo> selcetAllBaseAttrValue(Long base3Id) {
        BaseAttrInfo baseAttrInfo=new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(base3Id);
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoDao.select(baseAttrInfo);
        for(BaseAttrInfo b:baseAttrInfoList){
            Long id = b.getId();
            BaseAttrValue baseAttrValue=new BaseAttrValue();
            baseAttrValue.setAttrId(id);
            List<BaseAttrValue> baseAttrValueList =  baseAttrValueDao.select(baseAttrValue);
            b.setAttrValueList(baseAttrValueList);
        }
        return baseAttrInfoList;
    }

    @Override
    public List<SpuSaleAttr> selcetAllSpuSaleAttr(Long spuId) {
        //根据spuId获取销售属性列表SpuSaleAttr
        SpuSaleAttr spuSaleAttr=new SpuSaleAttr();
        spuSaleAttr.setSpuId(spuId);
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrDao.select(spuSaleAttr);
        //遍历
        for (SpuSaleAttr saleAttr : spuSaleAttrList) {
            Long saleAttrId = saleAttr.getSaleAttrId();
            //获取销售属性值列表SpuSaleAttrValue
           SpuSaleAttrValue spuSaleAttrValue=new SpuSaleAttrValue();
           spuSaleAttrValue.setSaleAttrId(saleAttrId);
           spuSaleAttrValue.setSpuId(spuId);
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttrValueDao.select(spuSaleAttrValue);
            //设置销售属性列表SpuSaleAttr里 的销售属性值spuSaleAttrValue
            saleAttr.setSpuSaleAttrValueList(spuSaleAttrValueList);
        }
        return spuSaleAttrList;
    }

    @Override
    public int addSkuInfos(SkuInfo skuInfo) {
        skuInfoDao.insert(skuInfo);
        //获取skuId
        Long skuId = skuInfo.getId();
        //获取3级Id catalog3Id
        Long catalog3Id = skuInfo.getCatalog3Id();

        //添加平台属性
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
            skuAttrValueDao.insert(skuAttrValue);
        }

        //添加平台属性值
        List<SkuSaleAttrValue> skuSaleAttrValueList=skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValueDao.insert(skuSaleAttrValue);
        }

        //添加图片
        List<SkuImage> skuImageList=skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
            skuImageDao.insert(skuImage);
        }

        return 1;
    }

    @Override
    public int deleteSkuInfos(Long skuId) {
        return skuInfoDao.deleteByPrimaryKey(skuId);
    }

    @Override
    public SkuInfo getSkuById(Long skuId) {
        Jedis jedis = null;
        try {
             jedis = redisUtil.getJedis();
        }catch (Exception e){
            return null;
        }

        SkuInfo skuInfo=null;

        // 查询redis缓存
        String key = "sku:"+skuId+":info";
        String val = jedis.get(key);

        if("empty".equals(val)){
            System.out.println(Thread.currentThread().getName()+"发现数据库中暂时没有该商品,直接返回空对象");
            return skuInfo;
        }


        if(StringUtil.isEmpty(val)){
            System.out.println(Thread.currentThread().getName()+"发现缓存中没有数据,申请分布式锁");
            //申请缓存锁
            String OK = jedis.set("sku:" + skuId + ":lock", "1", "nx", "px", 3000);
            if("OK".equals("OK")){//拿到缓存锁
                System.out.println(Thread.currentThread().getName()+"获得分布式锁,开始访问数据");
                //查询DB
                skuInfo=getSkuByIdFormDB(skuId);

                if(skuInfo!=null){
                    System.out.println(Thread.currentThread().getName()+"通过分布式锁,查询到数据,同步缓存,然后归还锁");
                    //同步缓存
                    jedis.set(key,JSON.toJSONString(skuInfo));
                }else {
                    //通知同伴
                    System.out.println(Thread.currentThread().getName()+"通过分布式锁,没有查询到数据,通知同步在10秒之内不要访问该sku");
                    jedis.setex("sku:" + skuId + ":info",10,"empty");
                }
                //归还缓存锁
                System.out.println(Thread.currentThread().getName()+"归还分布式锁");
                jedis.del("sku:" + skuId + ":lock");

            }else {//没有拿到缓存锁
                //自旋
                try {
                    Thread.sleep(3000);//睡眠3S
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getSkuById(skuId);
            }

        }else {
            System.out.println(Thread.currentThread().getName()+"正常从缓存中取得数据,返回结构");
            //正常转换缓存数据
            skuInfo = JSON.parseObject(val,SkuInfo.class);
        }
        return skuInfo;
    }
    //缓存
    private SkuInfo getSkuByIdFormDB(Long skuId){
        SkuInfo skuInfo=new SkuInfo();
        skuInfo.setId(skuId);
        SkuInfo skuInfo1 = skuInfoDao.selectOne(skuInfo);

        SkuImage skuImage=new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImageList = skuImageDao.select(skuImage);
        skuInfo1.setSkuImageList(skuImageList);

        SkuSaleAttrValue skuSaleAttrValue=new SkuSaleAttrValue();
        skuSaleAttrValue.setSkuId(skuId);
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuSaleAttrValueDao.select(skuSaleAttrValue);
        skuInfo1.setSkuSaleAttrValueList(skuSaleAttrValueList);
        return skuInfo1;
    }
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Map<String, Long> stringLongMap) {
        return spuSaleAttrValueDao.selectSpuSaleAttrListCheckBySku(stringLongMap);
    }

    @Override
    public List<SkuInfo> getSkuSaleAttrValueListBySpu(Long spuId) {
        return spuSaleAttrValueDao.selectSkuSaleAttrValueListBySpu(spuId);
    }

    @Override
    public List<SkuInfo> getSkuListByCatalog3Id(Long catalog3Id) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setCatalog3Id(catalog3Id);
        List<SkuInfo> skuInfoList = skuInfoDao.select(skuInfo);
        for (SkuInfo info : skuInfoList) {
            Long skuid=info.getId();
            SkuAttrValue skuAttrValue=new SkuAttrValue();
            skuAttrValue.setSkuId(skuid);
            List<SkuAttrValue> skuAttrValueList = skuAttrValueDao.select(skuAttrValue);
            info.setSkuAttrValueList(skuAttrValueList);
        }
        return skuInfoList;
    }

    @Override
    public List<BaseAttrInfo> getAttrListByValueIds(Set<String> valueIds) {

        String join = StringUtils.join(valueIds, ",");
        List<BaseAttrInfo> baseAttrInfoList=baseAttrValueDao.selectAttrListByValueIds(join);
        for (BaseAttrInfo baseAttrInfo : baseAttrInfoList) {
            System.out.println(baseAttrInfo.toString());
        }
        return baseAttrInfoList;
    }

}
