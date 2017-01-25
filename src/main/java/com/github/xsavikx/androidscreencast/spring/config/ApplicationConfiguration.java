package com.github.xsavikx.androidscreencast.spring.config;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.xsavikx.androidscreencast.app.DeviceChooserApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;

@Configuration
@ComponentScan(basePackages = "com.github.xsavikx.androidscreencast")
@PropertySources(value = {
        @PropertySource(value = "file:${user.dir}/app.properties", ignoreResourceNotFound = true)
})
public class ApplicationConfiguration {
    @Value("${adb.path}")
    private String adbPath;

    @Bean
    public AndroidDebugBridge initAndroidDebugBridge() {
        AndroidDebugBridge.initIfNeeded(false);
        if (!StringUtils.isEmpty(adbPath)) {
            return AndroidDebugBridge.createBridge(adbPath, false);
        }
        return AndroidDebugBridge.createBridge();
    }

    @PreDestroy
    public void preDestroy() {
        AndroidDebugBridge.disconnectBridge();
        AndroidDebugBridge.terminate();
    }

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
