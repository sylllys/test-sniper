package sylllys.test.sniper.factories;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class FreeMarkerFactory {

  static Configuration cfg;

  static {

    cfg = new Configuration(Configuration.VERSION_2_3_29);

    try {
      cfg.setDirectoryForTemplateLoading(new File("./"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    cfg.setIncompatibleImprovements(new Version(2, 3, 20));
    cfg.setDefaultEncoding("UTF-8");
    cfg.setLocale(Locale.US);
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
  }

  public Template getTemplate(String templatePath) throws IOException {

    return cfg.getTemplate(templatePath);
  }

  public void processTemplate(Template template, Map<String, Object> input, Writer requestBody)
      throws IOException, TemplateException {

    template.process(input, requestBody);
  }

}
