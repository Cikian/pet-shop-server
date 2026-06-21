package cn.cikian.system.core.enums;


/**
 * <p>
 * Redis前缀字典
 * </p>
 *
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026/6/21
 */
public final class RedisConst {
    public static final int BLOCK_TIME = 600;

    private RedisConst() {

    }

    public static final String DELIMITER = ":";

    public static final Integer DEVICE_ALIVE_SECOND = 60;

    public static final Integer WEBSOCKET_ALIVE_SECOND = 60 * 60 * 24;

    public static final String USER_LOGIN_PREFIX = "login_user" + DELIMITER;

}
