package com.pandatv.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandatv.conf.Conf;
import com.pandatv.service.InfoService;
import com.pandatv.service.model.InfoModel;
import com.pandatv.tools.HttpTools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author: likaiqing
 * @create: 2019-01-28 13:04
 **/
@Service
public class InfoServiceImpl implements InfoService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    HttpTools httpTools;

    @Override
    public InfoModel getSetInfo(String rid) {
        String key = new StringBuffer(Conf.detailKeyPre).append(rid).append(Conf.detailKeySuf).toString();
        String info = stringRedisTemplate.opsForValue().get(key);
        ObjectMapper mapper = new ObjectMapper();
        /*
        以下设置可以避免接口添加新字段，而bean没有，导致的报错UnrecognizedPropertyException
         */
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        InfoModel infoModel = new InfoModel();
        try {
            if (StringUtils.isNotEmpty(info)) {
                infoModel = mapper.readValue(info, InfoModel.class);
            } else {
                String httpInfo = httpTools.httpGet(new StringBuffer(Conf.detailUrlPre).append(rid).toString());
                String data = mapper.readTree(httpInfo).get("data").get(rid).toString();
                infoModel = mapper.readValue(data, InfoModel.class);
                String detailKey = new StringBuffer(Conf.detailKeyPre).append(rid).append(Conf.detailKeySuf).toString();
                stringRedisTemplate.opsForValue().set(detailKey, data);
                stringRedisTemplate.expire(detailKey, 40, TimeUnit.DAYS);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return infoModel;
    }
}
