package com.liuyue.igny.manager;

import java.lang.reflect.Type;

public class EasterEggDataManager extends BaseDataManager<EasterEggDataManager.EasterEggData> {
    public static final EasterEggDataManager INSTANCE = new EasterEggDataManager();

    private EasterEggData data = new EasterEggData();

    @Override protected String getFileName() { return "easter_egg.json"; }
    @Override protected Type getDataType() { return EasterEggData.class; }
    @Override public EasterEggData getDefaultData() { return new EasterEggData(); }
    @Override protected StorageScope getScope() { return StorageScope.GLOBAL; }
    @Override protected SideRestraint getSideRestraint() { return SideRestraint.CLIENT; }

    @Override
    public void clear() {

    }

    @Override
    protected void applyData(EasterEggData data) {
        if (data != null) {
            this.data = data;
        }
    }

    @Override
    public EasterEggData getCurrentData() {
        return data;
    }

    public boolean isSplashEnabled() {
        return data.splash;
    }
    @SuppressWarnings("all")
    public boolean isAprilFoolsActive() {
        return data.aprilFools;
    }

    @SuppressWarnings("unused")
    public void setEasterEgg(boolean enabled) {
        data.splash = enabled;
        data.aprilFools = enabled;
        save();
    }

    public static class EasterEggData {
        public boolean splash = true;
        public boolean aprilFools = true;
    }
}