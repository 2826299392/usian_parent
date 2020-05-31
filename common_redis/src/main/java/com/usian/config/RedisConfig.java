package com.usian.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    //创建一个RedisTmplate放到容器中用于执行redis操作方法
    //RedisConnectionFactory: 工厂，将配置文件redis的集群信息，获取到这个工厂
    @Bean
    public RedisTemplate<String,Object> getRedisTemplate(RedisConnectionFactory factory){
        //1、创建redistemplate对象，将信息存放到对象中
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        //2、将工厂信息放到RedisTemplate对象中
        template.setConnectionFactory(factory);

        //3、创建key的序列化器
        StringRedisSerializer serializer = new StringRedisSerializer(); //将存放的key序列化为json串

        //4、创建value的序列化器                     //vlaue的可能有pojo类型的值，object类型，将所有存放的类型序列化为json串
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        //5、创建Jackson库提供的实现json与bean(对象)之间转换工具类
        ObjectMapper om = new ObjectMapper();
        //对转换工具类进行增强    参数ALL：对所有的属性都可以进行序列化   ANY：序列化范围，private修饰的public修饰的，都可以进行序列化
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //获取得到的json串的序列化后的对象类型，然后通过反射将json串的数据通过类型的set和get方法赋值给对象  NON_FINAL：类不能被final修饰的
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        //将增强后的转换工具添加给 value的序列化器
        jackson2JsonRedisSerializer.setObjectMapper(om);

        //将key和vlaue的序列化器添加到redistemplate模板中
        template.setKeySerializer(serializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        //设置hash序列化方式
        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        //初始实例化redisTemplate
        template.afterPropertiesSet();
        return template;
    }
}
