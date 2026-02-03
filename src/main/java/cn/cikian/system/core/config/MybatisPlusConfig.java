package cn.cikian.system.core.config;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 15:19
 */

@Configuration
@EnableTransactionManagement
public class MybatisPlusConfig {

    /**
     * MyBatis-Plus 插件配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setMaxLimit(1000L); // 单页最大记录数
        paginationInnerInterceptor.setOverflow(true);  // 超过最大页数后是否处理
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        // 2. 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 3. 防止全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        // 4. 数据变动记录插件（需要自定义实现）
        // interceptor.addInnerInterceptor(new DataChangeRecorderInnerInterceptor());

        return interceptor;
    }

    /**
     * 自定义ID生成器
     */
    @Bean
    public IdentifierGenerator idGenerator() {
        return new CustomIdGenerator();
    }

    /**
     * 全局配置
     */
    @Bean
    public GlobalConfig globalConfig(IdentifierGenerator idGenerator) {
        GlobalConfig config = new GlobalConfig();

        // 数据库配置
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        dbConfig.setIdType(IdType.ASSIGN_ID);
        dbConfig.setLogicDeleteField("del_flag");
        dbConfig.setLogicDeleteValue("1");
        dbConfig.setLogicNotDeleteValue("0");
        dbConfig.setInsertStrategy(FieldStrategy.NOT_NULL);
        dbConfig.setUpdateStrategy(FieldStrategy.NOT_NULL);
        dbConfig.setWhereStrategy(FieldStrategy.NOT_NULL);
        config.setDbConfig(dbConfig);

        // ID生成器
        config.setIdentifierGenerator(idGenerator);

        // 开启 SQL 注入器
        // config.setSqlInjector(new DefaultSqlInjector());

        return config;
    }

    /**
     * 元对象处理器（自动填充字段）
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MyMetaObjectHandler();
    }

    /**
     * 自定义ID生成器实现
     */
    static class CustomIdGenerator implements IdentifierGenerator {
        @Override
        public Number nextId(Object entity) {
            // 使用雪花算法生成ID
            return SnowflakeIdGenerator.nextId();
        }

        @Override
        public String nextUUID(Object entity) {
            // 生成UUID
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

    /**
     * 雪花ID生成器
     */
    static class SnowflakeIdGenerator {
        // 起始时间戳
        private static final long START_TIMESTAMP = 1609459200000L; // 2021-01-01
        // 数据中心ID位数
        private static final long DATA_CENTER_ID_BITS = 5L;
        // 机器ID位数
        private static final long WORKER_ID_BITS = 5L;
        // 序列号位数
        private static final long SEQUENCE_BITS = 12L;

        // 最大数据中心ID
        private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);
        // 最大机器ID
        private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
        // 序列号掩码
        private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

        // 时间戳左移位数
        private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;
        // 数据中心ID左移位数
        private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
        // 机器ID左移位数
        private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

        // 数据中心ID
        private static final long DATA_CENTER_ID = 1L;
        // 机器ID
        private static final long WORKER_ID = 1L;

        private static long lastTimestamp = -1L;
        private static long sequence = 0L;

        public static synchronized long nextId() {
            long timestamp = timeGen();

            if (timestamp < lastTimestamp) {
                throw new RuntimeException("时钟回拨异常");
            }

            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & SEQUENCE_MASK;
                if (sequence == 0) {
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }

            lastTimestamp = timestamp;

            return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                    | (DATA_CENTER_ID << DATA_CENTER_ID_SHIFT)
                    | (WORKER_ID << WORKER_ID_SHIFT)
                    | sequence;
        }

        private static long tilNextMillis(long lastTimestamp) {
            long timestamp = timeGen();
            while (timestamp <= lastTimestamp) {
                timestamp = timeGen();
            }
            return timestamp;
        }

        private static long timeGen() {
            return System.currentTimeMillis();
        }
    }

    /**
     * 自定义元对象处理器
     */
    static class MyMetaObjectHandler implements MetaObjectHandler {

        @Override
        public void insertFill(MetaObject metaObject) {
            // 自动填充创建时间
            this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
            this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

            // 自动填充创建人
            this.strictInsertFill(metaObject, "createBy", String.class, getCurrentUsername());
            this.strictInsertFill(metaObject, "updateBy", String.class, getCurrentUsername());

            // 其他字段填充
            this.strictInsertFill(metaObject, "delFlag", Integer.class, 0);
            this.strictInsertFill(metaObject, "version", Integer.class, 0);
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            // 自动填充更新时间
            this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

            // 自动填充更新人
            this.strictUpdateFill(metaObject, "updateBy", String.class, getCurrentUsername());
        }

        @Override
        public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
            Object obj = fieldVal.get();
            if (obj != null) {
                metaObject.setValue(fieldName, obj);
            }
            return this;
        }

        private String getCurrentUsername() {
            // 从安全上下文中获取当前用户名
            // 这里需要根据你的安全框架实现
            return "system";
        }
    }
}
