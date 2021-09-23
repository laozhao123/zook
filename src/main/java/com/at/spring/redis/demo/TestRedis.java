package com.at.spring.redis.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestRedis {

    @Autowired
    private RedisTemplate redisTemplate; // 默认的 会将key value 使用java序列化

    @Autowired
    @Qualifier("ooxx")
    StringRedisTemplate stringRedisTemplate;




    @Autowired
    ObjectMapper objectMapper;

    public void getT(){
//        stringRedisTemplate.opsForValue().set("hello01","chaina");
//        System.out.println(stringRedisTemplate.opsForValue().get("hello01"));

//        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
//        connection.set("hello02".getBytes(),"mashibing".getBytes());
//
//        System.out.println(new String(connection.get("hello02".getBytes())));
//
//
//        HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();
//        hash.put("sean","name","zhouzhilei");
//        hash.put("sean","age","22");
//
//        System.out.println(hash.entries("sean"));

        Person person = new Person();
        person.setName("zhangsan");
        person.setAge(16);

        Jackson2HashMapper jm = new Jackson2HashMapper(objectMapper, false);


        // 高阶 会出现类型问题
//        stringRedisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));


        stringRedisTemplate.opsForHash().putAll("sean01",jm.toHash(person));
        Map map = stringRedisTemplate.opsForHash().entries("sean01");
        Person person1 = objectMapper.convertValue(map, Person.class);

        System.out.println(person1.getName());






        //
        RedisConnection connection = stringRedisTemplate.getConnectionFactory().getConnection();

        connection.subscribe(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                //
                byte[] body = message.getBody();
                System.out.println(new String(body));
            }
        }, "ooxx".getBytes());

        while (true){
            stringRedisTemplate.convertAndSend("ooxx","from mysqlf");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

}
