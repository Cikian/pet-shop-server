package cn.cikian.system.core.utils;


import com.upyun.RestManager;
import com.upyun.UpException;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-01 03:57
 */

@Component
public class OssUtils {
    @Autowired
    private RestManager upManager;
    @Value("${oss.up-yun.file-path}")
    private String filePath;
    @Value("${oss.up-yun.customer-uri}")
    private String uri;

    public String upToOss(String path, byte[] data) throws UpException, IOException {
        upToOss(path, data, null);
        return uri + path;
    }

    public Response upToOss(String path, File file) throws UpException, IOException {
        return upToOss(path, file, null);
    }

    public Response upToOss(String path, InputStream stream) throws UpException, IOException {
        return upToOss(path, stream, null);
    }

    public Response upToOss(String path, byte[] data, Map<String, String> params) throws UpException, IOException {
        if (params == null) {
            params = new HashMap<>();
        }
        return upManager.writeFile(path, data, params);
    }

    public Response upToOss(String path, File file, Map<String, String> params) throws UpException, IOException {
        if (params == null) {
            params = new HashMap<>();
        }
        return upManager.writeFile(path, file, params);
    }

    public Response upToOss(String path, InputStream stream, Map<String, String> params) throws UpException, IOException {
        if (params == null) {
            params = new HashMap<>();
        }
        return upManager.writeFile(path, stream, params);
    }
}
