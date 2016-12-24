package com.github.xsavikx.androidscreencast.spring.config;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.xsavikx.androidscreencast.app.DeviceChooserApplication;
import com.github.xsavikx.androidscreencast.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import javax.annotation.PreDestroy;

@Configuration
@ComponentScan(basePackages = "com.github.xsavikx.androidscreencast")
@PropertySources(value = {
        @PropertySource(value = "file:${user.dir}/app.properties", ignoreResourceNotFound = true)
})
public class ApplicationConfiguration {

    @Bean
    public AndroidDebugBridge initAndroidDebugBridge(Environment env) {
        AndroidDebugBridge.initIfNeeded(false);
        if (env.containsProperty(Constants.ADB_PATH_PROPERTY)) {
            return AndroidDebugBridge.createBridge(env.getProperty(Constants.ADB_PATH_PROPERTY), false);
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
