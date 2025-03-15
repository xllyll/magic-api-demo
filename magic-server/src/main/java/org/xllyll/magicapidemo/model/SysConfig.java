package org.xllyll.magicapidemo.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("sys_config")
public class SysConfig {
    @TableId
    private Integer configId;
    private String configName;
    private String configKey;
    private String configValue;
    private String configType;

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }
}
