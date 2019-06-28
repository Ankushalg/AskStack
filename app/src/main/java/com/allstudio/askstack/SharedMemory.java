package com.allstudio.askstack;

import android.content.Context;
import android.content.SharedPreferences;

class SharedMemory {
    private SharedPreferences prefs;
    SharedMemory(Context ctx) {prefs = ctx.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);}

    //-->    for boolean..........................................................>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    void setIsFirstLaunch(boolean value) {prefs.edit().putBoolean("IsFirstLaunch", value).apply();}
    boolean getIsFirstLaunch() {return prefs.getBoolean("IsFirstLaunch", true);}

    //-->    for int...............................................................>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    void setSearchingSite(int value) {prefs.edit().putInt("SearchingSite", value).apply();}
    int getSearchingSite() {return prefs.getInt("SearchingSite", 1);}

    void setPageSize(int value) {prefs.edit().putInt("PageSize", value).apply();}
    int getPageSize() {return prefs.getInt("PageSize", 10);}

    //-->    for Strings..........................................................>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    void setSearchFilter(String value) {prefs.edit().putString("SearchFilter", value).apply();}
    String getSearchFilter() {return prefs.getString("SearchFilter", null);}

    void setTagged(String value) {prefs.edit().putString("Tagged", value).apply();}
    String getTagged() {return prefs.getString("Tagged", null);}

    void setNotTagged(String value) {prefs.edit().putString("NotTagged", value).apply();}
    String getNotTagged() {return prefs.getString("NotTagged", null);}










}
