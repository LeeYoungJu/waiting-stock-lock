package co.wadcorp.waiting.data.config;

import static com.querydsl.jpa.JPQLTemplates.DEFAULT_ESCAPE;

import com.querydsl.jpa.DefaultQueryHandler;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"co.wadcorp.waiting.data.infra"})
@EntityScan(basePackages = {"co.wadcorp.waiting.data.domain"})
@Configuration
public class DbBeanConfig {

  @PersistenceContext
  private EntityManager entityManager;

  @Bean
  public JPAQueryFactory jpaQueryFactory() {
    return new JPAQueryFactory(
        // querydsl transform를 사용할 때 NoSuchMethodError 이슈가 발생해 JPQLTemplates 를 별도로 지정함
        // https://github.com/querydsl/querydsl/issues/3428
        new JPQLTemplates(DEFAULT_ESCAPE, DefaultQueryHandler.DEFAULT) {},
        entityManager
    );
  }
}
