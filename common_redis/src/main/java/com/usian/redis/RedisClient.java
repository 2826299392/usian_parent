package com.usian.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redisTemplate封装
 */
@Component
public class RedisClient {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /********************设置redis的基本命令方法**********************/

    //指定key值的缓存失效时间
    public boolean expire(String key,long time){
        try {
            if(time>0){  //失效时间大于0为正常
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //获取key的过期时间，还有多少秒
    public long ttl(String key){
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);  //根据key值，返回key还有多长时间过期
    }

    //判断key值是否存在
    public boolean exists(String key){
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*************************String类型****************************/

    //存值
    public boolean set(String key,Object value){
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //取值
    public Object get(String key){
        if(key==null){
            return false;
        }
        return redisTemplate.opsForValue().get(key);
    }

    //删除
    public boolean del(String key){
        return redisTemplate.delete(key);
    }

    //自增   delta 要增加几(大于0)
    public long incr(String key,long delta){
        if(delta<0){
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    //自减   delta 要减少几(小于0)
    public long decr(String key,long delta){
        if(delta<0){
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /***********************Hash K-K-V***************************/

    //存值
    public boolean hset(String key,String item,Object value){
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //取值
    public Object hget(String key,String item){
        return redisTemplate.opsForHash().get(key,item);
    }

    /**
     * 删除hash表中的值
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item){
        redisTemplate.opsForHash().delete(key,item);
    }

    /****************************set*******************************/

     //根据key获取Set中的所有值
    public Set<Object> smembers(String key){
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

     // 将数据放入set缓存
    public long smembers(String key, Object...values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

     // 移除值为value的
    public long setRemove(String key, Object ...values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /******************list********************/


    //获取list缓存的内容
    public List<Object> lrange(String key, long start, long end){
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

   // 将list放入缓存 存值
    public boolean rpush(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

     //将list放入缓存
    public boolean lpush(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lrem(String key,long count,Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
