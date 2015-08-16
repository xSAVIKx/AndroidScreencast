package com.github.xsavikx.android.screencast.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.xsavikx.android.screencast.app.DeviceChooserApplication;
import com.github.xsavikx.android.screencast.constant.Constants;

@Configuration
@ComponentScan(basePackages = "com.github.xsavikx.android.screencast")
@PropertySource(value = "file:${app.home}/app.properties", ignoreResourceNotFound = true)
public class ApplicationConfiguration {
  @Autowired
  private Environment env;

  @Bean
  public AndroidDebugBridge initAndroidDebugBridge() {
    AndroidDebugBridge.initIfNeeded(false);
    if (env.containsProperty(Constants.ADB_PATH_PROPERTY)) {
      return AndroidDebugBridge.createBridge(env.getProperty(Constants.ADB_PATH_PROPERTY), false);
    }
    return AndroidDebugBridge.createBridge();
  }

  @Bean
  public DefaultListableBeanFactory initBeanFactory() {
    DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
    return factory;
  }

  @Bean
  @Autowired
  public IDevice initDevice(DeviceChooserApplication application) {
    application.init();
    application.start();
    application.close();
    return application.getDevice();
  }

}
