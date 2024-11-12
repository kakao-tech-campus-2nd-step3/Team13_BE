package dbdr.global;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTest {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testRedisConnection() {
        // 키 저장
        redisTemplate.opsForValue().set("testKey", "Hello Redis!");

        // 키 검색
        String value = (String) redisTemplate.opsForValue().get("testKey");
        assertEquals("Hello Redis!", value);
    }
}
