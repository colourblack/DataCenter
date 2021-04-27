package com.yingf.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * DataModel模块的redis常用操作
 * @author yingf Fangjunjin
 */

@Component
public class DataModelRedisUtil {

    final RedisTemplate<String, Object> objectRedisTemplate;

    @Autowired
    public DataModelRedisUtil(RedisTemplate<String, Object> objectRedisTemplate) {
        this.objectRedisTemplate = objectRedisTemplate;
    }

    /* Redis 常见操作 **/

    /**
     * 判断redis中是否存在key
     * @param key redis key
     * @return true - exist or false - not exist
     */
    public Boolean existsKey(String key) {
        return objectRedisTemplate.hasKey(key);
    }

    /**
     * 设置有效时间
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(String key, long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        Boolean ret = objectRedisTemplate.expire(key, timeout, unit);
        return ret != null && ret;
    }

    /**
     * 删除单个key
     * @param key 键
     * @return true=删除成功；false=删除失败
     */
    public boolean delKey(String key) {
        Boolean ret = objectRedisTemplate.delete(key);
        return ret != null && ret;
    }


    /**
     * 存入普通对象
     * @param key   Redis键
     * @param value 值
     */
    public void setValue(String key, Object value) {
        objectRedisTemplate.opsForValue().set(key, value, 1, TimeUnit.MINUTES);
    }

    /* 存储普通对象操作 **/

