package sylllys.test.sniper.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import io.restassured.http.Header;
import io.restassured.response.Response;
import java.io.File;
import java.io.IOException;
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
  SniperProperties sniperProperties;

  public void loadRequestBody(Optional<String> requestBodyStr) throws Exception {

    if (requestBodyStr.isPresent()) {

      String templateName = bullet.getFtl();

      String pojoObjectName = templateName.substring(0, templateName.length() - 4);

      // 1. Configure FreeMarker
      //
      // You should do this ONLY ONCE, when your application starts,
      // then reuse the same Configuration object elsewhere.

      Configuration cfg = new Configuration();

      // Where do we load the templates from:
      cfg.setDirectoryForTemplateLoading(new File(
          "./"));

      // Some other recommended settings:
      cfg.setIncompatibleImprovements(new Version(2, 3, 20));
      cfg.setDefaultEncoding("UTF-8");
      cfg.setLocale(Locale.US);
      cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

      // 2. Proccess template(s)
      //
      // You will do this for several times in typical applications.

      // 2.1. Prepare the template input:

      Map<String, Object> input = new HashMap<String, Object>();

      ObjectMapper mapper = new ObjectMapper();
      String pojoClass =
          "sylllys.test.sniper.pojo." + pojoObjectName.substring(0, 1).toUpperCase()
              + pojoObjectName
              .substring(1);

      Class c = Class.forName(pojoClass);
      Object o = c.newInstance();

      //JSON file to Java object
      o = mapper
          .readValue(requestBodyStr.get(),
              Class.forName(pojoClass));

      input.put(pojoObjectName, o);

      // 2.2. Get the template

      Template template = cfg.getTemplate(sniperProperties.getFtlLocation() + templateName);

      // 2.3. Generate the output

      // Write output as string based object
      this.requestBody = new StringWriter();

      template.process(input, this.requestBody);

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

    finalResponse.setContentLength(testEndpoint.getResponse().body().asString().length());
    finalResponse.getWriter().write(testEndpoint.getResponse().body().asString());
  }
}
