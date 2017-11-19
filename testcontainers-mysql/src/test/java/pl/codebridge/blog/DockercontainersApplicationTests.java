package pl.codebridge.blog;

import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.shaded.com.google.common.collect.Iterables;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DockercontainersApplication.class)
@ContextConfiguration(initializers = DockercontainersApplicationTests.Initializer.class)
public class DockercontainersApplicationTests {

    @ClassRule
    public static MySQLContainer mysql = new MySQLContainer();

    @Autowired
    private UserRepository repository;

    @Test
    public void should_return_newly_created_user() {
        final String name = "Test";
        User u = new User(name);
        u = repository.save(u);

        Iterable<User> users = repository.findAll();

        assertEquals(1, Iterables.size(users));
        assertEquals(name, users.iterator().next().getName());
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            EnvironmentTestUtils.addEnvironment("testcontainers", configurableApplicationContext.getEnvironment(),
                    "spring.jpa.hibernate.ddl-auto=create",
                    "spring.datasource.url=" + mysql.getJdbcUrl(),
                    "spring.datasource.username=" + mysql.getUsername(),
                    "spring.datasource.password=" + mysql.getPassword()
            );
        }
    }
}
