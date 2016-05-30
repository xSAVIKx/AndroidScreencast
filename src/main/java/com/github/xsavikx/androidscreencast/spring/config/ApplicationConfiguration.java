package com.github.xsavikx.androidscreencast.spring.config;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.xsavikx.androidscreencast.app.DeviceChooserApplication;
import com.github.xsavikx.androidscreencast.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan(basePackages = "com.github.xsavikx.androidscreencast")
@PropertySources(value = {
        @PropertySource(value = "file:${user.dir}/app.properties", ignoreResourceNotFound = true)
})

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
        return new DefaultListableBeanFactory();
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
