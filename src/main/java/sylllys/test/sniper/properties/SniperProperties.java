package sylllys.test.sniper.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("test.sniper")
public class SniperProperties {

  public String getBulletsLocation() {
    return bulletsLocation;
  }

  public void setBulletsLocation(String bulletsLocation) {
    this.bulletsLocation = bulletsLocation;
  }

  String bulletsLocation;

  public String getFtlLocation() {
    return ftlLocation;
  }

  public void setFtlLocation(String ftlLocation) {
    this.ftlLocation = ftlLocation;
  }

  String ftlLocation;
}
