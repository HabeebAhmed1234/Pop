package com.stupidfungames.pop.eventbus;

public class GameSettingChangedEventPayload implements EventPayload {

    public final String settingKey;

    public GameSettingChangedEventPayload(String settingKey) {
        this.settingKey = settingKey;
    }
}
