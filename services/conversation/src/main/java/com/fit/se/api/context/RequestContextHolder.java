package com.fit.se.api.context;

import java.util.List;

public final class RequestContextHolder {
    private static final ThreadLocal<RequestContext> HOLDER = new ThreadLocal<>();

    private RequestContextHolder() {}

    public static void set(RequestContext requestContext) { HOLDER.set(requestContext); }
    public static RequestContext get() { return HOLDER.get(); }

    public static Long getRequiredUserId() {
        RequestContext ctx = HOLDER.get();
        if (ctx == null || ctx.userId() == null) throw new IllegalStateException("Missing user id in request context");
        return ctx.userId();
    }

    public static String getTraceId() {
        RequestContext ctx = HOLDER.get();
        return ctx != null ? ctx.traceId() : null;
    }

    public static List<String> getRoles() {
        RequestContext ctx = HOLDER.get();
        return ctx != null ? ctx.roles() : List.of();
    }

    public static void clear() { HOLDER.remove(); }
}
