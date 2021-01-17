package sylllys.test.sniper.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import io.restassured.http.Header;
import io.restassured.response.Response;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sylllys.test.sniper.entities.pojo.Bullet;
import sylllys.test.sniper.entities.pojo.SayHello;
import sylllys.test.sniper.factories.BulletFactory;
import sylllys.test.sniper.factories.HTTPRequestFactory;
import sylllys.test.sniper.utilities.HttpMethods;
import sylllys.test.sniper.utilities.TestEndpoint;


@RestController
@RequestMapping("/test-sniper")
public class TestSniperController {

  @Autowired
  BulletFactory bulletFactory;

  private static final Logger logger = LogManager.getLogger(TestSniperController.class);

  @GetMapping("/sayhello")
  public SayHello sayhello() {

    logger.info("Received request to say hello");
    return new SayHello();

  }

  @PostMapping("/shoot/{bulletName}")
  public void shoot(@PathVariable String bulletName,
      @RequestBody Optional<String> requestBody,
      @RequestHeader Map<String, String> headersAndParams,
      HttpServletResponse finalResponse)
      throws Exception {

    Bullet bullet = bulletFactory.loadBullet(bulletName);

    bulletFactory.fire(bullet, requestBody, headersAndParams, finalResponse);
  }

}
