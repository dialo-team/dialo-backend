package com.fit.se.infrastructure.config.context;

public final class HolderContext {
    private static final ThreadLocal<RequestContext> HOLDER = new ThreadLocal<>();

    public static void set(RequestContext requestContext) {
        HOLDER.set(requestContext);
    }

    public static RequestContext get() {
        return HOLDER.get();
    }

    public static String getRequiredUserId() {
        RequestContext context = HOLDER.get();
        if(context == null || context.userId() == null) {
            throw new IllegalStateException("Missing user id in request context");
        }
        return context.userId();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
