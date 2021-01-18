package sylllys.test.sniper.factories;

import freemarker.template.Template;
import io.restassured.http.Header;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sylllys.test.sniper.entities.pojo.Bullet;
import sylllys.test.sniper.properties.SniperProperties;
import sylllys.test.sniper.utilities.HttpMethods;
import sylllys.test.sniper.utilities.TestEndpoint;

@Component
public class HTTPRequestFactory {

  Bullet bullet;
  Writer requestBody = null;
  HashMap<String, String> requestHeaders;
  HashMap<String, String> requestParameters;
  TestEndpoint testEndpoint;

  private static final Logger logger = LogManager.getLogger(HTTPRequestFactory.class);

  @Autowired
  private SniperProperties sniperProperties;
  @Autowired
  private FreeMarkerFactory freeMarkerFactory;

  public void loadRequestBody(Optional<String> requestBodyStr) throws Exception {

    if (requestBodyStr.isPresent()) {

      String templateName = bullet.getFtl();

      Map<String, Object> input = new HashMap<String, Object>();
      input.put("dataForFTL", requestBodyStr.get());

      Template template = freeMarkerFactory
          .getTemplate(sniperProperties.getFtlLocation() + templateName);

      this.requestBody = new StringWriter();

      freeMarkerFactory.processTemplate(template, input, this.requestBody);

      logger.debug("processed template output:\n" + this.requestBody.toString());
    }

  }

  public void loadRequestHeadersAndParams(Map<String, String> headersAndParams) {

    requestHeaders = new HashMap<String, String>();
    requestParameters = new HashMap<String, String>();

    for (String key : headersAndParams.keySet()) {

      if (key.toLowerCase().startsWith("sniper-header:")) {
        requestHeaders.put(key.substring(14), headersAndParams.get(key));
      } else if (key.toLowerCase().startsWith("sniper-parameter:")) {
        requestParameters.put(key.substring(17), headersAndParams.get(key));
      }

    }

    if (requestHeaders.size() == 0) {
      requestHeaders = null;
    }

    if (requestParameters.size() == 0) {
      requestParameters = null;
    }

  }

  public void sendRequest() throws Exception {

    testEndpoint = new TestEndpoint();
    testEndpoint.constructRequest(bullet.getUrl(), requestHeaders, requestParameters,
        this.requestBody != null ? requestBody.toString() : null);
    testEndpoint.sendRequest(HttpMethods.valueOf(bullet.getMethod()));

    logger.debug(testEndpoint.getResponse().body().asString());
  }

  public void setBulletDetails(Bullet bullet) {
    this.bullet = bullet;
  }

  public void setServletResponseDetails(HttpServletResponse finalResponse) throws IOException {

    finalResponse.setStatus(testEndpoint.getResponse().getStatusCode());

    for (Header header : testEndpoint.getResponse().getHeaders().asList()) {
      finalResponse.addHeader(header.getName(), header.getValue());
    }

    for (io.restassured.http.Cookie detailedCookie : testEndpoint.getResponse().getDetailedCookies()
        .asList()) {
      Cookie cookie = new Cookie(detailedCookie.getName(), detailedCookie.getValue());
      cookie.setPath(detailedCookie.getPath());
      cookie.setDomain(detailedCookie.getDomain());
      cookie.setMaxAge(detailedCookie.getMaxAge());
      finalResponse.addCookie(cookie);
    }

    logger.debug("response:" + testEndpoint.getResponse().body().asString());
    finalResponse.setContentLength(testEndpoint.getResponse().body().asString().length());
    finalResponse.getWriter().write(testEndpoint.getResponse().body().asString());
    finalResponse.getWriter().close();
  }
}
