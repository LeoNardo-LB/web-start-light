package smm.archetype.util;

import com.alibaba.fastjson2.TypeReference;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.BigDecimalSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.ClassSerializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.esotericsoftware.kryo.serializers.TimeSerializers.DurationSerializer;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author Leonardo
 * @since 2025/7/15
 * 高性能序列化工具
 */
public final class KryoSerializer {
    
    // 序列化器
    private static final Map<Class<?>, Serializer<?>> DEFAULT_SERIALIZERS = new HashMap<>();
    
    // Kryo对象池配置
    private static final KryoFactory KRYO_FACTORY = () -> {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);   // 关闭强制注册
        kryo.setReferences(true);              // 启用循环引用支持
        kryo.setAutoReset(true);               // 自动重置避免状态污染
        DEFAULT_SERIALIZERS.forEach(kryo::register);
        return kryo;
    };
    
    // 创建线程安全的Kryo对象池
    private static final KryoPool KRYO_POOL = new KryoPool.Builder(KRYO_FACTORY).softReferences().build();
    
    // 类型缓存 (提高性能)
    private static final Map<Type, Class<?>> TYPE_TO_CLASS_CACHE = new HashMap<>();
    
    static {
        // Java Time
        DEFAULT_SERIALIZERS.put(Instant.class, new InstantSerializer());
        DEFAULT_SERIALIZERS.put(LocalDate.class, new LocalDateSerializer());
        DEFAULT_SERIALIZERS.put(LocalTime.class, new LocalTimeSerializer());
        DEFAULT_SERIALIZERS.put(LocalDateTime.class, new LocalDateTimeSerializer());
        DEFAULT_SERIALIZERS.put(ZonedDateTime.class, new ZonedDateTimeSerializer());
        DEFAULT_SERIALIZERS.put(Duration.class, new DurationSerializer());
        DEFAULT_SERIALIZERS.put(Serializable.class, new JavaSerializer());
        // 其他常用类
        DEFAULT_SERIALIZERS.put(Optional.class, new OptionalSerializer());
        DEFAULT_SERIALIZERS.put(Currency.class, new CurrencySerializer());
        DEFAULT_SERIALIZERS.put(Locale.class, new LocaleSerializer());
        DEFAULT_SERIALIZERS.put(Path.class, new PathSerializer());
        DEFAULT_SERIALIZERS.put(InetAddress.class, new InetAddressSerializer());
        DEFAULT_SERIALIZERS.put(URL.class, new URLSerializer());
        DEFAULT_SERIALIZERS.put(Class.class, new ClassSerializer());
        DEFAULT_SERIALIZERS.put(BigDecimal.class, new BigDecimalSerializer());
        DEFAULT_SERIALIZERS.put(Pattern.class, new PatternSerializer());
    }
    
    // 私有构造器
    private KryoSerializer() {}
    
    /**
     * 序列化对象到字节数组
     *
     * @param obj 要序列化的对象
     * @return 序列化后的字节数组
     */
    public static byte[] serialize(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Cannot serialize null object");
        }
        
        return KRYO_POOL.run(kryo -> {
            try (Output output = new Output(1024, -1)) {
                kryo.writeClassAndObject(output, obj);
                return output.toBytes();
            }
        });
    }
    
    /**
     * 反序列化字节数组到对象（使用TypeReference支持多层泛型）
     *
     * @param bytes   序列化后的字节数组
     * @param typeRef 目标对象类型引用
     * @param <T>     目标对象泛型
     * @return 反序列化的对象
     */
    public static <T> T deserialize(byte[] bytes, TypeReference<T> typeRef) {
        return deserialize(bytes, typeRef.getType());
    }
    
    /**
     * 反序列化字节数组到对象（支持多层泛型）
     *
     * @param bytes 序列化后的字节数组
     * @param type  目标对象类型
     * @param <T>   目标对象泛型
     * @return 反序列化的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] bytes, Type type) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("Invalid byte array");
        }
        if (type == null) {
            throw new IllegalArgumentException("Target type cannot be null");
        }
        
        return KRYO_POOL.run(kryo -> {
            try (Input input = new Input(new ByteArrayInputStream(bytes))) {
                // 读取对象
                Object result = kryo.readClassAndObject(input);
                // 获取原始类型
                Class<T> rawType = getRawClass(type);
                // 类型安全检查
                if (!rawType.isInstance(result)) {
                    throw new ClassCastException(
                            "Deserialized object is not of type " + rawType.getName() + ". Actual type: " + result.getClass().getName());
                }
                return (T) result;
            }
        });
    }
    
    /**
     * 获取原始类类型
     */
    @SuppressWarnings("unchecked")
    private static <T> Class<T> getRawClass(Type type) {
        // 从缓存获取
        if (TYPE_TO_CLASS_CACHE.containsKey(type)) {
            return (Class<T>) TYPE_TO_CLASS_CACHE.get(type);
        }
        Class<T> clazz;
        if (type instanceof Class) {
            clazz = (Class<T>) type;
        } else if (type instanceof ParameterizedType) {
            clazz = (Class<T>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof TypeReference) {
            clazz = getRawClass(((TypeReference<?>) type).getType());
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
        // 加入缓存
        TYPE_TO_CLASS_CACHE.put(type, clazz);
        return clazz;
    }
    
    /**
     * 辅助方法：处理普通类类型
     */
    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return deserialize(bytes, (Type) clazz);
    }
    
    /**
     * Instant 序列化器
     */
    public static class InstantSerializer extends Serializer<Instant> {
        
        @Override
        public void write(Kryo kryo, Output output, Instant instant) {
            // 将 Instant 序列化为秒和纳秒
            output.writeLong(instant.getEpochSecond());
            output.writeInt(instant.getNano());
        }
        
        @Override
        public Instant read(Kryo kryo, Input input, Class<? extends Instant> aClass) {
            // 从秒和纳秒重建 Instant
            long epochSecond = input.readLong();
            int nano = input.readInt();
            return Instant.ofEpochSecond(epochSecond, nano);
        }
        
    }
    
    
    /**
     * LocalTime 序列化器
     */
    public static class LocalTimeSerializer extends Serializer<LocalTime> {
        
        @Override
        public void write(Kryo kryo, Output output, LocalTime object) {
            output.writeString(object.format(DateTimeFormatter.ISO_LOCAL_TIME));
        }
        
        @Override
        public LocalTime read(Kryo kryo, Input input, Class<? extends LocalTime> aClass) {
            return LocalTime.parse(input.readString(), DateTimeFormatter.ISO_LOCAL_TIME);
        }
        
    }
    
    
    /**
     * LocalDate 序列化器
     */
    public static class LocalDateSerializer extends Serializer<LocalDate> {
        
        @Override
        public void write(Kryo kryo, Output output, LocalDate object) {
            output.writeString(object.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        
        @Override
        public LocalDate read(Kryo kryo, Input input, Class<? extends LocalDate> aClass) {
            return LocalDate.parse(input.readString(), DateTimeFormatter.ISO_LOCAL_DATE);
        }
        
    }
    
    
    /**
     * LocalDateTime 序列化器
     */
    public static class LocalDateTimeSerializer extends Serializer<LocalDateTime> {
        
        @Override
        public void write(Kryo kryo, Output output, LocalDateTime object) {
            output.writeString(object.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        
        @Override
        public LocalDateTime read(Kryo kryo, Input input, Class<? extends LocalDateTime> type) {
            return LocalDateTime.parse(input.readString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        
    }
    
    
    /**
     * ZonedDateTime 序列化器
     */
    public static class ZonedDateTimeSerializer extends Serializer<ZonedDateTime> {
        
        @Override
        public void write(Kryo kryo, Output output, ZonedDateTime zdt) {
            output.writeString(zdt.toInstant().toString());
            output.writeString(zdt.getZone().getId());
        }
        
        @Override
        public ZonedDateTime read(Kryo kryo, Input input, Class<? extends ZonedDateTime> type) {
            Instant instant = Instant.parse(input.readString());
            ZoneId zone = ZoneId.of(input.readString());
            return ZonedDateTime.ofInstant(instant, zone);
        }
        
    }
    
    
    public static class OptionalSerializer extends Serializer<Optional<?>> {
        
        @Override
        public void write(Kryo kryo, Output output, Optional<?> optional) {
            output.writeBoolean(optional.isPresent());
            optional.ifPresent(value -> kryo.writeClassAndObject(output, value));
        }
        
        @Override
        public Optional<?> read(Kryo kryo, Input input, Class<? extends Optional<?>> type) {
            return input.readBoolean() ? Optional.ofNullable(kryo.readClassAndObject(input)) : Optional.empty();
        }
        
    }
    
    
    public static class CurrencySerializer extends Serializer<Currency> {
        
        @Override
        public void write(Kryo kryo, Output output, Currency currency) {
            output.writeString(currency.getCurrencyCode());
        }
        
        @Override
        public Currency read(Kryo kryo, Input input, Class<? extends Currency> type) {
            return Currency.getInstance(input.readString());
        }
        
    }
    
    
    public static class LocaleSerializer extends Serializer<Locale> {
        
        @Override
        public void write(Kryo kryo, Output output, Locale locale) {
            output.writeString(locale.toLanguageTag());
        }
        
        @Override
        public Locale read(Kryo kryo, Input input, Class<? extends Locale> type) {
            return Locale.forLanguageTag(input.readString());
        }
        
    }
    
    
    public static class PathSerializer extends Serializer<Path> {
        
        @Override
        public void write(Kryo kryo, Output output, Path path) {
            output.writeString(path.toString());
        }
        
        @Override
        public Path read(Kryo kryo, Input input, Class<? extends Path> type) {
            return Paths.get(input.readString());
        }
        
    }
    
    
    public static class InetAddressSerializer extends Serializer<InetAddress> {
        
        @Override
        public void write(Kryo kryo, Output output, InetAddress address) {
            output.writeString(address.getHostAddress());
        }
        
        @Override
        public InetAddress read(Kryo kryo, Input input, Class<? extends InetAddress> type) {
            try {
                return InetAddress.getByName(input.readString());
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
        
    }
    
    
    public static class URLSerializer extends Serializer<URL> {
        
        @Override
        public void write(Kryo kryo, Output output, URL url) {
            output.writeString(url.toString());
        }
        
        @Override
        public URL read(Kryo kryo, Input input, Class<? extends URL> type) {
            try {
                return new URL(input.readString());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        
    }
    
    
    public static class PatternSerializer extends Serializer<Pattern> {
        
        @Override
        public void write(Kryo kryo, Output output, Pattern pattern) {
            output.writeString(pattern.pattern());
            output.writeInt(pattern.flags());
        }
        
        @Override
        public Pattern read(Kryo kryo, Input input, Class<? extends Pattern> type) {
            return Pattern.compile(input.readString(), input.readInt());
        }
        
    }
    
}