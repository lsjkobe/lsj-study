import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author lishangj
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.lsj.study"})
public class Oauth2Springboot {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2Springboot.class, args);
    }
}
