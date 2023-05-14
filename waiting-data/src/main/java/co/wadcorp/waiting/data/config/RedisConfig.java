package co.wadcorp.waiting.data.config;


import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Profile(value = {"default", "alpha", "real", "test"})
@RequiredArgsConstructor
@Configuration
@EnableRedisRepositories
@EnableCaching
public class RedisConfig {

  @Value("${redis.distributed-lock.host}")
  private String redisForDistributedLockHost;

  @Value("${redis.distributed-lock.port}")
  private int redisForDistributedLockPort;

  @Value("${redis.distributed-lock.timeout}")
  private int redisForDistributedLockTimeout;

  @Value("${redis.distributed-lock.connect-timeout}")
  private int redisForDistributedLockConnectTimeout;

  @Value("${redis.cache.host}")
  private String redisForCacheHost;

  @Value("${redis.cache.port}")
  private int redisForCachePort;

  @Value("${redis.cache.timeout}")
  private int redisForCacheTimeout;

  @Value("${redis.cache.connect-timeout}")
  private int redisForCacheConnectTimeout;

  @Bean
  @Qualifier("redisForDistributedLockConnectionFactory")
  public LettuceConnectionFactory redisForDistributedLockConnectionFactory() {
    ClientOptions clientOptions = ClientOptions.builder()
        .socketOptions(SocketOptions.builder()
            .connectTimeout(Duration.ofMillis(redisForDistributedLockConnectTimeout))
            .build()
        )
        .build();

    RedisConfiguration redisConfiguration = new RedisStandaloneConfiguration(
        redisForDistributedLockHost,
        redisForDistributedLockPort
    );

    LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
        .clientOptions(clientOptions)
        .commandTimeout(Duration.ofMillis(redisForDistributedLockTimeout))
        .build();
    return new LettuceConnectionFactory(redisConfiguration, lettuceClientConfiguration);
  }

  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();
    config.useSingleServer()
            .setAddress("redis://" + redisForDistributedLockHost + ":" + 46379);
    return Redisson.create(config);
  }

  @Bean
  @Primary
  @Qualifier("redisForCacheConnectionFactory")
  public LettuceConnectionFactory redisForCacheConnectionFactory() {
    ClientOptions clientOptions = ClientOptions.builder()
        .socketOptions(SocketOptions.builder()
            .connectTimeout(Duration.ofMillis(redisForCacheConnectTimeout))
            .build()
        )
        .build();

    RedisConfiguration redisConfiguration = new RedisStandaloneConfiguration(
        redisForCacheHost,
        redisForCachePort
    );

    LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
        .clientOptions(clientOptions)
        .commandTimeout(Duration.ofMillis(redisForCacheTimeout))
        .build();
    return new LettuceConnectionFactory(redisConfiguration, lettuceClientConfiguration);
  }

  /**
   * [중요] 웨이팅 번호 발급 같은 경우는 레디스 설정이 변경되면 번호가 1부터 다시 시작할 수 있으므로
   * <p>
   * 해당 패치가 있을 경우 웨이팅이 끝난 밤~새벽 시간에 배포해야 한다!
   */
  @Bean
  @Qualifier("waitingNumberRedisTemplate")
  public RedisTemplate<String, Integer> waitingNumberRedisTemplate(
      LettuceConnectionFactory redisForDistributedLockConnectionFactory) {
    RedisTemplate<String, Integer> template = new RedisTemplate<>();
    template.setConnectionFactory(redisForDistributedLockConnectionFactory);

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericToStringSerializer<>(Integer.class));
    return template;
  }

  @Bean
  @Qualifier("customerPhoneRedisTemplate")
  public RedisTemplate<String, String> customerPhoneRedisTemplate(
      LettuceConnectionFactory redisForDistributedLockConnectionFactory) {
    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(redisForDistributedLockConnectionFactory);

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    return template;
  }

  @Bean
  @Qualifier("cacheRedisTemplate")
  public RedisTemplate<String, String> cacheRedisTemplate(
      LettuceConnectionFactory redisForCacheConnectionFactory) {
    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(redisForCacheConnectionFactory);

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    return template;
  }

  @Bean
  @Qualifier("menuStockRedisTemplate")
  public RedisTemplate<String, Integer> menuStockRedisTemplate(LettuceConnectionFactory redisForDistributedLockConnectionFactory) {
    RedisTemplate<String, Integer> template = new RedisTemplate<>();
    template.setConnectionFactory(redisForDistributedLockConnectionFactory);

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericToStringSerializer<>(Integer.class));
    return template;
  }

}
