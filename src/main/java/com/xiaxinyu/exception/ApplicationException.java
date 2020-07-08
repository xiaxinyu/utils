package com.xiaxinyu.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class ApplicationException extends RuntimeException {
    private final transient Object[] parameters;

    private String code;

    /**
     * 构造器
     *
     * @param code       异常code
     * @param parameters parameters
     */
    public ApplicationException(String code, Object... parameters) {
        super(code);
        this.parameters = parameters;
        this.code = code;
    }

    public ApplicationException(String code, Throwable cause, Object... parameters) {
        super(code, cause);
        this.parameters = parameters;
        this.code = code;
    }

    public ApplicationException(String code, Throwable cause) {
        super(code, cause);
        this.code = code;
        this.parameters = new Object[]{};
    }


    public ApplicationException(Throwable cause, Object... parameters) {
        super(cause);
        this.parameters = parameters;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public String getCode() {
        return code;
    }

    public String getTrace() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps ;
        try {
            ps = new PrintStream(baos, false, "utf8");
            this.printStackTrace(ps);
            ps.flush();
            return new String(baos.toByteArray(), Charset.forName("utf8"));
        } catch (UnsupportedEncodingException e) {
            log.error("Encoding Error -- utf8");
        }
        return "";
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new LinkedHashMap<>();
        map.put("code", code);
        map.put("message", super.getMessage());
        return map;
    }
}
