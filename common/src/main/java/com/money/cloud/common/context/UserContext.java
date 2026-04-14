package com.money.cloud.common.context;

import com.money.cloud.common.exception.BusinessException;

public final class UserContext {

    private static final ThreadLocal<LoginUser> USER_HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(LoginUser loginUser) {
        USER_HOLDER.set(loginUser);
    }

    public static LoginUser get() {
        return USER_HOLDER.get();
    }

    public static Long requireUserId() {
        LoginUser loginUser = USER_HOLDER.get();
        if (loginUser == null || loginUser.getUserId() == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        return loginUser.getUserId();
    }

    public static void clear() {
        USER_HOLDER.remove();
    }
}
