package sylllys.test.sniper.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sylllys.test.sniper.entities.pojo.Bullet;
import sylllys.test.sniper.properties.SniperProperties;

@Component
public class BulletFactory {

  private static final Logger logger = LogManager.getLogger(BulletFactory.class);

  @Autowired
  SniperProperties sniperProperties;
  @Autowired
  HTTPRequestFactory httpRequestFactory;

  public Bullet loadBullet(String bulletName) throws IOException {

    ObjectMapper yamlmapper = new ObjectMapper(new YAMLFactory());
    return yamlmapper
        .readValue(new File(sniperProperties.getBulletsLocation() + bulletName + ".yml"),
            Bullet.class);
  }


  public void fire(Bullet bullet,
      Optional<String> requestBody,
      Map<String, String> dataForHeadersParamsUrl, HttpServletResponse finalResponse) throws Exception {

    httpRequestFactory.setBulletDetails(bullet);
    httpRequestFactory.loadRequestBody(requestBody);
    httpRequestFactory.loadRequestHeadersParamsUrl(dataForHeadersParamsUrl);
    httpRequestFactory.sendRequest();
    httpRequestFactory.setServletResponseDetails(finalResponse);
  }


}
