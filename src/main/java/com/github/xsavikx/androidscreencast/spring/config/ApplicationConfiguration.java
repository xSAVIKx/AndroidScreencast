package com.github.xsavikx.androidscreencast.spring.config;

import com.android.ddmlib.IDevice;
import com.github.xsavikx.androidscreencast.app.DeviceChooserApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = "com.github.xsavikx.androidscreencast")
@PropertySources(value = {
        @PropertySource(value = "file:${user.dir}/app.properties", ignoreResourceNotFound = true)
})
public class ApplicationConfiguration {
    @Value("${adb.path:}")
    private String adbPath;

    @Bean
    public DefaultListableBeanFactory initBeanFactory() {
        return new DefaultListableBeanFactory();
    }

    @Bean
    @Autowired
    public IDevice initDevice(DeviceChooserApplication application) {
        application.init();
        application.start();
        application.stop();
        return application.getDevice();
    }

}
