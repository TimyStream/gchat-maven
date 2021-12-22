package net.timystream.mc;

import net.timystream.mc.api.GChatApi;

public final class GChat 
{
    private static GChatApi api = null;

    private GChat() {
    }

    public static GChatApi getApi() {
        return api;
    }

    static void setApi(GChatApi api) {
        GChat.api = api;
    }

}
