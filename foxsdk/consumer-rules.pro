# ============================ SDK Consumer 规则 ============================
# 这些规则会被主工程自动合并

# 保留SDK的公共API
-keep class com.sohuglobal.foxsdk.** {
    public *;
    protected *;
}

# 保留SDK初始化
-keep class com.sohuglobal.foxsdk.core.WishFoxSdk { *; }

# 保留数据模型
-keep class com.sohuglobal.foxsdk.data.model.** { *; }

# 保留UI组件
-keep class com.sohuglobal.foxsdk.ui.view.** { *; }
-keep class * extends com.sohuglobal.foxsdk.ui.base.FoxSdkBaseMviActivity { *; }
-keep class * extends com.sohuglobal.foxsdk.ui.base.FoxSdkBaseMviFragment { *; }

# 保留ViewModel
-keep class com.sohuglobal.foxsdk.ui.viewmodel.** { *; }

# 保留工具类
-keep class com.sohuglobal.foxsdk.util.** { *; }