    /**
     * 存入普通对象
     * @param key     键
     * @param value   值
     * @param timeout 有效期，单位秒
     */
    public void setValueTimeout(String key, Object value, long timeout) {
        objectRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取普通对象
     * @param key 键
     * @return 对象
     */
    public Object getValue(String key) {
        return objectRedisTemplate.opsForValue().get(key);
    }

    /* 存储Hash操作 **/

    /**
     * 确定哈希hashKey是否存在
     * @param key     键
     * @param hashKey hash键
     * @return true=存在；false=不存在
     */
    public boolean hasHashKey(String key, String hashKey) {
        return objectRedisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 往Hash中存入数据
     * @param key     Redis键
     * @param hashKey Hash键
     * @param value   值
     */
    public void hashPut(String key, String hashKey, Object value) {
        objectRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 若Hash中key不存在，则Hash中存入数据
     * @param key     Redis键
     * @param hashKey Hash键
     * @param value   值
     */
    public void hashPutIfAbsent(String key, String hashKey, Object value) {
        objectRedisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    /**
     * 往Hash中存入多个数据
     * @param key    Redis键
     * @param values Hash键值对
     */
    public void hashPutAll(String key, Map<String, Object> values) {
        objectRedisTemplate.opsForHash().putAll(key, values);
    }

    /**
     * 获取Hash中的数据
     * @param key     Redis键
     * @param hashKey Hash键
     * @return Hash中的对象
     */
    public Object hashGet(String key, String hashKey) {
        return objectRedisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取Hash中的数据
     * @param key Redis键
     * @return Hash对象
     */
    public Map<Object, Object> hashGetAll(String key) {
        return objectRedisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取多个Hash中的数据
     * @param key      Redis键
     * @param hashKeys Hash键集合
     * @return Hash对象集合
     */
    public List<Object> hashMultiGet(String key, Collection<Object> hashKeys) {
        return objectRedisTemplate.opsForHash().multiGet(key, hashKeys);
    }

    /**
     * 批量删除Hash中的数据
     * @param key      Redis键
     * @param hashKeys Hash键集合
     * @return 成功删除的数目
     */
    public long hashDeleteKeys(String key, Collection<Object> hashKeys) {
        return objectRedisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 删除Hash中的数据
     * @param key     Redis键
     * @param hashKey Hash键
     * @return 成功删除的数目
     */
    public long hashDeleteKey(String key, String hashKey) {
        return objectRedisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 获取Hash所有的value
     * @param key Redis键
     * @return list of redis hash values
     */
    public List<Object> hashGetAllVal(String key) {
        return objectRedisTemplate.opsForHash().values(key);
    }

    /* 存储Set相关操作 **/

    /**
     * 往Set中存入数据
     * @param key    Redis键
     * @param values 值
     * @return 存入的个数
     */
    public long setSet(String key, Object... values) {
        Long count = objectRedisTemplate.opsForSet().add(key, values);
        return count == null ? 0 : count;
    }

    /**
     * 删除Set中的数据
     * @param key    Redis键
     * @param values 值
     * @return 移除的个数
     */
    public long setDel(String key, Object... values) {
        Long count = objectRedisTemplate.opsForSet().remove(key, values);
        return count == null ? 0 : count;
    }

    /**
     * 获取set中的所有对象
     * @param key Redis键
     * @return set集合
     */
    public Set<Object> getSetAll(String key) {
        return objectRedisTemplate.opsForSet().members(key);
    }

    /* 存储ZSet相关操作 **/

    /**
     * 往ZSet中存入数据
     *
     * @param key    Redis键
     * @param values 值
     * @return 存入的个数
     */
    public long zSetAdd(String key, Set<ZSetOperations.TypedTuple<Object>> values) {
        Long count = objectRedisTemplate.opsForZSet().add(key, values);
        return count == null ? 0 : count;
    }

    /**
     * 删除ZSet中的数据
     *
     * @param key    Redis键
     * @param values 值
     * @return 移除的个数
     */
    public long zSetDel(String key, Set<ZSetOperations.TypedTuple<Object>> values) {
        Long count = objectRedisTemplate.opsForZSet().remove(key, values);
        return count == null ? 0 : count;
    }

    /* 存储List相关操作 **/

    /**
     * 往List中存入数据
     * @param key   Redis键
     * @param value 数据
     * @return 存入的个数
     */
    public long listPush(String key, Object value) {
        Long count = objectRedisTemplate.opsForList().rightPush(key, value);
        return count == null ? 0 : count;
    }

    /**
     * 往List中存入多个数据
     * @param key    Redis键
     * @param values 多个数据
     * @return 存入的个数
     */
    public long listPushAll(String key, Collection<Object> values) {
        Long count = objectRedisTemplate.opsForList().rightPushAll(key, values);
        return count == null ? 0 : count;
    }

    /**
     * 从List中获取begin到end之间的元素
     * @param key   Redis键
     * @param start 开始位置
     * @param end   结束位置（start=0，end=-1表示获取全部元素）
     * @return List对象
     */
    public List<Object> listGet(String key, long start, long end) {
        return objectRedisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 将一个或多个值插入到列表头部
     * @param key    Redis键
     * @param values 多个数据
     * @return 存入的个数
     */
    public long listLeftPushAll(String key, Object... values){
        Long count = objectRedisTemplate.opsForList().leftPush(key, values);
        return count == null ? 0 : count;
    }

    /**
     * 将一个或多个值插入到列表尾部
     * @param key    Redis键
     * @param value 多个数据
     * @return 存入的个数
     */
    public long listRightPush(String key, Object value){
        Long count = objectRedisTemplate.opsForList().rightPush(key, value);
        return count == null ? 0 : count;
    }

    /**
     * 返回list左边第一个元素
     * @param key Redis键
     * @return 返回list左边第一个元素
     */
    public Object listLeftPop(String key) {
        return objectRedisTemplate.opsForList().leftPop(key);
    }

    /**
     * 根据索引返回redis list指定的元素
     * @param key   Redis键
     * @param index list下标, 下标从0开始
     * @return 返回指定元素
     */
    public Object listGet(String key, long index) {
        return objectRedisTemplate.opsForList().index(key, index);
    }


    /**
     * 返回list的长度大小
     * @param key Redis键
     * @return 返回list的长度大小
     */
    public Long listLength(String key) {
        Long count = objectRedisTemplate.opsForList().size(key);
        return count == null ? 0 : count;
    }

    /**
     * 转换并推送消息
     * @param channel 推送的频道名称
     * @param values  推送的消息内容
     */
    public void convertAndSend(String channel, Object values) {
        objectRedisTemplate.convertAndSend(channel, values);
    }

}
