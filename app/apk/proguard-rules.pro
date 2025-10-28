# 保留所有带有 @Parcelize 注解的类
-keep @kotlinx.parcelize.Parcelize class * { *; }

# 保留所有带有 @Serializable 注解的类
-keep @kotlinx.serialization.Serializable class * { *; }

# 保留 kotlinx 序列化库中的类
-keep class kotlinx.serialization.** { *; }
